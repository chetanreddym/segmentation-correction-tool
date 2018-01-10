package com.drew.metadata.exif;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.TagDescriptor;
import java.io.UnsupportedEncodingException;

public class PanasonicMakernoteDescriptor
  extends TagDescriptor<PanasonicMakernoteDirectory>
{
  public PanasonicMakernoteDescriptor(@NotNull PanasonicMakernoteDirectory paramPanasonicMakernoteDirectory)
  {
    super(paramPanasonicMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getQualityModeDescription();
    case 2: 
      return getVersionDescription();
    case 3: 
      return getWhiteBalanceDescription();
    case 7: 
      return getFocusModeDescription();
    case 15: 
      return getAfAreaModeDescription();
    case 26: 
      return getImageStabilizationDescription();
    case 28: 
      return getMacroModeDescription();
    case 31: 
      return getRecordModeDescription();
    case 32: 
      return getAudioDescription();
    case 33: 
      return getUnknownDataDumpDescription();
    case 40: 
      return getColorEffectDescription();
    case 41: 
      return getUptimeDescription();
    case 42: 
      return getBurstModeDescription();
    case 44: 
      return getContrastModeDescription();
    case 45: 
      return getNoiseReductionDescription();
    case 46: 
      return getSelfTimerDescription();
    case 48: 
      return getRotationDescription();
    case 49: 
      return getAfAssistLampDescription();
    case 50: 
      return getColorModeDescription();
    case 52: 
      return getOpticalZoomModeDescription();
    case 53: 
      return getConversionLensDescription();
    case 57: 
      return getContrastDescription();
    case 58: 
      return getWorldTimeLocationDescription();
    case 61: 
      return getAdvancedSceneModeDescription();
    case 78: 
      return getDetectedFacesDescription();
    case 89: 
      return getTransformDescription();
    case 32786: 
      return getTransform1Description();
    case 93: 
      return getIntelligentExposureDescription();
    case 98: 
      return getFlashWarningDescription();
    case 105: 
      return getCountryDescription();
    case 107: 
      return getStateDescription();
    case 109: 
      return getCityDescription();
    case 111: 
      return getLandmarkDescription();
    case 112: 
      return getIntelligentResolutionDescription();
    case 97: 
      return getRecognizedFacesDescription();
    case 3584: 
      return getPrintImageMatchingInfoDescription();
    case 32769: 
      return getSceneModeDescription();
    case 32775: 
      return getFlashFiredDescription();
    case 59: 
      return getTextStampDescription();
    case 62: 
      return getTextStamp1Description();
    case 32776: 
      return getTextStamp2Description();
    case 32777: 
      return getTextStamp3Description();
    case 32768: 
      return getMakernoteVersionDescription();
    case 38: 
      return getExifVersionDescription();
    case 37: 
      return getInternalSerialNumberDescription();
    case 101: 
      return getTitleDescription();
    case 102: 
      return getBabyNameDescription();
    case 103: 
      return getLocationDescription();
    case 51: 
      return getBabyAgeDescription();
    case 32784: 
      return getBabyAge1Description();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getPrintImageMatchingInfoDescription()
  {
    byte[] arrayOfByte = ((PanasonicMakernoteDirectory)_directory).getByteArray(3584);
    if (arrayOfByte == null) {
      return null;
    }
    return "(" + arrayOfByte.length + " bytes)";
  }
  
  @Nullable
  public String getTextStampDescription()
  {
    return getOnOffDescription(59);
  }
  
  @Nullable
  public String getTextStamp1Description()
  {
    return getOnOffDescription(62);
  }
  
  @Nullable
  public String getTextStamp2Description()
  {
    return getOnOffDescription(32776);
  }
  
  @Nullable
  public String getTextStamp3Description()
  {
    return getOnOffDescription(32777);
  }
  
  @Nullable
  public String getMacroModeDescription()
  {
    return getOnOffDescription(28);
  }
  
  @Nullable
  public String getFlashFiredDescription()
  {
    return getOnOffDescription(32775);
  }
  
  @Nullable
  public String getImageStabilizationDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(26);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "On, Mode 1";
    case 3: 
      return "Off";
    case 4: 
      return "On, Mode 2";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAudioDescription()
  {
    return getOnOffDescription(32);
  }
  
  @Nullable
  public String getTransformDescription()
  {
    return getTransformDescription(89);
  }
  
  @Nullable
  public String getTransform1Description()
  {
    return getTransformDescription(32786);
  }
  
  @Nullable
  private String getTransformDescription(int paramInt)
  {
    byte[] arrayOfByte = ((PanasonicMakernoteDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    ByteArrayReader localByteArrayReader = new ByteArrayReader(arrayOfByte);
    try
    {
      int i = localByteArrayReader.getUInt16(0);
      int j = localByteArrayReader.getUInt16(2);
      if ((i == -1) && (j == 1)) {
        return "Slim Low";
      }
      if ((i == -3) && (j == 2)) {
        return "Slim High";
      }
      if ((i == 0) && (j == 0)) {
        return "Off";
      }
      if ((i == 1) && (j == 1)) {
        return "Stretch Low";
      }
      if ((i == 3) && (j == 2)) {
        return "Stretch High";
      }
      return "Unknown (" + i + " " + j + ")";
    }
    catch (BufferBoundsException localBufferBoundsException) {}
    return null;
  }
  
  @Nullable
  public String getIntelligentExposureDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(93);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "Low";
    case 2: 
      return "Standard";
    case 3: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashWarningDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(98);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "No";
    case 1: 
      return "Yes (Flash required but disabled)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getCountryDescription()
  {
    return getTextDescription(105);
  }
  
  @Nullable
  public String getStateDescription()
  {
    return getTextDescription(107);
  }
  
  @Nullable
  public String getCityDescription()
  {
    return getTextDescription(109);
  }
  
  @Nullable
  public String getLandmarkDescription()
  {
    return getTextDescription(111);
  }
  
  @Nullable
  public String getTitleDescription()
  {
    return getTextDescription(101);
  }
  
  @Nullable
  public String getBabyNameDescription()
  {
    return getTextDescription(102);
  }
  
  @Nullable
  public String getLocationDescription()
  {
    return getTextDescription(103);
  }
  
  @Nullable
  public String getIntelligentResolutionDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(112);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 2: 
      return "Auto";
    case 3: 
      return "On";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(57);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getWorldTimeLocationDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(58);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Home";
    case 2: 
      return "Destination";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAdvancedSceneModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(61);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Normal";
    case 2: 
      return "Outdoor/Illuminations/Flower/HDR Art";
    case 3: 
      return "Indoor/Architecture/Objects/HDR B&W";
    case 4: 
      return "Creative";
    case 5: 
      return "Auto";
    case 7: 
      return "Expressive";
    case 8: 
      return "Retro";
    case 9: 
      return "Pure";
    case 10: 
      return "Elegant";
    case 12: 
      return "Monochrome";
    case 13: 
      return "Dynamic Art";
    case 14: 
      return "Silhouette";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getUnknownDataDumpDescription()
  {
    byte[] arrayOfByte = ((PanasonicMakernoteDirectory)_directory).getByteArray(33);
    if (arrayOfByte == null) {
      return null;
    }
    return "[" + arrayOfByte.length + " bytes]";
  }
  
  @Nullable
  public String getColorEffectDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(40);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Off";
    case 2: 
      return "Warm";
    case 3: 
      return "Cool";
    case 4: 
      return "Black & White";
    case 5: 
      return "Sepia";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getUptimeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(41);
    if (localInteger == null) {
      return null;
    }
    return localInteger.intValue() / 100.0F + " s";
  }
  
  @Nullable
  public String getBurstModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(42);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "On";
    case 2: 
      return "Infinite";
    case 4: 
      return "Unlimited";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(44);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Low";
    case 2: 
      return "High";
    case 6: 
      return "Medium Low";
    case 7: 
      return "Medium High";
    case 256: 
      return "Low";
    case 272: 
      return "Normal";
    case 288: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getNoiseReductionDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(45);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Standard (0)";
    case 1: 
      return "Low (-1)";
    case 2: 
      return "High (+1)";
    case 3: 
      return "Lowest (-2)";
    case 4: 
      return "Highest (+2)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSelfTimerDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(46);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Off";
    case 2: 
      return "10 s";
    case 3: 
      return "2 s";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getRotationDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(48);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Horizontal";
    case 3: 
      return "Rotate 180";
    case 6: 
      return "Rotate 90 CW";
    case 8: 
      return "Rotate 270 CW";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAfAssistLampDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(49);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Fired";
    case 2: 
      return "Enabled but not used";
    case 3: 
      return "Disabled but required";
    case 4: 
      return "Disabled and not required";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getColorModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(50);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Natural";
    case 2: 
      return "Vivid";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getOpticalZoomModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(52);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Standard";
    case 2: 
      return "Extended";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getConversionLensDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(53);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Off";
    case 2: 
      return "Wide";
    case 3: 
      return "Telephoto";
    case 4: 
      return "Macro";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getDetectedFacesDescription()
  {
    return buildFacesDescription(((PanasonicMakernoteDirectory)_directory).getDetectedFaces());
  }
  
  @Nullable
  public String getRecognizedFacesDescription()
  {
    return buildFacesDescription(((PanasonicMakernoteDirectory)_directory).getRecognizedFaces());
  }
  
  @Nullable
  private String buildFacesDescription(@Nullable Face[] paramArrayOfFace)
  {
    if (paramArrayOfFace == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfFace.length; i++) {
      localStringBuilder.append("Face ").append(i + 1).append(": ").append(paramArrayOfFace[i].toString()).append("\n");
    }
    if (localStringBuilder.length() > 0) {
      return localStringBuilder.substring(0, localStringBuilder.length() - 1);
    }
    return null;
  }
  
  @Nullable
  public String getRecordModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(31);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Normal";
    case 2: 
      return "Portrait";
    case 3: 
      return "Scenery";
    case 4: 
      return "Sports";
    case 5: 
      return "Night Portrait";
    case 6: 
      return "Program";
    case 7: 
      return "Aperture Priority";
    case 8: 
      return "Shutter Priority";
    case 9: 
      return "Macro";
    case 10: 
      return "Spot";
    case 11: 
      return "Manual";
    case 12: 
      return "Movie Preview";
    case 13: 
      return "Panning";
    case 14: 
      return "Simple";
    case 15: 
      return "Color Effects";
    case 16: 
      return "Self Portrait";
    case 17: 
      return "Economy";
    case 18: 
      return "Fireworks";
    case 19: 
      return "Party";
    case 20: 
      return "Snow";
    case 21: 
      return "Night Scenery";
    case 22: 
      return "Food";
    case 23: 
      return "Baby";
    case 24: 
      return "Soft Skin";
    case 25: 
      return "Candlelight";
    case 26: 
      return "Starry Night";
    case 27: 
      return "High Sensitivity";
    case 28: 
      return "Panorama Assist";
    case 29: 
      return "Underwater";
    case 30: 
      return "Beach";
    case 31: 
      return "Aerial Photo";
    case 32: 
      return "Sunset";
    case 33: 
      return "Pet";
    case 34: 
      return "Intelligent ISO";
    case 35: 
      return "Clipboard";
    case 36: 
      return "High Speed Continuous Shooting";
    case 37: 
      return "Intelligent Auto";
    case 39: 
      return "Multi-aspect";
    case 41: 
      return "Transform";
    case 42: 
      return "Flash Burst";
    case 43: 
      return "Pin Hole";
    case 44: 
      return "Film Grain";
    case 45: 
      return "My Color";
    case 46: 
      return "Photo Frame";
    case 51: 
      return "HDR";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSceneModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(32769);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Normal";
    case 2: 
      return "Portrait";
    case 3: 
      return "Scenery";
    case 4: 
      return "Sports";
    case 5: 
      return "Night Portrait";
    case 6: 
      return "Program";
    case 7: 
      return "Aperture Priority";
    case 8: 
      return "Shutter Priority";
    case 9: 
      return "Macro";
    case 10: 
      return "Spot";
    case 11: 
      return "Manual";
    case 12: 
      return "Movie Preview";
    case 13: 
      return "Panning";
    case 14: 
      return "Simple";
    case 15: 
      return "Color Effects";
    case 16: 
      return "Self Portrait";
    case 17: 
      return "Economy";
    case 18: 
      return "Fireworks";
    case 19: 
      return "Party";
    case 20: 
      return "Snow";
    case 21: 
      return "Night Scenery";
    case 22: 
      return "Food";
    case 23: 
      return "Baby";
    case 24: 
      return "Soft Skin";
    case 25: 
      return "Candlelight";
    case 26: 
      return "Starry Night";
    case 27: 
      return "High Sensitivity";
    case 28: 
      return "Panorama Assist";
    case 29: 
      return "Underwater";
    case 30: 
      return "Beach";
    case 31: 
      return "Aerial Photo";
    case 32: 
      return "Sunset";
    case 33: 
      return "Pet";
    case 34: 
      return "Intelligent ISO";
    case 35: 
      return "Clipboard";
    case 36: 
      return "High Speed Continuous Shooting";
    case 37: 
      return "Intelligent Auto";
    case 39: 
      return "Multi-aspect";
    case 41: 
      return "Transform";
    case 42: 
      return "Flash Burst";
    case 43: 
      return "Pin Hole";
    case 44: 
      return "Film Grain";
    case 45: 
      return "My Color";
    case 46: 
      return "Photo Frame";
    case 51: 
      return "HDR";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(7);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "Manual";
    case 4: 
      return "Auto, Focus Button";
    case 5: 
      return "Auto, Continuous";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAfAreaModeDescription()
  {
    int[] arrayOfInt = ((PanasonicMakernoteDirectory)_directory).getIntArray(15);
    if ((arrayOfInt == null) || (arrayOfInt.length < 2)) {
      return null;
    }
    switch (arrayOfInt[0])
    {
    case 0: 
      switch (arrayOfInt[1])
      {
      case 1: 
        return "Spot Mode On";
      case 16: 
        return "Spot Mode Off";
      }
      return "Unknown (" + arrayOfInt[0] + " " + arrayOfInt[1] + ")";
    case 1: 
      switch (arrayOfInt[1])
      {
      case 0: 
        return "Spot Focusing";
      case 1: 
        return "5-area";
      }
      return "Unknown (" + arrayOfInt[0] + " " + arrayOfInt[1] + ")";
    case 16: 
      switch (arrayOfInt[1])
      {
      case 0: 
        return "1-area";
      case 16: 
        return "1-area (high speed)";
      }
      return "Unknown (" + arrayOfInt[0] + " " + arrayOfInt[1] + ")";
    case 32: 
      switch (arrayOfInt[1])
      {
      case 0: 
        return "Auto or Face Detect";
      case 1: 
        return "3-area (left)";
      case 2: 
        return "3-area (center)";
      case 3: 
        return "3-area (right)";
      }
      return "Unknown (" + arrayOfInt[0] + " " + arrayOfInt[1] + ")";
    case 64: 
      return "Face Detect";
    }
    return "Unknown (" + arrayOfInt[0] + " " + arrayOfInt[1] + ")";
  }
  
  @Nullable
  public String getQualityModeDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(1);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "High";
    case 3: 
      return "Normal";
    case 6: 
      return "Very High";
    case 7: 
      return "Raw";
    case 9: 
      return "Motion Picture";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getVersionDescription()
  {
    return convertBytesToVersionString(((PanasonicMakernoteDirectory)_directory).getIntArray(2), 2);
  }
  
  @Nullable
  public String getMakernoteVersionDescription()
  {
    return convertBytesToVersionString(((PanasonicMakernoteDirectory)_directory).getIntArray(32768), 2);
  }
  
  @Nullable
  public String getExifVersionDescription()
  {
    return convertBytesToVersionString(((PanasonicMakernoteDirectory)_directory).getIntArray(38), 2);
  }
  
  @Nullable
  public String getInternalSerialNumberDescription()
  {
    byte[] arrayOfByte = ((PanasonicMakernoteDirectory)_directory).getByteArray(37);
    if (arrayOfByte == null) {
      return null;
    }
    int i = arrayOfByte.length;
    for (int j = 0; j < arrayOfByte.length; j++)
    {
      int k = arrayOfByte[j] & 0xFF;
      if ((k == 0) || (k > 127))
      {
        i = j;
        break;
      }
    }
    return new String(arrayOfByte, 0, i);
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "Daylight";
    case 3: 
      return "Cloudy";
    case 4: 
      return "Incandescent";
    case 5: 
      return "Manual";
    case 8: 
      return "Flash";
    case 10: 
      return "Black & White";
    case 11: 
      return "Manual";
    case 12: 
      return "Shade";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getBabyAgeDescription()
  {
    Age localAge = ((PanasonicMakernoteDirectory)_directory).getAge(51);
    if (localAge == null) {
      return null;
    }
    return localAge.toFriendlyString();
  }
  
  @Nullable
  public String getBabyAge1Description()
  {
    Age localAge = ((PanasonicMakernoteDirectory)_directory).getAge(32784);
    if (localAge == null) {
      return null;
    }
    return localAge.toFriendlyString();
  }
  
  @Nullable
  private String getTextDescription(int paramInt)
  {
    byte[] arrayOfByte = ((PanasonicMakernoteDirectory)_directory).getByteArray(paramInt);
    if (arrayOfByte == null) {
      return null;
    }
    try
    {
      return new String(arrayOfByte, "ASCII").trim();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return null;
  }
  
  @Nullable
  private String getOnOffDescription(int paramInt)
  {
    Integer localInteger = ((PanasonicMakernoteDirectory)_directory).getInteger(paramInt);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Off";
    case 2: 
      return "On";
    }
    return "Unknown (" + localInteger + ")";
  }
}
