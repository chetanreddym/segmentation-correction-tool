package javax.mail;








public class SendFailedException
  extends MessagingException
{
  protected transient Address[] invalid;
  





  protected transient Address[] validSent;
  





  protected transient Address[] validUnsent;
  






  public SendFailedException() {}
  






  public SendFailedException(String paramString)
  {
    super(paramString);
  }
  








  public SendFailedException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  













  public SendFailedException(String paramString, Exception paramException, Address[] paramArrayOfAddress1, Address[] paramArrayOfAddress2, Address[] paramArrayOfAddress3)
  {
    super(paramString, paramException);
    validSent = paramArrayOfAddress1;
    validUnsent = paramArrayOfAddress2;
    invalid = paramArrayOfAddress3;
  }
  



  public Address[] getValidSentAddresses()
  {
    return validSent;
  }
  





  public Address[] getValidUnsentAddresses()
  {
    return validUnsent;
  }
  




  public Address[] getInvalidAddresses()
  {
    return invalid;
  }
}
