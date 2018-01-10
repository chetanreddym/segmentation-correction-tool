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
import javax.media.jai.operator.MinFilterDescriptor;
import javax.media.jai.operator.MinFilterShape;






































final class MlibMinFilterOpImage
  extends AreaOpImage
{
  protected int maskType;
  protected int maskSize;
  
  public MlibMinFilterOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, MinFilterShape maskType, int maskSize)
  {
    super(source, layout, config, true, extender, (maskSize - 1) / 2, (maskSize - 1) / 2, maskSize / 2, maskSize / 2);
    








    this.maskSize = maskSize;
  }
  
  private static int mapToMlibMaskType(MinFilterShape maskType) {
    if (maskType.equals(MinFilterDescriptor.MIN_MASK_SQUARE))
      return 0;
    if (maskType.equals(MinFilterDescriptor.MIN_MASK_PLUS))
      return 1;
    if (maskType.equals(MinFilterDescriptor.MIN_MASK_X))
      return 2;
    if (maskType.equals(MinFilterDescriptor.MIN_MASK_SQUARE_SEPARABLE))
    {
      return 3;
    }
    throw new RuntimeException(JaiI18N.getString("MinFilterOpImage0"));
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
          Image.MinFilter3x3(dstML[i], srcML[i]);
        } else if (maskSize == 5)
        {
          Image.MinFilter5x5(dstML[i], srcML[i]);
        } else if (maskSize == 7)
        {
          Image.MinFilter7x7(dstML[i], srcML[i]);
        }
        
        break;
      case 4: 
      case 5: 
        if (maskSize == 3)
        {
          Image.MinFilter3x3_Fp(dstML[i], srcML[i]);
        } else if (maskSize == 5)
        {
          Image.MinFilter5x5_Fp(dstML[i], srcML[i]);
        } else if (maskSize == 7)
        {
          Image.MinFilter7x7_Fp(dstML[i], srcML[i]);
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
