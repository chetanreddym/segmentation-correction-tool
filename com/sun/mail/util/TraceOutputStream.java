package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;






















public class TraceOutputStream
  extends FilterOutputStream
{
  private boolean trace;
  private OutputStream traceOut;
  
  public TraceOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2)
  {
    super(paramOutputStream1);
    traceOut = paramOutputStream2;
  }
  


  public void setTrace(boolean paramBoolean)
  {
    trace = paramBoolean;
  }
  



  public void write(int paramInt)
    throws IOException
  {
    if (trace)
      traceOut.write(paramInt);
    out.write(paramInt);
  }
  



  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (trace)
      traceOut.write(paramArrayOfByte, paramInt1, paramInt2);
    out.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}
