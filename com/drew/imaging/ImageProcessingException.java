package com.drew.imaging;

import com.drew.lang.CompoundException;
import com.drew.lang.annotations.Nullable;

public class ImageProcessingException
  extends CompoundException
{
  private static final long serialVersionUID = -9115669182209912676L;
  
  public ImageProcessingException(@Nullable String paramString)
  {
    super(paramString);
  }
  
  public ImageProcessingException(@Nullable String paramString, @Nullable Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public ImageProcessingException(@Nullable Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}
