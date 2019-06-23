package com.crio.qeats.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  private String id;

  @NotNull
  String username;

  @NotNull
  String password;

  @NotNull
  String restaurantId;

  @NotNull
  String name;
}
