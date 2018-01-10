package com.drew.metadata.photoshop;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.iptc.IptcReader;
import java.util.HashMap;

public class PhotoshopReader
  implements MetadataReader
{
  public PhotoshopReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    PhotoshopDirectory localPhotoshopDirectory = (PhotoshopDirectory)paramMetadata.getOrCreateDirectory(PhotoshopDirectory.class);
    int i = 0;
    try
    {
      i = paramBufferReader.getString(0, 13).equals("Photoshop 3.0") ? 14 : 0;
    }
    catch (BufferBoundsException localBufferBoundsException1)
    {
      localPhotoshopDirectory.addError("Unable to read header");
      return;
    }
    while (i < paramBufferReader.getLength()) {
      try
      {
        i += 4;
        int j = paramBufferReader.getUInt16(i);
        i += 2;
        int k = paramBufferReader.getUInt16(i);
        i += 2;
        if ((k < 0) || (k + i > paramBufferReader.getLength())) {
          return;
        }
        i += k;
        if (i % 2 != 0) {
          i++;
        }
        int m = paramBufferReader.getInt32(i);
        i += 4;
        byte[] arrayOfByte = paramBufferReader.getBytes(i, m);
        i += m;
        if (i % 2 != 0) {
          i++;
        }
        localPhotoshopDirectory.setByteArray(j, arrayOfByte);
        if (j == 1028) {
          new IptcReader().extract(new ByteArrayReader(arrayOfByte), paramMetadata);
        }
        if ((j >= 4000) && (j <= 4999)) {
          PhotoshopDirectory._tagNameMap.put(Integer.valueOf(j), String.format("Plug-in %d Data", new Object[] { Integer.valueOf(j - 4000 + 1) }));
        }
      }
      catch (BufferBoundsException localBufferBoundsException2)
      {
        localPhotoshopDirectory.addError(localBufferBoundsException2.getMessage());
        return;
      }
    }
  }
}
