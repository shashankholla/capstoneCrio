/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.MenuEntity;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import com.crio.qeats.utils.GeoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

@Service
@Primary
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?
  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objectives:
  // 1. Implement findAllRestaurantsCloseby.
  // 2. Remember to keep the precision of GeoHash in mind while using it as a key.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
                                                    Double longitude,
                                                    LocalTime currentTime,
                                                    Double servingRadiusInKms) {
    // TODO: CRIO_TASK_MODULE_REDIS
    // We want to use cache to speed things up. Write methods that perform the same functionality,
    // but using the cache if it is present and reachable.
    // Remember, you must ensure that if cache is not present, the queries are directed at the
    // database instead.
    if (GlobalConstants.isCacheAvailable()) {
      try {
        return findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime,
            servingRadiusInKms);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
    }
    return null;
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude,
                                                         Double longitude,
                                                         LocalTime currentTime,
                                                         Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    List<RestaurantEntity> restaurants = restaurantRepository.findAll();
    int timing = currentTime.getHour() * 100 + currentTime.getMinute();
    for (RestaurantEntity restaurant : restaurants) {
      LocalTime opening = LocalTime.parse(restaurant.getOpensAt());
      LocalTime closing = LocalTime.parse(restaurant.getClosesAt());

      int openingTime = opening.getHour() * 100 + opening.getMinute();
      int closingTime = closing.getHour() * 100 + closing.getMinute();
      if (timing >= openingTime && timing <= closingTime) {
        Double distance = haversine(latitude, longitude, restaurant.getLatitude(),
            restaurant.getLongitude());
        if (distance <= servingRadiusInKms) {
          Restaurant r = new Restaurant();
          r.setRestaurant(
              restaurant.getId(),
              restaurant.getRestaurantId(),
              restaurant.getName(),
              restaurant.getCity(),
              restaurant.getImageUrl(),
              restaurant.getLatitude(),
              restaurant.getLongitude(),
              restaurant.getOpensAt(),
              restaurant.getClosesAt(),
              restaurant.getAttributes()
          );

          restaurantList.add(r);
        }
      }

    }
    return restaurantList;
  }

  public static final double R = 6372.8; // In kilometers

  public static double haversine(double lat1, double lon1, double lat2, double lon2) {
    double differenceLat = Math.toRadians(lat2 - lat1);
    double differenceLon = Math.toRadians(lon2 - lon1);
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    double a =
        Math.pow(Math.sin(differenceLat / 2), 2) + Math.pow(Math.sin(differenceLon / 2), 2) * Math
            .cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.asin(Math.sqrt(a));
    return R * c;
  }
  // TODO: CRIO_TASK_MODULE_REDIS - Implement caching.

  /**
   * Implement caching for restaurants closeby.
   * Whenever the entry is not there in the cache, you will have to populate it from DB.
   * If the entry is already available in the cache, then return it from cache to save DB lookup.
   * The cache entries should expire in GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS.
   * Make sure you use something like a GeoHash with a slightly lower precision,
   * so that for lat/long that are slightly close, the function returns the same set of restaurants.
   */
  private List<Restaurant> findAllRestaurantsCloseByFromCache(
      Double latitude, Double longitude,
      LocalTime currentTime, Double servingRadiusInKms) throws IOException {

    List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    Jedis jedis = GlobalConstants.getJedisPool().getResource();

    int timing = currentTime.getHour() * 100 + currentTime.getMinute();
    GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);
    if (jedis.exists(geoHash.toBase32())) {
      String restaurantsData = jedis.get(geoHash.toBase32());
      List<String> list = Arrays.asList(restaurantsData.split(";"));
      for (int i = 0; i < list.size(); i++) {
        Restaurant r = new ObjectMapper().readValue(list.get(i), Restaurant.class);
        LocalTime opening = LocalTime.parse(r.getOpensAt());
        LocalTime closing = LocalTime.parse(r.getClosesAt());

        int openingTime = opening.getHour() * 100 + opening.getMinute();
        int closingTime = closing.getHour() * 100 + closing.getMinute();
        if (timing >= openingTime && timing <= closingTime) {
          Double distance = haversine(latitude, longitude, r.getLatitude(),
              r.getLongitude());
          if (distance <= servingRadiusInKms) {
            restaurantList.add(r);
          }
        }
      }
    } else {
      restaurantList = findAllRestaurantsCloseFromDb(latitude, longitude,
          currentTime, servingRadiusInKms);
      for (Restaurant r : restaurantList) {

        if (jedis.exists(geoHash.toBase32())) {
          jedis.append(geoHash.toBase32(), ";" + r.serializeToJson());
          jedis.expire(geoHash.toBase32(), GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS);
        } else {
          jedis.set(geoHash.toBase32(), r.serializeToJson());
          jedis.expire(geoHash.toBase32(), GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS);
        }
      }
    }
    jedis.close();
    return restaurantList;
  }


  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose names have an exact or partial match with the search query.
  @Override
  public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
                                                String searchString,
                                                LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{name: {$regex: /" + searchString + "/i}}");
    List<RestaurantEntity> restaurants = mongoTemplate
        .find(query, RestaurantEntity.class, "restaurants");
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return restaurantList;
  }

  @Override
  @Async
  public CompletableFuture<List<Restaurant>> findRestaurantsByNameAsync(
      Double latitude, Double longitude, String searchString, LocalTime currentTime,
      Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{name: {$regex: /" + searchString + "/i}}");
    List<RestaurantEntity> restaurants = mongoTemplate
        .find(query, RestaurantEntity.class, "restaurants");
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return CompletableFuture.completedFuture(restaurantList);
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose attributes (cuisines) intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{attributes: {$regex: /" + searchString + "/i}}");
    List<RestaurantEntity> restaurants = mongoTemplate
        .find(query, RestaurantEntity.class, "restaurants");
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return restaurantList;
  }

  @Override
  @Async
  public CompletableFuture<List<Restaurant>> findRestaurantsByAttributesAsync(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{attributes: {$regex: /" + searchString + "/i}}");
    List<RestaurantEntity> restaurants = mongoTemplate
        .find(query, RestaurantEntity.class, "restaurants");
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return CompletableFuture.completedFuture(restaurantList);
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose names form a complete or partial match
  // with the search query.
  @Override
  public List<Restaurant> findRestaurantsByItemName(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{'items.name': {$regex: /" + searchString + "/i}}");
    List<MenuEntity> menus = mongoTemplate.find(query, MenuEntity.class, "menus");
    List<RestaurantEntity> restaurants = new ArrayList<>();
    for (MenuEntity menu : menus) {
      String restaurantId = menu.getRestaurantId();
      BasicQuery restaurantQuery = new BasicQuery("{restaurantId:" + restaurantId + "}");
      restaurants.add(mongoTemplate
          .findOne(restaurantQuery, RestaurantEntity.class, "restaurants"));
    }
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return restaurantList;
  }

  @Override
  @Async
  public CompletableFuture<List<Restaurant>> findRestaurantsByItemNameAsync(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{'items.name': {$regex: /" + searchString + "/i}}");
    List<MenuEntity> menus = mongoTemplate.find(query, MenuEntity.class, "menus");
    List<RestaurantEntity> restaurants = new ArrayList<>();
    for (MenuEntity menu : menus) {
      String restaurantId = menu.getRestaurantId();
      BasicQuery restaurantQuery = new BasicQuery("{restaurantId:" + restaurantId + "}");
      restaurants.add(mongoTemplate
          .findOne(restaurantQuery, RestaurantEntity.class, "restaurants"));
    }
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return CompletableFuture.completedFuture(restaurantList);
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose attributes intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
                                                          String searchString,
                                                          LocalTime currentTime,
                                                          Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{'items.attributes': {$regex: /" + searchString + "/i}}");
    List<MenuEntity> menus = mongoTemplate.find(query, MenuEntity.class, "menus");
    List<RestaurantEntity> restaurants = new ArrayList<>();
    for (MenuEntity menu : menus) {
      String restaurantId = menu.getRestaurantId();
      BasicQuery restaurantQuery = new BasicQuery("{restaurantId:" + restaurantId + "}");
      restaurants.add(mongoTemplate
          .findOne(restaurantQuery, RestaurantEntity.class, "restaurants"));
    }
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return restaurantList;
  }

  @Override
  @Async
  public CompletableFuture<List<Restaurant>> findRestaurantsByItemAttributesAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {
    BasicQuery query = new BasicQuery("{'items.attributes': {$regex: /" + searchString + "/i}}");
    List<MenuEntity> menus = mongoTemplate.find(query, MenuEntity.class, "menus");
    List<RestaurantEntity> restaurants = new ArrayList<>();
    for (MenuEntity menu : menus) {
      String restaurantId = menu.getRestaurantId();
      BasicQuery restaurantQuery = new BasicQuery("{restaurantId:" + restaurantId + "}");
      restaurants.add(mongoTemplate
          .findOne(restaurantQuery, RestaurantEntity.class, "restaurants"));
    }
    List<Restaurant> restaurantList = new ArrayList<Restaurant>();

    for (RestaurantEntity restaurant : restaurants) {

      if (isRestaurantCloseByAndOpen(restaurant, currentTime,
          latitude, longitude, servingRadiusInKms)) {
        Restaurant r = new Restaurant();
        r.setRestaurant(
            restaurant.getId(),
            restaurant.getRestaurantId(),
            restaurant.getName(),
            restaurant.getCity(),
            restaurant.getImageUrl(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getOpensAt(),
            restaurant.getClosesAt(),
            restaurant.getAttributes()
        );

        restaurantList.add(r);

      }

    }
    return CompletableFuture.completedFuture(restaurantList);
  }


  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   *
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
                                             LocalTime currentTime, Double latitude,
                                             Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude,
          restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
          < servingRadiusInKms;
    }

    return false;
  }
}
