package ocr.gui;

import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLException;
import gttool.exceptions.DLExceptionCodes;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Line2D.Float;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.swing.JViewport;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.tif.ImageAnalyzer;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import ocr.tif.ImageReaderDrawer.MyScrollPane;
import ocr.util.BidiString;

public class Zone extends DLZone
{
  private static final int DELTA_FOR_GTSPLIT_LINE = 10;
  OCRInterface ocrIF = OCRInterface.this_interface;
  
  public int caret = 0;
  

  public SelectedWord selectedWord = null;
  public int isHoriz = 0;
  



  public static final int SPECIALCHAR_ZONE_ID = 60;
  



  public static final int TOP_LEFT = 0;
  



  public static final int TOP_RIGHT = 1;
  


  public static final int BOT_LEFT = 2;
  


  public static final int BOT_RIGHT = 3;
  


  public static final int TOP = 4;
  


  public static final int BOTTOM = 5;
  


  public static final int LEFT = 6;
  


  public static final int RIGHT = 7;
  


  public static final int UNSELECTED = -1;
  


  public static final int LEFT_SIDE = 0;
  


  public static final int RIGHT_SIDE = 1;
  


  public static final int SPLIT_SIDE = 2;
  


  public static final int UNDET_ZONE_ID = 100;
  


  public Point splitLinePt1;
  


  public Point splitLinePt2;
  


  protected Point lt;
  


  protected Point rb;
  


  protected int moveOffsetX;
  


  protected int moveOffsetY;
  


  private Point creationPt;
  


  public boolean isIncomplete;
  


  private final int minSepConst = 2;
  



  protected int selectedCorner;
  


  public static final int defaultLineSelected = -5;
  


  public int lineSelected = -5;
  





  public int workmode;
  





  public boolean leftResize = false;
  public int delta = 0;
  





  protected Point lastMouseEvent;
  





  private int contentDirection;
  





  protected Color color;
  





  public Zone(int xIn, int yIn, float scale)
  {
    super(true);
    lt = new Point();
    rb = new Point();
    creationPt = new Point();
    creationPt.x = ((int)(xIn / scale));
    creationPt.y = ((int)(yIn / scale));
    isIncomplete = true;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = OCRInterface.currWorkmode;
  }
  










  public Zone(boolean selectionZone, int xIn, int yIn, float scale)
  {
    super(false);
    lt = new Point();
    rb = new Point();
    creationPt = new Point();
    creationPt.x = ((int)(xIn / scale));
    creationPt.y = ((int)(yIn / scale));
    isIncomplete = true;
    selectedCorner = -1;
  }
  







  public Zone(String id, int x, int y, int w, int h)
  {
    super(id, x, y, w, h);
    lt = new Point(x, y);
    rb = new Point(x + w, y + h);
    width = w;
    height = h;
    creationPt = new Point();
    isIncomplete = false;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = OCRInterface.currWorkmode;
  }
  
  public Zone(int x, int y, int w, int h) {
    super(x, y, w, h);
    lt = new Point(x, y);
    rb = new Point(x + w, y + h);
    width = w;
    height = h;
    creationPt = new Point();
    isIncomplete = false;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = OCRInterface.currWorkmode;
  }
  
















  public Zone(int workmode, String zoneID, int left, int top, int right, int bottom)
  {
    super(false);
    lt = new Point();
    rb = new Point();
    this.zoneID = zoneID;
    lt.x = left;
    lt.y = top;
    rb.x = right;
    rb.y = bottom;
    this.workmode = workmode;
    selectedCorner = -1;
    isIncomplete = false;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    try {
      dlSetZoneOrigin(lt.x, lt.y);
      dlSetZoneWidth(rb.x - lt.x);
      dlSetZoneHeight(rb.y - lt.y);
    }
    catch (DLException localDLException) {}
  }
  






  public Zone(DLZone mergedZone)
    throws DLException
  {
    super(mergedZone.dlGetZonePage(), mergedZone.dlGetParentZone(), dlGetZoneOriginx, dlGetZoneOriginy, mergedZone.dlGetZoneWidth(), mergedZone.dlGetZoneHeight());
    
    int x = dlGetZoneOriginx;
    int y = dlGetZoneOriginy;int w = mergedZone.dlGetZoneWidth();
    int h = mergedZone.dlGetZoneHeight();
    





    getZoneTags().putAll(mergedZone.getZoneTags());
    childZones.addAll(childZones);
    
    lt = new Point(x, y);
    rb = new Point(x + w, y + h);
    creationPt = new Point();
    isIncomplete = false;
    selectedCorner = -1;
    splitLinePt1 = new Point();
    splitLinePt2 = new Point();
    workmode = OCRInterface.currWorkmode;
    zoneID = zoneID;
  }
  
  public Zone(boolean zoneIDRequested) {
    super(zoneIDRequested);
  }
  



  public Zone clone()
  {
    return clone_zone();
  }
  





  public Zone clone_zone()
  {
    Zone z = new Zone(workmode, zoneID, get_lt_x(), get_lt_y(), get_rb_x(), 
      get_rb_y());
    zonePage = zonePage;
    z.getZoneTags().putAll(getZoneTags());
    parentZone = parentZone;
    nextZone = nextZone;
    previousZone = previousZone;
    caret = caret;
    
    for (Object child : childZones) {
      Zone c = (Zone)child;
      try {
        z.dlAppendChildZone(c.clone_zone());
      }
      catch (DLException localDLException) {}
    }
    


    return z;
  }
  


















  public void set_lt_x(int scaledXin)
  {
    if (scaledXin <= rb.x) {
      if (OCRInterface.currOppmode != 4) {
        delta += lt.x - scaledXin;
      }
      lt.x = scaledXin;
    } else {
      lt.x = (rb.x - 2);
    }
  }
  








  public void set_lt_y(int scaledYin)
  {
    if (scaledYin <= rb.y) {
      lt.y = scaledYin;
    } else {
      lt.y = (rb.y - 2);
    }
  }
  







  public void set_rb_x(int scaledXin)
  {
    if (scaledXin >= lt.x) {
      rb.x = scaledXin;
    } else {
      rb.x = (lt.x + 2);
    }
  }
  







  public void set_rb_y(int scaledYin)
  {
    if (scaledYin >= lt.y) {
      rb.y = scaledYin;
    } else {
      rb.y = (lt.y + 1);
    }
  }
  




  public int get_width()
  {
    return rb.x - lt.x;
  }
  





  public int get_height()
  {
    return rb.y - lt.y;
  }
  




  public int get_lt_x()
  {
    try
    {
      return lt.x;
    }
    catch (NullPointerException localNullPointerException) {}
    


    return 0;
  }
  






  public int get_lt_y()
  {
    return lt.y;
  }
  






  public int get_rb_x()
  {
    return rb.x;
  }
  





  public int get_rb_y()
  {
    return rb.y;
  }
  


  public String print()
  {
    String result = null;
    






















    return result;
  }
  





  public Point getCenter(float scale)
  {
    return new Point(
      (int)((get_lt_x() + (get_rb_x() - get_lt_x()) / 2) * scale), 
      (int)((get_lt_y() + (get_rb_y() - get_lt_y()) / 2) * scale));
  }
  
  public Point getULCorner(float scale) {
    return new Point(
      (int)((get_lt_x() + (get_rb_x() - get_lt_x()) / 2) * scale), 
      (int)((get_lt_y() + (get_rb_y() - get_lt_y()) / 2) * scale));
  }
  






















  public void draw(Graphics g, float scale, boolean isSelected, boolean drawSplit, Point p, int electronicTextSize, boolean isCtrlDown)
  {
    int cornerSize = 3;
    






    Graphics2D g2d = (Graphics2D)g;
    







    if ((isSelected) && (!drawSplit))
    {
      Color currColor = g.getColor();
      float[] comps = currColor.getComponents(null);
      currColor = new Color(comps[0], comps[1], comps[2], 1.0F - ocrIF.TRANSPARENCY);
      if (color == null) {
        g.setColor(currColor);
      } else {
        g.setColor(color);
      }
    }
    


    if (!OCRInterface.this_interface.isHideBoxes()) {
      g.drawRect((int)(lt.x * scale), (int)(lt.y * scale), 
        (int)((rb.x - lt.x) * scale), (int)((rb.y - lt.y) * scale));
    }
    if (isSelected)
    {





      AlphaComposite comp = AlphaComposite.getInstance(
        3, 0.45F);
      g2d.setComposite(comp);
      g2d.fillRect((int)(get_lt_x() * scale), 
        (int)(get_lt_y() * scale), (int)(get_width() * scale), 
        (int)(get_height() * scale));
      g2d.setComposite(AlphaComposite.getInstance(
        3, 1.0F));
      


      Point[] vertex = getZoneCorners();
      for (Point pt : vertex)
      {
        int radius = 2;
        g.fillOval((int)(x * scale) - radius, (int)(y * scale) - radius, radius * 2, radius * 2);
      }
    }
    

    drawRLE(g, scale);
    


    if ((p != null) && (isSelected))
    {

      int radius = 4;
      Point closestPt = null;
      
      closestPt = closeTo(p, scale);
      
      if (closestPt != null)
      {
        g.fillOval((int)(x * scale) - radius, 
          (int)(y * scale) - radius, 
          radius * 2, radius * 2);
      }
    }
    


    if ((selPt != null) && (isSelected)) {
      int cSize = 4;
      
      g.fillRect((int)(selPt.x * scale) - cSize, 
        (int)(selPt.y * scale) - cSize, 
        cSize * 2, cSize * 2);
    }
    











    switch (selectedCorner) {
    case -1: 
      break;
    case 0: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 1: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 2: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      
      break;
    
    case 3: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
    }
    
    

    if (drawSplit) {
      g.drawLine((int)(splitLinePt1.x * scale), 
        (int)(splitLinePt1.y * scale), 
        (int)(splitLinePt2.x * scale), 
        (int)(splitLinePt2.y * scale));
    }
    


    String zoneName = getAttributeValue("gedi_type");
    if ((!isIncomplete) && (ocrIF.getShowZoneTypes()) && (zoneName != null))
    {

      int size = currentHWObjcurr_canvas.getElectronicTextSize();
      
      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), 1, size);
      g2d.setFont(updatedFont);
      
      Color currentColor = g2d.getColor();
      
      int widthOfText = g2d.getFontMetrics().stringWidth(zoneName);
      



      float x = (lt.x + width / 2 - widthOfText / scale / 2.0F) * scale;
      float y = (lt.y + height / 2 + size / 2) * scale;
      
      g2d.drawString(zoneName, x, y);
      g2d.setColor(currentColor);
      g2d.setFont(currFont);
    }
    if (((hasContents()) && (!offsetsReady()) && (!isIncomplete)) || (
      (hasContents()) && (offsetsReady()) && 
      (getAttributeValue("offsets").isEmpty()) && 
      (!isIncomplete) && (!isCtrlDown))) {
      String contents = getContents();
      BidiString bs = new BidiString(contents, 0);
      int direction = bs.getDirection();
      


      Font currFont = g2d.getFont();
      Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), electronicTextSize);
      g2d.setFont(updatedFont);
      int x;
      int x;
      if (direction == 0) {
        x = (int)(lt.x * scale);
      } else {
        x = (int)(rb.x * scale - g2d.getFontMetrics().stringWidth(contents));
      }
      int y = (int)(lt.y * scale - g2d.getFontMetrics().getHeight());
      
      g2d.setFont(currFont);
      
      displayContents(g, scale, isSelected, electronicTextSize, x, y);
    }
    else if ((offsetsReady()) && (!isIncomplete))
    {
      String contents = getContents();
      
      int tempint = ((Integer)OCRInterface.lineGTModeMap.get(getAttributeValue("segmentation").trim())).intValue();
      
      if (getAttributeValue("segmentation").equals("line")) {
        isHoriz = 4;
      }
      else
        isHoriz = 0;
      BidiString bs = new BidiString(contents, tempint);
      int direction = bs.getDirection();
      
      int maxOffsets = bs.size();
      

      String firstLine = null;
      if (direction == 0) {
        firstLine = "0";
      } else {
        firstLine = String.valueOf(rb.x - lt.x);
      }
      
      ArrayList<String> offsetsA = getOffsetsArray(firstLine, direction, maxOffsets);
      



      if ((isCtrlDown) || 
        (OCRInterface.currOppmode == 10)) {
        if (x < lt.x * scale) {
          x = ((int)(lt.x * scale));
        } else if (x > rb.x * scale) {
          x = ((int)(rb.x * scale));
        }
        if (offsetsA.size() < maxOffsets) {
          if (isHoriz != 4) {
            g.drawLine(x, (int)(lt.y * scale), x, 
              (int)(rb.y * scale));
          }
          else {
            g.drawLine((int)(lt.x * scale), y, 
              (int)(rb.x * scale), y);
          }
          
        }
        else if (isHoriz != 4) {
          g.drawLine(x, (int)(lt.y * scale), x, 
            (int)(rb.y * scale));
        }
      }
      






      if (offsetsA.size() > maxOffsets) {
        System.out.println("zoneid/offsetsA.size():" + getZoneId() + 
          "/" + offsetsA.size());
        
        String offsets = new String();
        
        if ((maxOffsets == 0) || (maxOffsets == 1)) {
          offsets = "";
        } else {
          offsetsA.remove(firstLine);
          Iterator<String> it = offsetsA.iterator();
          int i = 0;
          if (direction == 1) {
            for (int j = 0; j < offsetsA.size() - (maxOffsets - 1); j++) {
              it.next();
            }
          }
          while (i < maxOffsets - 1) {
            offsets = offsets + (String)it.next() + ", ";
            i++;
          }
          if (!offsets.equals("")) {
            offsets = offsets.substring(0, offsets.length() - 2);
          }
        }
        setAttributeValue("offsets", offsets);
        this_interfacetbdPane.data_panel.a_window
          .showZoneInfo(ImageDisplay.getActiveZones());
        
        offsetsA = getOffsetsArray(firstLine, direction, maxOffsets);
      }
      



      int thisLtX = lt.x;
      
      ArrayList<String> newOffsetsA = new ArrayList(offsetsA);
      


      if (maxOffsets >= 0) {
        int index = 0;
        for (int i = 0; i < offsetsA.size(); i++)
        {
          int offset;
          
          if (direction == 0) {
            int offset = Integer.valueOf((String)offsetsA.get(i)).intValue();
            
            if ((leftResize) && (i != 0))
            {
              offset += delta;
              if (offset <= 0)
                offset = 1;
              newOffsetsA.set(i, Integer.toString(offset));
            }
          }
          else {
            offset = Integer.valueOf((String)offsetsA.get(offsetsA.size() - 1 - i)).intValue();
            
            if ((leftResize) && (i != 0))
            {
              offset += delta;
              if (offset <= 0)
                offset = 1;
              newOffsetsA.set(offsetsA.size() - 1 - i, Integer.toString(offset));
            }
          }
          








          String txt = bs.getNext(offsetsA.size() - (i + 1));
          
          index += txt.length() + 1;
          
          Font currFont = g2d.getFont();
          
          Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), electronicTextSize);
          g2d.setFont(updatedFont);
          
          Color currentColor = g2d.getColor();
          int widthOfText = g2d.getFontMetrics().stringWidth(txt);
          int heightOfText = g2d.getFontMetrics().getHeight();
          
          int wordIndex = bs.getIndex();
          AttributedCharacterIterator text = correctPairedPunctuation_and_highlightSelectedWord(bs, 
            txt, updatedFont, index, wordIndex);
          
          float x;
          float x;
          if (direction == 0) {
            x = (thisLtX + offset) * scale;
          } else {
            x = (thisLtX + offset) * scale - widthOfText;
          }
          float y = lt.y * scale - heightOfText;
          









          if ((!ocrIF.getShowTextOnAllZones()) && (isSelected))
          {
            g2d.fillRect((int)x - 1, (int)(y - 2.0F), widthOfText + 2, heightOfText);
            
            if (isLightColor(currentColor)) {
              g2d.setColor(Color.black);
            } else {
              g2d.setColor(Color.white);
            }
            

            if (txt.trim().isEmpty()) {
              g2d.drawString(txt, x, y + heightOfText - 8.0F);
            } else {
              g2d.drawString(text, x, y + heightOfText - 8.0F);
            }
            

          }
          else if (ocrIF.getShowTextOnAllZones())
          {
            g2d.fillRect((int)x - 1, (int)(y - 2.0F), widthOfText + 2, heightOfText);
            
            if (isLightColor(currentColor)) {
              g2d.setColor(Color.black);
            } else {
              g2d.setColor(Color.white);
            }
            
            if (txt.trim().isEmpty()) {
              g2d.drawString(txt, x, y + heightOfText - 8.0F);
            } else {
              g2d.drawString(text, x, y + heightOfText - 8.0F);
            }
          }
          
          g2d.setColor(currentColor);
          


          if ((isSelected) && (index > contents.length() - caret) && (index - txt.length() - 1 <= contents.length() - caret)) {
            int currCaret = caret;
            caret = (index - contents.length() + caret - 1);
            drawCaret(g2d, (int)((thisLtX + offset) * scale), (int)(lt.y * scale), txt);
            caret = currCaret;
          }
          

          if (txt.equals("∅")) {
            Color c = g2d.getColor();
            g2d.setColor(c);
            int start = i;
            int end = 0;
            int xx = 0;int yy = 0;int ww = 0;int hh = 0;
            if (direction == 0) {
              end = i + 1;
              if (end >= offsetsA.size()) {
                xx = get_lt_x() + Integer.parseInt((String)offsetsA.get(start));
                ww = get_width() - Integer.parseInt((String)offsetsA.get(start));
              }
              else {
                xx = get_lt_x() + Integer.parseInt((String)offsetsA.get(start));
                ww = Integer.parseInt((String)offsetsA.get(end)) - Integer.parseInt((String)offsetsA.get(start));
              }
            }
            else {
              start = offsetsA.size() - (i + 1) - 1;
              end = offsetsA.size() - (i + 1);
              if (start < 0) {
                xx = get_lt_x();
                ww = Integer.parseInt((String)offsetsA.get(end));
              }
              else {
                xx = get_lt_x() + Integer.parseInt((String)offsetsA.get(start));
                ww = Integer.parseInt((String)offsetsA.get(end)) - Integer.parseInt((String)offsetsA.get(start));
              }
            }
            yy = get_lt_y();
            hh = get_height();
            
            drawInsertedToken(g2d, (int)(xx * scale), (int)(yy * scale), (int)(ww * scale), (int)(hh * scale));
            
            g2d.setColor(c);
          }
          
          g2d.setFont(currFont);
          


          if (lineSelected == offset) {
            g2d.setStroke(new BasicStroke(ocrIF.getLineThickness(), 0, 
              2));
            if (isHoriz != 4) {
              g2d.drawLine(
                (int)((thisLtX + offset) * scale), (int)(lt.y * scale), 
                (int)((thisLtX + offset) * scale), (int)(rb.y * scale));
            } else {
              g2d.drawLine(
                (int)(thisLtX * scale), (int)((lt.y + offset) * scale), 
                (int)(rb.x * scale), (int)((lt.y + offset) * scale));
            }
            g2d.setStroke(new BasicStroke(ocrIF.getLineThickness()));
          }
          else if (isHoriz != 4) {
            g2d.drawLine((int)((thisLtX + offset) * scale), 
              (int)(lt.y * scale), (int)((thisLtX + offset) * scale), 
              (int)(rb.y * scale));
          } else {
            g2d.drawLine(
              (int)(thisLtX * scale), (int)(lt.y * scale), 
              (int)(rb.x * scale), (int)(lt.y * scale));
          }
          

          if (leftResize) {
            setOffsets(newOffsetsA, direction);
          }
        }
        












        if (leftResize)
        {
          delta = 0;
          leftResize = false;
        }
      }
    }
  }
  























































  private void drawInsertedToken(Graphics2D g2d, int x0, int y0, int w, int h)
  {
    Color currColor = g2d.getColor();
    float[] comps = currColor.getComponents(null);
    currColor = new Color(comps[0], comps[1], comps[2], ocrIF.TRANSPARENCY);
    g2d.setColor(currColor);
    
    Color c = g2d.getColor();
    

    g2d.fillRect(x0, y0, w, h);
    g2d.setColor(c);
  }
  
  public void drawRLE(Graphics g, float scale) {
    if ((hasAttribute("RLEIMAGE")) && (OCRInterface.this_interface.getCanvas().getScale() <= 4.0F)) {
      Map<String, BufferedImage> rleMap = OCRInterface.this_interface.getCanvas().getRLEMap();
      String id = getAttributeValue("id");
      
      if (!rleMap.containsKey(id)) {
        BufferedImage temp = ImageAnalyzer.runLengthDecode(this, scale);
        OCRInterface.this_interface.getCanvas().getRLEMap().put(id, temp);
      }
      
      BufferedImage rleMask = (BufferedImage)rleMap.get(id);
      
      String rle = getAttributeValue("RLEIMAGE");
      
      if ((rle != null) && (!rle.trim().isEmpty()) && (!rle.equals("0"))) {
        int startIndex = rle.indexOf(";");
        
        String start_info = rle.substring(0, startIndex);
        start_info = start_info.replaceAll("\\(", "");
        start_info = start_info.replaceAll("\\)", "");
        
        String[] coords = start_info.split(",");
        
        int x = Integer.parseInt(coords[0].trim());
        int y = Integer.parseInt(coords[1].trim());
        g.drawImage(rleMask, (int)(x * scale), (int)(y * scale), null);
      }
    }
  }
  





  protected AttributedCharacterIterator xx(BidiString bs, String text, Font font)
  {
    AttributedString string = new AttributedString(text);
    string.addAttribute(TextAttribute.FONT, font);
    
    int direction = bs.getDirection();
    if (direction == 1) {
      string.addAttribute(TextAttribute.RUN_DIRECTION, 
        TextAttribute.RUN_DIRECTION_RTL);
    } else {
      string.addAttribute(TextAttribute.RUN_DIRECTION, 
        TextAttribute.RUN_DIRECTION_LTR);
    }
    

    int index = text.indexOf("Java");
    
    if (index == -1) {
      return string.getIterator();
    }
    


    string.addAttribute(TextAttribute.FOREGROUND, new Color(243, 63, 
      163), index, index + 4);
    
    AttributedCharacterIterator textIter = string.getIterator();
    
    return textIter;
  }
  























  protected AttributedCharacterIterator correctPairedPunctuation_and_highlightSelectedWord(BidiString bs, String text, Font font, int currIndex, int wordIndex)
  {
    String contentOfPrevZone = null;
    String contentOfNextZone = null;
    boolean prevIsLeftToRight = true;
    boolean nextIsLeftToRight = true;
    
    Map<TextAttribute, NumericShaper> map = new HashMap();
    map.put(TextAttribute.NUMERIC_SHAPING, NumericShaper.getContextualShaper(2));
    map.put(TextAttribute.BIDI_EMBEDDING, NumericShaper.getContextualShaper(4));
    



    AttributedString string = new AttributedString(text);
    



    Color zoneColor = getZoneColor();
    
    Color newBackgroundColor = new Color(255 - zoneColor.getRed(), 
      255 - zoneColor.getGreen(), 
      255 - zoneColor.getBlue());
    
    Color newForegroundColor = null;
    




    if ((zoneColor.equals(Color.black)) || (
      (zoneColor.getGreen() == zoneColor.getBlue()) && (zoneColor.getGreen() == zoneColor.getRed())))
    {
      newForegroundColor = Color.black;
      newBackgroundColor = Color.pink;
    }
    else if (isLightColor(newBackgroundColor)) {
      newForegroundColor = Color.black;
    } else {
      newForegroundColor = Color.white;
    }
    if (selectedWord != null) {
      int startIndex = selectedWord.getStartPos();
      int endIndex = selectedWord.getEndPos();
      
      if (text.split("\\s+").length == 1)
      {




        if ((currIndex - 1 == endIndex) && (currIndex - 1 - text.length() == startIndex))
        {
          startIndex = 0;
          endIndex = text.length();
          string.addAttribute(TextAttribute.BACKGROUND, newBackgroundColor, 
            startIndex, endIndex);
          string.addAttribute(TextAttribute.FOREGROUND, newForegroundColor, 
            startIndex, endIndex);


        }
        


      }
      else
      {


        string.addAttribute(TextAttribute.BACKGROUND, newBackgroundColor, 
          startIndex, endIndex);
        string.addAttribute(TextAttribute.FOREGROUND, newForegroundColor, 
          startIndex, endIndex);
      }
    }
    

    if ((!text.equals(")")) && 
      (!text.equals("(")) && 
      (!text.equals("]")) && 
      (!text.equals("[")) && 
      (!text.equals("}")) && 
      (!text.equals("{")) && 
      (!text.equals("“")) && 
      (!text.equals("”")) && 
      (!text.equals("»")) && 
      (!text.equals("«")))
    {

      try
      {

        string.addAttribute(TextAttribute.FONT, font);
        int direction = bs.getDirection();
        
        if (direction == 1) {
          string.addAttribute(TextAttribute.RUN_DIRECTION, 
            TextAttribute.RUN_DIRECTION_RTL);
        } else
          string.addAttribute(TextAttribute.RUN_DIRECTION, 
            TextAttribute.RUN_DIRECTION_LTR);
      } catch (IllegalArgumentException e) {
        string = new AttributedString(text);
      }
      



      AttributedCharacterIterator textIter = string.getIterator();
      
      return textIter;
    }
    






    if (bs.size() > 1) {
      int direction = bs.getDirection();
      
      if (direction == 1) {
        string.addAttribute(TextAttribute.RUN_DIRECTION, 
          TextAttribute.RUN_DIRECTION_RTL);
      } else {
        string.addAttribute(TextAttribute.RUN_DIRECTION, 
          TextAttribute.RUN_DIRECTION_LTR);



      }
      



    }
    else if (bs.size() == 1) {
      if (getPreviousZone() != null) {
        contentOfPrevZone = getPreviousZone().getContents();
      }
      if (getNextZone() != null) {
        contentOfNextZone = getNextZone().getContents();
      }
      
      if ((getPreviousZone() == null) && (getNextZone() == null)) {
        DLZone nextZone = OCRInterface.this_interface.nextTabZone(this);
        DLZone prevZone = OCRInterface.this_interface.prevTabZone(this);
        contentOfNextZone = nextZone.getContents();
        contentOfPrevZone = prevZone.getContents();
      }
      
      if ((contentOfPrevZone != null) && (!contentOfPrevZone.isEmpty())) {
        AttributedString neighborString = new AttributedString(contentOfPrevZone, map);
        AttributedCharacterIterator iter = neighborString.getIterator();
        

        BidiString bd = new BidiString(contentOfPrevZone, 3);
        prevIsLeftToRight = bd.getDirection() == 0;
      }
      









      if ((contentOfNextZone != null) && (!contentOfNextZone.isEmpty())) {
        AttributedString neighborString = new AttributedString(contentOfNextZone, map);
        AttributedCharacterIterator iter = neighborString.getIterator();
        

        BidiString bd = new BidiString(contentOfNextZone, 3);
        nextIsLeftToRight = bd.getDirection() == 0;
      }
      










      if ((prevIsLeftToRight) && (nextIsLeftToRight)) {
        string.addAttribute(TextAttribute.RUN_DIRECTION, 
          TextAttribute.RUN_DIRECTION_LTR);
      } else {
        string.addAttribute(TextAttribute.RUN_DIRECTION, 
          TextAttribute.RUN_DIRECTION_RTL);
      }
    }
    

    string.addAttribute(TextAttribute.FONT, font);
    
    AttributedCharacterIterator textIter = string.getIterator();
    
    return textIter;
  }
  




  public void displayContents(Graphics g, float scale, boolean isSelected, int eTextSize, int x, int y)
  {
    String contents = getContents();
    
    if ((contents.length() == 0) || (currentHWObjcurr_canvas.zoneCompletelyOffScreen(this))) {
      return;
    }
    



    Graphics2D g2d = (Graphics2D)g;
    
    Font currFont = g2d.getFont();
    Font updatedFont = new Font(currFont.getName(), currFont.getStyle(), eTextSize);
    g2d.setFont(updatedFont);
    
    Color currentColor = g2d.getColor();
    int widthOfText = g2d.getFontMetrics().stringWidth(contents);
    int heightOfText = g2d.getFontMetrics().getHeight();
    
    JViewport viewport = OCRInterface.currentHWObj.getPictureScrollPane().getViewport();
    
    if (isSelected) {
      y = Math.max(Math.min(getViewPositiony + viewport.getHeight(), y + heightOfText), getViewPositiony + heightOfText + 2) - heightOfText;
    }
    int direction = new BidiString(contents, 3).getDirection();
    
    Rectangle bounds = get_Bounds();
    if (direction == 0) {
      x = (int)Math.max(Math.min(x * scale + width * scale, x + widthOfText + 2), x * scale + widthOfText + 3.0F) - widthOfText - 2;
    } else {
      x = (int)Math.min(Math.max(x + widthOfText + 2, x * scale + widthOfText + 3.0F), x * scale + width * scale) - widthOfText - 2;
    }
    

    if ((!ocrIF.getShowTextOnAllZones()) && (isSelected)) {
      g2d.fillRect(x - 1, y - 2, widthOfText + 2, heightOfText);
      
      if (isLightColor(currentColor)) {
        g2d.setColor(Color.black);
      } else {
        g2d.setColor(Color.white);
      }
      
      AttributedCharacterIterator text = correctPairedPunctuation_and_highlightSelectedWord(new BidiString(contents, 0), 
        contents, updatedFont, 0, -1);
      


      g2d.drawString(text, x, y + heightOfText - 8);

    }
    else if (ocrIF.getShowTextOnAllZones()) {
      g2d.fillRect(x - 1, y - 2, widthOfText + 2, heightOfText);
      
      if (isLightColor(currentColor)) {
        g2d.setColor(Color.black);
      } else {
        g2d.setColor(Color.white);
      }
      
      AttributedCharacterIterator text = correctPairedPunctuation_and_highlightSelectedWord(new BidiString(contents, 0), 
        contents, updatedFont, 0, -1);
      


      g2d.drawString(text, x, y + heightOfText - 8);
    }
    

    g2d.setColor(currentColor);
    
    if (direction == 1) {
      x += g2d.getFontMetrics().stringWidth(contents);
    }
    
    if (isSelected) {
      drawCaret(g2d, x, y + heightOfText, contents);
    }
    g2d.setFont(currFont);
  }
  
  public void drawCaret(Graphics2D g2d, int x, int y, String contents) {
    if (!ImageDisplay.showCaret) {
      return;
    }
    Color currentColor = g2d.getColor();
    int direction = new BidiString(contents, 1).getDirection();
    
    if (caret > contents.length()) {
      caret = 0;
    }
    
    if (isLightColor(currentColor)) {
      g2d.setColor(Color.black);
    } else {
      g2d.setColor(Color.white);
    }
    int xCaret = x;
    
    int stringWidth;
    
    int stringWidth;
    
    if (caret > 0) {
      stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(0, contents.length() - caret + 1)) - 
        OCRInterface.this_interface.getCanvas().getFontMetrics().charWidth(contents.charAt(contents.length() - caret));
    } else {
      stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(0, contents.length() - caret));
    }
    if (direction == 0) {
      xCaret += stringWidth;
    } else {
      xCaret -= stringWidth;
    }
    int heightCaret = (int)(g2d.getFontMetrics().getHeight() * 0.75D);
    int yCaret = (int)(y - g2d.getFontMetrics().getHeight() * 0.2D);
    g2d.drawLine(xCaret, yCaret, xCaret, yCaret - heightCaret);
    
    g2d.setColor(currentColor);
  }
  
  private void setOffsets(ArrayList<String> newOffsetsA, int direction) {
    String offsets = "";
    newOffsetsA.trimToSize();
    if (newOffsetsA.size() != 0)
    {
      if (direction == 0)
      {
        for (int i = 1; i < newOffsetsA.size(); i++)
        {
          offsets = offsets + (String)newOffsetsA.get(i) + ",";
        }
        
      }
      else {
        for (int i = newOffsetsA.size() - 2; i >= 0; i--)
        {
          offsets = offsets + (String)newOffsetsA.get(i) + ",";
        }
      }
    }
    
    String sortedOffsetsStr = sortOffsets(offsets);
    
    setAttributeValue("offsets", sortedOffsetsStr);
  }
  
  public static String sortOffsets(String offsetsStr) {
    String[] offsetArray = offsetsStr.split(",");
    ArrayList<Integer> offsetArrayList = new ArrayList();
    for (int i = 0; i < offsetArray.length; i++) {
      if (!offsetArray[i].trim().equals(""))
        offsetArrayList.add(Integer.valueOf(Integer.parseInt(offsetArray[i].trim())));
    }
    Collections.sort(offsetArrayList);
    
    String sorted = "";
    Iterator itr = offsetArrayList.iterator();
    while (itr.hasNext()) {
      String temp2 = ((Integer)itr.next()).toString();
      if (!temp2.equals("")) {
        sorted = sorted + "," + temp2.trim();
      }
    }
    if (sorted.length() > 1) {
      sorted = sorted.substring(1);
    }
    return sorted;
  }
  
  protected boolean isLightColor(Color color)
  {
    int border = 200;
    
    if (color.getGreen() > border) { return true;
    }
    return false;
  }
  
  public ArrayList<String> getOffsetsArray(String firstLine, int direction, int maxOffsets) {
    String offsets = getAttributeValue("offsets");
    if (offsets == null) {
      offsets = "";
    }
    offsets = offsets.replaceAll("[;\\s]", "");
    ArrayList<String> offsetsA = new ArrayList();
    
    String[] toffsetsA = (String[])null;
    
    if (offsets.length() == 0) {
      toffsetsA = new String[0];
    } else {
      toffsetsA = offsets.split(",");
    }
    
    for (int i = 0; i < toffsetsA.length; i++) {
      offsetsA.add(toffsetsA[i]);
    }
    
    if (maxOffsets > 0) {
      if (direction == 0) {
        offsetsA.add(0, firstLine);
      } else {
        offsetsA.add(firstLine);
      }
    }
    
    return offsetsA;
  }
  




















  public void draw(Graphics g, double x, double y, int imWidth, int imHeight, int rotate, float scale, boolean isSelected, boolean drawSplit)
  {
    int cornerSize = 3;
    



    int tx = 0;
    int ty = 0;
    int bx = 0;
    int by = 0;
    

    if (rotate == 0) {
      tx = (int)(lt.x * scale + x);
      ty = (int)(lt.y * scale + y);
      bx = (int)((rb.x - lt.x) * scale);
      by = (int)((rb.y - lt.y) * scale);
    } else if (rotate == 1) {
      tx = (int)(imWidth - rb.y * scale + x);
      ty = (int)(lt.x * scale + y);
      bx = (int)(imWidth - lt.y * scale - (imWidth - rb.y * scale));
      by = (int)(rb.x * scale - lt.x * scale);
    } else if (rotate == 2) {
      tx = (int)(imWidth - rb.x * scale + x);
      ty = (int)(imHeight - rb.y * scale + y);
      bx = (int)(imWidth - lt.x * scale - (imWidth - rb.x * scale));
      by = (int)(imHeight - lt.y * scale - (imHeight - rb.y * scale));
    } else if (rotate == 3) {
      tx = (int)(lt.y * scale + x);
      ty = (int)(imHeight - rb.x * scale + y);
      bx = (int)(rb.y * scale - lt.y * scale);
      by = (int)(imHeight - lt.x * scale - (imHeight - rb.x * scale));
    }
    
    g.drawRect(tx, ty, bx, by);
    





    if (isSelected)
    {

      Graphics2D g2d = (Graphics2D)g;
      
      AlphaComposite comp = AlphaComposite.getInstance(
        3, 0.45F);
      g2d.setComposite(comp);
      g2d.fillRect(tx, ty, bx, by);
      



      g2d.setComposite(AlphaComposite.getInstance(
        3, 1.0F));
    }
    











    switch (selectedCorner) {
    case -1: 
      break;
    case 0: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 1: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(lt.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      break;
    
    case 2: 
      g.drawRect((int)(lt.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
      
      break;
    
    case 3: 
      g.drawRect((int)(rb.x * scale) - cornerSize, (int)(rb.y * scale) - 
        cornerSize, cornerSize * 2, cornerSize * 2);
    }
    
    

    if (drawSplit) {
      g.drawLine((int)(splitLinePt1.x * scale), 
        (int)(splitLinePt1.y * scale), 
        (int)(splitLinePt2.x * scale), 
        (int)(splitLinePt2.y * scale));
    }
  }
  










  public void mergeWith(Zone zoneIn)
  {
    lt.x = Math.min(lt.x, zoneIn.get_lt_x());
    lt.y = Math.min(lt.y, zoneIn.get_lt_y());
    rb.x = Math.max(rb.x, zoneIn.get_rb_x());
    rb.y = Math.max(rb.y, zoneIn.get_rb_y());
  }
  











  public void createSplitLine(int xIn, int yIn, float scale)
  {
    if (!doesContain(xIn, yIn, scale)) {
      return;
    }
    Point scaledPtIn = new Point((int)(xIn / scale), (int)(yIn / scale));
    

    Double top_dist = new Double(new Line2D.Float(lt.x, lt.y, rb.x, lt.y)
      .ptLineDist(scaledPtIn));
    Double right_dist = new Double(
      new Line2D.Float(rb.x, lt.y, rb.x, rb.y)
      .ptLineDist(scaledPtIn));
    Double bot_dist = new Double(new Line2D.Float(lt.x, rb.y, rb.x, rb.y)
      .ptLineDist(scaledPtIn));
    Double left_dist = new Double(
      new Line2D.Float(lt.x, lt.y, lt.x, rb.y)
      .ptLineDist(scaledPtIn));
    

    Vector<Double> distances = new Vector();
    distances.add(top_dist);
    distances.add(right_dist);
    distances.add(left_dist);
    distances.add(bot_dist);
    
    Double smallest = (Double)Collections.min(distances);
    



    if (getHoriz() != 4) {
      if ((smallest.equals(top_dist)) || (smallest.equals(bot_dist)))
      {
        splitLinePt1.x = x;
        splitLinePt1.y = lt.y;
        splitLinePt2.x = x;
        splitLinePt2.y = rb.y;
      }
      else
      {
        splitLinePt1.x = lt.x;
        splitLinePt1.y = y;
        splitLinePt2.x = rb.x;
        splitLinePt2.y = y;
      }
    }
    else
    {
      splitLinePt1.x = lt.x;
      splitLinePt1.y = y;
      splitLinePt2.x = rb.x;
      splitLinePt2.y = y;
    }
  }
  

























  public int getSplitLoc()
  {
    if (splitLinePt1.x == splitLinePt2.x) {
      return splitLinePt1.x;
    }
    
    return splitLinePt1.y;
  }
  






  public boolean isSplitVertical()
  {
    if (splitLinePt1.x == splitLinePt2.x) {
      return true;
    }
    
    return false;
  }
  










  public int whichSide(int scaledSplit, boolean vertical)
  {
    if (vertical) {
      if (lt.x >= scaledSplit)
        return 1;
      if (rb.x <= scaledSplit) {
        return 0;
      }
      return 2;
    }
    


    if (lt.y >= scaledSplit)
      return 1;
    if (rb.y <= scaledSplit) {
      return 0;
    }
    return 2;
  }
  








  public Zone split()
  {
    Zone returnVal = clone_zone();
    
    if (splitLinePt1.x == splitLinePt2.x)
    {

      returnVal.set_lt_x(splitLinePt1.x);
      
      set_rb_x(splitLinePt1.x - 1);
      try {
        returnVal.dlSetZoneOrigin(lt.x, lt.y);
        dlSetZoneWidth(rb.x - lt.x);

      }
      catch (DLException localDLException) {}

    }
    else
    {

      returnVal.set_lt_y(splitLinePt1.y);
      
      set_rb_y(splitLinePt1.y - 1);
      try {
        returnVal.dlSetZoneOrigin(lt.x, lt.y);
        dlSetZoneHeight(rb.y - lt.y);
      }
      catch (DLException localDLException1) {}
    }
    

    return returnVal;
  }
  











  public void selectCorener(int xIn, int yIn, float scale)
  {
    selPt = null;
    
    Point scaledMouseIn = new Point((int)(xIn / scale), (int)(yIn / scale));
    Point mouseIn = new Point(xIn, yIn);
    Point cornerPt = closeTo(mouseIn, scale);
    

    Point lb = new Point(lt.x, rb.y);
    Point rt = new Point(rb.x, lt.y);
    

    Line2D.Double lt_rt = new Line2D.Double(lt, rt);
    Line2D.Double lb_rb = new Line2D.Double(lb, rb);
    Line2D.Double lt_lb = new Line2D.Double(lt, lb);
    Line2D.Double rt_rb = new Line2D.Double(rt, rb);
    

    Point top_left = new Point(get_lt_x(), get_lt_y());
    Point top_right = new Point(get_rb_x(), get_lt_y());
    Point bottom_left = new Point(get_lt_x(), get_rb_y());
    Point bottom_right = new Point(get_rb_x(), get_rb_y());
    

    int factor = 7;
    
    if ((cornerPt != null) && (cornerPt.equals(top_left))) {
      selectedCorner = 0;
    } else if ((cornerPt != null) && (cornerPt.equals(top_right))) {
      selectedCorner = 1;
    } else if ((cornerPt != null) && (cornerPt.equals(bottom_left))) {
      selectedCorner = 2;
    } else if ((cornerPt != null) && (cornerPt.equals(bottom_right))) {
      selectedCorner = 3;
    } else if ((lt_rt.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lt_rt.ptSegDist(scaledMouseIn) < factor)) {
      selectedCorner = 4;
    } else if ((lb_rb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lb_rb.ptSegDist(scaledMouseIn) < factor)) {
      selectedCorner = 5;
    } else if ((lt_lb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lt_lb.ptSegDist(scaledMouseIn) < factor)) {
      selectedCorner = 6;
    } else if ((rt_rb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (rt_rb.ptSegDist(scaledMouseIn) < factor)) {
      selectedCorner = 7;
    }
  }
  










  public void editSelectedCorener(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    DLZone child = dlFindTheCloseChildToPt(scaledXin, scaledYin);
    








    try
    {
      switch (selectedCorner) {
      case -1: 
        selPt = null;
        System.out.println("Error: corner unselected [Zone:565]");
        return;
      case 0: 
        if (scaledXin < 0) {
          set_lt_x(0);
        } else if ((parentZone != null) && 
          (scaledXin < parentZone.dlGetZoneOrigin().x)) {
          set_lt_x(parentZone.dlGetZoneOrigin().x);
        } else if ((dlHasChildZones()) && 
          (scaledXin > dlGetZoneOriginx)) {
          set_lt_x(dlGetZoneOriginx);
        }
        else {
          set_lt_x(scaledXin);
          leftResize = true;
        }
        
        if (scaledYin < 0) {
          set_lt_y(0);
        } else if ((parentZone != null) && 
          (scaledYin < parentZone.dlGetZoneOrigin().y)) {
          set_lt_y(parentZone.dlGetZoneOrigin().y);
        } else if ((dlHasChildZones()) && 
          (scaledYin > dlGetZoneOriginy)) {
          set_lt_y(dlGetZoneOriginy);
        } else {
          set_lt_y(scaledYin);
        }
        selPt = new Point(get_lt_x(), get_lt_y());
        break;
      
      case 1: 
        if (scaledXin < 0) {
          set_lt_x(0);
        } else if ((parentZone != null) && 
          (scaledXin > parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth())) {
          set_rb_x(parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth());
        } else if ((dlHasChildZones()) && 
          (scaledXin < dlGetZoneOriginx + child.dlGetZoneWidth()))
        {
          set_rb_x(dlGetZoneOriginx + child.dlGetZoneWidth());
        } else {
          set_rb_x(scaledXin);
        }
        if (scaledYin < 0) {
          set_lt_y(0);
        } else if ((parentZone != null) && 
          (scaledYin < parentZone.dlGetZoneOrigin().y)) {
          set_lt_y(parentZone.dlGetZoneOrigin().y);
        } else if ((dlHasChildZones()) && 
          (scaledYin > dlGetZoneOriginy)) {
          set_lt_y(dlGetZoneOriginy);
        } else {
          set_lt_y(scaledYin);
        }
        selPt = new Point(get_rb_x(), get_lt_y());
        break;
      case 2: 
        if (scaledXin < 0) {
          set_lt_x(0);
        } else if ((parentZone != null) && 
          (scaledXin < parentZone.dlGetZoneOrigin().x)) {
          set_lt_x(parentZone.dlGetZoneOrigin().x);
        } else if ((dlHasChildZones()) && 
          (scaledXin > dlGetZoneOriginx)) {
          set_lt_x(dlGetZoneOriginx);
        }
        else
        {
          set_lt_x(scaledXin);
          leftResize = true;
        }
        
        if (scaledYin > dlGetPageHeight()) {
          set_rb_y(dlGetPageHeight());
        } else { if (parentZone != null)
          {
            if (scaledYin > parentZone.dlGetZoneOrigin().y + parentZone.dlGetZoneHeight()) {
              set_rb_y(parentZone.dlGetZoneOrigin().y + 
                parentZone.dlGetZoneHeight());
              break label770; } } if (dlHasChildZones())
          {
            if (scaledYin < dlGetZoneOriginy + child.dlGetZoneHeight()) {
              set_rb_y(dlGetZoneOriginy + 
                child.dlGetZoneHeight());
              break label770; } }
          set_rb_y(scaledYin);
        }
        selPt = new Point(get_lt_x(), get_rb_y());
        break;
      case 3: 
        if (scaledXin > dlGetPageWidth()) {
          set_rb_x(dlGetPageWidth());
        } else { if (parentZone != null)
          {
            if (scaledXin > parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth()) {
              set_rb_x(parentZone.dlGetZoneOrigin().x + 
                parentZone.dlGetZoneWidth());
              break label920; } } if (dlHasChildZones())
          {
            if (scaledXin < dlGetZoneOriginx + child.dlGetZoneWidth()) {
              set_rb_x(dlGetZoneOriginx + child.dlGetZoneWidth());
              break label920; } }
          set_rb_x(scaledXin);
        }
        if (scaledYin > dlGetPageHeight()) {
          set_rb_y(dlGetPageHeight());
        } else { if (parentZone != null)
          {
            if (scaledYin > parentZone.dlGetZoneOrigin().y + parentZone.dlGetZoneHeight()) {
              set_rb_y(parentZone.dlGetZoneOrigin().y + 
                parentZone.dlGetZoneHeight());
              break label1048; } } if (dlHasChildZones())
          {
            if (scaledYin < dlGetZoneOriginy + child.dlGetZoneHeight()) {
              set_rb_y(dlGetZoneOriginy + 
                child.dlGetZoneHeight());
              break label1048; } }
          set_rb_y(scaledYin);
        }
        selPt = new Point(get_rb_x(), get_rb_y());
        break;
      

      case 4: 
        if (scaledYin < 0) {
          set_lt_y(0);
        } else if ((parentZone != null) && 
          (scaledYin < parentZone.dlGetZoneOrigin().y)) {
          set_lt_y(parentZone.dlGetZoneOrigin().y);
        } else if ((dlHasChildZones()) && 
          (scaledYin > dlGetZoneOriginy)) {
          set_lt_y(dlGetZoneOriginy);
        } else {
          set_lt_y(scaledYin);
        }
        selPt = null;
        
        break;
      

      case 5: 
        if (scaledYin > dlGetPageHeight()) {
          set_rb_y(dlGetPageHeight());
        } else if ((parentZone != null) && 
          (scaledYin > parentZone.dlGetZoneOrigin().y + parentZone.dlGetZoneHeight())) {
          set_rb_y(parentZone.dlGetZoneOrigin().y + parentZone.dlGetZoneHeight());
        } else if ((dlHasChildZones()) && 
          (scaledYin < dlGetZoneOriginy + child.dlGetZoneHeight())) {
          set_rb_y(dlGetZoneOriginy + child.dlGetZoneHeight());
        } else {
          set_rb_y(scaledYin);
        }
        selPt = null;
        
        break;
      
      case 6: 
        if (scaledXin < 0) {
          set_lt_x(0);
        } else if ((parentZone != null) && 
          (scaledXin < parentZone.dlGetZoneOrigin().x)) {
          set_lt_x(parentZone.dlGetZoneOrigin().x);
        } else if ((dlHasChildZones()) && 
          (scaledXin > dlGetZoneOriginx)) {
          set_lt_x(dlGetZoneOriginx);
        }
        else {
          set_lt_x(scaledXin);
          leftResize = true;
        }
        
        selPt = null;
        break; case 7:  label770:
        label920:
        label1048:
        if (scaledXin < 0) {
          set_rb_x(0);
        } else if ((parentZone != null) && 
          (scaledXin > parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth())) {
          set_rb_x(parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth());
        } else if ((dlHasChildZones()) && 
          (scaledXin < dlGetZoneOriginx + child.dlGetZoneWidth())) {
          set_rb_x(dlGetZoneOriginx + child.dlGetZoneWidth());
        } else {
          set_rb_x(scaledXin);
        }
        selPt = null;
      }
      
      dlEditZoneBoundaries(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
    }
    catch (DLException localDLException) {}
    




    super.resizeGroupToWhichZoneBelongs();
  }
  











  public void setMoveStart(int xIn, int yIn, float scale)
  {
    selPt = null;
    Rectangle bounds = get_Bounds();
    moveOffsetX = ((int)(xIn / scale) - x);
    moveOffsetY = ((int)(yIn / scale) - y);
    


    Iterator itr = childZones.iterator();
    while (itr.hasNext())
      ((Zone)itr.next()).setMoveStart(xIn, yIn, scale);
  }
  
  public Point checkMoveLimit(int newX, int newY) {
    Rectangle bounds = get_Bounds();
    int width = width;
    int height = height;
    newX -= moveOffsetX;
    newY -= moveOffsetY;
    
    if (parentZone != null) {
      newX = Math.max(Math.min(newX, 
        parentZone.dlGetZoneOrigin().x + parentZone.dlGetZoneWidth() - width), 
        parentZone.dlGetZoneOrigin().x);
      newY = Math.max(Math.min(newY, 
        parentZone.dlGetZoneOrigin().y + parentZone.dlGetZoneHeight() - height), 
        parentZone.dlGetZoneOrigin().y);
    } else {
      newX = Math.max(Math.min(newX, zonePage.dlGetWidth() - width), 0);
      newY = Math.max(Math.min(newY, zonePage.dlGetHeight() - height), 0);
    }
    
    return new Point(newX + moveOffsetX, newY + moveOffsetY);
  }
  
  public void rotate(int pageWidth) {
    int width = rb.x - lt.x;
    int height = rb.y - lt.y;
    

    int temp = lt.y;
    lt.y = lt.x;
    lt.x = (pageWidth - temp - height);
    
    rb.x = (lt.x + height);
    rb.y = (lt.y + width);
    try
    {
      dlSetZoneOrigin(lt.x, lt.y);
      dlSetZoneWidth(height);
      dlSetZoneHeight(width);
    } catch (DLException e) {
      e.printStackTrace();
    }
  }
  
  public Point dlGetZoneOrigin() {
    return lt;
  }
  






  public void moveTo(int newX, int newY, boolean moveByMouse)
  {
    Iterator itr = childZones.iterator();
    while (itr.hasNext()) {
      ((Zone)itr.next()).moveTo(newX, newY, moveByMouse);
    }
    if (!moveByMouse) {
      moveOffsetX = 0;
      moveOffsetY = 0;
    }
    
    newX -= moveOffsetX;
    newY -= moveOffsetY;
    try
    {
      dlSetZoneOrigin(newX, newY);
      lt.x = newX;
      lt.y = newY;
      rb.x = (lt.x + width);
      rb.y = (lt.y + height);
    } catch (DLException e) {
      e.printStackTrace();
    }
    
    super.resizeGroupToWhichZoneBelongs();
  }
  












  public boolean doesContain(int xIn, int yIn, float scale)
  {
    return get_Bounds().contains((int)(xIn / scale), (int)(yIn / scale));
  }
  










  public boolean clickedText(int x, int y, float scale)
  {
    String contents = getContents();
    if (contents == null) {
      return false;
    }
    contents = contents.trim();
    if ((contents.length() == 0) || ((!ImageDisplay.activeZones.contains(this)) && (!OCRInterface.this_interface.getShowTextOnAllZones()))) {
      return false;
    }
    
    int widthOfText = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents);
    int heightOfText = OCRInterface.this_interface.getCanvas().getFontMetrics().getHeight();
    


    if ((!offsetsReady()) || ((this instanceof PolygonZone)) || ((this instanceof OrientedBox))) {
      BidiString bs = new BidiString(contents, 1);
      int direction = bs.getDirection();
      
      int ptY;
      
      int ptY;
      if ((this instanceof PolygonZone)) {
        PolygonZone temp = (PolygonZone)this;
        
        Point textVertex = OCRInterface.this_interface.getUseStartPoint() ? 
          new Point(currPoly.xpoints[0], currPoly.ypoints[0]) : 
          PolygonZone.getHighestVertex(currPoly);
        int ptX;
        int ptX; if (direction == 0) {
          ptX = x;
        } else {
          ptX = x - widthOfText;
        }
        ptY = y - heightOfText; } else { int ptY;
        if ((this instanceof OrientedBox)) {
          OrientedBox temp = (OrientedBox)this;
          int ptX;
          int ptX; if (direction == 0) {
            ptX = (int)(getHighestVertexgetPolygonx * scale);
          } else {
            ptX = (int)(getHighestVertexgetPolygonx * scale) - widthOfText;
          }
          ptY = (int)(getHighestVertexgetPolygony * scale) - heightOfText;
        } else { int ptX;
          if (direction == 0) {
            ptX = (int)(lt.x * scale);
          } else {
            ptX = (int)(lt.x * scale - widthOfText);
          }
          ptY = (int)(lt.y * scale - heightOfText);
        }
      }
      




      Rectangle bounds = get_Bounds();
      int ptX = (int)Math.max(Math.min(x * scale + width * scale, ptX + widthOfText + 2), x * scale + widthOfText + 3.0F) - widthOfText - 2;
      


      boolean ret = (x > ptX) && (x < ptX + OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents)) && 
        (y > ptY - 2) && (y < ptY + OCRInterface.this_interface.getCanvas().getFontMetrics().getHeight() - 2);
      

      if (ret) {
        int temp = ptX;
        
        if (direction == 0) {
          temp = ptX;
        } else {
          temp = ptX + OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents);
        }
        int dist = temp - x;
        for (int j = 0; j < contents.length(); j++) { int stringWidth;
          int stringWidth;
          if (j + 1 < contents.length()) {
            stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(0, j + 2)) - 
              OCRInterface.this_interface.getCanvas().getFontMetrics().charWidth(contents.charAt(j + 1));
          } else {
            stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents.substring(0, j + 1));
          }
          if (direction == 0) {
            temp = ptX + stringWidth;
          } else {
            temp = ptX + OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(contents) - stringWidth;
          }
          
          if ((Math.abs(temp - x) > Math.abs(dist)) && (dist < OCRInterface.this_interface.getCanvas().getFontMetrics().getMaxAdvance() / 2)) {
            caret = (contents.length() - j);
            return true;
          }
          dist = temp - x;
        }
        caret = 0;
        return true;
      }
    } else {
      int tempint = ((Integer)OCRInterface.lineGTModeMap.get(getAttributeValue("segmentation").trim())).intValue();
      BidiString bs = new BidiString(contents, tempint);
      int direction = bs.getDirection();
      int maxOffsets = bs.size();
      
      String firstLine = null;
      if (direction == 0) {
        firstLine = "0";
      } else {
        firstLine = String.valueOf(rb.x - lt.x);
      }
      
      ArrayList<String> offsets = getOffsetsArray(firstLine, direction, maxOffsets);
      
      int index = 0;
      for (int i = 0; i < offsets.size(); i++) {
        int offset;
        int offset;
        if (direction == 0) {
          offset = Integer.valueOf((String)offsets.get(i)).intValue();
        } else {
          offset = Integer.valueOf((String)offsets.get(offsets.size() - 1 - i)).intValue();
        }
        
        String txt = bs.getNext(offsets.size() - (i + 1));
        

        boolean ret;
        
        if (direction == 0) {
          ret = (x > (lt.x + offset) * scale) && (x < (lt.x + offset) * scale + OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(txt));
        }
        else {
          ret = (x > (lt.x + offset) * scale - OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(txt)) && (x < (lt.x + offset) * scale);
        }
        
        boolean ret = (ret) && (y > lt.y * scale - OCRInterface.this_interface.getCanvas().getFontMetrics().getHeight() - 2.0F) && (y < lt.y * scale - 2.0F);
        if (ret) {
          int temp = (int)((lt.x + offset) * scale);
          int dist = temp - x;
          for (int j = 0; j < txt.length(); j++) { int stringWidth;
            int stringWidth;
            if (j + 1 < txt.length()) {
              stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(txt.substring(0, j + 2)) - 
                OCRInterface.this_interface.getCanvas().getFontMetrics().charWidth(txt.charAt(j + 1));
            } else {
              stringWidth = OCRInterface.this_interface.getCanvas().getFontMetrics().stringWidth(txt.substring(0, j + 1));
            }
            if (direction == 0) {
              temp = (int)((lt.x + offset) * scale + stringWidth);
            } else {
              temp = (int)((lt.x + offset) * scale - stringWidth);
            }
            
            if ((Math.abs(temp - x) > Math.abs(dist)) && (dist < OCRInterface.this_interface.getCanvas().getFontMetrics().getMaxAdvance() / 2)) {
              caret = (contents.length() - index - j);
              return true;
            }
            dist = temp - x;
          }
          caret = (contents.length() - index - txt.length());
          return true;
        }
        
        index += txt.length() + 1;
      }
    }
    
    return false;
  }
  












  public double getDistance(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    
    Vector<Double> distances = new Vector();
    distances.add(Double.valueOf(Line2D.ptSegDist(get_lt_x(), get_lt_y(), get_rb_x(), 
      get_lt_y(), scaledXin, scaledYin)));
    distances.add(Double.valueOf(Line2D.ptSegDist(get_lt_x(), get_rb_y(), get_rb_x(), 
      get_rb_y(), scaledXin, scaledYin)));
    distances.add(Double.valueOf(Line2D.ptSegDist(get_rb_x(), get_lt_y(), get_rb_x(), 
      get_rb_y(), scaledXin, scaledYin)));
    distances.add(Double.valueOf(Line2D.ptSegDist(get_lt_x(), get_lt_y(), get_lt_x(), 
      get_rb_y(), scaledXin, scaledYin)));
    return ((Double)Collections.min(distances)).doubleValue();
  }
  



























  public void creationDrag(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    



    if ((creationPt.x > scaledXin) && (creationPt.y > scaledYin)) {
      lt.x = scaledXin;
      lt.y = scaledYin;
      rb.x = creationPt.x;
      rb.y = creationPt.y;

    }
    else if ((creationPt.x <= scaledXin) && (creationPt.y > scaledYin)) {
      lt.x = creationPt.x;
      lt.y = scaledYin;
      rb.x = scaledXin;
      rb.y = creationPt.y;


    }
    else if ((creationPt.x <= scaledXin) && (creationPt.y <= scaledYin)) {
      lt.x = creationPt.x;
      lt.y = creationPt.y;
      rb.x = scaledXin;
      rb.y = scaledYin;


    }
    else if ((creationPt.x > scaledXin) && (creationPt.y <= scaledYin)) {
      lt.x = scaledXin;
      lt.y = creationPt.y;
      rb.x = creationPt.x;
      rb.y = scaledYin;
    }
    
    isIncomplete = false;
    try {
      dlSetZoneOrigin(lt.x, lt.y);
      dlSetZoneWidth(rb.x - lt.x);
      dlSetZoneHeight(rb.y - lt.y);
    }
    catch (DLException localDLException) {}
  }
  









  public String toString()
  {
    String returnVal = "[id=" + zoneID + " Type =" + getZoneType() + " ((" + 
      lt.x + "," + lt.y + ")(" + rb.x + "," + rb.y + ")) [parent =" + 
      parentZone + " ] ]";
    

    return returnVal;
  }
  
  public boolean intersects(Zone zIn)
  {
    Rectangle me = new Rectangle(lt, new Dimension(get_width(), 
      get_height()));
    Rectangle rectIn = new Rectangle(lt, new Dimension(zIn.get_width(), 
      zIn.get_height()));
    




    return rectIn.contains(me);
  }
  
  public boolean contains(Zone zIn) {
    Rectangle me = get_Bounds();
    Rectangle rectIn = zIn.get_Bounds();
    
    return me.contains(rectIn);
  }
  








  public void setSelectedCorner(int newSelectedCorner)
  {
    selectedCorner = newSelectedCorner;
  }
  






































































  public boolean doesLieOnBoundary(int xIn, int yIn, float scale)
  {
    int scaledXin = (int)(xIn / scale);
    int scaledYin = (int)(yIn / scale);
    Point scaledMouseIn = new Point(scaledXin, scaledYin);
    int factor = 7;
    

    Point lb = new Point(lt.x, rb.y);
    Point rt = new Point(rb.x, lt.y);
    

    Line2D.Double lt_rt = new Line2D.Double(lt, rt);
    Line2D.Double lb_rb = new Line2D.Double(lb, rb);
    Line2D.Double lt_lb = new Line2D.Double(lt, lb);
    Line2D.Double rt_rb = new Line2D.Double(rt, rb);
    if ((lt_rt.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lt_rt.ptSegDist(scaledMouseIn) < factor))
      return true;
    if ((lb_rb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lb_rb.ptSegDist(scaledMouseIn) < factor))
      return true;
    if ((lt_lb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (lt_lb.ptSegDist(scaledMouseIn) < factor))
      return true;
    if ((rt_rb.ptSegDist(scaledMouseIn) >= 0.0D) && 
      (rt_rb.ptSegDist(scaledMouseIn) < factor)) {
      return true;
    }
    return false;
  }
  




  public boolean isPartOfHierarchy()
  {
    return (parentZone != null) || (!childZones.isEmpty());
  }
  
  public void setZoneType(String newZonetypeId) {
    OCRInterface.this_interface.getCanvas().unlockSoftLock();
    
    setAttributeValue("gedi_type", newZonetypeId);
    
    if (newZonetypeId.equals("DL_TEXTLINEGT")) {
      changeToDL_TEXTLINEGT();
    }
  }
  
  public int getMoveOffX() {
    return moveOffsetX;
  }
  
  public int getMoveOffY() {
    return moveOffsetY;
  }
  





  public void addOffset(int x)
  {
    String offsets = getAttributeValue("offsets").replaceAll("\\s", 
      "");
    offsets = offsets.replaceAll(";", "");
    String[] offsetsA;
    String[] offsetsA; if (offsets.equals("")) {
      offsetsA = new String[0];
    } else {
      offsetsA = offsets.split(",");
    }
    int maxOffsets = 0;
    
    int segmenationType = getSegmantationType();
    
    if (ocrIF.getCurrLineGTMode() == 2) {
      maxOffsets = getContents().length() - 1;
    } else if (ocrIF.getCurrLineGTMode() == 3) {
      maxOffsets = getContents().split("\\s+").length - 1;
    }
    else if (ocrIF.getCurrLineGTMode() == 4) {
      maxOffsets = getContents().split("\n").length - 1;
    }
    


    if (offsetsA.length < maxOffsets)
    {







      if (x < lt.x) {
        x = lt.x + 1;
      } else if (x > rb.x) {
        x = rb.x - 1;
      }
      
      if (offsets.equals("")) {
        offsets = String.valueOf(x - lt.x);
      }
      else {
        int[] sortedOffsets = new int[offsetsA.length + 1];
        sortedOffsets[offsetsA.length] = (x - lt.x);
        for (int i = 0; i < offsetsA.length; i++) {
          sortedOffsets[i] = Integer.valueOf(offsetsA[i]).intValue();
        }
        Arrays.sort(sortedOffsets);
        
        offsets = String.valueOf(sortedOffsets[0]);
        for (int i = 1; i < sortedOffsets.length; i++) {
          offsets = offsets + ", " + sortedOffsets[i];
        }
      }
      

      setAttributeValue("offsets", offsets);
      this_interfacetbdPane.data_panel.a_window
        .showZoneInfo(ImageDisplay.getActiveZones());
      OCRInterface.this_interface.getCanvas().paintCanvas();
    }
  }
  
  public void addOffset(int y, String yaxis) {
    String offsets = getAttributeValue("offsets").replaceAll("\\s", 
      "");
    offsets = offsets.replaceAll(";", "");
    String[] offsetsA;
    String[] offsetsA; if (offsets.equals("")) {
      offsetsA = new String[0];
    } else {
      offsetsA = offsets.split(",");
    }
    int maxOffsets = 0;
    int segmenationType = getSegmantationType();
    
    if (ocrIF.getCurrLineGTMode() == 2) {
      maxOffsets = getContents().length() - 1;
    } else if (ocrIF.getCurrLineGTMode() == 3) {
      maxOffsets = getContents().split("\\s+").length - 1;
    }
    else if (ocrIF.getCurrLineGTMode() == 4) {
      maxOffsets = getContents().split("\n").length - 1;
    }
    


    if (offsetsA.length < maxOffsets)
    {







      if (y < lt.y) {
        y = lt.y + 1;
      } else if (y > rb.y) {
        y = rb.y - 1;
      }
      
      if (offsets.equals("")) {
        offsets = String.valueOf(y - lt.y);
      }
      else {
        int[] sortedOffsets = new int[offsetsA.length + 1];
        sortedOffsets[offsetsA.length] = (y - lt.y);
        for (int i = 0; i < offsetsA.length; i++) {
          sortedOffsets[i] = Integer.valueOf(offsetsA[i]).intValue();
        }
        Arrays.sort(sortedOffsets);
        
        offsets = String.valueOf(sortedOffsets[0]);
        for (int i = 1; i < sortedOffsets.length; i++) {
          offsets = offsets + ", " + sortedOffsets[i];
        }
      }
      

      setAttributeValue("offsets", offsets);
      this_interfacetbdPane.data_panel.a_window
        .showZoneInfo(ImageDisplay.getActiveZones());
      OCRInterface.this_interface.getCanvas().paintCanvas();
    }
  }
  
  private int getSegmantationType()
  {
    return ((Integer)OCRInterface.lineGTModeMap.get(getAttributeValue("segmentation").trim())).intValue();
  }
  



  public void changeToDL_TEXTLINEGT()
  {
    setAttributeValue("gedi_type", "DL_TEXTLINEGT");
    if (!hasAttribute("offsets"))
      setAttributeValue("offsets", "");
    if (!hasContents())
      setContents("");
    if (!hasAttribute("segmentation")) {
      setAttributeValue("segmentation", "word");
    }
  }
  





  public boolean checkLineSelected(int xPoint, float scale)
  {
    int last = (int)(currentHWObjcurr_canvas.lastMouseEvent.getX() / scale);
    
    if (offsetsReady()) {
      String offsets = getAttributeValue("offsets").replaceAll(
        "\\s", "");
      offsets = offsets.replaceAll(";", "");
      String[] offsetsA = offsets.split(",");
      
      if (last - xPoint < 0) {
        Arrays.sort(offsetsA, Collections.reverseOrder());
      }
      

      if (!offsets.equals("")) {
        for (int i = 0; i < offsetsA.length; i++) {
          int temp;
          int temp;
          if (getHoriz() != 4) {
            temp = lt.x;
          }
          else {
            temp = lt.y;
          }
          
          int x = temp + Integer.valueOf(offsetsA[i]).intValue();
          if ((xPoint <= x + 10) && 
            (xPoint >= x - 10)) {
            lineSelected = Integer.valueOf(offsetsA[i]).intValue();
            return true;
          }
          lineSelected = -5;
        }
      }
    }
    else {
      lineSelected = -5;
    }
    return false;
  }
  




  public void deleteSelectedOffset()
  {
    String offsets = getAttributeValue("offsets").replaceAll("\\s", 
      "");
    
    String[] offsetsA = offsets.split(",");
    String newOffsets = "";
    
    boolean oneDeleted = false;
    
    for (int i = 0; i < offsetsA.length; i++) {
      if (Integer.valueOf(offsetsA[i]).intValue() != lineSelected) {
        newOffsets = newOffsets + offsetsA[i] + ", ";





      }
      else if ((Integer.valueOf(offsetsA[i]).intValue() == lineSelected) && (!oneDeleted)) {
        oneDeleted = true;
      } else if ((Integer.valueOf(offsetsA[i]).intValue() == lineSelected) && (oneDeleted))
        newOffsets = newOffsets + offsetsA[i] + ", ";
    }
    if (!newOffsets.equals("")) {
      newOffsets = newOffsets.substring(0, newOffsets.length() - 2);
    }
    


    setAttributeValue("offsets", newOffsets);
    lineSelected = -5;
    this_interfacetbdPane.data_panel.a_window
      .showZoneInfo(ImageDisplay.getActiveZones());
    
    OCRInterface.this_interface.getCanvas().paintCanvas();
  }
  
  public void moveSelectedLine(MouseEvent e, float scale) {
    Point p = e.getPoint();
    















    deleteSelectedOffset();
    
    if (getHoriz() != 4) {
      addOffset((int)(x / scale));
    } else {
      addOffset((int)(y / scale), "yaxis");
    }
    checkLineSelected((int)(x / scale), scale);
  }
  






  public boolean isOnALine(int xPoint)
  {
    if (offsetsReady()) {
      String offsets = getAttributeValue("offsets").replaceAll(
        "\\s", "");
      offsets = offsets.replaceAll(";", "");
      String[] offsetsA = offsets.split(",");
      
      if (!offsets.equals("")) {
        for (int i = 0; i < offsetsA.length; i++)
        {
          int x = lt.x + Integer.valueOf(offsetsA[i]).intValue();
          if ((xPoint >= x - 10) && (xPoint <= x + 10))
            return true;
        }
      }
    }
    return false;
  }
  
  public boolean isOnALine(int yPoint, String yaxis) {
    if (offsetsReady()) {
      String offsets = getAttributeValue("offsets").replaceAll(
        "\\s", "");
      offsets = offsets.replaceAll(";", "");
      String[] offsetsA = offsets.split(",");
      
      if (!offsets.equals("")) {
        for (int i = 0; i < offsetsA.length; i++)
        {
          int y = lt.y + Integer.valueOf(offsetsA[i]).intValue();
          if ((yPoint >= y - 10) && (yPoint <= y + 10))
            return true;
        }
      }
    }
    return false;
  }
  
  public boolean isNeverVisible() {
    return (!this_interfacetbdPane.data_panel.t_window.isVisible(getZoneType())) && (!
      this_interfacetbdPane.data_panel.t_window.isOnlySelectedVisible(getZoneType()));
  }
  

  public boolean isVisible()
  {
    return (this_interfacetbdPane.data_panel.t_window.isVisible(getZoneType())) || ((this_interfacetbdPane.data_panel.t_window.isOnlySelectedVisible(getZoneType())) && ((ImageDisplay.activeZones.contains(this)) || (ImageDisplay.activeZones.isEmpty())));
  }
  



  public int getHoriz()
  {
    return isHoriz;
  }
  
  public void setHoriz(int num) { isHoriz = num; }
  







  public Rectangle get_Bounds()
  {
    Point origin = new Point(get_lt_x(), get_lt_y());
    Rectangle bounds = new Rectangle(origin, new Dimension(get_width(), get_height()));
    return bounds;
  }
  
  public Point getLastMouseEvent() {
    return lastMouseEvent;
  }
  
  public void setLastMouseEvent(Point lastMouseEvent) {
    this.lastMouseEvent = lastMouseEvent;
  }
  








































  public Point closeTo(Point mouseIn, float scale)
  {
    int buffer = 7;
    
    Point[] vertex = getZoneCorners();
    



    for (Point pt : vertex)
    {





      if ((x <= x * scale + buffer) && (y <= y * scale + buffer) && 
        (x >= x * scale - buffer) && (y >= y * scale - buffer))
        return pt;
    }
    return null;
  }
  





  public Point[] getZoneCorners()
  {
    Point[] vertex = new Point[4];
    vertex[0] = new Point(lt.x, lt.y);
    vertex[1] = new Point(lt.x + width, lt.y);
    vertex[2] = new Point(lt.x + width, lt.y + height);
    vertex[3] = new Point(lt.x, lt.y + height);
    return vertex;
  }
  
  public void setIsIncomplete(boolean incomplete) {
    isIncomplete = incomplete;
  }
  























  public void adjust()
  {
    System.out.println("zone adjust");
    
    if (!OCRInterface.currentHWObj.getOriginalImage().getBounds().contains(get_Bounds())) {
      System.out.println("Zone.adjust() zone (id: " + getZoneId() + ") outside of image bounds. " + get_Bounds());
      return;
    }
    
    ImageDisplay.activeZones.clear();
    ImageDisplay.activeZones.add(this);
    OCRInterface.this_interface.getML().actionPerformed(
      new java.awt.event.ActionEvent(OCRInterface.this_interface, 
      128, 
      "Shrink zone"));
  }
  
  public int getContentDirection()
  {
    return contentDirection;
  }
  
  public void setContentDirection(int contentDirection) {
    this.contentDirection = contentDirection;
  }
  
  public SelectedWord getSelectedWord() {
    return selectedWord;
  }
  
  public void setSelectedWord(int startPos, int endPos) {
    selectedWord = null;
    selectedWord = new SelectedWord(startPos, endPos);
  }
  
  public void setSelectedWord(SelectedWord selectedWord) {
    this.selectedWord = selectedWord;
  }
  







  public DLZone dlMergeZones(Zone zone1)
    throws DLException
  {
    if ((zonePage != null) && (zonePage != null) && 
      (zonePage != zonePage)) {
      throw new DLException(
        DLExceptionCodes.DL_NOT_SUPPORTED_EXCEPTION, 
        "<DLZone::dlMergeZones()> The zones to be merged must not have conflicting DLPage*s!");
    }
    Rectangle r1 = zone1.get_Bounds();
    Rectangle r2 = get_Bounds();
    
    Point origin1 = new Point(x, y);
    Point origin2 = new Point(x, y);
    

    int x1 = Math.min(x, x);
    int y1 = Math.min(y, y);
    

    int x2 = Math.max(x + width - 1, x + 
      width - 1);
    int y2 = Math.max(y + height - 1, y + 
      height - 1);
    

    DLPage parentPage = zonePage != null ? zonePage : zonePage;
    DLZone mergedZone = new DLZone(parentPage, null, x1, y1, x2 - 
      x1 + 1, y2 - y1 + 1);
    




    mergedZone.dlAppendChildZoneList(childZones);
    mergedZone.dlAppendChildZoneList(childZones);
    mergedZone.dlPropagateZonePointers();
    
    return mergedZone;
  }
  







  public DLZone dlMergeZones2(Zone zone1)
    throws DLException
  {
    if ((zonePage != null) && (zonePage != null) && 
      (zonePage != zonePage)) {
      System.out.println("here");
      System.out.println("zonePage: " + zonePage);
      System.out.println("zone1.zonePage: " + zonePage);
      throw new DLException(
        DLExceptionCodes.DL_NOT_SUPPORTED_EXCEPTION, 
        "<DLZone::dlMergeZones()> The zones to be merged must not have conflicting DLPage*s!");
    }
    
    Rectangle r1 = zone1.get_Bounds();
    Rectangle r2 = get_Bounds();
    
    System.out.println("r1/r2: " + r1 + "/" + r2);
    Point origin1 = new Point(x, y);
    Point origin2 = new Point(x, y);
    

    int x1 = Math.min(x, x);
    int y1 = Math.min(y, y);
    

    int x2 = Math.max(x + width - 1, x + 
      width - 1);
    int y2 = Math.max(y + height - 1, y + 
      height - 1);
    


    DLPage parentPage = zonePage != null ? zonePage : zonePage;
    DLZone mergedZone = new DLZone(parentPage, null, x1, y1, x2 - 
      x1 + 1, y2 - y1 + 1);
    




    mergedZone.dlAppendChildZoneList(childZones);
    mergedZone.dlAppendChildZoneList(childZones);
    mergedZone.dlPropagateZonePointers();
    
    return mergedZone;
  }
  
  public void setSpecificColor(Color color) {
    this.color = color;
  }
  
  public Color getSpecificColor() {
    return color;
  }
  











  public class SelectedWord
  {
    int startPos = 0;
    int endPos = 0;
    
    public SelectedWord(int startPos, int endPos) { this.startPos = startPos;
      this.endPos = endPos;
    }
    
    public int getStartPos() { return startPos; }
    
    public void setStartPos(int startPos) {
      this.startPos = startPos;
    }
    
    public int getEndPos() { return endPos; }
    
    public void setEndPos(int endPos) {
      this.endPos = endPos;
    }
  }
}
