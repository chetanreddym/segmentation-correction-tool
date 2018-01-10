package com.ibm.icu.impl;

import com.ibm.icu.util.VersionInfo;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;



































































































































































































































































final class UCharacterPropertyReader
  implements ICUBinary.Authenticate
{
  private static final int INDEX_SIZE_ = 16;
  private DataInputStream m_dataInputStream_;
  private int m_propertyOffset_;
  private int m_exceptionOffset_;
  private int m_caseOffset_;
  private int m_additionalOffset_;
  private int m_additionalVectorsOffset_;
  private int m_additionalColumnsCount_;
  private int m_reservedOffset_;
  private byte[] m_unicodeVersion_;
  
  public boolean isDataVersionAcceptable(byte[] version)
  {
    return (version[0] == DATA_FORMAT_VERSION_[0]) && (version[2] == DATA_FORMAT_VERSION_[2]) && (version[3] == DATA_FORMAT_VERSION_[3]);
  }
  










  protected UCharacterPropertyReader(InputStream inputStream)
    throws IOException
  {
    m_unicodeVersion_ = ICUBinary.readHeader(inputStream, DATA_FORMAT_ID_, this);
    
    m_dataInputStream_ = new DataInputStream(inputStream);
  }
  









  protected void read(UCharacterProperty ucharppty)
    throws IOException
  {
    int count = 16;
    m_propertyOffset_ = m_dataInputStream_.readInt();
    count--;
    m_exceptionOffset_ = m_dataInputStream_.readInt();
    count--;
    m_caseOffset_ = m_dataInputStream_.readInt();
    count--;
    m_additionalOffset_ = m_dataInputStream_.readInt();
    count--;
    m_additionalVectorsOffset_ = m_dataInputStream_.readInt();
    count--;
    m_additionalColumnsCount_ = m_dataInputStream_.readInt();
    count--;
    m_reservedOffset_ = m_dataInputStream_.readInt();
    count--;
    m_dataInputStream_.skipBytes(12);
    count -= 3;
    m_maxBlockScriptValue_ = m_dataInputStream_.readInt();
    count--;
    m_maxJTGValue_ = m_dataInputStream_.readInt();
    count--;
    m_dataInputStream_.skipBytes(count << 2);
    


    m_trie_ = new CharTrie(m_dataInputStream_, ucharppty);
    

    int size = m_exceptionOffset_ - m_propertyOffset_;
    m_property_ = new int[size];
    for (int i = 0; i < size; i++) {
      m_property_[i] = m_dataInputStream_.readInt();
    }
    

    size = m_caseOffset_ - m_exceptionOffset_;
    m_exception_ = new int[size];
    for (int i = 0; i < size; i++) {
      m_exception_[i] = m_dataInputStream_.readInt();
    }
    

    size = m_additionalOffset_ - m_caseOffset_ << 1;
    m_case_ = new char[size];
    for (int i = 0; i < size; i++) {
      m_case_[i] = m_dataInputStream_.readChar();
    }
    

    m_additionalTrie_ = new CharTrie(m_dataInputStream_, ucharppty);
    


    size = m_reservedOffset_ - m_additionalVectorsOffset_;
    m_additionalVectors_ = new int[size];
    for (int i = 0; i < size; i++) {
      m_additionalVectors_[i] = m_dataInputStream_.readInt();
    }
    
    m_dataInputStream_.close();
    m_additionalColumnsCount_ = m_additionalColumnsCount_;
    m_unicodeVersion_ = VersionInfo.getInstance(m_unicodeVersion_[0], m_unicodeVersion_[1], m_unicodeVersion_[2], m_unicodeVersion_[3]);
  }
  






























  private static final byte[] DATA_FORMAT_ID_ = { 85, 80, 114, 111 };
  
  private static final byte[] DATA_FORMAT_VERSION_ = { 3, 1, 5, 2 };
}
