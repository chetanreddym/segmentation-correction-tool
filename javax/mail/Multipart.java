package javax.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.activation.DataSource;


































public abstract class Multipart
{
  protected Vector parts = new Vector();
  




  protected String contentType = "multipart/mixed";
  







  protected Part parent;
  








  protected Multipart() {}
  







  protected void setMultipartDataSource(MultipartDataSource paramMultipartDataSource)
    throws MessagingException
  {
    contentType = paramMultipartDataSource.getContentType();
    
    int i = paramMultipartDataSource.getCount();
    for (int j = 0; j < i; j++) {
      addBodyPart(paramMultipartDataSource.getBodyPart(j));
    }
  }
  







  public String getContentType()
  {
    return contentType;
  }
  




  public int getCount()
    throws MessagingException
  {
    if (parts == null) {
      return 0;
    }
    return parts.size();
  }
  







  public BodyPart getBodyPart(int paramInt)
    throws MessagingException
  {
    if (parts == null) {
      throw new IndexOutOfBoundsException("No such BodyPart");
    }
    return (BodyPart)parts.elementAt(paramInt);
  }
  









  public boolean removeBodyPart(BodyPart paramBodyPart)
    throws MessagingException
  {
    if (parts == null) {
      throw new MessagingException("No such body part");
    }
    boolean bool = parts.removeElement(paramBodyPart);
    paramBodyPart.setParent(null);
    return bool;
  }
  










  public void removeBodyPart(int paramInt)
    throws MessagingException
  {
    if (parts == null) {
      throw new IndexOutOfBoundsException("No such BodyPart");
    }
    BodyPart localBodyPart = (BodyPart)parts.elementAt(paramInt);
    parts.removeElementAt(paramInt);
    localBodyPart.setParent(null);
  }
  









  public synchronized void addBodyPart(BodyPart paramBodyPart)
    throws MessagingException
  {
    if (parts == null) {
      parts = new Vector();
    }
    parts.addElement(paramBodyPart);
    paramBodyPart.setParent(this);
  }
  













  public synchronized void addBodyPart(BodyPart paramBodyPart, int paramInt)
    throws MessagingException
  {
    if (parts == null) {
      parts = new Vector();
    }
    parts.insertElementAt(paramBodyPart, paramInt);
    paramBodyPart.setParent(this);
  }
  







  public abstract void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException;
  






  public Part getParent()
  {
    return parent;
  }
  








  public void setParent(Part paramPart)
  {
    parent = paramPart;
  }
}
