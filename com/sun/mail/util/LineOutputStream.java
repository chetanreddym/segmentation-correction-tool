package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import javax.mail.MessagingException;






















public class LineOutputStream
  extends FilterOutputStream
{
  private static byte[] newline = new byte[2];
  static { newline[0] = 13;
    newline[1] = 10;
  }
  
  public LineOutputStream(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void writeln(String paramString) throws MessagingException {
    try {
      byte[] arrayOfByte = ASCIIUtility.getBytes(paramString);
      out.write(arrayOfByte);
      out.write(newline);
    } catch (Exception localException) {
      throw new MessagingException("IOException", localException);
    }
  }
  
  public void writeln() throws MessagingException {
    try {
      out.write(newline);
    } catch (Exception localException) {
      throw new MessagingException("IOException", localException);
    }
  }
}
