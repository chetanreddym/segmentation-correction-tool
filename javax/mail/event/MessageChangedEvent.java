package javax.mail.event;

import javax.mail.Message;


































public class MessageChangedEvent
  extends MailEvent
{
  public static final int FLAGS_CHANGED = 1;
  public static final int ENVELOPE_CHANGED = 2;
  protected int type;
  protected transient Message msg;
  
  public MessageChangedEvent(Object paramObject, int paramInt, Message paramMessage)
  {
    super(paramObject);
    msg = paramMessage;
    type = paramInt;
  }
  



  public int getMessageChangeType()
  {
    return type;
  }
  



  public Message getMessage()
  {
    return msg;
  }
  


  public void dispatch(Object paramObject)
  {
    ((MessageChangedListener)paramObject).messageChanged(this);
  }
}
