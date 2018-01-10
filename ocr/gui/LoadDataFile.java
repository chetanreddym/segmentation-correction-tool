package ocr.gui;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.io.DLDocumentXmlParser;
import gttool.io.DLDocumentXmlTransformer;
import gttool.io.XmlConstant;
import gttool.misc.Attribute;
import gttool.misc.Login;
import gttool.misc.TypeSettings;
import gttool.misc.WaitDialog;
import java.awt.Cursor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JOptionPane;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.manager.zones.ZonesManager;
import ocr.tag.GetDateTime;
import ocr.tif.ImageReaderDrawer;
import ocr.util.CapabilitiesControl;
import org.xml.sax.SAXException;


















public class LoadDataFile
{
  private String path = null;
  



  private String filename = null;
  



  private DLDocument document = null;
  


  public boolean isModified;
  

  private Map<String, Attribute> attributeSettings = null;
  
  private TypeSettings typeSettings = null;
  
  public LoadDataFile() {
    init(null, 0, 0, false);
  }
  






  public LoadDataFile(String f_path)
  {
    init(f_path, 0, 0, true);
  }
  
  public LoadDataFile(String f_path, boolean load) {
    init(path, 0, 0, load);
  }
  












  public LoadDataFile(String f_path, int f_type, int mode)
  {
    init(f_path, f_type, mode, true);
  }
  
  public LoadDataFile(String f_path, int f_type, int mode, boolean load) {
    init(f_path, f_type, mode, load);
  }
  






  public static LoadDataFile loadWorkspace(String f_path)
  {
    LoadDataFile ret = new LoadDataFile(f_path, false);
    



    File file = new File(f_path);
    if (file.exists()) {
      ret.loadFile(file);
    } else {
      return null;
    }
    document = null;
    
    return ret;
  }
  
  private void init(String filepath, int filetype, int mode, boolean load)
  {
    if ((filepath != null) && (filepath.length() > 0)) {
      path = filepath;
      File file = new File(path);
      filename = file.getName();
      
      if (load)
      {
        if ((file.exists()) && (file.isFile())) {
          loadFile(file);
        } else {
          System.out.println("[LoadDataFile: File: " + filepath + 
            " doesn't exist.");
          

          document = new DLDocument();
          try {
            document.dlAppendPage(new DLPage());
          }
          catch (Exception localException) {}
        }
      }
    }
    
    isModified = false;
  }
  







  public String loadFile(File file)
  {
    document = null;
    attributeSettings = null;
    

    DLDocumentXmlParser xmlParser = new DLDocumentXmlParser();
    try
    {
      xmlParser.parse(file.getAbsolutePath());
      
      document = xmlParser.getDocument();
      
      typeSettings = xmlParser.getTypeSettings();
      

      attributeSettings = xmlParser.getAttributeSettings();

    }
    catch (Exception e)
    {
      document = null;
      typeSettings = null;
      attributeSettings = null;
      
      String errorMessage = file.getAbsolutePath() + "\n\n" + e.getMessage();
      
      System.out.println(errorMessage);
      
      WaitDialog.hideDialog();
      
      isModified = false;
      
      String str1 = errorMessage;return str1;
    }
    finally {
      WaitDialog.hideDialog();
      OCRInterface.this_interface
        .setCursor(Cursor.getPredefinedCursor(0));
    }
    
    isModified = false;
    
    return null;
  }
  
  public String isFileValid(File file)
  {
    DLDocumentXmlParser xmlParser = new DLDocumentXmlParser();
    try
    {
      xmlParser.parse(file.getAbsolutePath());
      if (xmlParser.getDocument() == null)
        return 
          "Invalid XML.\nCheck the file format:\n" + file;
    } catch (Exception e) {
      e.printStackTrace();
      return file.getAbsolutePath() + "\n\n" + e.getMessage();
    }
    
    return null;
  }
  



  private void unRotateDoc()
  {
    if (OCRInterface.currDoc != null) {
      Iterator<DLPage> pageItr = currDocgetDocumentdocumentPages.iterator();
      while (pageItr.hasNext()) {
        DLPage tempPage = (DLPage)pageItr.next();
        String pageOrientStr = (String)pageTags.get("GEDI_orientation");
        if (pageOrientStr != null) {
          int rotations = (4 - Integer.parseInt(pageOrientStr) / 90) % 4;
          for (int i = 0; i < rotations; i++)
            tempPage.rotate();
        }
      }
    } else {
      int temp = (4 - OCRInterface.this_interface.getCurrentRotateDegrees() / 90) % 4;
      for (int i = 0; i < temp; i++) {
        this_interfacetbdPane.data_panel.a_window.getPage().rotate();
      }
    }
  }
  


  private void reRotateDoc()
  {
    if (OCRInterface.currDoc != null) {
      Iterator<DLPage> pageItr = currDocgetDocumentdocumentPages.iterator();
      while (pageItr.hasNext()) {
        DLPage tempPage = (DLPage)pageItr.next();
        String pageOrientStr = (String)pageTags.get("GEDI_orientation");
        if (pageOrientStr != null) {
          int rotations = Integer.parseInt(pageOrientStr) / 90;
          for (int i = 0; i < rotations; i++)
            tempPage.rotate();
        }
      }
    } else {
      int temp = OCRInterface.this_interface.getCurrentRotateDegrees();
      for (int i = 0; i < temp; i++) {
        this_interfacetbdPane.data_panel.a_window.getPage().rotate();
      }
    }
  }
  


  public void dumpData()
  {
    unRotateDoc();
    dump(document, null, null);
    reRotateDoc();
  }
  
  public void dumpDataAs(String newFilepath) {
    dumpAs(document, null, null, newFilepath);
  }
  
  public void dumpDataAndWorkspace() {
    dump(document, typeSettings, attributeSettings);
  }
  
  public void dumpDataAndWorkspaceAs(String newFilepath)
  {
    dumpAs(document, typeSettings, attributeSettings, 
      newFilepath);
  }
  
  public void dumpWorkspace() {
    dump(null, typeSettings, attributeSettings);
  }
  
  public void dumpWorksapceAs(String filepath) {
    dump(null, typeSettings, attributeSettings, filepath);
  }
  


  public static void dumpWorkspace(TypeSettings typeSettings, Map<String, Attribute> attributeSettings, String filePath)
  {
    dump(null, typeSettings, attributeSettings, filePath);
  }
  

  private void dump(DLDocument doc, TypeSettings typeSettings, Map<String, Attribute> attributeSettings)
  {
    dump(doc, typeSettings, attributeSettings, path);
    setModified(false);
  }
  
  private void dumpAs(DLDocument doc, TypeSettings typeSettings, Map<String, Attribute> attributeSettings, String newFilepath)
  {
    path = newFilepath;
    filename = new File(path).getName();
    
    dump(doc, typeSettings, attributeSettings, path);
    setModified(false);
  }
  
  private static void dump(DLDocument doc, TypeSettings typeSettings, Map<String, Attribute> attributeSettings, String filePath)
  {
    if (!CapabilitiesControl.isEnableEditing()) {
      String msg = "This is a demo version.\nYour changes can not be saved.";
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, 
        "Error writing to disk", 
        1);
      return;
    }
    
    DLDocumentXmlTransformer xmlTransformer = new DLDocumentXmlTransformer();
    Cursor cursor = null;
    
    if (OCRInterface.this_interface != null) {
      cursor = OCRInterface.this_interface.getCursor();
      OCRInterface.this_interface.setCursor(
        Cursor.getPredefinedCursor(3));
    }
    
    try
    {
      xmlTransformer.writeDocument(doc, typeSettings, attributeSettings, 
        filePath);
      OCRInterface.this_interface.setAllowToSwitchImage(true);
    } catch (IOException ioe) {
      System.out.println("dump data. IOException");
      ioe.printStackTrace();
      System.out.println("Error opening output file " + ioe.toString() + 
        "[LoadDataFile:397]");
      File temp = new File(filePath);
      











      String msg = temp.getAbsolutePath() + "\n\n" + 
        "WARNING: Unable to save XML -- " + 
        "either it is a read-only file or file path is incorrect.\n\n" + 
        "Problem description:\n" + ioe.toString();
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, 
        "Error writing to disk", 
        0);
      OCRInterface.this_interface.setAllowToSwitchImage(false);

    }
    catch (SAXException e)
    {
      System.out.println("dump data. IOException");
      e.printStackTrace();
      File temp = new File(filePath);
      String msg = temp.getAbsolutePath() + "\n\n" + 
        "WARNING: Unable to save XML -- " + 
        "either it is a read-only file or file path is incorrect.\n\n" + 
        "Problem description:\n" + e.toString();
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, 
        "Error writing to disk", 
        0);
      OCRInterface.this_interface.setAllowToSwitchImage(false);
      System.out.println("Error writing output: " + e.getMessage());
    }
    finally {
      if ((OCRInterface.this_interface != null) && (cursor != null)) {
        OCRInterface.this_interface.setCursor(new Cursor(0));
      }
    }
  }
  

  public DLDocument getDocument()
  {
    return document;
  }
  
  public void setDocument(DLDocument document) {
    this.document = document;
  }
  
  public Map<String, Object[]> getTypeWindow() {
    if (typeSettings != null) {
      return typeSettings.toTypeWindow();
    }
    return null;
  }
  
  public TypeSettings getTypeSettings() {
    return typeSettings;
  }
  
  public void setTypeSettings(TypeSettings typeSettings) {
    this.typeSettings = typeSettings;
  }
  
  public Map<String, Attribute> getAttributeSettings() {
    return attributeSettings;
  }
  
  public void setAttributeSettings(Map<String, Attribute> attributeSettings) {
    this.attributeSettings = attributeSettings;
  }
  
  public void attributeSettingsPutAll(Map<String, Attribute> attributeSettings) {
    this.attributeSettings.putAll(attributeSettings);
  }
  







  public ZonesManager getZonesManager(int pageNum)
  {
    if (document == null) {
      return null;
    }
    return new ZonesManager((DLPage)document.documentPages.get(pageNum));
  }
  



  public String getFilePath()
  {
    return path;
  }
  



  public String getFilePathOnly()
  {
    return path.replace(filename, "");
  }
  
  public void setFilePath(String path) {
    this.path = path;
    filename = new File(path).getName();
  }
  




  public String getFileName()
  {
    return filename;
  }
  


  public String toString()
  {
    String returnVal = "";
    returnVal = returnVal + "path=" + path + " filename=" + filename + "#";
    return returnVal;
  }
  



  public void setModified(boolean isModified)
  {
    this.isModified = isModified;
    OCRInterface.this_interface.updateCurrFilename();
  }
  


  public boolean isModified()
  {
    return isModified;
  }
  
  public static void createNewMultiPageDocument(String file_name, int numPages) throws SAXException, IOException
  {
    new File(file_name).getAbsoluteFile().createNewFile();
    
    String ENCODING = "UTF-8";
    
    String imageName = ImageReaderDrawer.getFile_name();
    
    PrintWriter out = new PrintWriter(new FileOutputStream(file_name));
    
    out.println("<?xml version=\"1.0\" encoding=\"" + 
      ENCODING + 
      "\"?>");
    out.println("<!--GEDI was developed at Language and Media Processing Laboratory, University of Maryland. -->");
    out.println("<GEDI xmlns=\"http://lamp.cfar.umd.edu/media/projects/GEDI/\" GEDI_version=\"" + 
    
      XmlConstant.VALUE_GEDI_VERSION + 
      "\" " + "GEDI_date" + 
      "=\"" + XmlConstant.VALUE_GEDI_DATE + "\">");
    
    addUserXMLTag(out);
    
    DLPage currentPage = 
      this_interfacetbdPane.data_panel.a_window.getPage();
    String pageWidth = Integer.toString(currentPage.dlGetWidth());
    String pageHeight = Integer.toString(currentPage.dlGetHeight());
    String pageOrientation = (String)pageTags.get("GEDI_orientation");
    
    out.println("\t<DL_DOCUMENT src=\"" + 
      imageName + 
      "\" NrOfPages=\"" + numPages + "\" docTag=\"xml\">");
    for (int i = 1; i <= numPages; i++) {
      out.println("\t\t<DL_PAGE gedi_type=\"DL_PAGE\" src=\"" + 
      

        imageName + "\" " + "pageID" + 
        "=\"" + i + "\" " + "width" + "=\"" + pageWidth + "\" " + 
        "height" + "=\"" + pageHeight + "\"");
      if (pageOrientation != null)
        out.println(" GEDI_orientation=\"" + pageOrientation + "\"");
      out.println(">");
      out.println("\t\t</DL_PAGE>");
    }
    out.println("\t</DL_DOCUMENT>");
    out.println("</GEDI>");
    out.flush();
    out.close();
  }
  
  public static void createNewDocument(String file_name) throws IOException, SAXException
  {
    String ENCODING = "UTF-8";
    
    String imageName = ImageReaderDrawer.getFile_name();
    
    PrintWriter out = new PrintWriter(new FileOutputStream(file_name));
    
    out.println("<?xml version=\"1.0\" encoding=\"" + 
      ENCODING + 
      "\"?>");
    out.println("<!--GEDI was developed at Language and Media Processing Laboratory, University of Maryland. -->");
    out.println("<GEDI xmlns=\"http://lamp.cfar.umd.edu/media/projects/GEDI/\" GEDI_version=\"" + 
    
      XmlConstant.VALUE_GEDI_VERSION + 
      "\" " + "GEDI_date" + 
      "=\"" + XmlConstant.VALUE_GEDI_DATE + "\">");
    
    addUserXMLTag(out);
    
    DLPage currentPage = 
      this_interfacetbdPane.data_panel.a_window.getPage();
    String pageWidth = Integer.toString(currentPage.dlGetWidth());
    String pageHeight = Integer.toString(currentPage.dlGetHeight());
    String pageOrientation = (String)pageTags.get("GEDI_orientation");
    

    out.println("\t<DL_DOCUMENT src=\"" + 
      imageName + 
      "\" NrOfPages=\"1\" docTag=\"xml\">");
    
    String pageString = "\t\t<DL_PAGE gedi_type=\"DL_PAGE\" src=\"" + 
    

      imageName + "\" " + "pageID" + 
      "=\"1\" " + "width" + "=\"" + pageWidth + "\" " + 
      "height" + "=\"" + pageHeight + "\"";
    if (pageOrientation != null)
      pageString = pageString + " GEDI_orientation=\"" + pageOrientation + "\"";
    pageString = pageString + ">";
    
    out.println(pageString);
    out.println("\t\t</DL_PAGE>");
    out.println("\t</DL_DOCUMENT>");
    
    out.println("</GEDI>");
    out.flush();
    out.close();
  }
  
  public ZonesManager get_zones_vec() {
    int index = this_interfacecurrPageID;
    if (index == 0) {
      return getZonesManager(0);
    }
    return getZonesManager(index - 1);
  }
  





  private static void addUserXMLTag(PrintWriter out)
  {
    String dateStr = GetDateTime.getCurrentDateTime();
    
    out.print("\t<USER name=\"" + 
      Login.userName + "\" " + 
      "date" + "=\"" + dateStr + "\" " + 
      "dateFormat" + "=\"" + "mm/dd/yyyy hh:mm" + 
      "\">");
    out.println("</USER>");
  }
}
