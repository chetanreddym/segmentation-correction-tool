package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageInterpTable;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;





































final class MlibScaleTableOpImage
  extends MlibScaleOpImage
{
  public MlibScaleTableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, float xScale, float yScale, float xTrans, float yTrans, Interpolation interp)
  {
    super(source, extender, config, layout, xScale, yScale, xTrans, yTrans, interp, true);
  }
  













  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    InterpolationTable jtable = (InterpolationTable)interp;
    



    Raster source = sources[0];
    Rectangle srcRect = source.getBounds();
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    


    float mlibScaleX = (float)scaleXRationalNum / (float)scaleXRationalDenom;
    float mlibScaleY = (float)scaleYRationalNum / (float)scaleYRationalDenom;
    





    float tempDX = (float)(x * scaleXRationalNum) / (float)scaleXRationalDenom;
    
    float tempDY = (float)(y * scaleYRationalNum) / (float)scaleYRationalDenom;
    

    float tx = transX - x + tempDX;
    float ty = transY - y + tempDY;
    


    switch (dstAccessor.getDataType())
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(3, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableData(), jtable.getVerticalTableData());
      










      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.ZoomTranslateTable(dstML[i], srcML[i], mlibScaleX, mlibScaleY, tx, ty, mlibInterpTable, 0);
        




        MlibUtils.clampImage(dstML[i], getColorModel());
      }
      break;
    

    case 4: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(4, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataFloat(), jtable.getVerticalTableDataFloat());
      











      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.ZoomTranslateTable_Fp(dstML[i], srcML[i], mlibScaleX, mlibScaleY, tx, ty, mlibInterpTable, 0);
      }
      





      break;
    

    case 5: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(5, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataDouble(), jtable.getVerticalTableDataDouble());
      











      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      for (int i = 0; i < dstML.length; i++) {
        Image.ZoomTranslateTable_Fp(dstML[i], srcML[i], mlibScaleX, mlibScaleY, tx, ty, mlibInterpTable, 0);
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
