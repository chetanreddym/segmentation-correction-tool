package javax.mail;










public class FolderNotFoundException
  extends MessagingException
{
  private transient Folder folder;
  









  public FolderNotFoundException() {}
  









  public FolderNotFoundException(Folder paramFolder)
  {
    folder = paramFolder;
  }
  






  public FolderNotFoundException(Folder paramFolder, String paramString)
  {
    super(paramString);
    folder = paramFolder;
  }
  





  public FolderNotFoundException(String paramString, Folder paramFolder)
  {
    super(paramString);
    folder = paramFolder;
  }
  




  public Folder getFolder()
  {
    return folder;
  }
}
