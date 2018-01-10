package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;































public class CharTrie
  extends Trie
{
  private char m_initialValue_;
  private char[] m_data_;
  private FriendAgent m_friendAgent_;
  
  public CharTrie(InputStream inputStream, Trie.DataManipulate dataManipulate)
    throws IOException
  {
    super(inputStream, dataManipulate);
    
    if (!isCharTrie()) {
      throw new IllegalArgumentException("Data given does not belong to a char trie.");
    }
    
    m_friendAgent_ = new FriendAgent();
  }
  



  public class FriendAgent
  {
    public FriendAgent() {}
    


    public char[] getPrivateIndex()
    {
      return m_index_;
    }
    



    public char[] getPrivateData()
    {
      return m_data_;
    }
    



    public int getPrivateInitialValue()
    {
      return m_initialValue_;
    }
  }
  







  public void putIndexData(UCharacterProperty friend)
  {
    friend.setIndexData(m_friendAgent_);
  }
  








  public final char getCodePointValue(int ch)
  {
    int offset = getCodePointOffset(ch);
    


    return offset >= 0 ? m_data_[offset] : m_initialValue_;
  }
  










  public final char getLeadValue(char ch)
  {
    return m_data_[getLeadOffset(ch)];
  }
  








  public final char getBMPValue(char ch)
  {
    return m_data_[getBMPOffset(ch)];
  }
  







  public final char getSurrogateValue(char lead, char trail)
  {
    int offset = getSurrogateOffset(lead, trail);
    if (offset > 0) {
      return m_data_[offset];
    }
    return m_initialValue_;
  }
  










  public final char getTrailValue(int leadvalue, char trail)
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
  







  public final char getLatin1LinearValue(char ch)
  {
    return m_data_[(32 + m_dataOffset_ + ch)];
  }
  







  public boolean equals(Object other)
  {
    boolean result = super.equals(other);
    if ((result) && ((other instanceof CharTrie))) {
      CharTrie othertrie = (CharTrie)other;
      return m_initialValue_ == m_initialValue_;
    }
    return false;
  }
  









  protected final void unserialize(InputStream inputStream)
    throws IOException
  {
    DataInputStream input = new DataInputStream(inputStream);
    int indexDataLength = m_dataOffset_ + m_dataLength_;
    m_index_ = new char[indexDataLength];
    for (int i = 0; i < indexDataLength; i++) {
      m_index_[i] = input.readChar();
    }
    m_data_ = m_index_;
    m_initialValue_ = m_data_[m_dataOffset_];
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
}
