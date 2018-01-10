package com.sun.media.jai.codec;

import com.sun.media.jai.codecimpl.BMPCodec;
import com.sun.media.jai.codecimpl.FPXCodec;
import com.sun.media.jai.codecimpl.GIFCodec;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.PNGCodec;
import com.sun.media.jai.codecimpl.PNMCodec;
import com.sun.media.jai.codecimpl.TIFFCodec;
import com.sun.media.jai.codecimpl.WBMPCodec;
import com.sun.media.jai.codecimpl.util.FloatDoubleColorModel;
import com.sun.media.jai.util.SimpleCMYKColorSpace;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;







































public abstract class ImageCodec
{
  private static Hashtable codecs = new Hashtable();
  





  static
  {
    registerCodec(new BMPCodec());
    registerCodec(new GIFCodec());
    registerCodec(new FPXCodec());
    registerCodec(new JPEGCodec());
    registerCodec(new PNGCodec());
    registerCodec(new PNMCodec());
    registerCodec(new TIFFCodec());
    registerCodec(new WBMPCodec());
  }
  







  public static ImageCodec getCodec(String name)
  {
    return (ImageCodec)codecs.get(name.toLowerCase());
  }
  







  public static void registerCodec(ImageCodec codec)
  {
    codecs.put(codec.getFormatName().toLowerCase(), codec);
  }
  






  public static void unregisterCodec(String name)
  {
    codecs.remove(name.toLowerCase());
  }
  



  public static Enumeration getCodecs()
  {
    return codecs.elements();
  }
  












  public static ImageEncoder createImageEncoder(String name, OutputStream dst, ImageEncodeParam param)
  {
    ImageCodec codec = getCodec(name);
    if (codec == null) {
      return null;
    }
    return codec.createImageEncoder(dst, param);
  }
  












  public static ImageDecoder createImageDecoder(String name, InputStream src, ImageDecodeParam param)
  {
    ImageCodec codec = getCodec(name);
    if (codec == null) {
      return null;
    }
    return codec.createImageDecoder(src, param);
  }
  












  public static ImageDecoder createImageDecoder(String name, File src, ImageDecodeParam param)
    throws IOException
  {
    ImageCodec codec = getCodec(name);
    if (codec == null) {
      return null;
    }
    return codec.createImageDecoder(src, param);
  }
  












  public static ImageDecoder createImageDecoder(String name, SeekableStream src, ImageDecodeParam param)
  {
    ImageCodec codec = getCodec(name);
    if (codec == null) {
      return null;
    }
    return codec.createImageDecoder(src, param);
  }
  
  private static String[] vectorToStrings(Vector nameVec) {
    int count = nameVec.size();
    String[] names = new String[count];
    for (int i = 0; i < count; i++) {
      names[i] = ((String)nameVec.elementAt(i));
    }
    return names;
  }
  





















  public static String[] getDecoderNames(SeekableStream src)
  {
    if ((!src.canSeekBackwards()) && (!src.markSupported())) {
      throw new IllegalArgumentException(JaiI18N.getString("ImageCodec2"));
    }
    
    Enumeration enumeration = codecs.elements();
    Vector nameVec = new Vector();
    
    String opName = null;
    while (enumeration.hasMoreElements()) {
      ImageCodec codec = (ImageCodec)enumeration.nextElement();
      
      int bytesNeeded = codec.getNumHeaderBytes();
      if ((bytesNeeded != 0) || (src.canSeekBackwards()))
      {
        try
        {

          if (bytesNeeded > 0) {
            src.mark(bytesNeeded);
            byte[] header = new byte[bytesNeeded];
            src.readFully(header);
            src.reset();
            
            if (codec.isFormatRecognized(header)) {
              nameVec.add(codec.getFormatName());
            }
          } else {
            long pointer = src.getFilePointer();
            src.seek(0L);
            if (codec.isFormatRecognized(src)) {
              nameVec.add(codec.getFormatName());
            }
            src.seek(pointer);
          }
        } catch (IOException e) {
          ImagingListenerProxy.errorOccurred(JaiI18N.getString("ImageCodec3"), e, ImageCodec.class, false);
        }
      }
    }
    

    return vectorToStrings(nameVec);
  }
  











  public static String[] getEncoderNames(RenderedImage im, ImageEncodeParam param)
  {
    Enumeration enumeration = codecs.elements();
    Vector nameVec = new Vector();
    
    String opName = null;
    while (enumeration.hasMoreElements()) {
      ImageCodec codec = (ImageCodec)enumeration.nextElement();
      
      if (codec.canEncodeImage(im, param)) {
        nameVec.add(codec.getFormatName());
      }
    }
    
    return vectorToStrings(nameVec);
  }
  



















  public int getNumHeaderBytes()
  {
    return 0;
  }
  













  public boolean isFormatRecognized(byte[] header)
  {
    throw new RuntimeException(JaiI18N.getString("ImageCodec0"));
  }
  














  public boolean isFormatRecognized(SeekableStream src)
    throws IOException
  {
    throw new RuntimeException(JaiI18N.getString("ImageCodec1"));
  }
  







































































  protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param)
  {
    SeekableStream stream = SeekableStream.wrapInputStream(src, true);
    return createImageDecoder(stream, param);
  }
  















  protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param)
    throws IOException
  {
    return createImageDecoder(new FileSeekableStream(src), param);
  }
  
















  private static final byte[][] grayIndexCmaps = { null, { 0, -1 }, { 0, 85, -86, -1 }, null, { 0, 17, 34, 51, 68, 85, 102, 119, -120, -103, -86, -69, -52, -35, -18, -1 } };
  





















  public static ColorModel createGrayIndexColorModel(SampleModel sm, boolean blackIsZero)
  {
    if (sm.getNumBands() != 1) {
      throw new IllegalArgumentException();
    }
    int sampleSize = sm.getSampleSize(0);
    
    byte[] cmap = null;
    if (sampleSize < 8) {
      cmap = grayIndexCmaps[sampleSize];
      if (!blackIsZero) {
        int length = cmap.length;
        byte[] newCmap = new byte[length];
        for (int i = 0; i < length; i++) {
          newCmap[i] = cmap[(length - i - 1)];
        }
        cmap = newCmap;
      }
    } else {
      cmap = new byte['Ā'];
      if (blackIsZero) {
        for (int i = 0; i < 256; i++) {
          cmap[i] = ((byte)i);
        }
      } else {
        for (int i = 0; i < 256; i++) {
          cmap[i] = ((byte)(255 - i));
        }
      }
    }
    
    return new IndexColorModel(sampleSize, cmap.length, cmap, cmap, cmap);
  }
  

  private static final int[] GrayBits8 = { 8 };
  private static final ComponentColorModel colorModelGray8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits8, false, false, 1, 0);
  




  private static final int[] GrayAlphaBits8 = { 8, 8 };
  private static final ComponentColorModel colorModelGrayAlpha8 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits8, true, false, 3, 0);
  




  private static final int[] GrayBits16 = { 16 };
  private static final ComponentColorModel colorModelGray16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits16, false, false, 1, 1);
  




  private static final int[] GrayAlphaBits16 = { 16, 16 };
  private static final ComponentColorModel colorModelGrayAlpha16 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits16, true, false, 3, 1);
  




  private static final int[] GrayBits32 = { 32 };
  private static final ComponentColorModel colorModelGray32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayBits32, false, false, 1, 3);
  




  private static final int[] GrayAlphaBits32 = { 32, 32 };
  private static final ComponentColorModel colorModelGrayAlpha32 = new ComponentColorModel(ColorSpace.getInstance(1003), GrayAlphaBits32, true, false, 3, 3);
  




  private static final int[] RGBBits8 = { 8, 8, 8 };
  private static final ComponentColorModel colorModelRGB8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits8, false, false, 1, 0);
  




  private static final int[] RGBABits8 = { 8, 8, 8, 8 };
  private static final ComponentColorModel colorModelRGBA8 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits8, true, false, 3, 0);
  




  private static final int[] RGBBits16 = { 16, 16, 16 };
  private static final ComponentColorModel colorModelRGB16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits16, false, false, 1, 1);
  




  private static final int[] RGBABits16 = { 16, 16, 16, 16 };
  private static final ComponentColorModel colorModelRGBA16 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits16, true, false, 3, 1);
  




  private static final int[] RGBBits32 = { 32, 32, 32 };
  private static final ComponentColorModel colorModelRGB32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBBits32, false, false, 1, 3);
  




  private static final int[] RGBABits32 = { 32, 32, 32, 32 };
  private static final ComponentColorModel colorModelRGBA32 = new ComponentColorModel(ColorSpace.getInstance(1000), RGBABits32, true, false, 3, 3);
  













  public static ColorModel createComponentColorModel(SampleModel sm)
  {
    int type = sm.getDataType();
    int bands = sm.getNumBands();
    ComponentColorModel cm = null;
    
    if (type == 0) {
      switch (bands) {
      case 1: 
        cm = colorModelGray8;
        break;
      case 2: 
        cm = colorModelGrayAlpha8;
        break;
      case 3: 
        cm = colorModelRGB8;
        break;
      case 4: 
        cm = colorModelRGBA8;
      }
    }
    else if (type == 1) {
      switch (bands) {
      case 1: 
        cm = colorModelGray16;
        break;
      case 2: 
        cm = colorModelGrayAlpha16;
        break;
      case 3: 
        cm = colorModelRGB16;
        break;
      case 4: 
        cm = colorModelRGBA16;
      }
    }
    else if (type == 3) {
      switch (bands) {
      case 1: 
        cm = colorModelGray32;
        break;
      case 2: 
        cm = colorModelGrayAlpha32;
        break;
      case 3: 
        cm = colorModelRGB32;
        break;
      case 4: 
        cm = colorModelRGBA32;
      }
    }
    else if ((type == 4) && (bands >= 1) && (bands <= 4))
    {
      ColorSpace cs = bands <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
      

      boolean hasAlpha = bands % 2 == 0;
      cm = new FloatDoubleColorModel(cs, hasAlpha, false, hasAlpha ? 3 : 1, 4);
    }
    




    return cm;
  }
  











  public static ColorModel createComponentColorModel(SampleModel sm, ColorSpace cp)
  {
    if (cp == null)
      return createComponentColorModel(sm);
    int type = sm.getDataType();
    int bands = sm.getNumBands();
    ComponentColorModel cm = null;
    
    int[] bits = null;
    int transferType = -1;
    boolean hasAlpha = bands % 2 == 0;
    if ((cp instanceof SimpleCMYKColorSpace))
      hasAlpha = false;
    int transparency = hasAlpha ? 3 : 1;
    
    if (type == 0) {
      transferType = 0;
      switch (bands) {
      case 1: 
        bits = GrayBits8;
        break;
      case 2: 
        bits = GrayAlphaBits8;
        break;
      case 3: 
        bits = RGBBits8;
        break;
      case 4: 
        bits = RGBABits8;
      }
    }
    else if (type == 1) {
      transferType = 1;
      switch (bands) {
      case 1: 
        bits = GrayBits16;
        break;
      case 2: 
        bits = GrayAlphaBits16;
        break;
      case 3: 
        bits = RGBBits16;
        break;
      case 4: 
        bits = RGBABits16;
      }
    }
    else if (type == 3) {
      transferType = 3;
      switch (bands) {
      case 1: 
        bits = GrayBits32;
        break;
      case 2: 
        bits = GrayAlphaBits32;
        break;
      case 3: 
        bits = RGBBits32;
        break;
      case 4: 
        bits = RGBABits32;
      }
      
    }
    
    if ((type == 4) && (bands >= 1) && (bands <= 4))
    {
      cm = new FloatDoubleColorModel(cp, hasAlpha, false, transparency, 4);
    }
    else
    {
      cm = new ComponentColorModel(cp, bits, hasAlpha, false, transparency, transferType);
    }
    

    return cm;
  }
  








  public static boolean isIndicesForGrayscale(byte[] r, byte[] g, byte[] b)
  {
    if ((r.length != g.length) || (r.length != b.length)) {
      return false;
    }
    int size = r.length;
    
    if (size != 256) {
      return false;
    }
    for (int i = 0; i < size; i++) {
      byte temp = (byte)i;
      
      if ((r[i] != temp) || (g[i] != temp) || (b[i] != temp)) {
        return false;
      }
    }
    return true;
  }
  
  protected ImageCodec() {}
  
  public abstract String getFormatName();
  
  protected abstract Class getEncodeParamClass();
  
  protected abstract Class getDecodeParamClass();
  
  protected abstract ImageEncoder createImageEncoder(OutputStream paramOutputStream, ImageEncodeParam paramImageEncodeParam);
  
  public abstract boolean canEncodeImage(RenderedImage paramRenderedImage, ImageEncodeParam paramImageEncodeParam);
  
  protected abstract ImageDecoder createImageDecoder(SeekableStream paramSeekableStream, ImageDecodeParam paramImageDecodeParam);
}
