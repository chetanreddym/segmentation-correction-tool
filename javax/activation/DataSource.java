package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface DataSource
{
  public abstract String getContentType();
  
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract String getName();
  
  public abstract OutputStream getOutputStream()
    throws IOException;
}
