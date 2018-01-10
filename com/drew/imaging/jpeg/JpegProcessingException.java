package com.drew.imaging.jpeg;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;

public class JpegProcessingException
  extends ImageProcessingException
{
  private static final long serialVersionUID = -7870179776125450158L;
  
  public JpegProcessingException(@Nullable String paramString)
  {
    super(paramString);
  }
  
  public JpegProcessingException(@Nullable String paramString, @Nullable Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public JpegProcessingException(@Nullable Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
