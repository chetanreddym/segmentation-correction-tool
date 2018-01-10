package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class PsdHeaderDirectory
  extends Directory
{
  public static final int TAG_CHANNEL_COUNT = 1;
  public static final int TAG_IMAGE_HEIGHT = 2;
  public static final int TAG_IMAGE_WIDTH = 3;
  public static final int TAG_BITS_PER_CHANNEL = 4;
  public static final int TAG_COLOR_MODE = 5;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public PsdHeaderDirectory()
  {
    setDescriptor(new PsdHeaderDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "PSD Header";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(1), "Channel Count");
    _tagNameMap.put(Integer.valueOf(2), "Image Height");
    _tagNameMap.put(Integer.valueOf(3), "Image Width");
    _tagNameMap.put(Integer.valueOf(4), "Bits Per Channel");
    _tagNameMap.put(Integer.valueOf(5), "Color Mode");
  }
}
