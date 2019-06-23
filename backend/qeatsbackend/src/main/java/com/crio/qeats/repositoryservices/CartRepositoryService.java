/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.CartNotFoundException;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface CartRepositoryService {

  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Add the given cart to the mongodb `carts` collection.
   * @param cart - cart to be created
   * @return return - the cartId of the created cart
   */
  String createCart(Cart cart);

  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Find and return the cart for the corresponding userId.
   * @param userId - userId
   * @return return Cart - cart corresponding to user id.
   */
  Optional<Cart> findCartByUserId(String userId);

  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Find and return the cart for the given cartId.
   * @param cartId - id of the cart to be found
   * @return Cart - cart corresponding to the given cartId
   * @throws CartNotFoundException - if cartId is invalid
   */
  Cart findCartByCartId(String cartId) throws CartNotFoundException;

  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Add the given item to cart with the given cartId.
   * @param item - item to be added
   * @param cartId - if of the cart
   * @param restaurantId - restaurant id of the the item
   * @return Cart - updated cart after adding item
   * @throws CartNotFoundException - if cartId is invalid
   */
  Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException;

  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Remove the given item from cart with the given cartId.
   * @param item - item to be removed
   * @param cartId - id of the cart
   * @param restaurantId - restaurant id of the item
   * @return Cart - updated cart after removing item
   * @throws CartNotFoundException if cartId is invalid
   */
  Cart removeItem(Item item, String cartId, String restaurantId) throws CartNotFoundException;


}
