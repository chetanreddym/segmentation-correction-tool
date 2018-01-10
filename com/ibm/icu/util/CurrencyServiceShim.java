package com.ibm.icu.util;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.ICUService.Factory;
import java.util.Locale;

















final class CurrencyServiceShim
  extends Currency.ServiceShim
{
  CurrencyServiceShim() {}
  
  Locale[] getAvailableLocales()
  {
    if (service.isDefault()) {
      return ICULocaleData.getAvailableLocales();
    }
    return service.getAvailableLocales();
  }
  
  Currency createInstance(Locale loc) {
    if (service.isDefault()) {
      return Currency.createCurrency(loc);
    }
    return (Currency)service.get(loc);
  }
  
  Object registerInstance(Currency currency, Locale locale) {
    return service.registerObject(currency, locale);
  }
  
  boolean unregister(Object registryKey) {
    return service.unregisterFactory((ICUService.Factory)registryKey);
  }
  
  private static class CFService extends ICULocaleService {
    CFService() {
      super();
      






      registerFactory(new CurrencyServiceShim.1.CurrencyFactory(this));
      markDefault();
    } }
  
  static final ICULocaleService service = new CFService();
}
