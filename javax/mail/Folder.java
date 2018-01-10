package javax.mail;

import java.util.Vector;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.MailEvent;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.SearchTerm;













































































public abstract class Folder
{
  protected Store store;
  protected int mode = -1;
  public static final int HOLDS_MESSAGES = 1;
  public static final int HOLDS_FOLDERS = 2;
  public static final int READ_ONLY = 1;
  public static final int READ_WRITE = 2;
  
  protected Folder(Store paramStore)
  {
    store = paramStore;
  }
  








  public abstract String getName();
  








  public abstract String getFullName();
  







  public URLName getURLName()
    throws MessagingException
  {
    URLName localURLName = getStore().getURLName();
    String str = getFullName();
    StringBuffer localStringBuffer = new StringBuffer();
    getSeparator();
    
    if (str != null)
    {















      localStringBuffer.append(str);
    }
    




    return new URLName(localURLName.getProtocol(), localURLName.getHost(), 
      localURLName.getPort(), localStringBuffer.toString(), 
      localURLName.getUsername(), 
      null);
  }
  




  public Store getStore()
  {
    return store;
  }
  



















  public abstract Folder getParent()
    throws MessagingException;
  


















  public abstract boolean exists()
    throws MessagingException;
  


















  public abstract Folder[] list(String paramString)
    throws MessagingException;
  


















  public Folder[] listSubscribed(String paramString)
    throws MessagingException
  {
    return list(paramString);
  }
  












  public Folder[] list()
    throws MessagingException
  {
    return list("%");
  }
  












  public Folder[] listSubscribed()
    throws MessagingException
  {
    return listSubscribed("%");
  }
  













  public abstract char getSeparator()
    throws MessagingException;
  













  public abstract int getType()
    throws MessagingException;
  













  public abstract boolean create(int paramInt)
    throws MessagingException;
  












  public boolean isSubscribed()
  {
    return true;
  }
  















  public void setSubscribed(boolean paramBoolean)
    throws MessagingException
  {
    throw new MethodNotSupportedException();
  }
  














  private volatile Vector connectionListeners;
  













  private volatile Vector folderListeners;
  













  private volatile Vector messageCountListeners;
  













  private volatile Vector messageChangedListeners;
  













  private EventQueue q;
  













  public abstract boolean hasNewMessages()
    throws MessagingException;
  














  public abstract Folder getFolder(String paramString)
    throws MessagingException;
  














  public abstract boolean delete(boolean paramBoolean)
    throws MessagingException;
  














  public abstract boolean renameTo(Folder paramFolder)
    throws MessagingException;
  














  public abstract void open(int paramInt)
    throws MessagingException;
  














  public abstract void close(boolean paramBoolean)
    throws MessagingException;
  














  public abstract boolean isOpen();
  













  public int getMode()
  {
    if (!isOpen())
      throw new IllegalStateException("Folder not open");
    return mode;
  }
  




















  public abstract Flags getPermanentFlags();
  



















  public abstract int getMessageCount()
    throws MessagingException;
  



















  public synchronized int getNewMessageCount()
    throws MessagingException
  {
    if (!isOpen()) {
      return -1;
    }
    int i = 0;
    int j = getMessageCount();
    for (int k = 1; k <= j; k++) {
      try {
        if (getMessage(k).isSet(Flags.Flag.RECENT)) {
          i++;
        }
      }
      catch (MessageRemovedException localMessageRemovedException) {}
    }
    
    return i;
  }
  


























  public synchronized int getUnreadMessageCount()
    throws MessagingException
  {
    if (!isOpen()) {
      return -1;
    }
    int i = 0;
    int j = getMessageCount();
    for (int k = 1; k <= j; k++) {
      try {
        if (!getMessage(k).isSet(Flags.Flag.SEEN)) {
          i++;
        }
      }
      catch (MessageRemovedException localMessageRemovedException) {}
    }
    
    return i;
  }
  




























  public abstract Message getMessage(int paramInt)
    throws MessagingException;
  



























  public synchronized Message[] getMessages(int paramInt1, int paramInt2)
    throws MessagingException
  {
    Message[] arrayOfMessage = new Message[paramInt2 - paramInt1 + 1];
    for (int i = paramInt1; i <= paramInt2; i++)
      arrayOfMessage[(i - paramInt1)] = getMessage(i);
    return arrayOfMessage;
  }
  





















  public synchronized Message[] getMessages(int[] paramArrayOfInt)
    throws MessagingException
  {
    int i = paramArrayOfInt.length;
    Message[] arrayOfMessage = new Message[i];
    for (int j = 0; j < i; j++)
      arrayOfMessage[j] = getMessage(paramArrayOfInt[j]);
    return arrayOfMessage;
  }
  




















  public synchronized Message[] getMessages()
    throws MessagingException
  {
    if (!isOpen())
      throw new IllegalStateException("Folder not open");
    int i = getMessageCount();
    Message[] arrayOfMessage = new Message[i];
    for (int j = 1; j <= i; j++)
      arrayOfMessage[(j - 1)] = getMessage(j);
    return arrayOfMessage;
  }
  



























  public abstract void appendMessages(Message[] paramArrayOfMessage)
    throws MessagingException;
  



























  public void fetch(Message[] paramArrayOfMessage, FetchProfile paramFetchProfile)
    throws MessagingException
  {}
  



























  public synchronized void setFlags(Message[] paramArrayOfMessage, Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    for (int i = 0; i < paramArrayOfMessage.length; i++) {
      try {
        paramArrayOfMessage[i].setFlags(paramFlags, paramBoolean);
      }
      catch (MessageRemovedException localMessageRemovedException) {}
    }
  }
  
































  public synchronized void setFlags(int paramInt1, int paramInt2, Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    for (int i = paramInt1; i <= paramInt2; i++) {
      try {
        Message localMessage = getMessage(i);
        localMessage.setFlags(paramFlags, paramBoolean);
      }
      catch (MessageRemovedException localMessageRemovedException) {}
    }
  }
  






























  public synchronized void setFlags(int[] paramArrayOfInt, Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      try {
        Message localMessage = getMessage(paramArrayOfInt[i]);
        localMessage.setFlags(paramFlags, paramBoolean);
      }
      catch (MessageRemovedException localMessageRemovedException) {}
    }
  }
  




























  public void copyMessages(Message[] paramArrayOfMessage, Folder paramFolder)
    throws MessagingException
  {
    if (!paramFolder.exists()) {
      throw new FolderNotFoundException(
        paramFolder.getFullName() + " does not exist", 
        paramFolder);
    }
    paramFolder.appendMessages(paramArrayOfMessage);
  }
  

























  public abstract Message[] expunge()
    throws MessagingException;
  

























  public Message[] search(SearchTerm paramSearchTerm)
    throws MessagingException
  {
    return search(paramSearchTerm, getMessages());
  }
  




























  public Message[] search(SearchTerm paramSearchTerm, Message[] paramArrayOfMessage)
    throws MessagingException
  {
    Vector localVector = new Vector();
    

    for (int i = 0; i < paramArrayOfMessage.length; i++) {
      try {
        if (paramArrayOfMessage[i].match(paramSearchTerm)) {
          localVector.addElement(paramArrayOfMessage[i]);
        }
      } catch (MessageRemovedException localMessageRemovedException) {}
    }
    Message[] arrayOfMessage = new Message[localVector.size()];
    localVector.copyInto(arrayOfMessage);
    return arrayOfMessage;
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
  










  public synchronized void addFolderListener(FolderListener paramFolderListener)
  {
    if (folderListeners == null)
      folderListeners = new Vector();
    folderListeners.addElement(paramFolderListener);
  }
  








  public synchronized void removeFolderListener(FolderListener paramFolderListener)
  {
    if (folderListeners != null) {
      folderListeners.removeElement(paramFolderListener);
    }
  }
  













  protected void notifyFolderListeners(int paramInt)
  {
    if (folderListeners != null) {
      FolderEvent localFolderEvent = new FolderEvent(this, this, paramInt);
      queueEvent(localFolderEvent, folderListeners);
    }
    store.notifyFolderListeners(paramInt, this);
  }
  
















  protected void notifyFolderRenamedListeners(Folder paramFolder)
  {
    if (folderListeners != null) {
      FolderEvent localFolderEvent = new FolderEvent(this, this, paramFolder, 
        3);
      queueEvent(localFolderEvent, folderListeners);
    }
    store.notifyFolderRenamedListeners(this, paramFolder);
  }
  











  public synchronized void addMessageCountListener(MessageCountListener paramMessageCountListener)
  {
    if (messageCountListeners == null)
      messageCountListeners = new Vector();
    messageCountListeners.addElement(paramMessageCountListener);
  }
  









  public synchronized void removeMessageCountListener(MessageCountListener paramMessageCountListener)
  {
    if (messageCountListeners != null) {
      messageCountListeners.removeElement(paramMessageCountListener);
    }
  }
  










  protected void notifyMessageAddedListeners(Message[] paramArrayOfMessage)
  {
    if (messageCountListeners == null) {
      return;
    }
    MessageCountEvent localMessageCountEvent = new MessageCountEvent(
      this, 
      1, 
      false, 
      paramArrayOfMessage);
    
    queueEvent(localMessageCountEvent, messageCountListeners);
  }
  












  protected void notifyMessageRemovedListeners(boolean paramBoolean, Message[] paramArrayOfMessage)
  {
    if (messageCountListeners == null) {
      return;
    }
    MessageCountEvent localMessageCountEvent = new MessageCountEvent(
      this, 
      2, 
      paramBoolean, 
      paramArrayOfMessage);
    queueEvent(localMessageCountEvent, messageCountListeners);
  }
  












  public synchronized void addMessageChangedListener(MessageChangedListener paramMessageChangedListener)
  {
    if (messageChangedListeners == null)
      messageChangedListeners = new Vector();
    messageChangedListeners.addElement(paramMessageChangedListener);
  }
  









  public synchronized void removeMessageChangedListener(MessageChangedListener paramMessageChangedListener)
  {
    if (messageChangedListeners != null) {
      messageChangedListeners.removeElement(paramMessageChangedListener);
    }
  }
  








  protected void notifyMessageChangedListeners(int paramInt, Message paramMessage)
  {
    if (messageChangedListeners == null) {
      return;
    }
    MessageChangedEvent localMessageChangedEvent = new MessageChangedEvent(this, paramInt, paramMessage);
    queueEvent(localMessageChangedEvent, messageChangedListeners);
  }
  











  private Object qLock = new Object();
  



  private void queueEvent(MailEvent paramMailEvent, Vector paramVector)
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
  
  protected void finalize() throws Throwable {
    super.finalize();
    terminateQueue();
  }
  





  public String toString()
  {
    String str = getFullName();
    if (str != null) {
      return str;
    }
    return super.toString();
  }
}
