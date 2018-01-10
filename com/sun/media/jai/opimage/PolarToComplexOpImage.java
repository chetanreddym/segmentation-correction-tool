package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
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




































final class PolarToComplexOpImage
  extends PointOpImage
{
  private double phaseGain = 1.0D;
  

  private double phaseBias = 0.0D;
  













  public PolarToComplexOpImage(RenderedImage magnitude, RenderedImage phase, Map config, ImageLayout layout)
  {
    super(magnitude, phase, layout, config, true);
    

    int numBands = 2 * Math.min(magnitude.getSampleModel().getNumBands(), phase.getSampleModel().getNumBands());
    

    if (sampleModel.getNumBands() != numBands)
    {
      sampleModel = RasterFactory.createComponentSampleModel(sampleModel, sampleModel.getTransferType(), sampleModel.getWidth(), sampleModel.getHeight(), numBands);
      





      if ((colorModel != null) && (!JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)))
      {

        colorModel = ImageUtil.getCompatibleColorModel(sampleModel, config);
      }
    }
    


    switch (phase.getSampleModel().getTransferType()) {
    case 0: 
      phaseGain = 0.024639942381096416D;
      phaseBias = -3.141592653589793D;
      break;
    case 2: 
      phaseGain = 1.9175345033660654E-4D;
      phaseBias = -3.141592653589793D;
      break;
    case 1: 
      phaseGain = 9.587526218325454E-5D;
      phaseBias = -3.141592653589793D;
      break;
    case 3: 
      phaseGain = 2.925836159896768E-9D;
      phaseBias = -3.141592653589793D;
      break;
    }
    
  }
  













  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = getFormatTags();
    

    RasterAccessor magAccessor = new RasterAccessor(sources[0], destRect, formatTags[0], getSource(0).getColorModel());
    

    RasterAccessor phsAccessor = new RasterAccessor(sources[1], destRect, formatTags[1], getSource(1).getColorModel());
    

    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[2], getColorModel());
    


    switch (dstAccessor.getDataType()) {
    case 0: 
      computeRectByte(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    case 2: 
      computeRectShort(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    case 1: 
      computeRectUShort(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    case 3: 
      computeRectInt(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    case 4: 
      computeRectFloat(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    case 5: 
      computeRectDouble(magAccessor, phsAccessor, dstAccessor, height, width);
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("PolarToComplexOpImage0"));
    }
    
    if (dstAccessor.needsClamping()) {
      dstAccessor.clampDataArrays();
    }
    

    dstAccessor.copyDataToRaster();
  }
  




  private void computeRectDouble(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      double[] dstReal = dstAccessor.getDoubleDataArray(dstBandReal);
      double[] dstImag = dstAccessor.getDoubleDataArray(dstBandImag);
      double[] magData = magAccessor.getDoubleDataArray(component);
      double[] phsData = phsAccessor.getDoubleDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel];
          double phs = phsData[phsPixel] * phaseGain + phaseBias;
          
          dstReal[dstPixelReal] = (mag * Math.cos(phs));
          dstImag[dstPixelImag] = (mag * Math.sin(phs));
          
          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
  




  private void computeRectFloat(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      float[] dstReal = dstAccessor.getFloatDataArray(dstBandReal);
      float[] dstImag = dstAccessor.getFloatDataArray(dstBandImag);
      float[] magData = magAccessor.getFloatDataArray(component);
      float[] phsData = phsAccessor.getFloatDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel];
          double phs = phsData[phsPixel] * phaseGain + phaseBias;
          
          dstReal[dstPixelReal] = ImageUtil.clampFloat(mag * Math.cos(phs));
          dstImag[dstPixelImag] = ImageUtil.clampFloat(mag * Math.sin(phs));
          
          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
  




  private void computeRectInt(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      int[] dstReal = dstAccessor.getIntDataArray(dstBandReal);
      int[] dstImag = dstAccessor.getIntDataArray(dstBandImag);
      int[] magData = magAccessor.getIntDataArray(component);
      int[] phsData = phsAccessor.getIntDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel];
          double phs = phsData[phsPixel] * phaseGain + phaseBias;
          
          dstReal[dstPixelReal] = ImageUtil.clampRoundInt(mag * Math.cos(phs));
          dstImag[dstPixelImag] = ImageUtil.clampRoundInt(mag * Math.sin(phs));
          
          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
  




  private void computeRectUShort(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      short[] dstReal = dstAccessor.getShortDataArray(dstBandReal);
      short[] dstImag = dstAccessor.getShortDataArray(dstBandImag);
      short[] magData = magAccessor.getShortDataArray(component);
      short[] phsData = phsAccessor.getShortDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel] & 0xFFFF;
          double phs = (phsData[phsPixel] & 0xFFFF) * phaseGain + phaseBias;
          

          dstReal[dstPixelReal] = ImageUtil.clampRoundUShort(mag * Math.cos(phs));
          
          dstImag[dstPixelImag] = ImageUtil.clampRoundUShort(mag * Math.sin(phs));
          

          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
  




  private void computeRectShort(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      short[] dstReal = dstAccessor.getShortDataArray(dstBandReal);
      short[] dstImag = dstAccessor.getShortDataArray(dstBandImag);
      short[] magData = magAccessor.getShortDataArray(component);
      short[] phsData = phsAccessor.getShortDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel];
          double phs = phsData[phsPixel] * phaseGain + phaseBias;
          
          dstReal[dstPixelReal] = ImageUtil.clampRoundShort(mag * Math.cos(phs));
          dstImag[dstPixelImag] = ImageUtil.clampRoundShort(mag * Math.sin(phs));
          
          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
  




  private void computeRectByte(RasterAccessor magAccessor, RasterAccessor phsAccessor, RasterAccessor dstAccessor, int numRows, int numCols)
  {
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    int magPixelStride = magAccessor.getPixelStride();
    int magScanlineStride = magAccessor.getScanlineStride();
    int phsPixelStride = phsAccessor.getPixelStride();
    int phsScanlineStride = phsAccessor.getScanlineStride();
    

    int numComponents = sampleModel.getNumBands() / 2;
    for (int component = 0; component < numComponents; component++)
    {
      int dstBandReal = 2 * component;
      int dstBandImag = dstBandReal + 1;
      

      byte[] dstReal = dstAccessor.getByteDataArray(dstBandReal);
      byte[] dstImag = dstAccessor.getByteDataArray(dstBandImag);
      byte[] magData = magAccessor.getByteDataArray(component);
      byte[] phsData = phsAccessor.getByteDataArray(component);
      

      int dstOffsetReal = dstAccessor.getBandOffset(dstBandReal);
      int dstOffsetImag = dstAccessor.getBandOffset(dstBandImag);
      int magOffset = magAccessor.getBandOffset(component);
      int phsOffset = phsAccessor.getBandOffset(component);
      

      int dstLineReal = dstOffsetReal;
      int dstLineImag = dstOffsetImag;
      int magLine = magOffset;
      int phsLine = phsOffset;
      
      for (int row = 0; row < numRows; row++)
      {
        int dstPixelReal = dstLineReal;
        int dstPixelImag = dstLineImag;
        int magPixel = magLine;
        int phsPixel = phsLine;
        
        for (int col = 0; col < numCols; col++) {
          double mag = magData[magPixel] & 0xFF;
          double phs = (phsData[phsPixel] & 0xFF) * phaseGain + phaseBias;
          

          dstReal[dstPixelReal] = ImageUtil.clampRoundByte(mag * Math.cos(phs));
          dstImag[dstPixelImag] = ImageUtil.clampRoundByte(mag * Math.sin(phs));
          
          dstPixelReal += dstPixelStride;
          dstPixelImag += dstPixelStride;
          magPixel += magPixelStride;
          phsPixel += phsPixelStride;
        }
        

        dstLineReal += dstScanlineStride;
        dstLineImag += dstScanlineStride;
        magLine += magScanlineStride;
        phsLine += phsScanlineStride;
      }
    }
  }
}
