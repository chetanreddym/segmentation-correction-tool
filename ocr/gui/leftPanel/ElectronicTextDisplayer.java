package ocr.gui.leftPanel;

import gttool.document.DLZone;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.BidiString;




public class ElectronicTextDisplayer
  extends JToolBar
{
  JTextPane textWindow = new JTextPane();
  private MutableAttributeSet blankStyle;
  private Vector<DLZone> zones = new Vector();
  private ImageDisplay.ZoneVector selectedZones = new ImageDisplay.ZoneVector();
  
  private HashMap<DLZone, LinkedList<Point>> passiveHighlights = new HashMap();
  private HashMap<DLZone, LinkedList<Point>> activeHighlights = new HashMap();
  
  private String text;
  private String selectedText;
  private Style highlightedTextStyle;
  private Style passiveZonesStyle;
  private Style activeZonesStyle;
  protected Object obj = new Object();
  
  private int start;
  private int end;
  private int fontSize;
  private BidiString bs;
  GlobalHotkeyManager hotkeyManager;
  
  ElectronicTextDisplayer()
  {
    textWindow.setEditable(false);
    fontSize = 18;
    textWindow.addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        enableHotkeys(true);
        if (e.getButton() == 3) {
          actionRightClick(e.getClickCount());
        } else {
          SwingWorker worker = new SwingWorker()
          {
            protected Object doInBackground() throws Exception {
              actionMouseReleased();
              return null;
            }
          };
          worker.execute();
        }
        
      }
      

    });
    blankStyle = textWindow.addStyle("blankstyle", null);
    passiveZonesStyle = textWindow.addStyle("pzstyle", null);
    activeZonesStyle = textWindow.addStyle("azstyle", null);
    highlightedTextStyle = textWindow.addStyle("hstyle", passiveZonesStyle);
    
    StyleConstants.setBackground(highlightedTextStyle, textWindow.getSelectionColor());
    StyleConstants.setBackground(blankStyle, Color.white);
    StyleConstants.setBackground(passiveZonesStyle, Color.green);
    StyleConstants.setBackground(activeZonesStyle, Color.orange);
    
    StyleConstants.setFontSize(blankStyle, fontSize);
    StyleConstants.setFontSize(highlightedTextStyle, fontSize);
    StyleConstants.setFontSize(passiveZonesStyle, fontSize);
    StyleConstants.setFontSize(activeZonesStyle, fontSize);
    
    MutableAttributeSet attr = new SimpleAttributeSet();
    textWindow.setParagraphAttributes(attr, false);
    
    setPreferredSize(new Dimension(30, 20));
    
    textWindow.setText("");
    
    add(new JScrollPane(textWindow));
    
    setLayout(new BoxLayout(this, 1));
  }
  
  protected void disableHotkeys() {
    hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getActionMap().remove("INCREASE_TEXT_SIZE");
    hotkeyManager.getActionMap().remove("DECREASE_TEXT_SIZE");
    hotkeyManager.getActionMap().remove("COPY_SEL");
  }
  
  protected void disableCopySelHotkey()
  {
    hotkeyManager = GlobalHotkeyManager.getInstance();
    hotkeyManager.getActionMap().remove("COPY_SEL");
  }
  
  protected void enableHotkeys(boolean copySel)
  {
    hotkeyManager = GlobalHotkeyManager.getInstance();
    
    if (copySel)
    {



      hotkeyManager.getInputMap().put(
        KeyStroke.getKeyStroke(86, 2), "COPY_SEL");
      
      hotkeyManager.getActionMap().put("COPY_SEL", new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          currentHWObjcurr_canvas.saveCurrentState();
          selectedText = textWindow.getSelectedText().trim();
          ElectronicTextDisplayer.this.copySelToZone();
          OCRInterface.currDoc.setModified(true);
          OCRInterface.this_interface.updateCurrFilename();
          disableCopySelHotkey();
        }
      });
    }
    

    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(61, 2), "INCREASE_TEXT_SIZE");
    hotkeyManager.getActionMap().put("INCREASE_TEXT_SIZE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        fontSize += 1;
        StyleConstants.setFontSize(blankStyle, fontSize);
        StyleConstants.setFontSize(highlightedTextStyle, fontSize);
        StyleConstants.setFontSize(passiveZonesStyle, fontSize);
        StyleConstants.setFontSize(activeZonesStyle, fontSize);
        ElectronicTextDisplayer.this.updatePane(true);
        Font currFont = textWindow.getFont();
        textWindow.setFont(new Font(currFont.getName(), 
          currFont.getStyle(), 
          fontSize));
        textWindow.repaint();
      }
      

    });
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(45, 2), "DECREASE_TEXT_SIZE");
    hotkeyManager.getActionMap().put("DECREASE_TEXT_SIZE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        fontSize -= 1;
        StyleConstants.setFontSize(blankStyle, fontSize);
        StyleConstants.setFontSize(highlightedTextStyle, fontSize);
        StyleConstants.setFontSize(passiveZonesStyle, fontSize);
        StyleConstants.setFontSize(activeZonesStyle, fontSize);
        ElectronicTextDisplayer.this.updatePane(true);
        Font currFont = textWindow.getFont();
        textWindow.setFont(new Font(currFont.getName(), 
          currFont.getStyle(), 
          fontSize));
        textWindow.repaint();
      }
    });
  }
  
  private void copySelToZone()
  {
    Zone tempZone = currentHWObjcurr_canvas.getActiveZone();
    tempZone.setContents(selectedText.trim());
    currentHWObjcurr_canvas.paintCanvas();
  }
  
  protected void actionRightClick(int clickCount) {
    int tempStart = textWindow.getSelectionStart();
    start = getLineStart(textWindow, tempStart);
    end = getLineEnd(textWindow, start);
    
    textWindow.setSelectionStart(start);
    textWindow.setSelectionEnd(end);
    
    selectedText = textWindow.getSelectedText().trim();
    
    updatePane(true);
  }
  
  protected void actionMouseReleased() {
    start = textWindow.getSelectionStart();
    end = textWindow.getSelectionEnd();
    
    selectedText = textWindow.getSelectedText().trim();
    
    updatePane(false);
  }
  
  private static int getLineStart(JTextPane thisTextWindow, int thisStart) {
    String text = thisTextWindow.getText();
    int returnInd = 0;
    
    for (int i = 0; i < thisStart + 1; i++)
    {
      if (text.charAt(i) == '\n')
        returnInd = i + 1;
    }
    return returnInd;
  }
  
  private static int getLineEnd(JTextPane thisTextWindow, int tempStart) {
    String text = thisTextWindow.getText().substring(tempStart);
    
    for (int i = 0; i < text.length(); i++)
    {
      if (text.charAt(i) == '\n')
        return i + tempStart;
    }
    return -1;
  }
  
  public int getNumTextLines()
  {
    return text.trim().replaceAll("(\n|\r)+\\s*(\n|\r)", "\n").split("\n").length;
  }
  
  private synchronized void updatePane(boolean doHighlightTextRange) {
    if (!isVisible()) {
      return;
    }
    removeAllHighlights();
    
    highlightZones(zones);
    if (doHighlightTextRange)
      highlightTextRange(start, end, highlightedTextStyle);
    repaint();
  }
  
  public void paneUpdate() {
    updatePane(false);
  }
  
  private void highlightTextRange(int start, int end, Style style) {
    if ((start >= end) || (start < 0) || (end <= 0)) { return;
    }
    
    textWindow.getStyledDocument().setCharacterAttributes(start, end - start, 
      style, false);
  }
  
  public void setText(String text)
  {
    textWindow.setText(ImageAnalyzer.cleanBidiString(text));
    textWindow.setCaretPosition(0);
    this.text = textWindow.getText();
    
    MutableAttributeSet attr = new SimpleAttributeSet();
    
    bs = new BidiString(this.text, 2);
    
    if (bs.getDirection() == 1) {
      StyleConstants.setAlignment(attr, 2);
      textWindow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }
    else {
      StyleConstants.setAlignment(attr, 0);
      textWindow.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }
    updatePane(true);
  }
  
  public String getSelectedText()
  {
    return selectedText;
  }
  







  public void setCurrentZones(Vector<DLZone> asVector, ImageDisplay.ZoneVector sZones)
  {
    zones = asVector;
    selectedZones = sZones;
    updatePane(true);
  }
  
  public void updateActiveSelection() {
    selectedZones = ImageDisplay.getActiveZones();
    Iterator<Zone> itr = selectedZones.iterator();
    while (itr.hasNext())
      addZoneToSelection((Zone)itr.next());
  }
  
  public void addZoneToSelection(Zone newSelection) {
    if ((zones == null) || (selectedZones == null)) {
      zones = OCRInterface.this_interface.getCanvas().getShapeVec().getAsVector();
      selectedZones = ImageDisplay.getActiveZones();
    }
    
    String str = newSelection.getContents();
    if (str == null) {
      return;
    }
    str = str.replaceAll("\\s+", " ").trim();
    if (str.isEmpty()) {
      return;
    }
    
    LinkedList<Point> zoneHighlights = (LinkedList)passiveHighlights.get(newSelection);
    
    if (zoneHighlights == null) {
      return;
    }
    
    activeHighlights.put(newSelection, zoneHighlights);
    
    StyledDocument styleDoc = textWindow.getStyledDocument();
    
    Iterator<Point> itr = zoneHighlights.iterator();
    while (itr.hasNext()) {
      Point highlight = (Point)itr.next();
      styleDoc.setCharacterAttributes(x, y, activeZonesStyle, true);
    }
  }
  
  public void removeZoneHighlight(DLZone removedZone) {
    if (removedZone == null) {
      return;
    }
    StyledDocument styleDoc = textWindow.getStyledDocument();
    
    LinkedList<Point> zoneHighlights = (LinkedList)passiveHighlights.get(removedZone);
    
    if (zoneHighlights != null) {
      Iterator<Point> itr = zoneHighlights.iterator();
      while (itr.hasNext()) {
        Point highlight = (Point)itr.next();
        styleDoc.setCharacterAttributes(x, y, blankStyle, true);
      }
    }
    
    passiveHighlights.remove(removedZone);
    activeHighlights.remove(removedZone);
    
    Iterator<DLZone> itr = passiveHighlights.keySet().iterator();
    Iterator<Point> hitr; for (; itr.hasNext(); 
        
        hitr.hasNext())
    {
      hitr = ((LinkedList)passiveHighlights.get(itr.next())).iterator();
      continue;
      Point highlight = (Point)hitr.next();
      styleDoc.setCharacterAttributes(x, y, passiveZonesStyle, true);
    }
  }
  








  public void updateZoneHighlight(DLZone updatedZone)
  {
    StyledDocument styleDoc = textWindow.getStyledDocument();
    String str = updatedZone.getContents();
    
    boolean isNewHighlight = true;
    if ((str == null) || (str.replaceAll("\\s+", " ").trim().isEmpty())) {
      isNewHighlight = false;
    }
    str = str.replaceAll("\\s+", " ").trim();
    
    removeZoneHighlight(updatedZone);
    
    if (isNewHighlight) {
      String localtext = " " + text + " ";
      localtext = localtext.replaceAll("\\uFEFF", "");
      
      str = regexCleanup(str);
      
      String[] words = str.split("\\s+");
      String regex = "\\s+";
      for (int i = 0; i < words.length; i++) {
        regex = regex + words[i] + "\\s+";
      }
      
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(localtext);
      
      LinkedList<Point> tempList = new LinkedList();
      
      int index = 0;
      while (matcher.find(index)) {
        tempList.add(new Point(matcher.start(), matcher.end() - matcher.start() - 1));
        styleDoc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start() - 1, activeZonesStyle, true);
        index = matcher.start() + 1;
      }
      passiveHighlights.put(updatedZone, tempList);
      activeHighlights.put(updatedZone, tempList);
    } else {
      passiveHighlights.remove(updatedZone);
      activeHighlights.remove(updatedZone);
    }
  }
  






  private String regexCleanup(String str)
  {
    str = str.replaceAll("\\\\", "\\\\\\\\");
    str = str.replaceAll("\\$", "\\\\\\$");
    str = str.replaceAll("\\.", "\\\\\\.");
    str = str.replaceAll("\\^", "\\\\\\^");
    str = str.replaceAll("\\[", "\\\\\\[");
    str = str.replaceAll("\\]", "\\\\\\]");
    str = str.replaceAll("\\|", "\\\\\\|");
    str = str.replaceAll("\\?", "\\\\\\?");
    str = str.replaceAll("\\*", "\\\\\\*");
    str = str.replaceAll("\\+", "\\\\\\+");
    str = str.replaceAll("\\(", "\\\\\\(");
    str = str.replaceAll("\\)", "\\\\\\)");
    str = str.replaceAll("\\{", "\\\\\\{");
    str = str.replaceAll("\\}", "\\\\\\}");
    return str;
  }
  
  private void highlightZones(Vector<DLZone> asVector) {
    if ((text == null) || (text.isEmpty()) || (textWindow == null)) {
      return;
    }
    
    String localtext = text;
    localtext = localtext.replaceAll("\\uFEFF", "");
    
    StyledDocument styleDoc = textWindow.getStyledDocument();
    
    for (DLZone zone : asVector) {
      String str = zone.getContents();
      if (str != null)
      {

        if (!str.isEmpty())
        {

          str = regexCleanup(str);
          
          String[] words = str.split("\\s+");
          String regex = "\\s+";
          for (int i = 0; i < words.length; i++)
          {


            if (zone.getContents().trim().length() == 1) {
              regex = words[i];
              localtext = text;

            }
            else
            {
              regex = regex + words[i] + "\\s+";
              localtext = " " + text + " ";
            }
          }
          
          localtext = localtext.replaceAll("\\uFEFF", "");
          
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(localtext);
          
          LinkedList<Point> tempList = new LinkedList();
          
          int index = 0;
          int start = 0;
          int end = 0;
          while (matcher.find(index)) {
            start = matcher.start();
            end = matcher.end();
            
            if (end - start == 1) {
              end = matcher.end() - matcher.start();
            } else {
              end = matcher.end() - matcher.start() - 1;
            }
            tempList.add(new Point(start, end));
            styleDoc.setCharacterAttributes(start, end, passiveZonesStyle, true);
            index = matcher.start() + 1;
          }
          passiveHighlights.put(zone, tempList);
        }
      }
    }
  }
  
  private void highlightZones2(Vector<DLZone> asVector)
  {
    if ((text == null) || (text.isEmpty()) || (textWindow == null)) {
      return;
    }
    String localtext = " " + text + " ";
    localtext = localtext.replaceAll("\\uFEFF", "");
    
    StyledDocument styleDoc = textWindow.getStyledDocument();
    
    for (DLZone zone : asVector) {
      String str = zone.getContents();
      if (str != null)
      {

        if (!str.isEmpty())
        {

          str = regexCleanup(str);
          
          String[] words = str.split("\\s+");
          String regex = "\\s+";
          for (int i = 0; i < words.length; i++) {
            regex = regex + words[i] + "\\s+";
          }
          
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(localtext);
          
          LinkedList<Point> tempList = new LinkedList();
          
          int index = 0;
          while (matcher.find(index)) {
            tempList.add(new Point(matcher.start(), matcher.end() - matcher.start() - 1));
            styleDoc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start() - 1, passiveZonesStyle, true);
            index = matcher.start() + 1;
          }
          passiveHighlights.put(zone, tempList);
        } }
    }
  }
  
  public void removeSelection() {
    selectedText = "";
    start = 0;
    end = 0;
  }
  
  public String getText() { return text; }
  
  public void removeAllHighlights()
  {
    if (textWindow == null) return;
    textWindow.getStyledDocument().setCharacterAttributes(0, text.length(), blankStyle, true);
    passiveHighlights.clear();
    activeHighlights.clear();
  }
  
  public void removeActiveHighlights() {
    if (textWindow == null) { return;
    }
    StyledDocument styleDoc = textWindow.getStyledDocument();
    
    Iterator<DLZone> itr = activeHighlights.keySet().iterator();
    while (itr.hasNext()) {
      DLZone keyZone = (DLZone)itr.next();
      LinkedList<Point> temp = (LinkedList)activeHighlights.get(keyZone);
      
      if (temp != null)
      {

        Iterator<Point> hitr = temp.iterator();
        while (hitr.hasNext()) {
          Point highlight = (Point)hitr.next();
          styleDoc.setCharacterAttributes(x, y, passiveZonesStyle, true);
        }
      } }
    activeHighlights.clear();
  }
}
