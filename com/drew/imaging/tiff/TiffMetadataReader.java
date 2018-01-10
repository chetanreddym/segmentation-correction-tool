package com.drew.imaging.tiff;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessFileReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class TiffMetadataReader
{
  public TiffMetadataReader() {}
  
  @NotNull
  public static Metadata readMetadata(@NotNull File paramFile)
    throws IOException
  {
    Metadata localMetadata = new Metadata();
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "r");
    try
    {
      new ExifReader().extractTiff(new RandomAccessFileReader(localRandomAccessFile), localMetadata);
    }
    finally
    {
      localRandomAccessFile.close();
    }
    return localMetadata;
  }
  
  @Deprecated
  @NotNull
  public static Metadata readMetadata(@NotNull InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i;
    while ((i = paramInputStream.read()) != -1) {
      localByteArrayOutputStream.write(i);
    }
    Metadata localMetadata = new Metadata();
    new ExifReader().extractTiff(new ByteArrayReader(localByteArrayOutputStream.toByteArray()), localMetadata);
    return localMetadata;
  }
}
