package com.ibm.icu.lang;

import com.ibm.icu.impl.UCharacterProperty;

public final class UCharacter { public static abstract interface HangulSyllableType { public static final int NOT_APPLICABLE = 0;
    public static final int LEADING_JAMO = 1;
    public static final int VOWEL_JAMO = 2;
    public static final int TRAILING_JAMO = 3;
    public static final int LV_SYLLABLE = 4;
    public static final int LVT_SYLLABLE = 5;
    public static final int COUNT = 6; }
  
  public static abstract interface NumericType { public static final int NONE = 0;
    public static final int DECIMAL = 1;
    public static final int DIGIT = 2;
    public static final int NUMERIC = 3;
    public static final int COUNT = 4; }
  
  public static abstract interface LineBreak { public static final int UNKNOWN = 0;
    public static final int AMBIGUOUS = 1;
    public static final int ALPHABETIC = 2;
    public static final int BREAK_BOTH = 3;
    public static final int BREAK_AFTER = 4;
    public static final int BREAK_BEFORE = 5;
    public static final int MANDATORY_BREAK = 6;
    public static final int CONTINGENT_BREAK = 7;
    public static final int CLOSE_PUNCTUATION = 8;
    public static final int COMBINING_MARK = 9;
    public static final int CARRIAGE_RETURN = 10;
    public static final int EXCLAMATION = 11;
    public static final int GLUE = 12;
    public static final int HYPHEN = 13;
    public static final int IDEOGRAPHIC = 14;
    public static final int INSEPERABLE = 15;
    public static final int INFIX_NUMERIC = 16;
    public static final int LINE_FEED = 17;
    public static final int NONSTARTER = 18;
    public static final int NUMERIC = 19;
    public static final int OPEN_PUNCTUATION = 20;
    public static final int POSTFIX_NUMERIC = 21;
    public static final int PREFIX_NUMERIC = 22;
    public static final int QUOTATION = 23;
    public static final int COMPLEX_CONTEXT = 24;
    public static final int SURROGATE = 25;
    public static final int SPACE = 26;
    public static final int BREAK_SYMBOLS = 27;
    public static final int ZWSPACE = 28;
    public static final int NEXT_LINE = 29;
    public static final int WORD_JOINER = 30;
    public static final int COUNT = 31; }
  
  public static abstract interface JoiningGroup { public static final int NO_JOINING_GROUP = 0;
    public static final int AIN = 1;
    public static final int ALAPH = 2;
    public static final int ALEF = 3;
    public static final int BEH = 4;
    public static final int BETH = 5;
    public static final int DAL = 6;
    public static final int DALATH_RISH = 7;
    public static final int E = 8;
    public static final int FEH = 9;
    public static final int FINAL_SEMKATH = 10;
    public static final int GAF = 11;
    public static final int GAMAL = 12;
    public static final int HAH = 13;
    public static final int HAMZA_ON_HEH_GOAL = 14;
    public static final int HE = 15;
    public static final int HEH = 16;
    public static final int HEH_GOAL = 17;
    public static final int HETH = 18;
    public static final int KAF = 19;
    public static final int KAPH = 20;
    public static final int KNOTTED_HEH = 21;
    public static final int LAM = 22;
    public static final int LAMADH = 23;
    public static final int MEEM = 24;
    public static final int MIM = 25;
    public static final int NOON = 26;
    public static final int NUN = 27;
    public static final int PE = 28;
    public static final int QAF = 29;
    public static final int QAPH = 30;
    public static final int REH = 31;
    public static final int REVERSED_PE = 32;
    public static final int SAD = 33;
    public static final int SADHE = 34;
    public static final int SEEN = 35;
    public static final int SEMKATH = 36;
    public static final int SHIN = 37;
    public static final int SWASH_KAF = 38;
    public static final int SYRIAC_WAW = 39;
    public static final int TAH = 40;
    public static final int TAW = 41;
    public static final int TEH_MARBUTA = 42;
    public static final int TETH = 43;
    public static final int WAW = 44;
    public static final int YEH = 45;
    public static final int YEH_BARREE = 46;
    public static final int YEH_WITH_TAIL = 47;
    public static final int YUDH = 48;
    public static final int YUDH_HE = 49;
    public static final int ZAIN = 50;
    public static final int FE = 51;
    public static final int KHAPH = 52;
    public static final int ZHAIN = 53;
    public static final int COUNT = 54; }
  
  public static abstract interface JoiningType { public static final int NON_JOINING = 0;
    public static final int JOIN_CAUSING = 1;
    public static final int DUAL_JOINING = 2;
    public static final int LEFT_JOINING = 3; public static final int RIGHT_JOINING = 4; public static final int TRANSPARENT = 5; public static final int COUNT = 6; }
  public static abstract interface DecompositionType { public static final int NONE = 0; public static final int CANONICAL = 1; public static final int COMPAT = 2; public static final int CIRCLE = 3; public static final int FINAL = 4; public static final int FONT = 5; public static final int FRACTION = 6; public static final int INITIAL = 7; public static final int ISOLATED = 8; public static final int MEDIAL = 9; public static final int NARROW = 10; public static final int NOBREAK = 11; public static final int SMALL = 12; public static final int SQUARE = 13; public static final int SUB = 14; public static final int SUPER = 15; public static final int VERTICAL = 16; public static final int WIDE = 17; public static final int COUNT = 18; }
  public static abstract interface EastAsianWidth { public static final int NEUTRAL = 0; public static final int AMBIGUOUS = 1; public static final int HALFWIDTH = 2; public static final int FULLWIDTH = 3; public static final int NARROW = 4; public static final int WIDE = 5; public static final int COUNT = 6; }
  public static final class UnicodeBlock extends Character.Subset { public static final UnicodeBlock NO_BLOCK = new UnicodeBlock("NO_BLOCK", 0);
    




    public static final UnicodeBlock BASIC_LATIN = new UnicodeBlock("BASIC_LATIN", 1);
    



    public static final UnicodeBlock LATIN_1_SUPPLEMENT = new UnicodeBlock("LATIN_1_SUPPLEMENT", 2);
    



    public static final UnicodeBlock LATIN_EXTENDED_A = new UnicodeBlock("LATIN_EXTENDED_A", 3);
    



    public static final UnicodeBlock LATIN_EXTENDED_B = new UnicodeBlock("LATIN_EXTENDED_B", 4);
    



    public static final UnicodeBlock IPA_EXTENSIONS = new UnicodeBlock("IPA_EXTENSIONS", 5);
    



    public static final UnicodeBlock SPACING_MODIFIER_LETTERS = new UnicodeBlock("SPACING_MODIFIER_LETTERS", 6);
    



    public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS", 7);
    




    public static final UnicodeBlock GREEK = new UnicodeBlock("GREEK", 8);
    


    public static final UnicodeBlock CYRILLIC = new UnicodeBlock("CYRILLIC", 9);
    



    public static final UnicodeBlock ARMENIAN = new UnicodeBlock("ARMENIAN", 10);
    



    public static final UnicodeBlock HEBREW = new UnicodeBlock("HEBREW", 11);
    



    public static final UnicodeBlock ARABIC = new UnicodeBlock("ARABIC", 12);
    



    public static final UnicodeBlock SYRIAC = new UnicodeBlock("SYRIAC", 13);
    



    public static final UnicodeBlock THAANA = new UnicodeBlock("THAANA", 14);
    



    public static final UnicodeBlock DEVANAGARI = new UnicodeBlock("DEVANAGARI", 15);
    



    public static final UnicodeBlock BENGALI = new UnicodeBlock("BENGALI", 16);
    



    public static final UnicodeBlock GURMUKHI = new UnicodeBlock("GURMUKHI", 17);
    



    public static final UnicodeBlock GUJARATI = new UnicodeBlock("GUJARATI", 18);
    



    public static final UnicodeBlock ORIYA = new UnicodeBlock("ORIYA", 19);
    


    public static final UnicodeBlock TAMIL = new UnicodeBlock("TAMIL", 20);
    


    public static final UnicodeBlock TELUGU = new UnicodeBlock("TELUGU", 21);
    



    public static final UnicodeBlock KANNADA = new UnicodeBlock("KANNADA", 22);
    



    public static final UnicodeBlock MALAYALAM = new UnicodeBlock("MALAYALAM", 23);
    



    public static final UnicodeBlock SINHALA = new UnicodeBlock("SINHALA", 24);
    



    public static final UnicodeBlock THAI = new UnicodeBlock("THAI", 25);
    


    public static final UnicodeBlock LAO = new UnicodeBlock("LAO", 26);
    


    public static final UnicodeBlock TIBETAN = new UnicodeBlock("TIBETAN", 27);
    



    public static final UnicodeBlock MYANMAR = new UnicodeBlock("MYANMAR", 28);
    



    public static final UnicodeBlock GEORGIAN = new UnicodeBlock("GEORGIAN", 29);
    



    public static final UnicodeBlock HANGUL_JAMO = new UnicodeBlock("HANGUL_JAMO", 30);
    



    public static final UnicodeBlock ETHIOPIC = new UnicodeBlock("ETHIOPIC", 31);
    



    public static final UnicodeBlock CHEROKEE = new UnicodeBlock("CHEROKEE", 32);
    



    public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", 33);
    



    public static final UnicodeBlock OGHAM = new UnicodeBlock("OGHAM", 34);
    


    public static final UnicodeBlock RUNIC = new UnicodeBlock("RUNIC", 35);
    


    public static final UnicodeBlock KHMER = new UnicodeBlock("KHMER", 36);
    


    public static final UnicodeBlock MONGOLIAN = new UnicodeBlock("MONGOLIAN", 37);
    



    public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL = new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL", 38);
    



    public static final UnicodeBlock GREEK_EXTENDED = new UnicodeBlock("GREEK_EXTENDED", 39);
    



    public static final UnicodeBlock GENERAL_PUNCTUATION = new UnicodeBlock("GENERAL_PUNCTUATION", 40);
    



    public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS", 41);
    



    public static final UnicodeBlock CURRENCY_SYMBOLS = new UnicodeBlock("CURRENCY_SYMBOLS", 42);
    





    public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS = new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS", 43);
    



    public static final UnicodeBlock LETTERLIKE_SYMBOLS = new UnicodeBlock("LETTERLIKE_SYMBOLS", 44);
    



    public static final UnicodeBlock NUMBER_FORMS = new UnicodeBlock("NUMBER_FORMS", 45);
    



    public static final UnicodeBlock ARROWS = new UnicodeBlock("ARROWS", 46);
    



    public static final UnicodeBlock MATHEMATICAL_OPERATORS = new UnicodeBlock("MATHEMATICAL_OPERATORS", 47);
    



    public static final UnicodeBlock MISCELLANEOUS_TECHNICAL = new UnicodeBlock("MISCELLANEOUS_TECHNICAL", 48);
    



    public static final UnicodeBlock CONTROL_PICTURES = new UnicodeBlock("CONTROL_PICTURES", 49);
    



    public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION = new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION", 50);
    



    public static final UnicodeBlock ENCLOSED_ALPHANUMERICS = new UnicodeBlock("ENCLOSED_ALPHANUMERICS", 51);
    



    public static final UnicodeBlock BOX_DRAWING = new UnicodeBlock("BOX_DRAWING", 52);
    



    public static final UnicodeBlock BLOCK_ELEMENTS = new UnicodeBlock("BLOCK_ELEMENTS", 53);
    



    public static final UnicodeBlock GEOMETRIC_SHAPES = new UnicodeBlock("GEOMETRIC_SHAPES", 54);
    



    public static final UnicodeBlock MISCELLANEOUS_SYMBOLS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS", 55);
    



    public static final UnicodeBlock DINGBATS = new UnicodeBlock("DINGBATS", 56);
    



    public static final UnicodeBlock BRAILLE_PATTERNS = new UnicodeBlock("BRAILLE_PATTERNS", 57);
    



    public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT = new UnicodeBlock("CJK_RADICALS_SUPPLEMENT", 58);
    



    public static final UnicodeBlock KANGXI_RADICALS = new UnicodeBlock("KANGXI_RADICALS", 59);
    



    public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS", 60);
    



    public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION", 61);
    



    public static final UnicodeBlock HIRAGANA = new UnicodeBlock("HIRAGANA", 62);
    



    public static final UnicodeBlock KATAKANA = new UnicodeBlock("KATAKANA", 63);
    



    public static final UnicodeBlock BOPOMOFO = new UnicodeBlock("BOPOMOFO", 64);
    



    public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO = new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO", 65);
    



    public static final UnicodeBlock KANBUN = new UnicodeBlock("KANBUN", 66);
    



    public static final UnicodeBlock BOPOMOFO_EXTENDED = new UnicodeBlock("BOPOMOFO_EXTENDED", 67);
    



    public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS", 68);
    



    public static final UnicodeBlock CJK_COMPATIBILITY = new UnicodeBlock("CJK_COMPATIBILITY", 69);
    



    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", 70);
    



    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS", 71);
    



    public static final UnicodeBlock YI_SYLLABLES = new UnicodeBlock("YI_SYLLABLES", 72);
    



    public static final UnicodeBlock YI_RADICALS = new UnicodeBlock("YI_RADICALS", 73);
    



    public static final UnicodeBlock HANGUL_SYLLABLES = new UnicodeBlock("HANGUL_SYLLABLES", 74);
    



    public static final UnicodeBlock HIGH_SURROGATES = new UnicodeBlock("HIGH_SURROGATES", 75);
    



    public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES = new UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES", 76);
    



    public static final UnicodeBlock LOW_SURROGATES = new UnicodeBlock("LOW_SURROGATES", 77);
    








    public static final UnicodeBlock PRIVATE_USE_AREA = new UnicodeBlock("PRIVATE_USE_AREA", 78);
    








    public static final UnicodeBlock PRIVATE_USE = PRIVATE_USE_AREA;
    


    public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS", 79);
    



    public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS = new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS", 80);
    



    public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A", 81);
    



    public static final UnicodeBlock COMBINING_HALF_MARKS = new UnicodeBlock("COMBINING_HALF_MARKS", 82);
    



    public static final UnicodeBlock CJK_COMPATIBILITY_FORMS = new UnicodeBlock("CJK_COMPATIBILITY_FORMS", 83);
    



    public static final UnicodeBlock SMALL_FORM_VARIANTS = new UnicodeBlock("SMALL_FORM_VARIANTS", 84);
    



    public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B", 85);
    



    public static final UnicodeBlock SPECIALS = new UnicodeBlock("SPECIALS", 86);
    



    public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS", 87);
    



    public static final UnicodeBlock OLD_ITALIC = new UnicodeBlock("OLD_ITALIC", 88);
    



    public static final UnicodeBlock GOTHIC = new UnicodeBlock("GOTHIC", 89);
    



    public static final UnicodeBlock DESERET = new UnicodeBlock("DESERET", 90);
    



    public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS = new UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS", 91);
    



    public static final UnicodeBlock MUSICAL_SYMBOLS = new UnicodeBlock("MUSICAL_SYMBOLS", 92);
    



    public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS", 93);
    



    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B", 94);
    




    public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT", 95);
    



    public static final UnicodeBlock TAGS = new UnicodeBlock("TAGS", 96);
    





    public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY = new UnicodeBlock("CYRILLIC_SUPPLEMENTARY", 97);
    



    public static final UnicodeBlock TAGALOG = new UnicodeBlock("TAGALOG", 98);
    



    public static final UnicodeBlock HANUNOO = new UnicodeBlock("HANUNOO", 99);
    



    public static final UnicodeBlock BUHID = new UnicodeBlock("BUHID", 100);
    


    public static final UnicodeBlock TAGBANWA = new UnicodeBlock("TAGBANWA", 101);
    



    public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", 102);
    



    public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A = new UnicodeBlock("SUPPLEMENTAL_ARROWS_A", 103);
    



    public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B = new UnicodeBlock("SUPPLEMENTAL_ARROWS_B", 104);
    



    public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B", 105);
    



    public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS", 106);
    



    public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = new UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS", 107);
    



    public static final UnicodeBlock VARIATION_SELECTORS = new UnicodeBlock("VARIATION_SELECTORS", 108);
    



    public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A", 109);
    



    public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B", 110);
    




    public static final UnicodeBlock LIMBU = new UnicodeBlock("LIMBU", 111);
    



    public static final UnicodeBlock TAI_LE = new UnicodeBlock("TAI LE", 112);
    



    public static final UnicodeBlock KHMER_SYMBOLS = new UnicodeBlock("KHMER SYMBOLS", 113);
    




    public static final UnicodeBlock PHONETIC_EXTENSIONS = new UnicodeBlock("PHONETIC EXTENSIONS", 114);
    




    public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS", 115);
    



    public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS = new UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS", 116);
    



    public static final UnicodeBlock LINEAR_B_SYLLABARY = new UnicodeBlock("LINEAR_B_SYLLABARY", 117);
    



    public static final UnicodeBlock LINEAR_B_IDEOGRAMS = new UnicodeBlock("LINEAR_B_IDEOGRAMS", 118);
    



    public static final UnicodeBlock AEGEAN_NUMBERS = new UnicodeBlock("AEGEAN_NUMBERS", 119);
    



    public static final UnicodeBlock UGARITIC = new UnicodeBlock("UGARITIC", 120);
    



    public static final UnicodeBlock SHAVIAN = new UnicodeBlock("SHAVIAN", 121);
    



    public static final UnicodeBlock OSMANYA = new UnicodeBlock("OSMANYA", 122);
    



    public static final UnicodeBlock CYPRIOT_SYLLABARY = new UnicodeBlock("CYPRIOT_SYLLABARY", 123);
    



    public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS = new UnicodeBlock("TAI_XUAN_JING_SYMBOLS", 124);
    




    public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT = new UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT", 125);
    




    public static final UnicodeBlock INVALID_CODE = new UnicodeBlock("INVALID_CODE", -1);
    


    public static final int INVALID_CODE_ID = -1;
    


    public static final int BASIC_LATIN_ID = 1;
    


    public static final int LATIN_1_SUPPLEMENT_ID = 2;
    


    public static final int LATIN_EXTENDED_A_ID = 3;
    


    public static final int LATIN_EXTENDED_B_ID = 4;
    


    public static final int IPA_EXTENSIONS_ID = 5;
    


    public static final int SPACING_MODIFIER_LETTERS_ID = 6;
    


    public static final int COMBINING_DIACRITICAL_MARKS_ID = 7;
    


    public static final int GREEK_ID = 8;
    


    public static final int CYRILLIC_ID = 9;
    


    public static final int ARMENIAN_ID = 10;
    


    public static final int HEBREW_ID = 11;
    


    public static final int ARABIC_ID = 12;
    


    public static final int SYRIAC_ID = 13;
    


    public static final int THAANA_ID = 14;
    


    public static final int DEVANAGARI_ID = 15;
    


    public static final int BENGALI_ID = 16;
    


    public static final int GURMUKHI_ID = 17;
    


    public static final int GUJARATI_ID = 18;
    


    public static final int ORIYA_ID = 19;
    


    public static final int TAMIL_ID = 20;
    


    public static final int TELUGU_ID = 21;
    


    public static final int KANNADA_ID = 22;
    


    public static final int MALAYALAM_ID = 23;
    


    public static final int SINHALA_ID = 24;
    


    public static final int THAI_ID = 25;
    


    public static final int LAO_ID = 26;
    


    public static final int TIBETAN_ID = 27;
    


    public static final int MYANMAR_ID = 28;
    


    public static final int GEORGIAN_ID = 29;
    


    public static final int HANGUL_JAMO_ID = 30;
    


    public static final int ETHIOPIC_ID = 31;
    


    public static final int CHEROKEE_ID = 32;
    


    public static final int UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_ID = 33;
    


    public static final int OGHAM_ID = 34;
    


    public static final int RUNIC_ID = 35;
    


    public static final int KHMER_ID = 36;
    


    public static final int MONGOLIAN_ID = 37;
    


    public static final int LATIN_EXTENDED_ADDITIONAL_ID = 38;
    


    public static final int GREEK_EXTENDED_ID = 39;
    


    public static final int GENERAL_PUNCTUATION_ID = 40;
    


    public static final int SUPERSCRIPTS_AND_SUBSCRIPTS_ID = 41;
    


    public static final int CURRENCY_SYMBOLS_ID = 42;
    


    public static final int COMBINING_MARKS_FOR_SYMBOLS_ID = 43;
    


    public static final int LETTERLIKE_SYMBOLS_ID = 44;
    


    public static final int NUMBER_FORMS_ID = 45;
    


    public static final int ARROWS_ID = 46;
    


    public static final int MATHEMATICAL_OPERATORS_ID = 47;
    


    public static final int MISCELLANEOUS_TECHNICAL_ID = 48;
    


    public static final int CONTROL_PICTURES_ID = 49;
    


    public static final int OPTICAL_CHARACTER_RECOGNITION_ID = 50;
    


    public static final int ENCLOSED_ALPHANUMERICS_ID = 51;
    


    public static final int BOX_DRAWING_ID = 52;
    


    public static final int BLOCK_ELEMENTS_ID = 53;
    


    public static final int GEOMETRIC_SHAPES_ID = 54;
    


    public static final int MISCELLANEOUS_SYMBOLS_ID = 55;
    


    public static final int DINGBATS_ID = 56;
    


    public static final int BRAILLE_PATTERNS_ID = 57;
    


    public static final int CJK_RADICALS_SUPPLEMENT_ID = 58;
    


    public static final int KANGXI_RADICALS_ID = 59;
    


    public static final int IDEOGRAPHIC_DESCRIPTION_CHARACTERS_ID = 60;
    


    public static final int CJK_SYMBOLS_AND_PUNCTUATION_ID = 61;
    


    public static final int HIRAGANA_ID = 62;
    


    public static final int KATAKANA_ID = 63;
    


    public static final int BOPOMOFO_ID = 64;
    


    public static final int HANGUL_COMPATIBILITY_JAMO_ID = 65;
    


    public static final int KANBUN_ID = 66;
    


    public static final int BOPOMOFO_EXTENDED_ID = 67;
    


    public static final int ENCLOSED_CJK_LETTERS_AND_MONTHS_ID = 68;
    


    public static final int CJK_COMPATIBILITY_ID = 69;
    


    public static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A_ID = 70;
    


    public static final int CJK_UNIFIED_IDEOGRAPHS_ID = 71;
    


    public static final int YI_SYLLABLES_ID = 72;
    


    public static final int YI_RADICALS_ID = 73;
    


    public static final int HANGUL_SYLLABLES_ID = 74;
    


    public static final int HIGH_SURROGATES_ID = 75;
    


    public static final int HIGH_PRIVATE_USE_SURROGATES_ID = 76;
    


    public static final int LOW_SURROGATES_ID = 77;
    


    public static final int PRIVATE_USE_AREA_ID = 78;
    


    public static final int PRIVATE_USE_ID = 78;
    


    public static final int CJK_COMPATIBILITY_IDEOGRAPHS_ID = 79;
    


    public static final int ALPHABETIC_PRESENTATION_FORMS_ID = 80;
    


    public static final int ARABIC_PRESENTATION_FORMS_A_ID = 81;
    


    public static final int COMBINING_HALF_MARKS_ID = 82;
    


    public static final int CJK_COMPATIBILITY_FORMS_ID = 83;
    


    public static final int SMALL_FORM_VARIANTS_ID = 84;
    


    public static final int ARABIC_PRESENTATION_FORMS_B_ID = 85;
    


    public static final int SPECIALS_ID = 86;
    


    public static final int HALFWIDTH_AND_FULLWIDTH_FORMS_ID = 87;
    


    public static final int OLD_ITALIC_ID = 88;
    


    public static final int GOTHIC_ID = 89;
    


    public static final int DESERET_ID = 90;
    


    public static final int BYZANTINE_MUSICAL_SYMBOLS_ID = 91;
    


    public static final int MUSICAL_SYMBOLS_ID = 92;
    


    public static final int MATHEMATICAL_ALPHANUMERIC_SYMBOLS_ID = 93;
    


    public static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B_ID = 94;
    


    public static final int CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT_ID = 95;
    


    public static final int TAGS_ID = 96;
    


    public static final int CYRILLIC_SUPPLEMENTARY_ID = 97;
    


    public static final int TAGALOG_ID = 98;
    


    public static final int HANUNOO_ID = 99;
    


    public static final int BUHID_ID = 100;
    


    public static final int TAGBANWA_ID = 101;
    


    public static final int MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A_ID = 102;
    


    public static final int SUPPLEMENTAL_ARROWS_A_ID = 103;
    


    public static final int SUPPLEMENTAL_ARROWS_B_ID = 104;
    


    public static final int MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B_ID = 105;
    


    public static final int SUPPLEMENTAL_MATHEMATICAL_OPERATORS_ID = 106;
    


    public static final int KATAKANA_PHONETIC_EXTENSIONS_ID = 107;
    


    public static final int VARIATION_SELECTORS_ID = 108;
    


    public static final int SUPPLEMENTARY_PRIVATE_USE_AREA_A_ID = 109;
    


    public static final int SUPPLEMENTARY_PRIVATE_USE_AREA_B_ID = 110;
    


    public static final int LIMBU_ID = 111;
    


    public static final int TAI_LE_ID = 112;
    


    public static final int KHMER_SYMBOLS_ID = 113;
    


    public static final int PHONETIC_EXTENSIONS_ID = 114;
    


    public static final int MISCELLANEOUS_SYMBOLS_AND_ARROWS_ID = 115;
    


    public static final int YIJING_HEXAGRAM_SYMBOLS_ID = 116;
    


    public static final int LINEAR_B_SYLLABARY_ID = 117;
    


    public static final int LINEAR_B_IDEOGRAMS_ID = 118;
    


    public static final int AEGEAN_NUMBERS_ID = 119;
    


    public static final int UGARITIC_ID = 120;
    


    public static final int SHAVIAN_ID = 121;
    


    public static final int OSMANYA_ID = 122;
    


    public static final int CYPRIOT_SYLLABARY_ID = 123;
    


    public static final int TAI_XUAN_JING_SYMBOLS_ID = 124;
    


    public static final int VARIATION_SELECTORS_SUPPLEMENT_ID = 125;
    


    public static final int COUNT = 126;
    



    public static UnicodeBlock getInstance(int id)
    {
      if ((id >= 0) && (id < BLOCKS_.length)) {
        return BLOCKS_[id];
      }
      return INVALID_CODE;
    }
    







    public static UnicodeBlock of(int ch)
    {
      if (ch > 1114111) {
        return INVALID_CODE;
      }
      
      return getInstance((UCharacter.PROPERTY_.getAdditional(ch, 0) & 0x7F80) >> 7);
    }
    






    public int getID()
    {
      return m_id_;
    }
    





    private static final UnicodeBlock[] BLOCKS_ = { NO_BLOCK, BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A, LATIN_EXTENDED_B, IPA_EXTENSIONS, SPACING_MODIFIER_LETTERS, COMBINING_DIACRITICAL_MARKS, GREEK, CYRILLIC, ARMENIAN, HEBREW, ARABIC, SYRIAC, THAANA, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, LAO, TIBETAN, MYANMAR, GEORGIAN, HANGUL_JAMO, ETHIOPIC, CHEROKEE, UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS, OGHAM, RUNIC, KHMER, MONGOLIAN, LATIN_EXTENDED_ADDITIONAL, GREEK_EXTENDED, GENERAL_PUNCTUATION, SUPERSCRIPTS_AND_SUBSCRIPTS, CURRENCY_SYMBOLS, COMBINING_MARKS_FOR_SYMBOLS, LETTERLIKE_SYMBOLS, NUMBER_FORMS, ARROWS, MATHEMATICAL_OPERATORS, MISCELLANEOUS_TECHNICAL, CONTROL_PICTURES, OPTICAL_CHARACTER_RECOGNITION, ENCLOSED_ALPHANUMERICS, BOX_DRAWING, BLOCK_ELEMENTS, GEOMETRIC_SHAPES, MISCELLANEOUS_SYMBOLS, DINGBATS, BRAILLE_PATTERNS, CJK_RADICALS_SUPPLEMENT, KANGXI_RADICALS, IDEOGRAPHIC_DESCRIPTION_CHARACTERS, CJK_SYMBOLS_AND_PUNCTUATION, HIRAGANA, KATAKANA, BOPOMOFO, HANGUL_COMPATIBILITY_JAMO, KANBUN, BOPOMOFO_EXTENDED, ENCLOSED_CJK_LETTERS_AND_MONTHS, CJK_COMPATIBILITY, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, CJK_UNIFIED_IDEOGRAPHS, YI_SYLLABLES, YI_RADICALS, HANGUL_SYLLABLES, HIGH_SURROGATES, HIGH_PRIVATE_USE_SURROGATES, LOW_SURROGATES, PRIVATE_USE_AREA, CJK_COMPATIBILITY_IDEOGRAPHS, ALPHABETIC_PRESENTATION_FORMS, ARABIC_PRESENTATION_FORMS_A, COMBINING_HALF_MARKS, CJK_COMPATIBILITY_FORMS, SMALL_FORM_VARIANTS, ARABIC_PRESENTATION_FORMS_B, SPECIALS, HALFWIDTH_AND_FULLWIDTH_FORMS, OLD_ITALIC, GOTHIC, DESERET, BYZANTINE_MUSICAL_SYMBOLS, MUSICAL_SYMBOLS, MATHEMATICAL_ALPHANUMERIC_SYMBOLS, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT, TAGS, CYRILLIC_SUPPLEMENTARY, TAGALOG, HANUNOO, BUHID, TAGBANWA, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A, SUPPLEMENTAL_ARROWS_A, SUPPLEMENTAL_ARROWS_B, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B, SUPPLEMENTAL_MATHEMATICAL_OPERATORS, KATAKANA_PHONETIC_EXTENSIONS, VARIATION_SELECTORS, SUPPLEMENTARY_PRIVATE_USE_AREA_A, SUPPLEMENTARY_PRIVATE_USE_AREA_B };
    


































    private int m_id_;
    



































    private UnicodeBlock(String name, int id)
    {
      super();
      m_id_ = id;
    }
  }
  









































































  public static final int MIN_VALUE = 0;
  








































































  public static final int MAX_VALUE = 1114111;
  








































































  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  








































































  public static final int REPLACEMENT_CHAR = 65533;
  








































































  public static final double NO_NUMERIC_VALUE = -1.23456789E8D;
  







































































  private static final int FOLD_CASE_OPTIONS_MASK = 255;
  







































































  public static final int FOLD_CASE_DEFAULT = 0;
  







































































  public static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1;
  







































































  public static int digit(int ch, int radix)
  {
    int props = getProperty(ch);
    if (getNumericType(props) != 1) {
      return radix <= 10 ? -1 : getEuropeanDigit(ch);
    }
    
    if (isNotExceptionIndicator(props))
    {







      if (props >= 0) {
        return UCharacterProperty.getSignedValue(props);
      }
    }
    else {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 4))
      {
        int result = PROPERTY_.getException(index, 4);
        
        if (result >= 0) {
          return result;
        }
      }
    }
    
    if (radix > 10) {
      int result = getEuropeanDigit(ch);
      if ((result >= 0) && (result < radix)) {
        return result;
      }
    }
    return -1;
  }
  













  public static int digit(int ch)
  {
    return digit(ch, 10);
  }
  














  public static int getNumericValue(int ch)
  {
    int props = getProperty(ch);
    if ((props & 0x7000) == 0) {
      return getEuropeanDigit(ch);
    }
    

    if (isNotExceptionIndicator(props))
    {
      return props >>> 20;
    }
    
    int index = UCharacterProperty.getExceptionIndex(props);
    if ((!PROPERTY_.hasExceptionValue(index, 5)) && (PROPERTY_.hasExceptionValue(index, 4)))
    {


      return PROPERTY_.getException(index, 4);
    }
    

    int europeannumeric = getEuropeanDigit(ch);
    if (europeannumeric >= 0) {
      return europeannumeric;
    }
    return -2;
  }
  
















  public static double getUnicodeNumericValue(int ch)
  {
    int props = PROPERTY_.getProperty(ch);
    int numericType = getNumericType(props);
    if ((numericType > 0) && (numericType < 4)) {
      if (isNotExceptionIndicator(props)) {
        return UCharacterProperty.getSignedValue(props);
      }
      
      int index = UCharacterProperty.getExceptionIndex(props);
      boolean nex = false;
      boolean dex = false;
      double numerator = 0.0D;
      if (PROPERTY_.hasExceptionValue(index, 4))
      {
        int num = PROPERTY_.getException(index, 4);
        




        if (num >= 2147483392) {
          num &= 0xFF;
          
          numerator = Math.pow(10.0D, num);
        }
        else {
          numerator = num;
        }
        nex = true;
      }
      double denominator = 0.0D;
      if (PROPERTY_.hasExceptionValue(index, 5))
      {
        denominator = PROPERTY_.getException(index, 5);
        

        if (numerator != 0.0D) {
          return numerator / denominator;
        }
        dex = true;
      }
      
      if (nex) {
        if (dex) {
          return numerator / denominator;
        }
        return numerator;
      }
      if (dex) {
        return 1.0D / denominator;
      }
    }
    
    return -1.23456789E8D;
  }
  











  public static int getType(int ch)
  {
    return getProperty(ch) & 0x1F;
  }
  











  public static boolean isDefined(int ch)
  {
    return getType(ch) != 0;
  }
  












  public static boolean isDigit(int ch)
  {
    return getType(ch) == 9;
  }
  










  public static boolean isISOControl(int ch)
  {
    return (ch >= 0) && (ch <= 159) && ((ch <= 31) || (ch >= 127));
  }
  









  public static boolean isLetter(int ch)
  {
    return (1 << getType(ch) & 0x3E) != 0;
  }
  













  public static boolean isLetterOrDigit(int ch)
  {
    return (1 << getType(ch) & 0x23E) != 0;
  }
  




















  public static boolean isLowerCase(int ch)
  {
    return getType(ch) == 2;
  }
  




























  public static boolean isWhitespace(int ch)
  {
    return (((1 << getType(ch) & 0x7000) != 0) && (ch != 160) && (ch != 8239) && (ch != 65279)) || ((ch >= 9) && (ch <= 13)) || ((ch >= 28) && (ch <= 31));
  }
  

















  public static boolean isSpaceChar(int ch)
  {
    return (1 << getType(ch) & 0x7000) != 0;
  }
  

















  public static boolean isTitleCase(int ch)
  {
    return getType(ch) == 3;
  }
  





























  public static boolean isUnicodeIdentifierPart(int ch)
  {
    return ((1 << getType(ch) & 0x40077E) != 0) || (isIdentifierIgnorable(ch));
  }
  

































  public static boolean isUnicodeIdentifierStart(int ch)
  {
    return (1 << getType(ch) & 0x43E) != 0;
  }
  





















  public static boolean isIdentifierIgnorable(int ch)
  {
    if (ch <= 159) {
      return (isISOControl(ch)) && ((ch < 9) || (ch > 13)) && ((ch < 28) || (ch > 31));
    }
    

    return getType(ch) == 16;
  }
  


















  public static boolean isUpperCase(int ch)
  {
    return getType(ch) == 1;
  }
  



















  public static int toLowerCase(int ch)
  {
    int props = PROPERTY_.getProperty(ch);
    
    if (isNotExceptionIndicator(props)) {
      int cat = 0x1F & props;
      if ((cat == 1) || (cat == 3))
      {
        return ch + UCharacterProperty.getSignedValue(props);
      }
    }
    else
    {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 1))
      {
        return PROPERTY_.getException(index, 1);
      }
    }
    
    return ch;
  }
  













  public static String toString(int ch)
  {
    if ((ch < 0) || (ch > 1114111)) {
      return null;
    }
    
    if (ch < 65536) {
      return String.valueOf((char)ch);
    }
    
    StringBuffer result = new StringBuffer();
    result.append(com.ibm.icu.text.UTF16.getLeadSurrogate(ch));
    result.append(com.ibm.icu.text.UTF16.getTrailSurrogate(ch));
    return result.toString();
  }
  


















  public static int toTitleCase(int ch)
  {
    int props = PROPERTY_.getProperty(ch);
    
    if (isNotExceptionIndicator(props)) {
      if ((0x1F & props) == 2)
      {

        return ch - UCharacterProperty.getSignedValue(props);
      }
    }
    else {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 2))
      {
        return PROPERTY_.getException(index, 2);
      }
      


      if (PROPERTY_.hasExceptionValue(index, 0))
      {
        return PROPERTY_.getException(index, 0);
      }
    }
    

    return ch;
  }
  















  public static int toUpperCase(int ch)
  {
    int props = PROPERTY_.getProperty(ch);
    
    if (isNotExceptionIndicator(props)) {
      if ((0x1F & props) == 2)
      {

        return ch - UCharacterProperty.getSignedValue(props);
      }
    }
    else
    {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 0))
      {
        return PROPERTY_.getException(index, 0);
      }
    }
    
    return ch;
  }
  











  public static boolean isSupplementary(int ch)
  {
    return (ch >= 65536) && (ch <= 1114111);
  }
  








  public static boolean isBMP(int ch)
  {
    return (ch >= 0) && (ch <= 65535);
  }
  







  public static boolean isPrintable(int ch)
  {
    int cat = getType(ch);
    
    return (cat != 0) && (cat != 15) && (cat != 16) && (cat != 17) && (cat != 18) && (cat != 0);
  }
  













  public static boolean isBaseForm(int ch)
  {
    int cat = getType(ch);
    
    return (cat == 9) || (cat == 11) || (cat == 10) || (cat == 1) || (cat == 2) || (cat == 3) || (cat == 4) || (cat == 5) || (cat == 6) || (cat == 7) || (cat == 8);
  }
  





















  public static int getDirection(int ch)
  {
    return getProperty(ch) >> 6 & 0x1F;
  }
  










  public static boolean isMirrored(int ch)
  {
    return (PROPERTY_.getProperty(ch) & 0x800) != 0;
  }
  
















  public static int getMirror(int ch)
  {
    int props = PROPERTY_.getProperty(ch);
    

    if ((props & 0x800) != 0) {
      if (isNotExceptionIndicator(props)) {
        return ch + UCharacterProperty.getSignedValue(props);
      }
      

      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 6))
      {
        return PROPERTY_.getException(index, 6);
      }
    }
    
    return ch;
  }
  






  public static int getCombiningClass(int ch)
  {
    if ((ch < 0) || (ch > 1114111)) {
      throw new IllegalArgumentException("Codepoint out of bounds");
    }
    return com.ibm.icu.impl.NormalizerImpl.getCombiningClass(ch);
  }
  












  public static boolean isLegal(int ch)
  {
    if (ch < 0) {
      return false;
    }
    if (ch < 55296) {
      return true;
    }
    if (ch <= 57343) {
      return false;
    }
    if (com.ibm.icu.impl.UCharacterUtility.isNonCharacter(ch)) {
      return false;
    }
    return ch <= 1114111;
  }
  













  public static boolean isLegal(String str)
  {
    int size = str.length();
    
    for (int i = 0; i < size; i++)
    {
      int codepoint = com.ibm.icu.text.UTF16.charAt(str, i);
      if (!isLegal(codepoint)) {
        return false;
      }
      if (isSupplementary(codepoint)) {
        i++;
      }
    }
    return true;
  }
  





  public static com.ibm.icu.util.VersionInfo getUnicodeVersion()
  {
    return PROPERTY_m_unicodeVersion_;
  }
  











  public static String getName(int ch)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getName(ch, 0);
  }
  











  public static String getName1_0(int ch)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getName(ch, 1);
  }
  



















  public static String getExtendedName(int ch)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getName(ch, 2);
  }
  










  public static String getISOComment(int ch)
  {
    if ((ch < 0) || (ch > 1114111)) {
      return null;
    }
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    String result = NAME_.getGroupName(ch, 3);
    
    return result;
  }
  










  public static int getCharFromName(String name)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getCharFromName(0, name);
  }
  











  public static int getCharFromName1_0(String name)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getCharFromName(1, name);
  }
  




















  public static int getCharFromExtendedName(String name)
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return NAME_.getCharFromName(2, name);
  }
  


































  public static String getPropertyName(int property, int nameChoice)
  {
    return PNAMES_.getPropertyName(property, nameChoice);
  }
  





















  public static int getPropertyEnum(String propertyAlias)
  {
    return PNAMES_.getPropertyEnum(propertyAlias);
  }
  

















































  public static String getPropertyValueName(int property, int value, int nameChoice)
  {
    return PNAMES_.getPropertyValueName(property, value, nameChoice);
  }
  































  public static int getPropertyValueEnum(int property, String valueAlias)
  {
    return PNAMES_.getPropertyValueEnum(property, valueAlias);
  }
  









  public static int getCodePoint(char lead, char trail)
  {
    if ((lead >= 55296) && (lead <= 56319) && (trail >= 56320) && (trail <= 57343))
    {


      return UCharacterProperty.getRawSupplementary(lead, trail);
    }
    throw new IllegalArgumentException("Illegal surrogate characters");
  }
  








  public static int getCodePoint(char char16)
  {
    if (isLegal(char16)) {
      return char16;
    }
    throw new IllegalArgumentException("Illegal codepoint");
  }
  







  public static String toUpperCase(String str)
  {
    return toUpperCase(java.util.Locale.getDefault(), str);
  }
  







  public static String toLowerCase(String str)
  {
    return toLowerCase(java.util.Locale.getDefault(), str);
  }
  


















  public static String toTitleCase(String str, com.ibm.icu.text.BreakIterator breakiter)
  {
    return toTitleCase(java.util.Locale.getDefault(), str, breakiter);
  }
  








  public static String toUpperCase(java.util.Locale locale, String str)
  {
    if (locale == null) {
      locale = java.util.Locale.getDefault();
    }
    return PROPERTY_.toUpperCase(locale, str, 0, str.length());
  }
  








  public static String toLowerCase(java.util.Locale locale, String str)
  {
    int length = str.length();
    StringBuffer result = new StringBuffer(length);
    if (locale == null) {
      locale = java.util.Locale.getDefault();
    }
    PROPERTY_.toLowerCase(locale, str, 0, length, result);
    return result.toString();
  }
  




















  public static String toTitleCase(java.util.Locale locale, String str, com.ibm.icu.text.BreakIterator breakiter)
  {
    if (breakiter == null) {
      if (locale == null) {
        locale = java.util.Locale.getDefault();
      }
      breakiter = com.ibm.icu.text.BreakIterator.getWordInstance(locale);
    }
    return PROPERTY_.toTitleCase(locale, str, breakiter);
  }
  





















































  public static int foldCase(int ch, boolean defaultmapping)
  {
    int props = PROPERTY_.getProperty(ch);
    if (isNotExceptionIndicator(props)) {
      int type = 0x1F & props;
      if ((type == 1) || (type == 3))
      {
        return ch + UCharacterProperty.getSignedValue(props);
      }
    }
    else {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 8))
      {
        int exception = PROPERTY_.getException(index, 8);
        
        if (exception != 0) {
          int foldedcasech = PROPERTY_.getFoldCase(exception & 0xFFFF);
          
          if (foldedcasech != 0) {
            return foldedcasech;
          }
        }
        else
        {
          if (defaultmapping)
          {
            if ((ch == 73) || (ch == 304))
            {


              return 105;
            }
          }
          else
          {
            if (ch == 73)
            {
              return 305;
            }
            if (ch == 304)
            {

              return 105;
            }
          }
          
          return ch;
        }
      }
      if (PROPERTY_.hasExceptionValue(index, 1))
      {

        return PROPERTY_.getException(index, 1);
      }
    }
    

    return ch;
  }
  

















  public static String foldCase(String str, boolean defaultmapping)
  {
    int size = str.length();
    StringBuffer result = new StringBuffer(size);
    int offset = 0;
    


    while (offset < size) {
      int ch = com.ibm.icu.text.UTF16.charAt(str, offset);
      offset += com.ibm.icu.text.UTF16.getCharCount(ch);
      int props = PROPERTY_.getProperty(ch);
      if (isNotExceptionIndicator(props)) {
        int type = 0x1F & props;
        if ((type == 1) || (type == 3))
        {
          ch += UCharacterProperty.getSignedValue(props);
        }
      }
      else {
        int index = UCharacterProperty.getExceptionIndex(props);
        if (PROPERTY_.hasExceptionValue(index, 8))
        {
          int exception = PROPERTY_.getException(index, 8);
          
          if (exception != 0) {
            PROPERTY_.getFoldCase(exception & 0xFFFF, exception >> 24, result); continue;
          }
          


          if ((ch != 73) && (ch != 304))
          {

            com.ibm.icu.text.UTF16.append(result, ch);
            continue;
          }
          if (defaultmapping)
          {
            if (ch == 73)
            {
              result.append('i'); continue;
            }
            
            if (ch != 304) {
              continue;
            }
            result.append('i');
            
            result.append('̇'); continue;
          }
          


          if (ch == 73)
          {
            result.append('ı'); continue;
          }
          if (ch != 304) {
            continue;
          }
          result.append('i'); continue;
        }
        






        if (PROPERTY_.hasExceptionValue(index, 1))
        {
          ch = PROPERTY_.getException(index, 1);
        }
      }
      




      com.ibm.icu.text.UTF16.append(result, ch);
    }
    
    return result.toString();
  }
  
















































  public static int foldCase(int ch, int options)
  {
    int props = PROPERTY_.getProperty(ch);
    if (isNotExceptionIndicator(props)) {
      int type = 0x1F & props;
      if ((type == 1) || (type == 3))
      {
        return ch + UCharacterProperty.getSignedValue(props);
      }
    }
    else {
      int index = UCharacterProperty.getExceptionIndex(props);
      if (PROPERTY_.hasExceptionValue(index, 8))
      {
        int exception = PROPERTY_.getException(index, 8);
        
        if (exception != 0) {
          int foldedcasech = PROPERTY_.getFoldCase(exception & 0xFFFF);
          
          if (foldedcasech != 0) {
            return foldedcasech;
          }
        }
        else
        {
          if ((options & 0xFF) == 0)
          {
            if ((ch == 73) || (ch == 304))
            {


              return 105;
            }
          }
          else
          {
            if (ch == 73)
            {
              return 305;
            }
            if (ch == 304)
            {

              return 105;
            }
          }
          
          return ch;
        }
      }
      if (PROPERTY_.hasExceptionValue(index, 1))
      {

        return PROPERTY_.getException(index, 1);
      }
    }
    

    return ch;
  }
  














  public static final String foldCase(String str, int options)
  {
    int size = str.length();
    StringBuffer result = new StringBuffer(size);
    int offset = 0;
    


    while (offset < size) {
      int ch = com.ibm.icu.text.UTF16.charAt(str, offset);
      offset += com.ibm.icu.text.UTF16.getCharCount(ch);
      int props = getProperty(ch);
      if ((props & 0x20) == 0) {
        int type = 0x1F & props;
        if ((type == 1) || (type == 3))
        {
          ch += UCharacterProperty.getSignedValue(props);
        }
      }
      else {
        int index = UCharacterProperty.getExceptionIndex(props);
        if (PROPERTY_.hasExceptionValue(index, 8))
        {
          int exception = PROPERTY_.getException(index, 8);
          
          if (exception != 0) {
            PROPERTY_.getFoldCase(exception & 0xFFFF, exception >> 24, result); continue;
          }
          


          if ((ch != 73) && (ch != 304))
          {

            com.ibm.icu.text.UTF16.append(result, ch);
            continue;
          }
          if ((options & 0xFF) == 0)
          {
            if (ch == 73)
            {
              result.append('i'); continue;
            }
            
            if (ch != 304) {
              continue;
            }
            result.append('i');
            
            result.append('̇'); continue;
          }
          


          if (ch == 73)
          {
            result.append('ı'); continue;
          }
          if (ch != 304) {
            continue;
          }
          result.append('i'); continue;
        }
        






        if (PROPERTY_.hasExceptionValue(index, 1))
        {
          ch = PROPERTY_.getException(index, 1);
        }
      }
      




      com.ibm.icu.text.UTF16.append(result, ch);
    }
    
    return result.toString();
  }
  











  public static int getHanNumericValue(int ch)
  {
    switch (ch)
    {
    case 12295: 
    case 38646: 
      return 0;
    case 19968: 
    case 22777: 
      return 1;
    case 20108: 
    case 36019: 
      return 2;
    case 19977: 
    case 21443: 
      return 3;
    case 22232: 
    case 32902: 
      return 4;
    case 20116: 
    case 20237: 
      return 5;
    case 20845: 
    case 38520: 
      return 6;
    case 19971: 
    case 26578: 
      return 7;
    case 20843: 
    case 25420: 
      return 8;
    case 20061: 
    case 29590: 
      return 9;
    case 21313: 
    case 25342: 
      return 10;
    case 20336: 
    case 30334: 
      return 100;
    case 20191: 
    case 21315: 
      return 1000;
    case 33356: 
      return 10000;
    case 20740: 
      return 100000000;
    }
    return -1;
  }
  


















  public static com.ibm.icu.util.RangeValueIterator getTypeIterator()
  {
    return new UCharacterTypeIterator(PROPERTY_);
  }
  




















  public static com.ibm.icu.util.ValueIterator getNameIterator()
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return new UCharacterNameIterator(NAME_, 0);
  }
  




















  public static com.ibm.icu.util.ValueIterator getName1_0Iterator()
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return new UCharacterNameIterator(NAME_, 1);
  }
  




















  public static com.ibm.icu.util.ValueIterator getExtendedNameIterator()
  {
    if (NAME_ == null) {
      throw new RuntimeException("Could not load unames.icu");
    }
    return new UCharacterNameIterator(NAME_, 2);
  }
  













  public static com.ibm.icu.util.VersionInfo getAge(int ch)
  {
    if ((ch < 0) || (ch > 1114111)) {
      throw new IllegalArgumentException("Codepoint out of bounds");
    }
    return PROPERTY_.getAge(ch);
  }
  

























  public static boolean hasBinaryProperty(int ch, int property)
  {
    if ((ch < 0) || (ch > 1114111)) {
      throw new IllegalArgumentException("Codepoint out of bounds");
    }
    return PROPERTY_.hasBinaryProperty(ch, property);
  }
  







  public static boolean isUAlphabetic(int ch)
  {
    return hasBinaryProperty(ch, 0);
  }
  







  public static boolean isULowercase(int ch)
  {
    return hasBinaryProperty(ch, 22);
  }
  







  public static boolean isUUppercase(int ch)
  {
    return hasBinaryProperty(ch, 30);
  }
  








  public static boolean isUWhiteSpace(int ch)
  {
    return hasBinaryProperty(ch, 31);
  }
  








































  public static int getIntPropertyValue(int ch, int type)
  {
    if (type < 0) {
      return 0;
    }
    if (type < 35) {
      return hasBinaryProperty(ch, type) ? 1 : 0;
    }
    if (type < 4096) {
      return 0;
    }
    if (type < 4108)
    {
      switch (type) {
      case 4096: 
        return getDirection(ch);
      case 4097: 
        return UnicodeBlock.of(ch).getID();
      case 4098: 
        return getCombiningClass(ch);
      case 4099: 
        return PROPERTY_.getAdditional(ch, 2) & 0x1F;
      
      case 4100: 
        return (PROPERTY_.getAdditional(ch, 0) & 0x38000) >> 15;
      
      case 4101: 
        return getType(ch);
      case 4102: 
        return (PROPERTY_.getAdditional(ch, 2) & 0x7E0) >> 5;
      
      case 4103: 
        return (PROPERTY_.getAdditional(ch, 2) & 0x3800) >> 11;
      

















      case 4104: 
        return (PROPERTY_.getAdditional(ch, 0) & 0x7C0000) >> 18;
      





















      case 4105: 
        return getNumericType(PROPERTY_.getProperty(ch));
      case 4106: 
        return UScript.getScript(ch);
      
      case 4107: 
        if (ch >= 4352)
        {
          if (ch <= 4607)
          {
            if (ch <= 4447)
            {
              if ((ch == 4447) || (ch <= 4441) || (getType(ch) == 5)) {
                return 1;
              }
            } else if (ch <= 4519)
            {
              if ((ch <= 4514) || (getType(ch) == 5)) {
                return 2;
              }
              
            }
            else if ((ch <= 4601) || (getType(ch) == 5)) {
              return 3;
            }
          }
          else if (ch -= 44032 >= 0)
          {
            if (ch < 11172)
            {
              return ch % 28 == 0 ? 4 : 5; } }
        }
        return 0;
      }
      
      
      return 0;
    }
    if (type == 8192) {
      return UCharacterProperty.getMask(getType(ch));
    }
    return 0;
  }
  



















  public static int getIntPropertyMinValue(int type)
  {
    return 0;
  }
  


























  public static int getIntPropertyMaxValue(int type)
  {
    if (type < 0) {
      return -1;
    }
    if (type < 35) {
      return 1;
    }
    if (type < 4096) {
      return -1;
    }
    if (type < 4108) {
      int max = 0;
      switch (type) {
      case 4096: 
        return 18;
      case 4097: 
        max = (PROPERTY_.getMaxValues(0) & 0x7F80) >> 7;
        
        return max != 0 ? max : 125;
      case 4098: 
        return 255;
      
      case 4099: 
        max = PROPERTY_.getMaxValues(2) & 0x1F;
        return max != 0 ? max : 17;
      case 4100: 
        max = (PROPERTY_.getMaxValues(0) & 0x38000) >> 15;
        return max != 0 ? max : 5;
      case 4101: 
        return 29;
      case 4102: 
        max = (PROPERTY_.getMaxValues(2) & 0x7E0) >> 5;
        return max != 0 ? max : 53;
      case 4103: 
        max = (PROPERTY_.getMaxValues(2) & 0x3800) >> 11;
        return max != 0 ? max : 5;
      case 4104: 
        max = (PROPERTY_.getMaxValues(0) & 0x7C0000) >> 18;
        return max != 0 ? max : 30;
      case 4105: 
        return 3;
      case 4106: 
        max = PROPERTY_.getMaxValues(0) & 0x7F;
        return max != 0 ? max : 53;
      case 4107: 
        return 5;
      }
      
    }
    return -1;
  }
  





  static com.ibm.icu.impl.UCharacterName NAME_ = null;
  



  static com.ibm.icu.impl.UPropertyAliases PNAMES_ = null;
  private static final UCharacterProperty PROPERTY_;
  private static final char[] PROPERTY_TRIE_INDEX_;
  private static final char[] PROPERTY_TRIE_DATA_;
  private static final int[] PROPERTY_DATA_;
  
  static {
    try { PNAMES_ = new com.ibm.icu.impl.UPropertyAliases();
      NAME_ = com.ibm.icu.impl.UCharacterName.getInstance();
    }
    catch (Exception e) {}
    























    try
    {
      PROPERTY_ = UCharacterProperty.getInstance();
      PROPERTY_TRIE_INDEX_ = PROPERTY_m_trieIndex_;
      PROPERTY_TRIE_DATA_ = PROPERTY_m_trieData_;
      PROPERTY_DATA_ = PROPERTY_m_property_;
      PROPERTY_INITIAL_VALUE_ = PROPERTY_DATA_[PROPERTY_m_trieInitialValue_];

    }
    catch (Exception e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }
  



  private static final int PROPERTY_INITIAL_VALUE_;
  


  private static final int LAST_CHAR_MASK_ = 65535;
  


  private static final int LAST_BYTE_MASK_ = 255;
  


  private static final int SHIFT_16_ = 16;
  


  private static final int SHIFT_24_ = 24;
  


  private static final int DECIMAL_RADIX_ = 10;
  


  private static final int NO_BREAK_SPACE_ = 160;
  


  private static final int NARROW_NO_BREAK_SPACE_ = 8239;
  


  private static final int ZERO_WIDTH_NO_BREAK_SPACE_ = 65279;
  


  private static final int IDEOGRAPHIC_NUMBER_ZERO_ = 12295;
  


  private static final int CJK_IDEOGRAPH_FIRST_ = 19968;
  


  private static final int CJK_IDEOGRAPH_SECOND_ = 20108;
  


  private static final int CJK_IDEOGRAPH_THIRD_ = 19977;
  


  private static final int CJK_IDEOGRAPH_FOURTH_ = 22232;
  


  private static final int CJK_IDEOGRAPH_FIFTH_ = 20116;
  


  private static final int CJK_IDEOGRAPH_SIXTH_ = 20845;
  


  private static final int CJK_IDEOGRAPH_SEVENTH_ = 19971;
  


  private static final int CJK_IDEOGRAPH_EIGHTH_ = 20843;
  


  private static final int CJK_IDEOGRAPH_NINETH_ = 20061;
  


  private static final int APPLICATION_PROGRAM_COMMAND_ = 159;
  


  private static final int UNIT_SEPARATOR_ = 31;
  


  private static final int DELETE_ = 127;
  


  private static final int ISO_CONTROL_FIRST_RANGE_MAX_ = 31;
  


  private static final int NUMERIC_TYPE_SHIFT_ = 12;
  


  private static final int NUMERIC_TYPE_MASK_ = 28672;
  


  private static final int BIDI_SHIFT_ = 6;
  


  private static final int BIDI_MASK_AFTER_SHIFT_ = 31;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_ZERO_ = 38646;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_ONE_ = 22777;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_TWO_ = 36019;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_THREE_ = 21443;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_FOUR_ = 32902;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_FIVE_ = 20237;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_SIX_ = 38520;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_SEVEN_ = 26578;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_EIGHT_ = 25420;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_NINE_ = 29590;
  


  private static final int CJK_IDEOGRAPH_TEN_ = 21313;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_TEN_ = 25342;
  


  private static final int CJK_IDEOGRAPH_HUNDRED_ = 30334;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_HUNDRED_ = 20336;
  


  private static final int CJK_IDEOGRAPH_THOUSAND_ = 21315;
  


  private static final int CJK_IDEOGRAPH_COMPLEX_THOUSAND_ = 20191;
  


  private static final int CJK_IDEOGRAPH_TEN_THOUSAND_ = 33356;
  


  private static final int CJK_IDEOGRAPH_HUNDRED_MILLION_ = 20740;
  


  private static final int NUMERATOR_POWER_LIMIT_ = 2147483392;
  


  private static final int JOINING_TYPE_MASK_ = 14336;
  


  private static final int JOINING_TYPE_SHIFT_ = 11;
  


  private static final int JOINING_GROUP_MASK_ = 2016;
  


  private static final int JOINING_GROUP_SHIFT_ = 5;
  


  private static final int DECOMPOSITION_TYPE_MASK_ = 31;
  


  private static final int EAST_ASIAN_MASK_ = 229376;
  


  private static final int EAST_ASIAN_SHIFT_ = 15;
  


  private static final int ZERO_WIDTH_NON_JOINER_ = 8204;
  


  private static final int ZERO_WIDTH_JOINER_ = 8205;
  


  private static final int LINE_BREAK_MASK_ = 8126464;
  


  private static final int LINE_BREAK_SHIFT_ = 18;
  


  private static final int BLOCK_MASK_ = 32640;
  


  private static final int BLOCK_SHIFT_ = 7;
  


  private static final int SCRIPT_MASK_ = 127;
  

  private static int getEuropeanDigit(int ch)
  {
    if (((ch > 122) && (ch < 65313)) || (ch < 65) || ((ch > 90) && (ch < 97)) || (ch > 65370) || ((ch > 65329) && (ch < 65345)))
    {

      return -1;
    }
    if (ch <= 122)
    {
      return ch + 10 - (ch <= 90 ? 65 : 97);
    }
    
    if (ch <= 65338) {
      return ch + 10 - 65313;
    }
    
    return ch + 10 - 65345;
  }
  





  private static int getNumericType(int props)
  {
    return (props & 0x7000) >> 12;
  }
  






  private static boolean isNotExceptionIndicator(int props)
  {
    return (props & 0x20) == 0;
  }
  












  private static int getProperty(int ch)
  {
    if ((ch < 55296) || ((ch > 56319) && (ch < 65536)))
    {
      try
      {

        return PROPERTY_DATA_[PROPERTY_TRIE_DATA_[((PROPERTY_TRIE_INDEX_[(ch >> 5)] << '\002') + (ch & 0x1F))]];

      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        return PROPERTY_INITIAL_VALUE_;
      }
    }
    if (ch <= 56319)
    {
      return PROPERTY_DATA_[PROPERTY_TRIE_DATA_[((PROPERTY_TRIE_INDEX_[(320 + (ch >> 5))] << '\002') + (ch & 0x1F))]];
    }
    



    if (ch <= 1114111)
    {

      return PROPERTY_DATA_[PROPERTY_m_trie_.getSurrogateValue(com.ibm.icu.text.UTF16.getLeadSurrogate(ch), (char)(ch & 0x3FF))];
    }
    





    return PROPERTY_INITIAL_VALUE_;
  }
  
  private UCharacter() {}
}
