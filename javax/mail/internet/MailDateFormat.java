package javax.mail.internet;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;























































































public class MailDateFormat
  extends SimpleDateFormat
{
  static boolean debug;
  
  public MailDateFormat()
  {
    super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
  }
  



















  public StringBuffer format(Date paramDate, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    int i = paramStringBuffer.length();
    super.format(paramDate, paramStringBuffer, paramFieldPosition);
    int j = 0;
    

    for (j = i + 25; paramStringBuffer.charAt(j) != 'X'; j++) {}
    


    calendar.clear();
    calendar.setTime(paramDate);
    int k = calendar.get(15) + 
      calendar.get(16);
    
    if (k < 0) {
      paramStringBuffer.setCharAt(j++, '-');
      k = -k;
    } else {
      paramStringBuffer.setCharAt(j++, '+');
    }
    int m = k / 60 / 1000;
    int n = m / 60;
    int i1 = m % 60;
    
    paramStringBuffer.setCharAt(j++, Character.forDigit(n / 10, 10));
    paramStringBuffer.setCharAt(j++, Character.forDigit(n % 10, 10));
    paramStringBuffer.setCharAt(j++, Character.forDigit(i1 / 10, 10));
    paramStringBuffer.setCharAt(j++, Character.forDigit(i1 % 10, 10));
    

    return paramStringBuffer;
  }
  










  public Date parse(String paramString, ParsePosition paramParsePosition)
  {
    return parseDate(paramString.toCharArray(), paramParsePosition);
  }
  












































  private static Date parseDate(char[] paramArrayOfChar, ParsePosition paramParsePosition)
  {
    try
    {
      int i = -1;
      int j = -1;
      int k = -1;
      int m = 0;
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      

      MailDateParser localMailDateParser = new MailDateParser(paramArrayOfChar);
      

      localMailDateParser.skipUntilNumber();
      i = localMailDateParser.parseNumber();
      
      if (!localMailDateParser.skipIfChar('-')) {
        localMailDateParser.skipWhiteSpace();
      }
      

      j = localMailDateParser.parseMonth();
      if (!localMailDateParser.skipIfChar('-')) {
        localMailDateParser.skipWhiteSpace();
      }
      

      k = localMailDateParser.parseNumber();
      if (k < 50) {
        k += 2000;
      } else if (k < 100) {
        k += 1900;
      }
      



      localMailDateParser.skipWhiteSpace();
      m = localMailDateParser.parseNumber();
      

      localMailDateParser.skipChar(':');
      n = localMailDateParser.parseNumber();
      

      if (localMailDateParser.skipIfChar(':')) {
        i1 = localMailDateParser.parseNumber();
      }
      

      try
      {
        localMailDateParser.skipWhiteSpace();
        i2 = localMailDateParser.parseTimeZone();
      } catch (ParseException localParseException) {
        if (debug) {
          System.out.println("No timezone? : '" + paramArrayOfChar + "'");
        }
      }
      
      paramParsePosition.setIndex(localMailDateParser.getIndex());
      return ourUTC(k, j, i, m, n, i1, i2);



    }
    catch (Exception localException)
    {


      if (debug) {
        System.out.println("Bad date: '" + paramArrayOfChar + "'");
        localException.printStackTrace();
      }
      paramParsePosition.setIndex(1); }
    return null;
  }
  

  private static TimeZone tz = TimeZone.getTimeZone("GMT");
  private static Calendar cal = new GregorianCalendar(tz);
  

  private static synchronized Date ourUTC(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    cal.clear();
    cal.set(1, paramInt1);
    cal.set(2, paramInt2);
    cal.set(5, paramInt3);
    cal.set(11, paramInt4);
    cal.set(12, paramInt5 + paramInt7);
    cal.set(13, paramInt6);
    
    return cal.getTime();
  }
  



  public void setCalendar(Calendar paramCalendar)
  {
    throw new RuntimeException("Method setCalendar() shouldn't be called");
  }
  
  public void setNumberFormat(NumberFormat paramNumberFormat)
  {
    throw new RuntimeException("Method setCalendar() shouldn't be called");
  }
}
