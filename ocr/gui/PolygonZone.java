package ocr.gui;

import gttool.document.DLPage;
import gttool.exceptions.DLException;
import gttool.exceptions.DLExceptionCodes;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.BidiString;








public class PolygonZone
  extends Zone
{
  public Vector<Point> points;
  public Vector<Line2D> sides;
  Point startPt;
  public Point newPt;
  private int selPtIndex;
  private int selectedSideIndex = -1;
  
  public Polygon currPoly;
  
  public Polygon absoluteCurrPoly;
  
  private boolean isPolygonIncomplete;
  
  private boolean resizing = false;
  private int sideIndex = -1;
  
  public PolygonZone()
  {
    super(false);
  }
  








  public PolygonZone(int xIn, int yIn, float scale, Vector<Point> pointsIn)
  {
    super(true);
    
    startPt = new Point();
    startPt.x = ((int)(xIn / scale));
    startPt.y = ((int)(yIn / scale));
    
    setBoxOrigin(startPt.x, startPt.y);
    width = 1;
    height = 1;
    
    isIncomplete = true;
    
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = 16;
    isPolygonIncomplete = true;
    
    newPt = null;
    resetSelPt();
    resetSelectedSide();
    
    if (pointsIn != null) {
      points = pointsIn;
      rectifyPoints();
    } else {
      points = new Vector();
    }
  }
  






  public PolygonZone(String zoneID, Point origin, int width, int height, Vector<Point> pointsIn)
  {
    super(false);
    this.zoneID = zoneID;
    this.origin = origin;
    startPt = origin;
    this.width = width;
    this.height = height;
    
    if (pointsIn != null) {
      points = pointsIn;
    }
    else {
      points = new Vector();
    }
    
    absoluteCurrPoly = new Polygon();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    Rectangle bounds = get_Bounds();
    this.origin = bounds.getLocation();
    startPt = bounds.getLocation();
    this.width = width;
    this.height = height;
    
    rectifyPoints();
  }
  
  public PolygonZone(Vector<Point> verteces) {
    super(true);
    
    startPt = origin;
    points = verteces;
    Rectangle bounds = get_Bounds();
    origin = bounds.getLocation();
    lt = origin;
    rb = new Point(x + width, y + height);
    startPt = origin;
    width = width;
    height = height;
    rectifyPoints();
    setAttributeValue("polygon", getPoints());
    absoluteCurrPoly = new Polygon();
    for (Point point : points)
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
  }
  
  public PolygonZone(Point origin, Vector<Point> pointsIn, int dx, int dy) {
    super(true);
    
    this.origin = ((Point)origin.clone());
    startPt = ((Point)origin.clone());
    width = 1;
    height = 1;
    
    Vector<Point> newPoints = new Vector();
    
    for (Point p : pointsIn) {
      Point tempPt = new Point(p);
      newPoints.add(tempPt);
    }
    points = newPoints;
    translatePoints(dx, dy);
  }
  
  public PolygonZone(Vector<Point> pointsIn, Map<String, String> zoneTags)
  {
    super(true);
    
    origin = new Point(0, 0);
    startPt = new Point(0, 0);
    points = pointsIn;
    
    rectifyPoints();
    absoluteCurrPoly = new Polygon();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    getZoneTags().putAll(zoneTags);
    setAttributeValue("polygon", getPoints());
  }
  










  public PolygonZone clone_zone()
  {
    PolygonZone z = new PolygonZone();
    z.getZoneTags().putAll(getZoneTags());
    parentZone = parentZone;
    nextZone = nextZone;
    previousZone = previousZone;
    caret = caret;
    selectedSideIndex = selectedSideIndex;
    isIncomplete = isIncomplete;
    lineSelected = lineSelected;
    zoneID = zoneID;
    
    points = derectifyPoints();
    
    Rectangle bounds = get_Bounds();
    origin = bounds.getLocation();
    startPt = bounds.getLocation();
    width = width;
    height = height;
    
    z.rectifyPoints();
    
    return z;
  }
  






































































  public void rectifyPoints()
  {
    for (int i = 0; i < points.size(); i++) {
      Point p = (Point)((Point)points.get(i)).clone();
      
      p.translate(-startPt.x, -startPt.y);
      points.set(i, p);
    }
  }
  
  public Vector<Point> derectifyPoints() {
    Vector<Point> newVector = new Vector(points.size());
    
    for (int i = 0; i < points.size(); i++) {
      Point p = (Point)((Point)points.get(i)).clone();
      
      p.translate(startPt.x, startPt.y);
      newVector.add(p);
    }
    
    return newVector;
  }
  
  public Vector<Point> getPointsVec() {
    return points;
  }
  
  public void setPointsVec(Vector<Point> newPoints) {
    points = newPoints;
    
    rectifyPoints();
    
    absoluteCurrPoly.reset();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    setAttributeValue("polygon", getPoints());
  }
  
  public void translatePoints(int dx, int dy) {
    Vector<Point> newPoints = new Vector();
    
    for (Point p : points) {
      Point tempPt = (Point)p.clone();
      tempPt.translate(dx, dy);
      newPoints.add(tempPt);
    }
    
    points = newPoints;
  }
  










  public void drawPolygonZone(Graphics g, float scale, boolean isSelected, int eTextSize, Point pt, boolean isControlDown)
  {
    int size = points.size();
    int[] xA = new int[size];
    int[] yA = new int[size];
    int[] xA2 = new int[size];
    int[] yA2 = new int[size];
    
    for (int i = 0; i < size; i++) {
      Point tempPt = new Point(points.get(i)).x + startPt.x, points.get(i)).y + startPt.y);
      
      xA[i] = ((int)(x * scale));
      yA[i] = ((int)(y * scale));
      xA2[i] = (points.get(i)).x + startPt.x);
      yA2[i] = (points.get(i)).y + startPt.y);
    }
    
    currPoly = new Polygon(xA, yA, size);
    absoluteCurrPoly = new Polygon(xA2, yA2, size);
    
    if ((isSelected) && (currPoly.npoints > 0)) {
      Color currColor = g.getColor();
      float[] comps = currColor.getComponents(null);
      
      currColor = new Color(comps[0], comps[1], comps[2], 1.0F - ocrIF.TRANSPARENCY);
      if (color == null) {
        g.setColor(currColor);
      } else {
        g.setColor(color);
      }
      Graphics2D g2d = (Graphics2D)g;
      AlphaComposite comp = AlphaComposite.getInstance(
        3, 0.45F);
      g2d.setComposite(comp);
      g2d.fillPolygon(currPoly);
      g2d.setComposite(AlphaComposite.getInstance(
        3, 1.0F));
      

      for (Point p : points)
      {


        int radius = 2;
        
        g.fillOval((int)((startPt.x + x) * scale) - radius, 
          (int)((startPt.y + y) * scale) - radius, 
          radius * 2, radius * 2);
      }
      
      Point firstPoint = (Point)points.firstElement();
      int radius = 6;
      g.drawOval((int)((startPt.x + x) * scale) - radius, 
        (int)((startPt.y + y) * scale) - radius, 
        radius * 2, radius * 2);
    }
    



    if (!OCRInterface.this_interface.isHideBoxes()) {
      g.drawPolygon(currPoly);
    }
    drawRLE(g, scale);
    

    if (pt != null)
    {
      int radius = 4;
      Point closestPt = null;
      
      closestPt = closeTo(pt, scale);
      
      if (closestPt != null) {
        g.fillOval((int)((startPt.x + x) * scale) - radius, 
          (int)((startPt.y + y) * scale) - radius, 
          radius * 2, radius * 2);
      }
    }
    


    if ((ImageDisplay.activeZones.size() == 1) && 
      (((Zone)ImageDisplay.activeZones.get(0)).equals(this)) && 
      (isControlDown) && (isSelected)) {
      selPt = null;
      drawNewPt(g, scale, pt);
    } else {
      newPt = null;
    }
    

    if ((selPt != null) && (isSelected)) {
      int cornerSize = 4;
      
      g.fillRect((int)((startPt.x + selPt.x) * scale) - cornerSize, 
        (int)((startPt.y + selPt.y) * scale) - cornerSize, 
        cornerSize * 2, cornerSize * 2);
    }
    

    String zoneName = getAttributeValue("gedi_type");
    if ((!isIncomplete) && (ocrIF.getShowZoneTypes()) && (zoneName != null)) {
      Graphics2D g2d = (Graphics2D)g;
      int textSize = currentHWObjcurr_canvas.getElectronicTextSize();
      
      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), 1, textSize);
      g2d.setFont(updatedFont);
      
      Color currentColor = g2d.getColor();
      
      int widthOfText = g2d.getFontMetrics().stringWidth(zoneName);
      





      Point center = getCenter(scale);
      
      float x = x - widthOfText / 2;
      float y = y + size / 2;
      
      g2d.drawString(zoneName, x, y);
      g2d.setColor(currentColor);
      g2d.setFont(currFont);
    }
    
    if ((zoneName != null) && (hasContents()) && (!isIncomplete)) {
      String contents = getContents();
      
      if (contents.length() == 0) {
        return;
      }
      



      Graphics2D g2d = (Graphics2D)g;
      
      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), eTextSize);
      g2d.setFont(updatedFont);
      

      int widthOfText = g2d.getFontMetrics().stringWidth(contents);
      int heightOfText = g2d.getFontMetrics().getHeight();
      


      Point textVertex = OCRInterface.this_interface.getUseStartPoint() ? 
        new Point(currPoly.xpoints[0], currPoly.ypoints[0]) : 
        getHighestVertex(currPoly);
      int x;
      int x; if (new BidiString(contents, 3).getDirection() == 0) {
        x = x;
      } else {
        x = x - widthOfText;
      }
      int y = y - heightOfText;
      
      displayContents(g, scale, isSelected, eTextSize, x, y);
      
      g2d.setFont(currFont);
    }
  }
  





  public static Point getHighestVertex(Polygon p)
  {
    int highest = 0;
    for (int i = 0; i < npoints; i++) {
      if (ypoints[i] < ypoints[highest])
        highest = i;
    }
    return new Point(xpoints[highest], ypoints[highest]);
  }
  









  public void draw(Graphics g, double xThumb, double yThumb, int imWidth, int imHeight, int rotate, float scale, boolean isSelected, boolean drawSplit)
  {
    int size = points.size();
    int[] xA = new int[size];
    int[] yA = new int[size];
    
    for (int i = 0; i < size; i++) {
      Point tempPt = new Point(points.get(i)).x + startPt.x + (int)(xThumb / scale), points.get(i)).y + startPt.y + (int)(yThumb / scale));
      
      xA[i] = ((int)(x * scale));
      yA[i] = ((int)(y * scale));
    }
    
    currPoly = new Polygon(xA, yA, size);
    g.drawPolygon(currPoly);
  }
  
  public void rotate(int pageWidth) {
    Rectangle bounds = get_Bounds();
    for (int i = 0; i < points.size(); i++) {
      Point tempPt = (Point)points.get(i);
      
      int swap = x;
      x = (height - y);
      y = swap;
      points.set(i, tempPt);
    }
    
    int swap = startPt.y;
    startPt.y = startPt.x;
    startPt.x = (pageWidth - height - swap);
    
    setAttributeValue("polygon", getPoints());
  }
  
  private void drawThickLine(Graphics g, int thickness, int x1, int y1, int x2, int y2)
  {
    int dX = x2 - x1;
    int dY = y2 - y1;
    
    double lineLength = Math.sqrt(dX * dX + dY * dY);
    double scale = thickness / (2.0D * lineLength);
    

    double ddx = -scale * dY;
    double ddy = scale * dX;
    ddx += (ddx > 0.0D ? 0.5D : -0.5D);
    ddy += (ddy > 0.0D ? 0.5D : -0.5D);
    int dx = (int)ddx;
    int dy = (int)ddy;
    

    int[] xPoints = new int[4];
    int[] yPoints = new int[4];
    
    xPoints[0] = (x1 + dx);yPoints[0] = (y1 + dy);
    xPoints[1] = (x1 - dx);yPoints[1] = (y1 - dy);
    xPoints[2] = (x2 - dx);yPoints[2] = (y2 - dy);
    xPoints[3] = (x2 + dx);yPoints[3] = (y2 + dy);
    
    g.fillPolygon(xPoints, yPoints, 4);
  }
  
  private void drawNewPt(Graphics g, float scale, Point pt)
  {
    sideIndex = getClosestLine(scale, pt);
    Line2D currLine = (Line2D)sides.get(sideIndex);
    int thickness = (int)(8.0F * scale);
    
    drawThickLine(g, thickness, (int)currLine.getX1(), 
      (int)currLine.getY1(), 
      (int)currLine.getX2(), 
      (int)currLine.getY2());
    int radius = 4;
    g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    
    int scaledXin = (int)(x / scale);
    int scaledYin = (int)(y / scale);
    
    newPt = new Point(scaledXin - startPt.x, 
      scaledYin - startPt.y);
  }
  
  public int getClosestLine(float scale, Point pt)
  {
    Vector<Double> distances = new Vector();
    getSides(scale, pt);
    
    for (Line2D line : sides) {
      distances.add(Double.valueOf(line.ptSegDist(pt)));
    }
    
    return distances.indexOf(Collections.min(distances));
  }
  

  public void getSides(float scale, Point pt)
  {
    sides = new Vector();
    
    for (int i = 0; i < points.size() - 1; i++) {
      Point p1 = new Point((int)((points.get(i)).x + startPt.x) * scale), 
        (int)((points.get(i)).y + startPt.y) * scale));
      Point p2 = new Point((int)((points.get(i + 1)).x + startPt.x) * scale), 
        (int)((points.get(i + 1)).y + startPt.y) * scale));
      Line2D.Float newLine = new Line2D.Float(p1, p2);
      
      sides.add(newLine);
    }
    
    Point p1 = new Point((int)((points.firstElement()).x + startPt.x) * scale), 
      (int)((points.firstElement()).y + startPt.y) * scale));
    Point p2 = new Point((int)((points.lastElement()).x + startPt.x) * scale), 
      (int)((points.lastElement()).y + startPt.y) * scale));
    Line2D.Float newLine = new Line2D.Float(p1, p2);
    
    sides.add(newLine);
  }
  




  public void addPoint()
  {
    if (newPt == null) {
      return;
    }
    points.add(sideIndex + 1, newPt);
    setAttributeValue("polygon", getPoints());
    
    absoluteCurrPoly.reset();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    updateRLEIMAGE();
    

    newPt = null;
    
    super.resizeGroupToWhichZoneBelongs();
  }
  







  public void recalculate(boolean ptModified)
  {
    Point lastPt = (Point)points.lastElement();
    Point nextToLastPt = (Point)points.get(points.size() - 2);
    
    PointComparator pc = new PointComparator();
    
    if (pc.compare(lastPt, nextToLastPt) == 0) {
      points.remove(points.get(points.size() - 1));
    }
    if ((selPtIndex == 0) && (ptModified)) {
      normalize();
    }
    removeDuplicates();
  }
  
  public void removeDuplicates()
  {
    Iterator<Point> ptItr = points.iterator();
    Point prev = (Point)ptItr.next();
    while (ptItr.hasNext()) {
      Point cur = (Point)ptItr.next();
      if (cur.equals(prev)) {
        ptItr.remove();
      } else {
        prev = cur;
      }
    }
  }
  
  private void normalize() {
    Point firstPt = (Point)points.firstElement();
    int normalizedX = x - 1;
    int normalizedY = y - 1;
    
    for (int i = 0; i < points.size(); i++) {
      Point tempPt = (Point)points.get(i);
      tempPt.translate(-normalizedX, -normalizedY);
      points.set(i, tempPt);
    }
    
    startPt.translate(normalizedX, normalizedY);
  }
  
  public void resetSelPt()
  {
    selPt = null;
    selPtIndex = -1;
  }
  




  public boolean deleteSelectedPoint()
  {
    points.remove(selPt);
    
    recalculate(true);
    setAttributeValue("polygon", getPoints());
    
    resetSelPt();
    
    absoluteCurrPoly.reset();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    updateRLEIMAGE();
    

    super.resizeGroupToWhichZoneBelongs();
    
    if (points.size() > 1) {
      return false;
    }
    return true;
  }
  






  public void selectPoint(int xIn, int yIn, float scale)
  {
    Point ptIn = new Point(xIn, yIn);
    resetSelPt();
    
    selPt = closeTo(ptIn, scale);
    selPtIndex = points.indexOf(selPt);
  }
  







  public void editSelectedPoint(int xIn, int yIn, float scale)
  {
    if (selPt == null)
    {
      return;
    }
    
    setResizing(true);
    
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    selPt.x = (scaledXin - startPt.x);
    selPt.y = (scaledYin - startPt.y);
    
    points.setElementAt(selPt, selPtIndex);
    recalculate(true);
    
    setAttributeValue("polygon", getPoints());
  }
  







  public void drawSide(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    if (points.size() <= 1) {
      points.add(new Point(scaledXin - startPt.x, scaledYin - startPt.y));
    } else {
      points.set(points.size() - 1, new Point(scaledXin - startPt.x, scaledYin - startPt.y));
    }
  }
  





  public void createSide(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    points.add(new Point(scaledXin - startPt.x, scaledYin - startPt.y));
  }
  







  public boolean doesLieOnBoundary(int x, int y, float scale)
  {
    int scaledXin = (int)(x / scale);
    int scaledYin = (int)(y / scale);
    int buffer = 7;
    
    if ((scaledXin >= get_lt_x()) && (scaledXin <= get_rb_x())) {
      if (((scaledYin >= get_lt_y()) && (scaledYin <= get_lt_y() + 7)) || (
        (scaledYin >= get_rb_y() - 7) && (scaledYin <= get_rb_y()))) {
        return true;
      }
      if (((scaledXin >= get_lt_x()) && (scaledXin <= get_lt_x() + 7)) || (
        (scaledXin >= get_rb_x() - 7) && (scaledXin <= get_rb_x()) && 
        (scaledYin >= get_lt_y()) && (scaledYin <= get_rb_y()))) {
        return true;
      }
    }
    return false;
  }
  





  public void moveTo(int newX, int newY, boolean moveByMouse)
  {
    Rectangle oldBounds = get_Bounds();
    


    if (!moveByMouse) {
      moveOffsetX = 0;
      moveOffsetY = 0;
    }
    
    newX -= x + moveOffsetX;
    newY -= y + moveOffsetY;
    
    startPt.x += newX;
    startPt.y += newY;
    
    setAttributeValue("polygon", getPoints());
    
    resetSelPt();
    
    super.resizeGroupToWhichZoneBelongs();
  }
  









































  public int get_width()
  {
    return currPoly.getBounds().width;
  }
  
















  public int get_height()
  {
    return currPoly.getBounds().height;
  }
  


  public int get_lt_x()
  {
    return getLtpoints).x + startPt.x;
  }
  


  public int get_lt_y()
  {
    return getLtpoints).y + startPt.y;
  }
  


  public int get_rb_x()
  {
    return getRbpoints).x + startPt.x;
  }
  


  public int get_rb_y()
  {
    return getRbpoints).y + startPt.y;
  }
  













  public Rectangle get_Bounds()
  {
    int width = get_rb_x() - get_lt_x();
    int height = get_rb_y() - get_lt_y();
    
    return new Rectangle(get_lt_x(), get_lt_y(), width, height);
  }
  


  private static Point getLt(Vector<Point> points)
  {
    Point currPt = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    
    for (Point p : points) {
      if (x < x)
        x = x;
      if (y < y) {
        y = y;
      }
    }
    return currPt;
  }
  


  private static Point getRb(Vector<Point> points)
  {
    Point currPt = new Point(-1, -1);
    
    for (Point p : points) {
      if (x > x)
        x = x;
      if (y > y) {
        y = y;
      }
    }
    return currPt;
  }
  








  public double getDistance(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    Vector<Double> distances = new Vector();
    
    for (int i = 0; i < points.size() - 1; i++)
    {
      distances.add(Double.valueOf(Line2D.ptSegDist(currPoly.xpoints[i] / scale, currPoly.ypoints[i] / scale, 
        currPoly.xpoints[(i + 1)] / scale, currPoly.ypoints[(i + 1)] / scale, 
        scaledXin, scaledYin)));
    }
    
    distances.add(Double.valueOf(Line2D.ptSegDist(currPoly.xpoints[(currPoly.npoints - 1)] / scale, currPoly.ypoints[(currPoly.npoints - 1)] / scale, 
      currPoly.xpoints[0] / scale, currPoly.ypoints[0] / scale, 
      scaledXin, scaledYin)));
    


    return ((Double)Collections.min(distances)).doubleValue();
  }
  
  public int getNumPoints()
  {
    return points.size();
  }
  




  public String getPoints()
  {
    String str = "";
    Point tempPt = new Point();
    
    recalculate(false);
    
    PointComparator pc = new PointComparator();
    
    for (int i = 0; i < points.size(); i++) {
      Point p = (Point)((Point)points.get(i)).clone();
      p.translate(startPt.x, startPt.y);
      
      if (i == 0) {
        tempPt = p;
        str = str + "(" + x + "," + y + ")";
      }
      else if (pc.compare(tempPt, p) != 0) {
        str = str + ";(" + x + "," + y + ")";
      }
    }
    

    return str;
  }
  



  public Point getStartPt()
  {
    return startPt;
  }
  



  public boolean getIsPolygonIncomplete()
  {
    return isPolygonIncomplete;
  }
  



  public void setIsPolygonIncomplete(boolean b)
  {
    isPolygonIncomplete = b;
  }
  








  public boolean doesContain(int xIn, int yIn, float scale)
  {
    int scaledXin = xIn;
    int scaledYin = yIn;
    
    return currPoly.contains(scaledXin, scaledYin);
  }
  

  public boolean doesContain(Point2D p)
  {
    Polygon polygon = new Polygon();
    for (Point point : points) {
      polygon.addPoint(x + startPt.x, y + startPt.y);
    }
    return polygon.contains(p);
  }
  



















  public void setBoxOrigin(int x, int y)
  {
    try
    {
      dlSetZoneOrigin(x, y);
    }
    catch (DLException localDLException) {}
  }
  






  public boolean contains(Point p)
  {
    return currPoly.contains(p);
  }
  






  public Point closeTo(Point p, float scale)
  {
    int buffer = 6;
    for (Point pt : points) {
      if ((x <= (startPt.x + x) * scale + buffer) && (y <= (startPt.y + y) * scale + buffer) && 
        (x >= (startPt.x + x) * scale - buffer) && (y >= (startPt.y + y) * scale - buffer)) {
        return pt;
      }
    }
    return null;
  }
  








  public void normalizePolygon()
  {
    Rectangle bounds = get_Bounds();
    
    Point p = new Point((int)(bounds.getX() + bounds.getWidth()), 
      (int)bounds.getY());
    
    double distance = 0.0D;
    int index = 0;
    
    distance = p.distance(absoluteCurrPoly.xpoints[0], absoluteCurrPoly.ypoints[0]);
    index = 0;
    
    for (int i = 0; i < absoluteCurrPoly.npoints; i++) {
      double d = p.distance(absoluteCurrPoly.xpoints[i], absoluteCurrPoly.ypoints[i]);
      if (d < distance) {
        distance = d;
        index = i;
      }
    }
    
    if (index == 0) {
      return;
    }
    Vector<Point> newPoints = new Vector(points.size());
    newPoints.add(new Point(absoluteCurrPoly.xpoints[index] - startPt.x, 
      absoluteCurrPoly.ypoints[index] - startPt.y));
    
    for (int i = 0; i < absoluteCurrPoly.npoints; i++) {
      index++;
      if (index == absoluteCurrPoly.npoints)
        index = 0;
      newPoints.add(new Point(absoluteCurrPoly.xpoints[index] - startPt.x, 
        absoluteCurrPoly.ypoints[index] - startPt.y));
    }
    
    points = newPoints;
    
    absoluteCurrPoly.reset();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    setAttributeValue("polygon", getPoints());
  }
  








  public boolean dlIntersects(Rectangle rIn)
  {
    if (!isVisible())
      return false;
    float currentScale = OCRInterface.this_interface.getCanvas().getScale();
    Rectangle rInScaled = new Rectangle((int)(x * currentScale), 
      (int)(y * currentScale), 
      (int)(width * currentScale), 
      (int)(height * currentScale));
    


    for (int i = 0; i < currPoly.npoints; i++) {
      if (!rInScaled.contains(currPoly.xpoints[i], currPoly.ypoints[i]))
        return false;
    }
    return true;
  }
  













  public boolean intersects(Zone zIn)
  {
    if (!isVisible())
      return false;
    Rectangle rectIn = new Rectangle(zIn.dlGetZoneOrigin(), new Dimension(zIn.get_width(), 
      zIn.get_height()));
    
    float currentScale = OCRInterface.this_interface.getCanvas().getScale(); Rectangle 
      tmp47_46 = rectIn;4746x = ((int)(4746x * currentScale)); Rectangle 
      tmp59_58 = rectIn;5958y = ((int)(5958y * currentScale)); Rectangle 
      tmp71_70 = rectIn;7170width = ((int)(7170width * currentScale)); Rectangle 
      tmp83_82 = rectIn;8382height = ((int)(8382height * currentScale));
    


    for (int i = 0; i < currPoly.npoints; i++) {
      if (!rectIn.contains(currPoly.xpoints[i], currPoly.ypoints[i]))
        return false;
    }
    return true;
  }
  



























































  public String toString()
  {
    String returnVal = "[id=" + zoneID + " Type =" + getZoneType() + " ((" + 
      "Points: " + getPoints() + ")) [parent =" + 
      parentZone + " ] ]";
    

    return returnVal;
  }
  


  public boolean isPtSelected()
  {
    return selPt != null;
  }
  


  public boolean isBeingResized()
  {
    return resizing;
  }
  



  public void setResizing(boolean b)
  {
    resizing = b;
  }
  
  public Point dlGetZoneOrigin() {
    return startPt;
  }
  












  public void dlSetZoneOrigin(int xIn, int yIn)
    throws DLException
  {
    if (zonePage == null) {
      origin.x = xIn;
      origin.y = yIn;
    } else {
      Point currentOrigin = (Point)dlGetZoneOrigin().clone();
      origin.x = xIn;
      origin.y = yIn;
      int pageW = zonePage.dlGetWidth();
      int pageH = zonePage.dlGetHeight();
      
      if (dlIsWithinPageBoundaries(pageW, pageH)) {
        origin.x = xIn;
        origin.y = yIn;
      }
      else {
        origin.x = x;
        origin.y = y;
        throw new DLException(
          DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
          "<DLZone::dlSetZoneWidth()> The specified width has gone beyond the width of the document page image!");
      }
    }
  }
  
  public Point getCenter(float scale)
  {
    int numVerts = absoluteCurrPoly.npoints;
    int polygonCenterX = 0;int polygonCenterY = 0;
    
    for (int i = 0; i < numVerts; i++) {
      polygonCenterX = (int)(polygonCenterX + absoluteCurrPoly.xpoints[i] * scale);
      polygonCenterY = (int)(polygonCenterY + absoluteCurrPoly.ypoints[i] * scale);
    }
    polygonCenterX /= numVerts;
    polygonCenterY /= numVerts;
    
    return new Point(polygonCenterX, polygonCenterY);
  }
  



  public class PointComparator
    implements Comparator<Object>
  {
    public PointComparator() {}
    



    public int compare(Object s1, Object s2)
    {
      if ((((Point2D)s1).getX() == ((Point2D)s2).getX()) && 
        (((Point2D)s1).getY() == ((Point2D)s2).getY())) {
        return 0;
      }
      return -1;
    }
  }
  
  private void updateRLEIMAGE()
  {
    String rleImage = (String)getZoneTags().get("RLEIMAGE");
    if ((OCRInterface.this_interface.getRecomputeRLEonEdit()) && 
      (rleImage != null) && (!rleImage.isEmpty())) {
      currentHWObjcurr_canvas.createRLE_CC(this, true, false, false);
    }
  }
  









































































  public void moveSelectedSide(Point mouse, float scale, boolean isShiftDown)
  {
    if (selectedSideIndex == -1) {
      return;
    }
    if (isShiftDown) {
      moveSelectedSideArbitrarily(mouse, scale);
    } else
      moveSelectedSideAlongPerpendicular(mouse, scale);
  }
  
  private void moveSelectedSideArbitrarily(Point currentMousePosition, float scale) {
    Point lastMousePosition = getLastMouseEvent();
    
    System.out.println("last: " + lastMousePosition);
    System.out.println("current: " + currentMousePosition);
    
    if (lastMousePosition == null) {
      return;
    }
    int deltaX = (int)((currentMousePosition.getX() - lastMousePosition.getX()) / scale);
    int deltaY = (int)((currentMousePosition.getY() - lastMousePosition.getY()) / scale);
    
    System.out.println("deltaX/deltaY: " + deltaX + "/" + deltaY);
    
    Line2D selectedSide = getSide(selectedSideIndex);
    


    double x1 = selectedSide.getX1();
    double y1 = selectedSide.getY1();
    double x2 = selectedSide.getX2();
    double y2 = selectedSide.getY2();
    
    double xo1 = x1 + deltaX;
    double xo2 = x2 + deltaX;
    double yo1 = y1 + deltaY;
    double yo2 = y2 + deltaY;
    
    selectedSide = getSide(selectedSideIndex);
    
    updatePolygonSidePoints(x1, y1, x2, y2, xo1, xo2, yo1, yo2);
  }
  









  private void moveSelectedSideAlongPerpendicular(Point mouse, float scale)
  {
    Line2D selectedSide = getSide(selectedSideIndex);
    




    double x1 = selectedSide.getX1();
    double y1 = selectedSide.getY1();
    double x2 = selectedSide.getX2();
    double y2 = selectedSide.getY2();
    double xc = mouse.getX() / scale;
    double yc = mouse.getY() / scale;
    
    double vx = x2 - x1;double vy = y2 - y1;
    
    double vr = Math.sqrt(vx * vx + vy * vy);
    
    vx /= vr;vy /= vr;
    
    double vpx = vy;double vpy = -vx;
    
    double d = ((x2 - x1) * (y1 - yc) - (y2 - y1) * (x1 - xc)) / vr;
    
    double xo1 = x1 + d * vpx;double xo2 = x2 + d * vpx;
    double yo1 = y1 + d * vpy;double yo2 = y2 + d * vpy;
    
    selectedSide = getSide(selectedSideIndex);
    
    updatePolygonSidePoints(x1, y1, x2, y2, xo1, xo2, yo1, yo2);
  }
  









  private void updatePolygonSidePoints(double x1, double y1, double x2, double y2, double xo1, double xo2, double yo1, double yo2)
  {
    Vector<Point> newPoints = new Vector(points.size());
    
    for (Point p : points) {
      if ((p.getX() + startPt.x == x1) && (p.getY() + startPt.y == y1))
        p = new Point((int)Math.round(xo1 - startPt.x), (int)Math.round(yo1 - startPt.y));
      if ((p.getX() + startPt.x == x2) && (p.getY() + startPt.y == y2)) {
        p = new Point((int)Math.round(xo2 - startPt.x), (int)Math.round(yo2 - startPt.y));
      }
      newPoints.add(p);
    }
    
    points = newPoints;
    
    absoluteCurrPoly.reset();
    for (Point point : points) {
      absoluteCurrPoly.addPoint(x + startPt.x, y + startPt.y);
    }
    setAttributeValue("polygon", getPoints());
  }
  
  public Line2D getSide(int index) {
    return (Line2D)getSides().get(index);
  }
  
  public Vector<Line2D> getSides()
  {
    Vector<Line2D> sides = new Vector();
    
    for (int i = 0; i < points.size() - 1; i++) {
      Point p1 = new Point(points.get(i)).x + startPt.x, 
        points.get(i)).y + startPt.y);
      Point p2 = new Point(points.get(i + 1)).x + startPt.x, 
        points.get(i + 1)).y + startPt.y);
      Line2D.Double newLine = new Line2D.Double(p1, p2);
      
      sides.add(newLine);
    }
    
    Point p1 = new Point(points.firstElement()).x + startPt.x, 
      points.firstElement()).y + startPt.y);
    Point p2 = new Point(points.lastElement()).x + startPt.x, 
      points.lastElement()).y + startPt.y);
    Line2D.Double newLine = new Line2D.Double(p1, p2);
    
    sides.add(newLine);
    
    return sides;
  }
  















  public int getClosestLineIndex(Point mouse, float scale)
  {
    Vector<Line2D> sides = getSides();
    Point2D.Double pt = new Point2D.Double(mouse.getX() / scale, 
      mouse.getY() / scale);
    
    int buffer = 7;
    
    for (int i = 0; i < sides.size(); i++) {
      if ((((Line2D)sides.get(i)).ptSegDist(pt) >= 0.0D) && 
        (((Line2D)sides.get(i)).ptSegDist(pt) <= 7.0D))
        return i;
    }
    return -1;
  }
  



















  public int getSelectedSideIndex()
  {
    return selectedSideIndex;
  }
  
  public void setSelectedSideIndex(int index) {
    selectedSideIndex = index;
  }
  
  public void resetSelectedSide()
  {
    selectedSideIndex = -1;
  }
  
  public double getSideOrientation(Point2D start, Point2D end) {
    return -Math.atan2(end.getY() - start.getY(), 
      end.getX() - start.getX());
  }
  
  public void adjust() {
    super.adjust();
  }
}
