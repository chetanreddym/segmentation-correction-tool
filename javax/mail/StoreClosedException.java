package javax.mail;














public class StoreClosedException
  extends MessagingException
{
  private transient Store store;
  













  public StoreClosedException(Store paramStore)
  {
    this(paramStore, null);
  }
  




  public StoreClosedException(Store paramStore, String paramString)
  {
    super(paramString);
    store = paramStore;
  }
  


  public Store getStore()
  {
    return store;
  }
}
