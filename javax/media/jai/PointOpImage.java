package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Vector;


























public abstract class PointOpImage
  extends OpImage
{
  private boolean isDisposed = false;
  

  private boolean areFieldsInitialized = false;
  

  private boolean checkInPlaceOperation = false;
  

  private boolean isInPlaceEnabled = false;
  


  private WritableRenderedImage source0AsWritableRenderedImage;
  


  private OpImage source0AsOpImage;
  


  private boolean source0IsWritableRenderedImage;
  

  private boolean sameBounds;
  

  private boolean sameTileGrid;
  


  private static ImageLayout layoutHelper(ImageLayout layout, Vector sources, Map config)
  {
    int numSources = sources.size();
    
    if (numSources < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic5"));
    }
    
    RenderedImage source0 = (RenderedImage)sources.get(0);
    Rectangle isect = new Rectangle(source0.getMinX(), source0.getMinY(), source0.getWidth(), source0.getHeight());
    


    Rectangle rect = new Rectangle();
    for (int i = 1; i < numSources; i++) {
      RenderedImage s = (RenderedImage)sources.get(i);
      rect.setBounds(s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
      
      isect = isect.intersection(rect);
    }
    
    if (isect.isEmpty()) {
      throw new IllegalArgumentException(JaiI18N.getString("PointOpImage0"));
    }
    

    if (layout == null) {
      layout = new ImageLayout(x, y, width, height);
    }
    else {
      layout = (ImageLayout)layout.clone();
      if (!layout.isValid(1)) {
        layout.setMinX(x);
      }
      if (!layout.isValid(2)) {
        layout.setMinY(y);
      }
      if (!layout.isValid(4)) {
        layout.setWidth(width);
      }
      if (!layout.isValid(8)) {
        layout.setHeight(height);
      }
      
      Rectangle r = new Rectangle(layout.getMinX(null), layout.getMinY(null), layout.getWidth(null), layout.getHeight(null));
      


      if (r.isEmpty()) {
        throw new IllegalArgumentException(JaiI18N.getString("PointOpImage1"));
      }
      

      if (!isect.contains(r)) {
        throw new IllegalArgumentException(JaiI18N.getString("PointOpImage2"));
      }
    }
    




    if ((numSources > 1) && (!layout.isValid(256)))
    {

      SampleModel sm = source0.getSampleModel();
      ColorModel cm = source0.getColorModel();
      int dtype0 = getAppropriateDataType(sm);
      int bands0 = getBandCount(sm, cm);
      int dtype = dtype0;
      int bands = bands0;
      
      for (int i = 1; i < numSources; i++) {
        RenderedImage source = (RenderedImage)sources.get(i);
        sm = source.getSampleModel();
        cm = source.getColorModel();
        int sourceBands = getBandCount(sm, cm);
        
        dtype = mergeTypes(dtype, getPixelType(sm));
        bands = Math.min(bands, sourceBands);
      }
      

      if ((dtype == -1) && (bands > 1)) {
        dtype = 0;
      }
      


      SampleModel sm0 = source0.getSampleModel();
      if ((dtype != sm0.getDataType()) || (bands != sm0.getNumBands())) {
        int tw = layout.getTileWidth(source0);
        int th = layout.getTileHeight(source0);
        SampleModel sampleModel;
        SampleModel sampleModel; if (dtype == -1) {
          sampleModel = new MultiPixelPackedSampleModel(0, tw, th, 1);
        }
        else
        {
          sampleModel = RasterFactory.createPixelInterleavedSampleModel(dtype, tw, th, bands);
        }
        



        layout.setSampleModel(sampleModel);
        

        if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, cm)))
        {
          cm = ImageUtil.getCompatibleColorModel(sampleModel, config);
          layout.setColorModel(cm);
        }
      }
    }
    
    return layout;
  }
  


  private static int getPixelType(SampleModel sampleModel)
  {
    return ImageUtil.isBinary(sampleModel) ? -1 : sampleModel.getDataType();
  }
  




  private static int getBandCount(SampleModel sampleModel, ColorModel colorModel)
  {
    if (ImageUtil.isBinary(sampleModel))
      return 1;
    if ((colorModel instanceof IndexColorModel)) {
      return colorModel.getNumComponents();
    }
    return sampleModel.getNumBands();
  }
  



  private static int getAppropriateDataType(SampleModel sampleModel)
  {
    int dataType = sampleModel.getDataType();
    int retVal = dataType;
    
    if (ImageUtil.isBinary(sampleModel)) {
      retVal = -1;
    } else if ((dataType == 1) || (dataType == 3))
    {
      boolean canUseBytes = true;
      boolean canUseShorts = true;
      
      int[] ss = sampleModel.getSampleSize();
      for (int i = 0; i < ss.length; i++) {
        if (ss[i] > 16) {
          canUseBytes = false;
          canUseShorts = false;
          break;
        }
        if (ss[i] > 8) {
          canUseBytes = false;
        }
      }
      
      if (canUseBytes) {
        retVal = 0;
      } else if (canUseShorts) {
        retVal = 1;
      }
    }
    
    return retVal;
  }
  








  private static int mergeTypes(int type0, int type1)
  {
    if (type0 == type1) {
      return type0;
    }
    

    int type = type1;
    


    switch (type0)
    {
    case -1: 
    case 0: 
      break;
    case 2: 
      if (type1 == 0) {
        type = 2;
      } else if (type1 == 1) {
        type = 3;
      }
      break;
    case 1: 
      if (type1 == 0) {
        type = 1;
      } else if (type1 == 2) {
        type = 3;
      }
      break;
    case 3: 
      if ((type1 == 0) || (type1 == 2) || (type1 == 1))
      {

        type = 3;
      }
      break;
    case 4: 
      if (type1 != 5) {
        type = 4;
      }
      break;
    case 5: 
      type = 5;
    }
    
    
    return type;
  }
  


















































  public PointOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    super(checkSourceVector(sources, true), layoutHelper(layout, sources, configuration), configuration, cobbleSources);
  }
  

























  public PointOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    this(vectorize(source), layout, configuration, cobbleSources);
  }
  

























  public PointOpImage(RenderedImage source0, RenderedImage source1, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    this(vectorize(source0, source1), layout, configuration, cobbleSources);
  }
  




























  public PointOpImage(RenderedImage source0, RenderedImage source1, RenderedImage source2, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    this(vectorize(source0, source1, source2), layout, configuration, cobbleSources);
  }
  





  private synchronized void initializeFields()
  {
    if (areFieldsInitialized) {
      return;
    }
    PlanarImage source0 = getSource(0);
    
    if (checkInPlaceOperation)
    {















      Vector source0Sinks = source0.getSinks();
      isInPlaceEnabled = ((source0 != null) && (getTileGridXOffset() == source0.getTileGridXOffset()) && (getTileGridYOffset() == source0.getTileGridYOffset()) && (getBounds().equals(source0.getBounds())) && ((source0 instanceof OpImage)) && (hasCompatibleSampleModel(source0)) && ((source0Sinks == null) || (source0Sinks.size() <= 1)));
      












      if ((isInPlaceEnabled) && (!((OpImage)source0).computesUniqueTiles()))
      {
        isInPlaceEnabled = false;
      }
      


      if (isInPlaceEnabled) {
        try {
          Method getTileMethod = source0.getClass().getMethod("getTile", new Class[] { Integer.TYPE, Integer.TYPE });
          


          Class opImageClass = Class.forName("javax.media.jai.OpImage");
          
          Class declaringClass = getTileMethod.getDeclaringClass();
          

          if (!declaringClass.equals(opImageClass)) {
            isInPlaceEnabled = false;
          }
        } catch (ClassNotFoundException e) {
          isInPlaceEnabled = false;
        } catch (NoSuchMethodException e) {
          isInPlaceEnabled = false;
        }
      }
      

      if (isInPlaceEnabled)
      {
        source0IsWritableRenderedImage = (source0 instanceof WritableRenderedImage);
        


        if (source0IsWritableRenderedImage) {
          source0AsWritableRenderedImage = ((WritableRenderedImage)source0);
        }
        else {
          source0AsOpImage = ((OpImage)source0);
        }
      }
      

      checkInPlaceOperation = false;
    }
    

    int numSources = getNumSources();
    

    sameBounds = true;
    sameTileGrid = true;
    

    for (int i = 0; (i < numSources) && ((sameBounds) || (sameTileGrid)); i++) {
      PlanarImage source = getSource(i);
      

      if (sameBounds) {
        sameBounds = ((sameBounds) && (minX == minX) && (minY == minY) && (width == width) && (height == height));
      }
      



      if (sameTileGrid) {
        sameTileGrid = ((sameTileGrid) && (tileGridXOffset == tileGridXOffset) && (tileGridYOffset == tileGridYOffset) && (tileWidth == tileWidth) && (tileHeight == tileHeight));
      }
    }
    





    areFieldsInitialized = true;
  }
  








  private boolean hasCompatibleSampleModel(PlanarImage src)
  {
    SampleModel srcSM = src.getSampleModel();
    int numBands = sampleModel.getNumBands();
    
    boolean isCompatible = (srcSM.getTransferType() == sampleModel.getTransferType()) && (srcSM.getWidth() == sampleModel.getWidth()) && (srcSM.getHeight() == sampleModel.getHeight()) && (srcSM.getNumBands() == numBands) && (srcSM.getClass().equals(sampleModel.getClass()));
    





    if (isCompatible) {
      if ((sampleModel instanceof ComponentSampleModel)) {
        ComponentSampleModel smSrc = (ComponentSampleModel)srcSM;
        ComponentSampleModel smDst = (ComponentSampleModel)sampleModel;
        isCompatible = (isCompatible) && (smSrc.getPixelStride() == smDst.getPixelStride()) && (smSrc.getScanlineStride() == smDst.getScanlineStride());
        

        int[] biSrc = smSrc.getBankIndices();
        int[] biDst = smDst.getBankIndices();
        int[] boSrc = smSrc.getBandOffsets();
        int[] boDst = smDst.getBandOffsets();
        for (int b = 0; (b < numBands) && (isCompatible); b++) {
          isCompatible = (isCompatible) && (biSrc[b] == biDst[b]) && (boSrc[b] == boDst[b]);
        }
        
      }
      else if ((sampleModel instanceof SinglePixelPackedSampleModel))
      {
        SinglePixelPackedSampleModel smSrc = (SinglePixelPackedSampleModel)srcSM;
        
        SinglePixelPackedSampleModel smDst = (SinglePixelPackedSampleModel)sampleModel;
        
        isCompatible = (isCompatible) && (smSrc.getScanlineStride() == smDst.getScanlineStride());
        
        int[] bmSrc = smSrc.getBitMasks();
        int[] bmDst = smDst.getBitMasks();
        for (int b = 0; (b < numBands) && (isCompatible); b++) {
          isCompatible = (isCompatible) && (bmSrc[b] == bmDst[b]);
        }
      }
      else if ((sampleModel instanceof MultiPixelPackedSampleModel)) {
        MultiPixelPackedSampleModel smSrc = (MultiPixelPackedSampleModel)srcSM;
        
        MultiPixelPackedSampleModel smDst = (MultiPixelPackedSampleModel)sampleModel;
        
        isCompatible = (isCompatible) && (smSrc.getPixelBitStride() == smDst.getPixelBitStride()) && (smSrc.getScanlineStride() == smDst.getScanlineStride()) && (smSrc.getDataBitOffset() == smDst.getDataBitOffset());

      }
      else
      {
        isCompatible = false;
      }
    }
    
    return isCompatible;
  }
  












  protected void permitInPlaceOperation()
  {
    Object inPlaceProperty = null;
    try {
      inPlaceProperty = AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run() {
          String name = "javax.media.jai.PointOpImage.InPlace";
          return System.getProperty(name);
        }
      });
    }
    catch (SecurityException se) {}
    


    checkInPlaceOperation = ((inPlaceProperty == null) || (!(inPlaceProperty instanceof String)) || (!((String)inPlaceProperty).equalsIgnoreCase("false")));
  }
  



















  protected boolean isColormapOperation()
  {
    return false;
  }
  















  public Raster computeTile(int tileX, int tileY)
  {
    if (!cobbleSources) {
      return super.computeTile(tileX, tileY);
    }
    

    initializeFields();
    

    WritableRaster dest = null;
    if (isInPlaceEnabled) {
      if (source0IsWritableRenderedImage)
      {
        dest = source0AsWritableRenderedImage.getWritableTile(tileX, tileY);

      }
      else
      {
        Raster raster = source0AsOpImage.getTileFromCache(tileX, tileY);
        

        if (raster == null) {
          try
          {
            raster = source0AsOpImage.computeTile(tileX, tileY);
            if ((raster instanceof WritableRaster)) {
              dest = (WritableRaster)raster;
            }
          }
          catch (Exception e) {}
        }
      }
    }
    






    boolean recyclingSource0Tile = dest != null;
    
    if (!recyclingSource0Tile)
    {
      Point org = new Point(tileXToX(tileX), tileYToY(tileY));
      dest = createWritableRaster(sampleModel, org);
    }
    


    if (isColormapOperation()) {
      if (!recyclingSource0Tile) {
        PlanarImage src = getSource(0);
        Raster srcTile = null;
        Rectangle srcRect = null;
        Rectangle dstRect = dest.getBounds();
        


        if (sameTileGrid)
        {

          srcTile = getSource(0).getTile(tileX, tileY);
        }
        else if (dstRect.intersects(src.getBounds()))
        {


          srcTile = src.getData(dstRect);

        }
        else
        {
          return dest;
        }
        
        srcRect = srcTile.getBounds();
        


        if (!dstRect.contains(srcRect)) {
          srcRect = dstRect.intersection(srcRect);
          srcTile = srcTile.createChild(srcTile.getMinX(), srcTile.getMinY(), width, height, x, y, null);
        }
        







        JDKWorkarounds.setRect(dest, srcTile, 0, 0);
      }
      return dest;
    }
    

    int destMinX = dest.getMinX();
    int destMinY = dest.getMinY();
    int destMaxX = destMinX + dest.getWidth();
    int destMaxY = destMinY + dest.getHeight();
    

    Rectangle bounds = getBounds();
    if (destMinX < x) {
      destMinX = x;
    }
    int boundsMaxX = x + width;
    if (destMaxX > boundsMaxX) {
      destMaxX = boundsMaxX;
    }
    if (destMinY < y) {
      destMinY = y;
    }
    int boundsMaxY = y + height;
    if (destMaxY > boundsMaxY) {
      destMaxY = boundsMaxY;
    }
    

    int numSrcs = getNumSources();
    


    if ((recyclingSource0Tile) && (numSrcs == 1))
    {
      Raster[] sources = { dest };
      Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
      

      computeRect(sources, dest, destRect);
    } else if ((recyclingSource0Tile) && (sameBounds) && (sameTileGrid))
    {
      Raster[] sources = new Raster[numSrcs];
      sources[0] = dest;
      for (int i = 1; i < numSrcs; i++) {
        sources[i] = getSource(i).getTile(tileX, tileY);
      }
      Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
      

      computeRect(sources, dest, destRect);
    }
    else {
      if (!sameBounds)
      {
        for (int i = recyclingSource0Tile ? 1 : 0; i < numSrcs; i++) {
          bounds = getSource(i).getBounds();
          if (destMinX < x) {
            destMinX = x;
          }
          boundsMaxX = x + width;
          if (destMaxX > boundsMaxX) {
            destMaxX = boundsMaxX;
          }
          if (destMinY < y) {
            destMinY = y;
          }
          boundsMaxY = y + height;
          if (destMaxY > boundsMaxY) {
            destMaxY = boundsMaxY;
          }
          
          if ((destMinX >= destMaxX) || (destMinY >= destMaxY)) {
            return dest;
          }
        }
      }
      

      Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
      



      Raster[] sources = new Raster[numSrcs];
      
      if (sameTileGrid)
      {

        if (recyclingSource0Tile) {
          sources[0] = dest;
        }
        for (int i = recyclingSource0Tile ? 1 : 0; i < numSrcs; i++) {
          sources[i] = getSource(i).getTile(tileX, tileY);
        }
        
        computeRect(sources, dest, destRect);

      }
      else
      {

        IntegerSequence xSplits = new IntegerSequence(destMinX, destMaxX);
        
        xSplits.insert(destMinX);
        xSplits.insert(destMaxX);
        
        IntegerSequence ySplits = new IntegerSequence(destMinY, destMaxY);
        
        ySplits.insert(destMinY);
        ySplits.insert(destMaxY);
        
        for (int i = recyclingSource0Tile ? 1 : 0; i < numSrcs; i++) {
          PlanarImage s = getSource(i);
          s.getSplits(xSplits, ySplits, destRect);
        }
        





        Rectangle subRect = new Rectangle();
        
        ySplits.startEnumeration();
        int y2; for (int y1 = ySplits.nextElement(); ySplits.hasMoreElements(); y1 = y2) {
          y2 = ySplits.nextElement();
          int h = y2 - y1;
          
          xSplits.startEnumeration();
          int x2; for (int x1 = xSplits.nextElement(); 
              xSplits.hasMoreElements(); x1 = x2) {
            x2 = xSplits.nextElement();
            int w = x2 - x1;
            

            if (recyclingSource0Tile) {
              sources[0] = dest;
            }
            for (int i = recyclingSource0Tile ? 1 : 0; 
                i < numSrcs; i++) {
              PlanarImage s = getSource(i);
              int tx = s.XToTileX(x1);
              int ty = s.YToTileY(y1);
              sources[i] = s.getTile(tx, ty);
            }
            
            x = x1;
            y = y1;
            width = w;
            height = h;
            computeRect(sources, dest, subRect);
          }
        }
      }
    }
    
    if ((recyclingSource0Tile) && (source0IsWritableRenderedImage)) {
      source0AsWritableRenderedImage.releaseWritableTile(tileX, tileY);
    }
    
    return dest;
  }
  















  public final Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    return new Rectangle(sourceRect);
  }
  
















  public final Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    return new Rectangle(destRect);
  }
  









  public synchronized void dispose()
  {
    if (isDisposed) {
      return;
    }
    
    isDisposed = true;
    
    if ((cache != null) && (isInPlaceEnabled) && (tileRecycler != null)) {
      cache.removeTiles(this);
    }
    
    super.dispose();
  }
}
