package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
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
import javax.media.jai.RasterFactory;















final class MlibBandSelectOpImage
  extends PointOpImage
{
  private int cmask = 0;
  











  public MlibBandSelectOpImage(RenderedImage source, Map config, ImageLayout layout, int[] bandIndices)
  {
    super(source, layout, config, true);
    
    int numBands = bandIndices.length;
    if (getSampleModel().getNumBands() != numBands)
    {
      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, sampleModel.getDataType(), tileWidth, tileHeight, numBands);
      


      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    


    int maxShift = source.getSampleModel().getNumBands() - 1;
    for (int i = 0; i < bandIndices.length; i++) {
      cmask |= 1 << maxShift - bandIndices[i];
    }
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
    
    for (int i = 0; i < dstML.length; i++) {
      Image.ChannelExtract(dstML[i], srcML[i], cmask);
    }
    
    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
}
