package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.ItemQuantity;
import com.crio.qeats.models.QuantityEntity;
import com.crio.qeats.repositories.QuantityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.Optional;

@Service
public class QuantityRepositoryServiceImpl implements QuantityRespositoryService {

  @Autowired
  private QuantityRepository quantityRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;
  @Override
  public ItemQuantity updateQuantity(String itemId, String restaurantId, int quantity) throws Exception {

    ItemQuantity itemQuantity = null;
    Optional<QuantityEntity> quantityEntityOptional = quantityRepository.findByItemIdAndRestaurantId(itemId, restaurantId);
    ModelMapper modelMapper = modelMapperProvider.get();
    if (quantityEntityOptional.isPresent()) {
      QuantityEntity quantityEntity = quantityEntityOptional.get();
      quantityEntity.setQuantity(quantity);
      quantityRepository.save(quantityEntity);

      itemQuantity = modelMapper.map(quantityEntity, ItemQuantity.class);

    } else {
      throw new Exception("Either restaurant or item id is invalid");
    }

    return itemQuantity;
  }

  @Override
  public ItemQuantity getQuantity(String itemId, String restaurantId) throws Exception {
    ItemQuantity itemQuantity = null;

    Optional<QuantityEntity> quantityEntityOptional = quantityRepository.findByItemIdAndRestaurantId(itemId, restaurantId);
    ModelMapper modelMapper = modelMapperProvider.get();
    if (quantityEntityOptional.isPresent()) {
      QuantityEntity quantityEntity = quantityEntityOptional.get();

      itemQuantity = modelMapper.map(quantityEntity, ItemQuantity.class);

    } else {
      throw new Exception("Either restaurant or item id is invalid");
    }

    return itemQuantity;
  }
}
