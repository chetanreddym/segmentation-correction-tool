package javax.mail.event;

import java.util.EventListener;

public interface TransportListener
  extends EventListener
{
  public abstract void messageDelivered(TransportEvent paramTransportEvent);
  
  public abstract void messageNotDelivered(TransportEvent paramTransportEvent);
  
  public abstract void messagePartiallyDelivered(TransportEvent paramTransportEvent);
}
