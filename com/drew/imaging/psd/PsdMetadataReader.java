package com.drew.imaging.psd;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessFileReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.photoshop.PsdReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class PsdMetadataReader
{
  public PsdMetadataReader() {}
  
  @NotNull
  public static Metadata readMetadata(@NotNull File paramFile)
    throws IOException
  {
    Metadata localMetadata = new Metadata();
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "r");
    try
    {
      new PsdReader().extract(new RandomAccessFileReader(localRandomAccessFile), localMetadata);
    }
    finally
    {
      localRandomAccessFile.close();
    }
    return localMetadata;
  }
  
  @NotNull
  public static Metadata readMetadata(@NotNull InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    Metadata localMetadata = new Metadata();
    byte[] arrayOfByte = new byte[26];
    paramInputStream.read(arrayOfByte, 0, 26);
    new PsdReader().extract(new ByteArrayReader(arrayOfByte), localMetadata);
    return localMetadata;
  }
}
