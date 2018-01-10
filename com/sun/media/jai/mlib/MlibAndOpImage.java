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




























final class MlibAndOpImage
  extends PointOpImage
{
  public MlibAndOpImage(RenderedImage source1, RenderedImage source2, Map config, ImageLayout layout)
  {
    super(source1, source2, layout, config, true);
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor1 = new MediaLibAccessor(sources[0], destRect, formatTag);
    
    MediaLibAccessor srcAccessor2 = new MediaLibAccessor(sources[1], destRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    

    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML1 = srcAccessor1.getMediaLibImages();
      mediaLibImage[] srcML2 = srcAccessor2.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.And(dstML[i], srcML1[i], srcML2[i]);
      }
      break;
    default: 
      String className = getClass().getName();
      throw new RuntimeException(className + JaiI18N.getString("Generic2"));
    }
    
    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
