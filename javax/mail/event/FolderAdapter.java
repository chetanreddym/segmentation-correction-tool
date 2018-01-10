package javax.mail.event;

public abstract class FolderAdapter
  implements FolderListener
{
  public void folderCreated(FolderEvent paramFolderEvent) {}
  
  public void folderRenamed(FolderEvent paramFolderEvent) {}
  
  public void folderDeleted(FolderEvent paramFolderEvent) {}
  
  public FolderAdapter() {}
}
