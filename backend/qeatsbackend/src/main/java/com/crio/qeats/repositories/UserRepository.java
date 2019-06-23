package com.crio.qeats.repositories;

import com.crio.qeats.models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {

  Optional<UserEntity> findByUsername(String username);

}
