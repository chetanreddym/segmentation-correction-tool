package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;

























final class MlibXorConstOpImage
  extends PointOpImage
{
  int[] constants;
  
  public MlibXorConstOpImage(RenderedImage source, Map config, ImageLayout layout, int[] constants)
  {
    super(source, layout, config, true);
    this.constants = MlibUtils.initConstants(constants, getSampleModel().getNumBands());
    

    permitInPlaceOperation();
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    

    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, destRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    

    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        int[] mlconstants = dstAccessor.getIntParameters(i, constants);
        Image.ConstXor(dstML[i], srcML[i], mlconstants);
      }
      break;
    default: 
      String className = getClass().getName();
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
