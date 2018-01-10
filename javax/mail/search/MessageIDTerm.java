package javax.mail.search;

import javax.mail.Message;


























public final class MessageIDTerm
  extends StringTerm
{
  public MessageIDTerm(String paramString)
  {
    super(paramString);
  }
  



  public boolean match(Message paramMessage)
  {
    String[] arrayOfString;
    


    try
    {
      arrayOfString = paramMessage.getHeader("Message-ID");
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
    if (!(paramObject instanceof MessageIDTerm))
      return false;
    return super.equals(paramObject);
  }
}
