package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class FujifilmMakernoteDirectory
  extends Directory
{
  public static final int TAG_FUJIFILM_MAKERNOTE_VERSION = 0;
  public static final int TAG_FUJIFILM_QUALITY = 4096;
  public static final int TAG_FUJIFILM_SHARPNESS = 4097;
  public static final int TAG_FUJIFILM_WHITE_BALANCE = 4098;
  public static final int TAG_FUJIFILM_COLOR_SATURATION = 4099;
  public static final int TAG_FUJIFILM_TONE = 4100;
  public static final int TAG_FUJIFILM_FLASH_MODE = 4112;
  public static final int TAG_FUJIFILM_FLASH_STRENGTH = 4113;
  public static final int TAG_FUJIFILM_MACRO = 4128;
  public static final int TAG_FUJIFILM_FOCUS_MODE = 4129;
  public static final int TAG_FUJIFILM_SLOW_SYNCH = 4144;
  public static final int TAG_FUJIFILM_PICTURE_MODE = 4145;
  public static final int TAG_FUJIFILM_UNKNOWN_1 = 4146;
  public static final int TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING = 4352;
  public static final int TAG_FUJIFILM_UNKNOWN_2 = 4608;
  public static final int TAG_FUJIFILM_BLUR_WARNING = 4864;
  public static final int TAG_FUJIFILM_FOCUS_WARNING = 4865;
  public static final int TAG_FUJIFILM_AE_WARNING = 4866;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public FujifilmMakernoteDirectory()
  {
    setDescriptor(new FujifilmMakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "FujiFilm Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(0), "Makernote Version");
    _tagNameMap.put(Integer.valueOf(4096), "Quality");
    _tagNameMap.put(Integer.valueOf(4097), "Sharpness");
    _tagNameMap.put(Integer.valueOf(4098), "White Balance");
    _tagNameMap.put(Integer.valueOf(4099), "Color Saturation");
    _tagNameMap.put(Integer.valueOf(4100), "Tone (Contrast)");
    _tagNameMap.put(Integer.valueOf(4112), "Flash Mode");
    _tagNameMap.put(Integer.valueOf(4113), "Flash Strength");
    _tagNameMap.put(Integer.valueOf(4128), "Macro");
    _tagNameMap.put(Integer.valueOf(4129), "Focus Mode");
    _tagNameMap.put(Integer.valueOf(4144), "Slow Synch");
    _tagNameMap.put(Integer.valueOf(4145), "Picture Mode");
    _tagNameMap.put(Integer.valueOf(4146), "Makernote Unknown 1");
    _tagNameMap.put(Integer.valueOf(4352), "Continuous Taking Or Auto Bracketting");
    _tagNameMap.put(Integer.valueOf(4608), "Makernote Unknown 2");
    _tagNameMap.put(Integer.valueOf(4864), "Blur Warning");
    _tagNameMap.put(Integer.valueOf(4865), "Focus Warning");
    _tagNameMap.put(Integer.valueOf(4866), "AE Warning");
  }
}
