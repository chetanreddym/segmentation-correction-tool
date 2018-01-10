package com.drew.metadata.xmp;

import com.adobe.xmp.XMPMeta;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import java.util.HashMap;
import java.util.Map;

public class XmpDirectory
  extends Directory
{
  public static final int TAG_MAKE = 1;
  public static final int TAG_MODEL = 2;
  public static final int TAG_EXPOSURE_TIME = 3;
  public static final int TAG_SHUTTER_SPEED = 4;
  public static final int TAG_F_NUMBER = 5;
  public static final int TAG_LENS_INFO = 6;
  public static final int TAG_LENS = 7;
  public static final int TAG_CAMERA_SERIAL_NUMBER = 8;
  public static final int TAG_FIRMWARE = 9;
  public static final int TAG_FOCAL_LENGTH = 10;
  public static final int TAG_APERTURE_VALUE = 11;
  public static final int TAG_EXPOSURE_PROGRAM = 12;
  public static final int TAG_DATETIME_ORIGINAL = 13;
  public static final int TAG_DATETIME_DIGITIZED = 14;
  public static final int TAG_RATING = 4097;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  @NotNull
  private final Map<String, String> _propertyValueByPath = new HashMap();
  @Nullable
  private XMPMeta _xmpMeta;
  
  public XmpDirectory()
  {
    setDescriptor(new XmpDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Xmp";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  void addProperty(@NotNull String paramString1, @NotNull String paramString2)
  {
    _propertyValueByPath.put(paramString1, paramString2);
  }
  
  @NotNull
  public Map<String, String> getXmpProperties()
  {
    return _propertyValueByPath;
  }
  
  public void setXMPMeta(@NotNull XMPMeta paramXMPMeta)
  {
    _xmpMeta = paramXMPMeta;
  }
  
  @Nullable
  public XMPMeta getXMPMeta()
  {
    return _xmpMeta;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(1), "Make");
    _tagNameMap.put(Integer.valueOf(2), "Model");
    _tagNameMap.put(Integer.valueOf(3), "Exposure Time");
    _tagNameMap.put(Integer.valueOf(4), "Shutter Speed Value");
    _tagNameMap.put(Integer.valueOf(5), "F-Number");
    _tagNameMap.put(Integer.valueOf(6), "Lens Information");
    _tagNameMap.put(Integer.valueOf(7), "Lens");
    _tagNameMap.put(Integer.valueOf(8), "Serial Number");
    _tagNameMap.put(Integer.valueOf(9), "Firmware");
    _tagNameMap.put(Integer.valueOf(10), "Focal Length");
    _tagNameMap.put(Integer.valueOf(11), "Aperture Value");
    _tagNameMap.put(Integer.valueOf(12), "Exposure Program");
    _tagNameMap.put(Integer.valueOf(13), "Date/Time Original");
    _tagNameMap.put(Integer.valueOf(14), "Date/Time Digitized");
    _tagNameMap.put(Integer.valueOf(4097), "Rating");
  }
}
