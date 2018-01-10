package com.drew.metadata;

import com.drew.lang.CompoundException;
import com.drew.lang.annotations.Nullable;

public class MetadataException
  extends CompoundException
{
  private static final long serialVersionUID = 8612756143363919682L;
  
  public MetadataException(@Nullable String paramString)
  {
    super(paramString);
  }
  
  public MetadataException(@Nullable Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public MetadataException(@Nullable String paramString, @Nullable Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
