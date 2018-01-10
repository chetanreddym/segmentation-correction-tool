package javax.mail.search;

import javax.mail.Message;


























public final class HeaderTerm
  extends StringTerm
{
  protected String headerName;
  
  public HeaderTerm(String paramString1, String paramString2)
  {
    super(paramString2);
    headerName = paramString1;
  }
  


  public String getHeaderName()
  {
    return headerName;
  }
  



  public boolean match(Message paramMessage)
  {
    String[] arrayOfString;
    

    try
    {
      arrayOfString = paramMessage.getHeader(headerName);
    } catch (Exception localException) {
      return false;
    }
    
    if (arrayOfString == null) {
      return false;
    }
    for (int i = 0; i < arrayOfString.length; i++)
      if (super.match(arrayOfString[i]))
        return true;
    return false;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof HeaderTerm))
      return false;
    HeaderTerm localHeaderTerm = (HeaderTerm)paramObject;
    
    return (headerName.equalsIgnoreCase(headerName)) && (super.equals(localHeaderTerm));
  }
  



  public int hashCode()
  {
    return headerName.toLowerCase().hashCode() + super.hashCode();
  }
}
