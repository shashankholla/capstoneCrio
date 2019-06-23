/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Menu;
import com.crio.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.models.MenuEntity;
import com.crio.qeats.models.QuantityEntity;
import com.crio.qeats.repositories.ItemRepository;
import com.crio.qeats.repositories.MenuRepository;

import java.util.Optional;
import javax.inject.Provider;

import com.crio.qeats.repositories.QuantityRepository;
import io.swagger.models.Model;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuRepositoryServiceImpl implements MenuRepositoryService {

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private QuantityRepository quantityRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  public Menu findMenu(String restaurantId) {
    ModelMapper modelMapper = modelMapperProvider.get();

    Optional<MenuEntity> menuById = menuRepository.findMenuByRestaurantId(restaurantId);

    Menu menu = null;

    if (menuById.isPresent()) {
      menu = modelMapper.map(menuById.get(), Menu.class);
    }

    return menu;
  }

  @Override
  public Menu addItemToMenu(String itemId, String restaurantId) throws Exception {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<MenuEntity> optionalMenuEntity = menuRepository.findMenuByRestaurantId(restaurantId);
    Menu menu = null;
    if (optionalMenuEntity.isPresent()) {
      MenuEntity menuEntity = modelMapper.map(optionalMenuEntity.get(), MenuEntity.class);
      Optional<ItemEntity> itemById = itemRepository.findByItemId(itemId);
      if (itemById.isPresent()) {
        Item item = modelMapper.map(itemById.get(), Item.class);
        menuEntity.addItem(item);
        menuRepository.save(menuEntity);
        menu = modelMapper.map(menuEntity, Menu.class);

        QuantityEntity quantityEntity = new QuantityEntity();
        quantityEntity.setItemId(itemId);
        quantityEntity.setRestaurantId(restaurantId);
        quantityEntity.setQuantity(0);

        quantityRepository.save(quantityEntity);
      }
    } else {
      throw new Exception("Invalid restaurant Id");
    }
    return menu;
  }

  @Override
  public Menu removeItemFromMenu(String itemId, String restaurantId) throws Exception {
    Optional<MenuEntity> menuByRestaurantId = menuRepository.findMenuByRestaurantId(restaurantId);
    Menu menu = null;
    if (menuByRestaurantId.isPresent()) {
      MenuEntity menuEntity = menuByRestaurantId.get();
      Item itemToBeDeleted = null;
      for (Item item : menuEntity.getItems()) {
        if (itemId.equals(item.getItemId())) {
          itemToBeDeleted = item;
        }
      }

      if (itemToBeDeleted == null) {
        throw new ItemNotFoundInRestaurantMenuException("Item not in menu");
      }

      menuEntity.removeItem(itemToBeDeleted);
      menuRepository.save(menuEntity);
      ModelMapper modelMapper = modelMapperProvider.get();
      menu = modelMapper.map(menuEntity, Menu.class);
    } else {
      throw new Exception("Invalid restaurant Id");
    }

    return menu;
  }

  @Override
  public Menu updateItemInMenu(Item editedItem, String restaurantId) {
    Optional<MenuEntity> menuByRestaurantId = menuRepository.findMenuByRestaurantId(restaurantId);
    ModelMapper modelMapper = modelMapperProvider.get();
    Menu menu = null;
    if (menuByRestaurantId.isPresent()) {
      MenuEntity menuEntity = menuByRestaurantId.get();
      int i=0;
      for (Item item : menuEntity.getItems()) {
        if (editedItem.getItemId().equals(item.getItemId())) {
          item.setItemId(editedItem.getItemId());
          item.setName(editedItem.getName());
          item.setImageUrl(editedItem.getImageUrl());
          item.setPrice(editedItem.getPrice());
          item.setAttributes(editedItem.getAttributes());
          menuEntity.getItems().set(i,item);
          break;
        }
        i++;
      }
      menuRepository.save(menuEntity);
      menu = modelMapper.map(menuEntity, Menu.class);

    }
    return menu;
  }
}
