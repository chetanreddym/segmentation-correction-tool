package com.sun.mail.util;

import java.io.IOException;
import java.io.OutputStream;

















public class QEncoderStream
  extends QPEncoderStream
{
  private static String WORD_SPECIALS = "=_?\"#$%&'(),.:;<>@[\\]^`{|}~";
  private static String TEXT_SPECIALS = "=_?";
  





  public QEncoderStream(OutputStream paramOutputStream, boolean paramBoolean)
  {
    super(paramOutputStream, Integer.MAX_VALUE);
  }
  




  private String specials = paramBoolean ? WORD_SPECIALS : TEXT_SPECIALS;
  




  public void write(int paramInt)
    throws IOException
  {
    paramInt &= 0xFF;
    if (paramInt == 32) {
      output(95, false);
    } else if ((paramInt < 32) || (paramInt >= 127) || (specials.indexOf(paramInt) >= 0))
    {
      output(paramInt, true);
    } else {
      output(paramInt, false);
    }
  }
  

  public static int encodedLength(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    int i = 0;
    String str = paramBoolean ? WORD_SPECIALS : TEXT_SPECIALS;
    for (int j = 0; j < paramArrayOfByte.length; j++) {
      int k = paramArrayOfByte[j] & 0xFF;
      if ((k < 32) || (k >= 127) || (str.indexOf(k) >= 0))
      {
        i += 3;
      } else
        i++;
    }
    return i;
  }
}
