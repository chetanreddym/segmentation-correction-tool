package com.sun.media.jai.opimage;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.OpImage;











































final class PeriodicShiftOpImage
  extends OpImage
{
  private int[] xTrans;
  private int[] yTrans;
  private TranslateIntOpImage[] images;
  private Rectangle[] bounds;
  
  public PeriodicShiftOpImage(RenderedImage source, Map config, ImageLayout layout, int shiftX, int shiftY)
  {
    super(vectorize(source), layout == null ? new ImageLayout() : (ImageLayout)layout.clone(), config, false);
    




    xTrans = new int[] { -shiftX, -shiftX, width - shiftX, width - shiftX };
    
    yTrans = new int[] { -shiftY, height - shiftY, -shiftY, height - shiftY };
    


    images = new TranslateIntOpImage[4];
    for (int i = 0; i < 4; i++) {
      images[i] = new TranslateIntOpImage(source, null, xTrans[i], yTrans[i]);
    }
    


    Rectangle destBounds = getBounds();
    bounds = new Rectangle[4];
    for (int i = 0; i < 4; i++) {
      bounds[i] = destBounds.intersection(images[i].getBounds());
    }
  }
  







  public Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster dest = createWritableRaster(sampleModel, org);
    

    Rectangle rect = new Rectangle(x, y, sampleModel.getWidth(), sampleModel.getHeight());
    

    Rectangle destRect = rect.intersection(getBounds());
    

    for (int i = 0; i < 4; i++)
    {
      Rectangle overlap = destRect.intersection(bounds[i]);
      

      if (!overlap.isEmpty())
      {
        JDKWorkarounds.setRect(dest, images[i].getData(overlap));
      }
    }
    
    return dest;
  }
  














  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    if (sourceRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("PeriodicShiftOpImage0"));
    }
    
    Rectangle destRect = null;
    for (int i = 0; i < 4; i++) {
      Rectangle srcRect = sourceRect;
      srcRect.translate(xTrans[i], yTrans[i]);
      Rectangle overlap = srcRect.intersection(getBounds());
      if (!overlap.isEmpty()) {
        destRect = destRect == null ? overlap : destRect.union(overlap);
      }
    }
    

    return destRect;
  }
  













  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    if (destRect == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if ((sourceIndex < 0) || (sourceIndex >= getNumSources())) {
      throw new IllegalArgumentException(JaiI18N.getString("PeriodicShiftOpImage0"));
    }
    
    Rectangle sourceRect = null;
    for (int i = 0; i < 4; i++) {
      Rectangle overlap = destRect.intersection(bounds[i]);
      if (!overlap.isEmpty()) {
        overlap.translate(-xTrans[i], -yTrans[i]);
        sourceRect = sourceRect == null ? overlap : sourceRect.union(overlap);
      }
    }
    

    return sourceRect;
  }
}
