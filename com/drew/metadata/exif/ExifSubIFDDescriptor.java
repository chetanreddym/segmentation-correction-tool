package com.drew.metadata.exif;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ExifSubIFDDescriptor
  extends TagDescriptor<ExifSubIFDDirectory>
{
  private final boolean _allowDecimalRepresentationOfRationals = true;
  @NotNull
  private static final DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");
  
  public ExifSubIFDDescriptor(@NotNull ExifSubIFDDirectory paramExifSubIFDDirectory)
  {
    super(paramExifSubIFDDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 254: 
      return getNewSubfileTypeDescription();
    case 255: 
      return getSubfileTypeDescription();
    case 263: 
      return getThresholdingDescription();
    case 266: 
      return getFillOrderDescription();
    case 33434: 
      return getExposureTimeDescription();
    case 37377: 
      return getShutterSpeedDescription();
    case 33437: 
      return getFNumberDescription();
    case 37122: 
      return getCompressedAverageBitsPerPixelDescription();
    case 37382: 
      return getSubjectDistanceDescription();
    case 37383: 
      return getMeteringModeDescription();
    case 37384: 
      return getWhiteBalanceDescription();
    case 37385: 
      return getFlashDescription();
    case 37386: 
      return getFocalLengthDescription();
    case 40961: 
      return getColorSpaceDescription();
    case 40962: 
      return getExifImageWidthDescription();
    case 40963: 
      return getExifImageHeightDescription();
    case 41488: 
      return getFocalPlaneResolutionUnitDescription();
    case 41486: 
      return getFocalPlaneXResolutionDescription();
    case 41487: 
      return getFocalPlaneYResolutionDescription();
    case 258: 
      return getBitsPerSampleDescription();
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
    case 34850: 
      return getExposureProgramDescription();
    case 37378: 
      return getApertureValueDescription();
    case 37381: 
      return getMaxApertureValueDescription();
    case 41495: 
      return getSensingMethodDescription();
    case 37380: 
      return getExposureBiasDescription();
    case 41728: 
      return getFileSourceDescription();
    case 41729: 
      return getSceneTypeDescription();
    case 37121: 
      return getComponentConfigurationDescription();
    case 36864: 
      return getExifVersionDescription();
    case 40960: 
      return getFlashPixVersionDescription();
    case 34855: 
      return getIsoEquivalentDescription();
    case 37510: 
      return getUserCommentDescription();
    case 41985: 
      return getCustomRenderedDescription();
    case 41986: 
      return getExposureModeDescription();
    case 41987: 
      return getWhiteBalanceModeDescription();
    case 41988: 
      return getDigitalZoomRatioDescription();
    case 41989: 
      return get35mmFilmEquivFocalLengthDescription();
    case 41990: 
      return getSceneCaptureTypeDescription();
    case 41991: 
      return getGainControlDescription();
    case 41992: 
      return getContrastDescription();
    case 41993: 
      return getSaturationDescription();
    case 41994: 
      return getSharpnessDescription();
    case 41996: 
      return getSubjectDistanceRangeDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getNewSubfileTypeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(254);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Full-resolution image";
    case 2: 
      return "Reduced-resolution image";
    case 3: 
      return "Single page of multi-page reduced-resolution image";
    case 4: 
      return "Transparency mask";
    case 5: 
      return "Transparency mask of reduced-resolution image";
    case 6: 
      return "Transparency mask of multi-page image";
    case 7: 
      return "Transparency mask of reduced-resolution multi-page image";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSubfileTypeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(255);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Full-resolution image";
    case 2: 
      return "Reduced-resolution image";
    case 3: 
      return "Single page of multi-page image";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getThresholdingDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(263);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "No dithering or halftoning";
    case 2: 
      return "Ordered dither or halftone";
    case 3: 
      return "Randomized dither";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFillOrderDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(266);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Normal";
    case 2: 
      return "Reversed";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSubjectDistanceRangeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41996);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Unknown";
    case 1: 
      return "Macro";
    case 2: 
      return "Close view";
    case 3: 
      return "Distant view";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSharpnessDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41994);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "None";
    case 1: 
      return "Low";
    case 2: 
      return "Hard";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSaturationDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41993);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "None";
    case 1: 
      return "Low saturation";
    case 2: 
      return "High saturation";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41992);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "None";
    case 1: 
      return "Soft";
    case 2: 
      return "Hard";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getGainControlDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41991);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "None";
    case 1: 
      return "Low gain up";
    case 2: 
      return "Low gain down";
    case 3: 
      return "High gain up";
    case 4: 
      return "High gain down";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSceneCaptureTypeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41990);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Standard";
    case 1: 
      return "Landscape";
    case 2: 
      return "Portrait";
    case 3: 
      return "Night scene";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String get35mmFilmEquivFocalLengthDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41989);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 0) {
      return "Unknown";
    }
    return SimpleDecimalFormatter.format(localInteger) + "mm";
  }
  
  @Nullable
  public String getDigitalZoomRatioDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(41988);
    if (localRational == null) {
      return null;
    }
    if (localRational.getNumerator() == 0L) {
      return "Digital zoom not used.";
    }
    return SimpleDecimalFormatter.format(localRational.doubleValue());
  }
  
  @Nullable
  public String getWhiteBalanceModeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41987);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto white balance";
    case 1: 
      return "Manual white balance";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getExposureModeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41986);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto exposure";
    case 1: 
      return "Manual exposure";
    case 2: 
      return "Auto bracket";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getCustomRenderedDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41985);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal process";
    case 1: 
      return "Custom process";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getUserCommentDescription()
  {
    byte[] arrayOfByte = ((ExifSubIFDDirectory)_directory).getByteArray(37510);
    if (arrayOfByte == null) {
      return null;
    }
    if (arrayOfByte.length == 0) {
      return "";
    }
    HashMap localHashMap = new HashMap();
    localHashMap.put("ASCII", System.getProperty("file.encoding"));
    localHashMap.put("UNICODE", "UTF-16LE");
    localHashMap.put("JIS", "Shift-JIS");
    try
    {
      if (arrayOfByte.length >= 10)
      {
        String str1 = new String(arrayOfByte, 0, 10);
        Iterator localIterator = localHashMap.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          String str2 = (String)localEntry.getKey();
          String str3 = (String)localEntry.getValue();
          if (str1.startsWith(str2))
          {
            for (int i = str2.length(); i < 10; i++)
            {
              int j = arrayOfByte[i];
              if ((j != 0) && (j != 32)) {
                return new String(arrayOfByte, i, arrayOfByte.length - i, str3).trim();
              }
            }
            return new String(arrayOfByte, 10, arrayOfByte.length - 10, str3).trim();
          }
        }
      }
      return new String(arrayOfByte, System.getProperty("file.encoding")).trim();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  @Nullable
  public String getIsoEquivalentDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(34855);
    if (localInteger == null) {
      return null;
    }
    return Integer.toString(localInteger.intValue());
  }
  
  @Nullable
  public String getExifVersionDescription()
  {
    int[] arrayOfInt = ((ExifSubIFDDirectory)_directory).getIntArray(36864);
    if (arrayOfInt == null) {
      return null;
    }
    return convertBytesToVersionString(arrayOfInt, 2);
  }
  
  @Nullable
  public String getFlashPixVersionDescription()
  {
    int[] arrayOfInt = ((ExifSubIFDDirectory)_directory).getIntArray(40960);
    if (arrayOfInt == null) {
      return null;
    }
    return convertBytesToVersionString(arrayOfInt, 2);
  }
  
  @Nullable
  public String getSceneTypeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41729);
    if (localInteger == null) {
      return null;
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFileSourceDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41728);
    if (localInteger == null) {
      return null;
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getExposureBiasDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(37380);
    if (localRational == null) {
      return null;
    }
    return localRational.toSimpleString(true) + " EV";
  }
  
  @Nullable
  public String getMaxApertureValueDescription()
  {
    Double localDouble = ((ExifSubIFDDirectory)_directory).getDoubleObject(37381);
    if (localDouble == null) {
      return null;
    }
    double d = PhotographicConversions.apertureToFStop(localDouble.doubleValue());
    return "F" + SimpleDecimalFormatter.format(d);
  }
  
  @Nullable
  public String getApertureValueDescription()
  {
    Double localDouble = ((ExifSubIFDDirectory)_directory).getDoubleObject(37378);
    if (localDouble == null) {
      return null;
    }
    double d = PhotographicConversions.apertureToFStop(localDouble.doubleValue());
    return "F" + SimpleDecimalFormatter.format(d);
  }
  
  @Nullable
  public String getExposureProgramDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(34850);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Manual control";
    case 2: 
      return "Program normal";
    case 3: 
      return "Aperture priority";
    case 4: 
      return "Shutter priority";
    case 5: 
      return "Program creative (slow program)";
    case 6: 
      return "Program action (high-speed program)";
    case 7: 
      return "Portrait mode";
    case 8: 
      return "Landscape mode";
    }
    return "Unknown program (" + localInteger + ")";
  }
  
  @Nullable
  public String getYCbCrSubsamplingDescription()
  {
    int[] arrayOfInt = ((ExifSubIFDDirectory)_directory).getIntArray(530);
    if (arrayOfInt == null) {
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
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(284);
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
    String str = ((ExifSubIFDDirectory)_directory).getString(277);
    return str + " samples/pixel";
  }
  
  @Nullable
  public String getRowsPerStripDescription()
  {
    String str = ((ExifSubIFDDirectory)_directory).getString(278);
    return str + " rows/strip";
  }
  
  @Nullable
  public String getStripByteCountsDescription()
  {
    String str = ((ExifSubIFDDirectory)_directory).getString(279);
    return str + " bytes";
  }
  
  @Nullable
  public String getPhotometricInterpretationDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(262);
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
  public String getBitsPerSampleDescription()
  {
    String str = ((ExifSubIFDDirectory)_directory).getString(258);
    return str + " bits/component/pixel";
  }
  
  @Nullable
  public String getFocalPlaneXResolutionDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(41486);
    if (localRational == null) {
      return null;
    }
    String str = getFocalPlaneResolutionUnitDescription();
    return localRational.getReciprocal().toSimpleString(true) + (str == null ? "" : new StringBuilder().append(" ").append(str.toLowerCase()).toString());
  }
  
  @Nullable
  public String getFocalPlaneYResolutionDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(41487);
    if (localRational == null) {
      return null;
    }
    String str = getFocalPlaneResolutionUnitDescription();
    return localRational.getReciprocal().toSimpleString(true) + (str == null ? "" : new StringBuilder().append(" ").append(str.toLowerCase()).toString());
  }
  
  @Nullable
  public String getFocalPlaneResolutionUnitDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41488);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "(No unit)";
    case 2: 
      return "Inches";
    case 3: 
      return "cm";
    }
    return "";
  }
  
  @Nullable
  public String getExifImageWidthDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(40962);
    if (localInteger == null) {
      return null;
    }
    return localInteger + " pixels";
  }
  
  @Nullable
  public String getExifImageHeightDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(40963);
    if (localInteger == null) {
      return null;
    }
    return localInteger + " pixels";
  }
  
  @Nullable
  public String getColorSpaceDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(40961);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 1) {
      return "sRGB";
    }
    if (localInteger.intValue() == 65535) {
      return "Undefined";
    }
    return "Unknown";
  }
  
  @Nullable
  public String getFocalLengthDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(37386);
    if (localRational == null) {
      return null;
    }
    DecimalFormat localDecimalFormat = new DecimalFormat("0.0##");
    return localDecimalFormat.format(localRational.doubleValue()) + " mm";
  }
  
  @Nullable
  public String getFlashDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(37385);
    if (localInteger == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if ((localInteger.intValue() & 0x1) != 0) {
      localStringBuilder.append("Flash fired");
    } else {
      localStringBuilder.append("Flash did not fire");
    }
    if ((localInteger.intValue() & 0x4) != 0) {
      if ((localInteger.intValue() & 0x2) != 0) {
        localStringBuilder.append(", return detected");
      } else {
        localStringBuilder.append(", return not detected");
      }
    }
    if ((localInteger.intValue() & 0x10) != 0) {
      localStringBuilder.append(", auto");
    }
    if ((localInteger.intValue() & 0x40) != 0) {
      localStringBuilder.append(", red-eye reduction");
    }
    return localStringBuilder.toString();
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(37384);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Unknown";
    case 1: 
      return "Daylight";
    case 2: 
      return "Florescent";
    case 3: 
      return "Tungsten";
    case 10: 
      return "Flash";
    case 17: 
      return "Standard light";
    case 18: 
      return "Standard light (B)";
    case 19: 
      return "Standard light (C)";
    case 20: 
      return "D55";
    case 21: 
      return "D65";
    case 22: 
      return "D75";
    case 255: 
      return "(Other)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getMeteringModeDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(37383);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Unknown";
    case 1: 
      return "Average";
    case 2: 
      return "Center weighted average";
    case 3: 
      return "Spot";
    case 4: 
      return "Multi-spot";
    case 5: 
      return "Multi-segment";
    case 6: 
      return "Partial";
    case 255: 
      return "(Other)";
    }
    return "";
  }
  
  @Nullable
  public String getSubjectDistanceDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(37382);
    if (localRational == null) {
      return null;
    }
    DecimalFormat localDecimalFormat = new DecimalFormat("0.0##");
    return localDecimalFormat.format(localRational.doubleValue()) + " metres";
  }
  
  @Nullable
  public String getCompressedAverageBitsPerPixelDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(37122);
    if (localRational == null) {
      return null;
    }
    String str = localRational.toSimpleString(true);
    if ((localRational.isInteger()) && (localRational.intValue() == 1)) {
      return str + " bit/pixel";
    }
    return str + " bits/pixel";
  }
  
  @Nullable
  public String getExposureTimeDescription()
  {
    String str = ((ExifSubIFDDirectory)_directory).getString(33434);
    return str + " sec";
  }
  
  @Nullable
  public String getShutterSpeedDescription()
  {
    Float localFloat = ((ExifSubIFDDirectory)_directory).getFloatObject(37377);
    if (localFloat == null) {
      return null;
    }
    if (localFloat.floatValue() <= 1.0F)
    {
      float f1 = (float)(1.0D / Math.exp(localFloat.floatValue() * Math.log(2.0D)));
      long l = Math.round(f1 * 10.0D);
      float f2 = (float)l / 10.0F;
      return f2 + " sec";
    }
    int i = (int)Math.exp(localFloat.floatValue() * Math.log(2.0D));
    return "1/" + i + " sec";
  }
  
  @Nullable
  public String getFNumberDescription()
  {
    Rational localRational = ((ExifSubIFDDirectory)_directory).getRational(33437);
    if (localRational == null) {
      return null;
    }
    return "F" + SimpleDecimalFormatter.format(localRational.doubleValue());
  }
  
  @Nullable
  public String getSensingMethodDescription()
  {
    Integer localInteger = ((ExifSubIFDDirectory)_directory).getInteger(41495);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "(Not defined)";
    case 2: 
      return "One-chip color area sensor";
    case 3: 
      return "Two-chip color area sensor";
    case 4: 
      return "Three-chip color area sensor";
    case 5: 
      return "Color sequential area sensor";
    case 7: 
      return "Trilinear sensor";
    case 8: 
      return "Color sequential linear sensor";
    }
    return "";
  }
  
  @Nullable
  public String getComponentConfigurationDescription()
  {
    int[] arrayOfInt = ((ExifSubIFDDirectory)_directory).getIntArray(37121);
    if (arrayOfInt == null) {
      return null;
    }
    String[] arrayOfString = { "", "Y", "Cb", "Cr", "R", "G", "B" };
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < Math.min(4, arrayOfInt.length); i++)
    {
      int j = arrayOfInt[i];
      if ((j > 0) && (j < arrayOfString.length)) {
        localStringBuilder.append(arrayOfString[j]);
      }
    }
    return localStringBuilder.toString();
  }
}
