package ocr.gui.workflows;

import gttool.document.DLZone;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.Raster;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import ocr.gui.BrowserToolBar;
import ocr.gui.OCRInterface;
import ocr.gui.OrientedBox;
import ocr.gui.PolygonZone;
import ocr.gui.ReadingOrder;
import ocr.gui.SaveFilesDialog;
import ocr.gui.Zone;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;

public class TranslateToolBar extends JPanel
{
  private static final long serialVersionUID = 1L;
  private CompoundBorder sel_border = BrowserToolBar.sel_border;
  private CompoundBorder normal_border = BrowserToolBar.normal_border;
  private JLabel verifyButton;
  private Point warningWindowLocation = null;
  private Dimension warningWindowSize = null;
  private PolyTranscribeToolBar.WarningWindow ww;
  private JPanel pairPanel;
  private boolean[][] visited_global;
  private int numOfBands;
  private Raster image;
  private int blackPixel;
  private int filter;
  private int globalCount;
  
  public TranslateToolBar()
  {
    setLayout(new FlowLayout(0, 5, 5));
    setPreferredSize(new Dimension(1000, 30));
    
    addStateLabels();
    
    if (OCRInterface.this_interface.getEnableTranslateWorkflow()) {
      enableActions();
    }
    setBorders(-1);
  }
  
  private void addStateLabels() {
    verifyButton = new JLabel(" 1. VERIFY  ");
    add(verifyButton);
    verifyButton.setEnabled(false);
  }
  
  private void setBorders(int whichState) {
    if (whichState == -1) {
      verifyButton.setEnabled(false);
      verifyButton.setBorder(normal_border);
    }
    else if (whichState == 7) {
      verifyButton.setEnabled(true);
      verifyButton.setBorder(sel_border);
    }
  }
  
  public void enableActions() {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(112, 
      2), "VERIFY");
    hotkeyManager.getActionMap().put("VERIFY", new AbstractAction() {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) { System.out.println("VERIFY");
        if (ww != null)
          ww.hideDialog();
        OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
        ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
        verify(false);
      }
      
    });
    hotkeyManager.setEnabled(true);
  }
  
  public void disableActions() {
    GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager = GlobalHotkeyManager.getInstance();
    
    hotkeyManager.getActionMap().remove("VERIFY");
    
    hotkeyManager.setEnabled(true);
  }
  
  public void setState(int verifystate) {
    setBorders(verifystate);
  }
  
  public boolean isReadingOrderOK() {
    String result = ImageAnalyzer.checkForReadingOrder();
    
    if (result.equals("99999")) {
      return true;
    }
    
    PolyTranscribeToolBar p = new PolyTranscribeToolBar();
    
    if (result.trim().equalsIgnoreCase("No Reading Order Found"))
    {
      PolyTranscribeToolBar tmp41_40 = p;tmp41_40.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp41_40, result, "Reading Order Warning", 
        result);
      ww.setReadingOrderWarning();
      ww.showDialog(warningWindowLocation, warningWindowSize);
      return false;
    }
    if (result.trim().startsWith("JUNK IN READ ORDER")) {
      result = result.replaceAll("JUNK IN READ ORDER", ""); PolyTranscribeToolBar 
        tmp107_106 = p;tmp107_106.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp107_106, result, "Reading Order Warning", 
        "<html>The following JUNK zone(s) in the Reading Order:");
      ww.setFatalWarning();
      ww.showDialog(warningWindowLocation, warningWindowSize);
      return false;
    }
    
    PolyTranscribeToolBar tmp153_152 = p;tmp153_152.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp153_152, result, "Reading Order Warning", 
      "<html>The following zone(s) out of Reading Order:");
    ww.setFatalWarning();
    ww.showDialog(warningWindowLocation, warningWindowSize);
    return false;
  }
  
  private boolean isFirstZoneInReadingOrderOK()
  {
    ReadingOrder ro = OCRInterface.this_interface.getCanvas().getReadingOrderHandler();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String errorHead = "";
    for (DLZone zone : shapeVec) {
      if ((ro.isHeader(zone)) && (
        (!hasNewCelAttr(zone)) || 
        (!isNewCellTrue(zone)))) {
        errorHead = errorHead + "\n" + zoneID;
      }
    }
    if (errorHead.trim().isEmpty()) {
      return true;
    }
    PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
    
      tmp138_136 = p;tmp138_136.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp138_136, errorHead, "Attribute Warning", 
      "<html>The first zone in Reading order must have NEWTU=TRUE.</html>");
    ww.setFatalWarning();
    ww.showDialog(warningWindowLocation, warningWindowSize);
    return false;
  }
  
  private boolean hasNewCelAttr(DLZone zone) {
    return zone.hasAttribute("newtu");
  }
  
  private boolean isNewCellTrue(DLZone zone) {
    String value = zone.getAttributeValue("newtu");
    return (value != null) && (value.trim().equalsIgnoreCase("true"));
  }
  
  private String getOffset(DLZone zone) {
    return zone.getAttributeValue("offsets");
  }
  
  private String getBlankZones() {
    int blackPixelValue = currentHWObjcurr_canvas.getBlackPixelValue();
    int numOfBands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    Raster image = OCRInterface.currentHWObj.getOriginalImage().getData();
    
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    
    String blankZones = "";
    
    for (DLZone zone : shapeVec) {
      Rectangle bounds = zone.get_Bounds();
      boolean blackFound = false;
      int pixelValue = 0;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Point pixel = new Point(x + x, y + y);
          if (isInsideOfZone((Zone)zone, pixel))
          {

            pixelValue = 0;
            
            for (int i = 0; i < numOfBands; i++) {
              pixelValue += image.getSample(x, y, i);
            }
            if (pixelValue == blackPixelValue) {
              blackFound = true;
              break;
            }
          }
        }
      }
      if (!blackFound) {
        blankZones = blankZones + zoneID + "\n";
      }
    }
    return blankZones;
  }
  
  private boolean isInsideOfZone(Zone zone, Point p) {
    if ((zone instanceof PolygonZone)) {
      Point2D pCheck = new Point2D.Float((float)(p.getX() + 0.5D), 
        (float)(p.getY() + 0.5D));
      return ((PolygonZone)zone).doesContain(pCheck);
    }
    if ((zone instanceof OrientedBox)) {
      Point2D pCheck = new Point2D.Float((float)(p.getX() + 0.5D), 
        (float)(p.getY() + 0.5D));
      return ((OrientedBox)zone).doesContain(pCheck);
    }
    
    return zone.doesContain(x, y, 1.0F);
  }
  


  private boolean isContentOK()
  {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String noContentZoneID = "";
    for (DLZone temp : shapeVec) {
      if (!temp.hasContents()) {
        noContentZoneID = noContentZoneID + zoneID + "\n";
      }
    }
    if (noContentZoneID.trim().isEmpty()) {
      return true;
    }
    PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
    
      tmp104_103 = p;tmp104_103.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp104_103, noContentZoneID, "Missing Content Warning", 
      "<html>The following zone(s) has no content:");
    ww.setFatalWarning();
    ww.showDialog(warningWindowLocation, warningWindowSize);
    return false;
  }
  



  private boolean isOffsetsOK()
  {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String offsetZoneID = "";
    for (DLZone zone : shapeVec) {
      String offset = getOffset(zone);
      if ((offset != null) && (!offset.trim().isEmpty())) {
        offsetZoneID = offsetZoneID + zoneID + "\n";
      }
    }
    if (offsetZoneID.trim().isEmpty()) {
      return true;
    }
    PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
    
      tmp120_119 = p;tmp120_119.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp120_119, offsetZoneID, "Offsets Warning", 
      "<html>The following zone(s) has offsets:");
    ww.setFatalWarning();
    ww.showDialog(warningWindowLocation, warningWindowSize);
    
    return false;
  }
  
  private boolean consecutiveNEWTU_OK() {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String consecutive = "";
    Vector<DLZone> heads = new Vector();
    for (DLZone zone : shapeVec) {
      if ((previousZone == null) && (nextZone != null))
        heads.add(zone);
    }
    DLZone temp;
    for (??? = heads.iterator(); ???.hasNext(); 
        

        temp != null)
    {
      DLZone head = (DLZone)???.next();
      temp = head;
      int newCellTrue = 0;
      continue;
      newCellTrue = isNewCellTrue(temp) ? newCellTrue + 1 : 0;
      
      if (newCellTrue > 1) {
        consecutive = consecutive + "\n" + zoneID + "," + previousZone.zoneID;
        newCellTrue = 0;
      }
      temp = nextZone;
    }
    


    if (!consecutive.isEmpty()) {
      PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
        tmp225_223 = p;tmp225_223.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp225_223, consecutive, "Consecutive NEWTU Attributes Warning", 
        "Consecutive NEWTU=TRUE detected:");
      





      ww.setIntersectingLineWarning();
      ww.showDialog(warningWindowLocation, warningWindowSize);
      return false;
    }
    return true;
  }
  
  private boolean intersectionExist() {
    this_interfacesaveFilesDialog.saveData();
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
    String intersection = checkIntersection();
    OCRInterface.this_interface.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
    if (!intersection.isEmpty()) {
      int count = intersection.trim().split("\n").length;
      PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
        tmp87_86 = p;tmp87_86.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp87_86, intersection, "Intersecting Zones Warning", 
        "Intersecting zones detected: # " + count + " (pairs)");
      ww.setIntersectingLineWarning();
      ww.setSecondStep(true);
      ww.showDialog(warningWindowLocation, warningWindowSize);
      return true;
    }
    return false;
  }
  
  private String checkIntersection() {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String intersectingPolygons = "";
    
    for (int i = 0; i < shapeVec.size(); i++) {
      Zone temp1 = (Zone)shapeVec.get(i);
      for (int j = i + 1; j < shapeVec.size(); j++) {
        Zone temp2 = (Zone)shapeVec.get(j);
        if (isIntersect(temp1, temp2)) {
          intersectingPolygons = 
            intersectingPolygons + "\n" + temp1.getAttributeValue("id") + "," + temp2.getAttributeValue("id");
        }
      }
    }
    
    return intersectingPolygons.trim();
  }
  
  public Polygon getPolygon(Zone node) {
    String str = "";
    
    if (isPolygon(node)) {
      str = node.getAttributeValue("polygon");
    } else {
      str = getPolygonString(node);
    }
    String[] pair = str.split(";");
    
    Polygon poly = new Polygon();
    
    for (String p : pair) {
      p = p.replaceAll(";", "");
      p = p.replaceAll("\\(", "");
      p = p.replaceAll("\\)", "");
      String[] x_y = p.split(",");
      poly.addPoint(Integer.parseInt(x_y[0]), Integer.parseInt(x_y[1]));
    }
    
    return poly;
  }
  
  private String getPolygonString(Zone zone)
  {
    String polygonStr = "";
    int col = zone.get_lt_x();
    int row = zone.get_lt_y();
    int width = zone.get_width();
    int height = zone.get_height();
    
    polygonStr = "(" + col + "," + row + ");" + 
      "(" + (col + width) + "," + row + ");" + 
      "(" + (col + width) + "," + (row + height) + ");" + 
      "(" + col + "," + (row + height) + ")";
    
    return polygonStr;
  }
  
  private boolean isPolygon(Zone node)
  {
    return (node.getAttributeValue("polygon") != null) && (!node.getAttributeValue("polygon").trim().isEmpty());
  }
  
  private boolean isIntersect(Zone zone1, Zone zone2) {
    if (!zone1.get_Bounds().intersects(zone2.get_Bounds())) {
      return false;
    }
    Polygon poly1 = getPolygon(zone1);
    Polygon poly2 = getPolygon(zone2);
    
    Area f_a = new Area(poly1);
    Area z_a = new Area(poly2);
    z_a.intersect(f_a);
    
    if (z_a.isEmpty()) {
      return false;
    }
    blackPixel = currentHWObjcurr_canvas.getBlackPixelValue();
    numOfBands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    image = OCRInterface.currentHWObj.getOriginalImage().getData();
    filter = OCRInterface.this_interface.getIntersectionThreshold();
    globalCount = 0;
    visitPixels(z_a);
    int count = 0;
    
    for (int y = 0; y < getBoundsheight; y++) {
      for (int x = 0; x < getBoundswidth; x++) {
        Point pixel = new Point(getBoundsx + x, getBoundsy + y);
        if ((z_a.contains(pixel)) && (isBlackPixel(pixel)))
          globalCount += 1;
        if (visited_global[x][y] != 0) {
          count++;
        }
      }
    }
    int id1 = Integer.parseInt(zoneID);
    int id2 = Integer.parseInt(zoneID);
    if (id1 < id2) {
      zone1.setAttributeValue("overlapcnt", globalCount);
    } else {
      zone2.setAttributeValue("overlapcnt", globalCount);
    }
    if (count == 0) {
      return false;
    }
    return true;
  }
  




























  private boolean isBlackPixel(Point pixel)
  {
    if (!image.getBounds().contains(pixel)) {
      return false;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    return pixelValue == blackPixel;
  }
  
  private void visitPixels(Area poly) {
    visited_global = new boolean[getBoundswidth][getBoundsheight];
    
    for (int y = 0; y < getBoundsheight; y++) {
      for (int x = 0; x < getBoundswidth; x++) {
        Point p = new Point(getBoundsx + x, getBoundsy + y);
        if ((image.getBounds().contains(p)) && (poly.contains(p))) {
          getCC(x, y, poly);
        }
      }
    }
  }
  










  private void getCC(int x, int y, Area bounds)
  {
    int xDelta = 0;
    int yDelta = 0;
    
    if (bounds != null) {
      xDelta = getBoundsx;
      yDelta = getBoundsy;
    }
    
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      return;
    }
    Point pIn = new Point(x, y);
    if (visited_global[(x - xDelta)][(y - yDelta)] != 0) {
      return;
    }
    Queue<Point> queue = new java.util.LinkedList();
    ArrayList<Point> noise = null;
    
    noise = new ArrayList(filter);
    
    int numOfPixels = 0;
    

    Point p0 = new Point(x, y);
    queue.add(p0);
    
    visited_global[(x - xDelta)][(y - yDelta)] = 1;
    














    while (!queue.isEmpty()) {
      Point p = (Point)queue.poll();
      
      numOfPixels++;
      
      if ((numOfPixels <= filter) && 
        (noise != null)) {
        noise.add(p);
      }
      






      Point neighborP = new Point(x, y - 1);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x + 1, y - 1);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x + 1, y);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x + 1, y + 1);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x, y + 1);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x - 1, y + 1);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x - 1, y);
      examine(neighborP, queue, bounds);
      

      neighborP = new Point(x - 1, y - 1);
      examine(neighborP, queue, bounds);
    }
    

    if (numOfPixels <= filter) {
      resetNoisePixels(noise, bounds);
      noise = null;
    }
    
    queue = null;
  }
  
  private void resetNoisePixels(ArrayList<Point> checkedPixels, Shape bounds)
  {
    if (checkedPixels == null) {
      return;
    }
    int xDelta = 0;
    int yDelta = 0;
    
    if (bounds != null) {
      xDelta = getBoundsx;
      yDelta = getBoundsy;
    }
    

    for (Point p : checkedPixels) {
      visited_global[(x - xDelta)][(y - yDelta)] = 0;
    }
  }
  
  private void examine(Point pixel, Queue<Point> queue, Area bounds) {
    int xDelta = 0;
    int yDelta = 0;
    
    if (bounds != null) {
      xDelta = getBoundsx;
      yDelta = getBoundsy;
      
      if (!bounds.contains(pixel)) {
        return;
      }
    }
    if (!image.getBounds().contains(pixel)) {
      return;
    }
    if (!bounds.contains(pixel)) {
      return;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      return;
    }
    if (visited_global[(x - xDelta)][(y - yDelta)] == 0) {
      visited_global[(x - xDelta)][(y - yDelta)] = 1;
      queue.add(pixel);
    }
  }
  
  private boolean hasBlankZones() {
    this_interfacesaveFilesDialog.saveData();
    String blankZones = getBlankZones();
    if (!blankZones.isEmpty()) {
      int count = blankZones.trim().split("\n").length;
      PolyTranscribeToolBar p = new PolyTranscribeToolBar(); PolyTranscribeToolBar 
        tmp47_46 = p;tmp47_46.getClass();ww = new PolyTranscribeToolBar.WarningWindow(tmp47_46, blankZones, "Blank Zones Warning", 
        "<html>The following zone(s) are blank: # " + count);
      ww.setBlankZonesWarning();
      ww.setThirdStep(true);
      ww.showDialog(warningWindowLocation, warningWindowSize);
      return true;
    }
    return false;
  }
  
  public void verify(boolean continueAnyway) {
    OCRInterface.this_interface.getCanvas().unlockSoftLock();
    
    if (!isContentOK()) {
      return;
    }
    if (!isOffsetsOK()) {
      return;
    }
    if ((!continueAnyway) && (!consecutiveNEWTU_OK())) {
      return;
    }
    verify_part2(false);
  }
  
  public void verify_part2(boolean continueAnyway) {
    if ((!continueAnyway) && (intersectionExist())) {
      return;
    }
    verify_part3(false);
  }
  
  public void verify_part3(boolean continueAnyway) {
    OCRInterface ocrIF = OCRInterface.this_interface;
    FilePropPacket fpp = workmodeProps[ocrTable.my_mode]
      .getElementFilePropVec(WorkmodeTable.curRow);
    
    if ((!continueAnyway) && (hasBlankZones())) {
      return;
    }
    if (!isReadingOrderOK()) {
      return;
    }
    
    if (!isFirstZoneInReadingOrderOK()) {
      return;
    }
    
    String result = "Verification is done.\nReading order is OK.";
    
    JOptionPane.showMessageDialog(
      OCRInterface.this_interface, 
      result, 
      "Verification", 
      1);
    
    fpp.setSoftLocked(1, true, true);
  }
  
  public void setWarningWindow(JDialog dlg) {
    if (!dlg.isShowing())
      return;
    warningWindowLocation = (dlg == null ? null : dlg.getLocationOnScreen());
    warningWindowSize = (dlg == null ? null : dlg.getSize());
  }
  
  public void setPairPanel(JPanel panel)
  {
    pairPanel = panel;
  }
  
  public void resetPairPanel(Zone zone) {
    if (pairPanel == null) {
      return;
    }
    Component[] comps = pairPanel.getComponents();
    Vector<Zone> selectedZones = ImageDisplay.activeZones;
    Vector<String> selectedIDs = new Vector(selectedZones.size());
    
    for (Zone z : selectedZones) {
      selectedIDs.add(zoneID);
    }
    for (Component c : comps) {
      Component[] comps2 = ((Container)c).getComponents();
      for (Component cc : comps2) {
        if ((cc instanceof PolyTranscribeToolBar.LinkLabel)) {
          PolyTranscribeToolBar.LinkLabel l = (PolyTranscribeToolBar.LinkLabel)cc;
          if ((selectedIDs.contains(l.getZoneID())) && (zone != null)) {
            cc.setForeground(YELLOW);
            ((JComponent)c).scrollRectToVisible(l.getBounds());
          }
          else {
            cc.setForeground(BLUE); }
          cc.repaint();
        }
      }
    }
  }
}
