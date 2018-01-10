package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;















































































































































































































public abstract class OpImage
  extends PlanarImage
{
  public static final int OP_COMPUTE_BOUND = 1;
  public static final int OP_IO_BOUND = 2;
  public static final int OP_NETWORK_BOUND = 3;
  private static final int LAYOUT_MASK_ALL = 1023;
  protected transient TileCache cache;
  protected Object tileCacheMetric;
  private transient TileScheduler scheduler = JAI.getDefaultInstance().getTileScheduler();
  




  private boolean isSunTileScheduler = false;
  





  protected boolean cobbleSources;
  




  private boolean isDisposed = false;
  




  private boolean isCachedTileRecyclingEnabled = false;
  





  protected TileRecycler tileRecycler;
  




  private RasterFormatTag[] formatTags = null;
  






































  private static ImageLayout layoutHelper(ImageLayout layout, Vector sources, Map config)
  {
    ImageLayout il = layout;
    

    if (sources != null) {
      checkSourceVector(sources, true);
    }
    


    RenderedImage im = (sources != null) && (sources.size() > 0) && ((sources.firstElement() instanceof RenderedImage)) ? (RenderedImage)sources.firstElement() : null;
    




    if (im != null)
    {

      if (layout == null)
      {
        il = layout = new ImageLayout(im);
        

        il.unsetValid(512);
      }
      else {
        il = new ImageLayout(layout.getMinX(im), layout.getMinY(im), layout.getWidth(im), layout.getHeight(im), layout.getTileGridXOffset(im), layout.getTileGridYOffset(im), layout.getTileWidth(im), layout.getTileHeight(im), layout.getSampleModel(im), null);
      }
      














      if ((layout.isValid(512)) && (layout.getColorModel(null) == null))
      {

        il.setColorModel(null);
      }
      else if (il.getSampleModel(null) != null)
      {



        SampleModel sm = il.getSampleModel(null);
        

        ColorModel cmLayout = layout.getColorModel(null);
        


        if (cmLayout != null)
        {
          if (JDKWorkarounds.areCompatibleDataModels(sm, cmLayout))
          {
            il.setColorModel(cmLayout);
          }
          else if (layout.getSampleModel(null) == null)
          {




            il.setColorModel(cmLayout);
            

            SampleModel derivedSM = cmLayout.createCompatibleSampleModel(il.getTileWidth(null), il.getTileHeight(null));
            




            il.setSampleModel(derivedSM);
          }
        }
        




        if ((!il.isValid(512)) && (!setColorModelFromFactory(sm, sources, config, il)))
        {

          ColorModel cmSource = im.getColorModel();
          if ((cmSource != null) && (JDKWorkarounds.areCompatibleDataModels(sm, cmSource)))
          {

            if ((cmSource != null) && ((cmSource instanceof IndexColorModel)) && (config != null) && (config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) && (((Boolean)config.get(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)).booleanValue()))
            {





              ColorModel newCM = PlanarImage.getDefaultColorModel(sm.getDataType(), cmSource.getNumComponents());
              
              SampleModel newSM;
              
              SampleModel newSM;
              
              if (newCM != null) {
                newSM = newCM.createCompatibleSampleModel(il.getTileWidth(null), il.getTileHeight(null));

              }
              else
              {
                newSM = RasterFactory.createPixelInterleavedSampleModel(sm.getDataType(), il.getTileWidth(null), il.getTileHeight(null), cmSource.getNumComponents());
              }
              





              il.setSampleModel(newSM);
              if (newCM != null)
                il.setColorModel(newCM);
            } else {
              il.setColorModel(cmSource);
            }
          }
        }
      } else if (il.getSampleModel(null) == null)
      {
        il.setColorModel(layout.getColorModel(im));
      }
    }
    else if (il != null)
    {

      il = (ImageLayout)layout.clone();
      


      if ((il.getColorModel(null) != null) && (il.getSampleModel(null) == null))
      {

        int smWidth = il.getTileWidth(null);
        if (smWidth == 0) {
          smWidth = 512;
        }
        int smHeight = il.getTileHeight(null);
        if (smHeight == 0) {
          smHeight = 512;
        }
        

        SampleModel derivedSM = il.getColorModel(null).createCompatibleSampleModel(smWidth, smHeight);
        



        il.setSampleModel(derivedSM);
      }
    }
    





    if ((il != null) && (!il.isValid(512)) && (il.getSampleModel(null) != null) && (!setColorModelFromFactory(il.getSampleModel(null), sources, config, il)))
    {




      ColorModel cm = null;
      SampleModel srcSM = il.getSampleModel(null);
      






      if ((im != null) && (im.getColorModel() != null) && ((im.getColorModel() instanceof IndexColorModel)) && (config != null) && (config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) && (((Boolean)config.get(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)).booleanValue()))
      {





        IndexColorModel icm = (IndexColorModel)im.getColorModel();
        




        cm = PlanarImage.getDefaultColorModel(srcSM.getDataType(), icm.getNumComponents());
        
        SampleModel newSM;
        SampleModel newSM;
        if (cm != null) {
          newSM = cm.createCompatibleSampleModel(srcSM.getWidth(), srcSM.getHeight());
        }
        else {
          newSM = RasterFactory.createPixelInterleavedSampleModel(srcSM.getDataType(), srcSM.getWidth(), srcSM.getHeight(), icm.getNumComponents());
        }
        




        il.setSampleModel(newSM);
      }
      else
      {
        cm = ImageUtil.getCompatibleColorModel(il.getSampleModel(null), config);
      }
      


      if (cm != null) {
        il.setColorModel(cm);
      }
    }
    



    if ((layout != null) && (il != null) && (!layout.isValid(192)))
    {

      Dimension defaultTileSize = JAI.getDefaultTileSize();
      if (defaultTileSize != null) {
        if (!layout.isValid(64)) {
          if (il.getTileWidth(null) <= 0) {
            il.setTileWidth(width);
          }
          else {
            int numX = XToTileX(il.getMinX(null) + il.getWidth(null) - 1, il.getTileGridXOffset(null), il.getTileWidth(null)) - XToTileX(il.getMinX(null), il.getTileGridXOffset(null), il.getTileWidth(null)) + 1;
            







            if ((numX <= 1) && (il.getWidth(null) >= 2 * width))
            {
              il.setTileWidth(width);
            }
          }
        }
        
        if (!layout.isValid(128)) {
          if (il.getTileHeight(null) <= 0) {
            il.setTileHeight(height);
          }
          else {
            int numY = YToTileY(il.getMinY(null) + il.getHeight(null) - 1, il.getTileGridYOffset(null), il.getTileHeight(null)) - YToTileY(il.getMinY(null), il.getTileGridYOffset(null), il.getTileHeight(null)) + 1;
            







            if ((numY <= 1) && (il.getHeight(null) >= 2 * height))
            {
              il.setTileHeight(height);
            }
          }
        }
      }
    }
    






    if (((layout == null) || (!layout.isValid(64))) && (il.isValid(68)) && (il.getTileWidth(null) > il.getWidth(null)))
    {



      il.setTileWidth(il.getWidth(null));
    }
    

    if (((layout == null) || (!layout.isValid(128))) && (il.isValid(136)) && (il.getTileHeight(null) > il.getHeight(null)))
    {



      il.setTileHeight(il.getHeight(null));
    }
    
    return il;
  }
  



























  private static boolean setColorModelFromFactory(SampleModel sampleModel, Vector sources, Map config, ImageLayout layout)
  {
    boolean isColorModelSet = false;
    
    if ((config != null) && (config.containsKey(JAI.KEY_COLOR_MODEL_FACTORY)))
    {
      ColorModelFactory cmf = (ColorModelFactory)config.get(JAI.KEY_COLOR_MODEL_FACTORY);
      
      ColorModel cm = cmf.createColorModel(sampleModel, sources, config);
      

      if ((cm != null) && (JDKWorkarounds.areCompatibleDataModels(sampleModel, cm)))
      {
        layout.setColorModel(cm);
        isColorModelSet = true;
      }
    }
    
    return isColorModelSet;
  }
  

































































































































































  public OpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    super(layoutHelper(layout, sources, configuration), sources, configuration);
    

    if (configuration != null)
    {
      Object cacheConfig = configuration.get(JAI.KEY_TILE_CACHE);
      

      if ((cacheConfig != null) && ((cacheConfig instanceof TileCache)) && (((TileCache)cacheConfig).getMemoryCapacity() > 0L))
      {

        cache = ((TileCache)cacheConfig);
      }
      

      Object schedulerConfig = configuration.get(JAI.KEY_TILE_SCHEDULER);
      

      if ((schedulerConfig != null) && ((schedulerConfig instanceof TileScheduler)))
      {
        scheduler = ((TileScheduler)schedulerConfig);
      }
      
      try
      {
        Class sunScheduler = Class.forName("com.sun.media.jai.util.SunTileScheduler");
        

        isSunTileScheduler = sunScheduler.isInstance(scheduler);
      }
      catch (Exception e) {}
      


      tileCacheMetric = configuration.get(JAI.KEY_TILE_CACHE_METRIC);
      

      Object recyclingEnabledValue = configuration.get(JAI.KEY_CACHED_TILE_RECYCLING_ENABLED);
      
      if ((recyclingEnabledValue instanceof Boolean)) {
        isCachedTileRecyclingEnabled = ((Boolean)recyclingEnabledValue).booleanValue();
      }
      


      Object recyclerValue = configuration.get(JAI.KEY_TILE_RECYCLER);
      if ((recyclerValue instanceof TileRecycler)) {
        tileRecycler = ((TileRecycler)recyclerValue);
      }
    }
    
    this.cobbleSources = cobbleSources;
  }
  
  private class TCL implements TileComputationListener
  {
    OpImage opImage;
    
    TCL(OpImage x1, OpImage.1 x2) {
      this(x1);
    }
    
    private TCL(OpImage opImage) {
      this.opImage = opImage;
    }
    


    public void tileComputed(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Raster tile)
    {
      if (image == opImage)
      {
        addTileToCache(tileX, tileY, tile);
      }
    }
    







    public void tileCancelled(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY) {}
    







    public void tileComputationFailure(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Throwable situation) {}
  }
  







  protected static Vector vectorize(RenderedImage image)
  {
    if (image == null) {
      throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
    }
    Vector v = new Vector(1);
    v.addElement(image);
    return v;
  }
  













  protected static Vector vectorize(RenderedImage image1, RenderedImage image2)
  {
    if ((image1 == null) || (image2 == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
    }
    Vector v = new Vector(2);
    v.addElement(image1);
    v.addElement(image2);
    return v;
  }
  















  protected static Vector vectorize(RenderedImage image1, RenderedImage image2, RenderedImage image3)
  {
    if ((image1 == null) || (image2 == null) || (image3 == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
    }
    Vector v = new Vector(3);
    v.addElement(image1);
    v.addElement(image2);
    v.addElement(image3);
    return v;
  }
  


















  static Vector checkSourceVector(Vector sources, boolean checkElements)
  {
    if (sources == null) {
      throw new IllegalArgumentException(JaiI18N.getString("OpImage2"));
    }
    
    if (checkElements)
    {
      int numSources = sources.size();
      for (int i = 0; i < numSources; i++)
      {
        if (sources.get(i) == null) {
          throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
        }
      }
    }
    

    return sources;
  }
  






  public TileCache getTileCache()
  {
    return cache;
  }
  










  public void setTileCache(TileCache cache)
  {
    if (this.cache != null) {
      this.cache.removeTiles(this);
    }
    this.cache = cache;
  }
  










  protected Raster getTileFromCache(int tileX, int tileY)
  {
    return cache != null ? cache.getTile(this, tileX, tileY) : null;
  }
  









  protected void addTileToCache(int tileX, int tileY, Raster tile)
  {
    if (cache != null) {
      cache.add(this, tileX, tileY, tile, tileCacheMetric);
    }
  }
  




  public Object getTileCacheMetric()
  {
    return tileCacheMetric;
  }
  

















  public Raster getTile(int tileX, int tileY)
  {
    Raster tile = null;
    

    if ((tileX >= getMinTileX()) && (tileX <= getMaxTileX()) && (tileY >= getMinTileY()) && (tileY <= getMaxTileY()))
    {

      tile = getTileFromCache(tileX, tileY);
      
      if (tile == null) {
        try {
          tile = scheduler.scheduleTile(this, tileX, tileY);
        }
        catch (OutOfMemoryError e) {
          if (cache != null) {
            cache.flush();
            System.gc();
          }
          

          tile = scheduler.scheduleTile(this, tileX, tileY);
        }
        

        addTileToCache(tileX, tileY, tile);
      }
    }
    
    return tile;
  }
  





























  public Raster computeTile(int tileX, int tileY)
  {
    WritableRaster dest = createWritableRaster(sampleModel, new Point(tileXToX(tileX), tileYToY(tileY)));
    



    Rectangle destRect = getTileRect(tileX, tileY);
    
    int numSources = getNumSources();
    
    if (cobbleSources) {
      Raster[] rasterSources = new Raster[numSources];
      
      for (int i = 0; i < numSources; i++) {
        PlanarImage source = getSource(i);
        Rectangle srcRect = mapDestRect(destRect, i);
        




        rasterSources[i] = ((srcRect != null) && (srcRect.isEmpty()) ? null : source.getData(srcRect));
      }
      
      computeRect(rasterSources, dest, destRect);
      
      for (int i = 0; i < numSources; i++) {
        Raster sourceData = rasterSources[i];
        if (sourceData != null) {
          PlanarImage source = getSourceImage(i);
          

          if (source.overlapsMultipleTiles(sourceData.getBounds())) {
            recycleTile(sourceData);
          }
        }
      }
    } else {
      PlanarImage[] imageSources = new PlanarImage[numSources];
      for (int i = 0; i < numSources; i++) {
        imageSources[i] = getSource(i);
      }
      computeRect(imageSources, dest, destRect);
    }
    
    return dest;
  }
  
































  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    String className = getClass().getName();
    throw new RuntimeException(className + " " + JaiI18N.getString("OpImage0"));
  }
  



























  protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect)
  {
    String className = getClass().getName();
    throw new RuntimeException(className + " " + JaiI18N.getString("OpImage1"));
  }
  


































  public Point[] getTileDependencies(int tileX, int tileY, int sourceIndex)
  {
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources()))
    {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    Rectangle rect = getTileRect(tileX, tileY);
    if (rect.isEmpty())
    {
      return null;
    }
    










    PlanarImage src = getSource(sourceIndex);
    Rectangle srcRect = mapDestRect(rect, sourceIndex);
    
    int minTileX = src.XToTileX(x);
    int maxTileX = src.XToTileX(x + width - 1);
    
    int minTileY = src.YToTileY(y);
    int maxTileY = src.YToTileY(y + height - 1);
    

    minTileX = Math.max(minTileX, src.getMinTileX());
    maxTileX = Math.min(maxTileX, src.getMaxTileX());
    
    minTileY = Math.max(minTileY, src.getMinTileY());
    maxTileY = Math.min(maxTileY, src.getMaxTileY());
    
    int numXTiles = maxTileX - minTileX + 1;
    int numYTiles = maxTileY - minTileY + 1;
    if ((numXTiles <= 0) || (numYTiles <= 0))
    {
      return null;
    }
    
    Point[] ret = new Point[numYTiles * numXTiles];
    int i = 0;
    
    for (int y = minTileY; y <= maxTileY; y++) {
      for (int x = minTileX; x <= maxTileX; x++) {
        ret[(i++)] = new Point(x, y);
      }
    }
    
    return ret;
  }
  

















  public Raster[] getTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int numTiles = tileIndices.length;
    

    Raster[] tiles = new Raster[numTiles];
    

    boolean[] computeTiles = new boolean[numTiles];
    
    int minTileX = getMinTileX();
    int maxTileX = getMaxTileX();
    int minTileY = getMinTileY();
    int maxTileY = getMaxTileY();
    
    int count = 0;
    
    for (int i = 0; i < numTiles; i++) {
      int tileX = x;
      int tileY = y;
      

      if ((tileX >= minTileX) && (tileX <= maxTileX) && (tileY >= minTileY) && (tileY <= maxTileY))
      {

        tiles[i] = getTileFromCache(tileX, tileY);
        
        if (tiles[i] == null)
        {
          computeTiles[i] = true;
          count++;
        }
      }
    }
    
    if (count > 0) {
      if (count == numTiles)
      {
        tiles = scheduler.scheduleTiles(this, tileIndices);
        
        if ((cache != null) && 
          (cache != null)) {
          for (int i = 0; i < numTiles; i++) {
            cache.add(this, x, y, tiles[i], tileCacheMetric);

          }
          
        }
        

      }
      else
      {

        Point[] indices = new Point[count];
        count = 0;
        for (int i = 0; i < numTiles; i++) {
          if (computeTiles[i] != 0) {
            indices[(count++)] = tileIndices[i];
          }
        }
        

        Raster[] newTiles = scheduler.scheduleTiles(this, indices);
        
        count = 0;
        for (int i = 0; i < numTiles; i++) {
          if (computeTiles[i] != 0) {
            tiles[i] = newTiles[(count++)];
            addTileToCache(x, y, tiles[i]);
          }
        }
      }
    }
    

    return tiles;
  }
  

  private static TileComputationListener[] prependListener(TileComputationListener[] listeners, TileComputationListener listener)
  {
    if (listeners == null) {
      return new TileComputationListener[] { listener };
    }
    
    TileComputationListener[] newListeners = new TileComputationListener[listeners.length + 1];
    
    newListeners[0] = listener;
    System.arraycopy(listeners, 0, newListeners, 1, listeners.length);
    
    return newListeners;
  }
  




































  public TileRequest queueTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    











    TileComputationListener[] tileListeners = getTileComputationListeners();
    



    if (!isSunTileScheduler)
    {
      TileComputationListener localListener = new TCL(this, null);
      

      tileListeners = prependListener(tileListeners, localListener);
    }
    

    return scheduler.scheduleTiles(this, tileIndices, tileListeners);
  }
  














  public void cancelTiles(TileRequest request, Point[] tileIndices)
  {
    if (request == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    scheduler.cancelTiles(request, tileIndices);
  }
  










  public void prefetchTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    






    if (tileIndices == null) {
      return;
    }
    

    scheduler.prefetchTiles(this, tileIndices);
  }
  









































  public Point2D mapDestPoint(Point2D destPt, int sourceIndex)
  {
    if (destPt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    Rectangle destRect = new Rectangle((int)destPt.getX(), (int)destPt.getY(), 1, 1);
    


    Rectangle sourceRect = mapDestRect(destRect, sourceIndex);
    
    Point2D pt = (Point2D)destPt.clone();
    pt.setLocation(x + (width - 1.0D) / 2.0D, y + (height - 1.0D) / 2.0D);
    

    return pt;
  }
  







































  public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex)
  {
    if (sourcePt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    Rectangle sourceRect = new Rectangle((int)sourcePt.getX(), (int)sourcePt.getY(), 1, 1);
    


    Rectangle destRect = mapSourceRect(sourceRect, sourceIndex);
    

    if (destRect == null) {
      return null;
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    pt.setLocation(x + (width - 1.0D) / 2.0D, y + (height - 1.0D) / 2.0D);
    

    return pt;
  }
  

























  public abstract Rectangle mapSourceRect(Rectangle paramRectangle, int paramInt);
  

























  public abstract Rectangle mapDestRect(Rectangle paramRectangle, int paramInt);
  

























  public int getOperationComputeType()
  {
    return 1;
  }
  









  public boolean computesUniqueTiles()
  {
    return true;
  }
  














  public synchronized void dispose()
  {
    if (isDisposed) {
      return;
    }
    
    isDisposed = true;
    
    if (cache != null) {
      if ((isCachedTileRecyclingEnabled) && (tileRecycler != null)) {
        Raster[] tiles = cache.getTiles(this);
        if (tiles != null) {
          int numTiles = tiles.length;
          for (int i = 0; i < numTiles; i++) {
            tileRecycler.recycleTile(tiles[i]);
          }
        }
      }
      cache.removeTiles(this);
    }
    super.dispose();
  }
  







  /**
   * @deprecated
   */
  public boolean hasExtender(int sourceIndex)
  {
    if (sourceIndex != 0)
      throw new ArrayIndexOutOfBoundsException();
    if ((this instanceof AreaOpImage))
      return ((AreaOpImage)this).getBorderExtender() != null;
    if ((this instanceof GeometricOpImage)) {
      return ((GeometricOpImage)this).getBorderExtender() != null;
    }
    return false;
  }
  








  /**
   * @deprecated
   */
  public static int getExpandedNumBands(SampleModel sampleModel, ColorModel colorModel)
  {
    if ((colorModel instanceof IndexColorModel)) {
      return colorModel.getNumComponents();
    }
    return sampleModel.getNumBands();
  }
  














  protected synchronized RasterFormatTag[] getFormatTags()
  {
    if (formatTags == null) {
      RenderedImage[] sourceArray = new RenderedImage[getNumSources()];
      if (sourceArray.length > 0) {
        getSources().toArray(sourceArray);
      }
      formatTags = RasterAccessor.findCompatibleTags(sourceArray, this);
    }
    
    return formatTags;
  }
  




  public TileRecycler getTileRecycler()
  {
    return tileRecycler;
  }
  












  protected final WritableRaster createTile(int tileX, int tileY)
  {
    return createWritableRaster(sampleModel, new Point(tileXToX(tileX), tileYToY(tileY)));
  }
  






















  protected void recycleTile(Raster tile)
  {
    if (tile == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (tileRecycler != null) {
      tileRecycler.recycleTile(tile);
    }
  }
}
