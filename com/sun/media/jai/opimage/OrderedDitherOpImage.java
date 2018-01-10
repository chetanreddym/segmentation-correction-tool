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
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;

















































final class OrderedDitherOpImage
  extends PointOpImage
{
  private static final int TYPE_OD_GENERAL = 0;
  private static final int TYPE_OD_BYTE_LUT_3BAND = 1;
  private static final int TYPE_OD_BYTE_LUT_NBAND = 2;
  private static final int DITHER_LUT_LENGTH_MAX = 262144;
  private static final int DITHER_LUT_CACHE_LENGTH_MAX = 4;
  private static Vector ditherLUTCache = new Vector(0, 4);
  




  private int odType = 0;
  




  protected int numBands;
  



  protected int[] dims;
  



  protected int[] mults;
  



  protected int adjustedOffset;
  



  protected int maskWidth;
  



  protected int maskHeight;
  



  protected byte[][] maskDataByte;
  



  protected int[][] maskDataInt;
  



  protected long[][] maskDataLong;
  



  protected float[][] maskDataFloat;
  



  protected DitherLUT odLUT = null;
  

  private static ImageLayout layoutHelper(ImageLayout layout, RenderedImage source, ColorCube colorMap)
  {
    ImageLayout il;
    
    ImageLayout il;
    
    if (layout == null) {
      il = new ImageLayout(source);
    } else {
      il = (ImageLayout)layout.clone();
    }
    

    SampleModel sm = il.getSampleModel(source);
    

    if ((colorMap.getNumBands() == 1) && (colorMap.getNumEntries() == 2) && (!ImageUtil.isBinary(il.getSampleModel(source))))
    {

      sm = new MultiPixelPackedSampleModel(0, il.getTileWidth(source), il.getTileHeight(source), 1);
      


      il.setSampleModel(sm);
    }
    

    if (sm.getNumBands() != 1)
    {
      sm = RasterFactory.createComponentSampleModel(sm, sm.getTransferType(), sm.getWidth(), sm.getHeight(), 1);
      



      il.setSampleModel(sm);
      

      ColorModel cm = il.getColorModel(null);
      if ((cm != null) && (!JDKWorkarounds.areCompatibleDataModels(sm, cm)))
      {

        il.unsetValid(512);
      }
    }
    






    if (((layout == null) || (!il.isValid(512))) && (source.getSampleModel().getDataType() == 0) && (il.getSampleModel(null).getDataType() == 0) && (colorMap.getDataType() == 0) && (colorMap.getNumBands() == 3))
    {



      ColorModel cm = source.getColorModel();
      if ((cm == null) || ((cm != null) && (cm.getColorSpace().isCS_sRGB())))
      {
        int size = colorMap.getNumEntries();
        byte[][] cmap = new byte[3]['Ā'];
        for (int i = 0; i < 3; i++) {
          byte[] band = cmap[i];
          byte[] data = colorMap.getByteData(i);
          int offset = colorMap.getOffset(i);
          int end = offset + size;
          for (int j = 0; j < offset; j++) {
            band[j] = 0;
          }
          for (int j = offset; j < end; j++) {
            band[j] = data[(j - offset)];
          }
          for (int j = end; j < 256; j++) {
            band[j] = -1;
          }
        }
        
        il.setColorModel(new IndexColorModel(8, 256, cmap[0], cmap[1], cmap[2]));
      }
    }
    


    return il;
  }
  

























  public OrderedDitherOpImage(RenderedImage source, Map config, ImageLayout layout, ColorCube colorMap, KernelJAI[] ditherMask)
  {
    super(source, layoutHelper(layout, source, colorMap), config, true);
    


    numBands = colorMap.getNumBands();
    mults = ((int[])colorMap.getMultipliers().clone());
    dims = ((int[])colorMap.getDimsLessOne().clone());
    adjustedOffset = colorMap.getAdjustedOffset();
    

    maskWidth = ditherMask[0].getWidth();
    maskHeight = ditherMask[0].getHeight();
    


    initializeDitherData(sampleModel.getTransferType(), ditherMask);
    

    permitInPlaceOperation();
  }
  



  private class DitherLUT
  {
    private int[] dimsCache;
    

    private int[] multsCache;
    

    private byte[][] maskDataCache;
    

    public int ditherLUTBandStride;
    

    public int ditherLUTRowStride;
    

    public int ditherLUTColStride;
    

    public byte[] ditherLUT;
    


    DitherLUT(int[] dims, int[] mults, byte[][] maskData)
    {
      dimsCache = ((int[])dims.clone());
      multsCache = ((int[])mults.clone());
      maskDataCache = new byte[maskData.length][];
      for (int i = 0; i < maskData.length; i++) {
        maskDataCache[i] = ((byte[])maskData[i].clone());
      }
      

      ditherLUTColStride = 256;
      ditherLUTRowStride = (maskWidth * ditherLUTColStride);
      ditherLUTBandStride = (maskHeight * ditherLUTRowStride);
      














      ditherLUT = new byte[numBands * ditherLUTBandStride];
      
      int pDithBand = 0;
      int maskSize2D = maskWidth * maskHeight;
      for (int band = 0; band < numBands; band++) {
        int step = dims[band];
        int delta = mults[band];
        byte[] maskDataBand = maskData[band];
        int sum = 0;
        for (int gray = 0; gray < 256; gray++) {
          int tmp = sum;
          int frac = tmp & 0xFF;
          int bin = tmp >> 8;
          int lowVal = bin * delta;
          int highVal = lowVal + delta;
          int pDith = pDithBand + gray;
          for (int dcount = 0; dcount < maskSize2D; dcount++) {
            int threshold = maskDataBand[dcount] & 0xFF;
            if (frac > threshold) {
              ditherLUT[pDith] = ((byte)(highVal & 0xFF));
            } else {
              ditherLUT[pDith] = ((byte)(lowVal & 0xFF));
            }
            pDith += 256;
          }
          sum += step;
        }
        pDithBand += ditherLUTBandStride;
      }
    }
    











    public boolean equals(int[] dims, int[] mults, byte[][] maskData)
    {
      if (dims.length != dimsCache.length) {
        return false;
      }
      
      for (int i = 0; i < dims.length; i++) {
        if (dims[i] != dimsCache[i]) { return false;
        }
      }
      
      if (mults.length != multsCache.length) {
        return false;
      }
      
      for (int i = 0; i < mults.length; i++) {
        if (mults[i] != multsCache[i]) { return false;
        }
      }
      
      if (maskData.length != maskDataByte.length) {
        return false;
      }
      
      for (int i = 0; i < maskData.length; i++) {
        if (maskData[i].length != maskDataCache[i].length) return false;
        byte[] refData = maskDataCache[i];
        byte[] data = maskData[i];
        for (int j = 0; j < maskData[i].length; j++) {
          if (data[j] != refData[j]) { return false;
          }
        }
      }
      return true;
    }
  }
  







  private void initializeDitherData(int dataType, KernelJAI[] ditherMask)
  {
    switch (dataType)
    {
    case 0: 
      maskDataByte = new byte[ditherMask.length][];
      for (int i = 0; i < maskDataByte.length; i++) {
        float[] maskData = ditherMask[i].getKernelData();
        maskDataByte[i] = new byte[maskData.length];
        for (int j = 0; j < maskData.length; j++) {
          maskDataByte[i][j] = ((byte)((int)(maskData[j] * 255.0F) & 0xFF));
        }
      }
      

      initializeDitherLUT();
      
      break;
    

    case 1: 
    case 2: 
      int scaleFactor = 65535;
      maskDataInt = new int[ditherMask.length][];
      for (int i = 0; i < maskDataInt.length; i++) {
        float[] maskData = ditherMask[i].getKernelData();
        maskDataInt[i] = new int[maskData.length];
        for (int j = 0; j < maskData.length; j++) {
          maskDataInt[i][j] = ((int)(maskData[j] * scaleFactor));
        }
      }
      
      break;
    

    case 3: 
      long scaleFactor = 4294967295L;
      
      maskDataLong = new long[ditherMask.length][];
      for (int i = 0; i < maskDataLong.length; i++) {
        float[] maskData = ditherMask[i].getKernelData();
        maskDataLong[i] = new long[maskData.length];
        for (int j = 0; j < maskData.length; j++) {
          maskDataLong[i][j] = ((maskData[j] * (float)scaleFactor));
        }
      }
      
      break;
    

    case 4: 
    case 5: 
      maskDataFloat = new float[ditherMask.length][];
      for (int i = 0; i < maskDataFloat.length; i++) {
        maskDataFloat[i] = ditherMask[i].getKernelData();
      }
      
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage0"));
    }
    
  }
  



  private synchronized void initializeDitherLUT()
  {
    if (numBands * maskHeight * maskWidth * 256 > 262144) {
      odType = 0;
      return;
    }
    


    odType = (numBands == 3 ? 1 : 2);
    


    int index = 0;
    while (index < ditherLUTCache.size()) {
      SoftReference lutRef = (SoftReference)ditherLUTCache.get(index);
      DitherLUT lut = (DitherLUT)lutRef.get();
      if (lut == null)
      {

        ditherLUTCache.remove(index);
      } else {
        if (lut.equals(dims, mults, maskDataByte))
        {
          odLUT = lut;
          break;
        }
        
        index++;
      }
    }
    

    if (odLUT == null) {
      odLUT = new DitherLUT(dims, mults, maskDataByte);
      
      if (ditherLUTCache.size() < 4) {
        ditherLUTCache.add(new SoftReference(odLUT));
      }
    }
  }
  










  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    RasterFormatTag[] formatTags = null;
    if ((ImageUtil.isBinary(getSampleModel())) && (!ImageUtil.isBinary(getSourceImage(0).getSampleModel())))
    {


      RenderedImage[] sourceArray = { getSourceImage(0) };
      
      RasterFormatTag[] sourceTags = RasterAccessor.findCompatibleTags(sourceArray, sourceArray[0]);
      
      RasterFormatTag[] destTags = RasterAccessor.findCompatibleTags(sourceArray, this);
      
      formatTags = new RasterFormatTag[] { sourceTags[0], destTags[1] };
    }
    else {
      formatTags = getFormatTags();
    }
    
    RasterAccessor src = new RasterAccessor(sources[0], destRect, formatTags[0], getSource(0).getColorModel());
    

    RasterAccessor dst = new RasterAccessor(dest, destRect, formatTags[1], getColorModel());
    

    switch (src.getDataType()) {
    case 0: 
      computeRectByte(src, dst);
      break;
    case 2: 
      computeRectShort(src, dst);
      break;
    case 1: 
      computeRectUShort(src, dst);
      break;
    case 3: 
      computeRectInt(src, dst);
      break;
    case 4: 
      computeRectFloat(src, dst);
      break;
    case 5: 
      computeRectDouble(src, dst);
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("OrderedDitherOpImage1"));
    }
    
    dst.copyDataToRaster();
  }
  


  private void computeRectByte(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    byte[][] sData = src.getByteDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    byte[] dData = dst.getByteDataArray(0);
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    switch (odType) {
    case 1: 
    case 2: 
      int[] srcLineOffsets = (int[])sBandOffsets.clone();
      int[] srcPixelOffsets = (int[])srcLineOffsets.clone();
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int yMod = (y0 + h) % maskHeight;
        
        if (odType == 1) {
          computeLineByteLUT3(sData, srcPixelOffsets, sPixelStride, dData, dLineOffset, dPixelStride, dwidth, xMod, yMod);
        }
        else
        {
          computeLineByteLUTN(sData, srcPixelOffsets, sPixelStride, dData, dLineOffset, dPixelStride, dwidth, xMod, yMod);
        }
        


        for (int i = 0; i < sbands; i++) {
          srcLineOffsets[i] += sLineStride;
          srcPixelOffsets[i] = srcLineOffsets[i];
        }
        dLineOffset += dLineStride;
      }
      
      break;
    case 0: 
    default: 
      computeRectByteGeneral(sData, sBandOffsets, sLineStride, sPixelStride, dData, dBandOffset, dLineStride, dPixelStride, dwidth, dheight, xMod, y0);
    }
    
  }
  










  private void computeLineByteLUT3(byte[][] sData, int[] sPixelOffsets, int sPixelStride, byte[] dData, int dPixelOffset, int dPixelStride, int dwidth, int xMod, int yMod)
  {
    int ditherLUTBandStride = odLUT.ditherLUTBandStride;
    int ditherLUTRowStride = odLUT.ditherLUTRowStride;
    int ditherLUTColStride = odLUT.ditherLUTColStride;
    byte[] ditherLUT = odLUT.ditherLUT;
    
    int base = adjustedOffset;
    
    int dlut0 = yMod * ditherLUTRowStride;
    int dlut1 = dlut0 + ditherLUTBandStride;
    int dlut2 = dlut1 + ditherLUTBandStride;
    
    int dlutLimit = dlut0 + ditherLUTRowStride;
    
    int xDelta = xMod * ditherLUTColStride;
    int pDtab0 = dlut0 + xDelta;
    int pDtab1 = dlut1 + xDelta;
    int pDtab2 = dlut2 + xDelta;
    
    byte[] sData0 = sData[0];
    byte[] sData1 = sData[1];
    byte[] sData2 = sData[2];
    
    for (int count = dwidth; count > 0; count--) {
      int idx = (ditherLUT[(pDtab0 + (sData0[sPixelOffsets[0]] & 0xFF))] & 0xFF) + (ditherLUT[(pDtab1 + (sData1[sPixelOffsets[1]] & 0xFF))] & 0xFF) + (ditherLUT[(pDtab2 + (sData2[sPixelOffsets[2]] & 0xFF))] & 0xFF);
      



      dData[dPixelOffset] = ((byte)(idx + base & 0xFF));
      
      sPixelOffsets[0] += sPixelStride;
      sPixelOffsets[1] += sPixelStride;
      sPixelOffsets[2] += sPixelStride;
      
      dPixelOffset += dPixelStride;
      
      pDtab0 += ditherLUTColStride;
      
      if (pDtab0 >= dlutLimit) {
        pDtab0 = dlut0;
        pDtab1 = dlut1;
        pDtab2 = dlut2;
      } else {
        pDtab1 += ditherLUTColStride;
        pDtab2 += ditherLUTColStride;
      }
    }
  }
  






  private void computeLineByteLUTN(byte[][] sData, int[] sPixelOffsets, int sPixelStride, byte[] dData, int dPixelOffset, int dPixelStride, int dwidth, int xMod, int yMod)
  {
    int ditherLUTBandStride = odLUT.ditherLUTBandStride;
    int ditherLUTRowStride = odLUT.ditherLUTRowStride;
    int ditherLUTColStride = odLUT.ditherLUTColStride;
    byte[] ditherLUT = odLUT.ditherLUT;
    
    int base = adjustedOffset;
    
    int dlutRow = yMod * ditherLUTRowStride;
    int dlutCol = dlutRow + xMod * ditherLUTColStride;
    int dlutLimit = dlutRow + ditherLUTRowStride;
    
    for (int count = dwidth; count > 0; count--) {
      int dlutBand = dlutCol;
      int idx = base;
      for (int i = 0; i < numBands; i++) {
        idx += (ditherLUT[(dlutBand + (sData[i][sPixelOffsets[i]] & 0xFF))] & 0xFF);
        
        dlutBand += ditherLUTBandStride;
        sPixelOffsets[i] += sPixelStride;
      }
      
      dData[dPixelOffset] = ((byte)(idx & 0xFF));
      
      dPixelOffset += dPixelStride;
      
      dlutCol += ditherLUTColStride;
      
      if (dlutCol >= dlutLimit) {
        dlutCol = dlutRow;
      }
    }
  }
  








  private void computeRectByteGeneral(byte[][] sData, int[] sBandOffsets, int sLineStride, int sPixelStride, byte[] dData, int dBandOffset, int dLineStride, int dPixelStride, int dwidth, int dheight, int xMod, int y0)
  {
    if (adjustedOffset > 0) {
      Arrays.fill(dData, (byte)(adjustedOffset & 0xFF));
    }
    
    int sbands = sBandOffsets.length;
    for (int b = 0; b < sbands; b++) {
      byte[] s = sData[b];
      byte[] d = dData;
      
      byte[] maskData = maskDataByte[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int yMod = (y0 + h) % maskHeight;
        


        int maskYBase = yMod * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        for (int w = 0; w < dwidth; w++) {
          int tmp = (s[sPixelOffset] & 0xFF) * dims[b];
          int frac = tmp & 0xFF;
          tmp >>= 8;
          if (frac > (maskData[maskIndex] & 0xFF)) {
            tmp++;
          }
          

          int result = (d[dPixelOffset] & 0xFF) + tmp * mults[b];
          d[dPixelOffset] = ((byte)(result & 0xFF));
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
      }
    }
    
    if (adjustedOffset < 0)
    {
      int length = dData.length;
      for (int i = 0; i < length; i++) {
        dData[i] = ((byte)((dData[i] & 0xFF) + adjustedOffset));
      }
    }
  }
  


  private void computeRectShort(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    short[][] sData = src.getShortDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    short[] dData = dst.getShortDataArray(0);
    


    if (adjustedOffset != 0) {
      Arrays.fill(dData, (short)(adjustedOffset & 0xFFFF));
    }
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    for (int b = 0; b < sbands; b++) {
      short[] s = sData[b];
      short[] d = dData;
      
      int[] maskData = maskDataInt[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
        


        int maskYBase = (y0 + h) % maskHeight * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        for (int w = 0; w < dwidth; w++) {
          int tmp = (s[sPixelOffset] - Short.MIN_VALUE) * dims[b];
          int frac = tmp & 0xFFFF;
          

          int result = (d[dPixelOffset] & 0xFFFF) + (tmp >> 16) * mults[b];
          
          if (frac > maskData[maskIndex]) {
            result += mults[b];
          }
          d[dPixelOffset] = ((short)(result & 0xFFFF));
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
      }
    }
  }
  


  private void computeRectUShort(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    short[][] sData = src.getShortDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    short[] dData = dst.getShortDataArray(0);
    


    if (adjustedOffset != 0) {
      Arrays.fill(dData, (short)(adjustedOffset & 0xFFFF));
    }
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    for (int b = 0; b < sbands; b++) {
      short[] s = sData[b];
      short[] d = dData;
      
      int[] maskData = maskDataInt[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
        


        int maskYBase = (y0 + h) % maskHeight * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        for (int w = 0; w < dwidth; w++) {
          int tmp = (s[sPixelOffset] & 0xFFFF) * dims[b];
          int frac = tmp & 0xFFFF;
          

          int result = (d[dPixelOffset] & 0xFFFF) + (tmp >> 16) * mults[b];
          
          if (frac > maskData[maskIndex]) {
            result += mults[b];
          }
          d[dPixelOffset] = ((short)(result & 0xFFFF));
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
      }
    }
  }
  


  private void computeRectInt(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    int[][] sData = src.getIntDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    int[] dData = dst.getIntDataArray(0);
    


    if (adjustedOffset != 0) {
      Arrays.fill(dData, adjustedOffset);
    }
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    for (int b = 0; b < sbands; b++) {
      int[] s = sData[b];
      int[] d = dData;
      
      long[] maskData = maskDataLong[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
        


        int maskYBase = (y0 + h) % maskHeight * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        for (int w = 0; w < dwidth; w++) {
          long tmp = (s[sPixelOffset] - -2147483648L) * dims[b];
          

          long frac = tmp & 0xFFFFFFFFFFFFFFFF;
          

          int result = d[dPixelOffset] + (int)(tmp >> 32) * mults[b];
          
          if (frac > maskData[maskIndex]) {
            result += mults[b];
          }
          d[dPixelOffset] = result;
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
      }
    }
  }
  


  private void computeRectFloat(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    float[][] sData = src.getFloatDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    float[] dData = dst.getFloatDataArray(0);
    


    if (adjustedOffset != 0) {
      Arrays.fill(dData, adjustedOffset);
    }
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    for (int b = 0; b < sbands; b++) {
      float[] s = sData[b];
      float[] d = dData;
      
      float[] maskData = maskDataFloat[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
        


        int maskYBase = (y0 + h) % maskHeight * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        for (int w = 0; w < dwidth; w++) {
          int tmp = (int)(s[sPixelOffset] * dims[b]);
          float frac = s[sPixelOffset] * dims[b] - tmp;
          

          float result = d[dPixelOffset] + tmp * mults[b];
          if (frac > maskData[maskIndex]) {
            result += mults[b];
          }
          d[dPixelOffset] = result;
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
      }
    }
  }
  


  private void computeRectDouble(RasterAccessor src, RasterAccessor dst)
  {
    int sbands = src.getNumBands();
    int sLineStride = src.getScanlineStride();
    int sPixelStride = src.getPixelStride();
    int[] sBandOffsets = src.getBandOffsets();
    double[][] sData = src.getDoubleDataArrays();
    
    int dwidth = dst.getWidth();
    int dheight = dst.getHeight();
    int dLineStride = dst.getScanlineStride();
    int dPixelStride = dst.getPixelStride();
    int dBandOffset = dst.getBandOffset(0);
    double[] dData = dst.getDoubleDataArray(0);
    


    if (adjustedOffset != 0) {
      Arrays.fill(dData, adjustedOffset);
    }
    
    int xMod = dst.getX() % maskWidth;
    int y0 = dst.getY();
    
    for (int b = 0; b < sbands; b++) {
      double[] s = sData[b];
      double[] d = dData;
      
      float[] maskData = maskDataFloat[b];
      
      int sLineOffset = sBandOffsets[b];
      int dLineOffset = dBandOffset;
      
      for (int h = 0; h < dheight; h++) {
        int sPixelOffset = sLineOffset;
        int dPixelOffset = dLineOffset;
        
        sLineOffset += sLineStride;
        dLineOffset += dLineStride;
        


        int maskYBase = (y0 + h) % maskHeight * maskWidth;
        


        int maskLimit = maskYBase + maskWidth;
        


        int maskIndex = maskYBase + xMod;
        
        for (int w = 0; w < dwidth; w++) {
          int tmp = (int)(s[sPixelOffset] * dims[b]);
          float frac = (float)(s[sPixelOffset] * dims[b] - tmp);
          

          double result = d[dPixelOffset] + tmp * mults[b];
          if (frac > maskData[maskIndex]) {
            result += mults[b];
          }
          d[dPixelOffset] = result;
          
          sPixelOffset += sPixelStride;
          dPixelOffset += dPixelStride;
          
          maskIndex++; if (maskIndex >= maskLimit) {
            maskIndex = maskYBase;
          }
        }
      }
    }
  }
}
