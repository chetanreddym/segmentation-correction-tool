package ocr.util;

import java.io.File;
import java.io.IOException;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.tif.ImageReaderDrawer;













public class Script
{
  private String name;
  private String executingString;
  private String logFile_path;
  private boolean showDialog;
  private boolean appendLog;
  
  public Script()
  {
    name = null;
    executingString = null;
    logFile_path = null;
    showDialog = true;
    appendLog = false;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setExecutingString(String executingString) {
    this.executingString = executingString;
  }
  
  public String getExecutingString() {
    return executingString;
  }
  
  public String getLogFile_path() {
    return logFile_path;
  }
  
  public void setLogFile_path(String logFile_path) {
    this.logFile_path = logFile_path;
  }
  
  public boolean isShowDialog() {
    return showDialog;
  }
  
  public void setShowDialog(boolean showDialog) {
    this.showDialog = showDialog;
  }
  
  public boolean isAppendLog() {
    return appendLog;
  }
  
  public void setAppendLog(boolean appendLog) {
    this.appendLog = appendLog;
  }
  
  public String parseExecutingString(String str) {
    String parsedStr = str;
    
    parsedStr = parsedStr.replace("[%img-root]", getCurrentImgRoot());
    parsedStr = parsedStr.replace("[%img-ext]", getCurrentImgExt());
    
    parsedStr = parsedStr.replace("[%img-dir-name]", getCurrentImageDirName());
    parsedStr = parsedStr.replace("[%img-dir]", getCurrentImageDir());
    
    parsedStr = parsedStr.replace("[%img]", getCurrentImg());
    
    parsedStr = parsedStr.replace("[%xml-root]", getCurrentXmlRoot());
    parsedStr = parsedStr.replace("[%xml-ext]", getCurrentXmlExt());
    
    parsedStr = parsedStr.replace("[%xml-dir-name]", getCurrentXmlDirName());
    parsedStr = parsedStr.replace("[%xml-dir]", getCurrentXmlDir());
    
    parsedStr = parsedStr.replace("[%xml]", getCurrentXml());
    
    parsedStr = parsedStr.replace("[%gedi-dir]", getGEDIDir());
    

    String exe = parsedStr.split(" ")[0].trim();
    String _exe = exe.replace("\"", "").trim();
    

    String fullPath = getCurrentImageDir() + File.separator + _exe;
    File exeFile = new File(fullPath);
    
    if ((!_exe.trim().equals("")) && (exeFile.exists())) {
      parsedStr = parsedStr.replace(exe, fullPath);
    }
    return parsedStr;
  }
  
  private String getCurrentImg() {
    return ImageReaderDrawer.getFile_path();
  }
  
  private String getCurrentImgRoot() {
    String name = getCurrentImageName();
    return name.substring(0, name.lastIndexOf('.'));
  }
  
  private String getCurrentImgExt() {
    String name = getCurrentImageName();
    return name.substring(name.lastIndexOf('.'), name.length());
  }
  
  private String getCurrentImageName() {
    File file = new File(getCurrentImg());
    return file.getName();
  }
  
  public String getCurrentImageDir() {
    return OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName();
  }
  
  private String getCurrentImageDirName() {
    return OCRInterface.getImageDirName();
  }
  
  private String getCurrentXml() {
    File xmlFile = getCurrentXmlFile();
    
    if (xmlFile == null) {
      return "null";
    }
    try {
      return xmlFile.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace(); }
    return null;
  }
  
  private String getCurrentXmlRoot()
  {
    String name = getCurrentXmlName();
    if (name == null) {
      return "null";
    }
    return name.substring(0, name.lastIndexOf('.'));
  }
  
  private String getCurrentXmlExt() {
    String name = getCurrentXmlName();
    if (name == null)
      return "null";
    return name.substring(name.lastIndexOf('.'), name.length());
  }
  
  private String getCurrentXmlName() {
    File xmlFile = getCurrentXmlFile();
    
    if (xmlFile == null) {
      return null;
    }
    return xmlFile.getName();
  }
  
  private File getCurrentXmlFile() {
    if (OCRInterface.currDoc == null) {
      return null;
    }
    File file = new File(OCRInterface.currDoc.getFilePath());
    try {
      return file.getCanonicalFile();
    } catch (IOException e) {
      e.printStackTrace(); }
    return null;
  }
  
  public String getCurrentXmlDir()
  {
    return OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName();
  }
  
  private String getCurrentXmlDirName() {
    return OCRInterface.getXmlDirName();
  }
  
  public String getGEDIDir() {
    return System.getProperty("user.dir");
  }
  
  public String toString() {
    String newLine = "\n";
    return 
      newLine + 
      "Script Name: " + name + newLine + 
      "Run String: " + executingString + newLine + 
      "Log file path: " + logFile_path + newLine;
  }
}
