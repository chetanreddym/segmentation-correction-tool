package com.sun.mail.smtp;

import com.sun.mail.util.CRLFOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;















public class SMTPOutputStream
  extends CRLFOutputStream
{
  public SMTPOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (((lastb == 10) || (lastb == 13) || (lastb == -1)) && (paramInt == 46)) {
      out.write(46);
    }
    
    super.write(paramInt);
  }
  

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = lastb == -1 ? 10 : lastb;
    int j = paramInt1;
    
    paramInt2 += paramInt1;
    for (int k = paramInt1; k < paramInt2; k++) {
      if (((i == 10) || (i == 13)) && (paramArrayOfByte[k] == 46)) {
        super.write(paramArrayOfByte, j, k - j);
        out.write(46);
        j = k;
      }
      i = paramArrayOfByte[k];
    }
    if (paramInt2 - j > 0) {
      super.write(paramArrayOfByte, j, paramInt2 - j);
    }
  }
}
