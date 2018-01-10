package javax.mail.event;

import javax.mail.Folder;






















































public class FolderEvent
  extends MailEvent
{
  public static final int CREATED = 1;
  public static final int DELETED = 2;
  public static final int RENAMED = 3;
  protected int type;
  protected transient Folder folder;
  protected transient Folder newFolder;
  
  public FolderEvent(Object paramObject, Folder paramFolder, int paramInt)
  {
    this(paramObject, paramFolder, paramFolder, paramInt);
  }
  









  public FolderEvent(Object paramObject, Folder paramFolder1, Folder paramFolder2, int paramInt)
  {
    super(paramObject);
    folder = paramFolder1;
    newFolder = paramFolder2;
    type = paramInt;
  }
  




  public int getType()
  {
    return type;
  }
  





  public Folder getFolder()
  {
    return folder;
  }
  










  public Folder getNewFolder()
  {
    return newFolder;
  }
  


  public void dispatch(Object paramObject)
  {
    if (type == 1) {
      ((FolderListener)paramObject).folderCreated(this);
    } else if (type == 2) {
      ((FolderListener)paramObject).folderDeleted(this);
    } else if (type == 3) {
      ((FolderListener)paramObject).folderRenamed(this);
    }
  }
}
