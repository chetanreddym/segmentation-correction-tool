package ocr.tif;

import gttool.document.DLZone;
import gttool.exceptions.DLException;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import ocr.gui.Group;
import ocr.gui.OCRInterface;
import ocr.gui.PolygonZone;
import ocr.gui.Zone;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.gui.workflows.PolyTranscribeToolBar;
import ocr.manager.zones.ZonesManager;
import ocr.util.AttributeConfigUtil;
import ocr.util.BidiString;
import ocr.util.UniqueZoneId;



public class ImageAnalyzer
{
  private static LinkedList<Point> open;
  private static Raster image;
  private static Rectangle bounds;
  private static Rectangle selection2;
  private static int top;
  private static int bottom;
  private static int left;
  private static int right;
  private static int bands;
  private static int black;
  private static int filter;
  private static int index;
  private static ArrayList<Rectangle> connectedComponents;
  private static final String FILE_NOT_FOUND = "Electronic text file not found.";
  private static final String FILE_READ_ERROR = "Error reading electronic text file.";
  public static final String MULTIPLE_BEGIN_OF_STREAM = "Multiple Reading Orders Found";
  private static final String FOUND_CYCLE = "Reading Order Cycle Found";
  private static final String PREMATURE_ENDING = "Premature End of Reading Order";
  private static final String CONTENT_MISMATCH = "Content Mismatch";
  public static final String NO_READ_ORDER = "No Reading Order Found";
  private static final String EXTRA_IN_READ_ORDER = "EXTRA Zone in Stream";
  private static final String ZONE_NOT_IN_READ_ORDER = "Zone Out of Reading Order (only EXTRAs allowed)";
  public static final String ZONE_NOT_IN_READ_ORDER1 = "Zone(s) Out of Reading Order";
  private static final String EXTRA_HAS_CONTENTS = "EXTRA Zone Found with Contents";
  private static final String EMPTY_ZONE = "Zone(s) do not have contents: ";
  private static final String MULTIWORD_ZONE = "Zone contents have more than one word: ";
  private static final String INTERSECTING_POLYGONS = "Intersecting zones found: ";
  private static final String NON_POLYGON_ZONE = "Non-polygon zone(s) found: ";
  public static final String WORD_TYPE_ENDING = "_word";
  public static final String LINE_TYPE_ENDING = "_line";
  public static final String GROUP_TYPE_ENDING = "_group";
  
  public ImageAnalyzer() {}
  
  public static String cleanBidiString(String bidiString)
  {
    bidiString = bidiString.replaceAll("\\u200F", "");
    bidiString = bidiString.replaceAll("\\u200E", "");
    bidiString = bidiString.replaceAll("\\u202A", "");
    bidiString = bidiString.replaceAll("\\u202B", "");
    bidiString = bidiString.replaceAll("\\u202C", "");
    bidiString = bidiString.replaceAll("\\u202D", "");
    bidiString = bidiString.replaceAll("\\u202E", "");
    bidiString = bidiString.replaceAll("\\uFEFF", "");
    bidiString = bidiString.replaceAll("\\uFFFE", "");
    bidiString = bidiString.replaceAll("\\uEFBBBF", "");
    bidiString = bidiString.trim();
    
    return bidiString;
  }
  
  public static String readRawText(String file) {
    InputStreamReader in = null;
    String encoding = "";
    if (this_interfaceutf16Selected) {
      encoding = "UTF-16";
    } else {
      encoding = "UTF-8";
    }
    try
    {
      in = new InputStreamReader(new FileInputStream(file), encoding);
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Electronic text file not found.", "Error", 0);
      return null;
    }
    
    String contents = "";
    try
    {
      while (in.ready()) {
        char[] buffer = new char['È'];
        int temp = in.read(buffer, 0, 200);
        contents = contents + new String(buffer, 0, temp);
      }
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error reading electronic text file.", "Error", 0);
      return null;
    }
    
    return contents;
  }
  
  private static int getBlackPixelValue() {
    int blackPixelValue = 0;
    ColorModel colorModel = OCRInterface.currentHWObj.getOriginalImage().getColorModel();
    System.out.println("colorModel: " + colorModel.getClass());
    if ((colorModel instanceof IndexColorModel)) {
      int numOfPossibleValues = ((IndexColorModel)colorModel).getMapSize();
      for (int i = 0; i < numOfPossibleValues; i++) {
        int sum = colorModel.getRed(i) + 
          colorModel.getGreen(i) + 
          colorModel.getBlue(i);
        
        if (sum == 0) {
          return i;
        }
      }
    }
    return blackPixelValue;
  }
  
  private static boolean isBlackPixel(int x, int y) {
    int pixelValue = 0;
    for (int i = 0; i < bands; i++) {
      pixelValue += image.getSample(x, y, i);
    }
    
    return pixelValue == black;
  }
  







  public static Color pseudoColor(String value)
  {
    Color ret = null;
    value = value + new String(OCRInterface.this_interface.getRandomConstantForPseudoColor());
    if (value.length() < 4) {
      byte[] stringBytes = value.getBytes();
      try
      {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(stringBytes);
        stringBytes = algorithm.digest();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      ret = new Color((stringBytes[0] ^ stringBytes[3] ^ stringBytes[6]) + 128, 
        (stringBytes[1] ^ stringBytes[4] ^ stringBytes[7]) + 128, 
        (stringBytes[2] ^ stringBytes[5] ^ stringBytes[8]) + 128);
    } else {
      ret = new Color(value.hashCode());
    }
    
    float hueScale = 250.0F / (ret.getBlue() + ret.getRed() + ret.getGreen());
    
    int red = Math.min((int)(ret.getRed() * hueScale), 255);
    int green = Math.min((int)(ret.getGreen() * hueScale), 255);
    int blue = Math.min((int)(ret.getBlue() * hueScale), 255);
    
    return new Color(red, green, blue);
  }
  












  public static BufferedImage runLengthDecode_old(Zone zone, float scale)
  {
    Rectangle zoneBounds = zone.get_Bounds();
    
    if (((int)(width * scale) <= 0) || ((int)(height * scale) <= 0)) {
      return null;
    }
    String rle = zone.getAttributeValue("RLEIMAGE");
    int startIndex = rle.indexOf("(");
    
    if (startIndex != -1) {
      rle = rle.substring(startIndex);
    }
    if ((rle == null) || (rle.isEmpty()) || (rle.equals("0"))) {
      return null;
    }
    BufferedImage mask = new BufferedImage((int)(width * scale), (int)(height * scale), 2);
    Graphics2D g2d = mask.createGraphics();
    




    int orientation = OCRInterface.this_interface.getCurrentRotateDegrees();
    AffineTransform rotated = g2d.getTransform();
    if (orientation == 90) {
      rotated.translate(mask.getWidth(), 0.0D);
    } else if (orientation == 180) {
      rotated.translate((int)(width * scale), (int)(height * scale));
    } else if (orientation == 270) {
      rotated.translate(0.0D, (int)(height * scale));
    }
    rotated.quadrantRotate(orientation / 90);
    g2d.setTransform(rotated);
    
    Color zoneColor = zone.getZoneColor();
    g2d.setColor(new Color(zoneColor.getRed(), zoneColor.getGreen(), zoneColor.getBlue(), (int)(175.0F * (1.0F - this_interfaceTRANSPARENCY))));
    
    int height = (int)Math.ceil(scale);
    
    String[] rleArray = rle.split(";");
    for (int i = 0; i < rleArray.length; i++) {
      String[] Yxx = rleArray[i].split(",");
      
      int unscaledRow = Integer.parseInt(Yxx[0].substring(1));
      int row = (int)(unscaledRow * scale);
      
      int startCol = (int)(Integer.parseInt(Yxx[1]) * scale);
      int finishCol = (int)(Integer.parseInt(Yxx[2].substring(0, Yxx[2].length() - 1)) * scale);
      
      int heightModifier = (int)((unscaledRow - 1) * scale) + height - row;
      

      g2d.fillRect(startCol, row + heightModifier, finishCol - startCol, height - heightModifier);
    }
    
    return mask;
  }
  
  public static BufferedImage runLengthDecode(Zone zone, float scale) {
    Rectangle zoneBounds = zone.get_Bounds();
    
    if (((int)(width * scale) <= 0) || ((int)(height * scale) <= 0)) {
      return null;
    }
    String rle = zone.getAttributeValue("RLEIMAGE");
    

    int startIndex = rle.indexOf(";");
    

    if (startIndex != -1) {
      rle = rle.substring(startIndex + 1);
    }
    if ((rle == null) || (rle.isEmpty()) || (rle.equals("0"))) {
      return null;
    }
    

    BufferedImage mask = new BufferedImage((int)(width * scale), (int)(height * scale), 2);
    Graphics2D g2d = mask.createGraphics();
    




    int orientation = OCRInterface.this_interface.getCurrentRotateDegrees();
    AffineTransform rotated = g2d.getTransform();
    if (orientation == 90) {
      rotated.translate(mask.getWidth(), 0.0D);
    } else if (orientation == 180) {
      rotated.translate((int)(width * scale), (int)(height * scale));
    } else if (orientation == 270) {
      rotated.translate(0.0D, (int)(height * scale));
    }
    rotated.quadrantRotate(orientation / 90);
    g2d.setTransform(rotated);
    
    Color zoneColor = zone.getZoneColor();
    g2d.setColor(new Color(zoneColor.getRed(), zoneColor.getGreen(), zoneColor.getBlue(), (int)(175.0F * (1.0F - this_interfaceTRANSPARENCY))));
    
    int height = (int)Math.ceil(scale);
    
    String[] rleArray = rle.split(";");
    for (int i = 0; i < rleArray.length; i++) {
      String[] Yxx = rleArray[i].split(",");
      
      int unscaledRow = Integer.parseInt(Yxx[0].substring(1));
      int row = (int)(unscaledRow * scale);
      
      int startCol = (int)(Integer.parseInt(Yxx[1]) * scale);
      int finishCol = (int)(Integer.parseInt(Yxx[2].substring(0, Yxx[2].length() - 1)) * scale);
      
      int heightModifier = (int)((unscaledRow - 1) * scale) + height - row;
      

      g2d.fillRect(startCol, row + heightModifier, finishCol - startCol, height - heightModifier);
    }
    
    return mask;
  }
  









  public static int addLineID()
  {
    Rectangle lastBounds = null;
    int lineID = 1;
    int numMissing = 0;
    
    DLZone head = null;
    



    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if ((nextZone != null) && (previousZone == null)) {
        if (head == null) {
          head = temp;
        } else
          return 0;
      }
    }
    while ((head != null) && ((!head.hasContents()) || (head.getContents().trim().isEmpty()))) {
      head = (Zone)nextZone;
    }
    int direction = new BidiString(head.getContents(), 4).getDirection();
    
    double averageHeight = 0.0D;
    int numZones = 0;
    DLZone tempHead = head;
    while (tempHead != null) {
      while ((tempHead != null) && ((!tempHead.hasContents()) || (tempHead.getContents().trim().isEmpty()))) {
        tempHead = nextZone;
      }
      if (tempHead == null) {
        return 0;
      }
      numZones++;
      averageHeight += get_Boundsheight;
      
      tempHead = (Zone)nextZone;
    }
    
    averageHeight /= numZones;
    averageHeight *= 2.0D;
    System.out.println("averageHeight: " + averageHeight);
    boolean firstZone = false;
    






    while (head != null) {
      while ((head != null) && ((!head.hasContents()) || (head.getContents().trim().isEmpty()))) {
        head = (Zone)nextZone;
      }
      if (head == null) {
        return 0;
      }
      Rectangle bounds = head.get_Bounds();
      
      if ((lastBounds != null) && (!bounds.intersects(lastBounds)) && (
        ((direction == 0) && (x > x)) || (
        (direction == 1) && (x + width < x + width))))
      {



        firstZone = true;
        if (numMissing > 4) {
          DLZone missing = head;
          for (int i = 0; i < numMissing; i++) {
            missing = previousZone;
            missing.setAttributeValue("lineID", Integer.toString(lineID));
          }
          lineID++;
        }
        else {
          lineID++; }
        numMissing = 0;
      } else {
        firstZone = false;
      }
      
      if ((head.hasAttribute("status")) && (head.getAttributeValue("status").equals("MISSING"))) {
        numMissing++;
      } else {
        if (numMissing > 4) {
          lineID++;
          DLZone missing = head;
          for (int i = 0; i < numMissing; i++) {
            missing = previousZone;
            missing.setAttributeValue("lineID", Integer.toString(lineID));
          }
          lineID++;
        }
        numMissing = 0;
      }
      
      head.setAttributeValue("lineID", Integer.toString(lineID));
      lastBounds = bounds;
      
      head = (Zone)nextZone;
    }
    return lineID;
  }
  
  public static boolean polygonIntersects(PolygonZone poly1, PolygonZone poly2) {
    if (!poly1.get_Bounds().intersects(poly2.get_Bounds())) {
      return false;
    }
    poly1.removeDuplicates();
    poly2.removeDuplicates();
    Point prev1 = (Point)poly1.derectifyPoints().lastElement();
    for (Point cur1 : poly1.derectifyPoints()) {
      Point prev2 = (Point)poly2.derectifyPoints().lastElement();
      for (Point cur2 : poly2.derectifyPoints()) {
        if (new Line2D.Float(cur1, prev1).intersectsLine(new Line2D.Float(cur2, prev2)))
          return true;
        prev2 = cur2;
      }
      prev1 = cur1;
    }
    
    return false;
  }
  
  public static String checkPolygonIntersection() {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String intersectingPolygons = "";
    
    for (int i = 0; i < shapeVec.size(); i++) {
      DLZone temp = shapeVec.get(i);
      if ((temp instanceof PolygonZone)) {
        for (int j = i + 1; j < shapeVec.size(); j++) {
          DLZone temp2 = shapeVec.get(j);
          if (((temp2 instanceof PolygonZone)) && (polygonIntersects((PolygonZone)temp, (PolygonZone)temp2)))
            intersectingPolygons = 
              intersectingPolygons + "\n" + temp.getAttributeValue("id") + "," + temp2.getAttributeValue("id");
        }
      }
    }
    return intersectingPolygons.trim();
  }
  
  public static String checkForEmptyZones(String transcriptionType) {
    System.out.println("transcriptionType: " + transcriptionType);
    String empty = "";
    
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (!temp.isGroup())
      {

        if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
        {

          if ((temp.getContents() == null) || (temp.getContents().trim().isEmpty()))
            empty = empty + "\n" + temp.getAttributeValue("id");
        }
      }
    }
    return empty;
  }
  
  public static String checkForType(String transcriptionType) {
    String noZonesFound = "";
    
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (!temp.isGroup())
      {
        if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
          noZonesFound = noZonesFound + "\n" + temp.getAttributeValue("id");
      }
    }
    return noZonesFound;
  }
  
  public static String checkPolygonTokenization(String transcriptionType) {
    String tokenizationErrors = "";
    String tokenRegEx = 
      "((\\(\\))|(\\[\\])|(\\{\\}))|(([A-Za-z]+)|(\\p{InArabic})+)|[-!\"#$%&'()*+,./:;<=>?@\\[\\\\\\]_`{|}~]|([A-Za-z0-9_+]+@[A-Za-z0-9_]+(.[A-Za-z0-9_]+)+)|(https?:://[\\x21-\\x7E]+)|([A-Za-z]+'[A-Za-z]+)|($?([0-9\\u0660-\\u0669]+,)*[0-9\\u0660-\\u0669]+(.[0-9\\u0660-\\u0669]+)?)|([0-9\\u0660-\\u0669]+(.[0-9\\u0660-\\u0669]+)?%)|([0-9\\u0660-\\u0669]+/[0-9\\u0660-\\u0669]+)|([0-2\\u0660-\\u0662]?[0-9\\u0660-\\u0669]:[0-5\\u0660-\\u0665][0-9\\u0660-\\u0669](:[0-5\\u0660-\\u0665][0-9\\u0660-\\u0669])?((am)|(pm))?)|((([0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669])(/|\\-|\\.))?([0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669])(/|\\-|\\.)([0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]?[0-9\\u0660-\\u0669]))";
    














    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
      {
        if (temp.getContents() != null) {
          String[] tokens = cleanBidiString(temp.getContents()).replaceAll("\\s+", " ").trim().split(" ");
          String invalidTok = "";
          for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].matches(tokenRegEx))
            {
              invalidTok = invalidTok + "\n" + zoneID + PolyTranscribeToolBar.divider + tokens[i];
              System.out.print("\nZone " + zoneID + " invalid token:");
              for (int j = 0; j < tokens[i].length(); j++)
                System.out.print("-" + tokens[i].charAt(j));
              System.out.println("");
            }
          }
          if (!invalidTok.isEmpty())
          {
            tokenizationErrors = tokenizationErrors + invalidTok; }
        }
      }
    }
    return tokenizationErrors;
  }
  
  public static String checkForLineID(String transcriptionType)
  {
    String empty = "";
    
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (!temp.isGroup())
      {


        if (temp.getZoneType().equalsIgnoreCase(transcriptionType))
        {
          if ((temp.getAttributeValue("lineID") == null) || 
            (temp.getAttributeValue("lineID").trim().isEmpty())) {
            empty = empty + "\n" + temp.getAttributeValue("id");
          }
        }
      }
    }
    return empty;
  }
  
  public static String markErrorPolygons(String transcriptionType) {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    String inconsistentStatus = "";
    for (DLZone temp : shapeVec)
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType))
      {

        String currentStatus = temp.getAttributeValue("status");
        String content = cleanBidiString(temp.getContents().trim());
        if (content != null)
        {
          if ((content == null) || 
            (content.matches("^\\(.*\\)$")) || 
            (content.matches("^\\[.*\\]$")) || 
            (content.matches("^\\{.*\\}$")) || 
            (content.matches("^\\<.*\\>$")) || 
            (content.trim().equalsIgnoreCase(PolyTranscribeToolBar.UNREADABLE_REPLACE)) || 
            (content.trim().equalsIgnoreCase(PolyTranscribeToolBar.JUNK_REPLACE)))
          {
            if ((content.matches("^\\(.*\\)$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (currentStatus.equalsIgnoreCase("ERROR")))) {
              temp.setAttributeValue("status", "UNREADABLE");
            } else if ((content.matches("^\\{.*\\}$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (currentStatus.equalsIgnoreCase("ERROR")))) {
              temp.setAttributeValue("status", "JUNK");
            } else if ((content.matches("^\\[.*\\]$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (currentStatus.equalsIgnoreCase("ERROR")))) {
              temp.setAttributeValue("status", "MISSPELLED");
            } else if ((content.matches("^\\<.*\\>$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (currentStatus.equalsIgnoreCase("ERROR")))) {
              temp.setAttributeValue("status", "TYPO");
            } else if (((content.matches("^\\(.*\\)$")) || 
              (content.trim().equalsIgnoreCase(PolyTranscribeToolBar.UNREADABLE_REPLACE))) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (!currentStatus.equalsIgnoreCase("UNREADABLE")))) {
              inconsistentStatus = inconsistentStatus + "\n" + temp.getAttributeValue("id");
            } else if (((content.matches("^\\{.*\\}$")) || 
              (content.trim().equalsIgnoreCase(PolyTranscribeToolBar.JUNK_REPLACE))) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (!currentStatus.equalsIgnoreCase("JUNK")))) {
              inconsistentStatus = inconsistentStatus + "\n" + temp.getAttributeValue("id");
            } else if ((content.matches("^\\[.*\\]$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (!currentStatus.equalsIgnoreCase("MISSPELLED")))) {
              inconsistentStatus = inconsistentStatus + "\n" + temp.getAttributeValue("id");
            } else if ((content.matches("^\\<.*\\>$")) && (
              (currentStatus == null) || (currentStatus.trim().isEmpty()) || (!currentStatus.equalsIgnoreCase("TYPO"))))
              inconsistentStatus = inconsistentStatus + "\n" + temp.getAttributeValue("id"); } }
      }
    return inconsistentStatus;
  }
  
  public static void markUnreadablePolygons() {
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec)
      if ((temp.getContents() != null) && (temp.getContents().matches("^\\(.*\\)$")))
        temp.setAttributeValue("status", "UNREADABLE");
  }
  
  public static String checkForNonPolygon(String transcriptionType) {
    String nonPoly = "";
    
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if (temp.getZoneType().equalsIgnoreCase(transcriptionType))
      {
        if ((!(temp instanceof PolygonZone)) && (!temp.isGroup()))
          nonPoly = nonPoly + temp.getAttributeValue("id") + "\n"; }
    }
    return nonPoly;
  }
  
  public static String checkForMultiWord() {
    String multiWord = "";
    
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if ((temp.getContents() != null) && (temp.getContents().trim().indexOf(' ') != -1))
        multiWord = multiWord + temp.getAttributeValue("id") + "\n";
    }
    return multiWord;
  }
  








  public static String checkForReadingOrder(String transcriptionType)
  {
    boolean hasReadOrder = false;
    
    DLZone head = null;
    



    String notInReadOrder = "";
    String wrongTypeInReadOrder = "";
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (!temp.isGroup())
      {
        if (nextZone != null) {
          hasReadOrder = true;
          if (previousZone == null) {
            if (head == null) {
              head = temp;
            } else {
              return "Multiple Reading Orders Found";
            }
          }
          if (!temp.getZoneType().equalsIgnoreCase(transcriptionType)) {
            wrongTypeInReadOrder = wrongTypeInReadOrder + "\n" + temp.getAttributeValue("id");
          }
        } else if ((previousZone == null) && (temp.getZoneType().startsWith(transcriptionType))) {
          notInReadOrder = notInReadOrder + "\n" + temp.getAttributeValue("id");
        } else if ((previousZone != null) && 
          (!temp.getZoneType().startsWith(transcriptionType))) {
          wrongTypeInReadOrder = wrongTypeInReadOrder + "\n" + temp.getAttributeValue("id");
        } }
    }
    if (!hasReadOrder)
      return "No Reading Order Found";
    if ((notInReadOrder != null) && (!notInReadOrder.trim().isEmpty()))
      return notInReadOrder;
    if ((wrongTypeInReadOrder != null) && (!wrongTypeInReadOrder.trim().isEmpty())) {
      return wrongTypeInReadOrder;
    }
    return "99999";
  }
  
  public static String checkForReadingOrder()
  {
    boolean hasReadOrder = false;
    




    String notInReadOrder = "";
    String junkInReadOrder = "";
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (!temp.isGroup())
      {
        if (nextZone != null) {
          hasReadOrder = true;
        }
        if ((previousZone == null) && (nextZone == null)) {
          String status = temp.getAttributeValue("status");
          
          if ((status == null) || (!status.equalsIgnoreCase("junk")))
          {
            notInReadOrder = notInReadOrder + "\n" + temp.getAttributeValue("id");
          }
        } else if ((previousZone != null) && (nextZone != null)) {
          String status = temp.getAttributeValue("status");
          if ((status != null) && (status.equalsIgnoreCase("junk"))) {
            junkInReadOrder = junkInReadOrder + "\n" + temp.getAttributeValue("id");
          }
        }
      }
    }
    if (!hasReadOrder)
      return "No Reading Order Found";
    if ((junkInReadOrder != null) && (!junkInReadOrder.trim().isEmpty())) {
      return "JUNK IN READ ORDER" + junkInReadOrder;
    }
    if ((notInReadOrder != null) && (!notInReadOrder.trim().isEmpty())) {
      return notInReadOrder;
    }
    return "99999";
  }
  
  public static String verifyPolygonTranscription() {
    String nonPoly = "";
    String empty = "";
    String multiWord = "";
    
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if (!(temp instanceof PolygonZone)) {
        nonPoly = nonPoly + temp.getAttributeValue("id") + ", ";
      }
      if ((temp.getContents() == null) || (temp.getContents().trim().isEmpty())) {
        empty = empty + temp.getAttributeValue("id") + ", ";
      }
      if ((temp.getContents() != null) && (temp.getContents().trim().indexOf(' ') != -1)) {
        multiWord = multiWord + temp.getAttributeValue("id") + ", ";
      }
    }
    if (!nonPoly.isEmpty()) {
      return "Non-polygon zone(s) found: " + nonPoly.substring(0, nonPoly.length() - 2);
    }
    if (!empty.isEmpty()) {
      return "Zone(s) do not have contents: " + empty.substring(0, empty.length() - 2);
    }
    if (!multiWord.isEmpty()) {
      return "Zone contents have more than one word: " + multiWord.substring(0, multiWord.length() - 2);
    }
    String intersectingPolygons = "";
    
    for (int i = 0; i < shapeVec.size(); i++) {
      DLZone temp = shapeVec.get(i);
      for (int j = i + 1; j < shapeVec.size(); j++) {
        DLZone temp2 = shapeVec.get(j);
        if (polygonIntersects((PolygonZone)temp, (PolygonZone)temp2)) {
          intersectingPolygons = 
            intersectingPolygons + "(" + temp.getAttributeValue("id") + "," + temp2.getAttributeValue("id") + ") ";
        }
      }
    }
    if (!intersectingPolygons.isEmpty()) {
      return "Intersecting zones found: " + intersectingPolygons.trim();
    }
    return "99999";
  }
  



  public static String verifyReadingOrder(String txtFile)
  {
    String contents = readRawText(txtFile);
    
    boolean hasReadOrder = false;
    
    DLZone head = null;
    



    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (nextZone != null) {
        hasReadOrder = true;
        if (previousZone == null) {
          if (head == null) {
            head = temp;
          } else {
            return "Multiple Reading Orders Found";
          }
        }
      } else if (previousZone == null) {
        if ((!temp.hasAttribute("status")) || (!temp.getAttributeValue("status").equals("EXTRA"))) {
          return "zone " + temp.getAttributeValue("id") + ": " + "Zone Out of Reading Order (only EXTRAs allowed)";
        }
        if ((temp.hasAttribute("contents")) && (!temp.getAttributeValue("contents").isEmpty()))
          return "zone " + temp.getAttributeValue("id") + ": " + "EXTRA Zone Found with Contents";
      }
    }
    if (!hasReadOrder) {
      return "No Reading Order Found";
    }
    



    if (head == null) {
      return "Reading Order Cycle Found";
    }
    int segmentationType = 3;
    

    if (OCRInterface.this_interface.getAlignmentDefaultSegmenation().trim().equalsIgnoreCase("character")) {
      segmentationType = 2;
    }
    contents = cleanBidiString(contents);
    contents = contents.trim().replaceAll("\\s+", " ");
    
    String[] words = (String[])null;
    
    if (segmentationType == 2) {
      char[] charWords = contents.replaceAll(" ", "").toCharArray();
      
      words = new String[charWords.length];
      

      for (int i = 0; i < charWords.length; i++) {
        words[i] = Character.toString(charWords[i]);
      }
    } else {
      words = contents.split(" ");
    }
    





    for (int i = 0; i < words.length; i++) {
      if (head == null) {
        return "Premature End of Reading Order";
      }
      if ((head.hasAttribute("status")) && (head.getAttributeValue("status").equals("EXTRA"))) {
        return "zone " + head.getAttributeValue("id") + ": " + "EXTRA Zone in Stream";
      }
      if ((!head.hasContents()) || (!head.getContents().trim().equals(words[i])))
        return "zone " + head.getAttributeValue("id") + ": " + "Content Mismatch";
      head = (Zone)nextZone;
    }
    
    if (head != null) {
      return "zone " + head.getAttributeValue("id") + ": " + "Content Mismatch";
    }
    return "99999";
  }
  
  public static void polygonShrinkExpandAll(String transcriptionType) {
    Map<String, ArrayList<Rectangle>> ccMap = new TreeMap();
    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType + "_line"))
      {
        if ((!temp.getZoneTags().containsKey("polygon")) && (!temp.getZoneTags().containsKey("orientationD"))) {
          ArrayList<Rectangle> connectedComponents = (ArrayList)ccMap.get(temp.getZoneTags().get("bounds_of"));
          if (connectedComponents == null) {
            connectedComponents = findConnectedComponents((PolygonZone)OCRInterface.this_interface.getUniqueZoneIdObj()
              .searchZone(temp.getAttributeValue("bounds_of"), false), OCRInterface.this_interface.getConnectedComponentFilterSize());
            ccMap.put(temp.getAttributeValue("bounds_of"), connectedComponents);
          }
          
          Rectangle zoneRect = temp.get_Bounds();
          Rectangle zoneRectMargin = new Rectangle(zoneRect);
          zoneRectMargin.grow(5, 5);
          Rectangle newBounds = null;
          
          Iterator<Rectangle> itr2 = connectedComponents.iterator();
          while (itr2.hasNext()) {
            Rectangle tempRect = (Rectangle)itr2.next();
            



            if ((tempRect.intersects(zoneRect)) && (!tempRect.contains(zoneRectMargin))) {
              if (newBounds == null) {
                newBounds = tempRect;
              } else {
                newBounds = newBounds.union(tempRect);
              }
            }
          }
          if (newBounds != null)
          {

            int ltx = x;int lty = y;
            int rbx = ltx + width;int rby = lty + height;
            
            if (rbx - OCRInterface.this_interface.getMaxRefineExpand() > x + width)
              rbx = x + width;
            if (rby - OCRInterface.this_interface.getMaxRefineExpand() > y + height)
              rby = y + height;
            if (ltx + OCRInterface.this_interface.getMaxRefineExpand() < x)
              ltx = x;
            if (lty + OCRInterface.this_interface.getMaxRefineExpand() < y) {
              lty = y;
            }
            

            if (width < 5)
              width = 4;
            if (height < 5) {
              height = 4;
            }
            Zone resized = (Zone)temp;
            
            resized.set_lt_x(ltx);
            resized.set_lt_y(lty);
            resized.set_rb_x(rbx);
            resized.set_rb_y(rby);
            try
            {
              temp.dlSetZoneOrigin(ltx, lty);
              temp.dlSetZoneWidth(rbx - ltx);
              temp.dlSetZoneHeight(rby - lty);
            } catch (DLException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    }
  }
  




















  public static void newShrinkExpandAll()
  {
    getConnectedComponents();
    
    if (connectedComponents == null) {
      return;
    }
    

    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if ((!temp.getZoneTags().containsKey("polygon")) && (!temp.getZoneTags().containsKey("orientationD"))) {
        Rectangle zoneRect = temp.get_Bounds();
        Rectangle zoneRectMargin = new Rectangle(zoneRect);
        zoneRectMargin.grow(5, 5);
        Rectangle newBounds = null;
        
        Iterator<Rectangle> itr2 = connectedComponents.iterator();
        while (itr2.hasNext()) {
          Rectangle temp2 = (Rectangle)itr2.next();
          


          Rectangle tempRect = temp2.intersection(zoneRect);
          



          if ((tempRect.intersects(zoneRect)) && (!tempRect.contains(zoneRectMargin))) {
            if (newBounds == null) {
              newBounds = tempRect;
            } else {
              newBounds = newBounds.union(tempRect);
            }
          }
        }
        if (newBounds != null)
        {

          int ltx = x;int lty = y;
          int rbx = ltx + width;int rby = lty + height;
          


          if (rbx - OCRInterface.this_interface.getMaxRefineExpand() > x + width)
            rbx = x + width;
          if (rby - OCRInterface.this_interface.getMaxRefineExpand() > y + height)
            rby = y + height;
          if (ltx + OCRInterface.this_interface.getMaxRefineExpand() < x)
            ltx = x;
          if (lty + OCRInterface.this_interface.getMaxRefineExpand() < y) {
            lty = y;
          }
          

          if (width < 5)
            width = 4;
          if (height < 5) {
            height = 4;
          }
          Zone resized = (Zone)temp;
          resized.set_lt_x(ltx);
          resized.set_lt_y(lty);
          resized.set_rb_x(rbx);
          resized.set_rb_y(rby);
          try
          {
            temp.dlSetZoneOrigin(ltx, lty);
            temp.dlSetZoneWidth(rbx - ltx);
            temp.dlSetZoneHeight(rby - lty);
          } catch (DLException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }
  
  public static void newShrinkExpandAll2()
  {
    getConnectedComponents();
    
    if (connectedComponents == null) {
      return;
    }
    

    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if ((!temp.getZoneTags().containsKey("polygon")) && (!temp.getZoneTags().containsKey("orientationD"))) {
        Rectangle zoneRect = temp.get_Bounds();
        Rectangle zoneRectMargin = new Rectangle(zoneRect);
        zoneRectMargin.grow(OCRInterface.this_interface.getMaxRefineExpand(), 
          OCRInterface.this_interface.getMaxRefineExpand());
        Rectangle zoneRectNew = null;
        
        Iterator<Rectangle> itr = connectedComponents.iterator();
        
        while (itr.hasNext()) {
          Rectangle zoneRectCC = (Rectangle)itr.next();
          System.out.println("zoneRectCC: " + zoneRectCC);
          
          Rectangle ZIntersect = zoneRectCC.intersection(zoneRect);
          











          if ((zoneRectCC.intersects(zoneRectMargin)) && (zoneRectCC.intersects(zoneRect))) {
            if (zoneRectMargin.contains(zoneRectCC))
            {
              if (zoneRectNew == null) {
                zoneRectNew = zoneRectCC;
                System.out.println("zoneRectNew 1: " + zoneRectNew);
              }
              else {
                zoneRectNew = zoneRectNew.union(zoneRectCC);
                System.out.println("zoneRectNew 2: " + zoneRectNew);
              }
              

            }
            else if (zoneRectNew == null) {
              zoneRectNew = ZIntersect;
              System.out.println("zoneRectNew 3: " + zoneRectNew);
            }
            else {
              zoneRectNew = zoneRectNew.union(ZIntersect);
              System.out.println("zoneRectNew 4: " + zoneRectNew);
            }
          }
        }
        



        if (zoneRectNew != null)
        {
          System.out.println("zoneRectNew: " + zoneRectNew);
          

          int ltx = x;int lty = y;
          int rbx = ltx + width;int rby = lty + height;
          
















          if (width < 5)
            width = 4;
          if (height < 5) {
            height = 4;
          }
          Zone resized = (Zone)temp;
          resized.set_lt_x(ltx);
          resized.set_lt_y(lty);
          resized.set_rb_x(rbx);
          resized.set_rb_y(rby);
          try
          {
            temp.dlSetZoneOrigin(ltx, lty);
            temp.dlSetZoneWidth(rbx - ltx);
            temp.dlSetZoneHeight(rby - lty);
          } catch (DLException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }
  








  public static void shrinkExpandAll()
  {
    getConnectedComponents();
    
    if (connectedComponents == null) {
      return;
    }
    

    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      if ((!temp.getZoneTags().containsKey("polygon")) && (!temp.getZoneTags().containsKey("orientationD"))) {
        Rectangle zoneRect = temp.get_Bounds();
        Rectangle zoneRectMargin = new Rectangle(zoneRect);
        zoneRectMargin.grow(5, 5);
        Rectangle newBounds = null;
        
        Iterator<Rectangle> itr2 = connectedComponents.iterator();
        while (itr2.hasNext()) {
          Rectangle tempRect = (Rectangle)itr2.next();
          



          if ((tempRect.intersects(zoneRect)) && (!tempRect.contains(zoneRectMargin))) {
            if (newBounds == null) {
              newBounds = tempRect;
            } else {
              newBounds = newBounds.union(tempRect);
            }
          }
        }
        if (newBounds != null)
        {

          int ltx = x;int lty = y;
          int rbx = ltx + width;int rby = lty + height;
          
          if (rbx - OCRInterface.this_interface.getMaxRefineExpand() > x + width)
            rbx = x + width;
          if (rby - OCRInterface.this_interface.getMaxRefineExpand() > y + height)
            rby = y + height;
          if (ltx + OCRInterface.this_interface.getMaxRefineExpand() < x)
            ltx = x;
          if (lty + OCRInterface.this_interface.getMaxRefineExpand() < y) {
            lty = y;
          }
          

          if (width < 5)
            width = 4;
          if (height < 5) {
            height = 4;
          }
          Zone resized = (Zone)temp;
          resized.set_lt_x(ltx);
          resized.set_lt_y(lty);
          resized.set_rb_x(rbx);
          resized.set_rb_y(rby);
          try
          {
            temp.dlSetZoneOrigin(ltx, lty);
            temp.dlSetZoneWidth(rbx - ltx);
            temp.dlSetZoneHeight(rby - lty);
          } catch (DLException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }
  







  public static void shrinkAll()
  {
    getConnectedComponents();
    
    if (connectedComponents == null) {
      return;
    }
    

    for (DLZone temp : currentHWObjcurr_canvas.getShapeVec()) {
      Rectangle zoneRect = temp.get_Bounds();
      Rectangle zoneRectMargin = new Rectangle(zoneRect);
      zoneRectMargin.grow(5, 5);
      Rectangle newBounds = null;
      
      Iterator<Rectangle> itr2 = connectedComponents.iterator();
      while (itr2.hasNext()) {
        Rectangle temp2 = (Rectangle)itr2.next();
        Rectangle tempRect = temp2.intersection(zoneRect);
        


        if ((!temp2.contains(zoneRectMargin)) && (temp2.intersects(zoneRect))) {
          if (newBounds == null) {
            newBounds = tempRect;
          } else {
            newBounds = newBounds.union(tempRect);
          }
        }
      }
      if (newBounds != null)
      {



        if (width < 5)
          width = 4;
        if (height < 5) {
          height = 4;
        }
        

        if ((newBounds != null) && (width > 0) && (height > 0)) {
          Zone resized = (Zone)temp;
          resized.set_lt_x(x);
          resized.set_lt_y(y);
          resized.set_rb_x(x + width);
          resized.set_rb_y(y + height);
          try
          {
            temp.dlSetZoneOrigin(x, y);
            temp.dlSetZoneWidth(width);
            temp.dlSetZoneHeight(height);
          } catch (DLException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }
  




  public static ArrayList<Rectangle> getConnectedComponents()
  {
    if (connectedComponents == null) {
      try {
        findConnectedComponents(OCRInterface.this_interface.getConnectedComponentFilterSize());
      } catch (OutOfMemoryError e) {
        top = ImageAnalyzer.left = ImageAnalyzer.right = ImageAnalyzer.bottom = -1;
        image = null;
        bounds = null;
        connectedComponents = null;
        System.gc();
        JOptionPane.showMessageDialog(OCRInterface.this_interface, 
          "OutOfMemoryError caught. Zones will not be resized", "Alignment Error", 2);
      } catch (Exception e) {
        top = ImageAnalyzer.left = ImageAnalyzer.right = ImageAnalyzer.bottom = -1;
        image = null;
        bounds = null;
        connectedComponents = null;
        System.gc();
        JOptionPane.showMessageDialog(OCRInterface.this_interface, 
          "Too many connected components. Zones will not be resized", "Alignment Error", 2);
      }
    }
    
    return connectedComponents;
  }
  




  public static void clearConnectedComponents()
  {
    connectedComponents = null;
    System.gc();
  }
  
  private static ArrayList<Rectangle> findConnectedComponents(PolygonZone poly, int filter)
  {
    image = OCRInterface.currentHWObj.getOriginalImage().getData();
    
    Rectangle bounds = poly.get_Bounds();
    Vector<Point> verteces = poly.derectifyPoints();
    black = getBlackPixelValue();
    

    bands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    int[][] pixelComponents = new int[height][width];
    ArrayList<Integer> equals = new ArrayList()
    {
      public Integer get(int index) {
        int curr = ((Integer)super.get(index)).intValue();
        int last;
        do { last = curr;
          curr = ((Integer)super.get(last)).intValue();
        } while (curr != last);
        return new Integer(curr);
      }
    };
    equals.add(0, new Integer(0));
    




    int nextLabel = 1;
    
    LinkedList<Integer> rowEdges = getPolyRowEdges(verteces, y);
    int prev = -1;
    Iterator<Integer> intItr = rowEdges.iterator();
    while (intItr.hasNext()) {
      int cur = ((Integer)intItr.next()).intValue();
      if (prev == -1) {
        prev = cur;
      } else {
        if (prev != cur) {
          if (isBlackPixel(prev, y)) {
            pixelComponents[0][(prev - x)] = nextLabel;
            equals.add(nextLabel, new Integer(nextLabel));
            nextLabel++;
          }
          for (int x = prev + 1; x < cur; x++) {
            if (isBlackPixel(x, y))
              if (pixelComponents[0][(x - 1 - x)] == 0) {
                pixelComponents[0][(x - x)] = nextLabel;
                equals.add(nextLabel, new Integer(nextLabel));
                nextLabel++;
              } else {
                pixelComponents[0][(x - x)] = pixelComponents[0][(x - 1 - x)];
              }
          }
        }
        prev = -1;
      }
    }
    
    for (int y = y + 1; y < y + height; y++) {
      rowEdges = getPolyRowEdges(verteces, y);
      prev = -1;
      intItr = rowEdges.iterator();
      while (intItr.hasNext()) {
        int cur = ((Integer)intItr.next()).intValue();
        if (prev == -1) {
          prev = cur;
        } else {
          if (prev != cur) {
            if (isBlackPixel(prev, y)) {
              if (pixelComponents[(y - 1 - y)][(prev - x)] == 0) {
                pixelComponents[(y - y)][(prev - x)] = nextLabel;
                equals.add(nextLabel, new Integer(nextLabel));
                nextLabel++;
              } else {
                pixelComponents[(y - y)][(prev - x)] = ((Integer)equals.get(pixelComponents[(y - 1 - y)][(prev - x)])).intValue();
              }
            }
            
            for (int x = prev + 1; x < cur; x++)
              if (isBlackPixel(x, y)) {
                int top = ((Integer)equals.get(pixelComponents[(y - 1 - y)][(x - x)])).intValue();
                int left = ((Integer)equals.get(pixelComponents[(y - y)][(x - 1 - x)])).intValue();
                if ((top == 0) && (left == 0)) {
                  pixelComponents[(y - y)][(x - x)] = nextLabel;
                  equals.add(nextLabel, new Integer(nextLabel));
                  nextLabel++;
                } else if (top == left) {
                  pixelComponents[(y - y)][(x - x)] = top;
                } else if (left == 0) {
                  pixelComponents[(y - y)][(x - x)] = top;
                } else if (top == 0) {
                  pixelComponents[(y - y)][(x - x)] = left;
                } else {
                  pixelComponents[(y - y)][(x - x)] = Math.min(left, top);
                  equals.set(Math.max(left, top), new Integer(pixelComponents[(y - y)][(x - x)]));
                }
              }
          }
          prev = -1;
        }
      }
    }
    


    LinkedList[] edgePoints = new LinkedList[nextLabel - 1];
    for (int y = 0; y < height; y++) {
      int prevLabel = 0;
      for (int x = 0; x < width; x++) {
        int temp = pixelComponents[y][x];
        if ((temp == 0) && (prevLabel != 0)) {
          int index = ((Integer)equals.get(prevLabel)).intValue();
          if (edgePoints[(index - 1)] == null) {
            LinkedList<Point> tempList = new LinkedList();
            tempList.add(new Point(x - 1 + x, y + y));
            edgePoints[(index - 1)] = tempList;
          } else {
            edgePoints[(index - 1)].add(new Point(x - 1 + x, y + y));
          }
        } else if ((temp != 0) && (prevLabel == 0)) {
          int index = ((Integer)equals.get(temp)).intValue();
          if (edgePoints[(index - 1)] == null) {
            LinkedList<Point> tempList = new LinkedList();
            tempList.add(new Point(x + x, y + y));
            edgePoints[(index - 1)] = tempList;
          } else {
            edgePoints[(index - 1)].add(new Point(x + x, y + y));
          }
        }
        prevLabel = temp;
      }
      if (prevLabel != 0) {
        int index = ((Integer)equals.get(prevLabel)).intValue();
        if (edgePoints[(index - 1)] == null) {
          LinkedList<Point> tempList = new LinkedList();
          tempList.add(new Point(width - 1, y + y));
          edgePoints[(index - 1)] = tempList;
        } else {
          edgePoints[(index - 1)].add(new Point(width - 1 + x, y + y));
        }
      }
    }
    
    ArrayList<Rectangle> connectedComponents = new ArrayList();
    

    for (int i = 0; i < edgePoints.length; i++) {
      LinkedList<Point> tempList = edgePoints[i];
      if (tempList != null) {
        Point temp = (Point)Collections.min(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (x < x) {
              return 1;
            }
            if (x > x) {
              return -1;
            }
            return 0;
          }
        });
        right = x + 1;
        
        temp = (Point)Collections.max(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (x < x) {
              return 1;
            }
            if (x > x) {
              return -1;
            }
            return 0;
          }
        });
        left = x;
        
        temp = (Point)Collections.min(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (y < y) {
              return 1;
            }
            if (y > y) {
              return -1;
            }
            return 0;
          }
        });
        bottom = y + 1;
        
        temp = (Point)Collections.max(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (y < y) {
              return 1;
            }
            if (y > y) {
              return -1;
            }
            return 0;
          }
        });
        top = y;
        
        if ((right - left + 1 > filter) || (bottom - top + 1 > filter)) {
          connectedComponents.add(new Rectangle(left, top, right - left, bottom - top));
        }
      }
    }
    image = null;
    top = ImageAnalyzer.left = ImageAnalyzer.right = ImageAnalyzer.bottom = -1;
    return connectedComponents;
  }
  







  private static void findConnectedComponents(int filter)
  {
    image = OCRInterface.currentHWObj.getOriginalImage().getData();
    bounds = image.getBounds();
    black = getBlackPixelValue();
    

    bands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    int[][] pixelComponents = new int[boundsheight][boundswidth];
    ArrayList<Integer> equals = new ArrayList()
    {
      public Integer get(int index) {
        int curr = ((Integer)super.get(index)).intValue();
        int last;
        do { last = curr;
          curr = ((Integer)super.get(last)).intValue();
        } while (curr != last);
        return new Integer(curr);
      }
    };
    equals.add(0, new Integer(0));
    





    int nextLabel = 1;
    if (isBlackPixel(0, 0)) {
      pixelComponents[0][0] = nextLabel;
      equals.add(nextLabel, new Integer(nextLabel));
      nextLabel++;
    }
    for (int x = 1; x < boundswidth; x++) {
      if (isBlackPixel(x, 0)) {
        if (pixelComponents[0][(x - 1)] == 0) {
          pixelComponents[0][x] = nextLabel;
          equals.add(nextLabel, new Integer(nextLabel));
          nextLabel++;
        } else {
          pixelComponents[0][x] = pixelComponents[0][(x - 1)];
        }
      }
    }
    for (int y = 1; y < boundsheight; y++) {
      if (isBlackPixel(0, y)) {
        if (pixelComponents[(y - 1)][0] == 0) {
          pixelComponents[y][0] = nextLabel;
          equals.add(nextLabel, new Integer(nextLabel));
          nextLabel++;
        } else {
          pixelComponents[y][0] = ((Integer)equals.get(pixelComponents[(y - 1)][0])).intValue();
        }
      }
      for (int x = 1; x < boundswidth; x++) {
        if (isBlackPixel(x, y)) {
          int top = ((Integer)equals.get(pixelComponents[(y - 1)][x])).intValue();
          int left = ((Integer)equals.get(pixelComponents[y][(x - 1)])).intValue();
          if ((top == 0) && (left == 0)) {
            pixelComponents[y][x] = nextLabel;
            equals.add(nextLabel, new Integer(nextLabel));
            nextLabel++;
          } else if (top == left) {
            pixelComponents[y][x] = top;
          } else if (left == 0) {
            pixelComponents[y][x] = top;
          } else if (top == 0) {
            pixelComponents[y][x] = left;
          } else {
            pixelComponents[y][x] = Math.min(left, top);
            equals.set(Math.max(left, top), new Integer(pixelComponents[y][x]));
          }
        }
      }
    }
    

    LinkedList[] edgePoints = new LinkedList[nextLabel - 1];
    for (int y = 0; y < boundsheight; y++) {
      int prevLabel = 0;
      for (int x = 0; x < boundswidth; x++) {
        int temp = pixelComponents[y][x];
        if ((temp == 0) && (prevLabel != 0)) {
          int index = ((Integer)equals.get(prevLabel)).intValue();
          if (edgePoints[(index - 1)] == null) {
            LinkedList<Point> tempList = new LinkedList();
            tempList.add(new Point(x - 1, y));
            edgePoints[(index - 1)] = tempList;
          } else {
            edgePoints[(index - 1)].add(new Point(x - 1, y));
          }
        } else if ((temp != 0) && (prevLabel == 0)) {
          int index = ((Integer)equals.get(temp)).intValue();
          if (edgePoints[(index - 1)] == null) {
            LinkedList<Point> tempList = new LinkedList();
            tempList.add(new Point(x, y));
            edgePoints[(index - 1)] = tempList;
          } else {
            edgePoints[(index - 1)].add(new Point(x, y));
          }
        }
        prevLabel = temp;
      }
      if (prevLabel != 0) {
        int index = ((Integer)equals.get(prevLabel)).intValue();
        if (edgePoints[(index - 1)] == null) {
          LinkedList<Point> tempList = new LinkedList();
          tempList.add(new Point(boundswidth - 1, y));
          edgePoints[(index - 1)] = tempList;
        } else {
          edgePoints[(index - 1)].add(new Point(boundswidth - 1, y));
        }
      }
    }
    
    connectedComponents = new ArrayList();
    

    for (int i = 0; i < edgePoints.length; i++) {
      LinkedList<Point> tempList = edgePoints[i];
      if (tempList != null) {
        Point temp = (Point)Collections.min(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (x < x) {
              return 1;
            }
            if (x > x) {
              return -1;
            }
            return 0;
          }
        });
        right = x + 1;
        
        temp = (Point)Collections.max(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (x < x) {
              return 1;
            }
            if (x > x) {
              return -1;
            }
            return 0;
          }
        });
        left = x;
        
        temp = (Point)Collections.min(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (y < y) {
              return 1;
            }
            if (y > y) {
              return -1;
            }
            return 0;
          }
        });
        bottom = y + 1;
        
        temp = (Point)Collections.max(tempList, new Comparator() {
          public int compare(Point pt1, Point pt2) {
            if (y < y) {
              return 1;
            }
            if (y > y) {
              return -1;
            }
            return 0;
          }
        });
        top = y;
        
        if ((right - left + 1 > filter) || (bottom - top + 1 > filter)) {
          connectedComponents.add(new Rectangle(left, top, right - left, bottom - top));
        }
      }
    }
    top = ImageAnalyzer.left = ImageAnalyzer.right = ImageAnalyzer.bottom = -1;
    image = null;
    bounds = null;
  }
  
  private static LinkedList<Integer> getPolyRowEdges(Vector<Point> verteces, int y) {
    LinkedList<Integer> rowEdges = new LinkedList();
    Iterator<Point> ptItr = verteces.iterator();
    Point p = (Point)verteces.lastElement();
    while (ptItr.hasNext()) {
      Point v = (Point)ptItr.next();
      if (((y < y ? 1 : 0) ^ (y < y ? 1 : 0)) != 0)
        rowEdges.add(new Integer(x - (x - x) * (y - y) / (y - y)));
      p = v;
    }
    Collections.sort(rowEdges);
    return rowEdges;
  }
  
  public static void createReadingOrderGroups() {
    LinkedList<DLZone> heads = new LinkedList();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if ((previousZone == null) && (temp.getZoneTags().containsKey("polygon")))
        heads.add(temp);
    }
    String groupZoneType = null;
    if (!heads.isEmpty()) {
      groupZoneType = ((DLZone)heads.peekFirst()).getZoneType().replaceAll("_word$", "_group");
      if (!this_interfacetbdPane.data_panel.t_window.types.containsKey(groupZoneType)) {
        OCRInterface.getAttsConfigUtil().createNewGroupType(groupZoneType, ((DLZone)heads.peekFirst()).getZoneType());
        OCRInterface.getAttsConfigUtil().fireChanges();
      }
    }
    
    for (DLZone head : heads) {
      boolean makeGroup = false;
      String prevLineID = head.getAttributeValue("lineID");
      ArrayList<Zone> toGroup = new ArrayList();
      while (head != null) {
        Zone cur = (Zone)head;
        head = nextZone;
        String curLineID = cur.getAttributeValue("lineID");
        if (!curLineID.equals(prevLineID))
          makeGroup = true;
        toGroup.add(cur);
      }
      
      if (makeGroup) {
        Group newGroup = new Group(toGroup);
        newGroup.setZoneType(groupZoneType);
        newGroup.dlSetPagePointer(shapeVec.getPage());
        shapeVec.add(newGroup);
      }
    }
  }
  






  public static void polygonSegWord()
  {
    bands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    image = OCRInterface.currentHWObj.getOriginalImage().getData();
    black = getBlackPixelValue();
    
    LinkedList<DLZone> heads = new LinkedList();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    
    for (DLZone temp : shapeVec) {
      if ((previousZone == null) && (temp.getZoneTags().containsKey("polygon"))) {
        heads.add(temp);
      }
    }
    LinkedList<Zone> boundingList = new LinkedList();
    
    String lineZoneType = null;
    if (!heads.isEmpty()) {
      lineZoneType = ((DLZone)heads.peekFirst()).getZoneType() + "_line";
      if (!this_interfacetbdPane.data_panel.t_window.types.containsKey(lineZoneType)) {
        OCRInterface.getAttsConfigUtil().createNewZoneType(lineZoneType);
        OCRInterface.getAttsConfigUtil().fireChanges();
      }
    }
    
    DLZone head;
    
    for (Iterator localIterator2 = heads.iterator(); localIterator2.hasNext(); 
        
        head != null)
    {
      head = (DLZone)localIterator2.next();
      DLZone prevZone = null;
      continue;
      DLZone toSeg = head;
      head = nextZone;
      

      if (toSeg.getZoneTags().containsKey("polygon")) {
        Vector<Point> verteces = ((PolygonZone)toSeg).derectifyPoints();
        

        Vector<Point> tempVerteces = new Vector();
        for (Point p : verteces) {
          if (p.getY() >= image.getHeight())
            y = (image.getHeight() - 1);
          if (p.getY() < 0.0D)
            y = 0;
          if (p.getX() >= image.getWidth())
            x = (image.getWidth() - 1);
          if (p.getX() < 0.0D) {
            x = 0;
          }
          tempVerteces.add(p);
        }
        
        ((PolygonZone)toSeg).setPointsVec(tempVerteces);
        bounds = toSeg.get_Bounds();
        
        Zone boundingZone = new Zone(boundsx, boundsy, boundswidth, boundsheight);
        String boundingZoneID = boundingZone.getAttributeValue("id");
        boundingZone.getZoneTags().putAll(toSeg.getZoneTags());
        boundingZone.setAttributeValue("id", boundingZoneID);
        boundingZone.getZoneTags().remove("polygon");
        boundingZone.setZoneType(lineZoneType);
        boundingZone.setAttributeValue("segmentation", "word");
        boundingZone.dlSetPagePointer(shapeVec.getPage());
        previousZone = prevZone;
        if (prevZone != null) {
          nextZone = boundingZone;
        }
        String offsetString = "";
        



        if (toSeg.hasContents()) {
          String contents = cleanBidiString(toSeg.getContents());
          

          int numOffsets = contents.split("\\s+").length - 1;
          
          int[] profile = new int[boundswidth];
          



          int average = 0;
          for (int x = 0; x < boundswidth; x++) {
            profile[x] = 0;
          }
          for (int y = boundsy; y < boundsheight + boundsy; y++) {
            LinkedList<Integer> rowEdges = getPolyRowEdges(verteces, y);
            
            int prev = -1;
            Iterator<Integer> intItr = rowEdges.iterator();
            while (intItr.hasNext()) {
              int cur = ((Integer)intItr.next()).intValue();
              if (prev == -1) {
                prev = cur;
              } else {
                for (int x = prev; x < cur; x++)
                  profile[(x - boundsx)] += (isBlackPixel(x, y) ? 1 : 0);
                prev = -1;
              }
            }
            
            if (prev != -1) {
              for (int x = prev; x < boundswidth; x++)
                profile[x] += (isBlackPixel(x, y) ? 1 : 0);
            }
          }
          for (int x = 0; x < boundswidth; x++) {
            average += profile[x];
          }
          average /= boundswidth;
          







          LinkedList<Point> cuts = null;
          int cutTolerance = OCRInterface.this_interface.getCutTolerance();
          
          cuts = getCuts(profile, cutTolerance, numOffsets);
          for (int i = 0; (cuts == null) && (i < 10); i++) {
            cuts = getCuts(profile, average + i, numOffsets);
            if (cuts == null) {
              cuts = getCuts(profile, average - i - 1, numOffsets);
            }
          }
          TreeSet<Integer> offsetTree = new TreeSet();
          BidiString bs = new BidiString(contents, 4);
          int direction = bs.getDirection();
          int stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents);
          int offset = 0;
          int prevIndex = 0;
          




          for (int i = 0; i < numOffsets; i++) {
            index = contents.substring(prevIndex).indexOf(' ');
            int width = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(prevIndex, prevIndex + index + 1));
            prevIndex += index + 1;
            offset += width;
            
            index = Math.min(Math.max(offset * boundswidth / stringWidth, 0), boundswidth);
            
            if (direction == 1) {
              index = boundswidth - index;
            }
            if (cuts != null) {
              Point temp = (Point)Collections.min(cuts, new Comparator() {
                public int compare(Point p1, Point p2) {
                  return new Integer(Math.abs(ImageAnalyzer.index - (x + y) / 2)).compareTo(new Integer(Math.abs(ImageAnalyzer.index - (x + y) / 2)));
                }
              });
              cuts.remove(temp);
              index = (x + y) / 2;
              
              if (direction == 1) {
                offset = (boundswidth - index) * stringWidth / boundswidth;
              } else {
                offset = index * stringWidth / boundswidth;
              }
            }
            
            offsetTree.add(new Integer(index));
          }
          
          for (int i = 0; i < numOffsets; i++) {
            offsetString = offsetString + ((Integer)offsetTree.pollFirst()).intValue() + ", ";
          }
          if (offsetString.length() > 2) {
            offsetString = offsetString.substring(0, offsetString.length() - 2);
          }
        }
        

        toSeg.setContents("");
        boundingZone.setAttributeValue("offsets", offsetString);
        boundingZone.setAttributeValue("bounds_of", toSeg.getAttributeValue("id"));
        boundingList.add(boundingZone);
        prevZone = boundingZone;
      }
    }
    

    for (Zone toAdd : boundingList) {
      shapeVec.add(toAdd);
    }
    bounds = null;
    image = null;
  }
  








  public static void segWord()
  {
    image = OCRInterface.currentHWObj.getOriginalImage().getData();
    black = getBlackPixelValue();
    

    bands = OCRInterface.currentHWObj.getOriginalImage().getData().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    
    int segmentationType = getSegmentationType();
    


    for (DLZone toSeg : shapeVec)
    {
      BidiString bidiStr = null;
      if (toSeg.hasContents()) {
        String contents = cleanBidiString(toSeg.getContents());
        int numOffsets = 0;
        
        if (segmentationType == 2) {
          bidiStr = new BidiString(contents, 2);
          numOffsets = bidiStr.size() - 1;
        }
        else
        {
          numOffsets = contents.split("\\s+").length - 1;
        }
        bounds = toSeg.get_Bounds();
        int[] profile = new int[boundswidth];
        



        int average = 0;
        for (int x = 0; x < boundswidth; x++) {
          profile[x] = 0;
          for (int y = 0; y < boundsheight; y++) {
            profile[x] += (isBlackPixel(x + boundsx, y + boundsy) ? 1 : 0);
          }
          average += profile[x];
        }
        
        average /= boundswidth;
        







        LinkedList<Point> cuts = null;
        int cutTolerance = OCRInterface.this_interface.getCutTolerance();
        
        cuts = getCuts(profile, cutTolerance, numOffsets);
        for (int i = 0; (cuts == null) && (i < 10); i++) {
          cuts = getCuts(profile, average + i, numOffsets);
          if (cuts == null) {
            cuts = getCuts(profile, average - i - 1, numOffsets);
          }
        }
        String offsetString = "";
        ArrayList<Integer> offsetTree = new ArrayList();
        BidiString bs = new BidiString(contents, 4);
        int direction = bs.getDirection();
        int stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents);
        int offset = 0;
        int prevIndex = 0;
        




        for (int i = 0; i < numOffsets; i++) {
          int width = 0;
          if (segmentationType == 2) {
            index = i + 1;
            
            String nextChar = bidiStr.getNext(index);
            width = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(nextChar);
          }
          else {
            index = contents.substring(prevIndex).indexOf(' ');
            width = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(prevIndex, prevIndex + index + 1));
          }
          
          prevIndex += index + 1;
          offset += width;
          
          index = Math.min(Math.max(offset * boundswidth / stringWidth, 0), boundswidth);
          
          if (direction == 1) {
            index = boundswidth - index;
          }
          if (cuts != null) {
            Point temp = (Point)Collections.min(cuts, new Comparator() {
              public int compare(Point p1, Point p2) {
                return new Integer(Math.abs(ImageAnalyzer.index - (x + y) / 2)).compareTo(new Integer(Math.abs(ImageAnalyzer.index - (x + y) / 2)));
              }
            });
            cuts.remove(temp);
            index = (x + y) / 2;
            
            if (direction == 1) {
              offset = (boundswidth - index) * stringWidth / boundswidth;
            } else {
              offset = index * stringWidth / boundswidth;
            }
          }
          

          offsetTree.add(new Integer(index));
        }
        
        Collections.sort(offsetTree);
        
        for (int i = 0; i < numOffsets; i++) {
          offsetString = offsetString + ((Integer)offsetTree.remove(0)).intValue() + ", ";
        }
        if (offsetString.length() > 2) {
          offsetString = offsetString.substring(0, offsetString.length() - 2);
        }
        if (segmentationType == 2) {
          toSeg.setAttributeValue("segmentation", "character");
        } else {
          toSeg.setAttributeValue("segmentation", "word");
        }
        toSeg.setAttributeValue("offsets", offsetString);
      }
    }
    bounds = null;
    image = null;
  }
  
  private static int getSegmentationType()
  {
    if (OCRInterface.this_interface.getAlignmentDefaultSegmenation().trim().equalsIgnoreCase("word")) {
      return 3;
    }
    return 2;
  }
  









  private static LinkedList<Point> getCuts(int[] profile, int threshold, int numOffsets)
  {
    LinkedList<Point> ret = new LinkedList();
    Point temp = null;
    for (int i = 0; i < profile.length; i++) {
      if ((temp == null) && (profile[i] < threshold)) {
        temp = new Point(i, -1);
      } else if ((temp != null) && (profile[i] >= threshold)) {
        y = (i - 1);
        

        if (x > 0) {
          ret.add(temp);
        }
        temp = null;
      }
    }
    


    if (ret.size() < numOffsets) {
      return null;
    }
    
    while (ret.size() > numOffsets + 2) {
      ret.remove(Collections.min(ret, new Comparator() {
        public int compare(Point p1, Point p2) {
          return new Integer(y - x).compareTo(new Integer(y - x));
        }
      }));
    }
    
    Collections.sort(ret, new Comparator() {
      public int compare(Point p1, Point p2) {
        return new Integer(x).compareTo(new Integer(x));
      }
      
    });
    return ret;
  }
  
  public static LinkedList<Point> clipPolygonRectangle(Vector<Point> poly, Rectangle rect) {
    LinkedList<Point> ret = new LinkedList();
    
    Point p = (Point)poly.lastElement();
    Iterator<Point> ptItr = poly.iterator();
    while (ptItr.hasNext()) {
      Point c = (Point)ptItr.next();
      if (((y > y ? 1 : 0) ^ (y > y ? 1 : 0)) != 0) {
        ret.push(new Point(x + (x - x) * (y - y) / (y - y), y));
      }
      if (y > y) {
        ret.push(c);
      }
      p = c;
    }
    
    p = (Point)ret.peekLast();
    ptItr = ret.iterator();
    ret = new LinkedList();
    while (ptItr.hasNext()) {
      Point c = (Point)ptItr.next();
      if (((x > x ? 1 : 0) ^ (x > x ? 1 : 0)) != 0) {
        ret.push(new Point(x, y + (y - y) * (x - x) / (x - x)));
      }
      if (x > x) {
        ret.push(c);
      }
      p = c;
    }
    
    p = (Point)ret.peekLast();
    ptItr = ret.iterator();
    ret = new LinkedList();
    while (ptItr.hasNext()) {
      Point c = (Point)ptItr.next();
      if (((y < y + height ? 1 : 0) ^ (y < y + height ? 1 : 0)) != 0) {
        ret.push(new Point(x + (x - x) * (y + height - y) / (y - y), y + height));
      }
      if (y < y + height) {
        ret.push(c);
      }
      p = c;
    }
    
    p = (Point)ret.peekLast();
    ptItr = ret.iterator();
    ret = new LinkedList();
    while (ptItr.hasNext()) {
      Point c = (Point)ptItr.next();
      if (((x < x + width ? 1 : 0) ^ (x < x + width ? 1 : 0)) != 0) {
        ret.push(new Point(x + width, y + (y - y) * (x + width - x) / (x - x)));
      }
      if (x < x + width) {
        ret.push(c);
      }
      p = c;
    }
    

    return ret;
  }
  
  public static Vector<String> clipAllToPoly(String transcriptionType) {
    Vector<String> problemPolygons = new Vector();
    LinkedList<DLZone> heads = new LinkedList();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType + "_line"))
      {
        if ((previousZone == null) && (temp.getZoneTags().containsKey("bounds_of")))
          heads.add(temp);
      }
    }
    String wordZoneType = null;
    if (!heads.isEmpty()) {
      wordZoneType = ((DLZone)heads.peekFirst()).getZoneType().replaceAll("_line$", "_word");
      if (!this_interfacetbdPane.data_panel.t_window.types.containsKey(wordZoneType)) {
        OCRInterface.getAttsConfigUtil().createNewZoneType(wordZoneType);
        OCRInterface.getAttsConfigUtil().fireChanges();
      }
    }
    
    int zoneID = 0;
    
    Collections.sort(heads, new Comparator()
    {
      public int compare(DLZone a, DLZone b) { return new Integer(get_Boundsy).compareTo(new Integer(get_Boundsy)); }
    });
    DLZone head;
    PolygonZone prev;
    for (Iterator localIterator2 = heads.iterator(); localIterator2.hasNext(); 
        
        head != null)
    {
      head = (DLZone)localIterator2.next();
      prev = null;
      continue;
      zoneID++;
      DLZone toClip = head;
      DLZone clipTo = OCRInterface.this_interface.getUniqueZoneIdObj().searchZone((String)toClip.getZoneTags().get("bounds_of"), false);
      LinkedList<Point> verteces = clipPolygonRectangle(((PolygonZone)clipTo).derectifyPoints(), toClip.get_Bounds());
      if (!verteces.isEmpty()) {
        PolygonZone clipped = new PolygonZone(new Vector(verteces));
        clipped.getZoneTags().putAll(toClip.getZoneTags());
        clipped.getZoneTags().remove("bounds_of");
        clipped.getZoneTags().remove("segmentation");
        clipped.setZoneType(wordZoneType);
        
        clipped.dlSetPagePointer(shapeVec.getPage());
        clipped.setAttributeValue("id", zoneID);
        zoneID = zoneID;
        previousZone = prev;
        if (prev != null) {
          nextZone = clipped;
        }
        shapeVec.add(clipped);
        
        prev = clipped;
        head = (Zone)nextZone;
        shapeVec.remove(toClip);
      }
      else {
        System.out.println("clipAllToPoly: problem polygon -- invalid intersection with box: " + clipTo);
        head = (Zone)nextZone;
        problemPolygons.add(zoneID);
      }
    }
    

    LinkedList<DLZone> toRemove = new LinkedList();
    for (DLZone temp : shapeVec) {
      if (!temp.getZoneType().startsWith(transcriptionType)) {
        zoneID++;
        temp.setAttributeValue("id", zoneID);

      }
      else if (!temp.getZoneType().endsWith("_word")) {
        toRemove.add(temp);
      }
    }
    for (DLZone rem : toRemove) {
      shapeVec.remove(rem);
    }
    return problemPolygons;
  }
  





  public static void polySplit(String transcriptionType)
  {
    LinkedList<DLZone> heads = new LinkedList();
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if (temp.getAttributeValue("gedi_type").equalsIgnoreCase(transcriptionType + "_line"))
      {
        if ((previousZone == null) && (temp.getZoneTags().containsKey("bounds_of")))
          heads.add(temp);
      }
    }
    int lineID = 0;
    DLZone head;
    for (Iterator localIterator2 = heads.iterator(); localIterator2.hasNext(); 
        
        head != null)
    {
      head = (DLZone)localIterator2.next();
      DLZone prevZone = null;
      continue;
      lineID++;
      Zone toSplit = (Zone)head;
      if (toSplit.hasContents()) {
        shapeVec.remove(toSplit);
        String contents = toSplit.getContents();
        contents = contents.replaceAll("\\s+", " ");
        toSplit.setContents(contents);
        BidiString bs = new BidiString(contents, 3);
        int direction = bs.getDirection();
        int maxOffsets = bs.size();
        
        String firstLine = null;
        if (direction == 0) {
          firstLine = "0";
        } else {
          firstLine = String.valueOf(toSplit.get_rb_x() - toSplit.get_lt_x());
        }
        ArrayList<String> offsets = toSplit.getOffsetsArray(firstLine, direction, maxOffsets);
        
        for (int i = 0; i < offsets.size(); i++) {
          String txt = bs.getNext(offsets.size() - (i + 1));
          
          if (!txt.trim().equalsIgnoreCase("∅"))
          {
            int nextOffset;
            
            int offset;
            int nextOffset;
            if (direction == 0) {
              int offset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(i)).intValue();
              int nextOffset; if (i + 1 < offsets.size()) {
                nextOffset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(i + 1)).intValue();
              } else
                nextOffset = toSplit.get_rb_x();
            } else {
              offset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 1 - i)).intValue();
              int nextOffset; if (i + 1 < offsets.size()) {
                nextOffset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 2 - i)).intValue();
              } else {
                nextOffset = toSplit.get_lt_x();
              }
            }
            
            Zone wordZone = new Zone(Math.min(offset, nextOffset), toSplit.get_lt_y(), 
              Math.abs(nextOffset - offset), toSplit.get_rb_y() - toSplit.get_lt_y());
            String wordZoneID = wordZone.getAttributeValue("id");
            wordZone.getZoneTags().putAll(toSplit.getZoneTags());
            wordZone.getZoneTags().remove("offsets");
            wordZone.setAttributeValue("id", wordZoneID);
            wordZone.setZoneType(toSplit.getZoneType());
            wordZone.setContents(txt);
            

            wordZone.setAttributeValue("lineID", lineID);
            wordZone.dlSetPagePointer(shapeVec.getPage());
            
            shapeVec.add(wordZone);
            previousZone = prevZone;
            if (prevZone != null)
              nextZone = wordZone;
            prevZone = wordZone;
          }
        }
      } else { shapeVec.remove(toSplit);
        
        Zone wordZone = new Zone(toSplit.get_lt_x(), toSplit.get_lt_y(), 
          toSplit.get_rb_x() - toSplit.get_lt_x(), toSplit.get_rb_y() - toSplit.get_lt_y());
        String wordZoneID = wordZone.getAttributeValue("id");
        wordZone.getZoneTags().putAll(toSplit.getZoneTags());
        wordZone.getZoneTags().remove("offsets");
        wordZone.setAttributeValue("id", wordZoneID);
        wordZone.setZoneType(toSplit.getZoneType());
        
        wordZone.setAttributeValue("lineID", lineID);
        wordZone.dlSetPagePointer(shapeVec.getPage());
        
        shapeVec.add(wordZone);
        previousZone = prevZone;
        if (prevZone != null)
          nextZone = wordZone;
        prevZone = wordZone;
      }
      head = nextZone;
    }
  }
  






  public static void split()
  {
    DLZone head = null;
    ZonesManager shapeVec = currentHWObjcurr_canvas.getShapeVec();
    for (DLZone temp : shapeVec) {
      if ((previousZone == null) && (nextZone != null))
        head = temp;
    }
    DLZone prevZone = null;
    
    System.out.println("->>head: " + head);
    
    if (head == null) {
      Object orderedShapeVec = (Vector)shapeVec.getAsVector().clone();
      Collections.sort((List)orderedShapeVec, new Comparator() {
        public int compare(DLZone z1, DLZone z2) {
          Integer y1 = new Integer(get_Boundsy);
          Integer y2 = new Integer(get_Boundsy);
          return y1.compareTo(y2);
        }
      });
      Iterator<DLZone> itr2 = ((Vector)orderedShapeVec).iterator();
      while (itr2.hasNext()) {
        Zone temp = (Zone)itr2.next();
        previousZone = prevZone;
        if (prevZone != null) {
          nextZone = temp;
        } else
          head = temp;
        prevZone = temp;
      }
      prevZone = null;
    }
    
    while (head != null) {
      Zone toSplit = (Zone)head;
      int segmentationType = getSegmentationType();
      String segmentatioTypeStr = "word";
      
      if (segmentationType == 2) {
        segmentatioTypeStr = "character";
      }
      if (toSplit.offsetsReady()) {
        shapeVec.remove(toSplit);
        String contents = cleanBidiString(toSplit.getContents());
        contents = contents.replaceAll("\\s+", " ");
        toSplit.setAttributeValue("contents", contents);
        
        BidiString bs = new BidiString(contents, segmentationType);
        int direction = bs.getDirection();
        int maxOffsets = bs.size();
        
        String firstLine = null;
        if (direction == 0) {
          firstLine = "0";
        } else {
          firstLine = String.valueOf(toSplit.get_rb_x() - toSplit.get_lt_x());
        }
        ArrayList<String> offsets = toSplit.getOffsetsArray(firstLine, direction, maxOffsets);
        
        for (int i = 0; i < offsets.size(); i++) {
          int nextOffset;
          int offset;
          int nextOffset;
          if (direction == 0) {
            int offset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(i)).intValue();
            int nextOffset; if (i + 1 < offsets.size()) {
              nextOffset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(i + 1)).intValue();
            } else
              nextOffset = toSplit.get_rb_x();
          } else {
            offset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 1 - i)).intValue();
            int nextOffset; if (i + 1 < offsets.size()) {
              nextOffset = toSplit.get_lt_x() + Integer.valueOf((String)offsets.get(offsets.size() - 2 - i)).intValue();
            } else {
              nextOffset = toSplit.get_lt_x();
            }
          }
          String txt = bs.getNext(offsets.size() - (i + 1));
          
          if ((txt != null) && (!txt.trim().isEmpty()))
          {

            Zone wordZone = new Zone(Math.min(offset, nextOffset), toSplit.get_lt_y(), 
              Math.abs(nextOffset - offset), toSplit.get_rb_y() - toSplit.get_lt_y());
            wordZone.setZoneType(toSplit.getZoneType());
            wordZone.setAttributeValue("contents", txt);
            wordZone.setAttributeValue("segmentation", segmentatioTypeStr);
            wordZone.setAttributeValue("srclineid", 
              toSplit.getAttributeValue("srclineid"));
            wordZone.dlSetPagePointer(shapeVec.getPage());
            
            shapeVec.add(wordZone);
            if (prevZone != null) {
              previousZone = prevZone;
              nextZone = wordZone;
            }
            prevZone = wordZone;

          }
          

        }
        


      }
      else
      {


        if ((toSplit.getContents() == null) || (toSplit.getContents().trim().isEmpty())) {
          continue;
        }
        shapeVec.remove(toSplit);
        String contents = cleanBidiString(toSplit.getContents());
        contents = contents.replaceAll("\\s+", " ");
        
        Zone wordZone = new Zone(toSplit.get_lt_x(), 
          toSplit.get_lt_y(), 
          toSplit.get_width(), 
          toSplit.get_height());
        wordZone.setZoneType(toSplit.getZoneType());
        wordZone.setAttributeValue("contents", contents);
        wordZone.setAttributeValue("segmentation", segmentatioTypeStr);
        wordZone.setAttributeValue("srclineid", 
          toSplit.getAttributeValue("srclineid"));
        wordZone.dlSetPagePointer(shapeVec.getPage());
        
        shapeVec.add(wordZone);
        
        if (prevZone != null) {
          previousZone = prevZone;
          nextZone = wordZone;
        }
        prevZone = wordZone;
      }
      
      head = (Zone)nextZone;
    }
  }
  










  public static void merge(String txt, boolean oneZone)
  {
    String contents = cleanBidiString(readRawText(txt));
    
    Vector<DLZone> shapeVec = currentHWObjcurr_canvas.getShapeVec().getAsVector();
    Collections.sort(shapeVec, new Comparator() {
      public int compare(DLZone z1, DLZone z2) {
        Integer y1 = new Integer(get_Boundsy);
        Integer y2 = new Integer(get_Boundsy);
        return y1.compareTo(y2);
      }
      
    });
    String segmentatioType = "word";
    if (getSegmentationType() == 2) {
      segmentatioType = "character";
    }
    Iterator<DLZone> itr = shapeVec.iterator();
    if (oneZone) {
      Zone destination = null;
      while (itr.hasNext()) {
        Zone temp = (Zone)itr.next();
        if ((temp.hasContents()) && ((destination == null) || (destination.get_lt_y() > temp.get_lt_y())))
          destination = temp;
      }
      if (destination == null) {
        float scale = OCRInterface.this_interface.getCanvas().getScale();
        Rectangle visible = OCRInterface.currentHWObj.getPictureScrollPane().getViewport().getVisibleRect();
        destination = new Zone(
          (int)(x / scale + width / scale * 0.4D), 
          (int)(y / scale + height / scale * 0.4D), 
          (int)(width / scale * 0.2D), 
          (int)(height / scale * 0.2D));
        destination.setZoneType("DL_TEXTLINEGT");
        shapeVec.add(destination);
      }
      
      contents = contents.replaceAll("\\s+", " ");
      destination.setContents(contents);
      destination.setAttributeValue("offsets", "");
      destination.setAttributeValue("segmentation", segmentatioType);
    } else {
      contents = contents.replaceAll("(\n|\r)+\\s*(\n|\r)", "\n");
      Zone prev = null;
      while (itr.hasNext()) {
        Zone temp = (Zone)itr.next();
        String thisLine = "";
        if (contents.indexOf('\n') > -1) {
          thisLine = contents.substring(0, contents.indexOf('\n')).replaceAll("\\s+", " ");
          contents = contents.substring(contents.indexOf('\n') + 1);
        } else {
          thisLine = contents.replaceAll("\\s+", " ");
          contents = "";
        }
        temp.setContents(thisLine.trim());
        temp.setAttributeValue("offsets", "");
        temp.setAttributeValue("segmentation", segmentatioType);
        
        if (prev != null) {
          nextZone = temp;
          previousZone = prev;
        }
        prev = temp;
      }
    }
  }
  



  public static boolean copyFile(File in, File out)
  {
    out.delete();
    try {
      FileInputStream fis = new FileInputStream(in);
      FileOutputStream fos = new FileOutputStream(out);
      byte[] buf = new byte['Ѐ'];
      int i = 0;
      while ((i = fis.read(buf)) != -1) {
        fos.write(buf, 0, i);
      }
      fis.close();
      fos.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Could not copy data file.", "Error", 0); }
    return false;
  }
  










  public static Rectangle shrinkExpand(Raster img, Rectangle selection, int filterSize)
  {
    selection = shrink(img, selection, filterSize);
    

    if ((height == 0) || (width == 0)) {
      return selection;
    }
    filter = filterSize;
    bounds = (Rectangle)selection.clone();
    selection2 = selection;
    image = img;
    open = new LinkedList();
    
    black = getBlackPixelValue();
    
    bands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    boolean inside = true;
    
    int y = y;
    
    for (int x = x; x < width + x; x++)
    {
      if (isBlackPixel(x, y)) {
        if (!inside)
          expandComponent(x, y, 2);
        inside = true;
      } else {
        inside = false;
      }
    }
    
    x = x + width - 1;
    for (y = y; y < height + y; y++)
    {
      if (isBlackPixel(x, y)) {
        if (!inside)
          expandComponent(x, y, 4);
        inside = true;
      } else {
        inside = false;
      }
    }
    
    y = y + height - 1;
    for (x = x + width - 1; x >= x; x--)
    {
      if (isBlackPixel(x, y)) {
        if (!inside)
          expandComponent(x, y, 6);
        inside = true;
      } else {
        inside = false;
      }
    }
    
    x = x;
    for (y = y + height - 1; y >= y; y--)
    {
      if (isBlackPixel(x, y)) {
        if (!inside)
          expandComponent(x, y, 0);
        inside = true;
      } else {
        inside = false;
      }
    }
    
    open = null;
    image = null;
    bounds = null;
    
    return new Rectangle(left, top, right - left + 1, bottom - top + 1);
  }
  




  public static Rectangle shrink(Raster img, Rectangle selection, int filterSize)
  {
    filter = filterSize;
    bounds = (Rectangle)selection.clone();
    image = img;
    open = new LinkedList();
    top = ImageAnalyzer.bottom = ImageAnalyzer.left = ImageAnalyzer.right = -1;
    
    System.out.println("bounds w/h: " + boundswidth + "/" + boundsheight);
    if ((boundswidth < 1) || (boundsheight < 1)) {
      return new Rectangle(0, 0, 0, 0);
    }
    black = getBlackPixelValue();
    
    bands = OCRInterface.currentHWObj.getOriginalImage().getNumBands();
    if (bands > 3) {
      bands = 3;
    }
    for (int y = y; (top == -1) && (y < height + y); y++) {
      for (int x = x; (top == -1) && (x < width + x); x++)
      {
        if ((isBlackPixel(x, y)) && (passFilter(x, y))) {
          top = y;
        }
      }
    }
    
    if (top == -1) {
      return new Rectangle(0, 0, 0, 0);
    }
    for (int y = y + height - 1; (bottom == -1) && (y >= y); y--) {
      for (int x = x; (bottom == -1) && (x < width + x); x++) {
        if ((isBlackPixel(x, y)) && (passFilter(x, y))) {
          bottom = y;
        }
      }
    }
    
    for (int x = x; (left == -1) && (x < width + x); x++) {
      for (int y = top; (left == -1) && (y <= bottom); y++) {
        if ((isBlackPixel(x, y)) && (passFilter(x, y))) {
          left = x;
        }
      }
    }
    
    for (int x = x + width - 1; (right == -1) && (x >= x); x--) {
      for (int y = top; (right == -1) && (y <= bottom); y++) {
        if ((isBlackPixel(x, y)) && (passFilter(x, y))) {
          right = x;
        }
      }
    }
    
    open = null;
    image = null;
    
    return new Rectangle(left, top, right - left + 1, bottom - top + 1);
  }
  



  private static boolean passFilter(int x, int y)
  {
    open.add(0, new Point(x, y));
    for (int i = 0; (i < open.size()) && (open.size() < filter); i++) {
      expand((Point)open.get(i));
    }
    boolean ret = open.size() >= filter;
    open.clear();
    return ret;
  }
  
  private static void expand(Point pt) {
    if (y > boundsy) {
      add(x, y - 1);
    }
    if ((y > boundsy) && (boundsx + boundswidth > x + 1)) {
      add(x + 1, y - 1);
    }
    if (boundsx + boundswidth > x + 1) {
      add(x + 1, y);
    }
    if ((boundsx + boundswidth > x + 1) && (boundsy + boundsheight > y + 1)) {
      add(x + 1, y + 1);
    }
    if (boundsy + boundsheight > y + 1) {
      add(x, y + 1);
    }
    if ((boundsy + boundsheight > y + 1) && (boundsx < x)) {
      add(x - 1, y + 1);
    }
    if (boundsx < x) {
      add(x - 1, y);
    }
    if ((boundsx < x) && (y > boundsy)) {
      add(x - 1, y - 1);
    }
  }
  
  private static void expand_orig(Point pt)
  {
    if (y > boundsy) {
      add(x, y - 1);
    }
    if ((y > boundsy) && (boundsx + boundswidth > x + 1)) {
      add(x + 1, y - 1);
    }
    if (boundsx + boundswidth > x + 1) {
      add(x + 1, y);
    }
    if ((boundsx + boundswidth > x + 1) && (boundsy + boundsheight > y + 1)) {
      add(x + 1, y + 1);
    }
    if (boundsy + boundsheight > y + 1) {
      add(x, y + 1);
    }
    if ((boundsy + boundsheight > y + 1) && (boundsx < x)) {
      add(x - 1, y + 1);
    }
    if (boundsx < x) {
      add(x - 1, y);
    }
    if ((boundsx < x) && (y > boundsy)) {
      add(x - 1, y - 1);
    }
  }
  
  private static void expandComponent(int x, int y, int dir) {
    open.add(0, new Point(x, y));
    






    Point temp = new Point(x, y);
    
    int initDir = dir;
    do
    {
      dir = expand(dir, temp);
    } while (((dir != initDir) || (!selection2.contains(temp))) && ((x != x) || (y != y)));
    
    updateExtremes();
    
    open.clear();
  }
  
  private static boolean add(int x, int y) {
    if (isBlackPixel(x, y)) {
      if (!open.contains(new Point(x, y)))
        open.add(open.size(), new Point(x, y));
      return true;
    }
    return false;
  }
  
  private static int expand(int dir, Point pt)
  {
    int dir2 = (dir + 5) % 8;
    do
    {
      switch (dir2) {
      case 0: 
        if ((y > image.getMinY()) && (add(x, y - 1))) {
          y -= 1;
          return dir2;
        }
        break;
      case 1: 
        if ((y > image.getMinY()) && (image.getMinX() + image.getWidth() > x + 1) && (add(x + 1, y - 1))) {
          y -= 1;
          x += 1;
          return dir2;
        }
        break;
      case 2: 
        if ((image.getMinX() + image.getWidth() > x + 1) && (add(x + 1, y))) {
          x += 1;
          return dir2;
        }
        break;
      case 3: 
        if ((image.getMinX() + image.getWidth() > x + 1) && (image.getMinY() + image.getHeight() > y + 1) && (add(x + 1, y + 1))) {
          x += 1;
          y += 1;
          return dir2;
        }
        break;
      case 4: 
        if ((image.getMinY() + image.getHeight() > y + 1) && (add(x, y + 1))) {
          y += 1;
          return dir2;
        }
        break;
      case 5: 
        if ((image.getMinY() + image.getHeight() > y + 1) && (image.getMinX() < x) && (add(x - 1, y + 1))) {
          x -= 1;
          y += 1;
          return dir2;
        }
        break;
      case 6: 
        if ((image.getMinX() < x) && (add(x - 1, y))) {
          x -= 1;
          return dir2;
        }
        break;
      case 7: 
        if ((image.getMinX() < x) && (y > image.getMinY()) && (add(x - 1, y - 1))) {
          x -= 1;
          y -= 1;
          return dir2;
        }
        break;
      }
      dir2 = (dir2 + 1) % 8;
    } while (dir2 != (dir + 5) % 8);
    
    return -1;
  }
  
  private static void updateExtremes() {
    if (top == -1) {
      Point temp = (Point)open.peek();
      top = ImageAnalyzer.bottom = y;
      right = ImageAnalyzer.left = x;
    }
    
    Point temp = (Point)Collections.min(open, new Comparator() {
      public int compare(Point pt1, Point pt2) {
        if (x < x) {
          return 1;
        }
        if (x > x) {
          return -1;
        }
        return 0;
      }
    });
    if (x > right) {
      right = x;
    }
    temp = (Point)Collections.max(open, new Comparator() {
      public int compare(Point pt1, Point pt2) {
        if (x < x) {
          return 1;
        }
        if (x > x) {
          return -1;
        }
        return 0;
      }
    });
    if (x < left) {
      left = x;
    }
    temp = (Point)Collections.min(open, new Comparator() {
      public int compare(Point pt1, Point pt2) {
        if (y < y) {
          return 1;
        }
        if (y > y) {
          return -1;
        }
        return 0;
      }
    });
    if (y > bottom) {
      bottom = y;
    }
    temp = (Point)Collections.max(open, new Comparator() {
      public int compare(Point pt1, Point pt2) {
        if (y < y) {
          return 1;
        }
        if (y > y) {
          return -1;
        }
        return 0;
      }
    });
    if (y < top) {
      top = y;
    }
  }
}
