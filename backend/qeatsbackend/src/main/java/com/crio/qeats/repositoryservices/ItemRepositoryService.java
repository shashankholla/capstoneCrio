package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Item;
import org.springframework.stereotype.Service;

@Service
public interface ItemRepositoryService {

  String addItem(Item item);

  Item findByItemId(String itemId);
}
