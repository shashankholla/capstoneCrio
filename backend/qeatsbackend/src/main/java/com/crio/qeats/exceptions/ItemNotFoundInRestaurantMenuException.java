package com.crio.qeats.exceptions;

public class ItemNotFoundInRestaurantMenuException extends QEatsException {

  public ItemNotFoundInRestaurantMenuException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return ITEM_NOT_FOUND_IN_RESTAURANT_MENU;
  }
}
