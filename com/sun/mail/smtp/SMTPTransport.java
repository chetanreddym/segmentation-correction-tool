package com.sun.mail.smtp;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;



public class SMTPTransport
  extends Transport
{
  private MimeMessage message;
  private Address[] addresses;
  private Address[] validSentAddr;
  private Address[] validUnsentAddr;
  private Address[] invalidAddr;
  boolean sendPartiallyFailed = false;
  

  MessagingException exception;
  
  private Hashtable extMap;
  
  private boolean noAuth = false;
  

  private String name = "smtp";
  

  private static String localHostName;
  
  private static final String[] ignoreList = { "Bcc", "Content-Length" };
  private static final byte[] CRLF = { 13, 10 };
  private BufferedInputStream serverInput;
  private LineInputStream lineInputStream;
  
  public SMTPTransport(Session paramSession, URLName paramURLName) { super(paramSession, paramURLName);
    
    if (paramURLName != null) {
      name = paramURLName.getProtocol();
    }
  }
  



  private String getLocalHost()
  {
    try
    {
      if ((localHostName == null) || (localHostName.length() <= 0))
        localHostName = 
          session.getProperty("mail." + name + ".localhost");
      if ((localHostName == null) || (localHostName.length() <= 0)) {
        localHostName = InetAddress.getLocalHost().getHostName();
      }
    } catch (UnknownHostException localUnknownHostException) {}
    return localHostName;
  }
  
  public synchronized void connect() throws MessagingException {
    try {
      noAuth = true;
      super.connect();
    } finally {
      noAuth = false;
    }
  }
  


















  protected boolean protocolConnect(String paramString1, int paramInt, String paramString2, String paramString3)
    throws MessagingException
  {
    String str1 = session.getProperty("mail." + name + ".ehlo");
    boolean bool1 = (str1 == null) || (!str1.equalsIgnoreCase("false"));
    
    String str2 = session.getProperty("mail." + name + ".auth");
    boolean bool2 = (str2 != null) && (str2.equalsIgnoreCase("true"));
    if (debug) {
      System.out.println("DEBUG SMTP: useEhlo " + bool1 + 
        ", useAuth " + bool2);
    }
    





    if ((bool2) && ((paramString2 == null) || (paramString3 == null))) {
      return false;
    }
    



    if (paramInt == -1) {
      String str3 = session.getProperty("mail." + name + ".port");
      if (str3 != null) {
        paramInt = Integer.parseInt(str3);
      } else {
        paramInt = 25;
      }
    }
    
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      paramString1 = "localhost";
    }
    boolean bool3 = false;
    
    openServer(paramString1, paramInt);
    
    if (bool1) {
      bool3 = ehlo(getLocalHost());
    }
    if ((bool3) && (bool2) && (
      (supportsExtension("AUTH")) || (supportsExtension("AUTH=LOGIN")))) {
      if (debug) {
        System.out.println("DEBUG SMTP: Attempt to authenticate");
        if ((!supportsAuthentication("LOGIN")) && 
          (supportsExtension("AUTH=LOGIN")))
          System.out.println("DEBUG SMTP use AUTH=LOGIN hack"); }
      int i;
      ByteArrayOutputStream localByteArrayOutputStream;
      BASE64EncoderStream localBASE64EncoderStream; if ((supportsAuthentication("LOGIN")) || 
        (supportsExtension("AUTH=LOGIN")))
      {
        i = simpleCommand("AUTH LOGIN");
        








        try
        {
          localByteArrayOutputStream = new ByteArrayOutputStream();
          localBASE64EncoderStream = 
            new BASE64EncoderStream(localByteArrayOutputStream, Integer.MAX_VALUE);
          
          if (i == 334)
          {
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString2));
            localBASE64EncoderStream.flush();
            

            i = simpleCommand(localByteArrayOutputStream.toByteArray());
            localByteArrayOutputStream.reset();
          }
          if (i == 334)
          {
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString3));
            localBASE64EncoderStream.flush();
            

            i = simpleCommand(localByteArrayOutputStream.toByteArray());
            localByteArrayOutputStream.reset();
          }
        }
        catch (IOException localIOException1) {}finally {
          if (i != 235) {
            closeConnection();
            return false;
          }
        } }
      if (supportsAuthentication("PLAIN"))
      {
        i = simpleCommand("AUTH PLAIN");
        try {
          localByteArrayOutputStream = new ByteArrayOutputStream();
          localBASE64EncoderStream = 
            new BASE64EncoderStream(localByteArrayOutputStream, Integer.MAX_VALUE);
          if (i == 334)
          {

            localBASE64EncoderStream.write(0);
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString2));
            localBASE64EncoderStream.write(0);
            localBASE64EncoderStream.write(ASCIIUtility.getBytes(paramString3));
            localBASE64EncoderStream.flush();
            

            i = simpleCommand(localByteArrayOutputStream.toByteArray());
          }
        }
        catch (IOException localIOException2) {}finally {
          if (i != 235) {
            closeConnection();
            return false;
          }
        }
      }
    }
    if (!bool3) {
      helo(getLocalHost());
    }
    
    return true;
  }
  








  private OutputStream serverOutput;
  







  private String lastServerResponse;
  







  private Socket serverSocket;
  






  public synchronized void sendMessage(Message paramMessage, Address[] paramArrayOfAddress)
    throws MessagingException, SendFailedException
  {
    checkConnected();
    


    if (!(paramMessage instanceof MimeMessage)) {
      if (debug)
        System.out.println("DEBUG SMTP: Can only send RFC822 msgs");
      throw new MessagingException("SMTP can only send RFC822 messages");
    }
    for (int i = 0; i < paramArrayOfAddress.length; i++) {
      if (!(paramArrayOfAddress[i] instanceof InternetAddress)) {
        throw new MessagingException(paramArrayOfAddress[i] + 
          " is not an InternetAddress");
      }
    }
    
    message = ((MimeMessage)paramMessage);
    addresses = paramArrayOfAddress;
    
    boolean bool = false;
    if ((paramMessage instanceof SMTPMessage))
      bool = ((SMTPMessage)paramMessage).getAllow8bitMIME();
    if (!bool) {
      String str = 
        session.getProperty("mail." + name + ".allow8bitmime");
      bool = (str != null) && (str.equalsIgnoreCase("true"));
    }
    if (debug)
      System.out.println("DEBUG SMTP: use8bit " + bool);
    if ((bool) && (supportsExtension("8BITMIME"))) {
      convertTo8Bit(message);
    }
    try {
      mailFrom();
      rcptTo();
      message.writeTo(data(), ignoreList);
      finishData();
      if (sendPartiallyFailed)
      {

        if (debug) {
          System.out.println(
            "DEBUG SMTPTransport: Sending partially failed because of invalid destination addresses");
        }
        notifyTransportListeners(
          3, 
          validSentAddr, validUnsentAddr, invalidAddr, 
          message);
        
        throw new SendFailedException("Message partially delivered", 
          exception, validSentAddr, validUnsentAddr, invalidAddr);
      }
      notifyTransportListeners(1, 
        validSentAddr, validUnsentAddr, 
        invalidAddr, message);
    } catch (IOException localIOException) {
      if (debug) {
        localIOException.printStackTrace();
      }
      try
      {
        closeConnection();
      } catch (MessagingException localMessagingException) {}
      notifyTransportListeners(2, 
        validSentAddr, validUnsentAddr, 
        invalidAddr, message);
      
      throw new MessagingException("IOException while sending message", 
        localIOException);
    }
    finally {
      validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
      addresses = null;
      message = null;
      exception = null;
      sendPartiallyFailed = false;
    }
  }
  
  public synchronized void close() throws MessagingException
  {
    if (!super.isConnected())
      return;
    try {
      if (serverSocket != null)
        sendCommand("QUIT");
    } finally {
      closeConnection();
    }
  }
  
  private void closeConnection() throws MessagingException {
    try {
      if (serverSocket != null)
        serverSocket.close();
    } catch (IOException localIOException) {
      throw new MessagingException("Server Close Failed", localIOException);
    } finally {
      serverSocket = null;
      serverOutput = null;
      serverInput = null;
      lineInputStream = null;
      if (super.isConnected()) {
        super.close();
      }
    }
  }
  


  public synchronized boolean isConnected()
  {
    if (!super.isConnected())
    {
      return false;
    }
    try {
      sendCommand("NOOP");
      int i = readServerResponse();
      






      if (i >= 0) {
        return true;
      }
      try {
        closeConnection();
      } catch (MessagingException localMessagingException1) {}
      return false;
    }
    catch (Exception localException) {
      try {
        closeConnection();
      } catch (MessagingException localMessagingException2) {} }
    return false;
  }
  


  private void convertTo8Bit(MimePart paramMimePart)
  {
    label116:
    

    try
    {
      Object localObject;
      
      if (paramMimePart.isMimeType("text/*")) {
        localObject = paramMimePart.getEncoding();
        if ((((String)localObject).equalsIgnoreCase("quoted-printable")) || 
          (((String)localObject).equalsIgnoreCase("base64"))) {
          InputStream localInputStream = paramMimePart.getInputStream();
          if (!is8Bit(localInputStream)) break label116;
          paramMimePart.setHeader("Content-Transfer-Encoding", "8bit");
        }
      } else { if (!paramMimePart.isMimeType("multipart/*")) return;
        localObject = (MimeMultipart)paramMimePart.getContent();
        int i = ((MimeMultipart)localObject).getCount();
        for (int j = 0; j < i; j++) {
          convertTo8Bit((MimePart)((MimeMultipart)localObject).getBodyPart(j));
        }
      }
    }
    catch (IOException localIOException) {}catch (MessagingException localMessagingException) {}
  }
  









  private boolean is8Bit(InputStream paramInputStream)
  {
    int j = 0;
    boolean bool = false;
    try { int i;
      while ((i = paramInputStream.read()) >= 0) {
        i &= 0xFF;
        if ((i == 13) || (i == 10)) {
          j = 0;
        } else { if (i == 0) {
            return false;
          }
          j++;
          if (j > 998)
            return false;
        }
        if (i > 127)
          bool = true;
      }
    } catch (IOException localIOException) {
      return false;
    }
    if ((debug) && (bool))
      System.out.println("DEBUG SMTP: found an 8bit part");
    return bool;
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
    try {
      closeConnection();
    }
    catch (MessagingException localMessagingException) {}
  }
  






  private void helo(String paramString)
    throws MessagingException
  {
    if (paramString != null) {
      issueCommand("HELO " + paramString, 250);
    } else
      issueCommand("HELO", 250);
  }
  
  private boolean ehlo(String paramString) throws MessagingException {
    String str1;
    if (paramString != null) {
      str1 = "EHLO " + paramString;
    } else
      str1 = "EHLO";
    sendCommand(str1);
    int i = readServerResponse();
    if (i == 250)
    {
      BufferedReader localBufferedReader = 
        new BufferedReader(new StringReader(lastServerResponse));
      
      extMap = new Hashtable();
      try {
        int j = 1;
        String str2; while ((str2 = localBufferedReader.readLine()) != null)
          if (j != 0) {
            j = 0;

          }
          else if (str2.length() >= 5)
          {
            str2 = str2.substring(4);
            int k = str2.indexOf(' ');
            String str3 = "";
            if (k > 0) {
              str3 = str2.substring(k + 1);
              str2 = str2.substring(0, k);
            }
            if (debug)
              System.out.println("DEBUG SMTP Found extension \"" + 
                str2 + "\", arg \"" + str3 + "\"");
            extMap.put(str2.toUpperCase(), str3);
          }
      } catch (IOException localIOException) {}
    }
    return i == 250;
  }
  





  private void mailFrom()
    throws MessagingException
  {
    String str = null;
    if ((message instanceof SMTPMessage))
      str = ((SMTPMessage)message).getEnvelopeFrom();
    if ((str == null) || (str.length() <= 0))
      str = session.getProperty("mail." + name + ".from");
    Object localObject2; if ((str == null) || (str.length() <= 0))
    {

      if ((message != null) && ((localObject1 = message.getFrom()) != null) && 
        (localObject1.length > 0)) {
        localObject2 = localObject1[0];
      } else {
        localObject2 = InternetAddress.getLocalAddress(session);
      }
      if (localObject2 != null) {
        str = ((InternetAddress)localObject2).getAddress();
      } else {
        throw new MessagingException(
          "can't determine local email address");
      }
    }
    Object localObject1 = "MAIL FROM:" + normalizeAddress(str);
    

    if (supportsExtension("DSN")) {
      localObject2 = null;
      if ((message instanceof SMTPMessage))
        localObject2 = ((SMTPMessage)message).getDSNRet();
      if (localObject2 == null) {
        localObject2 = session.getProperty("mail." + name + ".dsn.ret");
      }
      if (localObject2 != null) {
        localObject1 = localObject1 + " RET=" + (String)localObject2;
      }
    }
    issueCommand((String)localObject1, 250);
  }
  
















  private void rcptTo()
    throws MessagingException
  {
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    Vector localVector3 = new Vector();
    int i = -1;
    Object localObject = null;
    int j = 0;
    SendFailedException localSendFailedException = null;
    validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
    boolean bool = false;
    if ((message instanceof SMTPMessage))
      bool = ((SMTPMessage)message).getSendPartial();
    if (!bool) {
      String str1 = session.getProperty("mail." + name + ".sendpartial");
      bool = (str1 != null) && (str1.equalsIgnoreCase("true"));
    }
    
    int k = 0;
    String str2 = null;
    if (supportsExtension("DSN")) {
      if ((message instanceof SMTPMessage))
        str2 = ((SMTPMessage)message).getDSNNotify();
      if (str2 == null) {
        str2 = session.getProperty("mail." + name + ".dsn.notify");
      }
      if (str2 != null) {
        k = 1;
      }
    }
    
    for (int m = 0; m < addresses.length; m++)
    {
      localSendFailedException = null;
      String str3 = "RCPT TO:" + 
        normalizeAddress(((InternetAddress)addresses[m]).getAddress());
      if (k != 0) {
        str3 = str3 + " NOTIFY=" + str2;
      }
      sendCommand(str3);
      
      i = readServerResponse();
      switch (i) {
      case 250: case 251: 
        localVector1.addElement(addresses[m]);
        break;
      case 501: case 503: 
      case 550: case 551: 
      case 553: 
        if (!bool)
          j = 1;
        localVector3.addElement(addresses[m]);
        
        localSendFailedException = new SendFailedException(lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else
          localObject.setNextException(localSendFailedException);
        break;
      case 450: case 451: 
      case 452: 
      case 552: 
        if (!bool)
          j = 1;
        localVector2.addElement(addresses[m]);
        
        localSendFailedException = new SendFailedException(lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else
          localObject.setNextException(localSendFailedException);
        break;
      

      default: 
        if ((i >= 400) && (i <= 499))
        {
          localVector2.addElement(addresses[m]);
        } else if ((i >= 500) && (i <= 599))
        {
          localVector3.addElement(addresses[m]);
        }
        else {
          String str4 = lastServerResponse;
          issueCommand("RSET", 250);
          throw new SendFailedException(str4);
        }
        if (!bool) {
          j = 1;
        }
        localSendFailedException = new SendFailedException(lastServerResponse);
        if (localObject == null) {
          localObject = localSendFailedException;
        } else {
          localObject.setNextException(localSendFailedException);
        }
        
        break;
      }
      
    }
    if ((bool) && (localVector1.size() == 0)) {
      j = 1;
    }
    int n;
    if (j != 0)
    {
      invalidAddr = new Address[localVector3.size()];
      localVector3.copyInto(invalidAddr);
      

      validUnsentAddr = new Address[localVector1.size() + localVector2.size()];
      n = 0;
      for (int i1 = 0; i1 < localVector1.size(); i1++)
        validUnsentAddr[(n++)] = ((Address)localVector1.elementAt(i1));
      for (int i2 = 0; i2 < localVector2.size(); i2++)
        validUnsentAddr[(n++)] = ((Address)localVector2.elementAt(i2));
    } else if ((bool) && (
      (localVector3.size() > 0) || (localVector2.size() > 0)))
    {

      sendPartiallyFailed = true;
      exception = localObject;
      

      invalidAddr = new Address[localVector3.size()];
      localVector3.copyInto(invalidAddr);
      

      validUnsentAddr = new Address[localVector2.size()];
      localVector2.copyInto(validUnsentAddr);
      

      validSentAddr = new Address[localVector1.size()];
      localVector1.copyInto(validSentAddr);
    } else {
      validSentAddr = addresses;
    }
    


    if (debug) {
      if ((validSentAddr != null) && (validSentAddr.length > 0)) {
        System.out.println("Verified Addresses");
        for (n = 0; n < validSentAddr.length; n++) {
          System.out.println("  " + validSentAddr[n]);
        }
      }
      if ((validUnsentAddr != null) && (validUnsentAddr.length > 0)) {
        System.out.println("Valid Unsent Addresses");
        for (n = 0; n < validUnsentAddr.length; n++) {
          System.out.println("  " + validUnsentAddr[n]);
        }
      }
      if ((invalidAddr != null) && (invalidAddr.length > 0)) {
        System.out.println("Invalid Addresses");
        for (n = 0; n < invalidAddr.length; n++) {
          System.out.println("  " + invalidAddr[n]);
        }
      }
    }
    

    if (j != 0) {
      if (debug) {
        System.out.println("DEBUG SMTPTransport: Sending failed because of invalid destination addresses");
      }
      notifyTransportListeners(2, 
        validSentAddr, validUnsentAddr, 
        invalidAddr, message);
      
      try
      {
        issueCommand("RSET", 250);
      }
      catch (MessagingException localMessagingException2) {
        try {
          close();
        }
        catch (MessagingException localMessagingException1) {
          if (debug) {
            localMessagingException1.printStackTrace();
          }
        }
      }
      throw new SendFailedException("Invalid Addresses", localObject, 
        validSentAddr, 
        validUnsentAddr, invalidAddr);
    }
  }
  


  private OutputStream data()
    throws MessagingException
  {
    issueCommand("DATA", 354);
    return new SMTPOutputStream(serverOutput);
  }
  
  private void finishData() throws MessagingException {
    issueCommand("\r\n.", 250);
  }
  


  private void openServer(String paramString, int paramInt)
    throws MessagingException
  {
    if (debug) {
      System.out.println("\nDEBUG: SMTPTransport trying to connect to host \"" + 
      
        paramString + "\", port " + paramInt + "\n");
    }
    session.getProperties();
    try
    {
      serverSocket = SocketFetcher.getSocket(paramString, paramInt, 
        session.getProperties(), "mail." + name);
      
      serverOutput = 
        new BufferedOutputStream(serverSocket.getOutputStream());
      serverInput = 
        new BufferedInputStream(serverSocket.getInputStream());
      lineInputStream = new LineInputStream(serverInput);
      
      if (readServerResponse() != 220) {
        serverSocket.close();
        serverSocket = null;
        serverOutput = null;
        serverInput = null;
        lineInputStream = null;
        if (debug)
          System.out.println(
            "DEBUG: SMTPTransport could not connect to host \"" + 
            paramString + "\", port: " + paramInt + "\n");
        throw new MessagingException(
          "Could not connect to SMTP host: " + 
          paramString + ", port: " + paramInt);
      }
      if (debug) {
        System.out.println(
          "DEBUG: SMTPTransport connected to host \"" + 
          paramString + "\", port: " + paramInt + "\n");
      }
    } catch (UnknownHostException localUnknownHostException) {
      throw new MessagingException("Unknown SMTP host: " + paramString, localUnknownHostException);
    } catch (IOException localIOException) {
      throw new MessagingException("Could not connect to SMTP host: " + 
        paramString + ", port: " + paramInt, localIOException);
    }
  }
  
  private void issueCommand(String paramString, int paramInt) throws MessagingException
  {
    sendCommand(paramString);
    


    if (readServerResponse() != paramInt)
      throw new MessagingException(lastServerResponse);
  }
  
  private int simpleCommand(String paramString) throws MessagingException {
    sendCommand(paramString);
    return readServerResponse();
  }
  
  private int simpleCommand(byte[] paramArrayOfByte) throws MessagingException {
    sendCommand(paramArrayOfByte);
    return readServerResponse();
  }
  


  private void sendCommand(String paramString)
    throws MessagingException
  {
    sendCommand(ASCIIUtility.getBytes(paramString));
  }
  
  private void sendCommand(byte[] paramArrayOfByte) throws MessagingException {
    if (debug) {
      System.out.println("DEBUG SMTP SENT: " + new String(paramArrayOfByte, 0));
    }
    try {
      serverOutput.write(paramArrayOfByte);
      serverOutput.write(CRLF);
      serverOutput.flush();
    } catch (IOException localIOException) {
      throw new MessagingException("Can't send command to SMTP host", localIOException);
    }
  }
  




  private int readServerResponse()
  {
    String str1 = "";
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer(100);
    

    try
    {
      String str2 = null;
      do
      {
        str2 = lineInputStream.readLine();
        if (str2 == null)
          return -1;
        localStringBuffer.append(str2);
        localStringBuffer.append("\n");
      } while (isNotLastLine(str2));
      
      str1 = localStringBuffer.toString();
    }
    catch (IOException localIOException) {
      i = -1;
    }
    

    if (debug) {
      System.out.println("DEBUG SMTP RCVD: " + str1);
    }
    
    if ((i != -1) && (str1 != null) && 
      (str1.length() >= 3)) {
      try {
        i = Integer.parseInt(str1.substring(0, 3));
      } catch (NumberFormatException localNumberFormatException) {
        try {
          close();
        }
        catch (MessagingException localMessagingException1) {
          if (debug)
            localMessagingException1.printStackTrace();
        }
        i = -1;
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
        try {
          close();
        }
        catch (MessagingException localMessagingException2) {
          if (debug)
            localMessagingException2.printStackTrace();
        }
        i = -1;
      }
    } else {
      i = -1;
    }
    
    lastServerResponse = str1;
    return i;
  }
  



  private void checkConnected()
  {
    if (!super.isConnected()) {
      throw new IllegalStateException("Not connected");
    }
  }
  
  private boolean isNotLastLine(String paramString) {
    return (paramString != null) && (paramString.length() >= 4) && (paramString.charAt(3) == '-');
  }
  
  private String normalizeAddress(String paramString)
  {
    if ((!paramString.startsWith("<")) && (!paramString.endsWith(">"))) {
      return "<" + paramString + ">";
    }
    return paramString;
  }
  
  private boolean supportsExtension(String paramString) {
    return (extMap != null) && (extMap.get(paramString.toUpperCase()) != null);
  }
  
  private boolean supportsAuthentication(String paramString) {
    if (extMap == null)
      return false;
    String str1 = (String)extMap.get("AUTH");
    if (str1 == null)
      return false;
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    while (localStringTokenizer.hasMoreTokens()) {
      String str2 = localStringTokenizer.nextToken();
      if (str2.equalsIgnoreCase(paramString))
        return true;
    }
    return false;
  }
}
