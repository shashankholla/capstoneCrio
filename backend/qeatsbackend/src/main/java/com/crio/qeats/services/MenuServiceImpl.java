/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Menu;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.GetMenuResponse;
import com.crio.qeats.repositoryservices.ItemRepositoryService;
import com.crio.qeats.repositoryservices.MenuRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;

@Service
public class MenuServiceImpl implements MenuService {

  @Autowired
  MenuRepositoryService menuRepositoryService;

  @Autowired
  ItemRepositoryService itemRepositoryService;


  @Autowired
  Provider<ModelMapper> modelMapperProvider;

  @Override
  public GetMenuResponse findMenu(String restaurantId) {
    GetMenuResponse getMenuResponse = new GetMenuResponse();
    Menu menu = menuRepositoryService.findMenu(restaurantId);
    getMenuResponse.setMenu(menu);
    return getMenuResponse;
  }

  @Override
  public Item findItem(String itemId, String restaurantId)
      throws ItemNotFoundInRestaurantMenuException, ItemNotFromSameRestaurantException {
    Menu menu = menuRepositoryService.findMenu(restaurantId);

    for (Item item : menu.getItems()) {
      if (itemId.equals(item.getItemId())) {
        return item;
      }
    }

    throw new ItemNotFoundInRestaurantMenuException("No item found matching the itemId " + itemId);
  }

  @Override
  public GetMenuResponse addItem(Item item, String restaurantId) throws Exception {

    String itemId = itemRepositoryService.addItem(item);
    GetMenuResponse menuResponse = new GetMenuResponse();

    try {
      Menu menu = menuRepositoryService.addItemToMenu(itemId, restaurantId);
      menuResponse.setMenu(menu);
      menuResponse.setMenuResponseType(0);
    } catch (Exception ex) {
      throw ex;
    }


    return menuResponse;
  }

  @Override
  public GetMenuResponse removeItem(String itemId, String restaurantId) {
    GetMenuResponse menuResponse = new GetMenuResponse();
    try {

      Menu menu = menuRepositoryService.removeItemFromMenu(itemId, restaurantId);
      menuResponse.setMenu(menu);
      menuResponse.setMenuResponseType(0);
    } catch (Exception ex) {
      if (ex instanceof ItemNotFoundInRestaurantMenuException) {
        menuResponse.setMenuResponseType(((ItemNotFoundInRestaurantMenuException) ex).getErrorType());
        return menuResponse;
      }
    }

    return menuResponse;
  }

  @Override
  public GetMenuResponse updateItem(Item editItem, String restaurantId) {
    GetMenuResponse menuResponse = new GetMenuResponse();
    Menu menu = menuRepositoryService.findMenu(restaurantId);
    if (menu != null) {
      itemRepositoryService.addItem(editItem);
      menu = menuRepositoryService.updateItemInMenu(editItem, restaurantId);
      menuResponse.setMenu(menu);
      menuResponse.setMenuResponseType(0);
    } else {
      menuResponse.setMenuResponseType(new ItemNotFoundInRestaurantMenuException("").getErrorType());
    }

    return menuResponse;
  }
}
