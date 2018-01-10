package ocr.util;

import ocr.gui.OCRInterface;

























public class CapabilitiesControl
{
  private static boolean enableNetworkListener;
  private static boolean enableAlignment;
  private static boolean enableScripting;
  private static boolean enableEditing;
  
  public CapabilitiesControl()
  {
    enableNetworkListener = true;
    enableAlignment = true;
    enableScripting = true;
    enableEditing = true;
    checkNetworkLisneter();
    checkAlignment();
  }
  
  private void checkNetworkLisneter() {
    if ((OCRInterface.this_interface.getNetworkListenerOn()) && 
      (!enableNetworkListener))
      OCRInterface.this_interface.setNetworkListenerOn(false);
  }
  
  private void checkAlignment() {
    if ((OCRInterface.this_interface.getEnableAlignment()) && 
      (!enableAlignment))
      OCRInterface.this_interface.setEnableAlignment(false);
  }
  
  public static boolean isEnableNetworkListener() {
    return enableNetworkListener;
  }
  
  public void setEnableNetworkListener(boolean enableNetworkListener) {
    enableNetworkListener = enableNetworkListener;
  }
  
  public static boolean isEnableAlignment() {
    return enableAlignment;
  }
  
  public void setEnableAlignment(boolean enableAlignment) {
    enableAlignment = enableAlignment;
  }
  
  public static boolean isEnableScripting() {
    return enableScripting;
  }
  
  public void setEnableScripting(boolean enableScripting) {
    enableScripting = enableScripting;
  }
  
  public static boolean isEnableEditing() {
    return enableEditing;
  }
  
  public void setEnableEditing(boolean enableEditing) {
    enableEditing = enableEditing;
  }
}
