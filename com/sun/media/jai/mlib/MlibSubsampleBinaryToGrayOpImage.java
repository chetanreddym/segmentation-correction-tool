package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.SubsampleBinaryToGrayOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;


















































































class MlibSubsampleBinaryToGrayOpImage
  extends SubsampleBinaryToGrayOpImage
{
  public MlibSubsampleBinaryToGrayOpImage(RenderedImage source, ImageLayout layout, Map config, float scaleX, float scaleY)
  {
    super(source, layout, config, scaleX, scaleY);
  }
  
















  protected Rectangle backwardMapRect(Rectangle destRect, int sourceIndex)
  {
    Rectangle sourceRect = super.backwardMapRect(destRect, sourceIndex);
    

    width += (int)invScaleX;
    height += (int)invScaleY;
    

    return sourceRect.intersection(getSourceImage(0).getBounds());
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    Rectangle srcRect = source.getBounds();
    


    int sourceFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0x0;
    





    int destFormatTag = MediaLibAccessor.findCompatibleTag(null, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, sourceFormatTag, true);
    

    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag);
    


    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.SubsampleBinaryToGray(dstML[i], srcML[i], scaleX, scaleY, lutGray);
      }
      



      break;
    default: 
      String className = getClass().getName();
      throw new RuntimeException(JaiI18N.getString("Generic2")); }
    mediaLibImage[] dstML;
    mediaLibImage[] srcML;
    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
