package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Hashtable;









final class CollationRuleParser
{
  static final int TOKEN_RESET_ = -559038737;
  int m_resultLength_;
  TokenListHeader[] m_listHeader_;
  Token m_variableTop_;
  OptionSet m_options_;
  StringBuffer m_source_;
  Hashtable m_hashTable_;
  private ParsedToken m_parsedToken_;
  private String m_rules_;
  private int m_current_;
  private int m_optionEnd_;
  private int m_sourceLimit_;
  private int m_extraCurrent_;
  UnicodeSet m_copySet_;
  UnicodeSet m_removeSet_;
  private static final int TOKEN_EXTRA_RULE_SPACE_SIZE_ = 2048;
  private static final int TOKEN_UNSET_ = -1;
  private static final int TOKEN_POLARITY_NEGATIVE_ = 0;
  private static final int TOKEN_POLARITY_POSITIVE_ = 1;
  private static final int TOKEN_TOP_MASK_ = 4;
  private static final int TOKEN_VARIABLE_TOP_MASK_ = 8;
  private static final int TOKEN_BEFORE_ = 3;
  private static final int TOKEN_SUCCESS_MASK_ = 16;
  
  CollationRuleParser(String rules)
    throws ParseException
  {
    extractSetsFromRules(rules);
    m_source_ = new StringBuffer(Normalizer.decompose(rules, false).trim());
    m_rules_ = m_source_.toString();
    m_current_ = 0;
    m_extraCurrent_ = m_source_.length();
    m_variableTop_ = null;
    m_parsedToken_ = new ParsedToken();
    m_hashTable_ = new Hashtable();
    m_options_ = new OptionSet(RuleBasedCollator.UCA_);
    m_listHeader_ = new TokenListHeader['Ȁ'];
    m_resultLength_ = 0;
  }
  

  static class OptionSet
  {
    int m_variableTopValue_;
    
    boolean m_isFrenchCollation_;
    
    boolean m_isAlternateHandlingShifted_;
    
    int m_caseFirst_;
    
    boolean m_isCaseLevel_;
    int m_decomposition_;
    int m_strength_;
    boolean m_isHiragana4_;
    
    OptionSet(RuleBasedCollator collator)
    {
      m_variableTopValue_ = m_variableTopValue_;
      m_isFrenchCollation_ = collator.isFrenchCollation();
      m_isAlternateHandlingShifted_ = collator.isAlternateHandlingShifted();
      
      m_caseFirst_ = m_caseFirst_;
      m_isCaseLevel_ = collator.isCaseLevel();
      m_decomposition_ = collator.getDecomposition();
      m_strength_ = collator.getStrength();
      m_isHiragana4_ = m_isHiragana4_;
    }
  }
  



  static class TokenListHeader
  {
    CollationRuleParser.Token m_first_;
    


    CollationRuleParser.Token m_last_;
    


    CollationRuleParser.Token m_reset_;
    


    boolean m_indirect_;
    


    int m_baseCE_;
    


    int m_baseContCE_;
    


    int m_nextCE_;
    


    int m_nextContCE_;
    


    int m_previousCE_;
    


    int m_previousContCE_;
    

    int[] m_pos_ = new int[16];
    int[] m_gapsLo_ = new int[9];
    int[] m_gapsHi_ = new int[9];
    int[] m_numStr_ = new int[9];
    CollationRuleParser.Token[] m_fStrToken_ = new CollationRuleParser.Token[3];
    CollationRuleParser.Token[] m_lStrToken_ = new CollationRuleParser.Token[3];
    

    TokenListHeader() {}
  }
  

  static class Token
  {
    int[] m_CE_;
    
    int m_CELength_;
    
    int[] m_expCE_;
    
    int m_expCELength_;
    int m_source_;
    int m_expansion_;
    int m_prefix_;
    int m_strength_;
    int m_toInsert_;
    int m_polarity_;
    CollationRuleParser.TokenListHeader m_listHeader_;
    Token m_previous_;
    Token m_next_;
    StringBuffer m_rules_;
    
    Token()
    {
      m_CE_ = new int[''];
      m_expCE_ = new int[''];
      
      m_polarity_ = 1;
      m_next_ = null;
      m_previous_ = null;
      m_CELength_ = 0;
      m_expCELength_ = 0;
    }
    






    public int hashCode()
    {
      int result = 0;
      int len = (m_source_ & 0xFF000000) >>> 24;
      int inc = (len - 32) / 32 + 1;
      
      int start = m_source_ & 0xFFFFFF;
      int limit = start + len;
      
      while (start < limit) {
        result = result * 37 + m_rules_.charAt(start);
        start += inc;
      }
      return result;
    }
    





    public boolean equals(Object target)
    {
      if (target == this) {
        return true;
      }
      if ((target instanceof Token)) {
        Token t = (Token)target;
        int sstart = m_source_ & 0xFFFFFF;
        int tstart = m_source_ & 0xFFFFFF;
        int slimit = (m_source_ & 0xFF000000) >> 24;
        int tlimit = (m_source_ & 0xFF000000) >> 24;
        
        int end = sstart + slimit - 1;
        
        if ((m_source_ == 0) || (m_source_ == 0)) {
          return false;
        }
        if (slimit != tlimit) {
          return false;
        }
        if (m_source_ == m_source_) {
          return true;
        }
        

        while ((sstart < end) && (m_rules_.charAt(sstart) == m_rules_.charAt(tstart)))
        {
          sstart++;
          tstart++;
        }
        if (m_rules_.charAt(sstart) == m_rules_.charAt(tstart)) {
          return true;
        }
      }
      return false;
    }
  }
  


































  void setDefaultOptionsInCollator(RuleBasedCollator collator)
  {
    m_defaultStrength_ = m_options_.m_strength_;
    m_defaultDecomposition_ = m_options_.m_decomposition_;
    m_defaultIsFrenchCollation_ = m_options_.m_isFrenchCollation_;
    m_defaultIsAlternateHandlingShifted_ = m_options_.m_isAlternateHandlingShifted_;
    
    m_defaultIsCaseLevel_ = m_options_.m_isCaseLevel_;
    m_defaultCaseFirst_ = m_options_.m_caseFirst_;
    m_defaultIsHiragana4_ = m_options_.m_isHiragana4_;
    m_defaultVariableTopValue_ = m_options_.m_variableTopValue_;
  }
  

  private static class ParsedToken
  {
    int m_strength_;
    int m_charsOffset_;
    int m_charsLen_;
    int m_extensionOffset_;
    int m_extensionLen_;
    int m_prefixOffset_;
    int m_prefixLen_;
    char m_flags_;
    char m_indirectIndex_;
    
    ParsedToken()
    {
      m_charsLen_ = 0;
      m_charsOffset_ = 0;
      m_extensionLen_ = 0;
      m_extensionOffset_ = 0;
      m_prefixLen_ = 0;
      m_prefixOffset_ = 0;
      m_flags_ = '\000';
      m_strength_ = -1;
    }
  }
  



  private static class IndirectBoundaries
  {
    int m_startCE_;
    


    int m_startContCE_;
    


    int m_limitCE_;
    


    int m_limitContCE_;
    


    IndirectBoundaries(int[] startce, int[] limitce)
    {
      m_startCE_ = startce[0];
      m_startContCE_ = startce[1];
      if (limitce != null) {
        m_limitCE_ = limitce[0];
        m_limitContCE_ = limitce[1];
      }
      else {
        m_limitCE_ = 0;
        m_limitContCE_ = 0;
      }
    }
  }
  


  private static class TokenOption
  {
    private String m_name_;
    

    private int m_attribute_;
    

    private String[] m_subOptions_;
    
    private int[] m_subOptionAttributeValues_;
    

    TokenOption(String name, int attribute, String[] suboptions, int[] suboptionattributevalue)
    {
      m_name_ = name;
      m_attribute_ = attribute;
      m_subOptions_ = suboptions;
      m_subOptionAttributeValues_ = suboptionattributevalue;
    }
  }
  



















































































































  private static final IndirectBoundaries[] INDIRECT_BOUNDARIES_ = new IndirectBoundaries[15];
  
  static { INDIRECT_BOUNDARIES_[0] = new IndirectBoundaries(UCA_CONSTANTS_LAST_NON_VARIABLE_, UCA_CONSTANTS_FIRST_IMPLICIT_);
    


    INDIRECT_BOUNDARIES_[1] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_PRIMARY_IGNORABLE_, null);
    


    INDIRECT_BOUNDARIES_[2] = new IndirectBoundaries(UCA_CONSTANTS_LAST_PRIMARY_IGNORABLE_, null);
    



    INDIRECT_BOUNDARIES_[3] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_SECONDARY_IGNORABLE_, null);
    


    INDIRECT_BOUNDARIES_[4] = new IndirectBoundaries(UCA_CONSTANTS_LAST_SECONDARY_IGNORABLE_, null);
    


    INDIRECT_BOUNDARIES_[5] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_TERTIARY_IGNORABLE_, null);
    


    INDIRECT_BOUNDARIES_[6] = new IndirectBoundaries(UCA_CONSTANTS_LAST_TERTIARY_IGNORABLE_, null);
    


    INDIRECT_BOUNDARIES_[7] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_VARIABLE_, null);
    


    INDIRECT_BOUNDARIES_[8] = new IndirectBoundaries(UCA_CONSTANTS_LAST_VARIABLE_, null);
    


    INDIRECT_BOUNDARIES_[9] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_NON_VARIABLE_, null);
    


    INDIRECT_BOUNDARIES_[10] = new IndirectBoundaries(UCA_CONSTANTS_LAST_NON_VARIABLE_, UCA_CONSTANTS_FIRST_IMPLICIT_);
    


    INDIRECT_BOUNDARIES_[11] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_IMPLICIT_, null);
    


    INDIRECT_BOUNDARIES_[12] = new IndirectBoundaries(UCA_CONSTANTS_LAST_IMPLICIT_, UCA_CONSTANTS_FIRST_TRAILING_);
    


    INDIRECT_BOUNDARIES_[13] = new IndirectBoundaries(UCA_CONSTANTS_FIRST_TRAILING_, null);
    


    INDIRECT_BOUNDARIES_[14] = new IndirectBoundaries(UCA_CONSTANTS_LAST_TRAILING_, null);
    

    INDIRECT_BOUNDARIES_14m_limitCE_ = (UCA_CONSTANTS_PRIMARY_SPECIAL_MIN_ << 24);
    

    RULES_OPTIONS_ = new TokenOption[19];
    String[] option = { "non-ignorable", "shifted" };
    int[] value = { 21, 20 };
    
    RULES_OPTIONS_[0] = new TokenOption("alternate", 1, option, value);
    

    option = new String[1];
    option[0] = "2";
    value = new int[1];
    value[0] = 17;
    RULES_OPTIONS_[1] = new TokenOption("backwards", 0, option, value);
    

    String[] offonoption = new String[2];
    offonoption[0] = "off";
    offonoption[1] = "on";
    int[] offonvalue = new int[2];
    offonvalue[0] = 16;
    offonvalue[1] = 17;
    RULES_OPTIONS_[2] = new TokenOption("caseLevel", 3, offonoption, offonvalue);
    

    option = new String[3];
    option[0] = "lower";
    option[1] = "upper";
    option[2] = "off";
    value = new int[3];
    value[0] = 24;
    value[1] = 25;
    value[2] = 16;
    RULES_OPTIONS_[3] = new TokenOption("caseFirst", 2, option, value);
    

    RULES_OPTIONS_[4] = new TokenOption("normalization", 4, offonoption, offonvalue);
    

    RULES_OPTIONS_[5] = new TokenOption("hiraganaQ", 6, offonoption, offonvalue);
    

    option = new String[5];
    option[0] = "1";
    option[1] = "2";
    option[2] = "3";
    option[3] = "4";
    option[4] = "I";
    value = new int[5];
    value[0] = 0;
    value[1] = 1;
    value[2] = 2;
    value[3] = 3;
    value[4] = 15;
    RULES_OPTIONS_[6] = new TokenOption("strength", 5, option, value);
    

    RULES_OPTIONS_[7] = new TokenOption("variable top", 7, null, null);
    

    RULES_OPTIONS_[8] = new TokenOption("rearrange", 7, null, null);
    

    option = new String[3];
    option[0] = "1";
    option[1] = "2";
    option[2] = "3";
    value = new int[3];
    value[0] = 0;
    value[1] = 1;
    value[2] = 2;
    RULES_OPTIONS_[9] = new TokenOption("before", 7, option, value);
    

    RULES_OPTIONS_[10] = new TokenOption("top", 7, null, null);
    

    String[] firstlastoption = new String[7];
    firstlastoption[0] = "primary";
    firstlastoption[1] = "secondary";
    firstlastoption[2] = "tertiary";
    firstlastoption[3] = "variable";
    firstlastoption[4] = "regular";
    firstlastoption[5] = "implicit";
    firstlastoption[6] = "trailing";
    
    int[] firstlastvalue = new int[7];
    Arrays.fill(firstlastvalue, 0);
    
    RULES_OPTIONS_[11] = new TokenOption("first", 7, firstlastoption, firstlastvalue);
    

    RULES_OPTIONS_[12] = new TokenOption("last", 7, firstlastoption, firstlastvalue);
    

    RULES_OPTIONS_[13] = new TokenOption("optimize", 7, null, null);
    

    RULES_OPTIONS_[14] = new TokenOption("suppressContractions", 7, null, null);
    

    RULES_OPTIONS_[15] = new TokenOption("undefined", 7, null, null);
    

    RULES_OPTIONS_[16] = new TokenOption("scriptOrder", 7, null, null);
    

    RULES_OPTIONS_[17] = new TokenOption("charsetname", 7, null, null);
    

    RULES_OPTIONS_[18] = new TokenOption("charset", 7, null, null);
  }
  

  private static final int INVERSE_SIZE_MASK_ = -1048576;
  private static final int INVERSE_OFFSET_MASK_ = 1048575;
  private static final int INVERSE_SHIFT_VALUE_ = 20;
  private static final TokenOption[] RULES_OPTIONS_;
  private Token m_utilToken_ = new Token();
  private CollationElementIterator m_UCAColEIter_ = RuleBasedCollator.UCA_.getCollationElementIterator("");
  
  private int[] m_utilCEBuffer_ = new int[2];
  






  int assembleTokenList()
    throws ParseException
  {
    Token lastToken = null;
    m_parsedToken_.m_strength_ = -1;
    int sourcelimit = m_source_.length();
    int expandNext = 0;
    
    while (m_current_ < sourcelimit) {
      m_parsedToken_.m_prefixOffset_ = 0;
      if (parseNextToken(lastToken == null) >= 0)
      {


        char specs = m_parsedToken_.m_flags_;
        boolean variableTop = (specs & 0x8) != 0;
        boolean top = (specs & 0x4) != 0;
        int lastStrength = -1;
        if (lastToken != null) {
          lastStrength = m_strength_;
        }
        m_utilToken_.m_source_ = (m_parsedToken_.m_charsLen_ << 24 | m_parsedToken_.m_charsOffset_);
        
        m_utilToken_.m_rules_ = m_source_;
        

        Token sourceToken = (Token)m_hashTable_.get(m_utilToken_);
        if (m_parsedToken_.m_strength_ != -559038737) {
          if (lastToken == null)
          {
            throwParseException(m_source_.toString(), 0);
          }
          
          if (sourceToken == null)
          {
            sourceToken = new Token();
            m_rules_ = m_source_;
            m_source_ = (m_parsedToken_.m_charsLen_ << 24 | m_parsedToken_.m_charsOffset_);
            
            m_prefix_ = (m_parsedToken_.m_prefixLen_ << 24 | m_parsedToken_.m_prefixOffset_);
            

            m_polarity_ = 1;
            m_next_ = null;
            m_previous_ = null;
            m_CELength_ = 0;
            m_expCELength_ = 0;
            m_hashTable_.put(sourceToken, sourceToken);


          }
          else if ((m_strength_ != -559038737) && (lastToken != sourceToken))
          {

            if (m_next_ != null) {
              if (m_next_.m_strength_ > m_strength_)
              {
                m_next_.m_strength_ = m_strength_;
              }
              
              m_next_.m_previous_ = m_previous_;
            }
            else
            {
              m_listHeader_.m_last_ = m_previous_;
            }
            
            if (m_previous_ != null) {
              m_previous_.m_next_ = m_next_;
            }
            else
            {
              m_listHeader_.m_first_ = m_next_;
            }
            
            m_next_ = null;
            m_previous_ = null;
          }
          
          m_strength_ = m_parsedToken_.m_strength_;
          m_listHeader_ = m_listHeader_;
          


          if ((lastStrength == -559038737) || (m_listHeader_.m_first_ == null))
          {

            if (m_listHeader_.m_first_ == null) {
              m_listHeader_.m_first_ = sourceToken;
              m_listHeader_.m_last_ = sourceToken;


            }
            else if (m_listHeader_.m_first_.m_strength_ <= m_strength_)
            {
              m_next_ = m_listHeader_.m_first_;
              
              m_next_.m_previous_ = sourceToken;
              m_listHeader_.m_first_ = sourceToken;
              m_previous_ = null;
            }
            else {
              lastToken = m_listHeader_.m_first_;
              

              while ((m_next_ != null) && (m_next_.m_strength_ > m_strength_)) {
                lastToken = m_next_;
              }
              if (m_next_ != null) {
                m_next_.m_previous_ = sourceToken;
              }
              else {
                m_listHeader_.m_last_ = sourceToken;
              }
              
              m_previous_ = lastToken;
              m_next_ = m_next_;
              m_next_ = sourceToken;



            }
            




          }
          else if (sourceToken != lastToken) {
            if (m_polarity_ == m_polarity_)
            {

              while ((m_next_ != null) && (m_next_.m_strength_ > m_strength_)) {
                lastToken = m_next_;
              }
              m_previous_ = lastToken;
              if (m_next_ != null) {
                m_next_.m_previous_ = sourceToken;
              }
              else {
                m_listHeader_.m_last_ = sourceToken;
              }
              m_next_ = m_next_;
              m_next_ = sourceToken;

            }
            else
            {
              while ((m_previous_ != null) && (m_previous_.m_strength_ > m_strength_)) {
                lastToken = m_previous_;
              }
              m_next_ = lastToken;
              if (m_previous_ != null) {
                m_previous_.m_next_ = sourceToken;
              }
              else {
                m_listHeader_.m_first_ = sourceToken;
              }
              
              m_previous_ = m_previous_;
              m_previous_ = sourceToken;
            }
            

          }
          else if (lastStrength < m_strength_) {
            m_strength_ = lastStrength;
          }
          


          if ((variableTop == true) && (m_variableTop_ == null)) {
            variableTop = false;
            m_variableTop_ = sourceToken;
          }
          




          m_expansion_ = (m_parsedToken_.m_extensionLen_ << 24 | m_parsedToken_.m_extensionOffset_);
          
          if (expandNext != 0) {
            if (m_strength_ == 0)
            {
              expandNext = 0;
            }
            else if (m_expansion_ == 0)
            {

              m_expansion_ = expandNext;

            }
            else
            {
              int start = expandNext & 0xFFFFFF;
              int size = expandNext >>> 24;
              if (size > 0) {
                m_source_.append(m_source_.substring(start, start + size));
              }
              
              start = m_parsedToken_.m_extensionOffset_;
              m_source_.append(m_source_.substring(start, start + m_parsedToken_.m_extensionLen_));
              
              m_expansion_ = (size + m_parsedToken_.m_extensionLen_ << 24 | m_extraCurrent_);
              

              m_extraCurrent_ += size + m_parsedToken_.m_extensionLen_;
            }
          }
        }
        else {
          if ((lastToken != null) && (lastStrength == -559038737))
          {


            if (m_listHeader_[(m_resultLength_ - 1)].m_first_ == null) {
              m_resultLength_ -= 1;
            }
          }
          if (sourceToken == null)
          {

            int searchCharsLen = m_parsedToken_.m_charsLen_;
            while ((searchCharsLen > 1) && (sourceToken == null)) {
              searchCharsLen--;
              
              m_utilToken_.m_source_ = (searchCharsLen << 24 | m_parsedToken_.m_charsOffset_);
              
              m_utilToken_.m_rules_ = m_source_;
              sourceToken = (Token)m_hashTable_.get(m_utilToken_);
            }
            if (sourceToken != null) {
              expandNext = m_parsedToken_.m_charsLen_ - searchCharsLen << 24 | m_parsedToken_.m_charsOffset_ + searchCharsLen;
            }
          }
          


          if ((specs & 0x3) != 0) {
            if (!top)
            {
              int strength = (specs & 0x3) - '\001';
              if ((sourceToken != null) && (m_strength_ != -559038737))
              {



                while ((m_strength_ > strength) && (m_previous_ != null)) {
                  sourceToken = m_previous_;
                }
                
                if (m_strength_ == strength) {
                  if (m_previous_ != null) {
                    sourceToken = m_previous_;
                  }
                  else {
                    sourceToken = m_listHeader_.m_reset_;
                  }
                }
                else
                {
                  sourceToken = m_listHeader_.m_reset_;
                  
                  sourceToken = getVirginBefore(sourceToken, strength);
                }
              }
              else
              {
                sourceToken = getVirginBefore(sourceToken, strength);
              }
              
            }
            else
            {
              top = false;
              m_listHeader_[m_resultLength_] = new TokenListHeader();
              m_listHeader_[m_resultLength_].m_previousCE_ = 0;
              m_listHeader_[m_resultLength_].m_previousContCE_ = 0;
              m_listHeader_[m_resultLength_].m_indirect_ = true;
              


              int strength = (specs & 0x3) - '\001';
              int baseCE = INDIRECT_BOUNDARIES_m_parsedToken_.m_indirectIndex_].m_startCE_;
              
              int baseContCE = INDIRECT_BOUNDARIES_m_parsedToken_.m_indirectIndex_].m_startContCE_;
              
              int[] ce = new int[2];
              CollationParsedRuleBuilder.InverseUCA invuca = CollationParsedRuleBuilder.INVERSE_UCA_;
              
              invuca.getInversePrevCE(baseCE, baseContCE, strength, ce);
              
              m_listHeader_[m_resultLength_].m_baseCE_ = ce[0];
              m_listHeader_[m_resultLength_].m_baseContCE_ = ce[1];
              m_listHeader_[m_resultLength_].m_nextCE_ = 0;
              m_listHeader_[m_resultLength_].m_nextContCE_ = 0;
              
              sourceToken = new Token();
              expandNext = initAReset(0, sourceToken);
            }
          }
          




          if (sourceToken == null) {
            if (m_listHeader_[m_resultLength_] == null) {
              m_listHeader_[m_resultLength_] = new TokenListHeader();
            }
            








            if (!top) {
              CollationElementIterator coleiter = RuleBasedCollator.UCA_.getCollationElementIterator(m_source_.substring(m_parsedToken_.m_charsOffset_, m_parsedToken_.m_charsOffset_ + m_parsedToken_.m_charsLen_));
              




              int CE = coleiter.next();
              
              int expand = coleiter.getOffset() + m_parsedToken_.m_charsOffset_;
              
              int SecondCE = coleiter.next();
              
              m_listHeader_[m_resultLength_].m_baseCE_ = (CE & 0xFF3F);
              
              if (RuleBasedCollator.isContinuation(SecondCE)) {
                m_listHeader_[m_resultLength_].m_baseContCE_ = SecondCE;
              }
              else
              {
                m_listHeader_[m_resultLength_].m_baseContCE_ = 0;
              }
              m_listHeader_[m_resultLength_].m_nextCE_ = 0;
              m_listHeader_[m_resultLength_].m_nextContCE_ = 0;
              m_listHeader_[m_resultLength_].m_previousCE_ = 0;
              m_listHeader_[m_resultLength_].m_previousContCE_ = 0;
              m_listHeader_[m_resultLength_].m_indirect_ = false;
              sourceToken = new Token();
              expandNext = initAReset(expand, sourceToken);
            }
            else {
              top = false;
              m_listHeader_[m_resultLength_].m_previousCE_ = 0;
              m_listHeader_[m_resultLength_].m_previousContCE_ = 0;
              m_listHeader_[m_resultLength_].m_indirect_ = true;
              IndirectBoundaries ib = INDIRECT_BOUNDARIES_[m_parsedToken_.m_indirectIndex_];
              
              m_listHeader_[m_resultLength_].m_baseCE_ = m_startCE_;
              
              m_listHeader_[m_resultLength_].m_baseContCE_ = m_startContCE_;
              
              m_listHeader_[m_resultLength_].m_nextCE_ = m_limitCE_;
              
              m_listHeader_[m_resultLength_].m_nextContCE_ = m_limitContCE_;
              
              sourceToken = new Token();
              expandNext = initAReset(0, sourceToken);
            }
          }
          else {
            top = false;
          }
        }
        

        lastToken = sourceToken;
      }
    }
    if ((m_resultLength_ > 0) && (m_listHeader_[(m_resultLength_ - 1)].m_first_ == null))
    {
      m_resultLength_ -= 1;
    }
    return m_resultLength_;
  }
  







  private static final void throwParseException(String rules, int offset)
    throws ParseException
  {
    String precontext = rules.substring(0, offset);
    String postcontext = rules.substring(offset, rules.length());
    StringBuffer error = new StringBuffer("Parse error occurred in rule at offset ");
    
    error.append(offset);
    error.append("\n after the prefix \"");
    error.append(precontext);
    error.append("\" before the suffix \"");
    error.append(postcontext);
    throw new ParseException(error.toString(), offset);
  }
  
  private final boolean doSetTop() {
    m_parsedToken_.m_charsOffset_ = m_extraCurrent_;
    m_source_.append(65534);
    IndirectBoundaries ib = INDIRECT_BOUNDARIES_[m_parsedToken_.m_indirectIndex_];
    
    m_source_.append((char)(m_startCE_ >> 16));
    m_source_.append((char)(m_startCE_ & 0xFFFF));
    m_extraCurrent_ += 3;
    if (INDIRECT_BOUNDARIES_m_parsedToken_.m_indirectIndex_].m_startContCE_ == 0)
    {
      m_parsedToken_.m_charsLen_ = 3;
    }
    else {
      m_source_.append((char)(INDIRECT_BOUNDARIES_m_parsedToken_.m_indirectIndex_].m_startContCE_ >> 16));
      

      m_source_.append((char)(INDIRECT_BOUNDARIES_m_parsedToken_.m_indirectIndex_].m_startContCE_ & 0xFFFF));
      

      m_extraCurrent_ += 2;
      m_parsedToken_.m_charsLen_ = 5;
    }
    return true;
  }
  






  private int parseNextToken(boolean startofrules)
    throws ParseException
  {
    boolean variabletop = false;
    boolean top = false;
    boolean inchars = true;
    boolean inquote = false;
    boolean wasinquote = false;
    byte before = 0;
    boolean isescaped = false;
    int newextensionlen = 0;
    int extensionoffset = 0;
    int newstrength = -1;
    
    m_parsedToken_.m_charsLen_ = 0;
    m_parsedToken_.m_charsOffset_ = 0;
    m_parsedToken_.m_prefixOffset_ = 0;
    m_parsedToken_.m_prefixLen_ = 0;
    m_parsedToken_.m_indirectIndex_ = '\000';
    
    int limit = m_rules_.length();
    label1558: while (m_current_ < limit) {
      char ch = m_source_.charAt(m_current_);
      if (inquote) {
        if (ch == '\'') {
          inquote = false;

        }
        else if ((m_parsedToken_.m_charsLen_ == 0) || (inchars)) {
          if (m_parsedToken_.m_charsLen_ == 0) {
            m_parsedToken_.m_charsOffset_ = m_extraCurrent_;
          }
          m_parsedToken_.m_charsLen_ += 1;
        }
        else {
          if (newextensionlen == 0) {
            extensionoffset = m_extraCurrent_;
          }
          newextensionlen++;
        }
        
      }
      else if (isescaped) {
        isescaped = false;
        if (newstrength == -1) {
          throwParseException(m_rules_, m_current_);
        }
        if ((ch != 0) && (m_current_ != limit)) {
          if (inchars) {
            if (m_parsedToken_.m_charsLen_ == 0) {
              m_parsedToken_.m_charsOffset_ = m_current_;
            }
            m_parsedToken_.m_charsLen_ += 1;
          }
          else {
            if (newextensionlen == 0) {
              extensionoffset = m_current_;
            }
            newextensionlen++;
          }
          
        }
      }
      else if (!UCharacterProperty.isRuleWhiteSpace(ch))
      {
        switch (ch) {
        case '=': 
          if (newstrength != -1) {
            return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
          }
          




          if (startofrules == true) {
            m_parsedToken_.m_indirectIndex_ = '\005';
            top = doSetTop();
            return doEndParseNextToken(-559038737, top, extensionoffset, newextensionlen, variabletop, before);
          }
          



          newstrength = 15;
          break;
        case ',': 
          if (newstrength != -1) {
            return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
          }
          




          if (startofrules == true) {
            m_parsedToken_.m_indirectIndex_ = '\005';
            top = doSetTop();
            return doEndParseNextToken(-559038737, top, extensionoffset, newextensionlen, variabletop, before);
          }
          



          newstrength = 2;
          break;
        case ';': 
          if (newstrength != -1) {
            return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
          }
          




          if (startofrules == true) {
            m_parsedToken_.m_indirectIndex_ = '\005';
            top = doSetTop();
            return doEndParseNextToken(-559038737, top, extensionoffset, newextensionlen, variabletop, before);
          }
          



          newstrength = 1;
          break;
        case '<': 
          if (newstrength != -1) {
            return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
          }
          




          if (startofrules == true) {
            m_parsedToken_.m_indirectIndex_ = '\005';
            top = doSetTop();
            return doEndParseNextToken(-559038737, top, extensionoffset, newextensionlen, variabletop, before);
          }
          





          if (m_source_.charAt(m_current_ + 1) == '<') {
            m_current_ += 1;
            if (m_source_.charAt(m_current_ + 1) == '<') {
              m_current_ += 1;
              newstrength = 2;
            }
            else {
              newstrength = 1;
            }
          }
          else {
            newstrength = 0;
          }
          break;
        case '&': 
          if (newstrength != -1) {
            return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
          }
          



          newstrength = -559038737;
          break;
        
        case '[': 
          m_optionEnd_ = m_rules_.indexOf(']', m_current_);
          if (m_optionEnd_ == -1) break label1558;
          byte result = readAndSetOption();
          m_current_ = m_optionEnd_;
          if ((result & 0x4) != 0) {
            if (newstrength == -559038737) {
              top = doSetTop();
              if (before != 0)
              {


                m_source_.append('-');
                m_source_.append((char)before);
                m_extraCurrent_ += 2;
                m_parsedToken_.m_charsLen_ += 2;
              }
              m_current_ += 1;
              return doEndParseNextToken(newstrength, true, extensionoffset, newextensionlen, variabletop, before);
            }
            




            throwParseException(m_rules_, m_current_);

          }
          else if ((result & 0x8) != 0) {
            if ((newstrength != -559038737) && (newstrength != -1))
            {
              variabletop = true;
              m_parsedToken_.m_charsOffset_ = m_extraCurrent_;
              
              m_source_.append(65535);
              m_extraCurrent_ += 1;
              m_current_ += 1;
              m_parsedToken_.m_charsLen_ = 1;
              return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
            }
            




            throwParseException(m_rules_, m_current_);

          }
          else if ((result & 0x3) != 0) {
            if (newstrength == -559038737) {
              before = (byte)(result & 0x3);
            }
            else {
              throwParseException(m_rules_, m_current_);
            }
          }
          
          break;
        case '/': 
          wasinquote = false;
          
          inchars = false;
          break;
        case '\\': 
          isescaped = true;
          break;
        
        case '\'': 
          if (newstrength == -1)
          {
            throwParseException(m_rules_, m_current_);
          }
          inquote = true;
          if (inchars) {
            if (!wasinquote) {
              m_parsedToken_.m_charsOffset_ = m_extraCurrent_;
            }
            if (m_parsedToken_.m_charsLen_ != 0) {
              m_source_.append(m_source_.substring(m_current_ - m_parsedToken_.m_charsLen_, m_current_));
              

              m_extraCurrent_ += m_parsedToken_.m_charsLen_;
            }
            m_parsedToken_.m_charsLen_ += 1;
          }
          else {
            if (!wasinquote) {
              extensionoffset = m_extraCurrent_;
            }
            if (newextensionlen != 0) {
              m_source_.append(m_source_.substring(m_current_ - newextensionlen, m_current_));
              

              m_extraCurrent_ += newextensionlen;
            }
            newextensionlen++;
          }
          wasinquote = true;
          m_current_ += 1;
          ch = m_source_.charAt(m_current_);
          if (ch != '\'') break label1558;
          m_source_.append(ch);
          m_extraCurrent_ += 1;
          inquote = false; break;
        



        case '@': 
          if (newstrength == -1)
            m_options_.m_isFrenchCollation_ = true;
          break;
        








        case '|': 
          m_parsedToken_.m_prefixOffset_ = m_parsedToken_.m_charsOffset_;
          
          m_parsedToken_.m_prefixLen_ = m_parsedToken_.m_charsLen_;
          
          if (inchars) {
            if (!wasinquote) {
              m_parsedToken_.m_charsOffset_ = m_extraCurrent_;
            }
            if (m_parsedToken_.m_charsLen_ != 0) {
              String prefix = m_source_.substring(m_current_ - m_parsedToken_.m_charsLen_, m_current_);
              

              m_source_.append(prefix);
              m_extraCurrent_ += m_parsedToken_.m_charsLen_;
            }
            m_parsedToken_.m_charsLen_ += 1;
          }
          wasinquote = true;
          do {
            m_current_ += 1;
            ch = m_source_.charAt(m_current_);
          }
          while (UCharacterProperty.isRuleWhiteSpace(ch));
          break;
        case '!': 
          break;
        }
        if (newstrength == -1) {
          throwParseException(m_rules_, m_current_);
        }
        if ((isSpecialChar(ch)) && (!inquote)) {
          throwParseException(m_rules_, m_current_);
        }
        if ((ch != 0) || (m_current_ + 1 != limit))
        {

          if (inchars) {
            if (m_parsedToken_.m_charsLen_ == 0) {
              m_parsedToken_.m_charsOffset_ = m_current_;
            }
            m_parsedToken_.m_charsLen_ += 1;
          }
          else {
            if (newextensionlen == 0) {
              extensionoffset = m_current_;
            }
            newextensionlen++;
          }
        }
      }
      

      if ((wasinquote) && 
        (ch != '\'')) {
        m_source_.append(ch);
        m_extraCurrent_ += 1;
      }
      
      m_current_ += 1;
    }
    return doEndParseNextToken(newstrength, top, extensionoffset, newextensionlen, variabletop, before);
  }
  










  private int doEndParseNextToken(int newstrength, boolean top, int extensionoffset, int newextensionlen, boolean variabletop, int before)
    throws ParseException
  {
    if (newstrength == -1) {
      return -1;
    }
    if ((m_parsedToken_.m_charsLen_ == 0) && (!top)) {
      throwParseException(m_rules_, m_current_);
    }
    
    m_parsedToken_.m_strength_ = newstrength;
    

    m_parsedToken_.m_extensionOffset_ = extensionoffset;
    m_parsedToken_.m_extensionLen_ = newextensionlen;
    m_parsedToken_.m_flags_ = ((char)((variabletop ? 8 : 0) | (top ? 4 : 0) | before));
    

    return m_current_;
  }
  








  private Token getVirginBefore(Token sourcetoken, int strength)
    throws ParseException
  {
    if (sourcetoken != null) {
      int offset = m_source_ & 0xFFFFFF;
      m_UCAColEIter_.setText(m_source_.substring(offset, offset + 1));
    }
    else {
      m_UCAColEIter_.setText(m_source_.substring(m_parsedToken_.m_charsOffset_, m_parsedToken_.m_charsOffset_ + 1));
    }
    


    int basece = m_UCAColEIter_.next() & 0xFF3F;
    int basecontce = m_UCAColEIter_.next();
    if (basecontce == -1) {
      basecontce = 0;
    }
    
    int invpos = CollationParsedRuleBuilder.INVERSE_UCA_.getInversePrevCE(basece, basecontce, strength, m_utilCEBuffer_);
    

    int ch = INVERSE_UCA_m_table_[(3 * invpos + 2)];
    
    if ((ch & 0xFFF00000) != 0) {
      int offset = ch & 0xFFFFF;
      ch = INVERSE_UCA_m_continuations_[offset];
    }
    
    m_source_.append((char)ch);
    m_extraCurrent_ += 1;
    m_parsedToken_.m_charsOffset_ = (m_extraCurrent_ - 1);
    m_parsedToken_.m_charsLen_ = 1;
    





    m_utilToken_.m_source_ = (m_parsedToken_.m_charsLen_ << 24 | m_parsedToken_.m_charsOffset_);
    
    m_utilToken_.m_rules_ = m_source_;
    sourcetoken = (Token)m_hashTable_.get(m_utilToken_);
    


    if ((sourcetoken != null) && (m_strength_ != -559038737))
    {

      m_source_.setCharAt(m_extraCurrent_ - 1, 65534);
      m_source_.append(ch);
      m_extraCurrent_ += 1;
      m_parsedToken_.m_charsLen_ += 1;
      m_listHeader_[m_resultLength_] = new TokenListHeader();
      m_listHeader_[m_resultLength_].m_baseCE_ = (m_utilCEBuffer_[0] & 0xFF3F);
      
      if (RuleBasedCollator.isContinuation(m_utilCEBuffer_[1])) {
        m_listHeader_[m_resultLength_].m_baseContCE_ = m_utilCEBuffer_[1];
      }
      else
      {
        m_listHeader_[m_resultLength_].m_baseContCE_ = 0;
      }
      m_listHeader_[m_resultLength_].m_nextCE_ = 0;
      m_listHeader_[m_resultLength_].m_nextContCE_ = 0;
      m_listHeader_[m_resultLength_].m_previousCE_ = 0;
      m_listHeader_[m_resultLength_].m_previousContCE_ = 0;
      m_listHeader_[m_resultLength_].m_indirect_ = false;
      sourcetoken = new Token();
      initAReset(-1, sourcetoken);
    }
    return sourcetoken;
  }
  











  private int initAReset(int expand, Token targetToken)
    throws ParseException
  {
    if (m_resultLength_ == m_listHeader_.length - 1)
    {

      TokenListHeader[] temp = new TokenListHeader[m_resultLength_ << 1];
      System.arraycopy(m_listHeader_, 0, temp, 0, m_resultLength_ + 1);
      m_listHeader_ = temp;
    }
    
    m_rules_ = m_source_;
    m_source_ = (m_parsedToken_.m_charsLen_ << 24 | m_parsedToken_.m_charsOffset_);
    
    m_expansion_ = (m_parsedToken_.m_extensionLen_ << 24 | m_parsedToken_.m_extensionOffset_);
    
    if (m_parsedToken_.m_prefixOffset_ != 0) {
      throwParseException(m_rules_, m_parsedToken_.m_charsOffset_ - 1);
    }
    
    m_prefix_ = 0;
    
    m_polarity_ = 1;
    m_strength_ = -559038737;
    m_next_ = null;
    m_previous_ = null;
    m_CELength_ = 0;
    m_expCELength_ = 0;
    m_listHeader_ = m_listHeader_[m_resultLength_];
    m_listHeader_[m_resultLength_].m_first_ = null;
    m_listHeader_[m_resultLength_].m_last_ = null;
    m_listHeader_[m_resultLength_].m_first_ = null;
    m_listHeader_[m_resultLength_].m_last_ = null;
    m_listHeader_[m_resultLength_].m_reset_ = targetToken;
    









    int result = 0;
    if (expand > 0)
    {
      if (m_parsedToken_.m_charsLen_ > 1) {
        m_source_ = (expand - m_parsedToken_.m_charsOffset_ << 24 | m_parsedToken_.m_charsOffset_);
        


        result = m_parsedToken_.m_charsLen_ + m_parsedToken_.m_charsOffset_ - expand << 24 | expand;
      }
    }
    


    m_resultLength_ += 1;
    m_hashTable_.put(targetToken, targetToken);
    return result;
  }
  





  private static final boolean isSpecialChar(char ch)
  {
    return ((ch <= '/') && (ch >= ' ')) || ((ch <= '?') && (ch >= ':')) || ((ch <= '`') && (ch >= '[')) || ((ch <= '~') && (ch >= '}')) || (ch == '{');
  }
  


  private UnicodeSet readAndSetUnicodeSet(String source, int start)
    throws ParseException
  {
    while (source.charAt(start) != '[') {
      start++;
    }
    

    int noOpenBraces = 1;
    int current = 1;
    while ((start + current < source.length()) && (noOpenBraces != 0)) {
      if (source.charAt(start + current) == '[') {
        noOpenBraces++;
      } else if (source.charAt(start + current) == ']') {
        noOpenBraces--;
      }
      current++;
    }
    

    if ((noOpenBraces != 0) || (source.indexOf("]", start + current) == -1)) {
      throwParseException(m_rules_, start);
    }
    return new UnicodeSet(source.substring(start, start + current));
  }
  




  private int m_optionarg_ = 0;
  
  private int readOption(String rules, int start, int optionend)
  {
    m_optionarg_ = 0;
    int i = 0;
    while (i < RULES_OPTIONS_.length) {
      String option = RULES_OPTIONS_m_name_;
      int optionlength = option.length();
      if ((rules.length() > start + optionlength) && (option.equalsIgnoreCase(rules.substring(start, start + optionlength))))
      {

        if (optionend - start <= optionlength) break;
        m_optionarg_ = (start + optionlength + 1);
        
        do
        {
          m_optionarg_ += 1;
          if (m_optionarg_ >= optionend) break; } while (UCharacter.isWhitespace(rules.charAt(m_optionarg_))); break;
      }
      




      i++;
    }
    if (i == RULES_OPTIONS_.length) {
      i = -1;
    }
    return i;
  }
  




  private byte readAndSetOption()
    throws ParseException
  {
    int start = m_current_ + 1;
    int i = readOption(m_rules_, start, m_optionEnd_);
    
    int optionarg = m_optionarg_;
    
    if (i < 0) {
      throwParseException(m_rules_, start);
    }
    
    if (i < 7) {
      if (optionarg != 0) {
        for (int j = 0; j < RULES_OPTIONS_m_subOptions_.length; 
            j++) {
          String subname = RULES_OPTIONS_m_subOptions_[j];
          int size = optionarg + subname.length();
          if ((m_rules_.length() > size) && (subname.equalsIgnoreCase(m_rules_.substring(optionarg, size))))
          {

            setOptions(m_options_, RULES_OPTIONS_m_attribute_, RULES_OPTIONS_m_subOptionAttributeValues_[j]);
            
            return 16;
          }
        }
      }
      throwParseException(m_rules_, optionarg);
    } else {
      if (i == 7) {
        return 24;
      }
      if (i == 8) {
        return 16;
      }
      if (i == 9) {
        if (optionarg != 0) {
          for (int j = 0; j < RULES_OPTIONS_m_subOptions_.length; 
              j++) {
            String subname = RULES_OPTIONS_m_subOptions_[j];
            int size = optionarg + subname.length();
            if ((m_rules_.length() > size) && (subname.equalsIgnoreCase(m_rules_.substring(optionarg, optionarg + subname.length()))))
            {


              return (byte)(0x10 | RULES_OPTIONS_m_subOptionAttributeValues_[j] + 1);
            }
          }
        }
        

        throwParseException(m_rules_, optionarg);
      } else {
        if (i == 10)
        {

          m_parsedToken_.m_indirectIndex_ = '\000';
          return 20;
        }
        if (i < 13) {
          for (int j = 0; j < RULES_OPTIONS_m_subOptions_.length; j++) {
            String subname = RULES_OPTIONS_m_subOptions_[j];
            int size = optionarg + subname.length();
            if ((m_rules_.length() > size) && (subname.equalsIgnoreCase(m_rules_.substring(optionarg, size))))
            {

              m_parsedToken_.m_indirectIndex_ = ((char)(i - 10 + (j << 1)));
              return 20;
            }
          }
          throwParseException(m_rules_, optionarg);
        } else {
          if ((i == 13) || (i == 14))
          {
            int noOpenBraces = 1;
            m_current_ += 1;
            while ((m_current_ < m_source_.length()) && (noOpenBraces != 0)) {
              if (m_source_.charAt(m_current_) == '[') {
                noOpenBraces++;
              } else if (m_source_.charAt(m_current_) == ']') {
                noOpenBraces--;
              }
              m_current_ += 1;
            }
            m_optionEnd_ = (m_current_ - 1);
            return 16;
          }
          
          throwParseException(m_rules_, optionarg);
        } } }
    return 16;
  }
  






  private void setOptions(OptionSet optionset, int attribute, int value)
  {
    switch (attribute) {
    case 6: 
      m_isHiragana4_ = (value == 17);
      
      break;
    case 0: 
      m_isFrenchCollation_ = (value == 17);
      
      break;
    case 1: 
      m_isAlternateHandlingShifted_ = (value == 20);
      

      break;
    case 2: 
      m_caseFirst_ = value;
      break;
    case 3: 
      m_isCaseLevel_ = (value == 17);
      
      break;
    case 4: 
      if (value == 17) {
        value = 17;
      }
      m_decomposition_ = value;
      break;
    case 5: 
      m_strength_ = value;
      break;
    }
    
  }
  
  UnicodeSet getTailoredSet()
    throws ParseException
  {
    boolean startOfRules = true;
    UnicodeSet tailored = new UnicodeSet();
    
    CanonicalIterator it = new CanonicalIterator("");
    
    m_parsedToken_.m_strength_ = -1;
    int sourcelimit = m_source_.length();
    

    while (m_current_ < sourcelimit) {
      m_parsedToken_.m_prefixOffset_ = 0;
      if (parseNextToken(startOfRules) >= 0)
      {


        startOfRules = false;
        

        if (m_parsedToken_.m_strength_ != -559038737) {
          it.setSource(m_source_.substring(m_parsedToken_.m_charsOffset_, m_parsedToken_.m_charsOffset_ + m_parsedToken_.m_charsLen_));
          

          String pattern = it.next();
          while (pattern != null) {
            if (Normalizer.quickCheck(pattern, Normalizer.FCD, 0) != Normalizer.NO) {
              tailored.add(pattern);
            }
            pattern = it.next();
          }
        }
      } }
    return tailored;
  }
  
  private final void extractSetsFromRules(String rules) throws ParseException {
    int optionNumber = -1;
    int setStart = 0;
    int i = 0;
    while (i < rules.length()) {
      if (rules.charAt(i) == '[') {
        optionNumber = readOption(rules, i + 1, rules.length());
        setStart = m_optionarg_;
        if (optionNumber == 13) {
          UnicodeSet newSet = readAndSetUnicodeSet(rules, setStart);
          if (m_copySet_ == null) {
            m_copySet_ = newSet;
          } else {
            m_copySet_.addAll(newSet);
          }
        } else if (optionNumber == 14) {
          UnicodeSet newSet = readAndSetUnicodeSet(rules, setStart);
          if (m_removeSet_ == null) {
            m_removeSet_ = newSet;
          } else {
            m_removeSet_.addAll(newSet);
          }
        }
      }
      i++;
    }
  }
}
