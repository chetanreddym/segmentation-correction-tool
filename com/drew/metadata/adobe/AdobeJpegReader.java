package com.drew.metadata.adobe;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

public class AdobeJpegReader
  implements MetadataReader
{
  public AdobeJpegReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    Directory localDirectory = paramMetadata.getOrCreateDirectory(AdobeJpegDirectory.class);
    if (paramBufferReader.getLength() != 12L)
    {
      localDirectory.addError(String.format("Adobe JPEG data is expected to be 12 bytes long, not %d.", new Object[] { Long.valueOf(paramBufferReader.getLength()) }));
      return;
    }
    try
    {
      paramBufferReader.setMotorolaByteOrder(false);
      if (!paramBufferReader.getString(0, 5).equals("Adobe"))
      {
        localDirectory.addError("Invalid Adobe JPEG data header.");
        return;
      }
      localDirectory.setInt(0, paramBufferReader.getUInt16(5));
      localDirectory.setInt(1, paramBufferReader.getUInt16(7));
      localDirectory.setInt(2, paramBufferReader.getUInt16(9));
      localDirectory.setInt(3, paramBufferReader.getInt8(11));
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localDirectory.addError("Exif data segment ended prematurely");
    }
  }
}
