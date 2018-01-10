package com.sun.mail.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

















public class ASCIIUtility
{
  private ASCIIUtility() {}
  
  public static int parseInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws NumberFormatException
  {
    if (paramArrayOfByte == null) {
      throw new NumberFormatException("null");
    }
    int i = 0;
    int j = 0;
    int k = paramInt1;
    



    if (paramInt2 > paramInt1) { int m;
      if (paramArrayOfByte[k] == 45) {
        j = 1;
        m = Integer.MIN_VALUE;
        k++;
      } else {
        m = -2147483647;
      }
      int n = m / paramInt3;
      int i1; if (k < paramInt2) {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException(
            "illegal number: " + toString(paramArrayOfByte, paramInt1, paramInt2));
        }
        
        i = -i1;
      }
      
      while (k < paramInt2)
      {
        i1 = Character.digit((char)paramArrayOfByte[(k++)], paramInt3);
        if (i1 < 0) {
          throw new NumberFormatException("illegal number");
        }
        if (i < n) {
          throw new NumberFormatException("illegal number");
        }
        i *= paramInt3;
        if (i < m + i1) {
          throw new NumberFormatException("illegal number");
        }
        i -= i1;
      }
    } else {
      throw new NumberFormatException("illegal number");
    }
    if (j != 0) {
      if (k > paramInt1 + 1) {
        return i;
      }
      throw new NumberFormatException("illegal number");
    }
    
    return -i;
  }
  





  public static int parseInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NumberFormatException
  {
    return parseInt(paramArrayOfByte, paramInt1, paramInt2, 10);
  }
  






  public static long parseLong(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws NumberFormatException
  {
    if (paramArrayOfByte == null) {
      throw new NumberFormatException("null");
    }
    long l1 = 0L;
    int i = 0;
    int j = paramInt1;
    



    if (paramInt2 > paramInt1) { long l2;
      if (paramArrayOfByte[j] == 45) {
        i = 1;
        l2 = Long.MIN_VALUE;
        j++;
      } else {
        l2 = -9223372036854775807L;
      }
      long l3 = l2 / paramInt3;
      int k; if (j < paramInt2) {
        k = Character.digit((char)paramArrayOfByte[(j++)], paramInt3);
        if (k < 0) {
          throw new NumberFormatException(
            "illegal number: " + toString(paramArrayOfByte, paramInt1, paramInt2));
        }
        
        l1 = -k;
      }
      
      while (j < paramInt2)
      {
        k = Character.digit((char)paramArrayOfByte[(j++)], paramInt3);
        if (k < 0) {
          throw new NumberFormatException("illegal number");
        }
        if (l1 < l3) {
          throw new NumberFormatException("illegal number");
        }
        l1 *= paramInt3;
        if (l1 < l2 + k) {
          throw new NumberFormatException("illegal number");
        }
        l1 -= k;
      }
    } else {
      throw new NumberFormatException("illegal number");
    }
    if (i != 0) {
      if (j > paramInt1 + 1) {
        return l1;
      }
      throw new NumberFormatException("illegal number");
    }
    
    return -l1;
  }
  





  public static long parseLong(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NumberFormatException
  {
    return parseLong(paramArrayOfByte, paramInt1, paramInt2, 10);
  }
  




  public static String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    char[] arrayOfChar = new char[i];
    
    int j = 0; for (int k = paramInt1; j < i;) {
      arrayOfChar[(j++)] = ((char)paramArrayOfByte[(k++)]);
    }
    return new String(arrayOfChar);
  }
  
  public static String toString(ByteArrayInputStream paramByteArrayInputStream) {
    int i = paramByteArrayInputStream.available();
    char[] arrayOfChar = new char[i];
    byte[] arrayOfByte = new byte[i];
    
    paramByteArrayInputStream.read(arrayOfByte, 0, i);
    for (int j = 0; j < i;) {
      arrayOfChar[j] = ((char)arrayOfByte[(j++)]);
    }
    return new String(arrayOfChar);
  }
  
  public static byte[] getBytes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    byte[] arrayOfByte = new byte[i];
    
    for (int j = 0; j < i;)
      arrayOfByte[j] = ((byte)arrayOfChar[(j++)]);
    return arrayOfByte;
  }
  
  public static byte[] getBytes(InputStream paramInputStream)
    throws IOException
  {
    int j = 1024;
    
    byte[] arrayOfByte;
    int i;
    if ((paramInputStream instanceof ByteArrayInputStream)) {
      j = paramInputStream.available();
      arrayOfByte = new byte[j];
      i = paramInputStream.read(arrayOfByte, 0, j);
    }
    else {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      arrayOfByte = new byte[j];
      while ((i = paramInputStream.read(arrayOfByte, 0, j)) != -1)
        localByteArrayOutputStream.write(arrayOfByte, 0, i);
      arrayOfByte = localByteArrayOutputStream.toByteArray();
    }
    return arrayOfByte;
  }
}
