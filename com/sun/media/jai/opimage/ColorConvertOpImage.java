package com.sun.media.jai.opimage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.media.jai.ColorSpaceJAI;
import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterFactory;



























final class ColorConvertOpImage
  extends PointOpImage
{
  private static final ColorSpace rgbColorSpace = ColorSpace.getInstance(1000);
  

  private static SoftReference softRef = null;
  

  private ImageParameters srcParam = null;
  

  private ImageParameters dstParam = null;
  

  private ImageParameters tempParam = null;
  

  private ColorConvertOp colorConvertOp = null;
  





  private int caseNumber;
  






  private static synchronized ColorConvertOp getColorConvertOp(ColorSpace src, ColorSpace dst)
  {
    HashMap colorConvertOpBuf = null;
    
    if ((softRef == null) || ((colorConvertOpBuf = (HashMap)softRef.get()) == null))
    {

      colorConvertOpBuf = new HashMap();
      softRef = new SoftReference(colorConvertOpBuf);
    }
    
    ArrayList hashcode = new ArrayList(2);
    hashcode.add(0, src);
    hashcode.add(1, dst);
    ColorConvertOp op = (ColorConvertOp)colorConvertOpBuf.get(hashcode);
    
    if (op == null) {
      op = new ColorConvertOp(src, dst, null);
      colorConvertOpBuf.put(hashcode, op);
    }
    
    return op;
  }
  





  private static float getMinValue(int dataType)
  {
    float minValue = 0.0F;
    switch (dataType) {
    case 0: 
      minValue = 0.0F;
      break;
    case 2: 
      minValue = -32768.0F;
      break;
    case 1: 
      minValue = 0.0F;
      break;
    case 3: 
      minValue = -2.14748365E9F;
      break;
    default: 
      minValue = 0.0F;
    }
    
    return minValue;
  }
  





  private static float getRange(int dataType)
  {
    float range = 1.0F;
    switch (dataType) {
    case 0: 
      range = 255.0F;
      break;
    case 2: 
      range = 65535.0F;
      break;
    case 1: 
      range = 65535.0F;
      break;
    case 3: 
      range = 4.2949673E9F;
      break;
    default: 
      range = 1.0F;
    }
    
    return range;
  }
  














  public ColorConvertOpImage(RenderedImage source, Map config, ImageLayout layout, ColorModel colorModel)
  {
    super(source, layout, config, true);
    this.colorModel = colorModel;
    

    srcParam = new ImageParameters(source.getColorModel(), source.getSampleModel());
    
    dstParam = new ImageParameters(colorModel, sampleModel);
    
    ColorSpace srcColorSpace = srcParam.getColorModel().getColorSpace();
    ColorSpace dstColorSpace = dstParam.getColorModel().getColorSpace();
    


    if (((srcColorSpace instanceof ColorSpaceJAI)) && ((dstColorSpace instanceof ColorSpaceJAI)))
    {


      caseNumber = 1;
      tempParam = createTempParam();
    } else if ((srcColorSpace instanceof ColorSpaceJAI))
    {


      if (dstColorSpace != rgbColorSpace) {
        caseNumber = 2;
        tempParam = createTempParam();
        colorConvertOp = getColorConvertOp(rgbColorSpace, dstColorSpace);
      }
      else {
        caseNumber = 3;
      } } else if ((dstColorSpace instanceof ColorSpaceJAI))
    {


      if (srcColorSpace != rgbColorSpace) {
        caseNumber = 4;
        tempParam = createTempParam();
        colorConvertOp = getColorConvertOp(srcColorSpace, rgbColorSpace);
      } else {
        caseNumber = 5;
      }
    }
    else {
      caseNumber = 6;
      colorConvertOp = getColorConvertOp(srcColorSpace, dstColorSpace);
    }
    

    permitInPlaceOperation();
  }
  









  protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect)
  {
    WritableRaster tempRas = null;
    

    Raster source = sources[0];
    

    if (!destRect.equals(source.getBounds())) {
      source = source.createChild(x, y, width, height, x, y, null);
    }
    



    switch (caseNumber)
    {

    case 1: 
      tempRas = computeRectColorSpaceJAIToRGB(source, srcParam, null, tempParam);
      
      computeRectColorSpaceJAIFromRGB(tempRas, tempParam, dest, dstParam);
      
      break;
    



    case 2: 
      tempRas = computeRectColorSpaceJAIToRGB(source, srcParam, null, tempParam);
      
      computeRectNonColorSpaceJAI(tempRas, tempParam, dest, dstParam, destRect);
      
      break;
    case 3: 
      computeRectColorSpaceJAIToRGB(source, srcParam, dest, dstParam);
      
      break;
    

    case 4: 
      tempRas = createTempWritableRaster(source);
      computeRectNonColorSpaceJAI(source, srcParam, tempRas, tempParam, destRect);
      
      computeRectColorSpaceJAIFromRGB(tempRas, tempParam, dest, dstParam);
      
      break;
    case 5: 
      computeRectColorSpaceJAIFromRGB(source, srcParam, dest, dstParam);
      
      break;
    
    case 6: 
      computeRectNonColorSpaceJAI(source, srcParam, dest, dstParam, destRect);
    }
    
  }
  









  private WritableRaster computeRectColorSpaceJAIToRGB(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam)
  {
    src = convertRasterToUnsigned(src);
    
    ColorSpaceJAI colorSpaceJAI = (ColorSpaceJAI)srcParam.getColorModel().getColorSpace();
    
    dest = colorSpaceJAI.toRGB(src, srcParam.getComponentSize(), dest, dstParam.getComponentSize());
    

    dest = convertRasterToSigned(dest);
    return dest;
  }
  







  private WritableRaster computeRectColorSpaceJAIFromRGB(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam)
  {
    src = convertRasterToUnsigned(src);
    ColorSpaceJAI colorSpaceJAI = (ColorSpaceJAI)dstParam.getColorModel().getColorSpace();
    
    dest = colorSpaceJAI.fromRGB(src, srcParam.getComponentSize(), dest, dstParam.getComponentSize());
    

    dest = convertRasterToSigned(dest);
    return dest;
  }
  






  private void computeRectNonColorSpaceJAI(Raster src, ImageParameters srcParam, WritableRaster dest, ImageParameters dstParam, Rectangle destRect)
  {
    if ((!srcParam.isFloat()) && (!dstParam.isFloat()))
    {




      Raster s = src;
      if ((s.getMinX() != x) || (s.getMinY() != y) || (s.getWidth() != width) || (s.getHeight() != height))
      {


        s = s.createChild(x, y, width, height, x, y, null);
      }
      

      WritableRaster d = dest;
      if ((d.getMinX() != x) || (d.getMinY() != y) || (d.getWidth() != width) || (d.getHeight() != height))
      {


        d = d.createWritableChild(x, y, width, height, x, y, null);
      }
      



      synchronized (colorConvertOp.getClass())
      {

        colorConvertOp.filter(s, d);
      }
    }
    else
    {
      ColorSpace srcColorSpace = srcParam.getColorModel().getColorSpace();
      ColorSpace dstColorSpace = dstParam.getColorModel().getColorSpace();
      boolean srcFloat = srcParam.isFloat();
      float srcMinValue = srcParam.getMinValue();
      float srcRange = srcParam.getRange();
      
      boolean dstFloat = dstParam.isFloat();
      float dstMinValue = dstParam.getMinValue();
      float dstRange = dstParam.getRange();
      
      int rectYMax = y + height;
      int rectXMax = x + width;
      int numComponents = srcColorSpace.getNumComponents();
      float[] srcPixel = new float[numComponents];
      

      for (int y = y; y < rectYMax; y++) {
        for (int x = x; x < rectXMax; x++) {
          srcPixel = src.getPixel(x, y, srcPixel);
          if (!srcFloat)
          {
            for (int i = 0; i < numComponents; i++) {
              srcPixel[i] = ((srcPixel[i] - srcMinValue) / srcRange);
            }
          }
          

          float[] xyzPixel = srcColorSpace.toCIEXYZ(srcPixel);
          float[] dstPixel = dstColorSpace.fromCIEXYZ(xyzPixel);
          
          if (!dstFloat)
          {
            for (int i = 0; i < numComponents; i++) {
              dstPixel[i] = (dstPixel[i] * dstRange + dstMinValue);
            }
          }
          dest.setPixel(x, y, dstPixel);
        }
      }
    }
  }
  

  private ImageParameters createTempParam()
  {
    ColorModel cm = null;
    SampleModel sm = null;
    
    if (srcParam.getDataType() > dstParam.getDataType()) {
      cm = srcParam.getColorModel();
      sm = srcParam.getSampleModel();
    } else {
      cm = dstParam.getColorModel();
      sm = dstParam.getSampleModel();
    }
    
    cm = new ComponentColorModel(rgbColorSpace, cm.getComponentSize(), cm.hasAlpha(), cm.isAlphaPremultiplied(), cm.getTransparency(), sm.getDataType());
    




    return new ImageParameters(cm, sm);
  }
  

  private WritableRaster createTempWritableRaster(Raster src)
  {
    Point origin = new Point(src.getMinX(), src.getMinY());
    return RasterFactory.createWritableRaster(src.getSampleModel(), origin);
  }
  

  private Raster convertRasterToUnsigned(Raster ras)
  {
    int type = ras.getSampleModel().getDataType();
    WritableRaster tempRas = null;
    
    if ((type == 3) || (type == 2))
    {
      int minX = ras.getMinX();int minY = ras.getMinY();
      int w = ras.getWidth();int h = ras.getHeight();
      
      int[] buf = ras.getPixels(minX, minY, w, h, (int[])null);
      convertBufferToUnsigned(buf, type);
      
      tempRas = createTempWritableRaster(ras);
      tempRas.setPixels(minX, minY, w, h, buf);
      return tempRas;
    }
    return ras;
  }
  
  private WritableRaster convertRasterToSigned(WritableRaster ras)
  {
    int type = ras.getSampleModel().getDataType();
    WritableRaster tempRas = null;
    
    if ((type == 3) || (type == 2))
    {
      int minX = ras.getMinX();int minY = ras.getMinY();
      int w = ras.getWidth();int h = ras.getHeight();
      
      int[] buf = ras.getPixels(minX, minY, w, h, (int[])null);
      convertBufferToSigned(buf, type);
      
      if ((ras instanceof WritableRaster)) {
        tempRas = ras;
      } else
        tempRas = createTempWritableRaster(ras);
      tempRas.setPixels(minX, minY, w, h, buf);
      return tempRas;
    }
    return ras;
  }
  
  private void convertBufferToSigned(int[] buf, int type)
  {
    if (buf == null) { return;
    }
    if (type == 2) {
      for (int i = 0; i < buf.length; i++) {
        buf[i] += 32768;
      }
    } else if (type == 3) {
      for (int i = 0; i < buf.length; i++) {
        buf[i] = ((int)((buf[i] & 0xFFFFFFFF) + -2147483648L));
      }
    }
  }
  
  private void convertBufferToUnsigned(int[] buf, int type)
  {
    if (buf == null) { return;
    }
    if (type == 2) {
      for (int i = 0; i < buf.length; i++) {
        buf[i] -= 32768;
      }
    } else if (type == 3) {
      for (int i = 0; i < buf.length; i++) {
        buf[i] = ((int)((buf[i] & 0xFFFFFFFF) - -2147483648L));
      }
    }
  }
  
  private final class ImageParameters
  {
    private boolean isFloat;
    private ColorModel colorModel;
    private SampleModel sampleModel;
    private float minValue;
    private float range;
    private int[] componentSize;
    private int dataType;
    
    ImageParameters(ColorModel cm, SampleModel sm)
    {
      colorModel = cm;
      sampleModel = sm;
      dataType = sm.getDataType();
      isFloat = ((dataType == 4) || (dataType == 5));
      
      minValue = ColorConvertOpImage.getMinValue(dataType);
      range = ColorConvertOpImage.getRange(dataType);
      componentSize = cm.getComponentSize();
    }
    
    public boolean isFloat() {
      return isFloat;
    }
    
    public ColorModel getColorModel() {
      return colorModel;
    }
    
    public SampleModel getSampleModel() {
      return sampleModel;
    }
    
    public float getMinValue() {
      return minValue;
    }
    
    public float getRange() {
      return range;
    }
    
    public int[] getComponentSize() {
      return componentSize;
    }
    
    public int getDataType() {
      return dataType;
    }
  }
}
