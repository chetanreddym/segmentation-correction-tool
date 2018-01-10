package javax.media.jai;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;


































































































public final class IHSColorSpace
  extends ColorSpaceJAI
{
  private static final double PI2 = 6.283185307179586D;
  private static final double PI23 = 2.0943951023931953D;
  private static final double PI43 = 4.1887902047863905D;
  private static final double SQRT3 = Math.sqrt(3.0D);
  
  private static final double BYTESCALE = 40.58451048843331D;
  
  private static SoftReference reference = new SoftReference(null);
  

  private static byte[] acosTable = null;
  private static double[] sqrtTable = null;
  private static double[] tanTable = null;
  
  private static SoftReference acosSoftRef;
  
  private static SoftReference sqrtSoftRef;
  
  private static SoftReference tanSoftRef;
  
  public static IHSColorSpace getInstance()
  {
    synchronized (reference) {
      Object referent = reference.get();
      IHSColorSpace cs;
      if (referent == null) {
        IHSColorSpace cs;
        reference = new SoftReference(cs = new IHSColorSpace());
      }
      else {
        cs = (IHSColorSpace)referent;
      }
      
      return cs;
    }
  }
  




  protected IHSColorSpace()
  {
    super(7, 3, true);
  }
  

  private synchronized void generateACosTable()
  {
    if ((acosSoftRef == null) || (acosSoftRef.get() == null)) {
      acosTable = new byte['ϩ'];
      acosSoftRef = new SoftReference(acosTable);
      
      for (int i = 0; i <= 1000; i++) {
        acosTable[i] = ((byte)(int)(40.58451048843331D * Math.acos((i - 500) * 0.002D) + 0.5D));
      }
    }
  }
  
  private synchronized void generateSqrtTable()
  {
    if ((sqrtSoftRef == null) || (sqrtSoftRef.get() == null)) {
      sqrtTable = new double['ϩ'];
      sqrtSoftRef = new SoftReference(sqrtTable);
      
      for (int i = 0; i <= 1000; i++) {
        sqrtTable[i] = Math.sqrt(i / 1000.0D);
      }
    }
  }
  
  private synchronized void generateTanTable() {
    if ((tanSoftRef == null) || (tanSoftRef.get() == null)) {
      tanTable = new double['Ā'];
      tanSoftRef = new SoftReference(tanTable);
      
      for (int i = 0; i < 256; i++) {
        tanTable[i] = Math.tan(i * 6.283185307179586D / 255.0D);
      }
    }
  }
  

  public float[] fromCIEXYZ(float[] colorValue)
  {
    float[] rgb = new float[3];
    XYZ2RGB(colorValue, rgb);
    
    float r = rgb[0];
    float g = rgb[1];
    float b = rgb[2];
    
    float[] ihs = new float[3];
    
    ihs[0] = ((r + g + b) / 3.0F);
    float drg = r - g;
    float drb = r - b;
    float temp = (float)Math.sqrt(drg * drg + drb * (drb - drg));
    



    if (temp != 0.0F) {
      temp = (float)Math.acos((drg + drb) / temp / 2.0D);
      if (g < b)
        ihs[1] = ((float)(6.283185307179586D - temp)); else
        ihs[1] = temp;
    } else {
      ihs[1] = 6.2831855F;
    }
    float min = r < g ? r : g;
    min = min < b ? min : b;
    


    if (ihs[0] == 0.0F) {
      ihs[2] = 0.0F;
    } else {
      ihs[2] = (1.0F - min / ihs[0]);
    }
    return ihs;
  }
  


  public float[] fromRGB(float[] rgbValue)
  {
    float r = rgbValue[0];
    float g = rgbValue[1];
    float b = rgbValue[2];
    
    r = r > 1.0F ? 1.0F : r < 0.0F ? 0.0F : r;
    g = g > 1.0F ? 1.0F : g < 0.0F ? 0.0F : g;
    b = b > 1.0F ? 1.0F : b < 0.0F ? 0.0F : b;
    
    float[] ihs = new float[3];
    
    ihs[0] = ((r + g + b) / 3.0F);
    float drg = r - g;
    float drb = r - b;
    float temp = (float)Math.sqrt(drg * drg + drb * (drb - drg));
    



    if (temp != 0.0F) {
      temp = (float)Math.acos((drg + drb) / temp / 2.0D);
      if (g < b)
        ihs[1] = ((float)(6.283185307179586D - temp)); else
        ihs[1] = temp;
    } else { ihs[1] = 6.2831855F;
    }
    float min = r < g ? r : g;
    min = min < b ? min : b;
    


    if (ihs[0] == 0.0F) {
      ihs[2] = 0.0F;
    } else {
      ihs[2] = (1.0F - min / ihs[0]);
    }
    return ihs;
  }
  


  public float[] toCIEXYZ(float[] colorValue)
  {
    float i = colorValue[0];
    float h = colorValue[1];
    float s = colorValue[2];
    
    i = i > 1.0F ? 1.0F : i < 0.0F ? 0.0F : i;
    h = h > 6.2831855F ? 6.2831855F : h < 0.0F ? 0.0F : h;
    s = s > 1.0F ? 1.0F : s < 0.0F ? 0.0F : s;
    
    float r = 0.0F;float g = 0.0F;float b = 0.0F;
    


    if (s == 0.0F) {
      r = g = b = i;

    }
    else if ((h >= 2.0943951023931953D) && (h < 4.1887902047863905D)) {
      r = (1.0F - s) * i;
      float c1 = 3.0F * i - r;
      float c2 = (float)(SQRT3 * (r - i) * Math.tan(h));
      g = (c1 + c2) / 2.0F;
      b = (c1 - c2) / 2.0F;
    }
    else if (h > 4.1887902047863905D) {
      g = (1.0F - s) * i;
      float c1 = 3.0F * i - g;
      float c2 = (float)(SQRT3 * (g - i) * Math.tan(h - 2.0943951023931953D));
      b = (c1 + c2) / 2.0F;
      r = (c1 - c2) / 2.0F;
    }
    else if (h < 2.0943951023931953D) {
      b = (1.0F - s) * i;
      float c1 = 3.0F * i - b;
      float c2 = (float)(SQRT3 * (b - i) * Math.tan(h - 4.1887902047863905D));
      r = (c1 + c2) / 2.0F;
      g = (c1 - c2) / 2.0F;
    }
    

    float[] xyz = new float[3];
    float[] rgb = new float[3];
    rgb[0] = r;
    rgb[1] = g;
    rgb[2] = b;
    
    RGB2XYZ(rgb, xyz);
    
    return xyz;
  }
  


  public float[] toRGB(float[] colorValue)
  {
    float i = colorValue[0];
    float h = colorValue[1];
    float s = colorValue[2];
    
    i = i > 1.0F ? 1.0F : i < 0.0F ? 0.0F : i;
    h = h > 6.2831855F ? 6.2831855F : h < 0.0F ? 0.0F : h;
    s = s > 1.0F ? 1.0F : s < 0.0F ? 0.0F : s;
    
    float[] rgb = new float[3];
    

    if (s == 0.0F) {
      float tmp109_108 = (rgb[2] = i);rgb[1] = tmp109_108;rgb[0] = tmp109_108;

    }
    else if ((h >= 2.0943951023931953D) && (h <= 4.1887902047863905D)) {
      float r = (1.0F - s) * i;
      float c1 = 3.0F * i - r;
      float c2 = (float)(SQRT3 * (r - i) * Math.tan(h));
      rgb[0] = r;
      rgb[1] = ((c1 + c2) / 2.0F);
      rgb[2] = ((c1 - c2) / 2.0F);
    }
    else if (h > 4.1887902047863905D) {
      float g = (1.0F - s) * i;
      float c1 = 3.0F * i - g;
      float c2 = (float)(SQRT3 * (g - i) * Math.tan(h - 2.0943951023931953D));
      rgb[0] = ((c1 - c2) / 2.0F);
      rgb[1] = g;
      rgb[2] = ((c1 + c2) / 2.0F);
    }
    else if (h < 2.0943951023931953D) {
      float b = (1.0F - s) * i;
      float c1 = 3.0F * i - b;
      float c2 = (float)(SQRT3 * (b - i) * Math.tan(h - 4.1887902047863905D));
      rgb[0] = ((c1 + c2) / 2.0F);
      rgb[1] = ((c1 - c2) / 2.0F);
      rgb[2] = b;
    }
    

    return rgb;
  }
  






  public WritableRaster fromCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    WritableRaster tempRas = CIEXYZToRGB(src, srcComponentSize, null, null);
    
    return fromRGB(tempRas, tempRas.getSampleModel().getSampleSize(), dest, destComponentSize);
  }
  








  public WritableRaster fromRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
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
      fromRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 1: 
    case 2: 
      fromRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 3: 
      fromRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 4: 
      fromRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 5: 
      fromRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
    }
    
    return dest;
  }
  

  private void fromRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    byte[] rBuf = src.getByteData(0);
    byte[] gBuf = src.getByteData(1);
    byte[] bBuf = src.getByteData(2);
    
    int normr = 8 - srcComponentSize[0];
    int normg = 8 - srcComponentSize[1];
    int normb = 8 - srcComponentSize[2];
    
    double normi = 0.00392156862745098D;double normh = 1.0D;double norms = 1.0D;
    
    int bnormi = 0;int bnormh = 0;int bnorms = 0;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      bnormi = 8 - destComponentSize[0];
      bnormh = 8 - destComponentSize[1];
      bnorms = 8 - destComponentSize[2];
      generateACosTable();
      generateSqrtTable();
    }
    else if (dstType < 4) {
      normi = ((1L << destComponentSize[0]) - 1L) / 255.0D;
      normh = ((1L << destComponentSize[1]) - 1L) / 6.283185307179586D;
      norms = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
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
        short R = (short)((rBuf[rIndex] & 0xFF) << normr);
        short G = (short)((gBuf[gIndex] & 0xFF) << normg);
        short B = (short)((bBuf[bIndex] & 0xFF) << normb);
        
        if (isByte) {
          float intensity = (R + G + B) / 3.0F;
          dstIntPixels[(dIndex++)] = ((short)(int)(intensity + 0.5F) >> bnormi);
          

          short drg = (short)(R - G);
          short drb = (short)(R - B);
          
          int tint = drg * drg + drb * (drb - drg);
          
          short sum = (short)(drg + drb);
          double temp;
          double temp; if (tint != 0)
            temp = sqrtTable[((int)(250.0D * sum * sum / tint + 0.5D))]; else
            temp = -1.0D;
          int hue;
          int hue;
          if (sum > 0) {
            hue = acosTable[((int)(500.0D * temp + 0.5D) + 500)];
          } else {
            hue = acosTable[((int)(-500.0D * temp - 0.5D) + 500)];
          }
          if (B >= G) {
            dstIntPixels[(dIndex++)] = (255 - hue >> bnormh);
          } else {
            dstIntPixels[(dIndex++)] = (hue >> bnormh);
          }
          short min = G > B ? B : G;
          min = R > min ? min : R;
          dstIntPixels[(dIndex++)] = (255 - (int)(255 * min / intensity + 0.5F) >> bnorms);
        }
        else
        {
          float intensity = (R + G + B) / 3.0F;
          dstPixels[(dIndex++)] = (normi * intensity);
          
          double drg = R - G;
          double drb = R - B;
          double temp = Math.sqrt(drg * drg + drb * (drb - drg));
          
          if (temp != 0.0D) {
            temp = Math.acos((drg + drb) / temp / 2.0D);
            if (B >= G)
              temp = 6.283185307179586D - temp;
          } else { temp = 6.283185307179586D;
          }
          dstPixels[(dIndex++)] = (normh * temp);
          
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstPixels[(dIndex++)] = (norms * (1.0D - min / intensity));
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    



























































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  


  private void fromRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    short[] rBuf = src.getShortData(0);
    short[] gBuf = src.getShortData(1);
    short[] bBuf = src.getShortData(2);
    

    int normr = 16 - srcComponentSize[0];
    int normg = 16 - srcComponentSize[1];
    int normb = 16 - srcComponentSize[2];
    

    double normi = 1.5259021896696422E-5D;double normh = 1.0D;double norms = 1.0D;
    

    int bnormi = 0;int bnormh = 0;int bnorms = 0;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      bnormi = 16 - destComponentSize[0];
      bnormh = 8 - destComponentSize[1];
      bnorms = 8 - destComponentSize[2];
      generateACosTable();
      generateSqrtTable();
    }
    else if (dstType < 4) {
      normi = ((1L << destComponentSize[0]) - 1L) / 65535.0D;
      normh = ((1L << destComponentSize[1]) - 1L) / 6.283185307179586D;
      norms = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
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
        int R = (rBuf[rIndex] & 0xFFFF) << normr;
        int G = (gBuf[gIndex] & 0xFFFF) << normg;
        int B = (bBuf[bIndex] & 0xFFFF) << normb;
        
        if (isByte) {
          float intensity = (R + G + B) / 3.0F;
          dstIntPixels[(dIndex++)] = ((int)(intensity + 0.5F) >> bnormi);
          

          int drg = R - G;
          int drb = R - B;
          
          double tint = drg * drg + drb * (drb - drg);
          
          double sum = drg + drb;
          double temp;
          double temp; if (tint != 0.0D)
            temp = sqrtTable[((int)(250.0D * sum * sum / tint + 0.5D))]; else
            temp = -1.0D;
          int hue;
          int hue;
          if (sum > 0.0D) {
            hue = acosTable[((int)(500.0D * temp + 0.5D) + 500)];
          } else {
            hue = acosTable[((int)(-500.0D * temp - 0.5D) + 500)];
          }
          if (B >= G) {
            dstIntPixels[(dIndex++)] = (255 - hue >> bnormh);
          } else {
            dstIntPixels[(dIndex++)] = (hue >> bnormh);
          }
          int min = G > B ? B : G;
          min = R > min ? min : R;
          dstIntPixels[(dIndex++)] = (255 - (int)(255 * min / intensity + 0.5F) >> bnorms);

        }
        else
        {
          float intensity = (R + G + B) / 3.0F;
          dstPixels[(dIndex++)] = (normi * intensity);
          
          double drg = R - G;
          double drb = R - B;
          double temp = Math.sqrt(drg * drg + drb * (drb - drg));
          
          if (temp != 0.0D) {
            temp = Math.acos((drg + drb) / temp / 2.0D);
            if (B >= G)
              temp = 6.283185307179586D - temp;
          } else { temp = 6.283185307179586D;
          }
          dstPixels[(dIndex++)] = (normh * temp);
          
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstPixels[(dIndex++)] = (norms * (1.0D - min / intensity));
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    





























































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  


  private void fromRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    int[] rBuf = src.getIntData(0);
    int[] gBuf = src.getIntData(1);
    int[] bBuf = src.getIntData(2);
    
    int normr = 32 - srcComponentSize[0];
    int normg = 32 - srcComponentSize[1];
    int normb = 32 - srcComponentSize[2];
    
    double range = 4.294967295E9D;
    double normi = 1.0D / range;double normh = 1.0D;double norms = 1.0D;
    
    int bnormi = 0;int bnormh = 0;int bnorms = 0;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      bnormi = 32 - destComponentSize[0];
      bnormh = 8 - destComponentSize[1];
      bnorms = 8 - destComponentSize[2];
      generateACosTable();
      generateSqrtTable();
    }
    else if (dstType < 4) {
      normi = ((1L << destComponentSize[0]) - 1L) / range;
      normh = ((1L << destComponentSize[1]) - 1L) / 6.283185307179586D;
      norms = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
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
        long R = (rBuf[rIndex] & 0xFFFFFFFF) << normr;
        long G = (gBuf[gIndex] & 0xFFFFFFFF) << normg;
        long B = (bBuf[bIndex] & 0xFFFFFFFF) << normb;
        
        if (isByte) {
          float intensity = (float)(R + G + B) / 3.0F;
          dstIntPixels[(dIndex++)] = ((int)((intensity + 0.5F) >> bnormi));
          

          long drg = R - G;
          long drb = R - B;
          
          double tint = drg * drg + drb * (drb - drg);
          
          double sum = drg + drb;
          double temp;
          double temp; if (tint != 0.0D)
            temp = sqrtTable[((int)(250.0D * sum * sum / tint + 0.5D))]; else
            temp = -1.0D;
          int hue;
          int hue;
          if (sum > 0.0D) {
            hue = acosTable[((int)(500.0D * temp + 0.5D) + 500)];
          } else {
            hue = acosTable[((int)(-500.0D * temp - 0.5D) + 500)];
          }
          if (B >= G) {
            dstIntPixels[(dIndex++)] = (255 - hue >> bnormh);
          } else {
            dstIntPixels[(dIndex++)] = (hue >> bnormh);
          }
          long min = G > B ? B : G;
          min = R > min ? min : R;
          dstIntPixels[(dIndex++)] = (255 - (int)((float)(255L * min) / intensity + 0.5F) >> bnorms);
        }
        else
        {
          float intensity = (float)(R + G + B) / 3.0F;
          dstPixels[(dIndex++)] = (normi * intensity);
          
          double drg = R - G;
          double drb = R - B;
          double temp = Math.sqrt(drg * drg + drb * (drb - drg));
          
          if (temp != 0.0D) {
            temp = Math.acos((drg + drb) / temp / 2.0D);
            if (B >= G)
              temp = 6.283185307179586D - temp;
          } else { temp = 6.283185307179586D;
          }
          dstPixels[(dIndex++)] = (normh * temp);
          
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstPixels[(dIndex++)] = (norms * (1.0D - min / intensity));
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    




























































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  


  private void fromRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    float[] rBuf = src.getFloatData(0);
    float[] gBuf = src.getFloatData(1);
    float[] bBuf = src.getFloatData(2);
    
    double normi = 1.0D;double normh = 1.0D;double norms = 1.0D;
    
    int bnormi = 0;int bnormh = 0;int bnorms = 0;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      bnormi = (1 << destComponentSize[0]) - 1;
      bnormh = 8 - destComponentSize[1];
      bnorms = 8 - destComponentSize[2];
      generateACosTable();
      generateSqrtTable();
    }
    else if (dstType < 4) {
      normi = (1L << destComponentSize[0]) - 1L;
      normh = ((1L << destComponentSize[1]) - 1L) / 6.283185307179586D;
      norms = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
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
        float R = rBuf[rIndex];
        float G = gBuf[gIndex];
        float B = bBuf[bIndex];
        
        if (isByte) {
          float intensity = (R + G + B) / 3.0F;
          dstIntPixels[(dIndex++)] = ((int)(intensity * bnormi + 0.5F));
          
          float drg = R - G;
          float drb = R - B;
          
          double tint = drg * drg + drb * (drb - drg);
          
          double sum = drg + drb;
          double temp;
          double temp; if (tint != 0.0D)
            temp = sqrtTable[((int)(250.0D * sum * sum / tint + 0.5D))]; else
            temp = -1.0D;
          int hue;
          int hue;
          if (sum > 0.0D) {
            hue = acosTable[((int)(500.0D * temp + 0.5D) + 500)];
          } else {
            hue = acosTable[((int)(-500.0D * temp - 0.5D) + 500)];
          }
          if (B >= G) {
            dstIntPixels[(dIndex++)] = (255 - hue >> bnormh);
          } else {
            dstIntPixels[(dIndex++)] = (hue >> bnormh);
          }
          float min = G > B ? B : G;
          min = R > min ? min : R;
          dstIntPixels[(dIndex++)] = (255 - (int)(255.0F * min / intensity + 0.5F) >> bnorms);
        }
        else
        {
          float intensity = (R + G + B) / 3.0F;
          dstPixels[(dIndex++)] = (normi * intensity);
          
          double drg = R - G;
          double drb = R - B;
          double temp = Math.sqrt(drg * drg + drb * (drb - drg));
          
          if (temp != 0.0D) {
            temp = Math.acos((drg + drb) / temp / 2.0D);
            if (B >= G)
              temp = 6.283185307179586D - temp;
          } else { temp = 6.283185307179586D;
          }
          dstPixels[(dIndex++)] = (normh * temp);
          
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstPixels[(dIndex++)] = (norms * (1.0D - min / intensity));
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    




























































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  


  private void fromRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    double[] rBuf = src.getDoubleData(0);
    double[] gBuf = src.getDoubleData(1);
    double[] bBuf = src.getDoubleData(2);
    
    double normi = 1.0D;double normh = 1.0D;double norms = 1.0D;
    
    int bnormi = 0;int bnormh = 0;int bnorms = 0;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      bnormi = (1 << destComponentSize[0]) - 1;
      bnormh = 8 - destComponentSize[1];
      bnorms = 8 - destComponentSize[2];
      generateACosTable();
      generateSqrtTable();
    }
    else if (dstType < 4) {
      normi = (1L << destComponentSize[0]) - 1L;
      normh = ((1L << destComponentSize[1]) - 1L) / 6.283185307179586D;
      norms = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
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
        double R = rBuf[rIndex];
        double G = gBuf[gIndex];
        double B = bBuf[bIndex];
        
        if (isByte) {
          double intensity = (R + G + B) / 3.0D;
          dstIntPixels[(dIndex++)] = ((int)(intensity * bnormi + 0.5D));
          
          double drg = R - G;
          double drb = R - B;
          
          double tint = drg * drg + drb * (drb - drg);
          
          double sum = drg + drb;
          double temp;
          double temp; if (tint != 0.0D)
            temp = sqrtTable[((int)(250.0D * sum * sum / tint + 0.5D))]; else
            temp = -1.0D;
          int hue;
          int hue;
          if (sum > 0.0D) {
            hue = acosTable[((int)(500.0D * temp + 0.5D) + 500)];
          } else {
            hue = acosTable[((int)(-500.0D * temp - 0.5D) + 500)];
          }
          if (B >= G) {
            dstIntPixels[(dIndex++)] = (255 - hue >> bnormh);
          } else {
            dstIntPixels[(dIndex++)] = (hue >> bnormh);
          }
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstIntPixels[(dIndex++)] = (255 - (int)(255.0D * min / intensity + 0.5D) >> bnorms);
        }
        else
        {
          double intensity = (R + G + B) / 3.0D;
          dstPixels[(dIndex++)] = (normi * intensity);
          
          double drg = R - G;
          double drb = R - B;
          double temp = Math.sqrt(drg * drg + drb * (drb - drg));
          
          if (temp != 0.0D) {
            temp = Math.acos((drg + drb) / temp / 2.0D);
            if (B >= G)
              temp = 6.283185307179586D - temp;
          } else { temp = 6.283185307179586D;
          }
          dstPixels[(dIndex++)] = (normh * temp);
          
          double min = G > B ? B : G;
          min = R > min ? min : R;
          dstPixels[(dIndex++)] = (norms * (1.0D - min / intensity));
        }
        i++;rIndex += srcPixelStride;
        gIndex += srcPixelStride;
      }
      j++;rStart += srcLineStride;
      gStart += srcLineStride;
    }
    


























































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  







  public WritableRaster toCIEXYZ(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    WritableRaster tempRas = toRGB(src, srcComponentSize, null, null);
    return RGBToCIEXYZ(tempRas, tempRas.getSampleModel().getSampleSize(), dest, destComponentSize);
  }
  








  public WritableRaster toRGB(Raster src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
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
      toRGBByte(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 1: 
    case 2: 
      toRGBShort(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 3: 
      toRGBInt(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 4: 
      toRGBFloat(srcUid, srcComponentSize, dest, destComponentSize);
      break;
    case 5: 
      toRGBDouble(srcUid, srcComponentSize, dest, destComponentSize);
    }
    
    return dest;
  }
  

  private void toRGBByte(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    byte[] iBuf = src.getByteData(0);
    byte[] hBuf = src.getByteData(1);
    byte[] sBuf = src.getByteData(2);
    
    double normi = 1.0D / ((1 << srcComponentSize[0]) - 1);
    double normh = 1.0D / ((1 << srcComponentSize[1]) - 1) * 6.283185307179586D;
    double norms = 1.0D / ((1 << srcComponentSize[2]) - 1);
    
    double normr = 1.0D;double normg = 1.0D;double normb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    boolean isByte = dstType == 0;
    
    if (isByte) {
      generateTanTable();
    }
    if (dstType < 4) {
      normr = (1L << destComponentSize[0]) - 1L;
      normg = (1L << destComponentSize[1]) - 1L;
      normb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = null;
    int[] dstIntPixels = null;
    
    if (isByte) {
      dstIntPixels = new int[3 * height * width];
    } else {
      dstPixels = new double[3 * height * width];
    }
    int iStart = bandOffsets[0];
    int hStart = bandOffsets[1];
    int sStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        sStart += srcLineStride) {
      int i = 0;int iIndex = iStart;int hIndex = hStart;int sIndex = sStart;
      for (; i < width; 
          sIndex += srcPixelStride) {
        double I = (iBuf[iIndex] & 0xFF) * normi;
        int h = hBuf[hIndex] & 0xFF;
        double S = (sBuf[sIndex] & 0xFF) * norms;
        
        if (isByte) {
          float b;
          float g;
          float r = g = b = (float)I;
          
          if (S != 0.0D) {
            if ((h >= 85) && (h <= 170)) {
              r = (float)((1.0D - S) * I);
              float c1 = (float)(3.0D * I - r);
              float c2 = (float)(SQRT3 * (r - I) * tanTable[h]);
              g = (c1 + c2) / 2.0F;
              b = (c1 - c2) / 2.0F;
            }
            else if (h > 170) {
              g = (float)((1.0D - S) * I);
              float c1 = (float)(3.0D * I - g);
              float c2 = (float)(SQRT3 * (g - I) * tanTable[(h - 85)]);
              b = (c1 + c2) / 2.0F;
              r = (c1 - c2) / 2.0F;
            }
            else if (h < 85) {
              b = (float)((1.0D - S) * I);
              float c1 = (float)(3.0D * I - b);
              float c2 = (float)(SQRT3 * (b - I) * tanTable[(h + 85)]);
              r = (c1 + c2) / 2.0F;
              g = (c1 - c2) / 2.0F;
            }
          }
          dstIntPixels[(dIndex++)] = ((int)((r > 1.0F ? 1.0F : r < 0.0F ? 0.0F : r) * normr + 0.5D));
          
          dstIntPixels[(dIndex++)] = ((int)((g > 1.0F ? 1.0F : g < 0.0F ? 0.0F : g) * normg + 0.5D));
          
          dstIntPixels[(dIndex++)] = ((int)((b > 1.0F ? 1.0F : b < 0.0F ? 0.0F : b) * normb + 0.5D));
        }
        else
        {
          double B;
          double G;
          double R = G = B = I;
          if (S != 0.0D) {
            double H = h * normh;
            if ((H >= 2.0943951023931953D) && (H <= 4.1887902047863905D)) {
              R = (1.0D - S) * I;
              double c1 = 3.0D * I - R;
              double c2 = SQRT3 * (R - I) * Math.tan(H);
              G = (c1 + c2) / 2.0D;
              B = (c1 - c2) / 2.0D;
            }
            else if (H > 4.1887902047863905D) {
              G = (1.0D - S) * I;
              double c1 = 3.0D * I - G;
              double c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953D);
              B = (c1 + c2) / 2.0D;
              R = (c1 - c2) / 2.0D;
            }
            else if (H < 2.0943951023931953D) {
              B = (1.0D - S) * I;
              double c1 = 3.0D * I - B;
              double c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905D);
              R = (c1 + c2) / 2.0D;
              G = (c1 - c2) / 2.0D;
            }
          }
          dstPixels[(dIndex++)] = ((R > 1.0D ? 1.0D : R < 0.0D ? 0.0D : R) * normr);
          dstPixels[(dIndex++)] = ((G > 1.0D ? 1.0D : G < 0.0D ? 0.0D : G) * normg);
          dstPixels[(dIndex++)] = ((B > 1.0D ? 1.0D : B < 0.0D ? 0.0D : B) * normb);
        }
        i++;iIndex += srcPixelStride;
        hIndex += srcPixelStride;
      }
      j++;iStart += srcLineStride;
      hStart += srcLineStride;
    }
    











































































    if (isByte) {
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstIntPixels);
    }
    else {
      convertToSigned(dstPixels, dstType);
      dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
    }
  }
  



  private void toRGBShort(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    short[] iBuf = src.getShortData(0);
    short[] hBuf = src.getShortData(1);
    short[] sBuf = src.getShortData(2);
    
    double normi = 1.0D / ((1 << srcComponentSize[0]) - 1);
    double normh = 1.0D / ((1 << srcComponentSize[1]) - 1) * 6.283185307179586D;
    double norms = 1.0D / ((1 << srcComponentSize[2]) - 1);
    
    double normr = 1.0D;double normg = 1.0D;double normb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    
    if (dstType < 4) {
      normr = (1L << destComponentSize[0]) - 1L;
      normg = (1L << destComponentSize[1]) - 1L;
      normb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int iStart = bandOffsets[0];
    int hStart = bandOffsets[1];
    int sStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        sStart += srcLineStride) {
      int i = 0;int iIndex = iStart;int hIndex = hStart;int sIndex = sStart;
      for (; i < width; 
          sIndex += srcPixelStride) {
        double I = (iBuf[iIndex] & 0xFFFF) * normi;
        double H = (hBuf[hIndex] & 0xFFFF) * normh;
        double S = (sBuf[sIndex] & 0xFFFF) * norms;
        double B;
        double G;
        double R = G = B = I;
        if (S != 0.0D) {
          if ((H >= 2.0943951023931953D) && (H <= 4.1887902047863905D)) {
            R = (1.0D - S) * I;
            double c1 = 3.0D * I - R;
            double c2 = SQRT3 * (R - I) * Math.tan(H);
            G = (c1 + c2) / 2.0D;
            B = (c1 - c2) / 2.0D;
          }
          else if (H > 4.1887902047863905D) {
            G = (1.0D - S) * I;
            double c1 = 3.0D * I - G;
            double c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953D);
            B = (c1 + c2) / 2.0D;
            R = (c1 - c2) / 2.0D;
          }
          else if (H < 2.0943951023931953D) {
            B = (1.0D - S) * I;
            double c1 = 3.0D * I - B;
            double c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905D);
            R = (c1 + c2) / 2.0D;
            G = (c1 - c2) / 2.0D;
          }
        }
        
        dstPixels[(dIndex++)] = ((R > 1.0D ? 1.0D : R < 0.0D ? 0.0D : R) * normr);
        dstPixels[(dIndex++)] = ((G > 1.0D ? 1.0D : G < 0.0D ? 0.0D : G) * normg);
        dstPixels[(dIndex++)] = ((B > 1.0D ? 1.0D : B < 0.0D ? 0.0D : B) * normb);i++;iIndex += srcPixelStride;hIndex += srcPixelStride;
      }
      j++;iStart += srcLineStride;
      hStart += srcLineStride;
    }
    





































    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  
  private void toRGBInt(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    int[] iBuf = src.getIntData(0);
    int[] hBuf = src.getIntData(1);
    int[] sBuf = src.getIntData(2);
    
    double normi = 1.0D / ((1L << srcComponentSize[0]) - 1L);
    double normh = 1.0D / ((1L << srcComponentSize[1]) - 1L) * 6.283185307179586D;
    double norms = 1.0D / ((1L << srcComponentSize[2]) - 1L);
    
    double normr = 1.0D;double normg = 1.0D;double normb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    
    if (dstType < 4) {
      normr = (1L << destComponentSize[0]) - 1L;
      normg = (1L << destComponentSize[1]) - 1L;
      normb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int iStart = bandOffsets[0];
    int hStart = bandOffsets[1];
    int sStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        sStart += srcLineStride) {
      int i = 0;int iIndex = iStart;int hIndex = hStart;int sIndex = sStart;
      for (; i < width; 
          sIndex += srcPixelStride) {
        double I = (iBuf[iIndex] & 0xFFFFFFFF) * normi;
        double H = (hBuf[hIndex] & 0xFFFFFFFF) * normh;
        double S = (sBuf[sIndex] & 0xFFFFFFFF) * norms;
        
        double B;
        double G;
        double R = G = B = I;
        if (S != 0.0D) {
          if ((H >= 2.0943951023931953D) && (H <= 4.1887902047863905D)) {
            R = (1.0D - S) * I;
            double c1 = 3.0D * I - R;
            double c2 = SQRT3 * (R - I) * Math.tan(H);
            G = (c1 + c2) / 2.0D;
            B = (c1 - c2) / 2.0D;
          }
          else if (H > 4.1887902047863905D) {
            G = (1.0D - S) * I;
            double c1 = 3.0D * I - G;
            double c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953D);
            B = (c1 + c2) / 2.0D;
            R = (c1 - c2) / 2.0D;
          }
          else if (H < 2.0943951023931953D) {
            B = (1.0D - S) * I;
            double c1 = 3.0D * I - B;
            double c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905D);
            R = (c1 + c2) / 2.0D;
            G = (c1 - c2) / 2.0D;
          }
        }
        
        dstPixels[(dIndex++)] = ((R > 1.0D ? 1.0D : R < 0.0D ? 0.0D : R) * normr);
        dstPixels[(dIndex++)] = ((G > 1.0D ? 1.0D : G < 0.0D ? 0.0D : G) * normg);
        dstPixels[(dIndex++)] = ((B > 1.0D ? 1.0D : B < 0.0D ? 0.0D : B) * normb);i++;iIndex += srcPixelStride;hIndex += srcPixelStride;
      }
      j++;iStart += srcLineStride;
      hStart += srcLineStride;
    }
    





































    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  
  private void toRGBFloat(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    float[] iBuf = src.getFloatData(0);
    float[] hBuf = src.getFloatData(1);
    float[] sBuf = src.getFloatData(2);
    
    double normr = 1.0D;double normg = 1.0D;double normb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    
    if (dstType < 4) {
      normr = (1L << destComponentSize[0]) - 1L;
      normg = (1L << destComponentSize[1]) - 1L;
      normb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int iStart = bandOffsets[0];
    int hStart = bandOffsets[1];
    int sStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        sStart += srcLineStride) {
      int i = 0;int iIndex = iStart;int hIndex = hStart;int sIndex = sStart;
      for (; i < width; 
          sIndex += srcPixelStride) {
        double I = iBuf[iIndex];
        double H = hBuf[hIndex];
        double S = sBuf[sIndex];
        double B;
        double G;
        double R = G = B = I;
        if (S != 0.0D) {
          if ((H >= 2.0943951023931953D) && (H <= 4.1887902047863905D)) {
            R = (1.0D - S) * I;
            double c1 = 3.0D * I - R;
            double c2 = SQRT3 * (R - I) * Math.tan(H);
            G = (c1 + c2) / 2.0D;
            B = (c1 - c2) / 2.0D;
          }
          else if (H > 4.1887902047863905D) {
            G = (1.0D - S) * I;
            double c1 = 3.0D * I - G;
            double c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953D);
            B = (c1 + c2) / 2.0D;
            R = (c1 - c2) / 2.0D;
          }
          else if (H < 2.0943951023931953D) {
            B = (1.0D - S) * I;
            double c1 = 3.0D * I - B;
            double c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905D);
            R = (c1 + c2) / 2.0D;
            G = (c1 - c2) / 2.0D;
          }
        }
        
        dstPixels[(dIndex++)] = ((R > 1.0D ? 1.0D : R < 0.0D ? 0.0D : R) * normr);
        dstPixels[(dIndex++)] = ((G > 1.0D ? 1.0D : G < 0.0D ? 0.0D : G) * normg);
        dstPixels[(dIndex++)] = ((B > 1.0D ? 1.0D : B < 0.0D ? 0.0D : B) * normb);i++;iIndex += srcPixelStride;hIndex += srcPixelStride;
      }
      j++;iStart += srcLineStride;
      hStart += srcLineStride;
    }
    




































    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
  
  private void toRGBDouble(UnpackedImageData src, int[] srcComponentSize, WritableRaster dest, int[] destComponentSize)
  {
    double[] iBuf = src.getDoubleData(0);
    double[] hBuf = src.getDoubleData(1);
    double[] sBuf = src.getDoubleData(2);
    
    double normr = 1.0D;double normg = 1.0D;double normb = 1.0D;
    
    int dstType = dest.getSampleModel().getDataType();
    
    if (dstType < 4) {
      normr = (1L << destComponentSize[0]) - 1L;
      normg = (1L << destComponentSize[1]) - 1L;
      normb = (1L << destComponentSize[2]) - 1L;
    }
    
    int height = dest.getHeight();
    int width = dest.getWidth();
    
    double[] dstPixels = new double[3 * height * width];
    
    int iStart = bandOffsets[0];
    int hStart = bandOffsets[1];
    int sStart = bandOffsets[2];
    int srcPixelStride = pixelStride;
    int srcLineStride = lineStride;
    
    int dIndex = 0;
    for (int j = 0; j < height; 
        sStart += srcLineStride) {
      int i = 0;int iIndex = iStart;int hIndex = hStart;int sIndex = sStart;
      for (; i < width; 
          sIndex += srcPixelStride) {
        double I = iBuf[iIndex];
        double H = hBuf[hIndex];
        double S = sBuf[sIndex];
        
        double B;
        double G;
        double R = G = B = I;
        if (S != 0.0D) {
          if ((H >= 2.0943951023931953D) && (H <= 4.1887902047863905D)) {
            R = (1.0D - S) * I;
            double c1 = 3.0D * I - R;
            double c2 = SQRT3 * (R - I) * Math.tan(H);
            G = (c1 + c2) / 2.0D;
            B = (c1 - c2) / 2.0D;
          }
          else if (H > 4.1887902047863905D) {
            G = (1.0D - S) * I;
            double c1 = 3.0D * I - G;
            double c2 = SQRT3 * (G - I) * Math.tan(H - 2.0943951023931953D);
            B = (c1 + c2) / 2.0D;
            R = (c1 - c2) / 2.0D;
          }
          else if (H < 2.0943951023931953D) {
            B = (1.0D - S) * I;
            double c1 = 3.0D * I - B;
            double c2 = SQRT3 * (B - I) * Math.tan(H - 4.1887902047863905D);
            R = (c1 + c2) / 2.0D;
            G = (c1 - c2) / 2.0D;
          }
        }
        
        dstPixels[(dIndex++)] = ((R > 1.0D ? 1.0D : R < 0.0D ? 0.0D : R) * normr);
        dstPixels[(dIndex++)] = ((G > 1.0D ? 1.0D : G < 0.0D ? 0.0D : G) * normg);
        dstPixels[(dIndex++)] = ((B > 1.0D ? 1.0D : B < 0.0D ? 0.0D : B) * normb);i++;iIndex += srcPixelStride;hIndex += srcPixelStride;
      }
      j++;iStart += srcLineStride;
      hStart += srcLineStride;
    }
    





































    convertToSigned(dstPixels, dstType);
    dest.setPixels(dest.getMinX(), dest.getMinY(), width, height, dstPixels);
  }
}
