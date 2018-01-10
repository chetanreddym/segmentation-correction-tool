package com.sun.mail.pop3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.SharedInputStream;















public class POP3Message
  extends MimeMessage
{
  static final String UNKNOWN = "UNKNOWN";
  private POP3Folder folder;
  private int hdrSize = -1;
  private int msgSize = -1;
  String uid = "UNKNOWN";
  
  public POP3Message(Folder paramFolder, int paramInt) throws MessagingException
  {
    super(paramFolder, paramInt);
    folder = ((POP3Folder)paramFolder);
  }
  





  public void setFlags(Flags paramFlags, boolean paramBoolean)
    throws MessagingException
  {
    Flags localFlags = (Flags)flags.clone();
    super.setFlags(paramFlags, paramBoolean);
    if (!flags.equals(localFlags)) {
      folder.notifyMessageChangedListeners(
        1, this);
    }
  }
  









  public int getSize()
    throws MessagingException
  {
    if (msgSize >= 0)
      return msgSize;
    try {
      synchronized (this) {
        if (msgSize < 0)
        {







          if (headers == null)
            loadHeaders();
          if (contentStream != null) {
            msgSize = contentStream.available();
          } else
            msgSize = (folder.getProtocol().list(msgnum) - hdrSize);
        }
        int i = msgSize;return i;
      }
    } catch (EOFException localEOFException) {
      folder.close(false);
      throw new FolderClosedException(folder, localEOFException.toString());
    } catch (IOException localIOException) {
      throw new MessagingException("error getting size", localIOException);
    }
  }
  




  protected InputStream getContentStream()
    throws MessagingException
  {
    try
    {
      synchronized (this) {
        if (contentStream == null) {
          InputStream localInputStream = folder.getProtocol().retr(msgnum, 
            msgSize > 0 ? msgSize + hdrSize : 0);
          if (localInputStream == null) {
            expunged = true;
            throw new MessageRemovedException();
          }
          if (headers == null) {
            headers = new InternetHeaders(localInputStream);
            hdrSize = 
              ((int)((SharedInputStream)localInputStream).getPosition());




          }
          else
          {



            for (;;)
            {



              int i = 0;
              int j;
              while ((j = localInputStream.read()) >= 0) {
                if (j == 10)
                  break;
                if (j == 13)
                {
                  if (localInputStream.available() <= 0) break;
                  localInputStream.mark(1);
                  if (localInputStream.read() == 10) break;
                  localInputStream.reset();
                  
                  break;
                }
                

                i++;
              }
              


              if (localInputStream.available() != 0)
              {


                if (i == 0)
                  break; }
            }
            hdrSize = 
              ((int)((SharedInputStream)localInputStream).getPosition());
          }
          contentStream = 
            ((SharedInputStream)localInputStream).newStream(hdrSize, -1L);
          localInputStream = null;
        }
      }
    } catch (EOFException localEOFException) {
      folder.close(false);
      throw new FolderClosedException(folder, localEOFException.toString());
    } catch (IOException localIOException) {
      throw new MessagingException("error fetching POP3 content", localIOException);
    }
    return super.getContentStream();
  }
  









  public String[] getHeader(String paramString)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getHeader(paramString);
  }
  










  public String getHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getHeader(paramString1, paramString2);
  }
  












  public void setHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    throw new IllegalWriteException("POP3 messages are read-only");
  }
  












  public void addHeader(String paramString1, String paramString2)
    throws MessagingException
  {
    throw new IllegalWriteException("POP3 messages are read-only");
  }
  









  public void removeHeader(String paramString)
    throws MessagingException
  {
    throw new IllegalWriteException("POP3 messages are read-only");
  }
  










  public Enumeration getAllHeaders()
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getAllHeaders();
  }
  





  public Enumeration getMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getMatchingHeaders(paramArrayOfString);
  }
  





  public Enumeration getNonMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getNonMatchingHeaders(paramArrayOfString);
  }
  








  public void addHeaderLine(String paramString)
    throws MessagingException
  {
    throw new IllegalWriteException("POP3 messages are read-only");
  }
  





  public Enumeration getAllHeaderLines()
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getAllHeaderLines();
  }
  






  public Enumeration getMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getMatchingHeaderLines(paramArrayOfString);
  }
  






  public Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString)
    throws MessagingException
  {
    if (headers == null)
      loadHeaders();
    return headers.getNonMatchingHeaderLines(paramArrayOfString);
  }
  






  public void saveChanges()
    throws MessagingException
  {
    throw new IllegalWriteException("POP3 messages are read-only");
  }
  

  private void loadHeaders()
    throws MessagingException
  {
    try
    {
      synchronized (this) {
        if (headers != null)
          return;
        InputStream localInputStream1 = folder.getProtocol().top(msgnum, 0);
        if (localInputStream1 == null)
        {


          InputStream localInputStream2 = getContentStream();
          localInputStream2.close();
        } else {
          hdrSize = localInputStream1.available();
          headers = new InternetHeaders(localInputStream1);
        }
      }
    } catch (EOFException localEOFException) {
      folder.close(false);
      throw new FolderClosedException(folder, localEOFException.toString());
    } catch (IOException localIOException) {
      throw new MessagingException("error loading POP3 headers", localIOException);
    }
  }
}
