package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.ItemQuantity;
import org.springframework.stereotype.Service;

@Service
public interface QuantityRespositoryService {

  public ItemQuantity updateQuantity(String itemId,  String restaurantId, int quantity) throws Exception;

  public ItemQuantity getQuantity(String itemId, String restaurantId) throws Exception;
}
