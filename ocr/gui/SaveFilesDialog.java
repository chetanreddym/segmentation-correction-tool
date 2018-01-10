package ocr.gui;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.misc.TypeAttributeEntry;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.util.AttributeConfigUtil;
import org.xml.sax.SAXException;

public class SaveFilesDialog extends JDialog implements ActionListener
{
  OCRInterface ocrIF = OCRInterface.this_interface;
  

  private static final String save = "Save";
  

  private static final String saveNone = "Save None";
  

  private static final String cancel = "Cancel";
  

  JPanel xboxPanel;
  
  private volatile boolean autoSaveOn = true;
  


  public void actionPerformed(ActionEvent e)
  {
    JButton sourceButt = (JButton)e.getSource();
    if (sourceButt.getText().equals("Save")) {
      OCRInterface.currDoc.dumpData();
      ocrIF.update_tables();
      ocrIF.fclose_cancel_selected = false;
      setVisible(false);
    } else if (sourceButt.getText().equals("Cancel")) {
      ocrIF.fclose_cancel_selected = true;
      setVisible(false);
    } else if (sourceButt.getText().equals("Save None")) {
      ocrIF.fclose_cancel_selected = false;
      setVisible(false);
    }
  }
  




  public SaveFilesDialog()
  {
    super(OCRInterface.this_interface, "Save Modified Files?");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }
    });
    setModal(true);
    
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, 1));
    contentPane.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
    
    xboxPanel = new JPanel();
    xboxPanel.setLayout(new BoxLayout(xboxPanel, 1));
    xboxPanel.setAlignmentX(0.0F);
    contentPane.add(xboxPanel);
    
    contentPane.add(javax.swing.Box.createVerticalStrut(5));
    
    JPanel buttonPanel = new JPanel();
    

    JButton curButton = new JButton("Save None");
    curButton.addActionListener(this);
    buttonPanel.add(curButton);
    curButton = new JButton("Save");
    curButton.addActionListener(this);
    buttonPanel.add(curButton);
    curButton = new JButton("Cancel");
    curButton.addActionListener(this);
    buttonPanel.add(curButton);
    buttonPanel.setAlignmentX(0.0F);
    contentPane.add(buttonPanel);
    
    setContentPane(contentPane);
    pack();
    setVisible(false);
  }
  







  public void saveData()
  {
    ocrIF = OCRInterface.this_interface;
    xboxPanel.removeAll();
    
    DLPage page = this_interfacetbdPane.data_panel.a_window.getPage();
    



    if ((OCRInterface.currDoc == null) && ((currentHWObjcurr_canvas.shapeVec.size() != 0) || (
      (page != null) && (pageTags.size() > 1))) && 
      (ocrIF.ocrTable.getSelectedRow() >= 0)) {
      String imageStr = (String)ocrIF.ocrTable.getModel().getValueAt(ocrIF.ocrTable
        .getSelectedRow(), 1);
      imageStr = imageStr.substring(0, imageStr.lastIndexOf('.'));
      String dataPath = OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName() + "/" + imageStr + ".xml";
      String imagePath = ImageReaderDrawer.getFile_path();
      createXML(dataPath, imagePath);
    }
    










    System.out.println("save data: " + OCRInterface.currDoc);
    LoadDataFile ldf = OCRInterface.currDoc;
    










    if ((ldf != null) && (ldf.isModified())) {
      String xboxName = ldf.getFileName();
      JLabel l = new JLabel(xboxName);
      l.setFont(new Font("Arial", 1, 14));
      xboxPanel.add(l);
      setLocation(new Point(300, 300));
      pack();
      if (autoSaveOn) {
        OCRInterface.currDoc.dumpData();
        ocrIF.fclose_cancel_selected = false;
      }
      else if (!OCRInterface.getAttsConfigUtil().isVisible()) {
        super.setVisible(true);
      }
      
      ocrIF.updateCurrFilename();
    }
    currentHWObjcurr_canvas.reset();
  }
  
  public void showForExit() {
    boolean flag = true;
    
    flag = true;
    if (!autoSaveOn) {
      OCRInterface.dialog_open = true;
      
      final JOptionPane optionPane = new JOptionPane("Save all XML modifications?", 3, 0);
      
      final JDialog dialog = new JDialog(OCRInterface.this_interface, 
        "Save Prompt", true);
      OCRInterface.setDialog(dialog);
      dialog.setContentPane(optionPane);
      
      dialog.setDefaultCloseOperation(0);
      
      optionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent e) {
          String prop = e.getPropertyName();
          
          if ((dialog.isVisible()) && 
            (e.getSource() == optionPane) && 
            (prop.equals("value"))) {
            dialog.setVisible(false);
          }
          
        }
        
      });
      dialog.setLocation(new Point(500, 300));
      dialog.pack();
      dialog.setVisible(true);
      int value = ((Integer)optionPane.getValue()).intValue();
      
      int response = value;
      
      if (response == 0) {
        flag = true;
        dispose();
      } else {
        flag = false;
        dispose();
      }
      OCRInterface.dialog_open = false;
    }
    
    if (flag) {
      ocrIF = OCRInterface.this_interface;
      xboxPanel.removeAll();
      if (OCRInterface.currDoc == null) {
        saveData();
      }
      else {
        LoadDataFile ldf = OCRInterface.currDoc;
        
        if (ldf.isModified())
        {
          String xboxName = ldf.getFileName();
          JLabel l = new JLabel(xboxName);
          l.setFont(new Font("Arial", 1, 14));
          xboxPanel.add(l);
          setLocation(new Point(300, 300));
          pack();
          if (autoSaveOn) {
            saveData();
            

            ocrIF.fclose_cancel_selected = false;
          } else {
            super.setVisible(true);
          }
        }
      }
    }
  }
  



















  public void setAutoSave(boolean autoSaveOn)
  {
    this.autoSaveOn = autoSaveOn;
  }
  
  public boolean isAutoSave() {
    return autoSaveOn;
  }
  








  public void createMultipleXML()
  {
    ocrIF = OCRInterface.this_interface;
    ocrIF.tbdPane.setCursor(Cursor.getPredefinedCursor(3));
    

    int[] selectedRows = ocrIF.ocrTable.getSelectedRows();
    int numberOfCreatedXML = 0;
    
    if ((OCRInterface.currDoc != null) && (selectedRows.length == 1)) {
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        "XML file for the selected image already exists.", "Auto-Create XML", 
        1);
      ocrIF.autoCreateXml = false;
      return;
    }
    if ((OCRInterface.currDoc == null) && (ImageDisplay.nulldoc.size() == 0)) {
      int answer = JOptionPane.showConfirmDialog(ocrIF, 
        "There no data to be saved for the selected image.\nDo you still want to create XML for the selected image?", 
        
        "Auto-Create XML", 
        1);
      if ((answer == 1) || (answer == 2)) {
        return;
      }
    }
    

    for (int i = 0; i < selectedRows.length; i++) {
      ocrIF.autoCreateXml = true;
      
      String imageName = (String)ocrIF.ocrTable.getModel()
        .getValueAt(selectedRows[i], 1);
      String imageNameNoExt = imageName.substring(0, imageName.lastIndexOf('.'));
      
      String xmlDir = OCRInterface.getCurrentXmlDir() + 
        File.separator + 
        OCRInterface.getXmlDirName() + 
        File.separator;
      String imageDir = OCRInterface.getCurrentImageDir() + 
        File.separator + 
        OCRInterface.getImageDirName() + 
        File.separator;
      
      File dataFile = new File(xmlDir + imageNameNoExt + ".XML");
      
      if (!dataFile.exists()) {
        dataFile = new File(xmlDir + imageNameNoExt + ".xml");
      }
      String imageFilePath = imageDir + imageName;
      
      createXML(dataFile.getAbsolutePath(), imageFilePath);
      numberOfCreatedXML++;
      
      ocrIF.autoCreateXml = false;
    }
    
    currentHWObjcurr_canvas.reset();
    ocrIF.ocrTable.setRowSelectionAllowed(true);
    

    ocrIF.ocrTable.processSelectionEvent(selectedRows[0], 1, true);
    
    if (numberOfCreatedXML == 0) {
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        "XML files for each selected image already exist.", "Auto-Create XML", 
        1);
    }
    


    this_interfacemenuListener.actionPerformed(new ActionEvent(
      OCRInterface.this_interface, 
      128, 
      "Refresh Document List"));
    

    this_interfacetbdPane.setCursor(Cursor.getPredefinedCursor(0));
    
    ocrIF.autoCreateXml = false;
  }
  












  public void createXML(String dataFilePath, String imageFilePath)
  {
    System.out.println("create xml: " + dataFilePath);
    this_interfacetbdPane.setCursor(Cursor.getPredefinedCursor(3));
    int numMultiTiffPages = 1;
    if ((imageFilePath.toLowerCase().contains(".tif")) || (imageFilePath.toLowerCase().contains(".tiff"))) {
      numMultiTiffPages = ImageReaderDrawer.numPagesInMultiTiff(imageFilePath);
    }
    try {
      if (numMultiTiffPages == 1) {
        LoadDataFile.createNewDocument(dataFilePath);
      }
      else if (numMultiTiffPages > 1) {
        LoadDataFile.createNewMultiPageDocument(dataFilePath, numMultiTiffPages);
      }
      LoadDataFile datafile = new LoadDataFile(dataFilePath, 0, 0);
      OCRInterface.currDoc = datafile;
      DLPage currPage;
      for (int i = 0; i < ImageDisplay.nulldoc.size(); i++) {
        DLZone tempZone = (DLZone)ImageDisplay.nulldoc.get(i);
        currPage = OCRInterface.currDoc.get_zones_vec().getPage();
        tempZone.dlSetZonePage(currPage);
        
        currDocget_zones_vecgetPagepageZones.add(tempZone);
      }
      
      LinkedHashMap<String, String> nullPageAttributes = new LinkedHashMap();
      
      for (Map.Entry<String, TypeAttributeEntry> pageAtts : ((Map)OCRInterface.getAttsConfigUtil().getTypeAttributesMap().get("DL_PAGE")).entrySet()) {
        nullPageAttributes.put((String)pageAtts.getKey(), ((TypeAttributeEntry)pageAtts.getValue()).getDefaultValue());
      }
      
      LinkedList<DLPage> pages = OCRInterface.currDoc.getDocument().getdocPages();
      for (DLPage the_page : pages) {
        pageTags.putAll(nullPageAttributes);
      }
      OCRInterface.currDoc.dumpData();
      OCRInterface.getAttsConfigUtil().reloadCurrentlyOpenedDocument();
      

      int col = WorkmodeTable.selCol;
      int newSelectedRow = WorkmodeTable.selRow;
      int currentSelectedRow = WorkmodeTable.curRow;
      
      String newFilePath = OCRInterface.getCurrentImageDir() + 
        OCRInterface.getImageDirName() + File.separator + 
        this_interfaceocrTable.newFileName;
      


      FilePropPacket fpp = ocrIF.workmodeProps[0]
        .getElementFilePropVec(currentSelectedRow);
      ocrIF.addToRawXmlOnlyFileList(new File(dataFilePath));
      fpp.getDatafile_paths_hash().put(new Integer(1), dataFilePath);
      ocrIF.ocrTable.updateRow(fpp, currentSelectedRow);
      

      this_interfaceocrTable.update(col + 1, newSelectedRow, newFilePath);
      


      OCRInterface.this_interface.updateCommentsWindow(true);
    }
    catch (SAXException e) {
      System.out.println("createXML. SAXException");
      ocrIF.autoCreateXml = false;
      e.printStackTrace();
      File temp = new File(dataFilePath);
      String msg = temp.getAbsolutePath() + "\n\n" + 
        "WARNING: Unable to save XML -- " + 
        "either it is a read-only file or file path is incorrect.\n\n" + 
        "Problem description:\n" + e.toString();
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, 
        "Error writing to disk", 
        0);
      ocrIF.setAllowToSwitchImage(false);
    } catch (IOException e) {
      System.out.println("createXML. IOException");
      ocrIF.autoCreateXml = false;
      e.printStackTrace();
      File temp = new File(dataFilePath);
      String msg = temp.getAbsolutePath() + "\n\n" + 
        "WARNING: Unable to save XML -- " + 
        "either it is a read-only file or file path is incorrect.\n\n" + 
        "Problem description:\n" + e.toString();
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        msg, 
        "Error writing to disk", 
        0);
      ocrIF.setAllowToSwitchImage(false);
    } finally {
      ocrIF.tbdPane.setCursor(Cursor.getPredefinedCursor(0));
      ocrIF.ocrTable.setCursor(Cursor.getPredefinedCursor(0));
    }
  }
}
