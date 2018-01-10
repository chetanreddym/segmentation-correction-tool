package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.MetadataException;

public class DataFormat
{
  @NotNull
  public static final DataFormat BYTE = new DataFormat("BYTE", 1);
  @NotNull
  public static final DataFormat STRING = new DataFormat("STRING", 2);
  @NotNull
  public static final DataFormat USHORT = new DataFormat("USHORT", 3);
  @NotNull
  public static final DataFormat ULONG = new DataFormat("ULONG", 4);
  @NotNull
  public static final DataFormat URATIONAL = new DataFormat("URATIONAL", 5);
  @NotNull
  public static final DataFormat SBYTE = new DataFormat("SBYTE", 6);
  @NotNull
  public static final DataFormat UNDEFINED = new DataFormat("UNDEFINED", 7);
  @NotNull
  public static final DataFormat SSHORT = new DataFormat("SSHORT", 8);
  @NotNull
  public static final DataFormat SLONG = new DataFormat("SLONG", 9);
  @NotNull
  public static final DataFormat SRATIONAL = new DataFormat("SRATIONAL", 10);
  @NotNull
  public static final DataFormat SINGLE = new DataFormat("SINGLE", 11);
  @NotNull
  public static final DataFormat DOUBLE = new DataFormat("DOUBLE", 12);
  @NotNull
  private final String _name;
  private final int _value;
  
  @NotNull
  public static DataFormat fromValue(int paramInt)
    throws MetadataException
  {
    switch (paramInt)
    {
    case 1: 
      return BYTE;
    case 2: 
      return STRING;
    case 3: 
      return USHORT;
    case 4: 
      return ULONG;
    case 5: 
      return URATIONAL;
    case 6: 
      return SBYTE;
    case 7: 
      return UNDEFINED;
    case 8: 
      return SSHORT;
    case 9: 
      return SLONG;
    case 10: 
      return SRATIONAL;
    case 11: 
      return SINGLE;
    case 12: 
      return DOUBLE;
    }
    throw new MetadataException("value '" + paramInt + "' does not represent a known data format.");
  }
  
  private DataFormat(@NotNull String paramString, int paramInt)
  {
    _name = paramString;
    _value = paramInt;
  }
  
  public int getValue()
  {
    return _value;
  }
  
  @NotNull
  public String toString()
  {
    return _name;
  }
}
