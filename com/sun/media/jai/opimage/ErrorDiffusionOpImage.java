package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.UntiledOpImage;


























































final class ErrorDiffusionOpImage
  extends UntiledOpImage
{
  private static final float FLOAT_EPSILON = 1.1920929E-7F;
  private static final int NBANDS = 3;
  private static final int NGRAYS = 256;
  private static final int OVERSHOOT = 256;
  private static final int UNDERSHOOT = 256;
  private static final int TOTALGRAYS = 768;
  private static final int ERR_SHIFT = 8;
  protected LookupTableJAI colorMap;
  protected KernelJAI errorKernel;
  private int numBandsSource;
  private boolean isOptimizedCase = false;
  




  private float minPixelValue;
  



  private float maxPixelValue;
  




  private static boolean isFloydSteinbergKernel(KernelJAI kernel)
  {
    int ky = kernel.getYOrigin();
    
    return (kernel.getWidth() == 3) && (kernel.getXOrigin() == 1) && (kernel.getHeight() - ky == 2) && (Math.abs(kernel.getElement(2, ky) - 0.4375F) < 1.1920929E-7F) && (Math.abs(kernel.getElement(0, ky + 1) - 0.1875F) < 1.1920929E-7F) && (Math.abs(kernel.getElement(1, ky + 1) - 0.3125F) < 1.1920929E-7F) && (Math.abs(kernel.getElement(2, ky + 1) - 0.0625F) < 1.1920929E-7F);
  }
  
















  private static int[] initFloydSteinberg24To8(ColorCube colorCube)
  {
    int[] ditherTable = new int['ऀ'];
    
    float[] thresh = new float['Ā'];
    



    int[] multipliers = colorCube.getMultipliers();
    int[] dimsLessOne = colorCube.getDimsLessOne();
    int offset = colorCube.getAdjustedOffset();
    



    for (int band = 0; band < 3; band++) {
      int pTab = band * 768;
      





      float binWidth = 255.0F / dimsLessOne[band];
      







      for (int i = 0; i < dimsLessOne[band]; i++) {
        thresh[i] = ((i + 0.5F) * binWidth);
      }
      thresh[dimsLessOne[band]] = 256.0F;
      





      int tableInc = 256;
      int tableValue = -65536;
      for (int gray = 65280; gray < 0; gray++) {
        ditherTable[(pTab++)] = tableValue;
        tableValue += tableInc;
      }
      



      int indexContrib = 0;
      float frepValue = 0.0F;
      
      int binNum = 0;
      float threshold = thresh[0];
      int gray = 0;
      while (gray < 256)
      {





        int tableBase = indexContrib;
        int repValue = (int)(frepValue + 0.5F);
        while (gray < threshold) {
          ditherTable[(pTab++)] = ((gray - repValue << 8) + tableBase);
          
          gray++;
        }
        






        threshold = thresh[(++binNum)];
        indexContrib += multipliers[band];
        frepValue += binWidth;
      }
      





      indexContrib -= multipliers[band];
      int repValue = 255;
      tableValue = 256 - repValue << 8 | indexContrib;
      
      for (gray = 256; gray < 512; gray++) {
        ditherTable[(pTab++)] = tableValue;
        tableValue += tableInc;
      }
    }
    






    int pTab = 0;
    for (int count = 768; count != 0; count--) {
      ditherTable[pTab] += offset;
      pTab++;
    }
    
    return ditherTable;
  }
  





  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, LookupTableJAI colorMap)
  {
    ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
    


    il.setMinX(source.getMinX());
    il.setMinY(source.getMinY());
    il.setWidth(source.getWidth());
    il.setHeight(source.getHeight());
    

    SampleModel sm = il.getSampleModel(source);
    

    if ((colorMap.getNumBands() == 1) && (colorMap.getNumEntries() == 2) && (!ImageUtil.isBinary(il.getSampleModel(source))))
    {

      sm = new MultiPixelPackedSampleModel(0, il.getTileWidth(source), il.getTileHeight(source), 1);
      


      il.setSampleModel(sm);
    }
    

    if (sm.getNumBands() != 1) {
      sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
      




      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    

    int numColorMapBands = colorMap.getNumBands();
    int maxIndex = 0;
    for (int i = 0; i < numColorMapBands; i++) {
      maxIndex = Math.max(colorMap.getOffset(i) + colorMap.getNumEntries() - 1, maxIndex);
    }
    



    if (((maxIndex > 255) && (sm.getDataType() == 0)) || ((maxIndex > 65535) && (sm.getDataType() != 3)))
    {
      int dataType = maxIndex > 65535 ? 3 : 1;
      
      sm = RasterFactory.createComponentSampleModel(sm, dataType, sm.getWidth(), sm.getHeight(), 1);
      




      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    





    if (((layout == null) || (!il.isValid(512))) && (source.getSampleModel().getDataType() == 0) && ((sm.getDataType() == 0) || (sm.getDataType() == 1)) && (colorMap.getDataType() == 0) && (colorMap.getNumBands() == 3))
    {




      ColorModel cm = source.getColorModel();
      if ((cm == null) || ((cm != null) && (cm.getColorSpace().isCS_sRGB())))
      {
        int size = colorMap.getNumEntries();
        byte[][] cmap = new byte[3][maxIndex + 1];
        for (int i = 0; i < 3; i++) {
          byte[] band = cmap[i];
          byte[] data = colorMap.getByteData(i);
          int offset = colorMap.getOffset(i);
          int end = offset + size;
          for (int j = offset; j < end; j++) {
            band[j] = data[(j - offset)];
          }
        }
        
        int numBits = sm.getDataType() == 0 ? 8 : 16;
        
        il.setColorModel(new IndexColorModel(numBits, maxIndex + 1, cmap[0], cmap[1], cmap[2]));
      }
    }
    


    return il;
  }
  



























  public ErrorDiffusionOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI colorMap, KernelJAI errorKernel)
  {
    super(source, config, layoutHelper(layout, source, colorMap));
    

    SampleModel srcSampleModel = source.getSampleModel();
    

    numBandsSource = srcSampleModel.getNumBands();
    

    this.colorMap = colorMap;
    

    this.errorKernel = errorKernel;
    

    isOptimizedCase = ((sampleModel.getTransferType() == 0) && (srcSampleModel.getTransferType() == 0) && (numBandsSource == 3) && ((colorMap instanceof ColorCube)) && (isFloydSteinbergKernel(errorKernel)));
    






    switch (colorMap.getDataType())
    {
    case 0: 
      minPixelValue = 0.0F;
      maxPixelValue = 255.0F;
      break;
    case 2: 
      minPixelValue = -32768.0F;
      maxPixelValue = 32767.0F;
      break;
    case 1: 
      minPixelValue = 0.0F;
      maxPixelValue = 65535.0F;
      break;
    case 3: 
      minPixelValue = -2.14748365E9F;
      maxPixelValue = 2.14748365E9F;
      break;
    case 4: 
      minPixelValue = 0.0F;
      maxPixelValue = Float.MAX_VALUE;
      break;
    case 5: 
      minPixelValue = 0.0F;
      maxPixelValue = Float.MAX_VALUE;
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("ErrorDiffusionOpImage0"));
    }
    
  }
  













  protected void computeImage(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    Raster source = sources[0];
    
    if (isOptimizedCase) {
      computeImageOptimized(source, dest, destRect);
    } else {
      computeImageDefault(source, dest, destRect);
    }
  }
  


  protected void computeImageDefault(Raster source, WritableRaster dest, Rectangle destRect)
  {
    int startX = minX;
    int endX = startX + width - 1;
    

    int startY = minY;
    int endY = startY + height - 1;
    

    int numLinesBuffer = errorKernel.getHeight() - errorKernel.getYOrigin();
    


    float[][] bufMem = new float[numLinesBuffer][width * numBandsSource];
    

    int[] bufIdx = new int[numLinesBuffer];
    

    for (int idx = 0; idx < numLinesBuffer; idx++) {
      bufIdx[idx] = idx;
      source.getPixels(startX, startY + idx, width, 1, bufMem[idx]);
    }
    

    int lastLineBuffer = numLinesBuffer - 1;
    

    int kernelWidth = errorKernel.getWidth();
    float[] kernelData = errorKernel.getKernelData();
    int diffuseRight = kernelWidth - errorKernel.getXOrigin() - 1;
    int diffuseBelow = errorKernel.getHeight() - errorKernel.getYOrigin() - 1;
    
    int kernelOffsetRight = errorKernel.getYOrigin() * kernelWidth + errorKernel.getXOrigin() + 1;
    

    int kernelOffsetBelow = (errorKernel.getYOrigin() + 1) * kernelWidth;
    

    float[] currentPixel = new float[numBandsSource];
    int offset = colorMap.getOffset();
    float[] qError = new float[numBandsSource];
    

    int[] dstData = new int[width];
    for (int y = startY; y <= endY; y++) {
      int currentIndex = bufIdx[0];
      float[] currentLine = bufMem[currentIndex];
      

      int dstOffset = 0;
      int x = startX; for (int z = 0; x <= endX; x++)
      {
        for (int b = 0; b < numBandsSource; b++) {
          currentPixel[b] = currentLine[(z++)];
          

          if ((currentPixel[b] < minPixelValue) || (currentPixel[b] > maxPixelValue))
          {
            currentPixel[b] = Math.max(currentPixel[b], minPixelValue);
            
            currentPixel[b] = Math.min(currentPixel[b], maxPixelValue);
          }
        }
        


        int nearestIndex = colorMap.findNearestEntry(currentPixel);
        

        dstData[(dstOffset++)] = nearestIndex;
        

        boolean isQuantizationError = false;
        for (int b = 0; b < numBandsSource; b++) {
          currentPixel[b] -= colorMap.lookupFloat(b, nearestIndex);
          

          if (qError[b] != 0.0F) {
            isQuantizationError = true;
          }
        }
        

        if (isQuantizationError)
        {
          int rightCount = Math.min(diffuseRight, endX - x);
          int kernelOffset = kernelOffsetRight;
          int sampleOffset = z;
          for (int u = 1; u <= rightCount; u++) {
            for (int b = 0; b < numBandsSource; b++) {
              currentLine[(sampleOffset++)] += qError[b] * kernelData[kernelOffset];
            }
            
            kernelOffset++;
          }
          

          int offsetLeft = Math.min(x - startX, diffuseRight);
          int count = Math.min(x + diffuseRight, endX) - Math.max(x - diffuseRight, startX) + 1;
          

          for (int v = 1; v <= diffuseBelow; v++) {
            float[] line = bufMem[bufIdx[v]];
            kernelOffset = kernelOffsetBelow;
            sampleOffset = z - (offsetLeft + 1) * numBandsSource;
            for (int u = 1; u <= count; u++) {
              for (int b = 0; b < numBandsSource; b++) {
                line[(sampleOffset++)] += qError[b] * kernelData[kernelOffset];
              }
              
              kernelOffset++;
            }
          }
        }
      }
      



      dest.setSamples(startX, y, width, 1, 0, dstData);
      

      for (int k = 0; k < lastLineBuffer; k++) {
        bufIdx[k] = bufIdx[(k + 1)];
      }
      bufIdx[lastLineBuffer] = currentIndex;
      

      if (y + numLinesBuffer < getMaxY()) {
        source.getPixels(startX, y + numLinesBuffer, width, 1, bufMem[bufIdx[lastLineBuffer]]);
      }
    }
  }
  



  protected void computeImageOptimized(Raster source, WritableRaster dest, Rectangle destRect)
  {
    int startX = minX;
    int endX = startX + width - 1;
    

    int startY = minY;
    int endY = startY + height - 1;
    

    int[] ditherTable = initFloydSteinberg24To8((ColorCube)colorMap);
    

    int sourceWidthPadded = source.getWidth() + 2;
    

    int[] errBuf = new int[sourceWidthPadded * 3];
    

    RasterFormatTag[] formatTags = getFormatTags();
    
    RasterAccessor srcAccessor = new RasterAccessor(source, new Rectangle(startX, startY, source.getWidth(), source.getHeight()), formatTags[0], getSourceImage(0).getColorModel());
    




    RasterAccessor dstAccessor = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    


    int srcPixelStride = srcAccessor.getPixelStride();
    int srcScanlineStride = srcAccessor.getScanlineStride();
    int dstPixelStride = dstAccessor.getPixelStride();
    int dstScanlineStride = dstAccessor.getScanlineStride();
    

    byte[] srcData0 = srcAccessor.getByteDataArray(0);
    byte[] srcData1 = srcAccessor.getByteDataArray(1);
    byte[] srcData2 = srcAccessor.getByteDataArray(2);
    byte[] dstData = dstAccessor.getByteDataArray(0);
    

    int srcLine0 = srcAccessor.getBandOffset(0);
    int srcLine1 = srcAccessor.getBandOffset(1);
    int srcLine2 = srcAccessor.getBandOffset(2);
    int dstLine = dstAccessor.getBandOffset(0);
    









    for (int y = startY; y <= endY; y++)
    {
      int srcPixel0 = srcLine0;
      int srcPixel1 = srcLine1;
      int srcPixel2 = srcLine2;
      int dstPixel = dstLine;
      















































      int errRedA = 0;
      int errRedC = 0;
      int errRedD = 0;
      int errGrnA = 0;
      int errGrnC = 0;
      int errGrnD = 0;
      int errBluA = 0;
      int errBluC = 0;
      int errBluD = 0;
      
      int pErr = 0;
      int dstOffset = 0;
      for (int x = startX; x <= endX; x++)
      {




        int pTab = 256;
        
        int adjVal = (errRedA + errBuf[(pErr + 3)] + 8 >> 4) + (srcData0[srcPixel0] & 0xFF);
        

        srcPixel0 += srcPixelStride;
        int tabval = ditherTable[(pTab + adjVal)];
        int err = tabval >> 8;
        int err1 = err;
        int index = tabval & 0xFF;
        int err2 = err + err; int 
          tmp377_376 = (err + err2);err = tmp377_376;errBuf[pErr] = (errRedC + tmp377_376);
        errRedC = errRedD + (err += err2);
        errRedD = err1;
        errRedA = err += err2;
        





        pTab += 768;
        
        adjVal = (errGrnA + errBuf[(pErr + 4)] + 8 >> 4) + (srcData1[srcPixel1] & 0xFF);
        

        srcPixel1 += srcPixelStride;
        tabval = ditherTable[(pTab + adjVal)];
        err = tabval >> 8;
        err1 = err;
        index += (tabval & 0xFF);
        err2 = err + err; int 
          tmp503_502 = (err + err2);err = tmp503_502;errBuf[(pErr + 1)] = (errGrnC + tmp503_502);
        errGrnC = errGrnD + (err += err2);
        errGrnD = err1;
        errGrnA = err += err2;
        
        pTab += 768;
        





        adjVal = (errBluA + errBuf[(pErr + 5)] + 8 >> 4) + (srcData2[srcPixel2] & 0xFF);
        

        srcPixel2 += srcPixelStride;
        tabval = ditherTable[(pTab + adjVal)];
        err = tabval >> 8;
        err1 = err;
        index += (tabval & 0xFF);
        err2 = err + err; int 
          tmp629_628 = (err + err2);err = tmp629_628;errBuf[(pErr + 2)] = (errBluC + tmp629_628);
        errBluC = errBluD + (err += err2);
        errBluD = err1;
        errBluA = err += err2;
        

        dstData[dstPixel] = ((byte)(index & 0xFF));
        dstPixel += dstPixelStride;
        
        pErr += 3;
      }
      




      int last = 3 * (sourceWidthPadded - 2);
      errBuf[last] = errRedC;
      errBuf[(last + 1)] = errGrnC;
      errBuf[(last + 2)] = errBluC;
      

      srcLine0 += srcScanlineStride;
      srcLine1 += srcScanlineStride;
      srcLine2 += srcScanlineStride;
      dstLine += dstScanlineStride;
    }
    


    dstAccessor.copyDataToRaster();
  }
}
