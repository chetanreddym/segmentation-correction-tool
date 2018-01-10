package ocr.gui;

import gttool.misc.Login;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.PropertiesInfoHolder;
import ocr.util.AttributeConfigUtil;
import ocr.util.UniqueZoneId;


public class FindPanel
  extends JPanel
{
  final String reachedEndMsg = "Reached end of list, continuing from top";
  final String reachedTopMsg = "Reached top of list, continuing from bottom";
  final String imageNotFoundMsg = "Image not found";
  final String invalidImageNum = "Image # is invalid or greater than total number of images";
  final String defaultMessage = "D L - G E D I";
  JLabel defaultMessageLabel = new JLabel("D L - G E D I");
  ClassLoader loader = getClass().getClassLoader();
  ImageIcon closeIcon = Login.localOrInJar("images/closeFindPanel.gif", getClass().getClassLoader());
  ImageIcon nextIcon = Login.localOrInJar("images/next.gif", getClass().getClassLoader());
  ImageIcon previousIcon = Login.localOrInJar("images/previous.gif", getClass().getClassLoader());
  ImageIcon warningIcon = Login.localOrInJar("images/warning.gif", getClass().getClassLoader());
  
  JLabel closeLabel;
  JLabel findImageLabel;
  JLabel nextLabel;
  JLabel previousLabel;
  JLabel gotoImageLabel;
  JLabel gotoZoneLabel;
  JLabel warningLabel;
  JTextField findImageText;
  JTextField gotoImageText;
  JTextField gotoZoneText;
  BevelBorder raisedBorder = new BevelBorder(0);
  BevelBorder loweredBorder = new BevelBorder(1);
  Border emptyBorder = BorderFactory.createEmptyBorder();
  
  Dimension d_large = new Dimension(10, 0);
  Dimension d_small = new Dimension(5, 0);
  
  ArrayList<String> imageNames = null;
  ArrayList<String> foundNames = null;
  int currentIndex = 0;
  String currentSearch = null;
  boolean imageNotFound = false;
  boolean invalidImageNumber = false;
  Color green = new Color(140, 201, 140);
  Color pink = Color.PINK;
  Color white = Color.WHITE;
  
  static final long serialVersionUID = 78322123893L;
  
  public FindPanel()
  {
    setLayout(new BoxLayout(this, 0));
    setPreferredSize(new Dimension(OCRInterface.this_interface.getWidth(), 35));
    
    BevelBorder outerLabelBorder = new BevelBorder(1);
    EmptyBorder innerLabelBorder = new EmptyBorder(1, 1, 1, 1);
    setBorder(new CompoundBorder(outerLabelBorder, innerLabelBorder));
    showFindPanel();
  }
  


  public void showFindPanel()
  {
    removeAll();
    revalidate();
    repaint();
    closeLabel = new JLabel(closeIcon);
    closeLabel.setMaximumSize(new Dimension(25, 25));
    findImageLabel = new JLabel("Find Image: ");
    findImageText = new JTextField();
    findImageText.setMaximumSize(new Dimension(150, 20));
    nextLabel = new JLabel("Next", nextIcon, 0);
    nextLabel.setMaximumSize(new Dimension(60, 25));
    previousLabel = new JLabel("Previous", previousIcon, 0);
    previousLabel.setMaximumSize(new Dimension(80, 25));
    

    gotoImageLabel = new JLabel("Goto Image #:");
    gotoZoneLabel = new JLabel("Goto Zone:");
    gotoImageText = new JTextField();
    gotoImageText.setMaximumSize(new Dimension(50, 20));
    
    gotoZoneText = new JTextField();
    gotoZoneText.setMaximumSize(new Dimension(50, 20));
    
    warningLabel = new JLabel(warningIcon);
    warningLabel.setVisible(false);
    
    add(Box.createRigidArea(d_large));
    add(closeLabel);
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(findImageLabel);
    add(Box.createRigidArea(d_small));
    add(findImageText);
    add(Box.createRigidArea(d_large));
    add(nextLabel);
    add(Box.createRigidArea(d_small));
    add(previousLabel);
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(gotoImageLabel);
    add(Box.createRigidArea(d_small));
    add(gotoImageText);
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(gotoZoneLabel);
    add(Box.createRigidArea(d_small));
    add(gotoZoneText);
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(Box.createRigidArea(d_large));
    add(warningLabel);
    
    addActions();
    fillInImageList();
    
    gotoZoneText.requestFocus();
  }
  



  private void addActions()
  {
    nextLabel.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent me) {
        if ((foundNames.isEmpty()) || (!currentSearch.equals(findImageText.getText()))) {
          FindPanel.this.findImage(findImageText.getText());
        } else
          FindPanel.this.goToNext();
      }
      
      public void mouseEntered(MouseEvent me) { nextLabel.setBorder(raisedBorder); }
      
      public void mouseExited(MouseEvent me) {
        nextLabel.setBorder(emptyBorder);
      }
      
      public void mousePressed(MouseEvent arg0) { nextLabel.setBorder(loweredBorder); }
      
      public void mouseReleased(MouseEvent arg0) {
        nextLabel.setBorder(raisedBorder);
      }
      

    });
    previousLabel.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent me) {
        if ((foundNames.isEmpty()) || (!currentSearch.equals(findImageText.getText()))) {
          FindPanel.this.findImage(findImageText.getText());
        } else
          FindPanel.this.goToPrevious();
      }
      
      public void mouseEntered(MouseEvent me) { previousLabel.setBorder(raisedBorder); }
      
      public void mouseExited(MouseEvent me) {
        previousLabel.setBorder(emptyBorder);
      }
      
      public void mousePressed(MouseEvent arg0) { previousLabel.setBorder(loweredBorder); }
      
      public void mouseReleased(MouseEvent arg0) {
        previousLabel.setBorder(raisedBorder);
      }
      

    });
    closeLabel.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent me) {
        removeAll();
        add(Box.createRigidArea(d_large));
        add(defaultMessageLabel);
        revalidate();
        repaint();
      }
      
      public void mouseEntered(MouseEvent me) { closeLabel.setBorder(raisedBorder); }
      
      public void mouseExited(MouseEvent me) {
        closeLabel.setBorder(emptyBorder);
      }
      
      public void mousePressed(MouseEvent arg0) { closeLabel.setBorder(loweredBorder); }
      



      public void mouseReleased(MouseEvent arg0) {}
    });
    gotoZoneText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == 10) {
          FindPanel.this.showWarning("");
          FindPanel.this.resetTextFields();
          if (gotoZoneText.getText().isEmpty())
            return;
          FindPanel.this.searchZone(gotoZoneText.getText());
        }
        else {
          FindPanel.this.resetTextFields();
        }
      }
      
      public void keyReleased(KeyEvent arg0) {}
      
      public void keyTyped(KeyEvent arg0) {} });
    gotoImageText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == 10) {
          FindPanel.this.showWarning("");
          FindPanel.this.resetTextFields();
          if (gotoImageText.getText().isEmpty())
            return;
          FindPanel.this.jumpToFile(gotoImageText.getText());
          if (!invalidImageNumber) {
            gotoImageText.setBackground(green);
          }
        } else {
          FindPanel.this.resetTextFields();
        }
      }
      
      public void keyReleased(KeyEvent arg0) {}
      
      public void keyTyped(KeyEvent arg0) {} });
    findImageText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == 10) {
          FindPanel.this.showWarning("");
          FindPanel.this.resetTextFields();
          if (foundNames.isEmpty()) {
            findImageText.setBackground(white);
            FindPanel.this.findImage(findImageText.getText());
            return;
          }
          if (imageNotFound) {
            FindPanel.this.showWarning("Image not found");
            return;
          }
          if ((currentSearch != null) && (!currentSearch.isEmpty()) && 
            (currentSearch.equals(findImageText.getText()))) {
            FindPanel.this.goToNext();
            return;
          }
          foundNames.clear();
          FindPanel.this.findImage(findImageText.getText());
        }
        else {
          FindPanel.this.resetTextFields();
          foundNames.clear();
          imageNotFound = false;
        }
      }
      



      public void keyReleased(KeyEvent arg0) {}
      


      public void keyTyped(KeyEvent arg0) {}
    });
  }
  


  private void findImage(String toSearchFor)
  {
    if (toSearchFor.isEmpty()) {
      currentSearch = "";
      return;
    }
    currentSearch = toSearchFor;
    toSearchFor = regexCleanup(toSearchFor);
    toSearchFor = toSearchFor.replace("*", ".*");
    if (toSearchFor.startsWith("?"))
      toSearchFor = "^." + toSearchFor.substring(1);
    toSearchFor = toSearchFor.replace("?", ".");
    
    for (int i = 0; i < imageNames.size(); i++) {
      String name = (String)imageNames.get(i);
      Pattern pattern = Pattern.compile(toSearchFor);
      Matcher matcher = pattern.matcher(name);
      
      if (matcher.find()) {
        foundNames.add(Integer.toString(i + 1));
      }
    }
    if (foundNames.isEmpty()) {
      showWarning("Image not found");
      findImageText.setBackground(pink);
      imageNotFound = true;
      return;
    }
    
    showWarning("");
    currentIndex = 0;
    imageNotFound = false;
    findImageText.setBackground(green);
    jumpToFile((String)foundNames.get(currentIndex));
  }
  


  private void searchZone(String zoneId)
  {
    String msg = "";
    if (OCRInterface.this_interface.getTitle().equals("DocLib - GroundTruthing Editor and Document Interface Professional Edition (DL-GEDIPro)")) {
      msg = "Please select a document to process.";
    } else {
      msg = OCRInterface.this_interface.getUniqueZoneIdObj().searchZone(false, zoneId);
    }
    if (msg == null) {
      gotoZoneText.setBackground(green);
      showWarning("");
    }
    else {
      gotoZoneText.setBackground(pink);
      showWarning(msg);
    }
  }
  




  private void jumpToFile(String index)
  {
    int jumpTo = 0;
    try {
      jumpTo = Integer.parseInt(index);
      if (jumpTo > OCRInterface.this_interface.getRawImageOnlyFileList().size())
        jumpTo = 0;
    } catch (NumberFormatException nfe) {
      jumpTo = 0;
    }
    
    if (jumpTo <= 0) {
      showWarning("Image # is invalid or greater than total number of images");
      gotoImageText.setBackground(pink);
      invalidImageNumber = true;
      return;
    }
    
    invalidImageNumber = false;
    
    File imagePath = (File)OCRInterface.this_interface.getRawImageOnlyFileList().get(jumpTo - 1);
    
    int baseImageRow = this_interfaceworkmodeProps[0]
      .getImageIndex(imagePath.getAbsolutePath());
    

    if (WorkmodeTable.curRow == baseImageRow) {
      return;
    }
    WorkmodeTable.curRow = -1;
    this_interfaceocrTable
      .processSelectionEvent(baseImageRow, 1, true);
    WorkmodeTable.curRow = baseImageRow;
    OCRInterface.getAttsConfigUtil().scrollToCenter(
      this_interfaceocrTable, baseImageRow, 1);
  }
  
  private String regexCleanup(String str) {
    str = str.replaceAll("\\\\", "\\\\\\\\");
    str = str.replaceAll("\\$", "\\\\\\$");
    str = str.replaceAll("\\.", "\\\\\\.");
    
    str = str.replaceAll("\\[", "\\\\\\[");
    str = str.replaceAll("\\]", "\\\\\\]");
    str = str.replaceAll("\\|", "\\\\\\|");
    


    str = str.replaceAll("\\(", "\\\\\\(");
    str = str.replaceAll("\\)", "\\\\\\)");
    str = str.replaceAll("\\{", "\\\\\\{");
    str = str.replaceAll("\\}", "\\\\\\}");
    return str;
  }
  
  public void updateImageList() {
    reset();
    fillInImageList();
  }
  
  private void fillInImageList() {
    if (imageNames == null) {
      imageNames = new ArrayList();
    }
    if (foundNames == null) {
      foundNames = new ArrayList();
    }
    imageNames.clear();
    
    if (OCRInterface.this_interface.getRawImageOnlyFileList() != null) {
      for (File imageFile : OCRInterface.this_interface.getRawImageOnlyFileList())
        imageNames.add(imageFile.getName());
    }
  }
  
  private void showWarning(String text) {
    if (text.isEmpty()) {
      warningLabel.setVisible(false);
    } else {
      warningLabel.setText(text);
      warningLabel.setVisible(true);
    }
  }
  



  private void goToNext()
  {
    if (currentIndex >= foundNames.size() - 1) {
      showWarning("Reached end of list, continuing from top");
      findImageText.setBackground(green);
      currentIndex = 0;
    }
    else {
      warningLabel.setVisible(false);
      currentIndex += 1;
      findImageText.setBackground(green);
    }
    
    jumpToFile((String)foundNames.get(currentIndex));
  }
  



  private void goToPrevious()
  {
    if (currentIndex == 0) {
      showWarning("Reached top of list, continuing from bottom");
      currentIndex = (foundNames.size() - 1);
      findImageText.setBackground(green);
    }
    else {
      warningLabel.setVisible(false);
      currentIndex -= 1;
    }
    
    jumpToFile((String)foundNames.get(currentIndex));
  }
  
  private void resetTextFields() {
    showWarning("");
    findImageText.setBackground(white);
    gotoImageText.setBackground(white);
    gotoZoneText.setBackground(white);
  }
  
  public void reset() {
    showWarning("");
    findImageText.setBackground(white);
    gotoImageText.setBackground(white);
    gotoZoneText.setBackground(white);
    currentIndex = -1;
    foundNames.clear();
  }
  
  public void resetZoneSearchField()
  {
    gotoZoneText.setBackground(white);
  }
  
  public ArrayList<String> getFoundNames() {
    return foundNames;
  }
}
