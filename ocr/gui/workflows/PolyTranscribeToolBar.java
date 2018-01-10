package ocr.gui.workflows;

import gttool.document.DLZone;
import gttool.misc.Login;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import ocr.gui.BrowserToolBar;
import ocr.gui.Group;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.OrientedBox;
import ocr.gui.PolygonZone;
import ocr.gui.ReadingOrder;
import ocr.gui.SaveFilesDialog;
import ocr.gui.Zone;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.zones.ZonesManager;
import ocr.tag.GetDateTime;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.BidiString;
import ocr.util.UniqueZoneId;








public class PolyTranscribeToolBar
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private final char LRO = '‭';
  private final char PDF = '‬';
  private final char LTR = '‎';
  private final char RTL = '‏';
  
  CompoundBorder sel_border = BrowserToolBar.sel_border;
  
  CompoundBorder normal_border = BrowserToolBar.normal_border;
  
  private static JLabel startButton;
  
  private static JLabel lineButton;
  
  private static JLabel textButton;
  private static JLabel wordButton;
  private static JLabel splitwordButton;
  private static JLabel lockButton;
  private static JLabel QCButton;
  OCRInterface ocrIF;
  JFrame frame;
  String numLines = "0";
  boolean isReadingOrderOK = false;
  
  private JLabel fileCount;
  
  public static final String successReadingOrder = "99999";
  
  private String QCExtension = ".final.xml.qc";
  





  private Point warningWindowLocation = null;
  private Dimension warningWindowSize = null;
  
  public static String divider = "zoneID_Divider999";
  public static String UNREADABLE_REPLACE = "???";
  public static String JUNK_REPLACE = "xxx";
  


  private HashMap<String, String> markups = new HashMap(4);
  
  private TreeSet<String> reviewedZones = null;
  
  private boolean fix_corrupted_cellIDs = false;
  
  private PlanarImage displayedImage = null;
  

  public PolyTranscribeToolBar()
  {
    setLayout(new FlowLayout(0, 5, 5));
    setPreferredSize(new Dimension(1000, 40));
    
    ocrIF = OCRInterface.this_interface;
    
    addStateLabels();
    if (OCRInterface.this_interface.getEnablePolygonTranscription()) {
      enableActions();
    }
    fileCount = new JLabel("   Num. of Files Completed:  ");
    fileCount.setFont(new Font(fileCount.getFont().getFontName(), 0, 16));
    fileCount.setToolTipText("Number of Files Completed in this Worklist");
    fileCount.setAlignmentX(0.0F);
    
    add(fileCount);
    
    add(new JLabel("      "));
    
    disableButtons();
    
    setBorders(-1);
    
    markups.put("(", ")");
    markups.put("[", "]");
    markups.put("{", "}");
    markups.put("<", ">");
  }
  
  public void highlightOnTable(String image, int rowToSelect)
  {
    int mode = 0;
    int baseImageRow = ocrIF.workmodeProps[mode].getImageIndex(image);
    
    FilePropPacket fpp = ocrIF.workmodeProps[mode]
      .getElementFilePropVec(baseImageRow);
    
    ocrIF.ocrTable.removeVarietyOfExtRows(fpp, baseImageRow);
    
    ocrIF.workmodeProps[mode].removeElementFilePropVec(baseImageRow);
    

    ocrIF.addToTable2(image, mode, baseImageRow);
    
    fpp = ocrIF.workmodeProps[mode]
      .getElementFilePropVec(baseImageRow);
    
    fpp.setCollapsed(false);
    ocrIF.ocrTable.updateRow(fpp, baseImageRow);
    
    ocrIF.ocrTable.addVarietyOfExtRows(fpp, baseImageRow);
    
    ocrIF.ocrTable.processSelectionEvent(rowToSelect, 1, false);
    
    ocrIF.ocrTable.setColumnWidths();
  }
  






















  private boolean initTasks(String ext, int state)
  {
    OCRInterface ocrIF = OCRInterface.this_interface;
    saveFilesDialog.saveData();
    
    if ((state == 8) && (ocrTable.selFileName.contains(".final")) && (ext.equals(".splitword"))) {
      return false;
    }
    if (this_interfaceocrTable.newFileName == null) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        ext + " File Not Selected!", 
        "Error", 
        0);
      return false;
    }
    
    if (state == 0) {
      String clickedName = this_interfaceocrTable.newFileName;
      String gtType = OCRInterface.this_interface.checkGTType(clickedName);
      if (gtType != null) {
        JOptionPane.showMessageDialog(
          OCRInterface.this_interface, 
          ext + " File Not Selected!", 
          "Error", 
          0);
        return false;
      }
      return true;
    }
    

    if (!exists(ext)) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        ext + " File Does Not Exist!", 
        "Error", 
        0);
      if (this_interfacecurrState != -1)
        setBorders(this_interfacecurrState);
      return false;
    }
    
    if (!ocrTable.selFileName.contains(ext)) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        ext + " File Not Selected!", 
        "Error", 
        0);
      if (this_interfacecurrState != -1)
        setBorders(this_interfacecurrState);
      return false;
    }
    
    return true;
  }
  
  private boolean exists(String gtType) {
    String currentImage = ImageReaderDrawer.getFile_path();
    String basename = OCRInterface.this_interface.getFileNameWithoutExt(fullPath());
    String extension = OCRInterface.this_interface.getFileExtension(currentImage);
    currentImage = basename + gtType + extension;
    
    if (OCRInterface.this_interface.hasMeta(currentImage) == null) {
      return false;
    }
    return true;
  }
  
  private void setBorders(int whichState) {
    if (whichState == 0) {
      startButton.setBorder(sel_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border); }
    if (whichState == 1) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(sel_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 2) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(sel_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 3) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(sel_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 4) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(sel_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 7) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 8) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(sel_border);
    } else if (whichState == -1) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
      QCButton.setBorder(normal_border);
    }
  }
  
  private String fullPath() {
    String currentImagePath = ImageReaderDrawer.getFile_path();
    String xmlFilePath = OCRInterface.this_interface.hasMeta(currentImagePath);
    System.out.println("xmlFilePath: " + xmlFilePath);
    if (xmlFilePath == null) {
      String imageName = ImageReaderDrawer.file_name;
      xmlFilePath = OCRInterface.getCurrentXmlDir() + 
        OCRInterface.getXmlDirName() + 
        File.separator + 
        imageName.substring(0, imageName.lastIndexOf('.')) + ".xml";
      
      this_interfacesaveFilesDialog.createXML(xmlFilePath, currentImagePath);
    }
    
    return xmlFilePath;
  }
  
  private void setEnabledQCButton(boolean enabled) {
    if (enabled) {
      QCButton.setEnabled(true);
      QCButton.setBorder(sel_border);
    } else {
      QCButton.setEnabled(false);
      QCButton.setBorder(normal_border);
    }
  }
  
  public void disableButtons() {
    startButton.setEnabled(false);
    lineButton.setEnabled(false);
    textButton.setEnabled(false);
    wordButton.setEnabled(false);
    splitwordButton.setEnabled(false);
    lockButton.setEnabled(false);
    QCButton.setEnabled(false);
  }
  
  private void resetTypeWindow() {
    TypeWindow typeWindow = this_interfacetbdPane.data_panel.t_window;
    
    typeWindow.clear(true);
    OCRInterface.this_interface.getToolbar().setSelectedReadingOrderBtn(false);
    
    int n = typeWindow.getTable().getRowCount();
    
    for (int row = 0; row < n; row++) {
      typeWindow.makeTypeVisible(true, false, row, 3);
    }
    typeWindow.resetDrawingButtons();
  }
  
  public void enableState(int whichState) {
    resetTypeWindow();
    
    TypeWindow typeWindow = this_interfacetbdPane.data_panel.t_window;
    
    if (isQCFileExists()) {
      setEnabledQCButton(true);
    } else {
      setEnabledQCButton(false);
    }
    if (displayedImage != null) {
      OCRInterface.currentHWObj.setImageFile(displayedImage);
    }
    if (whichState == 0) {
      startButton.setEnabled(true);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false); }
    if (whichState == 1) {
      startButton.setEnabled(false);
      lineButton.setEnabled(true);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false);
      



      int rowIndex = typeWindow.getRowIndex(getTranscriptionType());
      
      if (OCRInterface.this_interface.getTranscribeUsingBoxes()) {
        typeWindow.selectNormalButton();
        OCRInterface.currOppmode = 2;
      }
      else {
        typeWindow.selectPolygonButton();
        OCRInterface.currOppmode = 16;
      }
      
      this_interfacetoolbar.setStickyMode(true);
      typeWindow.selectType(rowIndex, (short)2);
      
      OCRInterface.this_interface.getToolbar().setSelectedReadingOrderBtn(true);
    }
    else if (whichState == 2) {
      lineButton.setEnabled(false);
      textButton.setEnabled(true);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false);
      




      int rowIndex = typeWindow.getRowIndex(getTranscriptionType());
      
      int n = typeWindow.getTable().getRowCount();
      
      for (int row = 0; row < n; row++) {
        String type = typeWindow.getTable().getValueAt(row, 0).toString();
        if (row == rowIndex) {
          typeWindow.makeTypeVisible(true, false, row, 3);
        } else if ((!type.endsWith("_line")) && 
          (!type.endsWith("_word")) && 
          (!type.endsWith("_group"))) {
          typeWindow.makeTypeVisible(true, false, row, 3);
        }
        else {
          typeWindow.makeTypeVisible(false, false, row, 3);
        }
      }
      OCRInterface.this_interface.getToolbar().setSelectedReadingOrderBtn(true);
      
      reviewedZones = null;
    } else if (whichState == 3) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(true);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false);
      





      int lineRowIndex = typeWindow.getRowIndex(getTranscriptionType() + "_line");
      

      int n = typeWindow.getTable().getRowCount();
      
      for (int row = 0; row < n; row++)
      {
        if (row == lineRowIndex) {
          typeWindow.makeTypeVisible(false, true, row, 3);



        }
        else
        {



          typeWindow.makeTypeVisible(false, false, row, 3);
        }
      }
    } else if (whichState == 4) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(true);
      lockButton.setEnabled(false);
      



      setTypeWindowOnSplitword();














    }
    else if (whichState == 7) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 8) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(true);
      



      OCRInterface.this_interface.getToolbar().setSelectedReadingOrderBtn(true);
    } else if (whichState == -1) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == -2) {
      disableButtons();
    }
    
    setBorders(whichState);
    

    currentHWObjcurr_canvas.getShapeVec().refreshCount();
  }
  
  private void setTypeWindowOnSplitword() {
    TypeWindow typeWindow = this_interfacetbdPane.data_panel.t_window;
    
    int rowIndex = typeWindow.getRowIndex(getTranscriptionType() + "_word");
    
    int n = typeWindow.getTable().getRowCount();
    
    for (int row = 0; row < n; row++) {
      String type = typeWindow.getTable().getValueAt(row, 0).toString();
      if (row == rowIndex) {
        typeWindow.makeTypeVisible(true, false, row, 3);
      } else if ((!type.endsWith("_line")) && 
        (!type.endsWith("_word")) && 
        (!type.endsWith("_group"))) {
        typeWindow.makeTypeVisible(true, false, row, 3);
      }
      else
        typeWindow.makeTypeVisible(false, false, row, 3);
    }
  }
  
  public void setNumFiles(int numFiles, int numTotalFiles) {
    fileCount.setText("  Num. of Files Done:  " + numFiles + "  out of  " + numTotalFiles);
    
    fileCount.setForeground(Color.black);
    fileCount.setFont(new Font(fileCount.getFont().getFontName(), 0, 16));
    
    fileCount.updateUI();
  }
  
  private boolean checkForWordsAndSegments() {
    String warningMessage = "The number of words is NOT equal to the number of segments \nfor zones with the following IDs:\n\n";
    
    boolean foundProblemZone = false;
    Vector<DLZone> zones = currentHWObjcurr_canvas.getShapeVec().getAsVector();
    
    for (int i = 0; i < zones.size(); i++) {
      Zone zone = (Zone)zones.get(i);
      String contents = zone.getContents();
      if (contents != null)
      {
        contents = ImageAnalyzer.cleanBidiString(contents);
        String offsets = zone.getAttributeValue("offsets");
        String zoneId = zone.getAttributeValue("id");
        




        if (zone.offsetsReady())
        {

          int numWords = 0;int numSegments = 0;
          
          numWords = contents.split("[ \t]+").length;
          





          numSegments = offsets.isEmpty() ? 1 : offsets.split(",").length + 1;
          











          if (numWords != numSegments) {
            String numbers = " ==> (" + numWords + " words, " + numSegments + " segments)";
            warningMessage = warningMessage + zoneId + numbers + "\n";
            foundProblemZone = true;
          }
        }
      } }
    if (foundProblemZone) {
      warningMessage = warningMessage + "\nNOTE: Use CTRL+F to search zone by ID";
      
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        warningMessage, "Watch Out", 0);
      return false;
    }
    return true;
  }
  
  private void showWarning(String message)
  {
    String title = "";
    int messageType = 0;
    if (message.equals("99999")) {
      message = "Final Check is OK. \nLocking File.";
      messageType = 1;
      title = "Success";
      isReadingOrderOK = true;
    } else {
      message = 
      

        "Errors Found in Final Check.\n\nErrors:\n\n" + message + "\n\nNOTE:" + "\nUse CTRL+F to search zone by ID";
      messageType = 2;
      title = "Failure";
      isReadingOrderOK = false;
    }
    
    JOptionPane.showMessageDialog(OCRInterface.this_interface, 
      message, title, messageType);
  }
  
  private void setWaitCursor() {
    ocrIF.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
  }
  
  private void setDefaultCursor() {
    ocrIF.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
  
  public void enableActions() {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(112, 
      2), "POLYLINE");
    hotkeyManager.getActionMap().put("POLYLINE", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { System.out.println("polyline");
        
        if (!PolyTranscribeToolBar.this.initTasks(".TIF", 0)) {
          return;
        }
        PolyTranscribeToolBar.this.setWaitCursor();
        this_interfacesaveFilesDialog.saveData();
        
        String xmlIn = PolyTranscribeToolBar.this.fullPath();
        String xmlOut = xmlIn.replace(".xml", ".line.xml");
        if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
          PolyTranscribeToolBar.this.updateTable(new File(xmlOut));
        }
        enableState(1);
        PolyTranscribeToolBar.this.setDefaultCursor();
        GlobalHotkeyManager.getInstance().setEnabled(true);
      }
      
    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(113, 
      2), "TRANSCRIBE");
    hotkeyManager.getActionMap().put("TRANSCRIBE", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { OCRInterface.this_interface.getCanvas().unlockSoftLock();
        PolyTranscribeToolBar.this.F2Action(false);
      }
      
    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(114, 
      2), "POLYSEGWORD");
    hotkeyManager.getActionMap().put("POLYSEGWORD", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { OCRInterface.this_interface.getCanvas().unlockSoftLock();
        PolyTranscribeToolBar.this.cleanUpContent(getTranscriptionType());
        PolyTranscribeToolBar.this.F3Action(false);
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(115, 
      2), "POLYSPLITWORD");
    hotkeyManager.getActionMap().put("POLYSPLITWORD", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { OCRInterface.this_interface.getCanvas().unlockSoftLock();
        PolyTranscribeToolBar.this.F4Action(false);
      }
      
    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(116, 
      2), "POLYLOCK");
    hotkeyManager.getActionMap().put("POLYLOCK", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { System.out.println("POLYLOCK");
        PolyTranscribeToolBar.this.setWaitCursor();
        this_interfacesaveFilesDialog.saveData();
        PolyTranscribeToolBar.this.initTasks(".splitword", 8);
        


        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        

        PolyTranscribeToolBar.this.F5Action(false);

      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(117, 
      2), "POLYQC");
    hotkeyManager.getActionMap().put("POLYQC", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { System.out.println("POLYQC");
        
        if (!PolyTranscribeToolBar.this.initTasks(".final", 8)) {
          return;
        }
        this_interfacesaveFilesDialog.saveData();
        PolyTranscribeToolBar.this.setWaitCursor();
        
        if (PolyTranscribeToolBar.this.isQCFileExists()) {
          PolyTranscribeToolBar.this.deleteQCFile();
          PolyTranscribeToolBar.this.setEnabledQCButton(false);
        } else {
          PolyTranscribeToolBar.this.createQCFile();
          PolyTranscribeToolBar.this.setEnabledQCButton(true);
        }
        
        PolyTranscribeToolBar.this.setDefaultCursor();
        GlobalHotkeyManager.getInstance().setEnabled(true);
      }
      
    });
    hotkeyManager.setEnabled(true);
  }
  
  public void disableActions() {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getActionMap().remove("POLYLINE");
    hotkeyManager.getActionMap().remove("TRANSCRIBE");
    hotkeyManager.getActionMap().remove("POLYSEGWORD");
    hotkeyManager.getActionMap().remove("POLYSPLITWORD");
    hotkeyManager.getActionMap().remove("POLYLOCK");
    hotkeyManager.getActionMap().remove("POLYQC");
    hotkeyManager.getActionMap().remove("OPEN_DIGIT_TOKEN_WINDOW");
    
    hotkeyManager.setEnabled(true);
  }
  
  private void addStateLabels() {
    startButton = new JLabel(" 0. TIF  ");
    add(startButton);
    
    lineButton = new JLabel(" 1. Line ");
    add(lineButton);
    
    textButton = new JLabel(" 2. Transcribe ");
    add(textButton);
    
    wordButton = new JLabel(" 3. Word ");
    add(wordButton);
    
    splitwordButton = new JLabel(" 4. SplitWord ");
    add(splitwordButton);
    
    lockButton = new JLabel(" 5. Lock ");
    add(lockButton);
    
    QCButton = new JLabel(" 6. QC ");
    add(QCButton);
  }
  
  private boolean isQCFileExists() {
    String selectedName = this_interfaceocrTable.newFileName;
    String xmlDir = OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator;
    
    String ext = OCRInterface.this_interface.getFileExtension(selectedName);
    
    String baseName = OCRInterface.this_interface.getBaseImage(selectedName, ext);
    
    String imageName = new File(baseName).getName();
    
    String baseNameNoExt = OCRInterface.this_interface.getFileNameWithoutExt(imageName);
    
    File file = new File(xmlDir + baseNameNoExt + QCExtension);
    
    if (file.exists()) {
      return true;
    }
    return false;
  }
  
  private void deleteQCFile() {
    String xmlPath = OCRInterface.this_interface.getFileNameWithoutExt(fullPath());
    System.out.println("xmlPath: " + xmlPath);
    File file = new File(xmlPath + QCExtension);
    if (file.exists())
      file.delete();
  }
  
  private void createQCFile() {
    String xmlPath = OCRInterface.this_interface.getFileNameWithoutExt(fullPath());
    File file = new File(xmlPath + QCExtension);
    Writer output = null;
    if (!file.exists())
      try {
        file.createNewFile();
        output = new BufferedWriter(new FileWriter(file));
        output.write("UserName: " + Login.userName + "\n\n");
        output.write("Date: " + GetDateTime.getCurrentDateTime() + 
          " (" + "mm/dd/yyyy hh:mm" + ")");
        output.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
  
  private void updateTable(File newXml) {
    String baseImage = ImageReaderDrawer.getFile_path();
    ocrIF.addToRawXmlOnlyFileList(newXml);
    highlightOnTable(baseImage, WorkmodeTable.curRow + 1);
  }
  
  private void updateTable(File newXml, int row) {
    String baseImage = ImageReaderDrawer.getFile_path();
    ocrIF.addToRawXmlOnlyFileList(newXml);
    highlightOnTable(baseImage, row);
  }
  

  private void goToSeg()
  {
    String xmlIn = fullPath().replace(".xml", ".transcribe.xml");
    String xmlOut = xmlIn.replace(".transcribe.xml", ".word.xml");
    
    if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
      updateTable(new File(xmlOut));
      convertAllToPoly(getTranscriptionType());
      ImageAnalyzer.polygonSegWord();
    }
    
    OCRInterface.currDoc.dumpData();
    enableState(3);
  }
  
  private void goToTrans() {
    String xmlIn = fullPath().replace(".xml", ".line.xml");
    String xmlOut = xmlIn.replace(".line.xml", ".transcribe.xml");
    
    if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
      updateTable(new File(xmlOut));
    }
    
    OCRInterface.currDoc.dumpData();
    enableState(2);
  }
  
  private boolean isReadingOrderOK() {
    this_interfacesaveFilesDialog.saveData();
    
    int state = this_interfacecurrState;
    String typeToCheck = getTranscriptionType();
    if ((state == 4) || (state == 8)) {
      typeToCheck = typeToCheck + "_word";
    }
    String result = ImageAnalyzer.checkForReadingOrder(typeToCheck);
    
    if (result.trim().equalsIgnoreCase("99999")) {
      return true;
    }
    if ((result.trim().equalsIgnoreCase("No Reading Order Found")) || 
      (result.trim().equalsIgnoreCase("Multiple Reading Orders Found"))) {
      WarningWindow ww = new WarningWindow(result, "Reading Order Warning", 
        result);
      ww.setReadingOrderWarning();
      ww.showDialog();
      return false;
    }
    
    WarningWindow ww = new WarningWindow(result, "Reading Order Warning", 
      "<html>The following zone(s) out of Reading Order or of wrong type \n<br>(all zones in Reading Order must be of " + 
      
      getTranscriptionType() + 
      " type):");
    ww.setFatalWarning();
    ww.showDialog();
    return false;
  }
  







  private boolean inconsistentStatusExists()
  {
    this_interfacesaveFilesDialog.saveData();
    String result = ImageAnalyzer.markErrorPolygons(getTranscriptionType() + "_word");
    if (result.trim().isEmpty())
      return false;
    WarningWindow ww = new WarningWindow(result, "Inconsistent Status Warning", 
      "<html>The following tokens have <br>inconsistent status:</html>");
    
    ww.setFatalWarning();
    ww.showDialog();
    return true;
  }
  
  private boolean invalidPolygonTokensExist() {
    this_interfacesaveFilesDialog.saveData();
    String token = ImageAnalyzer.checkPolygonTokenization(getTranscriptionType());
    if (!token.isEmpty()) {
      WarningWindow ww = new WarningWindow(token, "Tokenization Warning", 
        "(Possible) Invalid tokens:");
      ww.setTokenizationWarning();
      ww.showDialog();
      return true;
    }
    return false;
  }
  
  private boolean polygonIntersectionsExist() {
    this_interfacesaveFilesDialog.saveData();
    String intersection = ImageAnalyzer.checkPolygonIntersection();
    if (!intersection.isEmpty())
    {
      WarningWindow ww = new WarningWindow(intersection, "Intersecting Lines Warning", 
        "Intersecting lines detected:");
      ww.setIntersectingLineWarning();
      ww.showDialog();
      
      return true;
    }
    return false;
  }
  
  private boolean emptyLinesExist() {
    this_interfacesaveFilesDialog.saveData();
    int state = this_interfacecurrState;
    String typeToCheck = getTranscriptionType();
    if ((state == 4) || (state == 8)) {
      typeToCheck = typeToCheck + "_word";
    }
    String empty = ImageAnalyzer.checkForEmptyZones(typeToCheck);
    
    if (!empty.isEmpty()) {
      WarningWindow ww = new WarningWindow(empty, "Empty Lines Warning", 
        "Empty Lines/Zones Detected:");
      ww.setFatalWarning();
      ww.showDialog();
      return true;
    }
    return false;
  }
  
  private boolean multiWordsExist() {
    this_interfacesaveFilesDialog.saveData();
    String empty = ImageAnalyzer.checkForMultiWord();
    if (!empty.isEmpty()) {
      WarningWindow ww = new WarningWindow(empty, "Multiple Words Warning", 
        "Zone contents have more than one word:");
      ww.setFatalWarning();
      ww.showDialog();
      return true;
    }
    return false;
  }
  
  private boolean nonPolyZonesExist() {
    this_interfacesaveFilesDialog.saveData();
    String empty = ImageAnalyzer.checkForNonPolygon(getTranscriptionType() + "_word");
    if (!empty.isEmpty()) {
      WarningWindow ww = new WarningWindow(empty, "Non Polygons Warning", 
        "Non-polygon zone(s) found: ");
      ww.setFatalWarning();
      ww.showDialog();
      return true;
    }
    return false;
  }
  
  private boolean wrongTypesExist(String type) {
    this_interfacesaveFilesDialog.saveData();
    String empty = ImageAnalyzer.checkForType(type).trim();
    if (empty.isEmpty()) {
      WarningWindow ww = new WarningWindow("", "Zone types Warning", 
        "<html>No zones of " + type + " have been found.\n" + 
        "<br>Check Trancription Type in Preferences.");
      ww.setFatalWarning();
      ww.showDialog();
      if (this_interfacecurrState == 4)
        resetTypeWindow();
      return true;
    }
    if (this_interfacecurrState == 4)
      setTypeWindowOnSplitword();
    return false;
  }
  
  private boolean missingLineIDExist() {
    this_interfacesaveFilesDialog.saveData();
    String empty = ImageAnalyzer.checkForLineID(getTranscriptionType() + "_word");
    if (!empty.isEmpty()) {
      WarningWindow ww = new WarningWindow(empty, "Missing LineID Warning", 
        "Zones missing LineID:");
      ww.setFatalWarning();
      ww.showDialog();
      return true;
    }
    return false;
  }
  
  private void polygonIntersectionProblem_On_F4(String polygonIDs) {
    this_interfacesaveFilesDialog.saveData();
    
    WarningWindow ww = new WarningWindow(polygonIDs, "Invalid Polygon Intersection", 
      "<html>Line Box offsets must overlap Polygons. Adjust Boxes (and/or corresponding Polygons) for the following Box IDs: (polygons can be made visible on the zone type table to the left)</html>");
    

    ww.setFatalWarning();
    ww.showDialog();
  }
  
  private void F2Action(boolean goToTrans) {
    System.out.println("TRANSCRIBE");
    
    if (!initTasks(".line", 2)) {
      return;
    }
    this_interfacesaveFilesDialog.saveData();
    setWaitCursor();
    





















    if (wrongTypesExist(getTranscriptionType())) {
      return;
    }
    



    if ((!goToTrans) && (polygonIntersectionsExist())) {
      return;
    }
    goToTrans = true;
    
    if (goToTrans) {
      goToTrans();
    } else {
      highlightOnTable(ImageReaderDrawer.getFile_path(), WorkmodeTable.curRow);
    }
    
    setDefaultCursor();
    GlobalHotkeyManager.getInstance().setEnabled(true);
  }
  
  private void F3Action(boolean goToSeg) {
    System.out.println("POLYSEGWORD");
    
    if (!initTasks(".transcribe", 3)) {
      return;
    }
    this_interfacesaveFilesDialog.saveData();
    setWaitCursor();
    






    if (emptyLinesExist()) {
      return;
    }
    if (!isReadingOrderOK()) {
      return;
    }
    if ((!goToSeg) && (invalidPolygonTokensExist())) {
      return;
    }
    goToSeg = true;
    
    if (goToSeg) {
      if (OCRInterface.this_interface.getEnforceTranscriptionReview()) {
        WarningWindow ww = new WarningWindow("", "Transcription Final Review", "");
        ww.setTranscriptionReviewWarning();
        ww.showDialog();
        return;
      }
      
      Vector<DLZone> shapeVec = currentHWObjcurr_canvas.getShapeVec().getAsVector();
      OCRInterface.this_interface.shrinkZones(shapeVec, true);
      goToSeg();
    }
    else {
      highlightOnTable(ImageReaderDrawer.getFile_path(), WorkmodeTable.curRow);
    }
    
    setDefaultCursor();
    GlobalHotkeyManager.getInstance().setEnabled(true);
  }
  
  private void F4Action(boolean goToNext) {
    System.out.println("POLYSPLITWORD");
    



    if (this_interfacecurrState == 4) {
      ImageAnalyzer.markErrorPolygons(getTranscriptionType() + "_word");
      OCRInterface.currDoc.dumpData();
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        "<html>Status check routine was successful. <br>It is possible that status for some zones was changed by the check-up routine.</html>", 
        
        "Status check", 1);
      return;
    }
    
    if (!initTasks(".word", 4)) {
      return;
    }
    if (!checkForWordsAndSegments()) {
      return;
    }
    this_interfacesaveFilesDialog.saveData();
    setWaitCursor();
    
    String xmlIn = fullPath().replace(".xml", ".word.xml");
    String xmlOut = xmlIn.replace(".word.xml", ".splitword.xml");
    
    if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
      updateTable(new File(xmlOut));
      ImageAnalyzer.polySplit(getTranscriptionType());
    }
    

    Vector<String> problemPolygons = ImageAnalyzer.clipAllToPoly(getTranscriptionType());
    
    Vector<DLZone> shapeVec = currentHWObjcurr_canvas.getShapeVec().getAsVector();
    OCRInterface.this_interface.shrinkZones(shapeVec, true);
    
    if ((problemPolygons != null) && (!problemPolygons.isEmpty())) {
      updateTable(new File(xmlIn), WorkmodeTable.curRow - 1);
      enableState(3);
      String problem = "";
      Iterator localIterator2; for (Iterator localIterator1 = problemPolygons.iterator(); localIterator1.hasNext(); 
          
          localIterator2.hasNext())
      {
        String id = (String)localIterator1.next();
        ZonesManager vec = currentHWObjcurr_canvas.getShapeVec();
        localIterator2 = vec.iterator(); continue;DLZone temp = (DLZone)localIterator2.next();
        String bound_of = (String)temp.getZoneTags().get("bounds_of");
        System.out.println("temp: " + temp + "/" + bound_of);
        if ((bound_of != null) && (bound_of.equalsIgnoreCase(id))) {
          System.out.println("problem on polygon/box: " + id + "/" + zoneID);
          problem = problem + zoneID + "\n";
        }
      }
      

      polygonIntersectionProblem_On_F4(problem);
      return;
    }
    
    ImageAnalyzer.markErrorPolygons(getTranscriptionType() + "_word");
    
    normalizePolygons();
    OCRInterface.currDoc.dumpData();
    enableState(4);
    setDefaultCursor();
    
    OCRInterface.this_interface.updateETextWindow();
    GlobalHotkeyManager.getInstance().setEnabled(true);
  }
  


  private void F5Action(boolean goToNext)
  {
    if (nonPolyZonesExist()) {
      return;
    }
    if (emptyLinesExist()) {
      return;
    }
    if (multiWordsExist()) {
      return;
    }
    if (missingLineIDExist()) {
      return;
    }
    if (!isReadingOrderOK()) {
      return;
    }
    if (!isFirstZoneInReadingOrderOK()) {
      return;
    }
    String message = "99999";
    
    if ((message.trim().equals("99999")) && 
      (this_interfacecurrState == 4))
    {

      String xmlIn = fullPath().replace(".xml", ".splitword.xml");
      String xmlOut = xmlIn.replace(".splitword.xml", ".final.xml");
      ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut));
      
      updateTable(new File(xmlOut));
      
      enableState(8);
    }
    
    if (message.trim().equals("99999"))
    {
      if (this_interfacecurrState == 8) {
        ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
        for (DLZone temp : shapeVec) {
          if (temp.isGroup()) {
            ((Group)temp).deleteGroup();
          }
        }
        


        OCRInterface.currDoc.dumpData();
      }
      
      if (inconsistentStatusExists()) {
        return;
      }
      if (corruptedCellIDsExist()) {
        return;
      }
      removeMarkUp();
      OCRInterface.currDoc.dumpData();
      
      showWarning(message.trim());
      

      int curCol = 1;
      FilePropPacket fpp = ocrIF.workmodeProps[ocrIF.ocrTable.my_mode]
        .getElementFilePropVec(WorkmodeTable.curRow);
      fpp.setSoftLocked(curCol, true, true);
    }
    

    setDefaultCursor();
    GlobalHotkeyManager.getInstance().setEnabled(true);
  }
  
  private void cleanUpContent(String transcriptionType) {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    
    for (DLZone temp : shapeVec) {
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
      {
        String newContent = "";
        if ((temp.getContents() != null) && (!temp.getContents().trim().isEmpty()))
        {


          String content = ImageAnalyzer.cleanBidiString(temp.getContents().trim());
          
          String[] tokensSt = content.split("\\s+");
          String tokenRegEx = "\\p{Punct}";
          
          for (String s : tokensSt) {
            if (s.matches(tokenRegEx)) {
              newContent = newContent + " " + s;
            } else {
              char[] tokenChars = s.toCharArray();
              boolean letterFound = false;
              for (char c : tokenChars) {
                if (Character.isLetter(c)) {
                  letterFound = true;
                  break;
                }
              }
              

              if (letterFound) {
                newContent = newContent + " " + s;
              } else {
                BidiString bd = new BidiString(content, 3);
                if (bd.getDirection() == 0) {
                  newContent = newContent + " " + s;
                } else {
                  s = '‭' + s + '‬';
                  newContent = newContent + " ‎" + s + '‏';
                }
              }
            }
          }
          
          temp.setContents(newContent.trim());
          OCRInterface.currDoc.dumpData();
        }
      }
    }
  }
  
  private Component strut(int width, int height) {
    return Box.createRigidArea(new Dimension(width, height));
  }
  






  public String getTranscriptionType()
  {
    String transcriptionType = OCRInterface.this_interface.getPolygonTranscriptionType().trim();
    if ((transcriptionType != null) && 
      (!transcriptionType.isEmpty())) {
      return transcriptionType;
    }
    TypeWindow typeWindow = this_interfacetbdPane.data_panel.t_window;
    
    for (String type : typeWindow.getTypeSettings().keySet()) {
      if ((!type.endsWith("_line")) && 
        (!type.endsWith("_word")) && 
        (!type.endsWith("_group"))) {
        return type;
      }
    }
    
    return "";
  }
  
  private void removeMarkUp()
  {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String content = null;
    for (DLZone temp : shapeVec) {
      content = temp.getContents();
      if ((content != null) && (content.length() != 1))
      {

        content = ImageAnalyzer.cleanBidiString(content.trim());
        
        if ((content.equals("()")) || (content.equals("[]"))) {
          content = UNREADABLE_REPLACE;
          temp.setAttributeValue("contents", content);
        }
        else if (content.equals("{}")) {
          content = JUNK_REPLACE;
          temp.setAttributeValue("contents", content);
        }
        else {
          for (Map.Entry<String, String> e : markups.entrySet()) {
            String openMarkup = (String)e.getKey();
            String closedMarkup = (String)e.getValue();
            int end = content.length() - 1;
            if ((content.startsWith(openMarkup)) && 
              (content.endsWith(closedMarkup)) && 
              (!Character.isWhitespace(content.toCharArray()[1])) && 
              (!Character.isWhitespace(content.toCharArray()[(end - 1)]))) {
              content = content.substring(1, end);
              temp.setAttributeValue("contents", content);
            }
          }
        }
      }
    }
  }
  
  private void convertAllToPoly(String transcriptionType) { ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone zone : shapeVec) {
      if (zone.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
      {
        if (!(zone instanceof PolygonZone)) {
          String zoneID = zone.getAttributeValue("id");
          PolygonZone newPoly = new PolygonZone(getVertices((Zone)zone));
          newPoly.getZoneTags().putAll(zone.getZoneTags());
          newPoly.setZoneType(zone.getZoneType());
          
          newPoly.dlSetPagePointer(shapeVec.getPage());
          newPoly.setAttributeValue("id", zoneID);
          zoneID = zoneID;
          previousZone = previousZone;
          nextZone = nextZone;
          if (previousZone != null) {
            previousZone.nextZone = newPoly;
          } else {
            previousZone = null;
          }
          if (nextZone != null) {
            nextZone.previousZone = newPoly;
          } else {
            nextZone = null;
          }
          shapeVec.add(newPoly);
          shapeVec.remove(zone);
        } }
    }
  }
  
  private Vector<Point> getVertices(Zone zone) {
    Vector<Point> v = new Vector();
    Point localPoint1;
    Point p; if ((zone instanceof OrientedBox)) {
      OrientedBox obox = (OrientedBox)zone;
      obox.getZoneTags().remove("orientationD");
      Point[] arrayOfPoint2; int i = (arrayOfPoint2 = obox.getPolygonVertices(obox.getPolygon())).length; for (localPoint1 = 0; localPoint1 < i; localPoint1++) { p = arrayOfPoint2[localPoint1];
        v.add(p);
      }
    } else if ((zone instanceof Zone)) { Point[] arrayOfPoint1;
      localPoint1 = (arrayOfPoint1 = zone.getZoneCorners()).length; for (p = 0; p < localPoint1; p++) { Point p = arrayOfPoint1[p];
        v.add(p);
      }
    }
    return v;
  }
  





  private void normalizePolygons()
  {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone zone : shapeVec)
      if ((zone instanceof PolygonZone))
      {
        PolygonZone poly = (PolygonZone)zone;
        poly.normalizePolygon();
      }
  }
  
  public void addToReviewedZones(String zoneId) {
    if (reviewedZones == null) {
      return;
    }
    reviewedZones.add(zoneId);
  }
  
  private boolean allZonesReviewed() {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone zone : shapeVec) {
      String id = zone.getAttributeValue("id").trim();
      if (!reviewedZones.contains(id))
        return false;
    }
    return true;
  }
  





  private boolean isTable()
  {
    ReadingOrder ro = OCRInterface.this_interface.getCanvas().getReadingOrderHandler();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    
    for (DLZone zone : shapeVec) {
      if ((ro.isHeader(zone)) && (hasNewCelAttr(zone)))
        return true;
    }
    return false;
  }
  
  private boolean hasNewCelAttr(DLZone zone) {
    return zone.hasAttribute("newcell");
  }
  
  private boolean isNewCellTrue(DLZone zone) {
    String value = zone.getAttributeValue("newcell");
    return (value != null) && (value.trim().equalsIgnoreCase("true"));
  }
  










  private boolean isFirstZoneInReadingOrderOK()
  {
    ReadingOrder ro = OCRInterface.this_interface.getCanvas().getReadingOrderHandler();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    DLZone head = null;
    for (DLZone zone : shapeVec) {
      if (!isTable())
        return true;
      if (ro.isHeader(zone))
        head = zone;
      if ((ro.isHeader(zone)) && 
        (hasNewCelAttr(zone)) && 
        (isNewCellTrue(zone))) {
        return true;
      }
    }
    WarningWindow ww = new WarningWindow(zoneID, "New Cell Warning", 
      "<html>The first zone in Reading order must have newcell=true.</html>");
    ww.setFatalWarning();
    ww.showDialog();
    return false;
  }
  
  private boolean corruptedCellIDsExist() {
    if (!isTable()) {
      return false;
    }
    ReadingOrder ro = OCRInterface.this_interface.getCanvas().getReadingOrderHandler();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    DLZone head = null;
    
    for (DLZone zone : shapeVec) {
      if (ro.isHeader(zone)) {
        head = zone;
        break;
      }
    }
    
    int cellID = 0;
    String corruptedIDs = "";
    
    while (head != null)
    {
      String newcell_value = head.getAttributeValue("newcell");
      String cellID_value = head.getAttributeValue("cellID");
      
      if ((head.hasAttribute("newcell")) && (newcell_value.equalsIgnoreCase("true"))) {
        cellID++;
      }
      if ((!fix_corrupted_cellIDs) && (cellID_value != null) && (!cellID_value.isEmpty()) && (!cellID_value.equalsIgnoreCase(cellID))) {
        corruptedIDs = corruptedIDs + zoneID + "\n";
      } else {
        head.setAttributeValue("cellID", cellID);
      }
      head = nextZone;
    }
    
    if (corruptedIDs.trim().isEmpty()) {
      return false;
    }
    WarningWindow ww = new WarningWindow(corruptedIDs, "Cell ID Warning", 
      "<html>Cell ID's are corrupted. Recompute?</html>");
    ww.setFatalWarning(true);
    ww.showDialog();
    return true;
  }
  
  public void setDisplayedImageImage(PlanarImage image)
  {
    displayedImage = image;
  }
  
  public PlanarImage getDisplayedImage()
  {
    return OCRInterface.currentHWObj.getOriginalImage();
  }
  
  public class WarningWindow extends JDialog
  {
    private PolyTranscribeToolBar.LinkLabel tokenZoneID = null;
    private JFrame parent = null;
    private JButton yesBtn;
    private JButton noBtn; private JButton checkAgainBtn; private JButton okBtn; private JDialog dlg = null;
    private String[] invalidTokens;
    private String description;
    private Dimension specialSize = new Dimension(470, 470);
    private Dimension defaultSize = new Dimension(400, 330);
    private String windowTitle = null;
    private boolean secondStep = false;
    private boolean thirdStep = false;
    

    public WarningWindow(String text, String windowTitle, String description)
    {
      parent = OCRInterface.this_interface;
      if (text.trim().isEmpty()) {
        invalidTokens = new String[0];
      } else
        invalidTokens = text.trim().split("\n");
      this.description = description;
      this.windowTitle = windowTitle;
      

      yesBtn = new JButton("Yes");
      noBtn = new JButton("No");
      checkAgainBtn = new JButton("Check again");
      okBtn = new JButton("OK");
      
      dlg = null;
      dlg = new JDialog();
      dlg.getContentPane().setLayout(new BoxLayout(dlg.getContentPane(), 1));
      
      if (warningWindowSize == null) {
        warningWindowSize = defaultSize;
      }
      dlg.setSize(warningWindowSize);
      dlg.setPreferredSize(warningWindowSize);
      
      dlg.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent arg0)
        {
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            OCRInterface.this_interface.getTranslateWorkflowPanel().setPairPanel(null);
          }
          hideDialog();

        }
        

      });
      dlg.setTitle(windowTitle);
      Border line = BorderFactory.createLineBorder(Color.black);
      Border empty = BorderFactory.createEmptyBorder(20, 20, 20, 20);
      ((JComponent)dlg.getContentPane()).setBorder(BorderFactory.createCompoundBorder(line, empty));
    }
    
    public void setTranscriptionReviewWarning() {
      if (reviewedZones == null) {
        reviewedZones = new TreeSet();
      }
      DLZone firstZone = OCRInterface.this_interface.getUniqueZoneIdObj().searchZone("1", false);
      if (firstZone != null) {
        OCRInterface.this_interface.getCanvas().addToSelected((Zone)firstZone);
      }
      initTranscriptionReviewWarning();
      addActionListenersForTranscriptionReviewWarning();
    }
    
    public void setReadingOrderWarning() {
      initReadingOrderWarning();
      addActionListenersForReadingOrderWarning();
    }
    
    public void setTokenizationWarning() {
      initTokenizationWarning();
      addActionListenersForTokenizationWarning();
    }
    
    public void setIntersectingLineWarning() {
      initIntersectingLineWarning();
      addActionListenersForIntersectingLineWarning();
    }
    
    public void setBlankZonesWarning() {
      initBlankZonesWarning();
      addActionListenersForBlankZonesWarning();
    }
    
    public void setFatalWarning() {
      initFatalWarning(false);
      addActionListenersForFatalWarning();
    }
    
    public void setFatalWarning(boolean add_yesBtn) {
      initFatalWarning(add_yesBtn);
      addActionListenersForFatalWarning();
    }
    
    private void addActionListenersForReadingOrderWarning() {
      okBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
        
      });
      checkAgainBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            OCRInterface.this_interface.getTranslateWorkflowPanel().isReadingOrderOK();
          }
          else {
            int state = this_interfacecurrState;
            
            if (state == 2) {
              PolyTranscribeToolBar.this.F3Action(false);
            }
            else if ((state == 4) || (state == 8))
              PolyTranscribeToolBar.this.F5Action(false);
          }
        }
      });
    }
    
    private void addActionListenersForTokenizationWarning() {
      yesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          int state = this_interfacecurrState;
          if (state == 2)
          {
            PolyTranscribeToolBar.this.F3Action(true);

          }
          else if ((state == 4) || (state == 8))
          {
            PolyTranscribeToolBar.this.F5Action(false);
          }
          
        }
        
      });
      noBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
        
      });
      checkAgainBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          int state = this_interfacecurrState;
          


          if (state == 2) {
            PolyTranscribeToolBar.this.F3Action(false);
          } else if ((state == 4) || (state == 8))
            PolyTranscribeToolBar.this.F5Action(false);
        }
      });
    }
    
    private void addActionListenersForIntersectingLineWarning() {
      yesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          int state = this_interfacecurrState;
          
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            PolyTranscribeToolBar.this.setWaitCursor();
            if (secondStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part2(true);
            } else
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify(true);
            PolyTranscribeToolBar.this.setDefaultCursor();

          }
          else if (state == 1)
          {
            PolyTranscribeToolBar.this.F2Action(true);
          } else if ((state == 4) || (state == 8)) {
            PolyTranscribeToolBar.this.F5Action(true);
          }
          
        }
        
      });
      noBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
        
      });
      checkAgainBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          int state = this_interfacecurrState;
          
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            if (secondStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part2(false);
            } else {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify(false);
            }
            

          }
          else if (state == 1) {
            PolyTranscribeToolBar.this.F2Action(false);
          } else if ((state == 4) || (state == 8)) {
            PolyTranscribeToolBar.this.F5Action(false);
          }
        }
      });
    }
    
    private void addActionListenersForFatalWarning() {
      if (yesBtn != null) {
        yesBtn.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e) {
            int state = this_interfacecurrState;
            
            if (!fix_corrupted_cellIDs) {
              hideDialog();
              if ((state == 4) || (state == 8)) {
                fix_corrupted_cellIDs = true;
                PolyTranscribeToolBar.this.F5Action(false);
                fix_corrupted_cellIDs = false;
              }
            }
          }
        });
      }
      
      okBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
        
      });
      checkAgainBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            OCRInterface.this_interface.getTranslateWorkflowPanel().verify(false);
          }
          else {
            int state = this_interfacecurrState;
            
            if (state == 1) {
              PolyTranscribeToolBar.this.F2Action(false);
            }
            else if (state == 2) {
              PolyTranscribeToolBar.this.F3Action(false);
            }
            else if (state == 3) {
              PolyTranscribeToolBar.this.F4Action(false);
            }
            else if ((state == 4) || (state == 8))
              PolyTranscribeToolBar.this.F5Action(false);
          }
        }
      });
    }
    
    private void addActionListenersForTranscriptionReviewWarning() {
      yesBtn.setText("Done");
      noBtn.setText("Cancel");
      yesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          if (!PolyTranscribeToolBar.this.allZonesReviewed()) {
            JOptionPane.showMessageDialog(dlg, "Not all zones have been reviewed !", 
              windowTitle, 2);
            return;
          }
          hideDialog();
          PolyTranscribeToolBar.this.goToSeg();
        }
        
      });
      noBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
      });
    }
    
    private void addActionListenersForBlankZonesWarning() {
      yesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          
          if (OCRInterface.this_interface.getEnableTranslateWorkflow())
          {
            if (thirdStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part3(true);
            } else if (secondStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part2(true);
            } else {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify(true);
            }
          }
        }
      });
      noBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
        }
        
      });
      checkAgainBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          hideDialog();
          
          if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
            if (thirdStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part3(false);
            } else if (secondStep) {
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify_part2(false);
            } else
              OCRInterface.this_interface.getTranslateWorkflowPanel().verify(false);
          }
        }
      });
    }
    
    private void initReadingOrderWarning() {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      if (invalidTokens[0].trim().equalsIgnoreCase("Multiple Reading Orders Found")) {
        label.setText("<html>" + label.getText() + "<br><br>" + 
          "Tip: to select all Reading Order Headers, deselect all zones and " + 
          "select the \"Highlight all RO headers\" menu item on the right mouse click.</html>");
      } else if (!invalidTokens[0].trim().equalsIgnoreCase("No Reading Order Found"))
      {


        label.setText("Zone(s) Out of Reading Order");
        for (int i = 0; i < invalidTokens.length; i++) {
          String[] parts = invalidTokens[i].trim().split(PolyTranscribeToolBar.divider);
          tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, parts[0].trim(), null);
          tokenZoneID.setMaximumSize(new Dimension(40, 20));
          panel.add(tokenZoneID);
        }
        dlg.getContentPane().add(new JScrollPane(panel));
      }
      
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      







      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      btnPanel.add(okBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(checkAgainBtn);
      
      dlg.getContentPane().add(btnPanel);
    }
    












    public void initTokenizationWarning()
    {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      for (int i = 0; i < invalidTokens.length; i++) {
        JPanel labelPanel = new JPanel();
        labelPanel.setAlignmentX(0.0F);
        labelPanel.setLayout(new BoxLayout(labelPanel, 0));
        
        String[] parts = invalidTokens[i].trim().split(PolyTranscribeToolBar.divider);
        tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, parts[0].trim(), null);
        tokenZoneID.setMaximumSize(new Dimension(40, 20));
        
        String toketDetailsStr = "";
        if (parts.length > 1) {
          toketDetailsStr = "   " + parts[1].trim() + "   ";
        }
        labelPanel.add(tokenZoneID);
        JLabel tokenDetails = new JLabel(toketDetailsStr);
        tokenDetails.setFont(new JLabel().getFont().deriveFont(16.0F));
        labelPanel.add(tokenDetails);
        
        panel.add(labelPanel);
      }
      
      dlg.getContentPane().add(new JScrollPane(panel));
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      label = new JLabel("Continue anyway?");
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      btnPanel.add(yesBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(noBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(checkAgainBtn);
      

      dlg.getContentPane().add(btnPanel);
    }
    
    public void initIntersectingLineWarning() {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      JLabel label2 = new JLabel("(hold CTRL or SHIFT to select second zone of the pair)");
      label2.setFont(new JLabel().getFont().deriveFont(11.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 10));
      dlg.getContentPane().add(label2);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 5));
      

      for (int i = 0; i < invalidTokens.length; i++)
      {
        JPanel labelPanel = new JPanel();
        
        labelPanel.setAlignmentX(0.0F);
        labelPanel.setLayout(new BoxLayout(labelPanel, 0));
        
        labelPanel.add(new JLabel("   ( "));
        
        String[] parts = invalidTokens[i].trim().split(",");
        
        PolyTranscribeToolBar.LinkLabel selectBothLabel = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, "Select pair", parts[0].trim(), parts[1].trim(), null);
        
        tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, parts[0].trim(), null);
        tokenZoneID.setMaximumSize(new Dimension(50, 20));
        labelPanel.add(tokenZoneID);
        
        selectBothLabel.setZone1(tokenZoneID);
        
        labelPanel.add(new JLabel("  ,  "));
        
        tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, parts[1].trim(), null);
        tokenZoneID.setMaximumSize(new Dimension(50, 20));
        labelPanel.add(tokenZoneID);
        selectBothLabel.setZone2(tokenZoneID);
        
        labelPanel.add(new JLabel(")"));
        


        labelPanel.add(Box.createHorizontalStrut(5));
        labelPanel.add(selectBothLabel);
        
        panel.add(labelPanel);
        panel.add(Box.createVerticalStrut(5));
      }
      
      if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
        OCRInterface.this_interface.getTranslateWorkflowPanel().setPairPanel(panel);
      }
      dlg.getContentPane().add(new JScrollPane(panel));
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      label = new JLabel("Continue anyway?");
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      btnPanel.add(yesBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(noBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(checkAgainBtn);
      
      dlg.getContentPane().add(btnPanel);
    }
    
    private void initBlankZonesWarning() {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      

      for (int i = 0; i < invalidTokens.length; i++) {
        JPanel labelPanel = new JPanel();
        labelPanel.setAlignmentX(0.0F);
        labelPanel.setLayout(new BoxLayout(labelPanel, 0));
        


        tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, invalidTokens[i].trim(), null);
        tokenZoneID.setMaximumSize(new Dimension(50, 20));
        labelPanel.add(tokenZoneID);
        








        panel.add(labelPanel);
      }
      
      if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
        OCRInterface.this_interface.getTranslateWorkflowPanel().setPairPanel(panel);
      }
      dlg.getContentPane().add(new JScrollPane(panel));
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      label = new JLabel("Continue anyway?");
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      btnPanel.add(yesBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(noBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(checkAgainBtn);
      
      dlg.getContentPane().add(btnPanel);
    }
    
    private void initFatalWarning(boolean add_yesBtn) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      
      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      for (int i = 0; i < invalidTokens.length; i++) {
        JPanel labelPanel = new JPanel();
        labelPanel.setAlignmentX(0.0F);
        labelPanel.setLayout(new BoxLayout(labelPanel, 0));
        
        String[] parts = invalidTokens[i].trim().split(PolyTranscribeToolBar.divider);
        tokenZoneID = new PolyTranscribeToolBar.LinkLabel(PolyTranscribeToolBar.this, parts[0], null);
        tokenZoneID.setMaximumSize(new Dimension(50, 20));
        
        String toketDetailsStr = "";
        if (parts.length > 1) {
          toketDetailsStr = "   " + parts[1].trim() + "   ";
        }
        labelPanel.add(tokenZoneID);
        
        JLabel tokenDetails = new JLabel(toketDetailsStr);
        tokenDetails.setFont(new JLabel().getFont().deriveFont(16.0F));
        labelPanel.add(tokenDetails);
        
        panel.add(labelPanel);
      }
      
      if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
        OCRInterface.this_interface.getTranslateWorkflowPanel().setPairPanel(panel);
      }
      if (invalidTokens.length != 0) {
        dlg.getContentPane().add(new JScrollPane(panel));
      }
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      if (add_yesBtn) {
        btnPanel.add(yesBtn);
      } else
        btnPanel.add(okBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(10, 0));
      btnPanel.add(checkAgainBtn);
      
      dlg.getContentPane().add(btnPanel);
    }
    
    private void initTranscriptionReviewWarning() {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, 1));
      
      dlg.setSize(specialSize);
      
      description = "<html><p align=center><span style=font-weight: 700><font size=6 color=red>Transcription Final Review:</font></span></p><p><p>Before you are finished and move to the word level, you must carefully review each line for missing text, incorrect tokenization and errors in reading order.&nbsp; Please</p><ul><li>Use CTRL +/- to adjust text size to make it close to the size of the image</li><li>Tab through each zone and check contents carefully</li><li>All zones must be reviewed (the DONE button will not work until you do!)</li></ul><p>Your work will not be complete until you have moved to the word stage.</p><br></html>";
      
























      JLabel label = new JLabel(description);
      label.setFont(new JLabel().getFont().deriveFont(16.0F));
      
      dlg.getContentPane().add(label);
      dlg.getContentPane().add(PolyTranscribeToolBar.this.strut(0, 20));
      
      JPanel btnPanel = new JPanel();
      btnPanel.setAlignmentX(0.0F);
      btnPanel.setLayout(new BoxLayout(btnPanel, 0));
      btnPanel.add(PolyTranscribeToolBar.this.strut(120, 0));
      btnPanel.add(yesBtn);
      btnPanel.add(PolyTranscribeToolBar.this.strut(20, 0));
      btnPanel.add(noBtn);
      
      dlg.getContentPane().add(btnPanel);
    }
    
    public void showDialog() {
      if (warningWindowLocation == null) {
        dlg.setLocationRelativeTo(parent);
      } else
        dlg.setLocation(warningWindowLocation);
      dlg.setVisible(true);
      dlg.paint(dlg.getGraphics());
      dlg.setFocusable(true);
      dlg.setAlwaysOnTop(true);
      dlg.toFront();
    }
    
    public void showDialog(Point location, Dimension size) {
      warningWindowLocation = location;
      warningWindowSize = size;
      showDialog();
    }
    
    public void hideDialog() {
      if (dlg == null)
        return;
      if (dlg.isShowing())
        warningWindowLocation = dlg.getLocationOnScreen();
      warningWindowSize = dlg.getSize();
      
      if (warningWindowSize.equals(specialSize)) {
        warningWindowSize = defaultSize;
      }
      if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
        OCRInterface.this_interface.getTranslateWorkflowPanel().setWarningWindow(dlg);
        OCRInterface.this_interface.getTranslateWorkflowPanel().setPairPanel(null);
      }
      dlg.setVisible(false);
      dlg = null;
    }
    
    public JDialog getDialog()
    {
      return dlg;
    }
    
    public void setParent(JFrame parentIn) {
      parent = parentIn;
    }
    
    public void setText(String text) {
      tokenZoneID.setText(text);
    }
    
    public void setSecondStep(boolean b) {
      secondStep = b;
    }
    
    public void setThirdStep(boolean b) {
      thirdStep = b;
    }
  }
  
  public class LinkLabel extends JLabel {
    private static final long serialVersionUID = 1L;
    private String zoneID;
    private JDialog dlg = null;
    private Color GREEN = new Color(0, 211, 0);
    private Color RED = Color.RED;
    public Color YELLOW = new Color(255, 140, 77);
    public Color BLUE = Color.BLUE;
    private boolean greenRed = true;
    private String zone1 = null; private String zone2 = null;
    private LinkLabel zone1Label;
    private LinkLabel zone2Label;
    
    private LinkLabel(String txt, String zone1, String zone2) { super.setForeground(BLUE);
      super.setFont(new JLabel().getFont().deriveFont(12.0F));
      super.setHorizontalTextPosition(2);
      super.setIcon(Login.localOrInJar("images/icon_greenRed.jpg", getClass().getClassLoader()));
      super.setMaximumSize(new Dimension(150, 20));
      super.setMinimumSize(new Dimension(150, 20));
      super.setPreferredSize(new Dimension(150, 20));
      txt = "<html>&nbsp;&nbsp;<U>" + txt + "</U></html>";
      super.setText(txt);
      
      this.zone1 = zone1;
      this.zone2 = zone2;
      greenRed = true;
      
      addMouseListener2();
    }
    
    public void setZone1(LinkLabel zoneLabel) {
      zone1Label = zoneLabel;
    }
    
    public void setZone2(LinkLabel zoneLabel) {
      zone2Label = zoneLabel;
    }
    
    private LinkLabel(JDialog dlg) {
      this.dlg = dlg;
      addMouseListener();
    }
    
    private LinkLabel(String zoneID_text) {
      super.setForeground(BLUE);
      super.setFont(new JLabel().getFont().deriveFont(16.0F));
      zoneID = zoneID_text.trim();
      zoneID_text = "<html>&nbsp;&nbsp;<U>" + zoneID_text + "</U></html>";
      super.setText(zoneID_text);
      
      addMouseListener();
    }
    
    private void addMouseListener()
    {
      addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent me) {
          me.getComponent().setCursor(
            Cursor.getPredefinedCursor(12));
        }
        
        public void mousePressed(MouseEvent me) { if (((me.isControlDown()) || (me.isShiftDown())) && (ImageDisplay.activeZones.size() == 1)) {}
        }
        




        public void mouseReleased(MouseEvent me)
        {
          OCRInterface.this_interface.setFocusable(true);
          OCRInterface.this_interface.requestFocus();
          
          if (ImageDisplay.activeZones.size() >= 2) {
            for (Zone z : ImageDisplay.activeZones)
              z.setSpecificColor(null);
            ImageDisplay.activeZones.clear();
          }
          

          ImageDisplay.ZoneVector selected = (ImageDisplay.ZoneVector)ImageDisplay.activeZones.clone();
          
          OCRInterface.this_interface
            .getUniqueZoneIdObj().searchZone(false, zoneID.trim());
          
          if ((me.isControlDown()) || (me.isShiftDown())) {
            ImageDisplay.activeZones.addAll(0, selected);
          }
          for (Zone z : ImageDisplay.activeZones) {
            z.setSpecificColor(null);
          }
          if (ImageDisplay.activeZones.size() == 1) {
            setForeground(GREEN);
          } else if (ImageDisplay.activeZones.size() == 2) {
            setForeground(RED);
          }
          ((Zone)ImageDisplay.activeZones.get(0)).setSpecificColor(GREEN);
          
          if (ImageDisplay.activeZones.size() == 2) {
            ((Zone)ImageDisplay.activeZones.get(1)).setSpecificColor(RED);
          }
          if (dlg != null) {
            warningWindowLocation = dlg.getLocationOnScreen();
            dlg.setVisible(false);
          }
          
          if (this_interfacecurrState != 1) {
            return;
          }
          OCRInterface.this_interface.getToolbar();
          boolean isRO_ON = BrowserToolBar.showReadingOrder;
          PolyTranscribeToolBar.this.resetTypeWindow();
          if (isRO_ON) {
            OCRInterface.this_interface.getToolbar().setSelectedReadingOrderBtn(true);
          }
        }
      });
    }
    
    private void addMouseListener2() {
      addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent me) {
          me.getComponent().setCursor(
            Cursor.getPredefinedCursor(12));
        }
        
        public void mousePressed(MouseEvent me) { if ((me.isControlDown()) || (me.isShiftDown())) {}
        }
        




        public void mouseReleased(MouseEvent me)
        {
          if ((me.isControlDown()) || (me.isShiftDown())) {
            return;
          }
          OCRInterface.this_interface.setFocusable(true);
          OCRInterface.this_interface.requestFocus();
          
          if (ImageDisplay.activeZones.size() >= 2) {
            for (Zone z : ImageDisplay.activeZones)
              z.setSpecificColor(null);
            ImageDisplay.activeZones.clear();
          }
          

          OCRInterface.this_interface.getUniqueZoneIdObj().searchZone(false, zone1);
          
          Zone z1 = (Zone)ImageDisplay.activeZones.get(0);
          
          OCRInterface.this_interface.getUniqueZoneIdObj().searchZone(false, zone2);
          
          Zone z2 = (Zone)ImageDisplay.activeZones.get(0);
          
          System.out.println("z1/z2: " + zoneID + "/" + zoneID);
          if (greenRed) {
            setIcon(Login.localOrInJar("images/icon_redGreen.jpg", getClass().getClassLoader()));
            greenRed = false;
            z1.setSpecificColor(GREEN);
            z2.setSpecificColor(RED);
            ImageDisplay.activeZones.clear();
            ImageDisplay.activeZones.add(z1);
            ImageDisplay.activeZones.add(z2);
            zone1Label.setForeground(GREEN);
            zone2Label.setForeground(RED);
          }
          else if (!greenRed) {
            setIcon(Login.localOrInJar("images/icon_greenRed.jpg", getClass().getClassLoader()));
            greenRed = true;
            z2.setSpecificColor(GREEN);
            z1.setSpecificColor(RED);
            ImageDisplay.activeZones.clear();
            ImageDisplay.activeZones.add(z2);
            ImageDisplay.activeZones.add(z1);
            zone2Label.setForeground(GREEN);
            zone1Label.setForeground(RED);
          }
        }
      });
    }
    
    public void setText(String zoneID_text) {
      super.setForeground(BLUE);
      super.setFont(new JLabel().getFont().deriveFont(16.0F));
      zoneID = zoneID_text.trim();
      zoneID_text = "<html><U>" + zoneID_text + "</U></html>";
      super.setText(zoneID_text);
    }
    
    public String getZoneID() {
      if ((zoneID == null) || (zoneID.isEmpty()))
        return "";
      return zoneID;
    }
  }
}
