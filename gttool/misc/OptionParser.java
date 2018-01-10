package gttool.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.swing.JOptionPane;
import ocr.gui.OCRInterface;
import ocr.util.OutputStream;























public class OptionParser
{
  private final String IMAGE = "-img";
  private final String XML = "-xml";
  private final String IMAGE_DIR = "-imgDir";
  private final String XML_DIR = "-xmlDir";
  private final String CONFIG = "-config";
  private final String PROPS = "-props";
  private final String ZONE_ID = "-zoneID";
  private final String ROOT_PATH = "-rootPath";
  private final String LISTENER = "-listener";
  private final String HELP = "-help";
  
  private boolean networkListenerRequired = false;
  private boolean isNetworkListenerRunning = false;
  
  HashMap<String, String> parametersMap = new HashMap();
  private FileOutputStream logFile = null;
  
  public OptionParser(ArrayList<String> options, Login login) {
    selectErrorStream();
    String argument = null;String option = null;
    ListIterator<String> li = options.listIterator();
    while (li.hasNext()) {
      option = (String)li.next();
      if ((option.equals("-listener")) && (isValidOption(option))) {
        networkListenerRequired = true;
      }
      else {
        if ((option.equals("-help")) || (!isValidOption(option))) {
          printHelp(null);
        }
        try {
          argument = (String)li.next();
          argument = argument.replace("\"", "");
          parametersMap.put(option, argument);
        } catch (NoSuchElementException e) {
          printHelp("Provide a value for the \"" + option + "\" option.");
        }
      }
      


      if (parametersMap.containsKey("-config")) {
        File configFile = new File((String)parametersMap.get("-config"));
        if ((configFile.isDirectory()) || (!configFile.exists()))
          printHelp("ERROR: Configuration file for the \"-config\" option doesn't exist.");
      }
      if (parametersMap.containsKey("-rootPath")) {
        File rootPath = new File((String)parametersMap.get("-rootPath"));
        if (!rootPath.exists())
          printHelp("ERROR: Root path for the \"-rootPath\" option doesn't exist.");
      }
      if (parametersMap.containsKey("-props")) {
        File rootPath = new File((String)parametersMap.get("-props"));
        if (!rootPath.exists()) {
          printHelp("ERROR: Properties file for the \"-props\" option doesn't exist.");
        }
      }
    }
    System.out.println("Passing parameters: " + parametersMap);
    


    if (networkListenerRequired) {
      processOptionsViaNetworkListener();
    }
    closeLogFile();
  }
  
  public boolean isValidOption(String option) {
    boolean isValid = false;
    
    if ((option.equals("-img")) || 
      (option.equals("-xml")) || 
      (option.equals("-imgDir")) || 
      (option.equals("-xmlDir")) || 
      (option.equals("-config")) || 
      (option.equals("-props")) || 
      (option.equals("-zoneID")) || 
      (option.equals("-rootPath")) || 
      (option.equals("-listener"))) {
      isValid = true;
    }
    if (!isValid) {
      printHelp("\"" + option + "\" option is invalid.");
    }
    return isValid;
  }
  
  private void checkParameters()
  {
    if ((parametersMap.containsKey("-xml")) && (!parametersMap.containsKey("-img"))) {
      printHelp("\n\"-xml\" option can be used only with \"-img\".");
    }
    if ((parametersMap.containsKey("-xmlDir")) && (!parametersMap.containsKey("-imgDir"))) {
      printHelp("\n\"-xmlDir\" option can be used only with \"-imgDir\".");
    }
  }
  



  public void printHelp(String specificMessage)
  {
    if (specificMessage != null) {
      System.out.println(specificMessage + "\n\n");
      JOptionPane.showMessageDialog(null, specificMessage + "\n\n", 
        "Error", 0);
    }
    
    System.out.println("\nUsage: GEDI_Runnable.jar [-option <value>]\n\nwhere options include:\n-listener           the flag to load the following parameters via\n                    GEDI Network Listener\n-img <path>         load the image\n-xml <path>         load the specified XML for the image\n-imgDir <dir path>  load the directory with images\n-xmlDir <dir path>  load the specified data directory for the images\n-zoneID <a number>  highlight the zone with this given ID\n-config <path>      load the specified configuration file (GEDIConfig.xml)\n-props <path>      load specified properties file (GEDI.properties)\n-rootPath <path>      set the specified root path \n-help               print out this help");
    































    System.exit(0);
  }
  


  public void processOptionsViaNetworkListener()
  {
    String hostname = "http://localhost:" + getPort() + "/";
    
    if (parametersMap.containsKey("-rootPath")) {
      String rootPath = (String)parametersMap.get("-rootPath");
      String urlStr = hostname + "setrootpath?rootPath=" + rootPath;
      connectToNetworkListener(urlStr);
    }
    
    if (parametersMap.containsKey("-imgDir")) {
      String imageDir = (String)parametersMap.get("-imgDir");
      String xmlDir = (String)parametersMap.get("-xmlDir");
      String urlStr = hostname + "openimagedir?imgDirPath=" + imageDir;
      if (xmlDir != null) {
        urlStr = urlStr + "&xmlDirPath=" + xmlDir;
      }
      connectToNetworkListener(urlStr);

    }
    else if (parametersMap.containsKey("-img")) {
      String imagePath = (String)parametersMap.get("-img");
      String xmlPath = (String)parametersMap.get("-xml");
      String zoneID = (String)parametersMap.get("-zoneID");
      String urlStr = hostname + "openimage?imgPath=" + imagePath;
      if (xmlPath != null)
        urlStr = urlStr + "&xmlPath=" + xmlPath;
      if (zoneID != null) {
        urlStr = urlStr + "&zoneID=" + zoneID;
      }
      connectToNetworkListener(urlStr);
    }
    
    if (parametersMap.containsKey("-config")) {
      String configDir = (String)parametersMap.get("-config");
      String urlStr = hostname + "openconfig?configPath=" + configDir;
      connectToNetworkListener(urlStr);
    }
  }
  










  private void connectToNetworkListener(String urlStr)
  {
    System.out.println("I am passing parameters to the running NetworkListener of another open GEDI instance.\n");
    

    System.out.println("URL: " + urlStr);
    try {
      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      
      BufferedReader bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = bufReader.readLine()) != null) { String line;
        System.out.println("line: " + line);
      }
      
      bufReader.close();
      isNetworkListenerRunning = true;
    }
    catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ConnectException e) {
      System.out.println("Network Listener is not running !!!!\nNew GEDI instance will be launched.");
      
      isNetworkListenerRunning = false;
    } catch (IOException e) {
      e.printStackTrace();
      isNetworkListenerRunning = false;
    }
  }
  
  public String getRootPath()
  {
    if (parametersMap.get("-rootPath") == null) {
      return "";
    }
    return (String)parametersMap.get("-rootPath");
  }
  













  public String getImageDirPath()
  {
    if ((!parametersMap.containsKey("-imgDir")) && (!parametersMap.containsKey("-img"))) {
      return null;
    }
    if (parametersMap.containsKey("-img")) {
      return new File((String)parametersMap.get("-img")).getParent() + File.separator;
    }
    return new File((String)parametersMap.get("-imgDir")).getParent() + File.separator;
  }
  


  public String getImageDirName()
  {
    if ((!parametersMap.containsKey("-img")) && (!parametersMap.containsKey("-imgDir"))) {
      return null;
    }
    if (parametersMap.containsKey("-img")) {
      return new File((String)parametersMap.get("-img")).getName();
    }
    return new File((String)parametersMap.get("-imgDir")).getName();
  }
  


  public String getXmlDirPath()
  {
    if ((!parametersMap.containsKey("-xml")) && 
      (!parametersMap.containsKey("-xmlDir")) && 
      (!parametersMap.containsKey("-img")) && 
      (!parametersMap.containsKey("-imgDir"))) {
      return null;
    }
    if (parametersMap.containsKey("-xml"))
      return new File((String)parametersMap.get("-xml")).getParentFile().getParent() + File.separator;
    if (parametersMap.containsKey("-xmlDir"))
      return new File((String)parametersMap.get("-xmlDir")).getParent() + File.separator;
    if (parametersMap.containsKey("-img"))
      return new File((String)parametersMap.get("-img")).getParentFile().getParent() + File.separator;
    if (parametersMap.containsKey("-imgDir")) {
      return new File((String)parametersMap.get("-imgDir")).getParent() + File.separator;
    }
    return null;
  }
  


  public String getXmlDirName()
  {
    if ((!parametersMap.containsKey("-xml")) && 
      (!parametersMap.containsKey("-xmlDir")) && 
      (!parametersMap.containsKey("-img")) && 
      (!parametersMap.containsKey("-imgDir"))) {
      return null;
    }
    if (parametersMap.containsKey("-xml"))
      return new File((String)parametersMap.get("-xml")).getParentFile().getName();
    if (parametersMap.containsKey("-xmlDir"))
      return new File((String)parametersMap.get("-xmlDir")).getName();
    if (parametersMap.containsKey("-img"))
      return new File((String)parametersMap.get("-img")).getParentFile().getName();
    if (parametersMap.containsKey("-imgDir")) {
      return new File((String)parametersMap.get("-imgDir")).getName();
    }
    return null;
  }
  
  public String getConfigFilePath() {
    return (String)parametersMap.get("-config");
  }
  


  public String getZoneID()
  {
    return (String)parametersMap.get("-zoneID");
  }
  



  private void selectErrorStream()
  {
    if (!new File(OCRInterface.log_path).exists()) {
      new File(OCRInterface.log_path).mkdirs();
    }
    String filename = OCRInterface.log_path + "paramParsing-log.txt";
    try {
      logFile = new FileOutputStream(filename, false);
      

      PrintStream out = new PrintStream(logFile);
      
      PrintStream tee = new OutputStream(System.out, out);
      
      tee.append("\n");
      tee.append("GEDI: Groundtruthing Environment for Document Images\n");
      tee.append("Copyright(C) 2005-2009 University of Maryland, College Park");
      tee.append("\n\n");
      
      System.setOut(tee);
      

      PrintStream err = new PrintStream(logFile);
      tee = new OutputStream(System.err, err);
      
      System.setErr(tee);
    } catch (FileNotFoundException e) {
      System.out.println("Could not write to file: " + filename);
    }
  }
  
  private void closeLogFile() {
    if (logFile == null)
      return;
    try {
      logFile.close();
    } catch (IOException e) {
      System.out.println("OptionParser. Could not close the log file.");
      e.printStackTrace();
    }
  }
  





  private int getPort()
  {
    int defaultPort = 8000;
    Properties properties = loadGEDIProps();
    if (properties == null) {
      return defaultPort;
    }
    String listenerPort = properties.getProperty("listenerPort");
    if (listenerPort == null) {
      defaultPort = Integer.parseInt(listenerPort);
    }
    return defaultPort;
  }
  
  private Properties loadGEDIProps() {
    String GEDIPropsPath = null;
    GEDIPropsPath = getPropsFilePath();
    if (GEDIPropsPath == null) {
      LoadGEDIProps.getInstance();
      GEDIPropsPath = LoadGEDIProps.propsPath;
    }
    


    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(GEDIPropsPath));
      return properties;
    } catch (IOException e) {}
    return null;
  }
  
  public boolean isNetworkListenerRunning()
  {
    return isNetworkListenerRunning;
  }
  
  public boolean isNetworkListenerRequired() {
    return networkListenerRequired;
  }
  
  public String getPropsFilePath() {
    return (String)parametersMap.get("-props");
  }
}
