package javax.mail.internet;

import java.io.InputStream;

public interface SharedInputStream
{
  public abstract long getPosition();
  
  public abstract InputStream newStream(long paramLong1, long paramLong2);
}
