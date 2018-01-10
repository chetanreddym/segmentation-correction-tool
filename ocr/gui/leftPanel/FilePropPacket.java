package ocr.gui.leftPanel;

import gttool.misc.Login;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import ocr.gui.OCRInterface;
import ocr.tif.ImageDisplay;
import ocr.util.comments.CommentsParser;













public class FilePropPacket
{
  protected static final ClassLoader loader = ;
  
  public static final ImageIcon is_there_path = Login.localOrInJar("images/check.gif", loader);
  public static final ImageIcon is_there_path_and_messageExists = Login.localOrInJar("images/yellowcheck.gif", loader);
  public static final ImageIcon is_not_there_path = Login.localOrInJar("images/uncheck.gif", loader);
  public static final ImageIcon is_not_there_path_and_messageExists = Login.localOrInJar("images/yellowuncheck.gif", loader);
  public static final ImageIcon is_developing = Login.localOrInJar("images/yellowcheck.gif", loader);
  public static final ImageIcon is_soft_locked = Login.localOrInJar("images/soft_lock_icon.gif", loader);
  public static final ImageIcon is_hard_locked = Login.localOrInJar("images/hard_lock_icon.gif", loader);
  public static final ImageIcon is_only_selected_visible = Login.localOrInJar("images/check_only_selected.gif", loader);
  




  private Hashtable datafile_paths_hash = null;
  












  private Hashtable softLockingInfoHash = null;
  private Hashtable hardLockingInfoHash = null;
  private Hashtable hasNewCommentsInfoHash = null;
  




  private String tif_path = "";
  


  private boolean inRaw = false;
  



  private int ttl_datafiles = 0;
  
  public String[] varietyOfExt = null;
  
  private boolean collapsed = true;
  






  public FilePropPacket(String tif_p, Hashtable hash_t, Hashtable softLockingInfo, Hashtable hardLockingInfo, Hashtable hasNewCommentsInfo, String[] varietyOfExtInfo, int workmode)
  {
    tif_path = tif_p;
    
    datafile_paths_hash = hash_t;
    softLockingInfoHash = softLockingInfo;
    hardLockingInfoHash = hardLockingInfo;
    hasNewCommentsInfoHash = hasNewCommentsInfo;
    varietyOfExt = varietyOfExtInfo;
    
    ttl_datafiles = datafile_paths_hash.size();
    
    if (tif_p.equals("")) {
      inRaw = false;
    } else {
      inRaw = true;
    }
  }
  

  public int getTotalNumOfFiles()
  {
    return ttl_datafiles;
  }
  


  public boolean isSoftLocked(int datasetIndex)
  {
    return ((Boolean)softLockingInfoHash.get(new Integer(datasetIndex))).booleanValue();
  }
  
  public boolean isHardLocked(int datasetIndex)
  {
    return ((Boolean)hardLockingInfoHash.get(new Integer(datasetIndex))).booleanValue();
  }
  
  public boolean hasMessageFile(int datasetIndex)
  {
    if (!inSet(datasetIndex)) {
      File f = new File(getTif_path());
      String xmlPath = OCRInterface.getCurrentXmlDir() + 
        OCRInterface.getXmlDirName() + 
        File.separator + 
        f.getName();
      
      return CommentsParser.hasNewFileComments(xmlPath);
    }
    
    return ((Boolean)hasNewCommentsInfoHash.get(new Integer(datasetIndex))).booleanValue();
  }
  




  public boolean inSet(int index)
  {
    String does_exist = (String)datafile_paths_hash.get(new Integer(index));
    if (does_exist.equals(""))
      return false;
    return true;
  }
  

  boolean inRaw()
  {
    return inRaw;
  }
  
  String getTIFPath()
  {
    return tif_path;
  }
  






  public final String getDataFileAt(int index)
  {
    return datafile_paths_hash.get(new Integer(index));
  }
  




  public void setHardLocked(int datasetNum, boolean toLock, boolean updateTables)
  {
    Integer datasetInt = new Integer(datasetNum);
    File lockedFile = new File((String)datafile_paths_hash.get(datasetInt));
    System.out.println("lockedFile: " + lockedFile);
    
    if ((toLock) && (!isHardLocked(datasetNum)))
    {
      lockedFile.setReadOnly();
      
      hardLockingInfoHash.remove(datasetInt);
      hardLockingInfoHash.put(datasetInt, Boolean.TRUE);
      OCRInterface.this_interface.getCanvas().setAllowEdit(false);
    }
    else if ((!toLock) && (isHardLocked(datasetNum)))
    {
      lockedFile.setWritable(true, false);
      hardLockingInfoHash.remove(datasetInt);
      hardLockingInfoHash.put(datasetInt, Boolean.FALSE);
      OCRInterface.this_interface.getCanvas().setAllowEdit(true);
    }
    
    if (updateTables) {
      this_interfaceocrTable.updateRow(this, WorkmodeTable.curRow);
    }
  }
  









  public void setSoftLocked(int datasetNum, boolean toLock, boolean updateTables)
  {
    Integer datasetInt = new Integer(datasetNum);
    File lockFile = new File((String)datafile_paths_hash.get(datasetInt) + ".lock");
    
    if ((toLock) && (!isSoftLocked(datasetNum)))
    {
      try
      {
        lockFile.createNewFile();
        
        softLockingInfoHash.remove(datasetInt);
        softLockingInfoHash.put(datasetInt, Boolean.TRUE);
        if (!updateTables) return;
        this_interfaceocrTable.updateRow(this, WorkmodeTable.curRow);
      }
      catch (IOException ioe)
      {
        System.out.println("Could not create the lock file!");
        ioe.printStackTrace();
        return;

      }
      
    }
    else if ((!toLock) && (isSoftLocked(datasetNum)))
    {
      if (lockFile.delete())
      {
        softLockingInfoHash.remove(datasetInt);
        softLockingInfoHash.put(datasetInt, Boolean.FALSE);
        if (updateTables) {
          this_interfaceocrTable.updateRow(this, WorkmodeTable.curRow);
        }
      }
      else {
        System.out.println("Error: could not delete lock file");
      }
    }
  }
  

  public void setSoftLocked(int datasetNum, boolean toLock, int rowToUpdate)
  {
    Integer datasetInt = new Integer(datasetNum);
    File lockFile = new File((String)datafile_paths_hash.get(datasetInt) + ".lock");
    
    if ((toLock) && (!isSoftLocked(datasetNum)))
    {
      try
      {
        lockFile.createNewFile();
        
        softLockingInfoHash.remove(datasetInt);
        softLockingInfoHash.put(datasetInt, Boolean.TRUE);
        this_interfaceocrTable.updateRow(this, rowToUpdate);
      }
      catch (IOException ioe)
      {
        System.out.println("Could not create the lock file!");
        ioe.printStackTrace();
        return;

      }
      
    }
    else if ((!toLock) && (isSoftLocked(datasetNum)))
    {
      if (lockFile.delete())
      {
        softLockingInfoHash.remove(datasetInt);
        softLockingInfoHash.put(datasetInt, Boolean.FALSE);
        this_interfaceocrTable.updateRow(this, rowToUpdate);
      }
      else
      {
        System.out.println("Error: could not delete lock file");
      }
    }
  }
  
  public String setExpandCollapse(String sign)
  {
    if (varietyOfExt.length == 0) {
      setCollapsed(false);
      return "";
    }
    if (sign.equals("-")) {
      setCollapsed(true);
      return "+";
    }
    
    setCollapsed(false);
    return "-";
  }
  
  public boolean isBaseImage()
  {
    return varietyOfExt.length != 0;
  }
  
  public void setNewComments(int datasetNum, boolean hasNewComments, boolean updateTables)
  {
    Integer datasetInt = new Integer(datasetNum);
    Boolean recordExists = (Boolean)hasNewCommentsInfoHash.get(datasetInt);
    
    if ((hasNewComments) && (!recordExists.booleanValue())) {
      hasNewCommentsInfoHash.remove(datasetInt);
      hasNewCommentsInfoHash.put(datasetInt, Boolean.TRUE);
      if (updateTables) {
        this_interfaceocrTable.updateRow(this, WorkmodeTable.curRow);
      }
    } else if ((!hasNewComments) && (recordExists.booleanValue())) {
      hasNewCommentsInfoHash.remove(datasetInt);
      hasNewCommentsInfoHash.put(datasetInt, Boolean.FALSE);
      if (updateTables) {
        this_interfaceocrTable.updateRow(this, WorkmodeTable.curRow);
      }
    }
  }
  
  public void delete(int datasetNum) {
    Integer datasetInt = new Integer(datasetNum);
    File lockFile = new File((String)datafile_paths_hash.get(datasetInt) + ".lock");
    
    if (lockFile.delete())
    {
      softLockingInfoHash.remove(datasetInt);
      softLockingInfoHash.put(datasetInt, Boolean.FALSE);

    }
    else
    {
      System.out.println("Error: could not delete lock file");
    }
  }
  









  public Vector<Object> getRowData()
  {
    Vector<Object> vector = new Vector();
    File t_file = new File(getTIFPath());
    
    if (varietyOfExt.length == 0) {
      vector.add("");
    }
    else if (isCollapsed()) {
      vector.add("+");
    } else {
      vector.add("-");
    }
    
    String coloredName = colorMiddle(t_file.getAbsolutePath());
    
    vector.add(coloredName);
    

    for (int k = 0; k < getTotalNumOfFiles(); k++)
    {
      if (isHardLocked(k)) {
        vector.add(is_hard_locked);
      } else if (isSoftLocked(k)) {
        vector.add(is_soft_locked);

      }
      else if (inSet(k)) {
        if (hasMessageFile(k)) {
          vector.add(is_there_path_and_messageExists);
        } else {
          vector.add(is_there_path);
        }
      }
      else if (hasMessageFile(k)) {
        vector.add(is_not_there_path_and_messageExists);
      } else {
        vector.add(is_not_there_path);
      }
    }
    
    return vector;
  }
  
  public Vector<Object> getVarietyOfExtRowData(int i)
  {
    Vector<Object> vector = new Vector();
    File varietyXml = new File(varietyOfExt[i]);
    String xmlExt = OCRInterface.this_interface.getFileExtension(varietyXml.getName());
    String imageExt = OCRInterface.this_interface.getFileExtension(getTIFPath());
    String nameToAdd = varietyXml.getAbsolutePath().replace(xmlExt, imageExt);
    nameToAdd = colorMiddle(nameToAdd);
    
    vector.add("");
    
    vector.add(nameToAdd);
    
    if (isHardLocked(0)) {
      vector.add(is_hard_locked);
    } else {
      vector.add(is_there_path);
    }
    if (!varietyXml.canWrite()) {
      vector.add(is_hard_locked);
    } else if (new File(varietyXml + ".lock").exists()) {
      vector.add(is_soft_locked);
    } else if (varietyXml.exists()) {
      vector.add(is_there_path);
    } else {
      vector.add(is_not_there_path);
    }
    return vector;
  }
  
  public int getNumberOfVarietyOfExt() {
    return varietyOfExt.length;
  }
  
  private String colorMiddle(String name) {
    String ext = OCRInterface.this_interface.getFileExtension(name);
    String baseImage = OCRInterface.this_interface.getBaseImage(new File(name).getName(), ext);
    
    String imageName = new File(baseImage).getName();
    String imageNameNoExt = OCRInterface.this_interface.getFileNameWithoutExt(imageName);
    
    int baseNameIndex = name.lastIndexOf(imageNameNoExt) + imageNameNoExt.length();
    int extIndex = name.lastIndexOf(ext);
    
    String toBeColored = null;
    try
    {
      toBeColored = name.substring(baseNameIndex, extIndex).trim();
    } catch (StringIndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    
    if ((toBeColored == null) || (toBeColored.equals(""))) {
      return imageNameNoExt + ext;
    }
    return "<HTML>" + imageNameNoExt + "<FONT COLOR=Red>" + toBeColored + "</FONT>" + ext + "</HTML>";
  }
  
  public static String uncolorImageName(String colored) {
    String uncolored = colored;
    uncolored = uncolored.replace("<HTML>", "");
    uncolored = uncolored.replace("<FONT COLOR=Red>", "");
    uncolored = uncolored.replace("</FONT>", "");
    uncolored = uncolored.replace("</HTML>", "");
    
    return uncolored;
  }
  


















  public boolean isCollapsed()
  {
    return collapsed;
  }
  
  public void setCollapsed(boolean b) { collapsed = b; }
  
  public String[] getVarietyOfExt()
  {
    return varietyOfExt;
  }
  
  public String getTif_path() {
    return tif_path;
  }
  
  public Hashtable getDatafile_paths_hash() {
    return datafile_paths_hash;
  }
}
