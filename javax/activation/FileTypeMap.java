package javax.activation;

import java.io.File;



























public abstract class FileTypeMap
{
  private static FileTypeMap defaultMap = null;
  
































  public static void setDefaultFileTypeMap(FileTypeMap paramFileTypeMap)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();

      }
      catch (SecurityException localSecurityException)
      {
        if (FileTypeMap.class.getClassLoader() != 
          paramFileTypeMap.getClass().getClassLoader())
          throw localSecurityException;
      }
    }
    defaultMap = paramFileTypeMap;
  }
  









  public static FileTypeMap getDefaultFileTypeMap()
  {
    if (defaultMap == null)
      defaultMap = new MimetypesFileTypeMap();
    return defaultMap;
  }
  
  public abstract String getContentType(String paramString);
  
  public abstract String getContentType(File paramFile);
  
  public FileTypeMap() {}
}
