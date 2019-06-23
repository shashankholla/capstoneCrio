package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;

public class PostOrderRequest {

  @NotNull
  String cartId;

  public String getCartId() {
    return cartId;
  }

  public void setCartId(String cartId) {
    this.cartId = cartId;
  }
}