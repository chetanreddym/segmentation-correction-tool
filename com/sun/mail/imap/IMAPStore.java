package com.sun.mail.imap;

import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ResponseHandler;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.Namespaces;
import com.sun.mail.imap.protocol.Namespaces.Namespace;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.URLName;










































































public class IMAPStore
  extends Store
  implements ResponseHandler
{
  private String name = "imap";
  private int blksize = 16384;
  


  private int port = 143;
  
  private int statusCacheTimeout = 1000;
  
  private int appendBufferSize = -1;
  

  private String host;
  
  private String user;
  
  private String password;
  
  private Namespaces namespaces;
  
  private boolean debug;
  

  static class ConnectionPool
  {
    private Vector authenticatedConnections = new Vector();
    


    private Vector folders;
    

    private boolean separateStoreConnection = false;
    

    private long borrowedStoreConnections;
    

    private long clientTimeoutInterval = 45000L;
    
    private long lastTimePruned;
    

    ConnectionPool() {}
    
    private int poolSize = 1;
    

    private long pruningInterval = 60000L;
    

    private boolean debug = false;
    
     }
  private ConnectionPool pool = new ConnectionPool();
  




  public IMAPStore(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName);
    
    pool.lastTimePruned = System.currentTimeMillis();
    
    debug = paramSession.getDebug();
    
    String str = paramSession.getProperty("mail." + name + 
      ".connectionpool.debug");
    
    if ((str != null) && (str.equalsIgnoreCase("true"))) {
      pool.debug = true;
    }
    
    if (paramURLName != null) {
      name = paramURLName.getProtocol();
    }
    str = paramSession.getProperty("mail." + name + ".partialfetch");
    
    if ((str != null) && (str.equalsIgnoreCase("false")))
    {
      blksize = -1;

    }
    else if ((str = paramSession.getProperty("mail." + name + ".fetchsize")) != null)
    {
      blksize = Integer.parseInt(str);
    }
    
    str = paramSession.getProperty("mail." + name + ".statuscachetimeout");
    if (str != null)
      statusCacheTimeout = Integer.parseInt(str);
    str = paramSession.getProperty("mail." + name + ".appendbuffersize");
    if (str != null) {
      appendBufferSize = Integer.parseInt(str);
    }
    
    str = paramSession.getProperty("mail." + name + ".connectionpoolsize");
    int i; if (str != null) {
      try {
        i = Integer.parseInt(str);
        if (i > 0) {
          pool.poolSize = i;
        }
      } catch (NumberFormatException localNumberFormatException1) {}
      if (pool.debug) {
        System.out.println("DEBUG: found a pool size property: " + 
          pool.poolSize);
      }
    }
    

    str = paramSession.getProperty("mail." + name + ".connectionpooltimeout");
    if (str != null) {
      try {
        i = Integer.parseInt(str);
        if (i > 0) {
          pool.clientTimeoutInterval = i;
        }
      } catch (NumberFormatException localNumberFormatException2) {}
      if (pool.debug) {
        System.out.println("DEBUG: found a timeout property: " + 
          pool.clientTimeoutInterval);
      }
    }
    

    str = paramSession.getProperty("mail." + name + ".separatestoreconnection");
    if ((str != null) && (str.equalsIgnoreCase("true"))) {
      if (pool.debug)
        System.out.println("DEBUG: dedicate a store connection");
      pool.separateStoreConnection = true;
    }
  }
  




  protected synchronized boolean protocolConnect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    IMAPProtocol localIMAPProtocol = null;
    

    if ((paramString1 == null) || (paramString3 == null) || (paramString2 == null)) {
      return false;
    }
    
    if (paramInt != -1) {
      port = paramInt;
    } else {
      String str = session.getProperty("mail." + name + ".port");
      if (str != null) {
        port = Integer.parseInt(str);
      }
    }
    

    if (port == -1) {
      port = 143;
    }
    try
    {
      boolean bool;
      synchronized (pool) {
        bool = pool.authenticatedConnections.isEmpty();
      }
      
      if (bool) {
        localIMAPProtocol = new IMAPProtocol(name, paramString1, port, 
          session.getDebug(), 
          session.getProperties());
        
        login(localIMAPProtocol, paramString2, paramString3);
        
        localIMAPProtocol.addResponseHandler(this);
        
        host = paramString1;
        user = paramString2;
        password = paramString3;
        
        synchronized (pool) {
          pool.authenticatedConnections.addElement(localIMAPProtocol);
        }
      }
    }
    catch (CommandFailedException localCommandFailedException) {
      localIMAPProtocol.disconnect();
      localIMAPProtocol = null;
      throw new AuthenticationFailedException(
        localCommandFailedException.getResponse().getRest());
    } catch (ProtocolException localProtocolException) {
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
    } catch (IOException localIOException) {
      throw new MessagingException(localIOException.getMessage(), localIOException);
    }
    
    return true;
  }
  
  private void login(IMAPProtocol paramIMAPProtocol, String paramString1, String paramString2)
    throws ProtocolException
  {
    if (paramIMAPProtocol.isAuthenticated())
      return;
    if ((paramIMAPProtocol.hasCapability("AUTH-LOGIN")) || 
      (paramIMAPProtocol.hasCapability("AUTH=LOGIN"))) {
      paramIMAPProtocol.authlogin(paramString1, paramString2);
    } else {
      paramIMAPProtocol.login(paramString1, paramString2);
    }
  }
  



  IMAPProtocol getProtocol(IMAPFolder paramIMAPFolder)
    throws MessagingException
  {
    IMAPProtocol localIMAPProtocol = null;
    







    synchronized (pool)
    {


      if ((pool.authenticatedConnections.isEmpty()) || 
        ((pool.separateStoreConnection) && 
        (pool.authenticatedConnections.size() == 1)) || (
        (pool.borrowedStoreConnections > 0L) && 
        (pool.authenticatedConnections.size() == 1)))
      {
        if (debug) {
          System.out.println("DEBUG: no connections in the pool, creating a new one");
        }
        try
        {
          localIMAPProtocol = new IMAPProtocol(name, host, port, 
            session.getDebug(), 
            session.getProperties());
          

          login(localIMAPProtocol, user, password);
        } catch (Exception localException1) {
          if (localIMAPProtocol != null)
            try {
              localIMAPProtocol.disconnect();
            } catch (Exception localException2) {}
          localIMAPProtocol = null;
        }
        
        if (localIMAPProtocol == null)
          throw new MessagingException("connection failure");
      } else {
        if (debug) {
          System.out.println("DEBUG: connection available -- size: " + 
            pool.authenticatedConnections.size());
        }
        

        localIMAPProtocol = (IMAPProtocol)pool.authenticatedConnections.lastElement();
        localIMAPProtocol.removeResponseHandler(this);
        pool.authenticatedConnections.removeElement(localIMAPProtocol);
      }
      

      timeoutConnections();
      

      if (paramIMAPFolder != null) {
        if (pool.folders == null)
          pool.folders = new Vector();
        pool.folders.addElement(paramIMAPFolder);
      }
    }
    
    return localIMAPProtocol;
  }
  














  IMAPProtocol getStoreProtocol()
    throws ProtocolException
  {
    IMAPProtocol localIMAPProtocol1 = null;
    
    synchronized (pool)
    {


      if (pool.authenticatedConnections.isEmpty()) {
        if (pool.debug) {
          System.out.println(
            "DEBUG: getStoreProtocol() - no connections in the pool, creating a new one");
        }
        try
        {
          localIMAPProtocol1 = new IMAPProtocol(name, host, port, 
            session.getDebug(), 
            session.getProperties());
          

          login(localIMAPProtocol1, user, password);
        } catch (Exception localException1) {
          if (localIMAPProtocol1 != null)
            try {
              localIMAPProtocol1.logout();
            } catch (Exception localException2) {}
          localIMAPProtocol1 = null;
        }
        
        if (localIMAPProtocol1 == null) {
          throw new ProtocolException("connection failure");
        }
        localIMAPProtocol1.addResponseHandler(this);
        pool.authenticatedConnections.addElement(localIMAPProtocol1);
      }
      else
      {
        if (pool.debug)
          System.out.println(
            "DEBUG: getStoreProtocol() - connection available -- size: " + 
            
            pool.authenticatedConnections.size());
        localIMAPProtocol1 = (IMAPProtocol)pool.authenticatedConnections.firstElement();
      }
      


      if (!pool.separateStoreConnection) {
        pool.borrowedStoreConnections += 1L;
        
        if (pool.debug) {
          System.out.println("DEBUG: getStoreProtocol() -- borrowedStoreConnections: " + 
          
            pool.borrowedStoreConnections);
        }
      }
      timeoutConnections();
      
      IMAPProtocol localIMAPProtocol2 = localIMAPProtocol1;return localIMAPProtocol2;
    }
  }
  



  boolean hasSeparateStoreConnection()
  {
    return pool.separateStoreConnection;
  }
  


  boolean getConnectionPoolDebug()
  {
    return pool.debug;
  }
  



  boolean isConnectionPoolFull()
  {
    synchronized (pool) {
      if (pool.debug) {
        System.out.println("DEBUG: current size: " + 
          pool.authenticatedConnections.size() + 
          "   pool size: " + pool.poolSize);
      }
      boolean bool = pool.authenticatedConnections.size() >= pool.poolSize;return bool;
    }
  }
  




  void releaseProtocol(IMAPFolder paramIMAPFolder, IMAPProtocol paramIMAPProtocol)
  {
    synchronized (pool) {
      if (paramIMAPProtocol != null)
      {

        if (!isConnectionPoolFull()) {
          paramIMAPProtocol.addResponseHandler(this);
          pool.authenticatedConnections.addElement(paramIMAPProtocol);
          
          if (debug)
            System.out.println("DEBUG: added an Authenticated connection -- size: " + 
            
              pool.authenticatedConnections.size());
        } else {
          if (debug) {
            System.out.println("DEBUG: pool is full, not adding an Authenticated connection");
          }
          try {
            paramIMAPProtocol.logout();
          }
          catch (ProtocolException localProtocolException) {}
        }
      }
      if (pool.folders != null) {
        pool.folders.removeElement(paramIMAPFolder);
      }
      timeoutConnections();
    }
  }
  



  void releaseStoreProtocol(IMAPProtocol paramIMAPProtocol)
  {
    synchronized (pool)
    {

      if (!pool.separateStoreConnection) {
        pool.borrowedStoreConnections -= 1L;
        
        if (pool.debug) {
          System.out.println("DEBUG: releaseStoreProtocol() -- borrowedStoreConnections: " + 
          
            pool.borrowedStoreConnections);
        }
      }
      timeoutConnections();
    }
  }
  



  private void emptyConnectionPool()
  {
    synchronized (pool) {
      for (int i = pool.authenticatedConnections.size() - 1; 
          i >= 0; i--) {
        try
        {
          ((IMAPProtocol)pool.authenticatedConnections.elementAt(i)).removeResponseHandler(this);
          ((IMAPProtocol)pool.authenticatedConnections
            .elementAt(i)).logout();
        }
        catch (ProtocolException localProtocolException) {}
      }
      pool.authenticatedConnections.removeAllElements();
    }
    
    if (pool.debug) {
      System.out.println("DEBUG: removed all authenticated connections");
    }
  }
  


  private void timeoutConnections()
  {
    synchronized (pool)
    {


      if (System.currentTimeMillis() - pool.lastTimePruned > 
        pool.pruningInterval) {
        if (pool.authenticatedConnections.size() > 1)
        {
          if (pool.debug) {
            System.out.println("DEBUG: checking for connections to prune: " + (
            
              System.currentTimeMillis() - pool.lastTimePruned));
            System.out.println("DEBUG: clientTimeoutInterval: " + 
              pool.clientTimeoutInterval);
          }
          





          for (int i = pool.authenticatedConnections.size() - 1; 
              i > 0; i--) {
            IMAPProtocol localIMAPProtocol = (IMAPProtocol)pool.authenticatedConnections
              .elementAt(i);
            if (pool.debug) {
              System.out.println("DEBUG: protocol last used: " + (
                System.currentTimeMillis() - localIMAPProtocol.getTimestamp()));
            }
            if (System.currentTimeMillis() - localIMAPProtocol.getTimestamp() > 
              pool.clientTimeoutInterval)
            {
              if (pool.debug) {
                System.out.println("DEBUG: authenticated connection timed out");
                
                System.out.println("DEBUG: logging out the connection");
              }
              

              localIMAPProtocol.removeResponseHandler(this);
              pool.authenticatedConnections.removeElementAt(i);
              try
              {
                localIMAPProtocol.logout();
              } catch (ProtocolException localProtocolException) {}
            }
          }
          pool.lastTimePruned = System.currentTimeMillis();
        }
      }
    }
  }
  

  int getFetchBlockSize()
  {
    return blksize;
  }
  


  Session getSession()
  {
    return session;
  }
  


  int getStatusCacheTimeout()
  {
    return statusCacheTimeout;
  }
  


  int getAppendBufferSize()
  {
    return appendBufferSize;
  }
  



  public synchronized boolean isConnected()
  {
    if (!super.isConnected())
    {

      return false;
    }
    















    IMAPProtocol localIMAPProtocol = null;
    try {
      localIMAPProtocol = getStoreProtocol();
      localIMAPProtocol.noop();
    }
    catch (ProtocolException localProtocolException) {}finally {
      releaseStoreProtocol(localIMAPProtocol);
    }
    

    return super.isConnected();
  }
  

  public void close()
    throws MessagingException
  {
    if (!super.isConnected()) {
      return;
    }
    IMAPProtocol localIMAPProtocol = null;
    try {
      localIMAPProtocol = getStoreProtocol();
      












      localIMAPProtocol.logout();
    }
    catch (ProtocolException localProtocolException) {
      cleanup();
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
    } finally {
      releaseStoreProtocol(localIMAPProtocol);
    }
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
    close();
  }
  

  private synchronized void cleanup()
  {
    Vector localVector = null;
    int i = 1;
    








    for (;;)
    {
      synchronized (pool) {
        if (pool.folders != null) {
          i = 0;
          localVector = pool.folders;
          pool.folders = null;
        } else {
          i = 1;
        }
      }
      if (i != 0) {
        break;
      }
      
      int j = 0; for (int k = localVector.size(); j < k; j++) {
        IMAPFolder localIMAPFolder = (IMAPFolder)localVector.elementAt(j);
        try
        {
          localIMAPFolder.close(false);
        }
        catch (MessagingException localMessagingException1) {}
      }
    }
    


    synchronized (pool) {
      emptyConnectionPool();
    }
    
    try
    {
      super.close();
    }
    catch (MessagingException localMessagingException2) {}
  }
  

  public Folder getDefaultFolder()
    throws MessagingException
  {
    checkConnected();
    return new DefaultFolder(this);
  }
  

  public Folder getFolder(String paramString)
    throws MessagingException
  {
    checkConnected();
    return new IMAPFolder(paramString, 65535, this);
  }
  

  public Folder getFolder(URLName paramURLName)
    throws MessagingException
  {
    checkConnected();
    return new IMAPFolder(paramURLName.getFile(), 
      65535, 
      this);
  }
  


  public Folder[] getPersonalNamespaces()
    throws MessagingException
  {
    Namespaces localNamespaces = getNamespaces();
    if ((localNamespaces == null) || (personal == null))
      return super.getPersonalNamespaces();
    return namespaceToFolders(personal, null);
  }
  



  public Folder[] getUserNamespaces(String paramString)
    throws MessagingException
  {
    Namespaces localNamespaces = getNamespaces();
    if ((localNamespaces == null) || (otherUsers == null))
      return super.getUserNamespaces(paramString);
    return namespaceToFolders(otherUsers, paramString);
  }
  


  public Folder[] getSharedNamespaces()
    throws MessagingException
  {
    Namespaces localNamespaces = getNamespaces();
    if ((localNamespaces == null) || (shared == null))
      return super.getSharedNamespaces();
    return namespaceToFolders(shared, null);
  }
  
  private synchronized Namespaces getNamespaces() throws MessagingException {
    checkConnected();
    
    IMAPProtocol localIMAPProtocol = null;
    
    if (namespaces == null) {
      try {
        localIMAPProtocol = getStoreProtocol();
        namespaces = localIMAPProtocol.namespace();
      }
      catch (BadCommandException localBadCommandException) {}catch (ProtocolException localProtocolException)
      {
        throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
      } finally {
        releaseStoreProtocol(localIMAPProtocol);
      }
    }
    return namespaces;
  }
  
  private Folder[] namespaceToFolders(Namespaces.Namespace[] paramArrayOfNamespace, String paramString)
  {
    Folder[] arrayOfFolder = new Folder[paramArrayOfNamespace.length];
    for (int i = 0; i < arrayOfFolder.length; i++) {
      String str = prefix;
      if (paramString == null)
      {
        int j = str.length();
        if ((j > 0) && (str.charAt(j - 1) == delimiter)) {
          str = str.substring(0, j - 1);
        }
      } else {
        str = str + paramString;
      }
      arrayOfFolder[i] = new IMAPFolder(str, delimiter, this);
    }
    return arrayOfFolder;
  }
  














  public Quota[] getQuota(String paramString)
    throws MessagingException
  {
    Quota[] arrayOfQuota = null;
    
    IMAPProtocol localIMAPProtocol = null;
    try {
      localIMAPProtocol = getStoreProtocol();
      arrayOfQuota = localIMAPProtocol.getQuotaRoot(paramString);
    } catch (BadCommandException localBadCommandException) {
      throw new MessagingException("QUOTA not supported", localBadCommandException);
    } catch (ConnectionException localConnectionException) {
      throw new StoreClosedException(this, localConnectionException.getMessage());
    } catch (ProtocolException localProtocolException) {
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
    } finally {
      releaseStoreProtocol(localIMAPProtocol);
    }
    return arrayOfQuota;
  }
  







  public void setQuota(Quota paramQuota)
    throws MessagingException
  {
    IMAPProtocol localIMAPProtocol = null;
    try {
      localIMAPProtocol = getStoreProtocol();
      localIMAPProtocol.setQuota(paramQuota);
    } catch (BadCommandException localBadCommandException) {
      throw new MessagingException("QUOTA not supported", localBadCommandException);
    } catch (ConnectionException localConnectionException) {
      throw new StoreClosedException(this, localConnectionException.getMessage());
    } catch (ProtocolException localProtocolException) {
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);
    } finally {
      releaseStoreProtocol(localIMAPProtocol);
    }
  }
  
  private void checkConnected() {
    if (!super.isConnected()) {
      throw new IllegalStateException("Not connected");
    }
  }
  

  public void handleResponse(Response paramResponse)
  {
    if (paramResponse.isBYE())
    {
      if (super.isConnected())
        cleanup();
      return;
    }
    if (paramResponse.isOK())
    {
      String str = paramResponse.toString();
      if (str.indexOf("ALERT") != -1) {
        notifyStoreListeners(1, str);
      } else {
        notifyStoreListeners(2, str);
      }
    }
  }
}
