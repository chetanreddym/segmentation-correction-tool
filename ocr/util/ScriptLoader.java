package ocr.util;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ocr.gui.OCRInterface;
import ocr.gui.OCRInterface.OcrMenuListener;
import ocr.gui.SaveFilesDialog;
import ocr.manager.ExternalProcessManager;
import ocr.tag.GetDateTime;
import ocr.tif.ImageReaderDrawer;




































public class ScriptLoader
{
  private File scripDirPath;
  private ArrayList<String> scriptPropPaths;
  private File[] allFiles;
  private Properties scriptProperties;
  private ExternalProcessManager externalProcessManager;
  private File workingDir;
  private BufferedWriter log;
  
  public ScriptLoader()
  {
    scripDirPath = new File(System.getProperty("user.dir") + ScriptConstants.SCRIPT_DIR);
    scriptPropPaths = new ArrayList();
    new Vector();
    
    log = null;
  }
  






  private void loadSriptPropDir()
  {
    final String fileExtension = ".properties".toUpperCase();
    FileFilter fileFilter = new FileFilter()
    {
      public boolean accept(File file) {
        String fileName = file.getName().toUpperCase();
        


        return (fileName.endsWith(fileExtension)) && (!fileName.contains("ScriptConfig.properties".toUpperCase()));
      }
    };
    
    if (scripDirPath.isDirectory()) {
      allFiles = scripDirPath.listFiles(fileFilter);
      Arrays.sort(allFiles);
    }
  }
  



  public ArrayList<String> getScriptList()
  {
    loadSriptPropDir();
    scriptPropPaths.clear();
    
    if (allFiles == null) {
      return new ArrayList(1);
    }
    for (int i = 0; i < allFiles.length; i++) {
      String scriptPropFilePath = allFiles[i].getAbsolutePath();
      
      scriptProperties = new ScriptProperties(scriptPropFilePath, null);
      
      if (!scriptProperties.isEmpty()) {
        scriptPropPaths.add(scriptPropFilePath);
      }
    }
    return scriptPropPaths;
  }
  








  public String getScriptNameToAppear(String path)
  {
    scriptProperties = new ScriptProperties(path, null);
    
    String name = scriptProperties.getProperty("NAME");
    
    if (name == null) {
      return new File(path).getName();
    }
    return name.trim();
  }
  






  private Script readScriptProperties(String path)
  {
    scriptProperties = new ScriptProperties(path, null);
    
    Script script = new Script();
    
    String appendLogStr = scriptProperties.getProperty("APPEND_LOG");
    
    if (appendLogStr != null) {
      boolean appendLog = Boolean.parseBoolean(appendLogStr);
      script.setAppendLog(appendLog);
    }
    
    File propFile = new File(path);
    
    String logFile_path = propFile.getAbsolutePath();
    logFile_path = logFile_path.replace(".properties", 
      ".log");
    
    script.setLogFile_path(logFile_path);
    
    openLogFile(script, path);
    
    String name = scriptProperties.getProperty("NAME");
    
    if (name != null) {
      script.setName(name.trim());
    } else {
      script.setName(new File(path).getName());
    }
    String runString = scriptProperties.getProperty("COMMAND");
    
    if (runString != null) {
      script.setExecutingString(runString.trim());
    } else {
      showWarning(path, "COMMAND");
      return null;
    }
    
    String showDialogStr = scriptProperties.getProperty("SHOW_DIALOG");
    
    if (showDialogStr != null) {
      boolean showDialog = Boolean.parseBoolean(showDialogStr);
      script.setShowDialog(showDialog);
    }
    

    return script;
  }
  
  public ArrayList<String> getScriptPropPaths() {
    return scriptPropPaths;
  }
  



  public void runScript(String path)
  {
    this_interfacesaveFilesDialog.saveData();
    System.out.println("script prop file: " + path);
    
    Script script = readScriptProperties(path);
    
    if (script == null) {
      return;
    }
    System.out.println(script.toString());
    
    String parsedRunString = script.parseExecutingString(script.getExecutingString());
    System.out.println("parsed: " + parsedRunString);
    try
    {
      writeStars();
      log.newLine();
      log.write("EXECUTING COMMAND:");
      log.newLine();
      log.newLine();
      log.write(parsedRunString);
      log.newLine();
      log.newLine();
      log.flush();
    } catch (IOException e1) { e1.printStackTrace();
    }
    setWaitCursor();
    workingDir = new File(script.getGEDIDir());
    externalProcessManager = new ExternalProcessManager();
    externalProcessManager.executeProgram(
      parsedRunString, 
      workingDir, 
      script.getName(), 
      log, 
      script.isShowDialog());
    







    reloadCurrDoc();
    setDefaultCursor();
    deleteTempFile();
  }
  
  public void showWarning(String proFilePath, String param) {
    String msg = proFilePath + "\n\n" + 
      "Invalid Properties file.\n" + 
      "Check the parameter \"" + param + "\": it is empty, " + 
      " doesn't exist/commented out, or misspelled.\n" + 
      "Please read \"" + "ScriptConfig.properties" + "\" for details.";
    
    JOptionPane.showMessageDialog(
      OCRInterface.this_interface, 
      msg, 
      "Script Execution Error", 
      0);
    try
    {
      writeStars();
      log.write(msg);
      log.newLine();
      log.flush();
      log.close();
    } catch (IOException e) { e.printStackTrace();
    }
  }
  


  private void reloadCurrDoc()
  {
    this_interfacemenuListener.actionPerformed(new ActionEvent(
      OCRInterface.this_interface, 
      128, 
      "Refresh Document List"));
    
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run() {
        OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();
      }
    });
  }
  





  private void deleteTempFile()
  {
    File tempFile = new File(OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator + 
      "temp.xml");
    if (tempFile.exists())
      tempFile.delete();
  }
  
  private void writeStars() {
    try {
      log.newLine();
      log.newLine();
      log.write("**************************************************");
      log.newLine();
      log.write(GetDateTime.getInstance());
      log.newLine();
      log.write("**************************************************");
      log.newLine();
    }
    catch (IOException localIOException) {}
  }
  
  private void openLogFile(Script script, String path)
  {
    try {
      log = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(script.getLogFile_path(), script.isAppendLog())));
    } catch (FileNotFoundException e) { e.printStackTrace();
    }
  }
  
  private void setWaitCursor() { OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
  }
  
  private void setDefaultCursor() {
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
  


  private class ScriptProperties
    extends Properties
  {
    private ScriptProperties(String path)
    {
      try
      {
        myInput = new FileInputStream(path);
        load(myInput);
        myInput.close();
      }
      catch (Exception e) {
        FileInputStream myInput;
        e.printStackTrace();
        System.out.println("Could not load properties file.  Error: " + 
          path + "\n" + e);
      }
    }
  }
}
