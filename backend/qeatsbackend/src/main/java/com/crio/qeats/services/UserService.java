package com.crio.qeats.services;

import com.crio.qeats.exchanges.UserResponse;

public interface UserService {

  UserResponse login (String username, String password);

  UserResponse register (String username, String password, String name, String restaurantName);
}
