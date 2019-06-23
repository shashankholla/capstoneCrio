package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;

public class DeleteCartRequest {

  @NotNull
  private String cartId;

  @NotNull
  private String itemId;

  @NotNull
  private String restaurantId;

  public String getCartId() {
    return cartId;
  }

  public void setCartId(String cartId) {
    this.cartId = cartId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }
}
