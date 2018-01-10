package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import java.util.HashMap;

public class JpegCommentDirectory
  extends Directory
{
  public static final int TAG_JPEG_COMMENT = 0;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public JpegCommentDirectory()
  {
    setDescriptor(new JpegCommentDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "JpegComment";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(0), "Jpeg Comment");
  }
}
