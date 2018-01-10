package javax.mail.search;

import javax.mail.Message;




















public final class MessageNumberTerm
  extends IntegerComparisonTerm
{
  public MessageNumberTerm(int paramInt)
  {
    super(3, paramInt);
  }
  



  public boolean match(Message paramMessage)
  {
    int i;
    

    try
    {
      i = paramMessage.getMessageNumber();
    } catch (Exception localException) {
      return false;
    }
    
    return super.match(i);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MessageNumberTerm))
      return false;
    return super.equals(paramObject);
  }
}
