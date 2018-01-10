package javax.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.search.SearchTerm;















































public abstract class Message
  implements Part
{
  protected int msgnum;
  protected boolean expunged = false;
  




  protected Folder folder;
  



  protected Session session;
  




  protected Message() {}
  




  protected Message(Folder paramFolder, int paramInt)
  {
    folder = paramFolder;
    msgnum = paramInt;
    session = store.session;
  }
  





  protected Message(Session paramSession)
  {
    session = paramSession;
  }
  












  public abstract Address[] getFrom()
    throws MessagingException;
  












  public abstract void setFrom()
    throws MessagingException;
  











  public abstract void setFrom(Address paramAddress)
    throws MessagingException;
  











  public abstract void addFrom(Address[] paramArrayOfAddress)
    throws MessagingException;
  











  public abstract Address[] getRecipients(RecipientType paramRecipientType)
    throws MessagingException;
  











  public static class RecipientType
    implements Serializable
  {
    public static final RecipientType TO = new RecipientType("To");
    


    public static final RecipientType CC = new RecipientType("Cc");
    


    public static final RecipientType BCC = new RecipientType("Bcc");
    




    protected String type;
    




    protected RecipientType(String paramString)
    {
      type = paramString;
    }
    





    protected Object readResolve()
      throws ObjectStreamException
    {
      if (type.equals("To"))
        return TO;
      if (type.equals("Cc"))
        return CC;
      if (type.equals("Bcc")) {
        return BCC;
      }
      throw new InvalidObjectException(
        "Attempt to resolve unknown RecipientType: " + type);
    }
  }
  































  public Address[] getAllRecipients()
    throws MessagingException
  {
    Address[] arrayOfAddress1 = getRecipients(RecipientType.TO);
    Address[] arrayOfAddress2 = getRecipients(RecipientType.CC);
    Address[] arrayOfAddress3 = getRecipients(RecipientType.BCC);
    
    if ((arrayOfAddress2 == null) && (arrayOfAddress3 == null)) {
      return arrayOfAddress1;
    }
    int i = 
      (arrayOfAddress1 != null ? arrayOfAddress1.length : 0) + (
      arrayOfAddress2 != null ? arrayOfAddress2.length : 0) + (
      arrayOfAddress3 != null ? arrayOfAddress3.length : 0);
    Address[] arrayOfAddress4 = new Address[i];
    int j = 0;
    if (arrayOfAddress1 != null) {
      System.arraycopy(arrayOfAddress1, 0, arrayOfAddress4, j, arrayOfAddress1.length);
      j += arrayOfAddress1.length;
    }
    if (arrayOfAddress2 != null) {
      System.arraycopy(arrayOfAddress2, 0, arrayOfAddress4, j, arrayOfAddress2.length);
      j += arrayOfAddress2.length;
    }
    if (arrayOfAddress3 != null) {
      System.arraycopy(arrayOfAddress3, 0, arrayOfAddress4, j, arrayOfAddress3.length);
      j += arrayOfAddress3.length;
    }
    return arrayOfAddress4;
  }
  













  public abstract void setRecipients(RecipientType paramRecipientType, Address[] paramArrayOfAddress)
    throws MessagingException;
  












  public void setRecipient(RecipientType paramRecipientType, Address paramAddress)
    throws MessagingException
  {
    Address[] arrayOfAddress = new Address[1];
    arrayOfAddress[0] = paramAddress;
    setRecipients(paramRecipientType, arrayOfAddress);
  }
  












  public abstract void addRecipients(RecipientType paramRecipientType, Address[] paramArrayOfAddress)
    throws MessagingException;
  











  public void addRecipient(RecipientType paramRecipientType, Address paramAddress)
    throws MessagingException
  {
    Address[] arrayOfAddress = new Address[1];
    arrayOfAddress[0] = paramAddress;
    addRecipients(paramRecipientType, arrayOfAddress);
  }
  














  public Address[] getReplyTo()
    throws MessagingException
  {
    return getFrom();
  }
  


















  public void setReplyTo(Address[] paramArrayOfAddress)
    throws MessagingException
  {
    throw new MethodNotSupportedException("setReplyTo not supported");
  }
  









  public abstract String getSubject()
    throws MessagingException;
  









  public abstract void setSubject(String paramString)
    throws MessagingException;
  









  public abstract Date getSentDate()
    throws MessagingException;
  









  public abstract void setSentDate(Date paramDate)
    throws MessagingException;
  









  public abstract Date getReceivedDate()
    throws MessagingException;
  









  public abstract Flags getFlags()
    throws MessagingException;
  









  public boolean isSet(Flags.Flag paramFlag)
    throws MessagingException
  {
    return getFlags().contains(paramFlag);
  }
  



















  public abstract void setFlags(Flags paramFlags, boolean paramBoolean)
    throws MessagingException;
  


















  public void setFlag(Flags.Flag paramFlag, boolean paramBoolean)
    throws MessagingException
  {
    Flags localFlags = new Flags(paramFlag);
    setFlags(localFlags, paramBoolean);
  }
  












  public int getMessageNumber()
  {
    return msgnum;
  }
  



  protected void setMessageNumber(int paramInt)
  {
    msgnum = paramInt;
  }
  






  public Folder getFolder()
  {
    return folder;
  }
  
















  public boolean isExpunged()
  {
    return expunged;
  }
  





  protected void setExpunged(boolean paramBoolean)
  {
    expunged = paramBoolean;
  }
  
















  public abstract Message reply(boolean paramBoolean)
    throws MessagingException;
  
















  public abstract void saveChanges()
    throws MessagingException;
  
















  public boolean match(SearchTerm paramSearchTerm)
    throws MessagingException
  {
    return paramSearchTerm.match(this);
  }
  
  public abstract int getSize()
    throws MessagingException;
  
  public abstract int getLineCount()
    throws MessagingException;
  
  public abstract String getContentType()
    throws MessagingException;
  
  public abstract boolean isMimeType(String paramString)
    throws MessagingException;
  
  public abstract String getDisposition()
    throws MessagingException;
  
  public abstract void setDisposition(String paramString)
    throws MessagingException;
  
  public abstract String getDescription()
    throws MessagingException;
  
  public abstract void setDescription(String paramString)
    throws MessagingException;
  
  public abstract String getFileName()
    throws MessagingException;
  
  public abstract void setFileName(String paramString)
    throws MessagingException;
  
  public abstract InputStream getInputStream()
    throws IOException, MessagingException;
  
  public abstract DataHandler getDataHandler()
    throws MessagingException;
  
  public abstract Object getContent()
    throws IOException, MessagingException;
  
  public abstract void setDataHandler(DataHandler paramDataHandler)
    throws MessagingException;
  
  public abstract void setContent(Object paramObject, String paramString)
    throws MessagingException;
  
  public abstract void setText(String paramString)
    throws MessagingException;
  
  public abstract void setContent(Multipart paramMultipart)
    throws MessagingException;
  
  public abstract void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException;
  
  public abstract String[] getHeader(String paramString)
    throws MessagingException;
  
  public abstract void setHeader(String paramString1, String paramString2)
    throws MessagingException;
  
  public abstract void addHeader(String paramString1, String paramString2)
    throws MessagingException;
  
  public abstract void removeHeader(String paramString)
    throws MessagingException;
  
  public abstract Enumeration getAllHeaders()
    throws MessagingException;
  
  public abstract Enumeration getMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException;
  
  public abstract Enumeration getNonMatchingHeaders(String[] paramArrayOfString)
    throws MessagingException;
}
