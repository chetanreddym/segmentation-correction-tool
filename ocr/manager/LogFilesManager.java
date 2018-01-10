package ocr.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import ocr.util.DeveloperView;
import ocr.util.mail.EmailProps;
import ocr.util.mail.SimpleSender;







public class LogFilesManager
  extends Thread
{
  private File logFile;
  private StringBuffer messageToSend = new StringBuffer();
  
  private static String emailPropsPath = System.getProperty("user.home") + 
    File.separator + 
    ".gedi" + 
    File.separator + 
    "config" + 
    File.separator + 
    "email.properties";
  
  public LogFilesManager(File logFile) {
    this.logFile = logFile;
  }
  

  public void run()
  {
    File userPropsFile = new File(emailPropsPath);
    System.out.println("email.properties path: " + userPropsFile);
    if (!userPropsFile.exists()) {
      userPropsFile.getParentFile().mkdirs();
      try
      {
        userPropsFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(userPropsFile));
        out.write("SenderAddress=ezotkina@umiacs.umd.edu");
        out.newLine();
        out.write("ReceiverAddress=ezotkina@umiacs.umd.edu");
        out.newLine();
        out.write("ServerAddress=smtp.umiacs.umd.edu");
        out.newLine();
        out.write("SendBugReports=false");
        out.newLine();
        out.close();
        return;
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    





    if (needToEmail()) {
      EmailProps props = new EmailProps(new File(emailPropsPath));
      
      if ((props != null) && (props.getSendBugReports()))
      {

        String messageBody = getMessageBody();
        

        String sender = props.getSenderAddress();
        String senderServerAddress = props.getServerAddress();
        String subject = "GEDI Exception";
        String receiver = props.getReceiverAddress();
        

        SimpleSender.send(senderServerAddress, receiver, sender, subject, messageBody);
      } else {
        System.out.println("user doesn't want to send bug reports.");
      }
    }
    else {
      DeveloperView.println("No exception found thus no need to email admin.");
    }
    super.run();
  }
  







  private String getMessageBody()
  {
    return messageToSend.toString();
  }
  







  private boolean needToEmail()
  {
    boolean returnValue = false;
    
    try
    {
      BufferedReader r = new BufferedReader(new FileReader(logFile));
      String exceptionString = "Exception";
      
      String line = r.readLine();
      
      while (line != null) {
        if ((!returnValue) && (line.indexOf(exceptionString) != -1)) { returnValue = true;
        }
        messageToSend.append(line);
        line = r.readLine();
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    return returnValue;
  }
}
