package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ExifThumbnailDirectory
  extends Directory
{
  public static final int TAG_THUMBNAIL_IMAGE_WIDTH = 256;
  public static final int TAG_THUMBNAIL_IMAGE_HEIGHT = 257;
  public static final int TAG_BITS_PER_SAMPLE = 258;
  public static final int TAG_THUMBNAIL_COMPRESSION = 259;
  public static final int TAG_PHOTOMETRIC_INTERPRETATION = 262;
  public static final int TAG_STRIP_OFFSETS = 273;
  public static final int TAG_ORIENTATION = 274;
  public static final int TAG_SAMPLES_PER_PIXEL = 277;
  public static final int TAG_ROWS_PER_STRIP = 278;
  public static final int TAG_STRIP_BYTE_COUNTS = 279;
  public static final int TAG_X_RESOLUTION = 282;
  public static final int TAG_Y_RESOLUTION = 283;
  public static final int TAG_PLANAR_CONFIGURATION = 284;
  public static final int TAG_RESOLUTION_UNIT = 296;
  public static final int TAG_THUMBNAIL_OFFSET = 513;
  public static final int TAG_THUMBNAIL_LENGTH = 514;
  public static final int TAG_YCBCR_COEFFICIENTS = 529;
  public static final int TAG_YCBCR_SUBSAMPLING = 530;
  public static final int TAG_YCBCR_POSITIONING = 531;
  public static final int TAG_REFERENCE_BLACK_WHITE = 532;
  @NotNull
  protected static final HashMap<Integer, String> _tagNameMap = new HashMap();
  @Nullable
  private byte[] _thumbnailData;
  
  public ExifThumbnailDirectory()
  {
    setDescriptor(new ExifThumbnailDescriptor(this));
  }
  
  @NotNull
  public String getName()
  {
    return "Exif Thumbnail";
  }
  
  @NotNull
  protected HashMap<Integer, String> getTagNameMap()
  {
    return _tagNameMap;
  }
  
  public boolean hasThumbnailData()
  {
    return _thumbnailData != null;
  }
  
  @Nullable
  public byte[] getThumbnailData()
  {
    return _thumbnailData;
  }
  
  public void setThumbnailData(@Nullable byte[] paramArrayOfByte)
  {
    _thumbnailData = paramArrayOfByte;
  }
  
  public void writeThumbnail(@NotNull String paramString)
    throws MetadataException, IOException
  {
    byte[] arrayOfByte = _thumbnailData;
    if (arrayOfByte == null) {
      throw new MetadataException("No thumbnail data exists.");
    }
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(arrayOfByte);
    }
    finally
    {
      if (localFileOutputStream != null) {
        localFileOutputStream.close();
      }
    }
  }
  
  static
  {
    _tagNameMap.put(Integer.valueOf(256), "Thumbnail Image Width");
    _tagNameMap.put(Integer.valueOf(257), "Thumbnail Image Height");
    _tagNameMap.put(Integer.valueOf(258), "Bits Per Sample");
    _tagNameMap.put(Integer.valueOf(259), "Thumbnail Compression");
    _tagNameMap.put(Integer.valueOf(262), "Photometric Interpretation");
    _tagNameMap.put(Integer.valueOf(273), "Strip Offsets");
    _tagNameMap.put(Integer.valueOf(274), "Orientation");
    _tagNameMap.put(Integer.valueOf(277), "Samples Per Pixel");
    _tagNameMap.put(Integer.valueOf(278), "Rows Per Strip");
    _tagNameMap.put(Integer.valueOf(279), "Strip Byte Counts");
    _tagNameMap.put(Integer.valueOf(282), "X Resolution");
    _tagNameMap.put(Integer.valueOf(283), "Y Resolution");
    _tagNameMap.put(Integer.valueOf(284), "Planar Configuration");
    _tagNameMap.put(Integer.valueOf(296), "Resolution Unit");
    _tagNameMap.put(Integer.valueOf(513), "Thumbnail Offset");
    _tagNameMap.put(Integer.valueOf(514), "Thumbnail Length");
    _tagNameMap.put(Integer.valueOf(529), "YCbCr Coefficients");
    _tagNameMap.put(Integer.valueOf(530), "YCbCr Sub-Sampling");
    _tagNameMap.put(Integer.valueOf(531), "YCbCr Positioning");
    _tagNameMap.put(Integer.valueOf(532), "Reference Black/White");
  }
}
