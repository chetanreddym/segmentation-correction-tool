package ocr.gui;

import gttool.document.DLPage;
import gttool.misc.Login;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.JAIImageReader;









public class MultiPagePanel
  extends JPanel
  implements ActionListener
{
  public JButton upButton;
  public JButton downButton;
  private JComboBox pageNumBox;
  private OCRInterface ocrIF;
  private ArrayList<String> pages;
  public JLabel pgCountLabel;
  static final long serialVersionUID = 382982834L;
  
  public MultiPagePanel()
  {
    setLayout(new FlowLayout(0, 5, 0));
    
    ocrIF = OCRInterface.this_interface;
    pages = new ArrayList(1);
    
    ImageIcon up = Login.localOrInJar("images/arrow.plain.up_small.gif", getClass().getClassLoader());
    ImageIcon dn = Login.localOrInJar("images/arrow.plain.down_small.gif", getClass().getClassLoader());
    
    upButton = new JButton("", up);
    upButton.setName("Previous Page");
    upButton.setActionCommand("PREVPAGE");
    upButton.setToolTipText("Previous Page");
    upButton.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e) {
        int sel = getSelectedPage();
        if (sel > 1)
          selectPage(sel - 1);
      }
    });
    upButton.setAlignmentX(0.5F);
    upButton.setPreferredSize(new Dimension(up.getIconWidth(), up.getIconHeight()));
    
    downButton = new JButton("", dn);
    downButton.setName("Next Page");
    downButton.setActionCommand("NEXTPAGE");
    downButton.setToolTipText("Next Page");
    downButton.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e) {
        int sel = getSelectedPage();
        if (sel < pages.size())
          selectPage(sel + 1);
      }
    });
    downButton.setPreferredSize(new Dimension(dn.getIconWidth(), dn.getIconHeight()));
    downButton.setAlignmentX(0.5F);
    
    JLabel lbl = new JLabel("  Multi-Page:  ");
    
    add(lbl);
    
    add(upButton);
    add(downButton);
    
    lbl = new JLabel("Jump to: ");
    

    add(lbl);
  }
  




  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand() == "page change")
    {

      if (OCRInterface.currDoc == null) {
        this_interfacesaveFilesDialog.saveData();
        









        if (OCRInterface.currDoc != null) {
          return;
        }
      }
      ocrIF = OCRInterface.this_interface;
      






      ocrIF.currPageID = getSelectedPage();
      System.out.println("ocrIF.currPageID: " + ocrIF.currPageID);
      

      OCRInterface.currentHWObj.setOriginalImage(JAIImageReader.readImage(
        ImageReaderDrawer.file_path, false));
      OCRInterface.currentHWObj.processImageFile();
      
      ocrIF.toolbar.setScaleByIndex(ocrIF.toolbar.getCurrentScaleIndex());
      
      this_interfacetbdPane.data_panel.a_window
        .showZoneInfo(ImageDisplay.getActiveZones());
      OCRInterface.this_interface.getCanvas().paintCanvas();
      


      String orientStr = (String)this_interfacetbdPane.data_panel.a_window.getPage().pageTags.get("GEDI_orientation");
      int temp = orientStr != null ? Integer.parseInt(orientStr) / 90 : 0;
      currentHWObjcurr_canvas.rotateImage(90 * temp);
      



      this_interfacetbdPane.data_panel.a_window.getPage().dlSetWidth(
        OCRInterface.currentHWObj.getOriginalImage().getWidth());
      this_interfacetbdPane.data_panel.a_window.getPage().dlSetHeight(
        OCRInterface.currentHWObj.getOriginalImage().getHeight());
      

      OCRInterface.this_interface.updateCommentsWindow(false);
    }
  }
  
  public void setPageTotal(int total)
  {
    if ((pageNumBox != null) && (pgCountLabel != null)) {
      remove(pageNumBox);
      remove(pgCountLabel);
    }
    
    pages = new ArrayList(total);
    for (int i = 0; i < total; i++) {
      pages.add(i, Integer.toString(i + 1));
    }
    
    String[] pageArray = new String[total];
    int j = 0;
    for (String s : pages) {
      pageArray[j] = s;
      j++;
    }
    
    pageNumBox = new JComboBox(pageArray);
    
    if (this_interfacecurrPageID == 0) {
      pageNumBox.setSelectedIndex(0);
    } else {
      pageNumBox.setSelectedIndex(this_interfacecurrPageID - 1);
    }
    pageNumBox.setActionCommand("page change");
    pageNumBox.addActionListener(this);
    pageNumBox.setPreferredSize(new Dimension(40, 25));
    
    pgCountLabel = new JLabel(" of " + total);
    


    add(pageNumBox);
    add(pgCountLabel);
    
    pageNumBox.updateUI();
    repaint();
  }
  
  public void selectPage(int sel) {
    if ((pageNumBox == null) && (pgCountLabel == null)) {
      return;
    }
    System.out.println("MPT: " + sel);
    
    pageNumBox.setSelectedIndex(sel - 1);
    pageNumBox.updateUI();
    System.out.println("pageNumBox.getSelectedIndex(): " + 
      pageNumBox.getSelectedIndex());
  }
  
  public int getNumPages() {
    return pages.size();
  }
  
  public int getSelectedPage() {
    return pageNumBox.getSelectedIndex() + 1;
  }
  
  public void disableBtns() {
    upButton.setEnabled(false);
    downButton.setEnabled(false);
  }
  
  public void enableBtns() {
    upButton.setEnabled(true);
    downButton.setEnabled(true);
  }
  
  public void setEnabled(boolean enabled) {
    if (enabled) {
      enableBtns();
      pageNumBox.setEnabled(true);
    }
    else {
      disableBtns();
      pageNumBox.setEnabled(false);
    }
  }
}
