package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.awt.image.BytePackedRaster;













































































































































public class RasterAccessor
{
  private static final int COPY_MASK_SHIFT = 7;
  private static final int COPY_MASK_SIZE = 2;
  public static final int COPY_MASK = 384;
  public static final int UNCOPIED = 0;
  public static final int COPIED = 128;
  private static final int EXPANSION_MASK_SHIFT = 9;
  private static final int EXPANSION_MASK_SIZE = 2;
  public static final int EXPANSION_MASK = 1536;
  public static final int DEFAULTEXPANSION = 0;
  public static final int EXPANDED = 512;
  public static final int UNEXPANDED = 1024;
  public static final int DATATYPE_MASK = 127;
  public static final int TAG_BYTE_UNCOPIED = 0;
  public static final int TAG_USHORT_UNCOPIED = 1;
  public static final int TAG_SHORT_UNCOPIED = 2;
  public static final int TAG_INT_UNCOPIED = 3;
  public static final int TAG_FLOAT_UNCOPIED = 4;
  public static final int TAG_DOUBLE_UNCOPIED = 5;
  public static final int TAG_INT_COPIED = 131;
  public static final int TAG_FLOAT_COPIED = 132;
  public static final int TAG_DOUBLE_COPIED = 133;
  public static final int TAG_BYTE_EXPANDED = 512;
  private static final int TAG_BINARY = 1152;
  protected Raster raster;
  protected int rectWidth;
  protected int rectHeight;
  protected int rectX;
  protected int rectY;
  protected int formatTagID;
  protected byte[] binaryDataArray = null;
  











  protected byte[][] byteDataArrays = (byte[][])null;
  








  protected short[][] shortDataArrays = (short[][])null;
  







  protected int[][] intDataArrays = (int[][])null;
  







  protected float[][] floatDataArrays = (float[][])null;
  







  protected double[][] doubleDataArrays = (double[][])null;
  



  protected int[] bandDataOffsets;
  


  protected int[] bandOffsets;
  


  protected int numBands;
  


  protected int scanlineStride;
  


  protected int pixelStride;
  



  public static RasterFormatTag[] findCompatibleTags(RenderedImage[] srcs, RenderedImage dst)
  {
    int[] tagIDs;
    


    int[] tagIDs;
    


    if (srcs != null) {
      tagIDs = new int[srcs.length + 1];
    } else {
      tagIDs = new int[1];
    }
    SampleModel dstSampleModel = dst.getSampleModel();
    int dstDataType = dstSampleModel.getTransferType();
    
    int defaultDataType = dstDataType;
    boolean binaryDst = ImageUtil.isBinary(dstSampleModel);
    if (binaryDst) {
      defaultDataType = 0;
    } else if ((dstDataType == 0) || (dstDataType == 1) || (dstDataType == 2))
    {

      defaultDataType = 3;
    }
    

    if (srcs != null) {
      int numSources = srcs.length;
      
      for (int i = 0; i < numSources; i++) {
        SampleModel srcSampleModel = srcs[i].getSampleModel();
        int srcDataType = srcSampleModel.getTransferType();
        if (((!binaryDst) || (!ImageUtil.isBinary(srcSampleModel))) && (srcDataType > defaultDataType))
        {
          defaultDataType = srcDataType;
        }
      }
    }
    


    int tagID = defaultDataType | 0x80;
    
    if ((dstSampleModel instanceof ComponentSampleModel)) {
      if (srcs != null) {
        int numSources = srcs.length;
        
        for (int i = 0; i < numSources; i++) {
          SampleModel srcSampleModel = srcs[i].getSampleModel();
          int srcDataType = srcSampleModel.getTransferType();
          if ((!(srcSampleModel instanceof ComponentSampleModel)) || (srcDataType != dstDataType)) {
            break;
          }
        }
        
        if (i == numSources) {
          tagID = dstDataType | 0x0;
        }
      } else {
        tagID = dstDataType | 0x0;
      }
    }
    



    RasterFormatTag[] rft = new RasterFormatTag[tagIDs.length];
    if (srcs != null) {
      for (int i = 0; i < srcs.length; i++)
      {
        if ((srcs[i].getColorModel() instanceof IndexColorModel)) {
          if ((dst.getColorModel() instanceof IndexColorModel)) {
            tagIDs[i] = (tagID | 0x400);
          } else {
            tagIDs[i] = (tagID | 0x200);
          }
        } else if (((srcs[i].getColorModel() instanceof ComponentColorModel)) || ((binaryDst) && (ImageUtil.isBinary(srcs[i].getSampleModel()))))
        {


          tagIDs[i] = (tagID | 0x400);
        } else {
          tagIDs[i] = (tagID | 0x0);
        }
      }
      tagIDs[srcs.length] = (tagID | 0x400);
      
      for (int i = 0; i < srcs.length; i++) {
        rft[i] = new RasterFormatTag(srcs[i].getSampleModel(), tagIDs[i]);
      }
      

      rft[srcs.length] = new RasterFormatTag(dstSampleModel, tagIDs[srcs.length]);
    }
    else {
      rft[0] = new RasterFormatTag(dstSampleModel, tagID | 0x400);
    }
    
    return rft;
  }
  







  public static int findCompatibleTag(SampleModel[] srcSampleModels, SampleModel dstSampleModel)
  {
    int dstDataType = dstSampleModel.getTransferType();
    
    int tag = dstDataType | 0x80;
    if (ImageUtil.isBinary(dstSampleModel)) {
      tag = 128;
    } else if ((dstDataType == 0) || (dstDataType == 1) || (dstDataType == 2))
    {

      tag = 131;
    }
    
    if ((dstSampleModel instanceof ComponentSampleModel)) {
      if (srcSampleModels != null) {
        int numSources = srcSampleModels.length;
        
        for (int i = 0; i < numSources; i++) {
          int srcDataType = srcSampleModels[i].getTransferType();
          
          if ((!(srcSampleModels[i] instanceof ComponentSampleModel)) || (srcDataType != dstDataType)) {
            break;
          }
        }
        
        if (i == numSources) {
          tag = dstDataType | 0x0;
        }
      } else {
        tag = dstDataType | 0x0;
      }
    }
    return tag | 0x400;
  }
  



























  public RasterAccessor(Raster raster, Rectangle rect, RasterFormatTag rft, ColorModel theColorModel)
  {
    if ((raster == null) || (rect == null) || (rft == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    


    if (!raster.getBounds().contains(rect)) {
      throw new IllegalArgumentException(JaiI18N.getString("RasterAccessor2"));
    }
    

    this.raster = raster;
    rectX = x;
    rectY = y;
    rectWidth = width;
    rectHeight = height;
    formatTagID = rft.getFormatTagID();
    if ((formatTagID & 0x180) == 0)
    {
      numBands = rft.getNumBands();
      pixelStride = rft.getPixelStride();
      
      ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
      
      scanlineStride = csm.getScanlineStride();
      
      int[] bankIndices = null;
      






      if (rft.isPixelSequential()) {
        bandOffsets = rft.getBandOffsets();
        bankIndices = rft.getBankIndices();
      } else {
        bandOffsets = csm.getBandOffsets();
        bankIndices = csm.getBankIndices();
      }
      
      bandDataOffsets = new int[numBands];
      
      int[] dataBufferOffsets = raster.getDataBuffer().getOffsets();
      
      int subRasterOffset = (rectY - raster.getSampleModelTranslateY()) * scanlineStride + (rectX - raster.getSampleModelTranslateX()) * pixelStride;
      


      if (dataBufferOffsets.length == 1) {
        int theDataBufferOffset = dataBufferOffsets[0];
        for (int i = 0; i < numBands; i++) {
          bandDataOffsets[i] = (bandOffsets[i] + theDataBufferOffset + subRasterOffset);
        }
      }
      else if (dataBufferOffsets.length == bandDataOffsets.length) {
        for (int i = 0; i < numBands; i++) {
          bandDataOffsets[i] = (bandOffsets[i] + dataBufferOffsets[i] + subRasterOffset);
        }
      }
      else {
        throw new RuntimeException(JaiI18N.getString("RasterAccessor0"));
      }
      
      switch (formatTagID & 0x7F) {
      case 0: 
        DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
        byteDataArrays = new byte[numBands][];
        for (int i = 0; i < numBands; i++) {
          byteDataArrays[i] = dbb.getData(bankIndices[i]);
        }
        break;
      
      case 1: 
        DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
        
        shortDataArrays = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          shortDataArrays[i] = dbus.getData(bankIndices[i]);
        }
        break;
      
      case 2: 
        DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
        shortDataArrays = new short[numBands][];
        for (int i = 0; i < numBands; i++) {
          shortDataArrays[i] = dbs.getData(bankIndices[i]);
        }
        break;
      
      case 3: 
        DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
        intDataArrays = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
          intDataArrays[i] = dbi.getData(bankIndices[i]);
        }
        break;
      
      case 4: 
        DataBuffer dbf = raster.getDataBuffer();
        floatDataArrays = new float[numBands][];
        for (int i = 0; i < numBands; i++) {
          floatDataArrays[i] = DataBufferUtils.getDataFloat(dbf, bankIndices[i]);
        }
        
        break;
      
      case 5: 
        DataBuffer dbd = raster.getDataBuffer();
        doubleDataArrays = new double[numBands][];
        for (int i = 0; i < numBands; i++) {
          doubleDataArrays[i] = DataBufferUtils.getDataDouble(dbd, bankIndices[i]);
        }
      }
      
      

      if (((formatTagID & 0x600) == 512) && ((theColorModel instanceof IndexColorModel)))
      {
        IndexColorModel icm = (IndexColorModel)theColorModel;
        
        int newNumBands = icm.getNumComponents();
        
        int mapSize = icm.getMapSize();
        int[] newBandDataOffsets = new int[newNumBands];
        int newScanlineStride = rectWidth * newNumBands;
        int newPixelStride = newNumBands;
        byte[][] ctable = new byte[newNumBands][mapSize];
        
        icm.getReds(ctable[0]);
        icm.getGreens(ctable[1]);
        icm.getBlues(ctable[2]);
        byte[] rtable = ctable[0];
        byte[] gtable = ctable[1];
        byte[] btable = ctable[2];
        
        byte[] atable = null;
        if (newNumBands == 4) {
          icm.getAlphas(ctable[3]);
          atable = ctable[3];
        }
        
        for (int i = 0; i < newNumBands; i++) {
          newBandDataOffsets[i] = i;
        }
        
        switch (formatTagID & 0x7F) {
        case 0: 
          byte[] newBArray = new byte[rectWidth * rectHeight * newNumBands];
          
          byte[] byteDataArray = byteDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = byteDataArray[pixelOffset] & 0xFF;
              for (int k = 0; k < newNumBands; k++) {
                newBArray[(newPixelOffset + k)] = ctable[k][index];
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          byteDataArrays = new byte[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            byteDataArrays[i] = newBArray;
          }
          
          break;
        
        case 1: 
          short[] newIArray = new short[rectWidth * rectHeight * newNumBands];
          
          short[] shortDataArray = shortDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = shortDataArray[pixelOffset] & 0xFFFF;
              for (int k = 0; k < newNumBands; k++) {
                newIArray[(newPixelOffset + k)] = ((short)(ctable[k][index] & 0xFF));
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          
          shortDataArrays = new short[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            shortDataArrays[i] = newIArray;
          }
          
          break;
        
        case 2: 
          short[] newIArray = new short[rectWidth * rectHeight * newNumBands];
          
          short[] shortDataArray = shortDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = shortDataArray[pixelOffset];
              for (int k = 0; k < newNumBands; k++) {
                newIArray[(newPixelOffset + k)] = ((short)(ctable[k][index] & 0xFF));
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          
          shortDataArrays = new short[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            shortDataArrays[i] = newIArray;
          }
          
          break;
        
        case 3: 
          int[] newIArray = new int[rectWidth * rectHeight * newNumBands];
          
          int[] intDataArray = intDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = intDataArray[pixelOffset];
              for (int k = 0; k < newNumBands; k++) {
                newIArray[(newPixelOffset + k)] = (ctable[k][index] & 0xFF);
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          
          intDataArrays = new int[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            intDataArrays[i] = newIArray;
          }
          
          break;
        
        case 4: 
          float[] newFArray = new float[rectWidth * rectHeight * newNumBands];
          
          float[] floatDataArray = floatDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = (int)floatDataArray[pixelOffset];
              for (int k = 0; k < newNumBands; k++) {
                newFArray[(newPixelOffset + k)] = (ctable[k][index] & 0xFF);
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          floatDataArrays = new float[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            floatDataArrays[i] = newFArray;
          }
          
          break;
        
        case 5: 
          double[] newDArray = new double[rectWidth * rectHeight * newNumBands];
          
          double[] doubleDataArray = doubleDataArrays[0];
          int scanlineOffset = bandDataOffsets[0];
          int newScanlineOffset = 0;
          for (int j = 0; j < rectHeight; j++) {
            int pixelOffset = scanlineOffset;
            int newPixelOffset = newScanlineOffset;
            for (int i = 0; i < rectWidth; i++) {
              int index = (int)doubleDataArray[pixelOffset];
              for (int k = 0; k < newNumBands; k++) {
                newDArray[(newPixelOffset + k)] = (ctable[k][index] & 0xFF);
              }
              
              pixelOffset += pixelStride;
              newPixelOffset += newPixelStride;
            }
            scanlineOffset += scanlineStride;
            newScanlineOffset += newScanlineStride;
          }
          doubleDataArrays = new double[newNumBands][];
          for (int i = 0; i < newNumBands; i++) {
            doubleDataArrays[i] = newDArray;
          }
        }
        
        
        numBands = newNumBands;
        pixelStride = newPixelStride;
        scanlineStride = newScanlineStride;
        bandDataOffsets = newBandDataOffsets;
        bandOffsets = newBandDataOffsets;
      }
    } else if (((formatTagID & 0x180) == 128) && ((formatTagID & 0x600) != 1024) && (theColorModel != null))
    {

      numBands = ((theColorModel instanceof IndexColorModel) ? theColorModel.getNumComponents() : raster.getSampleModel().getNumBands());
      

      pixelStride = numBands;
      scanlineStride = (rectWidth * numBands);
      bandOffsets = new int[numBands];
      
      for (int i = 0; i < numBands; i++) {
        bandOffsets[i] = i;
      }
      bandDataOffsets = bandOffsets;
      
      Object odata = null;
      int offset = 0;
      
      int[] components = new int[theColorModel.getNumComponents()];
      
      switch (formatTagID & 0x7F)
      {
      case 3: 
        int[] idata = new int[rectWidth * rectHeight * numBands];
        intDataArrays = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
          intDataArrays[i] = idata;
        }
        
        odata = raster.getDataElements(rectX, rectY, null);
        offset = 0;
        

        byte[] bdata = null;
        if ((raster instanceof BytePackedRaster)) {
          bdata = (byte[])odata;
        }
        
        for (int j = rectY; j < rectY + rectHeight; j++) {
          for (int i = rectX; i < rectX + rectWidth; i++) {
            if (bdata != null) {
              bdata[0] = ((byte)raster.getSample(i, j, 0));
            } else {
              raster.getDataElements(i, j, odata);
            }
            
            theColorModel.getComponents(odata, components, 0);
            
            idata[offset] = components[0];
            idata[(offset + 1)] = components[1];
            idata[(offset + 2)] = components[2];
            if (numBands > 3) {
              idata[(offset + 3)] = components[3];
            }
            
            offset += pixelStride;
          }
        }
        break;
      
      case 4: 
        float[] fdata = new float[rectWidth * rectHeight * numBands];
        floatDataArrays = new float[numBands][];
        for (int i = 0; i < numBands; i++) {
          floatDataArrays[i] = fdata;
        }
        odata = null;
        offset = 0;
        for (int j = rectY; j < rectY + rectHeight; j++) {
          for (int i = rectX; i < rectX + rectWidth; i++) {
            odata = raster.getDataElements(i, j, odata);
            
            theColorModel.getComponents(odata, components, 0);
            
            fdata[offset] = components[0];
            fdata[(offset + 1)] = components[1];
            fdata[(offset + 2)] = components[2];
            if (numBands > 3) {
              fdata[(offset + 3)] = components[3];
            }
            offset += pixelStride;
          }
        }
        break;
      
      case 5: 
        double[] ddata = new double[rectWidth * rectHeight * numBands];
        doubleDataArrays = new double[numBands][];
        for (int i = 0; i < numBands; i++) {
          doubleDataArrays[i] = ddata;
        }
        odata = null;
        offset = 0;
        for (int j = rectY; j < rectY + rectHeight; j++) {
          for (int i = rectX; i < rectX + rectWidth; i++) {
            odata = raster.getDataElements(i, j, odata);
            
            theColorModel.getComponents(odata, components, 0);
            
            ddata[offset] = components[0];
            ddata[(offset + 1)] = components[1];
            ddata[(offset + 2)] = components[2];
            if (numBands > 3) {
              ddata[(offset + 3)] = components[3];
            }
            offset += pixelStride;
          }
        }
      



      }
      
    }
    else
    {
      numBands = rft.getNumBands();
      pixelStride = numBands;
      scanlineStride = (rectWidth * numBands);
      bandDataOffsets = rft.getBandOffsets();
      bandOffsets = bandDataOffsets;
      
      switch (formatTagID & 0x7F) {
      case 3: 
        int[] idata = raster.getPixels(rectX, rectY, rectWidth, rectHeight, (int[])null);
        

        intDataArrays = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
          intDataArrays[i] = idata;
        }
        break;
      
      case 4: 
        float[] fdata = raster.getPixels(rectX, rectY, rectWidth, rectHeight, (float[])null);
        

        floatDataArrays = new float[numBands][];
        for (int i = 0; i < numBands; i++) {
          floatDataArrays[i] = fdata;
        }
        break;
      
      case 5: 
        double[] ddata = raster.getPixels(rectX, rectY, rectWidth, rectHeight, (double[])null);
        

        doubleDataArrays = new double[numBands][];
        for (int i = 0; i < numBands; i++) {
          doubleDataArrays[i] = ddata;
        }
      }
      
    }
  }
  



  public int getX()
  {
    return rectX;
  }
  



  public int getY()
  {
    return rectY;
  }
  


  public int getWidth()
  {
    return rectWidth;
  }
  


  public int getHeight()
  {
    return rectHeight;
  }
  
  public int getNumBands()
  {
    return numBands;
  }
  








  public boolean isBinary()
  {
    return ((formatTagID & 0x480) == 1152) && (ImageUtil.isBinary(raster.getSampleModel()));
  }
  














  public byte[] getBinaryDataArray()
  {
    if ((binaryDataArray == null) && (isBinary())) {
      binaryDataArray = ImageUtil.getPackedBinaryData(raster, new Rectangle(rectX, rectY, rectWidth, rectHeight));
    }
    


    return binaryDataArray;
  }
  








  public byte[][] getByteDataArrays()
  {
    if ((byteDataArrays == null) && (isBinary())) {
      byte[] bdata = ImageUtil.getUnpackedBinaryData(raster, new Rectangle(rectX, rectY, rectWidth, rectHeight));
      



      byteDataArrays = new byte[][] { bdata };
    }
    return byteDataArrays;
  }
  



  public byte[] getByteDataArray(int b)
  {
    byte[][] bda = getByteDataArrays();
    return bda == null ? null : bda[b];
  }
  



  public short[][] getShortDataArrays()
  {
    return shortDataArrays;
  }
  




  public short[] getShortDataArray(int b)
  {
    return shortDataArrays == null ? null : shortDataArrays[b];
  }
  



  public int[][] getIntDataArrays()
  {
    return intDataArrays;
  }
  



  public int[] getIntDataArray(int b)
  {
    return intDataArrays == null ? null : intDataArrays[b];
  }
  



  public float[][] getFloatDataArrays()
  {
    return floatDataArrays;
  }
  



  public float[] getFloatDataArray(int b)
  {
    return floatDataArrays == null ? null : floatDataArrays[b];
  }
  



  public double[][] getDoubleDataArrays()
  {
    return doubleDataArrays;
  }
  



  public double[] getDoubleDataArray(int b)
  {
    return doubleDataArrays == null ? null : doubleDataArrays[b];
  }
  




  public Object getDataArray(int b)
  {
    Object dataArray = null;
    switch (getDataType()) {
    case 0: 
      dataArray = getByteDataArray(b);
      break;
    
    case 1: 
    case 2: 
      dataArray = getShortDataArray(b);
      break;
    
    case 3: 
      dataArray = getIntDataArray(b);
      break;
    
    case 4: 
      dataArray = getFloatDataArray(b);
      break;
    
    case 5: 
      dataArray = getDoubleDataArray(b);
      break;
    
    default: 
      dataArray = null;
    }
    
    return dataArray;
  }
  
  public int[] getBandOffsets()
  {
    return bandDataOffsets;
  }
  



  public int[] getOffsetsForBands()
  {
    return bandOffsets;
  }
  



  public int getBandOffset(int b)
  {
    return bandDataOffsets[b];
  }
  



  public int getOffsetForBand(int b)
  {
    return bandOffsets[b];
  }
  








  public int getScanlineStride()
  {
    return scanlineStride;
  }
  
  public int getPixelStride()
  {
    return pixelStride;
  }
  




  public int getDataType()
  {
    return formatTagID & 0x7F;
  }
  



  public boolean isDataCopy()
  {
    return (formatTagID & 0x180) == 128;
  }
  











  public void copyBinaryDataToRaster()
  {
    if ((binaryDataArray == null) || (!isBinary())) {
      return;
    }
    
    ImageUtil.setPackedBinaryData(binaryDataArray, (WritableRaster)raster, new Rectangle(rectX, rectY, rectWidth, rectHeight));
  }
  














  public void copyDataToRaster()
  {
    if (isDataCopy())
    {


      WritableRaster wr = (WritableRaster)raster;
      switch (getDataType())
      {

      case 0: 
        if (!isBinary())
        {



          throw new RuntimeException(JaiI18N.getString("RasterAccessor1"));
        }
        


        ImageUtil.setUnpackedBinaryData(byteDataArrays[0], wr, new Rectangle(rectX, rectY, rectWidth, rectHeight));
        



        break;
      case 3: 
        wr.setPixels(rectX, rectY, rectWidth, rectHeight, intDataArrays[0]);
        

        break;
      
      case 4: 
        wr.setPixels(rectX, rectY, rectWidth, rectHeight, floatDataArrays[0]);
        

        break;
      
      case 5: 
        wr.setPixels(rectX, rectY, rectWidth, rectHeight, doubleDataArrays[0]);
      }
      
    }
  }
  







  public boolean needsClamping()
  {
    int[] bits = raster.getSampleModel().getSampleSize();
    






    for (int i = 0; i < bits.length; i++) {
      if (bits[i] < 32) {
        return true;
      }
    }
    return false;
  }
  












  public void clampDataArrays()
  {
    int[] bits = raster.getSampleModel().getSampleSize();
    






    boolean needClamp = false;
    boolean uniformBitSize = true;
    int bitSize = bits[0];
    for (int i = 0; i < bits.length; i++) {
      if (bits[i] < 32) {
        needClamp = true;
      }
      if (bits[i] != bitSize) {
        uniformBitSize = false;
      }
    }
    if (!needClamp) {
      return;
    }
    
    int dataType = raster.getDataBuffer().getDataType();
    double[] hiVals = new double[bits.length];
    double[] loVals = new double[bits.length];
    
    if ((dataType == 1) && (uniformBitSize) && (bits[0] == 16))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 65535.0D;
        loVals[i] = 0.0D;
      }
    } else if ((dataType == 2) && (uniformBitSize) && (bits[0] == 16))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 32767.0D;
        loVals[i] = -32768.0D;
      }
    } else if ((dataType == 3) && (uniformBitSize) && (bits[0] == 32))
    {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = 2.147483647E9D;
        loVals[i] = -2.147483648E9D;
      }
    } else {
      for (int i = 0; i < bits.length; i++) {
        hiVals[i] = ((1 << bits[i]) - 1);
        loVals[i] = 0.0D;
      }
    }
    clampDataArray(hiVals, loVals);
  }
  
  private void clampDataArray(double[] hiVals, double[] loVals) {
    switch (getDataType()) {
    case 3: 
      clampIntArrays(toIntArray(hiVals), toIntArray(loVals));
      break;
    
    case 4: 
      clampFloatArrays(toFloatArray(hiVals), toFloatArray(loVals));
      break;
    
    case 5: 
      clampDoubleArrays(hiVals, loVals);
    }
  }
  
  private int[] toIntArray(double[] vals)
  {
    int[] returnVals = new int[vals.length];
    for (int i = 0; i < vals.length; i++) {
      returnVals[i] = ((int)vals[i]);
    }
    return returnVals;
  }
  
  private float[] toFloatArray(double[] vals) {
    float[] returnVals = new float[vals.length];
    for (int i = 0; i < vals.length; i++) {
      returnVals[i] = ((float)vals[i]);
    }
    return returnVals;
  }
  
  private void clampIntArrays(int[] hiVals, int[] loVals) {
    int width = rectWidth;
    int height = rectHeight;
    for (int k = 0; k < numBands; k++) {
      int[] data = intDataArrays[k];
      int scanlineOffset = bandDataOffsets[k];
      int hiVal = hiVals[k];
      int loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          int tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += pixelStride;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
  
  private void clampFloatArrays(float[] hiVals, float[] loVals) {
    int width = rectWidth;
    int height = rectHeight;
    for (int k = 0; k < numBands; k++) {
      float[] data = floatDataArrays[k];
      int scanlineOffset = bandDataOffsets[k];
      float hiVal = hiVals[k];
      float loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          float tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += pixelStride;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
  
  private void clampDoubleArrays(double[] hiVals, double[] loVals) {
    int width = rectWidth;
    int height = rectHeight;
    for (int k = 0; k < numBands; k++) {
      double[] data = doubleDataArrays[k];
      int scanlineOffset = bandDataOffsets[k];
      double hiVal = hiVals[k];
      double loVal = loVals[k];
      for (int j = 0; j < height; j++) {
        int pixelOffset = scanlineOffset;
        for (int i = 0; i < width; i++) {
          double tmp = data[pixelOffset];
          if (tmp < loVal) {
            data[pixelOffset] = loVal;
          } else if (tmp > hiVal) {
            data[pixelOffset] = hiVal;
          }
          pixelOffset += pixelStride;
        }
        scanlineOffset += scanlineStride;
      }
    }
  }
}
