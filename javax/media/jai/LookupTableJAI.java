package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;











































public class LookupTableJAI
  implements Serializable
{
  transient DataBuffer data;
  private int[] tableOffsets;
  
  public LookupTableJAI(byte[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    this.data = new DataBufferByte(data, data.length);
    initOffsets(1, 0);
  }
  






  public LookupTableJAI(byte[] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, offset);
    this.data = new DataBufferByte(data, data.length);
  }
  






  public LookupTableJAI(byte[][] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, 0);
    this.data = new DataBufferByte(data, data[0].length);
  }
  







  public LookupTableJAI(byte[][] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offset);
    this.data = new DataBufferByte(data, data[0].length);
  }
  







  public LookupTableJAI(byte[][] data, int[] offsets)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offsets);
    this.data = new DataBufferByte(data, data[0].length);
  }
  








  public LookupTableJAI(short[] data, boolean isUShort)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, 0);
    if (isUShort) {
      this.data = new DataBufferUShort(data, data.length);
    } else {
      this.data = new DataBufferShort(data, data.length);
    }
  }
  









  public LookupTableJAI(short[] data, int offset, boolean isUShort)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, offset);
    if (isUShort) {
      this.data = new DataBufferUShort(data, data.length);
    } else {
      this.data = new DataBufferShort(data, data.length);
    }
  }
  








  public LookupTableJAI(short[][] data, boolean isUShort)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, 0);
    if (isUShort) {
      this.data = new DataBufferUShort(data, data[0].length);
    } else {
      this.data = new DataBufferShort(data, data[0].length);
    }
  }
  









  public LookupTableJAI(short[][] data, int offset, boolean isUShort)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offset);
    if (isUShort) {
      this.data = new DataBufferUShort(data, data[0].length);
    } else {
      this.data = new DataBufferShort(data, data[0].length);
    }
  }
  









  public LookupTableJAI(short[][] data, int[] offsets, boolean isUShort)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offsets);
    
    if (isUShort) {
      this.data = new DataBufferUShort(data, data[0].length);
    } else {
      this.data = new DataBufferShort(data, data[0].length);
    }
  }
  





  public LookupTableJAI(int[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, 0);
    this.data = new DataBufferInt(data, data.length);
  }
  






  public LookupTableJAI(int[] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, offset);
    this.data = new DataBufferInt(data, data.length);
  }
  






  public LookupTableJAI(int[][] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, 0);
    this.data = new DataBufferInt(data, data[0].length);
  }
  







  public LookupTableJAI(int[][] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offset);
    this.data = new DataBufferInt(data, data[0].length);
  }
  







  public LookupTableJAI(int[][] data, int[] offsets)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offsets);
    this.data = new DataBufferInt(data, data[0].length);
  }
  





  public LookupTableJAI(float[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, 0);
    this.data = DataBufferUtils.createDataBufferFloat(data, data.length);
  }
  






  public LookupTableJAI(float[] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, offset);
    this.data = DataBufferUtils.createDataBufferFloat(data, data.length);
  }
  






  public LookupTableJAI(float[][] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, 0);
    this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
  }
  







  public LookupTableJAI(float[][] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offset);
    this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
  }
  







  public LookupTableJAI(float[][] data, int[] offsets)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offsets);
    this.data = DataBufferUtils.createDataBufferFloat(data, data[0].length);
  }
  





  public LookupTableJAI(double[] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, 0);
    this.data = DataBufferUtils.createDataBufferDouble(data, data.length);
  }
  






  public LookupTableJAI(double[] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(1, offset);
    this.data = DataBufferUtils.createDataBufferDouble(data, data.length);
  }
  






  public LookupTableJAI(double[][] data)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, 0);
    this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
  }
  







  public LookupTableJAI(double[][] data, int offset)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offset);
    this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
  }
  







  public LookupTableJAI(double[][] data, int[] offsets)
  {
    if (data == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    initOffsets(data.length, offsets);
    this.data = DataBufferUtils.createDataBufferDouble(data, data[0].length);
  }
  


  public DataBuffer getData()
  {
    return data;
  }
  



  public byte[][] getByteData()
  {
    return (data instanceof DataBufferByte) ? ((DataBufferByte)data).getBankData() : (byte[][])null;
  }
  




  public byte[] getByteData(int band)
  {
    return (data instanceof DataBufferByte) ? ((DataBufferByte)data).getData(band) : null;
  }
  






  public short[][] getShortData()
  {
    if ((data instanceof DataBufferUShort))
      return ((DataBufferUShort)data).getBankData();
    if ((data instanceof DataBufferShort)) {
      return ((DataBufferShort)data).getBankData();
    }
    return (short[][])null;
  }
  





  public short[] getShortData(int band)
  {
    if ((data instanceof DataBufferUShort))
      return ((DataBufferUShort)data).getData(band);
    if ((data instanceof DataBufferShort)) {
      return ((DataBufferShort)data).getData(band);
    }
    return null;
  }
  





  public int[][] getIntData()
  {
    return (data instanceof DataBufferInt) ? ((DataBufferInt)data).getBankData() : (int[][])null;
  }
  





  public int[] getIntData(int band)
  {
    return (data instanceof DataBufferInt) ? ((DataBufferInt)data).getData(band) : null;
  }
  





  public float[][] getFloatData()
  {
    return data.getDataType() == 4 ? DataBufferUtils.getBankDataFloat(data) : (float[][])null;
  }
  





  public float[] getFloatData(int band)
  {
    return data.getDataType() == 4 ? DataBufferUtils.getDataFloat(data, band) : null;
  }
  





  public double[][] getDoubleData()
  {
    return data.getDataType() == 5 ? DataBufferUtils.getBankDataDouble(data) : (double[][])null;
  }
  





  public double[] getDoubleData(int band)
  {
    return data.getDataType() == 5 ? DataBufferUtils.getDataDouble(data, band) : null;
  }
  

  public int[] getOffsets()
  {
    return tableOffsets;
  }
  



  public int getOffset()
  {
    return tableOffsets[0];
  }
  



  public int getOffset(int band)
  {
    return tableOffsets[band];
  }
  
  public int getNumBands()
  {
    return data.getNumBanks();
  }
  



  public int getNumEntries()
  {
    return data.getSize();
  }
  


  public int getDataType()
  {
    return data.getDataType();
  }
  






  public int getDestNumBands(int srcNumBands)
  {
    int tblNumBands = getNumBands();
    return srcNumBands == 1 ? tblNumBands : srcNumBands;
  }
  











  public SampleModel getDestSampleModel(SampleModel srcSampleModel)
  {
    if (srcSampleModel == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return getDestSampleModel(srcSampleModel, srcSampleModel.getWidth(), srcSampleModel.getHeight());
  }
  
















  public SampleModel getDestSampleModel(SampleModel srcSampleModel, int width, int height)
  {
    if (srcSampleModel == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!isIntegralDataType(srcSampleModel)) {
      return null;
    }
    
    return RasterFactory.createComponentSampleModel(srcSampleModel, getDataType(), width, height, getDestNumBands(srcSampleModel.getNumBands()));
  }
  







  public boolean isIntegralDataType(SampleModel sampleModel)
  {
    if (sampleModel == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return isIntegralDataType(sampleModel.getTransferType());
  }
  



  public boolean isIntegralDataType(int dataType)
  {
    if ((dataType == 0) || (dataType == 1) || (dataType == 2) || (dataType == 3))
    {


      return true;
    }
    return false;
  }
  







  public int lookup(int band, int value)
  {
    return data.getElem(band, value - tableOffsets[band]);
  }
  






  public float lookupFloat(int band, int value)
  {
    return data.getElemFloat(band, value - tableOffsets[band]);
  }
  






  public double lookupDouble(int band, int value)
  {
    return data.getElemDouble(band, value - tableOffsets[band]);
  }
  












  public WritableRaster lookup(WritableRaster src)
  {
    if (src == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    return lookup(src, src, src.getBounds());
  }
  



































  public WritableRaster lookup(Raster src, WritableRaster dst, Rectangle rect)
  {
    if (src == null) {
      throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI1"));
    }
    

    SampleModel srcSampleModel = src.getSampleModel();
    if (!isIntegralDataType(srcSampleModel)) {
      throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI2"));
    }
    


    if (rect == null) {
      rect = src.getBounds();
    } else {
      rect = rect.intersection(src.getBounds());
    }
    
    if (dst != null) {
      rect = rect.intersection(dst.getBounds());
    }
    
    SampleModel dstSampleModel;
    
    if (dst == null) {
      SampleModel dstSampleModel = getDestSampleModel(srcSampleModel, width, height);
      
      dst = RasterFactory.createWritableRaster(dstSampleModel, new Point(x, y));
    }
    else
    {
      dstSampleModel = dst.getSampleModel();
      
      if ((dstSampleModel.getTransferType() != getDataType()) || (dstSampleModel.getNumBands() != getDestNumBands(srcSampleModel.getNumBands())))
      {

        throw new IllegalArgumentException(JaiI18N.getString("LookupTableJAI3"));
      }
    }
    


    int sTagID = RasterAccessor.findCompatibleTag(null, srcSampleModel);
    int dTagID = RasterAccessor.findCompatibleTag(null, dstSampleModel);
    
    RasterFormatTag sTag = new RasterFormatTag(srcSampleModel, sTagID);
    RasterFormatTag dTag = new RasterFormatTag(dstSampleModel, dTagID);
    
    RasterAccessor s = new RasterAccessor(src, rect, sTag, null);
    RasterAccessor d = new RasterAccessor(dst, rect, dTag, null);
    
    int srcNumBands = s.getNumBands();
    int srcDataType = s.getDataType();
    
    int tblNumBands = getNumBands();
    int tblDataType = getDataType();
    
    int dstWidth = d.getWidth();
    int dstHeight = d.getHeight();
    int dstNumBands = d.getNumBands();
    int dstDataType = d.getDataType();
    

    int srcLineStride = s.getScanlineStride();
    int srcPixelStride = s.getPixelStride();
    int[] srcBandOffsets = s.getBandOffsets();
    
    byte[][] bSrcData = s.getByteDataArrays();
    short[][] sSrcData = s.getShortDataArrays();
    int[][] iSrcData = s.getIntDataArrays();
    
    if (srcNumBands < dstNumBands) {
      int offset0 = srcBandOffsets[0];
      srcBandOffsets = new int[dstNumBands];
      for (int i = 0; i < dstNumBands; i++) {
        srcBandOffsets[i] = offset0;
      }
      
      switch (srcDataType) {
      case 0: 
        byte[] bData0 = bSrcData[0];
        bSrcData = new byte[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          bSrcData[i] = bData0;
        }
        break;
      case 1: 
      case 2: 
        short[] sData0 = sSrcData[0];
        sSrcData = new short[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          sSrcData[i] = sData0;
        }
        break;
      case 3: 
        int[] iData0 = iSrcData[0];
        iSrcData = new int[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          iSrcData[i] = iData0;
        }
      }
      
    }
    

    int[] tblOffsets = getOffsets();
    
    byte[][] bTblData = getByteData();
    short[][] sTblData = getShortData();
    int[][] iTblData = getIntData();
    float[][] fTblData = getFloatData();
    double[][] dTblData = getDoubleData();
    
    if (tblNumBands < dstNumBands) {
      int offset0 = tblOffsets[0];
      tblOffsets = new int[dstNumBands];
      for (int i = 0; i < dstNumBands; i++) {
        tblOffsets[i] = offset0;
      }
      
      switch (tblDataType) {
      case 0: 
        byte[] bData0 = bTblData[0];
        bTblData = new byte[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          bTblData[i] = bData0;
        }
        break;
      case 1: 
      case 2: 
        short[] sData0 = sTblData[0];
        sTblData = new short[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          sTblData[i] = sData0;
        }
        break;
      case 3: 
        int[] iData0 = iTblData[0];
        iTblData = new int[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          iTblData[i] = iData0;
        }
        break;
      case 4: 
        float[] fData0 = fTblData[0];
        fTblData = new float[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          fTblData[i] = fData0;
        }
        break;
      case 5: 
        double[] dData0 = dTblData[0];
        dTblData = new double[dstNumBands][];
        for (int i = 0; i < dstNumBands; i++) {
          dTblData[i] = dData0;
        }
      }
      
    }
    
    int dstLineStride = d.getScanlineStride();
    int dstPixelStride = d.getPixelStride();
    int[] dstBandOffsets = d.getBandOffsets();
    
    byte[][] bDstData = d.getByteDataArrays();
    short[][] sDstData = d.getShortDataArrays();
    int[][] iDstData = d.getIntDataArrays();
    float[][] fDstData = d.getFloatDataArrays();
    double[][] dDstData = d.getDoubleDataArrays();
    
    switch (dstDataType) {
    case 0: 
      switch (srcDataType) {
      case 0: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, bTblData);
        




        break;
      
      case 1: 
        lookupU(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, bTblData);
        




        break;
      
      case 2: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, bTblData);
        




        break;
      
      case 3: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, bDstData, tblOffsets, bTblData);
      }
      
      




      break;
    
    case 1: 
    case 2: 
      switch (srcDataType) {
      case 0: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, sTblData);
        




        break;
      
      case 1: 
        lookupU(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, sTblData);
        




        break;
      
      case 2: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, sTblData);
        




        break;
      
      case 3: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, sDstData, tblOffsets, sTblData);
      }
      
      




      break;
    
    case 3: 
      switch (srcDataType) {
      case 0: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, iTblData);
        




        break;
      
      case 1: 
        lookupU(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, iTblData);
        




        break;
      
      case 2: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, iTblData);
        




        break;
      
      case 3: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, iDstData, tblOffsets, iTblData);
      }
      
      




      break;
    
    case 4: 
      switch (srcDataType) {
      case 0: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, fTblData);
        




        break;
      
      case 1: 
        lookupU(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, fTblData);
        




        break;
      
      case 2: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, fTblData);
        




        break;
      
      case 3: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, fDstData, tblOffsets, fTblData);
      }
      
      




      break;
    
    case 5: 
      switch (srcDataType) {
      case 0: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, bSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, dTblData);
        




        break;
      
      case 1: 
        lookupU(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, dTblData);
        




        break;
      
      case 2: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, sSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, dTblData);
        




        break;
      
      case 3: 
        lookup(srcLineStride, srcPixelStride, srcBandOffsets, iSrcData, dstWidth, dstHeight, dstNumBands, dstLineStride, dstPixelStride, dstBandOffsets, dDstData, tblOffsets, dTblData);
      }
      
      


      break;
    }
    
    

    d.copyDataToRaster();
    
    return dst;
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      byte[] s = srcData[b];
      byte[] d = dstData[b];
      byte[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFF) - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      byte[] d = dstData[b];
      byte[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFFFF) - tblOffset)];
          

          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      byte[] d = dstData[b];
      byte[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, byte[][] dstData, int[] tblOffsets, byte[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      int[] s = srcData[b];
      byte[] d = dstData[b];
      byte[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      byte[] s = srcData[b];
      short[] d = dstData[b];
      short[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFF) - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      short[] d = dstData[b];
      short[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFFFF) - tblOffset)];
          

          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      short[] d = dstData[b];
      short[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, short[][] dstData, int[] tblOffsets, short[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      int[] s = srcData[b];
      short[] d = dstData[b];
      short[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData)
  {
    if (tblData == null) {
      for (int b = 0; b < bands; b++) {
        byte[] s = srcData[b];
        int[] d = dstData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = data.getElem(b, s[srcPixelOffset] & 0xFF);
            

            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    } else {
      for (int b = 0; b < bands; b++) {
        byte[] s = srcData[b];
        int[] d = dstData[b];
        int[] t = tblData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        int tblOffset = tblOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFF) - tblOffset)];
            

            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  





  private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData)
  {
    if (tblData == null) {
      for (int b = 0; b < bands; b++) {
        short[] s = srcData[b];
        int[] d = dstData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = data.getElem(b, s[srcPixelOffset] & 0xFFFF);
            

            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    } else {
      for (int b = 0; b < bands; b++) {
        short[] s = srcData[b];
        int[] d = dstData[b];
        int[] t = tblData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        int tblOffset = tblOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFFFF) - tblOffset)];
            

            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData)
  {
    if (tblData == null) {
      for (int b = 0; b < bands; b++) {
        short[] s = srcData[b];
        int[] d = dstData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = data.getElem(b, s[srcPixelOffset]);
            

            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    } else {
      for (int b = 0; b < bands; b++) {
        short[] s = srcData[b];
        int[] d = dstData[b];
        int[] t = tblData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        int tblOffset = tblOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
            
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, int[][] dstData, int[] tblOffsets, int[][] tblData)
  {
    if (tblData == null) {
      for (int b = 0; b < bands; b++) {
        int[] s = srcData[b];
        int[] d = dstData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = data.getElem(b, s[srcPixelOffset]);
            
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    } else {
      for (int b = 0; b < bands; b++) {
        int[] s = srcData[b];
        int[] d = dstData[b];
        int[] t = tblData[b];
        
        int srcLineOffset = srcBandOffsets[b];
        int dstLineOffset = dstBandOffsets[b];
        int tblOffset = tblOffsets[b];
        
        for (int h = 0; h < height; h++) {
          int srcPixelOffset = srcLineOffset;
          int dstPixelOffset = dstLineOffset;
          
          srcLineOffset += srcLineStride;
          dstLineOffset += dstLineStride;
          
          for (int w = 0; w < width; w++) {
            d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
            
            srcPixelOffset += srcPixelStride;
            dstPixelOffset += dstPixelStride;
          }
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      byte[] s = srcData[b];
      float[] d = dstData[b];
      float[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFF) - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      float[] d = dstData[b];
      float[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFFFF) - tblOffset)];
          

          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      float[] d = dstData[b];
      float[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, float[][] dstData, int[] tblOffsets, float[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      int[] s = srcData[b];
      float[] d = dstData[b];
      float[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, byte[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      byte[] s = srcData[b];
      double[] d = dstData[b];
      double[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFF) - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookupU(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      double[] d = dstData[b];
      double[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[((s[srcPixelOffset] & 0xFFFF) - tblOffset)];
          

          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, short[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      short[] s = srcData[b];
      double[] d = dstData[b];
      double[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  





  private void lookup(int srcLineStride, int srcPixelStride, int[] srcBandOffsets, int[][] srcData, int width, int height, int bands, int dstLineStride, int dstPixelStride, int[] dstBandOffsets, double[][] dstData, int[] tblOffsets, double[][] tblData)
  {
    for (int b = 0; b < bands; b++) {
      int[] s = srcData[b];
      double[] d = dstData[b];
      double[] t = tblData[b];
      
      int srcLineOffset = srcBandOffsets[b];
      int dstLineOffset = dstBandOffsets[b];
      int tblOffset = tblOffsets[b];
      
      for (int h = 0; h < height; h++) {
        int srcPixelOffset = srcLineOffset;
        int dstPixelOffset = dstLineOffset;
        
        srcLineOffset += srcLineStride;
        dstLineOffset += dstLineStride;
        
        for (int w = 0; w < width; w++) {
          d[dstPixelOffset] = t[(s[srcPixelOffset] - tblOffset)];
          
          srcPixelOffset += srcPixelStride;
          dstPixelOffset += dstPixelStride;
        }
      }
    }
  }
  
















  public int findNearestEntry(float[] pixel)
  {
    if (pixel == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int dataType = data.getDataType();
    int numBands = getNumBands();
    int numEntries = getNumEntries();
    int index = -1;
    
    if (dataType == 0) {
      byte[][] buffer = getByteData();
      

      float minDistance = 0.0F;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        float delta = pixel[b] - (buffer[b][0] & 0xFF);
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        float distance = 0.0F;
        for (int b = 0; b < numBands; b++) {
          float delta = pixel[b] - (buffer[b][i] & 0xFF);
          
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    } else if (dataType == 2) {
      short[][] buffer = getShortData();
      

      float minDistance = 0.0F;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        float delta = pixel[b] - buffer[b][0];
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        float distance = 0.0F;
        for (int b = 0; b < numBands; b++) {
          float delta = pixel[b] - buffer[b][i];
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    } else if (dataType == 1) {
      short[][] buffer = getShortData();
      

      float minDistance = 0.0F;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        float delta = pixel[b] - (buffer[b][0] & 0xFFFF);
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        float distance = 0.0F;
        for (int b = 0; b < numBands; b++) {
          float delta = pixel[b] - (buffer[b][i] & 0xFFFF);
          
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    } else if (dataType == 3) {
      int[][] buffer = getIntData();
      

      float minDistance = 0.0F;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        float delta = pixel[b] - buffer[b][0];
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        float distance = 0.0F;
        for (int b = 0; b < numBands; b++) {
          float delta = pixel[b] - buffer[b][i];
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    } else if (dataType == 4) {
      float[][] buffer = getFloatData();
      

      float minDistance = 0.0F;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        float delta = pixel[b] - buffer[b][0];
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        float distance = 0.0F;
        for (int b = 0; b < numBands; b++) {
          float delta = pixel[b] - buffer[b][i];
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    } else if (dataType == 5) {
      double[][] buffer = getDoubleData();
      

      double minDistance = 0.0D;
      index = 0;
      for (int b = 0; b < numBands; b++) {
        double delta = pixel[b] - buffer[b][0];
        minDistance += delta * delta;
      }
      


      for (int i = 1; i < numEntries; i++) {
        double distance = 0.0D;
        for (int b = 0; b < numBands; b++) {
          double delta = pixel[b] - buffer[b][i];
          distance += delta * delta;
        }
        
        if (distance < minDistance) {
          minDistance = distance;
          index = i;
        }
      }
    }
    else {
      throw new RuntimeException(JaiI18N.getString("LookupTableJAI0"));
    }
    


    return index == -1 ? index : index + getOffset();
  }
  



  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    out.writeObject(SerializerFactory.getState(data));
  }
  




  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    Object object = in.readObject();
    SerializableState ss = (SerializableState)object;
    data = ((DataBuffer)ss.getObject());
  }
  
  private void initOffsets(int nbands, int offset) {
    tableOffsets = new int[nbands];
    for (int i = 0; i < nbands; i++) {
      tableOffsets[i] = offset;
    }
  }
  
  private void initOffsets(int nbands, int[] offset) {
    tableOffsets = new int[nbands];
    for (int i = 0; i < nbands; i++) {
      tableOffsets[i] = offset[i];
    }
  }
}
