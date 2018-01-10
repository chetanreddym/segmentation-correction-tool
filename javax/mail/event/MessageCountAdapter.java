package javax.mail.event;

public abstract class MessageCountAdapter
  implements MessageCountListener
{
  public void messagesAdded(MessageCountEvent paramMessageCountEvent) {}
  
  public void messagesRemoved(MessageCountEvent paramMessageCountEvent) {}
  
  public MessageCountAdapter() {}
}
