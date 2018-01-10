package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;





































































































































































public abstract class PlanarImage
  implements ImageJAI, RenderedImage
{
  private Object UID;
  protected int minX;
  protected int minY;
  protected int width;
  protected int height;
  private Rectangle bounds = new Rectangle();
  



  protected int tileGridXOffset;
  



  protected int tileGridYOffset;
  



  protected int tileWidth;
  



  protected int tileHeight;
  



  protected SampleModel sampleModel = null;
  



  protected ColorModel colorModel = null;
  









  protected TileFactory tileFactory = null;
  

  private Vector sources = null;
  

  private Vector sinks = null;
  





  protected PropertyChangeSupportJAI eventManager = null;
  





  protected WritablePropertySourceImpl properties = null;
  




  private SnapshotImage snapshot = null;
  

  private WeakReference weakThis;
  

  private Set tileListeners = null;
  
  private boolean disposed = false;
  



  private static final int MIN_ARRAYCOPY_SIZE = 64;
  




  public PlanarImage()
  {
    weakThis = new WeakReference(this);
    

    eventManager = new PropertyChangeSupportJAI(this);
    

    properties = new WritablePropertySourceImpl(null, null, eventManager);
    

    UID = ImageUtil.generateID(this);
  }
  















































































  public PlanarImage(ImageLayout layout, Vector sources, Map properties)
  {
    this();
    

    if (layout != null) {
      setImageLayout(layout);
    }
    



    if (sources != null) {
      setSources(sources);
    }
    
    if (properties != null)
    {
      this.properties.addProperties(properties);
      

      if (properties.containsKey(JAI.KEY_TILE_FACTORY)) {
        Object factoryValue = properties.get(JAI.KEY_TILE_FACTORY);
        


        if ((factoryValue instanceof TileFactory)) {
          tileFactory = ((TileFactory)factoryValue);
        }
      }
    }
  }
  






















































  protected void setImageLayout(ImageLayout layout)
  {
    if (layout == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (layout.isValid(1)) {
      minX = layout.getMinX(null);
    }
    if (layout.isValid(2)) {
      minY = layout.getMinY(null);
    }
    if (layout.isValid(4)) {
      width = layout.getWidth(null);
    }
    if (layout.isValid(8)) {
      height = layout.getHeight(null);
    }
    

    if (layout.isValid(16)) {
      tileGridXOffset = layout.getTileGridXOffset(null);
    }
    if (layout.isValid(32)) {
      tileGridYOffset = layout.getTileGridYOffset(null);
    }
    if (layout.isValid(64)) {
      tileWidth = layout.getTileWidth(null);
    } else {
      tileWidth = width;
    }
    if (layout.isValid(128)) {
      tileHeight = layout.getTileHeight(null);
    } else {
      tileHeight = height;
    }
    

    if (layout.isValid(256)) {
      sampleModel = layout.getSampleModel(null);
    }
    

    if ((sampleModel != null) && (tileWidth > 0) && (tileHeight > 0) && ((sampleModel.getWidth() != tileWidth) || (sampleModel.getHeight() != tileHeight)))
    {

      sampleModel = sampleModel.createCompatibleSampleModel(tileWidth, tileHeight);
    }
    



    if (layout.isValid(512)) {
      colorModel = layout.getColorModel(null);
    }
    if ((colorModel != null) && (sampleModel != null) && 
      (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
    {
      throw new IllegalArgumentException(JaiI18N.getString("PlanarImage5"));
    }
  }
  


































  public static PlanarImage wrapRenderedImage(RenderedImage image)
  {
    if (image == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((image instanceof PlanarImage))
      return (PlanarImage)image;
    if ((image instanceof WritableRenderedImage)) {
      return new WritableRenderedImageAdapter((WritableRenderedImage)image);
    }
    
    return new RenderedImageAdapter(image);
  }
  










  public PlanarImage createSnapshot()
  {
    if ((this instanceof WritableRenderedImage)) {
      if (snapshot == null) {
        synchronized (this) {
          snapshot = new SnapshotImage(this);
        }
      }
      return snapshot.createSnapshot();
    }
    
    return this;
  }
  




  public int getMinX()
  {
    return minX;
  }
  







  public int getMaxX()
  {
    return getMinX() + getWidth();
  }
  



  public int getMinY()
  {
    return minY;
  }
  







  public int getMaxY()
  {
    return getMinY() + getHeight();
  }
  



  public int getWidth()
  {
    return width;
  }
  



  public int getHeight()
  {
    return height;
  }
  







  public int getNumBands()
  {
    return getSampleModel().getNumBands();
  }
  


















  public Rectangle getBounds()
  {
    synchronized (bounds) {
      bounds.setBounds(getMinX(), getMinY(), getWidth(), getHeight());
    }
    
    return bounds;
  }
  



  public int getTileGridXOffset()
  {
    return tileGridXOffset;
  }
  



  public int getTileGridYOffset()
  {
    return tileGridYOffset;
  }
  



  public int getTileWidth()
  {
    return tileWidth;
  }
  



  public int getTileHeight()
  {
    return tileHeight;
  }
  






  public int getMinTileX()
  {
    return XToTileX(getMinX(), getTileGridXOffset(), getTileWidth());
  }
  






  public int getMaxTileX()
  {
    return XToTileX(getMinX() + getWidth() - 1, getTileGridXOffset(), getTileWidth());
  }
  








  public int getNumXTiles()
  {
    int x = getMinX();
    int tx = getTileGridXOffset();
    int tw = getTileWidth();
    return XToTileX(x + getWidth() - 1, tx, tw) - XToTileX(x, tx, tw) + 1;
  }
  






  public int getMinTileY()
  {
    return YToTileY(getMinY(), getTileGridYOffset(), getTileHeight());
  }
  






  public int getMaxTileY()
  {
    return YToTileY(getMinY() + getHeight() - 1, getTileGridYOffset(), getTileHeight());
  }
  








  public int getNumYTiles()
  {
    int y = getMinY();
    int ty = getTileGridYOffset();
    int th = getTileHeight();
    return YToTileY(y + getHeight() - 1, ty, th) - YToTileY(y, ty, th) + 1;
  }
  










  public static int XToTileX(int x, int tileGridXOffset, int tileWidth)
  {
    x -= tileGridXOffset;
    if (x < 0) {
      x += 1 - tileWidth;
    }
    return x / tileWidth;
  }
  










  public static int YToTileY(int y, int tileGridYOffset, int tileHeight)
  {
    y -= tileGridYOffset;
    if (y < 0) {
      y += 1 - tileHeight;
    }
    return y / tileHeight;
  }
  













  public int XToTileX(int x)
  {
    return XToTileX(x, getTileGridXOffset(), getTileWidth());
  }
  













  public int YToTileY(int y)
  {
    return YToTileY(y, getTileGridYOffset(), getTileHeight());
  }
  




  public static int tileXToX(int tx, int tileGridXOffset, int tileWidth)
  {
    return tx * tileWidth + tileGridXOffset;
  }
  




  public static int tileYToY(int ty, int tileGridYOffset, int tileHeight)
  {
    return ty * tileHeight + tileGridYOffset;
  }
  











  public int tileXToX(int tx)
  {
    return tileXToX(tx, getTileGridXOffset(), getTileWidth());
  }
  











  public int tileYToY(int ty)
  {
    return tileYToY(ty, getTileGridYOffset(), getTileHeight());
  }
  
















  public Rectangle getTileRect(int tileX, int tileY)
  {
    return getBounds().intersection(new Rectangle(tileXToX(tileX), tileYToY(tileY), getTileWidth(), getTileHeight()));
  }
  





  public SampleModel getSampleModel()
  {
    return sampleModel;
  }
  



  public ColorModel getColorModel()
  {
    return colorModel;
  }
  




























  public static ColorModel getDefaultColorModel(int dataType, int numBands)
  {
    if ((dataType < 0) || (dataType == 2) || (dataType > 5) || (numBands < 1) || (numBands > 4))
    {


      return null;
    }
    
    ColorSpace cs = numBands <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
    


    boolean useAlpha = (numBands == 2) || (numBands == 4);
    int transparency = useAlpha ? 3 : 1;
    

    return RasterFactory.createComponentColorModel(dataType, cs, useAlpha, false, transparency);
  }
  








































  public static ColorModel createColorModel(SampleModel sm)
  {
    if (sm == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int bands = sm.getNumBands();
    if ((bands < 1) || (bands > 4)) {
      return null;
    }
    
    if ((sm instanceof ComponentSampleModel)) {
      return getDefaultColorModel(sm.getDataType(), bands);
    }
    if ((sm instanceof SinglePixelPackedSampleModel)) {
      SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
      

      int[] bitMasks = sppsm.getBitMasks();
      int rmask = 0;
      int gmask = 0;
      int bmask = 0;
      int amask = 0;
      
      int numBands = bitMasks.length;
      if (numBands <= 2) {
        rmask = gmask = bmask = bitMasks[0];
        if (numBands == 2) {
          amask = bitMasks[1];
        }
      } else {
        rmask = bitMasks[0];
        gmask = bitMasks[1];
        bmask = bitMasks[2];
        if (numBands == 4) {
          amask = bitMasks[3];
        }
      }
      
      int[] sampleSize = sppsm.getSampleSize();
      int bits = 0;
      for (int i = 0; i < sampleSize.length; i++) {
        bits += sampleSize[i];
      }
      
      return new DirectColorModel(bits, rmask, gmask, bmask, amask);
    }
    if (ImageUtil.isBinary(sm)) {
      byte[] comp = { 0, -1 };
      
      return new IndexColorModel(1, 2, comp, comp, comp);
    }
    
    return null;
  }
  





  public TileFactory getTileFactory()
  {
    return tileFactory;
  }
  




  public int getNumSources()
  {
    return sources == null ? 0 : sources.size();
  }
  



  public Vector getSources()
  {
    if (getNumSources() == 0) {
      return null;
    }
    synchronized (sources) {
      return (Vector)sources.clone();
    }
  }
  













  /**
   * @deprecated
   */
  public PlanarImage getSource(int index)
  {
    if (sources == null) {
      throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
    }
    

    synchronized (sources) {
      return (PlanarImage)sources.get(index);
    }
  }
  















  protected void setSources(List sourceList)
  {
    if (sourceList == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int size = sourceList.size();
    
    synchronized (this) {
      if (sources != null)
      {
        Iterator it = sources.iterator();
        while (it.hasNext()) {
          Object src = it.next();
          if ((src instanceof PlanarImage)) {
            ((PlanarImage)src).removeSink(this);
          }
        }
      }
      sources = new Vector(size);
    }
    
    synchronized (sources) {
      for (int i = 0; i < size; i++) {
        Object sourceElement = sourceList.get(i);
        if (sourceElement == null) {
          throw new IllegalArgumentException(JaiI18N.getString("PlanarImage7"));
        }
        
        sources.add((sourceElement instanceof RenderedImage) ? wrapRenderedImage((RenderedImage)sourceElement) : sourceElement);
        



        if ((sourceElement instanceof PlanarImage)) {
          ((PlanarImage)sourceElement).addSink(this);
        }
      }
    }
  }
  



  protected void removeSources()
  {
    if (sources != null) {
      synchronized (this) {
        if (sources != null)
        {
          Iterator it = sources.iterator();
          while (it.hasNext()) {
            Object src = it.next();
            if ((src instanceof PlanarImage)) {
              ((PlanarImage)src).removeSink(this);
            }
          }
        }
        sources = null;
      }
    }
  }
  














  public PlanarImage getSourceImage(int index)
  {
    if (sources == null) {
      throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
    }
    

    synchronized (sources) {
      return (PlanarImage)sources.get(index);
    }
  }
  














  public Object getSourceObject(int index)
  {
    if (sources == null) {
      throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
    }
    

    synchronized (sources) {
      return sources.get(index);
    }
  }
  













  protected void addSource(Object source)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sources == null) {
      synchronized (this) {
        sources = new Vector();
      }
    }
    
    synchronized (sources)
    {
      sources.add((source instanceof RenderedImage) ? wrapRenderedImage((RenderedImage)source) : source);
    }
    


    if ((source instanceof PlanarImage)) {
      ((PlanarImage)source).addSink(this);
    }
  }
  




















  protected void setSource(Object source, int index)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sources == null) {
      throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
    }
    

    synchronized (sources) {
      if ((index < sources.size()) && ((sources.get(index) instanceof PlanarImage)))
      {
        getSourceImage(index).removeSink(this);
      }
      sources.set(index, (source instanceof RenderedImage) ? wrapRenderedImage((RenderedImage)source) : source);
    }
    

    if ((source instanceof PlanarImage)) {
      ((PlanarImage)source).addSink(this);
    }
  }
  














  protected boolean removeSource(Object source)
  {
    if (source == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sources == null) {
      return false;
    }
    
    synchronized (sources) {
      if ((source instanceof PlanarImage)) {
        ((PlanarImage)source).removeSink(this);
      }
      return sources.remove(source);
    }
  }
  




















  public Vector getSinks()
  {
    Vector v = null;
    
    if (sinks != null) {
      synchronized (sinks) {
        int size = sinks.size();
        v = new Vector(size);
        for (int i = 0; i < size; i++) {
          Object o = ((WeakReference)sinks.get(i)).get();
          
          if (o != null) {
            v.add(o);
          }
        }
      }
      
      if (v.size() == 0) {
        v = null;
      }
    }
    return v;
  }
  










  public synchronized boolean addSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      sinks = new Vector();
    }
    
    boolean result = false;
    if ((sink instanceof PlanarImage)) {
      result = sinks.add(weakThis);
    } else {
      result = sinks.add(new WeakReference(sink));
    }
    
    return result;
  }
  










  public synchronized boolean removeSink(Object sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      return false;
    }
    
    boolean result = false;
    if ((sink instanceof PlanarImage)) {
      result = sinks.remove(weakThis);
    } else {
      Iterator it = sinks.iterator();
      while (it.hasNext()) {
        Object referent = ((WeakReference)it.next()).get();
        if (referent == sink)
        {
          it.remove();
          result = true;
        }
        else if (referent == null)
        {
          it.remove();
        }
      }
    }
    
    return result;
  }
  






  /**
   * @deprecated
   */
  protected void addSink(PlanarImage sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      synchronized (this) {
        sinks = new Vector();
      }
    }
    
    synchronized (sinks) {
      sinks.add(weakThis);
    }
  }
  











  /**
   * @deprecated
   */
  protected boolean removeSink(PlanarImage sink)
  {
    if (sink == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sinks == null) {
      return false;
    }
    
    synchronized (sinks) {
      return sinks.remove(weakThis);
    }
  }
  
  public void removeSinks()
  {
    if (sinks != null) {
      synchronized (this) {
        sinks = null;
      }
    }
  }
  



  protected Hashtable getProperties()
  {
    return (Hashtable)properties.getProperties();
  }
  





  protected void setProperties(Hashtable properties)
  {
    this.properties.addProperties(properties);
  }
  












  public Object getProperty(String name)
  {
    return properties.getProperty(name);
  }
  












  public Class getPropertyClass(String name)
  {
    return properties.getPropertyClass(name);
  }
  










  public void setProperty(String name, Object value)
  {
    properties.setProperty(name, value);
  }
  









  public void removeProperty(String name)
  {
    properties.removeProperty(name);
  }
  






  public String[] getPropertyNames()
  {
    return properties.getPropertyNames();
  }
  















  public String[] getPropertyNames(String prefix)
  {
    return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
  }
  





  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    eventManager.addPropertyChangeListener(listener);
  }
  








  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    eventManager.addPropertyChangeListener(propertyName.toLowerCase(), listener);
  }
  







  public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    eventManager.removePropertyChangeListener(listener);
  }
  






  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
  {
    eventManager.removePropertyChangeListener(propertyName.toLowerCase(), listener);
  }
  
  private synchronized Set getTileComputationListeners(boolean createIfNull)
  {
    if ((createIfNull) && (tileListeners == null)) {
      tileListeners = Collections.synchronizedSet(new HashSet());
    }
    return tileListeners;
  }
  












  public synchronized void addTileComputationListener(TileComputationListener listener)
  {
    if (listener == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Set listeners = getTileComputationListeners(true);
    
    listeners.add(listener);
  }
  










  public synchronized void removeTileComputationListener(TileComputationListener listener)
  {
    if (listener == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Set listeners = getTileComputationListeners(false);
    
    if (listeners != null) {
      listeners.remove(listener);
    }
  }
  










  public TileComputationListener[] getTileComputationListeners()
  {
    Set listeners = getTileComputationListeners(false);
    
    if (listeners == null) {
      return null;
    }
    
    return (TileComputationListener[])listeners.toArray(new TileComputationListener[listeners.size()]);
  }
  















  public void getSplits(IntegerSequence xSplits, IntegerSequence ySplits, Rectangle rect)
  {
    if ((xSplits == null) || (ySplits == null) || (rect == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int minTileX = XToTileX(x);
    int maxTileX = XToTileX(x + width - 1);
    int xTilePos = tileXToX(minTileX);
    for (int i = minTileX; i <= maxTileX; i++) {
      xSplits.insert(xTilePos);
      xTilePos += tileWidth;
    }
    
    int minTileY = YToTileY(y);
    int maxTileY = YToTileY(y + height - 1);
    int yTilePos = tileYToY(minTileY);
    for (int i = minTileY; i <= maxTileY; i++) {
      ySplits.insert(yTilePos);
      yTilePos += tileHeight;
    }
  }
  















  public Point[] getTileIndices(Rectangle region)
  {
    if (region == null) {
      region = (Rectangle)getBounds().clone();
    } else { if (!region.intersects(getBounds())) {
        return null;
      }
      region = region.intersection(getBounds());
      if (region.isEmpty()) {
        return null;
      }
    }
    
    if (region == null) {
      region = getBounds();
    } else {
      Rectangle r = new Rectangle(getMinX(), getMinY(), getWidth() + 1, getHeight() + 1);
      
      if (!region.intersects(r)) {
        return null;
      }
      region = region.intersection(r);
    }
    

    int minTileX = XToTileX(x);
    int maxTileX = XToTileX(x + width - 1);
    int minTileY = YToTileY(y);
    int maxTileY = YToTileY(y + height - 1);
    
    Point[] tileIndices = new Point[(maxTileY - minTileY + 1) * (maxTileX - minTileX + 1)];
    

    int tileIndexOffset = 0;
    for (int ty = minTileY; ty <= maxTileY; ty++) {
      for (int tx = minTileX; tx <= maxTileX; tx++) {
        tileIndices[(tileIndexOffset++)] = new Point(tx, ty);
      }
    }
    
    return tileIndices;
  }
  







  public boolean overlapsMultipleTiles(Rectangle rect)
  {
    if (rect == null) {
      throw new IllegalArgumentException("rect == null!");
    }
    
    Rectangle xsect = rect.intersection(getBounds());
    


    return (!xsect.isEmpty()) && ((XToTileX(x) != XToTileX(x + width - 1)) || (YToTileY(y) != YToTileY(y + height - 1)));
  }
  




















  protected final WritableRaster createWritableRaster(SampleModel sampleModel, Point location)
  {
    if (sampleModel == null) {
      throw new IllegalArgumentException("sampleModel == null!");
    }
    
    return tileFactory != null ? tileFactory.createTile(sampleModel, location) : RasterFactory.createWritableRaster(sampleModel, location);
  }
  





























  public Raster getData()
  {
    return getData(null);
  }
  











































  public Raster getData(Rectangle region)
  {
    Rectangle b = getBounds();
    
    if (region == null) {
      region = b;
    } else if (!region.intersects(b)) {
      throw new IllegalArgumentException(JaiI18N.getString("PlanarImage4"));
    }
    


    Rectangle xsect = region == b ? region : region.intersection(b);
    

    int startTileX = XToTileX(x);
    int startTileY = YToTileY(y);
    int endTileX = XToTileX(x + width - 1);
    int endTileY = YToTileY(y + height - 1);
    
    if ((startTileX == endTileX) && (startTileY == endTileY) && (getTileRect(startTileX, startTileY).contains(region)))
    {

      Raster tile = getTile(startTileX, startTileY);
      
      if ((this instanceof WritableRenderedImage))
      {


        SampleModel sm = tile.getSampleModel();
        if ((sm.getWidth() != width) || (sm.getHeight() != height))
        {
          sm = sm.createCompatibleSampleModel(width, height);
        }
        
        WritableRaster destinationRaster = createWritableRaster(sm, region.getLocation());
        
        Raster sourceRaster = tile.getBounds().equals(region) ? tile : tile.createChild(x, y, width, height, x, y, null);
        




        JDKWorkarounds.setRect(destinationRaster, sourceRaster);
        return destinationRaster;
      }
      

      return tile.getBounds().equals(region) ? tile : tile.createChild(x, y, width, height, x, y, null);
    }
    






    SampleModel srcSM = getSampleModel();
    int dataType = srcSM.getDataType();
    int nbands = srcSM.getNumBands();
    boolean isBandChild = false;
    
    ComponentSampleModel csm = null;
    int[] bandOffs = null;
    
    boolean fastCobblePossible = false;
    if ((srcSM instanceof ComponentSampleModel)) {
      csm = (ComponentSampleModel)srcSM;
      int ps = csm.getPixelStride();
      boolean isBandInt = (ps == 1) && (nbands > 1);
      isBandChild = (ps > 1) && (nbands != ps);
      if ((!isBandChild) && (!isBandInt)) {
        bandOffs = csm.getBandOffsets();
        
        for (int i = 0; i < nbands; i++) {
          if (bandOffs[i] >= nbands) {
            break;
          }
        }
        if (i == nbands) {
          fastCobblePossible = true;
        }
      }
    }
    WritableRaster dstRaster;
    if (fastCobblePossible)
    {

      try
      {
        SampleModel interleavedSM = RasterFactory.createPixelInterleavedSampleModel(dataType, width, height, nbands, width * nbands, bandOffs);
        






        dstRaster = createWritableRaster(interleavedSM, region.getLocation());
      } catch (IllegalArgumentException e) {
        WritableRaster dstRaster;
        throw new IllegalArgumentException(JaiI18N.getString("PlanarImage2"));
      }
      
      WritableRaster dstRaster;
      switch (dataType) {
      case 0: 
        cobbleByte(region, dstRaster);
        break;
      case 2: 
        cobbleShort(region, dstRaster);
        break;
      case 1: 
        cobbleUShort(region, dstRaster);
        break;
      case 3: 
        cobbleInt(region, dstRaster);
        break;
      case 4: 
        cobbleFloat(region, dstRaster);
        break;
      case 5: 
        cobbleDouble(region, dstRaster);
        break;
      }
    }
    else
    {
      SampleModel sm = sampleModel;
      if ((sm.getWidth() != width) || (sm.getHeight() != height))
      {
        sm = sm.createCompatibleSampleModel(width, height);
      }
      
      try
      {
        dstRaster = createWritableRaster(sm, region.getLocation());
      } catch (IllegalArgumentException e) {
        WritableRaster dstRaster;
        throw new IllegalArgumentException(JaiI18N.getString("PlanarImage2"));
      }
      

      for (int j = startTileY; j <= endTileY; j++) {
        for (int i = startTileX; i <= endTileX; i++) {
          Raster tile = getTile(i, j);
          
          Rectangle subRegion = region.intersection(tile.getBounds());
          
          Raster subRaster = tile.createChild(x, y, width, height, x, y, null);
          







          if (((sm instanceof ComponentSampleModel)) && (isBandChild)) {}
          


          switch (sm.getDataType()) {
          case 4: 
            dstRaster.setPixels(x, y, width, height, subRaster.getPixels(x, y, width, height, new float[nbands * width * height]));
            









            break;
          case 5: 
            dstRaster.setPixels(x, y, width, height, subRaster.getPixels(x, y, width, height, new double[nbands * width * height]));
            









            break;
          default: 
            dstRaster.setPixels(x, y, width, height, subRaster.getPixels(x, y, width, height, new int[nbands * width * height]));
            









            continue;
            

            JDKWorkarounds.setRect(dstRaster, subRaster);
          }
          
        }
      }
    }
    
    return dstRaster;
  }
  

  public WritableRaster copyData()
  {
    return copyData(null);
  }
  













  public WritableRaster copyData(WritableRaster raster)
  {
    Rectangle region;
    











    if (raster == null) {
      Rectangle region = getBounds();
      
      SampleModel sm = getSampleModel();
      if ((sm.getWidth() != width) || (sm.getHeight() != height))
      {
        sm = sm.createCompatibleSampleModel(width, height);
      }
      
      raster = createWritableRaster(sm, region.getLocation());
    } else {
      region = raster.getBounds().intersection(getBounds());
      
      if (region.isEmpty()) {
        return raster;
      }
    }
    
    int startTileX = XToTileX(x);
    int startTileY = YToTileY(y);
    int endTileX = XToTileX(x + width - 1);
    int endTileY = YToTileY(y + height - 1);
    
    SampleModel[] sampleModels = { getSampleModel() };
    int tagID = RasterAccessor.findCompatibleTag(sampleModels, raster.getSampleModel());
    

    RasterFormatTag srcTag = new RasterFormatTag(getSampleModel(), tagID);
    RasterFormatTag dstTag = new RasterFormatTag(raster.getSampleModel(), tagID);
    

    for (int ty = startTileY; ty <= endTileY; ty++) {
      for (int tx = startTileX; tx <= endTileX; tx++) {
        Raster tile = getTile(tx, ty);
        Rectangle subRegion = region.intersection(tile.getBounds());
        
        RasterAccessor s = new RasterAccessor(tile, subRegion, srcTag, getColorModel());
        
        RasterAccessor d = new RasterAccessor(raster, subRegion, dstTag, null);
        
        ImageUtil.copyRaster(s, d);
      }
    }
    return raster;
  }
  


















  public void copyExtendedData(WritableRaster dest, BorderExtender extender)
  {
    if ((dest == null) || (extender == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    Rectangle destBounds = dest.getBounds();
    Rectangle imageBounds = getBounds();
    if (imageBounds.contains(destBounds)) {
      copyData(dest);
      return;
    }
    

    Rectangle isect = imageBounds.intersection(destBounds);
    
    if (!isect.isEmpty())
    {
      WritableRaster isectRaster = dest.createWritableChild(x, y, width, height, x, y, null);
      



      copyData(isectRaster);
    }
    

    extender.extend(dest, this);
  }
  




























  public Raster getExtendedData(Rectangle region, BorderExtender extender)
  {
    if (region == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (getBounds().contains(region)) {
      return getData(region);
    }
    
    if (extender == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    SampleModel destSM = getSampleModel();
    if ((destSM.getWidth() != width) || (destSM.getHeight() != height))
    {
      destSM = destSM.createCompatibleSampleModel(width, height);
    }
    


    WritableRaster dest = createWritableRaster(destSM, region.getLocation());
    

    copyExtendedData(dest, extender);
    return dest;
  }
  
































  public BufferedImage getAsBufferedImage(Rectangle rect, ColorModel cm)
  {
    if (cm == null) {
      cm = getColorModel();
      if (cm == null) {
        throw new IllegalArgumentException(JaiI18N.getString("PlanarImage6"));
      }
    }
    

    if (!JDKWorkarounds.areCompatibleDataModels(sampleModel, cm)) {
      throw new IllegalArgumentException(JaiI18N.getString("PlanarImage3"));
    }
    

    if (rect == null) {
      rect = getBounds();
    } else {
      rect = getBounds().intersection(rect);
    }
    

    SampleModel sm = (sampleModel.getWidth() != width) || (sampleModel.getHeight() != height) ? sampleModel.createCompatibleSampleModel(width, height) : sampleModel;
    





    WritableRaster ras = createWritableRaster(sm, rect.getLocation());
    copyData(ras);
    
    if ((x != 0) || (y != 0))
    {
      ras = RasterFactory.createWritableChild(ras, x, y, width, height, 0, 0, null);
    }
    


    return new BufferedImage(cm, ras, cm.isAlphaPremultiplied(), null);
  }
  







  public BufferedImage getAsBufferedImage()
  {
    return getAsBufferedImage(null, null);
  }
  







  public Graphics getGraphics()
  {
    throw new IllegalAccessError(JaiI18N.getString("PlanarImage1"));
  }
  















  public abstract Raster getTile(int paramInt1, int paramInt2);
  














  public Raster[] getTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int size = tileIndices.length;
    Raster[] tiles = new Raster[size];
    
    for (int i = 0; i < tileIndices.length; i++) {
      Point p = tileIndices[i];
      tiles[i] = getTile(x, y);
    }
    
    return tiles;
  }
  






  public Raster[] getTiles()
  {
    return getTiles(getTileIndices(getBounds()));
  }
  

















  public TileRequest queueTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    TileComputationListener[] listeners = getTileComputationListeners();
    return JAI.getDefaultInstance().getTileScheduler().scheduleTiles(this, tileIndices, listeners);
  }
  


















  public void cancelTiles(TileRequest request, Point[] tileIndices)
  {
    if (request == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    
    JAI.getDefaultInstance().getTileScheduler().cancelTiles(request, tileIndices);
  }
  
















  public void prefetchTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    JAI.getDefaultInstance().getTileScheduler().prefetchTiles(this, tileIndices);
  }
  


















  public synchronized void dispose()
  {
    if (disposed) {
      return;
    }
    disposed = true;
    



    Vector srcs = getSources();
    if (srcs != null) {
      int numSources = srcs.size();
      for (int i = 0; i < numSources; i++) {
        Object src = srcs.get(i);
        if ((src instanceof PlanarImage)) {
          ((PlanarImage)src).removeSink(this);
        }
      }
    }
  }
  







  protected void finalize()
    throws Throwable
  {
    dispose();
  }
  
  private void printBounds()
  {
    System.out.println("Bounds: [x=" + getMinX() + ", y=" + getMinY() + ", width=" + getWidth() + ", height=" + getHeight() + "]");
  }
  




  private void printTile(int i, int j)
  {
    int xmin = i * getTileWidth() + getTileGridXOffset();
    int ymin = j * getTileHeight() + getTileGridYOffset();
    
    Rectangle imageBounds = getBounds();
    Rectangle tileBounds = new Rectangle(xmin, ymin, getTileWidth(), getTileHeight());
    

    tileBounds = tileBounds.intersection(imageBounds);
    
    Raster tile = getTile(i, j);
    
    Rectangle realTileBounds = new Rectangle(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight());
    


    System.out.println("Tile bounds (actual)   = " + realTileBounds);
    System.out.println("Tile bounds (computed) = " + tileBounds);
    
    xmin = x;
    ymin = y;
    int xmax = x + width - 1;
    int ymax = y + height - 1;
    int numBands = getSampleModel().getNumBands();
    int[] val = new int[numBands];
    

    for (int pj = ymin; pj <= ymax; pj++) {
      for (int pi = xmin; pi <= xmax; pi++) {
        tile.getPixel(pi, pj, val);
        if (numBands == 1) {
          System.out.print("(" + val[0] + ") ");
        } else if (numBands == 3) {
          System.out.print("(" + val[0] + "," + val[1] + "," + val[2] + ") ");
        }
      }
      
      System.out.println();
    }
  }
  





  public String toString()
  {
    return "PlanarImage[minX=" + minX + " minY=" + minY + " width=" + width + " height=" + height + " tileGridXOffset=" + tileGridXOffset + " tileGridYOffset=" + tileGridYOffset + " tileWidth=" + tileWidth + " tileHeight=" + tileHeight + " sampleModel=" + sampleModel + " colorModel=" + colorModel + "]";
  }
  









  private void cobbleByte(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBufferByte dstDB = (DataBufferByte)dstRaster.getDataBuffer();
    byte[] dst = dstDB.getData();
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBufferByte srcDB = (DataBufferByte)tile.getDataBuffer();
          byte[] src = srcDB.getData();
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  
  private void cobbleShort(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBufferShort dstDB = (DataBufferShort)dstRaster.getDataBuffer();
    short[] dst = dstDB.getData();
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBufferShort srcDB = (DataBufferShort)tile.getDataBuffer();
          short[] src = srcDB.getData();
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  
  private void cobbleUShort(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBufferUShort dstDB = (DataBufferUShort)dstRaster.getDataBuffer();
    short[] dst = dstDB.getData();
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBufferUShort srcDB = (DataBufferUShort)tile.getDataBuffer();
          short[] src = srcDB.getData();
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  
  private void cobbleInt(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBufferInt dstDB = (DataBufferInt)dstRaster.getDataBuffer();
    int[] dst = dstDB.getData();
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBufferInt srcDB = (DataBufferInt)tile.getDataBuffer();
          int[] src = srcDB.getData();
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  
  private void cobbleFloat(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBuffer dstDB = dstRaster.getDataBuffer();
    float[] dst = DataBufferUtils.getDataFloat(dstDB);
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBuffer srcDB = tile.getDataBuffer();
          float[] src = DataBufferUtils.getDataFloat(srcDB);
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  
  private void cobbleDouble(Rectangle bounds, Raster dstRaster)
  {
    ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
    

    int startX = XToTileX(x);
    int startY = YToTileY(y);
    int rectXend = x + width - 1;
    int rectYend = y + height - 1;
    int endX = XToTileX(rectXend);
    int endY = YToTileY(rectYend);
    



    DataBuffer dstDB = dstRaster.getDataBuffer();
    double[] dst = DataBufferUtils.getDataDouble(dstDB);
    int dstPS = dstSM.getPixelStride();
    int dstSS = dstSM.getScanlineStride();
    
    boolean tileParamsSet = false;
    ComponentSampleModel srcSM = null;
    int srcPS = 0;int srcSS = 0;
    


    for (int y = startY; y <= endY; y++) {
      for (int x = startX; x <= endX; x++) {
        Raster tile = getTile(x, y);
        if (tile != null)
        {






          if (!tileParamsSet)
          {



            srcSM = (ComponentSampleModel)tile.getSampleModel();
            srcPS = srcSM.getPixelStride();
            srcSS = srcSM.getScanlineStride();
            tileParamsSet = true;
          }
          




          int yOrg = y * tileHeight + tileGridYOffset;
          int srcY1 = yOrg;
          int srcY2 = srcY1 + tileHeight - 1;
          if (y > srcY1) srcY1 = y;
          if (rectYend < srcY2) srcY2 = rectYend;
          int srcH = srcY2 - srcY1 + 1;
          
          int xOrg = x * tileWidth + tileGridXOffset;
          int srcX1 = xOrg;
          int srcX2 = srcX1 + tileWidth - 1;
          if (x > srcX1) srcX1 = x;
          if (rectXend < srcX2) srcX2 = rectXend;
          int srcW = srcX2 - srcX1 + 1;
          
          int dstX = srcX1 - x;
          int dstY = srcY1 - y;
          

          DataBuffer srcDB = tile.getDataBuffer();
          double[] src = DataBufferUtils.getDataDouble(srcDB);
          
          int nsamps = srcW * srcPS;
          boolean useArrayCopy = nsamps >= 64;
          
          int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
          int yDstIdx = dstY * dstSS + dstX * dstPS;
          if (useArrayCopy) {
            for (int row = 0; row < srcH; row++) {
              System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          } else {
            for (int row = 0; row < srcH; row++) {
              int xSrcIdx = ySrcIdx;
              int xDstIdx = yDstIdx;
              int xEnd = xDstIdx + nsamps;
              while (xDstIdx < xEnd) {
                dst[(xDstIdx++)] = src[(xSrcIdx++)];
              }
              ySrcIdx += srcSS;
              yDstIdx += dstSS;
            }
          }
        }
      }
    }
  }
  





  public Object getImageID()
  {
    return UID;
  }
}
