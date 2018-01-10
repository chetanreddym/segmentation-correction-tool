package com.drew.metadata.jpeg;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

public class JpegCommentReader
  implements MetadataReader
{
  public JpegCommentReader() {}
  
  public void extract(@NotNull BufferReader paramBufferReader, @NotNull Metadata paramMetadata)
  {
    JpegCommentDirectory localJpegCommentDirectory = (JpegCommentDirectory)paramMetadata.getOrCreateDirectory(JpegCommentDirectory.class);
    try
    {
      localJpegCommentDirectory.setString(0, paramBufferReader.getString(0, (int)paramBufferReader.getLength()));
    }
    catch (BufferBoundsException localBufferBoundsException)
    {
      localJpegCommentDirectory.addError("Exception reading JPEG comment string");
    }
  }
}
