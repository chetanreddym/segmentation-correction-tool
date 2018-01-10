package com.sun.mail.imap.protocol;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;









































































public class BASE64MailboxEncoder
{
  protected byte[] buffer = new byte[4];
  protected int bufsize;
  protected boolean started = false;
  protected Writer out;
  
  public static String encode(String paramString)
  {
    BASE64MailboxEncoder localBASE64MailboxEncoder = null;
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    int j = 0;
    CharArrayWriter localCharArrayWriter = new CharArrayWriter(i);
    

    for (int k = 0; k < i; k++) {
      int m = arrayOfChar[k];
      


      if ((m >= 32) && (m <= 126)) {
        if (localBASE64MailboxEncoder != null) {
          localBASE64MailboxEncoder.flush();
        }
        
        if (m == 38) {
          j = 1;
          localCharArrayWriter.write(38);
          localCharArrayWriter.write(45);
        } else {
          localCharArrayWriter.write(m);

        }
        

      }
      else
      {

        if (localBASE64MailboxEncoder == null) {
          localBASE64MailboxEncoder = new BASE64MailboxEncoder(localCharArrayWriter);
          j = 1;
        }
        
        localBASE64MailboxEncoder.write(m);
      }
    }
    

    if (localBASE64MailboxEncoder != null) {
      localBASE64MailboxEncoder.flush();
    }
    
    if (j != 0) {
      return localCharArrayWriter.toString();
    }
    return paramString;
  }
  




  public BASE64MailboxEncoder(Writer paramWriter)
  {
    out = paramWriter;
  }
  
  public void write(int paramInt)
  {
    try {
      if (!started) {
        started = true;
        out.write(38);
      }
      

      buffer[(bufsize++)] = ((byte)(paramInt >> 8));
      buffer[(bufsize++)] = ((byte)(paramInt & 0xFF));
      
      if (bufsize >= 3) {
        encode();
        bufsize -= 3;
      }
    } catch (IOException localIOException) {
      localIOException.printStackTrace();
    }
  }
  
  public void flush()
  {
    try
    {
      if (bufsize > 0) {
        encode();
        bufsize = 0;
      }
      

      if (started) {
        out.write(45);
        started = false;
      }
    } catch (IOException localIOException) {
      localIOException.printStackTrace();
    }
  }
  
  protected void encode() throws IOException { int i;
    int j;
    int k;
    if (bufsize == 1) {
      i = buffer[0];
      j = 0;
      k = 0;
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
    }
    else if (bufsize == 2) {
      i = buffer[0];
      j = buffer[1];
      k = 0;
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      out.write(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
    }
    else {
      i = buffer[0];
      j = buffer[1];
      k = buffer[2];
      out.write(pem_array[(i >>> 2 & 0x3F)]);
      out.write(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
      out.write(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
      out.write(pem_array[(k & 0x3F)]);
      

      if (bufsize == 4)
        buffer[0] = buffer[3];
    }
  }
  
  private static final char[] pem_array = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
    'w', 'x', 'y', 'z', '0', '1', '2', '3', 
    '4', '5', '6', '7', '8', '9', '+', ',' };
}
