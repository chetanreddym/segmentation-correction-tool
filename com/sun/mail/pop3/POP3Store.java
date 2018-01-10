package com.sun.mail.pop3;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;















public class POP3Store
  extends Store
{
  private String name = "pop3";
  private Protocol port;
  private POP3Folder portOwner;
  private String host;
  private int portNum = -1;
  private String user;
  private String passwd;
  boolean rsetBeforeQuit = false;
  Constructor messageConstructor;
  
  public POP3Store(Session paramSession, URLName paramURLName) {
    super(paramSession, paramURLName);
    if (paramURLName != null) {
      name = paramURLName.getProtocol();
    }
    String str = paramSession.getProperty("mail." + name + ".rsetbeforequit");
    if ((str != null) && (str.equalsIgnoreCase("true"))) {
      rsetBeforeQuit = true;
    }
    str = paramSession.getProperty("mail." + name + ".message.class");
    if (str != null) {
      if (paramSession.getDebug())
        System.out.println("DEBUG: POP3 message class: " + str);
      try {
        ClassLoader localClassLoader = getClass().getClassLoader();
        

        Class localClass = null;
        

        try
        {
          localClass = localClassLoader.loadClass(str);

        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          localClass = Class.forName(str);
        }
        
        Class[] arrayOfClass = { Folder.class, Integer.TYPE };
        messageConstructor = localClass.getConstructor(arrayOfClass);
      } catch (Exception localException) {
        if (paramSession.getDebug()) {
          System.out.println(
            "DEBUG: failed to load POP3 message class: " + localException);
        }
      }
    }
  }
  
  protected synchronized boolean protocolConnect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    if ((paramString1 == null) || (paramString3 == null) || (paramString2 == null)) {
      return false;
    }
    

    if (paramInt == -1) {
      String str = session.getProperty("mail." + name + ".port");
      if (str != null) {
        paramInt = Integer.parseInt(str);
      }
    }
    host = paramString1;
    portNum = paramInt;
    user = paramString2;
    passwd = paramString3;
    try {
      port = getPort(null);
    } catch (EOFException localEOFException) {
      throw new AuthenticationFailedException(localEOFException.getMessage());
    } catch (IOException localIOException) {
      throw new MessagingException("Connect failed", localIOException);
    }
    
    return true;
  }
  











  public synchronized boolean isConnected()
  {
    if (!super.isConnected())
    {

      return false; }
    synchronized (this) {
      try {
        if (port == null) {
          port = getPort(null);
        } else
          port.noop();
        return true;
      } catch (IOException localIOException) {
        boolean bool;
        try {
          super.close();
        }
        catch (MessagingException localMessagingException) {}finally
        {
          bool = false; } return bool;
      }
    }
  }
  


  synchronized Protocol getPort(POP3Folder paramPOP3Folder)
    throws IOException
  {
    if ((port != null) && (portOwner == null)) {
      portOwner = paramPOP3Folder;
      return port;
    }
    

    Protocol localProtocol = new Protocol(host, portNum, session.getDebug(), 
      session.getProperties(), "mail." + name);
    
    String str = null;
    if ((str = localProtocol.login(user, passwd)) != null) {
      try {
        localProtocol.quit();
      }
      catch (IOException localIOException) {}finally {
        throw new EOFException(str);
      }
    }
    if (portOwner == null)
      portOwner = paramPOP3Folder;
    return localProtocol;
  }
  
  synchronized void closePort(POP3Folder paramPOP3Folder) {
    if (portOwner == paramPOP3Folder) {
      port = null;
      portOwner = null;
    }
  }
  
  public synchronized void close() throws MessagingException {
    try {
      if (port != null) {
        port.quit();
      }
    } catch (IOException localIOException) {}finally {
      port = null;
      

      super.close();
    }
  }
  
  public Folder getDefaultFolder() throws MessagingException {
    checkConnected();
    return new DefaultFolder(this);
  }
  

  public Folder getFolder(String paramString)
    throws MessagingException
  {
    checkConnected();
    return new POP3Folder(this, paramString);
  }
  
  public Folder getFolder(URLName paramURLName) throws MessagingException {
    checkConnected();
    return new POP3Folder(this, paramURLName.getFile());
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
    
    if (port != null)
      close();
  }
  
  private void checkConnected() throws MessagingException {
    if (!super.isConnected()) {
      throw new MessagingException("Not connected");
    }
  }
}
