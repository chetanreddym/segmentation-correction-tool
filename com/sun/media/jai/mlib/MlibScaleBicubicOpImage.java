package com.sun.media.jai.mlib;

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
import javax.media.jai.InterpolationBicubic2;






































final class MlibScaleBicubicOpImage
  extends MlibScaleOpImage
{
  public MlibScaleBicubicOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, extender, config, layout, xScale, yScale, xTrans, yTrans, interp, true);
  }
  













  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    int mlibInterpType = 2;
    

    if ((interp instanceof InterpolationBicubic2)) {
      mlibInterpType = 3;
    }
    
    Raster source = sources[0];
    Rectangle srcRect = source.getBounds();
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    


    double mlibScaleX = scaleXRationalNum / scaleXRationalDenom;
    
    double mlibScaleY = scaleYRationalNum / scaleYRationalDenom;
    







    long tempDenomX = scaleXRationalDenom * transXRationalDenom;
    long tempDenomY = scaleYRationalDenom * transYRationalDenom;
    long tempNumerX = x * scaleXRationalNum * transXRationalDenom + transXRationalNum * scaleXRationalDenom - x * tempDenomX;
    


    long tempNumerY = y * scaleYRationalNum * transYRationalDenom + transYRationalNum * scaleYRationalDenom - y * tempDenomY;
    



    double tx = tempNumerX / tempDenomX;
    double ty = tempNumerY / tempDenomY;
    


    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.ZoomTranslate(dstML[i], srcML[i], mlibScaleX, mlibScaleY, tx, ty, mlibInterpType, 0);
        




        MlibUtils.clampImage(dstML[i], getColorModel());
      }
      break;
    
    case 4: 
    case 5: 
      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.ZoomTranslate_Fp(dstML[i], srcML[i], mlibScaleX, mlibScaleY, tx, ty, mlibInterpType, 0);
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
