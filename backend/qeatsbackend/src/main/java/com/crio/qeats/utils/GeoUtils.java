/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoUtils {

  public static double findDistanceInKm(double srcLatitude, double srcLongitude,
      double dstLatitude, double dstLongitude) {
    return distance(srcLatitude, dstLatitude, srcLongitude, dstLongitude, 0, 0);
  }

  /**
   * THIS IS BORROWED CODE. Calculate distance between two points in latitude and longitude taking
   * into account height difference. If you are not interested in height difference pass 0.0. Uses
   * Haversine method as its base.
   *
   * <p>lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters el2 End altitude
   * in
   * meters
   *
   * @returns Distance in Kilo Meters
   */
  private static double distance(
      double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

    final int R = 6371; // Radius of the earth

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a =
        Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2)
            * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c;

    double height = el1 - el2;

    distance = Math.pow(distance, 2) + Math.pow(height, 2);

    return Math.sqrt(distance);
  }
}