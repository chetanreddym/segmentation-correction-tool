package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

public class JpegCommentDescriptor
  extends TagDescriptor<JpegCommentDirectory>
{
  public JpegCommentDescriptor(@NotNull JpegCommentDirectory paramJpegCommentDirectory)
  {
    super(paramJpegCommentDirectory);
  }
  
  @Nullable
  public String getJpegCommentDescription()
  {
    return ((JpegCommentDirectory)_directory).getString(0);
  }
}
