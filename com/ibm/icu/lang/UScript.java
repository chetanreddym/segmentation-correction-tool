package com.ibm.icu.lang;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.impl.UCharacterProperty;
import java.util.Locale;
import java.util.ResourceBundle;


































































































































































































































































public final class UScript
{
  private static final String copyrightNotice = "Copyright ©2001 IBM Corp.  All rights reserved.";
  public static final int INVALID_CODE = -1;
  public static final int COMMON = 0;
  public static final int INHERITED = 1;
  public static final int ARABIC = 2;
  public static final int ARMENIAN = 3;
  public static final int BENGALI = 4;
  public static final int BOPOMOFO = 5;
  public static final int CHEROKEE = 6;
  public static final int COPTIC = 7;
  public static final int CYRILLIC = 8;
  public static final int DESERET = 9;
  public static final int DEVANAGARI = 10;
  public static final int ETHIOPIC = 11;
  public static final int GEORGIAN = 12;
  public static final int GOTHIC = 13;
  public static final int GREEK = 14;
  public static final int GUJARATI = 15;
  public static final int GURMUKHI = 16;
  public static final int HAN = 17;
  public static final int HANGUL = 18;
  public static final int HEBREW = 19;
  public static final int HIRAGANA = 20;
  public static final int KANNADA = 21;
  public static final int KATAKANA = 22;
  public static final int KHMER = 23;
  public static final int LAO = 24;
  public static final int LATIN = 25;
  public static final int MALAYALAM = 26;
  public static final int MONGOLIAN = 27;
  public static final int MYANMAR = 28;
  public static final int OGHAM = 29;
  public static final int OLD_ITALIC = 30;
  public static final int ORIYA = 31;
  public static final int RUNIC = 32;
  public static final int SINHALA = 33;
  public static final int SYRIAC = 34;
  public static final int TAMIL = 35;
  public static final int TELUGU = 36;
  public static final int THAANA = 37;
  public static final int THAI = 38;
  public static final int TIBETAN = 39;
  public static final int CANADIAN_ABORIGINAL = 40;
  public static final int UCAS = 40;
  public static final int YI = 41;
  public static final int TAGALOG = 42;
  public static final int HANUNOO = 43;
  public static final int BUHID = 44;
  public static final int TAGBANWA = 45;
  public static final int BRAILLE = 46;
  public static final int CYPRIOT = 47;
  public static final int LIMBU = 48;
  public static final int LINEAR_B = 49;
  public static final int OSMANYA = 50;
  public static final int SHAVIAN = 51;
  public static final int TAI_LE = 52;
  public static final int UGARITIC = 53;
  public static final int CODE_LIMIT = 54;
  private static final int SCRIPT_MASK = 127;
  private static final UCharacterProperty prop = ;
  

  private static final String INVALID_NAME = "Invalid";
  

  private static int[] findCodeFromLocale(Locale locale)
  {
    ResourceBundle rb = ICULocaleData.getLocaleElements(locale);
    

    if ((rb == null) || (!LocaleUtility.isFallbackOf(rb.getLocale(), locale))) {
      return null;
    }
    String[] scripts = rb.getStringArray("LocaleScript");
    int[] result = new int[scripts.length];
    int w = 0;
    for (int i = 0; i < scripts.length; i++) {
      int code = UCharacter.getPropertyValueEnum(4106, scripts[i]);
      
      result[(w++)] = code;
    }
    

    if (w < result.length) {
      throw new InternalError("bad locale data, listed " + scripts.length + " scripts but found only " + w);
    }
    
    return result;
  }
  







  public static final int[] getCode(Locale locale)
  {
    return findCodeFromLocale(locale);
  }
  











  public static final int[] getCode(String nameOrAbbrOrLocale)
  {
    try
    {
      return new int[] { UCharacter.getPropertyValueEnum(4106, nameOrAbbrOrLocale) };
    }
    catch (IllegalArgumentException e) {}
    

    return findCodeFromLocale(LocaleUtility.getLocaleFromName(nameOrAbbrOrLocale));
  }
  







  public static final int getScript(int codepoint)
  {
    if (((codepoint >= 0 ? 1 : 0) & (codepoint <= 1114111 ? 1 : 0)) != 0) {
      return prop.getAdditional(codepoint, 0) & 0x7F;
    }
    throw new IllegalArgumentException(Integer.toString(codepoint));
  }
  







  public static final String getName(int scriptCode)
  {
    return UCharacter.getPropertyValueName(4106, scriptCode, 1);
  }
  








  public static final String getShortName(int scriptCode)
  {
    return UCharacter.getPropertyValueName(4106, scriptCode, 0);
  }
  
  private UScript() {}
}
