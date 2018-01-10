package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.AreaOpImage;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;








































final class MlibSobelOpImage
  extends AreaOpImage
{
  public MlibSobelOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, KernelJAI kernel)
  {
    super(source, layout, config, true, extender, kernel.getLeftPadding(), kernel.getRightPadding(), kernel.getTopPadding(), kernel.getBottomPadding());
  }
  

















  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    
    int numBands = getSampleModel().getNumBands();
    
    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    for (int i = 0; i < dstML.length; i++) {
      switch (dstAccessor.getDataType()) {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        Image.Sobel(dstML[i], srcML[i], (1 << numBands) - 1, 0);
        


        break;
      
      case 4: 
      case 5: 
        Image.Sobel_Fp(dstML[i], srcML[i], (1 << numBands) - 1, 0);
        


        break;
      
      default: 
        String className = getClass().getName();
        throw new RuntimeException(JaiI18N.getString("Generic2"));
      }
      
    }
    if (dstAccessor.isDataCopy()) {
      dstAccessor.copyDataToRaster();
    }
  }
}
