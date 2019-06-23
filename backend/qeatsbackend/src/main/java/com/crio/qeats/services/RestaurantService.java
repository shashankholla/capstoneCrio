/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

public interface RestaurantService {

  /**
   * Get all the restaurants that are open now within a specific service radius.
   * - For peak hours: 8AM - 10AM, 1PM-2PM, 7PM-9PM
   * - service radius is 3KMs.
   * - All other times, serving radius is 5KMs.
   * - If there are no restaurants, return empty list of restaurants.
   * @param getRestaurantsRequest valid lat/long
   * @param currentTime current time.
   * @return GetRestaurantsResponse object containing a list of open restaurants or an
   *     empty list if none fits the criteria.
   */
  GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime);

  /**
   * Get the restaurants by processing the query.
   * -Ordering rules
   *  1) Restaurant name
   *    - exact matches first
   *    - partial matches second
   *  2) Restaurant attributes
   *    - partial and full matches in any order
   *  3) Item name
   *    - exact matches first
   *    - partial matches second
   *  4) Item attributes
   *    - partial and full matches in any order
   * - For peak hours: 8AM - 10AM, 1PM-2PM, 7PM-9PM
   * - service radius is 3KMs.
   * - All other times, serving radius is 5KMs.
   * - If there are no restaurants, return empty list of restaurants.
   * @param getRestaurantsRequest valid lat/long and searchFor
   * @return GetRestaurantsResponse object containing a list of open restaurants or an
   *     empty list if none fits the criteria.
   */
  GetRestaurantsResponse findRestaurantsBySearchQuery(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime);

  /**
   * Identical input and output to @link{findRestaurantsBySearchQuery} but
   * use a multi-threaded implementation.
   * @param getRestaurantsRequest valid lat/long with searchFor string
   * @param currentTime current time
   * @return GetRestaurantsResponse object containing a list of open restaurants or an
   *     empty list if none fits the criteria.
   */
  GetRestaurantsResponse findRestaurantsBySearchQueryMt(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime)
      throws ExecutionException, InterruptedException;
}
