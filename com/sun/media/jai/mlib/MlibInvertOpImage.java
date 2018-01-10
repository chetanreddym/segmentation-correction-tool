package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;






















final class MlibInvertOpImage
  extends PointOpImage
{
  public MlibInvertOpImage(RenderedImage source, Map config, ImageLayout layout)
  {
    super(source, layout, config, true);
    
    permitInPlaceOperation();
  }
  





  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcMA = new MediaLibAccessor(sources[0], destRect, formatTag);
    
    MediaLibAccessor dstMA = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcMLI = srcMA.getMediaLibImages();
    mediaLibImage[] dstMLI = dstMA.getMediaLibImages();
    
    switch (dstMA.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (int i = 0; i < dstMLI.length; i++) {
        Image.Invert(dstMLI[i], srcMLI[i]);
      }
      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < dstMLI.length; i++) {
        Image.Invert_Fp(dstMLI[i], srcMLI[i]);
      }
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    if (dstMA.isDataCopy()) {
      dstMA.clampDataArrays();
      dstMA.copyDataToRaster();
    }
  }
}
