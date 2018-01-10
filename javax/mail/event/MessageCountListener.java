package javax.mail.event;

import java.util.EventListener;

public interface MessageCountListener
  extends EventListener
{
  public abstract void messagesAdded(MessageCountEvent paramMessageCountEvent);
  
  public abstract void messagesRemoved(MessageCountEvent paramMessageCountEvent);
}
