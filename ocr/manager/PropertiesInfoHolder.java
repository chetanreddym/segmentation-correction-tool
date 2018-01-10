package ocr.manager;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.OCRInterface.OcrMenuListener;
import ocr.gui.SaveFilesDialog;
import ocr.gui.Zone;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.zones.ZonesManager;
import ocr.tag.AdditionalZoneArgsHandler;
import ocr.tif.ImageReaderDrawer;




public class PropertiesInfoHolder
{
  protected OCRInterface ocrIF = OCRInterface.this_interface;
  









  private int workmode;
  









  protected int numberOfDatasets;
  








  private Vector<String> datasetNamesVec;
  








  private Vector<String> type_extensions_vec;
  








  public Vector<String> type_homepath_vec;
  








  private Vector<String> type_homename_vec;
  








  private Hashtable list_of_files_hash;
  








  protected Hashtable<Integer, LoadDataFile> loadDataFileHash = null;
  










  private Hashtable<String, Zonetype> label_key_hash;
  










  protected Hashtable<String, Zonetype> id_key_hash;
  









  protected Vector<Vector> zonetypeIdVec;
  









  protected Vector<Vector> zonetypeNameVec;
  









  protected Hashtable<String, Boolean> selShapeHash;
  









  private Hashtable<Integer, AdditionalZoneArgsHandler> azah_track_hash;
  









  public Hashtable<Integer, ZonesManager> shape_vec_hash = null;
  











  public Vector<Vector> zonetypeDefVec;
  











  private Vector<FilePropPacket> file_prop_vec;
  










  private Hashtable<Integer, JComboBox> lof_combo_boxes_hash;
  











  public void createZonetype(int dataset, int id, String name, String color)
  {
    Zonetype newZonetype = new Zonetype(id, name, color, true);
    createZonetype(dataset, newZonetype);
  }
  

















  public String getWorkmodeName()
  {
    String returnVal = null;
    if (workmode == 0)
      returnVal = "OCR";
    return returnVal;
  }
  








  public void createZonetype(int dataset, Zonetype newZonetype)
  {
    ((Vector)zonetypeIdVec.get(dataset)).add(new Integer(id));
    ((Vector)zonetypeNameVec.get(dataset)).add(name);
    
    JLabel label = new JLabel();
    label.setForeground(Color.decode(color));
    

    selShapeHash.put(Integer.toString(id), new Boolean(true));
    id_key_hash.put(Integer.toString(id), newZonetype);
    ((Vector)zonetypeDefVec.get(dataset)).add(newZonetype);
  }
  










  public void removeAllZonetypes(int dataset)
  {
    ((Vector)zonetypeIdVec.get(dataset)).clear();
    ((Vector)zonetypeNameVec.get(dataset)).clear();
    
    ((Vector)zonetypeDefVec.get(dataset)).clear();
    selShapeHash.clear();
    id_key_hash.clear();
  }
  








  public PropertiesInfoHolder(int workmodeIn)
  {
    workmode = workmodeIn;
    
    numberOfDatasets = -1;
    datasetNamesVec = new Vector();
    type_extensions_vec = new Vector();
    type_homepath_vec = new Vector();
    type_homename_vec = new Vector();
    
    label_key_hash = new Hashtable();
    id_key_hash = new Hashtable();
    zonetypeIdVec = new Vector();
    zonetypeNameVec = new Vector();
    
    azah_track_hash = new Hashtable();
    selShapeHash = new Hashtable();
    zonetypeDefVec = new Vector();
    
    file_prop_vec = new Vector();
    list_of_files_hash = new Hashtable();
    lof_combo_boxes_hash = new Hashtable();
    loadDataFileHash = new Hashtable();
    shape_vec_hash = new Hashtable();
  }
  




  public String toString()
  {
    String returnVal = "\n";
    returnVal = returnVal + "numberOfDatasets =" + numberOfDatasets + "\n";
    returnVal = returnVal + "datasetNamesVec" + datasetNamesVec + "\n";
    returnVal = returnVal + "type_extensions_vec" + type_extensions_vec + "\n";
    returnVal = returnVal + "type_homepath_vec" + type_homepath_vec + "\n";
    returnVal = returnVal + "type_homename_vec" + type_homename_vec + "\n";
    












    returnVal = returnVal + "\n";
    return returnVal;
  }
  










  public void readInfo(Properties propFile)
  {
    numberOfDatasets = Integer.parseInt(propFile.getProperty("ttl_types"));
    
    int ttl_objs = 0;
    
    AdditionalZoneArgsHandler azah = null;
    
    for (int types = 0; types < numberOfDatasets; types++)
    {
      azah = new AdditionalZoneArgsHandler(propFile, types);
      azah_track_hash.put(new Integer(types), azah);
      


      datasetNamesVec.add(propFile.getProperty("type" + types));
      type_extensions_vec.add(propFile.getProperty("type" + types + 
        "_ext"));
      type_homepath_vec.add(propFile
        .getProperty("type" + types + "_home"));
      type_homename_vec.add(propFile.getProperty("type" + types + 
        "_homename"));
      
      ttl_objs = Integer.parseInt(propFile.getProperty("ttl_type" + types + 
        "_objects"));
      
      Vector temp_id_vec = new Vector();
      Vector temp_label_vec = new Vector();
      Vector temp_colorlabel_vec = new Vector();
      
      for (int ttl = 0; ttl < ttl_objs; ttl++) {
        String property = propFile.getProperty("type" + types + 
          "_object" + ttl);
        
        StringTokenizer tknizer = new StringTokenizer(property, " ");
        Zonetype newZone = new Zonetype(Integer.parseInt(tknizer
          .nextToken()), tknizer.nextToken(), 
          tknizer.nextToken(), new Boolean(tknizer.nextToken())
          .booleanValue());
        

        label_key_hash.put(newZone.get_name(), newZone);
        








        id_key_hash.put(newZone.get_id(), newZone);
        
        temp_id_vec.add(new Integer(newZone.get_id()));
        temp_label_vec.add(newZone.get_name());
        

        Color c = new Color(0);
        c = Color.decode(newZone.get_color());
        JLabel type_label = new JLabel(newZone.get_name());
        type_label.setForeground(c);
        temp_colorlabel_vec.add(type_label);
      }
      


      zonetypeIdVec.add(temp_id_vec);
      zonetypeNameVec.add(temp_label_vec);
    }
    






    int i = 0;
    
    for (i = 0; i < zonetypeIdVec.size(); i++) {
      Vector tmp_vec = (Vector)zonetypeIdVec.get(i);
      for (int j = 0; j < tmp_vec.size(); j++) {
        selShapeHash.put(tmp_vec.get(j), new Boolean(true));
      }
    }
    
    for (i = 0; i < numberOfDatasets; i++) {
      list_of_files_hash.put(new Integer(i), new Vector());
      lof_combo_boxes_hash.put(new Integer(i), new JComboBox());
    }
    processReadInfo();
  }
  








  void processReadInfo()
  {
    for (int datasetNum = 0; datasetNum < datasetNamesVec.size(); datasetNum++)
    {
      Vector curZonetypeDefVec = new Vector();
      Vector zonetypeIds = (Vector)zonetypeIdVec.get(datasetNum);
      
      for (int zonetypeNum = 0; zonetypeNum < zonetypeIds.size(); zonetypeNum++)
      {
        String key = zonetypeIds.get(zonetypeNum).toString();
        Zonetype zt = (Zonetype)id_key_hash.get(key);
        curZonetypeDefVec.add(zt);
      }
      zonetypeDefVec.add(curZonetypeDefVec);
    }
  }
  



  public void updateOnDicLoad(String current_dir)
  {
    file_prop_vec.removeAllElements();
    
    Vector unique_fols_list_vec = getListofFiles(current_dir);
    
    for (int lof = 0; lof < numberOfDatasets; lof++) {
      Vector tmp_vec = (Vector)list_of_files_hash.get(new Integer(lof));
      if (tmp_vec != null) {
        tmp_vec.removeAllElements();
      }
    }
    
    for (int i = 0; i < unique_fols_list_vec.size(); i++) {
      File tmpFile = new File((String)unique_fols_list_vec.get(i));
      for (int j = 0; j < type_homename_vec.size(); j++) {
        String tmp_name = ((String)type_homename_vec.get(j))
          .toUpperCase();
        if (tmpFile.getName().toUpperCase().startsWith(tmp_name))
        {
          ((Vector)list_of_files_hash.get(new Integer(j))).add((String)unique_fols_list_vec.get(i));
        }
      }
    }
    

    for (int k = 0; k < list_of_files_hash.size(); k++) {
      if (((Vector)list_of_files_hash.get(new Integer(k))).size() > 0)
      {
        Vector tmp_vec = (Vector)list_of_files_hash.get(new Integer(
          k));
        
        JComboBox tempTrainingSetCombo = new JComboBox(
          (Vector)list_of_files_hash.get(new Integer(k)));
        


        if (lof_combo_boxes_hash.containsKey(new Integer(k)))
        {
          try {
            ((JComboBox)lof_combo_boxes_hash.get(new Integer(k))).setModel(tempTrainingSetCombo.getModel());
          } catch (Exception except) {
            except.printStackTrace();
          }
        }
        else {
          System.out.println("Hyper Code System Crashed. Please Restart the Application.");
          
          System.exit(1);
        }
      } else {
        String tmp_name = (String)type_homename_vec.get(k);
        String hm_path = (String)type_homepath_vec.get(k);
        File new_folder = new File(current_dir + "/" + hm_path + "/" + 
          tmp_name);
        new_folder.mkdir();
        Vector empty_vec = new Vector();
        empty_vec.add(tmp_name);
        JComboBox tmp_cmb_box = new JComboBox(empty_vec);
        lof_combo_boxes_hash.put(new Integer(k), tmp_cmb_box);
      }
    }
  }
  











  private Vector getListofFiles(String current_dir)
  {
    Vector unique_homenames_vec = new Vector();
    Vector unique_fols_list_vec = new Vector();
    


    for (int i = 0; i < type_homepath_vec.size(); i++) {
      boolean flag = true;
      String hname_str = (String)type_homepath_vec.get(i);
      



      for (int z = 0; (flag) && (z < unique_homenames_vec.size()); z++) {
        String unique_str = (String)unique_homenames_vec.get(z);
        

        if (hname_str.toUpperCase().equals(unique_str.toUpperCase())) {
          flag = false;
        }
      }
      


      if (flag) {
        unique_homenames_vec.add(type_homepath_vec.get(i));
      }
    }
    




    Vector folder_list_vec = new Vector();
    for (int i = 0; i < unique_homenames_vec.size(); i++) {
      String[] str_list = new File(current_dir + 
        unique_homenames_vec.get(i)).list();
      folder_list_vec.add(str_list);
    }
    


    for (int file_list = 0; file_list < folder_list_vec.size(); file_list++) {
      String[] tmp_filelist = (String[])folder_list_vec.get(file_list);
      if (tmp_filelist != null)
      {
        for (int i = 0; i < tmp_filelist.length; i++) {
          unique_fols_list_vec.add(tmp_filelist[i]);
        }
      }
    }
    return unique_fols_list_vec;
  }
  







  public String getDataSetName(int i)
  {
    return (String)datasetNamesVec.get(i);
  }
  
  public String getDataSetExtension(int i) {
    return (String)type_extensions_vec.get(i);
  }
  









  public int addAllZonesToTraining()
  {
    int numZonesAdded = 0;
    
    ZonesManager trainingZones = 
      (ZonesManager)shape_vec_hash.get(new Integer(1));
    ZonesManager activeZones = 
      (ZonesManager)shape_vec_hash.get(new Integer(0));
    
    for (Iterator iter = activeZones.iterator(); iter.hasNext(); numZonesAdded++) {
      Zone curZone = (Zone)iter.next();
      trainingZones.add(curZone.clone_zone());
    }
    return numZonesAdded;
  }
  









  public int addSelectedZonesToTraining(Vector activeZones)
  {
    int numZonesAdded = 0;
    
    ZonesManager trainingZones = 
      (ZonesManager)shape_vec_hash.get(new Integer(1));
    

    for (Iterator iter = activeZones.iterator(); iter.hasNext(); numZonesAdded++) {
      Zone curZone = (Zone)iter.next();
      trainingZones.add(curZone.clone_zone());
    }
    return numZonesAdded;
  }
  









  public void initShapeVec()
  {
    shape_vec_hash.clear();
    
    for (int h = 0; h < loadDataFileHash.size(); h++) {
      LoadDataFile ldfile = 
        (LoadDataFile)loadDataFileHash.get(new Integer(h));
      ZonesManager tmp_vec = ldfile.get_zones_vec();
      if (tmp_vec == null) {
        shape_vec_hash.put(new Integer(h), new ZonesManager());
      } else {
        shape_vec_hash.put(new Integer(h), tmp_vec);
      }
    }
  }
  






























  public void updateOnChangeRun(int dataSetIndex, String newRunName) {}
  






























  protected void updateListOfFiles(int dataSetIndex, String currentRunDir, String newRunName)
  {
    Vector v = (Vector)list_of_files_hash.get(new Integer(dataSetIndex));
    int index = v.indexOf(currentRunDir);
    if (index != -1) {
      v.set(index, newRunName);
    }
  }
  








  protected void updateHomeName(String currentRunDir, String newRunName)
  {
    int index = type_homename_vec.indexOf(currentRunDir);
    
    if (index != -1) {
      type_homename_vec.set(index, newRunName);
    }
  }
  







  public String getlof_combo_boxes_hashItem(int tbl)
  {
    return (String)((JComboBox)lof_combo_boxes_hash.get(new Integer(tbl)))
      .getSelectedItem();
  }
  








  public void clearFilePropsVec()
  {
    file_prop_vec.removeAllElements();
  }
  







  public int getFilePropsVecSize()
  {
    return file_prop_vec.size();
  }
  








  public void addElementToFilePropsVec(int pos, FilePropPacket fpkt)
  {
    file_prop_vec.add(pos, fpkt);
  }
  









  public FilePropPacket getElementFilePropVec(int i)
  {
    return (FilePropPacket)file_prop_vec.get(i);
  }
  
  public FilePropPacket removeElementFilePropVec(int i) {
    return (FilePropPacket)file_prop_vec.remove(i);
  }
  
  public int getImageIndex(String imagePathIn) {
    for (int i = 0; i < file_prop_vec.size(); i++) {
      FilePropPacket fpk = (FilePropPacket)file_prop_vec.get(i);
      if (fpk.getTif_path().equals(imagePathIn))
        return i;
    }
    return -1;
  }
  
  public boolean isImagesHasVarietyOfExt() {
    for (int i = 0; i < file_prop_vec.size(); i++) {
      FilePropPacket fpk = (FilePropPacket)file_prop_vec.get(i);
      if (fpk.getVarietyOfExt().length != 0)
        return true;
    }
    return false;
  }
  











  public void setAllFilePropsPacketsLocked(int colClicked, boolean setLocked)
  {
    Vector filePropVec = file_prop_vec;
    System.out.println("colClicked: " + colClicked);
    for (int row = 0; row < filePropVec.size(); row++) {
      FilePropPacket fpp = (FilePropPacket)filePropVec.get(row);
      int dataSet = 0;
      if (colClicked == 3)
        dataSet = 1;
      fpp.setHardLocked(dataSet, setLocked, false);
    }
  }
  








  public String getHomePlusHomeName(int l)
  {
    String homename = getlof_combo_boxes_hashItem(l);
    
    if (homename == null) {
      homename = (String)type_homename_vec.get(l);
    }
    return (String)type_homepath_vec.get(l) + "/" + homename;
  }
  












  public void updatePropsOnImageLoad(String imagePath)
  {
    String xmlPath = OCRInterface.this_interface.hasMeta(imagePath);
    
    if (xmlPath != null)
    {
      LoadDataFile datafile = new LoadDataFile(xmlPath, 0, workmode);
      loadDataFileHash.put(new Integer(0), datafile);
      OCRInterface.currDoc = getDataFileHash(0);
    }
    else if (WorkmodeTable.col == 3)
    {
      File imageFile = new File(imagePath);
      String imageName = imageFile.getName();
      
      String xmlDir = OCRInterface.getCurrentXmlDir() + 
        File.separator + 
        OCRInterface.getXmlDirName() + 
        File.separator;
      
      String imageNameNoExt = OCRInterface.this_interface
        .getFileNameWithoutExt(imageName);
      
      File xmlFile = new File(xmlDir + imageNameNoExt + ".XML");
      
      if (!xmlFile.exists()) {
        xmlFile = new File(xmlDir + imageNameNoExt + ".xml");
      }
      xmlPath = xmlFile.getAbsolutePath();
      
      final JOptionPane optionPane = new JOptionPane(
        "This file does not have an associated XML file.\nWould you like the system to create:\n" + 
        
        xmlPath + "?", 
        3, 0);
      final JDialog dialog = new JDialog(OCRInterface.this_interface, 
        "Click a button", true);
      dialog.setContentPane(optionPane);
      
      dialog.setDefaultCloseOperation(0);
      
      optionPane
        .addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            if ((dialog.isVisible()) && 
              (e.getSource() == optionPane))
            {
              if (prop.equals("value"))
              {


                dialog.setVisible(false); }
            }
          }
        });
      dialog.setLocation(new Point(300, 300));
      dialog.pack();
      dialog.setVisible(true);
      int value = ((Integer)optionPane.getValue()).intValue();
      if (value == 0) {
        String baseImagePath = ImageReaderDrawer.getFile_path();
        

        this_interfacesaveFilesDialog
          .createXML(xmlPath, baseImagePath);
        

        this_interfacemenuListener
          .actionPerformed(new ActionEvent(
          OCRInterface.this_interface, 
          128, 
          "Refresh Document List"));
      }
      else {
        OCRInterface.currDoc = null;
      }
    }
  }
  










































































































  public Zonetype getZoneType(String zonename)
  {
    return (Zonetype)label_key_hash.get(zonename);
  }
  








  public Zonetype getZoneTypeById(String zoneId)
  {
    return (Zonetype)id_key_hash.get(zoneId);
  }
  












  public int getZoneTypeIndex(int dataSetIndex, int zoneId)
  {
    Vector v = (Vector)zonetypeIdVec.get(dataSetIndex);
    
    return v.indexOf(new Integer(zoneId));
  }
  








  public void updateZoneSelected(String name, boolean b)
  {
    selShapeHash.put(name, new Boolean(b));
  }
  








  public Object isZoneSelected(String sel_dset)
  {
    return selShapeHash.get(sel_dset);
  }
  








  public AdditionalZoneArgsHandler getAdditionalArgs(int dataSetIndex)
  {
    return (AdditionalZoneArgsHandler)azah_track_hash.get(new Integer(
      dataSetIndex));
  }
  








  public int getNumDataSets()
  {
    return numberOfDatasets;
  }
  









  public Vector getZoneNameVec(int datasetIndex)
  {
    return (Vector)zonetypeNameVec.get(datasetIndex);
  }
  









  public Vector getZonetypeIdVec(int currDatasetNum)
  {
    return (Vector)zonetypeIdVec.elementAt(currDatasetNum);
  }
  









  public LoadDataFile getDataFileHash(int datasetIndex)
  {
    return (LoadDataFile)loadDataFileHash.get(new Integer(datasetIndex));
  }
  








  public void clearDataFileHash()
  {
    loadDataFileHash.clear();
  }
  








  public String getHomename(int tbl)
  {
    return "";
  }
  
  public Vector<FilePropPacket> getFile_prop_vec()
  {
    return file_prop_vec;
  }
}
