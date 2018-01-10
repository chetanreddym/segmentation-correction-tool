package com.ibm.icu.lang;







public final class UCharacterCategory
{
  public static final int UNASSIGNED = 0;
  





  public static final int GENERAL_OTHER_TYPES = 0;
  





  public static final int UPPERCASE_LETTER = 1;
  





  public static final int LOWERCASE_LETTER = 2;
  





  public static final int TITLECASE_LETTER = 3;
  





  public static final int MODIFIER_LETTER = 4;
  





  public static final int OTHER_LETTER = 5;
  





  public static final int NON_SPACING_MARK = 6;
  




  public static final int ENCLOSING_MARK = 7;
  




  public static final int COMBINING_SPACING_MARK = 8;
  




  public static final int DECIMAL_DIGIT_NUMBER = 9;
  




  public static final int LETTER_NUMBER = 10;
  




  public static final int OTHER_NUMBER = 11;
  




  public static final int SPACE_SEPARATOR = 12;
  




  public static final int LINE_SEPARATOR = 13;
  




  public static final int PARAGRAPH_SEPARATOR = 14;
  




  public static final int CONTROL = 15;
  




  public static final int FORMAT = 16;
  




  public static final int PRIVATE_USE = 17;
  




  public static final int SURROGATE = 18;
  




  public static final int DASH_PUNCTUATION = 19;
  




  public static final int START_PUNCTUATION = 20;
  




  public static final int END_PUNCTUATION = 21;
  




  public static final int CONNECTOR_PUNCTUATION = 22;
  




  public static final int OTHER_PUNCTUATION = 23;
  




  public static final int MATH_SYMBOL = 24;
  




  public static final int CURRENCY_SYMBOL = 25;
  




  public static final int MODIFIER_SYMBOL = 26;
  




  public static final int OTHER_SYMBOL = 27;
  




  public static final int INITIAL_PUNCTUATION = 28;
  




  public static final int FINAL_PUNCTUATION = 29;
  




  public static final int CHAR_CATEGORY_COUNT = 30;
  





  public static String toString(int category)
  {
    switch (category) {
    case 1: 
      return "Letter, Uppercase";
    case 2: 
      return "Letter, Lowercase";
    case 3: 
      return "Letter, Titlecase";
    case 4: 
      return "Letter, Modifier";
    case 5: 
      return "Letter, Other";
    case 6: 
      return "Mark, Non-Spacing";
    case 7: 
      return "Mark, Enclosing";
    case 8: 
      return "Mark, Spacing Combining";
    case 9: 
      return "Number, Decimal Digit";
    case 10: 
      return "Number, Letter";
    case 11: 
      return "Number, Other";
    case 12: 
      return "Separator, Space";
    case 13: 
      return "Separator, Line";
    case 14: 
      return "Separator, Paragraph";
    case 15: 
      return "Other, Control";
    case 16: 
      return "Other, Format";
    case 17: 
      return "Other, Private Use";
    case 18: 
      return "Other, Surrogate";
    case 19: 
      return "Punctuation, Dash";
    case 20: 
      return "Punctuation, Open";
    case 21: 
      return "Punctuation, Close";
    case 22: 
      return "Punctuation, Connector";
    case 23: 
      return "Punctuation, Other";
    case 24: 
      return "Symbol, Math";
    case 25: 
      return "Symbol, Currency";
    case 26: 
      return "Symbol, Modifier";
    case 27: 
      return "Symbol, Other";
    case 28: 
      return "Punctuation, Initial quote";
    case 29: 
      return "Punctuation, Final quote";
    }
    return "Unassigned";
  }
  
  private UCharacterCategory() {}
}
