package com.sun.mail.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;
















public class SocketFetcher
  implements Runnable
{
  private Socket socket;
  private String host;
  private int port;
  private Properties props;
  private String prefix;
  private int cto;
  private IOException exception;
  private boolean aborted = false;
  private boolean done = false;
  
  private SocketFetcher(String paramString1, int paramInt1, Properties paramProperties, String paramString2, int paramInt2) throws IOException
  {
    host = paramString1;
    port = paramInt1;
    props = paramProperties;
    prefix = paramString2;
    cto = paramInt2;
  }
  





































  public static Socket getSocket(String paramString1, int paramInt, Properties paramProperties, String paramString2)
    throws IOException
  {
    if (paramString2 == null)
      paramString2 = "socket";
    if (paramProperties == null)
      paramProperties = new Properties();
    String str = paramProperties.getProperty(paramString2 + ".connectiontimeout", null);
    int i = -1;
    if (str != null) {
      try {
        i = Integer.parseInt(str);
      } catch (NumberFormatException localNumberFormatException) {}
    }
    if (i > 0) {
      SocketFetcher localSocketFetcher = 
        new SocketFetcher(paramString1, paramInt, paramProperties, paramString2, i);
      try {
        Thread localThread = new Thread(localSocketFetcher, "Connection thread for host " + paramString1);
        localThread.start();

      }
      catch (Exception localException)
      {

        return getSocket0(paramString1, paramInt, paramProperties, paramString2);
      }
      return localSocketFetcher.getSocket();
    }
    return getSocket0(paramString1, paramInt, paramProperties, paramString2);
  }
  





  private static Socket getSocket0(String paramString1, int paramInt, Properties paramProperties, String paramString2)
    throws IOException
  {
    Socket localSocket = null;
    String str1 = 
      paramProperties.getProperty(paramString2 + ".socketFactory.class", null);
    String str2 = paramProperties.getProperty(paramString2 + ".timeout", null);
    
    if ((str1 == null) || (str1.length() <= 0))
    {
      localSocket = new Socket(paramString1, paramInt);
    }
    else {
      String str3 = 
        paramProperties.getProperty(paramString2 + ".socketFactory.fallback", null);
      int j = 
        (str3 != null) && (str3.equalsIgnoreCase("false")) ? 0 : 1;
      String str4 = 
        paramProperties.getProperty(paramString2 + ".socketFactory.port", null);
      int k = -1;
      if (str4 != null) {
        try {
          k = Integer.parseInt(str4);
        }
        catch (NumberFormatException localNumberFormatException1) {}
      }
      try {
        Class localClass = Class.forName(str1);
        
        Method localMethod1 = localClass.getMethod("getDefault", 
          new Class[0]);
        Object localObject = localMethod1.invoke(new Object(), new Object[0]);
        
        Class[] arrayOfClass = { String.class, 
          Integer.TYPE };
        Method localMethod2 = 
          localClass.getMethod("createSocket", arrayOfClass);
        
        Integer localInteger = new Integer(k != -1 ? k : paramInt);
        Object[] arrayOfObject = { paramString1, localInteger };
        localSocket = (Socket)localMethod2.invoke(localObject, arrayOfObject);
      } catch (Exception localException) {
        if (j != 0) {
          localSocket = new Socket(paramString1, paramInt);
        } else {
          throw new IOException("Couldn't connect using \"" + 
            str1 + 
            "\" socket factory to host, port: " + 
            paramString1 + ", " + k + 
            "; Exception: " + localException);
        }
      }
    }
    int i = -1;
    if (str2 != null) {
      try {
        i = Integer.parseInt(str2);
      } catch (NumberFormatException localNumberFormatException2) {}
    }
    if (i >= 0) {
      localSocket.setSoTimeout(i);
    }
    return localSocket;
  }
  



  private synchronized Socket getSocket()
    throws IOException
  {
    if (!done) {
      try {
        long l1 = System.currentTimeMillis();
        long l2 = l1 + cto;
        while (l1 < l2) {
          wait(l2 - l1);
          if (done) {
            break;
          }
          

          l1 = System.currentTimeMillis();
        }
      } catch (InterruptedException localInterruptedException) {
        exception = new InterruptedIOException(localInterruptedException.toString());
      }
    }
    if (exception != null) {
      aborted = true;
      throw exception;
    }
    if (socket == null) {
      aborted = true;
      throw new ConnectException("connection to " + host + " timed out");
    }
    Socket localSocket = socket;
    socket = null;
    return localSocket;
  }
  



  public void run()
  {
    try
    {
      Socket localSocket = getSocket0(host, port, props, prefix);
      synchronized (this) {
        if (aborted)
        {
          try
          {
            localSocket.close();
          } catch (IOException localIOException2) {}
        } else
          socket = localSocket;
        done = true;
        notify();
      }
    } catch (IOException localIOException1) {
      synchronized (this) {
        exception = localIOException1;
        done = true;
        notify();
      }
    }
  }
  



  protected synchronized void finalize()
    throws IOException
  {
    if (socket != null) {
      socket.close();
    }
  }
}
