package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


















public class UUDecoderStream
  extends FilterInputStream
{
  private String name;
  private int mode;
  private byte[] buffer;
  private int bufsize;
  private int index;
  private boolean gotPrefix = false;
  

  private LineInputStream lin;
  

  public UUDecoderStream(InputStream paramInputStream)
  {
    super(paramInputStream);
    lin = new LineInputStream(paramInputStream);
    buffer = new byte[45];
  }
  












  public int read()
    throws IOException
  {
    if (index >= bufsize) {
      readPrefix();
      if (!decode())
        return -1;
      index = 0;
    }
    return buffer[(index++)] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i;
    try {
      for (i = 0; i < paramInt2; i++) { int j;
        if ((j = read()) == -1) {
          if (i != 0) break;
          i = -1;
          break;
        }
        paramArrayOfByte[(paramInt1 + i)] = ((byte)j);
      }
    } catch (IOException localIOException) {
      return -1;
    }
    return i;
  }
  
  public boolean markSupported() {
    return false;
  }
  
  public int available()
    throws IOException
  {
    return in.available() * 3 / 4 + (bufsize - index);
  }
  





  public String getName()
    throws IOException
  {
    readPrefix();
    return name;
  }
  





  public int getMode()
    throws IOException
  {
    readPrefix();
    return mode;
  }
  



  private void readPrefix()
    throws IOException
  {
    if (gotPrefix) {
      return;
    }
    String str;
    do
    {
      str = lin.readLine();
      if (str == null)
        throw new IOException("UUDecoder error: No Begin");
    } while (!str.regionMatches(true, 0, "begin", 0, 5));
    mode = Integer.parseInt(str.substring(6, 9));
    name = str.substring(10);
    gotPrefix = true;
  }
  


  private boolean decode()
    throws IOException
  {
    bufsize = 0;
    String str = lin.readLine();
    





    if (str == null)
      throw new IOException("Missing End");
    if (str.regionMatches(true, 0, "end", 0, 3)) {
      return false;
    }
    if (str.length() < 1)
      throw new IOException("Short buffer error");
    int i = str.charAt(0);
    if ((i < 32) || (i > 77)) {
      throw new IOException("Buffer format error");
    }
    





    i = i - 32 & 0x3F;
    
    if (i == 0) {
      str = lin.readLine();
      if ((str == null) || (!str.regionMatches(true, 0, "end", 0, 3)))
        throw new IOException("Missing End");
      return false;
    }
    
    int j = (i * 8 + 5) / 6;
    
    if (str.length() < j + 1) {
      throw new IOException("Short buffer error");
    }
    int k = 1;
    






    while (bufsize < i)
    {
      int m = (byte)(str.charAt(k++) - ' ' & 0x3F);
      int n = (byte)(str.charAt(k++) - ' ' & 0x3F);
      buffer[(bufsize++)] = ((byte)(m << 2 & 0xFC | n >>> 4 & 0x3));
      
      if (bufsize < i) {
        m = n;
        n = (byte)(str.charAt(k++) - ' ' & 0x3F);
        buffer[(bufsize++)] = 
          ((byte)(m << 4 & 0xF0 | n >>> 2 & 0xF));
      }
      
      if (bufsize < i) {
        m = n;
        n = (byte)(str.charAt(k++) - ' ' & 0x3F);
        buffer[(bufsize++)] = ((byte)(m << 6 & 0xC0 | n & 0x3F));
      }
    }
    return true;
  }
}
