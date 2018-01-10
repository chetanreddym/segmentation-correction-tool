package javax.mail.event;

import java.util.EventListener;

public interface FolderListener
  extends EventListener
{
  public abstract void folderCreated(FolderEvent paramFolderEvent);
  
  public abstract void folderDeleted(FolderEvent paramFolderEvent);
  
  public abstract void folderRenamed(FolderEvent paramFolderEvent);
}
