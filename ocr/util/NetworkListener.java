package ocr.util;

import gttool.misc.TypeAttributeEntry;
import gttool.misc.TypeSettings;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import ocr.gui.LoadDataFile;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.GlobalHotkeyManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.comments.CommentsParser;






public class NetworkListener
  extends Thread
{
  private ServerSocket serverSocket = null;
  private Socket clientSocket = null;
  
  private int port;
  
  private PrintWriter out;
  
  private BufferedReader in;
  
  private String responseToBrowser = "HTTP/1.0 204 No Content \r\n";
  private String characterEncoding = "UTF-8";
  
  private UniqueZoneId uniqueZoneId;
  private static OCRInterface thisInterface = OCRInterface.this_interface;
  private String fileSeparator = System.getProperty("file.separator");
  
  private boolean threadDone;
  
  private boolean isPortUsed;
  
  public static final String OPEN_IMAGE = "openimage";
  
  public static final String OPEN_XML = "openxml";
  
  public static final String OPEN_CONFIG = "openconfig";
  
  public static final String OPEN_LOG = "openlog";
  
  public static final String WRITE_LOG = "writelog";
  
  public static final String WRITE_MSG = "writemsg";
  
  public static final String REQUEST_COMMENT = "requestcomment";
  
  public static final String IMG_PATH = "imgPath";
  
  public static final String XML_PATH = "xmlPath";
  
  public static final String ZONE_ID = "zoneID";
  public static final String COMMENT = "comment";
  public static final String CONFIG_PATH = "configPath";
  public static final String PAGE_NUM = "pageNum";
  public static final String LOG_PATH = "logPath";
  public static final String OPEN_IMAGE_DIR = "openimagedir";
  public static final String IMG_DIR_PATH = "imgDirPath";
  public static final String XML_DIR_PATH = "xmlDirPath";
  public static final String SET_ROOT_PATH = "setrootpath";
  public static final String ROOT_PATH = "rootPath";
  public static final String SET_ATTRIBUTE = "setattribute";
  public static final String ATTR_NAME = "attrName";
  public static final String ATTR_VALUE = "attrValue";
  
  protected class NetworkListenerException
    extends Exception
  {
    private static final long serialVersionUID = 1L;
    
    public NetworkListenerException() {}
    
    public NetworkListenerException(String msg)
    {
      super();
    }
  }
  
  protected class NetworkListenerFileNotFoundException extends NetworkListener.NetworkListenerException {
    private static final long serialVersionUID = 1L;
    
    public NetworkListenerFileNotFoundException(String msg) { super(msg); }
  }
  
  protected class NetworkListenerZoneNotFoundException
    extends NetworkListener.NetworkListenerException {
    private static final long serialVersionUID = 1L;
    
    public NetworkListenerZoneNotFoundException(String msg) { super(msg); }
    
    public NetworkListenerZoneNotFoundException() { super(); }
  }
  
  protected class NetworkListenerSetAttributeException extends NetworkListener.NetworkListenerException {
    private static final long serialVersionUID = 1L;
    
    public NetworkListenerSetAttributeException(String msg) { super(msg); }
    
    public NetworkListenerSetAttributeException() { super(); }
  }
  
  protected class NetworkListenerInvalidFileException extends NetworkListener.NetworkListenerException {
    private static final long serialVersionUID = 1L;
    
    public NetworkListenerInvalidFileException(String msg) { super(msg); }
    
    public NetworkListenerInvalidFileException() { super(); }
  }
  



  public NetworkListener()
  {
    threadDone = false;
    isPortUsed = false;
  }
  
  public NetworkListener(int port)
  {
    this.port = port;
    threadDone = false;
    isPortUsed = false;
  }
  
  public void run() {
    port = thisInterface.getListenerPort();
    while (!threadDone) {
      try {
        serverSocket = new ServerSocket(port);
        System.out.println("Network Listener is running on port: " + port);
      } catch (IOException e) {
        System.err.println("Network Listener. Could not listen on port: " + port);
        isPortUsed = true;
        return;
      }
      
      try
      {
        do
        {
          clientSocket = serverSocket.accept();
          out = new PrintWriter(clientSocket.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          String inputLine = in.readLine();
          System.out.println("passing string to NL: " + inputLine);
          try {
            processInputString(inputLine);
            out.print(responseToBrowser);
            out.flush();
            closeInOut();
          } catch (NetworkListenerFileNotFoundException e) {
            out.print("HTTP/1.1 410 Gone\r\n");
            out.flush();
            closeInOut();
            String title = "HTTP Request";
            thisInterface.setAlwaysOnTop(true);
            thisInterface.setAlwaysOnTop(false);
            JOptionPane.showMessageDialog(null, e.getMessage(), 
              title, 2);
          } catch (NetworkListenerZoneNotFoundException e) {
            out.print("HTTP/1.1 404 Not Found\r\n");
            out.flush();
            closeInOut();
          } catch (NetworkListenerSetAttributeException e) {
            out.print("HTTP/1.1 404 Not Found\r\n");
            out.flush();
            closeInOut();
            thisInterface.setAlwaysOnTop(true);
            thisInterface.setAlwaysOnTop(false);
            JOptionPane.showMessageDialog(null, e.getMessage(), 
              "Set attribute failed", 2);
          } catch (NetworkListenerInvalidFileException e) {
            out.print("HTTP/1.1 403 Forbidden\r\n");
            out.flush();
            closeInOut();
            thisInterface.setAlwaysOnTop(true);
            thisInterface.setAlwaysOnTop(false);
            System.out.println("NetworkListener. Invalid File Format. Image cannot be loaded");
            JOptionPane.showMessageDialog(null, e.getMessage(), 
              "Invalid File", 0);
          } catch (NetworkListenerException e) {
            out.print("HTTP/1.1 400 Bad Request\r\n");
            out.flush();
            closeInOut();
            System.out.println("Network Listener. ProcessInputString failed.");
            threadDone = false;
            thisInterface.setAlwaysOnTop(true);
            thisInterface.setAlwaysOnTop(false);
            String msg = "HTTP request cannot be processed. Check the string you are passing.\n\nUse UTF-8 encoding.\nTwo valid formats are:\nhttp://host:port/image/zoneID\nhttp://host:port/operation?param1=value1&param2=value2&...";
            



            JOptionPane.showMessageDialog(null, msg, 
              "HTTP request failed", 2);
          }
        } while (!threadDone);







































      }
      catch (IOException e)
      {







































        System.err.println("Network Listener. Accept failed.");
      } finally {
        closeInOut();
      }
    }
  }
  
  public void processInputString(String inputStr)
    throws NetworkListener.NetworkListenerException
  {
    String decodedStr = null;
    


    String rootPath = thisInterface.getRootPath().trim();
    
    if (!rootPath.equals("")) {
      if ((rootPath.charAt(rootPath.length() - 1) != '\\') && 
        (rootPath.charAt(rootPath.length() - 1) != '/')) {
        rootPath = rootPath + fileSeparator;
      }
      rootPath = rootPath.replace("/", fileSeparator);
      rootPath = rootPath.replace("\\", fileSeparator);
    }
    else {
      rootPath = null;
    }
    

    try
    {
      decodedStr = URLDecoder.decode(inputStr, characterEncoding);
      
      decodedStr = decodedStr.replace("/", fileSeparator);
      decodedStr = decodedStr.replace("\\", fileSeparator);
    } catch (UnsupportedEncodingException e) {
      throw new NetworkListenerException();
    }
    
    String command = decodedStr.substring(decodedStr.indexOf(fileSeparator) + 1, decodedStr.lastIndexOf("HTTP")).trim();
    











    if (command.matches("^[A-Za-z0-9_]+\\?((([A-Za-z0-9_]+=[\\x20-\\x7E]+)&)*([A-Za-z0-9_]+=[\\x20-\\x7E]+))?$"))
    {
      String[] commandSplit = command.split("\\?");
      String operation = commandSplit[0];
      
      HashMap<String, String> paraMap = new HashMap();
      
      if (commandSplit.length > 1) {
        String[] parameters = commandSplit[1].split("&");
        
        for (int i = 0; i < parameters.length; i++) {
          String[] thisParam = parameters[i].split("=");
          paraMap.put(thisParam[0], thisParam[1]);
        }
      }
      
      if (operation.equals("setrootpath")) {
        String rootPathIn = (String)paraMap.get("rootPath");
        thisInterface.setRootPath(rootPathIn.trim());
      }
      else if (operation.equals("openimagedir")) {
        String imageDirPath = (String)paraMap.get("imgDirPath");
        String xmlDirPath = (String)paraMap.get("xmlDirPath");
        if (rootPath != null) {
          if (!new File(imageDirPath).isAbsolute())
            imageDirPath = rootPath + imageDirPath;
          if ((xmlDirPath != null) && (!new File(xmlDirPath).isAbsolute()))
            xmlDirPath = rootPath + xmlDirPath;
        }
        openFile(imageDirPath, null, xmlDirPath);
      }
      else if (operation.equals("openimage"))
      {
        openImageAction(paraMap, rootPath);















      }
      else if (operation.equals("openxml"))
      {
        String xmlPath = (String)paraMap.get("xmlPath");
        if ((rootPath != null) && (!new File(xmlPath).isAbsolute())) {
          xmlPath = rootPath + xmlPath;
        }
        thisInterface.setAlwaysOnTop(true);
        thisInterface.setAlwaysOnTop(false);
        if (!new File(xmlPath).exists()) {
          String msg = "Requested file:\n<HTML> <FONT COLOR=Blue>" + 
            xmlPath + 
            "</FONT></HTML>\n doesn't exist.\n\n" + 
            "You may need to set up the root path in the Preferences or " + 
            "check the path to the image in the HTTP request.\n" + 
            "Please note if you provide absolute image path in the HTTP request, the root path must be empty.";
          throw new NetworkListenerFileNotFoundException(msg);
        }
        openNotepad(xmlPath);
      } else if (operation.equals("openconfig")) {
        bringMainScreenToFront();
        OCRInterface.this_interface.loadConfigFile((String)paraMap.get("configPath"));
        OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();


















      }
      else if (operation.equals("writelog")) {
        System.out.println((String)paraMap.get("comment"));
      } else if (operation.equals("writemsg")) {
        CommentsParser parser = new CommentsParser();
        
        if (paraMap.get("zoneID") != null)
          selectZone((String)paraMap.get("zoneID"));
        parser.insertComment((String)paraMap.get("comment"));
        bringMainScreenToFront();
        OCRInterface.this_interface.updateCommentsWindow(true);
      } else if (operation.equals("requestcomment")) {
        String imagePath = (String)paraMap.get("imgPath");
        String xmlPath = (String)paraMap.get("xmlPath");
        if (imagePath != null) {
          if (rootPath != null) {
            if (!new File(imagePath).isAbsolute())
              imagePath = rootPath + imagePath;
            if ((xmlPath != null) && (!new File(xmlPath).isAbsolute()))
              xmlPath = rootPath + xmlPath;
          }
        } else {
          imagePath = ImageReaderDrawer.file_path;
        }
        openFile(imagePath, (String)paraMap.get("zoneID"), xmlPath);
        new CommentsParser();
        bringMainScreenToFront();
      } else if (operation.equalsIgnoreCase("setattribute")) {
        String imagePath = (String)paraMap.get("imgPath");
        
        if (imagePath != null) {
          openImageAction(paraMap, rootPath);
        }
        String attrName = (String)paraMap.get("attrName");
        String attrValue = (String)paraMap.get("attrValue");
        if (ImageDisplay.activeZones.isEmpty()) {
          String zoneID_toSelect = (String)paraMap.get("zoneID");
          String msg = null;
          if (zoneID_toSelect != null) {
            if (OCRInterface.currDoc == null) {
              msg = "Select a document.";
              throw new NetworkListenerSetAttributeException(msg);
            }
            selectZone((String)paraMap.get("zoneID"));
          }
          else {
            msg = "No selected zone.\n\nSelect a zone of interest and try again.\nOr provide zoneID in the string you are passing as:\n\nhttp://localhost:8000/setattribute?attrName=someName&attrValue=someValue&zoneID=someID\n\nNote: the following is valid syntax:\nhttp://host:port/setattribute?attrName=required&attrValue=required&zoneID=optional&imgPath=optional&xmlPath=optional";
            




            throw new NetworkListenerSetAttributeException(msg);
          }
        }
        
        ImageDisplay.ZoneVector selectedZone = ImageDisplay.activeZones;
        for (Zone zone : selectedZone)
        {
          Map<String, Map<String, TypeAttributeEntry>> ts = new TypeSettings(
            thisInterfacetbdPane.data_panel.t_window.getSaveableTypeSettings(), 
            OCRInterface.getAttsConfigUtil().getTypeAttributesMap()).toMap();
          
          String zoneType = zone.getAttributeValue("gedi_type");
          
          if (ts.containsKey(zoneType)) {
            if (((Map)ts.get(zoneType)).containsKey(attrName)) {
              bringMainScreenToFront();
              zone.setAttributeValue(attrName, attrValue);
              thisInterfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
            }
            else {
              String msg = "'" + attrName + "' attribute doesn't exist in the current configuration." + 
                "\nCheck the string you are passing.";
              throw new NetworkListenerSetAttributeException(msg);
            }
          }
        }
      }
      else
      {
        System.out.println("Network Listener. ProcessInputString failed.  Invalid command.");
        throw new NetworkListenerException();
      }
    }
    else if (command.matches("^[\\x20-\\x7E]*\\.(tif|TIF|tiff|TIFF|jpg|JPG|jpeg|JPEG|gif|GIF|bmp|BMP)(\\" + fileSeparator + "[A-Za-z0-9_]*)?$"))
    {

      int zoneIdStart = command.lastIndexOf(fileSeparator);
      String imagePath = command.substring(0, zoneIdStart).trim();
      String zoneID = command.substring(zoneIdStart + 1, command.length()).trim();
      
      if ((rootPath != null) && (!new File(imagePath).isAbsolute())) {
        imagePath = rootPath + imagePath;
      }
      System.out.println("absolute path-->>zoneID: " + imagePath + "-->>" + zoneID);
      
      openFile(imagePath, zoneID, imagePath);
    } else {
      System.out.println("Network Listener. ProcessInputString failed. Invalid format.");
      throw new NetworkListenerException();
    }
  }
  
  private void openImageAction(HashMap<String, String> paraMap, String rootPath) throws NetworkListener.NetworkListenerException {
    String imagePath = (String)paraMap.get("imgPath");
    String xmlPath = (String)paraMap.get("xmlPath");
    String pageNum = (String)paraMap.get("pageNum");
    String zoneID = (String)paraMap.get("zoneID");
    
    if (rootPath != null) {
      if (!new File(imagePath).isAbsolute())
        imagePath = rootPath + imagePath;
      if ((xmlPath != null) && (!new File(xmlPath).isAbsolute())) {
        xmlPath = rootPath + xmlPath;
      }
    }
    



    openFile(imagePath, zoneID, xmlPath);
    if ((pageNum != null) && (zoneID == null)) {
      OCRInterface.this_interface.getMultiPagePanel().selectPage(Integer.parseInt(pageNum));
    }
  }
  
  private void openFile(String imgPath, String zoneID, String xmlPath) throws NetworkListener.NetworkListenerException {
    if ((imgPath.contains(".xml")) || (imgPath.contains(".XML"))) {
      openNotepad(imgPath);
      return;
    }
    
    System.out.println("imgPath: " + imgPath);
    System.out.println("xmlPath: " + xmlPath);
    


    File imgFile = new File(imgPath);
    try {
      imgFile = imgFile.getCanonicalFile();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    
    File xmlFile = null;
    if (xmlPath != null)
    {
      xmlFile = new File(xmlPath);
      try {
        xmlFile = xmlFile.getCanonicalFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    if ((!imgFile.exists()) || ((xmlFile != null) && (!xmlFile.exists()))) {
      String msg_file = !imgFile.exists() ? imgFile.getAbsolutePath().trim() : xmlFile.getAbsolutePath().trim();
      String msg = "Requested file:\n<HTML> <FONT COLOR=Blue>" + 
        msg_file + 
        "</FONT></HTML>\n doesn't exist.\n\n" + 
        "You may need to set up the root path in the Preferences or " + 
        "check the path to the image in the HTTP request.\n" + 
        "Please note if you provide absolute image path in the HTTP request, the root path must be empty.";
      throw new NetworkListenerFileNotFoundException(msg);
    }
    bringMainScreenToFront();
    

    if (!isFileAlreadyOpened(imgPath, xmlPath)) {
      System.out.println("isFileAlreadyOpened: false");
      thisInterface.imageDirLoadFromNetworkListener(imgFile, xmlFile);
      



      if (zoneID != null) {
        try {
          Thread.sleep(500L);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    


    if (OCRInterface.currDoc == null) {
      GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
      hotkeyManager.setEnabled(true);
      return;
    }
    
    if (zoneID != null) {
      selectZone(zoneID);
    } else {
      OCRInterface.this_interface.getCanvas().clearActiveZones();
    }
    
    bringMainScreenToFront();
  }
  

  private void openNotepad(String pathToXmlFile)
  {
    String vendor = System.getProperty("os.name");
    System.out.println(vendor);
    System.out.println("\nCalling notepad.exe \"" + pathToXmlFile + "\"");
    try {
      if ((OCRInterface.currDoc != null) && (currDocisModified)) {
        OCRInterface.currDoc.dumpData();
      }
      Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + 
        "notepad.exe \"" + pathToXmlFile + "\"");
    } catch (IOException ioe) {
      System.out.println("ERROR: Could not call notepad.exe");
    }
  }
  
  public void kill() {
    try {
      if (serverSocket != null)
        serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      if (clientSocket != null)
        clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void closeInOut() {
    try {
      if (in != null)
        in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (out != null)
      out.close();
  }
  
  private boolean isFileAlreadyOpened(String imgPath, String xmlPath) {
    if (OCRInterface.currDoc == null) {
      return false;
    }
    String currentFile = ImageReaderDrawer.file_path;
    



    String currentXml = OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName() + fileSeparator + this_interfaceocrTable.selFileName + ".xml";
    
    System.out.println("currentFile: " + currentFile);
    System.out.println("currentXml: " + currentXml);
    if ((currentFile.equalsIgnoreCase(imgPath)) && ((xmlPath == null) || (currentXml.equalsIgnoreCase(xmlPath)))) {
      return true;
    }
    return false;
  }
  
  public void threadDone(boolean done) {
    threadDone = done;
    closeInOut();
    kill();
  }
  
  public boolean isPortUsed() {
    return isPortUsed;
  }
  
  private void bringMainScreenToFront()
  {
    OCRInterface.this_interface.setState(0);
    OCRInterface.this_interface.setAlwaysOnTop(true);
    OCRInterface.this_interface.setAlwaysOnTop(false);
  }
  
  private void selectZone(String zoneID) throws NetworkListener.NetworkListenerZoneNotFoundException {
    uniqueZoneId = thisInterface.getUniqueZoneIdObj();
    String msg = uniqueZoneId.searchZone(true, zoneID);
    if (msg != null)
      throw new NetworkListenerZoneNotFoundException(msg);
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.setEnabled(true);
  }
}
