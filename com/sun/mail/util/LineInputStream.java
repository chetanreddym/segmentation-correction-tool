package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;



















public class LineInputStream
  extends FilterInputStream
{
  private char[] lineBuffer;
  
  public LineInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  







  public String readLine()
    throws IOException
  {
    Object localObject = in;
    char[] arrayOfChar = lineBuffer;
    
    if (arrayOfChar == null) {
      arrayOfChar = this.lineBuffer = new char[''];
    }
    
    int j = arrayOfChar.length;
    int k = 0;
    int i;
    while ((i = ((InputStream)localObject).read()) != -1) {
      if (i == 10)
        break;
      if (i == 13)
      {
        int m = ((InputStream)localObject).read();
        if (m == 10)
          break;
        if (!(localObject instanceof PushbackInputStream))
          localObject = this.in = new PushbackInputStream((InputStream)localObject);
        ((PushbackInputStream)localObject).unread(m);
        
        break;
      }
      


      j--; if (j < 0) {
        arrayOfChar = new char[k + 128];
        j = arrayOfChar.length - k - 1;
        System.arraycopy(lineBuffer, 0, arrayOfChar, 0, k);
        lineBuffer = arrayOfChar;
      }
      arrayOfChar[(k++)] = ((char)i);
    }
    
    if ((i == -1) && (k == 0)) {
      return null;
    }
    return String.copyValueOf(arrayOfChar, 0, k);
  }
}
