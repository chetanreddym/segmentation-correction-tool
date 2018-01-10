package com.drew.metadata.photoshop;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

public class PsdReader
  implements MetadataReader
{
  public PsdReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    PsdHeaderDirectory localPsdHeaderDirectory = (PsdHeaderDirectory)paramMetadata.getOrCreateDirectory(PsdHeaderDirectory.class);
    try
    {
      int i = paramBufferReader.getInt32(0);
      if (i != 943870035)
      {
        localPsdHeaderDirectory.addError("Invalid PSD file signature");
        return;
      }
      int j = paramBufferReader.getUInt16(4);
      if ((j != 1) && (j != 2))
      {
        localPsdHeaderDirectory.addError("Invalid PSD file version (must be 1 or 2)");
        return;
      }
      int k = paramBufferReader.getUInt16(12);
      localPsdHeaderDirectory.setInt(1, k);
      int m = paramBufferReader.getInt32(14);
      localPsdHeaderDirectory.setInt(2, m);
      int n = paramBufferReader.getInt32(18);
      localPsdHeaderDirectory.setInt(3, n);
      int i1 = paramBufferReader.getUInt16(22);
      localPsdHeaderDirectory.setInt(4, i1);
      int i2 = paramBufferReader.getUInt16(24);
      localPsdHeaderDirectory.setInt(5, i2);
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localPsdHeaderDirectory.addError("Unable to read PSD header");
    }
  }
}
