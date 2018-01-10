package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.util.DataBufferFloat;
import com.sun.media.jai.codecimpl.util.FloatDoubleColorModel;
import com.sun.media.jai.codecimpl.util.ImagingException;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import com.sun.media.jai.util.SimpleCMYKColorSpace;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
































public class TIFFImage
  extends SimpleRenderedImage
{
  public static final int COMP_NONE = 1;
  public static final int COMP_FAX_G3_1D = 2;
  public static final int COMP_FAX_G3_2D = 3;
  public static final int COMP_FAX_G4_2D = 4;
  public static final int COMP_LZW = 5;
  public static final int COMP_JPEG_OLD = 6;
  public static final int COMP_JPEG_TTN2 = 7;
  public static final int COMP_PACKBITS = 32773;
  public static final int COMP_DEFLATE = 32946;
  private static final int TYPE_UNSUPPORTED = -1;
  private static final int TYPE_BILEVEL = 0;
  private static final int TYPE_GRAY_4BIT = 1;
  private static final int TYPE_GRAY = 2;
  private static final int TYPE_GRAY_ALPHA = 3;
  private static final int TYPE_PALETTE = 4;
  private static final int TYPE_RGB = 5;
  private static final int TYPE_RGB_ALPHA = 6;
  private static final int TYPE_YCBCR_SUB = 7;
  private static final int TYPE_GENERIC = 8;
  private static final int TYPE_CMYK = 9;
  private static final int TIFF_JPEG_TABLES = 347;
  private static final int TIFF_YCBCR_SUBSAMPLING = 530;
  SeekableStream stream;
  private boolean isTiled;
  int tileSize;
  int tilesX;
  int tilesY;
  long[] tileOffsets;
  long[] tileByteCounts;
  char[] colormap;
  int sampleSize;
  int compression;
  byte[] palette;
  int numBands;
  int chromaSubH;
  int chromaSubV;
  long tiffT4Options;
  long tiffT6Options;
  int fillOrder;
  int predictor;
  JPEGDecodeParam decodeParam = null;
  boolean colorConvertJPEG = false;
  

  Inflater inflater = null;
  
  boolean isBigEndian;
  
  int imageType;
  
  boolean isWhiteZero = false;
  
  int dataType;
  
  boolean decodePaletteAsShorts;
  
  private TIFFFaxDecoder decoder = null;
  private TIFFLZWDecoder lzwDecoder = null;
  
















  private static final Raster decodeJPEG(byte[] data, JPEGDecodeParam decodeParam, boolean colorConvert, int minX, int minY)
  {
    ByteArrayInputStream jpegStream = new ByteArrayInputStream(data);
    

    JPEGImageDecoder decoder = decodeParam == null ? JPEGCodec.createJPEGDecoder(jpegStream) : JPEGCodec.createJPEGDecoder(jpegStream, decodeParam);
    




    Raster jpegRaster = null;
    try {
      jpegRaster = colorConvert ? decoder.decodeAsBufferedImage().getWritableTile(0, 0) : decoder.decodeAsRaster();
    }
    catch (IOException ioe)
    {
      String message = JaiI18N.getString("TIFFImage13");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), TIFFImage.class, false);
    }
    




    return jpegRaster.createTranslatedChild(minX, minY);
  }
  



  private final void inflate(byte[] deflated, byte[] inflated)
  {
    inflater.setInput(deflated);
    try {
      inflater.inflate(inflated);
    } catch (DataFormatException dfe) {
      String message = JaiI18N.getString("TIFFImage17");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, dfe), this, false);
    }
    



    inflater.reset();
  }
  









  private static final SampleModel createPixelInterleavedSampleModel(int dataType, int tileWidth, int tileHeight, int pixelStride, int scanlineStride, int[] bandOffsets)
  {
    SampleModel sampleModel = null;
    
    if (dataType == 4)
    {

      try
      {

        Class rfClass = Class.forName("javax.media.jai.RasterFactory");
        
        Class[] paramTypes = { Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, new int[0].getClass() };
        

        Method rfMthd = rfClass.getMethod("createPixelInterleavedSampleModel", paramTypes);
        

        Object[] params = { new Integer(dataType), new Integer(tileWidth), new Integer(tileHeight), new Integer(pixelStride), new Integer(scanlineStride), bandOffsets };
        





        sampleModel = (SampleModel)rfMthd.invoke(null, params);
      }
      catch (Exception e) {}
    }
    




    if ((dataType != 4) || (sampleModel == null)) {
      sampleModel = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, pixelStride, scanlineStride, bandOffsets);
    }
    






    return sampleModel;
  }
  


  private final long[] getFieldAsLongs(TIFFField field)
  {
    long[] value = null;
    
    if (field.getType() == 3) {
      char[] charValue = field.getAsChars();
      value = new long[charValue.length];
      for (int i = 0; i < charValue.length; i++) {
        value[i] = (charValue[i] & 0xFFFF);
      }
    } else if (field.getType() == 4) {
      value = field.getAsLongs();
    } else {
      throw new RuntimeException();
    }
    
    return value;
  }
  




  private TIFFField getField(TIFFDirectory dir, int tagID, String tagName)
  {
    TIFFField field = dir.getField(tagID);
    if (field == null) {
      MessageFormat mf = new MessageFormat(JaiI18N.getString("TIFFImage5"));
      
      mf.setLocale(Locale.getDefault());
      throw new RuntimeException(mf.format(new Object[] { tagName }));
    }
    return field;
  }
  












  public TIFFImage(SeekableStream stream, TIFFDecodeParam param, int directory)
    throws IOException
  {
    this.stream = stream;
    if (param == null) {
      param = new TIFFDecodeParam();
    }
    
    decodePaletteAsShorts = param.getDecodePaletteAsShorts();
    

    TIFFDirectory dir = param.getIFDOffset() == null ? new TIFFDirectory(stream, directory) : new TIFFDirectory(stream, param.getIFDOffset().longValue(), directory);
    




    properties.put("tiff_directory", dir);
    

    TIFFField sfield = dir.getField(277);
    
    int samplesPerPixel = sfield == null ? 1 : (int)sfield.getAsLong(0);
    

    TIFFField planarConfigurationField = dir.getField(284);
    
    char[] planarConfiguration = planarConfigurationField == null ? new char[] { '\001' } : planarConfigurationField.getAsChars();
    



    if ((planarConfiguration[0] != '\001') && (samplesPerPixel != 1)) {
      throw new RuntimeException(JaiI18N.getString("TIFFImage0"));
    }
    

    TIFFField bitsField = dir.getField(258);
    
    char[] bitsPerSample = null;
    if (bitsField != null) {
      bitsPerSample = bitsField.getAsChars();
    } else {
      bitsPerSample = new char[] { '\001' };
      

      for (int i = 1; i < bitsPerSample.length; i++) {
        if (bitsPerSample[i] != bitsPerSample[0]) {
          throw new RuntimeException(JaiI18N.getString("TIFFImage1"));
        }
      }
    }
    
    sampleSize = bitsPerSample[0];
    


    TIFFField sampleFormatField = dir.getField(339);
    

    char[] sampleFormat = null;
    if (sampleFormatField != null) {
      sampleFormat = sampleFormatField.getAsChars();
      

      for (int l = 1; l < sampleFormat.length; l++) {
        if (sampleFormat[l] != sampleFormat[0]) {
          throw new RuntimeException(JaiI18N.getString("TIFFImage2"));
        }
      }
    }
    else
    {
      sampleFormat = new char[] { '\001' };
    }
    

    boolean isValidDataFormat = false;
    switch (sampleSize) {
    case 1: 
    case 4: 
    case 8: 
      if (sampleFormat[0] != '\003')
      {
        dataType = 0;
        isValidDataFormat = true;
      }
      break;
    case 16: 
      if (sampleFormat[0] != '\003') {
        dataType = (sampleFormat[0] == '\002' ? 2 : 1);
        
        isValidDataFormat = true;
      }
      break;
    case 32: 
      dataType = (sampleFormat[0] == '\003' ? 4 : 3);
      
      isValidDataFormat = true;
    }
    
    
    if (!isValidDataFormat) {
      throw new RuntimeException(JaiI18N.getString("TIFFImage3"));
    }
    

    TIFFField compField = dir.getField(259);
    compression = (compField == null ? 1 : compField.getAsInt(0));
    

    TIFFField photoInterpField = dir.getField(262);
    
    int photometricType;
    
    int photometricType;
    if (photoInterpField != null)
    {
      photometricType = (int)photoInterpField.getAsLong(0);
    }
    else {
      int photometricType;
      if (dir.getField(320) != null)
      {
        photometricType = 3; } else { int photometricType;
        if (sampleSize == 1)
        {
          int photometricType;
          if ((compression == 2) || (compression == 3) || (compression == 4))
          {

            photometricType = 0;
          } else
            photometricType = 1;
        } else { int photometricType;
          if ((samplesPerPixel == 3) || (samplesPerPixel == 4))
          {
            photometricType = 2;
          }
          else {
            photometricType = 1;
          }
        }
      }
    }
    imageType = -1;
    switch (photometricType) {
    case 0: 
      isWhiteZero = true;
    case 1: 
      if ((sampleSize == 1) && (samplesPerPixel == 1)) {
        imageType = 0;
      } else if ((sampleSize == 4) && (samplesPerPixel == 1)) {
        imageType = 1;
      } else if (sampleSize % 8 == 0) {
        if (samplesPerPixel == 1) {
          imageType = 2;
        } else if (samplesPerPixel == 2) {
          imageType = 3;
        } else {
          imageType = 8;
        }
      }
      break;
    case 2: 
      if (sampleSize % 8 != 0) break label1085;
      if (samplesPerPixel == 3) {
        imageType = 5;
      } else if (samplesPerPixel == 4) {
        imageType = 6;
      } else {
        imageType = 8;
      }
      
      break;
    case 3: 
      if ((samplesPerPixel != 1) || ((sampleSize != 4) && (sampleSize != 8) && (sampleSize != 16)))
        break label1085;
      imageType = 4; break;
    

    case 4: 
      if ((sampleSize != 1) || (samplesPerPixel != 1)) break label1085;
      imageType = 0; break;
    

    case 5: 
      if ((sampleSize == 8) && (samplesPerPixel == 4)) {
        imageType = 9;
      }
    case 6: 
      if ((compression == 7) && (sampleSize == 8) && (samplesPerPixel == 3))
      {

        colorConvertJPEG = param.getJPEGDecompressYCbCrToRGB();
        

        imageType = (colorConvertJPEG ? 5 : 8);
      } else {
        TIFFField chromaField = dir.getField(530);
        if (chromaField != null) {
          chromaSubH = chromaField.getAsInt(0);
          chromaSubV = chromaField.getAsInt(1);
        } else {
          chromaSubH = (this.chromaSubV = 2);
        }
        
        if (chromaSubH * chromaSubV == 1) {
          imageType = 8;
        } else if ((sampleSize == 8) && (samplesPerPixel == 3)) {
          imageType = 7;
        }
      }
      break;
    }
    if (sampleSize % 8 == 0) {
      imageType = 8;
    }
    
    label1085:
    
    if (imageType == -1) {
      throw new RuntimeException(JaiI18N.getString("TIFFImage4"));
    }
    

    minX = (this.minY = 0);
    width = ((int)getField(dir, 256, "Image Width").getAsLong(0));
    


    height = ((int)getField(dir, 257, "Image Length").getAsLong(0));
    



    numBands = samplesPerPixel;
    

    TIFFField efield = dir.getField(338);
    int extraSamples = efield == null ? 0 : (int)efield.getAsLong(0);
    
    if (dir.getField(324) != null)
    {
      isTiled = true;
      
      tileWidth = ((int)getField(dir, 322, "Tile Width").getAsLong(0));
      

      tileHeight = ((int)getField(dir, 323, "Tile Length").getAsLong(0));
      

      tileOffsets = getField(dir, 324, "Tile Offsets").getAsLongs();
      



      tileByteCounts = getFieldAsLongs(getField(dir, 325, "Tile Byte Counts"));


    }
    else
    {


      isTiled = false;
      




      tileWidth = (dir.getField(322) != null ? (int)dir.getFieldAsLong(322) : width);
      


      TIFFField field = dir.getField(278);
      
      if (field == null)
      {

        tileHeight = (dir.getField(323) != null ? (int)dir.getFieldAsLong(323) : height);

      }
      else
      {
        long l = field.getAsLong(0);
        long infinity = 1L;
        infinity = (infinity << 32) - 1L;
        if ((l == infinity) || (l > height))
        {


          tileHeight = height;
        } else {
          tileHeight = ((int)l);
        }
      }
      
      TIFFField tileOffsetsField = getField(dir, 273, "Strip Offsets");
      


      tileOffsets = getFieldAsLongs(tileOffsetsField);
      
      TIFFField tileByteCountsField = dir.getField(279);
      
      if (tileByteCountsField == null)
      {
        int totalBytes = (sampleSize + 7) / 8 * numBands * width * height;
        int bytesPerStrip = (sampleSize + 7) / 8 * numBands * width * tileHeight;
        
        int cumulativeBytes = 0;
        tileByteCounts = new long[tileOffsets.length];
        for (int i = 0; i < tileOffsets.length; i++) {
          tileByteCounts[i] = Math.min(totalBytes - cumulativeBytes, bytesPerStrip);
          

          cumulativeBytes += bytesPerStrip;
        }
        
        if (compression != 1)
        {

          this.stream = new NoEOFStream(stream);
        }
      } else {
        tileByteCounts = getFieldAsLongs(tileByteCountsField);
      }
      

      int maxBytes = width * height * numBands * ((sampleSize + 7) / 8);
      if ((tileByteCounts.length == 1) && (compression == 1) && (tileByteCounts[0] > maxBytes))
      {

        tileByteCounts[0] = maxBytes;
      }
    }
    

    tilesX = ((width + tileWidth - 1) / tileWidth);
    tilesY = ((height + tileHeight - 1) / tileHeight);
    tileSize = (tileWidth * tileHeight * numBands);
    

    isBigEndian = dir.isBigEndian();
    
    TIFFField fillOrderField = dir.getField(266);
    
    if (fillOrderField != null) {
      fillOrder = fillOrderField.getAsInt(0);
    }
    else {
      fillOrder = 1;
    }
    
    switch (compression)
    {
    case 1: 
    case 32773: 
      break;
    case 32946: 
      inflater = new Inflater();
      break;
    case 2: 
    case 3: 
    case 4: 
      if (sampleSize != 1) {
        throw new RuntimeException(JaiI18N.getString("TIFFImage7"));
      }
      

      if (compression == 3) {
        TIFFField t4OptionsField = dir.getField(292);
        
        if (t4OptionsField != null) {
          tiffT4Options = t4OptionsField.getAsLong(0);
        }
        else {
          tiffT4Options = 0L;
        }
      }
      

      if (compression == 4) {
        TIFFField t6OptionsField = dir.getField(293);
        
        if (t6OptionsField != null) {
          tiffT6Options = t6OptionsField.getAsLong(0);
        }
        else {
          tiffT6Options = 0L;
        }
      }
      

      this.decoder = new TIFFFaxDecoder(fillOrder, tileWidth, tileHeight);
      
      break;
    

    case 5: 
      TIFFField predictorField = dir.getField(317);
      

      if (predictorField == null) {
        predictor = 1;
      } else {
        predictor = predictorField.getAsInt(0);
        
        if ((predictor != 1) && (predictor != 2)) {
          throw new RuntimeException(JaiI18N.getString("TIFFImage8"));
        }
        
        if ((predictor == 2) && (sampleSize != 8)) {
          throw new RuntimeException(sampleSize + JaiI18N.getString("TIFFImage9"));
        }
      }
      

      lzwDecoder = new TIFFLZWDecoder(tileWidth, predictor, samplesPerPixel);
      
      break;
    
    case 6: 
      throw new RuntimeException(JaiI18N.getString("TIFFImage15"));
    
    case 7: 
      if ((sampleSize != 8) || (((imageType != 2) || (samplesPerPixel != 1)) && ((imageType != 4) || (samplesPerPixel != 1)) && ((imageType != 5) || (samplesPerPixel != 3))))
      {


        throw new RuntimeException(JaiI18N.getString("TIFFImage16"));
      }
      

      if (dir.isTagPresent(347)) {
        TIFFField jpegTableField = dir.getField(347);
        byte[] jpegTable = jpegTableField.getAsBytes();
        ByteArrayInputStream tableStream = new ByteArrayInputStream(jpegTable);
        
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(tableStream);
        
        decoder.decodeAsRaster();
        decodeParam = decoder.getJPEGDecodeParam();
      }
      
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("TIFFImage10"));
    }
    
    switch (imageType) {
    case 0: 
    case 1: 
      sampleModel = new MultiPixelPackedSampleModel(dataType, tileWidth, tileHeight, sampleSize);
      



      if (imageType == 0) {
        byte[] map = { (byte)(isWhiteZero ? 'ÿ' : 0), (byte)(isWhiteZero ? 0 : 'ÿ') };
        
        colorModel = new IndexColorModel(1, 2, map, map, map);
      } else {
        colorModel = ImageCodec.createGrayIndexColorModel(sampleModel, !isWhiteZero);
      }
      

      break;
    


    case 2: 
    case 3: 
    case 5: 
    case 6: 
    case 9: 
      int[] RGBOffsets = new int[numBands];
      if (compression == 7) {
        for (int i = 0; i < numBands; i++) {
          RGBOffsets[i] = (numBands - 1 - i);
        }
      } else {
        for (int i = 0; i < numBands; i++) {
          RGBOffsets[i] = i;
        }
      }
      sampleModel = createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands, numBands * tileWidth, RGBOffsets);
      





      if ((imageType == 2) || (imageType == 5)) {
        colorModel = ImageCodec.createComponentColorModel(sampleModel);
      }
      else if (imageType == 9) {
        colorModel = ImageCodec.createComponentColorModel(sampleModel, SimpleCMYKColorSpace.getInstance());


      }
      else
      {

        int transparency = 1;
        if ((extraSamples == 1) || (extraSamples == 2))
        {


          transparency = 3;
        }
        
        colorModel = createAlphaComponentColorModel(dataType, numBands, extraSamples == 1, transparency);
      }
      



      break;
    




    case 7: 
    case 8: 
      int[] bandOffsets = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        bandOffsets[i] = i;
      }
      
      sampleModel = createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands, numBands * tileWidth, bandOffsets);
      



      colorModel = null;
      break;
    

    case 4: 
      TIFFField cfield = getField(dir, 320, "Colormap");
      
      colormap = cfield.getAsChars();
      


      if (decodePaletteAsShorts) {
        numBands = 3;
        





        if (dataType == 0) {
          dataType = 1;
        }
        



        sampleModel = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands);
        



        colorModel = ImageCodec.createComponentColorModel(sampleModel);
      }
      else
      {
        numBands = 1;
        
        if (sampleSize == 4)
        {

          sampleModel = new MultiPixelPackedSampleModel(0, tileWidth, tileHeight, sampleSize);



        }
        else if (sampleSize == 8) {
          sampleModel = RasterFactory.createPixelInterleavedSampleModel(0, tileWidth, tileHeight, numBands);




        }
        else if (sampleSize == 16)
        {



          dataType = 1;
          sampleModel = RasterFactory.createPixelInterleavedSampleModel(1, tileWidth, tileHeight, numBands);
        }
        





        int bandLength = colormap.length / 3;
        byte[] r = new byte[bandLength];
        byte[] g = new byte[bandLength];
        byte[] b = new byte[bandLength];
        
        int gIndex = bandLength;
        int bIndex = bandLength * 2;
        
        if (dataType == 2)
        {
          for (int i = 0; i < bandLength; i++) {
            r[i] = param.decodeSigned16BitsTo8Bits((short)colormap[i]);
            
            g[i] = param.decodeSigned16BitsTo8Bits((short)colormap[(gIndex + i)]);
            
            b[i] = param.decodeSigned16BitsTo8Bits((short)colormap[(bIndex + i)]);
          }
          
        }
        else
        {
          for (int i = 0; i < bandLength; i++) {
            r[i] = param.decode16BitsTo8Bits(colormap[i] & 0xFFFF);
            g[i] = param.decode16BitsTo8Bits(colormap[(gIndex + i)] & 0xFFFF);
            
            b[i] = param.decode16BitsTo8Bits(colormap[(bIndex + i)] & 0xFFFF);
          }
        }
        


        colorModel = new IndexColorModel(sampleSize, bandLength, r, g, b);
      }
      
      break;
    
    default: 
      throw new RuntimeException("TIFFImage4");
    }
    
  }
  


  public TIFFDirectory getPrivateIFD(long offset)
    throws IOException
  {
    return new TIFFDirectory(stream, offset, 0);
  }
  



  public synchronized Raster getTile(int tileX, int tileY)
  {
    if ((tileX < 0) || (tileX >= tilesX) || (tileY < 0) || (tileY >= tilesY))
    {
      throw new IllegalArgumentException(JaiI18N.getString("TIFFImage12"));
    }
    

    WritableRaster tile = null;
    



    synchronized (stream)
    {

      byte[] bdata = null;
      short[] sdata = null;
      int[] idata = null;
      float[] fdata = null;
      DataBuffer buffer = sampleModel.createDataBuffer();
      
      int dataType = sampleModel.getDataType();
      if (dataType == 0) {
        bdata = ((DataBufferByte)buffer).getData();
      } else if (dataType == 1) {
        sdata = ((DataBufferUShort)buffer).getData();
      } else if (dataType == 2) {
        sdata = ((DataBufferShort)buffer).getData();
      } else if (dataType == 3) {
        idata = ((DataBufferInt)buffer).getData();
      } else if (dataType == 4) {
        if ((buffer instanceof DataBufferFloat)) {
          fdata = ((DataBufferFloat)buffer).getData();
        }
        else
        {
          try
          {

            Method getDataMethod = buffer.getClass().getMethod("getData", null);
            
            fdata = (float[])getDataMethod.invoke(buffer, null);
          } catch (Exception e) {
            String message = JaiI18N.getString("TIFFImage18");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
          }
        }
      }
      



      tile = RasterFactory.createWritableRaster(sampleModel, buffer, new Point(tileXToX(tileX), tileYToY(tileY)));
      





      long save_offset = 0L;
      try {
        save_offset = stream.getFilePointer();
        stream.seek(tileOffsets[(tileY * tilesX + tileX)]);
      } catch (IOException ioe) {
        String message = JaiI18N.getString("TIFFImage13");
        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
      }
      




      int byteCount = (int)tileByteCounts[(tileY * tilesX + tileX)];
      




      Rectangle tileRect = new Rectangle(tileXToX(tileX), tileYToY(tileY), tileWidth, tileHeight);
      
      Rectangle newRect = isTiled ? tileRect : tileRect.intersection(getBounds());
      
      int unitsInThisTile = width * height * numBands;
      

      byte[] data = (compression != 1) || (imageType == 4) ? new byte[byteCount] : null;
      



      if (imageType == 0) {
        try {
          if (compression == 32773) {
            stream.readFully(data, 0, byteCount);
            
            int bytesInThisTile;
            
            int bytesInThisTile;
            if (width % 8 == 0) {
              bytesInThisTile = width / 8 * height;
            } else {
              bytesInThisTile = (width / 8 + 1) * height;
            }
            
            decodePackbits(data, bytesInThisTile, bdata);
          } else if (compression == 5) {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          } else if (compression == 2) {
            stream.readFully(data, 0, byteCount);
            decoder.decode1D(bdata, data, 0, height);
          } else if (compression == 3) {
            stream.readFully(data, 0, byteCount);
            decoder.decode2D(bdata, data, 0, height, tiffT4Options);
          }
          else if (compression == 4) {
            stream.readFully(data, 0, byteCount);
            decoder.decodeT6(bdata, data, 0, height, tiffT6Options);
          }
          else if (compression == 32946) {
            stream.readFully(data, 0, byteCount);
            inflate(data, bdata);
          } else if (compression == 1) {
            stream.readFully(bdata, 0, byteCount);
          }
          
          stream.seek(save_offset);
        } catch (IOException ioe) {
          String message = JaiI18N.getString("TIFFImage13");
          ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        

      }
      else if (imageType == 4) {
        if (sampleSize == 16)
        {
          if (decodePaletteAsShorts)
          {
            short[] tempData = null;
            




            int unitsBeforeLookup = unitsInThisTile / 3;
            




            int entries = unitsBeforeLookup * 2;
            

            try
            {
              if (compression == 32773)
              {
                stream.readFully(data, 0, byteCount);
                
                byte[] byteArray = new byte[entries];
                decodePackbits(data, entries, byteArray);
                tempData = new short[unitsBeforeLookup];
                interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);

              }
              else if (compression == 5)
              {

                stream.readFully(data, 0, byteCount);
                
                byte[] byteArray = new byte[entries];
                lzwDecoder.decode(data, byteArray, height);
                tempData = new short[unitsBeforeLookup];
                interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);

              }
              else if (compression == 32946)
              {
                stream.readFully(data, 0, byteCount);
                byte[] byteArray = new byte[entries];
                inflate(data, byteArray);
                tempData = new short[unitsBeforeLookup];
                interpretBytesAsShorts(byteArray, tempData, unitsBeforeLookup);

              }
              else if (compression == 1)
              {




                tempData = new short[byteCount / 2];
                readShorts(byteCount / 2, tempData);
              }
              
              stream.seek(save_offset);
            }
            catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
            




            if (dataType == 1)
            {



              int count = 0;int len = colormap.length / 3;
              int len2 = len * 2;
              for (int i = 0; i < unitsBeforeLookup; i++)
              {
                int lookup = tempData[i] & 0xFFFF;
                
                int cmapValue = colormap[(lookup + len2)];
                sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
                
                cmapValue = colormap[(lookup + len)];
                sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
                
                cmapValue = colormap[lookup];
                sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              }
            }
            else if (dataType == 2)
            {



              int count = 0;int len = colormap.length / 3;
              int len2 = len * 2;
              for (int i = 0; i < unitsBeforeLookup; i++)
              {
                int lookup = tempData[i] & 0xFFFF;
                
                int cmapValue = colormap[(lookup + len2)];
                sdata[(count++)] = ((short)cmapValue);
                
                cmapValue = colormap[(lookup + len)];
                sdata[(count++)] = ((short)cmapValue);
                
                cmapValue = colormap[lookup];
                sdata[(count++)] = ((short)cmapValue);
              }
              
            }
            

          }
          else
          {
            try
            {
              if (compression == 32773)
              {
                stream.readFully(data, 0, byteCount);
                





                int bytesInThisTile = unitsInThisTile * 2;
                
                byte[] byteArray = new byte[bytesInThisTile];
                decodePackbits(data, bytesInThisTile, byteArray);
                interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);

              }
              else if (compression == 5)
              {
                stream.readFully(data, 0, byteCount);
                





                byte[] byteArray = new byte[unitsInThisTile * 2];
                lzwDecoder.decode(data, byteArray, height);
                interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);

              }
              else if (compression == 32946)
              {
                stream.readFully(data, 0, byteCount);
                byte[] byteArray = new byte[unitsInThisTile * 2];
                inflate(data, byteArray);
                interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);

              }
              else if (compression == 1)
              {
                readShorts(byteCount / 2, sdata);
              }
              
              stream.seek(save_offset);
            }
            catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);

            }
            
          }
          

        }
        else if (sampleSize == 8)
        {
          if (decodePaletteAsShorts)
          {
            byte[] tempData = null;
            




            int unitsBeforeLookup = unitsInThisTile / 3;
            

            try
            {
              if (compression == 32773)
              {
                stream.readFully(data, 0, byteCount);
                tempData = new byte[unitsBeforeLookup];
                decodePackbits(data, unitsBeforeLookup, tempData);
              }
              else if (compression == 5)
              {
                stream.readFully(data, 0, byteCount);
                tempData = new byte[unitsBeforeLookup];
                lzwDecoder.decode(data, tempData, height);
              }
              else if (compression == 7)
              {
                stream.readFully(data, 0, byteCount);
                Raster tempTile = decodeJPEG(data, decodeParam, colorConvertJPEG, tile.getMinX(), tile.getMinY());
                



                int[] tempPixels = new int[unitsBeforeLookup];
                tempTile.getPixels(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight(), tempPixels);
                



                tempData = new byte[unitsBeforeLookup];
                for (int i = 0; i < unitsBeforeLookup; i++) {
                  tempData[i] = ((byte)tempPixels[i]);
                }
              }
              else if (compression == 32946)
              {
                stream.readFully(data, 0, byteCount);
                tempData = new byte[unitsBeforeLookup];
                inflate(data, tempData);
              }
              else if (compression == 1)
              {
                tempData = new byte[byteCount];
                stream.readFully(tempData, 0, byteCount);
              }
              
              stream.seek(save_offset);
            }
            catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
            







            int count = 0;int len = colormap.length / 3;
            int len2 = len * 2;
            for (int i = 0; i < unitsBeforeLookup; i++)
            {
              int lookup = tempData[i] & 0xFF;
              
              int cmapValue = colormap[(lookup + len2)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              
              cmapValue = colormap[(lookup + len)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              
              cmapValue = colormap[lookup];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            }
            

          }
          else
          {
            try
            {
              if (compression == 32773)
              {
                stream.readFully(data, 0, byteCount);
                decodePackbits(data, unitsInThisTile, bdata);
              }
              else if (compression == 5)
              {
                stream.readFully(data, 0, byteCount);
                lzwDecoder.decode(data, bdata, height);
              }
              else if (compression == 7)
              {
                stream.readFully(data, 0, byteCount);
                tile.setRect(decodeJPEG(data, decodeParam, colorConvertJPEG, tile.getMinX(), tile.getMinY()));




              }
              else if (compression == 32946)
              {
                stream.readFully(data, 0, byteCount);
                inflate(data, bdata);
              }
              else if (compression == 1)
              {
                stream.readFully(bdata, 0, byteCount);
              }
              
              stream.seek(save_offset);
            }
            catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);

            }
            
          }
          

        }
        else if (sampleSize == 4)
        {
          int padding = width % 2 == 0 ? 0 : 1;
          int bytesPostDecoding = (width / 2 + padding) * height;
          


          if (decodePaletteAsShorts)
          {
            byte[] tempData = null;
            try
            {
              stream.readFully(data, 0, byteCount);
              stream.seek(save_offset);
            } catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
            





            if (compression == 32773)
            {
              tempData = new byte[bytesPostDecoding];
              decodePackbits(data, bytesPostDecoding, tempData);
            }
            else if (compression == 5)
            {
              tempData = new byte[bytesPostDecoding];
              lzwDecoder.decode(data, tempData, height);
            }
            else if (compression == 32946)
            {
              tempData = new byte[bytesPostDecoding];
              inflate(data, tempData);
            }
            else if (compression == 1)
            {
              tempData = data;
            }
            
            int bytes = unitsInThisTile / 3;
            

            data = new byte[bytes];
            
            int srcCount = 0;int dstCount = 0;
            for (int j = 0; j < height; j++) {
              for (int i = 0; i < width / 2; i++) {
                data[(dstCount++)] = ((byte)((tempData[srcCount] & 0xF0) >> 4));
                
                data[(dstCount++)] = ((byte)(tempData[(srcCount++)] & 0xF));
              }
              

              if (padding == 1) {
                data[(dstCount++)] = ((byte)((tempData[(srcCount++)] & 0xF0) >> 4));
              }
            }
            

            int len = colormap.length / 3;
            int len2 = len * 2;
            
            int count = 0;
            for (int i = 0; i < bytes; i++) {
              int lookup = data[i] & 0xFF;
              int cmapValue = colormap[(lookup + len2)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              cmapValue = colormap[(lookup + len)];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
              cmapValue = colormap[lookup];
              sdata[(count++)] = ((short)(cmapValue & 0xFFFF));
            }
            
          }
          else
          {
            try
            {
              if (compression == 32773)
              {
                stream.readFully(data, 0, byteCount);
                decodePackbits(data, bytesPostDecoding, bdata);
              }
              else if (compression == 5)
              {
                stream.readFully(data, 0, byteCount);
                lzwDecoder.decode(data, bdata, height);
              }
              else if (compression == 32946)
              {
                stream.readFully(data, 0, byteCount);
                inflate(data, bdata);
              }
              else if (compression == 1)
              {
                stream.readFully(bdata, 0, byteCount);
              }
              
              stream.seek(save_offset);
            }
            catch (IOException ioe) {
              String message = JaiI18N.getString("TIFFImage13");
              ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
            }
            
          }
          
        }
        
      }
      else if (imageType == 1) {
        try {
          if (compression == 32773)
          {
            stream.readFully(data, 0, byteCount);
            
            int bytesInThisTile;
            
            int bytesInThisTile;
            if (width % 8 == 0) {
              bytesInThisTile = width / 2 * height;
            } else {
              bytesInThisTile = (width / 2 + 1) * height;
            }
            

            decodePackbits(data, bytesInThisTile, bdata);
          }
          else if (compression == 5)
          {
            stream.readFully(data, 0, byteCount);
            lzwDecoder.decode(data, bdata, height);
          }
          else if (compression == 32946)
          {
            stream.readFully(data, 0, byteCount);
            inflate(data, bdata);
          }
          else
          {
            stream.readFully(bdata, 0, byteCount);
          }
          
          stream.seek(save_offset);
        } catch (IOException ioe) {
          String message = JaiI18N.getString("TIFFImage13");
          ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        
      }
      else
      {
        try
        {
          if (sampleSize == 8)
          {
            if (compression == 1)
            {
              stream.readFully(bdata, 0, byteCount);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              lzwDecoder.decode(data, bdata, height);
            }
            else if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              decodePackbits(data, unitsInThisTile, bdata);
            }
            else if (compression == 7)
            {
              stream.readFully(data, 0, byteCount);
              tile.setRect(decodeJPEG(data, decodeParam, colorConvertJPEG, tile.getMinX(), tile.getMinY()));



            }
            else if (compression == 32946)
            {
              stream.readFully(data, 0, byteCount);
              inflate(data, bdata);
            }
          }
          else if (sampleSize == 16)
          {
            if (compression == 1)
            {
              readShorts(byteCount / 2, sdata);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              





              byte[] byteArray = new byte[unitsInThisTile * 2];
              lzwDecoder.decode(data, byteArray, height);
              interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);

            }
            else if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              





              int bytesInThisTile = unitsInThisTile * 2;
              
              byte[] byteArray = new byte[bytesInThisTile];
              decodePackbits(data, bytesInThisTile, byteArray);
              interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
            }
            else if (compression == 32946)
            {
              stream.readFully(data, 0, byteCount);
              byte[] byteArray = new byte[unitsInThisTile * 2];
              inflate(data, byteArray);
              interpretBytesAsShorts(byteArray, sdata, unitsInThisTile);
            }
            
          }
          else if ((sampleSize == 32) && (dataType == 3))
          {
            if (compression == 1)
            {
              readInts(byteCount / 4, idata);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              





              byte[] byteArray = new byte[unitsInThisTile * 4];
              lzwDecoder.decode(data, byteArray, height);
              interpretBytesAsInts(byteArray, idata, unitsInThisTile);

            }
            else if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              





              int bytesInThisTile = unitsInThisTile * 4;
              
              byte[] byteArray = new byte[bytesInThisTile];
              decodePackbits(data, bytesInThisTile, byteArray);
              interpretBytesAsInts(byteArray, idata, unitsInThisTile);
            }
            else if (compression == 32946)
            {
              stream.readFully(data, 0, byteCount);
              byte[] byteArray = new byte[unitsInThisTile * 4];
              inflate(data, byteArray);
              interpretBytesAsInts(byteArray, idata, unitsInThisTile);
            }
            
          }
          else if ((sampleSize == 32) && (dataType == 4))
          {
            if (compression == 1)
            {
              readFloats(byteCount / 4, fdata);
            }
            else if (compression == 5)
            {
              stream.readFully(data, 0, byteCount);
              





              byte[] byteArray = new byte[unitsInThisTile * 4];
              lzwDecoder.decode(data, byteArray, height);
              interpretBytesAsFloats(byteArray, fdata, unitsInThisTile);

            }
            else if (compression == 32773)
            {
              stream.readFully(data, 0, byteCount);
              





              int bytesInThisTile = unitsInThisTile * 4;
              
              byte[] byteArray = new byte[bytesInThisTile];
              decodePackbits(data, bytesInThisTile, byteArray);
              interpretBytesAsFloats(byteArray, fdata, unitsInThisTile);
            }
            else if (compression == 32946)
            {
              stream.readFully(data, 0, byteCount);
              byte[] byteArray = new byte[unitsInThisTile * 4];
              inflate(data, byteArray);
              interpretBytesAsFloats(byteArray, fdata, unitsInThisTile);
            }
          }
          


          stream.seek(save_offset);
        }
        catch (IOException ioe) {
          String message = JaiI18N.getString("TIFFImage13");
          ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
        }
        




        switch (imageType) {
        case 2: 
        case 3: 
          if (isWhiteZero)
          {


            if ((dataType == 0) && (!(colorModel instanceof IndexColorModel)))
            {

              for (int l = 0; l < bdata.length; l += numBands) {
                bdata[l] = ((byte)(255 - bdata[l]));
              }
            } else if (dataType == 1)
            {
              int ushortMax = 65535;
              for (int l = 0; l < sdata.length; l += numBands) {
                sdata[l] = ((short)(ushortMax - sdata[l]));
              }
            }
            else if (dataType == 2)
            {
              for (int l = 0; l < sdata.length; l += numBands) {
                sdata[l] = ((short)(sdata[l] ^ 0xFFFFFFFF));
              }
            } else if (dataType == 3)
            {
              long uintMax = -1L;
              for (int l = 0; l < idata.length; l += numBands) {
                idata[l] = ((int)(uintMax - idata[l]));
              }
            }
          }
          

          break;
        case 7: 
          int pixelsPerDataUnit = chromaSubH * chromaSubV;
          
          int numH = width / chromaSubH;
          int numV = height / chromaSubV;
          
          byte[] tempData = new byte[numH * numV * (pixelsPerDataUnit + 2)];
          System.arraycopy(bdata, 0, tempData, 0, tempData.length);
          
          int samplesPerDataUnit = pixelsPerDataUnit * 3;
          int[] pixels = new int[samplesPerDataUnit];
          
          int bOffset = 0;
          int offsetCb = pixelsPerDataUnit;
          int offsetCr = offsetCb + 1;
          
          int y = y;
          for (int j = 0; j < numV; j++) {
            int x = x;
            for (int i = 0; i < numH; i++) {
              int Cb = tempData[(bOffset + offsetCb)];
              int Cr = tempData[(bOffset + offsetCr)];
              int k = 0;
              while (k < samplesPerDataUnit) {
                pixels[(k++)] = tempData[(bOffset++)];
                pixels[(k++)] = Cb;
                pixels[(k++)] = Cr;
              }
              bOffset += 2;
              tile.setPixels(x, y, chromaSubH, chromaSubV, pixels);
              x += chromaSubH;
            }
            y += chromaSubV;
          }
        }
        
      }
    }
    


    return tile;
  }
  


  private void readShorts(int shortCount, short[] shortArray)
  {
    int byteCount = 2 * shortCount;
    byte[] byteArray = new byte[byteCount];
    try
    {
      stream.readFully(byteArray, 0, byteCount);
    } catch (IOException ioe) {
      String message = JaiI18N.getString("TIFFImage13");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
    }
    



    interpretBytesAsShorts(byteArray, shortArray, shortCount);
  }
  


  private void readInts(int intCount, int[] intArray)
  {
    int byteCount = 4 * intCount;
    byte[] byteArray = new byte[byteCount];
    try
    {
      stream.readFully(byteArray, 0, byteCount);
    } catch (IOException ioe) {
      String message = JaiI18N.getString("TIFFImage13");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
    }
    



    interpretBytesAsInts(byteArray, intArray, intCount);
  }
  


  private void readFloats(int floatCount, float[] floatArray)
  {
    int byteCount = 4 * floatCount;
    byte[] byteArray = new byte[byteCount];
    try
    {
      stream.readFully(byteArray, 0, byteCount);
    } catch (IOException ioe) {
      String message = JaiI18N.getString("TIFFImage13");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ioe), this, false);
    }
    



    interpretBytesAsFloats(byteArray, floatArray, floatCount);
  }
  




  private void interpretBytesAsShorts(byte[] byteArray, short[] shortArray, int shortCount)
  {
    int j = 0;
    

    if (isBigEndian)
    {
      for (int i = 0; i < shortCount; i++) {
        int firstByte = byteArray[(j++)] & 0xFF;
        int secondByte = byteArray[(j++)] & 0xFF;
        shortArray[i] = ((short)((firstByte << 8) + secondByte));
      }
      
    }
    else {
      for (int i = 0; i < shortCount; i++) {
        int firstByte = byteArray[(j++)] & 0xFF;
        int secondByte = byteArray[(j++)] & 0xFF;
        shortArray[i] = ((short)((secondByte << 8) + firstByte));
      }
    }
  }
  




  private void interpretBytesAsInts(byte[] byteArray, int[] intArray, int intCount)
  {
    int j = 0;
    
    if (isBigEndian)
    {
      for (int i = 0; i < intCount; i++) {
        intArray[i] = ((byteArray[(j++)] & 0xFF) << 24 | (byteArray[(j++)] & 0xFF) << 16 | (byteArray[(j++)] & 0xFF) << 8 | byteArray[(j++)] & 0xFF);

      }
      

    }
    else
    {

      for (int i = 0; i < intCount; i++) {
        intArray[i] = (byteArray[(j++)] & 0xFF | (byteArray[(j++)] & 0xFF) << 8 | (byteArray[(j++)] & 0xFF) << 16 | (byteArray[(j++)] & 0xFF) << 24);
      }
    }
  }
  








  private void interpretBytesAsFloats(byte[] byteArray, float[] floatArray, int floatCount)
  {
    int j = 0;
    
    if (isBigEndian)
    {
      for (int i = 0; i < floatCount; i++) {
        int value = (byteArray[(j++)] & 0xFF) << 24 | (byteArray[(j++)] & 0xFF) << 16 | (byteArray[(j++)] & 0xFF) << 8 | byteArray[(j++)] & 0xFF;
        


        floatArray[i] = Float.intBitsToFloat(value);
      }
      
    }
    else {
      for (int i = 0; i < floatCount; i++) {
        int value = byteArray[(j++)] & 0xFF | (byteArray[(j++)] & 0xFF) << 8 | (byteArray[(j++)] & 0xFF) << 16 | (byteArray[(j++)] & 0xFF) << 24;
        


        floatArray[i] = Float.intBitsToFloat(value);
      }
    }
  }
  

  private byte[] decodePackbits(byte[] data, int arraySize, byte[] dst)
  {
    if (dst == null) {
      dst = new byte[arraySize];
    }
    
    int srcCount = 0;int dstCount = 0;
    int srcArraySize = data.length;
    

    try
    {
      while ((dstCount < arraySize) && (srcCount < srcArraySize))
      {
        byte b = data[(srcCount++)];
        
        if ((b >= 0) && (b <= Byte.MAX_VALUE))
        {

          for (int i = 0; i < b + 1; i++) {
            dst[(dstCount++)] = data[(srcCount++)];
          }
        }
        else if ((b <= -1) && (b >= -127))
        {

          byte repeat = data[(srcCount++)];
          for (int i = 0; i < -b + 1; i++) {
            dst[(dstCount++)] = repeat;
          }
        }
        else
        {
          srcCount++;
        }
      }
    } catch (ArrayIndexOutOfBoundsException ae) {
      String message = JaiI18N.getString("TIFFImage14");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, ae), this, false);
    }
    



    return dst;
  }
  






  private ComponentColorModel createAlphaComponentColorModel(int dataType, int numBands, boolean isAlphaPremultiplied, int transparency)
  {
    ComponentColorModel ccm = null;
    int[] RGBBits = null;
    ColorSpace cs = null;
    switch (numBands) {
    case 2: 
      cs = ColorSpace.getInstance(1003);
      break;
    case 4: 
      cs = ColorSpace.getInstance(1000);
      break;
    default: 
      throw new IllegalArgumentException();
    }
    
    if (dataType == 4) {
      ccm = new FloatDoubleColorModel(cs, true, isAlphaPremultiplied, transparency, dataType);

    }
    else
    {

      int componentSize = 0;
      switch (dataType) {
      case 0: 
        componentSize = 8;
        break;
      case 1: 
      case 2: 
        componentSize = 16;
        break;
      case 3: 
        componentSize = 32;
        break;
      default: 
        throw new IllegalArgumentException();
      }
      
      RGBBits = new int[numBands];
      for (int i = 0; i < numBands; i++) {
        RGBBits[i] = componentSize;
      }
      
      ccm = new ComponentColorModel(cs, RGBBits, true, isAlphaPremultiplied, transparency, dataType);
    }
    





    return ccm;
  }
}
