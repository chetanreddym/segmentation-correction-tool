package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class SonyType1MakernoteDirectory
  extends Directory
{
  public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 3584;
  public static final int TAG_PREVIEW_IMAGE = 8193;
  public static final int TAG_COLOR_MODE_SETTING = 45088;
  public static final int TAG_COLOR_TEMPERATURE = 45089;
  public static final int TAG_SCENE_MODE = 45091;
  public static final int TAG_ZONE_MATCHING = 45092;
  public static final int TAG_DYNAMIC_RANGE_OPTIMISER = 45093;
  public static final int TAG_IMAGE_STABILISATION = 45094;
  public static final int TAG_LENS_ID = 45095;
  public static final int TAG_MINOLTA_MAKER_NOTE = 45096;
  public static final int TAG_COLOR_MODE = 45097;
  public static final int TAG_MACRO = 45120;
  public static final int TAG_EXPOSURE_MODE = 45121;
  public static final int TAG_QUALITY = 45127;
  public static final int TAG_ANTI_BLUR = 45131;
  public static final int TAG_LONG_EXPOSURE_NOISE_REDUCTION = 45134;
  public static final int TAG_NO_PRINT = 65535;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public SonyType1MakernoteDirectory()
  {
    setDescriptor(new SonyType1MakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Sony Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(3584), "Print Image Matching Info");
    _tagNameMap.put(Integer.valueOf(8193), "Preview Image");
    _tagNameMap.put(Integer.valueOf(45088), "Color Mode Setting");
    _tagNameMap.put(Integer.valueOf(45089), "Color Temperature");
    _tagNameMap.put(Integer.valueOf(45091), "Scene Mode");
    _tagNameMap.put(Integer.valueOf(45092), "Zone Matching");
    _tagNameMap.put(Integer.valueOf(45093), "Dynamic Range Optimizer");
    _tagNameMap.put(Integer.valueOf(45094), "Image Stabilisation");
    _tagNameMap.put(Integer.valueOf(45095), "Lens ID");
    _tagNameMap.put(Integer.valueOf(45096), "Minolta Maker Note");
    _tagNameMap.put(Integer.valueOf(45097), "Color Mode");
    _tagNameMap.put(Integer.valueOf(45120), "Macro");
    _tagNameMap.put(Integer.valueOf(45121), "Exposure Mode");
    _tagNameMap.put(Integer.valueOf(45127), "Quality");
    _tagNameMap.put(Integer.valueOf(45131), "Anti Blur");
    _tagNameMap.put(Integer.valueOf(45134), "Long Exposure Noise Reduction");
    _tagNameMap.put(Integer.valueOf(65535), "No Print");
  }
}
