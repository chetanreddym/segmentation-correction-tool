package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class OlympusMakernoteDirectory
  extends Directory
{
  public static final int TAG_OLYMPUS_MAKERNOTE_VERSION = 0;
  public static final int TAG_OLYMPUS_CAMERA_SETTINGS_1 = 1;
  public static final int TAG_OLYMPUS_CAMERA_SETTINGS_2 = 3;
  public static final int TAG_OLYMPUS_COMPRESSED_IMAGE_SIZE = 64;
  public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_1 = 129;
  public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_2 = 136;
  public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_LENGTH = 137;
  public static final int TAG_OLYMPUS_COLOUR_MODE = 257;
  public static final int TAG_OLYMPUS_IMAGE_QUALITY_1 = 258;
  public static final int TAG_OLYMPUS_IMAGE_QUALITY_2 = 259;
  public static final int TAG_OLYMPUS_SPECIAL_MODE = 512;
  public static final int TAG_OLYMPUS_JPEG_QUALITY = 513;
  public static final int TAG_OLYMPUS_MACRO_MODE = 514;
  public static final int TAG_OLYMPUS_UNKNOWN_1 = 515;
  public static final int TAG_OLYMPUS_DIGI_ZOOM_RATIO = 516;
  public static final int TAG_OLYMPUS_UNKNOWN_2 = 517;
  public static final int TAG_OLYMPUS_UNKNOWN_3 = 518;
  public static final int TAG_OLYMPUS_FIRMWARE_VERSION = 519;
  public static final int TAG_OLYMPUS_PICT_INFO = 520;
  public static final int TAG_OLYMPUS_CAMERA_ID = 521;
  public static final int TAG_OLYMPUS_IMAGE_WIDTH = 523;
  public static final int TAG_OLYMPUS_IMAGE_HEIGHT = 524;
  public static final int TAG_OLYMPUS_ORIGINAL_MANUFACTURER_MODEL = 525;
  public static final int TAG_OLYMPUS_PRINT_IMAGE_MATCHING_INFO = 3584;
  public static final int TAG_OLYMPUS_DATA_DUMP = 3840;
  public static final int TAG_OLYMPUS_FLASH_MODE = 4100;
  public static final int TAG_OLYMPUS_BRACKET = 4102;
  public static final int TAG_OLYMPUS_FOCUS_MODE = 4107;
  public static final int TAG_OLYMPUS_FOCUS_DISTANCE = 4108;
  public static final int TAG_OLYMPUS_ZOOM = 4109;
  public static final int TAG_OLYMPUS_MACRO_FOCUS = 4110;
  public static final int TAG_OLYMPUS_SHARPNESS = 4111;
  public static final int TAG_OLYMPUS_COLOUR_MATRIX = 4113;
  public static final int TAG_OLYMPUS_BLACK_LEVEL = 4114;
  public static final int TAG_OLYMPUS_WHITE_BALANCE = 4117;
  public static final int TAG_OLYMPUS_RED_BIAS = 4119;
  public static final int TAG_OLYMPUS_BLUE_BIAS = 4120;
  public static final int TAG_OLYMPUS_SERIAL_NUMBER = 4122;
  public static final int TAG_OLYMPUS_FLASH_BIAS = 4131;
  public static final int TAG_OLYMPUS_CONTRAST = 4137;
  public static final int TAG_OLYMPUS_SHARPNESS_FACTOR = 4138;
  public static final int TAG_OLYMPUS_COLOUR_CONTROL = 4139;
  public static final int TAG_OLYMPUS_VALID_BITS = 4140;
  public static final int TAG_OLYMPUS_CORING_FILTER = 4141;
  public static final int TAG_OLYMPUS_FINAL_WIDTH = 4142;
  public static final int TAG_OLYMPUS_FINAL_HEIGHT = 4143;
  public static final int TAG_OLYMPUS_COMPRESSION_RATIO = 4148;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public OlympusMakernoteDirectory()
  {
    setDescriptor(new OlympusMakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Olympus Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(512), "Special Mode");
    _tagNameMap.put(Integer.valueOf(513), "Jpeg Quality");
    _tagNameMap.put(Integer.valueOf(514), "Macro");
    _tagNameMap.put(Integer.valueOf(515), "Makernote Unknown 1");
    _tagNameMap.put(Integer.valueOf(516), "DigiZoom Ratio");
    _tagNameMap.put(Integer.valueOf(517), "Makernote Unknown 2");
    _tagNameMap.put(Integer.valueOf(518), "Makernote Unknown 3");
    _tagNameMap.put(Integer.valueOf(519), "Firmware Version");
    _tagNameMap.put(Integer.valueOf(520), "Pict Info");
    _tagNameMap.put(Integer.valueOf(521), "Camera Id");
    _tagNameMap.put(Integer.valueOf(3840), "Data Dump");
    _tagNameMap.put(Integer.valueOf(0), "Makernote Version");
    _tagNameMap.put(Integer.valueOf(1), "Camera Settings");
    _tagNameMap.put(Integer.valueOf(3), "Camera Settings");
    _tagNameMap.put(Integer.valueOf(64), "Compressed Image Size");
    _tagNameMap.put(Integer.valueOf(129), "Thumbnail Offset");
    _tagNameMap.put(Integer.valueOf(136), "Thumbnail Offset");
    _tagNameMap.put(Integer.valueOf(137), "Thumbnail Length");
    _tagNameMap.put(Integer.valueOf(257), "Colour Mode");
    _tagNameMap.put(Integer.valueOf(258), "Image Quality");
    _tagNameMap.put(Integer.valueOf(259), "Image Quality");
    _tagNameMap.put(Integer.valueOf(524), "Image Height");
    _tagNameMap.put(Integer.valueOf(523), "Image Width");
    _tagNameMap.put(Integer.valueOf(525), "Original Manufacturer Model");
    _tagNameMap.put(Integer.valueOf(3584), "Print Image Matching (PIM) Info");
    _tagNameMap.put(Integer.valueOf(4100), "Flash Mode");
    _tagNameMap.put(Integer.valueOf(4102), "Bracket");
    _tagNameMap.put(Integer.valueOf(4107), "Focus Mode");
    _tagNameMap.put(Integer.valueOf(4108), "Focus Distance");
    _tagNameMap.put(Integer.valueOf(4109), "Zoom");
    _tagNameMap.put(Integer.valueOf(4110), "Macro Focus");
    _tagNameMap.put(Integer.valueOf(4111), "Sharpness");
    _tagNameMap.put(Integer.valueOf(4113), "Colour Matrix");
    _tagNameMap.put(Integer.valueOf(4114), "Black Level");
    _tagNameMap.put(Integer.valueOf(4117), "White Balance");
    _tagNameMap.put(Integer.valueOf(4119), "Red Bias");
    _tagNameMap.put(Integer.valueOf(4120), "Blue Bias");
    _tagNameMap.put(Integer.valueOf(4122), "Serial Number");
    _tagNameMap.put(Integer.valueOf(4131), "Flash Bias");
    _tagNameMap.put(Integer.valueOf(4137), "Contrast");
    _tagNameMap.put(Integer.valueOf(4138), "Sharpness Factor");
    _tagNameMap.put(Integer.valueOf(4139), "Colour Control");
    _tagNameMap.put(Integer.valueOf(4140), "Valid Bits");
    _tagNameMap.put(Integer.valueOf(4141), "Coring Filter");
    _tagNameMap.put(Integer.valueOf(4142), "Final Width");
    _tagNameMap.put(Integer.valueOf(4143), "Final Height");
    _tagNameMap.put(Integer.valueOf(4148), "Compression Ratio");
  }
}
