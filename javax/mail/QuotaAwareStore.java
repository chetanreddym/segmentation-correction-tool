package javax.mail;

public abstract interface QuotaAwareStore
{
  public abstract Quota[] getQuota(String paramString)
    throws MessagingException;
  
  public abstract void setQuota(Quota paramQuota)
    throws MessagingException;
}
