package javax.mail;











public class MessagingException
  extends Exception
{
  private Exception next;
  










  public MessagingException() {}
  










  public MessagingException(String paramString)
  {
    super(paramString);
  }
  








  public MessagingException(String paramString, Exception paramException)
  {
    super(paramString);
    next = paramException;
  }
  






  public Exception getNextException()
  {
    return next;
  }
  








  public synchronized boolean setNextException(Exception paramException)
  {
    Object localObject = this;
    while (((localObject instanceof MessagingException)) && 
      (next != null)) {
      localObject = next;
    }
    

    if ((localObject instanceof MessagingException)) {
      next = paramException;
      return true;
    }
    return false;
  }
  



  public String getMessage()
  {
    if (next == null) {
      return super.getMessage();
    }
    return 
    
      super.getMessage() + ";\n  nested exception is: \n\t" + next.toString();
  }
}
