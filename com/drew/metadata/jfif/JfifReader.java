package com.drew.metadata.jfif;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

public class JfifReader
  implements MetadataReader
{
  public JfifReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    JfifDirectory localJfifDirectory = (JfifDirectory)paramMetadata.getOrCreateDirectory(JfifDirectory.class);
    try
    {
      int i = paramBufferReader.getUInt16(5);
      localJfifDirectory.setInt(5, i);
      int j = paramBufferReader.getUInt8(7);
      localJfifDirectory.setInt(7, j);
      int k = paramBufferReader.getUInt16(8);
      localJfifDirectory.setInt(8, k);
      int m = paramBufferReader.getUInt16(10);
      localJfifDirectory.setInt(10, m);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localJfifDirectory.addError(localBufferBoundsException.getMessage());
    }
  }
}
