/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.actuate.endpoint.invoke.MissingParametersException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;

// TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement GetRestaurantsRequest.
// Complete the class such that it is able to deserialize the incoming query params from
// REST API clients.
// For instance, if a REST client calls API
// /qeats/v1/restaurants?latitude=28.4900591&longitude=77.536386&searchFor=tamil,
// this class should be able to deserialize lat/long and optional searchFor from that.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRestaurantsRequest {

  @NonNull
  private Double latitude;

  @NonNull
  private Double longitude;

  private String searchFor;

  public GetRestaurantsRequest(double v, double v1) {
    this.latitude = v;
    this.longitude = v1;
  }


  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(@RequestParam(value = "latitude", required = true) Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(@RequestParam(value = "longitude", required = true) Double longitude) {
    this.longitude = longitude;
  }

  public String getSearchFor() {
    return searchFor;
  }

  public void setSearchFor(@RequestParam(value = "searchFor", required = false) String searchFor) {
    this.searchFor = searchFor;
  }
}
