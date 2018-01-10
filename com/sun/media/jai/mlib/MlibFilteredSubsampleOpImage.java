package com.sun.media.jai.mlib;

import com.sun.media.jai.opimage.FilteredSubsampleOpImage;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;










































final class MlibFilteredSubsampleOpImage
  extends FilteredSubsampleOpImage
{
  protected double[] m_hKernel;
  protected double[] m_vKernel;
  private static final boolean DEBUG = false;
  
  public MlibFilteredSubsampleOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, int scaleX, int scaleY, float[] qsFilter, Interpolation interp)
  {
    super(source, extender, config, layout, scaleX, scaleY, qsFilter, interp);
    











    m_hKernel = new double[hKernel.length];
    m_vKernel = new double[vKernel.length];
    
    for (int i = 0; i < hKernel.length; i++) {
      m_hKernel[i] = hKernel[i];
    }
    

    for (int j = 0; j < vKernel.length; j++) {
      m_vKernel[j] = vKernel[j];
    }
  }
  















  public void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    

    MediaLibAccessor dst = new MediaLibAccessor(dest, destRect, formatTag);
    


    MediaLibAccessor src = new MediaLibAccessor(sources[0], mapDestRect(destRect, 0), formatTag);
    



    int transX = m_hKernel.length - (scaleX + 1) / 2 - hParity * (1 + scaleX) % 2;
    int transY = m_vKernel.length - (scaleY + 1) / 2 - vParity * (1 + scaleY) % 2;
    
    switch (dst.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = src.getMediaLibImages();
      mediaLibImage[] dstML = dst.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.FilteredSubsample(dstML[i], srcML[i], scaleX, scaleY, transX, transY, m_hKernel, m_vKernel, hParity, vParity, 0);
      }
      




      break;
    case 4: 
    case 5: 
      mediaLibImage[] srcML = src.getMediaLibImages();
      mediaLibImage[] dstML = dst.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.FilteredSubsample_Fp(dstML[i], srcML[i], scaleX, scaleY, transX, transY, m_hKernel, m_vKernel, hParity, vParity, 0);
      }
      




      break;
    default: 
      throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
    }
    
    


    if (dst.isDataCopy()) {
      dst.clampDataArrays();
      dst.copyDataToRaster();
    }
  }
}
