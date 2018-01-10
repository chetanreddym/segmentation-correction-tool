package com.sun.mail.imap.protocol;

import java.util.Vector;












public class MessageSet
{
  public int start;
  public int end;
  
  public MessageSet() {}
  
  public MessageSet(int paramInt1, int paramInt2)
  {
    start = paramInt1;
    end = paramInt2;
  }
  


  public int size()
  {
    return end - start + 1;
  }
  


  public static MessageSet[] createMessageSets(int[] paramArrayOfInt)
  {
    Vector localVector = new Vector();
    

    for (int i = 0; i < paramArrayOfInt.length; i++) {
      localObject = new MessageSet();
      start = paramArrayOfInt[i];
      

      for (int j = i + 1; j < paramArrayOfInt.length; j++) {
        if (paramArrayOfInt[j] != paramArrayOfInt[(j - 1)] + 1)
          break;
      }
      end = paramArrayOfInt[(j - 1)];
      localVector.addElement(localObject);
      i = j - 1;
    }
    Object localObject = new MessageSet[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  


  public static String toString(MessageSet[] paramArrayOfMessageSet)
  {
    if ((paramArrayOfMessageSet == null) || (paramArrayOfMessageSet.length == 0)) {
      return null;
    }
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    int j = paramArrayOfMessageSet.length;
    
    for (;;)
    {
      int k = start;
      int m = end;
      
      if (m > k) {
        localStringBuffer.append(k).append(':').append(m);
      } else {
        localStringBuffer.append(k);
      }
      i++;
      if (i >= j) {
        break;
      }
      localStringBuffer.append(',');
    }
    return localStringBuffer.toString();
  }
  



  public static int size(MessageSet[] paramArrayOfMessageSet)
  {
    int i = 0;
    
    if (paramArrayOfMessageSet == null) {
      return 0;
    }
    for (int j = 0; j < paramArrayOfMessageSet.length; j++) {
      i += paramArrayOfMessageSet[j].size();
    }
    return i;
  }
}
