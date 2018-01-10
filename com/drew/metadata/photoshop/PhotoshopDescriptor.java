package com.drew.metadata.photoshop;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class PhotoshopDescriptor
  extends TagDescriptor<PhotoshopDirectory>
{
  public PhotoshopDescriptor(@NotNull PhotoshopDirectory paramPhotoshopDirectory)
  {
    super(paramPhotoshopDirectory);
  }
  
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1033: 
    case 1036: 
      return getThumbnailDescription(paramInt);
    case 1002: 
    case 1035: 
      return getSimpleString(paramInt);
    case 1028: 
      return getBinaryDataString(paramInt);
    case 1050: 
      return getSlicesDescription();
    case 1057: 
      return getVersionDescription();
    case 1034: 
      return getBooleanString(paramInt);
    case 1005: 
      return getResolutionInfoDescription();
    case 1037: 
    case 1044: 
    case 1049: 
    case 1054: 
      return get32BitNumberString(paramInt);
    case 1030: 
      return getJpegQualityString();
    case 1062: 
      return getPrintScaleDescription();
    case 1064: 
      return getPixelAspectRatioString();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getJpegQualityString()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1030);
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = localByteArrayReader.getUInt16(0);
      int j = localByteArrayReader.getUInt16(2);
      int k = localByteArrayReader.getUInt16(4);
      int m;
      if ((i <= 65535) && (i >= 65533)) {
        m = i - 65532;
      } else if (i <= 8) {
        m = i + 4;
      } else {
        m = i;
      }
      String str1;
      switch (i)
      {
      case 0: 
      case 65533: 
      case 65534: 
      case 65535: 
        str1 = "Low";
        break;
      case 1: 
      case 2: 
      case 3: 
        str1 = "Medium";
        break;
      case 4: 
      case 5: 
        str1 = "High";
        break;
      case 6: 
      case 7: 
      case 8: 
        str1 = "Maximum";
        break;
      default: 
        str1 = "Unknown";
      }
      String str2;
      switch (j)
      {
      case 0: 
        str2 = "Standard";
        break;
      case 1: 
        str2 = "Optimised";
        break;
      case 257: 
        str2 = "Progressive ";
        break;
      default: 
        str2 = String.format("Unknown 0x%04X", new Object[] { Integer.valueOf(j) });
      }
      String str3 = (k >= 1) && (k <= 3) ? String.format("%d", new Object[] { Integer.valueOf(k + 2) }) : String.format("Unknown 0x%04X", new Object[] { Integer.valueOf(k) });
      return String.format("%d (%s), %s format, %s scans", new Object[] { Integer.valueOf(m), str1, str2, str3 });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  public String getPixelAspectRatioString()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1064);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      double d = localByteArrayReader.getDouble64(4);
      return Double.toString(d);
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getPrintScaleDescription()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1062);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = localByteArrayReader.getInt32(0);
      float f1 = localByteArrayReader.getFloat32(2);
      float f2 = localByteArrayReader.getFloat32(6);
      float f3 = localByteArrayReader.getFloat32(10);
      switch (i)
      {
      case 0: 
        return "Centered, Scale " + f3;
      case 1: 
        return "Size to fit";
      case 2: 
        return String.format("User defined, X:%s Y:%s, Scale:%s", new Object[] { Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3) });
      }
      return String.format("Unknown %04X, X:%s Y:%s, Scale:%s", new Object[] { Integer.valueOf(i), Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3) });
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getResolutionInfoDescription()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1005);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      float f1 = localByteArrayReader.getS15Fixed16(0);
      float f2 = localByteArrayReader.getS15Fixed16(8);
      return f1 + "x" + f2 + " DPI";
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getVersionDescription()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1057);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = 0;
      int j = localByteArrayReader.getInt32(0);
      i += 4;
      i++;
      int k = localByteArrayReader.getInt32(5);
      i += 4;
      String str1 = localByteArrayReader.getString(9, k * 2, "UTF-16");
      i += k * 2;
      int m = localByteArrayReader.getInt32(i);
      i += 4;
      String str2 = localByteArrayReader.getString(i, m * 2, "UTF-16");
      i += m * 2;
      int n = localByteArrayReader.getInt32(i);
      return String.format("%d (%s, %s) %d", new Object[] { Integer.valueOf(j), str1, str2, Integer.valueOf(n) });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  public String getSlicesDescription()
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(1050);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = localByteArrayReader.getInt32(20);
      String str = localByteArrayReader.getString(24, i * 2, "UTF-16");
      int j = 24 + i * 2;
      int k = localByteArrayReader.getInt32(j);
      return String.format("%s (%d,%d,%d,%d) %d Slices", new Object[] { str, Integer.valueOf(localByteArrayReader.getInt32(4)), Integer.valueOf(localByteArrayReader.getInt32(8)), Integer.valueOf(localByteArrayReader.getInt32(12)), Integer.valueOf(localByteArrayReader.getInt32(16)), Integer.valueOf(k) });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  public String getThumbnailDescription(int paramInt)
  {
    try
    {
      byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(paramInt);
      if (arrayOfByte == null) {
        return null;
      }
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = localByteArrayReader.getInt32(0);
      int j = localByteArrayReader.getInt32(4);
      int k = localByteArrayReader.getInt32(8);
      int m = localByteArrayReader.getInt32(16);
      int n = localByteArrayReader.getInt32(20);
      int i1 = localByteArrayReader.getInt32(24);
      return String.format("%s, %dx%d, Decomp %d bytes, %d bpp, %d bytes", new Object[] { i == 1 ? "JpegRGB" : "RawRGB", Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(i1), Integer.valueOf(n) });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  private String getBooleanString(int paramInt)
  {
    byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    return arrayOfByte[0] == 0 ? "No" : "Yes";
  }
  
  @Nullable
  private String get32BitNumberString(int paramInt)
  {
    byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
    try
    {
      return String.format("%d", new Object[] { Integer.valueOf(localByteArrayReader.getInt32(0)) });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  private String getSimpleString(int paramInt)
  {
    byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    return new String(arrayOfByte);
  }
  
  @Nullable
  private String getBinaryDataString(int paramInt)
  {
    byte[] arrayOfByte = ((PhotoshopDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    return String.format("%d bytes binary data", new Object[] { Integer.valueOf(arrayOfByte.length) });
  }
}
