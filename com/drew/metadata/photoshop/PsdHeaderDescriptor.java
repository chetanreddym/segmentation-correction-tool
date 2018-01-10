package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class PsdHeaderDescriptor
  extends TagDescriptor<PsdHeaderDirectory>
{
  public PsdHeaderDescriptor(@NotNull PsdHeaderDirectory paramPsdHeaderDirectory)
  {
    super(paramPsdHeaderDirectory);
  }
  
  public String getDescription(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return getChannelCountDescription();
    case 4: 
      return getBitsPerChannelDescription();
    case 5: 
      return getColorModeDescription();
    case 2: 
      return getImageHeightDescription();
    case 3: 
      return getImageWidthDescription();
    }
    return super.getDescription(paramInt);
  }
  
  @Nullable
  public String getChannelCountDescription()
  {
    try
    {
      Integer localInteger = Integer.valueOf(((PsdHeaderDirectory)_directory).getInt(1));
      if (localInteger == null) {
        return null;
      }
      return localInteger + " channel" + (localInteger.intValue() == 1 ? "" : "s");
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getBitsPerChannelDescription()
  {
    try
    {
      Integer localInteger = Integer.valueOf(((PsdHeaderDirectory)_directory).getInt(4));
      if (localInteger == null) {
        return null;
      }
      return localInteger + " bit" + (localInteger.intValue() == 1 ? "" : "s") + " per channel";
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getColorModeDescription()
  {
    try
    {
      Integer localInteger = Integer.valueOf(((PsdHeaderDirectory)_directory).getInt(5));
      if (localInteger == null) {
        return null;
      }
      switch (localInteger.intValue())
      {
      case 0: 
        return "Bitmap";
      case 1: 
        return "Grayscale";
      case 2: 
        return "Indexed";
      case 3: 
        return "RGB";
      case 4: 
        return "CMYK";
      case 7: 
        return "Multichannel";
      case 8: 
        return "Duotone";
      case 9: 
        return "Lab";
      }
      return "Unknown color mode (" + localInteger + ")";
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getImageHeightDescription()
  {
    try
    {
      Integer localInteger = Integer.valueOf(((PsdHeaderDirectory)_directory).getInt(2));
      if (localInteger == null) {
        return null;
      }
      return localInteger + " pixel" + (localInteger.intValue() == 1 ? "" : "s");
    }
    catch (Exception localException) {}
    return null;
  }
  
  @Nullable
  public String getImageWidthDescription()
  {
    try
    {
      Integer localInteger = Integer.valueOf(((PsdHeaderDirectory)_directory).getInt(3));
      if (localInteger == null) {
        return null;
      }
      return localInteger + " pixel" + (localInteger.intValue() == 1 ? "" : "s");
    }
    catch (Exception localException) {}
    return null;
  }
}
