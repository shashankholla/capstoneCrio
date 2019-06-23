package com.crio.qeats.exceptions;

abstract class QEatsException extends RuntimeException {

  static final int EMPTY_CART = 100;
  static final int ITEM_NOT_FOUND_IN_RESTAURANT_MENU = 101;
  static final int ITEM_NOT_FROM_SAME_RESTAURANT = 102;
  static final int CART_NOT_FOUND = 103;
  static final int USER_NOT_FOUND = 104;

  QEatsException() {}

  QEatsException(String message) {
    super(message);
  }

  public abstract int getErrorType();

}
