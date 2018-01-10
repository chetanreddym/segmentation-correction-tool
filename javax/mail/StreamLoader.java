package javax.mail;

import java.io.IOException;
import java.io.InputStream;

abstract interface StreamLoader
{
  public abstract void load(InputStream paramInputStream)
    throws IOException;
}
