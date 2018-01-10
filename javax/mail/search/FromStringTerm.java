package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;

























public final class FromStringTerm
  extends AddressStringTerm
{
  public FromStringTerm(String paramString)
  {
    super(paramString);
  }
  




  public boolean match(Message paramMessage)
  {
    Address[] arrayOfAddress;
    


    try
    {
      arrayOfAddress = paramMessage.getFrom();
    } catch (Exception localException) {
      return false;
    }
    
    if (arrayOfAddress == null) {
      return false;
    }
    for (int i = 0; i < arrayOfAddress.length; i++)
      if (super.match(arrayOfAddress[i]))
        return true;
    return false;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FromStringTerm))
      return false;
    return super.equals(paramObject);
  }
}
