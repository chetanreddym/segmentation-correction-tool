package com.sun.mail.imap;

import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import java.util.Vector;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;



















public class IMAPMultipartDataSource
  extends MimePartDataSource
  implements MultipartDataSource
{
  private Vector parts;
  
  protected IMAPMultipartDataSource(MimePart paramMimePart, BODYSTRUCTURE[] paramArrayOfBODYSTRUCTURE, String paramString, IMAPMessage paramIMAPMessage)
  {
    super(paramMimePart);
    
    parts = new Vector(paramArrayOfBODYSTRUCTURE.length);
    for (int i = 0; i < paramArrayOfBODYSTRUCTURE.length; i++) {
      parts.addElement(
        new IMAPBodyPart(paramArrayOfBODYSTRUCTURE[i], 
        

        paramString + "." + Integer.toString(i + 1), 
        paramIMAPMessage));
    }
  }
  
  public int getCount() {
    return parts.size();
  }
  
  public BodyPart getBodyPart(int paramInt) throws MessagingException {
    return (BodyPart)parts.elementAt(paramInt);
  }
}
