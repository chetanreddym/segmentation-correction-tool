package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.data.ResourceReader;
import com.ibm.icu.util.CaseInsensitiveString;
import java.io.IOException;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;


















































































































































































































































































































public abstract class Transliterator
{
  public static final int FORWARD = 0;
  public static final int REVERSE = 1;
  private String ID;
  private UnicodeFilter filter;
  
  public static abstract interface Factory
  {
    public abstract Transliterator getInstance(String paramString);
  }
  
  public static class Position
  {
    public int contextStart;
    public int contextLimit;
    public int start;
    public int limit;
    
    public Position()
    {
      this(0, 0, 0, 0);
    }
    





    public Position(int contextStart, int contextLimit, int start)
    {
      this(contextStart, contextLimit, start, contextLimit);
    }
    





    public Position(int contextStart, int contextLimit, int start, int limit)
    {
      this.contextStart = contextStart;
      this.contextLimit = contextLimit;
      this.start = start;
      this.limit = limit;
    }
    



    public Position(Position pos)
    {
      set(pos);
    }
    



    public void set(Position pos)
    {
      contextStart = contextStart;
      contextLimit = contextLimit;
      start = start;
      limit = limit;
    }
    



    public boolean equals(Object obj)
    {
      if ((obj instanceof Position)) {
        Position pos = (Position)obj;
        return (contextStart == contextStart) && (contextLimit == contextLimit) && (start == start) && (limit == limit);
      }
      


      return false;
    }
    



    public String toString()
    {
      return "[cs=" + contextStart + ", s=" + start + ", l=" + limit + ", cl=" + contextLimit + "]";
    }
    










    public final void validate(int length)
    {
      if ((contextStart < 0) || (start < contextStart) || (limit < start) || (contextLimit < limit) || (length < contextLimit))
      {



        throw new IllegalArgumentException("Invalid Position {cs=" + contextStart + ", s=" + start + ", l=" + limit + ", cl=" + contextLimit + "}, len=" + length);
      }
    }
  }
  


















  private int maximumContextLength = 0;
  






































































  protected Transliterator(String ID, UnicodeFilter filter)
  {
    if (ID == null) {
      throw new NullPointerException();
    }
    this.ID = ID;
    this.filter = filter;
  }
  














  public final int transliterate(Replaceable text, int start, int limit)
  {
    if ((start < 0) || (limit < start) || (text.length() < limit))
    {

      return -1;
    }
    
    Position pos = new Position(start, limit, start);
    filteredTransliterate(text, pos, false, true);
    return limit;
  }
  




  public final void transliterate(Replaceable text)
  {
    transliterate(text, 0, text.length());
  }
  






  public final String transliterate(String text)
  {
    ReplaceableString result = new ReplaceableString(text);
    transliterate(result);
    return result.toString();
  }
  
















































  public final void transliterate(Replaceable text, Position index, String insertion)
  {
    index.validate(text.length());
    

    if (insertion != null) {
      text.replace(limit, limit, insertion);
      limit += insertion.length();
      contextLimit += insertion.length();
    }
    
    if ((limit > 0) && (UTF16.isLeadSurrogate(text.charAt(limit - 1))))
    {




      return;
    }
    
    filteredTransliterate(text, index, true, true);
  }
  























  public final void transliterate(Replaceable text, Position index, int insertion)
  {
    transliterate(text, index, UTF16.valueOf(insertion));
  }
  











  public final void transliterate(Replaceable text, Position index)
  {
    transliterate(text, index, null);
  }
  











  public final void finishTransliteration(Replaceable text, Position index)
  {
    index.validate(text.length());
    filteredTransliterate(text, index, false, true);
  }
  
























































  protected abstract void handleTransliterate(Replaceable paramReplaceable, Position paramPosition, boolean paramBoolean);
  























































  private void filteredTransliterate(Replaceable text, Position index, boolean incremental, boolean rollback)
  {
    if ((filter == null) && (!rollback)) {
      handleTransliterate(text, index, incremental);
      return;
    }
    
























    int globalLimit = limit;
    










    StringBuffer log = null;
    



    for (;;)
    {
      if (filter != null)
      {
        int c;
        


        while ((start < globalLimit) && (!filter.contains(c = text.char32At(start)))) {
          int i;
          start += UTF16.getCharCount(i);
        }
        

        limit = start;
        while ((limit < globalLimit) && (filter.contains(c = text.char32At(limit))))
        {
          limit += UTF16.getCharCount(c);
        }
      }
      



      if (start == limit) {
        break;
      }
      




      boolean isIncrementalRun = limit < globalLimit ? false : incremental;
      















      int delta;
      














      if ((rollback) && (isIncrementalRun))
      {






        int runStart = start;
        int runLimit = limit;
        int runLength = runLimit - runStart;
        

        int rollbackOrigin = text.length();
        text.copy(runStart, runLimit, rollbackOrigin);
        





        int passStart = runStart;
        int rollbackStart = rollbackOrigin;
        


        int passLimit = start;
        


        int uncommittedLength = 0;
        

        int totalDelta = 0;
        



        for (;;)
        {
          int charLength = UTF16.getCharCount(text.char32At(passLimit));
          
          passLimit += charLength;
          if (passLimit > runLimit) {
            break;
          }
          uncommittedLength += charLength;
          
          limit = passLimit;
          










          handleTransliterate(text, index, true);
          





          delta = limit - passLimit;
          



          if (start != limit)
          {

            int rs = rollbackStart + delta - (limit - passStart);
            

            text.replace(passStart, limit, "");
            

            text.copy(rs, rs + uncommittedLength, passStart);
            

            start = passStart;
            limit = passLimit;
            contextLimit -= delta;




          }
          else
          {




            passStart = passLimit = start;
            




            rollbackStart += delta + uncommittedLength;
            uncommittedLength = 0;
            

            runLimit += delta;
            totalDelta += delta;
          }
        }
        







        rollbackOrigin += totalDelta;
        globalLimit += totalDelta;
        

        text.replace(rollbackOrigin, rollbackOrigin + runLength, "");
        

        start = passStart;




      }
      else
      {



        int limit = limit;
        handleTransliterate(text, index, isIncrementalRun);
        delta = limit - limit;
        












        if ((!isIncrementalRun) && (start != limit)) {
          throw new RuntimeException("ERROR: Incomplete non-incremental transliteration by " + getID());
        }
        



        globalLimit += delta;
      }
      




      if ((filter == null) || (isIncrementalRun)) {
        break;
      }
    }
    





    limit = globalLimit;
  }
  

















  protected void filteredTransliterate(Replaceable text, Position index, boolean incremental)
  {
    filteredTransliterate(text, index, incremental, false);
  }
  











  protected final int getMaximumContextLength()
  {
    return maximumContextLength;
  }
  




  protected void setMaximumContextLength(int a)
  {
    if (a < 0) {
      throw new IllegalArgumentException("Invalid context length " + a);
    }
    maximumContextLength = a;
  }
  







  public final String getID()
  {
    return ID;
  }
  




  protected final void setID(String id)
  {
    ID = id;
  }
  





  public static final String getDisplayName(String ID)
  {
    return getDisplayName(ID, Locale.getDefault());
  }
  

























  public static String getDisplayName(String id, Locale inLocale)
  {
    ResourceBundle bundle = ICULocaleData.getLocaleElements(inLocale);
    

    String[] stv = TransliteratorIDParser.IDtoSTV(id);
    if (stv == null)
    {
      return "";
    }
    String ID = stv[0] + '-' + stv[1];
    if ((stv[2] != null) && (stv[2].length() > 0)) {
      ID = ID + '/' + stv[2];
    }
    

    String n = (String)displayNameCache.get(new CaseInsensitiveString(ID));
    if (n != null) {
      return n;
    }
    

    try
    {
      return bundle.getString("%Translit%%" + ID);
    }
    catch (MissingResourceException e)
    {
      try
      {
        MessageFormat format = new MessageFormat(bundle.getString("TransliteratorNamePattern"));
        

        Object[] args = { new Integer(2), stv[0], stv[1] };
        

        for (int j = 1; j <= 2; j++) {
          try {
            args[j] = bundle.getString("%Translit%" + (String)args[j]);
          }
          catch (MissingResourceException e) {}
        }
        

        return stv[2].length() > 0 ? format.format(args) + '/' + stv[2] : format.format(args);


      }
      catch (MissingResourceException e2)
      {


        throw new RuntimeException();
      }
    }
  }
  


  public final UnicodeFilter getFilter()
  {
    return filter;
  }
  








  public void setFilter(UnicodeFilter filter)
  {
    this.filter = filter;
  }
  









  public static final Transliterator getInstance(String ID)
  {
    return getInstance(ID, 0);
  }
  















  public static Transliterator getInstance(String ID, int dir)
  {
    StringBuffer canonID = new StringBuffer();
    Vector list = new Vector();
    UnicodeSet[] globalFilter = new UnicodeSet[1];
    if (!TransliteratorIDParser.parseCompoundID(ID, dir, canonID, list, globalFilter)) {
      throw new IllegalArgumentException("Invalid ID " + ID);
    }
    
    TransliteratorIDParser.instantiateList(list, null, -1);
    

    Transliterator t = null;
    switch (list.size()) {
    case 1: 
      t = (Transliterator)list.elementAt(0);
      break;
    default: 
      t = new CompoundTransliterator(list);
    }
    
    t.setID(canonID.toString());
    if (globalFilter[0] != null) {
      t.setFilter(globalFilter[0]);
    }
    return t;
  }
  









  static Transliterator getBasicInstance(String id, String canonID)
  {
    StringBuffer s = new StringBuffer();
    Transliterator t = registry.get(id, s);
    if (s.length() != 0)
    {

      t = getInstance(s.toString(), 0);
    }
    if ((t != null) && (canonID != null)) {
      t.setID(canonID);
    }
    return t;
  }
  








  public static final Transliterator createFromRules(String ID, String rules, int dir)
  {
    Transliterator t = null;
    
    TransliteratorParser parser = new TransliteratorParser();
    parser.parse(rules, dir);
    

    if (idBlock.length() == 0) {
      if (data == null)
      {

        t = new NullTransliterator();
      }
      else
      {
        t = new RuleBasedTransliterator(ID, data, null);
      }
    }
    else if (data == null)
    {



      t = getInstance(idBlock);
      if (t != null) {
        t.setID(ID);
      }
    }
    else
    {
      t = new RuleBasedTransliterator("_", data, null);
      t = new CompoundTransliterator(ID, idBlock, idSplitPoint, t);
      
      if (compoundFilter != null) {
        t.setFilter(compoundFilter);
      }
    }
    

    return t;
  }
  






  public String toRules(boolean escapeUnprintable)
  {
    return baseToRules(escapeUnprintable);
  }
  












  protected final String baseToRules(boolean escapeUnprintable)
  {
    if (escapeUnprintable) {
      StringBuffer rulesSource = new StringBuffer();
      String id = getID();
      for (int i = 0; i < id.length();) {
        int c = UTF16.charAt(id, i);
        if (!Utility.escapeUnprintable(rulesSource, c)) {
          UTF16.append(rulesSource, c);
        }
        i += UTF16.getCharCount(c);
      }
      rulesSource.insert(0, "::");
      rulesSource.append(';');
      return rulesSource.toString();
    }
    return "::" + getID() + ';';
  }
  












  public final UnicodeSet getSourceSet()
  {
    UnicodeSet set = handleGetSourceSet();
    if (filter != null)
    {
      UnicodeSet filterSet;
      try
      {
        filterSet = (UnicodeSet)filter;
      } catch (ClassCastException e) {
        filterSet = new UnicodeSet();
        filter.addMatchSetTo(filterSet);
      }
      set.retainAll(filterSet);
    }
    return set;
  }
  












  protected UnicodeSet handleGetSourceSet()
  {
    return new UnicodeSet();
  }
  










  public UnicodeSet getTargetSet()
  {
    return new UnicodeSet();
  }
  

















  public final Transliterator getInverse()
  {
    return getInstance(ID, 1);
  }
  












  public static void registerClass(String ID, Class transClass, String displayName)
  {
    registry.put(ID, transClass, true);
    if (displayName != null) {
      displayNameCache.put(new CaseInsensitiveString(ID), displayName);
    }
  }
  






  public static void registerFactory(String ID, Factory factory)
  {
    registry.put(ID, factory, true);
  }
  





  public static void registerInstance(Transliterator trans)
  {
    registry.put(trans.getID(), trans, true);
  }
  




  static void registerInstance(Transliterator trans, boolean visible)
  {
    registry.put(trans.getID(), trans, visible);
  }
  

































  static void registerSpecialInverse(String target, String inverseTarget, boolean bidirectional)
  {
    TransliteratorIDParser.registerSpecialInverse(target, inverseTarget, bidirectional);
  }
  







  public static void unregister(String ID)
  {
    displayNameCache.remove(new CaseInsensitiveString(ID));
    registry.remove(ID);
  }
  











  public static final Enumeration getAvailableIDs()
  {
    return registry.getAvailableIDs();
  }
  






  public static final Enumeration getAvailableSources()
  {
    return registry.getAvailableSources();
  }
  






  public static final Enumeration getAvailableTargets(String source)
  {
    return registry.getAvailableTargets(source);
  }
  





  public static final Enumeration getAvailableVariants(String source, String target)
  {
    return registry.getAvailableVariants(source, target);
  }
  





















  private static TransliteratorRegistry registry = new TransliteratorRegistry();
  

  private static Hashtable displayNameCache = new Hashtable();
  private static final String RB_DISPLAY_NAME_PREFIX = "%Translit%%";
  private static final String RB_SCRIPT_DISPLAY_NAME_PREFIX = "%Translit%";
  private static final String RB_DISPLAY_NAME_PATTERN = "TransliteratorNamePattern";
  /**
   * @deprecated
   */
  protected static final char ID_DELIM = ';';
  
  static { ResourceReader r = new ResourceReader("Transliterator_index.txt");
    for (;;) {
      String line = null;
      try {
        line = r.readLine();
      } catch (IOException e) {
        throw new RuntimeException("Can't read Transliterator_index.txt");
      }
      if (line == null) {
        break;
      }
      try
      {
        int pos = 0;
        while ((pos < line.length()) && (UCharacterProperty.isRuleWhiteSpace(line.charAt(pos))))
        {
          pos++;
        }
        
        if ((pos != line.length()) && (line.charAt(pos) != '#'))
        {


          int colon = line.indexOf(':', pos);
          String ID = line.substring(pos, colon);
          pos = colon + 1;
          colon = line.indexOf(':', pos);
          String type = line.substring(pos, colon);
          pos = colon + 1;
          
          if ((type.equals("file")) || (type.equals("internal")))
          {

            colon = line.indexOf(':', pos);
            int c2 = line.indexOf(':', colon + 1);
            int dir;
            switch (line.charAt(c2 + 1)) {
            case 'F': 
              dir = 0;
              break;
            case 'R': 
              dir = 1;
              break;
            default: 
              throw new RuntimeException("Can't parse line: " + line);
            }
            registry.put(ID, line.substring(pos, colon), line.substring(colon + 1, c2), dir, !type.equals("internal"));



          }
          else if (type.equals("alias"))
          {
            registry.put(ID, line.substring(pos), true);
          }
          else {
            throw new RuntimeException("Can't parse line: " + line);
          }
        }
      } catch (StringIndexOutOfBoundsException e) { throw new RuntimeException("Can't parse line: " + line);
      }
    }
    
    registerSpecialInverse(NullTransliterator.SHORT_ID, NullTransliterator.SHORT_ID, false);
    

    registerClass(NullTransliterator._ID, NullTransliterator.class, null);
    
    RemoveTransliterator.register();
    EscapeTransliterator.register();
    UnescapeTransliterator.register();
    LowercaseTransliterator.register();
    UppercaseTransliterator.register();
    TitlecaseTransliterator.register();
    UnicodeNameTransliterator.register();
    NameUnicodeTransliterator.register();
    NormalizationTransliterator.register();
    BreakTransliterator.register();
    AnyTransliterator.register();
  }
  
  /**
   * @deprecated
   */
  protected static final char ID_SEP = '-';
  /**
   * @deprecated
   */
  protected static final char VARIANT_SEP = '/';
  static final boolean DEBUG = false;
  private static final String COPYRIGHT = "© IBM Corporation 1999. All rights reserved.";
}
