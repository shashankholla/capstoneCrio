package com.crio.qeats.repositories;

import com.crio.qeats.models.QuantityEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuantityRepository extends MongoRepository<QuantityEntity, String> {

  Optional<QuantityEntity> findByItemIdAndRestaurantId(String itemId, String restaurantId);
}
