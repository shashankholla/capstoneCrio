package com.crio.qeats.services;

import com.crio.qeats.exceptions.UserNotFoundException;
import com.crio.qeats.exchanges.UserResponse;
import com.crio.qeats.repositoryservices.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepositoryService userRepositoryService;

  @Override
  public UserResponse login(String username, String password) {
    try {
      if (userRepositoryService.checkLogin(username, password)) {
        return new UserResponse(userRepositoryService.getUserRestaurant(username), 0);
      } else {
        return new UserResponse(null, new UserNotFoundException().getErrorType());
      }
    } catch (UserNotFoundException e) {
      return new UserResponse(null, new UserNotFoundException().getErrorType());
    }

  }

  @Override
  public UserResponse register(String username, String password, String name, String restaurantName) {
    return null;
  }
}
