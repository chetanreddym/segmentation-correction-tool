package javax.mail.internet;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;















































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































class AsciiOutputStream
  extends OutputStream
{
  private boolean breakOnNonAscii;
  private int ascii;
  private int non_ascii;
  private int linelen;
  private boolean longLine = false;
  private int ret;
  
  public AsciiOutputStream(boolean paramBoolean) {
    breakOnNonAscii = paramBoolean;
  }
  
  public void write(int paramInt) throws IOException {
    check(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    paramInt2 += paramInt1;
    for (int i = paramInt1; i < paramInt2; i++)
      check(paramArrayOfByte[i]);
  }
  
  private final void check(int paramInt) throws IOException {
    paramInt &= 0xFF;
    if ((paramInt == 13) || (paramInt == 10)) {
      linelen = 0;
    } else {
      linelen += 1;
      if (linelen > 998)
        longLine = true;
    }
    if (paramInt > 127) {
      non_ascii += 1;
      if (breakOnNonAscii) {
        ret = 3;
        throw new EOFException();
      }
    } else {
      ascii += 1;
    }
  }
  

  public int getAscii()
  {
    if (ret != 0)
      return ret;
    if (non_ascii == 0)
    {
      if (longLine) {
        return 2;
      }
      return 1;
    }
    if (ascii > non_ascii)
      return 2;
    return 3;
  }
}