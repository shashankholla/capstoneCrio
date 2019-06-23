package com.crio.qeats.services;

import com.crio.qeats.dto.ItemQuantity;
import com.crio.qeats.repositoryservices.QuantityRespositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuantityServiceImpl implements QuantityService {

  @Autowired
  private QuantityRespositoryService quantityRespositoryService;

  @Override
  public ItemQuantity updateQuantity(String itemId, String restauantId, int quantity) {

    ItemQuantity itemQuantity = null;
    try {

      itemQuantity = quantityRespositoryService.updateQuantity(itemId, restauantId, quantity);
    } catch (Exception ex) {

    }

    return itemQuantity;
  }

  @Override
  public ItemQuantity getQuantity(String itemId, String restaurantId) throws Exception {
    ItemQuantity itemQuantity = null;

    try {
      itemQuantity = quantityRespositoryService.getQuantity(itemId, restaurantId);
    } catch (Exception ex) {

      throw  ex;
    }

    return itemQuantity;
  }
}
