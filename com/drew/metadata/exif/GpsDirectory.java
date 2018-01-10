package com.drew.metadata.exif;

import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class GpsDirectory
  extends Directory
{
  public static final int TAG_GPS_VERSION_ID = 0;
  public static final int TAG_GPS_LATITUDE_REF = 1;
  public static final int TAG_GPS_LATITUDE = 2;
  public static final int TAG_GPS_LONGITUDE_REF = 3;
  public static final int TAG_GPS_LONGITUDE = 4;
  public static final int TAG_GPS_ALTITUDE_REF = 5;
  public static final int TAG_GPS_ALTITUDE = 6;
  public static final int TAG_GPS_TIME_STAMP = 7;
  public static final int TAG_GPS_SATELLITES = 8;
  public static final int TAG_GPS_STATUS = 9;
  public static final int TAG_GPS_MEASURE_MODE = 10;
  public static final int TAG_GPS_DOP = 11;
  public static final int TAG_GPS_SPEED_REF = 12;
  public static final int TAG_GPS_SPEED = 13;
  public static final int TAG_GPS_TRACK_REF = 14;
  public static final int TAG_GPS_TRACK = 15;
  public static final int TAG_GPS_IMG_DIRECTION_REF = 16;
  public static final int TAG_GPS_IMG_DIRECTION = 17;
  public static final int TAG_GPS_MAP_DATUM = 18;
  public static final int TAG_GPS_DEST_LATITUDE_REF = 19;
  public static final int TAG_GPS_DEST_LATITUDE = 20;
  public static final int TAG_GPS_DEST_LONGITUDE_REF = 21;
  public static final int TAG_GPS_DEST_LONGITUDE = 22;
  public static final int TAG_GPS_DEST_BEARING_REF = 23;
  public static final int TAG_GPS_DEST_BEARING = 24;
  public static final int TAG_GPS_DEST_DISTANCE_REF = 25;
  public static final int TAG_GPS_DEST_DISTANCE = 26;
  public static final int TAG_GPS_PROCESSING_METHOD = 27;
  public static final int TAG_GPS_AREA_INFORMATION = 28;
  public static final int TAG_GPS_DATE_STAMP = 29;
  public static final int TAG_GPS_DIFFERENTIAL = 30;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public GpsDirectory()
  {
    setDescriptor(new GpsDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "GPS";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  @Nullable
  public GeoLocation getGeoLocation()
  {
    Rational[] arrayOfRational1 = getRationalArray(2);
    Rational[] arrayOfRational2 = getRationalArray(4);
    String str1 = getString(1);
    String str2 = getString(3);
    if ((arrayOfRational1 == null) || (arrayOfRational1.length != 3)) {
      return null;
    }
    if ((arrayOfRational2 == null) || (arrayOfRational2.length != 3)) {
      return null;
    }
    if ((str1 == null) || (str2 == null)) {
      return null;
    }
    Double localDouble1 = GeoLocation.degreesMinutesSecondsToDecimal(arrayOfRational1[0], arrayOfRational1[1], arrayOfRational1[2], str1.equalsIgnoreCase("S"));
    Double localDouble2 = GeoLocation.degreesMinutesSecondsToDecimal(arrayOfRational2[0], arrayOfRational2[1], arrayOfRational2[2], str2.equalsIgnoreCase("W"));
    if ((localDouble1 == null) || (localDouble2 == null)) {
      return null;
    }
    return new GeoLocation(localDouble1.doubleValue(), localDouble2.doubleValue());
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(0), "GPS Version ID");
    _tagNameMap.put(Integer.valueOf(1), "GPS Latitude Ref");
    _tagNameMap.put(Integer.valueOf(2), "GPS Latitude");
    _tagNameMap.put(Integer.valueOf(3), "GPS Longitude Ref");
    _tagNameMap.put(Integer.valueOf(4), "GPS Longitude");
    _tagNameMap.put(Integer.valueOf(5), "GPS Altitude Ref");
    _tagNameMap.put(Integer.valueOf(6), "GPS Altitude");
    _tagNameMap.put(Integer.valueOf(7), "GPS Time-Stamp");
    _tagNameMap.put(Integer.valueOf(8), "GPS Satellites");
    _tagNameMap.put(Integer.valueOf(9), "GPS Status");
    _tagNameMap.put(Integer.valueOf(10), "GPS Measure Mode");
    _tagNameMap.put(Integer.valueOf(11), "GPS DOP");
    _tagNameMap.put(Integer.valueOf(12), "GPS Speed Ref");
    _tagNameMap.put(Integer.valueOf(13), "GPS Speed");
    _tagNameMap.put(Integer.valueOf(14), "GPS Track Ref");
    _tagNameMap.put(Integer.valueOf(15), "GPS Track");
    _tagNameMap.put(Integer.valueOf(16), "GPS Img Direction Ref");
    _tagNameMap.put(Integer.valueOf(17), "GPS Img Direction");
    _tagNameMap.put(Integer.valueOf(18), "GPS Map Datum");
    _tagNameMap.put(Integer.valueOf(19), "GPS Dest Latitude Ref");
    _tagNameMap.put(Integer.valueOf(20), "GPS Dest Latitude");
    _tagNameMap.put(Integer.valueOf(21), "GPS Dest Longitude Ref");
    _tagNameMap.put(Integer.valueOf(22), "GPS Dest Longitude");
    _tagNameMap.put(Integer.valueOf(23), "GPS Dest Bearing Ref");
    _tagNameMap.put(Integer.valueOf(24), "GPS Dest Bearing");
    _tagNameMap.put(Integer.valueOf(25), "GPS Dest Distance Ref");
    _tagNameMap.put(Integer.valueOf(26), "GPS Dest Distance");
    _tagNameMap.put(Integer.valueOf(27), "GPS Processing Method");
    _tagNameMap.put(Integer.valueOf(28), "GPS Area Information");
    _tagNameMap.put(Integer.valueOf(29), "GPS Date Stamp");
    _tagNameMap.put(Integer.valueOf(30), "GPS Differential");
  }
}
