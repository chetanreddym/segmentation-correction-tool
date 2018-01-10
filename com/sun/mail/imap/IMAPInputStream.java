package com.sun.mail.imap;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.IMAPProtocol;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;













public class IMAPInputStream
  extends InputStream
{
  private IMAPMessage msg;
  private String section;
  private int pos;
  private int blksize;
  private int max;
  private byte[] buf;
  private int bufcount;
  private int bufpos;
  
  public IMAPInputStream(IMAPMessage paramIMAPMessage, String paramString, int paramInt)
  {
    msg = paramIMAPMessage;
    section = paramString;
    max = paramInt;
    pos = 0;
    blksize = paramIMAPMessage.getFetchBlockSize();
  }
  






  private void fill()
    throws IOException
  {
    if ((max != -1) && (pos >= max)) {
      if (pos == 0)
        checkSeen();
      return;
    }
    
    BODY localBODY = null;
    

    synchronized (msg.getMessageCacheLock())
    {

      if (msg.isExpunged()) {
        throw new IOException("No content for expunged message");
      }
      int j = msg.getSequenceNumber();
      try {
        localBODY = msg.getProtocol().fetchBody(j, section, pos, blksize);
      } catch (ProtocolException localProtocolException) {
        throw new IOException(localProtocolException.getMessage());
      } catch (FolderClosedException localFolderClosedException) {
        throw new IOException(localFolderClosedException.getMessage());
      }
    }
    

    if ((localBODY == null) || ((??? = localBODY.getByteArray()) == null)) {
      throw new IOException("No content");
    }
    
    if (pos == 0) {
      checkSeen();
    }
    
    buf = ((ByteArray)???).getBytes();
    bufpos = ((ByteArray)???).getStart();
    int i = ((ByteArray)???).getCount();
    
    bufcount = (bufpos + i);
    pos += i;
  }
  


  public synchronized int read()
    throws IOException
  {
    if (bufpos >= bufcount) {
      fill();
      if (bufpos >= bufcount)
        return -1;
    }
    return buf[(bufpos++)] & 0xFF;
  }
  














  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = bufcount - bufpos;
    if (i <= 0) {
      fill();
      i = bufcount - bufpos;
      if (i <= 0)
        return -1;
    }
    int j = i < paramInt2 ? i : paramInt2;
    System.arraycopy(buf, bufpos, paramArrayOfByte, paramInt1, j);
    bufpos += j;
    return j;
  }
  












  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  


  public synchronized int available()
    throws IOException
  {
    return bufcount - bufpos;
  }
  




  private void checkSeen()
  {
    try
    {
      Folder localFolder = msg.getFolder();
      if ((localFolder != null) && (localFolder.getMode() != 1) && 
        (!msg.isSet(Flags.Flag.SEEN))) {
        msg.setFlag(Flags.Flag.SEEN, true);
      }
    }
    catch (MessagingException localMessagingException) {}
  }
}
