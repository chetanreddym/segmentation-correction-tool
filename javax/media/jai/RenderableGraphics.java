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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;








































public class RenderableGraphics
  extends Graphics2D
  implements RenderableImage
{
  private static final Class GRAPHICS2D_CLASS = Graphics2D.class;
  
  private Rectangle2D dimensions;
  
  private LinkedList opArgList;
  
  private Point origin;
  
  private Shape clip;
  
  private Color color;
  
  private Font font;
  
  private Color background;
  
  private Composite composite;
  private Paint paint;
  private Stroke stroke;
  private RenderingHints renderingHints = new RenderingHints(null);
  


  private AffineTransform transform;
  


  public RenderableGraphics(Rectangle2D dimensions)
  {
    this(dimensions, new LinkedList(), new Point(0, 0), null);
  }
  











  private RenderableGraphics(Rectangle2D dimensions, LinkedList opArgList, Point origin, Graphics2D g)
  {
    if (dimensions.isEmpty()) {
      throw new RuntimeException(JaiI18N.getString("RenderableGraphics0"));
    }
    

    this.dimensions = dimensions;
    this.opArgList = opArgList;
    

    Graphics2D g2d = g;
    if (g2d == null) {
      g2d = getBogusGraphics2D();
    }
    

    this.origin = ((Point)origin.clone());
    setClip(g2d.getClip());
    setColor(g2d.getColor());
    setFont(g2d.getFont());
    

    setBackground(g2d.getBackground());
    setComposite(g2d.getComposite());
    setRenderingHints(g2d.getRenderingHints());
    setStroke(g2d.getStroke());
    setTransform(g2d.getTransform());
    

    if (g == null) { g2d.dispose();
    }
  }
  








  private Graphics2D getBogusGraphics2D()
  {
    TiledImage ti = createTiledImage(renderingHints, dimensions.getBounds());
    

    return ti.createGraphics();
  }
  









  private TiledImage createTiledImage(RenderingHints hints, Rectangle bounds)
  {
    int tileWidth = width;
    int tileHeight = height;
    



    SampleModel sm = null;
    ColorModel cm = null;
    RenderingHints hintsObserved = null;
    if (hints != null)
    {
      ImageLayout layout = (ImageLayout)hints.get(JAI.KEY_IMAGE_LAYOUT);
      
      if (layout != null)
      {
        hintsObserved = new RenderingHints(null);
        ImageLayout layoutObserved = new ImageLayout();
        

        if (layout.isValid(256)) {
          sm = layout.getSampleModel(null);
          if ((sm.getWidth() != tileWidth) || (sm.getHeight() != tileHeight))
          {
            sm = sm.createCompatibleSampleModel(tileWidth, tileHeight);
          }
          
          if (layoutObserved != null) {
            layoutObserved.setSampleModel(sm);
          }
        }
        

        if (layout.isValid(512)) {
          cm = layout.getColorModel(null);
          if (layoutObserved != null) {
            layoutObserved.setColorModel(cm);
          }
        }
        

        if (layout.isValid(64)) {
          tileWidth = layout.getTileWidth(null);
          if (layoutObserved != null) {
            layoutObserved.setTileWidth(tileWidth);
          }
        } else if (sm != null) {
          tileWidth = sm.getWidth();
        }
        if (layout.isValid(128)) {
          tileHeight = layout.getTileHeight(null);
          if (layoutObserved != null) {
            layoutObserved.setTileHeight(tileHeight);
          }
        } else if (sm != null) {
          tileHeight = sm.getHeight();
        }
        

        hintsObserved.put(JAI.KEY_IMAGE_LAYOUT, layoutObserved);
      }
    }
    

    if ((sm != null) && ((sm.getWidth() != tileWidth) || (sm.getHeight() != tileHeight)))
    {
      sm = sm.createCompatibleSampleModel(tileWidth, tileHeight);
    }
    

    if ((cm != null) && ((sm == null) || (!JDKWorkarounds.areCompatibleDataModels(sm, cm))))
    {


      sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);
    } else if ((cm == null) && (sm != null))
    {

      cm = PlanarImage.createColorModel(sm);
      


      ColorModel cmRGB = ColorModel.getRGBdefault();
      if ((cm == null) && (JDKWorkarounds.areCompatibleDataModels(sm, cmRGB)))
      {
        cm = cmRGB;
      }
    }
    

    TiledImage ti = null;
    if (sm != null)
    {
      ti = new TiledImage(x, y, width, height, x, y, sm, cm);

    }
    else
    {

      ti = TiledImage.createInterleaved(x, y, width, height, 3, 0, tileWidth, tileHeight, new int[] { 0, 1, 2 });
    }
    





    if (hintsObserved != null) {
      ti.setProperty("HINTS_OBSERVED", hintsObserved);
    }
    
    return ti;
  }
  
















  private void queueOpArg(String name, Class[] argTypes, Object[] args)
  {
    Method method = null;
    try {
      method = GRAPHICS2D_CLASS.getMethod(name, argTypes);
    } catch (Exception e) {
      String message = JaiI18N.getString("TiledGraphicsGraphics2") + name;
      sendExceptionToListener(message, new ImagingException(e));
    }
    



    opArgList.addLast(method);
    opArgList.addLast(args);
  }
  






  private void evaluateOpList(Graphics2D g2d)
  {
    if (opArgList == null) {
      return;
    }
    
    ListIterator li = opArgList.listIterator(0);
    
    while (li.hasNext()) {
      Method method = (Method)li.next();
      Object[] args = (Object[])li.next();
      try
      {
        method.invoke(g2d, args);
      } catch (Exception e) {
        String message = JaiI18N.getString("TiledGraphicsGraphics4") + method;
        sendExceptionToListener(message, new ImagingException(e));
      }
    }
  }
  



  public Graphics create()
  {
    return new RenderableGraphics(dimensions, opArgList, origin, this);
  }
  


  public Color getColor()
  {
    return color;
  }
  
  public void setColor(Color c) {
    color = c;
    
    queueOpArg("setColor", new Class[] { Color.class }, new Object[] { c });
  }
  

  public void setPaintMode()
  {
    queueOpArg("setPaintMode", null, null);
  }
  
  public void setXORMode(Color c1) {
    queueOpArg("setXORMode", new Class[] { Color.class }, new Object[] { c1 });
  }
  

  public Font getFont()
  {
    return font;
  }
  
  public void setFont(Font font) {
    this.font = font;
    
    queueOpArg("setFont", new Class[] { Font.class }, new Object[] { font });
  }
  

  public FontMetrics getFontMetrics(Font f)
  {
    Graphics2D g2d = getBogusGraphics2D();
    
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
    clip = new Rectangle(x, y, width, height);
    
    queueOpArg("setClip", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  


  public Shape getClip()
  {
    return clip;
  }
  
  public void setClip(Shape clip) {
    this.clip = clip;
    
    queueOpArg("setClip", new Class[] { Shape.class }, new Object[] { clip });
  }
  


  public void copyArea(int x, int y, int width, int height, int dx, int dy)
  {
    queueOpArg("copyArea", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(dx), new Integer(dy) });
  }
  




  public void drawLine(int x1, int y1, int x2, int y2)
  {
    queueOpArg("drawLine", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x1), new Integer(y1), new Integer(x2), new Integer(y2) });
  }
  


  public void fillRect(int x, int y, int width, int height)
  {
    queueOpArg("fillRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  





  public void clearRect(int x, int y, int width, int height)
  {
    queueOpArg("clearRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  



  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    queueOpArg("drawRoundRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight) });
  }
  






  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
  {
    queueOpArg("fillRoundRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(arcWidth), new Integer(arcHeight) });
  }
  








  public void draw3DRect(int x, int y, int width, int height, boolean raised)
  {
    queueOpArg("draw3DRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised) });
  }
  







  public void fill3DRect(int x, int y, int width, int height, boolean raised)
  {
    queueOpArg("fill3DRect", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Boolean(raised) });
  }
  




  public void drawOval(int x, int y, int width, int height)
  {
    queueOpArg("drawOval", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  


  public void fillOval(int x, int y, int width, int height)
  {
    queueOpArg("fillOval", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height) });
  }
  



  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    queueOpArg("drawArc", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle) });
  }
  






  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
  {
    queueOpArg("fillArc", new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y), new Integer(width), new Integer(height), new Integer(startAngle), new Integer(arcAngle) });
  }
  





  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    queueOpArg("drawPolyline", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  

  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    queueOpArg("drawPolygon", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  



  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
  {
    Class intArrayClass = xPoints.getClass();
    queueOpArg("fillPolygon", new Class[] { intArrayClass, intArrayClass, Integer.TYPE }, new Object[] { xPoints, yPoints, new Integer(nPoints) });
  }
  



  public void drawString(String str, int x, int y)
  {
    queueOpArg("drawString", new Class[] { String.class, Integer.TYPE, Integer.TYPE }, new Object[] { str, new Integer(x), new Integer(y) });
  }
  





  public boolean drawImage(Image img, int x, int y, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), observer });
    




    return true;
  }
  

  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), observer });
    





    return true;
  }
  

  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), bgcolor, observer });
    





    return true;
  }
  


  public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(x), new Integer(y), new Integer(width), new Integer(height), bgcolor, observer });
    






    return true;
  }
  


  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ImageObserver.class }, new Object[] { img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), observer });
    








    return true;
  }
  



  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
  {
    queueOpArg("drawImage", new Class[] { Image.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Color.class, ImageObserver.class }, new Object[] { img, new Integer(dx1), new Integer(dy1), new Integer(dx2), new Integer(dy2), new Integer(sx1), new Integer(sy1), new Integer(sx2), new Integer(sy2), bgcolor, observer });
    









    return true;
  }
  
  public void dispose() {
    queueOpArg("dispose", null, null);
  }
  




  public void addRenderingHints(Map hints)
  {
    renderingHints.putAll(hints);
    
    queueOpArg("addRenderingHints", new Class[] { Map.class }, new Object[] { hints });
  }
  

  public void draw(Shape s)
  {
    queueOpArg("draw", new Class[] { Shape.class }, new Object[] { s });
  }
  



  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
  {
    queueOpArg("drawImage", new Class[] { Image.class, AffineTransform.class, ImageObserver.class }, new Object[] { img, xform, obs });
    



    return true;
  }
  

  public void drawRenderedImage(RenderedImage img, AffineTransform xform)
  {
    queueOpArg("drawRenderedImage", new Class[] { RenderedImage.class, AffineTransform.class }, new Object[] { img, xform });
  }
  



  public void drawRenderableImage(RenderableImage img, AffineTransform xform)
  {
    queueOpArg("drawRenderableImage", new Class[] { RenderableImage.class, AffineTransform.class }, new Object[] { img, xform });
  }
  





  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
  {
    queueOpArg("drawImage", new Class[] { BufferedImage.class, BufferedImageOp.class, Integer.TYPE, Integer.TYPE }, new Object[] { img, op, new Integer(x), new Integer(y) });
  }
  





  public void drawString(String s, float x, float y)
  {
    queueOpArg("drawString", new Class[] { String.class, Float.TYPE, Float.TYPE }, new Object[] { s, new Float(x), new Float(y) });
  }
  



  public void drawString(AttributedCharacterIterator iterator, int x, int y)
  {
    queueOpArg("drawString", new Class[] { AttributedCharacterIterator.class, Integer.TYPE, Integer.TYPE }, new Object[] { iterator, new Integer(x), new Integer(y) });
  }
  



  public void drawString(AttributedCharacterIterator iterator, float x, float y)
  {
    queueOpArg("drawString", new Class[] { AttributedCharacterIterator.class, Float.TYPE, Float.TYPE }, new Object[] { iterator, new Float(x), new Float(y) });
  }
  




  public void drawGlyphVector(GlyphVector v, float x, float y)
  {
    queueOpArg("drawGlyphVector", new Class[] { GlyphVector.class, Float.TYPE, Float.TYPE }, new Object[] { v, new Float(x), new Float(y) });
  }
  



  public void fill(Shape s)
  {
    queueOpArg("fill", new Class[] { Shape.class }, new Object[] { s });
  }
  



  public boolean hit(Rectangle rect, Shape s, boolean onStroke)
  {
    Graphics2D g2d = getBogusGraphics2D();
    
    boolean hitTarget = g2d.hit(rect, s, onStroke);
    
    g2d.dispose();
    
    return hitTarget;
  }
  
  public GraphicsConfiguration getDeviceConfiguration() {
    Graphics2D g2d = getBogusGraphics2D();
    
    GraphicsConfiguration gConf = g2d.getDeviceConfiguration();
    
    g2d.dispose();
    
    return gConf;
  }
  
  public FontRenderContext getFontRenderContext() {
    Graphics2D g2d = getBogusGraphics2D();
    
    FontRenderContext fontRenderContext = g2d.getFontRenderContext();
    
    g2d.dispose();
    
    return fontRenderContext;
  }
  
  public void setComposite(Composite comp) {
    composite = comp;
    queueOpArg("setComposite", new Class[] { Composite.class }, new Object[] { comp });
  }
  

  public void setPaint(Paint paint)
  {
    this.paint = paint;
    queueOpArg("setPaint", new Class[] { Paint.class }, new Object[] { paint });
  }
  

  public void setStroke(Stroke s)
  {
    stroke = s;
    queueOpArg("setStroke", new Class[] { Stroke.class }, new Object[] { s });
  }
  


  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue)
  {
    renderingHints.put(hintKey, hintValue);
    
    queueOpArg("setRenderingHint", new Class[] { RenderingHints.Key.class, Object.class }, new Object[] { hintKey, hintValue });
  }
  


  public Object getRenderingHint(RenderingHints.Key hintKey)
  {
    return renderingHints.get(hintKey);
  }
  
  public void setRenderingHints(Map hints) {
    renderingHints.putAll(hints);
    
    queueOpArg("setRenderingHints", new Class[] { Map.class }, new Object[] { hints });
  }
  

  public RenderingHints getRenderingHints()
  {
    return renderingHints;
  }
  
  public void translate(int x, int y) {
    origin = new Point(x, y);
    transform.translate(x, y);
    
    queueOpArg("translate", new Class[] { Integer.TYPE, Integer.TYPE }, new Object[] { new Integer(x), new Integer(y) });
  }
  

  public void translate(double x, double y)
  {
    transform.translate(x, y);
    
    queueOpArg("translate", new Class[] { Double.TYPE, Double.TYPE }, new Object[] { new Double(x), new Double(y) });
  }
  

  public void rotate(double theta)
  {
    transform.rotate(theta);
    
    queueOpArg("rotate", new Class[] { Double.TYPE }, new Object[] { new Double(theta) });
  }
  

  public void rotate(double theta, double x, double y)
  {
    transform.rotate(theta, x, y);
    
    queueOpArg("rotate", new Class[] { Double.TYPE, Double.TYPE, Double.TYPE }, new Object[] { new Double(theta), new Double(x), new Double(y) });
  }
  


  public void scale(double sx, double sy)
  {
    transform.scale(sx, sy);
    
    queueOpArg("scale", new Class[] { Double.TYPE, Double.TYPE }, new Object[] { new Double(sx), new Double(sy) });
  }
  

  public void shear(double shx, double shy)
  {
    transform.shear(shx, shy);
    
    queueOpArg("shear", new Class[] { Double.TYPE, Double.TYPE }, new Object[] { new Double(shx), new Double(shy) });
  }
  

  public void transform(AffineTransform Tx)
  {
    transform.concatenate(Tx);
    
    queueOpArg("transform", new Class[] { AffineTransform.class }, new Object[] { Tx });
  }
  

  public void setTransform(AffineTransform Tx)
  {
    transform = Tx;
    
    queueOpArg("setTransform", new Class[] { AffineTransform.class }, new Object[] { Tx });
  }
  

  public AffineTransform getTransform()
  {
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
    
    queueOpArg("setBackground", new Class[] { Color.class }, new Object[] { color });
  }
  

  public Color getBackground()
  {
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
    
    queueOpArg("clip", new Class[] { Shape.class }, new Object[] { s });
  }
  



  public Vector getSources()
  {
    return null;
  }
  
  public Object getProperty(String name) {
    return Image.UndefinedProperty;
  }
  
  public String[] getPropertyNames() {
    return null;
  }
  
  public boolean isDynamic() {
    return false;
  }
  
  public float getWidth() {
    return (float)dimensions.getWidth();
  }
  
  public float getHeight() {
    return (float)dimensions.getHeight();
  }
  
  public float getMinX() {
    return (float)dimensions.getMinX();
  }
  
  public float getMinY() {
    return (float)dimensions.getMinY();
  }
  
  public RenderedImage createScaledRendering(int w, int h, RenderingHints hints)
  {
    if ((w <= 0) && (h <= 0))
      throw new IllegalArgumentException(JaiI18N.getString("RenderableGraphics1"));
    if (w <= 0) {
      w = (int)Math.round(h * dimensions.getWidth() / dimensions.getHeight());
    } else if (h <= 0) {
      h = (int)Math.round(w * dimensions.getHeight() / dimensions.getWidth());
    }
    
    double sx = w / dimensions.getWidth();
    double sy = h / dimensions.getHeight();
    AffineTransform usr2dev = new AffineTransform();
    usr2dev.setToScale(sx, sy);
    
    return createRendering(new RenderContext(usr2dev, hints));
  }
  
  public RenderedImage createDefaultRendering() {
    return createRendering(new RenderContext(new AffineTransform()));
  }
  

































  public RenderedImage createRendering(RenderContext renderContext)
  {
    AffineTransform usr2dev = renderContext.getTransform();
    if (usr2dev == null) {
      usr2dev = new AffineTransform();
    }
    RenderingHints hints = renderContext.getRenderingHints();
    Shape aoi = renderContext.getAreaOfInterest();
    if (aoi == null) {
      aoi = dimensions.getBounds();
    }
    

    Shape transformedAOI = usr2dev.createTransformedShape(aoi);
    

    TiledImage ti = createTiledImage(hints, transformedAOI.getBounds());
    



    Graphics2D g2d = ti.createGraphics();
    




    if (!usr2dev.isIdentity()) {
      AffineTransform tf = getTransform();
      tf.concatenate(usr2dev);
      g2d.setTransform(tf);
    }
    if (hints != null) {
      g2d.addRenderingHints(hints);
    }
    g2d.setClip(aoi);
    

    evaluateOpList(g2d);
    

    g2d.dispose();
    
    return ti;
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
