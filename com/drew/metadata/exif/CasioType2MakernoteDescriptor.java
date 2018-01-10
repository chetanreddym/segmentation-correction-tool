package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class CasioType2MakernoteDescriptor
  extends TagDescriptor<CasioType2MakernoteDirectory>
{
  public CasioType2MakernoteDescriptor(@NotNull CasioType2MakernoteDirectory paramCasioType2MakernoteDirectory)
  {
    super(paramCasioType2MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
      return getThumbnailDimensionsDescription();
    case 3: 
      return getThumbnailSizeDescription();
    case 4: 
      return getThumbnailOffsetDescription();
    case 8: 
      return getQualityModeDescription();
    case 9: 
      return getImageSizeDescription();
    case 13: 
      return getFocusMode1Description();
    case 20: 
      return getIsoSensitivityDescription();
    case 25: 
      return getWhiteBalance1Description();
    case 29: 
      return getFocalLengthDescription();
    case 31: 
      return getSaturationDescription();
    case 32: 
      return getContrastDescription();
    case 33: 
      return getSharpnessDescription();
    case 3584: 
      return getPrintImageMatchingInfoDescription();
    case 8192: 
      return getCasioPreviewThumbnailDescription();
    case 8209: 
      return getWhiteBalanceBiasDescription();
    case 8210: 
      return getWhiteBalance2Description();
    case 8226: 
      return getObjectDistanceDescription();
    case 8244: 
      return getFlashDistanceDescription();
    case 12288: 
      return getRecordModeDescription();
    case 12289: 
      return getSelfTimerDescription();
    case 12290: 
      return getQualityDescription();
    case 12291: 
      return getFocusMode2Description();
    case 12294: 
      return getTimeZoneDescription();
    case 12295: 
      return getBestShotModeDescription();
    case 12308: 
      return getCcdIsoSensitivityDescription();
    case 12309: 
      return getColourModeDescription();
    case 12310: 
      return getEnhancementDescription();
    case 12311: 
      return getFilterDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getFilterDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12311);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getEnhancementDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12310);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getColourModeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12309);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getCcdIsoSensitivityDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12308);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "On";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getBestShotModeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12295);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getTimeZoneDescription()
  {
    return ((CasioType2MakernoteDirectory)_directory).getString(12294);
  }
  
  @Nullable
  public String getFocusMode2Description()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12291);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Fixation";
    case 6: 
      return "Multi-Area Focus";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12290);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 3: 
      return "Fine";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSelfTimerDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12289);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Off";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getRecordModeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(12288);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "Normal";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashDistanceDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(8244);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getObjectDistanceDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(8226);
    if (localInteger == null) {
      return null;
    }
    return Integer.toString(localInteger.intValue()) + " mm";
  }
  
  @Nullable
  public String getWhiteBalance2Description()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(8210);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Manual";
    case 1: 
      return "Auto";
    case 4: 
      return "Flash";
    case 12: 
      return "Flash";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getWhiteBalanceBiasDescription()
  {
    return ((CasioType2MakernoteDirectory)_directory).getString(8209);
  }
  
  @Nullable
  public String getCasioPreviewThumbnailDescription()
  {
    byte[] arrayOfByte = ((CasioType2MakernoteDirectory)_directory).getByteArray(8192);
    if (arrayOfByte == null) {
      return null;
    }
    return "<" + arrayOfByte.length + " bytes of image data>";
  }
  
  @Nullable
  public String getPrintImageMatchingInfoDescription()
  {
    return ((CasioType2MakernoteDirectory)_directory).getString(3584);
  }
  
  @Nullable
  public String getSharpnessDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(33);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "-1";
    case 1: 
      return "Normal";
    case 2: 
      return "+1";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(32);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "-1";
    case 1: 
      return "Normal";
    case 2: 
      return "+1";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSaturationDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(31);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "-1";
    case 1: 
      return "Normal";
    case 2: 
      return "+1";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocalLengthDescription()
  {
    Double localDouble = ((CasioType2MakernoteDirectory)_directory).getDoubleObject(29);
    if (localDouble == null) {
      return null;
    }
    return Double.toString(localDouble.doubleValue() / 10.0D) + " mm";
  }
  
  @Nullable
  public String getWhiteBalance1Description()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(25);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 1: 
      return "Daylight";
    case 2: 
      return "Shade";
    case 3: 
      return "Tungsten";
    case 4: 
      return "Florescent";
    case 5: 
      return "Manual";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getIsoSensitivityDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(20);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 3: 
      return "50";
    case 4: 
      return "64";
    case 6: 
      return "100";
    case 9: 
      return "200";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusMode1Description()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(13);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Macro";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getImageSizeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(9);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "640 x 480 pixels";
    case 4: 
      return "1600 x 1200 pixels";
    case 5: 
      return "2048 x 1536 pixels";
    case 20: 
      return "2288 x 1712 pixels";
    case 21: 
      return "2592 x 1944 pixels";
    case 22: 
      return "2304 x 1728 pixels";
    case 36: 
      return "3008 x 2008 pixels";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityModeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(8);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Fine";
    case 2: 
      return "Super Fine";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getThumbnailOffsetDescription()
  {
    return ((CasioType2MakernoteDirectory)_directory).getString(4);
  }
  
  @Nullable
  public String getThumbnailSizeDescription()
  {
    Integer localInteger = ((CasioType2MakernoteDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    return Integer.toString(localInteger.intValue()) + " bytes";
  }
  
  @Nullable
  public String getThumbnailDimensionsDescription()
  {
    int[] arrayOfInt = ((CasioType2MakernoteDirectory)_directory).getIntArray(2);
    if ((arrayOfInt == null) || (arrayOfInt.length != 2)) {
      return ((CasioType2MakernoteDirectory)_directory).getString(2);
    }
    return arrayOfInt[0] + " x " + arrayOfInt[1] + " pixels";
  }
}
