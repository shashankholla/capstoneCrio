package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public class RestaurantRepositoryServiceDummyImpl implements RestaurantRepositoryService {
  private static final String FIXTURES = "fixtures/exchanges";
  private ObjectMapper objectMapper = new ObjectMapper();

  private List<Restaurant> loadRestaurantsDuringNormalHours() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/normal_hours_list_of_restaurants.json");

    return objectMapper.readValue(fixture, new TypeReference<List<Restaurant>>() {
    });
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Use this dummy implementation.
  // This function returns a list of restaurants in any lat/long of your choice randomly.
  // It will load some dummy restaurants and change their latitude/longitude near
  // the lat/long you pass. In the next module, once you start using mongodb, you will not use
  // it anymore.
  @Override
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude, Double longitude,
                                                    LocalTime currentTime,
                                                    Double servingRadiusInKms) {
    List<Restaurant> restaurantList = new ArrayList<>();
    try {
      restaurantList = loadRestaurantsDuringNormalHours();
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (Restaurant restaurant : restaurantList) {
      restaurant.setLatitude(latitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
      restaurant.setLongitude(longitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
    }
    return restaurantList;
  }

  @Override
  public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
                                                String searchString, LocalTime currentTime,
                                                Double servingRadiusInKms) {
    return null;
  }

  @Override
  public CompletableFuture<List<Restaurant>> findRestaurantsByNameAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {
    return null;
  }

  @Override
  public List<Restaurant> findRestaurantsByAttributes(Double latitude, Double longitude,
                                                      String searchString, LocalTime currentTime,
                                                      Double servingRadiusInKms) {
    return null;
  }

  @Override
  public CompletableFuture<List<Restaurant>> findRestaurantsByAttributesAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {
    return null;
  }

  @Override
  public List<Restaurant> findRestaurantsByItemName(Double latitude, Double longitude,
                                                    String searchString, LocalTime currentTime,
                                                    Double servingRadiusInKms) {
    return null;
  }

  @Override
  public CompletableFuture<List<Restaurant>> findRestaurantsByItemNameAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {
    return null;
  }

  @Override
  public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
                                                          String searchString,
                                                          LocalTime currentTime,
                                                          Double servingRadiusInKms) {
    return null;
  }

  @Override
  public CompletableFuture<List<Restaurant>> findRestaurantsByItemAttributesAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {
    return null;
  }
}
