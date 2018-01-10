package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class NikonType2MakernoteDescriptor
  extends TagDescriptor<NikonType2MakernoteDirectory>
{
  public NikonType2MakernoteDescriptor(@NotNull NikonType2MakernoteDirectory paramNikonType2MakernoteDirectory)
  {
    super(paramNikonType2MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 13: 
      return getProgramShiftDescription();
    case 14: 
      return getExposureDifferenceDescription();
    case 132: 
      return getLensDescription();
    case 146: 
      return getHueAdjustmentDescription();
    case 141: 
      return getColorModeDescription();
    case 18: 
      return getAutoFlashCompensationDescription();
    case 23: 
      return getFlashExposureCompensationDescription();
    case 24: 
      return getFlashBracketCompensationDescription();
    case 28: 
      return getExposureTuningDescription();
    case 139: 
      return getLensStopsDescription();
    case 30: 
      return getColorSpaceDescription();
    case 34: 
      return getActiveDLightingDescription();
    case 42: 
      return getVignetteControlDescription();
    case 2: 
      return getIsoSettingDescription();
    case 134: 
      return getDigitalZoomDescription();
    case 135: 
      return getFlashUsedDescription();
    case 136: 
      return getAutoFocusPositionDescription();
    case 1: 
      return getFirmwareVersionDescription();
    case 131: 
      return getLensTypeDescription();
    case 137: 
      return getShootingModeDescription();
    case 147: 
      return getNEFCompressionDescription();
    case 177: 
      return getHighISONoiseReductionDescription();
    case 182: 
      return getPowerUpTimeDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getPowerUpTimeDescription()
  {
    Long localLong = ((NikonType2MakernoteDirectory)_directory).getLongObject(182);
    if (localLong == null) {
      return null;
    }
    return new Date(localLong.longValue()).toString();
  }
  
  @Nullable
  public String getHighISONoiseReductionDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(177);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "Minimal";
    case 2: 
      return "Low";
    case 4: 
      return "Normal";
    case 6: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashUsedDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(135);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Flash Not Used";
    case 1: 
      return "Manual Flash";
    case 3: 
      return "Flash Not Ready";
    case 7: 
      return "External Flash";
    case 8: 
      return "Fired, Commander Mode";
    case 9: 
      return "Fired, TTL Mode";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getNEFCompressionDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(147);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Lossy (Type 1)";
    case 3: 
      return "Uncompressed";
    case 7: 
      return "Lossless";
    case 8: 
      return "Lossy (Type 2)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getShootingModeDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(137);
    if (localInteger == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    if ((localInteger.intValue() & 0x1) == 1) {
      localArrayList.add("Continuous");
    } else {
      localArrayList.add("Single Frame");
    }
    if ((localInteger.intValue() & 0x2) == 2) {
      localArrayList.add("Delay");
    }
    if ((localInteger.intValue() & 0x8) == 8) {
      localArrayList.add("PC Control");
    }
    if ((localInteger.intValue() & 0x10) == 16) {
      localArrayList.add("Exposure Bracketing");
    }
    if ((localInteger.intValue() & 0x20) == 32) {
      localArrayList.add("Auto ISO");
    }
    if ((localInteger.intValue() & 0x40) == 64) {
      localArrayList.add("White-Balance Bracketing");
    }
    if ((localInteger.intValue() & 0x80) == 128) {
      localArrayList.add("IR Control");
    }
    return StringUtil.join(localArrayList, ", ");
  }
  
  @Nullable
  public String getLensTypeDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(131);
    if (localInteger == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    if ((localInteger.intValue() & 0x1) == 1) {
      localArrayList.add("MF");
    } else {
      localArrayList.add("AF");
    }
    if ((localInteger.intValue() & 0x2) == 2) {
      localArrayList.add("D");
    }
    if ((localInteger.intValue() & 0x4) == 4) {
      localArrayList.add("G");
    }
    if ((localInteger.intValue() & 0x8) == 8) {
      localArrayList.add("VR");
    }
    return StringUtil.join(localArrayList, ", ");
  }
  
  @Nullable
  public String getColorSpaceDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(30);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "sRGB";
    case 2: 
      return "Adobe RGB";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getActiveDLightingDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(34);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "Light";
    case 3: 
      return "Normal";
    case 5: 
      return "High";
    case 7: 
      return "Extra High";
    case 65535: 
      return "Auto";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getVignetteControlDescription()
  {
    Integer localInteger = ((NikonType2MakernoteDirectory)_directory).getInteger(42);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "Low";
    case 3: 
      return "Normal";
    case 5: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAutoFocusPositionDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(136);
    if (arrayOfInt == null) {
      return null;
    }
    if ((arrayOfInt.length != 4) || (arrayOfInt[0] != 0) || (arrayOfInt[2] != 0) || (arrayOfInt[3] != 0)) {
      return "Unknown (" + ((NikonType2MakernoteDirectory)_directory).getString(136) + ")";
    }
    switch (arrayOfInt[1])
    {
    case 0: 
      return "Centre";
    case 1: 
      return "Top";
    case 2: 
      return "Bottom";
    case 3: 
      return "Left";
    case 4: 
      return "Right";
    }
    return "Unknown (" + arrayOfInt[1] + ")";
  }
  
  @Nullable
  public String getDigitalZoomDescription()
  {
    Rational localRational = ((NikonType2MakernoteDirectory)_directory).getRational(134);
    if (localRational == null) {
      return null;
    }
    return localRational.toSimpleString(true) + "x digital zoom";
  }
  
  @Nullable
  public String getProgramShiftDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(13);
    return getEVDescription(arrayOfInt);
  }
  
  @Nullable
  public String getExposureDifferenceDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(14);
    return getEVDescription(arrayOfInt);
  }
  
  @NotNull
  public String getAutoFlashCompensationDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(18);
    return getEVDescription(arrayOfInt);
  }
  
  @NotNull
  public String getFlashExposureCompensationDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(23);
    return getEVDescription(arrayOfInt);
  }
  
  @NotNull
  public String getFlashBracketCompensationDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(24);
    return getEVDescription(arrayOfInt);
  }
  
  @NotNull
  public String getExposureTuningDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(28);
    return getEVDescription(arrayOfInt);
  }
  
  @NotNull
  public String getLensStopsDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(139);
    return getEVDescription(arrayOfInt);
  }
  
  @Nullable
  private static String getEVDescription(@Nullable int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    if ((paramArrayOfInt.length < 3) || (paramArrayOfInt[2] == 0)) {
      return null;
    }
    DecimalFormat localDecimalFormat = new DecimalFormat("0.##");
    double d = paramArrayOfInt[0] * paramArrayOfInt[1] / paramArrayOfInt[2];
    return localDecimalFormat.format(d) + " EV";
  }
  
  @Nullable
  public String getIsoSettingDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(2);
    if (arrayOfInt == null) {
      return null;
    }
    if ((arrayOfInt[0] != 0) || (arrayOfInt[1] == 0)) {
      return "Unknown (" + ((NikonType2MakernoteDirectory)_directory).getString(2) + ")";
    }
    return "ISO " + arrayOfInt[1];
  }
  
  @Nullable
  public String getLensDescription()
  {
    Rational[] arrayOfRational = ((NikonType2MakernoteDirectory)_directory).getRationalArray(132);
    if (arrayOfRational == null) {
      return null;
    }
    if (arrayOfRational.length < 4) {
      return ((NikonType2MakernoteDirectory)_directory).getString(132);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(arrayOfRational[0].intValue());
    localStringBuilder.append('-');
    localStringBuilder.append(arrayOfRational[1].intValue());
    localStringBuilder.append("mm f/");
    localStringBuilder.append(arrayOfRational[2].floatValue());
    localStringBuilder.append('-');
    localStringBuilder.append(arrayOfRational[3].floatValue());
    return localStringBuilder.toString();
  }
  
  @Nullable
  public String getHueAdjustmentDescription()
  {
    String str = ((NikonType2MakernoteDirectory)_directory).getString(146);
    if (str == null) {
      return null;
    }
    return str + " degrees";
  }
  
  @Nullable
  public String getColorModeDescription()
  {
    String str = ((NikonType2MakernoteDirectory)_directory).getString(141);
    if (str == null) {
      return null;
    }
    if (str.startsWith("MODE1")) {
      return "Mode I (sRGB)";
    }
    return str;
  }
  
  @Nullable
  public String getFirmwareVersionDescription()
  {
    int[] arrayOfInt = ((NikonType2MakernoteDirectory)_directory).getIntArray(1);
    if (arrayOfInt == null) {
      return null;
    }
    return ExifSubIFDDescriptor.convertBytesToVersionString(arrayOfInt, 2);
  }
}
