package com.sun.mail.imap.protocol;

import java.util.Vector;












public class UIDSet
{
  public long start;
  public long end;
  
  public UIDSet() {}
  
  public UIDSet(long paramLong1, long paramLong2)
  {
    start = paramLong1;
    end = paramLong2;
  }
  


  public long size()
  {
    return end - start + 1L;
  }
  


  public static UIDSet[] createUIDSets(long[] paramArrayOfLong)
  {
    Vector localVector = new Vector();
    

    for (int i = 0; i < paramArrayOfLong.length; i++) {
      localObject = new UIDSet();
      start = paramArrayOfLong[i];
      

      for (int j = i + 1; j < paramArrayOfLong.length; j++) {
        if (paramArrayOfLong[j] != paramArrayOfLong[(j - 1)] + 1L)
          break;
      }
      end = paramArrayOfLong[(j - 1)];
      localVector.addElement(localObject);
      i = j - 1;
    }
    Object localObject = new UIDSet[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  


  public static String toString(UIDSet[] paramArrayOfUIDSet)
  {
    if ((paramArrayOfUIDSet == null) || (paramArrayOfUIDSet.length == 0)) {
      return null;
    }
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    int j = paramArrayOfUIDSet.length;
    
    for (;;)
    {
      long l1 = start;
      long l2 = end;
      
      if (l2 > l1) {
        localStringBuffer.append(l1).append(':').append(l2);
      } else {
        localStringBuffer.append(l1);
      }
      i++;
      if (i >= j) {
        break;
      }
      localStringBuffer.append(',');
    }
    return localStringBuffer.toString();
  }
  



  public static long size(UIDSet[] paramArrayOfUIDSet)
  {
    long l = 0L;
    
    if (paramArrayOfUIDSet == null) {
      return 0L;
    }
    for (int i = 0; i < paramArrayOfUIDSet.length; i++) {
      l += paramArrayOfUIDSet[i].size();
    }
    return l;
  }
}
