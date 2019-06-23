package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveMenuItemRequest {

  @NotNull
  String itemId;

  @NotNull
  String restaurantId;
}
