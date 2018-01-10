package javax.activation;



















public abstract class CommandMap
{
  private static CommandMap defaultCommandMap = null;
  


  public CommandMap() {}
  


  public abstract DataContentHandler createDataContentHandler(String paramString);
  


  public abstract CommandInfo[] getAllCommands(String paramString);
  

  public abstract CommandInfo getCommand(String paramString1, String paramString2);
  

  public static CommandMap getDefaultCommandMap()
  {
    if (defaultCommandMap == null) {
      defaultCommandMap = new MailcapCommandMap();
    }
    return defaultCommandMap;
  }
  



  public abstract CommandInfo[] getPreferredCommands(String paramString);
  


  public static void setDefaultCommandMap(CommandMap paramCommandMap)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();

      }
      catch (SecurityException localSecurityException)
      {
        if (CommandMap.class.getClassLoader() != 
          paramCommandMap.getClass().getClassLoader())
          throw localSecurityException;
      }
    }
    defaultCommandMap = paramCommandMap;
  }
}
