package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class PentaxMakernoteDescriptor
  extends TagDescriptor<PentaxMakernoteDirectory>
{
  public PentaxMakernoteDescriptor(@NotNull PentaxMakernoteDirectory paramPentaxMakernoteDirectory)
  {
    super(paramPentaxMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getCaptureModeDescription();
    case 2: 
      return getQualityLevelDescription();
    case 3: 
      return getFocusModeDescription();
    case 4: 
      return getFlashModeDescription();
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
      return getIsoSpeedDescription();
    case 23: 
      return getColourDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getColourDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(23);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Normal";
    case 2: 
      return "Black & White";
    case 3: 
      return "Sepia";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getIsoSpeedDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(20);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 10: 
      return "ISO 100";
    case 16: 
      return "ISO 200";
    case 100: 
      return "ISO 100";
    case 200: 
      return "ISO 200";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSaturationDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(13);
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
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(12);
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
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(11);
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
    Float localFloat = ((PentaxMakernoteDirectory)_directory).getFloatObject(10);
    if (localFloat == null) {
      return null;
    }
    if (localFloat.floatValue() == 0.0F) {
      return "Off";
    }
    return Float.toString(localFloat.floatValue());
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(7);
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
      return "Fluorescent";
    case 5: 
      return "Manual";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashModeDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(4);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "Flash On";
    case 4: 
      return "Flash Off";
    case 6: 
      return "Red-eye Reduction";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusModeDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "Custom";
    case 3: 
      return "Auto";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityLevelDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(2);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Good";
    case 1: 
      return "Better";
    case 2: 
      return "Best";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getCaptureModeDescription()
  {
    Integer localInteger = ((PentaxMakernoteDirectory)_directory).getInteger(1);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Auto";
    case 2: 
      return "Night-scene";
    case 3: 
      return "Manual";
    case 4: 
      return "Multiple";
    }
    return "Unknown (" + localInteger + ")";
  }
}
