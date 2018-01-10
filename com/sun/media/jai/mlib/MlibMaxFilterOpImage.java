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
import javax.media.jai.operator.MaxFilterDescriptor;
import javax.media.jai.operator.MaxFilterShape;







































final class MlibMaxFilterOpImage
  extends AreaOpImage
{
  protected int maskType;
  protected int maskSize;
  
  public MlibMaxFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MaxFilterShape maskType, int maskSize)
  {
    super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
    







    this.maskType = mapToMlibMaskType(maskType);
    this.maskSize = maskSize;
  }
  
  private static int mapToMlibMaskType(MaxFilterShape maskType) {
    if (maskType.equals(MaxFilterDescriptor.MAX_MASK_SQUARE))
      return 0;
    if (maskType.equals(MaxFilterDescriptor.MAX_MASK_PLUS))
      return 1;
    if (maskType.equals(MaxFilterDescriptor.MAX_MASK_X))
      return 2;
    if (maskType.equals(MaxFilterDescriptor.MAX_MASK_SQUARE_SEPARABLE)) {
      return 3;
    }
    throw new RuntimeException(JaiI18N.getString("MaxFilterOpImage0"));
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    
    int numBands = getSampleModel().getNumBands();
    

    int cmask = (1 << numBands) - 1;
    mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
    mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
    
    for (int i = 0; i < dstML.length; i++) {
      switch (dstAccessor.getDataType()) {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        if (maskSize == 3)
        {
          Image.MaxFilter3x3(dstML[i], srcML[i]);
        } else if (maskSize == 5)
        {
          Image.MaxFilter5x5(dstML[i], srcML[i]);
        } else if (maskSize == 7)
        {
          Image.MaxFilter7x7(dstML[i], srcML[i]);
        }
        
        break;
      case 4: 
      case 5: 
        if (maskSize == 3)
        {
          Image.MaxFilter3x3_Fp(dstML[i], srcML[i]);
        } else if (maskSize == 5)
        {
          Image.MaxFilter5x5_Fp(dstML[i], srcML[i]);
        } else if (maskSize == 7)
        {
          Image.MaxFilter7x7_Fp(dstML[i], srcML[i]);
        }
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
