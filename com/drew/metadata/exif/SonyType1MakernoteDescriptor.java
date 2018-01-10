package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class SonyType1MakernoteDescriptor
  extends TagDescriptor<SonyType1MakernoteDirectory>
{
  public SonyType1MakernoteDescriptor(@NotNull SonyType1MakernoteDirectory paramSonyType1MakernoteDirectory)
  {
    super(paramSonyType1MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 45089: 
      return getColorTemperatureDescription();
    case 45091: 
      return getSceneModeDescription();
    case 45092: 
      return getZoneMatchingDescription();
    case 45093: 
      return getDynamicRangeOptimizerDescription();
    case 45094: 
      return getImageStabilizationDescription();
    case 45097: 
      return getColorModeDescription();
    case 45120: 
      return getMacroDescription();
    case 45121: 
      return getExposureModeDescription();
    case 45127: 
      return getQualityDescription();
    case 45131: 
      return getAntiBlurDescription();
    case 45134: 
      return getLongExposureNoiseReductionDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getColorTemperatureDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45089);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 0) {
      return "Auto";
    }
    int i = (localInteger.intValue() & 0xFF0000) >> 8 | (localInteger.intValue() & 0xFF000000) >> 24;
    return String.format("%d K", new Object[] { Integer.valueOf(i) });
  }
  
  @Nullable
  public String getSceneModeDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45091);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Standard";
    case 1: 
      return "Portrait";
    case 2: 
      return "Text";
    case 3: 
      return "Night Scene";
    case 4: 
      return "Sunset";
    case 5: 
      return "Sports";
    case 6: 
      return "Landscape";
    case 7: 
      return "Night Portrait";
    case 8: 
      return "Macro";
    case 9: 
      return "Super Macro";
    case 16: 
      return "Auto";
    case 17: 
      return "Night View/Portrait";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getZoneMatchingDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45092);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "ISO Setting Used";
    case 1: 
      return "High Key";
    case 2: 
      return "Low Key";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getDynamicRangeOptimizerDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45093);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "Standard";
    case 2: 
      return "Advanced Auto";
    case 8: 
      return "Advanced LV1";
    case 9: 
      return "Advanced LV2";
    case 10: 
      return "Advanced LV3";
    case 11: 
      return "Advanced LV4";
    case 12: 
      return "Advanced LV5";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getImageStabilizationDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45094);
    if (localInteger == null) {
      return null;
    }
    return localInteger.intValue() == 0 ? "Off" : "On";
  }
  
  @Nullable
  public String getColorModeDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45097);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Standard";
    case 1: 
      return "Vivid";
    case 2: 
      return "Portrait";
    case 3: 
      return "Landscape";
    case 4: 
      return "Sunset";
    case 5: 
      return "Night Portrait";
    case 6: 
      return "Black & White";
    case 7: 
      return "Adobe RGB";
    case 12: 
    case 100: 
      return "Neutral";
    case 101: 
      return "Clear";
    case 102: 
      return "Deep";
    case 103: 
      return "Light";
    case 104: 
      return "Night View";
    case 105: 
      return "Autumn Leaves";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getMacroDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45120);
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
      return "Magnifying Glass/Super Macro";
    case 65535: 
      return "N/A";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getExposureModeDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45121);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 5: 
      return "Landscape";
    case 6: 
      return "Program";
    case 7: 
      return "Aperture Priority";
    case 8: 
      return "Shutter Priority";
    case 9: 
      return "Night Scene";
    case 15: 
      return "Manual";
    case 65535: 
      return "N/A";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getQualityDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45127);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Fine";
    case 65535: 
      return "N/A";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getAntiBlurDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45131);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "On (Continuous)";
    case 2: 
      return "On (Shooting)";
    case 65535: 
      return "N/A";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
  
  @Nullable
  public String getLongExposureNoiseReductionDescription()
  {
    Integer localInteger = ((SonyType1MakernoteDirectory)_directory).getInteger(45134);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Off";
    case 1: 
      return "On";
    case 65535: 
      return "N/A";
    }
    return String.format("Unknown (%d)", new Object[] { localInteger });
  }
}
