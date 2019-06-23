package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties({"status"})
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

  private String id;


  private String restaurantId;


  private String userId;


  private List<Item> items = new ArrayList();


  private int total;

}