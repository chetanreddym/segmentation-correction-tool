package com.sun.mail.pop3;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Store;










public class DefaultFolder
  extends Folder
{
  DefaultFolder(POP3Store paramPOP3Store)
  {
    super(paramPOP3Store);
  }
  
  public String getName() {
    return "";
  }
  
  public String getFullName() {
    return "";
  }
  
  public Folder getParent() {
    return null;
  }
  
  public boolean exists() {
    return true;
  }
  
  public Folder[] list(String paramString) throws MessagingException {
    Folder[] arrayOfFolder = { getInbox() };
    return arrayOfFolder;
  }
  
  public char getSeparator() {
    return '/';
  }
  
  public int getType() {
    return 2;
  }
  
  public boolean create(int paramInt) throws MessagingException {
    return false;
  }
  
  public boolean hasNewMessages() throws MessagingException {
    return false;
  }
  
  public Folder getFolder(String paramString) throws MessagingException {
    if (!paramString.equalsIgnoreCase("INBOX")) {
      throw new MessagingException("only INBOX supported");
    }
    return getInbox();
  }
  
  protected Folder getInbox() throws MessagingException
  {
    return getStore().getFolder("INBOX");
  }
  
  public boolean delete(boolean paramBoolean) throws MessagingException
  {
    throw new MethodNotSupportedException("delete");
  }
  
  public boolean renameTo(Folder paramFolder) throws MessagingException {
    throw new MethodNotSupportedException("renameTo");
  }
  
  public void open(int paramInt) throws MessagingException {
    throw new MethodNotSupportedException("open");
  }
  
  public void close(boolean paramBoolean) throws MessagingException {
    throw new MethodNotSupportedException("close");
  }
  
  public boolean isOpen() {
    return false;
  }
  
  public Flags getPermanentFlags() {
    return new Flags();
  }
  
  public int getMessageCount() throws MessagingException {
    return 0;
  }
  
  public Message getMessage(int paramInt) throws MessagingException {
    throw new MethodNotSupportedException("getMessage");
  }
  
  public void appendMessages(Message[] paramArrayOfMessage) throws MessagingException {
    throw new MethodNotSupportedException("Append not supported");
  }
  
  public Message[] expunge() throws MessagingException {
    throw new MethodNotSupportedException("expunge");
  }
}
