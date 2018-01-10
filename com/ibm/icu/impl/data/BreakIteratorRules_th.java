package com.ibm.icu.impl.data;

import java.net.URL;
import java.util.ListResourceBundle;









public class BreakIteratorRules_th
  extends ListResourceBundle
{
  public BreakIteratorRules_th() {}
  
  public Object[][] getContents()
  {
    URL url = getClass().getResource("thai_dict");
    


    if (url == null) {
      return new Object[0][0];
    }
    
    return new Object[][] { { "BreakIteratorClasses", { "RuleBasedBreakIterator", "DictionaryBasedBreakIterator", "DictionaryBasedBreakIterator", "RuleBasedBreakIterator" } }, { "WordBreakRules", "$_dictionary_=[ก-ฮะ-ฺเ-ไ็-๎];$surr_lo=[?-?];$surr_hi_let=[????];$surr_hi_ideo=[?-?];$surr_hi_tag=[?];$surr_hi_pua=[?-?];$pua=[-$surr_hi_pua];$_ignore_=[[[:Mn:][:Me:][:Cf:]$surr_lo$surr_hi_tag]-$_dictionary_];$paiyannoi=[ฯ];$maiyamok=[ๆ];$danda=[।॥];$kanji=[々一-龥豈-鶴$surr_hi_ideo$pua];$kata=[ァ-ヺ];$hira=[ぁ-ゔ];$cjk_diacrit=[゙-゜];$let=[[[:L:][:Mc:]$surr_hi_let]-[$kanji$kata$hira$cjk_diacrit$_dictionary_]];$dgt=[:N:];$mid_word=[[:Pd:]­‧\\\"\\'\\.];$mid_num=[\\\"\\'\\,٫\\.];$pre_num=[[[:Sc:]-[¢]]\\#\\.];$post_num=[\\%\\&¢٪‰‱];$ls=[\n\f  ];$ws=[[:Zs:]\t];$word=(($let+($mid_word$let+)*)$danda?);$number=($dgt+($mid_num$dgt+)*);$thai_etc=($paiyannoiล$paiyannoi);.;$word?($number$word)*($number$post_num?)?;$pre_num($number$word)*($number$post_num?)?;$_dictionary_+($paiyannoi?$maiyamok)?;$_dictionary_+$paiyannoi/([^ล$maiyamok$_ignore_]|ล[^$paiyannoi$_ignore_]);$thai_etc;$ws*\r?$ls?;[$kata$cjk_diacrit]*;[$hira$cjk_diacrit]*;$kanji*;" }, { "LineBreakRules", "$_dictionary_=[ก-ฮะ-ฺเ-ไ็-๎];$surr_lo=[?-?];$surr_hi_let=[????];$surr_hi_ideo=[?-?];$surr_hi_tag=[?];$surr_hi_pua=[?-?];$pua=[-$surr_hi_pua];$_ignore_=[[[:Mn:][:Me:][:Cf:]$surr_lo$surr_hi_tag]-[$_dictionary_]];$danda=[।॥];$break=[\003\t\n\f  ];$nbsp=[  ‑﻿];$space=[[[:Zs:][:Cc:]]-[$nbsp$break\r]];$dash=[[[:Pd:]­]-$nbsp];$paiyannoi=[ฯ];$maiyamok=[ๆ];$thai_etc=($paiyannoiล$paiyannoi);$pre_word=[[[:Sc:]-[¢]][:Ps:][:Pi:]\\\"];$post_word=[[:Pe:][:Pf:]\\!\\%\\.\\,\\:\\;\\?\\\"¢°٪‰-‴℃℅℉、。々ぁぃぅぇぉっゃゅょゎ゙-ゞァィゥェォッャュョヮヵヶー-ヾ！．？$maiyamok];$kanji=[[$surr_hi_ideo$pua一-龥豈-鶴ぁ-ゔァ-ヺ]-[$post_word$_ignore_]];$digit=[[:Nd:][:No:]];$mid_num=[\\.\\,];$char=[^$break$space$dash$kanji$nbsp$_ignore_$pre_word$post_word$mid_num\r$danda$_dictionary_$paiyannoi$maiyamok];$number=([$pre_word$dash]*$digit+($mid_num$digit+)*);$word_core=($char*|$kanji|$number|$_dictionary_+|$thai_etc);$word_suffix=(($dash+|$post_word*)$space*);$word=($pre_word*$word_core$word_suffix);$word($nbsp+$word)*(\r?$break?|$paiyannoi\r$break|$paiyannoi$break)?;$word($nbsp+$word)*$paiyannoi/([^[ล$_ignore_]]|ล[^$paiyannoi$_ignore_]);" }, { "WordBreakDictionary", url }, { "LineBreakDictionary", url } };
  }
}
