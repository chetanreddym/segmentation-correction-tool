package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
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
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

























































































































































































public final class PixelAccessor
{
  public static final int TYPE_BIT = -1;
  public final SampleModel sampleModel;
  public final ColorModel colorModel;
  public final boolean isComponentSM;
  public final boolean isMultiPixelPackedSM;
  public final boolean isSinglePixelPackedSM;
  public final int sampleType;
  public final int bufferType;
  public final int transferType;
  public final int numBands;
  public final int[] sampleSize;
  public final boolean isPacked;
  public final boolean hasCompatibleCM;
  public final boolean isComponentCM;
  public final boolean isIndexCM;
  public final boolean isPackedCM;
  public final int componentType;
  public final int numComponents;
  public final int[] componentSize;
  
  private static SampleModel getSampleModel(RenderedImage image)
  {
    if (image == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return image.getSampleModel();
  }
  












  public PixelAccessor(RenderedImage image)
  {
    this(getSampleModel(image), image.getColorModel());
  }
  











  public PixelAccessor(SampleModel sm, ColorModel cm)
  {
    if (sm == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    sampleModel = sm;
    colorModel = cm;
    

    isComponentSM = (sampleModel instanceof ComponentSampleModel);
    isMultiPixelPackedSM = (sampleModel instanceof MultiPixelPackedSampleModel);
    
    isSinglePixelPackedSM = (sampleModel instanceof SinglePixelPackedSampleModel);
    

    bufferType = sampleModel.getDataType();
    transferType = sampleModel.getTransferType();
    numBands = sampleModel.getNumBands();
    sampleSize = sampleModel.getSampleSize();
    sampleType = (isComponentSM ? bufferType : getType(sampleSize));
    

    isPacked = ((sampleType == -1) && (numBands == 1));
    

    hasCompatibleCM = ((colorModel != null) && (JDKWorkarounds.areCompatibleDataModels(sampleModel, colorModel)));
    

    if (hasCompatibleCM) {
      isComponentCM = (colorModel instanceof ComponentColorModel);
      isIndexCM = (colorModel instanceof IndexColorModel);
      isPackedCM = (colorModel instanceof PackedColorModel);
      
      numComponents = colorModel.getNumComponents();
      componentSize = colorModel.getComponentSize();
      int tempType = getType(componentSize);
      
      componentType = (tempType == -1 ? 0 : tempType);
    }
    else {
      isComponentCM = false;
      isIndexCM = false;
      isPackedCM = false;
      numComponents = numBands;
      componentSize = sampleSize;
      componentType = sampleType;
    }
  }
  









  private static int getType(int[] size)
  {
    int maxSize = size[0];
    for (int i = 1; i < size.length; i++) {
      maxSize = Math.max(maxSize, size[i]);
    }
    int type;
    int type;
    if (maxSize < 1) {
      type = 32; } else { int type;
      if (maxSize == 1) {
        type = -1; } else { int type;
        if (maxSize <= 8) {
          type = 0; } else { int type;
          if (maxSize <= 16) {
            type = 1; } else { int type;
            if (maxSize <= 32) {
              type = 3; } else { int type;
              if (maxSize <= 64) {
                type = 5;
              } else
                type = 32;
            } } } } }
    return type;
  }
  










  public static int getPixelType(SampleModel sm)
  {
    return (sm instanceof ComponentSampleModel) ? sm.getDataType() : getType(sm.getSampleSize());
  }
  

































  public static int getDestPixelType(Vector sources)
  {
    if (sources == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int type = 32;
    int size = sources.size();
    
    if (size > 0) {
      RenderedImage src = (RenderedImage)sources.get(0);
      SampleModel sm = src.getSampleModel();
      
      type = getPixelType(sm);
      
      for (int i = 1; i < size; i++) {
        src = (RenderedImage)sources.get(i);
        sm = src.getSampleModel();
        
        int t = getPixelType(sm);
        

        type = ((type == 1) && (t == 2)) || ((type == 2) && (t == 1)) ? 3 : Math.max(type, t);
      }
    }
    



    return type;
  }
  





























  public static int getDestNumBands(Vector sources)
  {
    if (sources == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    int bands = 0;
    int size = sources.size();
    
    if (size > 0) {
      RenderedImage src = (RenderedImage)sources.get(0);
      SampleModel sm = src.getSampleModel();
      
      bands = sm.getNumBands();
      
      for (int i = 1; i < size; i++) {
        src = (RenderedImage)sources.get(i);
        sm = src.getSampleModel();
        
        int b = sm.getNumBands();
        
        bands = (bands == 1) || (b == 1) ? Math.max(bands, b) : Math.min(bands, b);
      }
    }
    
    return bands;
  }
  









  public static boolean isPackedOperation(PixelAccessor[] srcs, PixelAccessor dst)
  {
    boolean canBePacked = isPacked;
    if ((canBePacked) && (srcs != null)) {
      for (int i = 0; i < srcs.length; i++) {
        canBePacked = (canBePacked) && (isPacked);
        if (!canBePacked) {
          break;
        }
      }
    }
    return canBePacked;
  }
  









  public static boolean isPackedOperation(PixelAccessor src, PixelAccessor dst)
  {
    return (isPacked) && (isPacked);
  }
  











  public static boolean isPackedOperation(PixelAccessor src1, PixelAccessor src2, PixelAccessor dst)
  {
    return (isPacked) && (isPacked) && (isPacked);
  }
  










































  public UnpackedImageData getPixels(Raster raster, Rectangle rect, int type, boolean isDest)
  {
    if (!raster.getBounds().contains(rect)) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
    }
    

    if ((type < 0) || (type > 5))
    {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor1"));
    }
    

    if ((type < sampleType) || ((sampleType == 1) && (type == 2)))
    {

      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor2"));
    }
    

    if (isComponentSM) {
      return getPixelsCSM(raster, rect, type, isDest);
    }
    

    int size = width * height * numBands;
    
    Object data = null;
    
    switch (type) {
    case 0: 
      byte[] bd;
      byte[] bd;
      if (isDest) {
        bd = new byte[size];
      } else { byte[] bd;
        if ((isMultiPixelPackedSM) && (transferType == 0))
        {
          bd = (byte[])raster.getDataElements(x, y, width, height, null);
        }
        else
        {
          bd = new byte[size];
          int[] d = raster.getPixels(x, y, width, height, (int[])null);
          
          for (int i = 0; i < size; i++) {
            bd[i] = ((byte)(d[i] & 0xFF));
          }
        }
      }
      
      data = repeatBand(bd, numBands);
      break;
    case 1: 
      short[] usd;
      
      short[] usd;
      if (isDest) {
        usd = new short[size];
      } else { short[] usd;
        if ((isMultiPixelPackedSM) && (transferType == 1))
        {
          usd = (short[])raster.getDataElements(x, y, width, height, null);
        }
        else
        {
          usd = new short[size];
          int[] d = raster.getPixels(x, y, width, height, (int[])null);
          
          for (int i = 0; i < size; i++) {
            usd[i] = ((short)(d[i] & 0xFFFF));
          }
        }
      }
      
      data = repeatBand(usd, numBands);
      break;
    
    case 2: 
      short[] sd = new short[size];
      
      if (!isDest) {
        int[] d = raster.getPixels(x, y, width, height, (int[])null);
        
        for (int i = 0; i < size; i++) {
          sd[i] = ((short)d[i]);
        }
      }
      
      data = repeatBand(sd, numBands);
      break;
    
    case 3: 
      return getPixelsInt(raster, rect, isDest);
    
    case 4: 
      return getPixelsFloat(raster, rect, isDest);
    
    case 5: 
      return getPixelsDouble(raster, rect, isDest);
    }
    
    return new UnpackedImageData(raster, rect, type, data, numBands, numBands * width, getInterleavedOffsets(numBands), isDest & raster instanceof WritableRaster);
  }
  













  private UnpackedImageData getPixelsCSM(Raster raster, Rectangle rect, int type, boolean isDest)
  {
    Object data = null;
    






    ComponentSampleModel sm = (ComponentSampleModel)raster.getSampleModel();
    boolean set;
    boolean set; int[] offsets; int lineStride; int pixelStride; if (type == sampleType)
    {

      DataBuffer db = raster.getDataBuffer();
      int[] bankIndices = sm.getBankIndices();
      
      switch (sampleType) {
      case 0: 
        byte[][] bbd = ((DataBufferByte)db).getBankData();
        byte[][] bd = new byte[numBands][];
        
        for (int b = 0; b < numBands; b++) {
          bd[b] = bbd[bankIndices[b]];
        }
        data = bd;
        break;
      
      case 1: 
      case 2: 
        short[][] sbd = sampleType == 1 ? ((DataBufferUShort)db).getBankData() : ((DataBufferShort)db).getBankData();
        

        short[][] sd = new short[numBands][];
        
        for (int b = 0; b < numBands; b++) {
          sd[b] = sbd[bankIndices[b]];
        }
        data = sd;
        break;
      
      case 3: 
        int[][] ibd = ((DataBufferInt)db).getBankData();
        int[][] id = new int[numBands][];
        
        for (int b = 0; b < numBands; b++) {
          id[b] = ibd[bankIndices[b]];
        }
        data = id;
        break;
      
      case 4: 
        float[][] fbd = DataBufferUtils.getBankDataFloat(db);
        float[][] fd = new float[numBands][];
        
        for (int b = 0; b < numBands; b++) {
          fd[b] = fbd[bankIndices[b]];
        }
        data = fd;
        break;
      
      case 5: 
        double[][] dbd = DataBufferUtils.getBankDataDouble(db);
        double[][] dd = new double[numBands][];
        
        for (int b = 0; b < numBands; b++) {
          dd[b] = dbd[bankIndices[b]];
        }
        data = dd;
      }
      
      
      int pixelStride = sm.getPixelStride();
      int lineStride = sm.getScanlineStride();
      

      int[] dbOffsets = db.getOffsets();
      int x = x - raster.getSampleModelTranslateX();
      int y = y - raster.getSampleModelTranslateY();
      
      int[] offsets = new int[numBands];
      for (int b = 0; b < numBands; b++) {
        offsets[b] = (sm.getOffset(x, y, b) + dbOffsets[bankIndices[b]]);
      }
      
      set = false;
    }
    else {
      switch (type) {
      case 3: 
        return getPixelsInt(raster, rect, isDest);
      
      case 4: 
        return getPixelsFloat(raster, rect, isDest);
      
      case 5: 
        return getPixelsDouble(raster, rect, isDest);
      }
      
      










      int size = width * height * numBands;
      
      short[] sd = new short[size];
      
      if (!isDest)
      {
        UnpackedImageData uid = getPixelsCSM(raster, rect, sampleType, isDest);
        
        byte[][] bdata = uid.getByteData();
        
        for (int b = 0; b < numBands; b++) {
          byte[] bd = bdata[b];
          int lo = uid.getOffset(b);
          
          int i = b; for (int h = 0; h < height; h++) {
            int po = lo;
            lo += lineStride;
            
            for (int w = 0; w < width; w++) {
              sd[i] = ((short)(bd[po] & 0xFF));
              
              po += pixelStride;
              i += numBands;
            }
          }
        }
      }
      
      data = repeatBand(sd, numBands);
      


      pixelStride = numBands;
      lineStride = pixelStride * width;
      offsets = getInterleavedOffsets(numBands);
      set = isDest & raster instanceof WritableRaster;
    }
    
    return new UnpackedImageData(raster, rect, type, data, pixelStride, lineStride, offsets, set);
  }
  










  private UnpackedImageData getPixelsInt(Raster raster, Rectangle rect, boolean isDest)
  {
    int size = width * height * numBands;
    






    int[] d = isDest ? new int[size] : raster.getPixels(x, y, width, height, (int[])null);
    


    return new UnpackedImageData(raster, rect, 3, repeatBand(d, numBands), numBands, numBands * width, getInterleavedOffsets(numBands), isDest & raster instanceof WritableRaster);
  }
  












  private UnpackedImageData getPixelsFloat(Raster raster, Rectangle rect, boolean isDest)
  {
    int size = width * height * numBands;
    






    float[] d = isDest ? new float[size] : raster.getPixels(x, y, width, height, (float[])null);
    


    return new UnpackedImageData(raster, rect, 4, repeatBand(d, numBands), numBands, numBands * width, getInterleavedOffsets(numBands), isDest & raster instanceof WritableRaster);
  }
  












  private UnpackedImageData getPixelsDouble(Raster raster, Rectangle rect, boolean isDest)
  {
    int size = width * height * numBands;
    






    double[] d = isDest ? new double[size] : raster.getPixels(x, y, width, height, (double[])null);
    


    return new UnpackedImageData(raster, rect, 5, repeatBand(d, numBands), numBands, numBands * width, getInterleavedOffsets(numBands), isDest & raster instanceof WritableRaster);
  }
  





  private byte[][] repeatBand(byte[] d, int numBands)
  {
    byte[][] data = new byte[numBands][];
    for (int i = 0; i < numBands; i++) {
      data[i] = d;
    }
    return data;
  }
  
  private short[][] repeatBand(short[] d, int numBands) {
    short[][] data = new short[numBands][];
    for (int i = 0; i < numBands; i++) {
      data[i] = d;
    }
    return data;
  }
  
  private int[][] repeatBand(int[] d, int numBands) {
    int[][] data = new int[numBands][];
    for (int i = 0; i < numBands; i++) {
      data[i] = d;
    }
    return data;
  }
  
  private float[][] repeatBand(float[] d, int numBands) {
    float[][] data = new float[numBands][];
    for (int i = 0; i < numBands; i++) {
      data[i] = d;
    }
    return data;
  }
  
  private double[][] repeatBand(double[] d, int numBands) {
    double[][] data = new double[numBands][];
    for (int i = 0; i < numBands; i++) {
      data[i] = d;
    }
    return data;
  }
  
  private int[] getInterleavedOffsets(int numBands)
  {
    int[] offsets = new int[numBands];
    for (int i = 0; i < numBands; i++) {
      offsets[i] = i;
    }
    return offsets;
  }
  













  public void setPixels(UnpackedImageData uid)
  {
    if (uid == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    setPixels(uid, true);
  }
  















  public void setPixels(UnpackedImageData uid, boolean clamp)
  {
    if (uid == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!convertToDest) {
      return;
    }
    
    if (clamp) {
      switch (sampleType) {
      case 0: 
        clampByte(data, type);
        break;
      case 1: 
        clampUShort(data, type);
        break;
      case 2: 
        clampShort(data, type);
        break;
      case 3: 
        clampInt(data, type);
        break;
      case 4: 
        clampFloat(data, type);
      }
      
    }
    
    WritableRaster raster = (WritableRaster)raster;
    Rectangle rect = rect;
    int type = type;
    
    switch (type) {
    case 0: 
      byte[] bd = uid.getByteData(0);
      
      if ((isMultiPixelPackedSM) && (transferType == 0))
      {
        raster.setDataElements(x, y, width, height, bd);
      }
      else {
        int size = bd.length;
        int[] d = new int[size];
        for (int i = 0; i < size; i++) {
          bd[i] &= 0xFF;
        }
        raster.setPixels(x, y, width, height, d);
      }
      break;
    
    case 1: 
    case 2: 
      short[] sd = uid.getShortData(0);
      
      if (isComponentSM)
      {
        UnpackedImageData buid = getPixelsCSM(raster, rect, 0, true);
        

        byte[][] bdata = buid.getByteData();
        
        for (int b = 0; b < numBands; b++) {
          byte[] d = bdata[b];
          int lo = buid.getOffset(b);
          
          int i = b; for (int h = 0; h < height; h++) {
            int po = lo;
            lo += lineStride;
            
            for (int w = 0; w < width; w++) {
              d[po] = ((byte)sd[i]);
              
              po += pixelStride;
              i += numBands;
            }
          }
        }
      } else if ((isMultiPixelPackedSM) && (transferType == 1))
      {
        raster.setDataElements(x, y, width, height, sd);
      }
      else {
        int size = sd.length;
        int[] d = new int[size];
        if (type == 1) {
          for (int i = 0; i < size; i++) {
            sd[i] &= 0xFFFF;
          }
        } else {
          for (int i = 0; i < size; i++) {
            d[i] = sd[i];
          }
        }
        raster.setPixels(x, y, width, height, d);
      }
      break;
    
    case 3: 
      raster.setPixels(x, y, width, height, uid.getIntData(0));
      
      break;
    
    case 4: 
      raster.setPixels(x, y, width, height, uid.getFloatData(0));
      
      break;
    
    case 5: 
      raster.setPixels(x, y, width, height, uid.getDoubleData(0));
    }
    
  }
  


  private void clampByte(Object data, int type)
  {
    switch (type) {
    case 1: 
      short[][] usd = (short[][])data;
      int bands = usd.length;
      
      for (int j = 0; j < bands; j++) {
        short[] d = usd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          int n = d[i] & 0xFFFF;
          d[i] = ((short)(n > 255 ? 'ÿ' : n));
        }
      }
      break;
    
    case 2: 
      short[][] sd = (short[][])data;
      int bands = sd.length;
      
      for (int j = 0; j < bands; j++) {
        short[] d = sd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          int n = d[i];
          d[i] = ((short)(n < 0 ? 0 : n > 255 ? 'ÿ' : n));
        }
      }
      break;
    
    case 3: 
      int[][] id = (int[][])data;
      int bands = id.length;
      
      for (int j = 0; j < bands; j++) {
        int[] d = id[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          int n = d[i];
          d[i] = (n < 0 ? 0 : n > 255 ? 'ÿ' : n);
        }
      }
      break;
    
    case 4: 
      float[][] fd = (float[][])data;
      int bands = fd.length;
      
      for (int j = 0; j < bands; j++) {
        float[] d = fd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          float n = d[i];
          d[i] = (n < 0.0F ? 0.0F : n > 255.0F ? 255.0F : n);
        }
      }
      break;
    
    case 5: 
      double[][] dd = (double[][])data;
      int bands = dd.length;
      
      for (int j = 0; j < bands; j++) {
        double[] d = dd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          double n = d[i];
          d[i] = (n < 0.0D ? 0.0D : n > 255.0D ? 255.0D : n);
        }
      }
    }
    
  }
  
  private void clampUShort(Object data, int type)
  {
    switch (type) {
    case 3: 
      int[][] id = (int[][])data;
      int bands = id.length;
      
      for (int j = 0; j < bands; j++) {
        int[] d = id[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          int n = d[i];
          d[i] = (n < 0 ? 0 : n > 65535 ? 65535 : n);
        }
      }
      break;
    
    case 4: 
      float[][] fd = (float[][])data;
      int bands = fd.length;
      
      for (int j = 0; j < bands; j++) {
        float[] d = fd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          float n = d[i];
          d[i] = (n < 0.0F ? 0.0F : n > 65535.0F ? 65535.0F : n);
        }
      }
      break;
    
    case 5: 
      double[][] dd = (double[][])data;
      int bands = dd.length;
      
      for (int j = 0; j < bands; j++) {
        double[] d = dd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          double n = d[i];
          d[i] = (n < 0.0D ? 0.0D : n > 65535.0D ? 65535.0D : n);
        }
      }
    }
    
  }
  
  private void clampShort(Object data, int type)
  {
    switch (type) {
    case 3: 
      int[][] id = (int[][])data;
      int bands = id.length;
      
      for (int j = 0; j < bands; j++) {
        int[] d = id[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          int n = d[i];
          d[i] = (n < 32768 ? '耀' : n > 32767 ? '翿' : n);
        }
      }
      
      break;
    
    case 4: 
      float[][] fd = (float[][])data;
      int bands = fd.length;
      
      for (int j = 0; j < bands; j++) {
        float[] d = fd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          float n = d[i];
          d[i] = (n < -32768.0F ? -32768.0F : n > 32767.0F ? 32767.0F : n);
        }
      }
      
      break;
    
    case 5: 
      double[][] dd = (double[][])data;
      int bands = dd.length;
      
      for (int j = 0; j < bands; j++) {
        double[] d = dd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          double n = d[i];
          d[i] = (n < -32768.0D ? -32768.0D : n > 32767.0D ? 32767.0D : n);
        }
      }
    }
    
  }
  

  private void clampInt(Object data, int type)
  {
    switch (type) {
    case 4: 
      float[][] fd = (float[][])data;
      int bands = fd.length;
      
      for (int j = 0; j < bands; j++) {
        float[] d = fd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          float n = d[i];
          d[i] = (n < -2.14748365E9F ? -2.14748365E9F : n > 2.14748365E9F ? 2.14748365E9F : n);
        }
      }
      
      break;
    
    case 5: 
      double[][] dd = (double[][])data;
      int bands = dd.length;
      
      for (int j = 0; j < bands; j++) {
        double[] d = dd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          double n = d[i];
          d[i] = (n < -2.147483648E9D ? -2.147483648E9D : n > 2.147483647E9D ? 2.147483647E9D : n);
        }
      }
    }
    
  }
  

  private void clampFloat(Object data, int type)
  {
    switch (type) {
    case 5: 
      double[][] dd = (double[][])data;
      int bands = dd.length;
      
      for (int j = 0; j < bands; j++) {
        double[] d = dd[j];
        int size = d.length;
        
        for (int i = 0; i < size; i++) {
          double n = d[i];
          d[i] = (n < -3.4028234663852886E38D ? -3.4028234663852886E38D : n > 3.4028234663852886E38D ? 3.4028234663852886E38D : n);
        }
      }
    }
    
  }
  














































  public PackedImageData getPackedPixels(Raster raster, Rectangle rect, boolean isDest, boolean coerceZeroOffset)
  {
    if (!isPacked) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor3"));
    }
    

    if (!raster.getBounds().contains(rect)) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
    }
    
    int lineStride;
    int offset;
    int bitOffset;
    boolean set;
    byte[] data;
    if (isMultiPixelPackedSM)
    {
      boolean set = isDest;
      int offset;
      if (coerceZeroOffset)
      {
        byte[] data = ImageUtil.getPackedBinaryData(raster, rect);
        int lineStride = (width + 7) / 8;
        int bitOffset; offset = bitOffset = 0;
      }
      else
      {
        MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)sampleModel;
        

        DataBuffer db = raster.getDataBuffer();
        int dbOffset = db.getOffset();
        
        int x = x - raster.getSampleModelTranslateX();
        int y = y - raster.getSampleModelTranslateY();
        
        int smLineStride = sm.getScanlineStride();
        int minOffset = sm.getOffset(x, y) + dbOffset;
        int maxOffset = sm.getOffset(x + width - 1, y) + dbOffset;
        int numElements = maxOffset - minOffset + 1;
        int smBitOffset = sm.getBitOffset(x);
        
        switch (bufferType) {
        case 0: 
          byte[] data = ((DataBufferByte)db).getData();
          int lineStride = smLineStride;
          int offset = minOffset;
          int bitOffset = smBitOffset;
          set = false;
          break;
        


        case 1: 
          int lineStride = numElements * 2;
          int offset = smBitOffset / 8;
          int bitOffset = smBitOffset % 8;
          byte[] data = new byte[lineStride * height];
          
          short[] sd = ((DataBufferUShort)db).getData();
          int i = 0; for (int h = 0; h < height; h++) {
            for (int w = minOffset; w <= maxOffset; w++) {
              short d = sd[w];
              data[(i++)] = ((byte)(d >>> 8 & 0xFF));
              data[(i++)] = ((byte)(d & 0xFF));
            }
            minOffset += smLineStride;
            maxOffset += smLineStride;
          }
          break;
        
        case 3: 
          int lineStride = numElements * 4;
          int offset = smBitOffset / 8;
          int bitOffset = smBitOffset % 8;
          byte[] data = new byte[lineStride * height];
          
          int[] id = ((DataBufferInt)db).getData();
          int i = 0; for (int h = 0; h < height; h++) {
            for (int w = minOffset; w <= maxOffset; w++) {
              int d = id[w];
              data[(i++)] = ((byte)(d >>> 24 & 0xFF));
              data[(i++)] = ((byte)(d >>> 16 & 0xFF));
              data[(i++)] = ((byte)(d >>> 8 & 0xFF));
              data[(i++)] = ((byte)(d & 0xFF));
            }
            minOffset += smLineStride;
            maxOffset += smLineStride;
          }
          break;
        case 2: 
        default: 
          throw new RuntimeException();
        }
      }
    }
    else {
      lineStride = (width + 7) / 8;
      offset = 0;
      bitOffset = 0;
      set = isDest & raster instanceof WritableRaster;
      data = new byte[lineStride * height];
      
      if (!isDest) {
        int size = lineStride * 8;
        int[] p = new int[size];
        int i = 0; for (int h = 0; h < height; h++) {
          p = raster.getPixels(x, y + h, width, 1, p);
          
          for (int w = 0; w < size; w += 8) {
            data[(i++)] = ((byte)(p[w] << 7 | p[(w + 1)] << 6 | p[(w + 2)] << 5 | p[(w + 3)] << 4 | p[(w + 4)] << 3 | p[(w + 5)] << 2 | p[(w + 6)] << 1 | p[(w + 7)]));
          }
        }
      }
    }
    



    return new PackedImageData(raster, rect, data, lineStride, offset, bitOffset, coerceZeroOffset, set);
  }
  
















  public void setPackedPixels(PackedImageData pid)
  {
    if (pid == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!convertToDest) {
      return;
    }
    
    Raster raster = raster;
    Rectangle rect = rect;
    byte[] data = data;
    
    if (isMultiPixelPackedSM)
    {
      if (coercedZeroOffset) {
        ImageUtil.setPackedBinaryData(data, (WritableRaster)raster, rect);
      }
      else
      {
        MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)sampleModel;
        

        DataBuffer db = raster.getDataBuffer();
        int dbOffset = db.getOffset();
        
        int x = x - raster.getSampleModelTranslateX();
        int y = y - raster.getSampleModelTranslateY();
        
        int lineStride = sm.getScanlineStride();
        int minOffset = sm.getOffset(x, y) + dbOffset;
        int maxOffset = sm.getOffset(x + width - 1, y) + dbOffset;
        

        switch (bufferType) {
        case 1: 
          short[] sd = ((DataBufferUShort)db).getData();
          int i = 0; for (int h = 0; h < height; h++) {
            for (int w = minOffset; w <= maxOffset; w++) {
              sd[w] = ((short)(data[(i++)] << 8 | data[(i++)]));
            }
            minOffset += lineStride;
            maxOffset += lineStride;
          }
          break;
        
        case 3: 
          int[] id = ((DataBufferInt)db).getData();
          int i = 0; for (int h = 0; h < height; h++) {
            for (int w = minOffset; w <= maxOffset; w++) {
              id[w] = (data[(i++)] << 24 | data[(i++)] << 16 | data[(i++)] << 8 | data[(i++)]);
            }
            
            minOffset += lineStride;
            maxOffset += lineStride;
          }
        



        }
        
      }
    }
    else
    {
      WritableRaster wr = (WritableRaster)raster;
      int size = lineStride * 8;
      int[] p = new int[size];
      
      int i = 0; for (int h = 0; h < height; h++) {
        for (int w = 0; w < size; w += 8) {
          p[w] = (data[i] >>> 7 & 0x1);
          p[(w + 1)] = (data[i] >>> 6 & 0x1);
          p[(w + 2)] = (data[i] >>> 5 & 0x1);
          p[(w + 3)] = (data[i] >>> 4 & 0x1);
          p[(w + 4)] = (data[i] >>> 3 & 0x1);
          p[(w + 5)] = (data[i] >>> 2 & 0x1);
          p[(w + 6)] = (data[i] >>> 1 & 0x1);
          p[(w + 7)] = (data[i] & 0x1);
          i++;
        }
        wr.setPixels(x, y + h, width, 1, p);
      }
    }
  }
  















































  public UnpackedImageData getComponents(Raster raster, Rectangle rect, int type)
  {
    if (!hasCompatibleCM) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor5"));
    }
    

    if (!raster.getBounds().contains(rect)) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
    }
    

    if ((type < 0) || (type > 5))
    {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor1"));
    }
    

    if ((type < componentType) || ((componentType == 1) && (type == 2)))
    {

      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor4"));
    }
    


    int size = width * height * numComponents;
    int[] ic = new int[size];
    int width = x + width;
    int height = y + height;
    
    int i = 0; for (int y = y; y < height; y++) {
      for (int x = x; x < width; x++) {
        Object p = raster.getDataElements(x, y, null);
        colorModel.getComponents(p, ic, i);
        i += numComponents;
      }
    }
    

    Object data = null;
    switch (type) {
    case 0: 
      byte[] bc = new byte[size];
      for (int i = 0; i < size; i++) {
        bc[i] = ((byte)(ic[i] & 0xFF));
      }
      data = repeatBand(bc, numComponents);
      break;
    
    case 1: 
      short[] usc = new short[size];
      for (int i = 0; i < size; i++) {
        usc[i] = ((short)(ic[i] & 0xFFFF));
      }
      data = repeatBand(usc, numComponents);
      break;
    
    case 2: 
      short[] sc = new short[size];
      for (int i = 0; i < size; i++) {
        sc[i] = ((short)ic[i]);
      }
      data = repeatBand(sc, numComponents);
      break;
    
    case 3: 
      data = repeatBand(ic, numComponents);
      break;
    
    case 4: 
      float[] fc = new float[size];
      for (int i = 0; i < size; i++) {
        fc[i] = ic[i];
      }
      data = repeatBand(fc, numComponents);
      break;
    
    case 5: 
      double[] dc = new double[size];
      for (int i = 0; i < size; i++) {
        dc[i] = ic[i];
      }
      data = repeatBand(dc, numComponents);
    }
    
    
    return new UnpackedImageData(raster, rect, type, data, numComponents, numComponents * width, getInterleavedOffsets(numComponents), raster instanceof WritableRaster);
  }
  



























  public void setComponents(UnpackedImageData uid)
  {
    if (uid == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!convertToDest) {
      return;
    }
    
    WritableRaster raster = (WritableRaster)raster;
    Rectangle rect = rect;
    int type = type;
    
    int size = width * height * numComponents;
    int[] ic = null;
    
    switch (type) {
    case 0: 
      byte[] bc = uid.getByteData(0);
      ic = new int[size];
      for (int i = 0; i < size; i++) {
        bc[i] &= 0xFF;
      }
      break;
    
    case 1: 
      short[] usc = uid.getShortData(0);
      ic = new int[size];
      for (int i = 0; i < size; i++) {
        usc[i] &= 0xFFFF;
      }
      break;
    
    case 2: 
      short[] sc = uid.getShortData(0);
      ic = new int[size];
      for (int i = 0; i < size; i++) {
        ic[i] = sc[i];
      }
      break;
    
    case 3: 
      ic = uid.getIntData(0);
      break;
    
    case 4: 
      float[] fc = uid.getFloatData(0);
      ic = new int[size];
      for (int i = 0; i < size; i++) {
        ic[i] = ((int)fc[i]);
      }
      break;
    
    case 5: 
      double[] dc = uid.getDoubleData(0);
      ic = new int[size];
      for (int i = 0; i < size; i++) {
        ic[i] = ((int)dc[i]);
      }
    }
    
    
    int width = x + width;
    int height = y + height;
    
    int i = 0; for (int y = y; y < height; y++) {
      for (int x = x; x < width; x++) {
        Object p = colorModel.getDataElements(ic, i, null);
        raster.setDataElements(x, y, p);
        i += numComponents;
      }
    }
  }
  



































  public UnpackedImageData getComponentsRGB(Raster raster, Rectangle rect)
  {
    if (!hasCompatibleCM) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor5"));
    }
    

    if (!raster.getBounds().contains(rect)) {
      throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
    }
    

    int size = width * height;
    
    byte[][] data = new byte[4][size];
    byte[] r = data[0];
    byte[] g = data[1];
    byte[] b = data[2];
    byte[] a = data[3];
    

    int maxX = x + width;
    int maxY = y + height;
    
    if (isIndexCM)
    {
      IndexColorModel icm = (IndexColorModel)colorModel;
      int mapSize = icm.getMapSize();
      

      byte[] reds = new byte[mapSize];
      icm.getReds(reds);
      byte[] greens = new byte[mapSize];
      icm.getGreens(greens);
      byte[] blues = new byte[mapSize];
      icm.getBlues(blues);
      byte[] alphas = null;
      if (icm.hasAlpha()) {
        alphas = new byte[mapSize];
        icm.getAlphas(alphas);
      }
      

      int[] indices = raster.getPixels(x, y, width, height, (int[])null);
      



      if (alphas == null)
      {
        int i = 0; for (int y = y; y < maxY; y++) {
          for (int x = x; x < maxX; x++) {
            int index = indices[i];
            
            r[i] = reds[index];
            g[i] = greens[index];
            b[i] = blues[index];
            
            i++;
          }
        }
      }
      else {
        int i = 0; for (int y = y; y < maxY; y++) {
          for (int x = x; x < maxX; x++) {
            int index = indices[i];
            
            r[i] = reds[index];
            g[i] = greens[index];
            b[i] = blues[index];
            a[i] = alphas[index];
            
            i++;
          }
          
        }
      }
    }
    else
    {
      int i = 0; for (int y = y; y < maxY; y++) {
        for (int x = x; x < maxX; x++) {
          Object p = raster.getDataElements(x, y, null);
          
          r[i] = ((byte)colorModel.getRed(p));
          g[i] = ((byte)colorModel.getGreen(p));
          b[i] = ((byte)colorModel.getBlue(p));
          a[i] = ((byte)colorModel.getAlpha(p));
          i++;
        }
      }
    }
    
    return new UnpackedImageData(raster, rect, 0, data, 1, width, new int[4], raster instanceof WritableRaster);
  }
  



























  public void setComponentsRGB(UnpackedImageData uid)
  {
    if (uid == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!convertToDest) {
      return;
    }
    
    byte[][] data = uid.getByteData();
    byte[] r = data[0];
    byte[] g = data[1];
    byte[] b = data[2];
    byte[] a = data[3];
    
    WritableRaster raster = (WritableRaster)raster;
    Rectangle rect = rect;
    
    int maxX = x + width;
    int maxY = y + height;
    
    int i = 0; for (int y = y; y < maxY; y++) {
      for (int x = x; x < maxX; x++) {
        int rgb = a[i] << 24 | b[i] << 16 | g[i] << 8 | r[i];
        

        Object p = colorModel.getDataElements(rgb, null);
        raster.setDataElements(x, y, p);
        i++;
      }
    }
  }
}
