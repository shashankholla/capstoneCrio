package com.crio.qeats.exchanges;

import com.crio.qeats.dto.Order;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrdersResponse {

  @NotNull
  List<Order> order;

  @NotNull
  String restaurantId;
}
