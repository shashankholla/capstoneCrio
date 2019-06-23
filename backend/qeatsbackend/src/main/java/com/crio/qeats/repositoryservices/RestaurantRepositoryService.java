/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Restaurant;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

public interface RestaurantRepositoryService {

  /**
   * Get the list of open restaurants within the specified serving radius.
   * - Use the Haversine formula to find the distance between two lat/longs.
   * - Ensure the restaurant is open currently.
   *
   * @param latitude           coordinates near which we have to search for restaurant
   * @param longitude          coordinates near which we have to search for restaurant
   * @param currentTime        current time
   * @param servingRadiusInKms serving radius
   * @return list of open restaurants within the specified radius or
   *     empty list if there is none
   */
  List<Restaurant> findAllRestaurantsCloseBy(Double latitude, Double longitude,
                                             LocalTime currentTime, Double servingRadiusInKms);

  /**
   * Get the list of open restaurants within the specified serving radius.
   * - Ensure the restaurant is open currently.
   *
   * @param searchString Query string for restaurants
   * @return list of restaurants
   */
  List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
                                         String searchString, LocalTime currentTime,
                                         Double servingRadiusInKms);

  @Async
  CompletableFuture<List<Restaurant>> findRestaurantsByNameAsync(Double latitude, Double longitude,
                                                                 String searchString,
                                                                 LocalTime currentTime,
                                                                 Double servingRadiusInKms);

  /**
   * Get the list of open restaurants within the specified serving radius.
   *
   * @param searchString Query string for item attributes
   * @return list of restaurants
   */
  List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms);

  @Async
  CompletableFuture<List<Restaurant>> findRestaurantsByAttributesAsync(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms);

  /**
   * Get the list of open restaurants within the specified serving radius which service item
   * name in search string.
   *
   * @param searchString The food items to search for
   * @return list of restaurants
   */
  List<Restaurant> findRestaurantsByItemName(Double latitude, Double longitude,
                                             String searchString,
                                             LocalTime currentTime, Double servingRadiusInKms);

  @Async
  CompletableFuture<List<Restaurant>> findRestaurantsByItemNameAsync(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms);

  /**
   * Get the list of open restaurants within the specified serving radius which have items of
   * specified attributes in search string.
   *
   * @param searchString The item attributes to search for
   * @return list of restaurants
   */
  List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
                                                   String searchString, LocalTime currentTime,
                                                   Double servingRadiusInKms);

  @Async
  CompletableFuture<List<Restaurant>> findRestaurantsByItemAttributesAsync(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms);
}
