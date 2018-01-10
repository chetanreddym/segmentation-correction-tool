package ocr.util;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import ocr.gui.OCRInterface;
import ocr.tif.ImageReaderDrawer;
















public class DesktopUtil
{
  private Desktop desktop;
  private Desktop.Action action = Desktop.Action.OPEN;
  private static final String DOC_DIRECTORY = "doc" + File.separator + "GEDI_Help";
  
  private static final String HELP_FILE = "ShowHelp.jar";
  private static final String PRE_STRING = "java -jar";
  private static final String DOC_JAR = "doc.jar";
  private static final String HELP_HOME_DIR = System.getProperty("user.home") + 
    File.separator + 
    ".gedi" + 
    File.separator;
  


  public DesktopUtil()
  {
    if (Desktop.isDesktopSupported()) {
      desktop = Desktop.getDesktop();
      
      enableSupportedActions();
    }
    else {
      desktop = null;
    }
  }
  
  private void enableSupportedActions() {
    desktop.isSupported(Desktop.Action.BROWSE);
    

    desktop.isSupported(Desktop.Action.MAIL);
  }
  

  public void launchMail(ActionEvent evt, String mailTo)
  {
    URI uriMailTo = null;
    try {
      if (mailTo.length() > 0) {
        uriMailTo = new URI("mailto", mailTo, null);
        desktop.mail(uriMailTo);
      } else {
        desktop.mail();
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (URISyntaxException use) {
      use.printStackTrace();
    }
  }
  
  public void launchBrowser(String pathIn) {
    if (pathIn == null)
      return;
    pathIn = pathIn.replaceAll(" ", "%20");
    pathIn = pathIn.replaceAll("\\\\", "/");
    pathIn = "file:///" + pathIn;
    URI uri = null;
    try
    {
      uri = new URI(pathIn);
      desktop.browse(uri);
    } catch (IOException ioe) {
      System.out.println("The system cannot find the " + uri + " file specified");
    }
    catch (URISyntaxException use) {
      System.out.println("Illegal character in path");
    }
  }
  

  public String getJarfileName()
  {
    URL outputURL = getClass().getProtectionDomain().getCodeSource().getLocation();
    String outputString = outputURL.toString();
    
    int index1 = outputString.indexOf(":");
    int index2 = outputString.lastIndexOf(":");
    String[] parseString; String[] parseString; if (index1 != index2) {
      parseString = outputString.split("file:/");
    } else
      parseString = outputString.split("file:");
    String jarFilename = parseString[1].replaceAll("%20", " ");
    
    return jarFilename;
  }
  
  public JarEntry getJarEntry(JarFile jar, String matchString) throws Exception {
    JarEntry je = null;
    Enumeration<JarEntry> resources = jar.entries();
    while (resources.hasMoreElements()) {
      je = (JarEntry)resources.nextElement();
      
      if (je.getName().matches(".*" + matchString))
        return (JarEntry)jar.getEntry(je.getName());
    }
    throw new Exception("Could not find " + matchString + " in " + jar.getName() + ".");
  }
  
  public String join(List<String> arr, String sep) {
    String res = "";
    for (int i = 0; i < arr.size() - 1; i++) {
      res = res + (String)arr.get(i) + sep;
    }
    if (arr.size() == 0) return res;
    return res + (String)arr.get(arr.size() - 1);
  }
  
  public void transferBytes(JarFile containsFrom, JarEntry from, File to) throws Exception {
    int N = 65536;
    int bytesRead = 0;
    File f = to;
    BufferedInputStream is = new BufferedInputStream(containsFrom.getInputStream(from));
    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
    while (bytesRead != -1) {
      byte[] bytes = new byte[N];
      bytesRead = is.read(bytes, 0, N);
      if (bytesRead != -1) {
        fos.write(bytes, 0, bytesRead);
        fos.flush();
      }
      bytes = (byte[])null;
    }
    fos.close();
    is.close();
  }
  
  public void extractHelp() throws Exception {
    JarFile thisjar = new JarFile(getJarfileName());
    
    File jar = new File(HELP_HOME_DIR + "doc.jar");
    if (!jar.getParentFile().exists())
      jar.getParentFile().mkdirs();
    transferBytes(thisjar, getJarEntry(thisjar, "doc.jar"), jar);
    JarFile jarfile = new JarFile(jar);
    Enumeration<JarEntry> enm = jarfile.entries();
    
    while (enm.hasMoreElements()) {
      JarEntry file = (JarEntry)enm.nextElement();
      ArrayList<String> d = new ArrayList(Arrays.asList(file.getName().split("(\\\\+)|(/+)")));
      



      if ((!file.getName().endsWith("\\")) && (!file.getName().endsWith("/")))
      {
        if (d.size() > 1) {
          File f = new File(HELP_HOME_DIR + join(d.subList(0, d.size() - 1), File.separator));
          if (!f.exists()) f.mkdirs();
        }
        File f = new File(HELP_HOME_DIR + file.getName().replace("\\", File.separator));
        transferBytes(jarfile, file, f);
      }
    }
  }
  
  public void launchHelp() { String relativeHelpPath = DOC_DIRECTORY + 
      File.separator + 
      "javahelp" + 
      File.separator + 
      "bin" + 
      File.separator + 
      "ShowHelp.jar";
    String helpPath = HELP_HOME_DIR + relativeHelpPath;
    
    File jarFile = new File(helpPath);
    
    if (!jarFile.exists()) {
      try {
        setWaitCursor();
        extractHelp();
      } catch (Exception e) {
        e.printStackTrace();
        String message = "GEDI Help cannot be loaded.\n\n" + 
          helpPath + " doesn't exist.";
        
        JOptionPane.showMessageDialog(
          OCRInterface.this_interface, 
          message, 
          "GEDI Help", 
          0);
        return;
      } finally {
        setDefaultCursor();
      }
    }
    
    String runStr = "java -jar \"" + helpPath + "\"";
    try
    {
      setWaitCursor();
      if (System.getProperty("mrj.version") != null) {
        String[] commandStrings = { "/usr/bin/open", helpPath };
        Runtime.getRuntime().exec(commandStrings);
      } else {
        Runtime.getRuntime().exec(runStr);
      }
      


      return;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally {
      setDefaultCursor();
    }
  }
  
  public Desktop _getDesktop()
  {
    return desktop;
  }
  



  public void setPrintAction()
  {
    action = Desktop.Action.PRINT;
  }
  



  public void setEditAction()
  {
    action = Desktop.Action.EDIT;
  }
  



  public void setOpenAction()
  {
    action = Desktop.Action.OPEN;
  }
  
  public void setBrowseAction() {
    action = Desktop.Action.BROWSE;
  }
  




  public void launchDefaultApplication(String filePath)
  {
    File file = new File(filePath);
    try
    {
      switch (action) {
      case BROWSE: 
        desktop.open(file);
        break;
      case EDIT: 
        desktop.edit(file);
        break;
      case MAIL: 
        desktop.print(file);
      }
    }
    catch (IOException ioe)
    {
      String name = file.getName();
      String ext = name.substring(name.lastIndexOf('.'));
      String vendor = System.getProperty("os.name");
      String msg = "Cannot perform the given operation to the \"" + file + "\" file.\n" + 
        "Probably there is no default application associated with file type \"" + 
        ext + "\".\n" + 
        "Use standard " + vendor + " tools to assign default application. ";
      System.out.println(msg);
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, "Message", 
        1);
      return;
    }
  }
  
  public boolean isOpenSupported() {
    if (desktop.isSupported(Desktop.Action.OPEN)) {
      return true;
    }
    System.out.println("Desktop \"Open\" action is not supported.");
    JOptionPane.showMessageDialog(null, 
      "\"Open\" action is not supported on your system.\nTry to open file outside of GEDI.", 
      "Open file", 
      2);
    return false;
  }
  
  public boolean isEditSupported()
  {
    if (desktop.isSupported(Desktop.Action.EDIT)) {
      return true;
    }
    System.out.println("Desktop \"Edit\" action is not supported.");
    JOptionPane.showMessageDialog(null, 
      "\"Edit\" action is not supported on your system.\nTry to edit file outside of GEDI.", 
      "Edit file", 
      2);
    return false;
  }
  
  public boolean isPrintSupported()
  {
    if (desktop.isSupported(Desktop.Action.PRINT)) {
      return true;
    }
    System.out.println("Desktop \"Print\" action is not supported.");
    JOptionPane.showMessageDialog(null, 
      "\"Print\" action is not supported on your system.\nTry to print file outside of GEDI.", 
      "Print file", 
      2);
    return false;
  }
  
  public boolean isBrowseSupported()
  {
    if (desktop.isSupported(Desktop.Action.BROWSE)) {
      return true;
    }
    System.out.println("Desktop \"Browse\" action is not supported.");
    JOptionPane.showMessageDialog(null, 
      "The action is not supported on your system.\nTry to launch browser outside of GEDI.", 
      "Browse file", 
      2);
    return false;
  }
  
  public boolean isMailSupported()
  {
    if (desktop.isSupported(Desktop.Action.MAIL)) {
      return true;
    }
    System.out.println("Desktop \"Mail\" action is not supported.");
    JOptionPane.showMessageDialog(null, 
      "The action is not supported on your system.\nTry to launch mail client outside of GEDI.", 
      "Mail", 
      2);
    return false;
  }
  
  private void setWaitCursor()
  {
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
    Toolkit.getDefaultToolkit().sync();
  }
  
  private void setDefaultCursor() {
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
}
