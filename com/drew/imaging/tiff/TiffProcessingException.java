package com.drew.imaging.tiff;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;

public class TiffProcessingException
  extends ImageProcessingException
{
  private static final long serialVersionUID = -1658134119488001891L;
  
  public TiffProcessingException(@Nullable String paramString)
  {
    super(paramString);
  }
  
  public TiffProcessingException(@Nullable String paramString, @Nullable Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public TiffProcessingException(@Nullable Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
