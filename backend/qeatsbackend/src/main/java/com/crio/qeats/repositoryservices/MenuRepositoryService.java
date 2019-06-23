/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Menu;
import com.crio.qeats.models.MenuEntity;
import org.springframework.stereotype.Service;

@Service
public interface MenuRepositoryService {

  /**
   * Return the restaurant menu.
   *
   * @param restaurantId id of the restaurant
   * @return the restaurant's menu
   */
  Menu findMenu(String restaurantId);

  Menu addItemToMenu(String itemId, String restaurantId) throws Exception;

  Menu removeItemFromMenu(String itemId, String restaurantId) throws Exception;

  Menu updateItemInMenu(Item item, String restaurantId);

}
