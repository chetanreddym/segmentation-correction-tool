package javax.mail;












public class ReadOnlyFolderException
  extends MessagingException
{
  private transient Folder folder;
  











  public ReadOnlyFolderException(Folder paramFolder)
  {
    this(paramFolder, null);
  }
  






  public ReadOnlyFolderException(Folder paramFolder, String paramString)
  {
    super(paramString);
    folder = paramFolder;
  }
  



  public Folder getFolder()
  {
    return folder;
  }
}
