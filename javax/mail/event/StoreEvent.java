package javax.mail.event;

import javax.mail.Store;










































public class StoreEvent
  extends MailEvent
{
  public static final int ALERT = 1;
  public static final int NOTICE = 2;
  protected int type;
  protected String message;
  
  public StoreEvent(Store paramStore, int paramInt, String paramString)
  {
    super(paramStore);
    type = paramInt;
    message = paramString;
  }
  






  public int getMessageType()
  {
    return type;
  }
  




  public String getMessage()
  {
    return message;
  }
  


  public void dispatch(Object paramObject)
  {
    ((StoreListener)paramObject).notification(this);
  }
}
