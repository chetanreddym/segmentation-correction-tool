package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



























































































































































































































































































































































































































































































































































































































































class DataHandlerDataSource
  implements DataSource
{
  DataHandler dataHandler = null;
  


  public DataHandlerDataSource(DataHandler paramDataHandler)
  {
    dataHandler = paramDataHandler;
  }
  


  public InputStream getInputStream()
    throws IOException
  {
    return dataHandler.getInputStream();
  }
  


  public OutputStream getOutputStream()
    throws IOException
  {
    return dataHandler.getOutputStream();
  }
  



  public String getContentType()
  {
    return dataHandler.getContentType();
  }
  



  public String getName()
  {
    return dataHandler.getName();
  }
}
