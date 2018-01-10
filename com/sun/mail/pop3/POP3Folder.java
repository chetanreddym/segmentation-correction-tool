package com.sun.mail.pop3;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Vector;
import javax.mail.FetchProfile;
import javax.mail.FetchProfile.Item;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.UIDFolder.FetchProfileItem;
import javax.mail.internet.MimeMessage;













public class POP3Folder
  extends Folder
{
  private String name;
  private Protocol port;
  private int total;
  private boolean exists = false;
  private boolean opened = false;
  private Vector message_cache;
  private boolean doneUidl = false;
  
  POP3Folder(POP3Store paramPOP3Store, String paramString) {
    super(paramPOP3Store);
    name = paramString;
    if (paramString.equalsIgnoreCase("INBOX"))
      exists = true;
  }
  
  public String getName() {
    return name;
  }
  
  public String getFullName() {
    return name;
  }
  
  public Folder getParent() {
    return new DefaultFolder((POP3Store)store);
  }
  





  public boolean exists()
  {
    return exists;
  }
  




  public Folder[] list(String paramString)
    throws MessagingException
  {
    throw new MessagingException("not a directory");
  }
  




  public char getSeparator()
  {
    return '\000';
  }
  




  public int getType()
  {
    return 1;
  }
  




  public boolean create(int paramInt)
    throws MessagingException
  {
    return false;
  }
  




  public boolean hasNewMessages()
    throws MessagingException
  {
    return false;
  }
  




  public Folder getFolder(String paramString)
    throws MessagingException
  {
    throw new MessagingException("not a directory");
  }
  





  public boolean delete(boolean paramBoolean)
    throws MessagingException
  {
    throw new MethodNotSupportedException("delete");
  }
  




  public boolean renameTo(Folder paramFolder)
    throws MessagingException
  {
    throw new MethodNotSupportedException("renameTo");
  }
  






  public synchronized void open(int paramInt)
    throws MessagingException
  {
    checkClosed();
    if (!exists) {
      throw new FolderNotFoundException(this, "folder is not INBOX");
    }
    try {
      port = ((POP3Store)store).getPort(this);
      mode = paramInt;
      opened = true;
      total = port.stat().total;
    } catch (IOException localIOException) {
      throw new MessagingException("Open failed", localIOException);
    }
    

    message_cache = new Vector(total);
    message_cache.setSize(total);
    doneUidl = false;
    
    notifyConnectionListeners(1);
  }
  
  public synchronized void close(boolean paramBoolean) throws MessagingException {
    checkOpen();
    








    try
    {
      if (store).rsetBeforeQuit)
        port.rset();
      if ((paramBoolean) && (mode == 2))
      {

        for (int i = 0; i < message_cache.size(); i++) { POP3Message localPOP3Message;
          if (((localPOP3Message = (POP3Message)message_cache.elementAt(i)) != null) && 
            (localPOP3Message.isSet(Flags.Flag.DELETED))) {
            try {
              port.dele(i + 1);
            } catch (IOException localIOException1) {
              throw new MessagingException();
            }
          }
        }
      }
      
      port.quit();
    }
    catch (IOException localIOException2) {}finally
    {
      port = null;
      ((POP3Store)store).closePort(this);
      message_cache = null;
      opened = false;
      notifyConnectionListeners(3);
    }
  }
  
  public boolean isOpen() {
    return opened;
  }
  





  public Flags getPermanentFlags()
  {
    return new Flags();
  }
  



  public int getMessageCount()
    throws MessagingException
  {
    if (!opened)
      return -1;
    checkReadable();
    return total;
  }
  
  public synchronized Message getMessage(int paramInt) throws MessagingException
  {
    checkOpen();
    

    POP3Message localPOP3Message;
    
    if ((localPOP3Message = (POP3Message)message_cache.elementAt(paramInt - 1)) == null) {
      localPOP3Message = createMessage(this, paramInt);
      message_cache.setElementAt(localPOP3Message, paramInt - 1);
    }
    return localPOP3Message;
  }
  
  protected POP3Message createMessage(Folder paramFolder, int paramInt) throws MessagingException
  {
    POP3Message localPOP3Message = null;
    Constructor localConstructor = store).messageConstructor;
    if (localConstructor != null) {
      try {
        Object[] arrayOfObject = { this, new Integer(paramInt) };
        localPOP3Message = (POP3Message)localConstructor.newInstance(arrayOfObject);
      }
      catch (Exception localException) {}
    }
    
    if (localPOP3Message == null)
      localPOP3Message = new POP3Message(this, paramInt);
    return localPOP3Message;
  }
  




  public void appendMessages(Message[] paramArrayOfMessage)
    throws MessagingException
  {
    throw new MethodNotSupportedException("Append not supported");
  }
  







  public Message[] expunge()
    throws MessagingException
  {
    throw new MethodNotSupportedException("Expunge not supported");
  }
  








  public synchronized void fetch(Message[] paramArrayOfMessage, FetchProfile paramFetchProfile)
    throws MessagingException
  {
    checkReadable();
    if ((!doneUidl) && (paramFetchProfile.contains(UIDFolder.FetchProfileItem.UID)))
    {







      String[] arrayOfString = new String[message_cache.size()];
      try {
        if (!port.uidl(arrayOfString))
          return;
      } catch (EOFException localEOFException) {
        close(false);
        throw new FolderClosedException(this, localEOFException.toString());
      } catch (IOException localIOException) {
        throw new MessagingException("error getting UIDL", localIOException);
      }
      for (int j = 0; j < arrayOfString.length; j++)
        if (arrayOfString[j] != null)
        {
          POP3Message localPOP3Message2 = (POP3Message)getMessage(j + 1);
          uid = arrayOfString[j];
        }
      doneUidl = true;
    }
    if (paramFetchProfile.contains(FetchProfile.Item.ENVELOPE)) {
      for (int i = 0; i < paramArrayOfMessage.length; i++) {
        POP3Message localPOP3Message1 = (POP3Message)paramArrayOfMessage[i];
        
        localPOP3Message1.getHeader("");
        
        localPOP3Message1.getSize();
      }
    }
  }
  





  public synchronized String getUID(Message paramMessage)
    throws MessagingException
  {
    checkOpen();
    POP3Message localPOP3Message = (POP3Message)paramMessage;
    try {
      if (uid == "UNKNOWN")
        uid = port.uidl(localPOP3Message.getMessageNumber());
      return uid;
    } catch (EOFException localEOFException) {
      close(false);
      throw new FolderClosedException(this, localEOFException.toString());
    } catch (IOException localIOException) {
      throw new MessagingException("error getting UIDL", localIOException);
    }
  }
  

  protected void finalize()
    throws Throwable
  {
    close(false);
  }
  
  void checkOpen() throws IllegalStateException
  {
    if (!opened) {
      throw new IllegalStateException("Folder is not Open");
    }
  }
  
  void checkClosed() throws IllegalStateException {
    if (opened) {
      throw new IllegalStateException("Folder is Open");
    }
  }
  
  void checkReadable() throws IllegalStateException {
    if ((!opened) || ((mode != 1) && (mode != 2))) {
      throw new IllegalStateException("Folder is not Readable");
    }
  }
  
  void checkWritable() throws IllegalStateException {
    if ((!opened) || (mode != 2)) {
      throw new IllegalStateException("Folder is not Writable");
    }
  }
  


  Protocol getProtocol()
    throws MessagingException
  {
    checkOpen();
    return port;
  }
  


  protected void notifyMessageChangedListeners(int paramInt, Message paramMessage)
  {
    super.notifyMessageChangedListeners(paramInt, paramMessage);
  }
}
