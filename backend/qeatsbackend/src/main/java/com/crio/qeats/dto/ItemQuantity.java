package com.crio.qeats.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemQuantity {
  @Id
  private String id;

  @NotNull
  private String itemId;

  @NotNull
  private String restaurantId;

  @NotNull
  private int quantity;
}
