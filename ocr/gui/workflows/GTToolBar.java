package ocr.gui.workflows;

import gttool.document.DLZone;
import gttool.misc.Login;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import ocr.gui.BrowserToolBar;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.SaveFilesDialog;
import ocr.gui.Zone;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.ElectronicTextDisplayer;
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
import ocr.tif.ImageReaderDrawer;











public class GTToolBar
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  CompoundBorder sel_border = BrowserToolBar.sel_border;
  
  CompoundBorder normal_border = BrowserToolBar.normal_border;
  
  private static JLabel startButton;
  
  private static JLabel lineButton;
  
  private static JLabel textButton;
  
  private static JLabel pawButton;
  private static JLabel wordButton;
  private static JLabel splitwordButton;
  private static JLabel refineButton;
  private static JLabel lockButton;
  private static JLabel QCButton;
  OCRInterface ocrIF;
  JFrame frame;
  String numLines = "0";
  boolean isReadingOrderOK = false;
  
  private JLabel fileCount;
  
  public static final String successReadingOrder = "99999";
  
  private String QCExtension = ".final.xml.qc";
  
  public GTToolBar() {
    setLayout(new FlowLayout(0, 5, 5));
    setPreferredSize(new Dimension(1000, 40));
    
    ocrIF = OCRInterface.this_interface;
    
    addStateLabels();
    if (OCRInterface.this_interface.getEnableAlignment()) {
      enableActions();
    }
    fileCount = new JLabel("   Num. of Files Completed:  ");
    fileCount.setFont(new Font(fileCount.getFont().getFontName(), 0, 16));
    fileCount.setToolTipText("Number of Files Completed in this Worklist");
    fileCount.setAlignmentX(0.0F);
    
    add(fileCount);
    disableButtons();
    
    setBorders(-1);
  }
  
  public void highlightOnTable(String image, int rowToSelect) {
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
  

  public void highlightOnTable(int whichState)
  {
    String xmlPath = fullPath();
    String image = ImageReaderDrawer.getFile_name();
    
    String ext = OCRInterface.this_interface.getFileExtension(xmlPath);
    
    System.out.println("\nxmlPath: " + xmlPath);
    System.out.println("image: " + image);
    System.out.println("ext: " + ext);
    
    if (whichState == 1) {
      xmlPath = xmlPath.replace(".xml", ".line" + ext);
    }
    else if (whichState == 2) {
      xmlPath = xmlPath.replace(".xml", ".merge" + ext);
    }
    else if (whichState == 5) {
      xmlPath = xmlPath.replace(".xml", ".paw" + ext);
    }
    else if (whichState == 3) {
      xmlPath = xmlPath.replace(".xml", ".word" + ext);
    }
    else if (whichState == 4) {
      xmlPath = xmlPath.replace(".xml", ".splitword" + ext);
    }
    else if (whichState == 6) {
      xmlPath = xmlPath.replace(".xml", ".refine" + ext);
    }
    else if (whichState == 8) {
      xmlPath = xmlPath.replace(".xml", ".final" + ext);
    }
    
    updateTable(new File(xmlPath));
  }
  





  private boolean initTasks(String ext, int state)
  {
    OCRInterface ocrIF = OCRInterface.this_interface;
    saveFilesDialog.saveData();
    

    if ((state == 8) && (ocrTable.selFileName.contains(".final")) && (ext.equals(".refine"))) {
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
  
  private void setBorders(int whichState)
  {
    if (whichState == 0) {
      startButton.setBorder(sel_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 1) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(sel_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 2) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(sel_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 5) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(sel_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 3) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(sel_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 4) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(sel_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 6) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(sel_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 7) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
    } else if (whichState == 8) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(sel_border);
    } else if (whichState == -1) {
      startButton.setBorder(normal_border);
      lineButton.setBorder(normal_border);
      textButton.setBorder(normal_border);
      pawButton.setBorder(normal_border);
      wordButton.setBorder(normal_border);
      splitwordButton.setBorder(normal_border);
      refineButton.setBorder(normal_border);
      lockButton.setBorder(normal_border);
      QCButton.setBorder(normal_border);
    }
  }
  
  private String fullPath() {
    String currentImagePath = ImageReaderDrawer.getFile_path();
    String xmlFilePath = OCRInterface.this_interface.hasMeta(currentImagePath);
    System.out.println("xmlFilePath: " + xmlFilePath);
    if (xmlFilePath == null)
    {



      String imageName = ImageReaderDrawer.file_name;
      xmlFilePath = OCRInterface.getCurrentXmlDir() + 
        OCRInterface.getXmlDirName() + 
        File.separator + 
        imageName.substring(0, imageName.lastIndexOf('.')) + ".xml";
      
      this_interfacesaveFilesDialog.createXML(xmlFilePath, currentImagePath);
    }
    

    return xmlFilePath;
  }
  
  private void runAction(int action) {
    String imgPath = OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName();
    String xmlPath = fullPath();
    String txtIn;
    String txtIn;
    if (ocrIF.getUseDirNameForTextFile()) {
      txtIn = imgPath + "\\" + OCRInterface.getImageDirName() + ".txt";
    } else {
      txtIn = xmlPath.replace(".xml", ".txt");
    }
    System.out.println("txtIn: " + txtIn);
    if (action == 5) {
      String xmlIn = xmlPath;
      String xmlOut = xmlIn.replace(".xml", ".line.xml");
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));
      }
    }
    else if (action == 1) {
      String xmlIn = xmlPath.replace(".xml", ".line.xml");
      String xmlOut = xmlIn.replace(".line.xml", ".merge.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));
        ImageAnalyzer.merge(txtIn, OCRInterface.this_interface.getMergeTextToFirstZone());
      }
    }
    else if (action == 10)
    {
      String xmlIn = xmlPath.replace(".xml", ".merge.xml");
      String xmlOut = xmlIn.replace(".merge.xml", ".word.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));
        ImageAnalyzer.segWord();
      }
    }
    else if (action == 11) {
      String xmlIn = xmlPath.replace(".xml", ".word.xml");
      String xmlOut = xmlIn.replace(".word.xml", ".splitword.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));
        ImageAnalyzer.split();
      }
    }
    else if (action == 19) {
      String xmlIn = xmlPath.replace(".xml", ".splitword.xml");
      String xmlOut = xmlIn.replace(".splitword.xml", ".refine.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));



      }
      



    }
    else if (action == 17)
    {
      String message = ImageAnalyzer.verifyReadingOrder(txtIn);
      
      showReadingOrderWarning(message.trim());
      
      if (message.trim().equals("99999")) {
        String xmlIn = xmlPath.replace(".xml", ".refine.xml");
        String xmlOut = xmlIn.replace(".refine.xml", ".final.xml");
        ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut));
        
        updateTable(new File(xmlOut));
      }
      
    }
    else if (action == 7)
    {
      if (OCRInterface.this_interface.getEnableNonConvexPolygonShrinking()) {
        System.out.println("HERE");
        Vector<DLZone> shapeVec = currentHWObjcurr_canvas.getShapeVec().getAsVector();
        OCRInterface.this_interface.shrinkZones(shapeVec, false);
      }
      else {
        ImageAnalyzer.newShrinkExpandAll();
        ImageAnalyzer.clearConnectedComponents();
      }
    }
  }
  







  private void runCmd(int whichCmd)
  {
    String GediAlignPath = System.getProperty("user.dir") + ocrIF.getGediAlignCmdPath();
    String preString = "cmd /c ";
    String imgPath = OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName();
    String xmlPath = fullPath();
    String command = "";
    String imageIn = ImageReaderDrawer.getFile_path();
    String xmlExt = ocrIF.getFileExtension(xmlPath);
    String imageExt = ocrIF.getFileExtension(imageIn);
    String txtIn;
    String txtIn;
    if (ocrIF.getUseDirNameForTextFile()) {
      txtIn = imgPath + "\\" + OCRInterface.getImageDirName() + ".txt";
    } else {
      txtIn = xmlPath.replace(".xml", ".txt");
    }
    if (whichCmd == 0) {
      String xmlLineOut = xmlPath.replace(".xml", ".line.xml") + "\"";
      System.out.println("numLines: " + numLines);
      command = "\"" + GediAlignPath + "\" -textline \"" + imageIn + "\" \"" + xmlLineOut + " " + numLines;
      System.out.println("command: " + command);
    } else if (whichCmd == 1) {
      String xmlIn = xmlPath.replace(".xml", ".line.xml");
      String xmlOut = xmlIn.replace(".line.xml", ".merge.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut)))
      {



        updateTable(new File(xmlOut));
        
        ImageAnalyzer.merge(txtIn, OCRInterface.this_interface.getMergeTextToFirstZone());
      }
    } else if (whichCmd == 10)
    {
      String xmlIn = xmlPath.replace(".xml", ".merge.xml");
      String xmlOut = xmlIn.replace(".merge.xml", ".word.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut)))
      {



        updateTable(new File(xmlOut));
        
        ImageAnalyzer.segWord();
      }
    } else if (whichCmd == 11)
    {

















      String xmlIn = xmlPath.replace(".xml", ".word.xml");
      String xmlOut = xmlIn.replace(".word.xml", ".splitword.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut)))
      {



        updateTable(new File(xmlOut));
        
        ImageAnalyzer.split();
      }
    } else if (whichCmd == 18) {
      String xmlIn = xmlPath.replace(".xml", ".refine.xml") + "\"";
      String xmlOut = xmlIn.replace(".refine.xml", ".refine-copy.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
      
      runSysCall(command, false);
      
      xmlIn = xmlOut;
      String xmlUpdateOut = xmlIn.replace(".refine-copy.xml", ".refine.xml");
      
      command = "\"" + GediAlignPath + "\" -splitword \"" + imageIn + "\" \"" + xmlIn + " \"" + xmlUpdateOut;
    } else if (whichCmd == 3) {
      String xmlIn = xmlPath.replace(".xml", ".splitword.xml") + "\"";
      String xmlOut = xmlIn.replace(".splitword.xml", ".paw.xml");
      
      command = "\"" + GediAlignPath + "\" -solopaw \"" + imageIn + "\" \"" + xmlIn + " \"" + xmlOut;
      
      System.out.println("PAW command 2: " + command);
    } else if (whichCmd == 8)
    {








      ImageAnalyzer.newShrinkExpandAll();
    }
    else if (whichCmd == 9) {
      String xmlIn = xmlPath.replace(".xml", ".refine-copy.xml") + "\"";
      String xmlOut = xmlIn.replace(".refine-copy.xml", ".refine.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
      
      runSysCall(command, true);
    } else if (whichCmd == 17)
    {

















      String message = ImageAnalyzer.verifyReadingOrder(txtIn);
      
      showReadingOrderWarning(message.trim());
      
      if (message.trim().equals("99999")) {
        String xmlIn = xmlPath.replace(".xml", ".refine.xml");
        String xmlOut = xmlIn.replace(".refine.xml", ".final.xml");
        ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut));
        


        updateTable(new File(xmlOut));
      }
    }
    else if (whichCmd == 12) {
      String xmlIn = xmlPath.replace(".xml", ".refine.xml") + "\"";
      String xmlOut = xmlIn.replace(".refine.xml", ".refine-copy.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
      
      runSysCall(command, false);
      
      xmlIn = xmlOut;
      String xmlUpdateOut = xmlIn.replace(".refine-copy.xml", ".refine.xml");
      
      command = "\"" + GediAlignPath + "\" -updatepaw \"" + imageIn + "\" \"" + xmlIn + " \"" + xmlUpdateOut;
    }
    else if (whichCmd == 5) {
      String xmlIn = xmlPath;
      String xmlOut = xmlIn.replace(".xml", ".line.xml");
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut)))
      {


        updateTable(new File(xmlOut));
      }
    }
    else if (whichCmd == 6) {
      String xmlIn = xmlPath.replace(".xml", ".line.xml") + "\"";
      String xmlOut = xmlIn.replace(".line.xml", ".merge.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;










    }
    else if (whichCmd == 14)
    {

      String xmlIn = xmlPath.replace(".xml", ".splitword.xml") + "\"";
      String xmlOut = xmlIn.replace(".splitword.xml", ".refine.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
      
      runSysCall(command, false);
      
      xmlIn = xmlOut;
      xmlOut = xmlIn.replace(".refine.xml", ".refine-copy.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
    }
    else if (whichCmd == 16) {
      String xmlIn = xmlPath.replace(".xml", ".merge.xml") + "\"";
      String xmlOut = xmlIn.replace(".merge.xml", ".word.xml");
      
      command = preString + " copy \"" + xmlIn + " \"" + xmlOut;
    } else if (whichCmd == 7) {
      String xmlIn = xmlPath.replace(".xml", ".splitword.xml");
      String xmlOut = xmlIn.replace(".splitword.xml", ".refine.xml");
      
      if (ImageAnalyzer.copyFile(new File(xmlIn), new File(xmlOut))) {
        updateTable(new File(xmlOut));
        
        ImageAnalyzer.newShrinkExpandAll();
        
        ImageAnalyzer.clearConnectedComponents();
      }
    }
  }
  
  private boolean runSysCall(String whichCommand, boolean doUpdate) {
    try {
      Runtime runtime = Runtime.getRuntime();
      
      Process process = runtime.exec(whichCommand);
      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader buffr = new BufferedReader(isr);
      

      process.waitFor();
      
      if (doUpdate) {
        ocrIF.fullPathElts.clear();
        ocrIF.update_tables();
      }
      
      System.out.println("Output of running " + whichCommand + ": ");
      String line; while ((line = buffr.readLine()) != null) { String line;
        System.out.println(line);
      }
      
      return true;
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        "GediAlign.exe is not found.", 
        "Error", 
        0); }
    return false;
  }
  

  private void setEnabledQCButton(boolean enabled)
  {
    if (enabled) {
      QCButton.setEnabled(true);
      QCButton.setBorder(sel_border);
    }
    else {
      QCButton.setEnabled(false);
      QCButton.setBorder(normal_border);
    }
  }
  
  public void disableButtons() {
    startButton.setEnabled(false);
    lineButton.setEnabled(false);
    textButton.setEnabled(false);
    pawButton.setEnabled(false);
    wordButton.setEnabled(false);
    splitwordButton.setEnabled(false);
    refineButton.setEnabled(false);
    lockButton.setEnabled(false);
    QCButton.setEnabled(false);
  }
  
  public void enableState(int whichState) {
    TypeWindow typeWindow = this_interfacetbdPane.data_panel.t_window;
    typeWindow.clear(true);
    
    if (isQCFileExists()) {
      setEnabledQCButton(true);
    } else {
      setEnabledQCButton(false);
    }
    if (whichState == 0) {
      startButton.setEnabled(true);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      pawButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false); }
    if (whichState == 1) {
      startButton.setEnabled(false);
      lineButton.setEnabled(true);
      textButton.setEnabled(false);
      pawButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
      




      typeWindow.selectNormalButton();
      
      OCRInterface.currOppmode = 2;
      
      this_interfacetoolbar.setStickyMode(true);
      
      typeWindow.selectType(0, (short)2);
    }
    else if (whichState == 2) {
      lineButton.setEnabled(false);
      textButton.setEnabled(true);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 3) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(true);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 4) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(true);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 5) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(true);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 6) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(false);
      refineButton.setEnabled(true);
      lockButton.setEnabled(false);
    } else if (whichState == 7) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == 8) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      pawButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(true);
    } else if (whichState == -1) {
      startButton.setEnabled(false);
      lineButton.setEnabled(false);
      textButton.setEnabled(false);
      pawButton.setEnabled(false);
      wordButton.setEnabled(false);
      splitwordButton.setEnabled(false);
      refineButton.setEnabled(false);
      lockButton.setEnabled(false);
    } else if (whichState == -2) {
      disableButtons();
    }
    
    setBorders(whichState);
  }
  
  private boolean checkForLines() {
    return numTextBoxes() == numTextLines();
  }
  
  private int checkForLinesOnFinal() {
    Vector<DLZone> allExistingZoneVector = currentHWObjcurr_canvas.getShapeVec().getAsVector();
    int newLineTRUE = 0;
    String attribute = "newline";
    

    String value = ((DLZone)allExistingZoneVector.get(0)).getAttributeValue(attribute);
    
    if (value == null) {
      value = ((DLZone)allExistingZoneVector.get(0)).getAttributeValue("newLine");
      attribute = "newLine";
    }
    
    if (value == null) {
      value = ((DLZone)allExistingZoneVector.get(0)).getAttributeValue("NewLine");
      attribute = "NewLine";
    }
    
    for (DLZone zone : allExistingZoneVector) {
      if ((zone.getAttributeValue(attribute) != null) && 
        (zone.getAttributeValue(attribute).equalsIgnoreCase("TRUE")))
        newLineTRUE++;
    }
    return newLineTRUE;
  }
  
  private int numTextBoxes() {
    return currentHWObjcurr_canvas.shapeVec.size();
  }
  
  private int numTextLines() {
    return OCRInterface.this_interface.numETextLines();
  }
  
  public void setNumFiles(int numFiles, int numTotalFiles) {
    fileCount.setText("  Num. of Files Done:  " + numFiles + "  out of  " + numTotalFiles);
    
    fileCount.setForeground(Color.black);
    fileCount.setFont(new Font(fileCount.getFont().getFontName(), 0, 16));
    
    fileCount.updateUI();
  }
  
  private boolean isElectronicTextExist() {
    String text = 
      this_interfacetbdPane.data_panel.a_window.eTextWindow.getText();
    return (text != null) && (!text.trim().isEmpty());
  }
  
  private boolean checkForWordsAndSegments() {
    String warningMessage = "The number of words is NOT equal to the number of segments \nfor zones with the following IDs:\n\n";
    
    boolean foundProblemZone = false;
    Vector<DLZone> zones = currentHWObjcurr_canvas.getShapeVec().getAsVector();
    
    for (int i = 0; i < zones.size(); i++) {
      Zone zone = (Zone)zones.get(i);
      String contents = zone.getContents();
      String offsets = zone.getAttributeValue("offsets");
      String zoneId = zone.getAttributeValue("id");
      
      String zoneType = zone.getAttributeValue("gedi_type");
      


      if (zone.offsetsReady())
      {

        StringTokenizer st = null;
        
        int numWords = 0;int numSegments = 0;
        

        if (OCRInterface.this_interface.getAlignmentDefaultSegmenation().equalsIgnoreCase("character")) {
          numWords = contents.length();
        }
        else {
          st = new StringTokenizer(contents);
          while (st.hasMoreTokens()) {
            st.nextToken();
            numWords++;
          }
        }
        
        st = new StringTokenizer(offsets, ",");
        while (st.hasMoreTokens()) {
          st.nextToken();
          numSegments++;
        }
        
        if (offsets.trim().isEmpty()) {
          numSegments = 1;
        } else {
          numSegments++;
        }
        

        if (numWords != numSegments) {
          String numbers = " ==> (" + numWords + " words, " + numSegments + " segments)";
          warningMessage = warningMessage + zoneId + numbers + "\n";
          foundProblemZone = true;
        }
      }
    }
    if (foundProblemZone)
    {
      warningMessage = warningMessage + "\nNOTE: Use CTRL+F to search zone by ID";
      
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        warningMessage, 
        "Watch Out", 
        0);
      return false;
    }
    return true;
  }
  
  private void showReadingOrderWarning(String message)
  {
    String title = "";
    int messageType = 0;
    if (message.equals("99999")) {
      message = "Reading Order is OK. \nLocking File.";
      messageType = 1;
      title = "Success";
      isReadingOrderOK = true;
    } else {
      message = 
      



        "Reading Order Does Not Match.\n\nErrors:\n\n" + message + "\n\nNOTE:" + "\nUse CTRL+F to search zone by ID" + "\nUse \"Reading Order Display Toggle\" button to show / hide reading order" + "\nUse Right mouse click to hightlight all head zones (Reading Order Display should be ON)";
      messageType = 2;
      title = "Failure";
      isReadingOrderOK = false;
    }
    
    JOptionPane.showMessageDialog(
      OCRInterface.this_interface, 
      message, 
      title, 
      messageType);
  }
  
  private boolean showLinesWarningAndContinue(int numOfNewlineTRUE) {
    String message = "Number of text lines is not equal to number of newline=TRUE\ntext lines: " + 
      numTextLines() + "\n" + 
      "newline = TRUE: " + numOfNewlineTRUE + "\n\n" + 
      "Continue anyway?\n\n";
    








    int answer = JOptionPane.showConfirmDialog(
      OCRInterface.this_interface, 
      message, 
      "Watch Out", 
      0, 
      0);
    
    if (answer == 0) {
      return true;
    }
    return false;
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
      2), "LINE");
    hotkeyManager.getActionMap().put("LINE", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("line");
        

        if (!GTToolBar.this.initTasks(".TIF", 0)) {
          return;
        }
        GTToolBar.this.setWaitCursor();
        this_interfacesaveFilesDialog.saveData();
        

        GTToolBar.this.runAction(5);
        
        enableState(1);
        GTToolBar.this.setBorders(1);
        GTToolBar.this.setDefaultCursor();
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(113, 
      2), "MERGE");
    hotkeyManager.getActionMap().put("MERGE", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("MERGE");
        
        if (!GTToolBar.this.initTasks(".line", 2))
          return;
        if (!GTToolBar.this.isElectronicTextExist()) {
          String message = "Text doesn't exist.\nMerge cannot be performed.";
          JOptionPane.showMessageDialog(
            OCRInterface.this_interface, 
            message, 
            "Watch Out", 
            0);
          return;
        }
        
        if ((!OCRInterface.this_interface.getMergeTextToFirstZone()) && (!GTToolBar.this.checkForLines())) {
          String message = "Number of text boxes is NOT equal to the number of text lines\nText Boxes: " + 
            GTToolBar.this.numTextBoxes() + "\n" + 
            "Text Lines: " + GTToolBar.this.numTextLines();
          
          JOptionPane.showMessageDialog(
            OCRInterface.this_interface, 
            message, 
            "Watch Out", 
            0);
          return;
        }
        
        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        
        this_interfacesaveFilesDialog.saveData();
        GTToolBar.this.setWaitCursor();
        

        GTToolBar.this.runAction(1);
        OCRInterface.currDoc.dumpData();
        
        GTToolBar.this.runAction(7);
        
        OCRInterface.currDoc.dumpData();
        
        enableState(2);
        GTToolBar.this.setBorders(2);
        GTToolBar.this.setDefaultCursor();
        
        OCRInterface.this_interface.updateETextWindow();
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(114, 
      2), "WORD");
    hotkeyManager.getActionMap().put("WORD", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("WORD");
        
        if (!GTToolBar.this.initTasks(".merge", 3)) {
          return;
        }
        this_interfacesaveFilesDialog.saveData();
        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        GTToolBar.this.setWaitCursor();
        

        GTToolBar.this.runAction(10);
        GTToolBar.this.setSrcLineID();
        OCRInterface.currDoc.dumpData();
        
        enableState(3);
        GTToolBar.this.setBorders(3);
        GTToolBar.this.setDefaultCursor();
      }
      
    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(115, 
      2), "SPLITWORD");
    hotkeyManager.getActionMap().put("SPLITWORD", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("SPLITWORD");
        
        if (!GTToolBar.this.initTasks(".word", 4)) {
          return;
        }
        if (!GTToolBar.this.checkForWordsAndSegments()) {
          return;
        }
        
        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        GTToolBar.this.setWaitCursor();
        this_interfacesaveFilesDialog.saveData();
        

        GTToolBar.this.runAction(11);
        OCRInterface.currDoc.dumpData();
        
        GTToolBar.this.setWaitCursor();
        
        GTToolBar.this.renumbering();
        OCRInterface.currDoc.dumpData();
        
        GTToolBar.this.setWaitCursor();
        GTToolBar.this.runCmd(8);
        
        OCRInterface.currDoc.dumpData();
        
        enableState(4);
        GTToolBar.this.setBorders(4);
        
        OCRInterface.this_interface.updateETextWindow();
        
        GTToolBar.this.setDefaultCursor();
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(121, 
      2), "PAW");
    hotkeyManager.getActionMap().put("PAW", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("PAW");








      }
      








    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(116, 
      2), "REFINE");
    hotkeyManager.getActionMap().put("REFINE", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("REFINE");
        
        if (!GTToolBar.this.initTasks(".splitword", 6)) {
          return;
        }
        this_interfacesaveFilesDialog.saveData();
        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        GTToolBar.this.setWaitCursor();
        
        GTToolBar.this.runAction(19);
        GTToolBar.this.runAction(7);
        OCRInterface.currDoc.dumpData();
        
        enableState(6);
        GTToolBar.this.setBorders(6);
        GTToolBar.this.setDefaultCursor();
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(117, 
      2), "LOCK");
    hotkeyManager.getActionMap().put("LOCK", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("LOCK");
        GTToolBar.this.setWaitCursor();
        this_interfacesaveFilesDialog.saveData();
        
        OCRInterface.this_interface.getCanvas().unlockSoftLock();
        if (!GTToolBar.this.initTasks(".refine", 8)) {
          if (ocrIF.ocrTable.selFileName.contains(".final"))
          {
            String imgPath = OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName();
            String xmlPath = GTToolBar.this.fullPath();
            String txtIn; String txtIn; if (ocrIF.getUseDirNameForTextFile()) {
              txtIn = imgPath + "\\" + OCRInterface.getImageDirName() + ".txt";
            } else
              txtIn = xmlPath.replace(".xml", ".txt");
            if (!GTToolBar.this.linesWarning()) {
              return;
            }
            String message = ImageAnalyzer.verifyReadingOrder(txtIn);
            GTToolBar.this.showReadingOrderWarning(message.trim());
            System.out.println("message: " + message);
            if (isReadingOrderOK)
            {
              GTToolBar.this.renumbering();
              OCRInterface.currDoc.dumpData();
              ocrIF.workmodeProps[ocrIF.ocrTable.my_mode]
                .getElementFilePropVec(WorkmodeTable.curRow)
                .setSoftLocked(1, true, true);
            }
            return;
          }
          return;
        }
        



        if (!GTToolBar.this.linesWarning()) {
          return;
        }
        
        GTToolBar.this.runAction(17);
        
        if (isReadingOrderOK)
        {

          GTToolBar.this.renumbering();
          
          OCRInterface.currDoc.dumpData();
          

          int curCol = 1;
          FilePropPacket fpp = ocrIF.workmodeProps[ocrIF.ocrTable.my_mode]
            .getElementFilePropVec(WorkmodeTable.curRow);
          fpp.setSoftLocked(curCol, true, true);
          
          enableState(8);
          GTToolBar.this.setBorders(8);
        }
        GTToolBar.this.setDefaultCursor();
        OCRInterface.currDoc.dumpData();


      }
      


    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(118, 
      2), "QC");
    hotkeyManager.getActionMap().put("QC", new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        System.out.println("QC");
        
        if (!GTToolBar.this.initTasks(".final", 8)) {
          return;
        }
        this_interfacesaveFilesDialog.saveData();
        GTToolBar.this.setWaitCursor();
        
        if (GTToolBar.this.isQCFileExists()) {
          GTToolBar.this.deleteQCFile();
          GTToolBar.this.setEnabledQCButton(false);
        }
        else {
          GTToolBar.this.createQCFile();
          GTToolBar.this.setEnabledQCButton(true);
        }
        
        GTToolBar.this.setDefaultCursor();
      }
      
    });
    hotkeyManager.setEnabled(true);
  }
  


  public void disableActions()
  {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getActionMap().remove("LINE");
    hotkeyManager.getActionMap().remove("MERGE");
    hotkeyManager.getActionMap().remove("WORD");
    hotkeyManager.getActionMap().remove("SPLITWORD");
    hotkeyManager.getActionMap().remove("PAW");
    hotkeyManager.getActionMap().remove("REFINE");
    hotkeyManager.getActionMap().remove("LOCK");
    hotkeyManager.getActionMap().remove("QC");
    
    hotkeyManager.setEnabled(true);
  }
  
  private void addStateLabels() {
    startButton = new JLabel(" 0. TIF  ");
    add(startButton);
    
    lineButton = new JLabel(" 1. Line ");
    add(lineButton);
    

    textButton = new JLabel(" 2. Merge ");
    add(textButton);
    
    wordButton = new JLabel(" 3. Word ");
    add(wordButton);
    
    splitwordButton = new JLabel(" 4. SplitWord ");
    add(splitwordButton);
    
    pawButton = new JLabel(" 5. PAW ");
    

    refineButton = new JLabel(" 5. Refine ");
    add(refineButton);
    
    lockButton = new JLabel(" 6. Lock ");
    add(lockButton);
    
    QCButton = new JLabel(" 7. QC ");
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
  
  private void deleteQCFile()
  {
    String xmlPath = OCRInterface.this_interface.getFileNameWithoutExt(fullPath());
    System.out.println("xmlPath: " + xmlPath);
    File file = new File(xmlPath + QCExtension);
    if (file.exists()) {
      file.delete();
    }
  }
  

  private void createQCFile()
  {
    String xmlPath = OCRInterface.this_interface.getFileNameWithoutExt(fullPath());
    File file = new File(xmlPath + QCExtension);
    Writer output = null;
    if (!file.exists())
      try {
        file.createNewFile();
        output = new BufferedWriter(new FileWriter(file));
        output.write("UserName: " + Login.userName + "\n\n");
        output.write("Date: " + 
          GetDateTime.getCurrentDateTime() + 
          " (" + 
          "mm/dd/yyyy hh:mm" + 
          ")");
        output.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  }
  
  private void updateTable(File newXml) {
    String baseImage = ImageReaderDrawer.getFile_path();
    ocrIF.addToRawXmlOnlyFileList(newXml);
    highlightOnTable(baseImage, WorkmodeTable.curRow + 1);
  }
  
  private boolean linesWarning() {
    int numOfNewlineTRUE = checkForLinesOnFinal();
    if (numOfNewlineTRUE != numTextLines()) {
      return showLinesWarningAndContinue(numOfNewlineTRUE);
    }
    return true;
  }
  







  private void renumbering()
  {
    String imgPath = OCRInterface.getCurrentImageDir() + OCRInterface.getImageDirName();
    String xmlPath = fullPath();
    String txtIn; String txtIn; if (ocrIF.getUseDirNameForTextFile()) {
      txtIn = imgPath + "\\" + OCRInterface.getImageDirName() + ".txt";
    } else {
      txtIn = xmlPath.replace(".xml", ".txt");
    }
    String message = ImageAnalyzer.verifyReadingOrder(txtIn);
    if (!message.trim().equals("99999")) {
      return;
    }
    DLZone head = null;
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if ((previousZone == null) && (nextZone != null))
        head = temp;
    }
    int i = 1;
    
    head.setAttributeValue("id", Integer.toString(i));
    head.setAttributeValue("nextZoneID", Integer.toString(i + 1));
    zoneID = Integer.toString(i);
    nextZone.zoneID = Integer.toString(i + 1);
    
    while (nextZone != null) {
      head.setAttributeValue("id", Integer.toString(i));
      head.setAttributeValue("nextZoneID", Integer.toString(i + 1));
      zoneID = Integer.toString(i);
      nextZone.zoneID = Integer.toString(i + 1);
      head = nextZone;
      i++;
    }
    head.setAttributeValue("id", Integer.toString(i));
    head.setAttributeValue("nextZoneID", Integer.toString(-1));
    zoneID = Integer.toString(i);
    
    for (DLZone temp : shapeVec) {
      if ((temp.getNextZone() == null) && 
        (temp.getPreviousZone() == null)) {
        i++;
        temp.setAttributeValue("id", Integer.toString(i));
        zoneID = Integer.toString(i);
      }
    }
  }
  









  private void setSrcLineID()
  {
    DLZone head = null;
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if ((previousZone == null) && (nextZone != null)) {
        head = temp;
        break;
      }
    }
    
    int i = 1;
    
    while (nextZone != null) {
      head.setAttributeValue("srclineid", Integer.toString(i));
      head = nextZone;
      i++;
    }
    head.setAttributeValue("srclineid", Integer.toString(i));
  }
}
