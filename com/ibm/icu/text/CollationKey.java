package com.ibm.icu.text;
























public final class CollationKey
  implements Comparable
{
  private byte[] m_key_;
  






















  private String m_source_;
  






















  private int m_hashCode_;
  






















  private int m_length_;
  





















  private static final int MERGE_SEPERATOR_ = 2;
  






















  public CollationKey(String source, byte[] key)
  {
    m_source_ = source;
    m_key_ = key;
    m_hashCode_ = 0;
    m_length_ = -1;
  }
  







  public String getSourceString()
  {
    return m_source_;
  }
  































  public byte[] toByteArray()
  {
    int length = 0;
    
    while (m_key_[length] != 0)
    {

      length++;
    }
    length++;
    byte[] result = new byte[length];
    System.arraycopy(m_key_, 0, result, 0, length);
    return result;
  }
  





















  public int compareTo(CollationKey target)
  {
    int i = 0;
    while ((m_key_[i] != 0) && (m_key_[i] != 0)) {
      byte key = m_key_[i];
      byte targetkey = m_key_[i];
      if (key == targetkey) {
        i++;
      }
      else {
        if (key >= 0) {
          if ((targetkey < 0) || (key < targetkey)) {
            return -1;
          }
          
          return 1;
        }
        

        if ((targetkey >= 0) || (key > targetkey)) {
          return 1;
        }
        return -1;
      }
    }
    
    if (m_key_[i] == m_key_[i]) {
      return 0;
    }
    if (m_key_[i] == 0) {
      return -1;
    }
    
    return 1;
  }
  


















  public int compareTo(Object obj)
  {
    return compareTo((CollationKey)obj);
  }
  
















  public boolean equals(Object target)
  {
    if (!(target instanceof CollationKey)) {
      return false;
    }
    
    return equals((CollationKey)target);
  }
  















  public boolean equals(CollationKey target)
  {
    if (this == target) {
      return true;
    }
    if (target == null) {
      return false;
    }
    CollationKey other = target;
    int i = 0;
    for (;;) {
      if (m_key_[i] != m_key_[i]) {
        return false;
      }
      if (m_key_[i] == 0) {
        break;
      }
      i++;
    }
    return true;
  }
  










  public int hashCode()
  {
    if (m_hashCode_ == 0) {
      int size = m_key_.length >> 1;
      StringBuffer key = new StringBuffer(size);
      int i = 0;
      while ((m_key_[i] != 0) && (m_key_[(i + 1)] != 0)) {
        key.append((char)(m_key_[i] << 8 | m_key_[(i + 1)]));
        i += 2;
      }
      if (m_key_[i] != 0) {
        key.append((char)(m_key_[i] << 8));
      }
      m_hashCode_ = key.toString().hashCode();
    }
    return m_hashCode_;
  }
  



























































  public CollationKey getBound(int boundType, int noOfLevels)
  {
    int offset = 0;
    int keystrength = 0;
    
    if (noOfLevels > 0) {
      do { if ((goto 59) && 
          (m_key_[(offset++)] == 1))
        {
          keystrength++;
          noOfLevels--;
          if ((noOfLevels == 0) || (offset == m_key_.length) || (m_key_[offset] == 0))
          {
            offset--;
            break;
          }
        }
      } while ((offset < m_key_.length) && (m_key_[offset] != 0));
    }
    











    if (noOfLevels > 0) {
      throw new IllegalArgumentException("Source collation key has only " + keystrength + " strength level. Call getBound() again " + " with noOfLevels < " + keystrength);
    }
    







    byte[] resultkey = new byte[offset + boundType + 1];
    System.arraycopy(m_key_, 0, resultkey, 0, offset);
    switch (boundType)
    {
    case 0: 
      break;
    
    case 1: 
      resultkey[(offset++)] = 2;
      break;
    
    case 2: 
      resultkey[(offset++)] = -1;
      resultkey[(offset++)] = -1;
      break;
    default: 
      throw new IllegalArgumentException("Illegal boundType argument");
    }
    
    resultkey[(offset++)] = 0;
    return new CollationKey(null, resultkey);
  }
  









































  public CollationKey merge(CollationKey source)
  {
    if ((source == null) || (source.getLength() == 0)) {
      throw new IllegalArgumentException("CollationKey argument can not be null or of 0 length");
    }
    

    getLength();
    int sourcelength = source.getLength();
    
    byte[] result = new byte[m_length_ + sourcelength + 2];
    

    int rindex = 0;
    int index = 0;
    int sourceindex = 0;
    


    for (;;)
    {
      result[(rindex++)] = m_key_[(index++)];
      while ((m_key_[index] >= 0) && (m_key_[index] < 2))
      {



        result[(rindex++)] = 2;
        


        while ((m_key_[sourceindex] < 0) || (m_key_[sourceindex] >= 2)) {
          result[(rindex++)] = m_key_[(sourceindex++)];
        }
        


        if ((m_key_[index] != 1) || (m_key_[sourceindex] != 1)) {
          break label183;
        }
        index++;
        sourceindex++;
        result[(rindex++)] = 1;
      }
    }
    


    label183:
    


    if (m_key_[index] != 0) {
      System.arraycopy(m_key_, index, result, rindex, m_length_ - index);

    }
    else if (m_key_[sourceindex] != 0) {
      System.arraycopy(m_key_, sourceindex, result, rindex, m_length_ - sourceindex);
    }
    
    result[(result.length - 1)] = 0;
    

    return new CollationKey(null, result);
  }
  































  private int getLength()
  {
    if (m_length_ >= 0) {
      return m_length_;
    }
    int length = m_key_.length;
    for (int index = 0; index < length; index++) {
      if (m_key_[index] == 0) {
        length = index;
        break;
      }
    }
    m_length_ = length;
    return m_length_;
  }
  
  public static final class BoundMode
  {
    public static final int LOWER = 0;
    public static final int UPPER = 1;
    public static final int UPPER_LONG = 2;
    public static final int COUNT = 3;
    
    private BoundMode() {}
  }
}
