package javax.mail.internet;

import com.sun.mail.util.LineOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.MessagingException;











































public class PreencodedMimeBodyPart
  extends MimeBodyPart
{
  private String encoding;
  
  public PreencodedMimeBodyPart(String paramString)
  {
    encoding = paramString;
  }
  


  public String getEncoding()
    throws MessagingException
  {
    return encoding;
  }
  










  public void writeTo(OutputStream paramOutputStream)
    throws IOException, MessagingException
  {
    LineOutputStream localLineOutputStream = null;
    if ((paramOutputStream instanceof LineOutputStream)) {
      localLineOutputStream = (LineOutputStream)paramOutputStream;
    } else {
      localLineOutputStream = new LineOutputStream(paramOutputStream);
    }
    

    Enumeration localEnumeration = getAllHeaderLines();
    while (localEnumeration.hasMoreElements()) {
      localLineOutputStream.writeln((String)localEnumeration.nextElement());
    }
    
    localLineOutputStream.writeln();
    

    getDataHandler().writeTo(paramOutputStream);
    paramOutputStream.flush();
  }
  


  protected void updateHeaders()
    throws MessagingException
  {
    super.updateHeaders();
    MimeBodyPart.setEncoding(this, encoding);
  }
}
