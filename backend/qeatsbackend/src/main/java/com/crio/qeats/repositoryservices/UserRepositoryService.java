package com.crio.qeats.repositoryservices;

import com.crio.qeats.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserRepositoryService {

  boolean checkLogin(String username, String password);

  String getUserRestaurant(String username) throws UserNotFoundException;
}
