package ocr.gui;

import gttool.document.DLPage;
import gttool.exceptions.DLException;
import gttool.exceptions.DLExceptionCodes;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.swing.JLabel;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageReaderDrawer;
import ocr.util.BidiString;


















public class OrientedBox
  extends Zone
{
  private double orientation;
  private Point startPt;
  private Point endPt;
  private Point rotationPt;
  private Point polygonCenter = null;
  

  public static Polygon currentPolygon;
  
  private static final int HEIGHT = 0;
  
  private static final int WIDTH = 1;
  
  public static final int ROTATION = 2;
  
  public static final int CORNER = 3;
  
  public static final double PRECISION = 1000.0D;
  
  public int selectedPtIndex = 0;
  



  public int selectedCorner;
  



  public static boolean rotationFlag;
  


  public static final String rotateIcon = "images/rotate.gif";
  



  public OrientedBox(int xIn, int yIn, float scale, double orientation)
  {
    super(true);
    
    this.orientation = orientation;
    startPt = new Point();
    startPt.x = ((int)(xIn / scale));
    startPt.y = ((int)(yIn / scale));
    height = 1;
    width = 1;
    
    setBoxOrigin(startPt.x, startPt.y);
    
    isIncomplete = true;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = 15;
    endPt = new Point(0, 0);
    rotationPt = null;
    
    selPt = null;
    rotationFlag = false;
  }
  
  public OrientedBox(String zoneID, Point origin, int width, int height, double orientation) {
    super(false);
    this.zoneID = zoneID;
    this.origin = origin;
    this.width = width;
    this.height = height;
    this.orientation = orientation;
  }
  
  public OrientedBox(Point origin, int width, int height, double orientation) {
    super(true);
    this.origin = origin;
    this.width = width;
    this.height = height;
    this.orientation = orientation;
  }
  









































































  public void drawOrientedBox(Graphics g, float scale, boolean isSelected, int eTextSize, Point mouseIn)
  {
    Polygon poly = getPolygon();
    
    int[] x = new int[npoints];
    int[] y = new int[npoints];
    
    for (int i = 0; i < npoints; i++) {
      x[i] = ((int)(xpoints[i] * scale));
      y[i] = ((int)(ypoints[i] * scale));
    }
    
    currentPolygon = new Polygon(x, y, 4);
    
    if (isSelected)
    {



      Color currColor = g.getColor();
      float[] comps = currColor.getComponents(null);
      
      currColor = new Color(comps[0], comps[1], comps[2], 1.0F - ocrIF.TRANSPARENCY);
      
      g.setColor(currColor);
      

      Graphics2D g2d = (Graphics2D)g;
      AlphaComposite comp = AlphaComposite.getInstance(
        3, 0.45F);
      g2d.setComposite(comp);
      g2d.fillPolygon(x, y, 4);
      g2d.setComposite(AlphaComposite.getInstance(
        3, 1.0F));
      
      Point[] vertex = getPolygonVertices(getPolygon());
      for (Point p : vertex)
      {
        int radius = 2;
        g.fillOval((int)(x * scale) - radius, (int)(y * scale) - radius, radius * 2, radius * 2);
      }
      

      Point firstPoint = vertex[0];
      int radius = 6;
      g.drawOval((int)(x * scale) - radius, (int)(y * scale) - radius, radius * 2, radius * 2);
      
      if (mouseIn != null)
      {
        radius = 4;
        Point closestPt = null;
        
        closestPt = closeTo(mouseIn, scale);
        
        if (closestPt != null) {
          g.fillOval((int)(x * scale) - radius, (int)(y * scale) - radius, radius * 2, radius * 2);
        }
      }
      drawRotationBar(g2d, scale, mouseIn);
    }
    



    if (!OCRInterface.this_interface.isHideBoxes()) {
      g.drawPolygon(x, y, 4);
    }
    drawRLE(g, scale);
    


    if ((selPt != null) && (isSelected)) {
      int cSize = 4;
      
      g.fillRect((int)(selPt.x * scale) - cSize, (int)(selPt.y * scale) - cSize, cSize * 2, cSize * 2);
    }
    
    if (hasContents()) {
      int widthOfText = g.getFontMetrics().stringWidth(getContents());
      int heightOfText = g.getFontMetrics().getHeight();
      
      Point highestVertex = getHighestVertex(getPolygon());
      int x2;
      int x2; if (new BidiString(getContents(), 3).getDirection() == 0) {
        x2 = (int)(x * scale);
      } else {
        x2 = (int)(x * scale - widthOfText);
      }
      int y2 = (int)(y * scale - heightOfText);
      
      displayContents(g, scale, isSelected, eTextSize, x2, y2);
    }
  }
  


























































































































  private void drawRotationBar(Graphics2D g2d, float scale, Point mouseIn)
  {
    Line2D[] sides = getPolygonSides(getPolygon());
    Point[] vertex = getPolygonVertices(getPolygon());
    int x1 = 0;int y1 = 0;int x2 = 0;int y2 = 0;
    



    x1 = (int)(sides[0].getX1() + (sides[0].getX2() - sides[0].getX1()) / 2.0D);
    y1 = (int)(sides[0].getY1() + (sides[0].getY2() - sides[0].getY1()) / 2.0D);
    



    double sine = 0.0D;double cosine = 0.0D;
    









    sine = Math.sin(getOrientation(vertex[0], vertex[1]));
    cosine = Math.cos(getOrientation(vertex[0], vertex[1]));
    
    int barLength = (int)(20.0F / scale);
    




    x2 = (int)(x1 - barLength * sine);
    y2 = (int)(y1 - barLength * cosine);
    
    rotationPt = new Point(x2, y2);
    
    x1 = (int)(x1 * scale);
    y1 = (int)(y1 * scale);
    x2 = (int)(x2 * scale);
    y2 = (int)(y2 * scale);
    
    int width = 2;
    BasicStroke bs = new BasicStroke(width);
    g2d.setStroke(bs);
    g2d.drawLine(x1, y1, x2, y2);
    bs = new BasicStroke(ocrIF.getLineThickness());
    g2d.setStroke(bs);
    
    int radius = 4;
    g2d.fillOval(x2 - radius, y2 - radius, radius * 2, radius * 2);
    
    if (mouseIn != null)
    {

      radius = 6;
      Point closestPt = null;
      
      closestPt = closeTo(mouseIn, scale);
      
      if ((closestPt != null) && (closestPt.equals(rotationPt)))
      {
        g2d.fillOval((int)(x * scale) - radius, 
          (int)(y * scale) - radius, 
          radius * 2, radius * 2);
      }
    }
  }
  











  public void draw(Graphics g, double xThumb, double yThumb, int imWidth, int imHeight, int rotate, float scale, boolean isSelected, boolean drawSplit)
  {
    double sine = Math.sin(orientation);
    double cosine = Math.cos(orientation);
    
    int w = dlGetZoneWidth();
    int h = dlGetZoneHeight();
    int[] vertexX = new int[4];
    int[] vertexY = new int[4];
    

    vertexX[0] = ((int)(dlGetZoneOriginx + xThumb / scale));
    vertexY[0] = ((int)(dlGetZoneOriginy + yThumb / scale));
    
    vertexX[1] = ((int)(vertexX[0] + w * cosine));
    vertexY[1] = ((int)(vertexY[0] - w * sine));
    
    vertexX[2] = ((int)(vertexX[0] + w * cosine + h * sine));
    vertexY[2] = ((int)(vertexY[0] - w * sine + h * cosine));
    
    vertexX[3] = ((int)(vertexX[0] + h * sine));
    vertexY[3] = ((int)(vertexY[0] + h * cosine));
    
    for (int i = 0; i < vertexX.length; i++) {
      vertexX[i] = ((int)(vertexX[i] * scale));
      vertexY[i] = ((int)(vertexY[i] * scale));
    }
    
    g.drawPolygon(vertexX, vertexY, 4);
  }
  








  public Point closeTo(Point p, float scale)
  {
    int buffer = 6;
    Point[] vertex = getPolygonVertices(getPolygon());
    for (Point pt : vertex)
    {
      if ((x <= x * scale + buffer) && (y <= y * scale + buffer) && 
        (x >= x * scale - buffer) && (y >= y * scale - buffer)) {
        return pt;
      }
    }
    if ((rotationPt != null) && (x <= rotationPt.x * scale + buffer) && (y <= rotationPt.y * scale + buffer) && 
      (x >= rotationPt.x * scale - buffer) && (y >= rotationPt.y * scale - buffer)) {
      return rotationPt;
    }
    return null;
  }
  














  public void creationDrag(int xIn, int yIn, float scale)
  {
    endPt.x = ((int)(xIn / scale));
    endPt.y = ((int)(yIn / scale));
    
    double width = Math.sqrt((xIn / scale - startPt.x) * (xIn / scale - startPt.x) + 
      (yIn / scale - startPt.y) * (yIn / scale - startPt.y));
    try
    {
      dlSetZoneWidth((int)width);
      setBoxOrientation(startPt, endPt);
    }
    catch (DLException localDLException) {}
  }
  

  public Point getEndPt()
  {
    return endPt;
  }
  
  public Point getStartPt() {
    return startPt;
  }
  


  public Rectangle get_Bounds()
  {
    return getPolygon().getBounds();
  }
  






  public Polygon getPolygon()
  {
    double sine = Math.sin(orientation);
    double cosine = Math.cos(orientation);
    int w = dlGetZoneWidth();
    int h = dlGetZoneHeight();
    int[] vertexX = new int[4];
    int[] vertexY = new int[4];
    
    vertexX[0] = dlGetZoneOriginx;
    vertexY[0] = dlGetZoneOriginy;
    
    vertexX[1] = ((int)(vertexX[0] + w * cosine));
    vertexY[1] = ((int)(vertexY[0] - w * sine));
    
    vertexX[2] = ((int)(vertexX[0] + w * cosine + h * sine));
    vertexY[2] = ((int)(vertexY[0] - w * sine + h * cosine));
    
    vertexX[3] = ((int)(vertexX[0] + h * sine));
    vertexY[3] = ((int)(vertexY[0] + h * cosine));
    
    Polygon p = new Polygon(vertexX, vertexY, 4);
    currentPolygon = p;
    return p;
  }
  







  public Point getPolygonCenter(Polygon p)
  {
    int polygonCenterX = 0;int polygonCenterY = 0;
    Point[] polygonVertex = getPolygonVertices(p);
    for (int i = 0; i < polygonVertex.length; i++) {
      polygonCenterX += x;
      polygonCenterY += y;
    }
    
    polygonCenterX /= polygonVertex.length;
    polygonCenterY /= polygonVertex.length;
    
    return new Point(polygonCenterX, polygonCenterY);
  }
  

  public Point getCenter(float scale)
  {
    int polygonCenterX = 0;int polygonCenterY = 0;
    Point[] polygonVertex = getPolygonVertices(getPolygon());
    for (int i = 0; i < polygonVertex.length; i++) {
      polygonCenterX = (int)(polygonCenterX + x * scale);
      polygonCenterY = (int)(polygonCenterY + y * scale);
    }
    
    polygonCenterX /= polygonVertex.length;
    polygonCenterY /= polygonVertex.length;
    
    return new Point(polygonCenterX, polygonCenterY);
  }
  
















  public void setBoxHeight(int xIn, int yIn, float scale)
  {
    double height = Line2D.ptLineDist(
      startPt.x, startPt.y, 
      endPt.x, endPt.y, 
      xIn / scale, yIn / scale);
    try
    {
      dlSetZoneHeight((int)height);
    }
    catch (DLException localDLException) {}
  }
  
  public void setBoxHeight(double height)
  {
    try
    {
      dlSetZoneHeight((int)height);
    }
    catch (DLException localDLException) {}
  }
  

  public void setBoxWidth(int xIn, int yIn, float scale)
  {
    double width = Math.sqrt((xIn / scale - startPt.x) * (xIn / scale - startPt.x) + 
      (yIn / scale - startPt.y) * (yIn / scale - startPt.y));
    try
    {
      dlSetZoneWidth((int)width);
    }
    catch (DLException localDLException) {}
  }
  
  public void setBoxWidth(double width)
  {
    try
    {
      dlSetZoneWidth((int)width);
    }
    catch (DLException localDLException) {}
  }
  

  public void rotate(int pageWidth)
  {
    double newOrient = orientation - 1.5707963267948966D;
    if (newOrient < 0.0D)
      newOrient += 6.283185307179586D;
    setBoxOrientation(newOrient);
    setBoxOrigin(pageWidth - origin.y, origin.x);
  }
  
  public void setBoxOrigin(int x, int y)
  {
    try {
      dlSetZoneOrigin(x, y);
    }
    catch (DLException localDLException) {}
  }
  
  public void setBoxOrientation(double orientation)
  {
    try
    {
      dlSetZoneOrientation(orientation);
    }
    catch (DLException localDLException) {}
  }
  

  public void setBoxOrientation(Point start, Point end)
  {
    double angle = -Math.atan2(y - y, 
      x - x);
    try
    {
      dlSetZoneOrientation(angle);
    }
    catch (DLException localDLException) {}
  }
  




  public void dlSetZoneHeight(int h)
    throws DLException
  {
    int currentHeight = dlGetZoneHeight();
    int pageW = OCRInterface.currentHWObj.getOriginalImage().getWidth();
    int pageH = OCRInterface.currentHWObj.getOriginalImage().getHeight();
    
    height = h;
    
    if (dlIsWithinPageBoundaries(pageW, pageH)) {
      height = h;
    } else {
      height = currentHeight;
      throw new DLException(
        DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
        "<DLZone::dlSetZoneWidth()> The specified width has gone beyond the width of the document page image!");
    }
  }
  


  public void dlSetZoneWidth(int w)
    throws DLException
  {
    int currentWidth = dlGetZoneWidth();
    int pageW = OCRInterface.currentHWObj.getOriginalImage().getWidth();
    int pageH = OCRInterface.currentHWObj.getOriginalImage().getHeight();
    
    width = w;
    
    if (dlIsWithinPageBoundaries(pageW, pageH)) {
      width = w;
    } else {
      width = currentWidth;
      throw new DLException(
        DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
        "<DLZone::dlSetZoneWidth()> The specified width has gone beyond the width of the document page image!");
    }
  }
  
  public Point dlGetZoneOrigin() {
    return origin;
  }
  



  public void dlSetZoneOrigin(int xIn, int yIn)
    throws DLException
  {
    if (zonePage == null) {
      System.out.println("zonePage: " + zonePage);
      origin.x = xIn;
      origin.y = yIn;
    }
    else {
      Point currentOrigin = (Point)dlGetZoneOrigin().clone();
      origin.x = xIn;
      origin.y = yIn;
      int pageW = zonePage.dlGetWidth();
      int pageH = zonePage.dlGetHeight();
      
      if (dlIsWithinPageBoundaries(pageW, pageH))
      {
        origin.x = xIn;
        origin.y = yIn;
      }
      else
      {
        origin.x = x;
        origin.y = y;
        throw new DLException(
          DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
          "<DLZone::dlSetZoneWidth()> The specified width has gone beyond the width of the document page image!");
      }
    }
  }
  



  public void dlSetZoneOrientation(double orientation)
    throws DLException
  {
    if (zonePage == null) {
      this.orientation = orientation;
    } else {
      double currentOrientation = getOrientation();
      this.orientation = orientation;
      int pageW = zonePage.dlGetWidth();
      int pageH = zonePage.dlGetHeight();
      
      if (dlIsWithinPageBoundaries(pageW, pageH)) {
        this.orientation = orientation;
      }
      else {
        this.orientation = currentOrientation;
        throw new DLException(
          DLExceptionCodes.DL_OUT_IMAGE_BOUNDARY_EXCEPTION, 
          "<DLZone::dlSetZoneWidth()> The specified width has gone beyond the width of the document page image!");
      }
    }
    
    setAttributeValue("orientationD", getDegrees(orientation));
  }
  





  public double getOrientation(Point start, Point end)
  {
    return -Math.atan2(y - y, 
      x - x);
  }
  
  public double getOrientation() {
    return orientation;
  }
  











  public Point getHighestVertex(Polygon p)
  {
    TreeMap<Integer, Integer> vertex = new TreeMap(
      new Comparator() {
        public int compare(Integer arg0, Integer arg1) {
          return arg0.compareTo(arg1);
        }
      });
    
    for (int i = 0; i < npoints; i++) {
      vertex.put(Integer.valueOf(ypoints[i]), Integer.valueOf(xpoints[i]));
    }
    Point highestVertex = new Point(((Integer)vertex.get(vertex.firstKey())).intValue(), ((Integer)vertex.firstKey()).intValue());
    
    return highestVertex;
  }
  




  public OrientedBox clone_zone()
  {
    OrientedBox z = new OrientedBox(zoneID.toString(), (Point)origin.clone(), width, height, orientation);
    zonePage = zonePage;
    z.getZoneTags().putAll(getZoneTags());
    parentZone = parentZone;
    nextZone = nextZone;
    previousZone = previousZone;
    childZones = getChildrenZones();
    startPt = startPt;
    endPt = endPt;
    rotationPt = rotationPt;
    caret = caret;
    
    return z;
  }
  







  public boolean doesContain(int xIn, int yIn, float scale)
  {
    double scaledXin = xIn / scale;
    double scaledYin = yIn / scale;
    
    Polygon zone = getPolygon();
    
    return zone.contains(scaledXin, scaledYin);
  }
  
  public boolean doesContain(Point2D p) {
    return getPolygon().contains(p);
  }
  





  public boolean intersects(Zone zIn)
  {
    Rectangle2D rectIn = new Rectangle(zIn.dlGetZoneOrigin(), new Dimension(zIn.get_width(), 
      zIn.get_height()));
    
    return rectIn.contains(getPolygon().getBounds2D());
  }
  










  public boolean dlIntersects(Rectangle selection)
  {
    return selection.contains(getPolygon().getBounds2D());
  }
  











  public double getDistance(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    Polygon zone = getPolygon();
    
    Vector<Double> distances = new Vector();
    
    int n = npoints;
    for (int i = 0; i < n - 1; i++) {
      distances.add(Double.valueOf(Line2D.ptSegDist(xpoints[i], 
        ypoints[i], 
        xpoints[(i + 1)], 
        ypoints[(i + 1)], 
        scaledXin, scaledYin)));
    }
    distances.add(Double.valueOf(Line2D.ptSegDist(xpoints[(n - 1)], 
      ypoints[(n - 1)], 
      xpoints[0], 
      ypoints[0], 
      scaledXin, scaledYin)));
    
    return ((Double)Collections.min(distances)).doubleValue();
  }
  







  public void changeMouseIcon(MouseEvent e, float scale)
  {
    Point pt = e.getPoint();
    
    Polygon zone = getPolygon();
    int n = npoints;
    
    double[] polygonSideOrientation = getPolygonOrientationOfSides(zone);
    
    Point mouse = new Point((int)(x / scale), (int)(y / scale));
    
    double pi8 = 0.39269908169872414D;
    
    int lineIndex = lineToMove(x, y, scale);
    
    Point closestPt = closeTo(new Point(e.getX(), e.getY()), scale);
    ClassLoader loader = getClass().getClassLoader();
    

    if ((closestPt != null) && (closestPt.equals(rotationPt)))
    {
      Toolkit tk = Toolkit.getDefaultToolkit();
      Image rotateImg = tk.getImage(loader.getResource("images/rotate.gif"));
      
      Cursor rotateCur = tk.createCustomCursor(rotateImg, new Point(16, 16), "rotateCur");
      
      ImageReaderDrawer.picture.setCursor(rotateCur);
      Toolkit.getDefaultToolkit().sync();
    }
    else if (closestPt != null)
    {
      Toolkit tk = Toolkit.getDefaultToolkit();
      Image ptMoveImage = tk.getImage(loader.getResource("images/ptmove.gif"));
      Cursor ptMoveCur = tk.createCustomCursor(ptMoveImage, new Point(16, 16), "ptMoveCur");
      
      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      
      Toolkit.getDefaultToolkit().sync();



    }
    else if ((lineIndex != -1) && (!rotationFlag)) {
      for (int i = 0; i < n; i++) {
        if (((polygonSideOrientation[lineIndex] >= -pi8) && 
          (polygonSideOrientation[lineIndex] < pi8)) || 
          (polygonSideOrientation[lineIndex] >= 7.0D * pi8) || 
          (polygonSideOrientation[lineIndex] < -7.0D * pi8)) {
          ImageReaderDrawer.picture.setCursor(
            Cursor.getPredefinedCursor(8));
          
          Toolkit.getDefaultToolkit().sync();
          break;
        }
        if (((polygonSideOrientation[lineIndex] >= pi8) && 
          (polygonSideOrientation[lineIndex] < 3.0D * pi8)) || (
          (polygonSideOrientation[lineIndex] >= -7.0D * pi8) && 
          (polygonSideOrientation[lineIndex] < -5.0D * pi8))) {
          ImageReaderDrawer.picture.setCursor(
            Cursor.getPredefinedCursor(6));
          Toolkit.getDefaultToolkit().sync();
          
          break;
        }
        
        if (((polygonSideOrientation[lineIndex] >= 3.0D * pi8) && 
          (polygonSideOrientation[lineIndex] < 5.0D * pi8)) || (
          (polygonSideOrientation[lineIndex] >= -5.0D * pi8) && 
          (polygonSideOrientation[lineIndex] < -3.0D * pi8))) {
          ImageReaderDrawer.picture.setCursor(
            Cursor.getPredefinedCursor(10));
          Toolkit.getDefaultToolkit().sync();
          
          break;
        }
        
        if (((polygonSideOrientation[lineIndex] >= 5.0D * pi8) && 
          (polygonSideOrientation[lineIndex] < 7.0D * pi8)) || (
          (polygonSideOrientation[lineIndex] >= -3.0D * pi8) && 
          (polygonSideOrientation[lineIndex] < -pi8))) {
          ImageReaderDrawer.picture.setCursor(
            Cursor.getPredefinedCursor(4));
          Toolkit.getDefaultToolkit().sync();
          
          break;
        }
        
      }
    }
    else if ((zone.contains(mouse)) && (!rotationFlag)) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(13));
      Toolkit.getDefaultToolkit().sync();

    }
    else if (!rotationFlag) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(0));
      Toolkit.getDefaultToolkit().sync();
    }
  }
  





  public Point[] getPolygonVertices(Polygon zone)
  {
    Point[] p = new Point[npoints];
    
    for (int i = 0; i < npoints; i++) {
      p[i] = new Point(xpoints[i], ypoints[i]);
    }
    return p;
  }
  




  private Line2D[] getPolygonSides(Polygon zone)
  {
    int n = npoints;
    
    Point[] p = getPolygonVertices(zone);
    
    Line2D[] polygonSide = new Line2D[n];
    
    for (int i = 0; i < n - 1; i++) {
      polygonSide[i] = new Line2D.Double(p[i], p[(i + 1)]);
    }
    polygonSide[(n - 1)] = new Line2D.Double(p[(n - 1)], p[0]);
    
    return polygonSide;
  }
  






  private double[] getPolygonOrientationOfSides(Polygon zone)
  {
    int n = npoints;
    
    Point[] p = getPolygonVertices(zone);
    
    double[] polygonSideOrientation = new double[n];
    
    for (int i = 0; i < n - 1; i++) {
      polygonSideOrientation[i] = getOrientation(p[i], p[(i + 1)]);
    }
    polygonSideOrientation[(n - 1)] = getOrientation(p[(n - 1)], p[0]);
    
    return polygonSideOrientation;
  }
  







  public boolean doesLieOnBoundary(int x, int y, float scale)
  {
    Point mouse = new Point((int)(x / scale), (int)(y / scale));
    
    int buffer = 7;
    
    Line2D[] polygonSide = getPolygonSides(getPolygon());
    
    for (int i = 0; i < polygonSide.length; i++) {
      if ((polygonSide[i].ptSegDist(mouse) >= 0.0D) && 
        (polygonSide[i].ptSegDist(mouse) < 7.0D))
        return true;
    }
    return false;
  }
  









  public int lineToMove(int x, int y, float scale)
  {
    Point mouse = new Point((int)(x / scale), (int)(y / scale));
    
    int buffer = 7;
    
    Line2D[] polygonSide = getPolygonSides(getPolygon());
    
    for (int i = 0; i < polygonSide.length; i++) {
      if ((polygonSide[i].ptSegDist(mouse) >= 0.0D) && 
        (polygonSide[i].ptSegDist(mouse) < 7.0D))
        return i;
    }
    return -1;
  }
  





  public void moveTo(int newX, int newY, boolean moveByMouse)
  {
    Rectangle oldBounds = get_Bounds();
    
    if (moveByMouse) {
      newX -= x + moveOffsetX;
      newY -= y + moveOffsetY;
      
      setBoxOrigin(origin.x + newX, origin.y + newY);
    }
    else
    {
      setBoxOrigin(newX, newY);
    }
    super.resizeGroupToWhichZoneBelongs();
  }
  






































  public void selectCorener(int xIn, int yIn, float scale)
  {
    selPt = null;
    
    Point scaledPtIn = new Point((int)(xIn / scale), (int)(yIn / scale));
    
    Polygon zone = getPolygon();
    Point[] polygonVertex = getPolygonVertices(zone);
    Line2D[] polygonSide = getPolygonSides(zone);
    


    Line2D closestLine = getClosestLine(polygonSide, scaledPtIn);
    
    Point2D p1 = closestLine.getP1();
    Point2D p2 = closestLine.getP2();
    
    double closestLineLength = p1.distance(p2);
    double length0 = polygonVertex[0].distance(polygonVertex[1]);
    double length1 = polygonVertex[1].distance(polygonVertex[2]);
    double length2 = polygonVertex[2].distance(polygonVertex[3]);
    double length3 = polygonVertex[3].distance(polygonVertex[0]);
    


    Point closestPt = closeTo(new Point(xIn, yIn), scale);
    if ((closestPt != null) && (closestPt.equals(rotationPt))) {
      lastMouseEvent = null;
      selectedCorner = 2;
      rotationFlag = true;
      Polygon p = getPolygon();
      polygonCenter = new Point(getPolygonCenterx, getPolygonCentery);















































    }
    else if (closestPt != null) {
      selectedCorner = 3;
      if (closestPt.equals(polygonVertex[0])) {
        selectedPtIndex = 0;
      } else if (closestPt.equals(polygonVertex[1])) {
        selectedPtIndex = 1;
      } else if (closestPt.equals(polygonVertex[2])) {
        selectedPtIndex = 2;
      } else if (closestPt.equals(polygonVertex[3])) {
        selectedPtIndex = 3;
      }
    }
    else if ((closestLineLength == length0) || (closestLineLength == length2))
    {




















      selectedPtIndex = 2;
      if ((p1.distance(dlGetZoneOrigin()) == 0.0D) || 
        (p2.distance(dlGetZoneOrigin()) == 0.0D)) {
        selectedPtIndex = 0;
      }
      selectedCorner = 1;

    }
    else if ((closestLineLength == length1) || (closestLineLength == length3))
    {
















      selectedPtIndex = 2;
      if ((p1.distance(dlGetZoneOrigin()) == 0.0D) || 
        (p2.distance(dlGetZoneOrigin()) == 0.0D)) {
        selectedPtIndex = 0;
      }
      
      selectedCorner = 0;
    }
  }
  




































  public void editSelectedCorener(int xIn, int yIn, float scale)
  {
    switch (selectedCorner) {
    case -1: 
      System.out.println("Error: corner unselected [Zone:565]");
      return;
    case 3: 
      Polygon p = getPolygon();
      Line2D[] polygonSide = getPolygonSides(p);
      
      Point[] vertex = getPolygonVertices(getPolygon());
      int fixedX = selectedPtIndex + 2) % 4)].x;
      int fixedY = selectedPtIndex + 2) % 4)].y;
      

      double distance = Line2D.ptSegDist(
        polygonSide[((selectedPtIndex + 1 + selectedPtIndex % 2) % 4)].getX1(), polygonSide[((selectedPtIndex + 1 + selectedPtIndex % 2) % 4)].getY1(), 
        polygonSide[((selectedPtIndex + 1 + selectedPtIndex % 2) % 4)].getX2(), polygonSide[((selectedPtIndex + 1 + selectedPtIndex % 2) % 4)].getY2(), 
        xIn / scale, yIn / scale);
      if (distance < 10.0D) {
        setBoxWidth(10.0D);
      } else {
        setBoxWidth(distance);
      }
      polygonSide = getPolygonSides(p);
      distance = Line2D.ptSegDist(
        polygonSide[((selectedPtIndex + 2 + selectedPtIndex % 2 * 3) % 4)].getX1(), polygonSide[((selectedPtIndex + 2 + selectedPtIndex % 2 * 3) % 4)].getY1(), 
        polygonSide[((selectedPtIndex + 2 + selectedPtIndex % 2 * 3) % 4)].getX2(), polygonSide[((selectedPtIndex + 2 + selectedPtIndex % 2 * 3) % 4)].getY2(), 
        xIn / scale, yIn / scale);
      if (distance < 10.0D) {
        setBoxHeight(10.0D);
      } else {
        setBoxHeight(distance);
      }
      vertex = getPolygonVertices(getPolygon());
      try
      {
        dlSetZoneOrigin(0x - selectedPtIndex + 2) % 4)].x + fixedX, 0y - selectedPtIndex + 2) % 4)].y + fixedY);
      }
      catch (DLException e)
      {
        selectedCorner = -1;
      }
      


      vertex = getPolygonVertices(getPolygon());
      selPt = vertex[selectedPtIndex];
      


      break;
    case 0: 
      Polygon p = getPolygon();
      Line2D[] polygonSide = getPolygonSides(p);
      
      Point[] vertex = getPolygonVertices(getPolygon());
      int fixedX = selectedPtIndex + 2) % 4)].x;
      int fixedY = selectedPtIndex + 2) % 4)].y;
      

      double distance = Line2D.ptSegDist(
        polygonSide[(selectedPtIndex + 1)].getX1(), polygonSide[(selectedPtIndex + 1)].getY1(), 
        polygonSide[(selectedPtIndex + 1)].getX2(), polygonSide[(selectedPtIndex + 1)].getY2(), 
        xIn / scale, yIn / scale);
      setBoxWidth(distance);
      
      vertex = getPolygonVertices(getPolygon());
      try
      {
        dlSetZoneOrigin(0x - selectedPtIndex + 2) % 4)].x + fixedX, 0y - selectedPtIndex + 2) % 4)].y + fixedY);
      }
      catch (DLException e)
      {
        selectedCorner = -1;
      }
    

    case 1: 
      Polygon p = getPolygon();
      Line2D[] polygonSide = getPolygonSides(p);
      
      Point[] vertex = getPolygonVertices(getPolygon());
      int fixedX = selectedPtIndex + 2) % 4)].x;
      int fixedY = selectedPtIndex + 2) % 4)].y;
      
      double distance = Line2D.ptSegDist(
        polygonSide[((selectedPtIndex + 2) % 4)].getX1(), polygonSide[((selectedPtIndex + 2) % 4)].getY1(), 
        polygonSide[((selectedPtIndex + 2) % 4)].getX2(), polygonSide[((selectedPtIndex + 2) % 4)].getY2(), 
        xIn / scale, yIn / scale);
      setBoxHeight(distance);
      
      vertex = getPolygonVertices(getPolygon());
      try
      {
        dlSetZoneOrigin(0x - selectedPtIndex + 2) % 4)].x + fixedX, 0y - selectedPtIndex + 2) % 4)].y + fixedY);
      }
      catch (DLException e)
      {
        selectedCorner = -1;
      }
    






    case 2: 
      Polygon p = getPolygon();
      
      Point mouseEvent = new Point((int)(xIn / scale), (int)(yIn / scale));
      try
      {
        dlSetZoneOrientation(getOrientation(getPolygonCenter(p), mouseEvent) - 1.5707963267948966D);
        p = getPolygon();
        dlSetZoneOrigin(xpoints[0] - getPolygonCenterx + polygonCenter.x, ypoints[0] - getPolygonCentery + polygonCenter.y);

      }
      catch (DLException e)
      {
        selectedCorner = -1;
      }
      
      OCRInterface.this_interface.getCanvas().paintCanvas();
      
      OCRInterface.this_interface.getCanvas().paintCanvas();
      
      lastMouseEvent = mouseEvent;
    }
    
    

    super.resizeGroupToWhichZoneBelongs();
  }
  





  private Line2D getClosestLine(Line2D[] polygonSides, Point scaledMousePt)
  {
    TreeMap<Double, Line2D> distances = new TreeMap(
      new Comparator() {
        public int compare(Double arg0, Double arg1) {
          return arg0.compareTo(arg1);
        }
      });
    Polygon zone = getPolygon();
    int n = npoints;
    for (int i = 0; i < n - 1; i++) {
      distances.put(Double.valueOf(Line2D.ptSegDist(xpoints[i], 
        ypoints[i], 
        xpoints[(i + 1)], 
        ypoints[(i + 1)], 
        x, 
        y)), 
        new Line2D.Double(
        xpoints[i], 
        ypoints[i], 
        xpoints[(i + 1)], 
        ypoints[(i + 1)]));
    }
    distances.put(Double.valueOf(Line2D.ptSegDist(xpoints[(n - 1)], 
      ypoints[(n - 1)], 
      xpoints[0], 
      ypoints[0], 
      x, 
      y)), 
      new Line2D.Double(
      xpoints[(n - 1)], 
      ypoints[(n - 1)], 
      xpoints[0], 
      ypoints[0]));
    
    Line2D closestLine = (Line2D)distances.get(distances.firstKey());
    
    return closestLine;
  }
  




  public String toString()
  {
    String returnVal = "[id=" + zoneID + " Type =" + getZoneType() + " ((" + 
      dlGetZoneOriginx + "," + dlGetZoneOriginy + ")(" + dlGetZoneWidth() + 
      "," + dlGetZoneHeight() + ")) [parent =" + 
      parentZone + " ] ]";
    

    return returnVal;
  }
  



















  protected boolean dlIsWithinPageBoundaries(int pageWidth, int pageHeight)
  {
    return true;
  }
  
  private void printC()
  {
    System.out.println("********");
    
    System.out.println("width, height: " + dlGetZoneWidth() + "," + dlGetZoneHeight());
    System.out.println("startPt (x, y)/endPt(x,y): " + startPt.x + "," + startPt.y + "/" + 
      endPt.x + "," + endPt.y);
    System.out.println("********");
  }
  
  public void setLastMouseEvent(Point lastMouseEvent) {
    this.lastMouseEvent = lastMouseEvent;
  }
  







  public static String getDegrees(double radians)
  {
    double degrees = Math.toDegrees(radians);
    double temp = degrees * 1000.0D;
    degrees = Math.round(temp) / 1000.0D;
    
    return Double.toString(degrees);
  }
  
  public int get_lt_y() {
    return origin.y;
  }
  
  public int get_lt_x() {
    return origin.x;
  }
}
