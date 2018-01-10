package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


























public class BASE64DecoderStream
  extends FilterInputStream
{
  public BASE64DecoderStream(InputStream paramInputStream) { super(paramInputStream); }
  private byte[] buffer = new byte[3];
  



  private int bufsize;
  


  private int index;
  



  public int read()
    throws IOException
  {
    if (index >= bufsize) {
      decode();
      if (bufsize == 0)
        return -1;
      index = 0;
    }
    return buffer[(index++)] & 0xFF;
  }
  






  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    




    try
    {
      for (i = 0; i < paramInt2; i++) { int j;
        if ((j = read()) == -1) {
          if (i != 0) break;
          i = -1;
          break;
        }
        paramArrayOfByte[(paramInt1 + i)] = ((byte)j);
      }
    } catch (IOException localIOException) { i = -1;
    }
    return i;
  }
  



  public boolean markSupported()
  {
    return false;
  }
  






  public int available()
    throws IOException
  {
    return in.available() * 3 / 4 + (bufsize - index);
  }
  




  private static final char[] pem_array = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
    'w', 'x', 'y', 'z', '0', '1', '2', '3', 
    '4', '5', '6', '7', '8', '9', '+', '/' };
  

  private static final byte[] pem_convert_array = new byte['Ā'];
  
  static {
    for (int i = 0; i < 255; i++)
      pem_convert_array[i] = -1;
    for (int j = 0; j < pem_array.length; j++) {
      pem_convert_array[pem_array[j]] = ((byte)j);
    }
  }
  
  private byte[] decode_buffer = new byte[4];
  
  private void decode() throws IOException { bufsize = 0;
    int i;
    do
    {
      i = in.read();
      if (i == -1)
      {
        return;
      }
    } while ((i == 10) || (i == 13));
    decode_buffer[0] = ((byte)i);
    




    int j = 3;int k = 1;
    int m; while ((m = in.read(decode_buffer, k, j)) != j)
    {

      if (m == -1)
        throw new IOException("Error in encoded stream");
      j -= m;
      k += m;
    }
    

    int n = pem_convert_array[(decode_buffer[0] & 0xFF)];
    int i1 = pem_convert_array[(decode_buffer[1] & 0xFF)];
    
    buffer[(bufsize++)] = ((byte)(n << 2 & 0xFC | i1 >>> 4 & 0x3));
    
    if (decode_buffer[2] == 61)
      return;
    n = i1;
    i1 = pem_convert_array[(decode_buffer[2] & 0xFF)];
    
    buffer[(bufsize++)] = ((byte)(n << 4 & 0xF0 | i1 >>> 2 & 0xF));
    
    if (decode_buffer[3] == 61)
      return;
    n = i1;
    i1 = pem_convert_array[(decode_buffer[3] & 0xFF)];
    
    buffer[(bufsize++)] = ((byte)(n << 6 & 0xC0 | i1 & 0x3F));
  }
  





  public static byte[] decode(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length / 4 * 3;
    if (i == 0) {
      return paramArrayOfByte;
    }
    if (paramArrayOfByte[(paramArrayOfByte.length - 1)] == 61) {
      i--;
      if (paramArrayOfByte[(paramArrayOfByte.length - 2)] == 61)
        i--;
    }
    byte[] arrayOfByte = new byte[i];
    
    int j = 0;int k = 0;
    i = paramArrayOfByte.length;
    while (i > 0)
    {
      int m = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      int n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      
      arrayOfByte[(k++)] = ((byte)(m << 2 & 0xFC | n >>> 4 & 0x3));
      
      if (paramArrayOfByte[j] == 61)
        return arrayOfByte;
      m = n;
      n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      
      arrayOfByte[(k++)] = ((byte)(m << 4 & 0xF0 | n >>> 2 & 0xF));
      
      if (paramArrayOfByte[j] == 61)
        return arrayOfByte;
      m = n;
      n = pem_convert_array[(paramArrayOfByte[(j++)] & 0xFF)];
      
      arrayOfByte[(k++)] = ((byte)(m << 6 & 0xC0 | n & 0x3F));
      i -= 4;
    }
    return arrayOfByte;
  }
}
