package com.sun.activation.registries;





public class MimeTypeEntry
{
  private String type;
  


  private String extension;
  



  public MimeTypeEntry(String paramString1, String paramString2)
  {
    type = paramString1;
    extension = paramString2;
  }
  
  public String getMIMEType() {
    return type;
  }
  
  public String getFileExtension() {
    return extension;
  }
  
  public String toString() {
    return "MIMETypeEntry: " + type + ", " + extension;
  }
}
