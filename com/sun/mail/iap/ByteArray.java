package com.sun.mail.iap;

import java.io.ByteArrayInputStream;




















public class ByteArray
{
  private byte[] bytes;
  private int start;
  private int count;
  
  public ByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    bytes = paramArrayOfByte;
    start = paramInt1;
    count = paramInt2;
  }
  



  public byte[] getBytes()
  {
    return bytes;
  }
  


  public byte[] getNewBytes()
  {
    byte[] arrayOfByte = new byte[count];
    System.arraycopy(bytes, start, arrayOfByte, 0, count);
    return arrayOfByte;
  }
  


  public int getStart()
  {
    return start;
  }
  


  public int getCount()
  {
    return count;
  }
  


  public ByteArrayInputStream toByteArrayInputStream()
  {
    return new ByteArrayInputStream(bytes, start, count);
  }
}
