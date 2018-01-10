package javax.media.jai;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
















































public abstract class ColorSpaceJAI
  extends ColorSpace
{
  private static final double maxXYZ = 1.999969482421875D;
  private static final double power1 = 0.4166666666666667D;
  private static double[] LUT = new double['Ā'];
  private boolean isRGBPreferredIntermediary;
  
  static { for (int i = 0; i < 256; i++) {
      double v = i / 255.0D;
      if (v < 0.040449936D) {
        LUT[i] = (v / 12.92D);
      } else {
        LUT[i] = Math.pow((v + 0.055D) / 1.055D, 2.4D);
      }
    }
  }
  





















































































































































  public static WritableRaster CIEXYZToRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    checkParameters(src, srcComponentSize, dest, destComponentSize);
    
    SampleModel srcSampleModel = src.getSampleModel();
    



    if (srcComponentSize == null) {
      srcComponentSize = srcSampleModel.getSampleSize();
    }
    
    if (dest == null) {
      Point origin = new Point(src.getMinX(), src.getMinY());
      dest = RasterFactory.createWritableRaster(srcSampleModel, origin);
    }
    




    SampleModel dstSampleModel = dest.getSampleModel();
    if (destComponentSize == null) {
      destComponentSize = dstSampleModel.getSampleSize();
    }
    PixelAccessor srcAcc = new PixelAccessor(srcSampleModel, null);
    UnpackedImageData srcUid = srcAcc.getPixels(src, src.getBounds(), srcSampleModel.getDataType(), false);
    


    switch (srcSampleModel.getDataType())
    {
    case 0: 
      CIEXYZToRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
      
      break;
    case 1: 
    case 2: 
      CIEXYZToRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
      
      break;
    case 3: 
      CIEXYZToRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
      
      break;
    case 4: 
      CIEXYZToRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
      
      break;
    case 5: 
      CIEXYZToRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
    }
    
    

    return dest;
  }
  





















  protected static void checkParameters(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    if (src == null) {
      throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI0"));
    }
    if (src.getNumBands() != 3) {
      throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI1"));
    }
    if ((dest != null) && (dest.getNumBands() != 3)) {
      throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI2"));
    }
    if ((srcComponentSize != null) && (srcComponentSize.length != 3)) {
      throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI3"));
    }
    if ((destComponentSize != null) && (destComponentSize.length != 3)) {
      throw new IllegalArgumentException(JaiI18N.getString("ColorSpaceJAI4"));
    }
  }
  













  static void convertToSigned(double[] buf, int dataType)
  {
    if (dataType == 2) {
      for (int i = 0; i < buf.length; i++) {
        short temp = (short)((int)buf[i] & 0xFFFF);
        buf[i] = temp;
      }
      
    } else if (dataType == 3) {
      for (int i = 0; i < buf.length; i++) {
        int temp = (int)(buf[i] & 0xFFFFFFFF);
        buf[i] = temp;
      }
    }
  }
  
  static void XYZ2RGB(float[] XYZ, float[] RGB) {
    RGB[0] = (2.9311228F * XYZ[0] - 1.4111496F * XYZ[1] - 0.6038046F * XYZ[2]);
    RGB[1] = (-0.8763701F * XYZ[0] + 1.7219844F * XYZ[1] + 0.0502565F * XYZ[2]);
    RGB[2] = (0.05038065F * XYZ[0] - 0.187272F * XYZ[1] + 1.280027F * XYZ[2]);
    
    for (int i = 0; i < 3; i++) {
      float v = RGB[i];
      
      if (v < 0.0F) {
        v = 0.0F;
      }
      if (v < 0.0031308F) {
        RGB[i] = (12.92F * v);
      } else {
        if (v > 1.0F) {
          v = 1.0F;
        }
        RGB[i] = ((float)(1.055D * Math.pow(v, 0.4166666666666667D) - 0.055D));
      }
    }
  }
  
  private static void roundValues(double[] data) {
    for (int i = 0; i < data.length; i++) {
      data[i] = ((data[i] + 0.5D));
    }
  }
  


  static void CIEXYZToRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    byte[] xBuf = src.getByteData(0);
    byte[] yBuf = src.getByteData(1);
    byte[] zBuf = src.getByteData(2);
    

    float normx = (float)(1.999969482421875D / ((1L << srcComponentSize[0]) - 1L));
    float normy = (float)(1.999969482421875D / ((1L << srcComponentSize[1]) - 1L));
    float normz = (float)(1.999969482421875D / ((1L << srcComponentSize[2]) - 1L));
    

    double upperr = 1.0D;double upperg = 1.0D;double upperb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    

    if (dstType < 4) {
      upperr = (1L << destComponentSize[0]) - 1L;
      upperg = (1L << destComponentSize[1]) - 1L;
      upperb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int xStart = bandOffsets[0];
    int yStart = bandOffsets[1];
    int zStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        zStart += srcLineStride) {
      int i = 0;int xIndex = xStart;int yIndex = yStart;int zIndex = zStart;
      for (; i < width; 
          zIndex += srcPixelStride) {
        XYZ[0] = ((xBuf[xIndex] & 0xFF) * normx);
        XYZ[1] = ((yBuf[yIndex] & 0xFF) * normy);
        XYZ[2] = ((zBuf[zIndex] & 0xFF) * normz);
        
        XYZ2RGB(XYZ, RGB);
        
        dstPixels[(dIndex++)] = (upperr * RGB[0]);
        dstPixels[(dIndex++)] = (upperg * RGB[1]);
        dstPixels[(dIndex++)] = (upperb * RGB[2]);i++;xIndex += srcPixelStride;yIndex += srcPixelStride;
      }
      j++;xStart += srcLineStride;
      yStart += srcLineStride;
    }
    
















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  


  private static void CIEXYZToRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    short[] xBuf = src.getShortData(0);
    short[] yBuf = src.getShortData(1);
    short[] zBuf = src.getShortData(2);
    

    float normx = (float)(1.999969482421875D / ((1L << srcComponentSize[0]) - 1L));
    float normy = (float)(1.999969482421875D / ((1L << srcComponentSize[1]) - 1L));
    float normz = (float)(1.999969482421875D / ((1L << srcComponentSize[2]) - 1L));
    

    double upperr = 1.0D;double upperg = 1.0D;double upperb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    

    if (dstType < 4) {
      upperr = (1L << destComponentSize[0]) - 1L;
      upperg = (1L << destComponentSize[1]) - 1L;
      upperb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int xStart = bandOffsets[0];
    int yStart = bandOffsets[1];
    int zStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        zStart += srcLineStride) {
      int i = 0;int xIndex = xStart;int yIndex = yStart;int zIndex = zStart;
      for (; i < width; 
          zIndex += srcPixelStride) {
        XYZ[0] = ((xBuf[xIndex] & 0xFFFF) * normx);
        XYZ[1] = ((yBuf[yIndex] & 0xFFFF) * normy);
        XYZ[2] = ((zBuf[zIndex] & 0xFFFF) * normz);
        
        XYZ2RGB(XYZ, RGB);
        
        dstPixels[(dIndex++)] = (upperr * RGB[0]);
        dstPixels[(dIndex++)] = (upperg * RGB[1]);
        dstPixels[(dIndex++)] = (upperb * RGB[2]);i++;xIndex += srcPixelStride;yIndex += srcPixelStride;
      }
      j++;xStart += srcLineStride;
      yStart += srcLineStride;
    }
    
















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  


  private static void CIEXYZToRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    int[] xBuf = src.getIntData(0);
    int[] yBuf = src.getIntData(1);
    int[] zBuf = src.getIntData(2);
    

    float normx = (float)(1.999969482421875D / ((1L << srcComponentSize[0]) - 1L));
    float normy = (float)(1.999969482421875D / ((1L << srcComponentSize[1]) - 1L));
    float normz = (float)(1.999969482421875D / ((1L << srcComponentSize[2]) - 1L));
    

    double upperr = 1.0D;double upperg = 1.0D;double upperb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    

    if (dstType < 4) {
      upperr = (1L << destComponentSize[0]) - 1L;
      upperg = (1L << destComponentSize[1]) - 1L;
      upperb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int xStart = bandOffsets[0];
    int yStart = bandOffsets[1];
    int zStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        zStart += srcLineStride) {
      int i = 0;int xIndex = xStart;int yIndex = yStart;int zIndex = zStart;
      for (; i < width; 
          zIndex += srcPixelStride) {
        XYZ[0] = ((float)(xBuf[xIndex] & 0xFFFFFFFF) * normx);
        XYZ[1] = ((float)(yBuf[yIndex] & 0xFFFFFFFF) * normy);
        XYZ[2] = ((float)(zBuf[zIndex] & 0xFFFFFFFF) * normz);
        
        XYZ2RGB(XYZ, RGB);
        
        dstPixels[(dIndex++)] = (upperr * RGB[0]);
        dstPixels[(dIndex++)] = (upperg * RGB[1]);
        dstPixels[(dIndex++)] = (upperb * RGB[2]);i++;xIndex += srcPixelStride;yIndex += srcPixelStride;
      }
      j++;xStart += srcLineStride;
      yStart += srcLineStride;
    }
    
















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void CIEXYZToRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    float[] xBuf = src.getFloatData(0);
    float[] yBuf = src.getFloatData(1);
    float[] zBuf = src.getFloatData(2);
    

    double upperr = 1.0D;double upperg = 1.0D;double upperb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    

    if (dstType < 4) {
      upperr = (1L << destComponentSize[0]) - 1L;
      upperg = (1L << destComponentSize[1]) - 1L;
      upperb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int xStart = bandOffsets[0];
    int yStart = bandOffsets[1];
    int zStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        zStart += srcLineStride) {
      int i = 0;int xIndex = xStart;int yIndex = yStart;int zIndex = zStart;
      for (; i < width; 
          zIndex += srcPixelStride) {
        XYZ[0] = xBuf[xIndex];
        XYZ[1] = yBuf[yIndex];
        XYZ[2] = zBuf[zIndex];
        
        XYZ2RGB(XYZ, RGB);
        
        dstPixels[(dIndex++)] = (upperr * RGB[0]);
        dstPixels[(dIndex++)] = (upperg * RGB[1]);
        dstPixels[(dIndex++)] = (upperb * RGB[2]);i++;xIndex += srcPixelStride;yIndex += srcPixelStride;
      }
      j++;xStart += srcLineStride;
      yStart += srcLineStride;
    }
    
















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void CIEXYZToRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    double[] xBuf = src.getDoubleData(0);
    double[] yBuf = src.getDoubleData(1);
    double[] zBuf = src.getDoubleData(2);
    

    double upperr = 1.0D;double upperg = 1.0D;double upperb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    

    if (dstType < 4) {
      upperr = (1L << destComponentSize[0]) - 1L;
      upperg = (1L << destComponentSize[1]) - 1L;
      upperb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int xStart = bandOffsets[0];
    int yStart = bandOffsets[1];
    int zStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        zStart += srcLineStride) {
      int i = 0;int xIndex = xStart;int yIndex = yStart;int zIndex = zStart;
      for (; i < width; 
          zIndex += srcPixelStride) {
        XYZ[0] = ((float)xBuf[xIndex]);
        XYZ[1] = ((float)yBuf[yIndex]);
        XYZ[2] = ((float)zBuf[zIndex]);
        
        XYZ2RGB(XYZ, RGB);
        
        dstPixels[(dIndex++)] = (upperr * RGB[0]);
        dstPixels[(dIndex++)] = (upperg * RGB[1]);
        dstPixels[(dIndex++)] = (upperb * RGB[2]);i++;xIndex += srcPixelStride;yIndex += srcPixelStride;
      }
      j++;xStart += srcLineStride;
      yStart += srcLineStride;
    }
    
















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  











































































































































  public static WritableRaster RGBToCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    checkParameters(src, srcComponentSize, dest, destComponentSize);
    
    SampleModel srcSampleModel = src.getSampleModel();
    


    if (srcComponentSize == null) {
      srcComponentSize = srcSampleModel.getSampleSize();
    }
    
    if (dest == null) {
      Point origin = new Point(src.getMinX(), src.getMinY());
      dest = RasterFactory.createWritableRaster(srcSampleModel, origin);
    }
    

    SampleModel dstSampleModel = dest.getSampleModel();
    


    if (destComponentSize == null) {
      destComponentSize = dstSampleModel.getSampleSize();
    }
    PixelAccessor srcAcc = new PixelAccessor(srcSampleModel, null);
    UnpackedImageData srcUid = srcAcc.getPixels(src, src.getBounds(), srcSampleModel.getDataType(), false);
    


    switch (srcSampleModel.getDataType())
    {
    case 0: 
      RGBToCIEXYZByte(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 1: 
    case 2: 
      RGBToCIEXYZShort(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 3: 
      RGBToCIEXYZInt(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 4: 
      RGBToCIEXYZFloat(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 5: 
      RGBToCIEXYZDouble(srcUid, srcComponentSize, dest, destComponentSize);
    }
    
    
    return dest;
  }
  
  static void RGB2XYZ(float[] RGB, float[] XYZ) {
    for (int i = 0; i < 3; i++) {
      if (RGB[i] < 0.040449936F) {
        RGB[i] /= 12.92F;
      } else {
        RGB[i] = ((float)Math.pow((RGB[i] + 0.055D) / 1.055D, 2.4D));
      }
    }
    XYZ[0] = (0.45593762F * RGB[0] + 0.39533818F * RGB[1] + 0.19954965F * RGB[2]);
    
    XYZ[1] = (0.23157515F * RGB[0] + 0.7790526F * RGB[1] + 0.07864978F * RGB[2]);
    
    XYZ[2] = (0.01593493F * RGB[0] + 0.09841772F * RGB[1] + 0.7848861F * RGB[2]);
  }
  




  private static void RGBToCIEXYZByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    byte[] rBuf = src.getByteData(0);
    byte[] gBuf = src.getByteData(1);
    byte[] bBuf = src.getByteData(2);
    

    int normr = 8 - srcComponentSize[0];
    int normg = 8 - srcComponentSize[1];
    int normb = 8 - srcComponentSize[2];
    

    double normx = 1.0D;double normy = normx;double normz = normx;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isInt = dstType < 4;
    



    if (isInt) {
      normx = ((1L << destComponentSize[0]) - 1L) / 1.999969482421875D;
      normy = ((1L << destComponentSize[1]) - 1L) / 1.999969482421875D;
      normz = ((1L << destComponentSize[2]) - 1L) / 1.999969482421875D;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int rStart = bandOffsets[0];
    int gStart = bandOffsets[1];
    int bStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        bStart += srcLineStride) {
      int i = 0;int rIndex = rStart;int gIndex = gStart;int bIndex = bStart;
      for (; i < width; 
          bIndex += srcPixelStride) {
        double R = LUT[((rBuf[rIndex] & 0xFF) << normr)];
        double G = LUT[((gBuf[gIndex] & 0xFF) << normg)];
        double B = LUT[((bBuf[bIndex] & 0xFF) << normb)];
        
        if (isInt)
        {
          dstPixels[(dIndex++)] = ((0.45593763D * R + 0.39533819D * G + 0.19954964D * B) * normx);
          
          dstPixels[(dIndex++)] = ((0.23157515D * R + 0.77905262D * G + 0.07864978D * B) * normy);
          
          dstPixels[(dIndex++)] = ((0.01593493D * R + 0.09841772D * G + 0.78488615D * B) * normz);
        }
        else {
          dstPixels[(dIndex++)] = (0.45593763D * R + 0.39533819D * G + 0.19954964D * B);
          
          dstPixels[(dIndex++)] = (0.23157515D * R + 0.77905262D * G + 0.07864978D * B);
          
          dstPixels[(dIndex++)] = (0.01593493D * R + 0.09841772D * G + 0.78488615D * B);
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    



























    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void RGBToCIEXYZShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    short[] rBuf = src.getShortData(0);
    short[] gBuf = src.getShortData(1);
    short[] bBuf = src.getShortData(2);
    

    float normr = (1 << srcComponentSize[0]) - 1;
    float normg = (1 << srcComponentSize[1]) - 1;
    float normb = (1 << srcComponentSize[2]) - 1;
    

    double normx = 1.0D;double normy = 1.0D;double normz = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isInt = dstType < 4;
    


    if (isInt) {
      normx = ((1L << destComponentSize[0]) - 1L) / 1.999969482421875D;
      normy = ((1L << destComponentSize[1]) - 1L) / 1.999969482421875D;
      normz = ((1L << destComponentSize[2]) - 1L) / 1.999969482421875D;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int rStart = bandOffsets[0];
    int gStart = bandOffsets[1];
    int bStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        bStart += srcLineStride) {
      int i = 0;int rIndex = rStart;int gIndex = gStart;int bIndex = bStart;
      for (; i < width; 
          bIndex += srcPixelStride) {
        RGB[0] = ((rBuf[rIndex] & 0xFFFF) / normr);
        RGB[1] = ((gBuf[gIndex] & 0xFFFF) / normg);
        RGB[2] = ((bBuf[bIndex] & 0xFFFF) / normb);
        
        RGB2XYZ(RGB, XYZ);
        
        if (isInt) {
          dstPixels[(dIndex++)] = (XYZ[0] * normx);
          dstPixels[(dIndex++)] = (XYZ[1] * normy);
          dstPixels[(dIndex++)] = (XYZ[2] * normz);
        } else {
          dstPixels[(dIndex++)] = XYZ[0];
          dstPixels[(dIndex++)] = XYZ[1];
          dstPixels[(dIndex++)] = XYZ[2];
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    






















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void RGBToCIEXYZInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    int[] rBuf = src.getIntData(0);
    int[] gBuf = src.getIntData(1);
    int[] bBuf = src.getIntData(2);
    

    float normr = (float)((1L << srcComponentSize[0]) - 1L);
    float normg = (float)((1L << srcComponentSize[1]) - 1L);
    float normb = (float)((1L << srcComponentSize[2]) - 1L);
    

    double normx = 1.0D;double normy = 1.0D;double normz = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isInt = dstType < 4;
    


    if (isInt) {
      normx = ((1L << destComponentSize[0]) - 1L) / 1.999969482421875D;
      normy = ((1L << destComponentSize[1]) - 1L) / 1.999969482421875D;
      normz = ((1L << destComponentSize[2]) - 1L) / 1.999969482421875D;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int rStart = bandOffsets[0];
    int gStart = bandOffsets[1];
    int bStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        bStart += srcLineStride) {
      int i = 0;int rIndex = rStart;int gIndex = gStart;int bIndex = bStart;
      for (; i < width; 
          bIndex += srcPixelStride) {
        RGB[0] = ((float)(rBuf[rIndex] & 0xFFFFFFFF) / normr);
        RGB[1] = ((float)(gBuf[gIndex] & 0xFFFFFFFF) / normg);
        RGB[2] = ((float)(bBuf[bIndex] & 0xFFFFFFFF) / normb);
        
        RGB2XYZ(RGB, XYZ);
        
        if (isInt) {
          dstPixels[(dIndex++)] = (XYZ[0] * normx);
          dstPixels[(dIndex++)] = (XYZ[1] * normx);
          dstPixels[(dIndex++)] = (XYZ[2] * normx);
        } else {
          dstPixels[(dIndex++)] = XYZ[0];
          dstPixels[(dIndex++)] = XYZ[1];
          dstPixels[(dIndex++)] = XYZ[2];
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    






















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void RGBToCIEXYZFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    float[] rBuf = src.getFloatData(0);
    float[] gBuf = src.getFloatData(1);
    float[] bBuf = src.getFloatData(2);
    

    double normx = 1.0D;double normy = 1.0D;double normz = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isInt = dstType < 4;
    

    if (isInt) {
      normx = ((1L << destComponentSize[0]) - 1L) / 1.999969482421875D;
      normy = ((1L << destComponentSize[1]) - 1L) / 1.999969482421875D;
      normz = ((1L << destComponentSize[2]) - 1L) / 1.999969482421875D;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int rStart = bandOffsets[0];
    int gStart = bandOffsets[1];
    int bStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        bStart += srcLineStride) {
      int i = 0;int rIndex = rStart;int gIndex = gStart;int bIndex = bStart;
      for (; i < width; 
          bIndex += srcPixelStride) {
        RGB[0] = rBuf[rIndex];
        RGB[1] = gBuf[gIndex];
        RGB[2] = bBuf[bIndex];
        
        RGB2XYZ(RGB, XYZ);
        
        if (isInt) {
          dstPixels[(dIndex++)] = (XYZ[0] * normx);
          dstPixels[(dIndex++)] = (XYZ[1] * normx);
          dstPixels[(dIndex++)] = (XYZ[2] * normx);
        } else {
          dstPixels[(dIndex++)] = XYZ[0];
          dstPixels[(dIndex++)] = XYZ[1];
          dstPixels[(dIndex++)] = XYZ[2];
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    






















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  



  private static void RGBToCIEXYZDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    double[] rBuf = src.getDoubleData(0);
    double[] gBuf = src.getDoubleData(1);
    double[] bBuf = src.getDoubleData(2);
    

    double normx = 1.0D;double normy = 1.0D;double normz = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isInt = dstType < 4;
    
    if (isInt) {
      normx = ((1L << destComponentSize[0]) - 1L) / 1.999969482421875D;
      normy = ((1L << destComponentSize[1]) - 1L) / 1.999969482421875D;
      normz = ((1L << destComponentSize[2]) - 1L) / 1.999969482421875D;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int rStart = bandOffsets[0];
    int gStart = bandOffsets[1];
    int bStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    float[] XYZ = new float[3];
    float[] RGB = new float[3];
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        bStart += srcLineStride) {
      int i = 0;int rIndex = rStart;int gIndex = gStart;int bIndex = bStart;
      for (; i < width; 
          bIndex += srcPixelStride) {
        RGB[0] = ((float)rBuf[rIndex]);
        RGB[1] = ((float)gBuf[gIndex]);
        RGB[2] = ((float)bBuf[bIndex]);
        
        RGB2XYZ(RGB, XYZ);
        
        if (isInt) {
          dstPixels[(dIndex++)] = (XYZ[0] * normx);
          dstPixels[(dIndex++)] = (XYZ[1] * normx);
          dstPixels[(dIndex++)] = (XYZ[2] * normx);
        } else {
          dstPixels[(dIndex++)] = XYZ[0];
          dstPixels[(dIndex++)] = XYZ[1];
          dstPixels[(dIndex++)] = XYZ[2];
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    






















    if (dstType < 4) {
      roundValues(dstPixels);
    }
    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  















  protected ColorSpaceJAI(int type, int numComponents, boolean isRGBPreferredIntermediary)
  {
    super(type, numComponents);
    this.isRGBPreferredIntermediary = isRGBPreferredIntermediary;
  }
  







  public boolean isRGBPreferredIntermediary()
  {
    return isRGBPreferredIntermediary;
  }
  
  public abstract WritableRaster fromCIEXYZ(Raster paramRaster, int[] paramArrayOfInt1, WritableRaster paramWritableRaster, int[] paramArrayOfInt2);
  
  public abstract WritableRaster fromRGB(Raster paramRaster, int[] paramArrayOfInt1, WritableRaster paramWritableRaster, int[] paramArrayOfInt2);
  
  public abstract WritableRaster toCIEXYZ(Raster paramRaster, int[] paramArrayOfInt1, WritableRaster paramWritableRaster, int[] paramArrayOfInt2);
  
  public abstract WritableRaster toRGB(Raster paramRaster, int[] paramArrayOfInt1, WritableRaster paramWritableRaster, int[] paramArrayOfInt2);
}
