package com.crio.qeats.services;

import com.crio.qeats.dto.ItemQuantity;
import org.springframework.stereotype.Service;

@Service
public interface QuantityService {

  public ItemQuantity updateQuantity (String itemId, String restaurantId, int quantity);

  public ItemQuantity getQuantity(String itemId, String restaurantId) throws Exception;
}
