package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleData;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeZone;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;








































































































































































































public class SimpleDateFormat
  extends DateFormat
{
  static final long serialVersionUID = 4774881970558875024L;
  static final int currentSerialVersion = 1;
  private int serialVersionOnStream = 1;
  



  private String pattern;
  


  private DateFormatSymbols formatData;
  


  private Date defaultCenturyStart;
  


  private transient int defaultCenturyStartYear;
  


  private static final int millisPerHour = 3600000;
  


  private static final int millisPerMinute = 60000;
  


  private static final String GMT_PLUS = "GMT+";
  


  private static final String GMT_MINUS = "GMT-";
  


  private static final String GMT = "GMT";
  


  private static final String SUPPRESS_NEGATIVE_PREFIX = "꬀";
  


  private static Hashtable cachedLocaleData = new Hashtable(3);
  







  public SimpleDateFormat()
  {
    this(3, 3, Locale.getDefault());
  }
  






  public SimpleDateFormat(String pattern)
  {
    this(pattern, Locale.getDefault());
  }
  






  public SimpleDateFormat(String pattern, Locale loc)
  {
    this.pattern = pattern;
    formatData = new DateFormatSymbols(loc);
    initialize(loc);
  }
  





  public SimpleDateFormat(String pattern, DateFormatSymbols formatData)
  {
    this.pattern = pattern;
    this.formatData = ((DateFormatSymbols)formatData.clone());
    initialize(Locale.getDefault());
  }
  

  SimpleDateFormat(int timeStyle, int dateStyle, Locale loc)
  {
    String[] dateTimePatterns = (String[])cachedLocaleData.get(loc);
    if (dateTimePatterns == null) {
      ResourceBundle r = ICULocaleData.getLocaleElements(loc);
      dateTimePatterns = r.getStringArray("DateTimePatterns");
      
      cachedLocaleData.put(loc, dateTimePatterns);
    }
    formatData = new DateFormatSymbols(loc);
    if ((timeStyle >= 0) && (dateStyle >= 0)) {
      Object[] dateTimeArgs = { dateTimePatterns[timeStyle], dateTimePatterns[(dateStyle + 4)] };
      
      pattern = MessageFormat.format(dateTimePatterns[8], dateTimeArgs);
    }
    else if (timeStyle >= 0) {
      pattern = dateTimePatterns[timeStyle];
    }
    else if (dateStyle >= 0) {
      pattern = dateTimePatterns[(dateStyle + 4)];
    }
    else {
      throw new IllegalArgumentException("No date or time style specified");
    }
    
    initialize(loc);
  }
  




  private void initialize(Locale loc)
  {
    calendar = Calendar.getInstance(TimeZone.getDefault(), loc);
    numberFormat = NumberFormat.getInstance(loc);
    numberFormat.setGroupingUsed(false);
    if ((numberFormat instanceof DecimalFormat))
      ((DecimalFormat)numberFormat).setDecimalSeparatorAlwaysShown(false);
    numberFormat.setParseIntegerOnly(true);
    numberFormat.setMinimumFractionDigits(0);
    
    initializeDefaultCentury();
  }
  


  private void initializeDefaultCentury()
  {
    calendar.setTime(new Date());
    calendar.add(1, -80);
    parseAmbiguousDatesAsAfter(calendar.getTime());
  }
  


  private void parseAmbiguousDatesAsAfter(Date startDate)
  {
    defaultCenturyStart = startDate;
    calendar.setTime(startDate);
    defaultCenturyStartYear = calendar.get(1);
  }
  






  public void set2DigitYearStart(Date startDate)
  {
    parseAmbiguousDatesAsAfter(startDate);
  }
  






  public Date get2DigitYearStart()
  {
    return defaultCenturyStart;
  }
  
















  public StringBuffer format(Calendar cal, StringBuffer toAppendTo, FieldPosition pos)
  {
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    
    boolean inQuote = false;
    char prevCh = '\000';
    int count = 0;
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      

      if ((ch != prevCh) && (count > 0)) {
        toAppendTo.append(subFormat(prevCh, count, toAppendTo.length(), pos, formatData, cal));
        
        count = 0;
      }
      if (ch == '\'')
      {

        if ((i + 1 < pattern.length()) && (pattern.charAt(i + 1) == '\'')) {
          toAppendTo.append('\'');
          i++;
        } else {
          inQuote = !inQuote;
        }
      } else if ((!inQuote) && (((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))))
      {


        prevCh = ch;
        count++;
      }
      else
      {
        toAppendTo.append(ch);
      }
    }
    
    if (count > 0) {
      toAppendTo.append(subFormat(prevCh, count, toAppendTo.length(), pos, formatData, cal));
    }
    
    return toAppendTo;
  }
  

  private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = { 0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 19 };
  










  private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
  











  private static final String NUMERIC_FORMAT_CHARS = "MyudhHmsSDFwWkK";
  











  protected String subFormat(char ch, int count, int beginOffset, FieldPosition pos, DateFormatSymbols formatData, Calendar cal)
    throws IllegalArgumentException
  {
    int patternCharIndex = -1;
    int maxIntCount = Integer.MAX_VALUE;
    String current = "";
    
    if ((patternCharIndex = "GyMdkHmsSEDFwWahKzu".indexOf(ch)) == -1) {
      throw new IllegalArgumentException("Illegal pattern character '" + ch + "'");
    }
    

    int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
    int value = cal.get(field);
    
    switch (patternCharIndex) {
    case 0: 
      current = eras[value];
      break;
    





    case 1: 
      if (count == 2) {
        current = zeroPaddingNumber(value, 2, 2);
      } else
        current = zeroPaddingNumber(value, count, maxIntCount);
      break;
    case 2: 
      if (count >= 4) {
        current = months[value];
      } else if (count == 3) {
        current = shortMonths[value];
      } else
        current = zeroPaddingNumber(value + 1, count, maxIntCount);
      break;
    case 4: 
      if (value == 0) {
        current = zeroPaddingNumber(cal.getMaximum(11) + 1, count, maxIntCount);
      }
      else
      {
        current = zeroPaddingNumber(value, count, maxIntCount); }
      break;
    case 9: 
      if (count >= 4) {
        current = weekdays[value];
      } else
        current = shortWeekdays[value];
      break;
    case 14: 
      current = ampms[value];
      break;
    case 15: 
      if (value == 0) {
        current = zeroPaddingNumber(cal.getLeastMaximum(10) + 1, count, maxIntCount);
      }
      else
      {
        current = zeroPaddingNumber(value, count, maxIntCount); }
      break;
    case 17: 
      int zoneIndex = formatData.getZoneIndex(cal.getTimeZone().getID());
      
      if (zoneIndex == -1)
      {



        StringBuffer zoneString = new StringBuffer();
        
        value = cal.get(15) + cal.get(16);
        

        if (value < 0)
        {
          zoneString.append("GMT-");
          value = -value;
        }
        else {
          zoneString.append("GMT+"); }
        zoneString.append(zeroPaddingNumber(value / 3600000, 2, 2));
        
        zoneString.append(':');
        zoneString.append(zeroPaddingNumber(value % 3600000 / 60000, 2, 2));
        

        current = zoneString.toString();
      }
      else if (cal.get(16) != 0)
      {
        if (count >= 4) {
          current = zoneStrings[zoneIndex][3];
        }
        else {
          current = zoneStrings[zoneIndex][4];
        }
        
      }
      else if (count >= 4) {
        current = zoneStrings[zoneIndex][1];
      } else {
        current = zoneStrings[zoneIndex][2];
      }
      break;
    
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 16: 
    default: 
      current = zeroPaddingNumber(value, count, maxIntCount);
    }
    
    
    if (pos.getField() == PATTERN_INDEX_TO_DATE_FORMAT_FIELD[patternCharIndex])
    {
      if ((pos.getBeginIndex() == 0) && (pos.getEndIndex() == 0)) {
        pos.setBeginIndex(beginOffset);
        pos.setEndIndex(beginOffset + current.length());
      }
    }
    
    return current;
  }
  




  protected String zeroPaddingNumber(long value, int minDigits, int maxDigits)
  {
    numberFormat.setMinimumIntegerDigits(minDigits);
    numberFormat.setMaximumIntegerDigits(maxDigits);
    return numberFormat.format(value);
  }
  









  private static final boolean isNumeric(char formatChar, int count)
  {
    int i = "MyudhHmsSDFwWkK".indexOf(formatChar);
    return (i > 0) || ((i == 0) && (count < 3));
  }
  





  public void parse(String text, Calendar cal, ParsePosition parsePos)
  {
    int pos = parsePos.getIndex();
    int start = pos;
    boolean[] ambiguousYear = { false };
    int count = 0;
    





    int abutPat = -1;
    int abutStart = 0;
    int abutPass = 0;
    boolean inQuote = false;
    
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      

      if ((!inQuote) && (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')))) {
        int fieldPat = i;
        

        count = 1;
        
        while ((i + 1 < pattern.length()) && (pattern.charAt(i + 1) == ch)) {
          count++;
          i++;
        }
        
        if (isNumeric(ch, count)) {
          if (abutPat < 0)
          {




            if (i + 1 < pattern.length())
            {
              char nextCh = pattern.charAt(i + 1);
              int k = "MyudhHmsSDFwWkK".indexOf(nextCh);
              boolean abutting; if (k == 0) {
                int j = i + 2;
                
                while ((j < pattern.length()) && (pattern.charAt(j) == nextCh)) {
                  j++;
                }
                abutting = j - i < 4;
              } else {
                abutting = k > 0;
              }
              


              if (abutting) {
                abutPat = fieldPat;
                abutStart = pos;
                abutPass = 0;
              }
            }
          }
        } else {
          abutPat = -1;
        }
        







        if (abutPat >= 0)
        {



          if (fieldPat == abutPat) {
            count -= abutPass++;
            if (count == 0) {
              parsePos.setIndex(start);
              parsePos.setErrorIndex(pos);
              return;
            }
          }
          
          pos = subParse(text, pos, ch, count, true, false, ambiguousYear, cal);
          



          if (pos < 0) {
            i = abutPat - 1;
            pos = abutStart;
            continue;
          }
          

        }
        else
        {
          int s = pos;
          pos = subParse(text, pos, ch, count, false, true, ambiguousYear, cal);
          

          if (pos < 0) {
            parsePos.setErrorIndex(s);
            parsePos.setIndex(start);
            return;
          }
          
        }
        

      }
      else
      {

        abutPat = -1;
        



        if (ch == '\'')
        {
          if ((i + 1 < pattern.length()) && (pattern.charAt(i + 1) == ch)) {
            i++;
          }
          else
          {
            inQuote = !inQuote;
            continue;
          }
        }
        


        if (UCharacterProperty.isRuleWhiteSpace(ch))
        {
          while ((i + 1 < pattern.length()) && (UCharacterProperty.isRuleWhiteSpace(pattern.charAt(i + 1))))
          {
            i++;
          }
          

          int s = pos;
          while ((pos < text.length()) && (UCharacter.isUWhiteSpace(text.charAt(pos))))
          {
            pos++;
          }
          

          if (pos > s) {
            continue;
          }
        } else if ((pos < text.length()) && (text.charAt(pos) == ch))
        {
          pos++;
          continue;
        }
        

        parsePos.setIndex(start);
        parsePos.setErrorIndex(pos);
        return;
      }
    }
    




    parsePos.setIndex(pos);
    





















    try
    {
      if (ambiguousYear[0] != 0)
      {




        Calendar copy = (Calendar)cal.clone();
        Date parsedDate = copy.getTime();
        if (parsedDate.before(defaultCenturyStart))
        {

          cal.set(1, defaultCenturyStartYear + 100);
        }
        
      }
    }
    catch (IllegalArgumentException e)
    {
      parsePos.setErrorIndex(pos);
      parsePos.setIndex(start);
    }
  }
  

















  protected int matchString(String text, int start, int field, String[] data, Calendar cal)
  {
    int i = 0;
    int count = data.length;
    
    if (field == 7) { i = 1;
    }
    



    int bestMatchLength = 0;int bestMatch = -1;
    for (; i < count; i++)
    {
      int length = data[i].length();
      

      if ((length > bestMatchLength) && (text.regionMatches(true, start, data[i], 0, length)))
      {

        bestMatch = i;
        bestMatchLength = length;
      }
    }
    if (bestMatch >= 0)
    {
      cal.set(field, bestMatch);
      return start + bestMatchLength;
    }
    return -start;
  }
  
  private int matchZoneString(String text, int start, int zoneIndex)
  {
    for (int j = 1; j <= 4; j++)
    {

      if (text.regionMatches(true, start, formatData.zoneStrings[zoneIndex][j], 0, formatData.zoneStrings[zoneIndex][j].length())) {
        break;
      }
    }
    

    return j > 4 ? -1 : j;
  }
  





  private int subParseZoneString(String text, int start, Calendar cal)
  {
    int zoneIndex = formatData.getZoneIndex(getTimeZone().getID());
    
    TimeZone tz = null;
    int j = 0;int i = 0;
    if ((zoneIndex != -1) && ((j = matchZoneString(text, start, zoneIndex)) > 0)) {
      tz = TimeZone.getTimeZone(formatData.zoneStrings[zoneIndex][0]);
      i = zoneIndex;
    }
    if (tz == null) {
      zoneIndex = formatData.getZoneIndex(TimeZone.getDefault().getID());
      
      if ((zoneIndex != -1) && ((j = matchZoneString(text, start, zoneIndex)) > 0)) {
        tz = TimeZone.getTimeZone(formatData.zoneStrings[zoneIndex][0]);
        i = zoneIndex;
      }
    }
    
    if (tz == null) {
      for (i = 0; i < formatData.zoneStrings.length; i++) {
        if ((j = matchZoneString(text, start, i)) > 0) {
          tz = TimeZone.getTimeZone(formatData.zoneStrings[i][0]);
          break;
        }
      }
    }
    if (tz != null) {
      cal.set(15, tz.getRawOffset());
      

      cal.set(16, j >= 3 ? ((SimpleTimeZone)tz).getDSTSavings() : 0);
      
      return start + formatData.zoneStrings[i][j].length();
    }
    return 0;
  }
  





















  protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal)
  {
    Number number = null;
    int value = 0;
    
    ParsePosition pos = new ParsePosition(0);
    int patternCharIndex = -1;
    
    if ((patternCharIndex = "GyMdkHmsSEDFwWahKzu".indexOf(ch)) == -1) {
      return -start;
    }
    
    int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
    

    for (;;)
    {
      if (start >= text.length()) {
        return -start;
      }
      int c = UTF16.charAt(text, start);
      if (!UCharacter.isUWhiteSpace(c)) {
        break;
      }
      start += UTF16.getCharCount(c);
    }
    pos.setIndex(start);
    




    if ((patternCharIndex == 4) || (patternCharIndex == 15) || ((patternCharIndex == 2) && (count <= 2)) || (patternCharIndex == 1))
    {





      if (obeyCount)
      {
        if (start + count > text.length()) return -start;
        number = parseInt(text.substring(0, start + count), pos, allowNegative);
      } else {
        number = parseInt(text, pos, allowNegative); }
      if (number == null)
        return -start;
      value = number.intValue();
    }
    
    switch (patternCharIndex)
    {
    case 0: 
      return matchString(text, start, 0, formatData.eras, cal);
    






    case 1: 
      if ((count == 2) && (pos.getIndex() - start == 2) && (Character.isDigit(text.charAt(start))) && (Character.isDigit(text.charAt(start + 1))))
      {










        int ambiguousTwoDigitYear = defaultCenturyStartYear % 100;
        ambiguousYear[0] = (value == ambiguousTwoDigitYear ? 1 : false);
        value += defaultCenturyStartYear / 100 * 100 + (value < ambiguousTwoDigitYear ? 100 : 0);
      }
      
      cal.set(1, value);
      return pos.getIndex();
    case 2: 
      if (count <= 2)
      {



        cal.set(2, value - 1);
        return pos.getIndex();
      }
      




      int newStart = 0;
      if ((newStart = matchString(text, start, 2, formatData.months, cal)) > 0)
      {
        return newStart;
      }
      return matchString(text, start, 2, formatData.shortMonths, cal);
    


    case 4: 
      if (value == cal.getMaximum(11) + 1) value = 0;
      cal.set(11, value);
      return pos.getIndex();
    

    case 9: 
      int newStart = 0;
      if ((newStart = matchString(text, start, 7, formatData.weekdays, cal)) > 0)
      {
        return newStart;
      }
      return matchString(text, start, 7, formatData.shortWeekdays, cal);
    

    case 14: 
      return matchString(text, start, 9, formatData.ampms, cal);
    
    case 15: 
      if (value == cal.getLeastMaximum(10) + 1) value = 0;
      cal.set(10, value);
      return pos.getIndex();
    




    case 17: 
      int sign = 0;
      


      int offset;
      


      if ((text.length() - start >= "GMT".length()) && (text.regionMatches(true, start, "GMT", 0, "GMT".length())))
      {

        cal.set(16, 0);
        
        pos.setIndex(start + "GMT".length());
        try
        {
          if (text.charAt(pos.getIndex()) == '+') {
            sign = 1;
          } else if (text.charAt(pos.getIndex()) == '-') {
            sign = -1;
          }
        }
        catch (StringIndexOutOfBoundsException e) {}
        if (sign == 0) {
          cal.set(15, 0);
          return pos.getIndex();
        }
        

        pos.setIndex(pos.getIndex() + 1);
        Number tzNumber = numberFormat.parse(text, pos);
        if (tzNumber == null) {
          return -start;
        }
        if (text.charAt(pos.getIndex()) == ':')
        {
          offset = tzNumber.intValue() * 60;
          pos.setIndex(pos.getIndex() + 1);
          tzNumber = numberFormat.parse(text, pos);
          if (tzNumber == null) {
            return -start;
          }
          offset += tzNumber.intValue();
        }
        else
        {
          offset = tzNumber.intValue();
          if (offset < 24) {
            offset *= 60;
          } else {
            offset = offset % 100 + offset / 100 * 60;
          }
          
        }
        

      }
      else
      {
        int i = subParseZoneString(text, start, cal);
        if (i != 0) {
          return i;
        }
        



        DecimalFormat fmt = new DecimalFormat("+####;-####");
        fmt.setParseIntegerOnly(true);
        Number tzNumber = fmt.parse(text, pos);
        if (tzNumber == null) {
          return -start;
        }
        offset = tzNumber.intValue();
        sign = 1;
        if (offset < 0) {
          sign = -1;
          offset = -offset;
        }
        if (offset < 24) {
          offset *= 60;
        } else {
          offset = offset % 100 + offset / 100 * 60;
        }
      }
      



      if (sign != 0)
      {
        offset *= 60000 * sign;
        
        if (cal.getTimeZone().useDaylightTime())
        {
          cal.set(16, 3600000);
          offset -= 3600000;
        }
        cal.set(15, offset);
        
        return pos.getIndex();
      }
      


      return -start;
    }
    
    












    if (obeyCount)
    {
      if (start + count > text.length()) return -start;
      number = parseInt(text.substring(0, start + count), pos, allowNegative);
    } else {
      number = parseInt(text, pos, allowNegative); }
    if (number != null) {
      cal.set(field, number.intValue());
      return pos.getIndex();
    }
    return -start;
  }
  






  private Number parseInt(String text, ParsePosition pos, boolean allowNegative)
  {
    String oldPrefix = null;
    DecimalFormat df = null;
    if (!allowNegative) {
      try {
        df = (DecimalFormat)numberFormat;
        oldPrefix = df.getNegativePrefix();
        df.setNegativePrefix("꬀");
      } catch (ClassCastException e1) {}
    }
    Number number = numberFormat.parse(text, pos);
    if (df != null) {
      df.setNegativePrefix(oldPrefix);
    }
    return number;
  }
  



  private String translatePattern(String pattern, String from, String to)
  {
    StringBuffer result = new StringBuffer();
    boolean inQuote = false;
    for (int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      if (inQuote) {
        if (c == '\'') {
          inQuote = false;
        }
      }
      else if (c == '\'') {
        inQuote = true;
      } else if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
        int ci = from.indexOf(c);
        if (ci == -1) {
          throw new IllegalArgumentException("Illegal pattern  character '" + c + "'");
        }
        
        c = to.charAt(ci);
      }
      
      result.append(c);
    }
    if (inQuote)
      throw new IllegalArgumentException("Unfinished quote in pattern");
    return result.toString();
  }
  



  public String toPattern()
  {
    return pattern;
  }
  



  public String toLocalizedPattern()
  {
    return translatePattern(pattern, "GyMdkHmsSEDFwWahKzu", formatData.localPatternChars);
  }
  






  public void applyPattern(String pattern)
  {
    this.pattern = pattern;
  }
  



  public void applyLocalizedPattern(String pattern)
  {
    this.pattern = translatePattern(pattern, formatData.localPatternChars, "GyMdkHmsSEDFwWahKzu");
  }
  








  public DateFormatSymbols getDateFormatSymbols()
  {
    return (DateFormatSymbols)formatData.clone();
  }
  





  public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols)
  {
    formatData = ((DateFormatSymbols)newFormatSymbols.clone());
  }
  



  protected DateFormatSymbols getSymbols()
  {
    return formatData;
  }
  



  public Object clone()
  {
    SimpleDateFormat other = (SimpleDateFormat)super.clone();
    formatData = ((DateFormatSymbols)formatData.clone());
    return other;
  }
  





  public int hashCode()
  {
    return pattern.hashCode();
  }
  





  public boolean equals(Object obj)
  {
    if (!super.equals(obj)) return false;
    SimpleDateFormat that = (SimpleDateFormat)obj;
    return (pattern.equals(pattern)) && (formatData.equals(formatData));
  }
  



  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    if (serialVersionOnStream < 1)
    {
      initializeDefaultCentury();
    }
    else
    {
      parseAmbiguousDatesAsAfter(defaultCenturyStart);
    }
    serialVersionOnStream = 1;
  }
}
