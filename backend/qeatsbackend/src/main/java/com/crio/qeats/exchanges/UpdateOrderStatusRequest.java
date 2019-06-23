package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
  @NotNull
  String orderId;

  @NotNull
  String status;

  @NotNull
  String restaurantId;
}
