package ocr.gui.leftPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.tif.ImageReaderDrawer;


















public class LeftPanel
  extends JPanel
  implements KeyListener
{
  public EncodingPanel encodingPanel;
  private JPanel generalInfoPanel = null;
  



  private JLabel current_file = null;
  



  private JPanel workmodePane = null;
  





  public JPanel shapeInfoPanel = null;
  



  private JPanel MainPanel = new JPanel(new BorderLayout());
  




















  public static final File resultsDir = new File("RuleBased/Result");
  



  public DatasetSpecificToolPanel data_panel;
  



  JSplitPane leftMainSplitPane;
  



  String glob_text = "";
  
  private int dividerLocation = 200;
  

  private InfoLabel infoLabel;
  

  public LeftPanel(OCRInterface ocrIF)
  {
    workmodePane = new JPanel(new BorderLayout());
    
    generalInfoPanel = new JPanel(new BorderLayout(5, 5));
    
    current_file = new JLabel("    Current File:  ");
    current_file.setToolTipText("Current Selected File");
    current_file.setAlignmentX(0.0F);
    
    infoLabel = new InfoLabel();
    
    MultiPagePanel mpPanel = ocrIF.getMultiPagePanel();
    mpPanel.setAlignmentX(0.0F);
    

    generalInfoPanel.add(current_file, "North");
    generalInfoPanel.add(infoLabel, "East");
    generalInfoPanel.add(mpPanel, "Center");
    generalInfoPanel.setPreferredSize(new Dimension(
      this_interfaceocrTable.getWidth(), 50));
    

    JScrollPane tableScroll = new JScrollPane(this_interfaceocrTable);
    tableScroll.setPreferredSize(new Dimension(270, 300));
    data_panel = new DatasetSpecificToolPanel(0);
    leftMainSplitPane = new JSplitPane(0, tableScroll, 
      data_panel);
    

    leftMainSplitPane.setContinuousLayout(true);
    leftMainSplitPane.setOneTouchExpandable(true);
    leftMainSplitPane.setDividerLocation(dividerLocation);
    leftMainSplitPane.setDividerSize(6);
    leftMainSplitPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    

    workmodePane.add(new JLabel("OCR"));
    workmodePane.add(leftMainSplitPane);
    

    this_interfaceocrTable.toolPanel = data_panel;
    data_panel.dataTable = this_interfaceocrTable;
    


    setLayout(new GridLayout(1, 1));
    
    MainPanel.add(generalInfoPanel, "North");
    MainPanel.add(workmodePane, "Center");
    
    add(MainPanel);
  }
  




















































































  protected Component makeTextPanel(String text)
  {
    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(0);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);
    return panel;
  }
  



  public void setCurrentFile(String text)
  {
    glob_text = text;
    
    String currentImage = ImageReaderDrawer.file_path;
    
    String dataFile = OCRInterface.this_interface.hasMeta(currentImage);
    if (dataFile != null) {
      File temp = new File(dataFile);
      if (!temp.canWrite()) {
        current_file.setText("    Current File " + 
          printNumberOfFiles(currentImage) + 
          " (read-only XML) " + 
          ":  " + text);
      } else {
        current_file.setText("    Current File " + 
          printNumberOfFiles(currentImage) + 
          ":  " + text);
      }
    } else {
      current_file.setText("    Current File " + 
        printNumberOfFiles(currentImage) + 
        ":  " + text);
    }
    current_file.updateUI();
  }
  
  private String printNumberOfFiles(String currentImage)
  {
    ArrayList<File> images = OCRInterface.this_interface.getRawImageOnlyFileList();
    
    if (images == null) {
      return "";
    }
    int imageN = 0;
    int numberOfImages = images.size();
    
    imageN = images.indexOf(new File(currentImage)) + 1;
    
    return "(" + 
      imageN + 
      " / " + 
      numberOfImages + 
      ")";
  }
  



























  public String getSelectedAssignID()
  {
    return data_panel.getSelectedAssignID();
  }
  


  public void keyTyped(KeyEvent arg0) {}
  


  public void keyPressed(KeyEvent arg0) {}
  


  public void keyReleased(KeyEvent arg0) {}
  

  public void resizeUp()
  {
    leftMainSplitPane.setDividerLocation(dividerLocation);
  }
  
  public void resizeDown() {
    leftMainSplitPane.setDividerLocation(dividerLocation + 300);
  }
  
  public String getCurrentFile() {
    return current_file.getText();
  }
  
  public InfoLabel getInfoLabel() {
    return infoLabel;
  }
}
