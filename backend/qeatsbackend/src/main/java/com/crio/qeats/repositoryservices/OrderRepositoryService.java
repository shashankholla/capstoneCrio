/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepositoryService {


  /**
   * TODO: CRIO_TASK_MODULE_MENUAPI - Implement placeOrder.
   * Place order based on the cart.
   * @param cart - cart to use for placing order.
   * @return return - the order that was just placed.
   */
  Order placeOrder(Cart cart);

  List<Order> getOrdersByRestaurant(String restaurantId);

  Order updateStatus(String restaurantId, String orderId,
                     String status);

  Order getOrderById(String orderId);

}
