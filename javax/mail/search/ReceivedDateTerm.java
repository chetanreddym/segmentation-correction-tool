package javax.mail.search;

import java.util.Date;
import javax.mail.Message;





















public final class ReceivedDateTerm
  extends DateTerm
{
  public ReceivedDateTerm(int paramInt, Date paramDate)
  {
    super(paramInt, paramDate);
  }
  



  public boolean match(Message paramMessage)
  {
    Date localDate;
    


    try
    {
      localDate = paramMessage.getReceivedDate();
    } catch (Exception localException) {
      return false;
    }
    
    if (localDate == null) {
      return false;
    }
    return super.match(localDate);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ReceivedDateTerm))
      return false;
    return super.equals(paramObject);
  }
}
