package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;




















public class BreakIteratorRules
  extends ListResourceBundle
{
  public BreakIteratorRules() {}
  
  public Object[][] getContents()
  {
    return contents;
  }
  
  static final Object[][] contents = { { "BreakIteratorClasses", { "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator", "RuleBasedBreakIterator" } }, { "CharacterBreakRules", "$devaVirama=[्];$_ignore_=[[[:Mn:]-$devaVirama][:Me:]];$choseong=[ᄀ-ᅟ];$jungseong=[ᅠ-ᆧ];$jongseong=[ᆨ-ᇿ];$surr_hi=[?-?];$surr_lo=[?-?];.;\r\n;$surr_hi$surr_lo;$choseong*$jungseong*$jongseong*;$devaNukta=[़];$devaVowel=[अ-औ];$devaMatra=[ा-ौॢॣ];$devaConsonant=[क-हक़-य़];$devaModifier=[ँ-ः॑-॔];$zwnj=[‌];$zwj=[‍];$devaCN=($devaConsonant$devaNukta?);$devaJoin=($devaVirama[$zwj$zwnj]?);($devaCN$devaJoin)*$devaCN($devaJoin|$devaMatra?$devaModifier*);$devaVowel$devaModifier*;" }, { "WordBreakRules", "$surr_lo=[?-?];$surr_hi_let=[????];$surr_hi_ideo=[?-?];$surr_hi_tag=[?];$surr_hi_pua=[?-?];$pua=[-$surr_hi_pua];$_ignore_=[[:Mn:][:Me:][:Cf:]$surr_lo$surr_hi_tag];$danda=[।॥];$kanji=[々㐀-䶵一-龥豈-頻$surr_hi_ideo$pua];$kata=[゙-゜ァ-ヾ];$hira=[ぁ-ゞー];$let=[[[:L:][:Mc:]$surr_hi_let]-[$kanji$kata$hira]];$dgt=[:N:];$mid_word=[[:Pd:]­‧\\\"\\'\\.];$mid_num=[\\\"\\'\\,٫\\.];$pre_num=[[[:Sc:]-[¢]]\\#\\.];$post_num=[\\%\\&¢٪‰‱];$ls=[\n\f  ];$ws=[[:Zs:]\t];$word=($let+($mid_word$let+)*$danda?);$number=($dgt+($mid_num$dgt+)*);.;$word?($number$word)*($number$post_num?)?;$pre_num($number$word)*($number$post_num?)?;$ws*\r?$ls?;$kata*;$hira*;$kanji*;" }, { "LineBreakRules", "$surr_lo=[?-?];$surr_hi_let=[????];$surr_hi_ideo=[?-?];$surr_hi_tag=[?];$surr_hi_pua=[?-?];$pua=[-$surr_hi_pua];$_ignore_=[[:Mn:][:Me:][:Cf:]$surr_lo$surr_hi_tag];$danda=[।॥];$break=[\003\t\n\f  ];$nbsp=[  ‑﻿];$space=[[[:Zs:][:Cc:]]-[$nbsp$break\r]];$dash=[[[:Pd:]­]-[$nbsp]];$pre_word=[[[:Sc:]-[¢]][:Ps:][:Pi:]\\\"\\'];$post_word=[[:Pe:][:Pf:]\\!\\\"\\'\\%\\.\\,\\:\\;\\?¢°٪‰-‴℃℅℉、。々ぁぃぅぇぉっゃゅょゎ゙-ゞァィゥェォッャュョヮヵヶー-ヾ！，．？];$kanji=[[$surr_hi_ideo$pua㐀-䶵一-龥豈-頻ぁ-ゔァ-ヺ]-[$post_word$_ignore_]];$digit=[[:Nd:][:No:]];$mid_num=[\\.\\,];$char=[^$break$space$dash$kanji$nbsp$_ignore_$pre_word$post_word$mid_num$danda\r\\\"\\'];$number=([$pre_word$dash]*$digit+($mid_num$digit+)*);$word_core=([$pre_word$char]*|$kanji|$number);$word_suffix=(($dash+|$post_word*)$space*);$word=($pre_word*$word_core$word_suffix);$word($nbsp+$word)*\r?$break?;" }, { "SentenceBreakRules", "$surr_lo=[?-?];$_ignore_=[[:Mn:][:Me:][:Cf:]$surr_lo];$lc=[:Ll:];$ucLatin=[A-Z];$space=[\t\r\f\n [:Zs:]];$start=[[:Ps:][:Pi:]\\\"\\'];$end=[[:Pe:][:Pf:]\\\"\\'];$digit=[:N:];$term=[\\!\\?。！？];$period=[\\.．];$sent_start=[^$lc$ucLatin$space$start$end$digit$term$period $_ignore_];$danda=[।॥];.*? ?;.*?$danda$space*;.*?$period[$period$end]*$space* ;.*?$period[$period$end]*$space*/($start*$sent_start|$start+$ucLatin);.*?$period[$period$end]*$space+/$ucLatin;.*?$term[$term$period$end]*$space* ?;![$sent_start$ucLatin]$start*$space+$end*$period;![$sent_start$lc$digit]$start*$space*$end*$term;" }, { "TitleBreakRules", "$case_ignorable=[[:Mn:][:Me:][:Cf:][:Lm:][:Sk:]\\u0027­’];$cased=[[[:Lu:][:Lt:][:Ll:]Ⅰ-ⅯⒶ-Ⓩʰ-ʸˀ-ˁˠ-ˤͅͺⅰ-ⅿⓐ-ⓩ]-$case_ignorable];$not_cased=[^$cased$case_ignorable];[$not_cased$case_ignorable]*;$cased[$cased$case_ignorable]*[$not_cased]*;!$not_cased*[$cased$case_ignorable]*$not_cased*;" } };
}
