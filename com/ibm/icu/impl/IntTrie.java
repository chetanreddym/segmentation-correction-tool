package com.ibm.icu.impl;

import com.ibm.icu.text.UTF16;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;





























public class IntTrie
  extends Trie
{
  private int m_initialValue_;
  private int[] m_data_;
  
  public IntTrie(InputStream inputStream, Trie.DataManipulate datamanipulate)
    throws IOException
  {
    super(inputStream, datamanipulate);
    if (!isIntTrie()) {
      throw new IllegalArgumentException("Data given does not belong to a int trie.");
    }
  }
  











  public final int getCodePointValue(int ch)
  {
    int offset = getCodePointOffset(ch);
    return offset >= 0 ? m_data_[offset] : m_initialValue_;
  }
  










  public final int getLeadValue(char ch)
  {
    return m_data_[getLeadOffset(ch)];
  }
  








  public final int getBMPValue(char ch)
  {
    return m_data_[getBMPOffset(ch)];
  }
  







  public final int getSurrogateValue(char lead, char trail)
  {
    if ((!UTF16.isLeadSurrogate(lead)) || (!UTF16.isTrailSurrogate(trail))) {
      throw new IllegalArgumentException("Argument characters do not form a supplementary character");
    }
    

    int offset = getSurrogateOffset(lead, trail);
    

    if (offset > 0) {
      return m_data_[offset];
    }
    

    return m_initialValue_;
  }
  









  public final int getTrailValue(int leadvalue, char trail)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    
    int offset = m_dataManipulate_.getFoldingOffset(leadvalue);
    if (offset > 0) {
      return m_data_[getRawOffset(offset, (char)(trail & 0x3FF))];
    }
    
    return m_initialValue_;
  }
  







  public final int getLatin1LinearValue(char ch)
  {
    return m_data_[(' ' + ch)];
  }
  







  public boolean equals(Object other)
  {
    boolean result = super.equals(other);
    if ((result) && ((other instanceof IntTrie))) {
      IntTrie othertrie = (IntTrie)other;
      if ((m_initialValue_ != m_initialValue_) || (!Arrays.equals(m_data_, m_data_)))
      {
        return false;
      }
      return true;
    }
    return false;
  }
  









  protected final void unserialize(InputStream inputStream)
    throws IOException
  {
    super.unserialize(inputStream);
    
    m_data_ = new int[m_dataLength_];
    DataInputStream input = new DataInputStream(inputStream);
    for (int i = 0; i < m_dataLength_; i++) {
      m_data_[i] = input.readInt();
    }
    m_initialValue_ = m_data_[0];
  }
  







  protected final int getSurrogateOffset(char lead, char trail)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    

    int offset = m_dataManipulate_.getFoldingOffset(getLeadValue(lead));
    

    if (offset > 0) {
      return getRawOffset(offset, (char)(trail & 0x3FF));
    }
    


    return -1;
  }
  








  protected final int getValue(int index)
  {
    return m_data_[index];
  }
  





  protected final int getInitialValue()
  {
    return m_initialValue_;
  }
  











  IntTrie(char[] index, int[] data, int initialvalue, int options, Trie.DataManipulate datamanipulate)
  {
    super(index, options, datamanipulate);
    m_index_ = index;
    m_data_ = data;
    m_dataLength_ = m_data_.length;
    m_initialValue_ = initialvalue;
  }
}
