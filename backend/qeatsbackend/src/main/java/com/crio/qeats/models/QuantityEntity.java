package com.crio.qeats.models;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quantities")
public class QuantityEntity {

  @Id
  private String id;

  @NotNull
  private String itemId;

  @NotNull
  private String restaurantId;

  @NotNull
  private int quantity;

}
