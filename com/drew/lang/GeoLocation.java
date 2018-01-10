package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

public final class GeoLocation
{
  private final double _latitude;
  private final double _longitude;
  
  public GeoLocation(double paramDouble1, double paramDouble2)
  {
    _latitude = paramDouble1;
    _longitude = paramDouble2;
  }
  
  public double getLatitude()
  {
    return _latitude;
  }
  
  public double getLongitude()
  {
    return _longitude;
  }
  
  public boolean isZero()
  {
    return (_latitude == 0.0D) && (_longitude == 0.0D);
  }
  
  @NotNull
  public static String decimalToDegreesMinutesSecondsString(double paramDouble)
  {
    double[] arrayOfDouble = decimalToDegreesMinutesSeconds(paramDouble);
    return arrayOfDouble[0] + "° " + arrayOfDouble[1] + "' " + arrayOfDouble[2] + '"';
  }
  
  @NotNull
  public static double[] decimalToDegreesMinutesSeconds(double paramDouble)
  {
    int i = (int)paramDouble;
    double d1 = Math.abs(paramDouble % 1.0D * 60.0D);
    double d2 = d1 % 1.0D * 60.0D;
    return new double[] { i, (int)d1, d2 };
  }
  
  @Nullable
  public static Double degreesMinutesSecondsToDecimal(@NotNull Rational paramRational1, @NotNull Rational paramRational2, @NotNull Rational paramRational3, boolean paramBoolean)
  {
    double d = Math.abs(paramRational1.doubleValue()) + paramRational2.doubleValue() / 60.0D + paramRational3.doubleValue() / 3600.0D;
    if (Double.isNaN(d)) {
      return null;
    }
    if (paramBoolean) {
      d *= -1.0D;
    }
    return Double.valueOf(d);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    GeoLocation localGeoLocation = (GeoLocation)paramObject;
    if (Double.compare(_latitude, _latitude) != 0) {
      return false;
    }
    return Double.compare(_longitude, _longitude) == 0;
  }
  
  public int hashCode()
  {
    long l = _latitude != 0.0D ? Double.doubleToLongBits(_latitude) : 0L;
    int i = (int)(l ^ l >>> 32);
    l = _longitude != 0.0D ? Double.doubleToLongBits(_longitude) : 0L;
    i = 31 * i + (int)(l ^ l >>> 32);
    return i;
  }
  
  @NotNull
  public String toString()
  {
    return _latitude + ", " + _longitude;
  }
  
  @NotNull
  public String toDMSString()
  {
    return decimalToDegreesMinutesSecondsString(_latitude) + ", " + decimalToDegreesMinutesSecondsString(_longitude);
  }
}
