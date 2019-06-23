package com.crio.qeats.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.web.bind.annotation.RequestParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCartRequest {
  @NonNull
  private String userId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(@RequestParam(value = "userId") String userId) {
    this.userId = userId;
  }
}
