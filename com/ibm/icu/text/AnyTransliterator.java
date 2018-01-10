package com.ibm.icu.text;

import com.ibm.icu.lang.UScript;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;

















































class AnyTransliterator
  extends Transliterator
{
  static final char TARGET_SEP = '-';
  static final char VARIANT_SEP = '/';
  static final String ANY = "Any";
  static final String NULL_ID = "Null";
  static final String LATIN_PIVOT = "-Latin;Latin-";
  private Map cache;
  private String target;
  private int targetScript;
  
  protected void handleTransliterate(Replaceable text, Transliterator.Position pos, boolean isIncremental)
  {
    int allStart = start;
    int allLimit = limit;
    
    ScriptRunIterator it = new ScriptRunIterator(text, contextStart, contextLimit);
    

    while (it.next())
    {
      if (limit > allStart)
      {


        Transliterator t = getTransliterator(scriptCode);
        
        if (t == null)
        {

          start = limit;


        }
        else
        {

          boolean incremental = (isIncremental) && (limit >= allLimit);
          
          start = Math.max(allStart, start);
          limit = Math.min(allLimit, limit);
          int limit = limit;
          t.filteredTransliterate(text, pos, incremental);
          int delta = limit - limit;
          allLimit += delta;
          it.adjustLimit(delta);
          

          if (limit >= allLimit)
            break;
        }
      }
    }
    limit = allLimit;
  }
  















  private AnyTransliterator(String id, String theTarget, String theVariant, int theTargetScript)
  {
    super(id, null);
    targetScript = theTargetScript;
    cache = new HashMap();
    
    target = theTarget;
    if (theVariant.length() > 0) {
      target = (theTarget + '/' + theVariant);
    }
  }
  







  private Transliterator getTransliterator(int source)
  {
    if ((source == targetScript) || (source == -1)) {
      return null;
    }
    
    Integer key = new Integer(source);
    Transliterator t = (Transliterator)cache.get(key);
    if (t == null) {
      String sourceName = UScript.getName(source);
      String id = sourceName + '-' + target;
      
      t = Transliterator.getInstance(id, 0);
      if (t == null)
      {

        id = sourceName + "-Latin;Latin-" + target;
        t = Transliterator.getInstance(id, 0);
      }
      
      if (t != null) {
        cache.put(key, t);
      }
    }
    
    return t;
  }
  





  static void register()
  {
    HashSet seen = new HashSet();
    
    for (Enumeration s = Transliterator.getAvailableSources(); s.hasMoreElements();) {
      String source = (String)s.nextElement();
      

      if (!source.equalsIgnoreCase("Any"))
      {
        Enumeration t = Transliterator.getAvailableTargets(source);
        while (t.hasMoreElements()) {
          String target = (String)t.nextElement();
          

          if (!seen.contains(target)) {
            seen.add(target);
            

            int targetScript = scriptNameToCode(target);
            if (targetScript != -1)
            {
              Enumeration v = Transliterator.getAvailableVariants(source, target);
              while (v.hasMoreElements()) {
                String variant = (String)v.nextElement();
                

                String id = TransliteratorIDParser.STVtoID("Any", target, variant);
                AnyTransliterator trans = new AnyTransliterator(id, target, variant, targetScript);
                
                Transliterator.registerInstance(trans);
                Transliterator.registerSpecialInverse(target, "Null", false);
              }
            }
          }
        }
      }
    }
  }
  
  private static int scriptNameToCode(String name)
  {
    try {
      int[] codes = UScript.getCode(name);
      return codes != null ? codes[0] : -1;
    } catch (MissingResourceException e) {}
    return -1;
  }
  





  private static class ScriptRunIterator
  {
    private Replaceable text;
    




    private int textStart;
    




    private int textLimit;
    




    public int scriptCode;
    




    public int start;
    



    public int limit;
    




    public ScriptRunIterator(Replaceable text, int start, int limit)
    {
      this.text = text;
      textStart = start;
      textLimit = limit;
      this.limit = start;
    }
    








    public boolean next()
    {
      scriptCode = -1;
      start = limit;
      

      if (start == textLimit) {
        return false;
      }
      
      int ch;
      int s;
      while (start > textStart) {
        ch = text.char32At(start - 1);
        s = UScript.getScript(ch);
        if ((s != 0) && (s != 1)) break;
        start -= 1;
      }
      





      while (limit < textLimit) {
        ch = text.char32At(limit);
        s = UScript.getScript(ch);
        if ((s != 0) && (s != 1)) {
          if (scriptCode == -1)
            scriptCode = s; else {
            if (s != scriptCode)
              break;
          }
        }
        limit += 1;
      }
      


      return true;
    }
    



    public void adjustLimit(int delta)
    {
      limit += delta;
      textLimit += delta;
    }
  }
}
