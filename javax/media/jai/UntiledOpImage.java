package javax.media.jai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;











































public abstract class UntiledOpImage
  extends OpImage
{
  private static ImageLayout layoutHelper(ImageLayout layout, Vector sources)
  {
    if (sources.size() < 1) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic5"));
    }
    
    RenderedImage source = (RenderedImage)sources.get(0);
    
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    



    il.setTileGridXOffset(il.getMinX(source));
    il.setTileGridYOffset(il.getMinY(source));
    il.setTileWidth(il.getWidth(source));
    il.setTileHeight(il.getHeight(source));
    
    return il;
  }
  



































  public UntiledOpImage(Vector sources, Map configuration, ImageLayout layout)
  {
    super(checkSourceVector(sources, true), layoutHelper(layout, sources), configuration, true);
  }
  































  public UntiledOpImage(RenderedImage source, Map configuration, ImageLayout layout)
  {
    super(vectorize(source), layoutHelper(layout, vectorize(source)), configuration, true);
  }
  











  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    return getBounds();
  }
  










  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    
    return getSource(sourceIndex).getBounds();
  }
  








  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(getMinX(), getMinY());
    WritableRaster dest = createWritableRaster(sampleModel, org);
    



    Rectangle destRect = getBounds();
    

    int numSources = getNumSources();
    Raster[] rasterSources = new Raster[numSources];
    for (int i = 0; i < numSources; i++) {
      PlanarImage source = getSource(i);
      Rectangle srcRect = mapDestRect(destRect, i);
      rasterSources[i] = source.getData(srcRect);
    }
    

    computeImage(rasterSources, dest, destRect);
    
    for (int i = 0; i < numSources; i++) {
      Raster sourceData = rasterSources[i];
      if (sourceData != null) {
        PlanarImage source = getSourceImage(i);
        

        if (source.overlapsMultipleTiles(sourceData.getBounds())) {
          recycleTile(sourceData);
        }
      }
    }
    
    return dest;
  }
  











  protected abstract void computeImage(Raster[] paramArrayOfRaster, WritableRaster paramWritableRaster, Rectangle paramRectangle);
  










  public Point[] getTileDependencies(int tileX, int tileY, int sourceIndex)
  {
    PlanarImage source = getSource(sourceIndex);
    
    int minTileX = source.getMinTileX();
    int minTileY = source.getMinTileY();
    int maxTileX = minTileX + source.getNumXTiles() - 1;
    int maxTileY = minTileY + source.getNumYTiles() - 1;
    
    Point[] tileDependencies = new Point[(maxTileX - minTileX + 1) * (maxTileY - minTileY + 1)];
    

    int count = 0;
    for (int ty = minTileY; ty <= maxTileY; ty++) {
      for (int tx = minTileX; tx <= maxTileX; tx++) {
        tileDependencies[(count++)] = new Point(tx, ty);
      }
    }
    
    return tileDependencies;
  }
}
