package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class OlympusMakernoteDescriptor
  extends TagDescriptor<OlympusMakernoteDirectory>
{
  public OlympusMakernoteDescriptor(@NotNull OlympusMakernoteDirectory paramOlympusMakernoteDirectory)
  {
    super(paramOlympusMakernoteDirectory);
  }
  
  @Nullable
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 512: 
      return getSpecialModeDescription();
    case 513: 
      return getJpegQualityDescription();
    case 514: 
      return getMacroModeDescription();
    case 516: 
      return getDigiZoomRatioDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getDigiZoomRatioDescription()
  {
    Integer localInteger = ((OlympusMakernoteDirectory)_directory).getInteger(516);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal";
    case 2: 
      return "Digital 2x Zoom";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getMacroModeDescription()
  {
    Integer localInteger = ((OlympusMakernoteDirectory)_directory).getInteger(514);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 0: 
      return "Normal (no macro)";
    case 1: 
      return "Macro";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getJpegQualityDescription()
  {
    Integer localInteger = ((OlympusMakernoteDirectory)_directory).getInteger(513);
    if (localInteger == null) {
      return null;
    }
    switch (localInteger.intValue())
    {
    case 1: 
      return "SQ";
    case 2: 
      return "HQ";
    case 3: 
      return "SHQ";
    }
    return "Unknown (" + localInteger + ")";
  }
  
  @Nullable
  public String getSpecialModeDescription()
  {
    int[] arrayOfInt = ((OlympusMakernoteDirectory)_directory).getIntArray(512);
    if (arrayOfInt == null) {
      return null;
    }
    if (arrayOfInt.length < 1) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    switch (arrayOfInt[0])
    {
    case 0: 
      localStringBuilder.append("Normal picture taking mode");
      break;
    case 1: 
      localStringBuilder.append("Unknown picture taking mode");
      break;
    case 2: 
      localStringBuilder.append("Fast picture taking mode");
      break;
    case 3: 
      localStringBuilder.append("Panorama picture taking mode");
      break;
    default: 
      localStringBuilder.append("Unknown picture taking mode");
    }
    if (arrayOfInt.length < 2) {
      return localStringBuilder.toString();
    }
    localStringBuilder.append(" - ");
    switch (arrayOfInt[1])
    {
    case 0: 
      localStringBuilder.append("Unknown sequence number");
      break;
    case 1: 
      localStringBuilder.append("1st in a sequence");
      break;
    case 2: 
      localStringBuilder.append("2nd in a sequence");
      break;
    case 3: 
      localStringBuilder.append("3rd in a sequence");
      break;
    default: 
      localStringBuilder.append(arrayOfInt[1]);
      localStringBuilder.append("th in a sequence");
    }
    if (arrayOfInt.length < 3) {
      return localStringBuilder.toString();
    }
    localStringBuilder.append(" - ");
    switch (arrayOfInt[2])
    {
    case 1: 
      localStringBuilder.append("Left to right panorama direction");
      break;
    case 2: 
      localStringBuilder.append("Right to left panorama direction");
      break;
    case 3: 
      localStringBuilder.append("Bottom to top panorama direction");
      break;
    case 4: 
      localStringBuilder.append("Top to bottom panorama direction");
    }
    return localStringBuilder.toString();
  }
}
