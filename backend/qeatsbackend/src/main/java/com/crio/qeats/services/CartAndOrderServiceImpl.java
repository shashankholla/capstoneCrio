package com.crio.qeats.services;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.exchanges.GetCartRequest;
import com.crio.qeats.exchanges.GetOrdersResponse;
import com.crio.qeats.repositories.ItemRepository;
import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CartAndOrderServiceImpl implements CartAndOrderService {

  @Autowired
  ItemRepository itemRepository;

  @Autowired
  CartRepositoryService cartRepositoryService;

  @Autowired
  OrderRepositoryService orderRepositoryService;

  @Autowired
  MenuService menuService;

  public CartModifiedResponse response = new CartModifiedResponse();

  @Override
  public Cart findCartByUserId(GetCartRequest getCartRequest) {
    Optional<Cart> cart = cartRepositoryService.findCartByUserId(getCartRequest.getUserId());
    Cart cartPassed = cart.get();
    return cartPassed;
  }

  @Override
  public Cart findOrCreateCart(String userId) throws CartNotFoundException {
    Optional<Cart> cart = cartRepositoryService.findCartByUserId(userId);

    if (cart.isPresent()) {
      return cart.get();
    } else {
      Cart newCart = new Cart();
      newCart.setRestaurantId("");
      newCart.setUserId(userId);
      cartRepositoryService.createCart(newCart);
      cartRepositoryService.findCartByCartId(newCart.getId());
      return newCart;
    }
  }

  @Override
  public CartModifiedResponse addItemToCart(String itemId, String cartId, String restaurantId)
      throws ItemNotFromSameRestaurantException, CartNotFoundException {
    CartModifiedResponse response = new CartModifiedResponse();
    try {
      Cart cart = cartRepositoryService.findCartByCartId(cartId);
      if (cart.getRestaurantId().equals(restaurantId)) {
        Item item = menuService.findItem(itemId, restaurantId);
        Cart updatedCart = cartRepositoryService.addItem(item, cartId, restaurantId);
        response.setCart(updatedCart);
        response.setCartResponseType(0);
      } else {
        response.setCart(cart);
        response.setCartResponseType(new ItemNotFromSameRestaurantException().getErrorType());
      }
    } catch (CartNotFoundException e) {
      System.out.println("Cart Not Found: " + e);
      throw new CartNotFoundException();
    }
    return response;
  }

  @Override
  public CartModifiedResponse removeItemFromCart(String itemId,
                                                 String cartId,
                                                 String restaurantId) {

    try {
      Item item = menuService.findItem(itemId, restaurantId);
      Cart updatedCart = cartRepositoryService.removeItem(item, cartId, restaurantId);
      response.setCart(updatedCart);
      response.setCartResponseType(0);
    } catch (ItemNotFromSameRestaurantException e) {
      System.out.println("Item Not Found: " + e);
    } catch (CartNotFoundException e) {
      System.out.println("Cart Not Found " + e);
    }
    return response;
  }

  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    try {
      Cart cart = cartRepositoryService.findCartByCartId(cartId);
      if (!cart.getItems().isEmpty()) {
        Order order = orderRepositoryService.placeOrder(cart);
        ;
        return order;
      } else {
        System.out.println(cart);
        throw new EmptyCartException("Cart is Empty");
      }

    } catch (CartNotFoundException e) {
      throw new CartNotFoundException();
    }
  }

  @Override
  public GetOrdersResponse getAllPlacedOrders(String restaurantId) {
    List<Order> orderList = orderRepositoryService.getOrdersByRestaurant(restaurantId);
    List<Order> finalOrderList = new ArrayList<>();
    if (orderList.size() > 0) {
      for (Order order : orderList) {
        if (order.getStatus().equals("PLACED")) {
          finalOrderList.add(order);
        }
      }
    }
    return new GetOrdersResponse(finalOrderList, restaurantId);
  }

  @Override
  public GetOrdersResponse getAllOrders(String restaurantId) {
    List<Order> orderList = orderRepositoryService.getOrdersByRestaurant(restaurantId);
    List<Order> finalOrderList = new ArrayList<>();
    if (orderList.size() > 0) {
      for (Order order : orderList) {
        if (order.getStatus().equals("ACCEPTED")
            || order.getStatus().equals("PREPARING")
            || order.getStatus().equals("PACKED")) {
          finalOrderList.add(order);
        }
      }
    }
    return new GetOrdersResponse(finalOrderList, restaurantId);
  }

  @Override
  public GetOrdersResponse updateStatusOfOrder(String restaurantId, String orderId,
                                               String status) {
    Order order = orderRepositoryService.getOrderById(orderId);
    if (order != null ) {
      Order newOrder = orderRepositoryService.updateStatus(restaurantId, orderId, status);
    }
    List<Order> orderList = orderRepositoryService.getOrdersByRestaurant(restaurantId);
    List<Order> finalOrderList = new ArrayList<>();
    if (orderList.size() > 0) {
      for (Order orderInList : orderList) {
        if (orderInList.getStatus().equals("ACCEPTED")
            || orderInList.getStatus().equals("PREPARING")
            || orderInList.getStatus().equals("PACKED")) {
          finalOrderList.add(orderInList);
        }
      }
    }
    return new GetOrdersResponse(finalOrderList, restaurantId);
  }

  @Override
  public GetOrdersResponse updateStatusOfPlacedOrders(String restaurantId, String orderId, String status) {
    Order order = orderRepositoryService.getOrderById(orderId);
    if (order != null && order.getStatus().equals("PLACED")) {
      Order newOrder = orderRepositoryService.updateStatus(restaurantId, orderId, status);
    }
    List<Order> orderList = orderRepositoryService.getOrdersByRestaurant(restaurantId);
    List<Order> finalOrderList = new ArrayList<>();
    if (orderList.size() > 0) {
      for (Order orderInList : orderList) {
        if (orderInList.getStatus().equals("PLACED")) {
          finalOrderList.add(orderInList);
        }
      }
    }
    return new GetOrdersResponse(finalOrderList, restaurantId);
  }
}
