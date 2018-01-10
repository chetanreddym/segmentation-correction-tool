package ocr.gui;

import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLException;
import gttool.misc.TypeAttributeEntry;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.zones.ZonesManager;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.util.AttributeConfigUtil;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.VoronoiAlgorithm;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.RepresentationInterface;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation.CalcCutOff;
import signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import signalprocesser.voronoi.statusstructure.VLinkedNode;










public class RLE_CC_Handler
{
  int minX;
  int maxX;
  int minY;
  int maxY;
  int imageMinX;
  int imageMinY;
  int imageWidth;
  int imageHeight;
  String RLEIMAGE = "";
  int blackPixel = 0;
  Rectangle bounds;
  PolygonZone selectionZonePolygon;
  Zone selectionZone;
  boolean[][] visited_global_single = null;
  boolean[][] visited_global_single_max = null;
  short[][] visited_global_multiple = null;
  OCRInterface ocrIF = OCRInterface.this_interface;
  int filter = ocrIF.getConnectedComponentFilterSize();
  short CC_label = 0;
  int numOfBands = 0;
  boolean createRLE;
  boolean createCC;
  float scale; int numOfSegments = 0;
  
  public RLE_CC_Handler(int blackPixelValue, Zone selection, boolean rle, boolean cc, float scale)
  {
    if ((selection instanceof Group)) {
      return;
    }
    setWaitCursor();
    currentHWObjcurr_canvas.setIdenticalZoneWasCreated(false);
    createRLE = rle;
    createCC = cc;
    this.scale = scale;
    

    Raster image = rotateImage(OCRInterface.currentHWObj.getOriginalImage()).getData();
    getImageParemeters(image);
    
    rotateSelectionZone(selection);
    
    selectionZone = selection;
    bounds = selection.get_Bounds();
    blackPixel = blackPixelValue;
    
    minX = -1;
    maxX = -1;
    minY = -1;
    maxY = -1;
    
    numOfSegments = 0;
    





    numOfBands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
    if (numOfBands > 3) {
      numOfBands = 3;
    }
    visited_global_multiple = new short[getBoundswidth][getBoundsheight];
    
    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(bounds.x + x, bounds.y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          getCC(x, y, image, null);
        }
      }
    }
    if (!createCC) {
      createZone();
    }
    visited_global_multiple = null;
    image = null;
    System.gc();
    
    currentHWObjcurr_canvas.showWarning_IdenticalZones();
    
    setDefaultCursor();
  }
  



  public RLE_CC_Handler(int blackPixelValue, int xIn, int yIn, boolean rle, boolean cc, float scale)
  {
    setWaitCursor();
    currentHWObjcurr_canvas.setIdenticalZoneWasCreated(false);
    createRLE = rle;
    createCC = cc;
    blackPixel = blackPixelValue;
    
    numOfSegments = 0;
    

    Raster image = rotateImage(OCRInterface.currentHWObj.getOriginalImage()).getData();
    getImageParemeters(image);
    



    numOfBands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
    if (numOfBands > 3) {
      numOfBands = 3;
    }
    visited_global_single = new boolean[getBoundswidth][getBoundsheight];
    
    int x = (int)(xIn / scale);
    int y = (int)(yIn / scale);
    
    Point p = rotateSelectionPoint(x, y);
    
    getCC(x, y, image, null);
    
    visited_global_single = null;
    image = null;
    System.gc();
    
    currentHWObjcurr_canvas.showWarning_IdenticalZones();
    
    setDefaultCursor();
  }
  


































  public RLE_CC_Handler(int blackPixelValue, Vector<DLZone> zoneVec, float scale, boolean merge)
  {
    setWaitCursor();
    createRLE = false;
    createCC = false;
    this.scale = scale;
    visited_global_multiple = null;
    visited_global_single = null;
    Rectangle union = null;
    
    setWaitCursor();
    

    Raster image = rotateImage(OCRInterface.currentHWObj.getOriginalImage()).getData();
    getImageParemeters(image);
    
    for (DLZone zone : zoneVec) {
      if ((!(zone instanceof Group)) && 
        (!(zone instanceof OrientedBox)))
      {

        rotateSelectionZone((Zone)zone);
        selectionZone = ((Zone)zone);
        bounds = zone.get_Bounds();
        blackPixel = blackPixelValue;
        
        minX = -1;
        maxX = -1;
        minY = -1;
        maxY = -1;
        
        numOfSegments = 0;
        





        numOfBands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
        if (numOfBands > 3) {
          numOfBands = 3;
        }
        if (merge) {
          if (union == null) {
            union = zone.get_Bounds();
          } else {
            union = union.union(zone.get_Bounds());

          }
          

        }
        else if (OCRInterface.this_interface.getExpandThenShrink()) {
          expandAndShrink(zone, image, !OCRInterface.this_interface.getEnableNonConvexPolygonShrinking());
        } else if (!OCRInterface.this_interface.getEnableNonConvexPolygonShrinking()) {
          shrink_pixelwise(zone, image);
        }
        else if (((zone instanceof PolygonZone)) && (OCRInterface.this_interface.getEnableNonConvexPolygonShrinking())) {
          shrinkPolygon_non_convex_hull((PolygonZone)zone, image);
        }
      }
    }
    
    if (merge) {
      for (DLZone zone : zoneVec) {
        merge((Zone)zone, image, union);
      }
      PolygonZone newPolygon = createNewPolygon(image, union);
      setNewPolygon(zoneVec, newPolygon);
    }
    
    currentHWObjcurr_canvas.paintCanvas();
    
    visited_global_multiple = null;
    image = null;
    System.gc();
    setDefaultCursor();
  }
  
  public RLE_CC_Handler(int blackPixelValue, Vector<Zone> zoneVec, float scale) {
    setWaitCursor();
    createRLE = false;
    createCC = false;
    this.scale = scale;
    visited_global_multiple = null;
    visited_global_single = null;
    blackPixel = blackPixelValue;
    this.scale = scale;
    
    minX = -1;
    maxX = -1;
    minY = -1;
    maxY = -1;
    
    setWaitCursor();
    
    Raster image = rotateImage(OCRInterface.currentHWObj.getOriginalImage()).getData();
    getImageParemeters(image);
    
    numOfBands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
    if (numOfBands > 3) {
      numOfBands = 3;
    }
    

    removeOverlapSimple(zoneVec, image);
  }
  
  private void removeOverlapSimple(Vector<Zone> zoneVec, Raster image) {
    Zone zone1 = (Zone)zoneVec.get(0);
    Zone zone2 = (Zone)zoneVec.get(1);
    
    System.out.println("overlapping zone1/zone2: " + zoneID + "/" + zoneID);
    
    rotateSelectionZone(zone1);
    rotateSelectionZone(zone2);
    
    Polygon poly1 = getPolygon(zone1);
    Polygon poly2 = getPolygon(zone2);
    
    Area area1 = new Area(poly1);
    Area area2 = new Area(poly2);
    
    if ((isInsidePolygon(poly1, poly2)) || (isInsidePolygon(poly2, poly1))) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        "Selected zones are nested. Overlap cannot be removed.", 
        "Overlap remove", 
        1);
      return;
    }
    
    Vector<DLZone> toShrink = new Vector(2);
    
    area2.intersect(area1);
    
    if (area2.isEmpty()) {
      JOptionPane.showMessageDialog(
        OCRInterface.this_interface, 
        "Selected zones do not overlap.", 
        "Overlap remove", 
        1);
      return;
    }
    
    area1 = new Area(poly1);
    area2 = new Area(poly2);
    
    area2.subtract(area1);
    toShrink.add(zone1);
    correctZone(zone2, area2, toShrink);
  }
  
  private boolean isInsidePolygon(Polygon polygon1, Polygon polygon2)
  {
    int[] xpoints = xpoints;
    int[] ypoints = ypoints;
    boolean result = true;
    int i = 0; for (int j = 0; i < npoints; j++) {
      result = polygon1.contains(new Point(xpoints[i], ypoints[j]));
      if (!result) {
        break;
      }
      i++;
    }
    

    return result;
  }
  









  private void removeOverlapIntelligently(Vector<Zone> zoneVec, Raster image)
  {
    Zone zone1 = (Zone)zoneVec.get(0);
    Zone zone2 = (Zone)zoneVec.get(1);
    
    System.out.println("overlapping zone1/zone2: " + zoneID + "/" + zoneID);
    
    rotateSelectionZone(zone1);
    rotateSelectionZone(zone2);
    
    Polygon poly1 = getPolygon(zone1);
    Polygon poly2 = getPolygon(zone2);
    
    Area area1 = new Area(poly1);
    Area area2 = new Area(poly2);
    
    Area area11 = new Area(poly1);
    Area area21 = new Area(poly2);
    
    area1.add(area2);
    area11.intersect(area21);
    
    createRLE = false;
    createCC = false;
    
    selectionZone = zone1;
    
    bounds = area1.getBounds();
    visited_global_multiple = new short[getBoundswidth][getBoundsheight];
    

    for (int y = 0; y < getBoundsheight; y++) {
      for (int x = 0; x < getBoundswidth; x++) {
        Point p = new Point(getBoundsx + x, getBoundsy + y);
        if ((isInsideOfImageBoundaries(p)) && (area11.contains(p))) {
          getCC(x, y, image, area1);
        }
      }
    }
    










    int poly1_count = 0;
    int poly2_count = 0;
    int intersect = 0;
    
    for (int y = 0; y < getBoundsheight; y++) {
      for (int x = 0; x < getBoundswidth; x++) {
        if (visited_global_multiple[x][y] != 0) {
          Point p = new Point(getBoundsx + x, getBoundsy + y);
          if (poly1.contains(p))
            poly1_count++;
          if (poly2.contains(p))
            poly2_count++;
          if (area11.contains(p)) {
            intersect++;
          }
        }
      }
    }
    System.out.println("intersect/poly1_count/poly2_count: " + intersect + "/" + poly1_count + "/" + poly2_count);
    
    Vector<DLZone> toShrink = new Vector(2);
    Area area_p1 = new Area(poly1);
    Area area_p2 = new Area(poly2);
    
    boolean ambigous = false;
    boolean emptyIntersection = false;
    
    if ((intersect == poly1_count) && (intersect == poly2_count)) {
      ambigous = true;
      if ((intersect == 0) && (poly1_count == 0) && (poly2_count == 0)) {
        emptyIntersection = true;
      }
    } else if ((intersect == poly1_count) && (poly2_count > 0)) {
      System.out.println("remove overlap from zone1: " + zoneID);
      area_p1.subtract(area_p2);
      toShrink.add(zone2);
      correctZone(zone1, area_p1, toShrink);
    }
    else if ((intersect == poly2_count) && (poly1_count > 0)) {
      System.out.println("remove overlap from zone2: " + zoneID);
      area_p2.subtract(area_p1);
      toShrink.add(zone1);
      correctZone(zone2, area_p2, toShrink);
    }
    else {
      ambigous = true;
    }
    
    if (ambigous) {
      System.out.println("ambiguity to remove overlap");
      
      if (area11.isEmpty()) {
        JOptionPane.showMessageDialog(
          OCRInterface.this_interface, 
          "Selected zones do not overlap.", 
          "Overlap remove", 
          1);
        return;
      }
      
      if (emptyIntersection) {
        area_p2.subtract(area_p1);
        toShrink.add(zone1);
        correctZone(zone2, area_p2, toShrink);
        return;
      }
      
      Color color = zone2.getSpecificColor();
      String colorStr = ", COLOR:RED)\n";
      if (color == null)
        colorStr = ")\n";
      String msg = "Ambiguous Overlap:\nOverlap will be removed from the second zone selected (ID: " + 
        zoneID + colorStr + 
        "Continue?";
      int answer = JOptionPane.showConfirmDialog(OCRInterface.this_interface, 
        msg, 
        "Overlap remove", 
        0, 
        3);
      if (answer == 0) {
        area_p2.subtract(area_p1);
        toShrink.add(zone1);
        correctZone(zone2, area_p2, toShrink);
      }
    }
  }
  
  private void correctZone(Zone zone, Area area, Vector<DLZone> toShrink) {
    System.out.println("correctZone");
    Polygon mask_tmp = new Polygon();
    
    PathIterator path = area.getPathIterator(null);
    while (!path.isDone()) {
      toPolygon(mask_tmp, path);
      path.next();
    }
    
    Vector<Point> newVerteces = new Vector(npoints);
    
    for (int i = 0; i < npoints; i++) {
      newVerteces.add(new Point(xpoints[i], ypoints[i]));
    }
    if (newVerteces.size() == 0)
    {
      System.out.println("polygon has zero number of points after overlap removal.");
    }
    else if ((zone instanceof PolygonZone)) {
      ((PolygonZone)zone).setPointsVec(newVerteces);
      toShrink.add(zone);
    }
    else if ((zone instanceof Zone)) {
      Zone newZone = OCRInterface.this_interface.convertRectangleToPolygon(zone);
      newZone.setSpecificColor(zone.getSpecificColor());
      currentHWObjcurr_canvas.shapeVec.remove(zone);
      ((PolygonZone)newZone).setPointsVec(newVerteces);
      toShrink.add(newZone);
    }
    


    if ((OCRInterface.this_interface.getShrinkAfterOverlapRemoval()) && (OCRInterface.this_interface.getOverlapRemovalPadding() > 0)) {
      System.out.println("Shrink after overlap:");
      String[] minMaxExpand = OCRInterface.this_interface.getMinMaxExpand().split(",");
      int padding = OCRInterface.this_interface.getPolygonPadding();
      
      OCRInterface.this_interface.setMinMaxExpand("0,0");
      OCRInterface.this_interface.setPolygonPadding(OCRInterface.this_interface.getOverlapRemovalPadding());
      OCRInterface.this_interface.shrinkZones(toShrink, false, false);
      OCRInterface.this_interface.setMinMaxExpand(minMaxExpand[0] + "," + minMaxExpand[1]);
      OCRInterface.this_interface.setPolygonPadding(padding);
    }
  }
  
  private static void toPolygon(Polygon mask_tmp, PathIterator p_path) {
    double[] point = new double[2];
    if (p_path.currentSegment(point) != 4) {
      mask_tmp.addPoint((int)point[0], (int)point[1]);
    }
  }
  





  private void setNewPolygon(Vector<DLZone> zoneVec, PolygonZone polygon)
  {
    String id = zoneID;
    DLZone incoming = null;
    DLZone outcoming = null;
    String content = "";
    for (DLZone zone : zoneVec)
    {
      polygon.setAttributeValue("gedi_type", zone
        .getAttributeValue("gedi_type"));
      polygon.setAttributeValue("id", id);
      if (OCRInterface.this_interface.getPreserveAttributesOnMerge()) {
        polygon.getZoneTags().putAll(zone.getZoneTags());
        if (polygon.getAttributeValue("orientationD") != null)
          polygon.setAttributeValue("orientationD", "");
        if (polygon.getAttributeValue("orientation") != null)
          polygon.setAttributeValue("orientation", "");
      }
      zoneID = id;
      

      String delimiter = OCRInterface.this_interface.getInsertSpaceOnMerge() ? " " : "";
      
      if (zone.hasContents()) {
        content = content + delimiter + zone.getContents();
      }
      
      if ((previousZone != null) && (!zoneVec.contains(previousZone))) {
        incoming = previousZone;
      }
      if ((nextZone != null) && (!zoneVec.contains(nextZone))) {
        outcoming = (Zone)nextZone;
      }
      currentHWObjcurr_canvas.getShapeVec().remove((Zone)zone);
    }
    

    if (incoming != null)
      nextZone = polygon;
    if (outcoming != null)
      previousZone = polygon;
    previousZone = incoming;
    nextZone = outcoming;
    
    polygon.setContents(content);
    
    currentHWObjcurr_canvas.getShapeVec().add(polygon);
    
    if (OCRInterface.currDoc == null) {
      ImageDisplay.nulldoc.add(polygon);
    }
    
    ImageDisplay.activeZones.clear();
    ImageDisplay.activeZones.add(polygon);
    this_interfacetbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
  }
  
  private void merge(Zone zone, Raster image, Rectangle union) {
    if (visited_global_single == null) {
      visited_global_single = new boolean[width][height];
    }
    selectionZone = zone;
    bounds = zone.get_Bounds();
    
    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(bounds.x + x, bounds.y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          visit(union, x, y, image);
        }
      }
    }
  }
  
  private void createImageOfPolygon(PolygonZone zone) {
    Rectangle bounds = zone.get_Bounds();
    selectionZone = zone;
    visited_global_single = null;
    visited_global_single = new boolean[width][height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Point p = new Point(x + x, y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          fill(visited_global_single, zone.get_Bounds(), x, y);
        }
      }
    }
  }
  








  private PolygonZone createNewPolygon(Raster image, Rectangle union)
  {
    System.out.println("createNewPolygon");
    ArrayList<VPoint> vpoints = new ArrayList();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (visited_global_single[x][y] != 0) {
          vpoints.add(new VPoint(x + x, y + y));
        }
      }
    }
    
    ArrayList<Point> plist = getNonConvexHull(vpoints);
    
    Vector<Point> newVerteces = new Vector(plist.size());
    newVerteces.addAll(plist);
    
    removePointsOnTheSameLine(newVerteces);
    
    PolygonZone polygon = new PolygonZone(newVerteces);
    
    createImageOfPolygon(polygon);
    
    vpoints.clear();
    plist.clear();
    
    Rectangle bounds = polygon.get_Bounds();
    int padding = OCRInterface.this_interface.getPolygonPadding();
    int w = width + padding * 2;
    int h = height + padding * 2;
    boolean[][] expanded_array = addPadding(visited_global_single, 
      width, height, padding);
    
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (expanded_array[x][y] != 0) {
          vpoints.add(new VPoint(x + x - padding, y + y - padding));
        }
      }
    }
    


    plist = getNonConvexHull(vpoints);
    
    newVerteces = new Vector(plist.size());
    newVerteces.addAll(plist);
    
    removePointsOnTheSameLine(newVerteces);
    
    polygon.setPointsVec(newVerteces);
    
    return polygon;
  }
  
















  private void shrinkPolygon_non_convex_hull(PolygonZone zone, Raster image)
  {
    bounds = zone.get_Bounds();
    
    selectionZone = zone;
    visited_global_single = null;
    visited_global_single = new boolean[bounds.width][bounds.height];
    
    String rleImage = (String)zone.getZoneTags().get("RLEIMAGE");
    
    if ((rleImage != null) && (!rleImage.trim().isEmpty()) && (!rleImage.trim().equals("0"))) {
      bounds = getBoundsAroundRLE(rleImage);
      visited_global_single = null;
      visited_global_single = new boolean[bounds.width][bounds.height];
      visit_byRLE(zone, rleImage);
    }
    else {
      for (int y = 0; y < bounds.height; y++) {
        for (int x = 0; x < bounds.width; x++) {
          Point p = new Point(bounds.x + x, bounds.y + y);
          if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
            visit(zone.get_Bounds(), x, y, image);
          }
        }
      }
    }
    
    ArrayList<VPoint> vpoints = new ArrayList();
    




    int padding = OCRInterface.this_interface.getPolygonPadding();
    
    int w = bounds.width + padding * 2;
    int h = bounds.height + padding * 2;
    
    if (padding != 0) {
      boolean[][] padding_array_intermediate = new boolean[w][h];
      for (int y = 0; y < bounds.height; y++) {
        for (int x = 0; x < bounds.width; x++) {
          if (visited_global_single[x][y] != 0) {
            padding_array_intermediate[(x + padding)][(y + padding)] = 1;
          }
        }
      }
      visited_global_single = null;
      
      boolean[][] padding_array_final = new boolean[w][h];
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          if (padding_array_intermediate[x][y] != 0) {
            padding_array_final[x][y] = 1;
            for (int j = y - padding; j <= y + padding; j++) {
              for (int i = x - padding; i <= x + padding; i++) {
                padding_array_final[i][j] = 1;
              }
            }
          }
        }
      }
      
      padding_array_intermediate = (boolean[][])null;
      
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          if (padding_array_final[x][y] != 0) {
            vpoints.add(new VPoint(x + bounds.x - padding, y + bounds.y - padding));
          }
        }
      }
    } else {
      for (int y = 0; y < bounds.height; y++) {
        for (int x = 0; x < bounds.width; x++) {
          if (visited_global_single[x][y] != 0) {
            vpoints.add(new VPoint(x + bounds.x, y + bounds.y));
          }
        }
      }
    }
    ArrayList<Point> plist = getNonConvexHull(vpoints);
    
    if (plist != null) {
      Vector<Point> newVerteces = new Vector(plist.size());
      newVerteces.addAll(plist);
      
      removePointsOnTheSameLine(newVerteces);
      
      ((PolygonZone)selectionZone).setPointsVec(newVerteces);
      
      OCRInterface.this_interface.setBlankFoundOnShrink(true);
    }
    else {
      System.out.println("shrinkPolygon_non_convex_hull. Blank zone id: " + zoneID);
    }
    selectionZone.selectedCorner = -1;
    rotateZone(selectionZone);
  }
  









  private void shrink_pixelwise(DLZone zone, Raster image)
  {
    selectionZone = ((Zone)zone);
    
    bounds = selectionZone.get_Bounds();
    
    minX = (this.minY = this.maxX = this.maxY = -1);
    visited_global_single = null;
    visited_global_single = new boolean[bounds.width][bounds.height];
    
    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(bounds.x + x, bounds.y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          visit(zone.get_Bounds(), x, y, image);
        }
      }
    }
    if ((minX == -1) && (minY == -1) && 
      (maxX == -1) && (maxY == -1)) {
      System.out.println("shrink_pixelwise. Blank zone id: " + zoneID);
      OCRInterface.this_interface.setBlankFoundOnShrink(true);
    }
    if ((zone instanceof PolygonZone)) {
      shrinkPolygon_pixelwise_help((PolygonZone)zone);
    } else if ((zone instanceof Zone))
      updateZoneParameters(selectionZone);
  }
  
  private void shrinkPolygon_pixelwise_help(PolygonZone zone) {
    DLZone rectangle = getBoundingBoxAroundContent();
    Rectangle box = rectangle.get_Bounds();
    int padding = OCRInterface.this_interface.getPolygonPadding();
    box.grow(padding, padding);
    DLZone polygon = zone;
    LinkedList<Point> verteces = ImageAnalyzer.clipPolygonRectangle(
      ((PolygonZone)polygon).derectifyPoints(), box);
    
    if (!verteces.isEmpty()) {
      Vector<Point> newVerteces = new Vector(verteces.size());
      newVerteces.addAll(verteces);
      zone.setPointsVec(newVerteces);
    }
    
    rotateZone(zone);
  }
  

  private void expandAndShrink(DLZone zone, Raster image, boolean clean)
  {
    System.out.println("expandAndShrink");
    bounds = zone.get_Bounds();
    selectionZone = ((Zone)zone);
    visited_global_single = null;
    visited_global_single = new boolean[bounds.width][bounds.height];
    String[] minMaxExpand = OCRInterface.this_interface.getMinMaxExpand().split(",");
    
    int expand = Integer.parseInt(minMaxExpand[0]);
    int foregroundExpand = Integer.parseInt(minMaxExpand[1]);
    int padding = OCRInterface.this_interface.getPolygonPadding();
    
    System.out.println("expand/foregroundExpand/padding: " + expand + "/" + foregroundExpand + "/" + padding);
    int wmax = bounds.width + expand * 2 + foregroundExpand * 2;
    int hmax = bounds.height + expand * 2 + foregroundExpand * 2;
    

    visited_global_single_max = new boolean[wmax][hmax];
    





    expandPolygonAndForegroundExpand(zone, image, expand, foregroundExpand, padding, clean);
  }
  




  private void expandPolygonAndForegroundExpand(DLZone zone, Raster image, int expand, int foregroundExpand, int padding, boolean clean)
  {
    int wmax = bounds.width + expand * 2 + foregroundExpand * 2;
    int hmax = bounds.height + expand * 2 + foregroundExpand * 2;
    
    int w = bounds.width + expand * 2;
    int h = bounds.height + expand * 2;
    
    visited_global_single_max = new boolean[w][h];
    
    ArrayList<VPoint> vpoints = new ArrayList();
    

    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(bounds.x + x, bounds.y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          fill(visited_global_single, zone.get_Bounds(), x, y);
        }
      }
    }
    














    boolean[][] expanded_array = new boolean[w][h];
    


    expanded_array = addPadding(visited_global_single, bounds.width, bounds.height, expand);
    












    Rectangle newbounds = new Rectangle(bounds.x - expand, bounds.y - expand, w, h);
    
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        Point p = new Point(x + bounds.x - expand, y + bounds.y - expand);
        
        if ((expanded_array[x][y] != 0) && (isBlackPixel(p, image))) {
          visited_global_single_max[(x - x)][(y - y)] = 1;
        } else {
          visited_global_single_max[(x - x)][(y - y)] = 0;
        }
      }
    }
    












    boolean[][] temp_array = new boolean[w][h];
    
    temp_array = visited_global_single_max;
    
    visited_global_single_max = null;
    visited_global_single_max = new boolean[wmax][hmax];
    
    minX = -1;
    maxX = -1;
    minY = -1;
    maxY = -1;
    












    newbounds = new Rectangle(bounds.x - expand - foregroundExpand, bounds.y - expand - foregroundExpand, wmax, hmax);
    
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        Point p = new Point(x + bounds.x - expand, y + bounds.y - expand);
        
        if (temp_array[x][y] != 0) {
          visit(p, newbounds, image, false);
        }
      }
    }
    














    if ((expand == 0) && (foregroundExpand == 0)) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          Point p = new Point(x + x, y + y);
          selectionZone = ((Zone)zone);
          if (isInsideOfZone(p)) {
            visited_global_single_max[(x - x)][(y - y)] = 1;
          } else {
            visited_global_single_max[(x - x)][(y - y)] = 0;
          }
        }
      }
    }
    boolean[][] padded_array = (boolean[][])null;
    
    if (padding != 0) {
      padded_array = addPadding(visited_global_single_max, width, height, padding);
    } else {
      padded_array = visited_global_single_max;
    }
    










    for (int y = 0; y < height + padding * 2; y++) {
      for (int x = 0; x < width + padding * 2; x++) {
        if (padded_array[x][y] != 0) {
          Point p = new Point(x + x - padding, y + y - padding);
          vpoints.add(new VPoint(x, y));
        }
      }
    }
    


    createPolygon(vpoints, zone, clean);
  }
  

  private void expandPolygon(DLZone zone, Raster image, int expand, int foregroundExpand, int padding, boolean clean)
  {
    ArrayList<VPoint> vpoints = new ArrayList();
    

    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(bounds.x + x, bounds.y + y);
        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          fill(visited_global_single, zone.get_Bounds(), x, y);
        }
      }
    }
    











    if (foregroundExpand == 0) {
      int w = bounds.width + expand * 2;
      int h = bounds.height + expand * 2;
      
      System.out.println("w/h:" + w + "/" + h);
      
      boolean[][] expanded_array = new boolean[w][h];
      
      boolean[][] padded_array = new boolean[w + padding * 2][h + padding * 2];
      

      expanded_array = addPadding(visited_global_single, bounds.width, bounds.height, expand);
      











      visited_global_single_max = new boolean[w][h];
      Rectangle newbounds = new Rectangle(bounds.x - expand, bounds.y - expand, w, h);
      
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          Point p = new Point(x + bounds.x - expand, y + bounds.y - expand);
          if (expanded_array[x][y] != 0) {
            visit(p, newbounds, image, false);
          }
        }
      }
      













      if (padding != 0) {
        padded_array = addPadding(visited_global_single_max, w, h, padding);
      } else {
        padded_array = visited_global_single_max;
      }
      












      for (int y = 0; y < height + padding * 2; y++) {
        for (int x = 0; x < width + padding * 2; x++) {
          if (padded_array[x][y] != 0) {
            Point p = new Point(x + x - padding, y + y - padding);
            vpoints.add(new VPoint(x, y));
          }
        }
      }
      



      createPolygon(vpoints, zone, clean);
    }
  }
  




  private void foregroundExpand(DLZone zone, Raster image, int foregroundExpand, int padding, boolean clean)
  {
    System.out.println("foregroundExpand");
    
    ArrayList<VPoint> vpoints = new ArrayList();
    






















    int w = bounds.width + foregroundExpand * 2;
    int h = bounds.height + foregroundExpand * 2;
    System.out.println("w/h:" + w + "/" + h);
    
    boolean[][] padded_array = new boolean[w + padding * 2][h + padding * 2];
    
















    visited_global_single_max = new boolean[w][h];
    Rectangle newbounds = new Rectangle(bounds.x - foregroundExpand, bounds.y - foregroundExpand, w, h);
    for (int y = 0; y < bounds.height; y++) {
      for (int x = 0; x < bounds.width; x++) {
        Point p = new Point(x + bounds.x, y + bounds.y);
        

        if ((isInsideOfImageBoundaries(p)) && (isInsideOfZone(p))) {
          visit(p, newbounds, image, true);
        }
      }
    }
    












    boolean[][] temp_array = new boolean[w][h];
    
    temp_array = visited_global_single_max;
    
    visited_global_single_max = null;
    visited_global_single_max = new boolean[w][h];
    
    minX = -1;
    maxX = -1;
    minY = -1;
    maxY = -1;
    













    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        Point p = new Point(x + bounds.x - foregroundExpand, y + bounds.y - foregroundExpand);
        
        if (temp_array[x][y] != 0) {
          if ((x == 1147) && (y == 1164))
            System.out.println("p: " + p);
          visit(p, newbounds, image, false);
        }
      }
    }
    











    if (padding != 0) {
      padded_array = addPadding(visited_global_single_max, w, h, padding);
    } else {
      padded_array = visited_global_single_max;
    }
    










    for (int y = 0; y < height + padding * 2; y++) {
      for (int x = 0; x < width + padding * 2; x++) {
        if (padded_array[x][y] != 0) {
          Point p = new Point(x + x - padding, y + y - padding);
          vpoints.add(new VPoint(x, y));
        }
      }
    }
    


    createPolygon(vpoints, zone, clean);
  }
  



  private void createPolygon(ArrayList<VPoint> vpoints, DLZone zone, boolean clean)
  {
    if ((!(zone instanceof PolygonZone)) && ((zone instanceof Zone))) {
      updateZoneParameters((Zone)zone);
      return;
    }
    
    ArrayList<Point> plist = getNonConvexHull(vpoints);
    
    if (plist != null) {
      Vector<Point> newVerteces = new Vector(plist.size());
      newVerteces.addAll(plist);
      
      removePointsOnTheSameLine(newVerteces);
      
      ((PolygonZone)selectionZone).setPointsVec(newVerteces);
      
      OCRInterface.this_interface.setBlankFoundOnShrink(true);
    }
    else {
      System.out.println("expandAndShrink. Blank zone id: " + zoneID);
    }
    selectionZone.selectedCorner = -1;
    
    if (clean) {
      shrinkPolygon_pixelwise_help((PolygonZone)selectionZone);
    }
    rotateZone(selectionZone);
  }
  
  private boolean[][] addPadding(boolean[][] array, int oldWidth, int oldHeight, int padding) {
    int w = oldWidth + padding * 2;
    int h = oldHeight + padding * 2;
    boolean[][] padding_array_final = new boolean[w][h];
    boolean[][] padding_array_intermediate = new boolean[w][h];
    
    for (int y = 0; y < oldHeight; y++) {
      for (int x = 0; x < oldWidth; x++) {
        if (array[x][y] != 0) {
          padding_array_intermediate[(x + padding)][(y + padding)] = 1;
        }
      }
    }
    
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (padding_array_intermediate[x][y] != 0) {
          padding_array_final[x][y] = 1;
          for (int j = y - padding; j <= y + padding; j++) {
            for (int i = x - padding; i <= x + padding; i++) {
              padding_array_final[i][j] = 1;
            }
          }
        }
      }
    }
    
    padding_array_intermediate = (boolean[][])null;
    
    return padding_array_final;
  }
  

  private void getImageParemeters(Raster image)
  {
    imageMinX = image.getMinX();
    imageMinY = image.getMinY();
    imageWidth = image.getWidth();
    imageHeight = image.getHeight();
  }
  
  private PlanarImage rotateImage(PlanarImage image)
  {
    int degrees = OCRInterface.this_interface.getCurrentRotateDegrees();
    
    if (degrees == 0) {
      return image;
    }
    TransposeType transposeType = null;
    
    switch (degrees) {
    case 90:  transposeType = TransposeDescriptor.ROTATE_270; break;
    case 180:  transposeType = TransposeDescriptor.ROTATE_180; break;
    case 270:  transposeType = TransposeDescriptor.ROTATE_90;
    }
    
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(transposeType);
    return PlanarImage.wrapRenderedImage(JAI.create("transpose", pb).getAsBufferedImage());
  }
  
  public void rotateSelectionZone(Zone zone) {
    int degrees = OCRInterface.this_interface.getCurrentRotateDegrees();
    if (degrees == 0) {
      return;
    }
    DLPage zonePage = zone.dlGetZonePage();
    zone.dlSetPagePointer(null);
    switch (degrees) {
    case 90: 
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getHeight());
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getWidth());
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getHeight());
      break;
    case 180: 
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getHeight());
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getWidth());
      break;
    case 270: 
      zone.rotate(OCRInterface.currentHWObj.getOriginalImage().getHeight());
    }
    zone.dlSetPagePointer(zonePage);
  }
  
  private Point rotatePoint(int x, int y, int width) {
    int temp = y;
    y = x;
    x = width - temp;
    
    return new Point(x, y);
  }
  
  public Point rotateSelectionPoint(int x, int y) {
    int degrees = OCRInterface.this_interface.getCurrentRotateDegrees();
    if (degrees == 0) {
      return new Point(x, y);
    }
    Point p = new Point(x, y);
    
    switch (degrees) {
    case 90: 
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getHeight());
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getWidth());
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getHeight());
      break;
    case 180: 
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getHeight());
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getWidth());
      break;
    case 270: 
      p = rotatePoint(x, y, OCRInterface.currentHWObj.getOriginalImage().getHeight());
    }
    
    return p;
  }
  
  private void getCC(int x, int y, Raster image, Shape bounds) {
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
    if ((visited_global_multiple != null) && (visited_global_multiple[(x - xDelta)][(y - yDelta)] != 0))
    {
      return;
    }
    if ((visited_global_single != null) && (visited_global_single[(x - xDelta)][(y - yDelta)] != 0)) {
      return;
    }
    Queue<Point> queue = new LinkedList();
    ArrayList<Point> noise = null;
    
    if ((createCC) && (CC_label == 0)) {
      CC_label = ((short)(CC_label + 1));
    } else {
      noise = new ArrayList(filter);
      CC_label = 1;
    }
    
    int numOfPixels = 0;
    
    if (createCC) {
      minX = -1;
      maxX = -1;
      minY = -1;
      maxY = -1;
    }
    
    Point p0 = new Point(x, y);
    queue.add(p0);
    
    if (visited_global_multiple != null) {
      visited_global_multiple[(x - xDelta)][(y - yDelta)] = CC_label;
    }
    else if (visited_global_single != null) {
      visited_global_single[(x - xDelta)][(y - yDelta)] = 1;
    }
    if (minX == -1) minX = x;
    if (maxX == -1) maxX = x;
    if (minY == -1) minY = y;
    if (maxY == -1) { maxY = y;
    }
    







    while (!queue.isEmpty()) {
      Point p = (Point)queue.poll();
      numOfPixels++;
      if ((numOfPixels <= filter) && 
        (noise != null)) {
        noise.add(p);
      }
      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) { maxY = y;
      }
      

      Point neighborP = new Point(x, y - 1);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x + 1, y - 1);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x + 1, y);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x + 1, y + 1);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x, y + 1);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x - 1, y + 1);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x - 1, y);
      examine(neighborP, image, queue, bounds);
      

      neighborP = new Point(x - 1, y - 1);
      examine(neighborP, image, queue, bounds);
    }
    

    if (numOfPixels <= filter) {
      resetNoisePixels(noise, bounds);
      noise = null;
      return;
    }
    
    if (!createCC) {
      return;
    }
    System.out.println("createCC");
    createZone();
    queue = null;
  }
  
  private void resetNoisePixels(ArrayList<Point> checkedPixels, Shape bounds) {
    if (checkedPixels == null) {
      return;
    }
    int xDelta = 0;
    int yDelta = 0;
    
    if (bounds != null) {
      xDelta = getBoundsx;
      yDelta = getBoundsy;
    }
    

    if (visited_global_multiple != null) {
      for (Point p : checkedPixels) {
        visited_global_multiple[(x - xDelta)][(y - yDelta)] = 0;
      }
    }
    if (visited_global_single != null) {
      for (Point p : checkedPixels) {
        visited_global_single[(x - xDelta)][(y - yDelta)] = 0;
      }
    }
  }
  
  private void resetNoisePixels(boolean[][] array, ArrayList<Point> checkedPixels, Shape bounds) {
    if (checkedPixels == null) {
      return;
    }
    if (array != null) {
      for (Point p : checkedPixels)
        array[(x - getBoundsx)][(y - getBoundsy)] = 0;
    }
  }
  
  private void createZone() {
    updateExtremes();
    
    if (createRLE) {
      if (visited_global_multiple != null) {
        RLEIMAGE = getRunLength(visited_global_multiple, CC_label);
        CC_label = ((short)(CC_label + 1));
      }
      else if (visited_global_single != null) {
        RLEIMAGE = getRunLength(visited_global_single);
      }
    }
    addZone();
  }
  
  private void examine_max(Point pixel, Raster image, Queue<Point> queue, Shape newbounds)
  {
    if (!isInsideOfImageBoundaries(pixel)) {
      return;
    }
    if (!newbounds.contains(pixel)) {
      return;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    

    if (pixelValue != blackPixel) {
      visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] = 0;
      return;
    }
    


    try
    {
      if (visited_global_single_max != null)
      {
        if (visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] == 0) {
          visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] = 1;
          queue.add(pixel);
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }
  
  private void examine(Rectangle bounds, Point pixel, Raster image, Queue<Point> queue) {
    if (!isInsideOfImageBoundaries(pixel)) {
      return;
    }
    if (!isInsideOfZone(pixel)) {
      return;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      return;
    }
    if ((visited_global_single != null) && 
      (visited_global_single[(x - x)][(y - y)] == 0)) {
      visited_global_single[(x - x)][(y - y)] = 1;
      queue.add(pixel);
    }
  }
  
  private void examine(Point pixel, Raster image, Queue<Point> queue, Shape bounds)
  {
    int xDelta = 0;
    int yDelta = 0;
    
    if (bounds != null) {
      xDelta = getBoundsx;
      yDelta = getBoundsy;
      
      if (!bounds.contains(pixel)) {
        return;
      }
    }
    if (!isInsideOfImageBoundaries(pixel)) {
      return;
    }
    if ((bounds == null) && (!createCC) && (!isInsideOfZone(pixel))) {
      return;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      return;
    }
    if (visited_global_single != null) {
      if (visited_global_single[(x - xDelta)][(y - yDelta)] == 0) {
        visited_global_single[(x - xDelta)][(y - yDelta)] = 1;
        queue.add(pixel);
      }
    }
    else if ((visited_global_multiple != null) && 
      (visited_global_multiple[(x - xDelta)][(y - yDelta)] == 0)) {
      visited_global_multiple[(x - xDelta)][(y - yDelta)] = CC_label;
      queue.add(pixel);
    }
  }
  
  private void addZone()
  {
    if (RLEIMAGE == null) {
      setRLEIMAGE("");
    }
    if (!createCC) {
      currentHWObjcurr_canvas.clearRLEMap();
      selectionZone.getZoneTags().put("RLEIMAGE", getRLEIMAGE());
      rotateZone(selectionZone);
      currentHWObjcurr_canvas.paintCanvas();
      return;
    }
    

    Zone zone = new Zone(true);
    isIncomplete = true;
    selectedCorner = -1;
    setZoneParameters(zone);
    




    if ((zone.dlGetZoneWidth() == 0) || (zone.dlGetZoneHeight() == 0)) {
      zone = null;
      return;
    }
    
    String newZonesLabel = ocrIF.tbdPane.getSelectedAssignID().trim();
    
    if ((newZonesLabel == null) || (newZonesLabel.isEmpty())) {
      newZonesLabel = (String)selectionZone.getZoneTags().get("gedi_type");
    }
    zone.setZoneType(newZonesLabel);
    

    Iterator localIterator1 = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator();
    Iterator localIterator2;
    for (; localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      Map.Entry<String, Map<String, TypeAttributeEntry>> f = (Map.Entry)localIterator1.next();
      

      localIterator2 = ((Map)f.getValue()).entrySet().iterator(); continue;Map.Entry<String, TypeAttributeEntry> f2 = (Map.Entry)localIterator2.next();
      if (((String)f.getKey()).equals(zone.getZoneType())) {
        zone.setAttributeValue((String)f2.getKey(), ((TypeAttributeEntry)f2.getValue()).getDefaultValue());
      }
    }
    

    zone.getZoneTags().put("RLEIMAGE", getRLEIMAGE());
    
    rotateZone(zone);
    
    currentHWObjcurr_canvas.insertZone(zone, null);
    
    if (OCRInterface.currDoc == null) {
      ImageDisplay.nulldoc.add(zone);
    }
    
    setRLEIMAGE("");
  }
  




  public void rotateZone(Zone zone)
  {
    int degrees = OCRInterface.this_interface.getCurrentRotateDegrees();
    if (degrees == 0) {
      return;
    }
    zone.dlSetPagePointer(null);
    switch (degrees) {
    case 90: 
      zone.rotate(imageHeight);
      break;
    case 180: 
      zone.rotate(imageHeight);
      zone.rotate(imageWidth);
      break;
    case 270: 
      zone.rotate(imageHeight);
      zone.rotate(imageWidth);
      zone.rotate(imageHeight);
    }
    
    zone.dlSetPagePointer(this_interfacetbdPane.data_panel.a_window.getPage());
  }
  












  private String getRunLength(boolean[][] visited)
  {
    numOfSegments = 0;
    
    if (visited == null) {
      return Integer.toString(numOfSegments);
    }
    StringBuffer runLengthStr = new StringBuffer();
    int runStart = -1;
    for (int y = minY; y < maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        if (x == maxX) {
          if ((visited[(maxX - 1)][y] != 0) && (runStart != -1)) {
            StringBuffer nextRL = getRL(y, runStart, x);
            if (nextRL != null) {
              runLengthStr.append(nextRL);
              numOfSegments += 1;
            }
            runStart = -1;
          }
          

        }
        else if ((visited[x][y] != 0) && (runStart == -1)) {
          runStart = x;
        } else if ((visited[x][y] == 0) && (runStart != -1)) {
          StringBuffer nextRL = getRL(y, runStart, x);
          if (nextRL != null) {
            runLengthStr.append(nextRL);
            numOfSegments += 1;
          }
          runStart = -1;
        }
      }
    }
    


    return "(" + bounds.x + "," + bounds.y + ")," + numOfSegments + ";" + runLengthStr.toString();
  }
  












  private String getRunLength(short[][] visited, short CC_label)
  {
    numOfSegments = 0;
    
    if ((visited == null) || (CC_label == 0)) {
      return Integer.toString(numOfSegments);
    }
    StringBuffer runLengthStr = new StringBuffer();
    int runStart = -1;
    
    for (int y = minY; y < maxY; y++) {
      for (int x = minX; x <= maxX; x++) {
        if (x == maxX) {
          if ((visited[(maxX - 1)][y] == CC_label) && (runStart != -1)) {
            StringBuffer nextRL = getRL(y, runStart, x);
            if (nextRL != null) {
              runLengthStr.append(nextRL);
              numOfSegments += 1;
            }
            runStart = -1;
          }
          

        }
        else if ((visited[x][y] == CC_label) && (runStart == -1)) {
          runStart = x;
        } else if ((visited[x][y] != CC_label) && (runStart != -1)) {
          StringBuffer nextRL = getRL(y, runStart, x);
          if (nextRL != null) {
            runLengthStr.append(nextRL);
            numOfSegments += 1;
          }
          runStart = -1;
        }
      }
    }
    


    return "(" + bounds.x + "," + bounds.y + ")," + numOfSegments + ";" + runLengthStr.toString();
  }
  
  private StringBuffer getRL(int y, int runStart, int x) {
    int deltaX;
    int deltaY;
    int deltaX;
    if (!createCC)
    {

      int deltaY = bounds.y;
      deltaX = bounds.x;
    }
    else {
      deltaY = minY;
      deltaX = minX;
    }
    
    int row = y - deltaY;
    int startCol = runStart - deltaX;
    int finishCol = x - deltaX;
    
    if (row < 0) {
      return null;
    }
    if (startCol < 0) {
      startCol = 0;
    }
    if (finishCol < 0) {
      finishCol = 0;
    }
    if ((startCol == 0) && (finishCol == 0)) {
      return null;
    }
    return new StringBuffer("(" + row + "," + startCol + "," + finishCol + ");");
  }
  
  private boolean isInsideOfImageBoundaries(Point p) {
    if ((x < imageMinX) || 
      (x >= imageMinX + imageWidth) || 
      (y < imageMinY) || 
      (y >= imageMinY + imageHeight)) {
      return false;
    }
    return true;
  }
  
  private boolean isInsideOfZone(Point p)
  {
    if ((selectionZone instanceof PolygonZone)) {
      Point2D pCheck = new Point2D.Float((float)(p.getX() + 0.5D), 
        (float)(p.getY() + 0.5D));
      return ((PolygonZone)selectionZone).doesContain(pCheck);
    }
    if ((selectionZone instanceof OrientedBox)) {
      Point2D pCheck = new Point2D.Float((float)(p.getX() + 0.5D), 
        (float)(p.getY() + 0.5D));
      return ((OrientedBox)selectionZone).doesContain(pCheck);
    }
    
    return selectionZone.doesContain(x, y, 1.0F);
  }
  
  private void updateExtremes() {
    maxX += 1;maxY += 1;
  }
  
  private void setZoneParameters(Zone zone) {
    int lt_x = minX;
    int rb_x = maxX;
    int lt_y = minY;
    int rb_y = maxY;
    
    int width = rb_x - lt_x;
    int height = rb_y - lt_y;
    


    lt = new Point(lt_x, lt_y);
    rb = new Point(rb_x, rb_y);
    try
    {
      zone.dlSetZoneWidth(width);
      zone.dlSetZoneHeight(height);
      zone.dlSetZoneOrigin(lt_x, lt_y);
    } catch (DLException e) {
      e.printStackTrace();
    }
  }
  
  public String getRLEIMAGE()
  {
    return RLEIMAGE;
  }
  
  public void setRLEIMAGE(String rleImage) { RLEIMAGE = rleImage; }
  
  private void setWaitCursor()
  {
    ocrIF.ocrTable.setCursor(Cursor.getPredefinedCursor(3));
    ocrIF.setCursor(Cursor.getPredefinedCursor(3));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(3));
  }
  
  private void setDefaultCursor() {
    ocrIF.ocrTable.setCursor(Cursor.getPredefinedCursor(0));
    ocrIF.setCursor(Cursor.getPredefinedCursor(0));
    ImageReaderDrawer.picture.setCursor(Cursor.getPredefinedCursor(0));
  }
  
  private void visit(Rectangle bounds, int x, int y, Raster image)
  {
    int pixelValue = 0;
    
    ArrayList<Point> noise = new ArrayList(filter);
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      return;
    }
    Point pIn = new Point(x, y);
    
    if ((visited_global_single != null) && 
      (visited_global_single[(x - x)][(y - y)] != 0)) {
      return;
    }
    Queue<Point> queue = new LinkedList();
    
    int numOfPixels = 0;
    

    Point p0 = new Point(x, y);
    queue.add(p0);
    
    visited_global_single[(x - x)][(y - y)] = 1;
    

    int t_minX = -1;int t_maxX = -1;int t_minY = -1;int t_maxY = -1;
    

    t_minX = x;
    t_maxX = x;
    t_minY = y;
    t_maxY = y;
    








    while (!queue.isEmpty()) {
      Point p = (Point)queue.poll();
      
      numOfPixels++;
      
      if ((numOfPixels <= filter) && 
        (noise != null)) {
        noise.add(p);
      }
      if (x < t_minX) t_minX = x;
      if (x > t_maxX) t_maxX = x;
      if (y < t_minY) t_minY = y;
      if (y > t_maxY) { t_maxY = y;
      }
      

      Point neighborP = new Point(x, y - 1);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x + 1, y - 1);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x + 1, y);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x + 1, y + 1);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x, y + 1);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x - 1, y + 1);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x - 1, y);
      examine(bounds, neighborP, image, queue);
      

      neighborP = new Point(x - 1, y - 1);
      examine(bounds, neighborP, image, queue);
    }
    

    if (numOfPixels <= filter) {
      resetNoisePixels(noise, bounds);
      noise = null;
      return;
    }
    
    if ((t_minX < minX) || (minX == -1)) minX = t_minX;
    if ((t_maxX > maxX) || (maxX == -1)) maxX = t_maxX;
    if ((t_minY < minY) || (minY == -1)) minY = t_minY;
    if ((t_maxY > maxY) || (maxY == -1)) maxY = t_maxY;
  }
  
  private void fill(boolean[][] array, Rectangle bounds, int x, int y)
  {
    if (array != null) {
      array[(x - x)][(y - y)] = 1;
    }
  }
  
  private void visit(Point pIn, Shape newbounds, Raster image, boolean includeNoise) {
    int pixelValue = 0;
    
    ArrayList<Point> noise = new ArrayList(filter);
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    if (pixelValue != blackPixel) {
      visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] = 0;
      return;
    }
    
    if ((visited_global_single_max != null) && 
      (visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] != 0)) {
      return;
    }
    Queue<Point> queue = new LinkedList();
    
    int numOfPixels = 0;
    
    queue.add(pIn);
    
    visited_global_single_max[(x - getBoundsx)][(y - getBoundsy)] = 1;
    

    int t_minX = -1;int t_maxX = -1;int t_minY = -1;int t_maxY = -1;
    

    t_minX = x;
    t_maxX = x;
    t_minY = y;
    t_maxY = y;
    












    while (!queue.isEmpty()) {
      Point p = (Point)queue.poll();
      
      numOfPixels++;
      
      if ((numOfPixels <= filter) && 
        (noise != null)) {
        noise.add(p);
      }
      if (x < t_minX) t_minX = x;
      if (x > t_maxX) t_maxX = x;
      if (y < t_minY) t_minY = y;
      if (y > t_maxY) { t_maxY = y;
      }
      
      Point neighborP = new Point(x, y - 1);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x + 1, y - 1);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x + 1, y);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x + 1, y + 1);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x, y + 1);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x - 1, y + 1);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x - 1, y);
      examine_max(neighborP, image, queue, newbounds);
      

      neighborP = new Point(x - 1, y - 1);
      examine_max(neighborP, image, queue, newbounds);
    }
    

    if ((!includeNoise) && (numOfPixels <= filter)) {
      resetNoisePixels(visited_global_single_max, noise, newbounds);
      noise = null;
      return;
    }
    
    if ((t_minX < minX) || (minX == -1)) minX = t_minX;
    if ((t_maxX > maxX) || (maxX == -1)) maxX = t_maxX;
    if ((t_minY < minY) || (minY == -1)) minY = t_minY;
    if ((t_maxY > maxY) || (maxY == -1)) { maxY = t_maxY;
    }
  }
  










  private void visit_byRLE(DLZone zone, String RLE)
  {
    if ((RLE == null) || (RLE.trim().isEmpty()) || (RLE.trim().equals("0"))) {
      return;
    }
    String[] rows = RLE.split(";");
    
    String ref = rows[0].trim().replaceAll("\\(", "");
    ref = ref.trim().replaceAll("\\)", "");
    String[] ref_tmp = ref.split(",");
    Point referencePoint = new Point(Integer.parseInt(ref_tmp[0]), Integer.parseInt(ref_tmp[1]));
    

    rows[0] = null;
    System.out.println("bounds for RLE: " + bounds);
    for (String s : rows)
      if (s != null)
      {
        s = s.trim().replaceAll("\\(", "");
        s = s.trim().replaceAll("\\)", "");
        String[] elements = s.split(",");
        int y = Integer.parseInt(elements[0]) - (bounds.y - y);
        int x_start = Integer.parseInt(elements[1]) - (bounds.x - x);
        int x_end = Integer.parseInt(elements[2]) - (bounds.x - x);
        



        for (int i = x_start; i < x_end; i++) {
          int _i = i >= bounds.width ? bounds.width - 1 : i;
          int _y = y >= bounds.height ? bounds.height - 1 : y;
          visited_global_single[_i][_y] = 1;
        }
      }
  }
  
  private Rectangle getBoundsAroundRLE(String RLE) {
    String[] rows = RLE.split(";");
    String ref = rows[0].trim().replaceAll("\\(", "");
    ref = ref.trim().replaceAll("\\)", "");
    String[] ref_tmp = ref.split(",");
    Point referencePoint = new Point(Integer.parseInt(ref_tmp[0]), Integer.parseInt(ref_tmp[1]));
    


    rows[0] = null;
    Set<Integer> yCoords = new TreeSet();
    Set<Integer> x_startCoords = new TreeSet();
    Set<Integer> x_endCoords = new TreeSet();
    
    for (String s : rows) {
      if (s != null)
      {
        s = s.trim().replaceAll("\\(", "");
        s = s.trim().replaceAll("\\)", "");
        String[] elements = s.split(",");
        Integer y = Integer.valueOf(Integer.parseInt(elements[0]) + y);
        Integer x_start = Integer.valueOf(Integer.parseInt(elements[1]) + x);
        Integer x_end = Integer.valueOf(Integer.parseInt(elements[2]) + x);
        

        yCoords.add(y);
        x_startCoords.add(x_start);
        x_endCoords.add(x_end);
      }
    }
    
    Object[] yArray = yCoords.toArray();
    Object[] x_startArray = x_startCoords.toArray();
    Object[] x_endArray = x_endCoords.toArray();
    
    int x = ((Integer)x_startArray[0]).intValue();
    int y = ((Integer)yArray[0]).intValue();
    int w = ((Integer)x_endArray[(x_endArray.length - 1)]).intValue() - ((Integer)x_startArray[0]).intValue();
    int h = ((Integer)yArray[(yArray.length - 1)]).intValue() - ((Integer)yArray[0]).intValue();
    return new Rectangle(x, y, w, h + 1);
  }
  
  private void updateZoneParameters(Zone zone) {
    updateExtremes();
    
    if (!isValidParameters()) {
      return;
    }
    selectedCorner = -1;
    selPt = null;
    setZoneParameters(zone);
    rotateZone(zone);
  }
  
  private Zone getBoundingBoxAroundContent() {
    updateExtremes();
    
    Zone zone = new Zone(true);
    isIncomplete = true;
    selectedCorner = -1;
    setZoneParameters(zone);
    
    if ((zone.dlGetZoneWidth() == 0) || (zone.dlGetZoneHeight() == 0)) {
      zone = null;
      return zone;
    }
    
    return zone;
  }
  
  private boolean isValidParameters() {
    int lt_x = minX;
    int rb_x = maxX;
    int lt_y = minY;
    int rb_y = maxY;
    
    int width = rb_x - lt_x;
    int height = rb_y - lt_y;
    





    return (lt_x > 0) && (rb_x > 0) && (lt_y > 0) && (rb_y > 0) && (width > 0) && (height > 0);
  }
  
  private boolean isBlackPixel(Point pixel, Raster image) {
    if (!isInsideOfImageBoundaries(pixel)) {
      return false;
    }
    int pixelValue = 0;
    
    for (int i = 0; i < numOfBands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    return pixelValue == blackPixel;
  }
  




  private void removePointsOnTheSameLine(Vector<Point> points)
  {
    boolean keep_deleting = true;
    int i;
    for (; keep_deleting; 
        


        i < points.size())
    {
      keep_deleting = false;
      int p2_index = 0;
      int p3_index = 0;
      i = 0; continue;
      



      if (i + 2 == points.size()) {
        p2_index = i + 1;
        p3_index = 0;
      } else if (i + 2 > points.size()) {
        p2_index = 0;
        p3_index = 1;
      } else {
        p2_index = i + 1;
        p3_index = i + 2;
      }
      
      Point p1 = (Point)points.get(i);
      Point p2 = (Point)points.get(p2_index);
      Point p3 = (Point)points.get(p3_index);
      
      int value = (x - x) * (y - y) - (y - y) * (x - x);
      
      if (value == 0) {
        points.remove(p2_index);
        keep_deleting = true;
      }
      i++;
    }
  }
  































  private ArrayList<Point> getNonConvexHull(ArrayList<VPoint> vpoints)
  {
    final int granularity = OCRInterface.this_interface.getEdgesGranularity();
    
    TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
    
    vpoints = RepresentationFactory.convertPointsToTriangulationPoints(vpoints);
    AbstractRepresentation representation = RepresentationFactory.createTriangulationRepresentation();
    innerrepresentation = representation;
    ((TriangulationRepresentation)representation).setReduceOuterBoundariesMode();
    TriangulationRepresentation.CalcCutOff calccutoff = new TriangulationRepresentation.CalcCutOff() {
      public int calculateCutOff(TriangulationRepresentation rep) {
        return granularity;
      }
      
    };
    ((TriangulationRepresentation)representation).setCalcCutOff(calccutoff);
    
    VoronoiAlgorithm.generateVoronoi(representationwrapper, vpoints);
    
    VHalfEdge outeredge = ((TriangulationRepresentation)representation).findOuterEdge();
    
    ArrayList<Point> plist = new ArrayList();
    
    if ((outeredge == null) || (next == null)) {
      return null;
    }
    
    VHalfEdge curredge = outeredge;
    do {
      plist.add(new Point(curredge.getX(), curredge.getY()));
    } while ((next).next != null) && (curredge != outeredge));
    
    return plist;
  }
  
  private Polygon getPolygon(Zone zone)
  {
    String str = "";
    
    if (isPolygon(zone)) {
      str = zone.getAttributeValue("polygon");
    } else {
      str = getPolygonString(zone);
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
  
  private static boolean isPolygon(Zone zone) {
    return zone instanceof PolygonZone;
  }
  
  private static String getPolygonString(Zone zone) {
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
  


  public class TestRepresentationWrapper
    implements RepresentationInterface
  {
    private final ArrayList<VPoint> circleevents = new ArrayList();
    
    private RepresentationInterface innerrepresentation = null;
    


    public TestRepresentationWrapper() {}
    

    public void beginAlgorithm(Collection<VPoint> points)
    {
      circleevents.clear();
      

      if (innerrepresentation != null) {
        innerrepresentation.beginAlgorithm(points);
      }
    }
    

    public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3)
    {
      if (innerrepresentation != null) {
        innerrepresentation.siteEvent(n1, n2, n3);
      }
    }
    
    public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3, int circle_x, int circle_y) {
      circleevents.add(new VPoint(circle_x, circle_y));
      

      if (innerrepresentation != null) {
        innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
      }
    }
    

    public void endAlgorithm(Collection<VPoint> points, int lastsweeplineposition, VLinkedNode headnode)
    {
      if (innerrepresentation != null) {
        innerrepresentation.endAlgorithm(points, lastsweeplineposition, headnode);
      }
    }
  }
}
