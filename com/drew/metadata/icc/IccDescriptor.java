package com.drew.metadata.icc;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.io.UnsupportedEncodingException;

public class IccDescriptor
  extends TagDescriptor<IccDirectory>
{
  private static final int ICC_TAG_TYPE_TEXT = 1952807028;
  private static final int ICC_TAG_TYPE_DESC = 1684370275;
  private static final int ICC_TAG_TYPE_SIG = 1936287520;
  private static final int ICC_TAG_TYPE_MEAS = 1835360627;
  private static final int ICC_TAG_TYPE_XYZ_ARRAY = 1482250784;
  private static final int ICC_TAG_TYPE_MLUC = 1835824483;
  private static final int ICC_TAG_TYPE_CURV = 1668641398;
  
  public IccDescriptor(@NotNull IccDirectory paramIccDirectory)
  {
    super(paramIccDirectory);
  }
  
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 8: 
      return getProfileVersionDescription();
    case 12: 
      return getProfileClassDescription();
    case 40: 
      return getPlatformDescription();
    case 64: 
      return getRenderingIntentDescription();
    }
    if ((paramInt > 538976288) && (paramInt < 2054847098)) {
      return getTagDataString(paramInt);
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  private String getTagDataString(int paramInt)
  {
    try
    {
      byte[] arrayOfByte = ((IccDirectory)_directory).getByteArray(paramInt);
      ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
      int i = localByteArrayReader.getInt32(0);
      float f3;
      float f5;
      int i1;
      int m;
      StringBuilder localStringBuilder2;
      switch (i)
      {
      case 1952807028: 
        try
        {
          return new String(arrayOfByte, 8, arrayOfByte.length - 8 - 1, "ASCII");
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException1)
        {
          return new String(arrayOfByte, 8, arrayOfByte.length - 8 - 1);
        }
      case 1684370275: 
        int j = localByteArrayReader.getInt32(8);
        return new String(arrayOfByte, 12, j - 1);
      case 1936287520: 
        return IccReader.getStringFromInt32(localByteArrayReader.getInt32(8));
      case 1835360627: 
        int k = localByteArrayReader.getInt32(8);
        float f1 = localByteArrayReader.getS15Fixed16(12);
        float f2 = localByteArrayReader.getS15Fixed16(16);
        f3 = localByteArrayReader.getS15Fixed16(20);
        int i2 = localByteArrayReader.getInt32(24);
        f5 = localByteArrayReader.getS15Fixed16(28);
        int i5 = localByteArrayReader.getInt32(32);
        String str3;
        switch (k)
        {
        case 0: 
          str3 = "Unknown";
          break;
        case 1: 
          str3 = "1931 2°";
          break;
        case 2: 
          str3 = "1964 10°";
          break;
        default: 
          str3 = String.format("Unknown %d", new Object[] { Integer.valueOf(k) });
        }
        String str4;
        switch (i2)
        {
        case 0: 
          str4 = "Unknown";
          break;
        case 1: 
          str4 = "0/45 or 45/0";
          break;
        case 2: 
          str4 = "0/d or d/0";
          break;
        default: 
          str4 = String.format("Unknown %d", new Object[] { Integer.valueOf(k) });
        }
        String str5;
        switch (i5)
        {
        case 0: 
          str5 = "unknown";
          break;
        case 1: 
          str5 = "D50";
          break;
        case 2: 
          str5 = "D65";
          break;
        case 3: 
          str5 = "D93";
          break;
        case 4: 
          str5 = "F2";
          break;
        case 5: 
          str5 = "D55";
          break;
        case 6: 
          str5 = "A";
          break;
        case 7: 
          str5 = "Equi-Power (E)";
          break;
        case 8: 
          str5 = "F8";
          break;
        default: 
          str5 = String.format("Unknown %d", new Object[] { Integer.valueOf(i5) });
        }
        return String.format("%s Observer, Backing (%s, %s, %s), Geometry %s, Flare %d%%, Illuminant %s", new Object[] { str3, Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3), str4, Integer.valueOf(Math.round(f5 * 100.0F)), str5 });
      case 1482250784: 
        StringBuilder localStringBuilder1 = new StringBuilder();
        int n = (arrayOfByte.length - 8) / 12;
        for (i1 = 0; i1 < n; i1++)
        {
          f3 = localByteArrayReader.getS15Fixed16(8 + i1 * 12);
          float f4 = localByteArrayReader.getS15Fixed16(8 + i1 * 12 + 4);
          f5 = localByteArrayReader.getS15Fixed16(8 + i1 * 12 + 8);
          if (i1 > 0) {
            localStringBuilder1.append(", ");
          }
          localStringBuilder1.append("(").append(f3).append(", ").append(f4).append(", ").append(f5).append(")");
        }
        return localStringBuilder1.toString();
      case 1835824483: 
        m = localByteArrayReader.getInt32(8);
        localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append(m);
        for (i1 = 0; i1 < m; i1++)
        {
          String str1 = IccReader.getStringFromInt32(localByteArrayReader.getInt32(16 + i1 * 12));
          int i3 = localByteArrayReader.getInt32(16 + i1 * 12 + 4);
          int i4 = localByteArrayReader.getInt32(16 + i1 * 12 + 8);
          String str2;
          try
          {
            str2 = new String(arrayOfByte, i4, i3, "UTF-16BE");
          }
          catch (UnsupportedEncodingException localUnsupportedEncodingException2)
          {
            str2 = new String(arrayOfByte, i4, i3);
          }
          localStringBuilder2.append(" ").append(str1).append("(").append(str2).append(")");
        }
        return localStringBuilder2.toString();
      case 1668641398: 
        m = localByteArrayReader.getInt32(8);
        localStringBuilder2 = new StringBuilder();
        for (i1 = 0; i1 < m; i1++)
        {
          if (i1 != 0) {
            localStringBuilder2.append(", ");
          }
          localStringBuilder2.append(formatDoubleAsString(localByteArrayReader.getUInt16(12 + i1 * 2) / 65535.0D, 7, false));
        }
        return localStringBuilder2.toString();
      }
      return String.format("%s(0x%08X): %d bytes", new Object[] { IccReader.getStringFromInt32(i), Integer.valueOf(i), Integer.valueOf(arrayOfByte.length) });
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @NotNull
  public static String formatDoubleAsString(double paramDouble, int paramInt, boolean paramBoolean)
  {
    if (paramInt < 1) {
      return "" + Math.round(paramDouble);
    }
    long l1 = Math.abs(paramDouble);
    long l2 = (int)Math.round((Math.abs(paramDouble) - l1) * Math.pow(10.0D, paramInt));
    long l3 = l2;
    String str = "";
    for (int j = paramInt; j > 0; j--)
    {
      int i = (byte)(int)Math.abs(l2 % 10L);
      l2 /= 10L;
      if ((str.length() > 0) || (paramBoolean) || (i != 0) || (j == 1)) {
        str = i + str;
      }
    }
    l1 += l2;
    j = (paramDouble < 0.0D) && ((l1 != 0L) || (l3 != 0L)) ? 1 : 0;
    return (j != 0 ? "-" : "") + l1 + "." + str;
  }
  
  @Nullable
  private String getRenderingIntentDescription()
  {
    Integer localInteger = ((IccDirectory)_directory).getInteger(64);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Perceptual";
    case 1: 
      return "Media-Relative Colorimetric";
    case 2: 
      return "Saturation";
    case 3: 
      return "ICC-Absolute Colorimetric";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  private String getPlatformDescription()
  {
    String str = ((IccDirectory)_directory).getString(40);
    if (str == null) {
      return null;
    }
    int i;
    try
    {
      i = getInt32FromString(str);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      return str;
    }
    switch (i)
    {
    case 1095782476: 
      return "Apple Computer, Inc.";
    case 1297303124: 
      return "Microsoft Corporation";
    case 1397180704: 
      return "Silicon Graphics, Inc.";
    case 1398099543: 
      return "Sun Microsystems, Inc.";
    case 1413959252: 
      return "Taligent, Inc.";
    }
    return String.format("Unknown (%s)", new Object[] { str });
  }
  
  @Nullable
  private String getProfileClassDescription()
  {
    String str = ((IccDirectory)_directory).getString(12);
    if (str == null) {
      return null;
    }
    int i;
    try
    {
      i = getInt32FromString(str);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      return str;
    }
    switch (i)
    {
    case 1935896178: 
      return "Input Device";
    case 1835955314: 
      return "Display Device";
    case 1886549106: 
      return "Output Device";
    case 1818848875: 
      return "DeviceLink";
    case 1936744803: 
      return "ColorSpace Conversion";
    case 1633842036: 
      return "Abstract";
    case 1852662636: 
      return "Named Color";
    }
    return String.format("Unknown (%s)", new Object[] { str });
  }
  
  @Nullable
  private String getProfileVersionDescription()
  {
    Integer localInteger = ((IccDirectory)_directory).getInteger(8);
    if (localInteger == null) {
      return null;
    }
    int i = (localInteger.intValue() & 0xFF000000) >> 24;
    int j = (localInteger.intValue() & 0xF00000) >> 20;
    int k = (localInteger.intValue() & 0xF0000) >> 16;
    return String.format("%d.%d.%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) });
  }
  
  private static int getInt32FromString(@NotNull String paramString)
    throws BufferBoundsException
  {
    byte[] arrayOfByte = paramString.getBytes();
    return new ByteArrayReader(arrayOfByte).getInt32(0);
  }
}
