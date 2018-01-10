package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;






















































































public class TiledImage
  extends PlanarImage
  implements WritableRenderedImage, PropertyChangeListener
{
  protected int tilesX;
  protected int tilesY;
  protected int minTileX;
  protected int minTileY;
  protected WritableRaster[][] tiles;
  protected int[][] writers;
  protected Vector tileObservers = null;
  

  private boolean areBuffersShared = false;
  

  private TiledImage parent = null;
  

  private SampleModel ancestorSampleModel = null;
  

  private int[] bandList = null;
  

  private int[] numWritableTiles = null;
  

  private ROI srcROI = null;
  


  private Rectangle overlapBounds = null;
  








  private static SampleModel coerceSampleModel(SampleModel sampleModel, int sampleModelWidth, int sampleModelHeight)
  {
    return (sampleModel.getWidth() == sampleModelWidth) && (sampleModel.getHeight() == sampleModelHeight) ? sampleModel : sampleModel.createCompatibleSampleModel(sampleModelWidth, sampleModelHeight);
  }
  











  private void initTileGrid(TiledImage parent)
  {
    if (parent != null) {
      minTileX = minTileX;
      minTileY = minTileY;
    } else {
      minTileX = getMinTileX();
      minTileY = getMinTileY();
    }
    
    int maxTileX = getMaxTileX();
    int maxTileY = getMaxTileY();
    
    tilesX = (maxTileX - minTileX + 1);
    tilesY = (maxTileY - minTileY + 1);
  }
  

























  public TiledImage(int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, SampleModel tileSampleModel, ColorModel colorModel)
  {
    this(null, minX, minY, width, height, tileGridXOffset, tileGridYOffset, tileSampleModel, colorModel);
  }
  










  private TiledImage(TiledImage parent, int minX, int minY, int width, int height, int tileGridXOffset, int tileGridYOffset, SampleModel sampleModel, ColorModel colorModel)
  {
    super(new ImageLayout(minX, minY, width, height, tileGridXOffset, tileGridYOffset, sampleModel.getWidth(), sampleModel.getHeight(), sampleModel, colorModel), null, null);
    


    initTileGrid(parent);
    
    if (parent == null) {
      tiles = new WritableRaster[tilesX][tilesY];
      writers = new int[tilesX][tilesY];
      tileObservers = new Vector();
      numWritableTiles = new int[1];
      numWritableTiles[0] = 0;
      ancestorSampleModel = sampleModel;
    } else {
      this.parent = parent;
      tiles = tiles;
      writers = writers;
      tileObservers = tileObservers;
      numWritableTiles = numWritableTiles;
      ancestorSampleModel = ancestorSampleModel;
    }
    
    tileFactory = ((TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY));
  }
  




















  /**
   * @deprecated
   */
  public TiledImage(Point origin, SampleModel sampleModel, int tileWidth, int tileHeight)
  {
    this(x, y, sampleModel.getWidth(), sampleModel.getHeight(), x, y, coerceSampleModel(sampleModel, tileWidth, tileHeight), PlanarImage.createColorModel(sampleModel));
  }
  

















  /**
   * @deprecated
   */
  public TiledImage(SampleModel sampleModel, int tileWidth, int tileHeight)
  {
    this(0, 0, sampleModel.getWidth(), sampleModel.getHeight(), 0, 0, coerceSampleModel(sampleModel, tileWidth, tileHeight), PlanarImage.createColorModel(sampleModel));
  }
  
















  public TiledImage(RenderedImage source, int tileWidth, int tileHeight)
  {
    this(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight(), source.getTileGridXOffset(), source.getTileGridYOffset(), coerceSampleModel(source.getSampleModel(), tileWidth, tileHeight), source.getColorModel());
    





    set(source);
  }
  


















  public TiledImage(RenderedImage source, boolean areBuffersShared)
  {
    this(source, source.getTileWidth(), source.getTileHeight());
    this.areBuffersShared = areBuffersShared;
  }
  


























  /**
   * @deprecated
   */
  public static TiledImage createInterleaved(int minX, int minY, int width, int height, int numBands, int dataType, int tileWidth, int tileHeight, int[] bandOffsets)
  {
    SampleModel sm = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands, numBands * tileWidth, bandOffsets);
    




    return new TiledImage(minX, minY, width, height, minX, minY, sm, PlanarImage.createColorModel(sm));
  }
  































  /**
   * @deprecated
   */
  public static TiledImage createBanded(int minX, int minY, int width, int height, int dataType, int tileWidth, int tileHeight, int[] bankIndices, int[] bandOffsets)
  {
    SampleModel sm = new BandedSampleModel(dataType, tileWidth, tileHeight, tileWidth, bankIndices, bandOffsets);
    




    return new TiledImage(minX, minY, width, height, minX, minY, sm, PlanarImage.createColorModel(sm));
  }
  











  private void overlayPixels(WritableRaster tile, RenderedImage im, Rectangle rect)
  {
    WritableRaster child = tile.createWritableChild(x, y, width, height, x, y, bandList);
    




    im.copyData(child);
  }
  









  private void overlayPixels(WritableRaster tile, RenderedImage im, Area a)
  {
    ROIShape rs = new ROIShape(a);
    Rectangle bounds = rs.getBounds();
    LinkedList rectList = rs.getAsRectangleList(x, y, width, height);
    

    int numRects = rectList.size();
    for (int i = 0; i < numRects; i++) {
      Rectangle rect = (Rectangle)rectList.get(i);
      WritableRaster child = tile.createWritableChild(x, y, width, height, x, y, bandList);
      



      im.copyData(child);
    }
  }
  






  private void overlayPixels(WritableRaster tile, RenderedImage im, Rectangle rect, int[][] bitmask)
  {
    Raster r = im.getData(rect);
    


    if (bandList != null) {
      tile = tile.createWritableChild(x, y, width, height, x, y, bandList);
    }
    




    Object data = r.getDataElements(x, y, null);
    



    int leftover = width % 32;
    int bitWidth = (width + 31) / 32 - (leftover > 0 ? 1 : 0);
    int y = y;
    
    for (int j = 0; j < height; y++) {
      int[] rowMask = bitmask[j];
      int x = x;
      
      for (int i = 0; i < bitWidth; i++) {
        int mask32 = rowMask[i];
        int bit = Integer.MIN_VALUE;
        
        for (int b = 0; b < 32; x++) {
          if ((mask32 & bit) != 0) {
            r.getDataElements(x, y, data);
            tile.setDataElements(x, y, data);
          }
          bit >>>= 1;b++;
        }
      }
      
      if (leftover > 0) {
        int mask32 = rowMask[i];
        int bit = Integer.MIN_VALUE;
        
        for (int b = 0; b < leftover; x++) {
          if ((mask32 & bit) != 0) {
            r.getDataElements(x, y, data);
            tile.setDataElements(x, y, data);
          }
          bit >>>= 1;b++;
        }
      }
      j++;
    }
  }
  













































  public void set(RenderedImage im)
  {
    if (im == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if ((getNumSources() > 0) && (im == getSourceImage(0))) {
      return;
    }
    
    Rectangle imRect = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
    


    if ((imRect = imRect.intersection(getBounds())).isEmpty()) {
      return;
    }
    

    areBuffersShared = false;
    

    int txMin = XToTileX(x);
    int tyMin = YToTileY(y);
    int txMax = XToTileX(x + width - 1);
    int tyMax = YToTileY(y + height - 1);
    

    for (int j = tyMin; j <= tyMax; j++) {
      for (int i = txMin; i <= txMax; i++) {
        WritableRaster t;
        if (((t = tiles[(i - minTileX)][(j - minTileY)]) != null) && (!isTileLocked(i, j)))
        {
          Rectangle tileRect = getTileRect(i, j);
          tileRect = tileRect.intersection(imRect);
          if (!tileRect.isEmpty()) {
            overlayPixels(t, im, tileRect);
          }
        }
      }
    }
    

    PlanarImage src = PlanarImage.wrapRenderedImage(im);
    if (getNumSources() == 0) {
      addSource(src);
    } else {
      setSource(src, 0);
    }
    srcROI = null;
    overlapBounds = imRect;
    

    properties.addProperties(src);
  }
  




















  public void set(RenderedImage im, ROI roi)
  {
    if (im == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if ((getNumSources() > 0) && (im == getSourceImage(0))) {
      return;
    }
    
    Rectangle imRect = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
    



    Rectangle overlap = imRect.intersection(roi.getBounds());
    if ((overlap.isEmpty()) || ((overlap = overlap.intersection(getBounds())).isEmpty()))
    {
      return;
    }
    

    areBuffersShared = false;
    

    int txMin = XToTileX(x);
    int tyMin = YToTileY(y);
    int txMax = XToTileX(x + width - 1);
    int tyMax = YToTileY(y + height - 1);
    
    Shape roiShape = roi.getAsShape();
    Area roiArea = null;
    if (roiShape != null) {
      roiArea = new Area(roiShape);
    }
    

    for (int j = tyMin; j <= tyMax; j++) {
      for (int i = txMin; i <= txMax; i++) {
        WritableRaster t;
        if (((t = tiles[(i - minTileX)][(j - minTileY)]) != null) && (!isTileLocked(i, j)))
        {
          Rectangle rect = getTileRect(i, j).intersection(overlap);
          if (!rect.isEmpty()) {
            if (roiShape != null) {
              Area a = new Area(rect);
              a.intersect(roiArea);
              
              if (!a.isEmpty()) {
                overlayPixels(t, im, a);
              }
            } else {
              int[][] bitmask = roi.getAsBitmask(x, y, width, height, (int[][])null);
              




              if ((bitmask != null) && (bitmask.length > 0)) {
                overlayPixels(t, im, rect, bitmask);
              }
            }
          }
        }
      }
    }
    

    PlanarImage src = PlanarImage.wrapRenderedImage(im);
    if (getNumSources() == 0) {
      addSource(src);
    } else {
      setSource(src, 0);
    }
    srcROI = roi;
    overlapBounds = overlap;
    

    properties.addProperties(src);
  }
  




  /**
   * @deprecated
   */
  public Graphics getGraphics()
  {
    return createGraphics();
  }
  





  public Graphics2D createGraphics()
  {
    int dataType = sampleModel.getDataType();
    if ((dataType != 0) && (dataType != 2) && (dataType != 1) && (dataType != 3))
    {


      throw new UnsupportedOperationException(JaiI18N.getString("TiledImage0"));
    }
    return new TiledImageGraphics(this);
  }
  

































  public TiledImage getSubImage(int x, int y, int w, int h, int[] bandSelect, ColorModel cm)
  {
    Rectangle subImageBounds = new Rectangle(x, y, w, h);
    if (subImageBounds.isEmpty()) {
      return null;
    }
    Rectangle overlap = subImageBounds.intersection(getBounds());
    if (overlap.isEmpty()) {
      return null;
    }
    

    SampleModel sm = bandSelect != null ? getSampleModel().createSubsetSampleModel(bandSelect) : getSampleModel();
    



    if ((cm == null) && ((bandSelect == null) || (bandSelect.length == getSampleModel().getNumBands())))
    {

      cm = getColorModel();
    }
    

    TiledImage subImage = new TiledImage(this, x, y, width, height, getTileGridXOffset(), getTileGridYOffset(), sm, cm);
    








    int[] subBandList = null;
    if (bandSelect != null) {
      if (bandList != null)
      {

        subBandList = new int[bandSelect.length];
        for (int band = 0; band < bandSelect.length; band++) {
          subBandList[band] = bandList[bandSelect[band]];
        }
      }
      else {
        subBandList = bandSelect;
      }
    }
    else {
      subBandList = bandList;
    }
    

    bandList = subBandList;
    
    return subImage;
  }
  
























  /**
   * @deprecated
   */
  public TiledImage getSubImage(int x, int y, int w, int h, int[] bandSelect)
  {
    SampleModel sm = bandSelect != null ? getSampleModel().createSubsetSampleModel(bandSelect) : getSampleModel();
    

    return getSubImage(x, y, w, h, bandSelect, createColorModel(sm));
  }
  













  public TiledImage getSubImage(int x, int y, int w, int h)
  {
    return getSubImage(x, y, w, h, null, null);
  }
  

















  public TiledImage getSubImage(int[] bandSelect, ColorModel cm)
  {
    if (bandSelect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return getSubImage(getMinX(), getMinY(), getWidth(), getHeight(), bandSelect, cm);
  }
  











  /**
   * @deprecated
   */
  public TiledImage getSubImage(int[] bandSelect)
  {
    if (bandSelect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return getSubImage(getMinX(), getMinY(), getWidth(), getHeight(), bandSelect);
  }
  







  private void createTile(int tileX, int tileY)
  {
    PlanarImage src = getNumSources() > 0 ? getSourceImage(0) : null;
    


    if ((src == null) && (parent != null)) {
      parent.createTile(tileX, tileY);
      return;
    }
    
    synchronized (tiles)
    {
      if (tiles[(tileX - minTileX)][(tileY - minTileY)] == null)
      {
        if (areBuffersShared) {
          Raster srcTile = src.getTile(tileX, tileY);
          if ((srcTile instanceof WritableRaster)) {
            tiles[(tileX - minTileX)][(tileY - minTileY)] = ((WritableRaster)srcTile);
          }
          else {
            Point location = new Point(srcTile.getMinX(), srcTile.getMinY());
            
            tiles[(tileX - minTileX)][(tileY - minTileY)] = Raster.createWritableRaster(sampleModel, srcTile.getDataBuffer(), location);
          }
          


          return;
        }
        

        tiles[(tileX - minTileX)][(tileY - minTileY)] = createWritableRaster(ancestorSampleModel, new Point(tileXToX(tileX), tileYToY(tileY)));
        


        WritableRaster tile = tiles[(tileX - minTileX)][(tileY - minTileY)];
        

        if (src != null)
        {
          Rectangle tileRect = getTileRect(tileX, tileY);
          

          Rectangle rect = overlapBounds.intersection(tileRect);
          

          if (rect.isEmpty()) {
            return;
          }
          

          if (srcROI != null)
          {
            Shape roiShape = srcROI.getAsShape();
            
            if (roiShape != null)
            {
              Area a = new Area(rect);
              a.intersect(new Area(roiShape));
              
              if (!a.isEmpty())
              {
                overlayPixels(tile, src, a);
              }
            } else {
              int[][] bitmask = srcROI.getAsBitmask(x, y, width, height, (int[][])null);
              



              overlayPixels(tile, src, rect, bitmask);

            }
            

          }
          else if (!rect.isEmpty()) {
            if ((bandList == null) && (rect.equals(tileRect)))
            {


              if (tileRect.equals(tile.getBounds())) {
                src.copyData(tile);
              } else {
                src.copyData(tile.createWritableChild(x, y, width, height, x, y, null));

              }
              

            }
            else
            {
              overlayPixels(tile, src, rect);
            }
          }
        }
      }
    }
  }
  








  public Raster getTile(int tileX, int tileY)
  {
    if ((tileX < minTileX) || (tileY < minTileY) || (tileX > getMaxTileX()) || (tileY > getMaxTileY()))
    {
      return null;
    }
    
    createTile(tileX, tileY);
    

    if (bandList == null) {
      return tiles[(tileX - minTileX)][(tileY - minTileY)];
    }
    

    Raster r = tiles[(tileX - minTileX)][(tileY - minTileY)];
    
    return r.createChild(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight(), r.getMinX(), r.getMinY(), bandList);
  }
  












  public WritableRaster getWritableTile(int tileX, int tileY)
  {
    if ((tileX < minTileX) || (tileY < minTileY) || (tileX > getMaxTileX()) || (tileY > getMaxTileY()))
    {
      return null;
    }
    
    if (isTileLocked(tileX, tileY)) {
      return null;
    }
    
    createTile(tileX, tileY);
    writers[(tileX - minTileX)][(tileY - minTileY)] += 1;
    
    if (writers[(tileX - minTileX)][(tileY - minTileY)] == 1) {
      numWritableTiles[0] += 1;
      
      Enumeration e = tileObservers.elements();
      while (e.hasMoreElements()) {
        TileObserver t = (TileObserver)e.nextElement();
        t.tileUpdate(this, tileX, tileY, true);
      }
    }
    

    if (bandList == null) {
      return tiles[(tileX - minTileX)][(tileY - minTileY)];
    }
    

    WritableRaster wr = tiles[(tileX - minTileX)][(tileY - minTileY)];
    
    return wr.createWritableChild(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight(), wr.getMinX(), wr.getMinY(), bandList);
  }
  










  public void releaseWritableTile(int tileX, int tileY)
  {
    if (isTileLocked(tileX, tileY)) {
      return;
    }
    
    writers[(tileX - minTileX)][(tileY - minTileY)] -= 1;
    
    if (writers[(tileX - minTileX)][(tileY - minTileY)] < 0) {
      throw new RuntimeException(JaiI18N.getString("TiledImage1"));
    }
    
    if (writers[(tileX - minTileX)][(tileY - minTileY)] == 0) {
      numWritableTiles[0] -= 1;
      
      Enumeration e = tileObservers.elements();
      while (e.hasMoreElements()) {
        TileObserver t = (TileObserver)e.nextElement();
        t.tileUpdate(this, tileX, tileY, false);
      }
    }
  }
  









  protected boolean lockTile(int tileX, int tileY)
  {
    if ((tileX < minTileX) || (tileY < minTileY) || (tileX > getMaxTileX()) || (tileY > getMaxTileY()))
    {
      return false;
    }
    

    if (isTileWritable(tileX, tileY)) {
      return false;
    }
    

    createTile(tileX, tileY);
    

    writers[(tileX - minTileX)][(tileY - minTileY)] = -1;
    
    return true;
  }
  






  protected boolean isTileLocked(int tileX, int tileY)
  {
    return writers[(tileX - minTileX)][(tileY - minTileY)] < 0;
  }
  










  public void setData(Raster r)
  {
    Rectangle rBounds = r.getBounds();
    if ((rBounds = rBounds.intersection(getBounds())).isEmpty()) {
      return;
    }
    

    int txMin = XToTileX(x);
    int tyMin = YToTileY(y);
    int txMax = XToTileX(x + width - 1);
    int tyMax = YToTileY(y + height - 1);
    
    for (int ty = tyMin; ty <= tyMax; ty++) {
      for (int tx = txMin; tx <= txMax; tx++) {
        WritableRaster wr = getWritableTile(tx, ty);
        if (wr != null)
        {




          Rectangle tileRect = getTileRect(tx, ty);
          if (tileRect.contains(rBounds)) {
            JDKWorkarounds.setRect(wr, r, 0, 0);
          } else {
            Rectangle xsect = rBounds.intersection(tileRect);
            Raster rChild = r.createChild(x, y, width, height, x, y, null);
            


            WritableRaster wChild = wr.createWritableChild(x, y, width, height, x, y, null);
            


            JDKWorkarounds.setRect(wChild, rChild, 0, 0);
          }
          releaseWritableTile(tx, ty);
        }
      }
    }
  }
  












  public void setData(Raster r, ROI roi)
  {
    Rectangle rBounds = r.getBounds();
    if (((rBounds = rBounds.intersection(getBounds())).isEmpty()) || ((rBounds = rBounds.intersection(roi.getBounds())).isEmpty()))
    {
      return;
    }
    

    LinkedList rectList = roi.getAsRectangleList(x, y, width, height);
    



    int txMin = XToTileX(x);
    int tyMin = YToTileY(y);
    int txMax = XToTileX(x + width - 1);
    int tyMax = YToTileY(y + height - 1);
    
    int numRects = rectList.size();
    
    for (int ty = tyMin; ty <= tyMax; ty++) {
      for (int tx = txMin; tx <= txMax; tx++) {
        WritableRaster wr = getWritableTile(tx, ty);
        if (wr != null) {
          Rectangle tileRect = getTileRect(tx, ty);
          for (int i = 0; i < numRects; i++) {
            Rectangle rect = (Rectangle)rectList.get(i);
            rect = rect.intersection(tileRect);
            

            if (!rect.isEmpty()) {
              Raster rChild = r.createChild(x, y, width, height, x, y, null);
              


              WritableRaster wChild = wr.createWritableChild(x, y, width, height, x, y, null);
              


              JDKWorkarounds.setRect(wChild, rChild, 0, 0);
            }
          }
          releaseWritableTile(tx, ty);
        }
      }
    }
  }
  

















  public void addTileObserver(TileObserver observer)
  {
    tileObservers.addElement(observer);
  }
  








  public void removeTileObserver(TileObserver observer)
  {
    tileObservers.removeElement(observer);
  }
  






  public Point[] getWritableTileIndices()
  {
    Point[] indices = null;
    
    if (hasTileWriters()) {
      Vector v = new Vector();
      int count = 0;
      
      for (int j = 0; j < tilesY; j++) {
        for (int i = 0; i < tilesX; i++) {
          if (writers[i][j] > 0) {
            v.addElement(new Point(i + minTileX, j + minTileY));
            count++;
          }
        }
      }
      
      indices = new Point[count];
      for (int k = 0; k < count; k++) {
        indices[k] = ((Point)v.elementAt(k));
      }
    }
    
    return indices;
  }
  






  public boolean hasTileWriters()
  {
    return numWritableTiles[0] > 0;
  }
  





  public boolean isTileWritable(int tileX, int tileY)
  {
    return writers[(tileX - minTileX)][(tileY - minTileY)] > 0;
  }
  








  public void clearTiles()
  {
    if (hasTileWriters()) {
      throw new IllegalStateException(JaiI18N.getString("TiledImage2"));
    }
    tiles = ((WritableRaster[][])null);
  }
  



























  public void setSample(int x, int y, int b, int s)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    WritableRaster t = getWritableTile(tileX, tileY);
    if (t != null) {
      t.setSample(x, y, b, s);
    }
    releaseWritableTile(tileX, tileY);
  }
  






  public int getSample(int x, int y, int b)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    Raster t = getTile(tileX, tileY);
    return t.getSample(x, y, b);
  }
  







  public void setSample(int x, int y, int b, float s)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    WritableRaster t = getWritableTile(tileX, tileY);
    if (t != null) {
      t.setSample(x, y, b, s);
    }
    releaseWritableTile(tileX, tileY);
  }
  






  public float getSampleFloat(int x, int y, int b)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    Raster t = getTile(tileX, tileY);
    return t.getSampleFloat(x, y, b);
  }
  







  public void setSample(int x, int y, int b, double s)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    WritableRaster t = getWritableTile(tileX, tileY);
    if (t != null) {
      t.setSample(x, y, b, s);
    }
    releaseWritableTile(tileX, tileY);
  }
  






  public double getSampleDouble(int x, int y, int b)
  {
    int tileX = XToTileX(x);
    int tileY = YToTileY(y);
    Raster t = getTile(tileX, tileY);
    return t.getSampleDouble(x, y, b);
  }
  






















  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    PlanarImage src = getNumSources() > 0 ? getSourceImage(0) : null;
    
    if ((evt.getSource() == src) && (((evt instanceof RenderingChangeEvent)) || (((evt instanceof PropertyChangeEventJAI)) && (evt.getPropertyName().equalsIgnoreCase("InvalidRegion")))))
    {




      Shape invalidRegion = (evt instanceof RenderingChangeEvent) ? ((RenderingChangeEvent)evt).getInvalidRegion() : (Shape)evt.getNewValue();
      




      Rectangle invalidBounds = invalidRegion.getBounds();
      if (invalidBounds.isEmpty()) {
        return;
      }
      

      Area invalidArea = new Area(invalidRegion);
      if (srcROI != null) {
        Shape roiShape = srcROI.getAsShape();
        if (roiShape != null) {
          invalidArea.intersect(new Area(roiShape));
        } else {
          LinkedList rectList = srcROI.getAsRectangleList(x, y, width, height);
          



          Iterator it = rectList.iterator();
          while ((it.hasNext()) && (!invalidArea.isEmpty())) {
            invalidArea.intersect(new Area((Rectangle)it.next()));
          }
        }
      }
      

      if (invalidArea.isEmpty()) {
        return;
      }
      

      Point[] tileIndices = getTileIndices(invalidArea.getBounds());
      int numIndices = tileIndices.length;
      

      for (int i = 0; i < numIndices; i++) {
        int tx = x;
        int ty = y;
        Raster tile = tiles[tx][ty];
        if ((tile != null) && (invalidArea.intersects(tile.getBounds())))
        {
          tiles[tx][ty] = null;
        }
      }
      
      if (eventManager.hasListeners("InvalidRegion"))
      {
        Shape oldInvalidRegion = new Rectangle();
        


        if (srcROI != null) {
          Area oldInvalidArea = new Area(getBounds());
          Shape roiShape = srcROI.getAsShape();
          if (roiShape != null) {
            oldInvalidArea.subtract(new Area(roiShape));
          } else {
            Rectangle oldInvalidBounds = oldInvalidArea.getBounds();
            
            LinkedList rectList = srcROI.getAsRectangleList(x, y, width, height);
            



            Iterator it = rectList.iterator();
            while ((it.hasNext()) && (!oldInvalidArea.isEmpty())) {
              oldInvalidArea.subtract(new Area((Rectangle)it.next()));
            }
          }
          oldInvalidRegion = oldInvalidArea;
        }
        

        PropertyChangeEventJAI irEvt = new PropertyChangeEventJAI(this, "InvalidRegion", oldInvalidRegion, invalidRegion);
        




        eventManager.firePropertyChange(irEvt);
        

        Vector sinks = getSinks();
        if (sinks != null) {
          int numSinks = sinks.size();
          for (int i = 0; i < numSinks; i++) {
            Object sink = sinks.get(i);
            if ((sink instanceof PropertyChangeListener)) {
              ((PropertyChangeListener)sink).propertyChange(irEvt);
            }
          }
        }
      }
    }
  }
}
