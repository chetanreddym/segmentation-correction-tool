package com.ibm.icu.impl;

import java.util.Arrays;






























































public class TrieBuilder
{
  public static final int DATA_BLOCK_LENGTH_ = 32;
  protected int[] m_index_;
  protected int m_indexLength_;
  protected int m_dataCapacity_;
  protected int m_dataLength_;
  protected boolean m_isLatin1Linear_;
  protected boolean m_isCompacted_;
  protected int[] m_map_;
  protected static final int SHIFT_ = 5;
  protected static final int MAX_INDEX_LENGTH_ = 34816;
  protected static final int BMP_INDEX_LENGTH_ = 2048;
  protected static final int SURROGATE_BLOCK_COUNT_ = 32;
  protected static final int MASK_ = 31;
  protected static final int INDEX_SHIFT_ = 2;
  protected static final int MAX_DATA_LENGTH_ = 262144;
  protected static final int OPTIONS_INDEX_SHIFT_ = 4;
  protected static final int OPTIONS_DATA_IS_32_BIT_ = 256;
  protected static final int OPTIONS_LATIN1_IS_LINEAR_ = 512;
  protected static final int DATA_GRANULARITY_ = 4;
  private static final int MAX_BUILD_TIME_DATA_LENGTH_ = 1115168;
  
  public boolean isInZeroBlock(int ch)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0))
    {
      return true;
    }
    
    return m_index_[(ch >> 5)] == 0;
  }
  


















































































  protected TrieBuilder()
  {
    m_index_ = new int[34816];
    m_map_ = new int[34849];
    m_isLatin1Linear_ = false;
    m_isCompacted_ = false;
    m_indexLength_ = 34816;
  }
  
  protected TrieBuilder(TrieBuilder table)
  {
    m_index_ = new int[34816];
    m_indexLength_ = m_indexLength_;
    System.arraycopy(m_index_, 0, m_index_, 0, m_indexLength_);
    m_dataCapacity_ = m_dataCapacity_;
    m_dataLength_ = m_dataLength_;
    m_map_ = new int[m_map_.length];
    System.arraycopy(m_map_, 0, m_map_, 0, m_map_.length);
    m_isLatin1Linear_ = m_isLatin1Linear_;
    m_isCompacted_ = m_isCompacted_;
  }
  











  protected void findUnusedBlocks()
  {
    Arrays.fill(m_map_, 255);
    

    for (int i = 0; i < m_indexLength_; i++) {
      m_map_[(Math.abs(m_index_[i]) >> 5)] = 0;
    }
    

    m_map_[0] = 0;
  }
  








  protected static final int findSameIndexBlock(int[] index, int indexLength, int otherBlock)
  {
    for (int block = 2048; block < indexLength; 
        block += 32) {
      for (int i = 0; 
          i < 32; i++) {
        if (index[(block + i)] != index[(otherBlock + i)]) {
          break;
        }
      }
      if (i == 32) {
        return block;
      }
    }
    return indexLength;
  }
  
  public static abstract interface DataManipulate
  {
    public abstract int getFoldedValue(int paramInt1, int paramInt2);
  }
}
