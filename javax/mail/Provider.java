package javax.mail;




public class Provider
{
  private Type type;
  


  private String protocol;
  


  private String className;
  


  private String vendor;
  


  private String version;
  



  public static class Type
  {
    public static final Type STORE = new Type("Store");
    public static final Type TRANSPORT = new Type("Transport");
    private String type;
    
    private Type(String paramString)
    {
      type = paramString;
    }
  }
  












  Provider(Type paramType, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    type = paramType;
    protocol = paramString1;
    className = paramString2;
    vendor = paramString3;
    version = paramString4;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public String getProtocol()
  {
    return protocol;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getVendor()
  {
    return vendor;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public String toString()
  {
    String str = "javax.mail.Provider[";
    if (type == Type.STORE) {
      str = str + "STORE,";
    } else if (type == Type.TRANSPORT) {
      str = str + "TRANSPORT,";
    }
    
    str = str + protocol + "," + className;
    
    if (vendor != null) {
      str = str + "," + vendor;
    }
    if (version != null) {
      str = str + "," + version;
    }
    str = str + "]";
    return str;
  }
}
