package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;




















final class UCharacterNameReader
  implements ICUBinary.Authenticate
{
  private DataInputStream m_dataInputStream_;
  private static final int GROUP_INFO_SIZE_ = 3;
  private int m_tokenstringindex_;
  private int m_groupindex_;
  private int m_groupstringindex_;
  private int m_algnamesindex_;
  private static final int ALG_INFO_SIZE_ = 12;
  
  public boolean isDataVersionAcceptable(byte[] version)
  {
    return version[0] == DATA_FORMAT_VERSION_[0];
  }
  








  protected UCharacterNameReader(InputStream inputStream)
    throws IOException
  {
    ICUBinary.readHeader(inputStream, DATA_FORMAT_ID_, this);
    m_dataInputStream_ = new DataInputStream(inputStream);
  }
  









  protected void read(UCharacterName data)
    throws IOException
  {
    m_tokenstringindex_ = m_dataInputStream_.readInt();
    m_groupindex_ = m_dataInputStream_.readInt();
    m_groupstringindex_ = m_dataInputStream_.readInt();
    m_algnamesindex_ = m_dataInputStream_.readInt();
    

    int count = m_dataInputStream_.readChar();
    char[] token = new char[count];
    for (char i = '\000'; i < count; i = (char)(i + '\001')) {
      token[i] = m_dataInputStream_.readChar();
    }
    int size = m_groupindex_ - m_tokenstringindex_;
    byte[] tokenstr = new byte[size];
    m_dataInputStream_.readFully(tokenstr);
    data.setToken(token, tokenstr);
    

    count = m_dataInputStream_.readChar();
    data.setGroupCountSize(count, 3);
    count *= 3;
    char[] group = new char[count];
    for (int i = 0; i < count; i++) {
      group[i] = m_dataInputStream_.readChar();
    }
    
    size = m_algnamesindex_ - m_groupstringindex_;
    byte[] groupstring = new byte[size];
    m_dataInputStream_.readFully(groupstring);
    
    data.setGroup(group, groupstring);
    
    count = m_dataInputStream_.readInt();
    UCharacterName.AlgorithmName[] alg = new UCharacterName.AlgorithmName[count];
    

    for (int i = 0; i < count; i++)
    {
      UCharacterName.AlgorithmName an = readAlg();
      if (an == null) {
        throw new IOException("unames.icu read error: Algorithmic names creation error");
      }
      alg[i] = an;
    }
    data.setAlgorithm(alg);
  }
  









  protected boolean authenticate(byte[] dataformatid, byte[] dataformatversion)
  {
    return (Arrays.equals(DATA_FORMAT_ID_, dataformatid)) && (Arrays.equals(DATA_FORMAT_VERSION_, dataformatversion));
  }
  
































  private static final byte[] DATA_FORMAT_VERSION_ = { 1, 0, 0, 0 };
  
  private static final byte[] DATA_FORMAT_ID_ = { 117, 110, 97, 109 };
  





  private static final String CORRUPTED_DATA_ERROR_ = "Data corrupted in character name data file";
  





  private UCharacterName.AlgorithmName readAlg()
    throws IOException
  {
    UCharacterName.AlgorithmName result = new UCharacterName.AlgorithmName();
    
    int rangestart = m_dataInputStream_.readInt();
    int rangeend = m_dataInputStream_.readInt();
    byte type = m_dataInputStream_.readByte();
    byte variant = m_dataInputStream_.readByte();
    if (!result.setInfo(rangestart, rangeend, type, variant)) {
      return null;
    }
    
    int size = m_dataInputStream_.readChar();
    if (type == 1)
    {
      char[] factor = new char[variant];
      for (int j = 0; j < variant; j++) {
        factor[j] = m_dataInputStream_.readChar();
      }
      
      result.setFactor(factor);
      size -= (variant << 1);
    }
    
    StringBuffer prefix = new StringBuffer();
    char c = (char)(m_dataInputStream_.readByte() & 0xFF);
    while (c != 0)
    {
      prefix.append(c);
      c = (char)(m_dataInputStream_.readByte() & 0xFF);
    }
    
    result.setPrefix(prefix.toString());
    
    size -= 12 + prefix.length() + 1;
    
    if (size > 0)
    {
      byte[] string = new byte[size];
      m_dataInputStream_.readFully(string);
      result.setFactorString(string);
    }
    return result;
  }
}
