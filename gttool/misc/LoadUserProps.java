package gttool.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;






public class LoadUserProps
  extends Properties
{
  private static LoadUserProps instance = null;
  



  private static String userPropsPath = System.getProperty("user.home") + 
    File.separator + 
    ".gedi" + 
    File.separator + 
    "config" + 
    File.separator + 
    "user.properties";
  
  private LoadUserProps()
  {
    File userPropsFile = new File(userPropsPath);
    System.out.println("user.properties path: " + userPropsFile);
    if (!userPropsFile.exists()) {
      userPropsFile.getParentFile().mkdirs();
      try
      {
        userPropsFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(userPropsFile));
        out.write("");
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    try
    {
      FileInputStream myInput = new FileInputStream(new String(userPropsPath));
      load(myInput);
      myInput.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("Could not load User.properties file.  Error: " + e);
    }
  }
  






  public static LoadUserProps getInstance()
  {
    instance = new LoadUserProps();
    
    return instance;
  }
  
  public static void closeUserProps() {
    try {
      FileOutputStream myOutput = new FileOutputStream(new String(userPropsPath));
      instance.store(myOutput, "GEDI user list");
      myOutput.close();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Unable to save user.properties.  Error: " + ex);
    }
  }
}
