package com.sun.mail.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;





















public class TraceInputStream
  extends FilterInputStream
{
  private boolean trace;
  private OutputStream traceOut;
  
  public TraceInputStream(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    super(paramInputStream);
    traceOut = paramOutputStream;
  }
  



  public void setTrace(boolean paramBoolean)
  {
    trace = paramBoolean;
  }
  



  public int read()
    throws IOException
  {
    int i = in.read();
    if ((trace) && (i != -1))
      traceOut.write(i);
    return i;
  }
  




  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if ((trace) && (i != -1))
      traceOut.write(paramArrayOfByte, paramInt1, i);
    return i;
  }
}
