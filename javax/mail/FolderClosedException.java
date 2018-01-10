package javax.mail;














public class FolderClosedException
  extends MessagingException
{
  private transient Folder folder;
  













  public FolderClosedException(Folder paramFolder)
  {
    this(paramFolder, null);
  }
  




  public FolderClosedException(Folder paramFolder, String paramString)
  {
    super(paramString);
    folder = paramFolder;
  }
  


  public Folder getFolder()
  {
    return folder;
  }
}
