package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Vector;














































































public abstract class StatisticsOpImage
  extends OpImage
{
  protected ROI roi;
  protected int xStart;
  protected int yStart;
  protected int xPeriod;
  protected int yPeriod;
  private boolean checkForSkippedTiles;
  
  public StatisticsOpImage(RenderedImage source, ROI roi, int xStart, int yStart, int xPeriod, int yPeriod)
  {
    super(vectorize(source), new ImageLayout(source), null, false);
    



    this.roi = (roi == null ? new ROIShape(getSource(0).getBounds()) : roi);
    
    this.xStart = xStart;
    this.yStart = yStart;
    this.xPeriod = xPeriod;
    this.yPeriod = yPeriod;
    
    checkForSkippedTiles = ((xPeriod > tileWidth) || (yPeriod > tileHeight));
  }
  







  public boolean computesUniqueTiles()
  {
    return false;
  }
  














  public Raster getTile(int tileX, int tileY)
  {
    return getSource(0).getTile(tileX, tileY);
  }
  










  public Raster computeTile(int tileX, int tileY)
  {
    return getSource(0).getTile(tileX, tileY);
  }
  








  public Raster[] getTiles(Point[] tileIndices)
  {
    if (tileIndices == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return getSource(0).getTiles(tileIndices);
  }
  













  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    return new Rectangle(sourceRect);
  }
  













  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    return new Rectangle(destRect);
  }
  







  public Object getProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    Object stats = super.getProperty(name);
    
    if (stats.equals(Image.UndefinedProperty))
    {
      synchronized (this) {
        stats = createStatistics(name);
        
        if (!stats.equals(Image.UndefinedProperty)) {
          PlanarImage source = getSource(0);
          

          int minTileX = source.getMinTileX();
          int maxTileX = source.getMaxTileX();
          int minTileY = source.getMinTileY();
          int maxTileY = source.getMaxTileY();
          
          for (int y = minTileY; y <= maxTileY; y++) {
            for (int x = minTileX; x <= maxTileX; x++)
            {


              Rectangle tileRect = getTileRect(x, y);
              

              if (roi.intersects(tileRect))
              {


                if ((checkForSkippedTiles) && (x >= xStart) && (y >= yStart))
                {


                  int offsetX = (xPeriod - (x - xStart) % xPeriod) % xPeriod;
                  


                  int offsetY = (yPeriod - (y - yStart) % yPeriod) % yPeriod;
                  





                  if ((offsetX >= width) || (offsetY >= height)) {}


                }
                else
                {

                  accumulateStatistics(name, source.getData(tileRect), stats);
                }
              }
            }
          }
          


          setProperty(name, stats);
        }
      }
    }
    
    return stats;
  }
  






  public String[] getPropertyNames()
  {
    String[] statsNames = getStatisticsNames();
    String[] superNames = super.getPropertyNames();
    

    if (superNames == null) {
      return statsNames;
    }
    

    Vector extraNames = new Vector();
    for (int i = 0; i < statsNames.length; i++) {
      String prefix = statsNames[i];
      String[] names = PropertyUtil.getPropertyNames(superNames, prefix);
      if (names != null) {
        for (int j = 0; j < names.length; j++) {
          if (names[j].equalsIgnoreCase(prefix)) {
            extraNames.add(prefix);
          }
        }
      }
    }
    

    if (extraNames.size() == 0) {
      return superNames;
    }
    

    String[] propNames = new String[superNames.length + extraNames.size()];
    System.arraycopy(superNames, 0, propNames, 0, superNames.length);
    int offset = superNames.length;
    for (int i = 0; i < extraNames.size(); i++) {
      propNames[(offset++)] = ((String)extraNames.get(i));
    }
    

    return propNames;
  }
  
  protected abstract String[] getStatisticsNames();
  
  protected abstract Object createStatistics(String paramString);
  
  protected abstract void accumulateStatistics(String paramString, Raster paramRaster, Object paramObject);
}
