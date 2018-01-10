package com.drew.metadata.adobe;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class AdobeJpegDirectory
  extends Directory
{
  public static final int TAG_DCT_ENCODE_VERSION = 0;
  public static final int TAG_APP14_FLAGS0 = 1;
  public static final int TAG_APP14_FLAGS1 = 2;
  public static final int TAG_COLOR_TRANSFORM = 3;
  private static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public AdobeJpegDirectory()
  {
    setDescriptor(new AdobeJpegDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Adobe Jpeg";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(0), "DCT Encode Version");
    _tagNameMap.put(Integer.valueOf(1), "Flags 0");
    _tagNameMap.put(Integer.valueOf(2), "Flags 1");
    _tagNameMap.put(Integer.valueOf(3), "Color Transform");
  }
}
