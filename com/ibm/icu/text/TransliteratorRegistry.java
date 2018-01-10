package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.impl.data.ResourceReader;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.util.CaseInsensitiveString;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;




























































class TransliteratorRegistry
{
  private static final char LOCALE_SEP = '_';
  private static final String NO_VARIANT = "";
  private static final String ANY = "Any";
  private Hashtable registry;
  private Hashtable specDAG;
  private Vector availableIDs;
  private static final boolean DEBUG = false;
  
  static class Spec
  {
    private String top;
    private String spec;
    private String nextSpec;
    private String scriptName;
    private boolean isSpecLocale;
    private boolean isNextLocale;
    private ResourceBundle res;
    
    public Spec(String theSpec)
    {
      top = theSpec;
      spec = null;
      scriptName = null;
      try {
        Locale toploc = LocaleUtility.getLocaleFromName(top);
        res = ICULocaleData.getLocaleElements(toploc);
        
        if ((res != null) && (LocaleUtility.isFallbackOf(res.getLocale().toString(), top))) {
          isSpecLocale = true;
        } else {
          isSpecLocale = false;
          res = null;
        }
        

        int[] s = UScript.getCode(top);
        if (s != null) {
          scriptName = UScript.getName(s[0]);
          
          if (scriptName.equalsIgnoreCase(top)) {
            scriptName = null;
          }
        }
      } catch (MissingResourceException e) {
        scriptName = null;
      }
      
      reset();
    }
    
    public boolean hasFallback() {
      return nextSpec != null;
    }
    
    public void reset() {
      if (spec != top) {
        spec = top;
        isSpecLocale = (res != null);
        setupNext();
      }
    }
    
    private void setupNext() {
      isNextLocale = false;
      if (isSpecLocale) {
        nextSpec = spec;
        int i = nextSpec.lastIndexOf('_');
        

        if (i > 0) {
          nextSpec = spec.substring(0, i);
          isNextLocale = true;
        } else {
          nextSpec = scriptName;
        }
        
      }
      else if (nextSpec != scriptName) {
        nextSpec = scriptName;
      } else {
        nextSpec = null;
      }
    }
    




    public String next()
    {
      spec = nextSpec;
      isSpecLocale = isNextLocale;
      setupNext();
      return spec;
    }
    
    public String get() {
      return spec;
    }
    
    public boolean isLocale() {
      return isSpecLocale;
    }
    






    public ResourceBundle getBundle()
    {
      if ((res != null) && (res.getLocale().toString().equals(spec)))
      {
        return res;
      }
      return null;
    }
    
    public String getTop() {
      return top;
    }
  }
  

  static class ResourceEntry
  {
    public String resourceName;
    public String encoding;
    public int direction;
    
    public ResourceEntry(String n, String enc, int d)
    {
      resourceName = n;
      encoding = enc;
      direction = d;
    }
  }
  
  static class LocaleEntry {
    public String rule;
    public int direction;
    
    public LocaleEntry(String r, int d) {
      rule = r;
      direction = d;
    }
  }
  
  static class AliasEntry {
    public String alias;
    
    public AliasEntry(String a) { alias = a; }
  }
  

  static class CompoundRBTEntry
  {
    private String ID;
    
    private String idBlock;
    private int idSplitPoint;
    private RuleBasedTransliterator.Data data;
    private UnicodeSet compoundFilter;
    
    public CompoundRBTEntry(String theID, String theIDBlock, int theIDSplitPoint, RuleBasedTransliterator.Data theData, UnicodeSet theCompoundFilter)
    {
      ID = theID;
      idBlock = theIDBlock;
      idSplitPoint = theIDSplitPoint;
      data = theData;
      compoundFilter = theCompoundFilter;
    }
    
    public Transliterator getInstance() {
      Transliterator t = new RuleBasedTransliterator("_", data, null);
      t = new CompoundTransliterator(ID, idBlock, idSplitPoint, t);
      t.setFilter(compoundFilter);
      return t;
    }
  }
  



  public TransliteratorRegistry()
  {
    registry = new Hashtable();
    specDAG = new Hashtable();
    availableIDs = new Vector();
  }
  










  public Transliterator get(String ID, StringBuffer aliasReturn)
  {
    Object[] entry = find(ID);
    return entry == null ? null : instantiateEntry(ID, entry, aliasReturn);
  }
  







  public void put(String ID, Class transliteratorSubclass, boolean visible)
  {
    registerEntry(ID, transliteratorSubclass, visible);
  }
  






  public void put(String ID, Transliterator.Factory factory, boolean visible)
  {
    registerEntry(ID, factory, visible);
  }
  








  public void put(String ID, String resourceName, String encoding, int dir, boolean visible)
  {
    registerEntry(ID, new ResourceEntry(resourceName, encoding, dir), visible);
  }
  






  public void put(String ID, String alias, boolean visible)
  {
    registerEntry(ID, new AliasEntry(alias), visible);
  }
  






  public void put(String ID, Transliterator trans, boolean visible)
  {
    registerEntry(ID, trans, visible);
  }
  




  public void remove(String ID)
  {
    String[] stv = TransliteratorIDParser.IDtoSTV(ID);
    
    String id = TransliteratorIDParser.STVtoID(stv[0], stv[1], stv[2]);
    registry.remove(new CaseInsensitiveString(id));
    removeSTV(stv[0], stv[1], stv[2]);
    availableIDs.removeElement(new CaseInsensitiveString(id));
  }
  



  private static class IDEnumeration
    implements Enumeration
  {
    Enumeration jdField_enum;
    


    public IDEnumeration(Enumeration e)
    {
      jdField_enum = e;
    }
    
    public boolean hasMoreElements() {
      return (jdField_enum != null) && (jdField_enum.hasMoreElements());
    }
    
    public Object nextElement() {
      return ((CaseInsensitiveString)jdField_enum.nextElement()).getString();
    }
  }
  







  public Enumeration getAvailableIDs()
  {
    return new IDEnumeration(availableIDs.elements());
  }
  




  public Enumeration getAvailableSources()
  {
    return new IDEnumeration(specDAG.keys());
  }
  





  public Enumeration getAvailableTargets(String source)
  {
    CaseInsensitiveString cisrc = new CaseInsensitiveString(source);
    Hashtable targets = (Hashtable)specDAG.get(cisrc);
    if (targets == null) {
      return new IDEnumeration(null);
    }
    return new IDEnumeration(targets.keys());
  }
  





  public Enumeration getAvailableVariants(String source, String target)
  {
    CaseInsensitiveString cisrc = new CaseInsensitiveString(source);
    CaseInsensitiveString citrg = new CaseInsensitiveString(target);
    Hashtable targets = (Hashtable)specDAG.get(cisrc);
    if (targets == null) {
      return new IDEnumeration(null);
    }
    Vector variants = (Vector)targets.get(citrg);
    if (variants == null) {
      return new IDEnumeration(null);
    }
    return new IDEnumeration(variants.elements());
  }
  










  private void registerEntry(String source, String target, String variant, Object entry, boolean visible)
  {
    String s = source;
    if (s.length() == 0) {
      s = "Any";
    }
    String ID = TransliteratorIDParser.STVtoID(source, target, variant);
    registerEntry(ID, s, target, variant, entry, visible);
  }
  




  private void registerEntry(String ID, Object entry, boolean visible)
  {
    String[] stv = TransliteratorIDParser.IDtoSTV(ID);
    
    String id = TransliteratorIDParser.STVtoID(stv[0], stv[1], stv[2]);
    registerEntry(id, stv[0], stv[1], stv[2], entry, visible);
  }
  








  private void registerEntry(String ID, String source, String target, String variant, Object entry, boolean visible)
  {
    CaseInsensitiveString ciID = new CaseInsensitiveString(ID);
    

    if (!(entry instanceof Object[])) {
      entry = new Object[] { entry };
    }
    
    registry.put(ciID, entry);
    if (visible) {
      registerSTV(source, target, variant);
      if (!availableIDs.contains(ciID)) {
        availableIDs.addElement(ciID);
      }
    } else {
      removeSTV(source, target, variant);
      availableIDs.removeElement(ciID);
    }
  }
  









  private void registerSTV(String source, String target, String variant)
  {
    CaseInsensitiveString cisrc = new CaseInsensitiveString(source);
    CaseInsensitiveString citrg = new CaseInsensitiveString(target);
    CaseInsensitiveString civar = new CaseInsensitiveString(variant);
    Hashtable targets = (Hashtable)specDAG.get(cisrc);
    if (targets == null) {
      targets = new Hashtable();
      specDAG.put(cisrc, targets);
    }
    Vector variants = (Vector)targets.get(citrg);
    if (variants == null) {
      variants = new Vector();
      targets.put(citrg, variants);
    }
    


    if (!variants.contains(civar)) {
      if (variant.length() > 0) {
        variants.addElement(civar);
      } else {
        variants.insertElementAt(civar, 0);
      }
    }
  }
  






  private void removeSTV(String source, String target, String variant)
  {
    CaseInsensitiveString cisrc = new CaseInsensitiveString(source);
    CaseInsensitiveString citrg = new CaseInsensitiveString(target);
    CaseInsensitiveString civar = new CaseInsensitiveString(variant);
    Hashtable targets = (Hashtable)specDAG.get(cisrc);
    if (targets == null) {
      return;
    }
    Vector variants = (Vector)targets.get(citrg);
    if (variants == null) {
      return;
    }
    variants.removeElement(civar);
    if (variants.size() == 0) {
      targets.remove(citrg);
      if (targets.size() == 0) {
        specDAG.remove(cisrc);
      }
    }
  }
  







  private Object[] findInDynamicStore(Spec src, Spec trg, String variant)
  {
    String ID = TransliteratorIDParser.STVtoID(src.get(), trg.get(), variant);
    



    return (Object[])registry.get(new CaseInsensitiveString(ID));
  }
  















  private Object[] findInStaticStore(Spec src, Spec trg, String variant)
  {
    Object[] entry = null;
    if (src.isLocale()) {
      entry = findInBundle(src, trg, variant, 0);
    } else if (trg.isLocale()) {
      entry = findInBundle(trg, src, variant, 1);
    }
    


    if (entry != null) {
      registerEntry(src.getTop(), trg.getTop(), variant, entry, false);
    }
    
    return entry;
  }
  













  private Object[] findInBundle(Spec specToOpen, Spec specToFind, String variant, int direction)
  {
    ResourceBundle res = specToOpen.getBundle();
    
    if (res == null)
    {

      return null;
    }
    
    for (int pass = 0; pass < 2; pass++) {
      StringBuffer tag = new StringBuffer();
      



      if (pass == 0) {
        tag.append(direction == 0 ? "TransliterateTo_" : "TransliterateFrom_");
      }
      else {
        tag.append("Transliterate_");
      }
      tag.append(specToFind.get().toUpperCase());
      


      try
      {
        String[] subres = res.getStringArray(tag.toString());
        


        int i = 0;
        if (variant.length() != 0) {
          for (i = 0; i < subres.length; i += 2) {
            if (subres[i].equalsIgnoreCase(variant)) {
              break;
            }
          }
        }
        
        if (i < subres.length)
        {










          int dir = pass == 0 ? 0 : direction;
          return new Object[] { new LocaleEntry(subres[(i + 1)], dir) };
        }
      }
      catch (MissingResourceException e) {}
    }
    




    return null;
  }
  


  private Object[] find(String ID)
  {
    String[] stv = TransliteratorIDParser.IDtoSTV(ID);
    return find(stv[0], stv[1], stv[2]);
  }
  























  private Object[] find(String source, String target, String variant)
  {
    Spec src = new Spec(source);
    Spec trg = new Spec(target);
    Object[] entry = null;
    
    if (variant.length() != 0)
    {

      entry = findInDynamicStore(src, trg, variant);
      if (entry != null) {
        return entry;
      }
      

      entry = findInStaticStore(src, trg, variant);
      if (entry != null) {
        return entry;
      }
    }
    for (;;)
    {
      src.reset();
      for (;;)
      {
        entry = findInDynamicStore(src, trg, "");
        if (entry != null) {
          return entry;
        }
        

        entry = findInStaticStore(src, trg, "");
        if (entry != null) {
          return entry;
        }
        if (!src.hasFallback()) {
          break;
        }
        src.next();
      }
      if (!trg.hasFallback()) {
        break;
      }
      trg.next();
    }
    
    return null;
  }
  




















  private Transliterator instantiateEntry(String ID, Object[] entryWrapper, StringBuffer aliasReturn)
  {
    for (;;)
    {
      Object entry = entryWrapper[0];
      
      if ((entry instanceof RuleBasedTransliterator.Data)) {
        RuleBasedTransliterator.Data data = (RuleBasedTransliterator.Data)entry;
        return new RuleBasedTransliterator(ID, data, null); }
      if ((entry instanceof Class)) {
        try {
          return (Transliterator)((Class)entry).newInstance();
        }
        catch (InstantiationException e) {}catch (IllegalAccessException e2) {}
        return null; }
      if ((entry instanceof AliasEntry)) {
        aliasReturn.append(alias);
        return null; }
      if ((entry instanceof Transliterator.Factory))
        return ((Transliterator.Factory)entry).getInstance(ID);
      if ((entry instanceof CompoundRBTEntry))
        return ((CompoundRBTEntry)entry).getInstance();
      if ((entry instanceof Transliterator)) {
        return (Transliterator)entry;
      }
      






      TransliteratorParser parser = new TransliteratorParser();
      try
      {
        ResourceEntry re = (ResourceEntry)entry;
        ResourceReader r = null;
        try {
          r = new ResourceReader(resourceName, encoding);
        }
        catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e.getMessage());
        }
        
        parser.parse(r, direction);
      }
      catch (ClassCastException e)
      {
        LocaleEntry le = (LocaleEntry)entry;
        parser.parse(rule, direction);
      }
      





      if (idBlock.length() == 0) {
        if (data == null)
        {

          entryWrapper[0] = new AliasEntry(NullTransliterator._ID);
        }
        else
        {
          entryWrapper[0] = data;
        }
      }
      else if (data == null)
      {



        entryWrapper[0] = new AliasEntry(idBlock);
      }
      else
      {
        entryWrapper[0] = new CompoundRBTEntry(ID, idBlock, idSplitPoint, data, compoundFilter);
      }
    }
  }
}
