package com.sun.mail.imap;

import com.sun.mail.imap.protocol.MessageSet;
import java.util.Vector;
import javax.mail.Message;
































public final class Utility
{
  private Utility() {}
  
  public static MessageSet[] toMessageSet(Message[] paramArrayOfMessage, Condition paramCondition)
  {
    Vector localVector = new Vector(1);
    


    for (int k = 0; k < paramArrayOfMessage.length; k++) {
      IMAPMessage localIMAPMessage = (IMAPMessage)paramArrayOfMessage[k];
      if (!localIMAPMessage.isExpunged())
      {

        int i = localIMAPMessage.getSequenceNumber();
        
        if ((paramCondition == null) || (paramCondition.test(localIMAPMessage)))
        {

          localObject = new MessageSet();
          start = i;
          

          for (k++; k < paramArrayOfMessage.length; k++)
          {
            localIMAPMessage = (IMAPMessage)paramArrayOfMessage[k];
            
            if (!localIMAPMessage.isExpunged())
            {
              int j = localIMAPMessage.getSequenceNumber();
              

              if ((paramCondition == null) || (paramCondition.test(localIMAPMessage)))
              {

                if (j == i + 1) {
                  i = j;

                }
                else
                {
                  k--;
                  break;
                } }
            } }
          end = i;
          localVector.addElement(localObject);
        }
      } }
    if (localVector.isEmpty()) {
      return null;
    }
    Object localObject = new MessageSet[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  
  public static interface Condition
  {
    public abstract boolean test(IMAPMessage paramIMAPMessage);
  }
}
