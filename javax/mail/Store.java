package javax.mail;

import java.util.Vector;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;





























public abstract class Store
  extends Service
{
  private volatile Vector storeListeners;
  private volatile Vector folderListeners;
  
  protected Store(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName);
  }
  














  public abstract Folder getDefaultFolder()
    throws MessagingException;
  














  public abstract Folder getFolder(String paramString)
    throws MessagingException;
  














  public abstract Folder getFolder(URLName paramURLName)
    throws MessagingException;
  














  public Folder[] getPersonalNamespaces()
    throws MessagingException
  {
    return new Folder[] { getDefaultFolder() };
  }
  















  public Folder[] getUserNamespaces(String paramString)
    throws MessagingException
  {
    return new Folder[0];
  }
  











  public Folder[] getSharedNamespaces()
    throws MessagingException
  {
    return new Folder[0];
  }
  











  public synchronized void addStoreListener(StoreListener paramStoreListener)
  {
    if (storeListeners == null)
      storeListeners = new Vector();
    storeListeners.addElement(paramStoreListener);
  }
  








  public synchronized void removeStoreListener(StoreListener paramStoreListener)
  {
    if (storeListeners != null) {
      storeListeners.removeElement(paramStoreListener);
    }
  }
  








  protected void notifyStoreListeners(int paramInt, String paramString)
  {
    if (storeListeners == null) {
      return;
    }
    StoreEvent localStoreEvent = new StoreEvent(this, paramInt, paramString);
    queueEvent(localStoreEvent, storeListeners);
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
  












  protected void notifyFolderListeners(int paramInt, Folder paramFolder)
  {
    if (folderListeners == null) {
      return;
    }
    FolderEvent localFolderEvent = new FolderEvent(this, paramFolder, paramInt);
    queueEvent(localFolderEvent, folderListeners);
  }
  














  protected void notifyFolderRenamedListeners(Folder paramFolder1, Folder paramFolder2)
  {
    if (folderListeners == null) {
      return;
    }
    FolderEvent localFolderEvent = new FolderEvent(this, paramFolder1, paramFolder2, 3);
    queueEvent(localFolderEvent, folderListeners);
  }
}
