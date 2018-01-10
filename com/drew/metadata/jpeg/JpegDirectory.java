package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import java.util.HashMap;

public class JpegDirectory
  extends Directory
{
  public static final int TAG_JPEG_COMPRESSION_TYPE = -3;
  public static final int TAG_JPEG_DATA_PRECISION = 0;
  public static final int TAG_JPEG_IMAGE_HEIGHT = 1;
  public static final int TAG_JPEG_IMAGE_WIDTH = 3;
  public static final int TAG_JPEG_NUMBER_OF_COMPONENTS = 5;
  public static final int TAG_JPEG_COMPONENT_DATA_1 = 6;
  public static final int TAG_JPEG_COMPONENT_DATA_2 = 7;
  public static final int TAG_JPEG_COMPONENT_DATA_3 = 8;
  public static final int TAG_JPEG_COMPONENT_DATA_4 = 9;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  
  public JpegDirectory()
  {
    setDescriptor(new JpegDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Jpeg";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  @Nullable
  public JpegComponent getComponent(int paramInt)
  {
    int i = 6 + paramInt;
    return (JpegComponent)getObject(i);
  }
  
  public int getImageWidth()
    throws MetadataException
  {
    return getInt(3);
  }
  
  public int getImageHeight()
    throws MetadataException
  {
    return getInt(1);
  }
  
  public int getNumberOfComponents()
    throws MetadataException
  {
    return getInt(5);
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(-3), "Compression Type");
    _tagNameMap.put(Integer.valueOf(0), "Data Precision");
    _tagNameMap.put(Integer.valueOf(3), "Image Width");
    _tagNameMap.put(Integer.valueOf(1), "Image Height");
    _tagNameMap.put(Integer.valueOf(5), "Number of Components");
    _tagNameMap.put(Integer.valueOf(6), "Component 1");
    _tagNameMap.put(Integer.valueOf(7), "Component 2");
    _tagNameMap.put(Integer.valueOf(8), "Component 3");
    _tagNameMap.put(Integer.valueOf(9), "Component 4");
  }
}
