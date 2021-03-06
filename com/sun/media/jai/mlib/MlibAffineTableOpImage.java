package com.sun.media.jai.mlib;

import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import com.sun.medialib.mlib.mediaLibImageInterpTable;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationTable;









































public class MlibAffineTableOpImage
  extends MlibAffineOpImage
{
  public MlibAffineTableOpImage(RenderedImage source, BorderExtender extender, Map config, ImageLayout layout, AffineTransform tr, Interpolation interp, double[] backgroundValues)
  {
    super(source, layout, config, extender, tr, interp, backgroundValues);
  }
  


















  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    InterpolationTable jtable = (InterpolationTable)interp;
    



    Raster source = sources[0];
    Rectangle srcRect = source.getBounds();
    
    int formatTag = MediaLibAccessor.findCompatibleTag(sources, dest);
    
    MediaLibAccessor srcAccessor = new MediaLibAccessor(source, srcRect, formatTag);
    
    MediaLibAccessor dstAccessor = new MediaLibAccessor(dest, destRect, formatTag);
    








    double[] medialib_tr = (double[])this.medialib_tr.clone();
    
    medialib_tr[2] = (m_transform[0] * x + m_transform[1] * y + m_transform[2] - x);
    


    medialib_tr[5] = (m_transform[3] * x + m_transform[4] * y + m_transform[5] - y);
    





    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(3, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableData(), jtable.getVerticalTableData());
      










      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      
      if (setBackground) {
        Image.AffineTable2(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0, intBackgroundValues);


      }
      else
      {

        Image.AffineTable(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0);
      }
      


      MlibUtils.clampImage(dstML[0], getColorModel());
      break;
    
    case 4: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(4, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataFloat(), jtable.getVerticalTableDataFloat());
      










      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      
      if (setBackground) {
        Image.AffineTable2_Fp(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0, backgroundValues);


      }
      else
      {

        Image.AffineTable_Fp(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0);
      }
      


      break;
    
    case 5: 
      mediaLibImageInterpTable mlibInterpTable = new mediaLibImageInterpTable(5, jtable.getWidth(), jtable.getHeight(), jtable.getLeftPadding(), jtable.getTopPadding(), jtable.getSubsampleBitsH(), jtable.getSubsampleBitsV(), jtable.getPrecisionBits(), jtable.getHorizontalTableDataDouble(), jtable.getVerticalTableDataDouble());
      










      mediaLibImage[] srcML = srcAccessor.getMediaLibImages();
      mediaLibImage[] dstML = dstAccessor.getMediaLibImages();
      
      if (setBackground) {
        Image.AffineTable2_Fp(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0, backgroundValues);


      }
      else
      {

        Image.AffineTable_Fp(dstML[0], srcML[0], medialib_tr, mlibInterpTable, 0);
      }
      


      break;
    
    default: 
      String className = getClass().getName();
      throw new RuntimeException(JaiI18N.getString("Generic2"));
    }
    
    if (dstAccessor.isDataCopy()) {
      dstAccessor.copyDataToRaster();
    }
  }
}
