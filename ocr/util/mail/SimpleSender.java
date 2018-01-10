package ocr.util.mail;

import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;







public class SimpleSender
{
  public SimpleSender() {}
  
  public static void main(String[] args)
  {
    try
    {
      String smtpServer = args[0];
      String to = args[1];
      String from = args[2];
      String subject = args[3];
      String body = args[4];
      
      send(smtpServer, to, from, subject, body);
    }
    catch (Exception ex)
    {
      System.out.println("Usage: java com.lotontech.mail.SimpleSender smtpServer toAddress fromAddress subjectText bodyText");
    }
    

    System.exit(0);
  }
  




  public static void send(String smtpServer, String to, String from, String subject, String body)
  {
    try
    {
      Properties props = System.getProperties();
      
      props.put("mail.smtp.starttls.enable", "true");
      




      props.put("mail.smtp.host", smtpServer);
      Session session = Session.getDefaultInstance(props, null);
      

      Message msg = new MimeMessage(session);
      

      msg.setFrom(new InternetAddress(from));
      msg.setRecipients(Message.RecipientType.TO, 
        InternetAddress.parse(to, false));
      






      msg.setSubject(subject);
      msg.setText(body);
      

      msg.setHeader("X-Mailer", "LOTONtechEmail");
      msg.setSentDate(new Date());
      



      Transport.send(msg);
      
      System.out.println("Message sent OK.");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
