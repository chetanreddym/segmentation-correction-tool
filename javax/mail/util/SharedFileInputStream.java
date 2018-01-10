package javax.mail.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import javax.mail.internet.SharedInputStream;




































public class SharedFileInputStream
  extends BufferedInputStream
  implements SharedInputStream
{
  private static int defaultBufferSize = 2048;
  




  protected RandomAccessFile in;
  




  protected int bufsize;
  



  protected long bufpos;
  



  protected long start = 0L;
  




  protected long datalen;
  



  private boolean master = true;
  
  private SharedFile sf;
  
  class SharedFile
  {
    private int cnt;
    private RandomAccessFile in;
    
    SharedFile(String paramString)
      throws IOException
    {
      in = new RandomAccessFile(paramString, "r");
    }
    
    SharedFile(File paramFile) throws IOException {
      in = new RandomAccessFile(paramFile, "r");
    }
    
    public RandomAccessFile open() {
      cnt += 1;
      return in;
    }
    
    public synchronized void close() throws IOException {
      if ((cnt > 0) && (--cnt <= 0))
        in.close();
    }
    
    public synchronized void forceClose() throws IOException {
      if (cnt > 0)
      {
        cnt = 0;
        in.close();
      }
      else {
        try {
          in.close();
        } catch (IOException localIOException) {}
      }
    }
    
    protected void finalize() throws Throwable {
      super.finalize();
      in.close();
    }
  }
  



  private void ensureOpen()
    throws IOException
  {
    if (in == null) {
      throw new IOException("Stream closed");
    }
  }
  



  public SharedFileInputStream(File paramFile)
    throws IOException
  {
    this(paramFile, defaultBufferSize);
  }
  




  public SharedFileInputStream(String paramString)
    throws IOException
  {
    this(paramString, defaultBufferSize);
  }
  






  public SharedFileInputStream(File paramFile, int paramInt)
    throws IOException
  {
    super(null);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0");
    init(new SharedFile(paramFile), paramInt);
  }
  






  public SharedFileInputStream(String paramString, int paramInt)
    throws IOException
  {
    super(null);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0");
    init(new SharedFile(paramString), paramInt);
  }
  
  private void init(SharedFile paramSharedFile, int paramInt) throws IOException {
    sf = paramSharedFile;
    in = paramSharedFile.open();
    start = 0L;
    datalen = in.length();
    bufsize = paramInt;
    buf = new byte[paramInt];
  }
  



  private SharedFileInputStream(SharedFile paramSharedFile, long paramLong1, long paramLong2, int paramInt)
  {
    super(null);
    master = false;
    sf = paramSharedFile;
    in = paramSharedFile.open();
    start = paramLong1;
    bufpos = paramLong1;
    datalen = paramLong2;
    bufsize = paramInt;
    buf = new byte[paramInt];
  }
  





  private void fill()
    throws IOException
  {
    if (markpos < 0) {
      pos = 0;
      bufpos += count;
    } else if (pos >= buf.length) {
      if (markpos > 0) {
        i = pos - markpos;
        System.arraycopy(buf, markpos, buf, 0, i);
        pos = i;
        bufpos += markpos;
        markpos = 0;
      } else if (buf.length >= marklimit) {
        markpos = -1;
        pos = 0;
        bufpos += count;
      } else {
        i = pos * 2;
        if (i > marklimit)
          i = marklimit;
        byte[] arrayOfByte = new byte[i];
        System.arraycopy(buf, 0, arrayOfByte, 0, pos);
        buf = arrayOfByte;
      } }
    count = pos;
    in.seek(bufpos + pos);
    
    int i = buf.length - pos;
    if (bufpos - start + pos + i > datalen)
      i = (int)(datalen - (bufpos - start + pos));
    int j = in.read(buf, pos, i);
    if (j > 0) {
      count = (j + pos);
    }
  }
  





  public synchronized int read()
    throws IOException
  {
    ensureOpen();
    if (pos >= count) {
      fill();
      if (pos >= count)
        return -1;
    }
    return buf[(pos++)] & 0xFF;
  }
  


  private int read1(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = count - pos;
    if (i <= 0)
    {









      fill();
      i = count - pos;
      if (i <= 0) return -1;
    }
    int j = i < paramInt2 ? i : paramInt2;
    System.arraycopy(buf, pos, paramArrayOfByte, paramInt1, j);
    pos += j;
    return j;
  }
  















  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfByte.length - (paramInt1 + paramInt2)) < 0)
      throw new IndexOutOfBoundsException();
    if (paramInt2 == 0) {
      return 0;
    }
    
    int i = read1(paramArrayOfByte, paramInt1, paramInt2);
    if (i <= 0) return i;
    while (i < paramInt2) {
      int j = read1(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j <= 0) break;
      i += j;
    }
    return i;
  }
  






  public synchronized long skip(long paramLong)
    throws IOException
  {
    ensureOpen();
    if (paramLong <= 0L) {
      return 0L;
    }
    long l1 = count - pos;
    
    if (l1 <= 0L)
    {






      fill();
      l1 = count - pos;
      if (l1 <= 0L) {
        return 0L;
      }
    }
    long l2 = l1 < paramLong ? l1 : paramLong;
    pos = ((int)(pos + l2));
    return l2;
  }
  






  public synchronized int available()
    throws IOException
  {
    ensureOpen();
    return count - pos + in_available();
  }
  
  private int in_available() throws IOException
  {
    return (int)(start + datalen - (bufpos + count));
  }
  







  public synchronized void mark(int paramInt)
  {
    marklimit = paramInt;
    markpos = pos;
  }
  












  public synchronized void reset()
    throws IOException
  {
    ensureOpen();
    if (markpos < 0)
      throw new IOException("Resetting to invalid mark");
    pos = markpos;
  }
  










  public boolean markSupported()
  {
    return true;
  }
  




  public void close()
    throws IOException
  {
    if (in == null)
      return;
    try {
      if (master) {
        sf.forceClose();
      } else
        sf.close();
    } finally {
      sf = null;
      in = null;
      buf = null;
    }
  }
  






  public long getPosition()
  {
    if (in == null)
      throw new RuntimeException("Stream closed");
    return bufpos + pos - start;
  }
  











  public InputStream newStream(long paramLong1, long paramLong2)
  {
    if (in == null)
      throw new RuntimeException("Stream closed");
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("start < 0");
    if (paramLong2 == -1L)
      paramLong2 = datalen;
    return new SharedFileInputStream(sf, start + (int)paramLong1, (int)(paramLong2 - paramLong1), bufsize);
  }
  




















  protected void finalize()
    throws Throwable
  {
    super.finalize();
    close();
  }
}
