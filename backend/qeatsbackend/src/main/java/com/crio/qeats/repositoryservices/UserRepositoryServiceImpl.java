package com.crio.qeats.repositoryservices;

import com.crio.qeats.exceptions.UserNotFoundException;
import com.crio.qeats.models.UserEntity;
import com.crio.qeats.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Optional;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Override
  public boolean checkLogin(String username, String password) {
    Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);

    if (optionalUserEntity.isPresent()) {
      if (optionalUserEntity.get().getPassword().equals(password)) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public String getUserRestaurant(String username) throws UserNotFoundException {
    Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);

    if (optionalUserEntity.isPresent()) {
      return optionalUserEntity.get().getRestaurantId();
    }
    throw new UserNotFoundException();
  }
}
