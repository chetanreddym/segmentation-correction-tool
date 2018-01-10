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
















final class MlibRescaleOpImage
  extends PointOpImage
{
  private double[] constants;
  private double[] offsets;
  
  private static ImageLayout layoutHelper(ImageLayout layout)
  {
    if (layout == null) {
      return null;
    }
    return (ImageLayout)layout.clone();
  }
  













  public MlibRescaleOpImage(RenderedImage source, Map config, ImageLayout layout, double[] constants, double[] offsets)
  {
    super(source, layoutHelper(layout), config, true);
    int numBands = getSampleModel().getNumBands();
    this.constants = MlibUtils.initConstants(constants, numBands);
    this.offsets = MlibUtils.initConstants(offsets, numBands);
    
    permitInPlaceOperation();
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    

    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    
    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (int i = 0; i < dstML.length; i++) {
        double[] mlconstants = dstAccessor.getDoubleParameters(i, constants);
        double[] mloffsets = dstAccessor.getDoubleParameters(i, offsets);
        Image.Scale2(dstML[i], srcML[i], mlconstants, mloffsets);
      }
      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < dstML.length; i++) {
        double[] mlconstants = dstAccessor.getDoubleParameters(i, constants);
        double[] mloffsets = dstAccessor.getDoubleParameters(i, offsets);
        Image.Scale_Fp(dstML[i], srcML[i], mlconstants, mloffsets);
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
