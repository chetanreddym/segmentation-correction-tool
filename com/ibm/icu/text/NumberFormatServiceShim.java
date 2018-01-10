package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICULocaleService.LocaleKey;
import com.ibm.icu.impl.ICULocaleService.LocaleKeyFactory;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import com.ibm.icu.impl.ICUService.Key;
import java.util.Locale;
import java.util.Set;













class NumberFormatServiceShim
  extends NumberFormat.NumberFormatShim
{
  NumberFormatServiceShim() {}
  
  Locale[] getAvailableLocales()
  {
    if (service.isDefault()) {
      return ICULocaleData.getAvailableLocales();
    }
    return service.getAvailableLocales();
  }
  
  private static final class NFFactory extends ICULocaleService.LocaleKeyFactory {
    private NumberFormat.NumberFormatFactory delegate;
    
    NFFactory(NumberFormat.NumberFormatFactory delegate) {
      super();
      
      this.delegate = delegate;
    }
    
    public Object create(ICUService.Key key, ICUService service) {
      if (handlesKey(key)) {
        ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
        Locale loc = lkey.canonicalLocale();
        int kind = lkey.kind();
        
        Object result = delegate.createFormat(loc, kind);
        if (result == null) {
          result = service.getKey(key, null, this);
        }
        return result;
      }
      return null;
    }
    
    protected Set getSupportedIDs() {
      return delegate.getSupportedLocaleNames();
    }
  }
  
  Object registerFactory(NumberFormat.NumberFormatFactory factory) {
    return service.registerFactory(new NFFactory(factory));
  }
  
  boolean unregister(Object registryKey) {
    return service.unregisterFactory((ICUService.Factory)registryKey);
  }
  
  NumberFormat createInstance(Locale desiredLocale, int choice) {
    if (service.isDefault()) {
      return NumberFormat.createInstance(desiredLocale, choice);
    }
    return (NumberFormat)service.get(desiredLocale, choice);
  }
  
  private static class NFService extends ICULocaleService {
    NFService() {
      super();
      






      registerFactory(new NumberFormatServiceShim.1.RBNumberFormatFactory(this));
      markDefault();
    } }
  
  private static ICULocaleService service = new NFService();
}
