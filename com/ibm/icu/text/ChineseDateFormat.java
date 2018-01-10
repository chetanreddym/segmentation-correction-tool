package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Locale;









































public class ChineseDateFormat
  extends SimpleDateFormat
{
  public ChineseDateFormat(String pattern, Locale locale)
  {
    super(pattern, new ChineseDateFormatSymbols(locale));
  }
  




  protected String subFormat(char ch, int count, int beginOffset, FieldPosition pos, DateFormatSymbols formatData, Calendar cal)
  {
    switch (ch) {
    case 'G': 
      return zeroPaddingNumber(cal.get(0), 1, 9);
    
    case 'l': 
      ChineseDateFormatSymbols symbols = (ChineseDateFormatSymbols)formatData;
      
      return symbols.getLeapMonth(cal.get(ChineseCalendar.IS_LEAP_MONTH));
    }
    
    
    return super.subFormat(ch, count, beginOffset, pos, formatData, cal);
  }
  




  protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal)
  {
    if ((ch != 'G') && (ch != 'l') && (ch != 'y')) {
      return super.subParse(text, start, ch, count, obeyCount, allowNegative, ambiguousYear, cal);
    }
    

    start = Utility.skipWhitespace(text, start);
    
    ParsePosition pos = new ParsePosition(start);
    
    switch (ch)
    {
    case 'G': 
    case 'y': 
      Number number = null;
      if (obeyCount) {
        if (start + count > text.length()) {
          return -start;
        }
        number = numberFormat.parse(text.substring(0, start + count), pos);
      } else {
        number = numberFormat.parse(text, pos);
      }
      if (number == null) {
        return -start;
      }
      int value = number.intValue();
      cal.set(ch == 'G' ? 0 : 1, value);
      return pos.getIndex();
    

    case 'l': 
      ChineseDateFormatSymbols symbols = (ChineseDateFormatSymbols)getSymbols();
      
      int result = matchString(text, start, ChineseCalendar.IS_LEAP_MONTH, isLeapMonth, cal);
      


      if (result < 0) {
        cal.set(ChineseCalendar.IS_LEAP_MONTH, 0);
        result = start;
      }
      return result;
    }
    
    return 0;
  }
}
