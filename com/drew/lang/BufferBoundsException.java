package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import java.io.IOException;

public final class BufferBoundsException
  extends Exception
{
  private static final long serialVersionUID = 2911102837808946396L;
  
  public BufferBoundsException(@NotNull byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(getMessage(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public BufferBoundsException(String paramString)
  {
    super(paramString);
  }
  
  public BufferBoundsException(String paramString, IOException paramIOException)
  {
    super(paramString, paramIOException);
  }
  
  private static String getMessage(@NotNull byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      return String.format("Attempt to read from buffer using a negative index (%s)", new Object[] { Integer.valueOf(paramInt1) });
    }
    return String.format("Attempt to read %d byte%s from beyond end of buffer (requested index: %d, max index: %d)", new Object[] { Integer.valueOf(paramInt2), paramInt2 == 1 ? "" : "s", Integer.valueOf(paramInt1), Integer.valueOf(paramArrayOfByte.length - 1) });
  }
}
