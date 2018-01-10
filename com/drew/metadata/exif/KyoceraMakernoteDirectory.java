package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class KyoceraMakernoteDirectory
  extends Directory
{
  public static final int TAG_KYOCERA_PROPRIETARY_THUMBNAIL = 1;
  public static final int TAG_KYOCERA_PRINT_IMAGE_MATCHING_INFO = 3584;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public KyoceraMakernoteDirectory()
  {
    setDescriptor(new KyoceraMakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Kyocera/Contax Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(1), "Proprietary Thumbnail Format Data");
    _tagNameMap.put(Integer.valueOf(3584), "Print Image Matching (PIM) Info");
  }
}
