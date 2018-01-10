package com.sun.mail.util;

import java.io.OutputStream;





















public class BEncoderStream
  extends BASE64EncoderStream
{
  public BEncoderStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream, Integer.MAX_VALUE);
  }
  




  public static int encodedLength(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte.length + 2) / 3 * 4;
  }
}
