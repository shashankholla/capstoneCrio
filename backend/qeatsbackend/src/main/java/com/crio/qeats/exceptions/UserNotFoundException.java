package com.crio.qeats.exceptions;

public class UserNotFoundException extends QEatsException {
  public UserNotFoundException() {
  }

  @Override
  public int getErrorType() {
    return USER_NOT_FOUND;
  }
}



