package gttool.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import ocr.gui.OCRInterface;












public class LoadGEDIProps
  extends Properties
{
  private static LoadGEDIProps instance = null;
  
  public static String propsPath = null;
  
  private LoadGEDIProps() {
    try {
      FileInputStream myInput = null;
      
      propsPath = getPropsPath();
      
      if (!new File(propsPath).exists()) {
        System.out.println("GEDI.properties not found");
        return;
      }
      
      System.out.println("GEDI.properties path: " + propsPath);
      myInput = new FileInputStream(new String(propsPath));
      load(myInput);
      myInput.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("Could not load GEDI.properties file.  Error: " + e);
    }
  }
  




  public static LoadGEDIProps getInstance()
  {
    if (instance == null) {
      instance = new LoadGEDIProps();
    }
    return instance;
  }
  
  public static void closeGEDIProps()
  {
    try {
      FileOutputStream myOutput = new FileOutputStream(new String(propsPath));
      
      instance.store(myOutput, 
        "DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
      myOutput.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Unable to save Properties.  Error: " + e);
    }
  }
  
  public static void saveGEDIPropsAt(String newPath) {
    try {
      FileOutputStream myOutput = new FileOutputStream(new String(newPath));
      
      instance.store(myOutput, 
        "DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)");
      myOutput.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Unable to save Properties.  Error: " + e);
    }
  }
  
  private String getPropsPath() {
    String defaultPropsPath = System.getProperty("user.home") + 
      File.separator + 
      ".gedi" + 
      File.separator + 
      "config" + 
      File.separator + 
      "GEDI.properties";
    
    String pathFromCommandLine = null;
    
    if (OCRInterface.this_interface.getCommandLineOptions() != null) {
      pathFromCommandLine = 
        OCRInterface.this_interface.getCommandLineOptions().getPropsFilePath();
    }
    LoadUserProps userProps = LoadUserProps.getInstance();
    
    boolean enableProfileLoadOnLogin = Boolean.parseBoolean(
      userProps.getProperty("enableProfileLoadOnLogin"));
    
    boolean enableLoadPropsKey = Boolean.parseBoolean(
      userProps.getProperty("enableLoadProperties"));
    
    String propsPathKey = userProps.getProperty("propertiesPath");
    
    if ((pathFromCommandLine != null) && (!enableProfileLoadOnLogin))
      return pathFromCommandLine;
    if ((pathFromCommandLine != null) && 
      (enableProfileLoadOnLogin) && 
      (enableLoadPropsKey) && 
      (propsPathKey != null) && 
      (!propsPathKey.trim().isEmpty()))
      return propsPathKey;
    if ((pathFromCommandLine == null) && 
      (enableProfileLoadOnLogin) && 
      (enableLoadPropsKey) && 
      (propsPathKey != null) && 
      (!propsPathKey.trim().isEmpty())) {
      return propsPathKey;
    }
    return defaultPropsPath;
  }
}
