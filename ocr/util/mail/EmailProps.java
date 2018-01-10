package ocr.util.mail;

import java.io.File;
import ocr.view.Props;












public class EmailProps
  extends Props
{
  public EmailProps(File propsFile)
  {
    super(propsFile);
  }
  
  public String getSenderAddress()
  {
    String address = getProperty("SenderAddress");
    
    return address;
  }
  
  public String getReceiverAddress() {
    String address = getProperty("ReceiverAddress");
    
    return address;
  }
  
  public String getServerAddress() {
    String address = getProperty("ServerAddress");
    
    return address;
  }
  





  public boolean getSendBugReports()
  {
    String bugReportString = getProperty("SendBugReports");
    return Boolean.parseBoolean(bugReportString);
  }
}
