package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class NikonType1MakernoteDescriptor
  extends TagDescriptor<NikonType1MakernoteDirectory>
{
  public NikonType1MakernoteDescriptor(@NotNull NikonType1MakernoteDirectory paramNikonType1MakernoteDirectory)
  {
    super(paramNikonType1MakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
      return getQualityDescription();
    case 4: 
      return getColorModeDescription();
    case 5: 
      return getImageAdjustmentDescription();
    case 6: 
      return getCcdSensitivityDescription();
    case 7: 
      return getWhiteBalanceDescription();
    case 8: 
      return getFocusDescription();
    case 10: 
      return getDigitalZoomDescription();
    case 11: 
      return getConverterDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getConverterDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(11);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "None";
    case 1: 
      return "Fisheye converter";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getDigitalZoomDescription()
  {
    Rational localRational = ((NikonType1MakernoteDirectory)_directory).getRational(10);
    if (localRational == null) {
      return null;
    }
    if (localRational.getNumerator() == 0L) {
      return "No digital zoom";
    }
    return localRational.toSimpleString(true) + "x digital zoom";
  }
  
  @Nullable
  public String getFocusDescription()
  {
    Rational localRational = ((NikonType1MakernoteDirectory)_directory).getRational(8);
    if (localRational == null) {
      return null;
    }
    if ((localRational.getNumerator() == 1L) && (localRational.getDenominator() == 0L)) {
      return "Infinite";
    }
    return localRational.toSimpleString(true);
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(7);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 1: 
      return "Preset";
    case 2: 
      return "Daylight";
    case 3: 
      return "Incandescence";
    case 4: 
      return "Florescence";
    case 5: 
      return "Cloudy";
    case 6: 
      return "SpeedLight";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getCcdSensitivityDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(6);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "ISO80";
    case 2: 
      return "ISO160";
    case 4: 
      return "ISO320";
    case 5: 
      return "ISO100";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getImageAdjustmentDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(5);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 1: 
      return "Bright +";
    case 2: 
      return "Bright -";
    case 3: 
      return "Contrast +";
    case 4: 
      return "Contrast -";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getColorModeDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(4);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Color";
    case 2: 
      return "Monochrome";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityDescription()
  {
    Integer localInteger = ((NikonType1MakernoteDirectory)_directory).getInteger(3);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "VGA Basic";
    case 2: 
      return "VGA Normal";
    case 3: 
      return "VGA Fine";
    case 4: 
      return "SXGA Basic";
    case 5: 
      return "SXGA Normal";
    case 6: 
      return "SXGA Fine";
    }
    return "Unknown (" + localInteger + ")";
  }
}
