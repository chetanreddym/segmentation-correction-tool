package com.ibm.icu.impl.data;

import com.ibm.icu.impl.ICUListResourceBundle;


public class LocaleElements_fr_CH
  extends ICUListResourceBundle
{
  public LocaleElements_fr_CH() { contents = data; }
  
  static final Object[][] data = { { "DateTimeElements", { "2", "4" } }, { "DateTimePatterns", { "HH.mm' h' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm", "EEEE, d. MMMM yyyy", "d. MMMM yyyy", "d MMM yy", "dd.MM.yy", "{1} {0}" } }, { "LocaleID", new Integer(4108) }, { "NumberElements", { ".", "'", ";", "%", "0", "#", "-", "E", "‰", "∞", "�", "." } }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%", "#E0" } }, { "SpelloutRules", "%main:\n    -x: moins >>;\n    x.x: << virgule >>;\n    zéro; un; deux; trois; quatre; cinq; six; sept; huit; neuf;\n    dix; onze; douze; treize; quatorze; quinze; seize;\n        dix-sept; dix-huit; dix-neuf;\n    20: vingt[->%%alt-ones>];\n    30: trente[->%%alt-ones>];\n    40: quarante[->%%alt-ones>];\n    50: cinquante[->%%alt-ones>];\n    60: soixante[->%%alt-ones>];\n    70: septante[->%%alt-ones>];\n    80: huitante[->%%alt-ones>];\n    90: nonante[->%%alt-ones>];\n    100: cent[ >>];\n    200: << cents[ >>];\n    1000: mille[ >>];\n    1100>: onze cents[ >>];\n    1200: mille >>;\n    2000: << mille[ >>];\n    1,000,000: << million[ >>];\n    1,000,000,000: << milliarde[ >>];\n    1,000,000,000,000: << billion[ >>];\n    1,000,000,000,000,000: =#,##0=;\n%%alt-ones:\n    ; et-un; =%main=;" }, { "Version", "2.0" }, { "zoneStrings", { { "Africa/Casablanca", "GMT", "GMT", "GMT", "GMT" } } } };
}
