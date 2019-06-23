package com.crio.qeats.exceptions;

public class EmptyCartException extends QEatsException {

  public EmptyCartException(String message) {
    super(message);
  }

  @Override
  public int getErrorType() {
    return EMPTY_CART;
  }
}
