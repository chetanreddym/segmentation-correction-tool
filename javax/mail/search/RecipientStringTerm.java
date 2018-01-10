package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;






























public final class RecipientStringTerm
  extends AddressStringTerm
{
  private Message.RecipientType type;
  
  public RecipientStringTerm(Message.RecipientType paramRecipientType, String paramString)
  {
    super(paramString);
    type = paramRecipientType;
  }
  


  public Message.RecipientType getRecipientType()
  {
    return type;
  }
  




  public boolean match(Message paramMessage)
  {
    Address[] arrayOfAddress;
    


    try
    {
      arrayOfAddress = paramMessage.getRecipients(type);
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
    if (!(paramObject instanceof RecipientStringTerm))
      return false;
    RecipientStringTerm localRecipientStringTerm = (RecipientStringTerm)paramObject;
    return (type.equals(type)) && (super.equals(paramObject));
  }
  


  public int hashCode()
  {
    return type.hashCode() + super.hashCode();
  }
}
