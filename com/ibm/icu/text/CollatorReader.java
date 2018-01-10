package com.ibm.icu.text;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.ICUBinary.Authenticate;
import com.ibm.icu.impl.IntTrie;
import com.ibm.icu.impl.Trie;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.VersionInfo;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;































final class CollatorReader
{
  protected CollatorReader(InputStream inputStream)
    throws IOException
  {
    byte[] UnicodeVersion = ICUBinary.readHeader(inputStream, DATA_FORMAT_ID_, UCA_AUTHENTICATE_);
    

    VersionInfo UCDVersion = UCharacter.getUnicodeVersion();
    if ((UnicodeVersion[0] != UCDVersion.getMajor()) || (UnicodeVersion[1] != UCDVersion.getMinor()))
    {
      throw new IOException("Unicode version in binary image is not compatible with the current Unicode version");
    }
    m_dataInputStream_ = new DataInputStream(inputStream);
  }
  







  protected CollatorReader(InputStream inputStream, boolean readICUHeader)
    throws IOException
  {
    if (readICUHeader) {
      byte[] UnicodeVersion = ICUBinary.readHeader(inputStream, DATA_FORMAT_ID_, UCA_AUTHENTICATE_);
      


      VersionInfo UCDVersion = UCharacter.getUnicodeVersion();
      if ((UnicodeVersion[0] != UCDVersion.getMajor()) || (UnicodeVersion[1] != UCDVersion.getMinor()))
      {
        throw new IOException("Unicode version in binary image is not compatible with the current Unicode version");
      }
    }
    m_dataInputStream_ = new DataInputStream(inputStream);
  }
  







  protected void readHeader(RuleBasedCollator rbc)
    throws IOException
  {
    int size = m_dataInputStream_.readInt();
    


    m_dataInputStream_.skip(4L);
    

    int UCAConst = m_dataInputStream_.readInt();
    

    m_dataInputStream_.skip(4L);
    
    m_dataInputStream_.skipBytes(4);
    
    int mapping = m_dataInputStream_.readInt();
    
    m_expansionOffset_ = m_dataInputStream_.readInt();
    
    m_contractionOffset_ = m_dataInputStream_.readInt();
    
    int contractionCE = m_dataInputStream_.readInt();
    
    int contractionSize = m_dataInputStream_.readInt();
    
    int expansionEndCE = m_dataInputStream_.readInt();
    

    int expansionEndCEMaxSize = m_dataInputStream_.readInt();
    
    m_dataInputStream_.skipBytes(4);
    
    int unsafe = m_dataInputStream_.readInt();
    
    int contractionEnd = m_dataInputStream_.readInt();
    
    m_dataInputStream_.skipBytes(4);
    
    m_isJamoSpecial_ = m_dataInputStream_.readBoolean();
    
    m_dataInputStream_.skipBytes(3);
    m_version_ = readVersion(m_dataInputStream_);
    m_UCA_version_ = readVersion(m_dataInputStream_);
    m_UCD_version_ = readVersion(m_dataInputStream_);
    
    m_dataInputStream_.skipBytes(32);
    m_dataInputStream_.skipBytes(56);
    if (m_contractionOffset_ == 0) {
      m_contractionOffset_ = mapping;
      contractionCE = mapping;
    }
    m_expansionSize_ = (m_contractionOffset_ - m_expansionOffset_);
    m_contractionIndexSize_ = (contractionCE - m_contractionOffset_);
    m_contractionCESize_ = (mapping - contractionCE);
    m_trieSize_ = (expansionEndCE - mapping);
    m_expansionEndCESize_ = (expansionEndCEMaxSize - expansionEndCE);
    m_expansionEndCEMaxSizeSize_ = (unsafe - expansionEndCEMaxSize);
    m_unsafeSize_ = (contractionEnd - unsafe);
    m_UCAValuesSize_ = (size - UCAConst);
    

    m_contractionEndSize_ = (size - contractionEnd);
    
    m_contractionOffset_ >>= 1;
    m_expansionOffset_ >>= 2;
  }
  






  protected void readOptions(RuleBasedCollator rbc)
    throws IOException
  {
    m_defaultVariableTopValue_ = m_dataInputStream_.readInt();
    m_defaultIsFrenchCollation_ = (m_dataInputStream_.readInt() == 17);
    
    m_defaultIsAlternateHandlingShifted_ = (m_dataInputStream_.readInt() == 20);
    

    m_defaultCaseFirst_ = m_dataInputStream_.readInt();
    m_defaultIsCaseLevel_ = (m_dataInputStream_.readInt() == 17);
    
    int value = m_dataInputStream_.readInt();
    if (value == 17) {
      value = 17;
    }
    else {
      value = 16;
    }
    m_defaultDecomposition_ = value;
    m_defaultStrength_ = m_dataInputStream_.readInt();
    m_defaultIsHiragana4_ = (m_dataInputStream_.readInt() == 17);
    
    m_dataInputStream_.skip(64L);
  }
  












  protected char[] read(RuleBasedCollator rbc, RuleBasedCollator.UCAConstants UCAConst)
    throws IOException
  {
    readHeader(rbc);
    readOptions(rbc);
    m_expansionSize_ >>= 2;
    m_expansion_ = new int[m_expansionSize_];
    for (int i = 0; i < m_expansionSize_; i++) {
      m_expansion_[i] = m_dataInputStream_.readInt();
    }
    if (m_contractionIndexSize_ > 0) {
      m_contractionIndexSize_ >>= 1;
      m_contractionIndex_ = new char[m_contractionIndexSize_];
      for (int i = 0; i < m_contractionIndexSize_; i++) {
        m_contractionIndex_[i] = m_dataInputStream_.readChar();
      }
      m_contractionCESize_ >>= 2;
      m_contractionCE_ = new int[m_contractionCESize_];
      for (int i = 0; i < m_contractionCESize_; i++) {
        m_contractionCE_[i] = m_dataInputStream_.readInt();
      }
    }
    m_trie_ = new IntTrie(m_dataInputStream_, RuleBasedCollator.DataManipulate.getInstance());
    
    if (!m_trie_.isLatin1Linear()) {
      throw new IOException("Data corrupted, Collator Tries expected to have linear latin one data arrays");
    }
    

    m_expansionEndCESize_ >>= 2;
    m_expansionEndCE_ = new int[m_expansionEndCESize_];
    for (int i = 0; i < m_expansionEndCESize_; i++) {
      m_expansionEndCE_[i] = m_dataInputStream_.readInt();
    }
    m_expansionEndCEMaxSize_ = new byte[m_expansionEndCEMaxSizeSize_];
    for (int i = 0; i < m_expansionEndCEMaxSizeSize_; i++) {
      m_expansionEndCEMaxSize_[i] = m_dataInputStream_.readByte();
    }
    m_unsafe_ = new byte[m_unsafeSize_];
    for (int i = 0; i < m_unsafeSize_; i++) {
      m_unsafe_[i] = m_dataInputStream_.readByte();
    }
    if (UCAConst != null)
    {


      m_contractionEndSize_ -= m_UCAValuesSize_;
    }
    m_contractionEnd_ = new byte[m_contractionEndSize_];
    for (int i = 0; i < m_contractionEndSize_; i++) {
      m_contractionEnd_[i] = m_dataInputStream_.readByte();
    }
    if (UCAConst != null) {
      FIRST_TERTIARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_TERTIARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_TERTIARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_TERTIARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_PRIMARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_PRIMARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_SECONDARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_SECONDARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_SECONDARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_SECONDARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_PRIMARY_IGNORABLE_[0] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      LAST_PRIMARY_IGNORABLE_[1] = m_dataInputStream_.readInt();
      
      m_UCAValuesSize_ -= 4;
      FIRST_VARIABLE_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_VARIABLE_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_VARIABLE_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_VARIABLE_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_NON_VARIABLE_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_NON_VARIABLE_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_NON_VARIABLE_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_NON_VARIABLE_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      RESET_TOP_VALUE_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      RESET_TOP_VALUE_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_IMPLICIT_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_IMPLICIT_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_IMPLICIT_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_IMPLICIT_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_TRAILING_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      FIRST_TRAILING_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_TRAILING_[0] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      LAST_TRAILING_[1] = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_TOP_MIN_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_IMPLICIT_MIN_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_IMPLICIT_MAX_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_TRAILING_MIN_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_TRAILING_MAX_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_SPECIAL_MIN_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      PRIMARY_SPECIAL_MAX_ = m_dataInputStream_.readInt();
      m_UCAValuesSize_ -= 4;
      m_UCAValuesSize_ >>= 1;
      char[] result = new char[m_UCAValuesSize_];
      for (int i = 0; i < m_UCAValuesSize_; i++) {
        result[i] = m_dataInputStream_.readChar();
      }
      return result;
    }
    return null;
  }
  








  protected static CollationParsedRuleBuilder.InverseUCA readInverseUCA(InputStream inputStream)
    throws IOException
  {
    byte[] UnicodeVersion = ICUBinary.readHeader(inputStream, INVERSE_UCA_DATA_FORMAT_ID_, INVERSE_UCA_AUTHENTICATE_);
    



    VersionInfo UCDVersion = UCharacter.getUnicodeVersion();
    if ((UnicodeVersion[0] != UCDVersion.getMajor()) || (UnicodeVersion[1] != UCDVersion.getMinor()))
    {
      throw new IOException("Unicode version in binary image is not compatible with the current Unicode version");
    }
    
    CollationParsedRuleBuilder.InverseUCA result = new CollationParsedRuleBuilder.InverseUCA();
    
    DataInputStream input = new DataInputStream(inputStream);
    input.readInt();
    int tablesize = input.readInt();
    int contsize = input.readInt();
    input.readInt();
    input.readInt();
    m_UCA_version_ = readVersion(input);
    input.skipBytes(8);
    
    int size = tablesize * 3;
    m_table_ = new int[size];
    m_continuations_ = new char[contsize];
    
    for (int i = 0; i < size; i++) {
      m_table_[i] = input.readInt();
    }
    for (int i = 0; i < contsize; i++) {
      m_continuations_[i] = input.readChar();
    }
    input.close();
    return result;
  }
  









  protected static VersionInfo readVersion(DataInputStream input)
    throws IOException
  {
    byte[] version = new byte[4];
    version[0] = input.readByte();
    version[1] = input.readByte();
    version[2] = input.readByte();
    version[3] = input.readByte();
    
    VersionInfo result = VersionInfo.getInstance(version[0], version[1], version[2], version[3]);
    



    return result;
  }
  







  private static final ICUBinary.Authenticate UCA_AUTHENTICATE_ = new ICUBinary.Authenticate()
  {
    public boolean isDataVersionAcceptable(byte[] version)
    {
      return (version[0] == CollatorReader.DATA_FORMAT_VERSION_[0]) && (version[1] >= CollatorReader.DATA_FORMAT_VERSION_[1]);
    }
  };
  








  private static final ICUBinary.Authenticate INVERSE_UCA_AUTHENTICATE_ = new ICUBinary.Authenticate()
  {
    public boolean isDataVersionAcceptable(byte[] version)
    {
      return (version[0] == CollatorReader.INVERSE_UCA_DATA_FORMAT_VERSION_[0]) && (version[1] >= CollatorReader.INVERSE_UCA_DATA_FORMAT_VERSION_[1]);
    }
  };
  





  private DataInputStream m_dataInputStream_;
  





  private static final byte[] DATA_FORMAT_VERSION_ = { 2, 2, 0, 0 };
  
  private static final byte[] DATA_FORMAT_ID_ = { 85, 67, 111, 108 };
  




  private static final byte[] INVERSE_UCA_DATA_FORMAT_VERSION_ = { 2, 1, 0, 0 };
  
  private static final byte[] INVERSE_UCA_DATA_FORMAT_ID_ = { 73, 110, 118, 67 };
  private static final String CORRUPTED_DATA_ERROR_ = "Data corrupted in Collation data file";
  private static final String WRONG_UNICODE_VERSION_ERROR_ = "Unicode version in binary image is not compatible with the current Unicode version";
  private int m_expansionSize_;
  private int m_contractionIndexSize_;
  private int m_contractionCESize_;
  private int m_trieSize_;
  private int m_expansionEndCESize_;
  private int m_expansionEndCEMaxSizeSize_;
  private int m_unsafeSize_;
  private int m_contractionEndSize_;
  private int m_UCAValuesSize_;
}
