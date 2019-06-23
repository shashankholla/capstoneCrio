/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.controller;

import static org.apache.commons.lang3.RandomStringUtils.random;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.ItemQuantity;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.AddCartRequest;
import com.crio.qeats.exchanges.AddMenuRequest;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.exchanges.DeleteCartRequest;
import com.crio.qeats.exchanges.EditQuantityRequest;
import com.crio.qeats.exchanges.GetCartRequest;
import com.crio.qeats.exchanges.GetMenuResponse;
import com.crio.qeats.exchanges.GetOrdersResponse;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.exchanges.PostOrderRequest;
import com.crio.qeats.exchanges.RemoveMenuItemRequest;
import com.crio.qeats.exchanges.UpdateOrderStatusRequest;
import com.crio.qeats.exchanges.UserLoginRequest;
import com.crio.qeats.exchanges.UserResponse;
import com.crio.qeats.services.CartAndOrderService;
import com.crio.qeats.services.MenuService;
import com.crio.qeats.services.QuantityService;
import com.crio.qeats.services.RestaurantService;
import com.crio.qeats.services.UserService;

import java.time.LocalTime;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// TODO: CRIO_TASK_MODULE_RESTAURANTSAPI
// Implement Controller using Spring annotations.
// Remember, annotations have various "targets". They can be class level, method level or others.

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Log4j2
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)
public class RestaurantController {

  public static final String RESTAURANT_API_ENDPOINT = "/qeats/v1";
  public static final String RESTAURANTS_API = "/restaurants";
  public static final String MENU_API = "/menu";
  public static final String MENU_ITEM_API = "/menu/item";
  public static final String MENU_ITEM_QUANTITY_API = "/item/available";
  public static final String CART_API = "/cart";
  public static final String CART_ITEM_API = "/cart/item";
  public static final String CART_CLEAR_API = "/cart/clear";
  public static final String POST_ORDER_API = "/order"; //FIXME: rename it
  public static final String GET_ORDERS_API = "/orders";
  public static final String PLACED_ORDERS_API = "/orders/placed";
  public static final String USER_LOGIN_API = "/user/login";

  @Autowired
  RestaurantService restaurantService;

  @Autowired
  MenuService menuService;

  @Autowired
  CartAndOrderService cartAndOrderService;

  @Autowired
  UserService userService;

  @Autowired
  QuantityService quantityService;

  // TODO: CRIO_TASK_MODULE_MULTITHREADING - Improve the performance of this GetRestaurants API
  //  and keep the functionality same.
  // Get the list of open restaurants near the specified latitude/longitude & matching searchFor.
  // API URI: /qeats/v1/restaurants?latitude=21.93&longitude=23.0&searchFor=tamil
  // Method: GET
  // Query Params: latitude, longitude, searchFor(optional)
  // Success Output:
  // 1). If searchFor param is present, return restaurants as a list matching the following criteria
  //   1) open now
  //   2) is near the specified latitude and longitude
  //   3) searchFor matching(partially or fully):
  //      - restaurant name
  //      - or restaurant attribute
  //      - or item name
  //      - or item attribute (all matching is done ignoring case)
  //
  //   4) order the list by following the rules before returning
  //      1) Restaurant name
  //          - exact matches first
  //          - partial matches second
  //      2) Restaurant attributes
  //          - partial and full matches in any order
  //      3) Item name
  //          - exact matches first
  //          - partial matches second
  //      4) Item attributes
  //          - partial and full matches in any order
  //      Eg: For example, when user searches for "Udupi", "Udupi Bhavan" restaurant should
  //      come ahead of restaurants having "Udupi" in attribute.
  // 2). If searchFor param is absent,
  //     1) If there are restaurants near by return the list
  //     2) Else return empty list
  //
  // - For peak hours: 8AM-10AM, 1PM-2PM, 7PM-9PM
  //   - service radius is 3KMs.
  // - All other times
  //   - serving radius is 5KMs.
  // - If there are no restaurants, return empty list of restaurants.
  //
  //
  // HTTP Code: 200
  // {
  //  "restaurants": [
  //    {
  //      "restaurantId": "10",
  //      "name": "A2B",
  //      "city": "Hsr Layout",
  //      "imageUrl": "www.google.com",
  //      "latitude": 20.027,
  //      "longitude": 30.0,
  //      "opensAt": "18:00",
  //      "closesAt": "23:00",
  //      "attributes": [
  //        "Tamil",
  //        "South Indian"
  //      ]
  //    }
  //  ]
  // }
  //
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // Eg:
  // curl -X GET "http://localhost:8081/qeats/v1/restaurants?latitude=28.4900591&longitude=77.536386&searchFor=tamil"
  @GetMapping(RESTAURANTS_API)
  public ResponseEntity<GetRestaurantsResponse> getRestaurants(
      GetRestaurantsRequest getRestaurantsRequest) {
    log.info("getRestaurants called with {}", getRestaurantsRequest);

    GetRestaurantsResponse getRestaurantsResponse;

    Double latitude = getRestaurantsRequest.getLatitude();
    Double longitude = getRestaurantsRequest.getLongitude();
    String searchFor = getRestaurantsRequest.getSearchFor();
    getRestaurantsResponse = restaurantService
        .findAllRestaurantsCloseBy(getRestaurantsRequest, LocalTime.now());
    log.info("getRestaurants returned {}", getRestaurantsResponse);
    if (latitude == null || longitude == null
        || latitude < 0 || latitude > 90 || longitude < 0
        || longitude > 180) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getRestaurantsResponse);
    } else if (searchFor != null && !searchFor.equals("")) {
      getRestaurantsResponse = restaurantService
          .findRestaurantsBySearchQuery(getRestaurantsRequest, LocalTime.now());
      return ResponseEntity.ok().body(getRestaurantsResponse);
    } else {
      return ResponseEntity.ok().body(getRestaurantsResponse);
    }
  }

  // TIP(MODULE_MENUAPI): Model Implementation for getting menu given a restaurantId.
  // Get the Menu for the given restaurantId
  // API URI: /qeats/v1/menu?restaurantId=11
  // Method: GET
  // Query Params: restaurantId
  // Success Output:
  // 1). If restaurantId is present return Menu
  // 2). Otherwise respond with BadHttpRequest.
  //
  // HTTP Code: 200
  // {
  //  "menu": {
  //    "items": [
  //      {
  //        "attributes": [
  //          "South Indian"
  //        ],
  //        "id": "1",
  //        "imageUrl": "www.google.com",
  //        "itemId": "10",
  //        "name": "Idly",
  //        "price": 45
  //      }
  //    ],
  //    "restaurantId": "11"
  //  }
  // }
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // Eg:
  // curl -X GET "http://localhost:8081/qeats/v1/menu?restaurantId=11"
  @GetMapping(MENU_API)
  public ResponseEntity<GetMenuResponse> getMenu(
      @RequestParam("restaurantId") String restaurantId) {
    GetMenuResponse getMenuResponse = menuService.findMenu(restaurantId);

    log.info("getMenu returned with {}", getMenuResponse);

    return ResponseEntity.ok().body(getMenuResponse);
  }

  @PostMapping(MENU_ITEM_API)
  public ResponseEntity<GetMenuResponse> addItem(@RequestBody AddMenuRequest addMenuRequest) {

    GetMenuResponse getMenuResponse = null;
    try {

      Item item = addMenuRequest.getItem();
      item.setItemId(random(10,true,true));
      String restaurantId = addMenuRequest.getRestaurantId();
      getMenuResponse = menuService.addItem(item, restaurantId);
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(null);
    }

    log.info("Add item returned");

    return ResponseEntity.ok().body(getMenuResponse);
  }

  @DeleteMapping(MENU_ITEM_API)
  public ResponseEntity<GetMenuResponse> removeItem(
      @RequestBody RemoveMenuItemRequest removeMenuItemRequest) {

    GetMenuResponse menuResponse = null;
    try {
      String itemId = removeMenuItemRequest.getItemId();
      String restaurantId = removeMenuItemRequest.getRestaurantId();
      menuResponse = menuService.removeItem(itemId, restaurantId);
    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(null);
    }

    return ResponseEntity.ok().body(menuResponse);
  }

  @PutMapping(MENU_ITEM_API)
  public ResponseEntity<GetMenuResponse> editItem(@RequestBody AddMenuRequest addMenuRequest) {
    Item item = addMenuRequest.getItem();
    String resturantId = addMenuRequest.getRestaurantId();
    if (resturantId == null || resturantId.equals("")) {
      return ResponseEntity.badRequest().body(null);
    } else {
      GetMenuResponse menuResponse = menuService.updateItem(item, resturantId);
      return ResponseEntity.ok().body(menuResponse);
    }

  }

  @PutMapping(MENU_ITEM_QUANTITY_API)
  public ResponseEntity<ItemQuantity> editItemQuantity(@RequestBody EditQuantityRequest editQuantityRequest) {

    ItemQuantity itemQuantity = null;
    try {
      String itemId = editQuantityRequest.getItemId();

      String restaurantId = editQuantityRequest.getRestaurantId();
      int quantity = editQuantityRequest.getQuantity();
      itemQuantity = quantityService.updateQuantity(itemId, restaurantId, quantity);

    } catch (Exception ex) {
      return ResponseEntity.badRequest().body(null);
    }

    if (itemQuantity == null) {
      return ResponseEntity.badRequest().body(null);
    }

    return ResponseEntity.ok().body(itemQuantity);


  }

  @GetMapping(MENU_ITEM_QUANTITY_API)
  public ResponseEntity<ItemQuantity> getItemQuantity(
      @RequestParam("itemId") String itemId,
      @RequestParam("restaurantId") String restaurantId) {
    ItemQuantity itemQuantity = null;
    try {
      itemQuantity = quantityService.getQuantity(itemId, restaurantId);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body(null);
    }
    return ResponseEntity.ok().body(itemQuantity);
  }

  // TODO: CRIO_TASK_MODULE_MENUAPI - Implement GET Cart for the given userId.
  // API URI: /qeats/v1/cart?userId=arun
  // Method: GET
  // Query Params: userId
  // Success Output:
  // 1). If userId is present return user's cart
  //     - If user has an active cart, then return it
  //     - otherwise return an empty cart
  //
  // 2). If userId is not present then respond with BadHttpRequest.
  //
  // HTTP Code: 200
  // {
  //  "id": "10",
  //  "items": [
  //    {
  //      "attributes": [
  //        "South Indian"
  //      ],
  //      "id": "1",
  //      "imageUrl": "www.google.com",
  //      "itemId": "10",
  //      "name": "Idly",
  //      "price": 45
  //    }
  //  ],
  //  "restaurantId": "11",
  //  "total": 45,
  //  "userId": "arun"
  // }
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // Eg:
  // curl -X GET "http://localhost:8081/qeats/v1/cart?userId=arun"
  @GetMapping(CART_API)
  public ResponseEntity<Cart> getCart(GetCartRequest getCartRequest) {
    log.info("getRestaurants called with {}", getCartRequest);
    Cart getCartResponse = new Cart();
    String userId = getCartRequest.getUserId();
    if (userId == null || userId.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getCartResponse);
    }
    try {
      getCartResponse =
          cartAndOrderService.findOrCreateCart(getCartRequest.getUserId());
      return ResponseEntity.ok().body(getCartResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getCartResponse);
    }
  }


  // TODO: CRIO_TASK_MODULE_MENUAPI: Implement add item to cart
  // API URI: /qeats/v1/cart/item
  // Method: POST
  // Request Body format:
  //  {
  //    "cartId": "1",
  //    "itemId": "10",
  //    "restaurantId": "11"
  //  }
  //
  // Success Output:
  // 1). If user has an active cart, add item to the cart.
  // 2). Otherwise create an empty cart and add the given item.
  // 3). If item to be added is not from same restaurant the 'cartResponseType' should be
  //     QEatsException.ITEM_NOT_FOUND_IN_RESTAURANT_MENU.
  //
  // HTTP Code: 200
  // Response body contains
  //  {
  //    "cart": {
  //      "id": "1",
  //      "items": [
  //        {
  //          "attributes": [
  //            "South Indian"
  //          ],
  //          "id": "1",
  //          "imageUrl": "www.google.com",
  //          "itemId": "10",
  //          "name": "Idly",
  //          "price": 45
  //        }
  //      ],
  //      "restaurantId": "11",
  //      "total": 45,
  //      "userId": "arun"
  //     },
  //     "cartResponseType": 0
  //  }
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // curl -X GET "http://localhost:8081/qeats/v1/cart/item"
  @PostMapping(CART_ITEM_API)
  public ResponseEntity<CartModifiedResponse> addItem(@RequestBody AddCartRequest addCartRequest) {
    CartModifiedResponse cartModifiedResponse = new CartModifiedResponse();
    try {
      cartModifiedResponse =
          cartAndOrderService.addItemToCart(addCartRequest.getItemId(), addCartRequest.getCartId(),
              addCartRequest.getRestaurantId());
      return ResponseEntity.ok().body(cartModifiedResponse);
    } catch (CartNotFoundException | ItemNotFromSameRestaurantException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cartModifiedResponse);
    }
  }


  // TODO: CRIO_TASK_MODULE_MENUAPI: Implement remove item from given cartId
  // API URI: /qeats/v1/cart/item
  // Method: DELETE
  // Request Body format:
  //  {
  //    "cartId": "1",
  //    "itemId": "10",
  //    "restaurantId": "11"
  //  }
  //
  // Success Output:
  // 1). If item is present in user cart, then remove it.
  // 2). Otherwise, do nothing.
  //
  // HTTP Code: 200
  // Response body contains
  //  {
  //    "cart" : {
  //      "id": "1",
  //      "items": [ ],
  //      "restaurantId": "",
  //      "total": 0,
  //      "userId": "arun"
  //     },
  //     "cartResponseType": 0
  //  }
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // curl -X GET "http://localhost:8081/qeats/v1/cart/item"
  @DeleteMapping(CART_ITEM_API)
  public ResponseEntity<CartModifiedResponse> deleteItem(
      @RequestBody DeleteCartRequest deleteCartRequest) {
    CartModifiedResponse cartModifiedResponse =
        cartAndOrderService.removeItemFromCart(
            deleteCartRequest.getItemId(), deleteCartRequest.getCartId(),
            deleteCartRequest.getRestaurantId());
    return ResponseEntity.ok().body(cartModifiedResponse);

  }


  // TODO: CRIO_TASK_MODULE_MENUAPI: Place order for the given cartId.
  // API URI: /qeats/v1/order
  // Method: POST
  // Request Body format:
  //  {
  //    "cartId": "1"
  //  }
  //
  // Success Output:
  // 1). Place order for the given cartId and clear the cart.
  // 2). If cart is empty then response should be Bad Http Request.
  //
  // HTTP Code: 200
  // Response body contains
  //  {
  //    "id": "1",
  //    "items": [
  //      {
  //        "attributes": [
  //          "South Indian"
  //        ],
  //        "id": "1",
  //        "imageUrl": "www.google.com",
  //        "itemId": "10",
  //        "name": "Idly",
  //        "price": 45
  //      }
  //    ],
  //    "restaurantId": "11",
  //    "total": 45,
  //    "userId": "arun"
  //  }
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // curl -X GET "http://localhost:8081/qeats/v1/order"
  @PostMapping(POST_ORDER_API)
  public ResponseEntity<Order> placeOrder(@RequestBody PostOrderRequest postOrderRequest) {
    String cartId = postOrderRequest.getCartId();
    Order order = new Order();
    try {
      order = cartAndOrderService.postOrder(cartId);
      return ResponseEntity.ok().body(order);
    } catch (CartNotFoundException | EmptyCartException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(order);
    }
  }

  @GetMapping(PLACED_ORDERS_API)
  public ResponseEntity<GetOrdersResponse> getAllPlacedOrders(
      @RequestParam("restaurantId") String restaurantId) {
    if (restaurantId == null || restaurantId.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      GetOrdersResponse getOrdersResponse =
          cartAndOrderService.getAllPlacedOrders(restaurantId);
      return ResponseEntity.ok().body(getOrdersResponse);
    }

  }

  @GetMapping(GET_ORDERS_API)
  public ResponseEntity<GetOrdersResponse> getAllOrders(
      @RequestParam("restaurantId") String restaurantId) {
    if (restaurantId == null || restaurantId.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      GetOrdersResponse getOrdersResponse =
          cartAndOrderService.getAllOrders(restaurantId);
      return ResponseEntity.ok().body(getOrdersResponse);
    }

  }

  @PutMapping(PLACED_ORDERS_API)
  public ResponseEntity<GetOrdersResponse> updatePlacedOrders(
      @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
    String restaurantId = updateOrderStatusRequest.getRestaurantId();
    String orderId = updateOrderStatusRequest.getOrderId();
    if (restaurantId == null || restaurantId.equals("")
        || orderId == null || orderId.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      GetOrdersResponse getOrdersResponse =
          cartAndOrderService.updateStatusOfPlacedOrders(restaurantId, orderId,
              updateOrderStatusRequest.getStatus());
      return ResponseEntity.ok().body(getOrdersResponse);
    }

  }

  @PutMapping(GET_ORDERS_API)
  public ResponseEntity<GetOrdersResponse> updateOrderStatus(
      @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
    String restaurantId = updateOrderStatusRequest.getRestaurantId();
    String orderId = updateOrderStatusRequest.getOrderId();
    if (restaurantId == null || restaurantId.equals("")
        || orderId == null || orderId.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      GetOrdersResponse getOrdersResponse =
          cartAndOrderService.updateStatusOfOrder(restaurantId, orderId,
              updateOrderStatusRequest.getStatus());
      return ResponseEntity.ok().body(getOrdersResponse);
    }

  }

  @PostMapping(USER_LOGIN_API)
  public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
    String username = userLoginRequest.getUsername();
    String password = userLoginRequest.getPassword();
    if (username == null || username.equals("") || password == null || password.equals("")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } else {
      return ResponseEntity.ok().body(userService.login(username, password));
    }

  }

}
