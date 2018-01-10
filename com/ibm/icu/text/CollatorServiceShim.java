package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICULocaleService.LocaleKeyFactory;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import com.ibm.icu.impl.ICUService.Key;
import com.ibm.icu.impl.LocaleUtility;
import java.util.Locale;
import java.util.Set;












final class CollatorServiceShim
  extends Collator.ServiceShim
{
  CollatorServiceShim() {}
  
  Collator getInstance(Locale locale)
  {
    if (service.isDefault()) {
      return new RuleBasedCollator(locale);
    }
    try
    {
      return (Collator)((Collator)service.get(locale)).clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new InternalError(e.getMessage());
    }
  }
  
  Object registerInstance(Collator collator, Locale locale)
  {
    return service.registerObject(collator, locale);
  }
  
  class 1$CFactory extends ICULocaleService.LocaleKeyFactory
  {
    Collator.CollatorFactory delegate;
    
    1$CFactory(Collator.CollatorFactory f) {
      super("CFactory");
      


      delegate = f;
    }
    
    public Object handleCreate(Locale loc, int kind, ICUService service) {
      Object coll = delegate.createCollator(loc);
      return coll;
    }
    
    public String getDisplayName(String id, Locale displayLocale) {
      Locale objectLocale = LocaleUtility.getLocaleFromName(id);
      return delegate.getDisplayName(objectLocale, displayLocale);
    }
    
    public Set getSupportedIDs() {
      return delegate.getSupportedLocaleIDs();
    }
  }
  
  Object registerFactory(Collator.CollatorFactory f) { return service.registerFactory(new 1.CFactory(f)); }
  
  boolean unregister(Object registryKey)
  {
    return service.unregisterFactory((ICUService.Factory)registryKey);
  }
  
  Locale[] getAvailableLocales() {
    if (service.isDefault()) {
      return ICULocaleData.getAvailableLocales();
    }
    return service.getAvailableLocales();
  }
  
  String getDisplayName(Locale objectLocale, Locale displayLocale) {
    String id = LocaleUtility.canonicalLocaleString(objectLocale);
    return service.getDisplayName(id, displayLocale);
  }
  
  private static class CService extends ICULocaleService {
    CService() {
      super();
      










      registerFactory(new CollatorServiceShim.1.CollatorFactory(this));
      markDefault();
    }
    
    protected Object handleDefault(ICUService.Key key, String[] actualIDReturn) {
      if (actualIDReturn != null) {
        actualIDReturn[0] = "root";
      }
      return new RuleBasedCollator(new Locale("", "", ""));
    } }
  
  private static ICULocaleService service = new CService();
}
