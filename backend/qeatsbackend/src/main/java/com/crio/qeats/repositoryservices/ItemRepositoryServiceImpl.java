package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Item;
import com.crio.qeats.models.ItemEntity;
import com.crio.qeats.repositories.ItemRepository;
import javax.inject.Provider;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemRepositoryServiceImpl implements ItemRepositoryService {

  @Autowired
  Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private ItemRepository itemRepository;

  @Override
  public String addItem(Item item) {
    ModelMapper modelMapper = modelMapperProvider.get();
    ItemEntity itemEntity = modelMapper.map(item, ItemEntity.class);
    itemRepository.save(itemEntity);

    return item.getItemId();
  }

  @Override
  public Item findByItemId(String itemId) {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<ItemEntity> itemEntity = itemRepository.findByItemId(itemId);

    Item item = null;
    if (itemEntity.isPresent()) {
      item = modelMapper.map(itemEntity, Item.class);
    }
    return item;
  }
}
