package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.io.UnsupportedEncodingException;

public class ExifIFD0Descriptor
  extends TagDescriptor<ExifIFD0Directory>
{
  private final boolean _allowDecimalRepresentationOfRationals = true;
  
  public ExifIFD0Descriptor(@NotNull ExifIFD0Directory paramExifIFD0Directory)
  {
    super(paramExifIFD0Directory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 296: 
      return getResolutionDescription();
    case 531: 
      return getYCbCrPositioningDescription();
    case 282: 
      return getXResolutionDescription();
    case 283: 
      return getYResolutionDescription();
    case 532: 
      return getReferenceBlackWhiteDescription();
    case 274: 
      return getOrientationDescription();
    case 40093: 
      return getWindowsAuthorDescription();
    case 40092: 
      return getWindowsCommentDescription();
    case 40094: 
      return getWindowsKeywordsDescription();
    case 40095: 
      return getWindowsSubjectDescription();
    case 40091: 
      return getWindowsTitleDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getReferenceBlackWhiteDescription()
  {
    int[] arrayOfInt = ((ExifIFD0Directory)_directory).getIntArray(532);
    if (arrayOfInt == null) {
      return null;
    }
    int i = arrayOfInt[0];
    int j = arrayOfInt[1];
    int k = arrayOfInt[2];
    int m = arrayOfInt[3];
    int n = arrayOfInt[4];
    int i1 = arrayOfInt[5];
    return "[" + i + "," + k + "," + n + "] " + "[" + j + "," + m + "," + i1 + "]";
  }
  
  @Nullable
  public String getYResolutionDescription()
  {
    Rational localRational = ((ExifIFD0Directory)_directory).getRational(283);
    if (localRational == null) {
      return null;
    }
    String str = getResolutionDescription();
    return localRational.toSimpleString(true) + " dots per " + (str == null ? "unit" : str.toLowerCase());
  }
  
  @Nullable
  public String getXResolutionDescription()
  {
    Rational localRational = ((ExifIFD0Directory)_directory).getRational(282);
    if (localRational == null) {
      return null;
    }
    String str = getResolutionDescription();
    return localRational.toSimpleString(true) + " dots per " + (str == null ? "unit" : str.toLowerCase());
  }
  
  @Nullable
  public String getYCbCrPositioningDescription()
  {
    Integer localInteger = ((ExifIFD0Directory)_directory).getInteger(531);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Center of pixel array";
    case 2: 
      return "Datum point";
    }
    return String.valueOf(localInteger);
  }
  
  @Nullable
  public String getOrientationDescription()
  {
    Integer localInteger = ((ExifIFD0Directory)_directory).getInteger(274);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Top, left side (Horizontal / normal)";
    case 2: 
      return "Top, right side (Mirror horizontal)";
    case 3: 
      return "Bottom, right side (Rotate 180)";
    case 4: 
      return "Bottom, left side (Mirror vertical)";
    case 5: 
      return "Left side, top (Mirror horizontal and rotate 270 CW)";
    case 6: 
      return "Right side, top (Rotate 90 CW)";
    case 7: 
      return "Right side, bottom (Mirror horizontal and rotate 90 CW)";
    case 8: 
      return "Left side, bottom (Rotate 270 CW)";
    }
    return String.valueOf(localInteger);
  }
  
  @Nullable
  public String getResolutionDescription()
  {
    Integer localInteger = ((ExifIFD0Directory)_directory).getInteger(296);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "(No unit)";
    case 2: 
      return "Inch";
    case 3: 
      return "cm";
    }
    return "";
  }
  
  @Nullable
  private String getUnicodeDescription(int paramInt)
  {
    byte[] arrayOfByte = ((ExifIFD0Directory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    try
    {
      return new String(arrayOfByte, "UTF-16LE").trim();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  @Nullable
  public String getWindowsAuthorDescription()
  {
    return getUnicodeDescription(40093);
  }
  
  @Nullable
  public String getWindowsCommentDescription()
  {
    return getUnicodeDescription(40092);
  }
  
  @Nullable
  public String getWindowsKeywordsDescription()
  {
    return getUnicodeDescription(40094);
  }
  
  @Nullable
  public String getWindowsTitleDescription()
  {
    return getUnicodeDescription(40091);
  }
  
  @Nullable
  public String getWindowsSubjectDescription()
  {
    return getUnicodeDescription(40095);
  }
}
