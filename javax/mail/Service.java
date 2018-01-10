package javax.mail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MailEvent;


































public abstract class Service
{
  protected Session session;
  protected URLName url;
  protected boolean debug = false;
  
  private boolean connected = false;
  

  private Vector connectionListeners;
  
  private EventQueue q;
  

  protected Service(Session paramSession, URLName paramURLName)
  {
    session = paramSession;
    url = paramURLName;
    debug = paramSession.getDebug();
  }
  




















  public void connect()
    throws MessagingException
  {
    connect(null, null, null);
  }
  










































  public void connect(String paramString1, String paramString2, String paramString3)
    throws MessagingException
  {
    connect(paramString1, -1, paramString2, paramString3);
  }
  















  public void connect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    if (isConnected()) {
      throw new MessagingException("already connected");
    }
    
    boolean bool = false;
    int i = 0;
    String str1 = null;
    String str2 = null;
    



    if (url != null) {
      str1 = url.getProtocol();
      if (paramString1 == null)
        paramString1 = url.getHost();
      if (paramInt == -1) {
        paramInt = url.getPort();
      }
      if (paramString2 == null) {
        paramString2 = url.getUsername();
        if (paramString3 == null) {
          paramString3 = url.getPassword();
        }
      } else if ((paramString3 == null) && (paramString2.equals(url.getUsername())))
      {
        paramString3 = url.getPassword();
      }
      
      str2 = url.getFile();
    }
    

    if (str1 != null) {
      if (paramString1 == null)
        paramString1 = session.getProperty("mail." + str1 + ".host");
      if (paramString2 == null) {
        paramString2 = session.getProperty("mail." + str1 + ".user");
      }
    }
    
    if (paramString1 == null) {
      paramString1 = session.getProperty("mail.host");
    }
    if (paramString2 == null) {
      paramString2 = session.getProperty("mail.user");
    }
    
    if (paramString2 == null) {
      try {
        paramString2 = System.getProperty("user.name");
      } catch (SecurityException localSecurityException) {
        if (debug) {
          localSecurityException.printStackTrace();
        }
      }
    }
    PasswordAuthentication localPasswordAuthentication;
    if ((paramString3 == null) && (url != null))
    {
      setURLName(new URLName(str1, paramString1, paramInt, str2, paramString2, paramString3));
      localPasswordAuthentication = session.getPasswordAuthentication(getURLName());
      if (localPasswordAuthentication != null) {
        if (paramString2 == null) {
          paramString2 = localPasswordAuthentication.getUserName();
          paramString3 = localPasswordAuthentication.getPassword();
        } else if (paramString2.equals(localPasswordAuthentication.getUserName())) {
          paramString3 = localPasswordAuthentication.getPassword();
        }
      } else {
        i = 1;
      }
    }
    


    Object localObject = null;
    try {
      bool = protocolConnect(paramString1, paramInt, paramString2, paramString3);
    } catch (AuthenticationFailedException localAuthenticationFailedException) {
      localObject = localAuthenticationFailedException;
    }
    

    if (!bool) {
      InetAddress localInetAddress;
      try {
        localInetAddress = InetAddress.getByName(paramString1);
      } catch (UnknownHostException localUnknownHostException) {
        localInetAddress = null;
      }
      localPasswordAuthentication = session.requestPasswordAuthentication(
        localInetAddress, paramInt, 
        str1, 
        null, paramString2);
      if (localPasswordAuthentication != null) {
        paramString2 = localPasswordAuthentication.getUserName();
        paramString3 = localPasswordAuthentication.getPassword();
        

        bool = protocolConnect(paramString1, paramInt, paramString2, paramString3);
      }
    }
    

    if (!bool) {
      if (localObject != null) {
        throw localObject;
      }
      throw new AuthenticationFailedException();
    }
    
    setURLName(new URLName(str1, paramString1, paramInt, str2, paramString2, paramString3));
    
    if (i != 0) {
      session.setPasswordAuthentication(getURLName(), 
        new PasswordAuthentication(paramString2, paramString3));
    }
    
    setConnected(true);
    

    notifyConnectionListeners(1);
  }
  































  protected boolean protocolConnect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    return false;
  }
  











  public boolean isConnected()
  {
    return connected;
  }
  












  protected void setConnected(boolean paramBoolean)
  {
    connected = paramBoolean;
  }
  

















  public synchronized void close()
    throws MessagingException
  {
    setConnected(false);
    notifyConnectionListeners(3);
  }
  













  public URLName getURLName()
  {
    if ((url != null) && ((url.getPassword() != null) || (url.getFile() != null))) {
      return new URLName(url.getProtocol(), url.getHost(), 
        url.getPort(), null, 
        url.getUsername(), null);
    }
    return url;
  }
  
















  protected void setURLName(URLName paramURLName)
  {
    url = paramURLName;
  }
  








  public synchronized void addConnectionListener(ConnectionListener paramConnectionListener)
  {
    if (connectionListeners == null)
      connectionListeners = new Vector();
    connectionListeners.addElement(paramConnectionListener);
  }
  








  public synchronized void removeConnectionListener(ConnectionListener paramConnectionListener)
  {
    if (connectionListeners != null) {
      connectionListeners.removeElement(paramConnectionListener);
    }
  }
  








  protected void notifyConnectionListeners(int paramInt)
  {
    if (connectionListeners != null) {
      ConnectionEvent localConnectionEvent = new ConnectionEvent(this, paramInt);
      queueEvent(localConnectionEvent, connectionListeners);
    }
    










    if (paramInt == 3) {
      terminateQueue();
    }
  }
  


  public String toString()
  {
    URLName localURLName = getURLName();
    if (localURLName != null) {
      return localURLName.toString();
    }
    return super.toString();
  }
  











  private Object qLock = new Object();
  



  protected void queueEvent(MailEvent paramMailEvent, Vector paramVector)
  {
    synchronized (qLock) {
      if (q == null) {
        q = new EventQueue();
      }
    }
    







    ??? = (Vector)paramVector.clone();
    q.enqueue(paramMailEvent, (Vector)???);
  }
  
  private void terminateQueue()
  {
    synchronized (qLock) {
      if (q != null) {
        Vector localVector = new Vector();
        localVector.setSize(1);
        
        q.enqueue(
          new MailEvent(new Object())
          {
            public void dispatch(Object paramAnonymousObject) {
              Thread.currentThread().interrupt();
            }
            
          }, localVector);
        

        q = null;
      }
    }
  }
  

  protected void finalize()
    throws Throwable
  {
    super.finalize();
    terminateQueue();
  }
}
