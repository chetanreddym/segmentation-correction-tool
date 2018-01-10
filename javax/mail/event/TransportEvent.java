package javax.mail.event;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;



















































public class TransportEvent
  extends MailEvent
{
  public static final int MESSAGE_DELIVERED = 1;
  public static final int MESSAGE_NOT_DELIVERED = 2;
  public static final int MESSAGE_PARTIALLY_DELIVERED = 3;
  protected int type;
  protected transient Address[] validSent;
  protected transient Address[] validUnsent;
  protected transient Address[] invalid;
  protected transient Message msg;
  
  public TransportEvent(Transport paramTransport, int paramInt, Address[] paramArrayOfAddress1, Address[] paramArrayOfAddress2, Address[] paramArrayOfAddress3, Message paramMessage)
  {
    super(paramTransport);
    type = paramInt;
    validSent = paramArrayOfAddress1;
    validUnsent = paramArrayOfAddress2;
    invalid = paramArrayOfAddress3;
    msg = paramMessage;
  }
  



  public int getType()
  {
    return type;
  }
  



  public Address[] getValidSentAddresses()
  {
    return validSent;
  }
  





  public Address[] getValidUnsentAddresses()
  {
    return validUnsent;
  }
  



  public Address[] getInvalidAddresses()
  {
    return invalid;
  }
  





  public Message getMessage()
  {
    return msg;
  }
  


  public void dispatch(Object paramObject)
  {
    if (type == 1) {
      ((TransportListener)paramObject).messageDelivered(this);
    } else if (type == 2) {
      ((TransportListener)paramObject).messageNotDelivered(this);
    } else {
      ((TransportListener)paramObject).messagePartiallyDelivered(this);
    }
  }
}
