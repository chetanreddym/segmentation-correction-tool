package javax.mail.event;

import javax.mail.Folder;
import javax.mail.Message;




























































public class MessageCountEvent
  extends MailEvent
{
  public static final int ADDED = 1;
  public static final int REMOVED = 2;
  protected int type;
  protected boolean removed;
  protected transient Message[] msgs;
  
  public MessageCountEvent(Folder paramFolder, int paramInt, boolean paramBoolean, Message[] paramArrayOfMessage)
  {
    super(paramFolder);
    type = paramInt;
    removed = paramBoolean;
    msgs = paramArrayOfMessage;
  }
  



  public int getType()
  {
    return type;
  }
  










  public boolean isRemoved()
  {
    return removed;
  }
  



  public Message[] getMessages()
  {
    return msgs;
  }
  


  public void dispatch(Object paramObject)
  {
    if (type == 1) {
      ((MessageCountListener)paramObject).messagesAdded(this);
    } else {
      ((MessageCountListener)paramObject).messagesRemoved(this);
    }
  }
}
