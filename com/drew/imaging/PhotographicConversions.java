package com.drew.imaging;

public final class PhotographicConversions
{
  public static final double ROOT_TWO = Math.sqrt(2.0D);
  
  private PhotographicConversions()
    throws Exception
  {
    throw new Exception("Not intended for instantiation.");
  }
  
  public static double apertureToFStop(double paramDouble)
  {
    return Math.pow(ROOT_TWO, paramDouble);
  }
  
  public static double shutterSpeedToExposureTime(double paramDouble)
  {
    return (float)(1.0D / Math.exp(paramDouble * Math.log(2.0D)));
  }
}
