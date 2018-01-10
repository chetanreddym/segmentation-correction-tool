package javax.mail.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.mail.internet.SharedInputStream;





































public class SharedByteArrayInputStream
  extends ByteArrayInputStream
  implements SharedInputStream
{
  protected int start = 0;
  





  public SharedByteArrayInputStream(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }
  








  public SharedByteArrayInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(paramArrayOfByte, paramInt1, paramInt2);
    start = paramInt1;
  }
  





  public long getPosition()
  {
    return pos - start;
  }
  











  public InputStream newStream(long paramLong1, long paramLong2)
  {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("start < 0");
    if (paramLong2 == -1L)
      paramLong2 = count - start;
    return new SharedByteArrayInputStream(buf, start + (int)paramLong1, (int)(paramLong2 - paramLong1));
  }
}
