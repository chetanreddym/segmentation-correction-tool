package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class KodakMakernoteDirectory
  extends Directory
{
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public KodakMakernoteDirectory()
  {
    setDescriptor(new KodakMakernoteDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Kodak Makernote";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
}
