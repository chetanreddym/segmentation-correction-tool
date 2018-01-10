package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;



























































public abstract class WarpOpImage
  extends GeometricOpImage
{
  protected Warp warp;
  
  private static ImageLayout getLayout(ImageLayout layout, RenderedImage source, Warp warp)
  {
    if ((layout != null) && (layout.isValid(15)))
    {



      return layout;
    }
    

    Rectangle sourceBounds = new Rectangle(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
    



    Rectangle destBounds = warp.mapSourceRect(sourceBounds);
    

    if (destBounds == null) {
      Point[] srcPts = { new Point(x, y), new Point(x + width, y), new Point(x, y + height), new Point(x + width, y + height) };
      








      boolean verticesMapped = true;
      
      double xMin = Double.MAX_VALUE;
      double xMax = -1.7976931348623157E308D;
      double yMin = Double.MAX_VALUE;
      double yMax = -1.7976931348623157E308D;
      
      for (int i = 0; i < 4; i++) {
        Point2D destPt = warp.mapSourcePoint(srcPts[i]);
        if (destPt == null) {
          verticesMapped = false;
          break;
        }
        
        double x = destPt.getX();
        double y = destPt.getY();
        if (x < xMin) {
          xMin = x;
        }
        if (x > xMax) {
          xMax = x;
        }
        if (y < yMin) {
          yMin = y;
        }
        if (y > yMax) {
          yMax = y;
        }
      }
      

      if (verticesMapped) {
        destBounds = new Rectangle();
        x = ((int)Math.floor(xMin));
        y = ((int)Math.floor(yMin));
        width = ((int)Math.ceil(xMax - x));
        height = ((int)Math.ceil(yMax - y));
      }
    }
    




    if ((destBounds == null) && (!(warp instanceof WarpAffine))) {
      Point[] destPts = { new Point(x, y), new Point(x + width, y), new Point(x, y + height), new Point(x + width, y + height) };
      








      float[] sourceCoords = new float[8];
      float[] destCoords = new float[8];
      int offset = 0;
      
      for (int i = 0; i < 4; i++) {
        Point2D dstPt = destPts[i];
        Point2D srcPt = warp.mapDestPoint(destPts[i]);
        destCoords[offset] = ((float)dstPt.getX());
        destCoords[(offset + 1)] = ((float)dstPt.getY());
        sourceCoords[offset] = ((float)srcPt.getX());
        sourceCoords[(offset + 1)] = ((float)srcPt.getY());
        offset += 2;
      }
      

      WarpAffine wa = (WarpAffine)WarpPolynomial.createWarp(sourceCoords, 0, destCoords, 0, 8, 1.0F, 1.0F, 1.0F, 1.0F, 1);
      






      destBounds = wa.mapSourceRect(sourceBounds);
    }
    


    if (destBounds != null) {
      if (layout == null) {
        layout = new ImageLayout(x, y, width, height);
      }
      else {
        layout = (ImageLayout)layout.clone();
        layout.setMinX(x);
        layout.setMinY(y);
        layout.setWidth(width);
        layout.setHeight(height);
      }
    }
    
    return layout;
  }
  






































  public WarpOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, Warp warp)
  {
    this(source, layout, configuration, cobbleSources, extender, interp, warp, null);
  }
  




















































  public WarpOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, Warp warp, double[] backgroundValues)
  {
    super(vectorize(source), getLayout(layout, source, warp), configuration, cobbleSources, extender, interp, backgroundValues);
    






    if (warp == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.warp = warp;
    
    if ((cobbleSources) && (extender == null))
    {

      int l = interp == null ? 0 : interp.getLeftPadding();
      int r = interp == null ? 0 : interp.getRightPadding();
      int t = interp == null ? 0 : interp.getTopPadding();
      int b = interp == null ? 0 : interp.getBottomPadding();
      
      int x = getMinX() + l;
      int y = getMinY() + t;
      int w = Math.max(getWidth() - l - r, 0);
      int h = Math.max(getHeight() - t - b, 0);
      
      computableBounds = new Rectangle(x, y, w, h);
    }
    else
    {
      computableBounds = getBounds();
    }
  }
  



  /**
   * @deprecated
   */
  public int getLeftPadding()
  {
    return interp == null ? 0 : interp.getLeftPadding();
  }
  



  /**
   * @deprecated
   */
  public int getRightPadding()
  {
    return interp == null ? 0 : interp.getRightPadding();
  }
  



  /**
   * @deprecated
   */
  public int getTopPadding()
  {
    return interp == null ? 0 : interp.getTopPadding();
  }
  



  /**
   * @deprecated
   */
  public int getBottomPadding()
  {
    return interp == null ? 0 : interp.getBottomPadding();
  }
  





















  public Point2D mapDestPoint(Point2D destPt, int sourceIndex)
  {
    if (destPt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (sourceIndex != 0) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    return warp.mapDestPoint(destPt);
  }
  





















  public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex)
  {
    if (sourcePt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (sourceIndex != 0) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    return warp.mapSourcePoint(sourcePt);
  }
  




















  protected Rectangle forwardMapRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    return warp.mapSourceRect(sourceRect);
  }
  


















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (sourceIndex != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    Rectangle wrect = warp.mapDestRect(destRect);
    
    return wrect == null ? getSource(0).getBounds() : wrect;
  }
  





















  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    

    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    Rectangle destRect = new Rectangle(x, y, tileWidth, tileHeight).intersection(computableBounds);
    

    if (destRect.isEmpty()) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect, backgroundValues);
      }
      return dest;
    }
    
    PlanarImage source = getSource(0);
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    if (!srcRect.intersects(source.getBounds())) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect, backgroundValues);
      }
      return dest;
    }
    

    if (cobbleSources) {
      Raster[] srcs = new Raster[1];
      srcs[0] = (extender != null ? source.getExtendedData(srcRect, extender) : source.getData(srcRect));
      



      computeRect(srcs, dest, destRect);
      

      if (source.overlapsMultipleTiles(srcRect)) {
        recycleTile(srcs[0]);
      }
    } else {
      PlanarImage[] srcs = { source };
      computeRect(srcs, dest, destRect);
    }
    
    return dest;
  }
}
