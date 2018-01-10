package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
















































public class DataHandler
  implements Transferable
{
  private DataSource dataSource = null;
  private DataSource objDataSource = null;
  



  private Object object = null;
  private String objectMimeType = null;
  

  private CommandMap currentCommandMap = null;
  

  private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
  private DataFlavor[] transferFlavors = emptyFlavors;
  

  private DataContentHandler dataContentHandler = null;
  private DataContentHandler factoryDCH = null;
  

  private static DataContentHandlerFactory factory = null;
  private DataContentHandlerFactory oldFactory = null;
  
  private String shortType = null;
  







  public DataHandler(DataSource paramDataSource)
  {
    dataSource = paramDataSource;
    oldFactory = factory;
  }
  








  public DataHandler(Object paramObject, String paramString)
  {
    object = paramObject;
    objectMimeType = paramString;
    oldFactory = factory;
  }
  






  public DataHandler(URL paramURL)
  {
    dataSource = new URLDataSource(paramURL);
    oldFactory = factory;
  }
  


  private synchronized CommandMap getCommandMap()
  {
    if (currentCommandMap != null) {
      return currentCommandMap;
    }
    return CommandMap.getDefaultCommandMap();
  }
  













  public DataSource getDataSource()
  {
    if (dataSource == null)
    {
      if (objDataSource == null)
        objDataSource = new DataHandlerDataSource(this);
      return objDataSource;
    }
    return dataSource;
  }
  







  public String getName()
  {
    if (dataSource != null) {
      return dataSource.getName();
    }
    return null;
  }
  






  public String getContentType()
  {
    if (dataSource != null) {
      return dataSource.getContentType();
    }
    return objectMimeType;
  }
  






















  public InputStream getInputStream()
    throws IOException
  {
    Object localObject = null;
    
    if (dataSource != null) {
      localObject = dataSource.getInputStream();
    } else {
      DataContentHandler localDataContentHandler1 = getDataContentHandler();
      
      if (localDataContentHandler1 == null) {
        throw new UnsupportedDataTypeException(
          "no DCH for MIME type " + getBaseType());
      }
      if (((localDataContentHandler1 instanceof ObjectDataContentHandler)) && 
        (((ObjectDataContentHandler)localDataContentHandler1).getDCH() == null)) {
        throw new UnsupportedDataTypeException(
          "no object DCH for MIME type " + getBaseType());
      }
      
      DataContentHandler localDataContentHandler2 = localDataContentHandler1;
      






      PipedOutputStream localPipedOutputStream = new PipedOutputStream();
      PipedInputStream localPipedInputStream = new PipedInputStream(localPipedOutputStream);
      new Thread(
        new Runnable() { private final PipedOutputStream val$pos;
          private final DataHandler this$0;
          
          public void run() { try { writeTo(this$0.object, this$0.objectMimeType, val$pos);
            }
            catch (IOException localIOException1) {}finally
            {
              try {
                val$pos.close();
              }
              catch (IOException localIOException2) {}
            }
          }
        }, "DataHandler.getInputStream").start();
      localObject = localPipedInputStream;
    }
    
    return localObject;
  }
  







  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    Object localObject;
    





    if (dataSource != null) {
      localObject = null;
      byte[] arrayOfByte = new byte[' '];
      

      localObject = dataSource.getInputStream();
      int i;
      while ((i = ((InputStream)localObject).read(arrayOfByte)) > 0) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
      ((InputStream)localObject).close();
    } else {
      localObject = getDataContentHandler();
      ((DataContentHandler)localObject).writeTo(object, objectMimeType, paramOutputStream);
    }
  }
  










  public OutputStream getOutputStream()
    throws IOException
  {
    if (dataSource != null) {
      return dataSource.getOutputStream();
    }
    return null;
  }
  























  public synchronized DataFlavor[] getTransferDataFlavors()
  {
    if (factory != oldFactory) {
      transferFlavors = emptyFlavors;
    }
    
    if (transferFlavors == emptyFlavors)
      transferFlavors = getDataContentHandler().getTransferDataFlavors();
    return transferFlavors;
  }
  











  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    DataFlavor[] arrayOfDataFlavor = getTransferDataFlavors();
    
    for (int i = 0; i < arrayOfDataFlavor.length; i++) {
      if (arrayOfDataFlavor[i].equals(paramDataFlavor))
        return true;
    }
    return false;
  }
  
































  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    return getDataContentHandler().getTransferData(paramDataFlavor, dataSource);
  }
  











  public synchronized void setCommandMap(CommandMap paramCommandMap)
  {
    if ((paramCommandMap != currentCommandMap) || (paramCommandMap == null))
    {
      transferFlavors = emptyFlavors;
      dataContentHandler = null;
      
      currentCommandMap = paramCommandMap;
    }
  }
  












  public CommandInfo[] getPreferredCommands()
  {
    return getCommandMap().getPreferredCommands(getBaseType());
  }
  











  public CommandInfo[] getAllCommands()
  {
    return getCommandMap().getAllCommands(getBaseType());
  }
  











  public CommandInfo getCommand(String paramString)
  {
    return getCommandMap().getCommand(getBaseType(), paramString);
  }
  















  public Object getContent()
    throws IOException
  {
    return getDataContentHandler().getContent(getDataSource());
  }
  











  public Object getBean(CommandInfo paramCommandInfo)
  {
    Object localObject = null;
    
    try
    {
      localObject = paramCommandInfo.getCommandObject(this, getClass().getClassLoader());
    }
    catch (IOException localIOException) {}catch (ClassNotFoundException localClassNotFoundException) {}
    
    return localObject;
  }
  


















  private synchronized DataContentHandler getDataContentHandler()
  {
    if (factory != oldFactory) {
      oldFactory = factory;
      factoryDCH = null;
      dataContentHandler = null;
      transferFlavors = emptyFlavors;
    }
    
    if (dataContentHandler != null) {
      return dataContentHandler;
    }
    String str = getBaseType();
    
    if ((factoryDCH == null) && (factory != null)) {
      factoryDCH = factory.createDataContentHandler(str);
    }
    if (factoryDCH != null) {
      dataContentHandler = factoryDCH;
    }
    if (dataContentHandler == null) {
      dataContentHandler = 
        getCommandMap().createDataContentHandler(str);
    }
    


    if (dataSource != null) {
      dataContentHandler = new DataSourceDataContentHandler(
        dataContentHandler, 
        dataSource);
    } else
      dataContentHandler = new ObjectDataContentHandler(
        dataContentHandler, 
        object, 
        objectMimeType);
    return dataContentHandler;
  }
  



  private synchronized String getBaseType()
  {
    if (shortType == null) {
      String str = getContentType();
      try {
        MimeType localMimeType = new MimeType(str);
        shortType = localMimeType.getBaseType();
      } catch (MimeTypeParseException localMimeTypeParseException) {
        shortType = str;
      }
    }
    return shortType;
  }
  













  public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory paramDataContentHandlerFactory)
  {
    if (factory != null) {
      throw new Error("DataContentHandlerFactory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();

      }
      catch (SecurityException localSecurityException)
      {
        if (DataHandler.class.getClassLoader() != 
          paramDataContentHandlerFactory.getClass().getClassLoader())
          throw localSecurityException;
      }
    }
    factory = paramDataContentHandlerFactory;
  }
}
