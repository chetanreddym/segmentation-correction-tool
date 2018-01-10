package javax.mail.event;

import java.util.EventListener;

public interface MessageChangedListener
  extends EventListener
{
  public abstract void messageChanged(MessageChangedEvent paramMessageChangedEvent);
}
