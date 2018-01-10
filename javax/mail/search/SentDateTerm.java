package javax.mail.search;

import java.util.Date;
import javax.mail.Message;





















public final class SentDateTerm
  extends DateTerm
{
  public SentDateTerm(int paramInt, Date paramDate)
  {
    super(paramInt, paramDate);
  }
  



  public boolean match(Message paramMessage)
  {
    Date localDate;
    


    try
    {
      localDate = paramMessage.getSentDate();
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
    if (!(paramObject instanceof SentDateTerm))
      return false;
    return super.equals(paramObject);
  }
}
