package com.ibm.icu.lang;

import com.ibm.icu.impl.TrieIterator;
import com.ibm.icu.impl.UCharacterProperty;







































class UCharacterTypeIterator
  extends TrieIterator
{
  private int[] m_property_;
  
  protected UCharacterTypeIterator(UCharacterProperty property)
  {
    super(m_trie_);
  }
  














  protected int extract(int value)
  {
    if (m_property_ == null) {
      m_property_ = getInstancem_property_;
    }
    return m_property_[value] & 0x1F;
  }
}
