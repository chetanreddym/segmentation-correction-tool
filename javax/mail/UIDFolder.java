package javax.mail;





public interface UIDFolder
{
  public static final long LASTUID = -1L;
  




  public abstract long getUIDValidity()
    throws MessagingException;
  




  public abstract Message getMessageByUID(long paramLong)
    throws MessagingException;
  




  public abstract Message[] getMessagesByUID(long paramLong1, long paramLong2)
    throws MessagingException;
  




  public abstract Message[] getMessagesByUID(long[] paramArrayOfLong)
    throws MessagingException;
  



  public abstract long getUID(Message paramMessage)
    throws MessagingException;
  



  public static class FetchProfileItem
    extends FetchProfile.Item
  {
    protected FetchProfileItem(String paramString)
    {
      super();
    }
    
















    public static final FetchProfileItem UID = new FetchProfileItem("UID");
  }
}
