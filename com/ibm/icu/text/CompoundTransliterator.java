package com.ibm.icu.text;

import java.util.Vector;














































/**
 * @deprecated
 */
public class CompoundTransliterator
  extends Transliterator
{
  private Transliterator[] trans;
  private int compoundRBTIndex;
  private static final String COPYRIGHT = "© IBM Corporation 1999-2001. All rights reserved.";
  
  /**
   * @deprecated
   */
  public CompoundTransliterator(Transliterator[] transliterators, UnicodeFilter filter)
  {
    super(joinIDs(transliterators), filter);
    trans = new Transliterator[transliterators.length];
    System.arraycopy(transliterators, 0, trans, 0, trans.length);
    computeMaximumContextLength();
  }
  





  /**
   * @deprecated
   */
  public CompoundTransliterator(Transliterator[] transliterators)
  {
    this(transliterators, null);
  }
  





  /**
   * @deprecated
   */
  public CompoundTransliterator(String ID, int direction, UnicodeFilter filter)
  {
    super(ID, filter);
    init(ID, direction, -1, null, true);
  }
  


  /**
   * @deprecated
   */
  public CompoundTransliterator(String ID, int direction)
  {
    this(ID, direction, null);
  }
  

  /**
   * @deprecated
   */
  public CompoundTransliterator(String ID)
  {
    this(ID, 0, null);
  }
  







  CompoundTransliterator(String ID, String idBlock, int idSplitPoint, Transliterator splitTrans)
  {
    super(ID, null);
    init(idBlock, 0, idSplitPoint, splitTrans, false);
  }
  




  CompoundTransliterator(Vector list)
  {
    super("", null);
    trans = null;
    compoundRBTIndex = -1;
    init(list, 0, false);
  }
  





















  private void init(String id, int direction, int idSplitPoint, Transliterator splitTrans, boolean fixReverseID)
  {
    Vector list = new Vector();
    UnicodeSet[] compoundFilter = new UnicodeSet[1];
    StringBuffer regenID = new StringBuffer();
    if (!TransliteratorIDParser.parseCompoundID(id, direction, regenID, list, compoundFilter))
    {
      throw new IllegalArgumentException("Invalid ID " + id);
    }
    
    compoundRBTIndex = TransliteratorIDParser.instantiateList(list, splitTrans, idSplitPoint);
    
    init(list, direction, fixReverseID);
    
    if (compoundFilter[0] != null) {
      setFilter(compoundFilter[0]);
    }
  }
  
















  private void init(Vector list, int direction, boolean fixReverseID)
  {
    int count = list.size();
    trans = new Transliterator[count];
    



    for (int i = 0; i < count; i++) {
      int j = direction == 0 ? i : count - 1 - i;
      trans[i] = ((Transliterator)list.elementAt(j));
    }
    

    if ((compoundRBTIndex >= 0) && (direction == 1)) {
      compoundRBTIndex = (count - 1 - compoundRBTIndex);
    }
    


    if ((direction == 1) && (fixReverseID)) {
      StringBuffer newID = new StringBuffer();
      for (i = 0; i < count; i++) {
        if (i > 0) {
          newID.append(';');
        }
        newID.append(trans[i].getID());
      }
      setID(newID.toString());
    }
    
    computeMaximumContextLength();
  }
  




  private static String joinIDs(Transliterator[] transliterators)
  {
    StringBuffer id = new StringBuffer();
    for (int i = 0; i < transliterators.length; i++) {
      if (i > 0) {
        id.append(';');
      }
      id.append(transliterators[i].getID());
    }
    return id.toString();
  }
  

  /**
   * @deprecated
   */
  public int getCount()
  {
    return trans.length;
  }
  


  /**
   * @deprecated
   */
  public Transliterator getTransliterator(int index)
  {
    return trans[index];
  }
  


  private static void _smartAppend(StringBuffer buf, char c)
  {
    if ((buf.length() != 0) && (buf.charAt(buf.length() - 1) != c))
    {
      buf.append(c);
    }
  }
  













  /**
   * @deprecated
   */
  public String toRules(boolean escapeUnprintable)
  {
    StringBuffer rulesSource = new StringBuffer();
    if ((compoundRBTIndex >= 0) && (getFilter() != null))
    {

      rulesSource.append("::").append(getFilter().toPattern(escapeUnprintable)).append(';');
    }
    for (int i = 0; i < trans.length; i++) {
      String rule;
      if (i == compoundRBTIndex) {
        rule = trans[i].toRules(escapeUnprintable);
      } else {
        rule = trans[i].baseToRules(escapeUnprintable);
      }
      _smartAppend(rulesSource, '\n');
      rulesSource.append(rule);
      _smartAppend(rulesSource, ';');
    }
    return rulesSource.toString();
  }
  

  /**
   * @deprecated
   */
  protected UnicodeSet handleGetSourceSet()
  {
    UnicodeSet set = new UnicodeSet();
    for (int i = 0; i < trans.length; i++) {
      set.addAll(trans[i].getSourceSet());
      







      if (!set.isEmpty()) {
        break;
      }
    }
    return set;
  }
  

  /**
   * @deprecated
   */
  public UnicodeSet getTargetSet()
  {
    UnicodeSet set = new UnicodeSet();
    for (int i = 0; i < trans.length; i++)
    {
      set.addAll(trans[i].getTargetSet());
    }
    return set;
  }
  
























































  /**
   * @deprecated
   */
  protected void handleTransliterate(Replaceable text, Transliterator.Position index, boolean incremental)
  {
    if (trans.length < 1) {
      start = limit;
      return;
    }
    




    int compoundLimit = limit;
    


    int compoundStart = start;
    
    int delta = 0;
    
    StringBuffer log = null;
    








    for (int i = 0; i < trans.length; i++) {
      start = compoundStart;
      int limit = limit;
      
      if (start == limit) {
        break;
      }
      
















      trans[i].filteredTransliterate(text, index, incremental);
      







      if ((!incremental) && (start != limit)) {
        throw new RuntimeException("ERROR: Incomplete non-incremental transliteration by " + trans[i].getID());
      }
      







      delta += limit - limit;
      
      if (incremental)
      {




        limit = start;
      }
    }
    
    compoundLimit += delta;
    



    limit = compoundLimit;
  }
  











  private void computeMaximumContextLength()
  {
    int max = 0;
    for (int i = 0; i < trans.length; i++) {
      int len = trans[i].getMaximumContextLength();
      if (len > max) {
        max = len;
      }
    }
    setMaximumContextLength(max);
  }
}
