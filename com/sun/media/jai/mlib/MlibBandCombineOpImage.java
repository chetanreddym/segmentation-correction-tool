package com.sun.media.jai.mlib;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;















final class MlibBandCombineOpImage
  extends PointOpImage
{
  private double[] cmat = new double[9];
  

  private double[] offset = new double[3];
  

  private boolean isOffsetNonZero = false;
  











  public MlibBandCombineOpImage(RenderedImage source, Map config, ImageLayout layout, double[][] matrix)
  {
    super(source, layout, config, true);
    
    int numBands = matrix.length;
    if (getSampleModel().getNumBands() != numBands) {
      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, sampleModel.getDataType(), tileWidth, tileHeight, numBands);
      


      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    



    ComponentSampleModel csm = (ComponentSampleModel)source.getSampleModel();
    
    int[] bankIndices = csm.getBankIndices();
    int[] bandOffsets = csm.getBandOffsets();
    



    if ((bankIndices[0] == bankIndices[1]) && (bankIndices[0] == bankIndices[2]) && (bandOffsets[0] > bandOffsets[2]))
    {

      for (int j = 0; j < 3; j++) {
        int k = 8 - 3 * j;
        for (int i = 0; i < 3; i++) {
          cmat[(k--)] = matrix[j][i];
        }
        offset[(2 - j)] = matrix[j][3];
        if (offset[j] != 0.0D) {
          isOffsetNonZero = true;
        }
      }
    } else {
      for (int j = 0; j < 3; j++) {
        int k = 3 * j;
        for (int i = 0; i < 3; i++) {
          cmat[(k++)] = matrix[j][i];
        }
        offset[j] = matrix[j][3];
        if (offset[j] != 0.0D) {
          isOffsetNonZero = true;
        }
      }
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
    
    switch (dstAccessor.getDataType()) {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
      for (int i = 0; i < dstML.length; i++) {
        if (isOffsetNonZero) {
          Image.ColorConvert2(dstML[i], srcML[i], cmat, offset);

        }
        else
        {
          Image.ColorConvert1(dstML[i], srcML[i], cmat);
        }
      }
      

      break;
    
    case 4: 
    case 5: 
      for (int i = 0; i < dstML.length; i++) {
        if (isOffsetNonZero) {
          Image.ColorConvert2_Fp(dstML[i], srcML[i], cmat, offset);

        }
        else
        {
          Image.ColorConvert1_Fp(dstML[i], srcML[i], cmat);
        }
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
