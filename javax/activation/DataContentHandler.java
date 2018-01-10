package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface DataContentHandler
{
  public abstract Object getContent(DataSource paramDataSource)
    throws IOException;
  
  public abstract Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws UnsupportedFlavorException, IOException;
  
  public abstract DataFlavor[] getTransferDataFlavors();
  
  public abstract void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException;
}
