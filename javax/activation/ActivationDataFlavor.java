package javax.activation;

import java.awt.datatransfer.DataFlavor;

































public class ActivationDataFlavor
  extends DataFlavor
{
  private String mimeType = null;
  private MimeType mimeObject = null;
  private String humanPresentableName = null;
  private Class representationClass = null;
  

















  public ActivationDataFlavor(Class paramClass, String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    

    mimeType = paramString1;
    humanPresentableName = paramString2;
    representationClass = paramClass;
  }
  

















  public ActivationDataFlavor(Class paramClass, String paramString)
  {
    super(paramClass, paramString);
    mimeType = super.getMimeType();
    representationClass = paramClass;
    humanPresentableName = paramString;
  }
  














  public ActivationDataFlavor(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
    mimeType = paramString1;
    try {
      representationClass = Class.forName("java.io.InputStream");
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    
    humanPresentableName = paramString2;
  }
  




  public String getMimeType()
  {
    return mimeType;
  }
  




  public Class getRepresentationClass()
  {
    return representationClass;
  }
  




  public String getHumanPresentableName()
  {
    return humanPresentableName;
  }
  




  public void setHumanPresentableName(String paramString)
  {
    humanPresentableName = paramString;
  }
  







  public boolean equals(DataFlavor paramDataFlavor)
  {
    return (isMimeTypeEqual(paramDataFlavor)) && 
      (paramDataFlavor.getRepresentationClass() == representationClass);
  }
  











  public boolean isMimeTypeEqual(String paramString)
  {
    MimeType localMimeType = null;
    try {
      if (mimeObject == null)
        mimeObject = new MimeType(mimeType);
      localMimeType = new MimeType(paramString);
    }
    catch (MimeTypeParseException localMimeTypeParseException) {}
    return mimeObject.match(localMimeType);
  }
  













  protected String normalizeMimeTypeParameter(String paramString1, String paramString2)
  {
    return paramString1 + "=" + paramString2;
  }
  









  protected String normalizeMimeType(String paramString)
  {
    return paramString;
  }
}
