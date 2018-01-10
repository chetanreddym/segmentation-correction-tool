package com.ibm.icu.text;

import com.ibm.icu.impl.IntTrieBuilder;
import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.impl.TrieBuilder;
import com.ibm.icu.impl.TrieBuilder.DataManipulate;
import com.ibm.icu.impl.TrieIterator;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.RangeValueIterator;
import com.ibm.icu.util.RangeValueIterator.Element;
import com.ibm.icu.util.VersionInfo;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;





















final class CollationParsedRuleBuilder
{
  static final InverseUCA INVERSE_UCA_;
  private static final String INV_UCA_VERSION_MISMATCH_ = "UCA versions of UCA and inverse UCA should match";
  private static final String UCA_NOT_INSTANTIATED_ = "UCA is not instantiated!";
  private static final int CE_BASIC_STRENGTH_LIMIT_ = 3;
  private static final int CE_STRENGTH_LIMIT_ = 16;
  
  CollationParsedRuleBuilder(String rules)
    throws ParseException
  {
    m_parser_ = new CollationRuleParser(rules);
    m_parser_.assembleTokenList();
    m_utilColEIter_ = RuleBasedCollator.UCA_.getCollationElementIterator("");
  }
  







  static class InverseUCA
  {
    int[] m_table_;
    






    char[] m_continuations_;
    






    VersionInfo m_UCA_version_;
    







    InverseUCA() {}
    






    final int getInversePrevCE(int ce, int contce, int strength, int[] prevresult)
    {
      int result = findInverseCE(ce, contce);
      
      if (result < 0) {
        prevresult[0] = -1;
        return -1;
      }
      
      ce &= CollationParsedRuleBuilder.STRENGTH_MASK_[strength];
      contce &= CollationParsedRuleBuilder.STRENGTH_MASK_[strength];
      
      prevresult[0] = ce;
      prevresult[1] = contce;
      


      while (((prevresult[0] & CollationParsedRuleBuilder.STRENGTH_MASK_[strength]) == ce) && ((prevresult[1] & CollationParsedRuleBuilder.STRENGTH_MASK_[strength]) == contce) && (result > 0))
      {


        prevresult[0] = m_table_[(3 * --result)];
        prevresult[1] = m_table_[(3 * result + 1)];
      }
      return result;
    }
    






    int findInverseCE(int ce, int contce)
    {
      int bottom = 0;
      int top = m_table_.length / 3;
      int result = 0;
      
      while (bottom < top - 1) {
        result = top + bottom >> 1;
        int first = m_table_[(3 * result)];
        int second = m_table_[(3 * result + 1)];
        int comparison = Utility.compareUnsigned(first, ce);
        if (comparison > 0) {
          top = result;
        }
        else if (comparison < 0) {
          bottom = result;

        }
        else if (second > contce) {
          top = result;
        } else {
          if (second >= contce) break;
          bottom = result;
        }
      }
      




      return result;
    }
    








    void getInverseGapPositions(CollationRuleParser.TokenListHeader listheader)
      throws Exception
    {
      CollationRuleParser.Token token = m_first_;
      int tokenstrength = m_strength_;
      
      for (int i = 0; i < 3; i++) {
        m_gapsHi_[(3 * i)] = 0;
        m_gapsHi_[(3 * i + 1)] = 0;
        m_gapsHi_[(3 * i + 2)] = 0;
        m_gapsLo_[(3 * i)] = 0;
        m_gapsLo_[(3 * i + 1)] = 0;
        m_gapsLo_[(3 * i + 2)] = 0;
        m_numStr_[i] = 0;
        m_fStrToken_[i] = null;
        m_lStrToken_[i] = null;
        m_pos_[i] = -1;
      }
      
      if ((m_baseCE_ >>> 24 >= UCA_CONSTANTS_PRIMARY_IMPLICIT_MIN_) && (m_baseCE_ >>> 24 < UCA_CONSTANTS_PRIMARY_IMPLICIT_MAX_))
      {




        m_pos_[0] = 0;
        int t1 = m_baseCE_;
        int t2 = m_baseContCE_;
        m_gapsLo_[0] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
        
        m_gapsLo_[1] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
        
        m_gapsLo_[2] = CollationParsedRuleBuilder.mergeCE(t1, t2, 2);
        
        if (m_baseCE_ < -285212672)
        {


          t2 += 33554432;


        }
        else
        {

          t2 += 131072;
        }
        m_gapsHi_[0] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
        
        m_gapsHi_[1] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
        
        m_gapsHi_[2] = CollationParsedRuleBuilder.mergeCE(t1, t2, 2);

      }
      else if ((m_indirect_ == true) && (m_nextCE_ != 0))
      {
        m_pos_[0] = 0;
        int t1 = m_baseCE_;
        int t2 = m_baseContCE_;
        m_gapsLo_[0] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
        
        m_gapsLo_[1] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
        
        m_gapsLo_[2] = CollationParsedRuleBuilder.mergeCE(t1, t2, 2);
        
        t1 = m_nextCE_;
        t2 = m_nextContCE_;
        m_gapsHi_[0] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
        
        m_gapsHi_[1] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
        
        m_gapsHi_[2] = CollationParsedRuleBuilder.mergeCE(t1, t2, 2);
      }
      else
      {
        for (;;) {
          if (tokenstrength < 3) {
            m_pos_[tokenstrength] = getInverseNext(listheader, tokenstrength);
            

            if (m_pos_[tokenstrength] >= 0) {
              m_fStrToken_[tokenstrength] = token;
            }
          }
          






          while ((token != null) && (m_strength_ >= tokenstrength))
          {
            if (tokenstrength < 3) {
              m_lStrToken_[tokenstrength] = token;
            }
            token = m_next_;
          }
          if (tokenstrength < 2)
          {

            if (m_pos_[tokenstrength] == m_pos_[(tokenstrength + 1)])
            {
              m_fStrToken_[tokenstrength] = m_fStrToken_[(tokenstrength + 1)];
              

              m_fStrToken_[(tokenstrength + 1)] = null;
              m_lStrToken_[(tokenstrength + 1)] = null;
              m_pos_[(tokenstrength + 1)] = -1;
            }
          }
          if (token == null) break;
          tokenstrength = m_strength_;
        }
        



        for (int st = 0; st < 3; st++) {
          int pos = m_pos_[st];
          if (pos >= 0) {
            int t1 = m_table_[(3 * pos)];
            int t2 = m_table_[(3 * pos + 1)];
            m_gapsHi_[(3 * st)] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
            
            m_gapsHi_[(3 * st + 1)] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
            
            m_gapsHi_[(3 * st + 2)] = ((t1 & 0x3F) << 24 | (t2 & 0x3F) << 16);
            
            pos--;
            t1 = m_table_[(3 * pos)];
            t2 = m_table_[(3 * pos + 1)];
            m_gapsLo_[(3 * st)] = CollationParsedRuleBuilder.mergeCE(t1, t2, 0);
            
            m_gapsLo_[(3 * st + 1)] = CollationParsedRuleBuilder.mergeCE(t1, t2, 1);
            
            m_gapsLo_[(3 * st + 2)] = ((t1 & 0x3F) << 24 | (t2 & 0x3F) << 16);
          }
        }
      }
    }
    









    private final int getInverseNext(CollationRuleParser.TokenListHeader listheader, int strength)
    {
      int ce = m_baseCE_;
      int secondce = m_baseContCE_;
      int result = findInverseCE(ce, secondce);
      
      if (result < 0) {
        return -1;
      }
      
      ce &= CollationParsedRuleBuilder.STRENGTH_MASK_[strength];
      secondce &= CollationParsedRuleBuilder.STRENGTH_MASK_[strength];
      
      int nextce = ce;
      int nextcontce = secondce;
      

      while (((nextce & CollationParsedRuleBuilder.STRENGTH_MASK_[strength]) == ce) && ((nextcontce & CollationParsedRuleBuilder.STRENGTH_MASK_[strength]) == secondce)) {
        nextce = m_table_[(3 * ++result)];
        nextcontce = m_table_[(3 * result + 1)];
      }
      
      m_nextCE_ = nextce;
      m_nextContCE_ = nextcontce;
      
      return result;
    }
  }
  





















  static
  {
    try
    {
      String invdat = "/com/ibm/icu/impl/data/invuca.icu";
      InputStream i = CollationParsedRuleBuilder.class.getResourceAsStream(invdat);
      BufferedInputStream b = new BufferedInputStream(i, 110000);
      INVERSE_UCA_ = CollatorReader.readInverseUCA(b);
      b.close();
      i.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
    if (RuleBasedCollator.UCA_ != null) {
      if (!INVERSE_UCA_m_UCA_version_.equals(UCA_m_UCA_version_)) {
        throw new RuntimeException("UCA versions of UCA and inverse UCA should match");
      }
    } else {
      throw new RuntimeException("UCA is not instantiated!");
    }
  }
  






  void setRules(RuleBasedCollator collator)
    throws Exception
  {
    if ((m_parser_.m_resultLength_ > 0) || (m_parser_.m_removeSet_ != null))
    {
      assembleTailoringTable(collator);
    }
    else
    {
      collator.setWithUCATables();
    }
    
    m_parser_.setDefaultOptionsInCollator(collator);
  }
  
  private void copyRangeFromUCA(BuildTable t, int start, int end) {
    int u = 0;
    for (u = start; u <= end; u++)
    {
      int CE = m_mapping_.getValue(u);
      if ((CE == -268435456) || ((isContractionTableElement(CE)) && (getCE(m_contractions_, CE, 0) == -268435456)))
      {






        m_utilElement_.m_uchars_ = UCharacter.toString(u);
        m_utilElement_.m_cPoints_ = m_utilElement_.m_uchars_;
        m_utilElement_.m_prefix_ = 0;
        m_utilElement_.m_CELength_ = 0;
        m_utilColEIter_.setText(m_utilElement_.m_uchars_);
        while (CE != -1) {
          CE = m_utilColEIter_.next();
          if (CE != -1) {
            m_utilElement_.m_CEs_[(m_utilElement_.m_CELength_++)] = CE;
          }
        }
        
        addAnElement(t, m_utilElement_);
      }
    }
  }
  






































  void assembleTailoringTable(RuleBasedCollator collator)
    throws Exception
  {
    for (int i = 0; i < m_parser_.m_resultLength_; i++)
    {


      if (m_parser_.m_listHeader_[i].m_first_ != null)
      {



        initBuffers(m_parser_.m_listHeader_[i]);
      }
    }
    
    if (m_parser_.m_variableTop_ != null)
    {
      m_parser_.m_options_.m_variableTopValue_ = (m_parser_.m_variableTop_.m_CE_[0] >>> 16);
      

      if (m_parser_.m_variableTop_.m_listHeader_.m_first_ == m_parser_.m_variableTop_)
      {
        m_parser_.m_variableTop_.m_listHeader_.m_first_ = m_parser_.m_variableTop_.m_next_;
      }
      
      if (m_parser_.m_variableTop_.m_listHeader_.m_last_ == m_parser_.m_variableTop_)
      {

        m_parser_.m_variableTop_.m_listHeader_.m_last_ = m_parser_.m_variableTop_.m_previous_;
      }
      
      if (m_parser_.m_variableTop_.m_next_ != null) {
        m_parser_.m_variableTop_.m_next_.m_previous_ = m_parser_.m_variableTop_.m_previous_;
      }
      
      if (m_parser_.m_variableTop_.m_previous_ != null) {
        m_parser_.m_variableTop_.m_previous_.m_next_ = m_parser_.m_variableTop_.m_next_;
      }
    }
    

    BuildTable t = new BuildTable(m_parser_);
    



    for (int i = 0; i < m_parser_.m_resultLength_; i++)
    {


      createElements(t, m_parser_.m_listHeader_[i]);
    }
    
    m_utilElement_.clear();
    StringBuffer str = new StringBuffer();
    


    copyRangeFromUCA(t, 0, 255);
    

    if (m_parser_.m_copySet_ != null) {
      int i = 0;
      for (i = 0; i < m_parser_.m_copySet_.getRangeCount(); i++) {
        copyRangeFromUCA(t, m_parser_.m_copySet_.getRangeStart(i), m_parser_.m_copySet_.getRangeEnd(i));
      }
    }
    

    char[] conts = RuleBasedCollator.UCA_CONTRACTIONS_;
    int offset = 0;
    while (conts[offset] != 0)
    {
      int tailoredCE = m_mapping_.getValue(conts[offset]);
      if (tailoredCE != -268435456) {
        boolean needToAdd = true;
        if ((isContractionTableElement(tailoredCE)) && 
          (isTailored(m_contractions_, tailoredCE, conts, offset + 1) == true))
        {
          needToAdd = false;
        }
        
        if ((m_parser_.m_removeSet_ != null) && (m_parser_.m_removeSet_.contains(conts[offset]))) {
          needToAdd = false;
        }
        

        if (needToAdd == true)
        {
          m_utilElement_.m_prefix_ = 0;
          m_utilElement_.m_prefixChars_ = null;
          m_utilElement_.m_cPoints_ = m_utilElement_.m_uchars_;
          str.delete(0, str.length());
          str.append(conts[offset]);
          str.append(conts[(offset + 1)]);
          if (conts[(offset + 2)] != 0) {
            str.append(conts[(offset + 2)]);
          }
          m_utilElement_.m_uchars_ = str.toString();
          m_utilElement_.m_CELength_ = 0;
          m_utilColEIter_.setText(m_utilElement_.m_uchars_);
          for (;;) {
            int CE = m_utilColEIter_.next();
            if (CE == -1) break;
            m_utilElement_.m_CEs_[(m_utilElement_.m_CELength_++)] = CE;
          }
          




          addAnElement(t, m_utilElement_);
        }
      } else if ((m_parser_.m_removeSet_ != null) && (m_parser_.m_removeSet_.contains(conts[offset]))) {
        copyRangeFromUCA(t, conts[offset], conts[offset]);
      }
      
      offset += 3;
    }
    

    processUCACompleteIgnorables(t);
    

    canonicalClosure(t);
    

    assembleTable(t, collator);
  }
  

  private static class CEGenerator
  {
    CollationParsedRuleBuilder.WeightRange[] m_ranges_;
    
    int m_rangesLength_;
    
    int m_byteSize_;
    
    int m_start_;
    
    int m_limit_;
    
    int m_maxCount_;
    int m_count_;
    int m_current_;
    int m_fLow_;
    int m_fHigh_;
    
    CEGenerator()
    {
      m_ranges_ = new CollationParsedRuleBuilder.WeightRange[7];
      for (int i = 6; i >= 0; i--) {
        m_ranges_[i] = new CollationParsedRuleBuilder.WeightRange();
      }
    }
  }
  
  private static class WeightRange implements Comparable
  {
    int m_start_;
    int m_end_;
    int m_length_;
    int m_count_;
    int m_length2_;
    int m_count2_;
    
    public int compareTo(Object target)
    {
      if (this == target) {
        return 0;
      }
      int tstart = m_start_;
      if (m_start_ == tstart) {
        return 0;
      }
      if (m_start_ > tstart) {
        return 1;
      }
      return -1;
    }
    



    public void clear()
    {
      m_start_ = 0;
      m_end_ = 0;
      m_length_ = 0;
      m_count_ = 0;
      m_length2_ = 0;
      m_count2_ = 0;
    }
    











    WeightRange()
    {
      clear();
    }
    





    WeightRange(WeightRange source)
    {
      m_start_ = m_start_;
      m_end_ = m_end_;
      m_length_ = m_length_;
      m_count_ = m_count_;
      m_length2_ = m_length2_;
      m_count2_ = m_count2_;
    }
  }
  

  private static class MaxJamoExpansionTable
  {
    Vector m_endExpansionCE_;
    
    Vector m_isV_;
    
    byte m_maxLSize_;
    
    byte m_maxVSize_;
    
    byte m_maxTSize_;
    
    MaxJamoExpansionTable()
    {
      m_endExpansionCE_ = new Vector();
      m_isV_ = new Vector();
      m_endExpansionCE_.add(new Integer(0));
      m_isV_.add(new Boolean(false));
      m_maxLSize_ = 1;
      m_maxVSize_ = 1;
      m_maxTSize_ = 1;
    }
    
    MaxJamoExpansionTable(MaxJamoExpansionTable table)
    {
      m_endExpansionCE_ = ((Vector)m_endExpansionCE_.clone());
      m_isV_ = ((Vector)m_isV_.clone());
      m_maxLSize_ = m_maxLSize_;
      m_maxVSize_ = m_maxVSize_;
      m_maxTSize_ = m_maxTSize_;
    }
  }
  
  private static class MaxExpansionTable
  {
    Vector m_endExpansionCE_;
    Vector m_expansionCESize_;
    
    MaxExpansionTable() {
      m_endExpansionCE_ = new Vector();
      m_expansionCESize_ = new Vector();
      m_endExpansionCE_.add(new Integer(0));
      m_expansionCESize_.add(new Byte((byte)0));
    }
    
    MaxExpansionTable(MaxExpansionTable table)
    {
      m_endExpansionCE_ = ((Vector)m_endExpansionCE_.clone());
      m_expansionCESize_ = ((Vector)m_expansionCESize_.clone());
    }
  }
  


  private static class BasicContractionTable
  {
    StringBuffer m_codePoints_;
    
    Vector m_CEs_;
    

    BasicContractionTable()
    {
      m_CEs_ = new Vector();
      m_codePoints_ = new StringBuffer();
    }
  }
  

  private static class ContractionTable
  {
    Vector m_elements_;
    
    IntTrieBuilder m_mapping_;
    
    StringBuffer m_codePoints_;
    
    Vector m_CEs_;
    Vector m_offsets_;
    int m_currentTag_;
    
    ContractionTable(IntTrieBuilder mapping)
    {
      m_mapping_ = mapping;
      m_elements_ = new Vector();
      m_CEs_ = new Vector();
      m_codePoints_ = new StringBuffer();
      m_offsets_ = new Vector();
      m_currentTag_ = 0;
    }
    





    ContractionTable(ContractionTable table)
    {
      m_mapping_ = m_mapping_;
      m_elements_ = ((Vector)m_elements_.clone());
      m_codePoints_ = new StringBuffer(m_codePoints_.toString());
      m_CEs_ = ((Vector)m_CEs_.clone());
      m_offsets_ = ((Vector)m_offsets_.clone());
      m_currentTag_ = m_currentTag_;
    }
  }
  

  private static final class BuildTable
    implements TrieBuilder.DataManipulate
  {
    RuleBasedCollator m_collator_;
    
    IntTrieBuilder m_mapping_;
    
    Vector m_expansions_;
    
    CollationParsedRuleBuilder.ContractionTable m_contractions_;
    
    CollationRuleParser.OptionSet m_options_;
    
    CollationParsedRuleBuilder.MaxExpansionTable m_maxExpansions_;
    
    CollationParsedRuleBuilder.MaxJamoExpansionTable m_maxJamoExpansions_;
    
    byte[] m_unsafeCP_;
    
    byte[] m_contrEndCP_;
    
    Hashtable m_prefixLookup_;
    

    public int getFoldedValue(int cp, int offset)
    {
      int limit = cp + 1024;
      while (cp < limit) {
        int value = m_mapping_.getValue(cp);
        boolean inBlockZero = m_mapping_.isInZeroBlock(cp);
        int tag = CollationParsedRuleBuilder.getCETag(value);
        if (inBlockZero == true) {
          cp += 32;
        } else {
          if ((!CollationParsedRuleBuilder.isSpecial(value)) || ((tag != 10) && (tag != 0)))
          {




            return 0xF5000000 | offset;
          }
          

          cp++;
        }
      }
      return 0;
    }
    






    BuildTable(CollationRuleParser parser)
    {
      m_collator_ = new RuleBasedCollator();
      m_collator_.setWithUCAData();
      CollationParsedRuleBuilder.MaxExpansionTable maxet = new CollationParsedRuleBuilder.MaxExpansionTable();
      CollationParsedRuleBuilder.MaxJamoExpansionTable maxjet = new CollationParsedRuleBuilder.MaxJamoExpansionTable();
      m_options_ = m_options_;
      m_expansions_ = new Vector();
      

      m_mapping_ = new IntTrieBuilder(null, 1048576, -268435456, true);
      


      m_prefixLookup_ = new Hashtable();
      
      m_contractions_ = new CollationParsedRuleBuilder.ContractionTable(m_mapping_);
      
      m_maxExpansions_ = maxet;
      
      for (int i = 0; 
          i < UCA_m_expansionEndCE_.length; i++) {
        m_endExpansionCE_.add(new Integer(UCA_m_expansionEndCE_[i]));
        
        m_expansionCESize_.add(new Byte(UCA_m_expansionEndCEMaxSize_[i]));
      }
      
      m_maxJamoExpansions_ = maxjet;
      
      m_unsafeCP_ = new byte['Р'];
      m_contrEndCP_ = new byte['Р'];
      Arrays.fill(m_unsafeCP_, (byte)0);
      Arrays.fill(m_contrEndCP_, (byte)0);
    }
    





    BuildTable(BuildTable table)
    {
      m_collator_ = m_collator_;
      m_mapping_ = new IntTrieBuilder(m_mapping_);
      m_expansions_ = ((Vector)m_expansions_.clone());
      m_contractions_ = new CollationParsedRuleBuilder.ContractionTable(m_contractions_);
      m_contractions_.m_mapping_ = m_mapping_;
      m_options_ = m_options_;
      m_maxExpansions_ = new CollationParsedRuleBuilder.MaxExpansionTable(m_maxExpansions_);
      m_maxJamoExpansions_ = new CollationParsedRuleBuilder.MaxJamoExpansionTable(m_maxJamoExpansions_);
      
      m_unsafeCP_ = new byte[m_unsafeCP_.length];
      System.arraycopy(m_unsafeCP_, 0, m_unsafeCP_, 0, m_unsafeCP_.length);
      
      m_contrEndCP_ = new byte[m_contrEndCP_.length];
      System.arraycopy(m_contrEndCP_, 0, m_contrEndCP_, 0, m_contrEndCP_.length);
    }
  }
  



  private static class Elements
  {
    String m_prefixChars_;
    


    int m_prefix_;
    


    String m_uchars_;
    


    String m_cPoints_;
    


    int m_cPointsOffset_;
    

    int[] m_CEs_;
    

    int m_CELength_;
    

    int m_mapCE_;
    

    int[] m_sizePrim_;
    

    int[] m_sizeSec_;
    

    int[] m_sizeTer_;
    

    boolean m_variableTop_;
    

    boolean m_caseBit_;
    

    boolean m_isThai_;
    


    Elements()
    {
      m_sizePrim_ = new int[''];
      m_sizeSec_ = new int[''];
      m_sizeTer_ = new int[''];
      m_CEs_ = new int['Ā'];
      m_CELength_ = 0;
    }
    



    Elements(Elements element)
    {
      m_prefixChars_ = m_prefixChars_;
      m_prefix_ = m_prefix_;
      m_uchars_ = m_uchars_;
      m_cPoints_ = m_cPoints_;
      m_cPointsOffset_ = m_cPointsOffset_;
      m_CEs_ = m_CEs_;
      m_CELength_ = m_CELength_;
      m_mapCE_ = m_mapCE_;
      m_sizePrim_ = m_sizePrim_;
      m_sizeSec_ = m_sizeSec_;
      m_sizeTer_ = m_sizeTer_;
      m_variableTop_ = m_variableTop_;
      m_caseBit_ = m_caseBit_;
      m_isThai_ = m_isThai_;
    }
    





    public void clear()
    {
      m_prefixChars_ = null;
      m_prefix_ = 0;
      m_uchars_ = null;
      m_cPoints_ = null;
      m_cPointsOffset_ = 0;
      m_CELength_ = 0;
      m_mapCE_ = 0;
      Arrays.fill(m_sizePrim_, 0);
      Arrays.fill(m_sizeSec_, 0);
      Arrays.fill(m_sizeTer_, 0);
      m_variableTop_ = false;
      m_caseBit_ = false;
      m_isThai_ = false;
    }
    





    public int hashCode()
    {
      String str = m_cPoints_.substring(m_cPointsOffset_);
      return str.hashCode();
    }
    





    public boolean equals(Object target)
    {
      if (target == this) {
        return true;
      }
      if ((target instanceof Elements)) {
        Elements t = (Elements)target;
        int size = m_cPoints_.length() - m_cPointsOffset_;
        if (size == m_cPoints_.length() - m_cPointsOffset_) {
          return m_cPoints_.regionMatches(m_cPointsOffset_, m_cPoints_, m_cPointsOffset_, size);
        }
      }
      

      return false;
    }
  }
  













  private static final int[] STRENGTH_MASK_ = { -65536, 65280, -1 };
  


  private static final int CE_NOT_FOUND_ = -268435456;
  


  private static final int CE_NOT_FOUND_TAG_ = 0;
  


  private static final int CE_EXPANSION_TAG_ = 1;
  


  private static final int CE_CONTRACTION_TAG_ = 2;
  


  private static final int CE_THAI_TAG_ = 3;
  


  private static final int CE_CHARSET_TAG_ = 4;
  


  private static final int CE_SURROGATE_TAG_ = 5;
  


  private static final int CE_HANGUL_SYLLABLE_TAG_ = 6;
  


  private static final int CE_LEAD_SURROGATE_TAG_ = 7;
  


  private static final int CE_TRAIL_SURROGATE_TAG_ = 8;
  


  private static final int CE_CJK_IMPLICIT_TAG_ = 9;
  


  private static final int CE_IMPLICIT_TAG_ = 10;
  


  private static final int CE_SPEC_PROC_TAG_ = 11;
  


  private static final int CE_LONG_PRIMARY_TAG_ = 12;
  


  private static final int UNSAFECP_TABLE_SIZE_ = 1056;
  


  private static final int UNSAFECP_TABLE_MASK_ = 8191;
  


  private static final int UPPER_CASE_ = 128;
  


  private static final int MIXED_CASE_ = 64;
  


  private static final int LOWER_CASE_ = 0;
  

  private static final int INIT_TABLE_SIZE_ = 1028;
  

  private static final int HEADER_SIZE_ = 196;
  

  private static final int CONTRACTION_TABLE_NEW_ELEMENT_ = 16777215;
  

  private CollationRuleParser m_parser_;
  

  private CollationElementIterator m_utilColEIter_;
  

  private CEGenerator[] m_utilGens_ = { new CEGenerator(), new CEGenerator(), new CEGenerator() };
  
  private int[] m_utilCEBuffer_ = new int[3];
  private int[] m_utilIntBuffer_ = new int[16];
  private Elements m_utilElement_ = new Elements();
  private Elements m_utilElement2_ = new Elements();
  private CollationRuleParser.Token m_utilToken_ = new CollationRuleParser.Token();
  
  private int[] m_utilCountBuffer_ = new int[6];
  private long[] m_utilLongBuffer_ = new long[5];
  private WeightRange[] m_utilLowerWeightRange_ = { new WeightRange(), new WeightRange(), new WeightRange(), new WeightRange(), new WeightRange() };
  


  private WeightRange[] m_utilUpperWeightRange_ = { new WeightRange(), new WeightRange(), new WeightRange(), new WeightRange(), new WeightRange() };
  


  private WeightRange m_utilWeightRange_ = new WeightRange();
  private char[] m_utilCharBuffer_ = new char['Ā'];
  private CanonicalIterator m_utilCanIter_ = new CanonicalIterator("");
  private StringBuffer m_utilStringBuffer_ = new StringBuffer("");
  






  private void initBuffers(CollationRuleParser.TokenListHeader listheader)
    throws Exception
  {
    CollationRuleParser.Token token = m_last_;
    Arrays.fill(m_utilIntBuffer_, 0, 16, 0);
    
    m_toInsert_ = 1;
    m_utilIntBuffer_[m_strength_] = 1;
    while (m_previous_ != null) {
      if (m_previous_.m_strength_ < m_strength_)
      {
        m_utilIntBuffer_[m_strength_] = 0;
        m_utilIntBuffer_[m_previous_.m_strength_] += 1;
      }
      else if (m_previous_.m_strength_ > m_strength_)
      {
        m_utilIntBuffer_[m_previous_.m_strength_] = 1;
      }
      else {
        m_utilIntBuffer_[m_strength_] += 1;
      }
      token = m_previous_;
      m_toInsert_ = m_utilIntBuffer_[m_strength_];
    }
    
    m_toInsert_ = m_utilIntBuffer_[m_strength_];
    INVERSE_UCA_.getInverseGapPositions(listheader);
    
    token = m_first_;
    int fstrength = 15;
    int initstrength = 15;
    
    m_utilCEBuffer_[0] = mergeCE(m_baseCE_, m_baseContCE_, 0);
    

    m_utilCEBuffer_[1] = mergeCE(m_baseCE_, m_baseContCE_, 1);
    

    m_utilCEBuffer_[2] = mergeCE(m_baseCE_, m_baseContCE_, 2);
    

    while (token != null) {
      fstrength = m_strength_;
      if (fstrength < initstrength) {
        initstrength = fstrength;
        if (m_pos_[fstrength] == -1) {
          while ((m_pos_[fstrength] == -1) && (fstrength > 0))
          {
            fstrength--;
          }
          if (m_pos_[fstrength] == -1) {
            throw new Exception("Internal program error");
          }
        }
        if (initstrength == 2)
        {
          m_utilCEBuffer_[0] = m_gapsLo_[(fstrength * 3)];
          
          m_utilCEBuffer_[1] = m_gapsLo_[(fstrength * 3 + 1)];
          
          m_utilCEBuffer_[2] = getCEGenerator(m_utilGens_[2], m_gapsLo_, m_gapsHi_, token, fstrength);




        }
        else if (initstrength == 1)
        {
          m_utilCEBuffer_[0] = m_gapsLo_[(fstrength * 3)];
          
          m_utilCEBuffer_[1] = getCEGenerator(m_utilGens_[1], m_gapsLo_, m_gapsHi_, token, fstrength);
          




          m_utilCEBuffer_[2] = getSimpleCEGenerator(m_utilGens_[2], token, 2);


        }
        else
        {

          m_utilCEBuffer_[0] = getCEGenerator(m_utilGens_[0], m_gapsLo_, m_gapsHi_, token, fstrength);
          




          m_utilCEBuffer_[1] = getSimpleCEGenerator(m_utilGens_[1], token, 1);
          


          m_utilCEBuffer_[2] = getSimpleCEGenerator(m_utilGens_[2], token, 2);

        }
        


      }
      else if (m_strength_ == 2) {
        m_utilCEBuffer_[2] = getNextGenerated(m_utilGens_[2]);

      }
      else if (m_strength_ == 1) {
        m_utilCEBuffer_[1] = getNextGenerated(m_utilGens_[1]);
        
        m_utilCEBuffer_[2] = getSimpleCEGenerator(m_utilGens_[2], token, 2);



      }
      else if (m_strength_ == 0) {
        m_utilCEBuffer_[0] = getNextGenerated(m_utilGens_[0]);
        

        m_utilCEBuffer_[1] = getSimpleCEGenerator(m_utilGens_[1], token, 1);
        


        m_utilCEBuffer_[2] = getSimpleCEGenerator(m_utilGens_[2], token, 2);
      }
      



      doCE(m_utilCEBuffer_, token);
      token = m_next_;
    }
  }
  





  private int getNextGenerated(CEGenerator g)
  {
    m_current_ = nextWeight(g);
    return m_current_;
  }
  








  private int getSimpleCEGenerator(CEGenerator g, CollationRuleParser.Token token, int strength)
    throws Exception
  {
    int count = 1;
    int maxbyte = strength == 2 ? 63 : 255;
    int low;
    int high; if (strength == 1) {
      low = -2046820352;
      high = -1;
      count = 121;
    }
    else {
      low = 83886080;
      high = 1073741824;
      count = 59;
    }
    
    if ((m_next_ != null) && (m_next_.m_strength_ == strength)) {
      count = m_next_.m_toInsert_;
    }
    
    m_rangesLength_ = allocateWeights(low, high, count, maxbyte, m_ranges_);
    
    m_current_ = 83886080;
    
    if (m_rangesLength_ == 0) {
      throw new Exception("Internal program error");
    }
    return m_current_;
  }
  







  private static int mergeCE(int ce1, int ce2, int strength)
  {
    int mask = 255;
    if (strength == 1) {
      mask = 65280;
    }
    else if (strength == 0) {
      mask = -65536;
    }
    ce1 &= mask;
    ce2 &= mask;
    switch (strength)
    {
    case 0: 
      return ce1 | ce2 >>> 16;
    case 1: 
      return ce1 << 16 | ce2 << 8;
    }
    return ce1 << 24 | ce2 << 16;
  }
  










  private int getCEGenerator(CEGenerator g, int[] lows, int[] highs, CollationRuleParser.Token token, int fstrength)
    throws Exception
  {
    int strength = m_strength_;
    int low = lows[(fstrength * 3 + strength)];
    int high = highs[(fstrength * 3 + strength)];
    int maxbyte = strength == 2 ? 63 : 255;
    
    int count = m_toInsert_;
    
    if ((Utility.compareUnsigned(low, high) >= 0) && (strength > 0))
    {
      int s = strength;
      do {
        s--;
        if (lows[(fstrength * 3 + s)] != highs[(fstrength * 3 + s)]) {
          if (strength == 1) {
            low = -2046820352;
            high = -1; break;
          }
          



          high = 1073741824;
          
          break;
        }
      } while (s >= 0);
      throw new Exception("Internal program error");
    }
    

    if (low == 0) {
      low = 16777216;
    }
    if (strength == 1) {
      if ((Utility.compareUnsigned(low, 83886080) >= 0) && (Utility.compareUnsigned(low, -2046820352) < 0))
      {


        low = -2046820352;
      }
      if ((Utility.compareUnsigned(high, 83886080) > 0) && (Utility.compareUnsigned(high, -2046820352) < 0))
      {


        high = -2046820352;
      }
      if (Utility.compareUnsigned(low, 83886080) < 0)
      {
        m_rangesLength_ = allocateWeights(83886080, high, count, maxbyte, m_ranges_);
        

        m_current_ = 83886080;
        return m_current_;
      }
    }
    
    m_rangesLength_ = allocateWeights(low, high, count, maxbyte, m_ranges_);
    
    if (m_rangesLength_ == 0) {
      throw new Exception("Internal program error");
    }
    m_current_ = nextWeight(g);
    return m_current_;
  }
  







  private void doCE(int[] ceparts, CollationRuleParser.Token token)
    throws Exception
  {
    for (int i = 0; i < 3; i++)
    {
      m_utilIntBuffer_[i] = countBytes(ceparts[i]);
    }
    

    int cei = 0;
    int value = 0;
    

    while ((cei << 1 < m_utilIntBuffer_[0]) || (cei < m_utilIntBuffer_[1]) || (cei < m_utilIntBuffer_[2])) {
      if (cei > 0) {
        value = 192;
      } else {
        value = 0;
      }
      
      if (cei << 1 < m_utilIntBuffer_[0]) {
        value |= (ceparts[0] >> 32 - (cei + 1 << 4) & 0xFFFF) << 16;
      }
      
      if (cei < m_utilIntBuffer_[1]) {
        value |= (ceparts[1] >> 32 - (cei + 1 << 3) & 0xFF) << 8;
      }
      
      if (cei < m_utilIntBuffer_[2]) {
        value |= ceparts[2] >> 32 - (cei + 1 << 3) & 0x3F;
      }
      m_CE_[cei] = value;
      cei++;
    }
    if (cei == 0) {
      m_CELength_ = 1;
      m_CE_[0] = 0;
    }
    else {
      m_CELength_ = cei;
    }
    

    int startoftokenrule = m_source_ & 0xFF;
    if (m_source_ >>> 24 > 1)
    {
      int length = m_source_ >>> 24;
      String tokenstr = m_rules_.substring(startoftokenrule, startoftokenrule + length);
      
      m_CE_[0] |= getCaseBits(tokenstr);
    }
    else
    {
      int caseCE = getFirstCE(m_rules_.charAt(startoftokenrule));
      
      m_CE_[0] |= caseCE & 0xC0;
    }
  }
  





  private static final int countBytes(int ce)
  {
    int mask = -1;
    int result = 0;
    while (mask != 0) {
      if ((ce & mask) != 0) {
        result++;
      }
      mask >>>= 8;
    }
    return result;
  }
  







  private void createElements(BuildTable t, CollationRuleParser.TokenListHeader lh)
  {
    CollationRuleParser.Token tok = m_first_;
    m_utilElement_.clear();
    while (tok != null)
    {



      if (m_expansion_ != 0) {
        int len = m_expansion_ >>> 24;
        int currentSequenceLen = len;
        int expOffset = m_expansion_ & 0xFFFFFF;
        m_utilToken_.m_source_ = (currentSequenceLen | expOffset);
        m_utilToken_.m_rules_ = m_parser_.m_source_;
        
        while (len > 0) {
          currentSequenceLen = len;
          while (currentSequenceLen > 0) {
            m_utilToken_.m_source_ = (currentSequenceLen << 24 | expOffset);
            
            CollationRuleParser.Token expt = (CollationRuleParser.Token)m_parser_.m_hashTable_.get(m_utilToken_);
            

            if ((expt != null) && (m_strength_ != -559038737))
            {


              int noOfCEsToCopy = m_CELength_;
              for (int j = 0; j < noOfCEsToCopy; j++) {
                m_expCE_[(m_expCELength_ + j)] = m_CE_[j];
              }
              
              m_expCELength_ += noOfCEsToCopy;
              

              expOffset += currentSequenceLen;
              len -= currentSequenceLen;
              break;
            }
            
            currentSequenceLen--;
          }
          
          if (currentSequenceLen == 0)
          {



            m_utilColEIter_.setText(m_parser_.m_source_.substring(expOffset, expOffset + 1));
            for (;;)
            {
              int order = m_utilColEIter_.next();
              if (order == -1) {
                break;
              }
              m_expCE_[(m_expCELength_++)] = order;
            }
            expOffset++;
            len--;
          }
        }
      }
      else {
        m_expCELength_ = 0;
      }
      

      m_utilElement_.m_CELength_ = (m_CELength_ + m_expCELength_);
      

      System.arraycopy(m_CE_, 0, m_utilElement_.m_CEs_, 0, m_CELength_);
      
      System.arraycopy(m_expCE_, 0, m_utilElement_.m_CEs_, m_CELength_, m_expCELength_);
      





      m_utilElement_.m_prefix_ = 0;
      m_utilElement_.m_cPointsOffset_ = 0;
      if (m_prefix_ != 0)
      {



        int size = m_prefix_ >> 24;
        int offset = m_prefix_ & 0xFFFFFF;
        m_utilElement_.m_prefixChars_ = m_parser_.m_source_.substring(offset, offset + size);
        
        size = (m_source_ >> 24) - (m_prefix_ >> 24);
        offset = (m_source_ & 0xFFFFFF) + (m_prefix_ >> 24);
        m_utilElement_.m_uchars_ = m_parser_.m_source_.substring(offset, offset + size);
      }
      else
      {
        m_utilElement_.m_prefixChars_ = null;
        int offset = m_source_ & 0xFFFFFF;
        int size = m_source_ >>> 24;
        m_utilElement_.m_uchars_ = m_parser_.m_source_.substring(offset, offset + size);
      }
      
      m_utilElement_.m_cPoints_ = m_utilElement_.m_uchars_;
      m_utilElement_.m_isThai_ = CollationElementIterator.isThaiPreVowel(m_utilElement_.m_cPoints_.charAt(0));
      
      for (int i = 0; 
          i < m_utilElement_.m_cPoints_.length() - m_utilElement_.m_cPointsOffset_; i++) {
        if (isJamo(m_utilElement_.m_cPoints_.charAt(i))) {
          m_collator_.m_isJamoSpecial_ = true;
          break;
        }
      }
      



















      addAnElement(t, m_utilElement_);
      tok = m_next_;
    }
  }
  





  private final int getCaseBits(String src)
    throws Exception
  {
    int uCount = 0;
    int lCount = 0;
    src = Normalizer.decompose(src, true);
    m_utilColEIter_.setText(src);
    for (int i = 0; i < src.length(); i++) {
      m_utilColEIter_.setText(src.substring(i, i + 1));
      int order = m_utilColEIter_.next();
      if (RuleBasedCollator.isContinuation(order)) {
        throw new Exception("Internal program error");
      }
      if ((order & 0xC0) == 128)
      {
        uCount++;
      }
      else {
        char ch = src.charAt(i);
        if (UCharacter.isLowerCase(ch)) {
          lCount++;

        }
        else if ((toSmallKana(ch) == ch) && (toLargeKana(ch) != ch)) {
          lCount++;
        }
      }
    }
    

    if ((uCount != 0) && (lCount != 0)) {
      return 64;
    }
    if (uCount != 0) {
      return 128;
    }
    
    return 0;
  }
  






  private static final char toLargeKana(char ch)
  {
    if (('あ' < ch) && (ch < 'ワ')) {
      switch (ch - '　') {
      case 65: 
      case 67: 
      case 69: 
      case 71: 
      case 73: 
      case 99: 
      case 131: 
      case 133: 
      case 142: 
      case 161: 
      case 163: 
      case 165: 
      case 167: 
      case 169: 
      case 195: 
      case 227: 
      case 229: 
      case 238: 
        ch = (char)(ch + '\001');
        break;
      case 245: 
        ch = 'カ';
        break;
      case 246: 
        ch = 'ケ';
      }
      
    }
    return ch;
  }
  





  private static final char toSmallKana(char ch)
  {
    if (('あ' < ch) && (ch < 'ワ')) {
      switch (ch - '　') {
      case 66: 
      case 68: 
      case 70: 
      case 72: 
      case 74: 
      case 100: 
      case 132: 
      case 134: 
      case 143: 
      case 162: 
      case 164: 
      case 166: 
      case 168: 
      case 170: 
      case 196: 
      case 228: 
      case 230: 
      case 239: 
        ch = (char)(ch - '\001');
        break;
      case 171: 
        ch = 'ヵ';
        break;
      case 177: 
        ch = 'ヶ';
      }
      
    }
    return ch;
  }
  



  private int getFirstCE(char ch)
  {
    m_utilColEIter_.setText(UCharacter.toString(ch));
    return m_utilColEIter_.next();
  }
  






  private int addAnElement(BuildTable t, Elements element)
  {
    Vector expansions = m_expansions_;
    if (m_CELength_ == 1) {
      if (!m_isThai_) {
        m_mapCE_ = m_CEs_[0];

      }
      else
      {
        int expansion = 0xF3000000 | addExpansion(expansions, m_CEs_[0]) << 4 | 0x1;
        



        m_mapCE_ = expansion;




      }
      





    }
    else if ((m_CELength_ == 2) && (RuleBasedCollator.isContinuation(m_CEs_[1])) && ((m_CEs_[1] & 0xFFFF3F) == 0) && ((m_CEs_[0] >> 8 & 0xFF) == 5) && ((m_CEs_[0] & 0xFF) == 5))
    {









      m_mapCE_ = (0xFC000000 | m_CEs_[0] >> 8 & 0xFFFF00 | m_CEs_[1] >> 24 & 0xFF);




    }
    else
    {



      int expansion = 0xF1000000 | addExpansion(expansions, m_CEs_[0]) << 4 & 0xFFFFF0;
      




      for (int i = 1; i < m_CELength_; i++) {
        addExpansion(expansions, m_CEs_[i]);
      }
      if (m_CELength_ <= 15) {
        expansion |= m_CELength_;
      }
      else {
        addExpansion(expansions, 0);
      }
      m_mapCE_ = expansion;
      setMaxExpansion(m_CEs_[(m_CELength_ - 1)], (byte)m_CELength_, m_maxExpansions_);
      

      if (isJamo(m_cPoints_.charAt(0))) {
        m_collator_.m_isJamoSpecial_ = true;
        setMaxJamoExpansion(m_cPoints_.charAt(0), m_CEs_[(m_CELength_ - 1)], (byte)m_CELength_, m_maxJamoExpansions_);
      }
    }
    









    if ((m_prefixChars_ != null) && (m_prefixChars_.length() - m_prefix_ > 0))
    {



      m_utilElement2_.m_caseBit_ = m_caseBit_;
      m_utilElement2_.m_CELength_ = m_CELength_;
      m_utilElement2_.m_CEs_ = m_CEs_;
      m_utilElement2_.m_isThai_ = m_isThai_;
      m_utilElement2_.m_mapCE_ = m_mapCE_;
      
      m_utilElement2_.m_sizePrim_ = m_sizePrim_;
      m_utilElement2_.m_sizeSec_ = m_sizeSec_;
      m_utilElement2_.m_sizeTer_ = m_sizeTer_;
      m_utilElement2_.m_variableTop_ = m_variableTop_;
      m_utilElement2_.m_prefix_ = m_prefix_;
      m_utilElement2_.m_prefixChars_ = Normalizer.compose(m_prefixChars_, false);
      
      m_utilElement2_.m_uchars_ = m_uchars_;
      m_utilElement2_.m_cPoints_ = m_cPoints_;
      m_utilElement2_.m_cPointsOffset_ = 0;
      
      if (m_prefixLookup_ != null) {
        Elements uCE = (Elements)m_prefixLookup_.get(element);
        if (uCE != null)
        {
          m_mapCE_ = addPrefix(t, m_mapCE_, element);
        }
        else {
          m_mapCE_ = addPrefix(t, -268435456, element);
          uCE = new Elements(element);
          m_cPoints_ = m_uchars_;
          m_prefixLookup_.put(uCE, uCE);
        }
        if ((m_utilElement2_.m_prefixChars_.length() != m_prefixChars_.length() - m_prefix_) || (!m_utilElement2_.m_prefixChars_.regionMatches(0, m_prefixChars_, m_prefix_, m_utilElement2_.m_prefixChars_.length())))
        {




          m_utilElement2_.m_mapCE_ = addPrefix(t, m_mapCE_, m_utilElement2_);
        }
      }
    }
    




    if ((m_cPoints_.length() - m_cPointsOffset_ > 1) && ((m_cPoints_.length() - m_cPointsOffset_ != 2) || (!UTF16.isLeadSurrogate(m_cPoints_.charAt(0))) || (!UTF16.isTrailSurrogate(m_cPoints_.charAt(1)))))
    {




      m_utilCanIter_.setSource(m_cPoints_);
      String source = m_utilCanIter_.next();
      while ((source != null) && (source.length() > 0)) {
        if (Normalizer.quickCheck(source, Normalizer.FCD, 0) != Normalizer.NO)
        {
          m_uchars_ = source;
          m_cPoints_ = m_uchars_;
          finalizeAddition(t, element);
        }
        source = m_utilCanIter_.next();
      }
      
      return m_mapCE_;
    }
    
    return finalizeAddition(t, element);
  }
  







  private static final int addExpansion(Vector expansions, int value)
  {
    expansions.add(new Integer(value));
    return expansions.size() - 1;
  }
  










  private static int setMaxExpansion(int endexpansion, byte expansionsize, MaxExpansionTable maxexpansion)
  {
    int start = 0;
    int limit = m_endExpansionCE_.size();
    long unsigned = endexpansion;
    unsigned &= 0xFFFFFFFF;
    


    int result = -1;
    while (start < limit - 1) {
      int mid = start + (limit - start >> 1);
      long unsignedce = ((Integer)m_endExpansionCE_.get(mid)).intValue();
      
      unsignedce &= 0xFFFFFFFF;
      if (unsigned <= unsignedce) {
        limit = mid;
      }
      else {
        start = mid;
      }
    }
    
    if (((Integer)m_endExpansionCE_.get(start)).intValue() == endexpansion)
    {
      result = start;
    }
    else if (((Integer)m_endExpansionCE_.get(limit)).intValue() == endexpansion)
    {
      result = limit;
    }
    if (result > -1)
    {

      Object currentsize = m_expansionCESize_.get(result);
      if (((Byte)currentsize).byteValue() < expansionsize) {
        m_expansionCESize_.set(result, new Byte(expansionsize));
      }
      

    }
    else
    {
      m_endExpansionCE_.insertElementAt(new Integer(endexpansion), start + 1);
      

      m_expansionCESize_.insertElementAt(new Byte(expansionsize), start + 1);
    }
    

    return m_endExpansionCE_.size();
  }
  












  private static int setMaxJamoExpansion(char ch, int endexpansion, byte expansionsize, MaxJamoExpansionTable maxexpansion)
  {
    boolean isV = true;
    if ((ch >= 'ᄀ') && (ch <= 'ᄒ'))
    {

      if (m_maxLSize_ < expansionsize) {
        m_maxLSize_ = expansionsize;
      }
      return m_endExpansionCE_.size();
    }
    
    if ((ch >= 'ᅡ') && (ch <= 'ᅵ'))
    {
      if (m_maxVSize_ < expansionsize) {
        m_maxVSize_ = expansionsize;
      }
    }
    
    if ((ch >= 'ᆨ') && (ch <= 'ᇂ')) {
      isV = false;
      
      if (m_maxTSize_ < expansionsize) {
        m_maxTSize_ = expansionsize;
      }
    }
    
    int pos = m_endExpansionCE_.size();
    while (pos > 0) {
      pos--;
      if (((Integer)m_endExpansionCE_.get(pos)).intValue() == endexpansion)
      {
        return m_endExpansionCE_.size();
      }
    }
    m_endExpansionCE_.add(new Integer(endexpansion));
    m_isV_.add(new Boolean(isV));
    
    return m_endExpansionCE_.size();
  }
  











  private int addPrefix(BuildTable t, int CE, Elements element)
  {
    ContractionTable contractions = m_contractions_;
    String oldCP = m_cPoints_;
    int oldCPOffset = m_cPointsOffset_;
    
    m_currentTag_ = 11;
    
    int size = m_prefixChars_.length() - m_prefix_;
    for (int j = 1; j < size; j++)
    {


      char ch = m_prefixChars_.charAt(j + m_prefix_);
      if (!UTF16.isTrailSurrogate(ch)) {
        unsafeCPSet(m_unsafeCP_, ch);
      }
    }
    

    m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
    for (int j = 0; j < size; j++)
    {

      int offset = m_prefixChars_.length() - j - 1;
      m_utilStringBuffer_.append(m_prefixChars_.charAt(offset));
    }
    m_prefixChars_ = m_utilStringBuffer_.toString();
    m_prefix_ = 0;
    


    if (!UTF16.isTrailSurrogate(m_cPoints_.charAt(0))) {
      unsafeCPSet(m_unsafeCP_, m_cPoints_.charAt(0));
    }
    
    m_cPoints_ = m_prefixChars_;
    m_cPointsOffset_ = m_prefix_;
    



    if (!UTF16.isTrailSurrogate(m_cPoints_.charAt(m_cPoints_.length() - 1)))
    {
      ContrEndCPSet(m_contrEndCP_, m_cPoints_.charAt(m_cPoints_.length() - 1));
    }
    





    if (isJamo(m_prefixChars_.charAt(m_prefix_))) {
      m_collator_.m_isJamoSpecial_ = true;
    }
    

    if (!isPrefix(CE))
    {
      int firstContractionOffset = addContraction(contractions, 16777215, '\000', CE);
      

      int newCE = processContraction(contractions, element, -268435456);
      
      addContraction(contractions, firstContractionOffset, m_prefixChars_.charAt(m_prefix_), newCE);
      

      addContraction(contractions, firstContractionOffset, 65535, CE);
      
      CE = constructSpecialCE(11, firstContractionOffset);


    }
    else
    {


      char ch = m_prefixChars_.charAt(m_prefix_);
      int position = findCP(contractions, CE, ch);
      if (position > 0)
      {
        int eCE = getCE(contractions, CE, position);
        int newCE = processContraction(contractions, element, eCE);
        setContraction(contractions, CE, position, ch, newCE);
      }
      else
      {
        processContraction(contractions, element, -268435456);
        insertContraction(contractions, CE, ch, m_mapCE_);
      }
    }
    
    m_cPoints_ = oldCP;
    m_cPointsOffset_ = oldCPOffset;
    
    return CE;
  }
  





  private static final boolean isContraction(int CE)
  {
    return (isSpecial(CE)) && (getCETag(CE) == 2);
  }
  





  private static final boolean isPrefix(int CE)
  {
    return (isSpecial(CE)) && (getCETag(CE) == 11);
  }
  





  private static final boolean isSpecial(int CE)
  {
    return (CE & 0xF0000000) == -268435456;
  }
  





  private static final int getCETag(int CE)
  {
    return (CE & 0xF000000) >>> 24;
  }
  








  private static final int getCE(ContractionTable table, int element, int position)
  {
    element &= 0xFFFFFF;
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    
    if (tbl == null) {
      return -268435456;
    }
    if ((position > m_CEs_.size()) || (position == -1)) {
      return -268435456;
    }
    
    return ((Integer)m_CEs_.get(position)).intValue();
  }
  






  private static final void unsafeCPSet(byte[] table, char c)
  {
    int hash = c;
    if (hash >= 8448) {
      if ((hash >= 55296) && (hash <= 63743))
      {

        return;
      }
      hash = (hash & 0x1FFF) + 256;
    }
    int tmp36_35 = (hash >> 3);table[tmp36_35] = ((byte)(table[tmp36_35] | 1 << (hash & 0x7)));
  }
  





  private static final void ContrEndCPSet(byte[] table, char c)
  {
    int hash = c;
    if (hash >= 8448) {
      hash = (hash & 0x1FFF) + 256;
    }
    int tmp23_22 = (hash >> 3);table[tmp23_22] = ((byte)(table[tmp23_22] | 1 << (hash & 0x7)));
  }
  










  private static int addContraction(ContractionTable table, int element, char codePoint, int value)
  {
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      tbl = addAContractionElement(table);
      element = m_elements_.size() - 1;
    }
    
    m_CEs_.add(new Integer(value));
    m_codePoints_.append(codePoint);
    return constructSpecialCE(m_currentTag_, element);
  }
  






  private static BasicContractionTable addAContractionElement(ContractionTable table)
  {
    BasicContractionTable result = new BasicContractionTable();
    m_elements_.add(result);
    return result;
  }
  






  private static final int constructSpecialCE(int tag, int CE)
  {
    return 0xF0000000 | tag << 24 | CE & 0xFFFFFF;
  }
  










  private static int processContraction(ContractionTable contractions, Elements element, int existingCE)
  {
    int firstContractionOffset = 0;
    
    if (m_cPoints_.length() - m_cPointsOffset_ == 1) {
      if ((isContractionTableElement(existingCE)) && (getCETag(existingCE) == m_currentTag_))
      {
        changeContraction(contractions, existingCE, '\000', m_mapCE_);
        
        changeContraction(contractions, existingCE, 65535, m_mapCE_);
        
        return existingCE;
      }
      


      return m_mapCE_;
    }
    






    m_cPointsOffset_ += 1;
    if (!isContractionTableElement(existingCE))
    {
      firstContractionOffset = addContraction(contractions, 16777215, '\000', existingCE);
      

      int newCE = processContraction(contractions, element, -268435456);
      
      addContraction(contractions, firstContractionOffset, m_cPoints_.charAt(m_cPointsOffset_), newCE);
      

      addContraction(contractions, firstContractionOffset, 65535, existingCE);
      
      existingCE = constructSpecialCE(m_currentTag_, firstContractionOffset);



    }
    else
    {


      int position = findCP(contractions, existingCE, m_cPoints_.charAt(m_cPointsOffset_));
      
      if (position > 0)
      {
        int eCE = getCE(contractions, existingCE, position);
        int newCE = processContraction(contractions, element, eCE);
        setContraction(contractions, existingCE, position, m_cPoints_.charAt(m_cPointsOffset_), newCE);

      }
      else
      {

        int newCE = processContraction(contractions, element, -268435456);
        
        insertContraction(contractions, existingCE, m_cPoints_.charAt(m_cPointsOffset_), newCE);
      }
    }
    

    m_cPointsOffset_ -= 1;
    return existingCE;
  }
  





  private static final boolean isContractionTableElement(int CE)
  {
    return (isSpecial(CE)) && ((getCETag(CE) == 2) || (getCETag(CE) == 11));
  }
  










  private static int findCP(ContractionTable table, int element, char codePoint)
  {
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      return -1;
    }
    
    int position = 0;
    while (codePoint > m_codePoints_.charAt(position)) {
      position++;
      if (position > m_codePoints_.length()) {
        return -1;
      }
    }
    if (codePoint == m_codePoints_.charAt(position)) {
      return position;
    }
    
    return -1;
  }
  









  private static final BasicContractionTable getBasicContractionTable(ContractionTable table, int offset)
  {
    offset &= 0xFFFFFF;
    if (offset == 16777215) {
      return null;
    }
    return (BasicContractionTable)m_elements_.get(offset);
  }
  










  private static final int changeContraction(ContractionTable table, int element, char codePoint, int newCE)
  {
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      return 0;
    }
    int position = 0;
    while (codePoint > m_codePoints_.charAt(position)) {
      position++;
      if (position > m_codePoints_.length()) {
        return -268435456;
      }
    }
    if (codePoint == m_codePoints_.charAt(position)) {
      m_CEs_.set(position, new Integer(newCE));
      return element & 0xFFFFFF;
    }
    
    return -268435456;
  }
  













  private static final int setContraction(ContractionTable table, int element, int offset, char codePoint, int value)
  {
    element &= 0xFFFFFF;
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      tbl = addAContractionElement(table);
      element = m_elements_.size() - 1;
    }
    
    m_CEs_.set(offset, new Integer(value));
    m_codePoints_.setCharAt(offset, codePoint);
    return constructSpecialCE(m_currentTag_, element);
  }
  











  private static final int insertContraction(ContractionTable table, int element, char codePoint, int value)
  {
    element &= 0xFFFFFF;
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      tbl = addAContractionElement(table);
      element = m_elements_.size() - 1;
    }
    
    int offset = 0;
    
    while ((m_codePoints_.charAt(offset) < codePoint) && (offset < m_codePoints_.length())) {
      offset++;
    }
    
    m_CEs_.insertElementAt(new Integer(value), offset);
    m_codePoints_.insert(offset, codePoint);
    
    return constructSpecialCE(m_currentTag_, element);
  }
  





  private static final int finalizeAddition(BuildTable t, Elements element)
  {
    int CE = -268435456;
    


    if (m_mapCE_ == 0) {
      for (int i = 0; i < m_cPoints_.length(); i++) {
        char ch = m_cPoints_.charAt(i);
        if (!UTF16.isTrailSurrogate(ch)) {
          unsafeCPSet(m_unsafeCP_, ch);
        }
      }
    }
    
    if (m_cPoints_.length() - m_cPointsOffset_ > 1)
    {
      int cp = UTF16.charAt(m_cPoints_, m_cPointsOffset_);
      CE = m_mapping_.getValue(cp);
      CE = addContraction(t, CE, element);
    }
    else
    {
      CE = m_mapping_.getValue(m_cPoints_.charAt(m_cPointsOffset_));
      

      if (CE != -268435456) {
        if (isContractionTableElement(CE))
        {

          if (!isPrefix(m_mapCE_))
          {



            setContraction(m_contractions_, CE, 0, '\000', m_mapCE_);
            


            changeLastCE(m_contractions_, CE, m_mapCE_);
          }
        }
        else {
          m_mapping_.setValue(m_cPoints_.charAt(m_cPointsOffset_), m_mapCE_);
        }
        

      }
      else {
        m_mapping_.setValue(m_cPoints_.charAt(m_cPointsOffset_), m_mapCE_);
      }
    }
    

    return CE;
  }
  







  private static int addContraction(BuildTable t, int CE, Elements element)
  {
    ContractionTable contractions = m_contractions_;
    m_currentTag_ = 2;
    

    int cp = UTF16.charAt(m_cPoints_, 0);
    int cpsize = 1;
    if (UCharacter.isSupplementary(cp)) {
      cpsize = 2;
    }
    if (cpsize < m_cPoints_.length())
    {

      int size = m_cPoints_.length() - m_cPointsOffset_;
      for (int j = 1; j < size; j++)
      {


        if (!UTF16.isTrailSurrogate(m_cPoints_.charAt(m_cPointsOffset_ + j)))
        {
          unsafeCPSet(m_unsafeCP_, m_cPoints_.charAt(m_cPointsOffset_ + j));
        }
      }
      




      if (!UTF16.isTrailSurrogate(m_cPoints_.charAt(m_cPoints_.length() - 1)))
      {
        ContrEndCPSet(m_contrEndCP_, m_cPoints_.charAt(m_cPoints_.length() - 1));
      }
      




      if (isJamo(m_cPoints_.charAt(m_cPointsOffset_))) {
        m_collator_.m_isJamoSpecial_ = true;
      }
      

      m_cPointsOffset_ += cpsize;
      if (!isContraction(CE))
      {
        int firstContractionOffset = addContraction(contractions, 16777215, '\000', CE);
        
        int newCE = processContraction(contractions, element, -268435456);
        
        addContraction(contractions, firstContractionOffset, m_cPoints_.charAt(m_cPointsOffset_), newCE);
        

        addContraction(contractions, firstContractionOffset, 65535, CE);
        
        CE = constructSpecialCE(2, firstContractionOffset);



      }
      else
      {


        int position = findCP(contractions, CE, m_cPoints_.charAt(m_cPointsOffset_));
        
        if (position > 0)
        {
          int eCE = getCE(contractions, CE, position);
          int newCE = processContraction(contractions, element, eCE);
          setContraction(contractions, CE, position, m_cPoints_.charAt(m_cPointsOffset_), newCE);

        }
        else
        {

          int newCE = processContraction(contractions, element, -268435456);
          
          insertContraction(contractions, CE, m_cPoints_.charAt(m_cPointsOffset_), newCE);
        }
      }
      

      m_cPointsOffset_ -= cpsize;
      m_mapping_.setValue(cp, CE);
    }
    else if (!isContraction(CE))
    {
      m_mapping_.setValue(cp, m_mapCE_);

    }
    else
    {
      changeContraction(contractions, CE, '\000', m_mapCE_);
      changeContraction(contractions, CE, 65535, m_mapCE_);
    }
    return CE;
  }
  








  private static final int changeLastCE(ContractionTable table, int element, int value)
  {
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      return 0;
    }
    
    m_CEs_.set(m_CEs_.size() - 1, new Integer(value));
    return constructSpecialCE(m_currentTag_, element & 0xFFFFFF);
  }
  







  private static int nextWeight(CEGenerator cegenerator)
  {
    if (m_rangesLength_ > 0)
    {
      int maxByte = m_ranges_[0].m_count_;
      
      int weight = m_ranges_[0].m_start_;
      if (weight == m_ranges_[0].m_end_)
      {

        m_rangesLength_ -= 1;
        if (m_rangesLength_ > 0) {
          System.arraycopy(m_ranges_, 1, m_ranges_, 0, m_rangesLength_);
          

          m_ranges_[0].m_count_ = maxByte;
        }
        
      }
      else
      {
        m_ranges_[0].m_start_ = incWeight(weight, m_ranges_[0].m_length2_, maxByte);
      }
      

      return weight;
    }
    return -1;
  }
  






  private static final int incWeight(int weight, int length, int maxByte)
  {
    for (;;)
    {
      int b = getWeightByte(weight, length);
      if (b < maxByte) {
        return setWeightByte(weight, length, b + 1);
      }
      


      weight = setWeightByte(weight, length, 4);
      
      length--;
    }
  }
  







  private static final int getWeightByte(int weight, int index)
  {
    return weight >> (4 - index << 3) & 0xFF;
  }
  






  private static final int setWeightByte(int weight, int index, int b)
  {
    index <<= 3;
    
    int mask = -1 >>> index;
    index = 32 - index;
    mask |= 65280 << index;
    return weight & mask | b << index;
  }
  












  private int allocateWeights(int lowerLimit, int upperLimit, int n, int maxByte, WeightRange[] ranges)
  {
    int countBytes = maxByte - 4 + 1;
    


    m_utilLongBuffer_[0] = 1L;
    m_utilLongBuffer_[1] = countBytes;
    m_utilLongBuffer_[2] = (m_utilLongBuffer_[1] * countBytes);
    m_utilLongBuffer_[3] = (m_utilLongBuffer_[2] * countBytes);
    m_utilLongBuffer_[4] = (m_utilLongBuffer_[3] * countBytes);
    int rangeCount = getWeightRanges(lowerLimit, upperLimit, maxByte, countBytes, ranges);
    
    if (rangeCount <= 0) {
      return 0;
    }
    
    long maxCount = 0L;
    for (int i = 0; i < rangeCount; i++) {
      maxCount += m_count_ * m_utilLongBuffer_[(4 - m_length_)];
    }
    
    if (maxCount < n) {
      return 0;
    }
    
    int i = 0;
    for (;;) { m_length2_ = m_length_;
      m_count2_ = m_count_;i++;
      if (i >= rangeCount) {
        break;
      }
    }
    
    for (;;)
    {
      int minLength = 0m_length2_;
      

      Arrays.fill(m_utilCountBuffer_, 0);
      for (int i = 0; i < rangeCount; i++) {
        m_utilCountBuffer_[m_length2_] += m_count2_;
      }
      
      if (n <= m_utilCountBuffer_[minLength] + m_utilCountBuffer_[(minLength + 1)])
      {

        maxCount = 0L;
        rangeCount = 0;
        do {
          maxCount += m_count2_;
          rangeCount++;
        } while (n > maxCount);
        break;
      }
      if (n <= 0m_count2_ * countBytes)
      {

        rangeCount = 1;
        

        long power_1 = m_utilLongBuffer_[(minLength - 0m_length_)];
        
        long power = power_1 * countBytes;
        int count2 = (int)((n + power - 1L) / power);
        int count1 = 0m_count_ - count2;
        
        if (count1 < 1)
        {
          lengthenRange(ranges, 0, maxByte, countBytes); break;
        }
        



        rangeCount = 2;
        1m_end_ = 0m_end_;
        1m_length_ = 0m_length_;
        1m_length2_ = minLength;
        
        int i = 0m_length_;
        int b = getWeightByte(0m_start_, i) + count1 - 1;
        

        if (b <= maxByte) {
          0m_end_ = setWeightByte(0m_start_, i, b);
        }
        else
        {
          0m_end_ = setWeightByte(incWeight(0m_start_, i - 1, maxByte), i, b - countBytes);
        }
        




        b = maxByte << 24 | maxByte << 16 | maxByte << 8 | maxByte;
        
        0m_end_ = (truncateWeight(0m_end_, i) | b >>> (i << 3) & b << (4 - minLength << 3));
        



        1m_start_ = incWeight(0m_end_, minLength, maxByte);
        

        0m_count_ = count1;
        1m_count_ = count2;
        
        0m_count2_ = ((int)(count1 * power_1));
        
        1m_count2_ = ((int)(count2 * power_1));
        

        lengthenRange(ranges, 1, maxByte, countBytes);
        
        break;
      }
      
      for (int i = 0; m_length2_ == minLength; i++) {
        lengthenRange(ranges, i, maxByte, countBytes);
      }
    }
    
    if (rangeCount > 1)
    {
      Arrays.sort(ranges, 0, rangeCount);
    }
    

    0m_count_ = maxByte;
    
    return rangeCount;
  }
  









  private static final int lengthenRange(WeightRange[] range, int offset, int maxByte, int countBytes)
  {
    int length = m_length2_ + 1;
    m_start_ = setWeightTrail(m_start_, length, 4);
    
    m_end_ = setWeightTrail(m_end_, length, maxByte);
    
    m_count2_ *= countBytes;
    m_length2_ = length;
    return length;
  }
  







  private static final int setWeightTrail(int weight, int length, int trail)
  {
    length = 4 - length << 3;
    return weight & 65280 << length | trail << length;
  }
  














  private int getWeightRanges(int lowerLimit, int upperLimit, int maxByte, int countBytes, WeightRange[] ranges)
  {
    int lowerLength = lengthOfWeight(lowerLimit);
    int upperLength = lengthOfWeight(upperLimit);
    if (Utility.compareUnsigned(lowerLimit, upperLimit) >= 0) {
      return 0;
    }
    
    if ((lowerLength < upperLength) && 
      (lowerLimit == truncateWeight(upperLimit, lowerLength))) {
      return 0;
    }
    



















    for (int length = 0; length < 5; length++) {
      m_utilLowerWeightRange_[length].clear();
      m_utilUpperWeightRange_[length].clear();
    }
    m_utilWeightRange_.clear();
    
    int weight = lowerLimit;
    for (int length = lowerLength; length >= 2; length--) {
      m_utilLowerWeightRange_[length].clear();
      int trail = getWeightByte(weight, length);
      if (trail < maxByte) {
        m_utilLowerWeightRange_[length].m_start_ = incWeightTrail(weight, length);
        
        m_utilLowerWeightRange_[length].m_end_ = setWeightTrail(weight, length, maxByte);
        
        m_utilLowerWeightRange_[length].m_length_ = length;
        m_utilLowerWeightRange_[length].m_count_ = (maxByte - trail);
      }
      weight = truncateWeight(weight, length - 1);
    }
    m_utilWeightRange_.m_start_ = incWeightTrail(weight, 1);
    
    weight = upperLimit;
    


    for (int length = upperLength; length >= 2; length--) {
      int trail = getWeightByte(weight, length);
      if (trail > 4) {
        m_utilUpperWeightRange_[length].m_start_ = setWeightTrail(weight, length, 4);
        

        m_utilUpperWeightRange_[length].m_end_ = decWeightTrail(weight, length);
        
        m_utilUpperWeightRange_[length].m_length_ = length;
        m_utilUpperWeightRange_[length].m_count_ = (trail - 4);
      }
      
      weight = truncateWeight(weight, length - 1);
    }
    m_utilWeightRange_.m_end_ = decWeightTrail(weight, 1);
    

    m_utilWeightRange_.m_length_ = 1;
    if (Utility.compareUnsigned(m_utilWeightRange_.m_end_, m_utilWeightRange_.m_start_) >= 0)
    {
      m_utilWeightRange_.m_count_ = ((m_utilWeightRange_.m_end_ - m_utilWeightRange_.m_start_ >>> 24) + 1);


    }
    else
    {

      m_utilWeightRange_.m_count_ = 0;
      
      for (int length = 4; length >= 2; length--) {
        if ((m_utilLowerWeightRange_[length].m_count_ > 0) && (m_utilUpperWeightRange_[length].m_count_ > 0))
        {
          int start = m_utilUpperWeightRange_[length].m_start_;
          int end = m_utilLowerWeightRange_[length].m_end_;
          if ((end >= start) || (incWeight(end, length, maxByte) == start))
          {



            start = m_utilLowerWeightRange_[length].m_start_;
            end = m_utilLowerWeightRange_[length].m_end_ = m_utilUpperWeightRange_[length].m_end_;
            



            m_utilLowerWeightRange_[length].m_count_ = (getWeightByte(end, length) - getWeightByte(start, length) + 1 + countBytes * (getWeightByte(end, length - 1) - getWeightByte(start, length - 1)));
            




            m_utilUpperWeightRange_[length].m_count_ = 0;
            do {
              m_utilLowerWeightRange_[length].m_count_ = (m_utilUpperWeightRange_[length].m_count_ = 0);length--;
            } while (length >= 2);
            


            break;
          }
        }
      }
    }
    

    int rangeCount = 0;
    if (m_utilWeightRange_.m_count_ > 0) {
      ranges[0] = new WeightRange(m_utilWeightRange_);
      rangeCount = 1;
    }
    for (int length = 2; length <= 4; length++)
    {

      if (m_utilUpperWeightRange_[length].m_count_ > 0) {
        ranges[rangeCount] = new WeightRange(m_utilUpperWeightRange_[length]);
        
        rangeCount++;
      }
      if (m_utilLowerWeightRange_[length].m_count_ > 0) {
        ranges[rangeCount] = new WeightRange(m_utilLowerWeightRange_[length]);
        
        rangeCount++;
      }
    }
    return rangeCount;
  }
  






  private static final int truncateWeight(int weight, int length)
  {
    return weight & -1 << (4 - length << 3);
  }
  





  private static final int lengthOfWeight(int weight)
  {
    if ((weight & 0xFFFFFF) == 0) {
      return 1;
    }
    if ((weight & 0xFFFF) == 0) {
      return 2;
    }
    if ((weight & 0xFF) == 0) {
      return 3;
    }
    return 4;
  }
  






  private static final int incWeightTrail(int weight, int length)
  {
    return weight + (1 << (4 - length << 3));
  }
  






  private static int decWeightTrail(int weight, int length)
  {
    return weight - (1 << (4 - length << 3));
  }
  






  private static int findCP(BasicContractionTable tbl, char codePoint)
  {
    int position = 0;
    while (codePoint > m_codePoints_.charAt(position)) {
      position++;
      if (position > m_codePoints_.length()) {
        return -1;
      }
    }
    if (codePoint == m_codePoints_.charAt(position)) {
      return position;
    }
    
    return -1;
  }
  








  private static int findCE(ContractionTable table, int element, char ch)
  {
    if (table == null) {
      return -268435456;
    }
    BasicContractionTable tbl = getBasicContractionTable(table, element);
    if (tbl == null) {
      return -268435456;
    }
    int position = findCP(tbl, ch);
    if ((position > m_CEs_.size()) || (position < 0)) {
      return -268435456;
    }
    return ((Integer)m_CEs_.get(position)).intValue();
  }
  









  private static boolean isTailored(ContractionTable table, int element, char[] array, int offset)
  {
    while (array[offset] != 0) {
      element = findCE(table, element, array[offset]);
      if (element == -268435456) {
        return false;
      }
      if (!isContractionTableElement(element)) {
        return true;
      }
      offset++;
    }
    if (getCE(table, element, 0) != -268435456) {
      return true;
    }
    
    return false;
  }
  






  private void assembleTable(BuildTable t, RuleBasedCollator collator)
  {
    IntTrieBuilder mapping = m_mapping_;
    Vector expansions = m_expansions_;
    ContractionTable contractions = m_contractions_;
    MaxExpansionTable maxexpansion = m_maxExpansions_;
    




    m_contractionOffset_ = 0;
    int contractionsSize = constructTable(contractions);
    



    getMaxExpansionJamo(mapping, maxexpansion, m_maxJamoExpansions_, m_isJamoSpecial_);
    



    setAttributes(collator, m_options_);
    
    int size = expansions.size();
    m_expansion_ = new int[size];
    for (int i = 0; i < size; i++) {
      m_expansion_[i] = ((Integer)expansions.get(i)).intValue();
    }
    
    if (contractionsSize != 0)
    {
      m_contractionIndex_ = new char[contractionsSize];
      m_codePoints_.getChars(0, contractionsSize, m_contractionIndex_, 0);
      


      m_contractionCE_ = new int[contractionsSize];
      for (int i = 0; i < contractionsSize; i++) {
        m_contractionCE_[i] = ((Integer)m_CEs_.get(i)).intValue();
      }
    }
    

    m_trie_ = mapping.serialize(t, RuleBasedCollator.DataManipulate.getInstance());
    





    m_expansionOffset_ = 0;
    size = m_endExpansionCE_.size();
    m_expansionEndCE_ = new int[size - 1];
    for (int i = 1; i < size; i++) {
      m_expansionEndCE_[(i - 1)] = ((Integer)m_endExpansionCE_.get(i)).intValue();
    }
    
    m_expansionEndCEMaxSize_ = new byte[size - 1];
    for (int i = 1; i < size; i++) {
      m_expansionEndCEMaxSize_[(i - 1)] = ((Byte)m_expansionCESize_.get(i)).byteValue();
    }
    

    unsafeCPAddCCNZ(t);
    
    for (int i = 0; i < 1056; i++) {
      int tmp330_328 = i; byte[] tmp330_325 = m_unsafeCP_;tmp330_325[tmp330_328] = ((byte)(tmp330_325[tmp330_328] | UCA_m_unsafe_[i]));
    }
    m_unsafe_ = m_unsafeCP_;
    



    for (int i = 0; i < 1056; i++) {
      int tmp375_373 = i; byte[] tmp375_370 = m_contrEndCP_;tmp375_370[tmp375_373] = ((byte)(tmp375_370[tmp375_373] | UCA_m_contractionEnd_[i]));
    }
    m_contractionEnd_ = m_contrEndCP_;
  }
  






  private static final void setAttributes(RuleBasedCollator collator, CollationRuleParser.OptionSet option)
  {
    latinOneFailed_ = true;
    m_caseFirst_ = m_caseFirst_;
    collator.setDecomposition(m_decomposition_);
    collator.setAlternateHandlingShifted(m_isAlternateHandlingShifted_);
    
    collator.setCaseLevel(m_isCaseLevel_);
    collator.setFrenchCollation(m_isFrenchCollation_);
    m_isHiragana4_ = m_isHiragana4_;
    collator.setStrength(m_strength_);
    m_variableTopValue_ = m_variableTopValue_;
    latinOneFailed_ = false;
  }
  






  private int constructTable(ContractionTable table)
  {
    int tsize = m_elements_.size();
    if (tsize == 0) {
      return 0;
    }
    m_offsets_.clear();
    int position = 0;
    for (int i = 0; i < tsize; i++) {
      m_offsets_.add(new Integer(position));
      position += m_elements_.get(i)).m_CEs_.size();
    }
    
    m_CEs_.clear();
    m_codePoints_.delete(0, m_codePoints_.length());
    
    StringBuffer cpPointer = m_codePoints_;
    Vector CEPointer = m_CEs_;
    for (int i = 0; i < tsize; i++) {
      BasicContractionTable bct = (BasicContractionTable)m_elements_.get(i);
      
      int size = m_CEs_.size();
      char ccMax = '\000';
      char ccMin = 'ÿ';
      int offset = CEPointer.size();
      CEPointer.add(m_CEs_.get(0));
      for (int j = 1; j < size; j++) {
        char ch = m_codePoints_.charAt(j);
        char cc = (char)(UCharacter.getCombiningClass(ch) & 0xFF);
        if (cc > ccMax) {
          ccMax = cc;
        }
        if (cc < ccMin) {
          ccMin = cc;
        }
        cpPointer.append(ch);
        CEPointer.add(m_CEs_.get(j));
      }
      cpPointer.insert(offset, (char)((ccMin == ccMax ? '\001' : '\000') | ccMax));
      
      for (int j = 0; j < size; j++) {
        if (isContractionTableElement(((Integer)CEPointer.get(offset + j)).intValue()))
        {
          int ce = ((Integer)CEPointer.get(offset + j)).intValue();
          CEPointer.set(offset + j, new Integer(constructSpecialCE(getCETag(ce), ((Integer)m_offsets_.get(getContractionOffset(ce))).intValue())));
        }
      }
    }
    



    for (int i = 0; i <= 1114111; i++) {
      int CE = m_mapping_.getValue(i);
      if (isContractionTableElement(CE)) {
        CE = constructSpecialCE(getCETag(CE), ((Integer)m_offsets_.get(getContractionOffset(CE))).intValue());
        

        m_mapping_.setValue(i, CE);
      }
    }
    return position;
  }
  





  private static final int getContractionOffset(int ce)
  {
    return ce & 0xFFFFFF;
  }
  











  private static void getMaxExpansionJamo(IntTrieBuilder mapping, MaxExpansionTable maxexpansion, MaxJamoExpansionTable maxjamoexpansion, boolean jamospecial)
  {
    int VBASE = 4449;
    int TBASE = 4520;
    int VCOUNT = 21;
    int TCOUNT = 28;
    int v = VBASE + VCOUNT - 1;
    int t = TBASE + TCOUNT - 1;
    
    while (v >= VBASE) {
      int ce = mapping.getValue(v);
      if ((ce & 0xF0000000) != -268435456)
      {
        setMaxExpansion(ce, (byte)2, maxexpansion);
      }
      v--;
    }
    
    while (t >= TBASE)
    {
      int ce = mapping.getValue(t);
      if ((ce & 0xF0000000) != -268435456)
      {
        setMaxExpansion(ce, (byte)3, maxexpansion);
      }
      t--;
    }
    
    if (jamospecial)
    {
      int count = m_endExpansionCE_.size();
      byte maxTSize = (byte)(m_maxLSize_ + m_maxVSize_ + m_maxTSize_);
      

      byte maxVSize = (byte)(m_maxLSize_ + m_maxVSize_);
      

      while (count > 0) {
        count--;
        if (((Boolean)m_isV_.get(count)).booleanValue() == true)
        {
          setMaxExpansion(((Integer)m_endExpansionCE_.get(count)).intValue(), maxVSize, maxexpansion);

        }
        else
        {
          setMaxExpansion(((Integer)m_endExpansionCE_.get(count)).intValue(), maxTSize, maxexpansion);
        }
      }
    }
  }
  







  private static final void unsafeCPAddCCNZ(BuildTable t)
  {
    for (char c = '\000'; c < 65535; c = (char)(c + '\001')) {
      char fcd = NormalizerImpl.getFCD16(c);
      if ((fcd >= 'Ā') || ((UTF16.isLeadSurrogate(c)) && (fcd != 0)))
      {

        unsafeCPSet(m_unsafeCP_, c);
      }
    }
    
    if (m_prefixLookup_ != null) {
      Enumeration enum = m_prefixLookup_.elements();
      while (enum.hasMoreElements()) {
        Elements e = (Elements)enum.nextElement();
        






        String comp = Normalizer.compose(m_cPoints_, false);
        unsafeCPSet(m_unsafeCP_, comp.charAt(0));
      }
    }
  }
  













  private boolean enumCategoryRangeClosureCategory(BuildTable t, RuleBasedCollator collator, CollationElementIterator colEl, int start, int limit, int type)
  {
    if ((type != 0) && (type != 17))
    {


      for (int u32 = start; u32 < limit; u32++) {
        int noOfDec = NormalizerImpl.getDecomposition(u32, false, m_utilCharBuffer_, 0, 256);
        

        if (noOfDec > 0)
        {
          String comp = UCharacter.toString(u32);
          String decomp = new String(m_utilCharBuffer_, 0, noOfDec);
          if (!collator.equals(comp, decomp)) {
            m_utilElement_.m_cPoints_ = decomp;
            m_utilElement_.m_prefix_ = 0;
            Elements prefix = (Elements)m_prefixLookup_.get(m_utilElement_);
            
            if (prefix == null) {
              m_utilElement_.m_cPoints_ = comp;
              m_utilElement_.m_prefix_ = 0;
              m_utilElement_.m_prefixChars_ = null;
              colEl.setText(decomp);
              int ce = colEl.next();
              m_utilElement_.m_CELength_ = 0;
              while (ce != -1) {
                m_utilElement_.m_CEs_[(m_utilElement_.m_CELength_++)] = ce;
                

                ce = colEl.next();
              }
            }
            else {
              m_utilElement_.m_cPoints_ = comp;
              m_utilElement_.m_prefix_ = 0;
              m_utilElement_.m_prefixChars_ = null;
              m_utilElement_.m_CELength_ = 1;
              m_utilElement_.m_CEs_[0] = m_mapCE_;
              






              m_utilElement_.m_isThai_ = CollationElementIterator.isThaiPreVowel(m_utilElement_.m_cPoints_.charAt(0));
            }
            

            addAnElement(t, m_utilElement_);
          }
        }
      }
    }
    return true;
  }
  





  private static final boolean isJamo(char ch)
  {
    return ((ch >= 'ᄀ') && (ch <= 'ᄒ')) || ((ch >= 'ᅵ') && (ch <= 'ᅡ')) || ((ch >= 'ᆨ') && (ch <= 'ᇂ'));
  }
  





  private void canonicalClosure(BuildTable t)
  {
    BuildTable temp = new BuildTable(t);
    assembleTable(temp, m_collator_);
    
    CollationElementIterator coleiter = m_collator_.getCollationElementIterator("");
    
    RangeValueIterator typeiter = UCharacter.getTypeIterator();
    RangeValueIterator.Element element = new RangeValueIterator.Element();
    while (typeiter.next(element)) {
      enumCategoryRangeClosureCategory(t, m_collator_, coleiter, start, limit, value);
    }
  }
  


  private void processUCACompleteIgnorables(BuildTable t)
  {
    TrieIterator trieiterator = new TrieIterator(UCA_m_trie_);
    
    RangeValueIterator.Element element = new RangeValueIterator.Element();
    while (trieiterator.next(element)) {
      int start = start;
      int limit = limit;
      if (value == 0) {
        while (start < limit) {
          int CE = m_mapping_.getValue(start);
          if (CE == -268435456) {
            m_utilElement_.m_isThai_ = false;
            m_utilElement_.m_prefix_ = 0;
            m_utilElement_.m_uchars_ = UCharacter.toString(start);
            m_utilElement_.m_cPoints_ = m_utilElement_.m_uchars_;
            m_utilElement_.m_cPointsOffset_ = 0;
            m_utilElement_.m_CELength_ = 1;
            m_utilElement_.m_CEs_[0] = 0;
            addAnElement(t, m_utilElement_);
          }
          start++;
        }
      }
    }
  }
}
