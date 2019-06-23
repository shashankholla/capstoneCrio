package com.crio.qeats.exchanges;

import com.crio.qeats.dto.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartModifiedResponse {

  @NotNull
  private Cart cart;

  @NotNull
  private int cartResponseType;

}
