package javax.media.jai.widget;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;























































/**
 * @deprecated
 */
public class ImageCanvas
  extends Canvas
{
  protected RenderedImage im;
  protected SampleModel sampleModel;
  protected ColorModel colorModel;
  protected int minTileX;
  protected int maxTileX;
  protected int minTileY;
  protected int maxTileY;
  protected int tileWidth;
  protected int tileHeight;
  protected int tileGridXOffset;
  protected int tileGridYOffset;
  protected int imWidth;
  protected int imHeight;
  protected int padX;
  protected int padY;
  protected boolean drawBorder = false;
  

  protected int originX;
  
  protected int originY;
  
  protected int canvasWidth = 0;
  
  protected int canvasHeight = 0;
  
  private Color grayColor = new Color(192, 192, 192);
  
  private Color backgroundColor = null;
  
  private synchronized void initialize()
  {
    int mx = im.getMinX();
    int my = im.getMinY();
    if ((mx < 0) || (my < 0)) {
      ParameterBlock pb = new ParameterBlock();
      pb.addSource(im);
      pb.add(Math.max(-mx, 0));
      pb.add(Math.max(-my, 0));
      pb.add(new InterpolationNearest());
      im = JAI.create("translate", pb, null);
    }
    
    sampleModel = im.getSampleModel();
    

    colorModel = im.getColorModel();
    if (colorModel == null)
    {
      colorModel = PlanarImage.createColorModel(im.getSampleModel());
      if (colorModel == null) {
        throw new IllegalArgumentException(JaiI18N.getString("ImageCanvas0"));
      }
    }
    
    Object col = im.getProperty("background_color");
    if (col != Image.UndefinedProperty) {
      backgroundColor = ((Color)col);
    }
    
    minTileX = im.getMinTileX();
    maxTileX = (im.getMinTileX() + im.getNumXTiles() - 1);
    minTileY = im.getMinTileY();
    maxTileY = (im.getMinTileY() + im.getNumYTiles() - 1);
    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    tileGridXOffset = im.getTileGridXOffset();
    tileGridYOffset = im.getTileGridYOffset();
    
    imWidth = (im.getMinX() + im.getWidth());
    imHeight = (im.getMinY() + im.getHeight());
    
    originX = (this.originY = 0);
  }
  





  public ImageCanvas(RenderedImage im, boolean drawBorder)
  {
    this.im = im;
    this.drawBorder = drawBorder;
    initialize();
  }
  




  public ImageCanvas(RenderedImage im)
  {
    this(im, false);
  }
  
  public void addNotify() {
    super.addNotify();
    initialize();
  }
  
  public synchronized void set(RenderedImage im)
  {
    this.im = im;
    initialize();
    repaint();
  }
  
  public void setOrigin(int x, int y)
  {
    padX = 0;
    padY = 0;
    originX = x;
    originY = y;
    repaint();
  }
  
  public int getXOrigin() {
    return originX;
  }
  
  public int getYOrigin() {
    return originY;
  }
  
  public int getXPad() {
    return padX;
  }
  
  public int getYPad() {
    return padY;
  }
  
  public Dimension getMinimumSize() {
    return new Dimension(im.getMinX() + im.getWidth() + (drawBorder ? 4 : 0), im.getMinY() + im.getHeight() + (drawBorder ? 4 : 0));
  }
  


  public Dimension getPreferredSize()
  {
    return getMinimumSize();
  }
  
  public Dimension getMaximumSize() {
    return getMinimumSize();
  }
  
  public void setBounds(int x, int y, int width, int height)
  {
    super.setBounds(x, y, width, height);
    canvasWidth = width;
    canvasHeight = height;
    
    padX = Math.max((canvasWidth - imWidth - (drawBorder ? 4 : 0)) / 2, 0);
    padY = Math.max((canvasHeight - imHeight - (drawBorder ? 4 : 0)) / 2, 0);
  }
  
  private int XtoTileX(int x) {
    return (int)Math.floor((x - tileGridXOffset) / tileWidth);
  }
  
  private int YtoTileY(int y) {
    return (int)Math.floor((y - tileGridYOffset) / tileHeight);
  }
  
  private int TileXtoX(int tx) {
    return tx * tileWidth + tileGridXOffset;
  }
  
  private int TileYtoY(int ty) {
    return ty * tileHeight + tileGridYOffset;
  }
  



  public void update(Graphics g)
  {
    paint(g);
  }
  





  public synchronized void paint(Graphics g)
  {
    if (im == null) {
      return;
    }
    
    Graphics2D g2D = null;
    if ((g instanceof Graphics2D)) {
      g2D = (Graphics2D)g;
    } else {
      System.err.println(JaiI18N.getString("ImageCanvas1"));
      return;
    }
    
    Color saveColor = g2D.getColor();
    
    if (drawBorder) {
      g.setColor(new Color(171, 171, 171));
      g.draw3DRect(padX, padY, imWidth + 3, imHeight + 3, true);
      


      g.draw3DRect(padX + 1, padY + 1, imWidth + 1, imHeight + 1, true);
    }
    




    Rectangle clipBounds = g.getClipBounds();
    if (clipBounds == null) {
      clipBounds = new Rectangle(0, 0, canvasWidth, canvasHeight);
    }
    
    int border = drawBorder ? 2 : 0;
    int transX = padX + border - originX;
    int transY = padY + border - originY;
    
    clipBounds.translate(-transX, -transY);
    



    int txmin = XtoTileX(x);
    txmin = Math.max(txmin, minTileX);
    txmin = Math.min(txmin, maxTileX);
    
    int txmax = XtoTileX(x + width - 1);
    txmax = Math.max(txmax, minTileX);
    txmax = Math.min(txmax, maxTileX);
    
    int tymin = YtoTileY(y);
    tymin = Math.max(tymin, minTileY);
    tymin = Math.min(tymin, maxTileY);
    
    int tymax = YtoTileY(y + height - 1);
    tymax = Math.max(tymax, minTileY);
    tymax = Math.min(tymax, maxTileY);
    
    if (backgroundColor != null) {
      g2D.setColor(backgroundColor);
    }
    else {
      g2D.setColor(grayColor);
    }
    
    int xmin = im.getMinX();
    int xmax = im.getMinX() + im.getWidth();
    int ymin = im.getMinY();
    int ymax = im.getMinY() + im.getHeight();
    int screenX = x + width;
    int screenY = y + height;
    

    if (xmin > x) {
      g2D.fillRect(x + transX, y + transY, xmin - x, height);
    }
    




    if (xmax < screenX) {
      g2D.fillRect(xmax + transX, y + transY, screenX - xmax, height);
    }
    




    if (ymin > y) {
      g2D.fillRect(xmin + transX, y + transY, xmax - xmin, ymin - y);
    }
    




    if (ymax < screenY) {
      g2D.fillRect(xmin + transX, ymax + transY, xmax - xmin, screenY - ymax);
    }
    




    g2D.setClip(new Rectangle(transX + im.getMinX(), transY + im.getMinY(), im.getWidth(), im.getHeight()));
    




    Point[] tileIndices = new Point[(txmax - txmin + 1) * (tymax - tymin + 1)];
    int index = 0;
    for (int tj = tymin; tj <= tymax; tj++) {
      for (int ti = txmin; ti <= txmax; ti++) {
        tileIndices[(index++)] = new Point(ti, tj);
      }
    }
    Raster[] tiles = PlanarImage.wrapRenderedImage(im).getTiles(tileIndices);
    


    int numTiles = tiles.length;
    for (int tileNum = 0; tileNum < numTiles; tileNum++) {
      Raster tile = tiles[tileNum];
      
      int tx = tile.getMinX();
      int ty = tile.getMinY();
      
      if (tile != null) {
        WritableRaster wr = (tile instanceof WritableRaster) ? ((WritableRaster)tile).createWritableTranslatedChild(0, 0) : Raster.createWritableRaster(sampleModel, tile.getDataBuffer(), new Point(0, 0));
        





        BufferedImage bi = new BufferedImage(colorModel, wr, colorModel.isAlphaPremultiplied(), null);
        




        AffineTransform transform = AffineTransform.getTranslateInstance(tx + transX, ty + transY);
        

        if (backgroundColor != null) {
          g2D.fillRect(tx + transX, ty + transY, tileWidth, tileHeight);
        }
        
        g2D.drawRenderedImage(bi, transform);
      }
    }
    

    g2D.setColor(saveColor);
    notifyPaintListeners(g2D);
  }
  

















  private HashSet paintListeners = new HashSet();
  




  public void addPaintListener(PaintListener pl)
  {
    paintListeners.add(pl);
  }
  




  public void removePaintListener(PaintListener pl)
  {
    paintListeners.remove(pl);
  }
  
  private void notifyPaintListeners(Graphics g)
  {
    Iterator it = paintListeners.iterator();
    
    while (it.hasNext()) {
      ((PaintListener)it.next()).paint(this, g);
    }
  }
  
  public static abstract interface PaintListener
  {
    public abstract void paint(ImageCanvas paramImageCanvas, Graphics paramGraphics);
  }
}
