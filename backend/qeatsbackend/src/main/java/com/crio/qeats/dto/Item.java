/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

  @Id
  private String id;

  @NotNull
  String itemId;

  @NotNull
  String name;

  @NotNull
  String imageUrl;

  @NotNull
  List<String> attributes = new ArrayList<>();

  @NotNull
  int price;

}