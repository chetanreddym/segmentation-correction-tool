package com.ibm.icu.impl.data;

import com.ibm.icu.impl.ICUListResourceBundle;


public class LocaleElements_en_GB
  extends ICUListResourceBundle
{
  public LocaleElements_en_GB() { contents = data; }
  
  static final Object[][] data = { { "DateTimeElements", { "2", "1" } }, { "DateTimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm", "EEEE, d MMMM yyyy", "d MMMM yyyy", "d MMM yyyy", "dd/MM/yyyy", "{1} {0}" } }, { "LocaleID", new Integer(2057) }, { "SpelloutRules", "%simplified:\n    -x: minus >>;\n    x.x: << point >>;\n    zero; one; two; three; four; five; six; seven; eight; nine;\n    ten; eleven; twelve; thirteen; fourteen; fifteen; sixteen;\n        seventeen; eighteen; nineteen;\n    20: twenty[->>];\n    30: thirty[->>];\n    40: forty[->>];\n    50: fifty[->>];\n    60: sixty[->>];\n    70: seventy[->>];\n    80: eighty[->>];\n    90: ninety[->>];\n    100: << hundred[ >>];\n    1000: << thousand[ >>];\n    1,000,000: << million[ >>];\n    1,000,000,000,000: << billion[ >>];\n    1,000,000,000,000,000: =#,##0=;\n%default:\n    -x: minus >>;\n    x.x: << point >>;\n    =%simplified=;\n    100: << hundred[ >%%and>];\n    1000: << thousand[ >%%and>];\n    100,000>>: << thousand[>%%commas>];\n    1,000,000: << million[>%%commas>];\n    1,000,000,000,000: << billion[>%%commas>];\n    1,000,000,000,000,000: =#,##0=;\n%%and:\n    and =%default=;\n    100: =%default=;\n%%commas:\n    ' and =%default=;\n    100: , =%default=;\n    1000: , <%default< thousand, >%default>;\n    1,000,000: , =%default=;%%lenient-parse:\n    & ' ' , ',' ;\n" }, { "Version", "2.0" }, { "zoneStrings", { { "Europe/London", "Greenwich Mean Time", "GMT", "British Summer Time", "BST" } } } };
}
