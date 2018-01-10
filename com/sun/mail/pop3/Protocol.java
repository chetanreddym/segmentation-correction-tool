package com.sun.mail.pop3;

import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;




















class Protocol
{
  private Socket socket;
  private DataInputStream input;
  private PrintStream output;
  private static final int POP3_PORT = 110;
  private static final String CRLF = "\r\n";
  private boolean debug = false;
  



  Protocol(String paramString1, int paramInt, boolean paramBoolean, Properties paramProperties, String paramString2)
    throws IOException
  {
    debug = paramBoolean;
    Response localResponse;
    try {
      if (paramInt == -1)
        paramInt = 110;
      if (paramBoolean) {
        System.out.println("POP3: connecting to host \"" + paramString1 + 
          "\", port " + paramInt);
      }
      socket = SocketFetcher.getSocket(paramString1, paramInt, paramProperties, paramString2);
      
      input = new DataInputStream(
        new BufferedInputStream(socket.getInputStream()));
      output = new PrintStream(
        new BufferedOutputStream(socket.getOutputStream()), true);
      
      localResponse = simpleCommand(null);
    } catch (IOException localIOException) {
      try {
        socket.close();
      } finally {
        throw localIOException;
      }
    }
    
    if (!ok) {
      try {
        socket.close();
      } finally {
        throw new IOException("Connect failed");
      }
    }
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
    if (socket != null) {
      quit();
    }
  }
  



  synchronized String login(String paramString1, String paramString2)
    throws IOException
  {
    Response localResponse = simpleCommand("USER " + paramString1);
    if (!ok)
      return data;
    localResponse = simpleCommand("PASS " + paramString2);
    if (!ok)
      return data;
    return null;
  }
  


  synchronized boolean quit()
    throws IOException
  {
    boolean bool = false;
    try {
      Response localResponse = simpleCommand("QUIT");
      bool = ok;
    } finally {
      try {
        socket.close();
      } finally {
        socket = null;
        input = null;
        output = null;
      }
    }
    return bool;
  }
  


  synchronized Status stat()
    throws IOException
  {
    Response localResponse = simpleCommand("STAT");
    Status localStatus = new Status();
    if ((ok) && (data != null)) {
      try {
        StringTokenizer localStringTokenizer = new StringTokenizer(data);
        total = Integer.parseInt(localStringTokenizer.nextToken());
        size = Integer.parseInt(localStringTokenizer.nextToken());
      }
      catch (Exception localException) {}
    }
    return localStatus;
  }
  

  synchronized int list(int paramInt)
    throws IOException
  {
    Response localResponse = simpleCommand("LIST " + paramInt);
    int i = -1;
    if ((ok) && (data != null)) {
      try {
        StringTokenizer localStringTokenizer = new StringTokenizer(data);
        localStringTokenizer.nextToken();
        i = Integer.parseInt(localStringTokenizer.nextToken());
      }
      catch (Exception localException) {}
    }
    return i;
  }
  




  synchronized InputStream retr(int paramInt1, int paramInt2)
    throws IOException
  {
    Response localResponse = multilineCommand("RETR " + paramInt1, paramInt2);
    return bytes;
  }
  

  synchronized InputStream top(int paramInt1, int paramInt2)
    throws IOException
  {
    Response localResponse = multilineCommand("TOP " + paramInt1 + " " + paramInt2, 0);
    return bytes;
  }
  

  synchronized boolean dele(int paramInt)
    throws IOException
  {
    Response localResponse = simpleCommand("DELE " + paramInt);
    return ok;
  }
  

  synchronized String uidl(int paramInt)
    throws IOException
  {
    Response localResponse = simpleCommand("UIDL " + paramInt);
    if (!ok)
      return null;
    int i = data.indexOf(' ');
    if (i > 0) {
      return data.substring(i + 1);
    }
    return null;
  }
  


  synchronized boolean uidl(String[] paramArrayOfString)
    throws IOException
  {
    Response localResponse = multilineCommand("UIDL", 15 * paramArrayOfString.length);
    if (!ok)
      return false;
    LineInputStream localLineInputStream = new LineInputStream(bytes);
    String str = null;
    while ((str = localLineInputStream.readLine()) != null) {
      int i = str.indexOf(' ');
      if ((i >= 1) && (i < str.length()))
      {
        int j = Integer.parseInt(str.substring(0, i));
        if ((j > 0) && (j <= paramArrayOfString.length))
          paramArrayOfString[(j - 1)] = str.substring(i + 1);
      } }
    return true;
  }
  

  synchronized boolean noop()
    throws IOException
  {
    Response localResponse = simpleCommand("NOOP");
    return ok;
  }
  

  synchronized boolean rset()
    throws IOException
  {
    Response localResponse = simpleCommand("RSET");
    return ok;
  }
  

  private Response simpleCommand(String paramString)
    throws IOException
  {
    if (socket == null)
      throw new IOException("Folder is closed");
    if (paramString != null) {
      if (debug)
        System.out.println("C: " + paramString);
      paramString = paramString + "\r\n";
      output.print(paramString);
    }
    String str = input.readLine();
    if (str == null) {
      if (debug)
        System.out.println("S: EOF");
      throw new EOFException("EOF on socket");
    }
    if (debug)
      System.out.println("S: " + str);
    Response localResponse = new Response();
    if (str.startsWith("+OK")) {
      ok = true;
    } else if (str.startsWith("-ERR")) {
      ok = false;
    } else
      throw new IOException("Unexpected response: " + str);
    int i;
    if ((i = str.indexOf(' ')) >= 0)
      data = str.substring(i + 1);
    return localResponse;
  }
  


  private Response multilineCommand(String paramString, int paramInt)
    throws IOException
  {
    Response localResponse = simpleCommand(paramString);
    if (!ok) {
      return localResponse;
    }
    SharedByteArrayOutputStream localSharedByteArrayOutputStream = new SharedByteArrayOutputStream(paramInt);
    int j = 10;
    int i; while ((i = input.read()) >= 0) {
      if ((j == 10) && (i == 46)) {
        i = input.read();
        if (i == 13)
        {
          input.read();
          break;
        }
      }
      localSharedByteArrayOutputStream.write(i);
      if (debug)
        System.out.write(i);
      j = i;
    }
    if (i < 0)
      throw new EOFException("EOF on socket");
    bytes = localSharedByteArrayOutputStream.toStream();
    return localResponse;
  }
}
