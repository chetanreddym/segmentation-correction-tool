package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class ExifThumbnailDescriptor
  extends TagDescriptor<ExifThumbnailDirectory>
{
  private final boolean _allowDecimalRepresentationOfRationals = true;
  
  public ExifThumbnailDescriptor(@NotNull ExifThumbnailDirectory paramExifThumbnailDirectory)
  {
    super(paramExifThumbnailDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 274: 
      return getOrientationDescription();
    case 296: 
      return getResolutionDescription();
    case 531: 
      return getYCbCrPositioningDescription();
    case 282: 
      return getXResolutionDescription();
    case 283: 
      return getYResolutionDescription();
    case 513: 
      return getThumbnailOffsetDescription();
    case 514: 
      return getThumbnailLengthDescription();
    case 256: 
      return getThumbnailImageWidthDescription();
    case 257: 
      return getThumbnailImageHeightDescription();
    case 258: 
      return getBitsPerSampleDescription();
    case 259: 
      return getCompressionDescription();
    case 262: 
      return getPhotometricInterpretationDescription();
    case 278: 
      return getRowsPerStripDescription();
    case 279: 
      return getStripByteCountsDescription();
    case 277: 
      return getSamplesPerPixelDescription();
    case 284: 
      return getPlanarConfigurationDescription();
    case 530: 
      return getYCbCrSubsamplingDescription();
    case 532: 
      return getReferenceBlackWhiteDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getReferenceBlackWhiteDescription()
  {
    int[] arrayOfInt = ((ExifThumbnailDirectory)_directory).getIntArray(532);
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
  public String getYCbCrSubsamplingDescription()
  {
    int[] arrayOfInt = ((ExifThumbnailDirectory)_directory).getIntArray(530);
    if ((arrayOfInt == null) || (arrayOfInt.length < 2)) {
      return null;
    }
    if ((arrayOfInt[0] == 2) && (arrayOfInt[1] == 1)) {
      return "YCbCr4:2:2";
    }
    if ((arrayOfInt[0] == 2) && (arrayOfInt[1] == 2)) {
      return "YCbCr4:2:0";
    }
    return "(Unknown)";
  }
  
  @Nullable
  public String getPlanarConfigurationDescription()
  {
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(284);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Chunky (contiguous for each subsampling pixel)";
    case 2: 
      return "Separate (Y-plane/Cb-plane/Cr-plane format)";
    }
    return "Unknown configuration";
  }
  
  @Nullable
  public String getSamplesPerPixelDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(277);
    return str + " samples/pixel";
  }
  
  @Nullable
  public String getRowsPerStripDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(278);
    return str + " rows/strip";
  }
  
  @Nullable
  public String getStripByteCountsDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(279);
    return str + " bytes";
  }
  
  @Nullable
  public String getPhotometricInterpretationDescription()
  {
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(262);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "WhiteIsZero";
    case 1: 
      return "BlackIsZero";
    case 2: 
      return "RGB";
    case 3: 
      return "RGB Palette";
    case 4: 
      return "Transparency Mask";
    case 5: 
      return "CMYK";
    case 6: 
      return "YCbCr";
    case 8: 
      return "CIELab";
    case 9: 
      return "ICCLab";
    case 10: 
      return "ITULab";
    case 32803: 
      return "Color Filter Array";
    case 32844: 
      return "Pixar LogL";
    case 32845: 
      return "Pixar LogLuv";
    case 32892: 
      return "Linear Raw";
    }
    return "Unknown colour space";
  }
  
  @Nullable
  public String getCompressionDescription()
  {
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(259);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Uncompressed";
    case 2: 
      return "CCITT 1D";
    case 3: 
      return "T4/Group 3 Fax";
    case 4: 
      return "T6/Group 4 Fax";
    case 5: 
      return "LZW";
    case 6: 
      return "JPEG (old-style)";
    case 7: 
      return "JPEG";
    case 8: 
      return "Adobe Deflate";
    case 9: 
      return "JBIG B&W";
    case 10: 
      return "JBIG Color";
    case 32766: 
      return "Next";
    case 32771: 
      return "CCIRLEW";
    case 32773: 
      return "PackBits";
    case 32809: 
      return "Thunderscan";
    case 32895: 
      return "IT8CTPAD";
    case 32896: 
      return "IT8LW";
    case 32897: 
      return "IT8MP";
    case 32898: 
      return "IT8BL";
    case 32908: 
      return "PixarFilm";
    case 32909: 
      return "PixarLog";
    case 32946: 
      return "Deflate";
    case 32947: 
      return "DCS";
    case 32661: 
      return "JBIG";
    case 32676: 
      return "SGILog";
    case 32677: 
      return "SGILog24";
    case 32712: 
      return "JPEG 2000";
    case 32713: 
      return "Nikon NEF Compressed";
    }
    return "Unknown compression";
  }
  
  @Nullable
  public String getBitsPerSampleDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(258);
    return str + " bits/component/pixel";
  }
  
  @Nullable
  public String getThumbnailImageWidthDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(256);
    return str + " pixels";
  }
  
  @Nullable
  public String getThumbnailImageHeightDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(257);
    return str + " pixels";
  }
  
  @Nullable
  public String getThumbnailLengthDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(514);
    return str + " bytes";
  }
  
  @Nullable
  public String getThumbnailOffsetDescription()
  {
    String str = ((ExifThumbnailDirectory)_directory).getString(513);
    return str + " bytes";
  }
  
  @Nullable
  public String getYResolutionDescription()
  {
    Rational localRational = ((ExifThumbnailDirectory)_directory).getRational(283);
    if (localRational == null) {
      return null;
    }
    String str = getResolutionDescription();
    return localRational.toSimpleString(true) + " dots per " + (str == null ? "unit" : str.toLowerCase());
  }
  
  @Nullable
  public String getXResolutionDescription()
  {
    Rational localRational = ((ExifThumbnailDirectory)_directory).getRational(282);
    if (localRational == null) {
      return null;
    }
    String str = getResolutionDescription();
    return localRational.toSimpleString(true) + " dots per " + (str == null ? "unit" : str.toLowerCase());
  }
  
  @Nullable
  public String getYCbCrPositioningDescription()
  {
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(531);
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
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(274);
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
    Integer localInteger = ((ExifThumbnailDirectory)_directory).getInteger(296);
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
}
