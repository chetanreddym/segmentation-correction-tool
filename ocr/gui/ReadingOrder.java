package ocr.gui;

import gttool.document.DLZone;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.util.UniqueZoneId;

public class ReadingOrder
{
  private Vector<DLZone> allZones;
  private boolean readingOrderFound;
  
  public ReadingOrder()
  {
    readingOrderFound = false;
  }
  
  public ReadingOrder(Vector<DLZone> allZones) {
    readingOrderFound = false;
    this.allZones = allZones;
    setUpReadingOrder();
    validateReadingOrder();
  }
  
  public void removeFromReadingOrder(DLZone zone) {
    if (previousZone != null) {
      previousZone.nextZone = null;
      previousZone = null;
    }
    
    if (nextZone != null) {
      nextZone.previousZone = null;
      nextZone = null;
    }
  }
  






  public void deleteFromReadingOrder(DLZone zone)
    throws NullPointerException
  {
    if ((previousZone != null) && (nextZone != null)) {
      previousZone.nextZone = nextZone;
      nextZone.previousZone = previousZone;
    }
    if (previousZone == null) {
      nextZone.previousZone = null;
    }
    if (nextZone == null)
      previousZone.nextZone = null;
  }
  
  private void setUpReadingOrder() {
    UniqueZoneId searcher = new UniqueZoneId(OCRInterface.currDoc);
    
    for (DLZone zone : allZones)
    {
      String nextZoneID = zone.getAttributeValue("nextZoneID");
      
      if (nextZoneID != null) {
        readingOrderFound = true;
        DLZone nextZone = searcher.searchZone(nextZoneID, false);
        if (nextZone != null) {
          nextZone = nextZone;
          previousZone = zone;
        }
      }
      


      zone.removeAttribute("nextZoneID");
    }
  }
  














  private void validateReadingOrder()
  {
    if (!readingOrderFound) {
      return;
    }
    boolean problemFound = false;
    Vector<String> problematicZones = new Vector();
    boolean do_more;
    boolean error; label159: for (Iterator localIterator = currentHWObjcurr_canvas.getShapeVec().iterator(); localIterator.hasNext(); 
        






        (do_more) && (!error))
    {
      DLZone zone = (DLZone)localIterator.next();
      if ((previousZone == null) && (nextZone == null)) {
        break label159;
      }
      Vector<String> visited = new Vector();
      DLZone nextZone = null;
      do_more = true;
      error = false;
      continue;
      visited.add(zoneID);
      nextZone = nextZone;
      if (nextZone == null) {
        do_more = false;

      }
      else if (visited.contains(zoneID)) {
        nextZone = null;
        error = true;
        problemFound = true;
        problematicZones.add(zoneID);
      }
      else {
        zone = nextZone;
      }
    }
    
    if (!problemFound) {
      return;
    }
    for (DLZone zone : currentHWObjcurr_canvas.getShapeVec())
      if ((previousZone != null) || (nextZone != null))
      {
        previousZone = null; }
    DLZone nextZone;
    label311:
    for (localIterator = currentHWObjcurr_canvas.getShapeVec().iterator(); localIterator.hasNext(); 
        





        nextZone != null)
    {
      DLZone zone = (DLZone)localIterator.next();
      if ((previousZone == null) && (nextZone == null)) {
        break label311;
      }
      nextZone = null;
      nextZone = nextZone;
      
      continue;
      previousZone = zone;
      zone = nextZone;
      nextZone = nextZone;
    }
    

    OCRInterface.currDoc.dumpData();
    
    String msg = "WARNING: invalid Reading Order has been discovered and fixed \n(either reading order has been cyclic or zone pointed to itself).The following zone(s) caused the problem: \n";
    


    for (String id : problematicZones) {
      msg = msg + id + ", ";
    }
    msg = msg.trim().substring(0, msg.trim().length() - 1);
    
    JOptionPane.showMessageDialog(OCRInterface.this_interface, 
      msg, 
      "Reading Order Warning", 
      2);
  }
  








  public boolean isCyclicReadingOrder(DLZone zone)
  {
    String currentId = zoneID;
    DLZone nextZone = nextZone;
    while (nextZone != null) {
      if (zoneID == currentId) {
        return true;
      }
      nextZone = nextZone;
    }
    
    return false;
  }
  










  public boolean isHeader(DLZone selectedZone)
  {
    return (nextZone != null) && (previousZone == null);
  }
  











  public boolean isEnd(DLZone selectedZone)
  {
    return (nextZone == null) && (previousZone != null);
  }
  



  public void drawArrow(Graphics g, boolean header, int xStart, int yStart, int xEnd, int yEnd)
  {
    float stroke = 0.2F;
    double aDir = Math.atan2(xStart - xEnd, yStart - yEnd);
    Graphics2D g2 = (Graphics2D)g;
    g2.setStroke(new java.awt.BasicStroke(2.0F));
    
    g2.setColor(Color.RED);
    

    if (header)
    {
      int radius = 6;
      g.drawOval(xStart - radius, 
        yStart - radius, 
        radius * 2, radius * 2);
    }
    

    g.drawLine(xEnd, yEnd, xStart, yStart);
    Polygon tmpPoly = new Polygon();
    int i1 = 12 + (int)(stroke * 2.0F);
    int i2 = 6 + (int)stroke;
    tmpPoly.addPoint(xEnd, yEnd);
    tmpPoly.addPoint(xEnd + xCor(i1, aDir + 0.5D), yEnd + yCor(i1, aDir + 0.5D));
    tmpPoly.addPoint(xEnd + xCor(i2, aDir), yEnd + yCor(i2, aDir));
    tmpPoly.addPoint(xEnd + xCor(i1, aDir - 0.5D), yEnd + yCor(i1, aDir - 0.5D));
    tmpPoly.addPoint(xEnd, yEnd);
    g.drawPolygon(tmpPoly);
    g.fillPolygon(tmpPoly);
  }
  
  private static int yCor(int len, double dir) { return (int)(len * Math.cos(dir)); }
  private static int xCor(int len, double dir) { return (int)(len * Math.sin(dir)); }
}
