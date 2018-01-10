package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class CasioType1MakernoteDirectory
  extends Directory
{
  public static final int TAG_CASIO_RECORDING_MODE = 1;
  public static final int TAG_CASIO_QUALITY = 2;
  public static final int TAG_CASIO_FOCUSING_MODE = 3;
  public static final int TAG_CASIO_FLASH_MODE = 4;
  public static final int TAG_CASIO_FLASH_INTENSITY = 5;
  public static final int TAG_CASIO_OBJECT_DISTANCE = 6;
  public static final int TAG_CASIO_WHITE_BALANCE = 7;
  public static final int TAG_CASIO_UNKNOWN_1 = 8;
  public static final int TAG_CASIO_UNKNOWN_2 = 9;
  public static final int TAG_CASIO_DIGITAL_ZOOM = 10;
  public static final int TAG_CASIO_SHARPNESS = 11;
  public static final int TAG_CASIO_CONTRAST = 12;
  public static final int TAG_CASIO_SATURATION = 13;
  public static final int TAG_CASIO_UNKNOWN_3 = 14;
  public static final int TAG_CASIO_UNKNOWN_4 = 15;
  public static final int TAG_CASIO_UNKNOWN_5 = 16;
  public static final int TAG_CASIO_UNKNOWN_6 = 17;
  public static final int TAG_CASIO_UNKNOWN_7 = 18;
  public static final int TAG_CASIO_UNKNOWN_8 = 19;
  public static final int TAG_CASIO_CCD_SENSITIVITY = 20;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public CasioType1MakernoteDirectory()
  {
    setDescriptor(new CasioType1MakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Casio Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(20), "CCD Sensitivity");
    _tagNameMap.put(Integer.valueOf(12), "Contrast");
    _tagNameMap.put(Integer.valueOf(10), "Digital Zoom");
    _tagNameMap.put(Integer.valueOf(5), "Flash Intensity");
    _tagNameMap.put(Integer.valueOf(4), "Flash Mode");
    _tagNameMap.put(Integer.valueOf(3), "Focusing Mode");
    _tagNameMap.put(Integer.valueOf(6), "Object Distance");
    _tagNameMap.put(Integer.valueOf(2), "Quality");
    _tagNameMap.put(Integer.valueOf(1), "Recording Mode");
    _tagNameMap.put(Integer.valueOf(13), "Saturation");
    _tagNameMap.put(Integer.valueOf(11), "Sharpness");
    _tagNameMap.put(Integer.valueOf(8), "Makernote Unknown 1");
    _tagNameMap.put(Integer.valueOf(9), "Makernote Unknown 2");
    _tagNameMap.put(Integer.valueOf(14), "Makernote Unknown 3");
    _tagNameMap.put(Integer.valueOf(15), "Makernote Unknown 4");
    _tagNameMap.put(Integer.valueOf(16), "Makernote Unknown 5");
    _tagNameMap.put(Integer.valueOf(17), "Makernote Unknown 6");
    _tagNameMap.put(Integer.valueOf(18), "Makernote Unknown 7");
    _tagNameMap.put(Integer.valueOf(19), "Makernote Unknown 8");
    _tagNameMap.put(Integer.valueOf(7), "White Balance");
  }
}
