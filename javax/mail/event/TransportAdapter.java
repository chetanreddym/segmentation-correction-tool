package javax.mail.event;

public abstract class TransportAdapter
  implements TransportListener
{
  public void messageDelivered(TransportEvent paramTransportEvent) {}
  
  public void messageNotDelivered(TransportEvent paramTransportEvent) {}
  
  public void messagePartiallyDelivered(TransportEvent paramTransportEvent) {}
  
  public TransportAdapter() {}
}
