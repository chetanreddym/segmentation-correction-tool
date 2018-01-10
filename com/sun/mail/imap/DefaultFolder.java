package com.sun.mail.imap;

import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.ListInfo;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;













public class DefaultFolder
  extends IMAPFolder
{
  protected DefaultFolder(IMAPStore paramIMAPStore)
  {
    super("", 65535, paramIMAPStore);
    exists = true;
    type = 2;
  }
  
  public String getName() {
    return fullName;
  }
  
  public Folder getParent() {
    return null;
  }
  
  public Folder[] list(String paramString) throws MessagingException {
    ListInfo[] arrayOfListInfo = null;
    IMAPProtocol localIMAPProtocol = null;
    try
    {
      localIMAPProtocol = getStoreProtocol();
      synchronized (lockFor(localIMAPProtocol)) {
        arrayOfListInfo = localIMAPProtocol.list("", paramString);
      }
    } catch (CommandFailedException localCommandFailedException) { localCommandFailedException;
    } catch (ProtocolException localProtocolException) {
      localProtocolException = localProtocolException;
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);jsr 14;
      break label81;
    }
    finally
    {
      localObject1 = finally; } jsr 6;throw localObject1;localObject2 = returnAddress;
    








    releaseStoreProtocol(localIMAPProtocol);ret;
    





    label81:
    




    if (arrayOfListInfo == null) {
      return null;
    }
    IMAPFolder[] arrayOfIMAPFolder = new IMAPFolder[arrayOfListInfo.length];
    for (int i = 0; i < arrayOfIMAPFolder.length; i++)
      arrayOfIMAPFolder[i] = new IMAPFolder(arrayOfListInfo[i], (IMAPStore)store);
    return arrayOfIMAPFolder;
  }
  
  public Folder[] listSubscribed(String paramString) throws MessagingException {
    ListInfo[] arrayOfListInfo = null;
    IMAPProtocol localIMAPProtocol = null;
    try
    {
      localIMAPProtocol = getStoreProtocol();
      synchronized (lockFor(localIMAPProtocol)) {
        arrayOfListInfo = localIMAPProtocol.lsub("", paramString);
      }
    } catch (CommandFailedException localCommandFailedException) { localCommandFailedException;
    } catch (ProtocolException localProtocolException) {
      localProtocolException = localProtocolException;
      throw new MessagingException(localProtocolException.getMessage(), localProtocolException);jsr 14;
      break label81;
    }
    finally
    {
      localObject1 = finally; } jsr 6;throw localObject1;localObject2 = returnAddress;
    








    releaseStoreProtocol(localIMAPProtocol);ret;
    





    label81:
    




    if (arrayOfListInfo == null) {
      return null;
    }
    IMAPFolder[] arrayOfIMAPFolder = new IMAPFolder[arrayOfListInfo.length];
    for (int i = 0; i < arrayOfIMAPFolder.length; i++)
      arrayOfIMAPFolder[i] = new IMAPFolder(arrayOfListInfo[i], (IMAPStore)store);
    return arrayOfIMAPFolder;
  }
  
  public boolean hasNewMessages() throws MessagingException
  {
    return false;
  }
  
  public Folder getFolder(String paramString) throws MessagingException {
    return new IMAPFolder(paramString, 65535, (IMAPStore)store);
  }
  
  public boolean delete(boolean paramBoolean) throws MessagingException
  {
    throw new MethodNotSupportedException("Cannot delete Default Folder");
  }
  
  public boolean renameTo(Folder paramFolder) throws MessagingException
  {
    throw new MethodNotSupportedException("Cannot rename Default Folder");
  }
  
  public void appendMessages(Message[] paramArrayOfMessage) throws MessagingException
  {
    throw new MethodNotSupportedException("Cannot append to Default Folder");
  }
  
  public Message[] expunge() throws MessagingException
  {
    throw new MethodNotSupportedException("Cannot expunge Default Folder");
  }
}
