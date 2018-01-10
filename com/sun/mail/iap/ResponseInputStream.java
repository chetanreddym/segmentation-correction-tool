package com.sun.mail.iap;

import com.sun.mail.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;




















public class ResponseInputStream
  extends BufferedInputStream
{
  private static final int increment = 256;
  private byte[] buffer;
  private int sz;
  private int idx;
  
  public ResponseInputStream(InputStream paramInputStream)
  {
    super(paramInputStream, 2048);
  }
  


  public ByteArray readResponse()
    throws IOException
  {
    buffer = new byte[''];
    idx = 0;
    sz = 128;
    read0();
    return new ByteArray(buffer, 0, idx);
  }
  





  private void read0()
    throws IOException
  {
    int i = 0;
    int j = 0;
    

    do
    {
      switch (i) {
      case 10: 
        if ((idx > 0) && (buffer[(idx - 1)] == 13))
          j = 1;
        break; }
      if (idx >= sz)
        growBuffer(256);
      buffer[(idx++)] = ((byte)i);
      if (j != 0) break;
    } while ((i = pos >= count ? read() : buf[(pos++)] & 0xFF) != -1);
    










    if (i == -1) {
      throw new IOException();
    }
    

    if ((idx >= 5) && (buffer[(idx - 3)] == 125))
    {

      for (int k = idx - 4; k >= 0; k--) {
        if (buffer[k] == 123)
          break;
      }
      if (k < 0) {
        return;
      }
      int m = 0;
      try
      {
        m = ASCIIUtility.parseInt(buffer, k + 1, idx - 3);
      } catch (NumberFormatException localNumberFormatException) {
        return;
      }
      

      if (m > 0) {
        int n = sz - idx;
        if (m > n)
        {
          growBuffer(m - n < 256 ? 
            256 : m - n);
        }
        



        while (m > 0) {
          int i1 = read(buffer, idx, m);
          m -= i1;
          idx += i1;
        }
      }
      




      read0();
    }
  }
  

  private void growBuffer(int paramInt)
  {
    byte[] arrayOfByte = new byte[sz + paramInt];
    if (buffer != null)
      System.arraycopy(buffer, 0, arrayOfByte, 0, idx);
    buffer = arrayOfByte;
    sz += paramInt;
  }
}
