package com.drew.metadata.jfif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import java.util.HashMap;

public class JfifDirectory
  extends Directory
{
  public static final int TAG_JFIF_VERSION = 5;
  public static final int TAG_JFIF_UNITS = 7;
  public static final int TAG_JFIF_RESX = 8;
  public static final int TAG_JFIF_RESY = 10;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public JfifDirectory()
  {
    setDescriptor(new JfifDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Jfif";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  public int getVersion()
    throws MetadataException
  {
    return getInt(5);
  }
  
  public int getResUnits()
    throws MetadataException
  {
    return getInt(7);
  }
  
  public int getImageWidth()
    throws MetadataException
  {
    return getInt(10);
  }
  
  public int getImageHeight()
    throws MetadataException
  {
    return getInt(8);
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(5), "Version");
    _tagNameMap.put(Integer.valueOf(7), "Resolution Units");
    _tagNameMap.put(Integer.valueOf(10), "Y Resolution");
    _tagNameMap.put(Integer.valueOf(8), "X Resolution");
  }
}
