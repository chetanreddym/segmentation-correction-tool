package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;














public class CRLFOutputStream
  extends FilterOutputStream
{
  protected int lastb = -1;
  

  protected static byte[] newline = new byte[2];
  static { newline[0] = 13;
    newline[1] = 10;
  }
  
  public CRLFOutputStream(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void write(int paramInt) throws IOException {
    if (paramInt == 13) {
      out.write(newline);
    } else if (paramInt == 10) {
      if (lastb != 13)
        out.write(newline);
    } else {
      out.write(paramInt);
    }
    lastb = paramInt;
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1;
    
    paramInt2 += paramInt1;
    for (int j = i; j < paramInt2; j++) {
      if (paramArrayOfByte[j] == 13) {
        out.write(paramArrayOfByte, i, j - i);
        out.write(newline);
        i = j + 1;
      } else if (paramArrayOfByte[j] == 10) {
        if (lastb != 13) {
          out.write(paramArrayOfByte, i, j - i);
          out.write(newline);
        }
        i = j + 1;
      }
      lastb = paramArrayOfByte[j];
    }
    if (paramInt2 - i > 0) {
      out.write(paramArrayOfByte, i, paramInt2 - i);
    }
  }
  
  public void writeln()
    throws IOException
  {
    out.write(newline);
  }
}
