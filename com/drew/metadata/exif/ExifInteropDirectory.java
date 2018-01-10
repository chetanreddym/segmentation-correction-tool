package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class ExifInteropDirectory
  extends Directory
{
  public static final int TAG_INTEROP_INDEX = 1;
  public static final int TAG_INTEROP_VERSION = 2;
  public static final int TAG_RELATED_IMAGE_FILE_FORMAT = 4096;
  public static final int TAG_RELATED_IMAGE_WIDTH = 4097;
  public static final int TAG_RELATED_IMAGE_LENGTH = 4098;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public ExifInteropDirectory()
  {
    setDescriptor(new ExifInteropDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Interoperability";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(1), "Interoperability Index");
    _tagNameMap.put(Integer.valueOf(2), "Interoperability Version");
    _tagNameMap.put(Integer.valueOf(4096), "Related Image File Format");
    _tagNameMap.put(Integer.valueOf(4097), "Related Image Width");
    _tagNameMap.put(Integer.valueOf(4098), "Related Image Length");
  }
}
