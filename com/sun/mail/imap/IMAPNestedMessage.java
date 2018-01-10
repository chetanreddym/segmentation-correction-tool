package com.sun.mail.imap;

import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.IMAPProtocol;
import javax.mail.Flags;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;














public class IMAPNestedMessage
  extends IMAPMessage
{
  private IMAPMessage msg;
  
  IMAPNestedMessage(IMAPMessage paramIMAPMessage, BODYSTRUCTURE paramBODYSTRUCTURE, ENVELOPE paramENVELOPE, String paramString)
  {
    super(paramIMAPMessage._getSession());
    msg = paramIMAPMessage;
    bs = paramBODYSTRUCTURE;
    envelope = paramENVELOPE;
    sectionId = paramString;
  }
  


  protected IMAPProtocol getProtocol()
    throws FolderClosedException
  {
    return msg.getProtocol();
  }
  



  protected Object getMessageCacheLock()
  {
    return msg.getMessageCacheLock();
  }
  



  protected int getSequenceNumber()
  {
    return msg.getSequenceNumber();
  }
  


  protected void checkExpunged()
    throws MessageRemovedException
  {
    msg.checkExpunged();
  }
  



  public boolean isExpunged()
  {
    return msg.isExpunged();
  }
  


  protected int getFetchBlockSize()
  {
    return msg.getFetchBlockSize();
  }
  


  public int getSize()
    throws MessagingException
  {
    return bs.size;
  }
  



  public synchronized void setFlags(Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    throw new MethodNotSupportedException(
      "Cannot set flags on this nested message");
  }
}
