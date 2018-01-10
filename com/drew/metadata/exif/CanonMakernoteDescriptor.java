package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class CanonMakernoteDescriptor
  extends TagDescriptor<CanonMakernoteDirectory>
{
  public CanonMakernoteDescriptor(@NotNull CanonMakernoteDirectory paramCanonMakernoteDirectory)
  {
    super(paramCanonMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 12: 
      return getSerialNumberDescription();
    case 49436: 
      return getFlashActivityDescription();
    case 49426: 
      return getFocusTypeDescription();
    case 49420: 
      return getDigitalZoomDescription();
    case 49411: 
      return getQualityDescription();
    case 49409: 
      return getMacroModeDescription();
    case 49410: 
      return getSelfTimerDelayDescription();
    case 49412: 
      return getFlashModeDescription();
    case 49413: 
      return getContinuousDriveModeDescription();
    case 49415: 
      return getFocusMode1Description();
    case 49418: 
      return getImageSizeDescription();
    case 49419: 
      return getEasyShootingModeDescription();
    case 49421: 
      return getContrastDescription();
    case 49422: 
      return getSaturationDescription();
    case 49423: 
      return getSharpnessDescription();
    case 49424: 
      return getIsoDescription();
    case 49425: 
      return getMeteringModeDescription();
    case 49427: 
      return getAfPointSelectedDescription();
    case 49428: 
      return getExposureModeDescription();
    case 49431: 
      return getLongFocalLengthDescription();
    case 49432: 
      return getShortFocalLengthDescription();
    case 49433: 
      return getFocalUnitsPerMillimetreDescription();
    case 49437: 
      return getFlashDetailsDescription();
    case 49440: 
      return getFocusMode2Description();
    case 49671: 
      return getWhiteBalanceDescription();
    case 49678: 
      return getAfPointUsedDescription();
    case 49679: 
      return getFlashBiasDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getSerialNumberDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(12);
    if (localInteger == null) {
      return null;
    }
    return String.format("%04X%05d", new Object[] { Integer.valueOf(localInteger.intValue() >> 8 & 0xFF), Integer.valueOf(localInteger.intValue() & 0xFF) });
  }
  
  @Nullable
  public String getFlashBiasDescription()
  {
    Integer localInteger1 = ((CanonMakernoteDirectory)_directory).getInteger(49679);
    if (localInteger1 == null) {
      return null;
    }
    int i = 0;
    Integer localInteger2;
    if (localInteger1.intValue() > 61440)
    {
      i = 1;
      localInteger1 = Integer.valueOf(65535 - localInteger1.intValue());
      localInteger2 = localInteger1;
      Integer localInteger3 = localInteger1 = Integer.valueOf(localInteger1.intValue() + 1);
    }
    return (i != 0 ? "-" : "") + Float.toString(localInteger1.intValue() / 32.0F) + " EV";
  }
  
  @Nullable
  public String getAfPointUsedDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49678);
    if (localInteger == null) {
      return null;
    }
    if ((localInteger.intValue() & 0x7) == 0) {
      return "Right";
    }
    if ((localInteger.intValue() & 0x7) == 1) {
      return "Centre";
    }
    if ((localInteger.intValue() & 0x7) == 2) {
      return "Left";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getWhiteBalanceDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49671);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Auto";
    case 1: 
      return "Sunny";
    case 2: 
      return "Cloudy";
    case 3: 
      return "Tungsten";
    case 4: 
      return "Florescent";
    case 5: 
      return "Flash";
    case 6: 
      return "Custom";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusMode2Description()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49440);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Single";
    case 1: 
      return "Continuous";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashDetailsDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49437);
    if (localInteger == null) {
      return null;
    }
    if ((localInteger.intValue() >> 14 & 0x1) > 0) {
      return "External E-TTL";
    }
    if ((localInteger.intValue() >> 13 & 0x1) > 0) {
      return "Internal flash";
    }
    if ((localInteger.intValue() >> 11 & 0x1) > 0) {
      return "FP sync used";
    }
    if ((localInteger.intValue() >> 4 & 0x1) > 0) {
      return "FP sync enabled";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocalUnitsPerMillimetreDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49433);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() != 0) {
      return Integer.toString(localInteger.intValue());
    }
    return "";
  }
  
  @Nullable
  public String getShortFocalLengthDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49432);
    if (localInteger == null) {
      return null;
    }
    String str = getFocalUnitsPerMillimetreDescription();
    return Integer.toString(localInteger.intValue()) + " " + str;
  }
  
  @Nullable
  public String getLongFocalLengthDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49431);
    if (localInteger == null) {
      return null;
    }
    String str = getFocalUnitsPerMillimetreDescription();
    return Integer.toString(localInteger.intValue()) + " " + str;
  }
  
  @Nullable
  public String getExposureModeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49428);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Easy shooting";
    case 1: 
      return "Program";
    case 2: 
      return "Tv-priority";
    case 3: 
      return "Av-priority";
    case 4: 
      return "Manual";
    case 5: 
      return "A-DEP";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getAfPointSelectedDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49427);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 12288: 
      return "None (MF)";
    case 12289: 
      return "Auto selected";
    case 12290: 
      return "Right";
    case 12291: 
      return "Centre";
    case 12292: 
      return "Left";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getMeteringModeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49425);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 3: 
      return "Evaluative";
    case 4: 
      return "Partial";
    case 5: 
      return "Centre weighted";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getIsoDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49424);
    if (localInteger == null) {
      return null;
    }
    int i = 16384;
    if ((localInteger.intValue() & i) > 0) {
      return "" + (localInteger.intValue() & (i ^ 0xFFFFFFFF));
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Not specified (see ISOSpeedRatings tag)";
    case 15: 
      return "Auto";
    case 16: 
      return "50";
    case 17: 
      return "100";
    case 18: 
      return "200";
    case 19: 
      return "400";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSharpnessDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49423);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 65535: 
      return "Low";
    case 0: 
      return "Normal";
    case 1: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSaturationDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49422);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 65535: 
      return "Low";
    case 0: 
      return "Normal";
    case 1: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContrastDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49421);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 65535: 
      return "Low";
    case 0: 
      return "Normal";
    case 1: 
      return "High";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getEasyShootingModeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49419);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Full auto";
    case 1: 
      return "Manual";
    case 2: 
      return "Landscape";
    case 3: 
      return "Fast shutter";
    case 4: 
      return "Slow shutter";
    case 5: 
      return "Night";
    case 6: 
      return "B&W";
    case 7: 
      return "Sepia";
    case 8: 
      return "Portrait";
    case 9: 
      return "Sports";
    case 10: 
      return "Macro / Closeup";
    case 11: 
      return "Pan focus";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getImageSizeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49418);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Large";
    case 1: 
      return "Medium";
    case 2: 
      return "Small";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusMode1Description()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49415);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "One-shot";
    case 1: 
      return "AI Servo";
    case 2: 
      return "AI Focus";
    case 3: 
      return "Manual Focus";
    case 4: 
      return "Single";
    case 5: 
      return "Continuous";
    case 6: 
      return "Manual Focus";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getContinuousDriveModeDescription()
  {
    Integer localInteger1 = ((CanonMakernoteDirectory)_directory).getInteger(49413);
    if (localInteger1 == null) {
      return null;
    }
    switch (localInteger1.intValue())
    {
    case 0: 
      Integer localInteger2 = ((CanonMakernoteDirectory)_directory).getInteger(49410);
      if (localInteger2 != null) {
        return localInteger2.intValue() == 0 ? "Single shot" : "Single shot with self-timer";
      }
    case 1: 
      return "Continuous";
    }
    return "Unknown (" + localInteger1 + ")";
  }
  
  @Nullable
  public String getFlashModeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49412);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "No flash fired";
    case 1: 
      return "Auto";
    case 2: 
      return "On";
    case 3: 
      return "Red-eye reduction";
    case 4: 
      return "Slow-synchro";
    case 5: 
      return "Auto and red-eye reduction";
    case 6: 
      return "On and red-eye reduction";
    case 16: 
      return "External flash";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSelfTimerDelayDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49410);
    if (localInteger == null) {
      return null;
    }
    if (localInteger.intValue() == 0) {
      return "Self timer not used";
    }
    return Double.toString(localInteger.intValue() * 0.1D) + " sec";
  }
  
  @Nullable
  public String getMacroModeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49409);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "Macro";
    case 2: 
      return "Normal";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getQualityDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49411);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 2: 
      return "Normal";
    case 3: 
      return "Fine";
    case 5: 
      return "Superfine";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getDigitalZoomDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49420);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "No digital zoom";
    case 1: 
      return "2x";
    case 2: 
      return "4x";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFocusTypeDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49426);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Manual";
    case 1: 
      return "Auto";
    case 3: 
      return "Close-up (Macro)";
    case 8: 
      return "Locked (Pan Mode)";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getFlashActivityDescription()
  {
    Integer localInteger = ((CanonMakernoteDirectory)_directory).getInteger(49436);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Flash did not fire";
    case 1: 
      return "Flash fired";
    }
    return "Unknown (" + localInteger + ")";
  }
}
