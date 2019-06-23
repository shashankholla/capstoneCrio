package com.crio.qeats.repositories;

import com.crio.qeats.models.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<ItemEntity, String> {

  Optional<ItemEntity> findByItemId(String itemId);

}
