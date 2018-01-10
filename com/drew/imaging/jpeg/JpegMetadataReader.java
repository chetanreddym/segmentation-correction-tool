package com.drew.imaging.jpeg;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.adobe.AdobeJpegReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.jfif.JfifReader;
import com.drew.metadata.jpeg.JpegCommentReader;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.jpeg.JpegReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class JpegMetadataReader
{
  @NotNull
  public static Metadata readMetadata(@NotNull InputStream paramInputStream)
    throws JpegProcessingException
  {
    return readMetadata(paramInputStream, true);
  }
  
  @NotNull
  public static Metadata readMetadata(@NotNull InputStream paramInputStream, boolean paramBoolean)
    throws JpegProcessingException
  {
    JpegSegmentReader localJpegSegmentReader = new JpegSegmentReader(paramInputStream, paramBoolean);
    return extractMetadataFromJpegSegmentReader(localJpegSegmentReader.getSegmentData());
  }
  
  @NotNull
  public static Metadata readMetadata(@NotNull File paramFile)
    throws JpegProcessingException, IOException
  {
    JpegSegmentReader localJpegSegmentReader = new JpegSegmentReader(paramFile);
    return extractMetadataFromJpegSegmentReader(localJpegSegmentReader.getSegmentData());
  }
  
  @NotNull
  public static Metadata extractMetadataFromJpegSegmentReader(@NotNull JpegSegmentData paramJpegSegmentData)
  {
    Metadata localMetadata = new Metadata();
    Object localObject2;
    for (int i = 0; i < 16; i = (byte)(i + 1)) {
      if ((i != 4) && (i != 12))
      {
        localObject1 = paramJpegSegmentData.getSegment((byte)(-64 + i));
        if (localObject1 != null)
        {
          localObject2 = (JpegDirectory)localMetadata.getOrCreateDirectory(JpegDirectory.class);
          ((JpegDirectory)localObject2).setInt(-3, i);
          new JpegReader().extract(new ByteArrayReader((byte[])localObject1), localMetadata);
          break;
        }
      }
    }
    byte[] arrayOfByte1 = paramJpegSegmentData.getSegment((byte)-2);
    if (arrayOfByte1 != null) {
      new JpegCommentReader().extract(new ByteArrayReader(arrayOfByte1), localMetadata);
    }
    Object localObject1 = paramJpegSegmentData.getSegments((byte)-32).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      if ((localObject2.length > 3) && (new String((byte[])localObject2, 0, 4).equals("JFIF"))) {
        new JfifReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      }
    }
    localObject1 = paramJpegSegmentData.getSegments((byte)-31).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      if ((localObject2.length > 3) && ("EXIF".equalsIgnoreCase(new String((byte[])localObject2, 0, 4)))) {
        new ExifReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      }
      if ((localObject2.length > 27) && ("http://ns.adobe.com/xap/1.0/".equalsIgnoreCase(new String((byte[])localObject2, 0, 28)))) {
        new XmpReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      }
    }
    localObject1 = paramJpegSegmentData.getSegments((byte)-30).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      if ((localObject2.length > 10) && (new String((byte[])localObject2, 0, 11).equalsIgnoreCase("ICC_PROFILE")))
      {
        byte[] arrayOfByte2 = new byte[localObject2.length - 14];
        System.arraycopy(localObject2, 14, arrayOfByte2, 0, localObject2.length - 14);
        new IccReader().extract(new ByteArrayReader(arrayOfByte2), localMetadata);
      }
    }
    localObject1 = paramJpegSegmentData.getSegments((byte)-19).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      if ((localObject2.length > 12) && ("Photoshop 3.0".compareTo(new String((byte[])localObject2, 0, 13)) == 0)) {
        new PhotoshopReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      } else {
        new IptcReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      }
    }
    localObject1 = paramJpegSegmentData.getSegments((byte)-18).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (byte[])((Iterator)localObject1).next();
      if ((localObject2.length == 12) && ("Adobe".compareTo(new String((byte[])localObject2, 0, 5)) == 0)) {
        new AdobeJpegReader().extract(new ByteArrayReader((byte[])localObject2), localMetadata);
      }
    }
    return localMetadata;
  }
  
  private JpegMetadataReader()
    throws Exception
  {
    throw new Exception("Not intended for instantiation");
  }
}
