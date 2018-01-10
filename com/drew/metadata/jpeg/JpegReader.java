package com.drew.metadata.jpeg;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

public class JpegReader
  implements MetadataReader
{
  public JpegReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    JpegDirectory localJpegDirectory = (JpegDirectory)paramMetadata.getOrCreateDirectory(JpegDirectory.class);
    try
    {
      int i = paramBufferReader.getUInt8(0);
      localJpegDirectory.setInt(0, i);
      int j = paramBufferReader.getUInt16(1);
      localJpegDirectory.setInt(1, j);
      int k = paramBufferReader.getUInt16(3);
      localJpegDirectory.setInt(3, k);
      int m = paramBufferReader.getUInt8(5);
      localJpegDirectory.setInt(5, m);
      int n = 6;
      for (int i1 = 0; i1 < m; i1++)
      {
        int i2 = paramBufferReader.getUInt8(n++);
        int i3 = paramBufferReader.getUInt8(n++);
        int i4 = paramBufferReader.getUInt8(n++);
        JpegComponent localJpegComponent = new JpegComponent(i2, i3, i4);
        localJpegDirectory.setObject(6 + i1, localJpegComponent);
      }
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localJpegDirectory.addError(localBufferBoundsException.getMessage());
    }
  }
}
