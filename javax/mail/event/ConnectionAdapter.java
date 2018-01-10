package javax.mail.event;

public abstract class ConnectionAdapter
  implements ConnectionListener
{
  public void opened(ConnectionEvent paramConnectionEvent) {}
  
  public void disconnected(ConnectionEvent paramConnectionEvent) {}
  
  public void closed(ConnectionEvent paramConnectionEvent) {}
  
  public ConnectionAdapter() {}
}
