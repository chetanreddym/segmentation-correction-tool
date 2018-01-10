package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class NikonType1MakernoteDirectory
  extends Directory
{
  public static final int TAG_NIKON_TYPE1_UNKNOWN_1 = 2;
  public static final int TAG_NIKON_TYPE1_QUALITY = 3;
  public static final int TAG_NIKON_TYPE1_COLOR_MODE = 4;
  public static final int TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT = 5;
  public static final int TAG_NIKON_TYPE1_CCD_SENSITIVITY = 6;
  public static final int TAG_NIKON_TYPE1_WHITE_BALANCE = 7;
  public static final int TAG_NIKON_TYPE1_FOCUS = 8;
  public static final int TAG_NIKON_TYPE1_UNKNOWN_2 = 9;
  public static final int TAG_NIKON_TYPE1_DIGITAL_ZOOM = 10;
  public static final int TAG_NIKON_TYPE1_CONVERTER = 11;
  public static final int TAG_NIKON_TYPE1_UNKNOWN_3 = 3840;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public NikonType1MakernoteDirectory()
  {
    setDescriptor(new NikonType1MakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Nikon Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(6), "CCD Sensitivity");
    _tagNameMap.put(Integer.valueOf(4), "Color Mode");
    _tagNameMap.put(Integer.valueOf(10), "Digital Zoom");
    _tagNameMap.put(Integer.valueOf(11), "Fisheye Converter");
    _tagNameMap.put(Integer.valueOf(8), "Focus");
    _tagNameMap.put(Integer.valueOf(5), "Image Adjustment");
    _tagNameMap.put(Integer.valueOf(3), "Quality");
    _tagNameMap.put(Integer.valueOf(2), "Makernote Unknown 1");
    _tagNameMap.put(Integer.valueOf(9), "Makernote Unknown 2");
    _tagNameMap.put(Integer.valueOf(3840), "Makernote Unknown 3");
    _tagNameMap.put(Integer.valueOf(7), "White Balance");
  }
}
