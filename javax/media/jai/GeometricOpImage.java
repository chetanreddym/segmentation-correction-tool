package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;






























































public abstract class GeometricOpImage
  extends OpImage
{
  protected Interpolation interp;
  protected BorderExtender extender = null;
  













  protected Rectangle computableBounds;
  













  protected boolean setBackground;
  













  protected double[] backgroundValues;
  













  protected int[] intBackgroundValues;
  













  public GeometricOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp)
  {
    this(sources, layout, configuration, cobbleSources, extender, interp, null);
  }
  


  private static Map configHelper(Map configuration)
  {
    Map config;
    

    Map config;
    

    if (configuration == null) {
      config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
    }
    else
    {
      config = configuration;
      


      if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL))
      {
        RenderingHints hints = new RenderingHints(null);
        
        hints.putAll(configuration);
        config = hints;
        config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.TRUE);
      }
    }
    
    return config;
  }
  






































































  public GeometricOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources, BorderExtender extender, Interpolation interp, double[] backgroundValues)
  {
    super(sources, layout, configHelper(configuration), cobbleSources);
    



    this.extender = extender;
    this.interp = (interp != null ? interp : new InterpolationNearest());
    
    if (backgroundValues == null) {
      backgroundValues = new double[] { 0.0D };
    }
    setBackground = false;
    for (int i = 0; i < backgroundValues.length; i++) {
      if (backgroundValues[i] != 0.0D)
        setBackground = true;
    }
    this.backgroundValues = backgroundValues;
    int numBands = getSampleModel().getNumBands();
    if (backgroundValues.length < numBands) {
      this.backgroundValues = new double[numBands];
      for (int i = 0; i < numBands; i++) {
        this.backgroundValues[i] = backgroundValues[0];
      }
    }
    if (sampleModel.getDataType() <= 3) {
      int length = this.backgroundValues.length;
      intBackgroundValues = new int[length];
      for (int i = 0; i < length; i++) {
        intBackgroundValues[i] = ((int)this.backgroundValues[i]);
      }
    }
    
    computableBounds = getBounds();
  }
  





  public Interpolation getInterpolation()
  {
    return interp;
  }
  






  public BorderExtender getBorderExtender()
  {
    return extender;
  }
  



































  public Point2D mapDestPoint(Point2D destPt, int sourceIndex)
  {
    if (destPt == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
    }
    
    Rectangle destRect = new Rectangle((int)destPt.getX(), (int)destPt.getY(), 1, 1);
    


    Rectangle sourceRect = backwardMapRect(destRect, sourceIndex);
    
    if (sourceRect == null) {
      return null;
    }
    
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
    

    Rectangle destRect = forwardMapRect(sourceRect, sourceIndex);
    
    if (destRect == null) {
      return null;
    }
    
    Point2D pt = (Point2D)sourcePt.clone();
    pt.setLocation(x + (width - 1.0D) / 2.0D, y + (height - 1.0D) / 2.0D);
    

    return pt;
  }
  





























  protected abstract Rectangle forwardMapRect(Rectangle paramRectangle, int paramInt);
  





























  protected abstract Rectangle backwardMapRect(Rectangle paramRectangle, int paramInt);
  





























  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    int lpad = interp.getLeftPadding();
    int tpad = interp.getTopPadding();
    

    Rectangle srcRect = (Rectangle)sourceRect.clone();
    x += lpad;
    y += tpad;
    width -= lpad + interp.getRightPadding();
    height -= tpad + interp.getBottomPadding();
    

    Rectangle destRect = forwardMapRect(srcRect, sourceIndex);
    

    return destRect == null ? getBounds() : destRect;
  }
  























  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    Rectangle sourceRect = backwardMapRect(destRect, sourceIndex);
    if (sourceRect == null) {
      return getSource(sourceIndex).getBounds();
    }
    

    int lpad = interp.getLeftPadding();
    int tpad = interp.getTopPadding();
    

    return new Rectangle(x - lpad, y - tpad, width + lpad + interp.getRightPadding(), height + tpad + interp.getBottomPadding());
  }
  



























  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    

    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    Rectangle destRect = getTileRect(tileX, tileY).intersection(getBounds());
    

    if (destRect.isEmpty()) {
      if (setBackground) {
        ImageUtil.fillBackground(dest, destRect, backgroundValues);
      }
      
      return dest;
    }
    
    int numSources = getNumSources();
    if (cobbleSources) {
      Raster[] rasterSources = new Raster[numSources];
      

      for (int i = 0; i < numSources; i++) {
        PlanarImage source = getSource(i);
        
        Rectangle srcBounds = source.getBounds();
        Rectangle srcRect = mapDestRect(destRect, i);
        if (srcRect == null)
        {
          srcRect = srcBounds;
        } else {
          if ((extender == null) && (!srcBounds.contains(srcRect)))
          {
            srcRect = srcBounds.intersection(srcRect);
          }
          if (!srcRect.intersects(srcBounds))
          {
            if (setBackground) {
              ImageUtil.fillBackground(dest, destRect, backgroundValues);
            }
            
            return dest;
          }
        }
        
        rasterSources[i] = (extender != null ? source.getExtendedData(srcRect, extender) : source.getData(srcRect));
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
}
