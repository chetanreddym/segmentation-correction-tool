package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.mail.internet.MailDateFormat;


















public class INTERNALDATE
  implements Item
{
  public static char[] name = { 'I', 'N', 'T', 'E', 'R', 'N', 'A', 'L', 'D', 'A', 'T', 'E' };
  
  public int msgno;
  
  protected Date date;
  private static MailDateFormat mailDateFormat = new MailDateFormat();
  

  public INTERNALDATE(FetchResponse paramFetchResponse)
    throws ParsingException
  {
    msgno = paramFetchResponse.getNumber();
    paramFetchResponse.skipSpaces();
    String str = paramFetchResponse.readString();
    try {
      date = mailDateFormat.parse(str);
    } catch (ParseException localParseException) {
      throw new ParsingException("INTERNALDATE parse error");
    }
  }
  
  public Date getDate() {
    return date;
  }
  





  private static SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss ", Locale.US);
  private static String tz_offset;
  
  static
  {
    TimeZone localTimeZone = TimeZone.getDefault();
    int i = localTimeZone.getRawOffset();
    StringBuffer localStringBuffer = new StringBuffer();
    if (i < 0) {
      localStringBuffer.append('-');
      i = -i;
    } else {
      localStringBuffer.append('+');
    }
    int j = i / 60 / 1000;
    int k = j / 60;
    int m = j % 60;
    
    localStringBuffer.append(Character.forDigit(k / 10, 10));
    localStringBuffer.append(Character.forDigit(k % 10, 10));
    localStringBuffer.append(Character.forDigit(m / 10, 10));
    localStringBuffer.append(Character.forDigit(m % 10, 10));
    
    tz_offset = localStringBuffer.toString();
  }
  


  public static String format(Date paramDate)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    df.format(paramDate, localStringBuffer, new FieldPosition(0));
    localStringBuffer.append(tz_offset);
    return localStringBuffer.toString();
  }
}
