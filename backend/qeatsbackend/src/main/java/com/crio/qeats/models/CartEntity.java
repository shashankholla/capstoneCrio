package com.crio.qeats.models;

import com.crio.qeats.dto.Item;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "carts")
@JsonIgnoreProperties({"status"})
@NoArgsConstructor
public class CartEntity {

  @Id
  private String id;

  @NotNull
  private String restaurantId;

  @NotNull
  private String userId;

  @NotNull
  private List<Item> items = new ArrayList();

  @NotNull
  private int total = 0;

  public void addItem(Item item) {
    items.add(item);
    total += item.getPrice();
  }

  public void removeItem(Item item) {
    boolean removed = items.remove(item);

    if (removed) {
      total -= item.getPrice();
    }
  }

  public void clearCart() {
    if (items.size() > 0) {
      items.clear();
      total = 0;
    }
  }
}