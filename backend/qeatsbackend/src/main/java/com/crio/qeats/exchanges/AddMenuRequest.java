package com.crio.qeats.exchanges;

import com.crio.qeats.dto.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMenuRequest {

  @NonNull
  private Item item;

  @NonNull
  private String restaurantId;

}
