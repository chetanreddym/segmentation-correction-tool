package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;




















public class ENVELOPE
  implements Item
{
  public static char[] name = { 'E', 'N', 'V', 'E', 'L', 'O', 'P', 'E' };
  
  public int msgno;
  
  public Date date;
  
  public String subject;
  public InternetAddress[] from;
  public InternetAddress[] sender;
  public InternetAddress[] replyTo;
  public InternetAddress[] to;
  public InternetAddress[] cc;
  public InternetAddress[] bcc;
  public String inReplyTo;
  public String messageId;
  private static MailDateFormat mailDateFormat = new MailDateFormat();
  
  public ENVELOPE(FetchResponse paramFetchResponse) throws ParsingException {
    msgno = paramFetchResponse.getNumber();
    
    paramFetchResponse.skipSpaces();
    
    if (paramFetchResponse.readByte() != 40) {
      throw new ParsingException("ENVELOPE parse error");
    }
    String str = paramFetchResponse.readString();
    if (str != null) {
      try {
        date = mailDateFormat.parse(str);
      }
      catch (Exception localException) {}
    }
    




    subject = paramFetchResponse.readString();
    from = parseAddressList(paramFetchResponse);
    sender = parseAddressList(paramFetchResponse);
    replyTo = parseAddressList(paramFetchResponse);
    to = parseAddressList(paramFetchResponse);
    cc = parseAddressList(paramFetchResponse);
    bcc = parseAddressList(paramFetchResponse);
    inReplyTo = paramFetchResponse.readString();
    messageId = paramFetchResponse.readString();
    
    if (paramFetchResponse.readByte() != 41) {
      throw new ParsingException("ENVELOPE parse error");
    }
  }
  
  private InternetAddress[] parseAddressList(Response paramResponse) throws ParsingException {
    paramResponse.skipSpaces();
    
    int i = paramResponse.readByte();
    if (i == 40) {
      Vector localVector = new Vector();
      do
      {
        localVector.addElement(new IMAPAddress(paramResponse));
      } while (paramResponse.peekByte() != 41);
      

      paramResponse.skip(1);
      
      InternetAddress[] arrayOfInternetAddress = new InternetAddress[localVector.size()];
      localVector.copyInto(arrayOfInternetAddress);
      return arrayOfInternetAddress; }
    if ((i == 78) || (i == 110)) {
      paramResponse.skip(2);
      return null;
    }
    throw new ParsingException("ADDRESS parse error");
  }
}
