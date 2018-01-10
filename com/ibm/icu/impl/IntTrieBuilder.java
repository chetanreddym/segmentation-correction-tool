package com.ibm.icu.impl;

import java.util.Arrays;


































public class IntTrieBuilder
  extends TrieBuilder
{
  protected int[] m_data_;
  protected int m_initialValue_;
  
  public IntTrieBuilder(IntTrieBuilder table)
  {
    super(table);
    m_data_ = new int[m_dataCapacity_];
    System.arraycopy(m_data_, 0, m_data_, 0, m_dataLength_);
    m_initialValue_ = m_initialValue_;
  }
  










  public IntTrieBuilder(int[] aliasdata, int maxdatalength, int initialvalue, boolean latin1linear)
  {
    if ((maxdatalength < 32) || ((latin1linear) && (maxdatalength < 1024)))
    {
      throw new IllegalArgumentException("Argument maxdatalength is too small");
    }
    

    if (aliasdata != null) {
      m_data_ = aliasdata;
    }
    else {
      m_data_ = new int[maxdatalength];
    }
    

    int j = 32;
    
    if (latin1linear)
    {



      int i = 0;
      
      do
      {
        m_index_[(i++)] = j;
        j += 32;
      } while (i < 8);
    }
    
    m_dataLength_ = j;
    
    Arrays.fill(m_data_, 0, m_dataLength_, initialvalue);
    m_initialValue_ = initialvalue;
    m_dataCapacity_ = maxdatalength;
    m_isLatin1Linear_ = latin1linear;
    m_isCompacted_ = false;
  }
  


































































  public int getValue(int ch)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0)) {
      return 0;
    }
    
    int block = m_index_[(ch >> 5)];
    return m_data_[(Math.abs(block) + (ch & 0x1F))];
  }
  








  public boolean setValue(int ch, int value)
  {
    if ((m_isCompacted_) || (ch > 1114111) || (ch < 0)) {
      return false;
    }
    
    int block = getDataBlock(ch);
    if (block < 0) {
      return false;
    }
    
    m_data_[(block + (ch & 0x1F))] = value;
    return true;
  }
  







  public IntTrie serialize(TrieBuilder.DataManipulate datamanipulate, Trie.DataManipulate triedatamanipulate)
  {
    if (datamanipulate == null) {
      throw new IllegalArgumentException("Parameters can not be null");
    }
    

    if (!m_isCompacted_)
    {
      compact(false);
      
      fold(datamanipulate);
      
      compact(true);
      m_isCompacted_ = true;
    }
    
    if (m_dataLength_ >= 262144) {
      throw new ArrayIndexOutOfBoundsException("Data length too small");
    }
    
    char[] index = new char[m_indexLength_];
    int[] data = new int[m_dataLength_];
    

    for (int i = 0; i < m_indexLength_; i++) {
      index[i] = ((char)(m_index_[i] >>> 2));
    }
    
    System.arraycopy(m_data_, 0, data, 0, m_dataLength_);
    
    int options = 37;
    options |= 0x100;
    if (m_isLatin1Linear_) {
      options |= 0x200;
    }
    return new IntTrie(index, data, m_initialValue_, options, triedatamanipulate);
  }
  













  private int getDataBlock(int ch)
  {
    ch >>= 5;
    int indexValue = m_index_[ch];
    if (indexValue > 0) {
      return indexValue;
    }
    

    int newBlock = m_dataLength_;
    int newTop = newBlock + 32;
    if (newTop > m_dataCapacity_)
    {
      return -1;
    }
    m_dataLength_ = newTop;
    m_index_[ch] = newBlock;
    

    Arrays.fill(m_data_, newBlock, newBlock + 32, m_initialValue_);
    
    return newBlock;
  }
  













  private void compact(boolean overlap)
  {
    if (m_isCompacted_) {
      return;
    }
    


    findUnusedBlocks();
    


    int overlapStart = 32;
    if (m_isLatin1Linear_) {
      overlapStart += 256;
    }
    
    int newStart = 32;
    int prevEnd = newStart - 1;
    for (int start = newStart; start < m_dataLength_;)
    {



      if (m_map_[(start >> 5)] < 0)
      {
        start += 32;

      }
      else
      {
        if (start >= overlapStart) {
          int i = findSameDataBlock(m_data_, newStart, start, overlap ? 4 : 32);
          
          if (i >= 0)
          {

            m_map_[(start >> 5)] = i;
            
            start += 32;
            
            continue;
          }
        }
        


        int x = m_data_[start];
        int i = 0;
        if ((x == m_data_[prevEnd]) && (overlap) && (start >= overlapStart))
        {

          i = 1;
          
          while ((i < 32) && (x == m_data_[(start + i)]) && (x == m_data_[(prevEnd - i)])) { i++;
          }
          


          i &= 0xFFFFFFFC;
        }
        if (i > 0)
        {
          m_map_[(start >> 5)] = (newStart - i);
          
          start += i;
          for (i = 32 - i; i > 0; i--) {
            m_data_[(newStart++)] = m_data_[(start++)];
          }
        }
        else if (newStart < start)
        {
          m_map_[(start >> 5)] = newStart;
          for (i = 32; i > 0; i--) {
            m_data_[(newStart++)] = m_data_[(start++)];
          }
        }
        else {
          m_map_[(start >> 5)] = start;
          newStart += 32;
          start = newStart;
        }
        
        prevEnd = newStart - 1;
      }
    }
    for (int i = 0; i < m_indexLength_; i++) {
      m_index_[i] = m_map_[(m_index_[i] >>> 5)];
    }
    m_dataLength_ = newStart;
  }
  





  private static final int findSameDataBlock(int[] data, int dataLength, int otherBlock, int step)
  {
    
    




    for (int block = 0; block <= dataLength; block += step) {
      int i = 0;
      for (i = 0; i < 32; i++) {
        if (data[(block + i)] != data[(otherBlock + i)]) {
          break;
        }
      }
      if (i == 32) {
        return block;
      }
    }
    return -1;
  }
  











  private final void fold(TrieBuilder.DataManipulate manipulate)
  {
    int[] leadIndexes = new int[32];
    int[] index = m_index_;
    
    System.arraycopy(index, 1728, leadIndexes, 0, 32);
    




    for (char c = 55296; c <= 56319; c = (char)(c + '\001')) {
      int block = index[(c >> '\005')];
      if (block > 0) {
        index[(c >> '\005')] = (-block);
      }
    }
    







    int indexLength = 2048;
    
    for (int c = 65536; c < 1114112;) {
      if (index[(c >> 5)] != 0)
      {
        c &= 0xFC00;
        
        int block = TrieBuilder.findSameIndexBlock(index, indexLength, c >> 5);
        

        int value = manipulate.getFoldedValue(c, block + 32);
        
        if (value != 0) {
          if (!setValue(55232 + (c >> 10), value))
          {
            throw new ArrayIndexOutOfBoundsException("Data table overflow");
          }
          

          if (block == indexLength)
          {

            System.arraycopy(index, c >> 5, index, indexLength, 32);
            
            indexLength += 32;
          }
        }
        c += 1024;
      }
      else {
        c += 32;
      }
    }
    








    if (indexLength >= 34816) {
      throw new ArrayIndexOutOfBoundsException("Index table overflow");
    }
    

    System.arraycopy(index, 2048, index, 2080, indexLength - 2048);
    

    System.arraycopy(leadIndexes, 0, index, 2048, 32);
    
    indexLength += 32;
    m_indexLength_ = indexLength;
  }
}
