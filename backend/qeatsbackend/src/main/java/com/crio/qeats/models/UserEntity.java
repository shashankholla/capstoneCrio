package com.crio.qeats.models;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  @Id
  private String id;

  @NotNull
  private String username;

  @NotNull
  private String password;

  @NotNull
  private String name;

  @NotNull
  private String restaurantId;
}
