package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;






























final class ConjugateOpImage
  extends PointOpImage
{
  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    SampleModel sm = il.getSampleModel(source);
    

    int dataType = sm.getTransferType();
    


    boolean createNewSampleModel = false;
    if (dataType == 0) {
      dataType = 2;
      createNewSampleModel = true;
    } else if (dataType == 1) {
      dataType = 3;
      createNewSampleModel = true;
    }
    

    if (createNewSampleModel) {
      sm = RasterFactory.createComponentSampleModel(sm, dataType, sm.getWidth(), sm.getHeight(), sm.getNumBands());
      



      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    
    return il;
  }
  












  public ConjugateOpImage(RenderedImage source, Map config, ImageLayout layout)
  {
    super(source, layoutHelper(layout, source), config, true);
    

    permitInPlaceOperation();
  }
  











  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    
    Raster source = sources[0];
    Rectangle srcRect = mapDestRect(destRect, 0);
    
    RasterAccessor srcAccessor = new RasterAccessor(source, srcRect, formatTags[0], getSourceImage(0).getColorModel());
    



    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    switch (dstAccessor.getDataType()) {
    case 2: 
      shortLoop(srcAccessor, dstAccessor);
      break;
    case 3: 
      intLoop(srcAccessor, dstAccessor);
      break;
    case 4: 
      floatLoop(srcAccessor, dstAccessor);
      break;
    case 5: 
      doubleLoop(srcAccessor, dstAccessor);
      break;
    case 0: 
    case 1: 
    default: 
      throw new RuntimeException(JaiI18N.getString("ConjugateOpImage0"));
    }
    
    


    if (dstAccessor.isDataCopy()) {
      dstAccessor.clampDataArrays();
      dstAccessor.copyDataToRaster();
    }
  }
  
  private void shortLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    short[][] dstDataArrays = dst.getShortDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    short[][] srcDataArrays = src.getShortDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    for (int k = 0; k < dnumBands; k++) {
      boolean isRealPart = k % 2 == 0;
      short[] dstData = dstDataArrays[k];
      short[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      if (isRealPart) {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = srcData[srcPixelOffset];
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      } else {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = ImageUtil.clampShort(-srcData[srcPixelOffset]);
            
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      }
    }
  }
  

  private void intLoop(RasterAccessor src, RasterAccessor dst)
  {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    int[][] dstDataArrays = dst.getIntDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    int[][] srcDataArrays = src.getIntDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    for (int k = 0; k < dnumBands; k++) {
      boolean isRealPart = k % 2 == 0;
      int[] dstData = dstDataArrays[k];
      int[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      if (isRealPart) {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = srcData[srcPixelOffset];
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      } else {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = (-srcData[srcPixelOffset]);
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      }
    }
  }
  
  private void floatLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    float[][] dstDataArrays = dst.getFloatDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    float[][] srcDataArrays = src.getFloatDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    for (int k = 0; k < dnumBands; k++) {
      boolean isRealPart = k % 2 == 0;
      float[] dstData = dstDataArrays[k];
      float[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      if (isRealPart) {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = srcData[srcPixelOffset];
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      } else {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = (-srcData[srcPixelOffset]);
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      }
    }
  }
  
  private void doubleLoop(RasterAccessor src, RasterAccessor dst) {
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dnumBands = dst.getNumBands();
    
    double[][] dstDataArrays = dst.getDoubleDataArrays();
    int[] dstBandOffsets = dst.getBandOffsets();
    int dstPixelStride = dst.getPixelStride();
    int dstScanlineStride = dst.getScanlineStride();
    
    double[][] srcDataArrays = src.getDoubleDataArrays();
    int[] srcBandOffsets = src.getBandOffsets();
    int srcPixelStride = src.getPixelStride();
    int srcScanlineStride = src.getScanlineStride();
    
    for (int k = 0; k < dnumBands; k++) {
      boolean isRealPart = k % 2 == 0;
      double[] dstData = dstDataArrays[k];
      double[] srcData = srcDataArrays[k];
      int srcScanlineOffset = srcBandOffsets[k];
      int dstScanlineOffset = dstBandOffsets[k];
      if (isRealPart) {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = srcData[srcPixelOffset];
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      } else {
        for (int j = 0; j < dheight; j++) {
          int srcPixelOffset = srcScanlineOffset;
          int dstPixelOffset = dstScanlineOffset;
          for (int i = 0; i < dwidth; i++) {
            dstData[dstPixelOffset] = (-srcData[srcPixelOffset]);
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
          srcScanlineOffset += srcScanlineStride;
          dstScanlineOffset += dstScanlineStride;
        }
      }
    }
  }
}
