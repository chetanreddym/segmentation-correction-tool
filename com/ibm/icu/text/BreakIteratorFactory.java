package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

















final class BreakIteratorFactory
  extends BreakIterator.BreakIteratorServiceShim
{
  BreakIteratorFactory() {}
  
  public Object registerInstance(BreakIterator iter, Locale locale, int kind)
  {
    return service.registerObject(iter, locale, kind);
  }
  
  public boolean unregister(Object key) {
    if (service.isDefault()) {
      return false;
    }
    return service.unregisterFactory((ICUService.Factory)key);
  }
  
  public Locale[] getAvailableLocales() {
    if (service == null) {
      return ICULocaleData.getAvailableLocales();
    }
    return service.getAvailableLocales();
  }
  
  public BreakIterator createBreakIterator(Locale locale, int kind)
  {
    if (service.isDefault()) {
      return createBreakInstance(locale, kind);
    }
    return (BreakIterator)service.get(locale, kind);
  }
  
  private static class BFService extends ICULocaleService {
    BFService() {
      super();
      





      registerFactory(new BreakIteratorFactory.1.RBBreakIteratorFactory(this));
      
      markDefault();
    } }
  
  static final ICULocaleService service = new BFService();
  
  private static final String[] KIND_NAMES = { "Character", "Word", "Line", "Sentence", "Title" };
  

  private static BreakIterator createBreakInstance(Locale locale, int kind)
  {
    String prefix = KIND_NAMES[kind];
    return createBreakInstance(locale, kind, prefix + "BreakRules", prefix + "BreakDictionary");
  }
  





  private static BreakIterator createBreakInstance(Locale where, int kind, String rulesName, String dictionaryName)
  {
    ResourceBundle bundle = ICULocaleData.getResourceBundle("BreakIteratorRules", where);
    String[] classNames = bundle.getStringArray("BreakIteratorClasses");
    
    String rules = bundle.getString(rulesName);
    
    if (classNames[kind].equals("RuleBasedBreakIterator")) {
      return new RuleBasedBreakIterator(rules);
    }
    if (classNames[kind].equals("DictionaryBasedBreakIterator"))
    {
      try {
        Object t = bundle.getObject(dictionaryName);
        
        URL url = (URL)t;
        InputStream dictionary = url.openStream();
        return new DictionaryBasedBreakIterator(rules, dictionary);
      }
      catch (IOException e) {}catch (MissingResourceException e) {}
      





      return new RuleBasedBreakIterator(rules);
    }
    




    throw new IllegalArgumentException("Invalid break iterator class \"" + classNames[kind] + "\"");
  }
}
