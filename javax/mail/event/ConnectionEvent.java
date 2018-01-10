package javax.mail.event;







public class ConnectionEvent
  extends MailEvent
{
  public static final int OPENED = 1;
  





  public static final int DISCONNECTED = 2;
  





  public static final int CLOSED = 3;
  





  protected int type;
  






  public ConnectionEvent(Object paramObject, int paramInt)
  {
    super(paramObject);
    type = paramInt;
  }
  



  public int getType()
  {
    return type;
  }
  


  public void dispatch(Object paramObject)
  {
    if (type == 1) {
      ((ConnectionListener)paramObject).opened(this);
    } else if (type == 2) {
      ((ConnectionListener)paramObject).disconnected(this);
    } else if (type == 3) {
      ((ConnectionListener)paramObject).closed(this);
    }
  }
}
