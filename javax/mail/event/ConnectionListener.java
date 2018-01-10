package javax.mail.event;

import java.util.EventListener;

public interface ConnectionListener
  extends EventListener
{
  public abstract void opened(ConnectionEvent paramConnectionEvent);
  
  public abstract void disconnected(ConnectionEvent paramConnectionEvent);
  
  public abstract void closed(ConnectionEvent paramConnectionEvent);
}
