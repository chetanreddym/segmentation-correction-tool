package javax.mail;

















public class MessageContext
{
  private Part part;
  















  public MessageContext(Part paramPart)
  {
    part = paramPart;
  }
  




  public Part getPart()
  {
    return part;
  }
  





  public Message getMessage()
  {
    try
    {
      return getMessage(part);
    } catch (MessagingException localMessagingException) {}
    return null;
  }
  








  private static Message getMessage(Part paramPart)
    throws MessagingException
  {
    while (paramPart != null) {
      if ((paramPart instanceof Message))
        return (Message)paramPart;
      BodyPart localBodyPart = (BodyPart)paramPart;
      Multipart localMultipart = localBodyPart.getParent();
      paramPart = localMultipart.getParent();
    }
    return null;
  }
  




  public Session getSession()
  {
    Message localMessage = getMessage();
    if (localMessage != null) return session; return null;
  }
}
