package com.crio.qeats.models;

import com.crio.qeats.dto.Item;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

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

  @NotNull
  private String placedTime;

  @NotNull
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public String getPlacedTime() {
    return placedTime;
  }

  public void setPlacedTime(String placedTime) {
    this.placedTime = placedTime;
  }
}
