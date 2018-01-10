package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class ExifIFD0Directory
  extends Directory
{
  public static final int TAG_IMAGE_DESCRIPTION = 270;
  public static final int TAG_MAKE = 271;
  public static final int TAG_MODEL = 272;
  public static final int TAG_ORIENTATION = 274;
  public static final int TAG_X_RESOLUTION = 282;
  public static final int TAG_Y_RESOLUTION = 283;
  public static final int TAG_RESOLUTION_UNIT = 296;
  public static final int TAG_SOFTWARE = 305;
  public static final int TAG_DATETIME = 306;
  public static final int TAG_ARTIST = 315;
  public static final int TAG_WHITE_POINT = 318;
  public static final int TAG_PRIMARY_CHROMATICITIES = 319;
  public static final int TAG_YCBCR_COEFFICIENTS = 529;
  public static final int TAG_YCBCR_POSITIONING = 531;
  public static final int TAG_REFERENCE_BLACK_WHITE = 532;
  public static final int TAG_COPYRIGHT = 33432;
  public static final int TAG_WIN_TITLE = 40091;
  public static final int TAG_WIN_COMMENT = 40092;
  public static final int TAG_WIN_AUTHOR = 40093;
  public static final int TAG_WIN_KEYWORDS = 40094;
  public static final int TAG_WIN_SUBJECT = 40095;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public ExifIFD0Directory()
  {
    setDescriptor(new ExifIFD0Descriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Exif IFD0";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(270), "Image Description");
    _tagNameMap.put(Integer.valueOf(271), "Make");
    _tagNameMap.put(Integer.valueOf(272), "Model");
    _tagNameMap.put(Integer.valueOf(274), "Orientation");
    _tagNameMap.put(Integer.valueOf(282), "X Resolution");
    _tagNameMap.put(Integer.valueOf(283), "Y Resolution");
    _tagNameMap.put(Integer.valueOf(296), "Resolution Unit");
    _tagNameMap.put(Integer.valueOf(305), "Software");
    _tagNameMap.put(Integer.valueOf(306), "Date/Time");
    _tagNameMap.put(Integer.valueOf(315), "Artist");
    _tagNameMap.put(Integer.valueOf(318), "White Point");
    _tagNameMap.put(Integer.valueOf(319), "Primary Chromaticities");
    _tagNameMap.put(Integer.valueOf(529), "YCbCr Coefficients");
    _tagNameMap.put(Integer.valueOf(531), "YCbCr Positioning");
    _tagNameMap.put(Integer.valueOf(532), "Reference Black/White");
    _tagNameMap.put(Integer.valueOf(33432), "Copyright");
    _tagNameMap.put(Integer.valueOf(40093), "Windows XP Author");
    _tagNameMap.put(Integer.valueOf(40092), "Windows XP Comment");
    _tagNameMap.put(Integer.valueOf(40094), "Windows XP Keywords");
    _tagNameMap.put(Integer.valueOf(40095), "Windows XP Subject");
    _tagNameMap.put(Integer.valueOf(40091), "Windows XP Title");
  }
}
