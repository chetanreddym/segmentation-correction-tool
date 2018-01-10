package javax.mail;

import javax.activation.DataSource;

public interface MultipartDataSource
  extends DataSource
{
  public abstract int getCount();
  
  public abstract BodyPart getBodyPart(int paramInt)
    throws MessagingException;
}
