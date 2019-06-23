/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.models.CartEntity;
import com.crio.qeats.repositories.CartRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartRepositoryServiceImpl implements CartRepositoryService {

  @Autowired
  CartRepository cartRepository;

  @Autowired
  Provider<ModelMapper> modelMapperProvider;

  @Override
  public String createCart(Cart cart) {
    ModelMapper modelMapper = modelMapperProvider.get();
    CartEntity cartEntity = modelMapper.map(cart, CartEntity.class);
    cartRepository.save(cartEntity);
    return cart.getRestaurantId();
  }

  @Override
  public Optional<Cart> findCartByUserId(String userId) throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();

    Optional<CartEntity> optionalCartEntity = cartRepository.findByUserId(userId);

    Optional<Cart> optionalCart = Optional.empty();

    Cart cart = null;

    if (optionalCartEntity.isPresent()) {
      cart = modelMapper.map(optionalCartEntity.get(), Cart.class);
      optionalCart = Optional.of(cart);
    } else {
      throw new CartNotFoundException();
    }
    return optionalCart;
  }

  @Override
  public Cart findCartByCartId(String cartId) throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<CartEntity> cartEntity = cartRepository.findById(cartId);
    Cart cart = null;
    if (cartEntity.isPresent()) {
      cart = modelMapper.map(cartEntity.get(), Cart.class);
    } else {
      throw new CartNotFoundException();
    }
    return cart;
  }

  @Override
  public Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<CartEntity> optionalCartEntity = cartRepository.findById(cartId);
    Cart cart = null;
    if (optionalCartEntity.isPresent()) {
      CartEntity cartEntity = optionalCartEntity.get();
      cartEntity.addItem(item);
      if (cartEntity.getItems().size() == 1) {
        cartEntity.setRestaurantId(restaurantId);
      }
      cartRepository.save(cartEntity);
      cart = modelMapper.map(cartEntity, Cart.class);
    } else {
      throw new CartNotFoundException();
    }
    return cart;
  }

  @Override
  public Cart removeItem(Item item, String cartId, String restaurantId)
      throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<CartEntity> optionalCartEntity = cartRepository.findById(cartId);
    Cart cart = null;
    if (optionalCartEntity.isPresent()) {
      CartEntity cartEntity = optionalCartEntity.get();
      List<Item> itemList = cartEntity.getItems();
      for (Item itemInCart : itemList) {
        if (item.getItemId().equals(itemInCart.getId())) {
          cartEntity.removeItem(item);
          break;
        }
      }
      if (cartEntity.getItems().size() == 0) {
        cartEntity.setRestaurantId("");
      }
      cartRepository.save(cartEntity);
      cart = modelMapper.map(cartEntity, Cart.class);
    } else {
      throw new CartNotFoundException();
    }
    return cart;
  }
}