package javax.mail.search;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;

























public final class RecipientTerm
  extends AddressTerm
{
  protected Message.RecipientType type;
  
  public RecipientTerm(Message.RecipientType paramRecipientType, Address paramAddress)
  {
    super(paramAddress);
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
    if (!(paramObject instanceof RecipientTerm))
      return false;
    RecipientTerm localRecipientTerm = (RecipientTerm)paramObject;
    return (type.equals(type)) && (super.equals(paramObject));
  }
  


  public int hashCode()
  {
    return type.hashCode() + super.hashCode();
  }
}
