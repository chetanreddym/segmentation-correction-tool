package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
















































class MlibBinarizeOpImage
  extends PointOpImage
{
  private double thresh;
  
  public MlibBinarizeOpImage(RenderedImage source, ImageLayout layout, Map config, double thresh)
  {
    super(source, layoutHelper(source, layout, config), config, true);
    



    this.thresh = thresh;
  }
  




  private static ImageLayout layoutHelper(RenderedImage source, ImageLayout il, Map config)
  {
    ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
    

    SampleModel sm = layout.getSampleModel(source);
    if (!ImageUtil.isBinary(sm)) {
      sm = new MultiPixelPackedSampleModel(0, layout.getTileWidth(source), layout.getTileHeight(source), 1);
      


      layout.setSampleModel(sm);
    }
    
    ColorModel cm = layout.getColorModel(null);
    if ((cm == null) || (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
    {
      layout.setColorModel(ImageUtil.getCompatibleColorModel(sm, config));
    }
    

    return layout;
  }
  









  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    Rectangle srcRect = mapDestRect(destRect, 0);
    


    int sourceFormatTag = MediaLibAccessor.findCompatibleTag(sources, source);
    



    int destFormatTag = dest.getSampleModel().getDataType() | 0x100 | 0x0;
    



    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, sourceFormatTag, false);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, destFormatTag, true);
    



    switch (srcAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++)
      {




        Image.Thresh1(dstML[i], srcML[i], new int[] { (int)thresh - 1 }, new int[] { 1 }, new int[] { 0 });
      }
      



      break;
    case 4: 
    case 5: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.Thresh1_Fp(dstML[i], srcML[i], new double[] { thresh }, new double[] { 1.0D }, new double[] { 0.0D });
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
