package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class CasioType1MakernoteDescriptor
  extends TagDescriptor<CasioType1MakernoteDirectory>
{
  public CasioType1MakernoteDescriptor(@NotNull CasioType1MakernoteDirectory paramCasioType1MakernoteDirectory)
  {
    super(paramCasioType1MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getRecordingModeDescription();
    case 2: 
      return getQualityDescription();
    case 3: 
      return getFocusingModeDescription();
    case 4: 
      return getFlashModeDescription();
    case 5: 
      return getFlashIntensityDescription();
    case 6: 
      return getObjectDistanceDescription();
    case 7: 
      return getWhiteBalanceDescription();
    case 10: 
      return getDigitalZoomDescription();
    case 11: 
      return getSharpnessDescription();
    case 12: 
      return getContrastDescription();
    case 13: 
      return getSaturationDescription();
    case 20: 
      return getCcdSensitivityDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getCcdSensitivityDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(20);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 64: 
      return "Normal";
    case 125: 
      return "+1.0";
    case 250: 
      return "+2.0";
    case 244: 
      return "+3.0";
    case 80: 
      return "Normal (ISO 80 equivalent)";
    case 100: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSaturationDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(13);
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
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(12);
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
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSharpnessDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(11);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Soft";
    case 2: 
      return "Hard";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getDigitalZoomDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(10);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 65536: 
      return "No digital zoom";
    case 65537: 
      return "2x digital zoom";
    case 131072: 
      return "2x digital zoom";
    case 262144: 
      return "4x digital zoom";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(7);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "Tungsten";
    case 3: 
      return "Daylight";
    case 4: 
      return "Florescent";
    case 5: 
      return "Shade";
    case 129: 
      return "Manual";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getObjectDistanceDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(6);
    if (localInteger == null) {
      return null;
    }
    return localInteger + " mm";
  }
  
  @Nullable
  public String getFlashIntensityDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(5);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 11: 
      return "Weak";
    case 13: 
      return "Normal";
    case 15: 
      return "Strong";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashModeDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(4);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "On";
    case 3: 
      return "Off";
    case 4: 
      return "Red eye reduction";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusingModeDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "Macro";
    case 3: 
      return "Auto focus";
    case 4: 
      return "Manual focus";
    case 5: 
      return "Infinity";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(2);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Economy";
    case 2: 
      return "Normal";
    case 3: 
      return "Fine";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getRecordingModeDescription()
  {
    Integer localInteger = ((CasioType1MakernoteDirectory)_directory).getInteger(1);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Single shutter";
    case 2: 
      return "Panorama";
    case 3: 
      return "Night scene";
    case 4: 
      return "Portrait";
    case 5: 
      return "Landscape";
    }
    return "Unknown (" + localInteger + ")";
  }
}
