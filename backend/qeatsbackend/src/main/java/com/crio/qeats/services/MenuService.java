/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.GetMenuResponse;

public interface MenuService {

  /**
   * Return the restaurant menu.
   * @param restaurantId id of the restaurant
   * @return the restaurant's menu
   */
  GetMenuResponse findMenu(String restaurantId);

  /**
   * Find the item in the restaurant using restaurantId/itemId and return the item if found.
   * @param itemId id of the item
   * @param restaurantId id of the restaurant
   * @return item if found
   * @exception ItemNotFoundInRestaurantMenuException if the item is not found
   */
  Item findItem(String itemId, String restaurantId) throws ItemNotFoundInRestaurantMenuException,
      ItemNotFromSameRestaurantException;

  GetMenuResponse addItem(Item item, String restaurantId) throws Exception;

  GetMenuResponse removeItem(String itemId, String restaurantId) throws Exception;

  GetMenuResponse updateItem(Item item, String restaurantId) throws ItemNotFoundInRestaurantMenuException;
}
