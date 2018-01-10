package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class SonyType6MakernoteDirectory
  extends Directory
{
  public static final int TAG_MAKER_NOTE_THUMB_OFFSET = 1299;
  public static final int TAG_MAKER_NOTE_THUMB_LENGTH = 1300;
  public static final int TAG_UNKNOWN_1 = 1301;
  public static final int TAG_MAKER_NOTE_THUMB_VERSION = 8192;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public SonyType6MakernoteDirectory()
  {
    setDescriptor(new SonyType6MakernoteDescriptor(this));
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
    _tagNameMap.put(Integer.valueOf(1299), "Maker Note Thumb Offset");
    _tagNameMap.put(Integer.valueOf(1300), "Maker Note Thumb Length");
    _tagNameMap.put(Integer.valueOf(1301), "Sony-6-0x0203");
    _tagNameMap.put(Integer.valueOf(8192), "Maker Note Thumb Version");
  }
}
