package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;











































class TiledImageGraphics
  extends Graphics2D
{
  private static final Class GRAPHICS2D_CLASS = Graphics2D.class;
  
  private static final int PAINT_MODE = 1;
  
  private static final int XOR_MODE = 2;
  
  private TiledImage tiledImage;
  
  Hashtable properties;
  
  private RenderingHints renderingHints;
  
  private int tileWidth;
  private int tileHeight;
  private int tileXMinimum;
  private int tileXMaximum;
  private int tileYMinimum;
  private int tileYMaximum;
  private ColorModel colorModel;
  private Point origin;
  private Shape clip;
  private Color color;
  private Font font;
  private int paintMode = 1;
  

  private Color XORColor;
  

  private Color background;
  

  private Composite composite;
  

  private Paint paint;
  

  private Stroke stroke;
  

  private AffineTransform transform;
  

  private static final Rectangle getBoundingBox(int[] xPoints, int[] yPoints, int nPoints)
  {
    if (nPoints <= 0) { return null;
    }
    

    int maxX;
    

    int minX = maxX = xPoints[0];
    int maxY; int minY = maxY = yPoints[0];
    
    for (int i = 1; i < nPoints; i++) {
      minX = Math.min(minX, xPoints[i]);
      maxX = Math.max(maxX, xPoints[i]);
      minY = Math.min(minY, yPoints[i]);
      maxY = Math.max(maxY, yPoints[i]);
    }
    
    return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
  }
  













  public TiledImageGraphics(TiledImage im)
  {
    int dataType = im.getSampleModel().getTransferType();
    if ((dataType != 0) && (dataType != 2) && (dataType != 1) && (dataType != 3))
    {


      throw new UnsupportedOperationException(JaiI18N.getString("TiledImageGraphics0"));
    }
    

    tiledImage = im;
    

    tileWidth = im.getTileWidth();
    tileHeight = im.getTileHeight();
    tileXMinimum = im.getMinTileX();
    tileXMaximum = im.getMaxTileX();
    tileYMinimum = im.getMinTileY();
    tileYMaximum = im.getMaxTileY();
    

    colorModel = getColorModel(tiledImage);
    

    Graphics2D g = getBogusGraphics2D(false);
    

    origin = new Point(0, 0);
    setClip(tiledImage.getBounds());
    setColor(g.getColor());
    setFont(g.getFont());
    setPaintMode();
    

    setBackground(g.getBackground());
    setComposite(g.getComposite());
    setStroke(g.getStroke());
    setTransform(g.getTransform());
    

    g.dispose();
    


    properties = tiledImage.getProperties();
    

    renderingHints = new RenderingHints(properties);
  }
  






  private void copyState(Graphics2D g2d)
  {
    g2d.translate(origin.x, origin.y);
    setClip(getClip());
    g2d.setColor(getColor());
    if (paintMode == 1) {
      g2d.setPaintMode();
    } else if (XORColor != null) {
      g2d.setXORMode(XORColor);
    }
    g2d.setFont(getFont());
    

    g2d.setBackground(getBackground());
    g2d.setComposite(getComposite());
    if (paint != null) g2d.setPaint(getPaint());
    g2d.setRenderingHints(renderingHints);
    g2d.setStroke(getStroke());
    g2d.setTransform(getTransform());
  }
  













  private Graphics2D getBogusGraphics2D(boolean shouldCopyState)
  {
    Raster r = tiledImage.getTile(tileXMinimum, tileYMinimum);
    WritableRaster wr = r.createCompatibleWritableRaster(1, 1);
    BufferedImage bi = new BufferedImage(colorModel, wr, colorModel.isAlphaPremultiplied(), properties);
    


    Graphics2D bogusG2D = bi.createGraphics();
    if (shouldCopyState) {
      copyState(bogusG2D);
    }
    return bogusG2D;
  }
  











  private static ColorModel getColorModel(TiledImage ti)
  {
    ColorModel colorModel = ti.getColorModel();
    
    if (colorModel == null)
    {
      if (colorModel == null)
      {
        SampleModel sm = ti.getSampleModel();
        colorModel = PlanarImage.createColorModel(sm);
        if (colorModel == null)
        {
          ColorModel cm = ColorModel.getRGBdefault();
          if (JDKWorkarounds.areCompatibleDataModels(sm, cm)) {
            colorModel = cm;
          }
        }
        

        if (colorModel == null) {
          throw new UnsupportedOperationException(JaiI18N.getString("TiledImageGraphics1"));
        }
      }
    }
    
    return colorModel;
  }
  
















  private boolean doGraphicsOp(int x, int y, int width, int height, String name, Class[] argTypes, Object[] args)
  {
    boolean returnValue = false;
    



    Method method = null;
    try {
      method = GRAPHICS2D_CLASS.getMethod(name, argTypes);
    } catch (Exception e) {
      String message = JaiI18N.getString("TiledImageGraphics2") + name;
      sendExceptionToListener(message, new ImagingException(e));
    }
    


    Rectangle bounds = new Rectangle(x, y, width, height);
    bounds = getTransform().createTransformedShape(bounds).getBounds();
    

    int minTileX = tiledImage.XToTileX(x);
    if (minTileX < tileXMinimum)
      minTileX = tileXMinimum;
    int minTileY = tiledImage.YToTileY(y);
    if (minTileY < tileYMinimum)
      minTileY = tileYMinimum;
    int maxTileX = tiledImage.XToTileX(x + width - 1);
    if (maxTileX > tileXMaximum)
      maxTileX = tileXMaximum;
    int maxTileY = tiledImage.YToTileY(y + height - 1);
    if (maxTileY > tileYMaximum) {
      maxTileY = tileYMaximum;
    }
    
    for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
      int tileMinY = tiledImage.tileYToY(tileY);
      for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
        int tileMinX = tiledImage.tileXToX(tileX);
        

        WritableRaster wr = tiledImage.getWritableTile(tileX, tileY);
        wr = wr.createWritableTranslatedChild(0, 0);
        

        BufferedImage bi = new BufferedImage(colorModel, wr, colorModel.isAlphaPremultiplied(), properties);
        




        Graphics2D g2d = bi.createGraphics();
        

        copyState(g2d);
        

        try
        {
          Point2D origin2D = g2d.getTransform().transform(new Point2D.Double(), null);
          

          Point pt = new Point((int)origin2D.getX() - tileMinX, (int)origin2D.getY() - tileMinY);
          
          Point2D pt2D = g2d.getTransform().inverseTransform(pt, null);
          
          g2d.translate(pt2D.getX(), pt2D.getY());
        } catch (Exception e) {
          String message = JaiI18N.getString("TiledImageGraphics3");
          sendExceptionToListener(message, new ImagingException(e));
        }
        

        try
        {
          Object retVal = method.invoke(g2d, args);
          if ((retVal != null) && (retVal.getClass() == Boolean.TYPE)) {
            returnValue = ((Boolean)retVal).booleanValue();
          }
        } catch (Exception e) {
          String message = JaiI18N.getString("TiledImageGraphics3") + " " + name;
          
          sendExceptionToListener(message, new ImagingException(e));
        }
        


        g2d.dispose();
        

        tiledImage.releaseWritableTile(tileX, tileY);
      }
    }
    
    return returnValue;
  }
  












  private boolean doGraphicsOp(Shape s, String name, Class[] argTypes, Object[] args)
  {
    Rectangle r = s.getBounds();
    return doGraphicsOp(x, y, width, height, name, argTypes, args);
  }
  


  public Graphics create()
  {
    TiledImageGraphics tig = new TiledImageGraphics(tiledImage);
    

    copyState(tig);
    
    return tig;
  }
  


  public Color getColor()
  {
    return color;
  }
  
  public void setColor(Color c) {
    color = c;
  }
  
  public void setPaintMode() {
    paintMode = 1;
  }
  
  public void setXORMode(Color c1) {
    paintMode = 2;
    XORColor = c1;
  }
  
  public Font getFont() {
    return font;
  }
  
  public void setFont(Font font) {
    this.font = font;
  }
  
  public FontMetrics getFontMetrics(Font f) {
    Graphics2D g2d = getBogusGraphics2D(true);
    
    FontMetrics fontMetrics = g2d.getFontMetrics(f);
    
    g2d.dispose();
    
    return fontMetrics;
  }
  
  public Rectangle getClipBounds() {
    return clip.getBounds();
  }
  
  public void clipRect(int x, int y, int width, int height) {
    clip(new Rectangle(x, y, width, height));
  }
  
  public void setClip(int x, int y, int width, int height) {
    setClip(new Rectangle(x, y, width, height));
  }
  
  public Shape getClip() {
    return clip;
  }
  
  public void setClip(Shape clip) {
    this.clip = clip;
  }
  
  public void copyArea(int x, int y, int width, int height, int dx, int dy)
  {
    Rectangle rect = getBoundingBox(new int[] { x, x + dx, x + width - 1, x + width - 1 + dx }, new int[] { y, y + dy, y + height - 1, y + height - 1 + dy }, 4);
    



    doGraphicsOp(rect, "copyArea", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(dx), new Integer(dy) });
  }
  




  public void drawLine(int x1, int y1, int x2, int y2)
  {
    Rectangle rect = getBoundingBox(new int[] { x1, x2 }, new int[] { y1, y2 }, 2);
    

    doGraphicsOp(rect, "drawLine", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x1), new Integer(y1), new Integer(x2), new Integer(y2) });
  }
  


  public void fillRect(int x, int y, int width, int height)
  {
    doGraphicsOp(x, y, width, height, "fillRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  





  public void clearRect(int x, int y, int width, int height)
  {
    doGraphicsOp(x, y, width, height, "clearRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  



  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    doGraphicsOp(x - arcWidth, y - arcHeight, width + 2 * arcWidth, height + 2 * arcHeight, "drawRoundRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight) });
  }
  







  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    doGraphicsOp(x - arcWidth, y - arcHeight, width + 2 * arcWidth, height + 2 * arcHeight, "fillRoundRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight) });
  }
  










  public void draw3DRect(int x, int y, int width, int height, boolean raised)
  {
    doGraphicsOp(x, y, width, height, "draw3DRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised) });
  }
  








  public void fill3DRect(int x, int y, int width, int height, boolean raised)
  {
    doGraphicsOp(x, y, width, height, "fill3DRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised) });
  }
  





  public void drawOval(int x, int y, int width, int height)
  {
    doGraphicsOp(x, y, width, height, "drawOval", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  


  public void fillOval(int x, int y, int width, int height)
  {
    doGraphicsOp(x, y, width, height, "fillOval", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  



  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    doGraphicsOp(x, y, width, height, "drawArc", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle) });
  }
  






  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    doGraphicsOp(x, y, width, height, "fillArc", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle) });
  }
  





  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    Rectangle box = getBoundingBox(xPoints, yPoints, nPoints);
    
    if (box == null) { return;
    }
    doGraphicsOp(box, "drawPolyline", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  

  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    Rectangle box = getBoundingBox(xPoints, yPoints, nPoints);
    
    if (box == null) { return;
    }
    doGraphicsOp(box, "drawPolygon", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  



  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    Rectangle box = getBoundingBox(xPoints, yPoints, nPoints);
    
    if (box == null) { return;
    }
    doGraphicsOp(box, "fillPolygon", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  



  public void drawString(String str, int x, int y)
  {
    Rectangle2D r2d = getFontMetrics(getFont()).getStringBounds(str, this);
    


    r2d.setRect(x, y - r2d.getHeight() + 1.0D, r2d.getWidth(), r2d.getHeight());
    

    doGraphicsOp(r2d, "drawString", new Class[] { String.class, Integer.TYPE, Integer.TYPE }, new Object[] { str, new Integer(x), new Integer(y) });
  }
  







  public boolean drawImage(Image img, int x, int y, ImageObserver observer)
  {
    return doGraphicsOp(x, y, img.getWidth(observer), img.getHeight(observer), "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), observer });
  }
  








  public void drawRenderedImage(RenderedImage im, AffineTransform transform)
  {
    Rectangle2D.Double srcRect = new Rectangle2D.Double(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
    



    Rectangle2D dstRect = transform.createTransformedShape(srcRect).getBounds2D();
    

    doGraphicsOp(dstRect, "drawRenderedImage", new Class[] { RenderedImage.class, AffineTransform.class }, new Object[] { im, transform });
  }
  



  public void drawRenderableImage(RenderableImage img, AffineTransform xform)
  {
    Rectangle2D.Double srcRect = new Rectangle2D.Double(img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight());
    



    Rectangle2D dstRect = xform.createTransformedShape(srcRect).getBounds2D();
    

    doGraphicsOp(dstRect, "drawRenderableImage", new Class[] { RenderableImage.class, AffineTransform.class }, new Object[] { img, xform });
  }
  




  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
  {
    return doGraphicsOp(x, y, width, height, "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), observer });
  }
  







  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
  {
    return doGraphicsOp(x, y, img.getWidth(observer), img.getHeight(observer), "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), bgcolor, observer });
  }
  










  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
  {
    return doGraphicsOp(x, y, width, height, "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), bgcolor, observer });
  }
  









  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
  {
    return doGraphicsOp(dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1, "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), observer });
  }
  













  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
  {
    return doGraphicsOp(dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1, "drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), bgcolor, observer });
  }
  









  public void dispose() {}
  








  public void addRenderingHints(Map hints)
  {
    renderingHints.putAll(hints);
  }
  
  public void draw(Shape s) {
    doGraphicsOp(s.getBounds(), "draw", new Class[] { Shape.class }, new Object[] { s });
  }
  




  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
  {
    Rectangle2D.Double srcRect = new Rectangle2D.Double(0.0D, 0.0D, img.getWidth(obs), img.getHeight(obs));
    


    Rectangle2D dstRect = transform.createTransformedShape(srcRect).getBounds2D();
    

    return doGraphicsOp(dstRect, "drawImage", new Class[] { Image.class, AffineTransform.class, ImageObserver.class }, new Object[] { img, xform, obs });
  }
  






  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
  {
    doGraphicsOp(op.getBounds2D(img), "drawImage", new Class[] { BufferedImage.class, BufferedImageOp.class, Integer.TYPE, Integer.TYPE }, new Object[] { img, op, new Integer(x), new Integer(y) });
  }
  






  public void drawString(String s, float x, float y)
  {
    Rectangle2D r2d = getFontMetrics(getFont()).getStringBounds(s, this);
    


    r2d.setRect(x, y - r2d.getHeight() + 1.0D, r2d.getWidth(), r2d.getHeight());
    

    doGraphicsOp(r2d, "drawString", new Class[] { String.class, Float.TYPE, Float.TYPE }, new Object[] { s, new Float(x), new Float(y) });
  }
  




  public void drawString(AttributedCharacterIterator iterator, int x, int y)
  {
    Rectangle2D r2d = getFontMetrics(getFont()).getStringBounds(iterator, iterator.getBeginIndex(), iterator.getEndIndex(), this);
    





    r2d.setRect(x, y - r2d.getHeight() + 1.0D, r2d.getWidth(), r2d.getHeight());
    

    doGraphicsOp(r2d, "drawString", new Class[] { AttributedCharacterIterator.class, Integer.TYPE, Integer.TYPE }, new Object[] { iterator, new Integer(x), new Integer(y) });
  }
  




  public void drawString(AttributedCharacterIterator iterator, float x, float y)
  {
    Rectangle2D r2d = getFontMetrics(getFont()).getStringBounds(iterator, iterator.getBeginIndex(), iterator.getEndIndex(), this);
    





    r2d.setRect(x, y - r2d.getHeight() + 1.0D, r2d.getWidth(), r2d.getHeight());
    

    doGraphicsOp(r2d, "drawString", new Class[] { AttributedCharacterIterator.class, Float.TYPE, Float.TYPE }, new Object[] { iterator, new Float(x), new Float(y) });
  }
  





  public void drawGlyphVector(GlyphVector g, float x, float y)
  {
    doGraphicsOp(g.getVisualBounds(), "drawGlyphVector", new Class[] { GlyphVector.class, Float.TYPE, Float.TYPE }, new Object[] { g, new Float(x), new Float(y) });
  }
  



  public void fill(Shape s)
  {
    doGraphicsOp(s.getBounds(), "fill", new Class[] { Shape.class }, new Object[] { s });
  }
  




  public boolean hit(Rectangle rect, Shape s, boolean onStroke)
  {
    Graphics2D g2d = getBogusGraphics2D(true);
    
    boolean hitTarget = g2d.hit(rect, s, onStroke);
    
    g2d.dispose();
    
    return hitTarget;
  }
  
  public GraphicsConfiguration getDeviceConfiguration() {
    Graphics2D g2d = getBogusGraphics2D(true);
    
    GraphicsConfiguration gConf = g2d.getDeviceConfiguration();
    
    g2d.dispose();
    
    return gConf;
  }
  
  public void setComposite(Composite comp) {
    composite = comp;
  }
  
  public void setPaint(Paint paint) {
    this.paint = paint;
  }
  
  public void setStroke(Stroke s) {
    stroke = s;
  }
  
  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue)
  {
    renderingHints.put(hintKey, hintValue);
  }
  
  public Object getRenderingHint(RenderingHints.Key hintKey) {
    return renderingHints.get(hintKey);
  }
  
  public void setRenderingHints(Map hints) {
    renderingHints.putAll(hints);
  }
  
  public RenderingHints getRenderingHints() {
    return renderingHints;
  }
  
  public void translate(int x, int y) {
    origin = new Point(x, y);
    transform.translate(x, y);
  }
  
  public void translate(double x, double y) {
    transform.translate(x, y);
  }
  
  public void rotate(double theta) {
    transform.rotate(theta);
  }
  
  public void rotate(double theta, double x, double y) {
    transform.rotate(theta, x, y);
  }
  
  public void scale(double sx, double sy) {
    transform.scale(sx, sy);
  }
  
  public void shear(double shx, double shy) {
    transform.shear(shx, shy);
  }
  
  public void transform(AffineTransform Tx) {
    transform.concatenate(Tx);
  }
  
  public void setTransform(AffineTransform Tx) {
    transform = Tx;
  }
  
  public AffineTransform getTransform() {
    return transform;
  }
  
  public Paint getPaint() {
    return paint;
  }
  
  public Composite getComposite() {
    return composite;
  }
  
  public void setBackground(Color color) {
    background = color;
  }
  
  public Color getBackground() {
    return background;
  }
  
  public Stroke getStroke() {
    return stroke;
  }
  
  public void clip(Shape s) {
    if (clip == null) {
      clip = s;
    } else {
      Area clipArea = (clip instanceof Area) ? (Area)clip : new Area(clip);
      
      clipArea.intersect((s instanceof Area) ? (Area)s : new Area(s));
      clip = clipArea;
    }
  }
  
  public FontRenderContext getFontRenderContext() {
    Graphics2D g2d = getBogusGraphics2D(true);
    
    FontRenderContext fontRenderContext = g2d.getFontRenderContext();
    
    g2d.dispose();
    
    return fontRenderContext;
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = null;
    if (renderingHints != null) {
      listener = (ImagingListener)renderingHints.get(JAI.KEY_IMAGING_LISTENER);
    }
    
    if (listener == null)
      listener = JAI.getDefaultInstance().getImagingListener();
    listener.errorOccurred(message, e, this, false);
  }
}
