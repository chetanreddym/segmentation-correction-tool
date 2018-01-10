package com.ibm.icu.util;

import java.util.Locale;

abstract interface CalendarFactory
{
  public abstract Calendar create(TimeZone paramTimeZone, Locale paramLocale);
  
  public abstract String factoryName();
}
