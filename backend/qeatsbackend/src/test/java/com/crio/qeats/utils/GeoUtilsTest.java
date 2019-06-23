/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GeoUtilsTest {

  @Test
  public void samePositionIsZeroMetersApart() {
    assertEquals(0, GeoUtils.findDistanceInKm(10.0, 20.0, 10.0, 20.0));
  }


  @Test
  public void test() {
    double distanceBetweenA2bHsrAndA2bBtm = GeoUtils
        .findDistanceInKm(12.9168585, 77.6072902, 12.9138172, 77.63517);

    assertEquals(3.04, distanceBetweenA2bHsrAndA2bBtm, 0.1);
  }

}