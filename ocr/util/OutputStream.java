package ocr.util;

import java.io.PrintStream;






public class OutputStream
  extends PrintStream
{
  PrintStream out;
  
  public OutputStream(PrintStream out1, PrintStream out2)
  {
    super(out1, true);
    out = out2;
  }
  
  public void write(byte[] buf, int off, int len) {
    try { super.write(buf, off, len);
      out.write(buf, off, len);
    } catch (Exception localException) {}
  }
  
  public void flush() {
    super.flush();
    out.flush();
  }
  
  public void close()
  {
    out.flush();
    super.flush();
    super.close();
    out.close();
  }
}
