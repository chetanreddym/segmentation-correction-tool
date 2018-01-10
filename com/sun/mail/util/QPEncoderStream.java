package com.sun.mail.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
















public class QPEncoderStream
  extends FilterOutputStream
{
  private int count;
  private int bytesPerLine;
  private boolean gotSpace = false;
  private boolean gotCR = false;
  






  public QPEncoderStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream);
    

    bytesPerLine = (paramInt - 1);
  }
  




  public QPEncoderStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, 76);
  }
  








  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  


  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  



  public void write(int paramInt)
    throws IOException
  {
    paramInt &= 0xFF;
    if (gotSpace) {
      if ((paramInt == 13) || (paramInt == 10))
      {
        output(32, true);
      } else
        output(32, false);
      gotSpace = false;
    }
    
    if (paramInt == 32) {
      gotSpace = true;
    } else if (paramInt == 13) {
      gotCR = true;
      outputCRLF();
    } else if (paramInt == 10) {
      if (gotCR)
      {

        gotCR = false;
      } else
        outputCRLF();
    } else if ((paramInt < 32) || (paramInt >= 127) || (paramInt == 61))
    {
      output(paramInt, true);
    } else {
      output(paramInt, false);
    }
  }
  


  public void flush()
    throws IOException
  {
    out.flush();
  }
  


  public void close()
    throws IOException
  {
    out.close();
  }
  
  private void outputCRLF() throws IOException {
    out.write(13);
    out.write(10);
    count = 0;
  }
  

  private static final char[] hex = {
    '0', '1', '2', '3', '4', '5', '6', '7', 
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  protected void output(int paramInt, boolean paramBoolean) throws IOException
  {
    if (paramBoolean) {
      if (this.count += 3 > bytesPerLine) {
        out.write(61);
        out.write(13);
        out.write(10);
        count = 3;
      }
      out.write(61);
      out.write(hex[(paramInt >> 4)]);
      out.write(hex[(paramInt & 0xF)]);
    } else {
      if (++count > bytesPerLine) {
        out.write(61);
        out.write(13);
        out.write(10);
        count = 1;
      }
      out.write(paramInt);
    }
  }
}
