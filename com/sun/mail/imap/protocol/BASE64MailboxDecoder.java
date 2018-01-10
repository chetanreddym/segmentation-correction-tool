package com.sun.mail.imap.protocol;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;



















public class BASE64MailboxDecoder
{
  public static String decode(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    int i = 0;
    int j = 0;
    
    char[] arrayOfChar = new char[paramString.length()];
    StringCharacterIterator localStringCharacterIterator = new StringCharacterIterator(paramString);
    
    for (int k = localStringCharacterIterator.first(); k != 65535; 
        k = localStringCharacterIterator.next())
    {
      if (k == 38) {
        i = 1;
        j = base64decode(arrayOfChar, j, localStringCharacterIterator);
      } else {
        arrayOfChar[(j++)] = k;
      }
    }
    

    if (i != 0) {
      return new String(arrayOfChar, 0, j);
    }
    return paramString;
  }
  


  protected static int base64decode(char[] paramArrayOfChar, int paramInt, CharacterIterator paramCharacterIterator)
  {
    int i = 1;
    int j = -1;
    int k = 0;
    
    for (;;)
    {
      int m = (byte)paramCharacterIterator.next();
      if (m == -1) break;
      if (m == 45) {
        if (i == 0)
          break;
        paramArrayOfChar[(paramInt++)] = '&';
        

        break;
      }
      i = 0;
      

      int n = (byte)paramCharacterIterator.next();
      if ((n == -1) || (n == 45)) {
        break;
      }
      
      int i1 = pem_convert_array[(m & 0xFF)];
      int i2 = pem_convert_array[(n & 0xFF)];
      
      int i3 = (byte)(i1 << 2 & 0xFC | i2 >>> 4 & 0x3);
      

      if (j != -1) {
        paramArrayOfChar[(paramInt++)] = ((char)(j << 8 | i3 & 0xFF));
        j = -1;
      } else {
        j = i3 & 0xFF;
      }
      
      int i4 = (byte)paramCharacterIterator.next();
      if (i4 != 61)
      {
        if ((i4 == -1) || (i4 == 45)) {
          break;
        }
        

        i1 = i2;
        i2 = pem_convert_array[(i4 & 0xFF)];
        i3 = (byte)(i1 << 4 & 0xF0 | i2 >>> 2 & 0xF);
        

        if (j != -1) {
          paramArrayOfChar[(paramInt++)] = ((char)(j << 8 | i3 & 0xFF));
          j = -1;
        } else {
          j = i3 & 0xFF;
        }
        
        int i5 = (byte)paramCharacterIterator.next();
        if (i5 != 61)
        {
          if ((i5 == -1) || (i5 == 45)) {
            break;
          }
          

          i1 = i2;
          i2 = pem_convert_array[(i5 & 0xFF)];
          i3 = (byte)(i1 << 6 & 0xC0 | i2 & 0x3F);
          

          if (j != -1) {
            k = (char)(j << 8 | i3 & 0xFF);
            paramArrayOfChar[(paramInt++)] = ((char)(j << 8 | i3 & 0xFF));
            j = -1;
          } else {
            j = i3 & 0xFF;
          }
        }
      } }
    return paramInt;
  }
  


  public BASE64MailboxDecoder() {}
  


  protected static final char[] pem_array = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
    'w', 'x', 'y', 'z', '0', '1', '2', '3', 
    '4', '5', '6', '7', '8', '9', '+', ',' };
  

  protected static final byte[] pem_convert_array = new byte['Ā'];
  
  static {
    for (int i = 0; i < 255; i++)
      pem_convert_array[i] = -1;
    for (int j = 0; j < pem_array.length; j++) {
      pem_convert_array[pem_array[j]] = ((byte)j);
    }
  }
}
