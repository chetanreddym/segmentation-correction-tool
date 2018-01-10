package javax.mail.event;

import java.util.EventObject;














public abstract class MailEvent
  extends EventObject
{
  public MailEvent(Object paramObject)
  {
    super(paramObject);
  }
  
  public abstract void dispatch(Object paramObject);
}
