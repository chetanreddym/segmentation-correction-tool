package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class FujifilmMakernoteDescriptor
  extends TagDescriptor<FujifilmMakernoteDirectory>
{
  public FujifilmMakernoteDescriptor(@NotNull FujifilmMakernoteDirectory paramFujifilmMakernoteDirectory)
  {
    super(paramFujifilmMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 4097: 
      return getSharpnessDescription();
    case 4098: 
      return getWhiteBalanceDescription();
    case 4099: 
      return getColorDescription();
    case 4100: 
      return getToneDescription();
    case 4112: 
      return getFlashModeDescription();
    case 4113: 
      return getFlashStrengthDescription();
    case 4128: 
      return getMacroDescription();
    case 4129: 
      return getFocusModeDescription();
    case 4144: 
      return getSlowSyncDescription();
    case 4145: 
      return getPictureModeDescription();
    case 4352: 
      return getContinuousTakingOrAutoBrackettingDescription();
    case 4864: 
      return getBlurWarningDescription();
    case 4865: 
      return getFocusWarningDescription();
    case 4866: 
      return getAutoExposureWarningDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getSharpnessDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4097);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Softest";
    case 2: 
      return "Soft";
    case 3: 
      return "Normal";
    case 4: 
      return "Hard";
    case 5: 
      return "Hardest";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4098);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 256: 
      return "Daylight";
    case 512: 
      return "Cloudy";
    case 768: 
      return "DaylightColor-fluorescence";
    case 769: 
      return "DaywhiteColor-fluorescence";
    case 770: 
      return "White-fluorescence";
    case 1024: 
      return "Incandescence";
    case 3840: 
      return "Custom white balance";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getColorDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4099);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal (STD)";
    case 256: 
      return "High (HARD)";
    case 512: 
      return "Low (ORG)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getToneDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4100);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal (STD)";
    case 256: 
      return "High (HARD)";
    case 512: 
      return "Low (ORG)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashModeDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4112);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 1: 
      return "On";
    case 2: 
      return "Off";
    case 3: 
      return "Red-eye reduction";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashStrengthDescription()
  {
    Rational localRational = ((FujifilmMakernoteDirectory)_directory).getRational(4113);
    if (localRational == null) {
      return null;
    }
    return localRational.toSimpleString(false) + " EV (Apex)";
  }
  
  @Nullable
  public String getMacroDescription()
  {
    return getOnOffDescription(4128);
  }
  
  @Nullable
  public String getFocusModeDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4129);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto focus";
    case 1: 
      return "Manual focus";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSlowSyncDescription()
  {
    return getOnOffDescription(4144);
  }
  
  @Nullable
  public String getPictureModeDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4145);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 1: 
      return "Portrait scene";
    case 2: 
      return "Landscape scene";
    case 4: 
      return "Sports scene";
    case 5: 
      return "Night scene";
    case 6: 
      return "Program AE";
    case 256: 
      return "Aperture priority AE";
    case 512: 
      return "Shutter priority AE";
    case 768: 
      return "Manual exposure";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContinuousTakingOrAutoBrackettingDescription()
  {
    return getOnOffDescription(4352);
  }
  
  @Nullable
  public String getBlurWarningDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4864);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "No blur warning";
    case 1: 
      return "Blur warning";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusWarningDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4865);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto focus good";
    case 1: 
      return "Out of focus";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAutoExposureWarningDescription()
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(4866);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "AE good";
    case 1: 
      return "Over exposed (>1/1000s @ F11)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  private String getOnOffDescription(int paramInt)
  {
    Integer localInteger = ((FujifilmMakernoteDirectory)_directory).getInteger(paramInt);
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
}
