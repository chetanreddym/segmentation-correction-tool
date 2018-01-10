package com.drew.metadata.exif;

import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.text.DecimalFormat;

public class GpsDescriptor
  extends TagDescriptor<GpsDirectory>
{
  public GpsDescriptor(@NotNull GpsDirectory paramGpsDirectory)
  {
    super(paramGpsDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return getGpsVersionIdDescription();
    case 6: 
      return getGpsAltitudeDescription();
    case 5: 
      return getGpsAltitudeRefDescription();
    case 9: 
      return getGpsStatusDescription();
    case 10: 
      return getGpsMeasureModeDescription();
    case 12: 
      return getGpsSpeedRefDescription();
    case 14: 
    case 16: 
    case 23: 
      return getGpsDirectionReferenceDescription(paramInt);
    case 15: 
    case 17: 
    case 24: 
      return getGpsDirectionDescription(paramInt);
    case 25: 
      return getGpsDestinationReferenceDescription();
    case 7: 
      return getGpsTimeStampDescription();
    case 4: 
      return getGpsLongitudeDescription();
    case 2: 
      return getGpsLatitudeDescription();
    case 30: 
      return getGpsDifferentialDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  private String getGpsVersionIdDescription()
  {
    return convertBytesToVersionString(((GpsDirectory)_directory).getIntArray(0), 1);
  }
  
  @Nullable
  public String getGpsLatitudeDescription()
  {
    GeoLocation localGeoLocation = ((GpsDirectory)_directory).getGeoLocation();
    if (localGeoLocation == null) {
      return null;
    }
    return GeoLocation.decimalToDegreesMinutesSecondsString(localGeoLocation.getLatitude());
  }
  
  @Nullable
  public String getGpsLongitudeDescription()
  {
    GeoLocation localGeoLocation = ((GpsDirectory)_directory).getGeoLocation();
    if (localGeoLocation == null) {
      return null;
    }
    return GeoLocation.decimalToDegreesMinutesSecondsString(localGeoLocation.getLongitude());
  }
  
  @Nullable
  public String getGpsTimeStampDescription()
  {
    int[] arrayOfInt = ((GpsDirectory)_directory).getIntArray(7);
    if (arrayOfInt == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(arrayOfInt[0]);
    localStringBuilder.append(":");
    localStringBuilder.append(arrayOfInt[1]);
    localStringBuilder.append(":");
    localStringBuilder.append(arrayOfInt[2]);
    localStringBuilder.append(" UTC");
    return localStringBuilder.toString();
  }
  
  @Nullable
  public String getGpsDestinationReferenceDescription()
  {
    String str1 = ((GpsDirectory)_directory).getString(25);
    if (str1 == null) {
      return null;
    }
    String str2 = str1.trim();
    if ("K".equalsIgnoreCase(str2)) {
      return "kilometers";
    }
    if ("M".equalsIgnoreCase(str2)) {
      return "miles";
    }
    if ("N".equalsIgnoreCase(str2)) {
      return "knots";
    }
    return "Unknown (" + str2 + ")";
  }
  
  @Nullable
  public String getGpsDirectionDescription(int paramInt)
  {
    Rational localRational = ((GpsDirectory)_directory).getRational(paramInt);
    String str = localRational != null ? new DecimalFormat("0.##").format(localRational.doubleValue()) : ((GpsDirectory)_directory).getString(paramInt);
    if ((str == null) || (str.trim().length() == 0)) {
      return null;
    }
    return str.trim() + " degrees";
  }
  
  @Nullable
  public String getGpsDirectionReferenceDescription(int paramInt)
  {
    String str1 = ((GpsDirectory)_directory).getString(paramInt);
    if (str1 == null) {
      return null;
    }
    String str2 = str1.trim();
    if ("T".equalsIgnoreCase(str2)) {
      return "True direction";
    }
    if ("M".equalsIgnoreCase(str2)) {
      return "Magnetic direction";
    }
    return "Unknown (" + str2 + ")";
  }
  
  @Nullable
  public String getGpsSpeedRefDescription()
  {
    String str1 = ((GpsDirectory)_directory).getString(12);
    if (str1 == null) {
      return null;
    }
    String str2 = str1.trim();
    if ("K".equalsIgnoreCase(str2)) {
      return "kph";
    }
    if ("M".equalsIgnoreCase(str2)) {
      return "mph";
    }
    if ("N".equalsIgnoreCase(str2)) {
      return "knots";
    }
    return "Unknown (" + str2 + ")";
  }
  
  @Nullable
  public String getGpsMeasureModeDescription()
  {
    String str1 = ((GpsDirectory)_directory).getString(10);
    if (str1 == null) {
      return null;
    }
    String str2 = str1.trim();
    if ("2".equalsIgnoreCase(str2)) {
      return "2-dimensional measurement";
    }
    if ("3".equalsIgnoreCase(str2)) {
      return "3-dimensional measurement";
    }
    return "Unknown (" + str2 + ")";
  }
  
  @Nullable
  public String getGpsStatusDescription()
  {
    String str1 = ((GpsDirectory)_directory).getString(9);
    if (str1 == null) {
      return null;
    }
    String str2 = str1.trim();
    if ("A".equalsIgnoreCase(str2)) {
      return "Active (Measurement in progress)";
    }
    if ("V".equalsIgnoreCase(str2)) {
      return "Void (Measurement Interoperability)";
    }
    return "Unknown (" + str2 + ")";
  }
  
  @Nullable
  public String getGpsAltitudeRefDescription()
  {
    Integer localInteger = ((GpsDirectory)_directory).getInteger(5);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 0) {
      return "Sea level";
    }
    if (localInteger.intValue() == 1) {
      return "Below sea level";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getGpsAltitudeDescription()
  {
    Rational localRational = ((GpsDirectory)_directory).getRational(6);
    if (localRational == null) {
      return null;
    }
    return localRational.intValue() + " metres";
  }
  
  @Nullable
  public String getGpsDifferentialDescription()
  {
    Integer localInteger = ((GpsDirectory)_directory).getInteger(30);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 0) {
      return "No Correction";
    }
    if (localInteger.intValue() == 1) {
      return "Differential Corrected";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getDegreesMinutesSecondsDescription()
  {
    GeoLocation localGeoLocation = ((GpsDirectory)_directory).getGeoLocation();
    if (localGeoLocation == null) {
      return null;
    }
    return localGeoLocation.toDMSString();
  }
}
