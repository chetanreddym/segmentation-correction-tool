package ocr.tif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLException;
import gttool.misc.TypeAttributeEntry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.LookupOp;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import ocr.gui.BrowserToolBar;
import ocr.gui.FindPanel;
import ocr.gui.Group;
import ocr.gui.LoadDataFile;
import ocr.gui.OCRInterface;
import ocr.gui.OrientedBox;
import ocr.gui.ParChldLine;
import ocr.gui.PolygonZone;
import ocr.gui.RLE_CC_Handler;
import ocr.gui.ReadingOrder;
import ocr.gui.Zone;
import ocr.gui.leftPanel.AttributeTable;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.ElectronicTextDisplayer;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.TypeWindow;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.gui.workflows.PolyTranscribeToolBar;
import ocr.gui.workflows.TranslateToolBar;
import ocr.manager.GlobalDisableManager;
import ocr.manager.GlobalHotkeyManager;
import ocr.manager.PropertiesInfoHolder;
import ocr.manager.UndoRedoManager;
import ocr.manager.zones.ZonesManager;
import ocr.util.AttributeConfigUtil;
import ocr.util.BidiString;
import ocr.util.DeveloperView;
import ocr.util.UniqueZoneId;
import ocr.util.comments.CommentsParser;
























public class ImageDisplay
  extends JComponent
  implements Icon, MouseListener, MouseMotionListener
{
  public String str = null;
  public ZoneVector zvec = new ZoneVector();
  
  public int rotate = 0;
  
  public RenderedImage origIm;
  
  private Raster imageRaster;
  
  public boolean rotating = false;
  
  public boolean rotated = false;
  
  public boolean allowEdit = false;
  
  public static boolean showCaret = true;
  
  public Zone lastZoneSelected = null;
  

  public boolean selected_has_contents = false;
  





  public Zone zoneLineSelected = null;
  public PolygonZone zonePtSelected = null;
  
  public Zone dpc_zone = null;
  
  public Zone rpc_zone = null;
  
  public int x;
  public int y;
  public int xSc;
  public int ySc;
  public static final String ptMoveImg = "images/ptmove.gif";
  private ReadingOrder readingOrderHandler;
  private FontMetrics fontMetrics = null;
  
  private Graphics context = null;
  
  private Map<String, BufferedImage> RLEMap = new LinkedHashMap();
  



  public UndoRedoManager undoManager;
  




  public static class ZoneVector
    extends Vector<Zone>
  {
    public ZoneVector() {}
    



    public Zone elementAt(int index)
    {
      if (size() > 0) {
        return (Zone)super.elementAt(index);
      }
      return null;
    }
    
    public void setElementAt(Zone zoneIn, int index) {
      if (size() > 0) {
        super.setElementAt(zoneIn, index);
      } else
        super.add(index, zoneIn);
    }
    
    public boolean add(Zone z) {
      if (!contains(z)) {
        super.add(z);
        
        if (OCRInterface.this_interface.getEnableTranslateWorkflow())
          OCRInterface.this_interface.getTranslateWorkflowPanel().resetPairPanel(z);
        return true;
      }
      return false;
    }
    
    public boolean contains(Zone z) {
      Iterator<Zone> itr = iterator();
      while (itr.hasNext()) {
        if (((Zone)itr.next()).equals(z))
          return true;
      }
      return false;
    }
  }
  










  public ZonesManager shapeVec = null;
  


  private HashSet<AllZones> allZones = null;
  private boolean identicalZoneWasCreated = false;
  






  private Zone selectionZone = null;
  





  private Zone toBeDrawn = null;
  
  private Zone toBeErased = null;
  
  private Zone toBeOrdered = null;
  






  public static ZoneVector activeZones = new ZoneVector();
  
  public static ZoneVector actZones = null;
  




  public MouseEvent lastMouseEvent = null;
  
  private MouseEvent startMeasureMouseEvent = null;
  private MouseEvent endMeasureMouseEvent = null;
  private boolean stopMeasure = false;
  





  private MouseEvent lastMouseMovedEvent = null;
  





  private Zone toBeEdited = null;
  

  Zone tSelectFirst;
  

  Zone tSelectLast;
  

  private RenderedImage im;
  

  private SampleModel sampleModel;
  

  private ColorModel colorModel;
  

  private int minX;
  

  private int minY;
  

  private int width;
  

  private int height;
  

  private int minTileX;
  

  private int maxTileX;
  

  private int minTileY;
  

  private int maxTileY;
  

  private int tileWidth;
  

  private int tileHeight;
  

  private int tileGridXOffset;
  

  private int tileGridYOffset;
  
  private Color backgroundColor = null;
  
  private int blackPixelValue = 0;
  

  private Vector<Group> groupList = null;
  





  private float scale = 1.0F;
  



  private OCRInterface ocrIF = null;
  



  private ImageReaderDrawer hw = null;
  

  private Interpolation interp = Interpolation.getInstance(0);
  



  protected Toolkit defaultKit = Toolkit.getDefaultToolkit();
  

  protected Image textlineMoveCursorImage;
  

  protected Image textlineMoveHorizCursorImage;
  

  protected Cursor textlineMoveCursor;
  

  protected Cursor textlineMoveHorizCursor;
  

  protected Image cl_cusor;
  

  protected Cursor closed;
  

  protected Image op_cusor;
  

  protected Cursor open;
  

  protected Cursor changeZoneCursor;
  

  protected LoadDataFile ld;
  

  private boolean showImage;
  

  private boolean showData;
  
  public static boolean alwaysDestroyH = true;
  
  public static boolean neverDestroyH = false;
  
  public static LinkedList<DLZone> nulldoc = new LinkedList();
  public static LinkedHashMap<String, String> nulldocpageatts = new LinkedHashMap();
  public int electronicTextSize;
  private boolean isControlDown;
  
  public ImageDisplay(RenderedImage im, OCRInterface myIF, ImageReaderDrawer myHW)
  {
    if (new ImageIcon("images/textlinemove.gif").getClass().getResource("images/textlinemove.gif") == null) {
      textlineMoveCursorImage = defaultKit.createImage(getClass().getClassLoader().getResource("images/textlinemove.gif"));
    } else {
      textlineMoveCursorImage = defaultKit.createImage("images/textlinemove.gif");
    }
    if (new ImageIcon("images/textlinemove_horiz.gif").getClass().getResource("images/textlinemove_horiz.gif") == null) {
      textlineMoveHorizCursorImage = defaultKit.createImage(getClass().getClassLoader().getResource("images/textlinemove_horiz.gif"));
    } else {
      textlineMoveHorizCursorImage = defaultKit.createImage("images/textlinemove_horiz.gif");
    }
    

    textlineMoveCursor = defaultKit.createCustomCursor(textlineMoveCursorImage, new Point(12, 12), "textlineMove");
    
    textlineMoveHorizCursor = defaultKit.createCustomCursor(textlineMoveHorizCursorImage, new Point(12, 12), "textlineMoveHoriz");
    
    cl_cusor = defaultKit.createImage("images/closed_hand.gif");
    

    closed = defaultKit.createCustomCursor(cl_cusor, new Point(), "Closed");
    
    op_cusor = defaultKit.createImage("images/open_hand.gif");
    

    open = defaultKit.createCustomCursor(op_cusor, new Point(), "Open");
    
    changeZoneCursor = null;
    
    ld = null;
    


    showImage = true;showData = true;
    







































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































    electronicTextSize = 12;ocrIF = myIF;hw = myHW;Init();int mx = im.getMinX();int my = im.getMinY();
    if ((mx < 0) || (my < 0)) {
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(im);
      pb.add(Math.max(-mx, 0));
      pb.add(Math.max(-my, 0));
      pb.add(new InterpolationNearest());
      im = JAI.create("translate", pb, null);
    }
    
    setRenderedImage(im);
    sampleModel = im.getSampleModel();
    colorModel = im.getColorModel();
    



















    minX = im.getMinX();
    minY = im.getMinY();
    width = im.getWidth();
    height = im.getHeight();
    new Rectangle(minX, minY, width, height);
    
    minTileX = im.getMinTileX();
    maxTileX = (im.getMinTileX() + im.getNumXTiles() - 1);
    minTileY = im.getMinTileY();
    maxTileY = (im.getMinTileY() + im.getNumYTiles() - 1);
    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    tileGridXOffset = im.getTileGridXOffset();
    tileGridYOffset = im.getTileGridYOffset();
    
    undoManager = new UndoRedoManager();
    
    Image cursorImage = defaultKit.createImage("images/move.gif");
    
    changeZoneCursor = defaultKit.createCustomCursor(cursorImage, 
      new Point(), "Change ZoneType");
  }
  



  public int getIconWidth()
  {
    return getRenderedImage().getWidth();
  }
  
  public void clearRLEMap() {
    RLEMap.clear();
  }
  
  public Map<String, BufferedImage> getRLEMap()
  {
    return RLEMap;
  }
  







  public int getIconHeight()
  {
    return getRenderedImage().getHeight();
  }
  
  public Graphics getContext() {
    return context;
  }
  
  public FontMetrics getFontMetrics() {
    return fontMetrics;
  }
  






  public void repaint()
  {
    DeveloperView.println("[ImageDisplay:repaint]");
  }
  





  public void paint(Graphics g)
  {
    DeveloperView.println("[ImageDisplay:paint]");
  }
  












  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    context = g;
    
    Graphics2D g2D = null;
    if ((g instanceof Graphics2D)) {
      g2D = (Graphics2D)g;
    } else {
      return;
    }
    
    RenderingHints rHints = g2D.getRenderingHints();
    rHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, 
      RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    rHints.put(RenderingHints.KEY_RENDERING, 
      RenderingHints.VALUE_RENDER_SPEED);
    rHints.put(RenderingHints.KEY_COLOR_RENDERING, 
      RenderingHints.VALUE_COLOR_RENDER_SPEED);
    
    g2D.setRenderingHints(rHints);
    

    Rectangle clipBounds = g.getClipBounds();
    
    int transX = x;
    int transY = y;
    





    Rectangle imageRect = new Rectangle(minX + transX, minY + transY, 
      width, height);
    if (clipBounds == null) {
      clipBounds = new Rectangle(0, 0, c.getWidth(), c.getHeight());
    }
    

    int txmin = PlanarImage.XToTileX(x - transX, tileGridXOffset, 
      tileWidth);
    txmin = Math.max(txmin, minTileX);
    txmin = Math.min(txmin, maxTileX);
    

    int txmax = PlanarImage.XToTileX(x + width - 1 - 
      transX, tileGridXOffset, tileWidth);
    txmax = Math.max(txmax, minTileX);
    txmax = Math.min(txmax, maxTileX);
    

    int tymin = PlanarImage.YToTileY(y - transY, tileGridYOffset, 
      tileHeight);
    tymin = Math.max(tymin, minTileY);
    tymin = Math.min(tymin, maxTileY);
    

    int tymax = PlanarImage.YToTileY(y + height - 1 - 
      transY, tileGridYOffset, tileHeight);
    tymax = Math.max(tymax, minTileY);
    tymax = Math.min(tymax, maxTileY);
    if (showImage) {
      g2D.clip(imageRect);
      
      if (backgroundColor != null) {
        g2D.setColor(backgroundColor);
      }
      



      for (int tj = tymin; tj <= tymax; tj++) {
        for (int ti = txmin; ti <= txmax; ti++) {
          int tx = PlanarImage.tileXToX(ti, tileGridXOffset, 
            tileWidth);
          int ty = PlanarImage.tileYToY(tj, tileGridYOffset, 
            tileHeight);
          
          Raster tile = getRenderedImage().getTile(ti, tj);
          DataBuffer dataBuffer = tile.getDataBuffer();
          
          Point origin = new Point(0, 0);
          WritableRaster wr = Raster.createWritableRaster(sampleModel, 
            dataBuffer, origin);
          
          BufferedImage bi = new BufferedImage(colorModel, wr, false, 
            null);
          
          AffineTransform transform = 
            AffineTransform.getTranslateInstance(tx + transX, ty + transY);
          
          if (backgroundColor != null) {
            g2D.fillRect(tx + transX, ty + transY, tileWidth, 
              tileHeight);
          }
          g2D.drawImage(bi, transform, null);
        }
      }
    } else {
      g2D.setColor(Color.white);
      for (int tj = tymin; tj <= tymax; tj++) {
        for (int ti = txmin; ti <= txmax; ti++) {
          int tx = PlanarImage.tileXToX(ti, tileGridXOffset, 
            tileWidth);
          int ty = PlanarImage.tileYToY(tj, tileGridYOffset, 
            tileHeight);
          g2D.fillRect(tx + transX, ty + transY, tileWidth, 
            tileHeight);
        }
      }
    }
    if (showData) {
      if (fontMetrics == null) {
        Font currFont = g.getFont();
        g.setFont(new Font(currFont.getName(), currFont.getStyle(), electronicTextSize));
        fontMetrics = g.getFontMetrics();
        g.setFont(currFont);
      }
      drawZones(g);
    }
  }
  




































  private void drawZones(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    
    float strokeThickness = ocrIF.getLineThickness();
    
    BasicStroke stroke = new BasicStroke(strokeThickness);
    g2d.setStroke(stroke);
    








    if (lastMouseMovedEvent == null) {
      lastMouseMovedEvent = new MouseEvent(hw, 0, 0L, 0, 0, 0, 0, false);
      lastMouseMovedEvent.translatePoint(0, 0);
    }
    




    if ((OCRInterface.currOppmode == 20) && 
      (startMeasureMouseEvent != null) && 
      (endMeasureMouseEvent != null)) {
      int cross = 10;
      Color curr_color = g2d.getColor();
      float[] dash1 = { 10.0F };
      BasicStroke dashed = 
        new BasicStroke(2.0F, 
        0, 
        0, 
        10.0F, dash1, 1.0F);
      
      g2d.setStroke(dashed);
      g2d.setColor(Color.RED);
      
      g2d.drawLine(startMeasureMouseEvent.getPoint().x, 
        startMeasureMouseEvent.getPoint().y, 
        endMeasureMouseEvent.getPoint().x, 
        endMeasureMouseEvent.getPoint().y);
      
      g2d.setStroke(new BasicStroke(2.0F));
      
      g.drawLine(startMeasureMouseEvent.getX() - cross, startMeasureMouseEvent.getY(), startMeasureMouseEvent.getX() + cross, startMeasureMouseEvent.getY());
      g.drawLine(startMeasureMouseEvent.getX(), startMeasureMouseEvent.getY() - cross, startMeasureMouseEvent.getX(), startMeasureMouseEvent.getY() + cross);
      
      g.drawLine(endMeasureMouseEvent.getX() - cross, endMeasureMouseEvent.getY(), endMeasureMouseEvent.getX() + cross, endMeasureMouseEvent.getY());
      g.drawLine(endMeasureMouseEvent.getX(), endMeasureMouseEvent.getY() - cross, endMeasureMouseEvent.getX(), endMeasureMouseEvent.getY() + cross);
      
      g2d.setStroke(stroke);
      g2d.setColor(curr_color);
      
      double distance = Point2D.distance(
        startMeasureMouseEvent.getPoint().x / scale, 
        startMeasureMouseEvent.getPoint().y / scale, 
        endMeasureMouseEvent.getPoint().x / scale, 
        endMeasureMouseEvent.getPoint().y / scale);
      
      distance = Math.round(distance * 10.0D) / 10.0D;
      
      double atan2 = Math.atan2(
        endMeasureMouseEvent.getPoint().y / scale - 
        startMeasureMouseEvent.getPoint().y / scale, 
        endMeasureMouseEvent.getPoint().x / scale - 
        startMeasureMouseEvent.getPoint().x / scale);
      
      double degrees = Math.toDegrees(atan2);
      degrees = Math.round(degrees * 100.0D) / 100.0D;
      degrees *= -1.0D;
      
      int x = (int)(endMeasureMouseEvent.getX() / scale);
      int y = (int)(endMeasureMouseEvent.getY() / scale);
      int gray = OCRInterface.this_interface.getGrayLevelValue(x, y, OCRInterface.this_interface.getCanvas().getImageRaster());
      String str = "(" + x + ", " + y + ")" + " - " + gray + "     ";
      OCRInterface.this_interface.setMousePositionLabel(str + "[ " + 
        distance + " px, " + 
        degrees + " " + "°" + " ]    'c' to copy");
    }
    


    if (OCRInterface.currOppmode == 15) {
      Zone cur_zone = activeZones.elementAt(0);
      if ((cur_zone != null) && ((cur_zone instanceof OrientedBox)))
      {
        boolean isSelected = true;
        Color cl = cur_zone.getZoneColor();
        
        boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
        if (cc) {
          isSelected = false;
          cl = Color.gray;
        }
        
        g.setColor(cl);
        ((OrientedBox)cur_zone).drawOrientedBox(g, scale, isSelected, electronicTextSize, new Point(
          lastMouseMovedEvent.getX(), 
          lastMouseMovedEvent.getY()));
      }
    }
    

    if (OCRInterface.currOppmode == 16) {
      Zone cur_zone = activeZones.elementAt(0);
      if ((cur_zone != null) && ((cur_zone instanceof PolygonZone))) {
        boolean isSelected = true;
        

        if (shapeVec.contains(cur_zone)) {
          isSelected = false;
        }
        Color cl = cur_zone.getZoneColor();
        
        boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
        if (cc) {
          isSelected = false;
          cl = Color.gray;
        }
        
        g.setColor(cl);
        ((PolygonZone)cur_zone).drawPolygonZone(g, scale, isSelected, electronicTextSize, new Point(
          lastMouseMovedEvent.getX(), 
          lastMouseMovedEvent.getY()), isControlDown);
      }
    }
    

    if (OCRInterface.currOppmode == 2)
    {
      Zone cur_zone = activeZones.elementAt(0);
      if ((cur_zone != null) && 
        (cur_zone.isVisible()))
      {
        boolean isSelected = true;
        Color cl = cur_zone.getZoneColor();
        
        g.setColor(cl);
        cur_zone.draw(g, scale, isSelected, 
          OCRInterface.currOppmode == 7, new Point(
          lastMouseMovedEvent.getX(), 
          lastMouseMovedEvent.getY()), electronicTextSize, isControlDown);
      }
    }
    

    for (Iterator iter = shapeVec.iterator(); iter.hasNext();)
    {

      Zone cur_zone = (Zone)iter.next();
      
      if (cur_zone.isVisible())
      {
        Color cl = cur_zone.getZoneColor();
        
        float[] comps = cl.getComponents(null);
        cl = new Color(comps[0], comps[1], comps[2], comps[3] - ocrIF.TRANSPARENCY);
        
        g.setColor(cl);
        
        if ((activeZones.contains(cur_zone)) || 
          (cur_zone == toBeEdited))
        {

          if ((cur_zone instanceof OrientedBox)) {
            ((OrientedBox)cur_zone).drawOrientedBox(g, scale, true, electronicTextSize, new Point(
              lastMouseMovedEvent.getX(), lastMouseMovedEvent.getY()));


          }
          else if ((cur_zone instanceof PolygonZone))
          {
            boolean isSelected = true;
            
            boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
            if (cc) {
              isSelected = false;
              Color color = Color.gray;
              g.setColor(color);
            }
            
            ((PolygonZone)cur_zone).drawPolygonZone(g, scale, isSelected, electronicTextSize, new Point(
              lastMouseMovedEvent.getX(), 
              lastMouseMovedEvent.getY()), isControlDown);
          }
          else if ((cur_zone instanceof Group)) {
            ((Group)cur_zone).draw(g, scale, true, 
              new Point(
              lastMouseMovedEvent.getX(), 
              lastMouseMovedEvent.getY()), electronicTextSize);
          } else {
            cur_zone.draw(g, scale, true, 
              OCRInterface.currOppmode == 7, new Point(
              lastMouseMovedEvent.getX(), 
              lastMouseMovedEvent.getY()), electronicTextSize, isControlDown);
          } } else if ((OCRInterface.currOppmode == 10) && 
          (cur_zone == zoneLineSelected))
        {
          cur_zone.draw(g, scale, true, false, new Point(
            lastMouseMovedEvent.getX(), lastMouseMovedEvent
            .getY()), electronicTextSize, isControlDown);
        } else if ((OCRInterface.currOppmode == 12) && 
          (cur_zone == toBeDrawn)) {
          dpc_zone = cur_zone;
          cur_zone.draw(g, scale, true, false, new Point(
            lastMouseMovedEvent.getX(), lastMouseMovedEvent
            .getY()), electronicTextSize, isControlDown);
        } else if ((OCRInterface.currOppmode == 14) && 
          (cur_zone == toBeErased)) {
          rpc_zone = cur_zone;
          cur_zone.draw(g, scale, true, false, new Point(
            lastMouseMovedEvent.getX(), lastMouseMovedEvent
            .getY()), electronicTextSize, isControlDown);
        } else if ((cur_zone instanceof OrientedBox)) {
          ((OrientedBox)cur_zone).drawOrientedBox(g, scale, false, electronicTextSize, null);
        } else if ((cur_zone instanceof PolygonZone)) {
          ((PolygonZone)cur_zone).drawPolygonZone(g, scale, false, electronicTextSize, null, isControlDown);
        }
        else if ((cur_zone instanceof Group)) {
          ((Group)cur_zone).draw(g, scale, false, 
            new Point(
            lastMouseMovedEvent.getX(), 
            lastMouseMovedEvent.getY()), electronicTextSize);
        } else {
          cur_zone.draw(g, scale, false, false, new Point(), electronicTextSize, isControlDown);
        }
      }
    }
    








    if ((OCRInterface.currOppmode == 11) || (BrowserToolBar.pcLine))
    {
      Vector<ParChldLine> arr = new Vector();
      


      LoadDataFile ldf = OCRInterface.currDoc;
      ZonesManager zm = ldf.get_zones_vec();
      
      DLPage dlp = zm.getPage();
      
      Vector<DLZone> vec = dlp.dlGetAllZones();
      
      arr.clear();
      
      for (int i = 0; i < vec.size(); i++)
      {
        if (((DLZone)vec.elementAt(i)).dlHasChildZones()) {
          LinkedList<DLZone> childList = ((DLZone)vec.elementAt(i)).getChildrenZones();
          
          for (int j = 0; j < childList.size(); j++)
          {
            ParChldLine dl = new ParChldLine(vec, i, childList, j);
            arr.add(dl);
          }
        }
      }
      
      OCRInterface.setVec(arr);
      
      g.setColor(Color.BLACK);
      for (int i = 0; i < arr.size(); i++) {
        g.drawLine((int)(((ParChldLine)arr.elementAt(i)).getX1() * getScale()), (int)(((ParChldLine)arr.elementAt(i)).getY1() * getScale()), (int)(((ParChldLine)arr.elementAt(i)).getX2() * getScale()), (int)(((ParChldLine)arr.elementAt(i)).getY2() * getScale()));
      }
    }
    

    if ((toBeDrawn != null) && (lastMouseMovedEvent != null) && (OCRInterface.currOppmode == 12))
    {



      String colorOfRec = "5";
      
      Color cl = new Color(0);
      cl = Color.decode(colorOfRec);
      g.setColor(cl);
      
      DLZone temp = toBeDrawn;
      
      g.drawLine((int)(temp.dlGetZoneOrigin().getX() * getScale()), (int)(temp.dlGetZoneOrigin().getY() * getScale()), 
        lastMouseMovedEvent.getX(), 
        lastMouseMovedEvent.getY());
    }
    DLZone temp;
    if ((toBeErased != null) && (lastMouseMovedEvent != null) && (OCRInterface.currOppmode == 14))
    {


      String colorOfRec = "5";
      Color cl = new Color(0);
      cl = Color.decode(colorOfRec);
      g.setColor(cl);
      
      temp = toBeErased;
      
      g.drawLine((int)(temp.dlGetZoneOrigin().getX() * getScale()), (int)(temp.dlGetZoneOrigin().getY() * getScale()), 
        lastMouseMovedEvent.getX(), 
        lastMouseMovedEvent.getY());
    }
    


    if ((toBeOrdered != null) && (lastMouseMovedEvent != null))
    {
      boolean header = false;
      
      if (toBeOrdered.previousZone == null) {
        header = true;
      }
      

      readingOrderHandler.drawArrow(g, header, 
        toBeOrdered.getCenter(scale).x, 
        toBeOrdered.getCenter(scale).y, 
        lastMouseMovedEvent.getX(), 
        lastMouseMovedEvent.getY());
    }
    
    if ((OCRInterface.currOppmode == 17) || (BrowserToolBar.showReadingOrder)) {
      Vector<DLZone> allZones = shapeVec.getAsVector();
      for (DLZone z : allZones) {
        Zone zone = (Zone)z;
        Zone nextZone = (Zone)nextZone;
        



        if ((nextZone != null) && 
          (zone.isVisible()) && 
          (nextZone.isVisible()))
        {

          boolean header = false;
          
          if (previousZone == null) {
            header = true;
          }
          readingOrderHandler.drawArrow(g, header, 
            getCenterscale).x, 
            getCenterscale).y, 
            getCenterscale).x, 
            getCenterscale).y);
        }
        
      }
    }
    else if (OCRInterface.currOppmode == 19)
    {
      Group cur_zone = (Group)activeZones.elementAt(0);
      if ((cur_zone != null) && 
        (cur_zone.isVisible()))
      {
        Color cl = cur_zone.getZoneColor();
        g.setColor(cl);
        cur_zone.draw(g, scale, false, 
          new Point(
          lastMouseMovedEvent.getX(), 
          lastMouseMovedEvent.getY()), electronicTextSize);
      }
    }
    


    if (selectionZone != null) {
      g.setColor(Color.gray);
      
      selectionZone.draw(g, scale, false, false, new Point(), electronicTextSize, isControlDown);

    }
    else if (activeZones.size() == 0) {
      ZonesManager.actZones.removeAllElements();
    }
  }
  








  public void paintCanvas()
  {
    ImageReaderDrawer.picture.repaint();
    Toolkit.getDefaultToolkit().sync();
  }
  

  public void Init()
  {
    activeZones.removeAllElements();
    
    scale = 1.0F;
    
    ocrIF.workmodeProps[0].initShapeVec();
  }
  






  private void setScale(float newscale)
  {
    if (newscale > 0.0F) {
      scale = newscale;
    }
  }
  
















  public void changeScale(float newscale)
  {
    if ((getRenderedImage() == null) || (newscale == scale)) {
      return;
    }
    


    setScale(newscale);
    

    initializeImage(getscaledImage());
    RLEMap.clear();
    



    width = getRenderedImage().getWidth();
    height = getRenderedImage().getHeight();
    


    int w = getIconWidth();
    int h = getIconHeight();
    



    Dimension new_dim = new Dimension(w, h);
    ImageReaderDrawer.picture.setPreferredSize(new_dim);
    hw.setPreferredSize(new Dimension(w + 3, h + 3));
    ImageReaderDrawer.picture.revalidate();
    
    recenterZone();
  }
  


































  public void recenterZone()
  {
    if ((!OCRInterface.this_interface.getAllowZoneRecenter()) || (!currentHWObjcurr_canvas.zoneOffScreen()) || (activeZones.size() == 0)) {
      return;
    }
    Zone selectedZone = activeZones.elementAt(0);
    
    ImageReaderDrawer.MyScrollPane picscroll = OCRInterface.currentHWObj.getPictureScrollPane();
    
    picscroll.validate();
    
    int portWidth = picscroll.getViewport().getWidth();
    int portHeight = picscroll.getViewport().getHeight();
    int viewPortX = getCentergetScalex - portWidth / 2;
    int viewPortY = getCentergetScaley - portHeight / 2;
    





    viewPortX = Math.max(Math.min(viewPortX + portWidth, 
      picscroll.getViewport().getView().getWidth()) - portWidth, 0);
    viewPortY = Math.max(Math.min(viewPortY + portHeight, 
      picscroll.getViewport().getView().getHeight()) - portHeight, 0);
    


    picscroll.getViewport().setViewPosition(new Point(viewPortX, viewPortY));
  }
  



  public boolean zoneOffScreen()
  {
    if (activeZones.size() == 0) {
      return false;
    }
    JViewport view = OCRInterface.currentHWObj.getPictureScrollPane().getViewport();
    
    Rectangle bounds = activeZones.elementAt(0).get_Bounds();
    


    return (x * scale < getViewPositionx) || (y * scale < getViewPositiony) || ((x + width) * scale > getViewPositionx + view.getWidth()) || ((y + height) * scale > getViewPositiony + view.getHeight());
  }
  






  public boolean zoneCompletelyOffScreen(Zone toCheck)
  {
    JViewport view = OCRInterface.currentHWObj.getPictureScrollPane().getViewport();
    
    Rectangle bounds = toCheck.get_Bounds();
    



    return ((x + width) * scale < getViewPositionx) || ((y + height) * scale < getViewPositiony) || (x * scale > getViewPositionx + view.getWidth()) || (y * scale > getViewPositiony + view.getHeight());
  }
  













  public void fitImageToWidth(boolean fitToWindow, boolean fitUsingZoneData)
  {
    DeveloperView.println("[ImageDisplay:fitImageToWidth]");
    
    RLEMap.clear();
    
    ZonesManager ocrZones = 
      (ZonesManager)ocrIF.workmodeProps[0].shape_vec_hash.get(new Integer(0));
    
    if (getRenderedImage() == null) {
      return;
    }
    Rectangle viewPortBounds = hw.pictureScrollPane
      .getViewportBorderBounds();
    
    Rectangle r = null;
    
    if ((ocrZones != null) && (fitUsingZoneData)) {
      r = ocrZones.getBoxAroundZones();
    }
    int imgWidthToDisplay = OCRInterface.currentHWObj.getOriginalImage().getWidth();
    int imgHeightToDisplay = OCRInterface.currentHWObj.getOriginalImage().getHeight();
    
    Point startCoordForView = new Point(0, 0);
    
    if (r != null)
    {

      imgWidthToDisplay = (int)(r.getWidth() + 80.0D);
      imgHeightToDisplay = (int)(r.getHeight() + 80.0D);
      startCoordForView = r.getLocation();
      x -= 40;
      y -= 40;
    }
    
    float newScaleWidth = (float)(viewPortBounds.getWidth() / imgWidthToDisplay);
    float newScaleHeight = (float)(viewPortBounds.getHeight() / imgHeightToDisplay);
    
    float newScale = newScaleWidth;
    
    if ((fitToWindow) && (newScaleHeight < newScaleWidth)) {
      newScale = newScaleHeight;
    }
    changeScale(newScale);
    initializeImage(getscaledImage());
    
    width = getRenderedImage().getWidth();
    height = getRenderedImage().getHeight();
    int w = getIconWidth();
    int h = getIconHeight();
    



    Dimension new_dim = new Dimension(w, h);
    ImageReaderDrawer.picture.setPreferredSize(new_dim);
    hw.setPreferredSize(new Dimension(w + 3, h + 3));
    ImageReaderDrawer.picture.revalidate();
    

    Point p = new Point((int)(x * scale), 
      (int)(y * scale));
    hw.pictureScrollPane.setViewportView(ImageReaderDrawer.picture);
    hw.pictureScrollPane.getViewport().setViewPosition(p);
    




    recenterZone();
  }
  


  public float getScale()
  {
    return scale;
  }
  



  private void setMouseCoordinate(int x, int y)
  {
    ocrIF.setMousePositionLabel((int)(x / scale), (int)(y / scale));
  }
  
  private void setMouseCoordinate(DLZone zone) {
    if (!(zone instanceof Zone))
      return;
    ocrIF.setMousePositionLabel((Zone)zone);
  }
  
















  public void setImage(RenderedImage im)
  {
    Init();
    
    initializeImage(im);
    ld = OCRInterface.currDoc;
    if (ld == null) {
      shapeVec = new ZonesManager();
    } else {
      shapeVec = ld.get_zones_vec();
    }
    
    if (ocrIF.tbdPane != null) {
      ocrIF.tbdPane.data_panel.a_window.setPage(shapeVec.getPage());
    }
    undoManager.newPage();
    
    readingOrderHandler = new ReadingOrder(shapeVec.getAsVector());
    
    imageRaster = im.getData();
    
    blackPixelValue = getBlackPixelValue();
    
    rotateImageUsingRotationBits(false);
    
    setAllZones();
  }
  











  void initializeImage(RenderedImage im)
  {
    if (im == null) {
      return;
    }
    setRenderedImage(im);
    
    sampleModel = im.getSampleModel();
    colorModel = im.getColorModel();
    
    minX = im.getMinX();
    minY = im.getMinY();
    width = im.getWidth();
    height = im.getHeight();
    new Rectangle(minX, minY, width, height);
    
    minTileX = im.getMinTileX();
    maxTileX = (im.getMinTileX() + im.getNumXTiles() - 1);
    minTileY = im.getMinTileY();
    maxTileY = (im.getMinTileY() + im.getNumYTiles() - 1);
    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    tileGridXOffset = im.getTileGridXOffset();
    tileGridYOffset = im.getTileGridYOffset();
  }
  
















  public RenderedImage getscaledImage()
  {
    RenderedImage image = OCRInterface.currentHWObj.getOriginalImage();
    
    if (image == null) {
      return null;
    }
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(scale);
    pb.add(scale);
    pb.add(0.0F);
    pb.add(0.0F);
    
    if (interp == null)
      interp = Interpolation.getInstance(0);
    pb.add(interp);
    


    return JAI.create("scale", pb);
  }
  







  public void reset()
  {
    toBeOrdered = null;
    
    clearActiveZones();
    lastMouseEvent = null;
    


    toBeEdited = null;
    OCRInterface.disableManager.zonesSelected(activeZones.size());
    paintCanvas();
    

    ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
  }
  





































  public boolean selectZone(MouseEvent mouseEvent, Zone startingWith)
  {
    Vector zones = shapeVec.getAsVector();
    Zone curr_zone_1;
    for (Object zone : zones) {
      curr_zone_1 = (Zone)zone;
      curr_zone_1.setSelectedWord(null);
    }
    
    if (mouseEvent.getButton() == 3) {
      return true;
    }
    if ((activeZones.size() > 0) && 
      (activeZones.elementAt(0).doesContain(mouseEvent
      .getX(), mouseEvent.getY(), scale))) {
      if (activeZones.elementAt(0) == toBeEdited) {
        return true;
      }
    }
    
    if ((mouseEvent.getModifiers() & 0x1) == 0)
    {


      if (activeZones.elementAt(0) != null) {
        Zone zone = activeZones.elementAt(0);
        if ((zone.closeTo(new Point(mouseEvent.getX(), mouseEvent.getY()), scale) != null) || 
          (zone.doesLieOnBoundary(mouseEvent.getX(), mouseEvent.getY(), scale)))
          return true;
      }
      if ((activeZones.elementAt(0) != null) && 
        (activeZones.elementAt(0).doesContain(mouseEvent
        .getX(), mouseEvent.getY(), scale))) {
        if (activeZones.elementAt(0) == toBeOrdered) {
          return true;
        }
      }
    }
    


    TreeMap<Double, Zone> distance = new TreeMap(
      new Comparator() {
        public int compare(Double arg0, Double arg1) {
          return arg0.compareTo(arg1);
        }
      });
    for (Object zone : zones) {
      Zone curr_zone_1 = (Zone)zone;
      curr_zone_1.setSpecificColor(null);
      if (OCRInterface.this_interface.getEnableTranslateWorkflow())
        OCRInterface.this_interface.getTranslateWorkflowPanel().resetPairPanel(null);
      boolean isTextClicked = curr_zone_1.clickedText(mouseEvent.getX(), mouseEvent.getY(), scale);
      if ((curr_zone_1.isVisible()) && (isTextClicked)) {
        noShiftClearSelection(mouseEvent);
        
        addToSelected(curr_zone_1);
        
        shapeVec.setActZones(activeZones);
        


        if ((isTextClicked) && (mouseEvent.getClickCount() == 2)) {
          String contents = curr_zone_1.getContents();
          int direction = new BidiString(contents, 0).getDirection();
          
          int start = 0;
          int end = 0;
          
          if (direction == 0) {
            OCRInterface.this_interface.word_left_action();
          } else if (direction == 1) {
            OCRInterface.this_interface.word_right_action();
          }
          if (caret != contents.length()) {
            caret -= 1;
          }
          start = contents.length() - caret;
          end = contents.indexOf(' ', start);
          if (end == -1) {
            end = contents.length();
          }
          System.out.println("length/start/end: " + contents.length() + "/" + start + "/" + end);
          curr_zone_1.setSelectedWord(start, end);

        }
        else if ((isTextClicked) && (mouseEvent.getClickCount() == 3)) {
          String contents = curr_zone_1.getContents();
          int start = 0;
          int end = 0;
          
          caret = contents.length();
          
          end = contents.length();
          
          curr_zone_1.setSelectedWord(start, end);
        }
        
        paintCanvas();
        return true;
      }
      
      boolean curr_1_not_contain = (!curr_zone_1.isVisible()) || (!curr_zone_1
        .doesContain(mouseEvent.getX(), mouseEvent.getY(), scale));
      if (!curr_1_not_contain)
      {

        if ((activeZones.contains(curr_zone_1)) && 
          ((mouseEvent.getModifiers() & 0x1) != 0) && 
          (!activeZones.isEmpty()) && 
          (((Zone)activeZones.firstElement()).isGroup()))
        {
          if (!curr_zone_1.equals(activeZones.firstElement()))
          {

            saveCurrentState();
            ((Group)activeZones.firstElement()).removeFromGroup(curr_zone_1);
            ((Group)activeZones.firstElement()).resizeGroupBox();
            activeZones.remove(curr_zone_1);
            
            return true;
          }
        } else {
          if ((activeZones.contains(curr_zone_1)) && 
            ((mouseEvent.getModifiers() & 0x1) != 0)) {
            activeZones.remove(curr_zone_1);
            return true;
          }
          


          if ((activeZones.size() > 1) && 
            (activeZones.contains(curr_zone_1)) && 
            ((mouseEvent.getModifiers() & 0x2) != 0)) {
            activeZones.remove(curr_zone_1);
            return true;
          }
          


          if ((!activeZones.contains(curr_zone_1)) && 
            ((mouseEvent.getModifiers() & 0x1) != 0) && 
            (!activeZones.isEmpty()) && 
            (((Zone)activeZones.firstElement()).isGroup()) && 
            (!curr_zone_1.isGroup()))
          {
            saveCurrentState();
            ((Group)activeZones.firstElement()).addToGroup(curr_zone_1);
            ((Group)activeZones.firstElement()).resizeGroupBox();
            addToSelected(curr_zone_1);
            
            return true;
          }
          
          distance.put(Double.valueOf(curr_zone_1.getDistance(mouseEvent.getX(), 
            mouseEvent.getY(), scale)), curr_zone_1);
          
          curr_zone_1 = null;
        } }
    }
    noShiftClearSelection(mouseEvent);
    
    if (distance.isEmpty()) {
      distance = null;
      zones = null;
      return false;
    }
    
    addToSelected((Zone)distance.get(distance.firstKey()));
    
    shapeVec.setActZones(activeZones);
    
    paintCanvas();
    
    return true;
  }
  
  private void noShiftClearSelection(MouseEvent mouseEvent)
  {
    if (((mouseEvent.getModifiers() & 0x1) == 0) && 
      ((mouseEvent.getModifiers() & 0x8) == 0) && 
      ((mouseEvent.getModifiers() & 0x2) == 0)) {
      clearActiveZones();
    }
  }
  


















  public void processMousePressed(MouseEvent e, Zone selZoneIn)
  {
    System.out.println("processMousePressed currOppmode: " + OCRInterface.currOppmode);
    





    Zone zone2 = activeZones.elementAt(0);
    
    if ((zone2 != null) && ((zone2 instanceof PolygonZone)) && (!isIncomplete)) {
      if ((e.isControlDown()) && (activeZones.size() == 1)) {
        saveCurrentState();
        ((PolygonZone)zone2).addPoint();
        paintCanvas();
      } else {
        ((PolygonZone)zone2).selectPoint(e.getX(), e.getY(), scale);
        if (((PolygonZone)zone2).isPtSelected()) {
          zonePtSelected = ((PolygonZone)zone2);
        } else {
          zonePtSelected = null;
        }
      }
      PolygonZone p = (PolygonZone)zone2;
      
      if (p.getClosestLineIndex(e.getPoint(), scale) != -1) {
        toBeEdited = zone2;
        ocrIF.setOppmode(3);
        initiateEditMode(e);
        return;
      }
    }
    
    if ((OCRInterface.currOppmode == 0) && (!e.isMetaDown()) && (e.getClickCount() == 1) && (!SwingUtilities.isLeftMouseButton(e))) {}
    


    DeveloperView.print("[ImageDisplay:processMousePressed]");
    
    if (OCRInterface.currOppmode == 8) {
      return;
    }
    




    lastZoneSelected = activeZones.elementAt(0);
    
    if (lastZoneSelected != null) {
      lastZoneSelected.lineSelected = -5;
    }
    
    if ((OCRInterface.currOppmode != 15) && 
      (OCRInterface.currOppmode != 16) && 
      (OCRInterface.currOppmode != 2) && 
      (OCRInterface.currOppmode != 20)) {
      selectZone(e, selZoneIn);
      OCRInterface.this_interface.updateCommentsWindow(false);
    }
    
    if (activeZones.elementAt(0) != null)
    {

      selected_has_contents = activeZones.elementAt(0).hasContents();
    } else {
      selected_has_contents = false;
    }
    
    if ((toBeEdited != null) && (activeZones.elementAt(0) != toBeEdited)) {
      toBeEdited = null;
    }
    

    if ((e.getModifiers() & 0x10) != 0)
    {


      if ((OCRInterface.currOppmode != 12) && (toBeDrawn != null))
        toBeDrawn = null;
      if ((OCRInterface.currOppmode != 14) && (toBeErased != null))
        toBeErased = null;
      if ((OCRInterface.currOppmode != 17) && (toBeOrdered != null)) {
        toBeOrdered = null;
      }
      if (OCRInterface.currOppmode == 20) {
        if (e.getClickCount() == 1) {
          if ((startMeasureMouseEvent == null) || (stopMeasure)) {
            stopMeasure = false;
            startMeasureMouseEvent = null;
            endMeasureMouseEvent = null;
            startMeasureMouseEvent = e;
          }
        } else if (e.getClickCount() == 2) {
          stopMeasure = true;
          endMeasureMouseEvent = e;
        }
      }
      else if (OCRInterface.currOppmode == 0)
      {
        selectionZone = new Zone(true, e.getX(), e.getY(), scale);
        


        if (activeZones.size() >= 1) {
          Zone currSelectedZone = activeZones.elementAt(0);
          
          Point p = new Point(e.getPoint());
          saveCurrentState();
          if (currSelectedZone.hasAttribute("segmentation")) {
            if (currSelectedZone.getAttributeValue("segmentation").equals("line")) {
              currSelectedZone.setHoriz(4);
            }
            else
            {
              currSelectedZone.setHoriz(0);
            }
          }
          
          if (currSelectedZone.getHoriz() != 4) {
            if (currSelectedZone.checkLineSelected((int)(x / scale), scale)) {
              zoneLineSelected = currSelectedZone;
            }
            else
              zoneLineSelected = null;
          } else if (currSelectedZone.getHoriz() == 4)
          {
            if (currSelectedZone.checkLineSelected((int)(y / scale), scale)) {
              zoneLineSelected = activeZones.elementAt(0);
            }
            else
              zoneLineSelected = null;
          } else {
            zoneLineSelected = null;
          }
          
          if ((zoneLineSelected != null) && (e.getClickCount() == 2) && (e.isAltDown())) {
            saveCurrentState();
            zoneLineSelected.deleteSelectedOffset();
          }
          
        }
        

      }
      else if (OCRInterface.currOppmode == 1) {
        if (activeZones.size() > 0)
          tSelectFirst = ((Zone)activeZones.get(0));
      } else if ((OCRInterface.currOppmode != 5) || 
        (activeZones.size() <= 0))
      {
        Map.Entry<String, Map<String, TypeAttributeEntry>> f;
        
        Map.Entry<String, TypeAttributeEntry> f2;
        
        if (OCRInterface.currOppmode == 2) {
          String newZonesLabel = ocrIF.tbdPane.getSelectedAssignID();
          
          Zone newZone = new Zone(e.getX(), e.getY(), scale);
          

          boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
          if (cc) {
            selectionZone = newZone;
            OCRInterface.currOppmode = 18;
            return;
          }
          

          newZone.setZoneType(newZonesLabel);
          

          Iterator localIterator1 = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator();
          Iterator localIterator2;
          for (; localIterator1.hasNext(); 
              

              localIterator2.hasNext())
          {
            f = (Map.Entry)localIterator1.next();
            

            localIterator2 = ((Map)f.getValue()).entrySet().iterator(); continue;f2 = (Map.Entry)localIterator2.next();
            if (((String)f.getKey()).equals(newZone.getZoneType())) {
              newZone.setAttributeValue((String)f2.getKey(), ((TypeAttributeEntry)f2.getValue()).getDefaultValue());
            }
          }
          

          if (OCRInterface.currDoc == null)
          {
            nulldoc.add(newZone);
          }
          





          activeZones.setElementAt(newZone, 0);
          newZone = null;





        }
        else if ((OCRInterface.currOppmode == 3) && 
          (activeZones.size() > 0)) {
          if (toBeEdited == null) {
            toBeEdited = activeZones.elementAt(0);
          }
          initiateEditMode(e);

        }
        else if ((OCRInterface.currOppmode == 4) && 
          (activeZones.size() > 0)) {
          if (toBeEdited == null)
            toBeEdited = activeZones.elementAt(0);
          initMoveMode(e);
        } else if (OCRInterface.currOppmode != 12)
        {

          if ((OCRInterface.currOppmode == 7) && 
            (activeZones.size() > 0)) {
            activeZones.elementAt(0).createSplitLine(e.getX(), e
              .getY(), scale);
          }
          else if (OCRInterface.currOppmode == 9)
          {


            OCRInterface.getAttsConfigUtil().createDL_TEXTLINEGT();
            String type = "DL_TEXTLINEGT";
            
            Zone newZone = new Zone(e.getX(), e.getY(), scale);
            newZone.setZoneType(type);
            
            activeZones.setElementAt(newZone, 0);
            newZone = null;



          }
          else if (OCRInterface.currOppmode == 15) {
            String newZonesLabel = ocrIF.tbdPane.getSelectedAssignID();
            OrientedBox newZone = (OrientedBox)activeZones.elementAt(0);
            if ((newZone != null) && (isIncomplete)) {
              isIncomplete = false;
              System.out.println("newZone.isIncomplete");
            } else if ((newZone != null) && (!isIncomplete)) {
              clearActiveZones();
              ocrIF.setOppmode(15);
            } else if (newZone == null) {
              newZone = new OrientedBox(e.getX(), e.getY(), scale, 0.0D);
              
              newZone.setZoneType(newZonesLabel);
              

              f = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator();
              for (; f.hasNext(); 
                  

                  f2.hasNext())
              {
                Map.Entry<String, Map<String, TypeAttributeEntry>> f = (Map.Entry)f.next();
                

                f2 = ((Map)f.getValue()).entrySet().iterator(); continue;Object f2 = (Map.Entry)f2.next();
                
                if (((String)f.getKey()).equals(newZone.getZoneType())) {
                  newZone.setAttributeValue((String)((Map.Entry)f2).getKey(), ((TypeAttributeEntry)((Map.Entry)f2).getValue()).getDefaultValue());
                }
              }
              

              if (OCRInterface.currDoc == null) {
                nulldoc.add(newZone);
              }
              activeZones.setElementAt(newZone, 0);
              newZone = null;
            }
            

          }
          else if (OCRInterface.currOppmode == 16) {
            PolygonZone zone = (PolygonZone)activeZones.elementAt(0);
            
            if ((zone != null) && (isIncomplete) && (zone.getNumPoints() == 3)) {
              insertZone(zone, e);
              zone.setAttributeValue("polygon", zone.getPoints());
              ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
            }
            else if ((zone != null) && (!isIncomplete)) {
              clearActiveZones();
              ocrIF.setOppmode(16);
            } else if (zone == null) {
              String newZonesLabel = ocrIF.tbdPane.getSelectedAssignID();
              
              zone = new PolygonZone(e.getX(), e.getY(), scale, null);
              isIncomplete = true;
              zone.setZoneType(newZonesLabel);
              

              for (f = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator(); f.hasNext(); 
                  f2.hasNext())
              {
                Map.Entry<String, Map<String, TypeAttributeEntry>> f = (Map.Entry)f.next();
                f2 = ((Map)f.getValue()).entrySet().iterator(); continue;Object f2 = (Map.Entry)f2.next();
                if (((String)f.getKey()).equals(zone.getZoneType())) {
                  zone.setAttributeValue((String)((Map.Entry)f2).getKey(), ((TypeAttributeEntry)((Map.Entry)f2).getValue()).getDefaultValue());
                }
              }
              

              if (OCRInterface.currDoc == null)
              {
                nulldoc.add(zone);
              }
              activeZones.setElementAt(zone, 0);
              zone = null;
              
              ocrIF.setCurrentOppMode(16);
            }
            
            if ((zone != null) && (isIncomplete) && (e.getClickCount() == 1)) {
              saveCurrentState();
              zone.createSide(e.getX(), e.getY(), scale);
            }
          } else if (OCRInterface.currOppmode == 19) {
            System.out.println("GROUP_MODE");
            String newZonesLabel = ocrIF.tbdPane.getSelectedAssignID();
            
            Group newGroupZone = new Group(e.getX(), e.getY(), scale);
            
            newGroupZone.setZoneType(newZonesLabel);
            

            f = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator();
            for (; f.hasNext(); 
                

                f2.hasNext())
            {
              Map.Entry<String, Map<String, TypeAttributeEntry>> f = (Map.Entry)f.next();
              

              f2 = ((Map)f.getValue()).entrySet().iterator(); continue;Object f2 = (Map.Entry)f2.next();
              if (((String)f.getKey()).equals(newGroupZone.getZoneType())) {
                newGroupZone.setAttributeValue((String)((Map.Entry)f2).getKey(), ((TypeAttributeEntry)((Map.Entry)f2).getValue()).getDefaultValue());
              }
            }
            

            if (OCRInterface.currDoc == null) {
              nulldoc.add(newGroupZone);
            }
            activeZones.setElementAt(newGroupZone, 0);
            newGroupZone = null;
          }
        }
      }
    }
    




    paintCanvas();
  }
  




  private void initMoveMode(MouseEvent e)
  {
    toBeEdited = activeZones.elementAt(0);
    
    activeZones.elementAt(0)
      .setMoveStart(e.getX(), e.getY(), scale);
    
    saveCurrentState();
  }
  
  private void initMultMoveMode(MouseEvent e) {
    ocrIF.setCurrentOppMode(4);
    
    for (Object zone : activeZones) {
      ((Zone)zone).setMoveStart(e.getX(), e.getY(), scale);
    }
    saveCurrentState();
  }
  









  private void initiateEditMode(MouseEvent e)
  {
    if (toBeEdited != null)
    {



      if ((toBeEdited instanceof PolygonZone))
      {
        if (((PolygonZone)toBeEdited).closeTo(e.getPoint(), scale) != null)
        {
          saveCurrentState();
          if (((PolygonZone)toBeEdited).isPtSelected()) {
            ((PolygonZone)toBeEdited).resetSelectedSide();
            ((PolygonZone)toBeEdited).selectPoint(e.getX(), e.getY(), scale);
          }
          else {
            ((PolygonZone)toBeEdited).resetSelPt();
            ((PolygonZone)toBeEdited).editSelectedPoint(e.getX(), e.getY(), scale);
          }
          
          if (((PolygonZone)toBeEdited).isPtSelected()) {
            zonePtSelected = ((PolygonZone)toBeEdited);
          } else {
            zonePtSelected = null;
          }
        }
        else if (((PolygonZone)toBeEdited).getSelectedSideIndex() == -1) {
          saveCurrentState();
          ((PolygonZone)toBeEdited).setSelectedSideIndex(((PolygonZone)toBeEdited).getClosestLineIndex(e.getPoint(), scale));
          ((PolygonZone)toBeEdited).setLastMouseEvent(e.getPoint());
        }
        else if (((PolygonZone)toBeEdited).getSelectedSideIndex() == -1)
        {





          toBeEdited = null;
        }
      }
      else if (toBeEdited.isGroup()) {
        if (toBeEdited.doesLieOnBoundary(e.getX(), e.getY(), scale)) {
          saveCurrentState();
          this_interfacezoneBeingResized = true;
          toBeEdited.selectCorener(e.getX(), e.getY(), scale);
          toBeEdited.editSelectedCorener(e.getX(), e.getY(), scale);
        }
        else {
          initMultMoveMode(e);

        }
        

      }
      else if ((toBeEdited.doesLieOnBoundary(e.getX(), e.getY(), scale)) || 
        (((OrientedBox)toBeEdited).closeTo(new Point(e.getX(), e.getY()), scale) != null)) {
        saveCurrentState();
        this_interfacezoneBeingResized = true;
        toBeEdited.selectCorener(e.getX(), e.getY(), scale);
        toBeEdited.editSelectedCorener(e.getX(), e.getY(), scale);
      }
      else
      {
        toBeEdited = null;
      }
    }
  }
  















  public final void mouseMoved(MouseEvent e)
  {
    lastMouseMovedEvent = e;
    Point p = e.getPoint();
    setFocusable(true);
    



    isControlDown = e.isControlDown();
    
    setMouseCoordinate(x, y);
    
    int currOppMode = OCRInterface.currOppmode;
    
    if (isSpecialCase()) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else if (activeZones.size() == 1) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else if ((activeZones.size() > 1) && (((Zone)activeZones.firstElement()).isGroup())) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else {
      changeMouseIcon(null, e);
    }
    


    paintCanvas();
    
    if (OCRInterface.currOppmode == 20) {
      if (!stopMeasure) {
        endMeasureMouseEvent = e;
      }
    }
    else if ((OCRInterface.currOppmode == 15) && (activeZones.size() != 0)) {
      if (((OrientedBox)activeZones.elementAt(0)).dlGetZoneWidth() != 1) {
        OrientedBox cur_zone = (OrientedBox)activeZones.elementAt(0);
        if ((cur_zone != null) && (isIncomplete)) {
          cur_zone.setBoxHeight(e.getX(), e.getY(), scale);
        }
      }
      else {
        clearActiveZones();
      }
    }
    
    if ((OCRInterface.currOppmode == 16) && (activeZones.size() != 0))
    {
      PolygonZone cur_zone = (PolygonZone)activeZones.elementAt(0);
      
      if ((cur_zone != null) && (isIncomplete))
      {

        cur_zone.drawSide(e.getX(), e.getY(), scale);
        paintCanvas();
      }
    }
    


    if ((currOppMode == 7) || 
      (selected_has_contents) || 
      (currOppMode == 12) || 
      (currOppMode == 14))
    {
      paintCanvas();
    }
  }
  
  public boolean allowEdit()
  {
    if (OCRInterface.currDoc == null) {
      allowEdit = true;
    } else
      allowEdit = ((allowEdit) || (ocrIF.isXmlWriteable(OCRInterface.currDoc.getFilePath())));
    if (!allowEdit) {
      allowEdit = (JOptionPane.showConfirmDialog(null, 
        "The XML file for this image is not writeable.\nChanges will not be saved.\n\nContinue editing anyway?", 
        

        "Cannot save", 
        0, 
        2) == 0);
      ocrIF.setCurrentOppMode(0);
    }
    return allowEdit;
  }
  










  public final void mousePressed(MouseEvent e)
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        OCRInterface.this_interface.requestFocusInWindow();
      }
    });
    




    if (e.isPopupTrigger()) {
      showPopUpMenu(e);
    }
    DeveloperView.println("[ImageDisplay:mousePressed] ");
    if (SwingUtilities.isRightMouseButton(e))
    {
      x = e.getX();
      y = e.getY();
      
      JScrollBar horiz = currentHWObjpictureScrollPane.getHorizontalScrollBar();
      JScrollBar vert = currentHWObjpictureScrollPane.getVerticalScrollBar();
      
      xSc = horiz.getValue();
      ySc = vert.getValue();
    }
    
    int currOppMode = OCRInterface.currOppmode;
    

    if (((currOppMode == 2) || (currOppMode == 9)) && 
      (rotated)) {
      int answer = JOptionPane.showConfirmDialog(
        null, 
        "Rotated image must be saved before continuing. \n Save Now?", 
        "Save Now?", 0);
      
      if (answer == 0) {
        saveImage();
      } else {
        return;
      }
    }
    










    lastMouseEvent = e;
    










    ocrIF.requestFocusInWindow();
    






    processMousePressed(e, null);
  }
  


  public final void mouseDragged(MouseEvent e)
  {
    DeveloperView.println("[ImageDisplay:mouseDragged]");
    
    if (OCRInterface.currOppmode == 20) {
      endMeasureMouseEvent = e;
    }
    
    if (isSpecialCase()) {
      if (zoneLineSelected != null) {
        ocrIF.setOppmode(10);
        zoneLineSelected.moveSelectedLine(e, scale);
      }
      

    }
    else if ((activeZones.size() > 1) && 
      (((Zone)activeZones.firstElement()).isGroup()) && 
      (OCRInterface.currOppmode == 0) && 
      (lastMouseEvent.getID() == 501))
    {
      if (toBeEdited == null) {
        toBeEdited = activeZones.elementAt(0);
        

        ocrIF.setOppmode(3);
        

        initiateEditMode(lastMouseEvent);
      }
    }
    
    if ((activeZones.size() > 0) && (OCRInterface.currOppmode == 0) && 
      ((e.getModifiers() & 0x1) != 0))
    {
      initMultMoveMode(e);
    }
    


    if ((activeZones.size() == 1) && 
      (OCRInterface.currOppmode == 10)) {
      if (zoneLineSelected != null) {
        ocrIF.setOppmode(10);
        zoneLineSelected.moveSelectedLine(e, scale);
      }
      
    }
    else if ((activeZones.size() == 1) && (OCRInterface.currOppmode == 0) && 
      (lastMouseEvent.getID() == 501)) {
      Zone zoneSelected = (Zone)activeZones.firstElement();
      

      if (zoneLineSelected != null) {
        ocrIF.setOppmode(10);
        zoneLineSelected.moveSelectedLine(e, scale);

      }
      else if (((zoneSelected instanceof PolygonZone)) && (
        (((PolygonZone)zoneSelected).closeTo(lastMouseEvent.getPoint(), scale) != null) || 
        (((PolygonZone)zoneSelected).getClosestLineIndex(lastMouseEvent.getPoint(), scale) != -1)))
      {
        if (toBeEdited == null) {
          toBeEdited = activeZones.elementAt(0);
          

          ocrIF.setOppmode(3);
          

          initiateEditMode(lastMouseEvent);
        }
        

      }
      else if ((zoneSelected.doesLieOnBoundary(lastMouseEvent.getX(), 
        lastMouseEvent.getY(), scale)) || (
        ((zoneSelected instanceof OrientedBox)) && 
        (((OrientedBox)zoneSelected).closeTo(new Point(lastMouseEvent.getX(), lastMouseEvent.getY()), scale) != null)))
      {

        if (toBeEdited == null) {
          toBeEdited = activeZones.elementAt(0);
          
          ocrIF.setOppmode(3);
          

          initiateEditMode(lastMouseEvent);
        }
        

      }
      else if (zoneSelected.doesContain(lastMouseEvent.getX(), 
        lastMouseEvent.getY(), scale)) {
        if (!zoneSelected.doesLieOnBoundary(lastMouseEvent.getX(), 
          lastMouseEvent.getY(), scale))
        {




          ocrIF.setCurrentOppMode(4);
          

          initMoveMode(e);
        }
      }
    }
    lastMouseEvent = e;
    lastMouseMovedEvent = e;
    



    if ((e.getModifiers() & 0x10) == 0)
    {


      return;
    }
    int eXScaled = (int)(e.getX() / scale);
    int eYScaled = (int)(e.getY() / scale);
    if (((OCRInterface.currOppmode == 3) || 
    
      (OCRInterface.currOppmode == 2) || (OCRInterface.currOppmode == 9)) && (
      (eYScaled < 0) || (e.getY() > im.getHeight()) || (eXScaled < 0) || 
      (e.getX() > im.getWidth())))
    {

      if ((toBeEdited == null) || (!(toBeEdited instanceof OrientedBox)) || (toBeEdited).selectedCorner != 2))
      {
        return;
      }
    }
    





    if (OCRInterface.currOppmode == 0)
    {







      ZonesManager vecToSelectFrom = shapeVec;
      
      selectionZone.creationDrag(e.getX(), e.getY(), scale);
      






      Vector zonesUnderSelection = vecToSelectFrom
        .getIntersectingZones(selectionZone);
      
      for (Iterator iter = zonesUnderSelection.iterator(); iter.hasNext();) {
        Zone z = (Zone)iter.next();
        






        if (!activeZones.contains(z)) {
          addToSelected(z);
        }
      }
      


      paintCanvas();
    } else if (OCRInterface.currOppmode == 1)
    {






      ZonesManager vecToSelectFrom = shapeVec;
      
      if (tSelectFirst != null)
      {





        for (int i = vecToSelectFrom.size() - 1; i >= 0; i--) {
          Zone curZone = (Zone)vecToSelectFrom.get(i);
          if ((curZone.doesContain(e.getX(), e.getY(), scale)) && 
            (curZone.isVisible())) {
            tSelectLast = curZone;
            break;
          }
        }
        
        if (tSelectLast != null)
        {
          clearActiveZones();
          for (i = vecToSelectFrom.indexOf(tSelectFirst); 
              i <= vecToSelectFrom.lastIndexOf(tSelectLast); i++)
          {

            addToSelected((Zone)vecToSelectFrom.get(i));
          }
          
          paintCanvas();
        }
      } else {
        selectZone(e, null);
        if (activeZones.size() > 0) {
          tSelectFirst = ((Zone)activeZones.get(0));
        }
        
      }
      
    }
    else if (OCRInterface.currOppmode == 2)
    {





      Zone z = activeZones.elementAt(0);
      if (z != null) {
        z.creationDrag(e.getX(), e.getY(), scale);
        isIncomplete = true;
      }
    }
    else if (OCRInterface.currOppmode == 9) {
      Zone z = activeZones.elementAt(0);
      if (z != null) {
        if (isIncomplete)
          insertZone(activeZones.elementAt(0), e);
        z.creationDrag(e.getX(), e.getY(), scale);
        isIncomplete = true;
      }
    } else if ((OCRInterface.currOppmode == 4) && (activeZones.size() > 0)) {
      moveZones(e);
      
      ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);

    }
    else if (OCRInterface.currOppmode == 3) {
      if (toBeEdited != null) {
        activeZones.add(toBeEdited);
        if ((toBeEdited instanceof PolygonZone))
        {

          ((PolygonZone)toBeEdited).editSelectedPoint(e.getX(), e.getY(), scale);
          ((PolygonZone)toBeEdited).moveSelectedSide(e.getPoint(), scale, e.isShiftDown());
          ((PolygonZone)toBeEdited).setLastMouseEvent(e.getPoint());
        }
        else {
          toBeEdited.editSelectedCorener(e.getX(), e.getY(), scale);
        }
        ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
      }
      
    }
    else if ((OCRInterface.currOppmode == 7) && 
      (activeZones.size() > 0)) {
      activeZones.elementAt(0).createSplitLine(e.getX(), e
        .getY(), scale);


    }
    else if (OCRInterface.currOppmode == 15)
    {
      OrientedBox z = (OrientedBox)activeZones.elementAt(0);
      if ((z != null) && ((z instanceof OrientedBox)) && (isIncomplete))
      {



        z.creationDrag(e.getX(), e.getY(), scale);
      }
    }
    else if (OCRInterface.currOppmode == 18) {
      selectionZone.creationDrag(e.getX(), e.getY(), scale);
      clearActiveZones();
    } else if (OCRInterface.currOppmode == 19) {
      Group groupZone = (Group)activeZones.elementAt(0);
      groupZone.deselectZones();
      if (groupZone != null) {
        groupZone.creationDrag(e.getX(), e.getY(), scale);
        isIncomplete = true;
        


        ZonesManager vecToSelectFrom = shapeVec;
        
        groupZone.creationDrag(e.getX(), e.getY(), scale);
        
        Vector zonesUnderSelection = vecToSelectFrom
          .getIntersectingZones(groupZone);
        
        for (Iterator iter = zonesUnderSelection.iterator(); iter.hasNext();) {
          Zone z = (Zone)iter.next();
          groupZone.addToGroup(z);
        }
        addToSelected(groupZone);
        ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
        paintCanvas();
      }
    }
    

    if (OCRInterface.currOppmode == 2) {
      setMouseCoordinate((DLZone)activeZones.firstElement());
    } else if (OCRInterface.currOppmode == 0) {
      setMouseCoordinate(selectionZone);
    } else {
      setMouseCoordinate(e.getX(), e.getY());
    }
    
    if (isSpecialCase()) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else if ((OCRInterface.currOppmode == 3) && (toBeEdited != null)) {
      changeMouseIcon(toBeEdited, e);
    } else if (activeZones.size() == 1) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else if ((activeZones.size() > 1) && (((Zone)activeZones.firstElement()).isGroup())) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else {
      changeMouseIcon(null, e);
    }
    paintCanvas();
  }
  
  private void changeMouseIcon(Object object, MouseEvent e) {
    Point pt = e.getPoint();
    int currOppmode = OCRInterface.currOppmode;
    Zone z = (Zone)object;
    
    boolean visible = false;
    if ((object != null) && (
      (currOppmode == 3) || 
      (currOppmode == 4) || 
      (currOppmode == 0) || 
      (currOppmode == 10) || 
      (currOppmode == 9))) {
      visible = z.isVisible();
    } else if (currOppmode == 8) {
      if ((e.getID() == 506) || (e.getID() == 501)) {
        ImageReaderDrawer.picture.setCursor(closed);
        Toolkit.getDefaultToolkit().sync();
      } else if ((e.getID() == 503) || 
        (e.getID() == 502)) {
        ImageReaderDrawer.picture.setCursor(open);
        Toolkit.getDefaultToolkit().sync();
      }
      return;
    }
    
    if (!visible) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(0));
      Toolkit.getDefaultToolkit().sync();
      return;
    }
    
    if ((z instanceof OrientedBox)) {
      ((OrientedBox)z).changeMouseIcon(e, scale);
    }
    else if ((z instanceof PolygonZone)) {
      try {
        changePolyZoneIcon((PolygonZone)z, pt);
      } catch (Exception exc) {
        exc.printStackTrace();
      }
    } else {
      changeRegZoneIcon(z, pt, e);
    }
  }
  
  private void changePolyZoneIcon(PolygonZone z, Point pt)
    throws IOException
  {
    if (isControlDown) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(1));
      Toolkit.getDefaultToolkit().sync();
    } else if (z.closeTo(pt, scale) != null) {
      ClassLoader loader = getClass().getClassLoader();
      
      Toolkit tk = Toolkit.getDefaultToolkit();
      Image ptMoveImage = tk.getImage(loader.getResource("images/ptmove.gif"));
      Cursor ptMoveCur = tk.createCustomCursor(ptMoveImage, new Point(16, 16), "ptMoveCur");
      
      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      
      Toolkit.getDefaultToolkit().sync();
    } else if (z.getClosestLineIndex(pt, scale) != -1) {
      int index = z.getClosestLineIndex(pt, scale);
      Line2D side = z.getSide(index);
      double sideOrientation = z.getSideOrientation(side.getP1(), side.getP2());
      double pi8 = 0.39269908169872414D;
      
      if (((sideOrientation >= -pi8) && 
        (sideOrientation < pi8)) || 
        (sideOrientation >= 7.0D * pi8) || 
        (sideOrientation < -7.0D * pi8)) {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(8));
        
        Toolkit.getDefaultToolkit().sync();
      }
      else if (((sideOrientation >= pi8) && 
        (sideOrientation < 3.0D * pi8)) || (
        (sideOrientation >= -7.0D * pi8) && 
        (sideOrientation < -5.0D * pi8))) {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(6));
        Toolkit.getDefaultToolkit().sync();

      }
      else if (((sideOrientation >= 3.0D * pi8) && 
        (sideOrientation < 5.0D * pi8)) || (
        (sideOrientation >= -5.0D * pi8) && 
        (sideOrientation < -3.0D * pi8))) {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(10));
        Toolkit.getDefaultToolkit().sync();

      }
      else if (((sideOrientation >= 5.0D * pi8) && 
        (sideOrientation < 7.0D * pi8)) || (
        (sideOrientation >= -3.0D * pi8) && 
        (sideOrientation < -pi8))) {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(4));
        Toolkit.getDefaultToolkit().sync();


      }
      


    }
    else if (z.contains(pt)) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(13));
      
      Toolkit.getDefaultToolkit().sync();
    }
    else
    {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(0));
      Toolkit.getDefaultToolkit().sync();
    }
  }
  
  private void changeRegZoneIcon(Zone z, Point pt, MouseEvent e)
  {
    ClassLoader loader = getClass().getClassLoader();
    Rectangle zone = new Rectangle(z.get_lt_x(), z.get_lt_y(), z
      .get_width(), z.get_height());
    Point top_left = new Point(z.get_lt_x(), z.get_lt_y());
    Point top_right = new Point(z.get_rb_x(), z.get_lt_y());
    Point bottom_left = new Point(z.get_lt_x(), z.get_rb_y());
    Point bottom_right = new Point(z.get_rb_x(), z.get_rb_y());
    Line2D north = new Line2D.Float(top_left, top_right);
    Line2D south = new Line2D.Float(bottom_left, bottom_right);
    Line2D east = new Line2D.Float(top_right, bottom_right);
    Line2D west = new Line2D.Float(top_left, bottom_left);
    Point p = new Point((int)(x / scale), (int)(y / scale));
    
    Point corner = z.closeTo(pt, scale);
    

    Toolkit tk = Toolkit.getDefaultToolkit();
    Image ptMoveImage = tk.getImage(loader.getResource("images/ptmove.gif"));
    Cursor ptMoveCur = tk.createCustomCursor(ptMoveImage, new Point(16, 16), "ptMoveCur");
    

    int buffer = (int)(7.0F / scale);
    

    if ((isControlDown) && 
      (activeZones != null) && 
      (activeZones.elementAt(0).offsetsReady())) {
      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(0));
      Toolkit.getDefaultToolkit().sync();


    }
    else if (((z.isOnALine(x)) || (isControlDown) || 
      (OCRInterface.currOppmode == 10)) && (activeZones.elementAt(0).getHoriz() != 4)) {
      ImageReaderDrawer.picture.setCursor(textlineMoveCursor);
      Toolkit.getDefaultToolkit().sync();


    }
    else if (((z.isOnALine(y, "yaxis")) || (isControlDown) || 
      (OCRInterface.currOppmode == 10)) && (activeZones.elementAt(0).getHoriz() == 4)) {
      ImageReaderDrawer.picture.setCursor(textlineMoveHorizCursor);
      Toolkit.getDefaultToolkit().sync();



    }
    else if ((corner != null) && (corner.equals(top_left)))
    {
      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      
      Toolkit.getDefaultToolkit().sync();





    }
    else if ((corner != null) && (corner.equals(bottom_right)))
    {

      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      

      Toolkit.getDefaultToolkit().sync();
    } else if ((corner != null) && (corner.equals(bottom_left)))
    {
      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      

      Toolkit.getDefaultToolkit().sync();
    } else if ((corner != null) && (corner.equals(top_right)))
    {
      ImageReaderDrawer.picture.setCursor(ptMoveCur);
      

      Toolkit.getDefaultToolkit().sync();
    } else if ((north.ptSegDist(p) >= 0.0D) && (north.ptSegDist(p) <= buffer))
    {

      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(8));
      Toolkit.getDefaultToolkit().sync();
    } else if ((south.ptSegDist(p) >= 0.0D) && (south.ptSegDist(p) <= buffer))
    {

      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(9));
      Toolkit.getDefaultToolkit().sync();
    } else if ((east.ptSegDist(p) >= 0.0D) && (east.ptSegDist(p) <= buffer))
    {

      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(11));
      Toolkit.getDefaultToolkit().sync();
    } else if ((west.ptSegDistSq(p) >= 0.0D) && (west.ptSegDist(p) <= buffer))
    {

      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(10));
      Toolkit.getDefaultToolkit().sync();
    } else if (zone.contains(p)) {
      if ((z.isGroup()) && ((e.getModifiers() & 0x1) != 0)) {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(0));
      } else {
        ImageReaderDrawer.picture.setCursor(
          Cursor.getPredefinedCursor(13));
      }
      Toolkit.getDefaultToolkit().sync();

    }
    else
    {

      ImageReaderDrawer.picture.setCursor(
        Cursor.getPredefinedCursor(0));
      Toolkit.getDefaultToolkit().sync();
    }
  }
  

  public final void mouseClicked(MouseEvent e)
  {
    if ((e.getClickCount() == 2) && (OCRInterface.currOppmode == 16)) {
      PolygonZone newZone = (PolygonZone)activeZones.elementAt(0);
      newZone.recalculate(false);
      
      if ((newZone != null) && (isIncomplete) && (newZone.getNumPoints() >= 3)) {
        String selectedText = ocrIF.getETextWindowSelectedText();
        
        if ((selectedText != null) && (!selectedText.equals(""))) {
          newZone.setContents(selectedText);
        }
        
        if ((newZone.hasContents()) && 
          (ocrIF.getShowContentWindowOnDlTextLineCreate())) {
          actionRightClickDLTextLine(newZone, e);
        }
        
        String coordStr = newZone.getPoints();
        
        newZone.setAttributeValue("polygon", coordStr);
        
        if (!ocrIF.toolbar.getStickyMode())
        {
          ocrIF.setOppmode(0);
          ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
        }
        else {
          clearActiveZones();
        }
        isIncomplete = false;
        newZone.recalculate(false);
        
        boolean shrink = OCRInterface.this_interface.getApplyExpandShrink();
        if (shrink) {
          boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
          if (!cc) {
            currentHWObjcurr_canvas.setIdenticalZoneWasCreated(false);
            saveCurrentState();
            newZone.adjust();
          }
        }
      }
      else if ((newZone != null) && (!isIncomplete)) {
        clearActiveZones();
        ocrIF.setOppmode(0);
      }
      if (!ocrIF.toolbar.getStickyMode()) {
        ocrIF.setOppmode(0);
        ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
      } else {
        clearActiveZones();
      }
      

      boolean rle = ocrIF.tbdPane.data_panel.t_window.isRLESelected();
      boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
      createRLE_CC(newZone, rle, cc, true);
    }
    
    if ((e.getClickCount() == 2) && (OCRInterface.currOppmode == 17)) {
      saveCurrentState();
      unlockSoftLock();
      toBeOrdered = null;
      clearActiveZones();
    }
  }
  

  public final void mouseEntered(MouseEvent e) {}
  

  public final void mouseExited(MouseEvent e)
  {
    disableHotKeys();
  }
  
  private void disableHotKeys() {
    ocrIF.hotkeyManager.getInputMap().remove(KeyStroke.getKeyStroke("shift typed +"));
    ocrIF.hotkeyManager.getInputMap().remove(KeyStroke.getKeyStroke("shift typed _"));
  }
  
  private void enableHotKeys() {
    ocrIF.hotkeyManager.getInputMap().put(
      KeyStroke.getKeyStroke("shift typed +"), "ZOOM_IN");
    ocrIF.hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke("shift typed _"), 
      "ZOOM_OUT");
    ocrIF.hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(61, 2), "INCREASE_TEXT_SIZE");
    ocrIF.hotkeyManager.getActionMap().put("INCREASE_TEXT_SIZE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.increaseElectronicTextSize();
        ImageReaderDrawer.picture.repaint();
      }
      
    });
    ocrIF.hotkeyManager.getInputMap().put(KeyStroke.getKeyStroke(45, 2), "DECREASE_TEXT_SIZE");
    ocrIF.hotkeyManager.getActionMap().put("DECREASE_TEXT_SIZE", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        currentHWObjcurr_canvas.decreaseElectronicTextSize();
        ImageReaderDrawer.picture.repaint();
      }
    });
  }
  





  public final void mouseReleased(MouseEvent e)
  {
    if (e.isPopupTrigger()) {
      showPopUpMenu(e);
    }
    DeveloperView.println("[ImageDisplay:mouseReleased]");
    ocrIF.windowFocus[0] = true;
    this_interfacezoneBeingResized = false;
    lastMouseEvent = null;
    enableHotKeys();
    
    if (isSpecialCase()) {
      if ((activeZones.elementAt(0).offsetsReady()) && 
        (lastZoneSelected == activeZones.elementAt(0)))
      {
        if ((isControlDown) && 
          (!(activeZones.elementAt(0) instanceof PolygonZone)) && 
          (!(activeZones.elementAt(0) instanceof OrientedBox))) {
          saveCurrentState();
          
          Point p = e.getPoint();
          
          if (activeZones.elementAt(0).getHoriz() != 4) {
            if (!e.isPopupTrigger())
              activeZones.elementAt(0).addOffset((int)(x / scale));
          } else {
            int maxOffsets = activeZones.elementAt(0).getContents().split("\n").length - 1;
            if (maxOffsets > 0) {
              activeZones.elementAt(0).createSplitLine(e.getX(), e.getY(), scale);
              splitZone(activeZones.elementAt(0));
            }
          }
        }
      }
      if (OCRInterface.currOppmode == 10) {
        ocrIF.setCurrentOppMode(0);
        zoneLineSelected = null;


      }
      


    }
    else if ((e.getModifiers() & 0x10) != 0)
    {
      if (OCRInterface.currOppmode == 20) {
        lastMouseEvent = e;
      }
      else if (OCRInterface.currOppmode == 0)
      {

        ZonesManager vecToSelectFrom = shapeVec;
        










        int size = vecToSelectFrom.size();
        for (int i = 0; i < size; i++) {
          if (!selectionZone.isIncomplete)
          {
            if (((Zone)vecToSelectFrom.elementAt(i)).intersects(selectionZone))
            {
              if (((Zone)vecToSelectFrom.elementAt(i)).isVisible())
              {
                addToSelected((Zone)vecToSelectFrom.elementAt(i));
              }
            }
          }
        }
        if (activeZones.size() == 1)
        {

          if ((activeZones.elementAt(0).offsetsReady()) && 
            (lastZoneSelected == activeZones.elementAt(0)))
          {
            if ((isControlDown) && 
              (!(activeZones.elementAt(0) instanceof PolygonZone)) && 
              (!(activeZones.elementAt(0) instanceof OrientedBox))) {
              saveCurrentState();
              
              Point p = e.getPoint();
              if (activeZones.elementAt(0).getHoriz() != 4) {
                if (!e.isPopupTrigger())
                  activeZones.elementAt(0).addOffset((int)(x / scale));
              } else {
                int maxOffsets = activeZones.elementAt(0).getContents().split("\n").length - 1;
                if (maxOffsets > 0) {
                  activeZones.elementAt(0).createSplitLine(e.getX(), e.getY(), scale);
                  splitZone(activeZones.elementAt(0));
                }
                
              }
              
            }
          }
        }
        else {
          ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
        }
        

        paintCanvas();
        selectionZone = null;








      }
      else if (OCRInterface.currOppmode == 1) {
        tSelectFirst = (this.tSelectLast = null);
      } else if (OCRInterface.currOppmode == 5)
      {
        if ((activeZones.size() > 0) && 
          (activeZones.elementAt(0).doesContain(e
          .getX(), e.getY(), scale))) {
          deleteZone(activeZones.elementAt(0));
        }
      }
      else if (OCRInterface.currOppmode == 7) {
        if ((activeZones.size() > 0) && 
          (activeZones.elementAt(0).doesContain(e
          .getX(), e.getY(), scale))) {
          splitZone(activeZones.elementAt(0));
        }
        


        clearActiveZones();
        if (!ocrIF.toolbar.getStickyMode())
          ocrIF.setOppmode(0);
      } else {
        boolean rle;
        if ((OCRInterface.currOppmode == 2) && 
          (activeZones.size() > 0))
        {


          Zone zone = activeZones.elementAt(0);
          
          if ((zone.dlGetZoneHeight() != 0) && (zone.dlGetZoneWidth() != 0)) {
            insertZone(zone, e);
            isIncomplete = false;
            
            String selectedText = ocrIF.getETextWindowSelectedText();
            if ((selectedText != null) && (!selectedText.equals(""))) {
              zone.setContents(selectedText);
            }
            
            ocrIF.clearETextSel();
            if ((zone.hasContents()) && (ocrIF.getShowContentWindowOnDlTextLineCreate())) {
              actionRightClickDLTextLine(zone, e);
            }
            

            if (OCRInterface.this_interface.getApplyExpandShrink()) {
              currentHWObjcurr_canvas.setIdenticalZoneWasCreated(false);
              saveCurrentState();
              
              Zone zone_temp = zone.clone();
              
              boolean zoneShrunk = true;
              zone.adjust();
              
              if (zone_temp.get_Bounds().equals(zone.get_Bounds())) {
                zoneShrunk = false;
              }
              
              if ((zoneShrunk) && (isDuplicatedZone(zone))) {
                deleteZone(zone);
                setAllZones();
              }
              
              currentHWObjcurr_canvas.showWarning_IdenticalZones();
            }
          }
          else {
            nulldoc.remove(zone);
            clearActiveZones();
            OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId(zoneID);
          }
          
          if (!ocrIF.toolbar.getStickyMode()) {
            ocrIF.setOppmode(0);
            ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
          }
          else {
            clearActiveZones();
          }
          


          rle = ocrIF.tbdPane.data_panel.t_window.isRLESelected();
          boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
          createRLE_CC(zone, rle, cc, true);
          ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);


        }
        else if ((OCRInterface.currOppmode == 9) && 
          (activeZones.size() > 0))
        {



          if (!ocrIF.toolbar.getStickyMode()) {
            ocrIF.setOppmode(0);
          }
          else {
            clearActiveZones();
          }
        }
        else if (OCRInterface.currOppmode == 4) {
          if (OCRInterface.this_interface.getRecomputeRLEonEdit()) {
            for (Zone z : activeZones) {
              String rleImage = (String)z.getZoneTags().get("RLEIMAGE");
              if ((rleImage != null) && (!rleImage.isEmpty())) {
                createRLE_CC(z, true, false, true);
              }
            }
          }
          
          ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
          
          ocrIF.setCurrentOppMode(0);
          
          toBeEdited = null;


        }
        else if (OCRInterface.currOppmode == 3) {
          if (toBeEdited == null) {
            toBeEdited = activeZones.elementAt(0);
          } else {
            if ((toBeEdited instanceof OrientedBox))
            {
              String orientationStr = OrientedBox.getDegrees(((OrientedBox)toBeEdited).getOrientation());
              toBeEdited.setAttributeValue("orientationD", orientationStr);
              OrientedBox.rotationFlag = false;
            }
            else if ((toBeEdited instanceof PolygonZone))
            {
              ((PolygonZone)toBeEdited).resetSelectedSide();
            }
            

            toBeEdited.setSelectedCorner(-1);
            
            String rleImage = (String)toBeEdited.getZoneTags().get("RLEIMAGE");
            
            if ((OCRInterface.this_interface.getRecomputeRLEonEdit()) && 
              (rleImage != null) && (!rleImage.isEmpty())) {
              createRLE_CC(toBeEdited, true, false, true);
            }
            

            ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
            
            if (toBeEdited.isGroup()) {
              ((Group)toBeEdited).resizeGroupBox();
            }
            toBeEdited = null;
          }
          
          ocrIF.setCurrentOppMode(0);

        }
        else if (OCRInterface.currOppmode == 12)
        {
          if (toBeDrawn == null) {
            toBeDrawn = activeZones.elementAt(0);


          }
          else
          {

            if ((activeZones.size() > 0) && (toBeDrawn != activeZones.elementAt(0)) && 
              (activeZones.elementAt(0).doesContain(e
              .getX(), e.getY(), scale)))
            {
              makePCLine(toBeDrawn, activeZones.elementAt(0));
            }
            




            clearActiveZones();
            lastMouseMovedEvent = null;
            

            if (!ocrIF.toolbar.getStickyMode()) {
              ocrIF.setOppmode(0);
            }
            
          }
          
        }
        else if (OCRInterface.currOppmode == 14)
        {
          if (toBeErased == null) {
            toBeErased = activeZones.elementAt(0);


          }
          else
          {

            if ((toBeErased != activeZones.elementAt(0)) && 
              (activeZones.elementAt(0).doesContain(e
              .getX(), e.getY(), scale)))
            {
              erasePCLine(toBeErased, activeZones.elementAt(0));
            }
            




            clearActiveZones();
            lastMouseMovedEvent = null;
            

            if (!ocrIF.toolbar.getStickyMode()) {
              ocrIF.setOppmode(0);
            }
            
          }
          

        }
        else if (OCRInterface.currOppmode == 10)
        {










          ocrIF.setCurrentOppMode(0);
          zoneLineSelected = null;


        }
        else if (OCRInterface.currOppmode == 15) {
          OrientedBox cur_zone = (OrientedBox)activeZones.elementAt(0);
          





          if ((cur_zone != null) && (cur_zone.dlGetZoneHeight() == 1) && 
            (cur_zone.dlGetZoneWidth() == 1) && 
            (cur_zone.getOrientation() == 0.0D)) {
            clearActiveZones();
            deleteZone(cur_zone);
            OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId(zoneID);
            paintCanvas();


          }
          else if ((cur_zone != null) && (!isIncomplete)) {
            insertZone(cur_zone, e);
            
            String selectedText = ocrIF.getETextWindowSelectedText();
            
            if ((selectedText != null) && (!selectedText.equals(""))) {
              cur_zone.setContents(selectedText);
            }
            ocrIF.updateETextWindow();
            if ((cur_zone.hasContents()) && (ocrIF.getShowContentWindowOnDlTextLineCreate())) {
              actionRightClickDLTextLine(cur_zone, e);
            }
            
            String orientationStr = OrientedBox.getDegrees(cur_zone.getOrientation());
            cur_zone.setAttributeValue("orientationD", orientationStr);
            if (!ocrIF.toolbar.getStickyMode()) {
              addToSelected(cur_zone);
              ocrIF.setOppmode(0);
              ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
            } else {
              clearActiveZones();
              ocrIF.setOppmode(15);
            }
            

            boolean rle = ocrIF.tbdPane.data_panel.t_window.isRLESelected();
            boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
            createRLE_CC(cur_zone, rle, cc, true);

          }
          


        }
        else if (OCRInterface.currOppmode == 17) {
          saveCurrentState();
          unlockSoftLock();
          if (toBeOrdered == null)
          {

            if (!activeZones.isEmpty()) {
              toBeOrdered = activeZones.elementAt(0);
              if (toBeOrdered.nextZone != null) {
                toBeOrdered.nextZone.previousZone = null;
              }
              toBeOrdered.nextZone = null;
            }
            
          }
          else
          {
            Zone nextToBeOrdered = activeZones.elementAt(0);
            
            if ((nextToBeOrdered != null) && 
              (toBeOrdered.zoneID != zoneID)) {
              if (previousZone != null) {
                previousZone.nextZone = null;
                previousZone = null;
              }
              

              if ((toBeOrdered.nextZone != null) && 
                (toBeOrdered.nextZone.zoneID != zoneID)) {
                toBeOrdered.nextZone.previousZone = null;
                toBeOrdered.nextZone = null;
              }
              
              toBeOrdered.nextZone = nextToBeOrdered;
              previousZone = toBeOrdered;
              


              if (readingOrderHandler.isCyclicReadingOrder(toBeOrdered)) {
                nextZone.previousZone = null;
                nextZone = null;
              }
              
              toBeOrdered = nextToBeOrdered;
            }
            
          }
          
        }
        else if (OCRInterface.currOppmode == 18) {
          saveCurrentState();
          if (selectionZone == null) return;
          int blackPixelValue = getBlackPixelValue();
          System.out.println("\nblackPixelValue: " + blackPixelValue);
          
          long start = System.currentTimeMillis();
          
          Raster image = imageRaster;
          




          int bands = image.getNumBands();
          if (bands > 3) {
            bands = 3;
          }
          boolean rle = ocrIF.tbdPane.data_panel.t_window.isRLESelected();
          boolean cc = ocrIF.tbdPane.data_panel.t_window.isCCSelected();
          


          if ((selectionZone.get_Bounds().width == 0) && (selectionZone.get_Bounds().height == 0)) {
            int x = (int)(e.getX() / scale);
            int y = (int)(e.getY() / scale);
            
            int pixelValue = 0;
            
            for (int i = 0; i < bands; i++) {
              System.out.println("pixel clicked. band # " + i + ": " + image.getSample(x, y, i));
              pixelValue += image.getSample(x, y, i);
            }
            

            if (pixelValue == blackPixelValue) {
              new RLE_CC_Handler(blackPixelValue, e.getX(), e.getY(), rle, cc, scale);
            }
          } else {
            new RLE_CC_Handler(blackPixelValue, selectionZone, rle, cc, scale);
          }
          long time = System.currentTimeMillis() - start;
          System.out.println("CC. Time spent: " + time);
          if (!ocrIF.toolbar.getStickyMode()) {
            ocrIF.setOppmode(0);
            ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
          } else {
            updateState();
          }
          selectionZone = null;

        }
        else if ((OCRInterface.currOppmode == 19) && 
          (activeZones.size() > 0))
        {
          Group zone = (Group)activeZones.elementAt(0);
          
          if ((zone.dlGetZoneHeight() != 0) && (zone.dlGetZoneWidth() != 0) && 
            (zone.getZonesOfGroup() != null) && 
            (!zone.getZonesOfGroup().isEmpty())) {
            zone.resizeGroupBox();
            insertZone(zone, e);
            isIncomplete = false;
            
            String selectedText = ocrIF.getETextWindowSelectedText();
            if ((selectedText != null) && (!selectedText.equals(""))) {
              zone.setContents(selectedText);
            }
            ocrIF.clearETextSel();
            if ((zone.hasContents()) && (ocrIF.getShowContentWindowOnDlTextLineCreate())) {
              actionRightClickDLTextLine(zone, e);
            }
            
            if (isDuplicatedZone(zone)) {
              deleteZone(zone);
              setAllZones();
            }
            currentHWObjcurr_canvas.showWarning_IdenticalZones();
            
            if (!ocrIF.toolbar.getStickyMode()) {
              ocrIF.setOppmode(0);
              ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
            }
            else {
              clearActiveZones();
            }
          } else {
            if ((OCRInterface.currDoc == null) && (nulldoc != null))
              nulldoc.remove(zone);
            clearActiveZones();
            OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId(zoneID);
          }
          

          ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
        }
      }
    }
    































    if (activeZones.size() == 1) {
      changeMouseIcon(activeZones.firstElement(), e);
    } else {
      changeMouseIcon(null, e);
    }
    paintCanvas();
  }
  


  private void actionRightClickDLTextLine(final Zone z, MouseEvent e)
  {
    int yZone;
    
    int xZone;
    
    int yZone;
    
    if ((z instanceof OrientedBox)) {
      int xZone = (int)(dlGetZoneOriginx * scale);
      yZone = (int)(dlGetZoneOriginy * scale);
    }
    else
    {
      xZone = (int)(z.get_lt_x() * scale);
      yZone = (int)(z.get_lt_y() * scale);
    }
    

    int xOnScreenZone = e.getXOnScreen() - (e.getX() - xZone);
    int yOnScreenZone = e.getYOnScreen() - (e.getY() - yZone);
    

    final JDialog dlg = new JDialog(ocrIF, true);
    dlg.setName("Contents");
    dlg.setTitle("Contents");
    
    final JTextArea ta = new JTextArea();
    ta.setLineWrap(true);
    ta.setRows(10);
    OCRInterface.dialog_open = true;
    GlobalHotkeyManager.getInstance().setEnabled(false);
    
    int heightOfPanel = 225;
    
    dlg.setSize(300, heightOfPanel);
    
    int dialogX = Math.min(xOnScreenZone, ocrIF.getWidth() + ocrIF.getX() - 300);
    int dialogY = Math.max(yOnScreenZone - heightOfPanel - 20, ocrIF.getY());
    
    dlg.setLocation(dialogX, dialogY);
    dlg.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e) {
        ImageDisplay.this.updateOnContentWindowClosing(z, dlg, ta);

      }
      

    });
    ta.setText(z.getContents());
    
    JButton ok_btn = new JButton("OK") {
      public void fireActionPerformed(ActionEvent ae) {
        ImageDisplay.this.updateOnContentWindowClosing(z, dlg, ta);
      }
      
    };
    JButton cancel_btn = new JButton("Cancel") {
      public void fireActionPerformed(ActionEvent ae) {
        dlg.dispose();
        OCRInterface.dialog_open = false;
        GlobalHotkeyManager.getInstance().setEnabled(true);
      }
      
    };
    dlg.getContentPane().add(ta, "North");
    dlg.getContentPane().add(ok_btn, "Center");
    dlg.getContentPane().add(cancel_btn, "East");
    
    dlg.setVisible(true);
  }
  

  public void deleteCurrentSelectedZone()
  {
    if (activeZones.size() > 0) {
      saveCurrentState();
      for (Iterator<Zone> i = activeZones.iterator(); i.hasNext();)
        deleteZone((Zone)i.next());
      clearActiveZones();
      
      paintCanvas();
      OCRInterface.this_interface.updateETextWindow();
      OCRInterface.this_interface.getUniqueZoneIdObj().updateMaxZoneId();
    }
  }
  
  public void clearSpiltSelection() {
    clearActiveZones();
  }
  













  public Zone mergeZones(Zone zone1, Zone zone2)
  {
    Zone merged = null;
    try {
      Group group = null;
      Vector<Group> parentGroups = getGroupsToWhichZoneBelongs(zone1);
      
      if ((parentGroups != null) && (parentGroups.size() == 1) && 
        (((Group)parentGroups.get(0)).containsZone(zone2))) {
        group = (Group)parentGroups.get(0);
      }
      merged = shapeVec.mergeZones(zone1, zone2, group);
      
      readingOrderHandler.removeFromReadingOrder(zone1);
      readingOrderHandler.removeFromReadingOrder(zone2);
      return merged;
    }
    catch (DLException e)
    {
      JOptionPane.showMessageDialog(ocrIF, e.getMessage(), 
        "Merge Error: ", 0);
    }
    
    return null;
  }
  
  public void makePCLine(Zone parzone, Zone chzone)
  {
    saveCurrentState();
    try
    {
      shapeVec.makePCLine(parzone, chzone);
    }
    catch (DLException e)
    {
      JOptionPane.showMessageDialog(ocrIF, e.getMessage(), 
        "Create Parent/Child Line Error: ", 0);
    }
  }
  
  public void erasePCLine(Zone parzone, Zone chzone) {
    saveCurrentState();
    try
    {
      shapeVec.erasePCLine(parzone, chzone);
    }
    catch (DLException e)
    {
      JOptionPane.showMessageDialog(ocrIF, e.getMessage(), 
        "Erase Parent/Child Line Error: ", 0);
    }
  }
  








  public void splitZone(Zone toSplit)
  {
    saveCurrentState();
    try {
      Group group = null;
      Vector<Group> parentGroups = getGroupsToWhichZoneBelongs(toSplit);
      
      if ((parentGroups != null) && (parentGroups.size() == 1)) {
        group = (Group)parentGroups.get(0);
      }
      shapeVec.splitOcrZone(toSplit, group);
      

      readingOrderHandler.removeFromReadingOrder(toSplit);

    }
    catch (DLException e)
    {
      JOptionPane.showMessageDialog(ocrIF, e.getMessage(), 
        "Split Error: ", 0);
    }
  }
  











  public void deleteZone(Zone toDelete)
  {
    if ((toDelete.isGroup()) && (!((Group)toDelete).getZonesOfGroup().isEmpty())) {
      return;
    }
    deleteZoneFromGroup(toDelete);
    if (OCRInterface.currDoc == null) {
      nulldoc.remove(toDelete);
    }
    shapeVec.deleteZone(toDelete, ocrIF.isOCRMode());
    

    allZones.remove(new AllZones(
      zoneID, 
      dlGetZoneOriginx, 
      dlGetZoneOriginy, 
      toDelete.dlGetZoneWidth(), 
      toDelete.dlGetZoneHeight()));
    
    unlockSoftLock();
    clearRLEMap();
  }
  
  private void deleteZoneFromGroup(Zone toDelete) {
    Vector<Group> parentGroups = getGroupsToWhichZoneBelongs(toDelete);
    if (parentGroups == null) {
      return;
    }
    for (Group g : parentGroups) {
      g.removeFromGroup(toDelete);
      
      if (g.getZonesOfGroup().isEmpty()) {
        g.deleteGroup();
      }
    }
  }
  


  public void insertZone(Zone toInsert, MouseEvent e)
  {
    DeveloperView.print("[ImageDisplay:insertZone]");
    int index = 0;
    insertZone(index, toInsert);
  }
  









  public void insertZone(int position, Zone toInsert)
  {
    if (allowEdit())
    {

      if (OCRInterface.currOppmode != 18) {
        saveCurrentState();
      }
      if (!isDuplicatedZone(toInsert)) {
        shapeVec.add(position, toInsert);
      }
    }
  }
  










  public void setTypeToZones(String newZonetypeId, Vector<Zone> zones)
  {
    if (zones.size() > 0) {
      saveCurrentState();
      for (Iterator<Zone> i = zones.iterator(); i.hasNext();) {
        Zone curZone = (Zone)i.next();
        Zone clonedZone = curZone.clone_zone();
        Map<String, String> zoneTags = clonedZone.getZoneTags();
        curZone.getZoneTags().clear();
        String orientationD = null;
        String polygon = null;
        String zoneId = null;
        String groupElements = null;
        


        if (zoneTags.containsKey("id")) {
          zoneId = (String)zoneTags.get("id");
          curZone.setAttributeValue("id", zoneId);
        }
        if (zoneTags.containsKey("orientationD")) {
          orientationD = (String)zoneTags.get("orientationD");
          curZone.setAttributeValue("orientationD", orientationD);
        }
        if (zoneTags.containsKey("polygon")) {
          polygon = (String)zoneTags.get("polygon");
          curZone.setAttributeValue("polygon", polygon);
        }
        if (zoneTags.containsKey("elements")) {
          groupElements = (String)zoneTags.get("elements");
          curZone.setAttributeValue("elements", groupElements);
        }
        

        curZone.setZoneType(newZonetypeId);
      }
    }
    
    shapeVec.refreshCount();
    
    ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
    DeveloperView.println("activeZones: " + activeZones.size() + " " + 
      newZonetypeId);
    paintCanvas();
  }
  
















































  public void addToSelected(Zone toSelect)
  {
    if (toSelect.isVisible())
    {
      activeZones.add(toSelect);
      
      if (activeZones.size() == 1) {
        ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
      } else {
        ocrIF.tbdPane.data_panel.a_window.updateMultipleSelectedZonesCount(activeZones);
      }
      selPt = null;
      

      if (toSelect.isGroup()) {
        addToGroupList((Group)toSelect);
        ArrayList<Zone> zonesGroup = ((Group)toSelect).getZonesOfGroup();
        if (zonesGroup != null) {
          for (Zone z : zonesGroup)
            addToSelected(z);
        }
      }
      addAdditionalZones(toSelect);
      



      this_interfacetbdPane.data_panel.a_window.eTextWindow.addZoneToSelection(toSelect);
      
      OCRInterface.this_interface.updateCommentsWindow(false);
    }
    
    OCRInterface.disableManager.zonesSelected(activeZones.size());
  }
  















































  private void addAdditionalZones(Zone originalZone)
  {
    if ((OCRInterface.this_interface.getEnablePolygonTranscription()) && 
      (this_interfacecurrState == 2)) {
      this_interfacePolyTranscribePanel.addToReviewedZones(
        originalZone.getAttributeValue("id"));
    }
  }
  

  public void clearSelection()
  {
    clearActiveZones();
    OCRInterface.disableManager.zonesSelected(activeZones.size());
    paintCanvas();
  }
  
  public boolean zoneTypeSelected() {
    String s = ocrIF.tbdPane.getSelectedAssignID();
    return !s.equals("");
  }
  



  private void setRenderedImage(RenderedImage im)
  {
    this.im = im;
  }
  



  public RenderedImage getRenderedImage()
  {
    return im;
  }
  












  public void clearActiveZones()
  {
    this_interfacetbdPane.data_panel.a_window.eTextWindow.removeActiveHighlights();
    
    activeZones.removeAllElements();
    ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
    if (this_interfacebottomPanel != null) {
      this_interfacebottomPanel.resetZoneSearchField();
    }
  }
  






  public Zone getActiveZone()
  {
    return activeZones.elementAt(0);
  }
  







  public static ZoneVector getActiveZones()
  {
    return activeZones;
  }
  
  public static ZoneVector getActZones()
  {
    return actZones;
  }
  
  public boolean isShowData() { return showData; }
  
  public void setShowData(boolean showData)
  {
    this.showData = showData;
  }
  
  public boolean isShowImage() {
    return showImage;
  }
  
  public void setShowImage(boolean showImage) {
    this.showImage = showImage;
  }
  


  public void rotateImage(int newRotateDegrees)
  {
    System.out.println("ATTN rotateImage(" + newRotateDegrees + ") currentRotateDegrees: " + OCRInterface.this_interface.getCurrentRotateDegrees());
    try {
      OCRInterface.currentHWObj.rotateOriginalImage((newRotateDegrees + 360 - OCRInterface.this_interface.getCurrentRotateDegrees()) % 360);
      
      initializeImage(getscaledImage());
      
      width = getRenderedImage().getWidth();
      height = getRenderedImage().getHeight();
      
      int w = getIconWidth();
      int h = getIconHeight();
      
      Dimension new_dim = new Dimension(w, h);
      ImageReaderDrawer.picture.setPreferredSize(new_dim);
      hw.setPreferredSize(new Dimension(w + 3, h + 3));
      ImageReaderDrawer.picture.revalidate();
      
      paintCanvas();
      
      OCRInterface.this_interface.setCurrentRotateDegrees(newRotateDegrees);
    }
    catch (Exception e) {
      System.out.println("Unexpected exception " + e);
      e.printStackTrace();
    }
  }
  





























































  public void rotateImage1()
  {
    rotated = true;
    rotating = true;
    









    try
    {
      int rotateby = Math.abs(rotate % 4);
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(getscaledImage());
      

      pb.add(0.0F);
      pb.add(0.0F);
      float angle = (float)(rotateby * Math.toRadians(90.0D));
      
      pb.add(angle);
      
      PlanarImage temp = JAI.create("rotate", pb);
      


      pb = new ParameterBlock();
      pb.addSource(temp);
      if (rotateby == 1) {
        pb.add(temp.getWidth());
        pb.add(0.0F);
      } else if (rotateby == 2) {
        pb.add(temp.getWidth());
        pb.add(temp.getHeight());
      } else if (rotateby == 3) {
        pb.add(0.0F);
        pb.add(temp.getHeight());
      }
      
      temp = JAI.create("translate", pb);
      







      initializeImage(temp);
      


      width = getRenderedImage().getWidth();
      height = getRenderedImage().getHeight();
      



      int w = getIconWidth();
      int h = getIconHeight();
      




      Dimension new_dim = new Dimension(w, h);
      ImageReaderDrawer.picture.setPreferredSize(new_dim);
      hw.setPreferredSize(new Dimension(w + 3, h + 3));
      ImageReaderDrawer.picture.revalidate();
      
      ocrIF.enableImageSave(true);
      rotating = false;
    }
    catch (Exception x) {
      System.out.println("Unexpected exception " + x);
      x.printStackTrace();
    }
  }
  







  private RenderedImage rotateImage(RenderedImage im)
  {
    int rotateby = Math.abs(rotate % 4);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(im);
    

    pb.add(0.0F);
    pb.add(0.0F);
    float angle = (float)(rotateby * Math.toRadians(90.0D));
    pb.add(angle);
    
    RenderedImage temp = JAI.create("rotate", pb);
    
    pb = new ParameterBlock();
    pb.addSource(temp);
    if (rotateby == 1) {
      pb.add(temp.getWidth());
      pb.add(0.0F);
    } else if (rotateby == 2) {
      pb.add(temp.getWidth());
      pb.add(temp.getHeight());
    } else if (rotateby == 3) {
      pb.add(0.0F);
      pb.add(temp.getHeight());
    }
    
    temp = JAI.create("translate", pb);
    



    return temp;
  }
  




  public void saveImage()
  {
    try
    {
      PlanarImage temp = JAI.create("fileload", ImageReaderDrawer.file_path);
      



      int rotateby = Math.abs(rotate % 4);
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(temp);
      

      pb.add(0.0F);
      pb.add(0.0F);
      float angle = (float)(rotateby * Math.toRadians(90.0D));
      pb.add(angle);
      
      temp = JAI.create("rotate", pb);
      


      pb = new ParameterBlock();
      pb.addSource(temp);
      if (rotateby == 1) {
        pb.add(temp.getWidth());
        pb.add(0.0F);
      } else if (rotateby == 2) {
        pb.add(temp.getWidth());
        pb.add(temp.getHeight());
      } else if (rotateby == 3) {
        pb.add(0.0F);
        pb.add(temp.getHeight());
      }
      
      temp = JAI.create("translate", pb);
      


      String filetype = ImageReaderDrawer.file_path.substring(
        ImageReaderDrawer.file_path.lastIndexOf(".") + 1).toUpperCase();
      

      if (filetype.equals("TIF")) {
        filetype = "TIFF";
        TIFFEncodeParam encodeParam = new TIFFEncodeParam();
        encodeParam.setCompression(4);
        


        JAI.create("filestore", temp, ImageReaderDrawer.file_path, filetype, 
          encodeParam);
      }
      else if (filetype.equals("JPG")) {
        filetype = "JPEG";
      }
      
      rotate = 0;
      ocrIF.enableImageSave(false);
      rotated = false;
      









      temp = (PlanarImage)getscaledImage();
      initializeImage(temp);
      


      width = getRenderedImage().getWidth();
      height = getRenderedImage().getHeight();
      



      int w = getIconWidth();
      int h = getIconHeight();
      




      Dimension new_dim = new Dimension(w, h);
      ImageReaderDrawer.picture.setPreferredSize(new_dim);
      hw.setPreferredSize(new Dimension(w + 3, h + 3));
      ImageReaderDrawer.picture.revalidate();
    } catch (Exception x) {
      System.out.println("Unexpected exception " + x);
      x.printStackTrace();
    }
  }
  
  public static void copyFile(File source, File dest) throws IOException
  {
    FileChannel in = null;FileChannel out = null;
    try {
      in = new FileInputStream(source).getChannel();
      out = new FileOutputStream(dest).getChannel();
      
      long size = in.size();
      MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0L, 
        size);
      
      out.write(buf);
    }
    finally {
      if (in != null)
        in.close();
      if (out != null)
        out.close();
    }
  }
  
  public void showDestroyDialog() {
    if (shapeVec.hasHiearchy()) {
      if (((!neverDestroyH) && (!alwaysDestroyH)) || (
        (neverDestroyH) && (alwaysDestroyH))) {
        final JOptionPane optionPane = new JOptionPane(
          "Current document contains a hierarchical zone structure.\n Would you like to destroy this structure?\n", 
          
          3, 0);
        
        final JDialog dialog = new JDialog(OCRInterface.this_interface, 
          "Click a button", true);
        dialog.setContentPane(optionPane);
        
        dialog.setDefaultCloseOperation(0);
        
        optionPane
          .addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
              String prop = e.getPropertyName();
              
              if ((dialog.isVisible()) && 
                (e.getSource() == optionPane))
              {
                if (prop.equals("value"))
                {


                  dialog.setVisible(false);
                }
              }
            }
          });
        dialog.setLocation(new Point(300, 300));
        dialog.pack();
        dialog.setVisible(true);
        int value = ((Integer)optionPane.getValue()).intValue();
        if (value == 0) {
          saveCurrentState();
          shapeVec.destroyHierarchy();
          ld.setModified(true);
          if (OCRInterface.currDoc != null)
            OCRInterface.currDoc.setModified(true);
          OCRInterface.this_interface.updateCurrFilename();

        }
        


      }
      else if (alwaysDestroyH) {
        saveCurrentState();
        shapeVec.destroyHierarchy();
        ld.setModified(true);
        if (OCRInterface.currDoc != null)
          OCRInterface.currDoc.setModified(true);
        OCRInterface.this_interface.updateCurrFilename();
        return;
      }
    }
  }
  
  public void setAlwaysDestroyH(boolean autoDestroyH) {
    alwaysDestroyH = autoDestroyH;
  }
  
  public void setNeverDestroyH(boolean neverDestroyH) {
    neverDestroyH = neverDestroyH;
  }
  
  public void copyZones() {
    OCRInterface.this_interface.setOppmode(0);
    zvec.clear();
    for (int i = 0; i < getActiveZones().size(); i++) {
      zvec.add(getActiveZones().elementAt(i));
    }
  }
  
  public void pasteZones() throws DLException {
    LinkedList<DLZone> llzone = new LinkedList();
    HashMap<DLZone, Zone> hm = new HashMap();
    HashMap<Zone, DLZone> hm2 = new HashMap();
    DLZone z = null;
    hm.clear();
    hm2.clear();
    for (int i = 0; i < zvec.size(); i++) {
      if ((zvec.elementAt(i) instanceof OrientedBox)) {
        Point origin = (Point)((OrientedBox)zvec.elementAt(i)).dlGetZoneOrigin().clone();
        
        int w = ((OrientedBox)zvec.elementAt(i)).dlGetZoneWidth();
        int h = ((OrientedBox)zvec.elementAt(i)).dlGetZoneHeight();
        double o = ((OrientedBox)zvec.elementAt(i)).getOrientation();
        z = new OrientedBox(origin, w, h, o);
        z.dlSetZoneOrigin(x + 20, y + 20);
      }
      else if ((zvec.elementAt(i) instanceof PolygonZone)) {
        z = new PolygonZone(((PolygonZone)zvec.elementAt(i)).getStartPt(), 
          ((PolygonZone)zvec.elementAt(i)).getPointsVec(), 20, 20);
      }
      else
      {
        z = new Zone(zvec.elementAt(i).get_lt_x() + 50, zvec.elementAt(i).get_lt_y() + 50, zvec.elementAt(i).dlGetZoneWidth(), zvec.elementAt(i).dlGetZoneHeight());
      }
      ((Zone)z).setZoneType(zvec.elementAt(i).getZoneType());
      


      String zoneId = z.getAttributeValue("id");
      z.getZoneTags().putAll(zvec.elementAt(i).getZoneTags());
      z.setAttributeValue("id", zoneId);
      
      llzone.add(z);
      hm.put(z, zvec.elementAt(i));
      hm2.put(zvec.elementAt(i), z);
      
      addToSelected((Zone)z);
      
      if (alwaysDestroyH) {
        if (OCRInterface.currDoc != null) {
          ZonesManager zm = OCRInterface.currDoc.get_zones_vec();
          DLPage pg = zm.getPage();
          pg.dlAppendZoneWithoutCheck(z);
        } else {
          nulldoc.add(z);
          shapeVec.add(z);
        }
      }
    }
    if (!alwaysDestroyH) {
      for (int x = 0; x < zvec.size(); x++)
      {
        if ((zvec.elementAt(x).dlGetParentZone() != null) && (hm2.containsKey(zvec.elementAt(x).dlGetParentZone()))) {
          ((DLZone)llzone.get(x)).dlSetParentZone((DLZone)hm2.get(zvec.elementAt(x).dlGetParentZone()));
        }
        if (zvec.elementAt(x).dlHasChildZones()) {
          for (int j = 0; j < zvec.elementAt(x).childZones.size(); j++) {
            if (zvec.contains(zvec.elementAt(x).childZones.get(j))) {
              ((DLZone)llzone.get(x)).dlAppendChildZone((DLZone)hm2.get(zvec.elementAt(x).childZones.get(j)));
            }
          }
        }
      }
      for (int x = 0; x < llzone.size(); x++)
      {
        if (((DLZone)llzone.get(x)).dlGetParentZone() == null) {
          OCRInterface.currDoc.get_zones_vec().getPage().dlAppendZone((DLZone)llzone.get(x));
        }
      }
    }
    
    paintCanvas();
    zvec.clear();
    llzone.clear();
    hm.clear();
    hm2.clear();
  }
  
  public Zone minULCorner()
  {
    Zone z = activeZones.elementAt(0);
    int xmin = z.get_lt_x();int ymin = z.get_lt_y();
    
    for (int i = 1; i < activeZones.size(); i++)
    {
      z = activeZones.elementAt(i);
      if ((z.get_lt_x() < xmin) && (z.get_lt_y() < ymin)) {
        Zone temp = activeZones.elementAt(0);
        activeZones.setElementAt(z, 0);
        activeZones.setElementAt(temp, i);
      }
    }
    
    return z;
  }
  
  public ZonesManager getShapeVec() {
    return shapeVec;
  }
  




  public void moveZones(MouseEvent e)
  {
    int moveX = e.getX();
    int moveY = e.getY();
    int newX = (int)(moveX / scale);
    int newY = (int)(moveY / scale);
    
    for (Zone zone : activeZones) {
      Point temp = zone.checkMoveLimit(newX, newY);
      newX = x;
      newY = y;
    }
    
    for (Zone zone : activeZones)
      zone.moveTo(newX, newY, true);
  }
  
  public void moveZones(int direction) {
    int newX = 0;
    int newY = 0;
    for (Zone zone : activeZones) {
      switch (direction) {
      case 1: 
        newX = zone.get_lt_x() - 1;
        newY = zone.get_lt_y();
        break;
      case 2: 
        newX = zone.get_lt_x() + 1;
        newY = zone.get_lt_y();
        break;
      case 3: 
        newX = zone.get_lt_x();
        newY = zone.get_lt_y() - 1;
        break;
      case 4: 
        newX = zone.get_lt_x();
        newY = zone.get_lt_y() + 1;
      }
      
      
      Point temp = zone.checkMoveLimit(newX, newY);
      newX = x;
      newY = y;
      
      zone.moveTo(newX, newY, false);
      ocrIF.tbdPane.data_panel.a_window.showZoneInfo(activeZones);
      paintCanvas();
    }
  }
  


  public void increaseElectronicTextSize()
  {
    electronicTextSize += 1;
    fontMetrics = null;
  }
  
  public void decreaseElectronicTextSize() {
    electronicTextSize -= 1;
    fontMetrics = null;
  }
  
  public int getElectronicTextSize() {
    return electronicTextSize;
  }
  




  public void saveCurrentState()
  {
    if (!allowEdit()) {
      this_interfaceocrTable.processSelectionEvent(
        WorkmodeTable.selRow, 
        WorkmodeTable.selCol, true);
    }
    else
    {
      undoManager.saveState(shapeVec, activeZones);
    }
  }
  









  private void updateOnContentWindowClosing(Zone z, JDialog dlg, JTextArea ta)
  {
    str = ta.getText();
    dlg.dispose();
    OCRInterface.dialog_open = false;
    GlobalHotkeyManager.getInstance().setEnabled(true);
    
    z.setContents(str);
    OCRInterface.currentHWObj.repaint();
    this_interfacetbdPane.data_panel.a_window.showZoneInfo(activeZones);
    
    if (ld != null) {
      ld.dumpData();
    }
    ocrIF.updateETextWindow();
  }
  











  private void showPopUpMenu2(MouseEvent e)
  {
    JMenuItem item = null;
    


    if (activeZones.isEmpty()) {
      item = new JMenuItem("Show all Headers");
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          Vector<DLZone> allZones = shapeVec.getAsVector();
          for (DLZone zone : allZones) {
            if (readingOrderHandler.isHeader(zone)) {
              addToSelected((Zone)zone);
            }
          }
        }
      });
      JPopupMenu menu = new JPopupMenu();
      
      menu.add(item);
      menu.show(e.getComponent(), e.getX(), e.getY());


    }
    else
    {


      if (!readingOrderHandler.isHeader(activeZones.elementAt(0))) {
        return;
      }
      DLZone header = activeZones.elementAt(0);
      DLZone nextZone = nextZone;
      
      while (nextZone != null) {
        addToSelected((Zone)nextZone);
        nextZone = nextZone;
      }
      
      if (((Zone)header).hasContents()) {
        actionRightClickDLTextLine((Zone)header, e);
      }
    }
  }
  
  private void showPopUpMenu(final MouseEvent e)
  {
    System.out.println("showPopUpMenu");
    
    JPopupMenu menu = new JPopupMenu();
    
    JMenuItem item = null;
    
    item = new JMenuItem("Add Token");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Point p = e.getPoint();
        Zone selectedZone = ImageDisplay.activeZones.elementAt(0);
        
        BidiString bs = new BidiString(selectedZone.getContents(), 3);
        int direction = bs.getDirection();
        int maxOffsets = bs.size();
        String firstLine = null;
        
        if (direction == 0) {
          firstLine = "0";
        } else {
          firstLine = String.valueOf(selectedZone.get_rb_x() - selectedZone.get_lt_x());
        }
        ArrayList<String> offsets = selectedZone.getOffsetsArray(firstLine, direction, maxOffsets);
        int index = 0;
        
        int x_scaled = (int)(x / scale);
        
        for (String s : offsets)
        {
          if (direction == 0)
          {
            if (x_scaled - selectedZone.get_lt_x() > Integer.parseInt(s)) {
              index++;
            }
          }
          else if (x_scaled - selectedZone.get_lt_x() < Integer.parseInt(s)) {
            index++;
          }
        }
        
        System.out.println("index: " + index);
        String[] content = selectedZone.getContents().split("\\s+");
        String[] new_content = new String[content.length + 1];
        
        for (int i = 0; i < new_content.length; i++) {
          if (i == index) {
            new_content[i] = new String("∅");
          } else if (i < index) {
            new_content[i] = content[i];
          } else {
            new_content[i] = content[(i - 1)];
          }
        }
        String contentStr = "";
        
        for (String s : new_content) {
          contentStr = contentStr + s + " ";
        }
        selectedZone.setContents(contentStr);
        ImageDisplay.activeZones.elementAt(0).addOffset((int)(x / scale));
      }
      
    });
    menu.add(item);
    menu.addSeparator();
    
    item = new JMenuItem("Add/View Comments");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (CommentsParser.continueToParse()) {
          new CommentsParser();
        }
      }
    });
    menu.add(item);
    menu.addSeparator();
    
    item = new JMenuItem("Copy content");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if ((ImageDisplay.activeZones.size() == 1) && 
          (((Zone)ImageDisplay.activeZones.firstElement()).hasContents())) {
          ImageDisplay.this.setClipboard(((Zone)ImageDisplay.activeZones.firstElement()).getContents().trim());
        } else {
          ImageDisplay.this.setClipboard("");
        }
      }
    });
    menu.add(item);
    
    item = new JMenuItem("Paste content");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (ImageDisplay.activeZones.size() == 1) {
          Zone z = (Zone)ImageDisplay.activeZones.firstElement();
          
          if (!z.hasContents()) {
            z.setContents(getClipboard().trim());
          } else {
            String content = z.getContents();
            int index = content.length() - caret;
            content = content.substring(0, index) + getClipboard().trim() + 
              content.substring(index, content.length());
            z.setContents(content);
          }
          
        }
      }
    });
    menu.add(item);
    
    item = new JMenuItem("Add/Edit Content");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if ((ImageDisplay.activeZones.size() == 1) && 
          (((Zone)ImageDisplay.activeZones.firstElement()).hasContents())) {
          ImageDisplay.this.actionRightClickDLTextLine((Zone)ImageDisplay.activeZones.firstElement(), e);
        }
      }
    });
    if ((OCRInterface.this_interface.getLockContentEditing()) || 
      (activeZones.isEmpty()) || 
      (activeZones.size() > 1) || (
      (activeZones.size() == 1) && (!((Zone)activeZones.firstElement()).hasContents()))) {
      item.setEnabled(false);
    }
    menu.add(item);
    

    item = new JMenuItem("Delete");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        deleteCurrentSelectedZone();
      }
    });
    if (activeZones.isEmpty())
      item.setEnabled(false);
    menu.add(item);
    
    JMenu submenu = new JMenu("Reset Zone Type");
    if (activeZones.isEmpty()) {
      submenu.setEnabled(false);
    }
    Object[] types = 
      this_interfacetbdPane.data_panel.t_window.getTypeSettings()
      .keySet().toArray();
    
    item = null;
    
    for (final Object type : types)
    {
      if (!this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
        item = new JMenuItem(type.toString());
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            resetZoneType(type.toString());
          }
        });
      }
      if (item != null)
        submenu.add(item);
    }
    menu.add(submenu);
    
    submenu = new JMenu("Reset Group Type");
    
    if (getActiveGroups().isEmpty()) {
      submenu.setEnabled(false);
    }
    menu.add(submenu);
    
    for (final Object type : types)
    {
      if (this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
        item = new JMenuItem(type.toString());
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            resetGroupType(type.toString());
          }
        });
        if (item != null) {
          submenu.add(item);
        }
      }
    }
    
    menu.addSeparator();
    
    submenu = new JMenu("Group");
    if (activeZones.isEmpty()) {
      submenu.setEnabled(false);
    }
    menu.add(submenu);
    
    for (Object type : types)
    {
      if (this_interfacetbdPane.data_panel.t_window.isGroup(type.toString())) {
        JMenuItem groupNameItem = new JMenuItem(type.toString());
        groupNameItem.setEnabled(false);
        final Group group = new Group(0, 0, 0, 0);
        group.setZoneType(type.toString());
        

        Iterator localIterator2 = OCRInterface.getAttsConfigUtil().getTypeAttributesMap().entrySet().iterator();
        Iterator localIterator3;
        for (; localIterator2.hasNext(); 
            

            localIterator3.hasNext())
        {
          Map.Entry<String, Map<String, TypeAttributeEntry>> f = (Map.Entry)localIterator2.next();
          

          localIterator3 = ((Map)f.getValue()).entrySet().iterator(); continue;Map.Entry<String, TypeAttributeEntry> f2 = (Map.Entry)localIterator3.next();
          if (((String)f.getKey()).equals(group.getZoneType())) {
            group.setAttributeValue((String)f2.getKey(), ((TypeAttributeEntry)f2.getValue()).getDefaultValue());
          }
        }
        

        for (Zone z : activeZones) {
          if (!groupNameItem.isEnabled())
          {
            if (group.doesTypeBelongToGroup(z))
              groupNameItem.setEnabled(true);
          }
        }
        groupNameItem.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent arg0) {
            if (OCRInterface.currDoc == null) {
              ImageDisplay.nulldoc.add(group);
            }
            



            if (group.isGroupOfAny()) {
              for (Zone zone : ImageDisplay.activeZones) {
                if (zone.isGroup()) {
                  for (Zone z : ((Group)zone).getZonesOfGroup())
                    group.addToGroup(z);
                } else
                  group.addToGroup(zone);
              }
              for (Zone zone : ImageDisplay.activeZones) {
                if (zone.isGroup()) {
                  group.addToGroup(zone);
                }
              }
            } else {
              for (Zone zone : ImageDisplay.activeZones)
                group.addToGroup(zone);
            }
            if (group.getZonesOfGroup().isEmpty()) {
              clearActiveZones();
              return;
            }
            

            clearActiveZones();
            addToSelected(group);
            group.resizeGroupBox();
            insertZone(group, e);
            groupisIncomplete = false;
            
            ocrIF.tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
            
            paintCanvas();
          }
          
        });
        submenu.add(groupNameItem);
      }
    }
    
    item = new JMenuItem("Ungroup");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Vector<Group> groupsToDelete = new Vector();
        for (Zone z : ImageDisplay.activeZones) {
          if ((z.isGroup()) && 
            (getGroupsToWhichZoneBelongs(z).isEmpty())) {
            groupsToDelete.add((Group)z);
          }
        }
        

        if (!groupsToDelete.isEmpty()) {
          saveCurrentState();
        }
        for (Group g : groupsToDelete) {
          g.deleteGroup();
          ImageDisplay.activeZones.remove(g);
        }
        
        ocrIF.tbdPane.data_panel.a_window.showZoneInfo(ImageDisplay.activeZones);
        paintCanvas();
      }
    });
    
    if (activeZones.isEmpty()) {
      item.setEnabled(false);
    }
    int groupsSelected = 0;
    
    for (Zone z : activeZones) {
      if (z.isGroup()) {
        Object groups = getGroupsToWhichZoneBelongs(z);
        if ((groups == null) || (((Vector)groups).isEmpty())) {
          groupsSelected++;
          break;
        }
      }
    }
    
    if (groupsSelected == 0) {
      item.setEnabled(false);
    }
    
    menu.add(item);
    menu.addSeparator();
    




    item = new JMenuItem("Highlight all RO Headers");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Vector<DLZone> allZones = shapeVec.getAsVector();
        for (DLZone zone : allZones) {
          if (readingOrderHandler.isHeader(zone))
            addToSelected((Zone)zone);
        }
        paintCanvas();
      }
      

    });
    menu.add(item);
    if (!activeZones.isEmpty()) {
      item.setEnabled(false);
    }
    
    item = new JMenuItem("Highlight RO (must select Header)");
    item.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent arg0)
      {
        DLZone header = ImageDisplay.activeZones.elementAt(0);
        DLZone nextZone = nextZone;
        
        while (nextZone != null) {
          addToSelected((Zone)nextZone);
          nextZone = nextZone;
        }
        paintCanvas();
      }
    });
    

    if ((activeZones.isEmpty()) || 
      (!readingOrderHandler.isHeader(activeZones.elementAt(0)))) {
      item.setEnabled(false);
    }
    menu.add(item);
    
    menu.show(e.getComponent(), e.getX(), e.getY());
  }
  
  public void resetType(String newType) {
    Vector<Zone> activeGroups = getActiveGroups();
    if (activeGroups.isEmpty()) {
      resetZoneType(newType);
    } else
      resetGroupType(newType);
  }
  
  public void resetZoneType(String newType) {
    boolean changeType = true;
    Vector<Zone> zonesToChangeType = new Vector();
    for (Zone z : activeZones) {
      if (!z.isGroup())
      {
        Vector<Group> groups = getGroupsToWhichZoneBelongs(z);
        if (((groups == null) || (groups.isEmpty())) && (!z.isGroup())) {
          zonesToChangeType.add(z);
        } else {
          for (Group g : groups)
            if (g.doesTypeBelongToGroup(newType)) {
              zonesToChangeType.add(z);
            } else
              changeType = false;
        }
      }
    }
    setTypeToZones(newType, zonesToChangeType);
    if (!changeType)
      Group.showWarning(true, false);
  }
  
  public void resetGroupType(String newType) {
    boolean changeType = true;
    Vector<Zone> groupsToChangeType = new Vector();
    Set<String> typesOfGroup = ocrIF.tbdPane.data_panel.t_window
      .getTypesOfGroup(newType);
    Vector<Zone> activeGroups = getActiveGroups();
    for (Zone z : activeGroups)
    {
      Vector<Group> groups = getGroupsToWhichZoneBelongs(z);
      if (!groups.isEmpty()) {
        for (Group group : groups) {
          if (!group.doesTypeBelongToGroup(newType)) {
            changeType = false;
            break;
          }
        }
      }
      



      if (changeType) {
        if ((typesOfGroup.size() == 1) && 
          (((String)typesOfGroup.iterator().next()).equals("ANY"))) {
          groupsToChangeType.add(z);
        }
        else {
          for (Zone zone : ((Group)z).getZonesOfGroup()) {
            if (!typesOfGroup.contains(zone.getZoneType())) {
              changeType = false;
              break;
            }
          }
        }
        if (changeType) {
          groupsToChangeType.add(z);
        }
      }
    }
    setTypeToZones(newType, groupsToChangeType);
    if (!changeType) {
      Group.showWarning(false, true);
    }
  }
  
  public Vector<Zone> getActiveGroups() {
    Vector<Zone> groups = new Vector();
    for (Zone z : activeZones) {
      if (z.isGroup()) {
        Vector<Group> parentGroups = getGroupsToWhichZoneBelongs(z);
        if ((parentGroups != null) && (parentGroups.isEmpty())) {
          groups.add(z);
        }
        else
        {
          for (Group g : parentGroups)
            if (!activeZones.contains(g))
              groups.add(z);
        }
      }
    }
    for (Group g : (Vector)groups.clone()) {
      if ((!zoneID.equals(firstElementzoneID)) && 
        (((Zone)groups.firstElement()).contains(g))) {
        groups.remove(g);
      }
    }
    return groups;
  }
  
  public ReadingOrder getReadingOrderHandler() {
    return readingOrderHandler;
  }
  
  public void setAllowEdit(boolean allowEdit) { this.allowEdit = allowEdit; }
  






















  public void unlockSoftLock()
  {
    String baseImage = ImageReaderDrawer.getFile_path();
    int index = baseImage.lastIndexOf(".");
    String name = baseImage.substring(0, index);
    String ext = baseImage.substring(index, baseImage.length());
    
    String finalImage = "";
    if (ocrIF.getEnableTranslateWorkflow()) {
      finalImage = name + ext;
    } else {
      finalImage = name + ".final" + ext;
    }
    int mode = 0;
    int finalImageRow = ocrIF.workmodeProps[mode].getImageIndex(finalImage);
    
    if (finalImageRow != -1)
    {
      ocrIF.workmodeProps[ocrIF.ocrTable.my_mode].getElementFilePropVec(finalImageRow).setSoftLocked(1, false, finalImageRow);
    }
  }
  































  public int getBlackPixelValue()
  {
    System.out.println("curr img: " + ImageReaderDrawer.getFile_path());
    
    blackPixelValue = 0;
    System.out.println("Image color model/bands: " + colorModel.getClass() + 
      "/" + OCRInterface.this_interface.getCanvas().getImageRaster().getNumBands());
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
    } else if ((colorModel instanceof ComponentColorModel)) {
      for (int i = 0; i < 255; i++) {
        int sum = 0;
        try {
          sum = colorModel.getRed(i) + 
            colorModel.getGreen(i) + 
            colorModel.getBlue(i);
          if (sum == 0) {
            blackPixelValue = i;
            return i;
          }
        } catch (IllegalArgumentException e) {
          System.out.println("Cannot process color image: " + 
            ImageReaderDrawer.getFile_path() + " --> " + e.getMessage());
          return blackPixelValue;
        }
      }
    }
    

    return blackPixelValue;
  }
  







  public void rotateImageUsingRotationBits(boolean loadOriginal)
  {
    String orientStr = (String)this_interfacetbdPane.data_panel.a_window.getPage().pageTags.get("GEDI_orientation");
    





    if (orientStr != null) {
      return;
    }
    if (!new File(ImageReaderDrawer.getFile_path()).exists()) {
      return;
    }
    int orientation = getImageOrientation(new File(ImageReaderDrawer.getFile_path()));
    PlanarImage img = JAI.create("fileload", ImageReaderDrawer.getFile_path());
    TransposeType transposeType = null;
    
    switch (orientation) {
    case 3: 
      transposeType = TransposeDescriptor.ROTATE_180;
      break;
    case 6: 
      transposeType = TransposeDescriptor.ROTATE_90;
      break;
    case 8: 
      transposeType = TransposeDescriptor.ROTATE_270;
      break;
    case 4: case 5: case 7: default: 
      img = null;
    }
    
    
    if (img != null) {
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(img);
      pb.add(transposeType);
      PlanarImage rotatedImg = PlanarImage.wrapRenderedImage(JAI.create("transpose", pb));
      
      OCRInterface.currentHWObj.setImageFile(rotatedImg);
    }
    else if (loadOriginal) {
      OCRInterface.currentHWObj.setImageFile(JAI.create("fileload", ImageReaderDrawer.getFile_path()));
    }
  }
  
  private int getImageOrientation(File imageFile) {
    System.out.println("rotate image: " + imageFile);
    
    int orientation = 1;
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
      ExifIFD0Directory directory = (ExifIFD0Directory)metadata.getDirectory(ExifIFD0Directory.class);
      if (directory != null)
        orientation = directory.getInt(274);
    } catch (ImageProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (MetadataException e) {
      e.printStackTrace();
    }
    catch (NoClassDefFoundError e) {
      e.printStackTrace();
    }
    
    System.out.println("image orientation read from Exif: " + orientation);
    return orientation;
  }
  














  public BufferedImage convertToBW(BufferedImage img, int lowerValue, int upperValue)
  {
    short[] threshold = new short['Ā'];
    
    for (int i = 0; i < threshold.length; i++) {
      threshold[i] = ((i >= lowerValue) && (i <= upperValue) ? 0 : 255);
    }
    BufferedImageOp thresholdOp = new LookupOp(new ShortLookupTable(0, threshold), null);
    
    BufferedImage blackAndWhiteImage = new BufferedImage(
      img.getWidth(), 
      img.getHeight(), 
      10);
    
    Graphics g = blackAndWhiteImage.getGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose();
    
    BufferedImage imgOut = thresholdOp.createCompatibleDestImage(
      blackAndWhiteImage, 
      blackAndWhiteImage.getColorModel());
    
    thresholdOp.filter(blackAndWhiteImage, imgOut);
    
    return imgOut;
  }
  
  private void updateState() {
    if ((TypeWindow.selectedIndex != -1) && 
      (ocrIF.tbdPane.data_panel.t_window.isNormalBoxEnabled())) {
      OCRInterface.this_interface.setOppmode(2);
    } else if ((TypeWindow.selectedIndex != -1) && 
      (ocrIF.tbdPane.data_panel.t_window.isOBoxEnabled())) {
      OCRInterface.this_interface.setOppmode(15);
    } else if ((TypeWindow.selectedIndex != -1) && 
      (ocrIF.tbdPane.data_panel.t_window.isPolyBoxEnabled())) {
      OCRInterface.this_interface.setOppmode(16);
    } else
      OCRInterface.this_interface.setOppmode(0);
  }
  
  public void createRLE_CC(Zone zone, boolean rle, boolean cc, boolean deleteOriginalZone) {
    if ((zone == null) || (zone.isGroup())) { return;
    }
    
    if (cc)
      OCRInterface.this_interface.setOppmode(18);
    if ((rle) || (cc)) {
      zone.getZoneTags().remove("RLEIMAGE");
      new RLE_CC_Handler(getBlackPixelValue(), zone, rle, cc, scale);
      if ((cc) && (deleteOriginalZone))
        deleteZone(zone);
    }
    updateState();
  }
  








  private class AllZones
    extends Zone
  {
    public AllZones(String id, int x, int y, int w, int h) { super(x, y, w, h); }
    
    public boolean equals(Object obj) {
      String polygonStrObj = ((AllZones)obj).getAttributeValue("polygon");
      String polygonStrThis = getAttributeValue("polygon");
      
      if ((polygonStrObj != null) && 
        (polygonStrThis != null) && 
        (polygonStrObj.equals(polygonStrThis)))
        return true;
      if ((dlGetZoneOriginx == dlGetZoneOriginx) && 
        (dlGetZoneOriginy == dlGetZoneOriginy) && 
        (((AllZones)obj).dlGetZoneWidth() == dlGetZoneWidth()) && 
        (((AllZones)obj).dlGetZoneHeight() == dlGetZoneHeight()))
      {
        return true;
      }
      
      return false;
    }
    
    public int hashCode() {
      String polygonStr = getAttributeValue("polygon");
      int hash = 7;
      if ((polygonStr != null) && (!polygonStr.isEmpty())) {
        hash = 31 * hash + polygonStr.hashCode();
      } else {
        hash = 31 * hash + dlGetZoneOriginx;
        hash = 31 * hash + dlGetZoneOriginy;
        hash = 31 * hash + dlGetZoneWidth();
        hash = 31 * hash + dlGetZoneHeight();
      }
      
      return hash;
    }
  }
  
  public void setAllZones() {
    if (allZones == null)
      allZones = new HashSet();
    allZones.clear();
    for (DLZone z : shapeVec.getAsVector()) {
      allZones.add(new AllZones(
        zoneID, 
        dlGetZoneOriginx, 
        dlGetZoneOriginy, 
        z.dlGetZoneWidth(), 
        z.dlGetZoneHeight()));
    }
  }
  

  private boolean isDuplicatedZone(Zone zone)
  {
    boolean duplicate = !allZones.add(new AllZones(
      zoneID, 
      dlGetZoneOriginx, 
      dlGetZoneOriginy, 
      zone.dlGetZoneWidth(), 
      zone.dlGetZoneHeight()));
    
    if (ocrIF.getAllowIdenticalZones()) {
      if (duplicate)
        setIdenticalZoneWasCreated(true);
      return false;
    }
    
    return duplicate;
  }
  
  public boolean identicalZoneWasCreated() {
    return identicalZoneWasCreated;
  }
  
  public void setIdenticalZoneWasCreated(boolean identicalZoneWasCreated) { this.identicalZoneWasCreated = identicalZoneWasCreated; }
  
  public void showWarning_IdenticalZones()
  {
    if ((ocrIF.getAllowIdenticalZones()) && 
      (ocrIF.getWarnIdenticalZonesWereCreated()) && 
      (currentHWObjcurr_canvas.identicalZoneWasCreated())) {
      JOptionPane.showMessageDialog(OCRInterface.this_interface, 
        "WARNING:\nYou have created duplicate zones.\nThis is simply a notification message. Click OK to continue.\n\nUse \"Preferences\" menu to disable this warning, or to  disallow creation of duplicate zones altogether.", 
        


        "Duplicate Zones", 
        1);
      currentHWObjcurr_canvas.setIdenticalZoneWasCreated(false);
    }
  }
  
  public void setToBeEdited(Zone toBeEdited) {
    this.toBeEdited = toBeEdited;
  }
  
  public void addToGroupList(Group groupIn) {
    if (groupList == null) {
      groupList = new Vector();
    }
    if (!groupList.contains(groupIn))
      groupList.add(groupIn);
  }
  
  public Vector<Group> getGroupList() {
    if (groupList == null) {
      groupList = new Vector();
    }
    return groupList;
  }
  
  public void resizeGroupToWhichZoneBelongs(Zone zone) {
    if (groupList == null)
      return;
    for (Group group : groupList) {
      if ((group.containsZone(zone)) && 
        (!zoneID.equals(zoneID)))
      {
        group.resizeGroupBox();
      }
    }
  }
  

  public void resizeAllGroups()
  {
    if ((groupList == null) || (groupList.isEmpty())) {
      return;
    }
    
    Vector<Group> groupList_clone = (Vector)groupList.clone();
    for (Group group : groupList_clone) {
      group.resizeGroupBox();
    }
    
    if (OCRInterface.currDoc != null)
      OCRInterface.currDoc.dumpData();
  }
  
  public Vector<Group> getGroupsToWhichZoneBelongs(Zone zone) {
    if ((groupList == null) || (groupList.isEmpty())) {
      return null;
    }
    Vector<Group> groups = new Vector();
    for (Group group : groupList) {
      if ((group.containsZone(zone)) && 
        (!zoneID.equals(zoneID)))
      {
        groups.add(group);
      }
    }
    return groups;
  }
  





  private boolean isSpecialCase()
  {
    return (OCRInterface.this_interface.getEnablePolygonTranscription()) && (this_interfacecurrState == 3) && (activeZones.size() == 2);
  }
  
  public void setStartMeasureMouseEvent(MouseEvent e) {
    startMeasureMouseEvent = e;
  }
  
  public String getClipboard() {
    Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    try
    {
      if ((t != null) && (t.isDataFlavorSupported(DataFlavor.stringFlavor))) {
        return (String)t.getTransferData(DataFlavor.stringFlavor);
      }
    }
    catch (UnsupportedFlavorException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private void setClipboard(String str) {
    StringSelection ss = new StringSelection(str);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
  }
  
  public void setClipboard() {
    String str = "";
    currentHWObjcurr_canvas.zvec.clear();
    
    if (OCRInterface.this_interface.getCtrl_C_Copies().trim().equalsIgnoreCase("<ZONE>")) {
      currentHWObjcurr_canvas.copyZones();
    }
    else {
      String attr = OCRInterface.this_interface.getCtrl_C_Copies();
      String separator = OCRInterface.this_interface.getCtrl_C_separator();
      str = 
        this_interfacetbdPane.data_panel.a_window.getAttributeTable().copyValues(attr, separator);
    }
    
    setClipboard(str);
  }
  
  public Raster getImageRaster() {
    return imageRaster;
  }
}
