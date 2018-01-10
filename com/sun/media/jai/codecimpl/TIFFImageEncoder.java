package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.SeekableOutputStream;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.Deflater;



































public class TIFFImageEncoder
  extends ImageEncoderImpl
{
  private static final int TIFF_UNSUPPORTED = -1;
  private static final int TIFF_BILEVEL_WHITE_IS_ZERO = 0;
  private static final int TIFF_BILEVEL_BLACK_IS_ZERO = 1;
  private static final int TIFF_GRAY = 2;
  private static final int TIFF_PALETTE = 3;
  private static final int TIFF_RGB = 4;
  private static final int TIFF_CMYK = 5;
  private static final int TIFF_YCBCR = 6;
  private static final int TIFF_CIELAB = 7;
  private static final int TIFF_GENERIC = 8;
  private static final int COMP_NONE = 1;
  private static final int COMP_GROUP3_1D = 2;
  private static final int COMP_GROUP3_2D = 3;
  private static final int COMP_GROUP4 = 4;
  private static final int COMP_JPEG_TTN2 = 7;
  private static final int COMP_PACKBITS = 32773;
  private static final int COMP_DEFLATE = 32946;
  private static final int TIFF_JPEG_TABLES = 347;
  private static final int TIFF_YCBCR_SUBSAMPLING = 530;
  private static final int TIFF_YCBCR_POSITIONING = 531;
  private static final int TIFF_REF_BLACK_WHITE = 532;
  private static final int EXTRA_SAMPLE_UNSPECIFIED = 0;
  private static final int EXTRA_SAMPLE_ASSOCIATED_ALPHA = 1;
  private static final int EXTRA_SAMPLE_UNASSOCIATED_ALPHA = 2;
  private static final int DEFAULT_ROWS_PER_STRIP = 8;
  private boolean isLittleEndian = false;
  
  private static final char[] intsToChars(int[] intArray) {
    int arrayLength = intArray.length;
    char[] charArray = new char[arrayLength];
    for (int i = 0; i < arrayLength; i++) {
      charArray[i] = ((char)(intArray[i] & 0xFFFF));
    }
    return charArray;
  }
  
  public TIFFImageEncoder(OutputStream output, ImageEncodeParam param) {
    super(output, param);
    if (this.param == null) {
      this.param = new TIFFEncodeParam();
    }
  }
  



  public void encode(RenderedImage im)
    throws IOException
  {
    TIFFEncodeParam encodeParam = (TIFFEncodeParam)param;
    

    isLittleEndian = encodeParam.getLittleEndian();
    

    writeFileHeader();
    
    Iterator iter = encodeParam.getExtraImages();
    if (iter != null) {
      int ifdOffset = 8;
      RenderedImage nextImage = im;
      TIFFEncodeParam nextParam = encodeParam;
      boolean hasNext;
      do {
        hasNext = iter.hasNext();
        ifdOffset = encode(nextImage, nextParam, ifdOffset, !hasNext);
        if (hasNext) {
          Object obj = iter.next();
          if ((obj instanceof RenderedImage)) {
            nextImage = (RenderedImage)obj;
            nextParam = encodeParam;
          } else if ((obj instanceof Object[])) {
            Object[] o = (Object[])obj;
            nextImage = (RenderedImage)o[0];
            nextParam = (TIFFEncodeParam)o[1];
          }
        }
      } while (hasNext);
    } else {
      encode(im, encodeParam, 8, true);
    }
  }
  
  private int encode(RenderedImage im, TIFFEncodeParam encodeParam, int ifdOffset, boolean isLast)
    throws IOException
  {
    if (CodecUtils.isPackedByteImage(im))
    {
      ColorModel sourceCM = im.getColorModel();
      

      ColorModel destCM = RasterFactory.createComponentColorModel(0, sourceCM.getColorSpace(), sourceCM.hasAlpha(), sourceCM.isAlphaPremultiplied(), sourceCM.getTransparency());
      







      Point origin = new Point(im.getMinX(), im.getMinY());
      WritableRaster raster = Raster.createWritableRaster(destCM.createCompatibleSampleModel(im.getWidth(), im.getHeight()), origin);
      





      raster.setRect(im.getData());
      

      im = new SingleTileRenderedImage(raster, destCM);
    }
    

    int compression = encodeParam.getCompression();
    

    boolean isTiled = encodeParam.getWriteTiled();
    

    int minX = im.getMinX();
    int minY = im.getMinY();
    int width = im.getWidth();
    int height = im.getHeight();
    

    SampleModel sampleModel = im.getSampleModel();
    

    int[] sampleSize = sampleModel.getSampleSize();
    for (int i = 1; i < sampleSize.length; i++) {
      if (sampleSize[i] != sampleSize[0]) {
        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder0"));
      }
    }
    

    int numBands = sampleModel.getNumBands();
    if (((sampleSize[0] == 1) || (sampleSize[0] == 4)) && (numBands != 1)) {
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder1"));
    }
    

    int dataType = sampleModel.getDataType();
    switch (dataType) {
    case 0: 
      if ((sampleSize[0] != 1) && (sampleSize[0] != 4) && (sampleSize[0] != 8))
      {
        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder2"));
      }
      break;
    case 1: 
    case 2: 
      if (sampleSize[0] != 16) {
        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder3"));
      }
      break;
    case 3: 
    case 4: 
      if (sampleSize[0] != 32) {
        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder4"));
      }
      break;
    default: 
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder5"));
    }
    
    boolean dataTypeIsShort = (dataType == 2) || (dataType == 1);
    


    ColorModel colorModel = im.getColorModel();
    if ((colorModel != null) && ((colorModel instanceof IndexColorModel)) && (dataType != 0))
    {


      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder6"));
    }
    IndexColorModel icm = null;
    int sizeOfColormap = 0;
    int[] colormap = null;
    

    int imageType = -1;
    int numExtraSamples = 0;
    int extraSampleType = 0;
    if ((colorModel instanceof IndexColorModel)) {
      icm = (IndexColorModel)colorModel;
      int mapSize = icm.getMapSize();
      
      if ((sampleSize[0] == 1) && (numBands == 1))
      {
        if (mapSize != 2) {
          throw new IllegalArgumentException(JaiI18N.getString("TIFFImageEncoder7"));
        }
        

        byte[] r = new byte[mapSize];
        icm.getReds(r);
        byte[] g = new byte[mapSize];
        icm.getGreens(g);
        byte[] b = new byte[mapSize];
        icm.getBlues(b);
        
        if (((r[0] & 0xFF) == 0) && ((r[1] & 0xFF) == 255) && ((g[0] & 0xFF) == 0) && ((g[1] & 0xFF) == 255) && ((b[0] & 0xFF) == 0) && ((b[1] & 0xFF) == 255))
        {





          imageType = 1;
        }
        else if (((r[0] & 0xFF) == 255) && ((r[1] & 0xFF) == 0) && ((g[0] & 0xFF) == 255) && ((g[1] & 0xFF) == 0) && ((b[0] & 0xFF) == 255) && ((b[1] & 0xFF) == 0))
        {





          imageType = 0;
        }
        else {
          imageType = 3;
        }
      }
      else if (numBands == 1)
      {
        imageType = 3;
      }
    } else if (colorModel == null)
    {
      if ((sampleSize[0] == 1) && (numBands == 1)) {
        imageType = 1;
      } else {
        imageType = 8;
        if (numBands > 1) {
          numExtraSamples = numBands - 1;
        }
      }
    }
    else {
      ColorSpace colorSpace = colorModel.getColorSpace();
      
      switch (colorSpace.getType()) {
      case 9: 
        imageType = 5;
        break;
      case 6: 
        imageType = 2;
        break;
      case 1: 
        imageType = 7;
        break;
      case 5: 
        if ((compression == 7) && (encodeParam.getJPEGCompressRGBToYCbCr()))
        {
          imageType = 6;
        } else {
          imageType = 4;
        }
        break;
      case 3: 
        imageType = 6;
        break;
      case 2: case 4: case 7: case 8: default: 
        imageType = 8;
      }
      
      
      if (imageType == 8) {
        numExtraSamples = numBands - 1;
      } else if (numBands > 1) {
        numExtraSamples = numBands - colorSpace.getNumComponents();
      }
      
      if ((numExtraSamples == 1) && (colorModel.hasAlpha())) {
        extraSampleType = colorModel.isAlphaPremultiplied() ? 1 : 2;
      }
    }
    


    if (imageType == -1) {
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder8"));
    }
    

    if (compression == 7) {
      if (imageType == 3)
        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder11"));
      if ((sampleSize[0] != 8) || ((imageType != 2) && (imageType != 4) && (imageType != 6)))
      {


        throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder9"));
      }
    }
    

    if ((imageType != 0) && (imageType != 1) && ((compression == 2) || (compression == 3) || (compression == 4)))
    {



      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder12"));
    }
    
    int photometricInterpretation = -1;
    switch (imageType)
    {
    case 0: 
      photometricInterpretation = 0;
      break;
    
    case 1: 
      photometricInterpretation = 1;
      break;
    

    case 2: 
    case 8: 
      photometricInterpretation = 1;
      break;
    
    case 3: 
      photometricInterpretation = 3;
      
      icm = (IndexColorModel)colorModel;
      sizeOfColormap = icm.getMapSize();
      
      byte[] r = new byte[sizeOfColormap];
      icm.getReds(r);
      byte[] g = new byte[sizeOfColormap];
      icm.getGreens(g);
      byte[] b = new byte[sizeOfColormap];
      icm.getBlues(b);
      
      int redIndex = 0;int greenIndex = sizeOfColormap;
      int blueIndex = 2 * sizeOfColormap;
      colormap = new int[sizeOfColormap * 3];
      for (int i = 0; i < sizeOfColormap; i++) {
        colormap[(redIndex++)] = (r[i] << 8 & 0xFFFF);
        colormap[(greenIndex++)] = (g[i] << 8 & 0xFFFF);
        colormap[(blueIndex++)] = (b[i] << 8 & 0xFFFF);
      }
      
      sizeOfColormap *= 3;
      
      break;
    
    case 4: 
      photometricInterpretation = 2;
      break;
    
    case 5: 
      photometricInterpretation = 5;
      break;
    
    case 6: 
      photometricInterpretation = 6;
      break;
    
    case 7: 
      photometricInterpretation = 8;
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder8"));
    }
    
    int tileHeight;
    int tileWidth;
    int tileHeight;
    if (isTiled) {
      int tileWidth = encodeParam.getTileWidth() > 0 ? encodeParam.getTileWidth() : im.getTileWidth();
      
      tileHeight = encodeParam.getTileHeight() > 0 ? encodeParam.getTileHeight() : im.getTileHeight();
    }
    else {
      tileWidth = width;
      
      tileHeight = encodeParam.getTileHeight() > 0 ? encodeParam.getTileHeight() : 8;
    }
    


    com.sun.media.jai.codec.JPEGEncodeParam jep = null;
    if (compression == 7)
    {
      jep = encodeParam.getJPEGEncodeParam();
      

      int maxSubH = jep.getHorizontalSubsampling(0);
      int maxSubV = jep.getVerticalSubsampling(0);
      for (int i = 1; i < numBands; i++) {
        int subH = jep.getHorizontalSubsampling(i);
        if (subH > maxSubH) {
          maxSubH = subH;
        }
        int subV = jep.getVerticalSubsampling(i);
        if (subV > maxSubV) {
          maxSubV = subV;
        }
      }
      
      int factorV = 8 * maxSubV;
      tileHeight = (int)(tileHeight / factorV + 0.5F) * factorV;
      
      if (tileHeight < factorV) {
        tileHeight = factorV;
      }
      
      if (isTiled) {
        int factorH = 8 * maxSubH;
        tileWidth = (int)(tileWidth / factorH + 0.5F) * factorH;
        
        if (tileWidth < factorH) {
          tileWidth = factorH;
        }
      }
    }
    int numTiles;
    int numTiles;
    if (isTiled)
    {
      numTiles = (width + tileWidth - 1) / tileWidth * ((height + tileHeight - 1) / tileHeight);
    }
    else
    {
      numTiles = (int)Math.ceil(height / tileHeight);
    }
    
    long[] tileByteCounts = new long[numTiles];
    
    long bytesPerRow = Math.ceil(sampleSize[0] / 8.0D * tileWidth * numBands);
    

    long bytesPerTile = bytesPerRow * tileHeight;
    
    for (int i = 0; i < numTiles; i++) {
      tileByteCounts[i] = bytesPerTile;
    }
    
    if (!isTiled)
    {
      long lastStripRows = height - tileHeight * (numTiles - 1);
      tileByteCounts[(numTiles - 1)] = (lastStripRows * bytesPerRow);
    }
    
    long totalBytesOfData = bytesPerTile * (numTiles - 1) + tileByteCounts[(numTiles - 1)];
    



    long[] tileOffsets = new long[numTiles];
    














    SortedSet fields = new TreeSet();
    

    fields.add(new TIFFField(256, 4, 1, new long[] { width }));
    



    fields.add(new TIFFField(257, 4, 1, new long[] { height }));
    


    fields.add(new TIFFField(258, 3, numBands, intsToChars(sampleSize)));
    


    fields.add(new TIFFField(259, 3, 1, new char[] { (char)compression }));
    


    fields.add(new TIFFField(262, 3, 1, new char[] { (char)photometricInterpretation }));
    



    if (!isTiled) {
      fields.add(new TIFFField(273, 4, numTiles, (long[])tileOffsets));
    }
    


    fields.add(new TIFFField(277, 3, 1, new char[] { (char)numBands }));
    


    if (!isTiled) {
      fields.add(new TIFFField(278, 4, 1, new long[] { tileHeight }));
      


      fields.add(new TIFFField(279, 4, numTiles, (long[])tileByteCounts));
    }
    


    if (colormap != null) {
      fields.add(new TIFFField(320, 3, sizeOfColormap, intsToChars(colormap)));
    }
    


    if (isTiled) {
      fields.add(new TIFFField(322, 4, 1, new long[] { tileWidth }));
      


      fields.add(new TIFFField(323, 4, 1, new long[] { tileHeight }));
      


      fields.add(new TIFFField(324, 4, numTiles, (long[])tileOffsets));
      


      fields.add(new TIFFField(325, 4, numTiles, (long[])tileByteCounts));
    }
    


    if (numExtraSamples > 0) {
      int[] extraSamples = new int[numExtraSamples];
      for (int i = 0; i < numExtraSamples; i++) {
        extraSamples[i] = extraSampleType;
      }
      fields.add(new TIFFField(338, 3, numExtraSamples, intsToChars(extraSamples)));
    }
    



    if (dataType != 0)
    {
      int[] sampleFormat = new int[numBands];
      if (dataType == 4) {
        sampleFormat[0] = 3;
      } else if (dataType == 1) {
        sampleFormat[0] = 1;
      } else {
        sampleFormat[0] = 2;
      }
      for (int b = 1; b < numBands; b++) {
        sampleFormat[b] = sampleFormat[0];
      }
      fields.add(new TIFFField(339, 3, numBands, intsToChars(sampleFormat)));
    }
    









    boolean inverseFill = encodeParam.getReverseFillOrder();
    boolean T4encode2D = encodeParam.getT4Encode2D();
    boolean T4PadEOLs = encodeParam.getT4PadEOLs();
    TIFFFaxEncoder faxEncoder = null;
    

    if (((imageType == 1) || (imageType == 0)) && ((compression == 2) || (compression == 3) || (compression == 4)))
    {





      faxEncoder = new TIFFFaxEncoder(inverseFill);
      

      fields.add(new TIFFField(266, 3, 1, new char[] { inverseFill ? 2 : 1 }));
      



      if (compression == 3)
      {
        long T4Options = 0L;
        if (T4encode2D) {
          T4Options |= 1L;
        }
        if (T4PadEOLs) {
          T4Options |= 0x4;
        }
        fields.add(new TIFFField(292, 4, 1, new long[] { T4Options }));

      }
      else if (compression == 4)
      {
        fields.add(new TIFFField(293, 4, 1, new long[] { 0L }));
      }
    }
    



    com.sun.image.codec.jpeg.JPEGEncodeParam jpegEncodeParam = null;
    com.sun.image.codec.jpeg.JPEGImageEncoder jpegEncoder = null;
    int jpegColorID = 0;
    
    if (compression == 7)
    {

      jpegColorID = 0;
      
      switch (imageType) {
      case 2: 
      case 3: 
        jpegColorID = 1;
        
        break;
      case 4: 
        jpegColorID = 2;
        
        break;
      case 6: 
        jpegColorID = 3;
      }
      
      


      Raster tile00 = im.getTile(im.getMinTileX(), im.getMinTileY());
      jpegEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam(tile00, jpegColorID);
      



      JPEGImageEncoder.modifyEncodeParam(jep, jpegEncodeParam, numBands);
      

      if (jep.getWriteImageOnly())
      {
        jpegEncodeParam.setImageInfoValid(false);
        jpegEncodeParam.setTableInfoValid(true);
        ByteArrayOutputStream tableStream = new ByteArrayOutputStream();
        
        jpegEncoder = JPEGCodec.createJPEGEncoder(tableStream, jpegEncodeParam);
        


        jpegEncoder.encode(tile00);
        byte[] tableData = tableStream.toByteArray();
        fields.add(new TIFFField(347, 7, tableData.length, tableData));
        




        jpegEncoder = null;
      }
    }
    
    if (imageType == 6)
    {

      int subsampleH = 1;
      int subsampleV = 1;
      

      if (compression == 7)
      {
        subsampleH = jep.getHorizontalSubsampling(0);
        subsampleV = jep.getVerticalSubsampling(0);
        for (int i = 1; i < numBands; i++) {
          int subH = jep.getHorizontalSubsampling(i);
          if (subH > subsampleH) {
            subsampleH = subH;
          }
          int subV = jep.getVerticalSubsampling(i);
          if (subV > subsampleV) {
            subsampleV = subV;
          }
        }
      }
      
      fields.add(new TIFFField(530, 3, 2, new char[] { (char)subsampleH, (char)subsampleV }));
      





      fields.add(new TIFFField(531, 3, 1, new char[] { compression == 7 ? 1 : 2 }));
      

      long[][] refbw;
      
      long[][] refbw;
      
      if (compression == 7) {
        refbw = new long[][] { { 0L, 1L }, { 255L, 1L }, { 128L, 1L }, { 255L, 1L }, { 128L, 1L }, { 255L, 1L } };

      }
      else
      {
        refbw = new long[][] { { 15L, 1L }, { 235L, 1L }, { 128L, 1L }, { 240L, 1L }, { 128L, 1L }, { 240L, 1L } };
      }
      


      fields.add(new TIFFField(532, 5, 6, refbw));
    }
    






    TIFFField[] extraFields = encodeParam.getExtraFields();
    if (extraFields != null) {
      ArrayList extantTags = new ArrayList(fields.size());
      Iterator fieldIter = fields.iterator();
      while (fieldIter.hasNext()) {
        TIFFField fld = (TIFFField)fieldIter.next();
        extantTags.add(new Integer(fld.getTag()));
      }
      
      int numExtraFields = extraFields.length;
      for (int i = 0; i < numExtraFields; i++) {
        TIFFField fld = extraFields[i];
        Integer tagValue = new Integer(fld.getTag());
        if (!extantTags.contains(tagValue)) {
          fields.add(fld);
          extantTags.add(tagValue);
        }
      }
    }
    





    int dirSize = getDirectorySize(fields);
    


    tileOffsets[0] = (ifdOffset + dirSize);
    









    OutputStream outCache = null;
    byte[] compressBuf = null;
    File tempFile = null;
    
    int nextIFDOffset = 0;
    boolean skipByte = false;
    
    Deflater deflater = null;
    int deflateLevel = -1;
    
    boolean jpegRGBToYCbCr = false;
    
    if (compression == 1)
    {



      int numBytesPadding = 0;
      if ((sampleSize[0] == 16) && (tileOffsets[0] % 2L != 0L)) {
        numBytesPadding = 1;
        tileOffsets[0] += 1L;
      } else if ((sampleSize[0] == 32) && (tileOffsets[0] % 4L != 0L)) {
        numBytesPadding = (int)(4L - tileOffsets[0] % 4L);
        tileOffsets[0] += numBytesPadding;
      }
      

      for (int i = 1; i < numTiles; i++) {
        tileOffsets[i] = (tileOffsets[(i - 1)] + tileByteCounts[(i - 1)]);
      }
      
      if (!isLast)
      {
        nextIFDOffset = (int)(tileOffsets[0] + totalBytesOfData);
        

        if (nextIFDOffset % 2 != 0) {
          nextIFDOffset++;
          skipByte = true;
        }
      }
      

      writeDirectory(ifdOffset, fields, nextIFDOffset);
      


      if (numBytesPadding != 0) {
        for (int padding = 0; padding < numBytesPadding; padding++) {
          output.write(0);
        }
        
      }
    }
    else
    {
      if ((output instanceof SeekableOutputStream))
      {
        ((SeekableOutputStream)output).seek(tileOffsets[0]);
      }
      else {
        outCache = output;
        
        try
        {
          tempFile = File.createTempFile("jai-SOS-", ".tmp");
          tempFile.deleteOnExit();
          RandomAccessFile raFile = new RandomAccessFile(tempFile, "rw");
          
          output = new SeekableOutputStream(raFile);
        }
        catch (Exception e)
        {
          tempFile = null;
          
          output = new ByteArrayOutputStream((int)totalBytesOfData);
        }
      }
      
      int bufSize = 0;
      switch (compression)
      {







      case 2: 
        bufSize = (int)Math.ceil(((tileWidth + 1) / 2 * 9 + 2) / 8.0D);
        break;
      


      case 3: 
      case 4: 
        bufSize = (int)Math.ceil(((tileWidth + 1) / 2 * 9 + 2) / 8.0D);
        bufSize = tileHeight * (bufSize + 2) + 12;
        break;
      case 32773: 
        bufSize = (int)(bytesPerTile + (bytesPerRow + 127L) / 128L * tileHeight);
        
        break;
      case 7: 
        bufSize = 0;
        

        if ((imageType == 6) && (colorModel != null) && (colorModel.getColorSpace().getType() == 5))
        {


          jpegRGBToYCbCr = true;
        }
        break;
      case 32946: 
        bufSize = (int)bytesPerTile;
        deflater = new Deflater(encodeParam.getDeflateLevel());
        break;
      default: 
        bufSize = 0; }
      
      if (bufSize != 0) {
        compressBuf = new byte[bufSize];
      }
    }
    



    int[] pixels = null;
    float[] fpixels = null;
    

    boolean checkContiguous = ((sampleSize[0] == 1) && ((sampleModel instanceof MultiPixelPackedSampleModel)) && (dataType == 0)) || ((sampleSize[0] == 8) && ((sampleModel instanceof ComponentSampleModel)));
    







    byte[] bpixels = null;
    if (compression != 7) {
      if (dataType == 0) {
        bpixels = new byte[tileHeight * tileWidth * numBands];
      } else if (dataTypeIsShort) {
        bpixels = new byte[2 * tileHeight * tileWidth * numBands];
      } else if ((dataType == 3) || (dataType == 4))
      {
        bpixels = new byte[4 * tileHeight * tileWidth * numBands];
      }
    }
    

    int lastRow = minY + height;
    int lastCol = minX + width;
    int tileNum = 0;
    for (int row = minY; row < lastRow; row += tileHeight) {
      int rows = isTiled ? tileHeight : Math.min(tileHeight, lastRow - row);
      
      int size = rows * tileWidth * numBands;
      
      for (int col = minX; col < lastCol; col += tileWidth)
      {
        Raster src = im.getData(new Rectangle(col, row, tileWidth, rows));
        

        boolean useDataBuffer = false;
        if (compression != 7) {
          if (checkContiguous) {
            if (sampleSize[0] == 8) {
              ComponentSampleModel csm = (ComponentSampleModel)src.getSampleModel();
              
              int[] bankIndices = csm.getBankIndices();
              int[] bandOffsets = csm.getBandOffsets();
              int pixelStride = csm.getPixelStride();
              int lineStride = csm.getScanlineStride();
              
              if ((pixelStride != numBands) || (lineStride != bytesPerRow))
              {
                useDataBuffer = false;
              } else {
                useDataBuffer = true;
                for (int i = 0; 
                    (useDataBuffer) && (i < numBands); 
                    i++) {
                  if ((bankIndices[i] != 0) || (bandOffsets[i] != i))
                  {
                    useDataBuffer = false;
                  }
                }
              }
            } else {
              MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)src.getSampleModel();
              
              if ((mpp.getNumBands() == 1) && (mpp.getDataBitOffset() == 0) && (mpp.getPixelBitStride() == 1))
              {

                useDataBuffer = true;
              }
            }
          }
          
          if (!useDataBuffer) {
            if (dataType == 4) {
              fpixels = src.getPixels(col, row, tileWidth, rows, fpixels);
            }
            else {
              pixels = src.getPixels(col, row, tileWidth, rows, pixels);
            }
          }
        }
        



        int pixel = 0;
        int k = 0;
        switch (sampleSize[0])
        {

        case 1: 
          if (useDataBuffer) {
            byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
            
            MultiPixelPackedSampleModel mpp = (MultiPixelPackedSampleModel)src.getSampleModel();
            
            int lineStride = mpp.getScanlineStride();
            int inOffset = mpp.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
            



            if (lineStride == (int)bytesPerRow) {
              System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);
            }
            else
            {
              int outOffset = 0;
              for (int j = 0; j < rows; j++) {
                System.arraycopy(btmp, inOffset, bpixels, outOffset, (int)bytesPerRow);
                

                inOffset += lineStride;
                outOffset += (int)bytesPerRow;
              }
            }
          } else {
            int index = 0;
            

            for (int i = 0; i < rows; i++)
            {

              for (int j = 0; j < tileWidth / 8; j++)
              {
                pixel = pixels[(index++)] << 7 | pixels[(index++)] << 6 | pixels[(index++)] << 5 | pixels[(index++)] << 4 | pixels[(index++)] << 3 | pixels[(index++)] << 2 | pixels[(index++)] << 1 | pixels[(index++)];
                







                bpixels[(k++)] = ((byte)pixel);
              }
              

              if (tileWidth % 8 > 0) {
                pixel = 0;
                for (int j = 0; j < tileWidth % 8; j++) {
                  pixel |= pixels[(index++)] << 7 - j;
                }
                bpixels[(k++)] = ((byte)pixel);
              }
            }
          }
          
          if (compression == 1) {
            output.write(bpixels, 0, rows * ((tileWidth + 7) / 8));
          } else if (compression == 2) {
            int rowStride = (tileWidth + 7) / 8;
            int rowOffset = 0;
            int numCompressedBytes = 0;
            for (int tileRow = 0; tileRow < rows; tileRow++) {
              int numCompressedBytesInRow = faxEncoder.encodeRLE(bpixels, rowOffset, 0, tileWidth, compressBuf);
              


              output.write(compressBuf, 0, numCompressedBytesInRow);
              
              rowOffset += rowStride;
              numCompressedBytes += numCompressedBytesInRow;
            }
            tileByteCounts[(tileNum++)] = numCompressedBytes;
          } else if (compression == 3) {
            int numCompressedBytes = faxEncoder.encodeT4(!T4encode2D, T4PadEOLs, bpixels, (tileWidth + 7) / 8, 0, tileWidth, rows, compressBuf);
            







            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 4) {
            int numCompressedBytes = faxEncoder.encodeT6(bpixels, (tileWidth + 7) / 8, 0, tileWidth, rows, compressBuf);
            





            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 32773) {
            int numCompressedBytes = compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
            


            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 32946) {
            int numCompressedBytes = deflate(deflater, bpixels, compressBuf);
            
            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          }
          


          break;
        case 4: 
          int index = 0;
          

          for (int i = 0; i < rows; i++)
          {


            for (int j = 0; j < tileWidth / 2; j++) {
              pixel = pixels[(index++)] << 4 | pixels[(index++)];
              bpixels[(k++)] = ((byte)pixel);
            }
            

            if (tileWidth % 2 == 1) {
              pixel = pixels[(index++)] << 4;
              bpixels[(k++)] = ((byte)pixel);
            }
          }
          
          if (compression == 1) {
            output.write(bpixels, 0, rows * ((tileWidth + 1) / 2));
          } else if (compression == 32773) {
            int numCompressedBytes = compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
            


            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 32946) {
            int numCompressedBytes = deflate(deflater, bpixels, compressBuf);
            
            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          }
          

          break;
        case 8: 
          if (compression != 7) {
            if (useDataBuffer) {
              byte[] btmp = ((DataBufferByte)src.getDataBuffer()).getData();
              
              ComponentSampleModel csm = (ComponentSampleModel)src.getSampleModel();
              
              int inOffset = csm.getOffset(col - src.getSampleModelTranslateX(), row - src.getSampleModelTranslateY());
              



              int lineStride = csm.getScanlineStride();
              if (lineStride == (int)bytesPerRow) {
                System.arraycopy(btmp, inOffset, bpixels, 0, (int)bytesPerRow * rows);

              }
              else
              {
                int outOffset = 0;
                for (int j = 0; j < rows; j++) {
                  System.arraycopy(btmp, inOffset, bpixels, outOffset, (int)bytesPerRow);
                  

                  inOffset += lineStride;
                  outOffset += (int)bytesPerRow;
                }
              }
            } else {
              for (int i = 0; i < size; i++) {
                bpixels[i] = ((byte)pixels[i]);
              }
            }
          }
          
          if (compression == 1) {
            output.write(bpixels, 0, size);
          } else if (compression == 32773) {
            int numCompressedBytes = compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
            


            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 7) {
            long startPos = getOffset(output);
            



            if ((jpegEncoder == null) || (jpegEncodeParam.getWidth() != src.getWidth()) || (jpegEncodeParam.getHeight() != src.getHeight()))
            {


              jpegEncodeParam = JPEGCodec.getDefaultJPEGEncodeParam(src, jpegColorID);
              


              JPEGImageEncoder.modifyEncodeParam(jep, jpegEncodeParam, numBands);
              


              jpegEncoder = JPEGCodec.createJPEGEncoder(output, jpegEncodeParam);
            }
            



            if (jpegRGBToYCbCr) {
              WritableRaster wRas = null;
              if ((src instanceof WritableRaster)) {
                wRas = (WritableRaster)src;
              } else {
                wRas = src.createCompatibleWritableRaster();
                wRas.setRect(src);
              }
              
              if ((wRas.getMinX() != 0) || (wRas.getMinY() != 0)) {
                wRas = wRas.createWritableTranslatedChild(0, 0);
              }
              
              BufferedImage bi = new BufferedImage(colorModel, wRas, false, null);
              

              jpegEncoder.encode(bi);
            } else {
              jpegEncoder.encode(src.createTranslatedChild(0, 0));
            }
            

            long endPos = getOffset(output);
            tileByteCounts[(tileNum++)] = ((int)(endPos - startPos));
          } else if (compression == 32946) {
            int numCompressedBytes = deflate(deflater, bpixels, compressBuf);
            
            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          }
          

          break;
        case 16: 
          int ls = 0;
          for (int i = 0; i < size; i++) {
            short value = (short)pixels[i];
            bpixels[(ls++)] = ((byte)((value & 0xFF00) >> 8));
            bpixels[(ls++)] = ((byte)(value & 0xFF));
          }
          
          if (compression == 1) {
            output.write(bpixels, 0, size * 2);
          } else if (compression == 32773) {
            int numCompressedBytes = compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
            


            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 32946) {
            int numCompressedBytes = deflate(deflater, bpixels, compressBuf);
            
            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          }
          
          break;
        case 32: 
          if (dataType == 3) {
            int li = 0;
            for (int i = 0; i < size; i++) {
              int value = pixels[i];
              bpixels[(li++)] = ((byte)((value & 0xFF000000) >> 24));
              bpixels[(li++)] = ((byte)((value & 0xFF0000) >> 16));
              bpixels[(li++)] = ((byte)((value & 0xFF00) >> 8));
              bpixels[(li++)] = ((byte)(value & 0xFF));
            }
          } else {
            int lf = 0;
            for (int i = 0; i < size; i++) {
              int value = Float.floatToIntBits(fpixels[i]);
              bpixels[(lf++)] = ((byte)((value & 0xFF000000) >> 24));
              bpixels[(lf++)] = ((byte)((value & 0xFF0000) >> 16));
              bpixels[(lf++)] = ((byte)((value & 0xFF00) >> 8));
              bpixels[(lf++)] = ((byte)(value & 0xFF));
            }
          }
          if (compression == 1) {
            output.write(bpixels, 0, size * 4);
          } else if (compression == 32773) {
            int numCompressedBytes = compressPackBits(bpixels, rows, (int)bytesPerRow, compressBuf);
            


            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          } else if (compression == 32946) {
            int numCompressedBytes = deflate(deflater, bpixels, compressBuf);
            
            tileByteCounts[(tileNum++)] = numCompressedBytes;
            output.write(compressBuf, 0, numCompressedBytes);
          }
          
          break;
        }
        
      }
    }
    if (compression == 1)
    {
      if (skipByte) {
        output.write(0);
      }
    }
    else {
      int totalBytes = 0;
      for (int i = 1; i < numTiles; i++) {
        int numBytes = (int)tileByteCounts[(i - 1)];
        totalBytes += numBytes;
        tileOffsets[i] = (tileOffsets[(i - 1)] + numBytes);
      }
      totalBytes += (int)tileByteCounts[(numTiles - 1)];
      
      nextIFDOffset = isLast ? 0 : ifdOffset + dirSize + totalBytes;
      


      if (nextIFDOffset % 2 != 0) {
        nextIFDOffset++;
        skipByte = true;
      }
      
      if (outCache == null)
      {


        if (skipByte) {
          output.write(0);
        }
        
        SeekableOutputStream sos = (SeekableOutputStream)output;
        

        long savePos = sos.getFilePointer();
        

        sos.seek(ifdOffset);
        writeDirectory(ifdOffset, fields, nextIFDOffset);
        

        sos.seek(savePos);
      } else if (tempFile != null)
      {



        output.close();
        

        FileInputStream fileStream = new FileInputStream(tempFile);
        

        output = outCache;
        

        writeDirectory(ifdOffset, fields, nextIFDOffset);
        

        byte[] copyBuffer = new byte[' '];
        int bytesCopied = 0;
        while (bytesCopied < totalBytes) {
          int bytesRead = fileStream.read(copyBuffer);
          if (bytesRead == -1) {
            break;
          }
          output.write(copyBuffer, 0, bytesRead);
          bytesCopied += bytesRead;
        }
        

        fileStream.close();
        tempFile.delete();
        

        if (skipByte) {
          output.write(0);
        }
      } else if ((output instanceof ByteArrayOutputStream))
      {


        ByteArrayOutputStream memoryStream = (ByteArrayOutputStream)output;
        


        output = outCache;
        

        writeDirectory(ifdOffset, fields, nextIFDOffset);
        

        memoryStream.writeTo(output);
        

        if (skipByte) {
          output.write(0);
        }
      }
      else {
        throw new IllegalStateException();
      }
    }
    

    return nextIFDOffset;
  }
  



  private int getDirectorySize(SortedSet fields)
  {
    int numEntries = fields.size();
    

    int dirSize = 2 + numEntries * 12 + 4;
    

    Iterator iter = fields.iterator();
    while (iter.hasNext())
    {
      TIFFField field = (TIFFField)iter.next();
      

      int valueSize = getValueSize(field);
      

      if (valueSize > 4) {
        dirSize += valueSize;
      }
    }
    
    return dirSize;
  }
  

  private void writeFileHeader()
    throws IOException
  {
    if (isLittleEndian)
    {
      output.write(73);
      output.write(73);
    }
    else {
      output.write(77);
      output.write(77);
    }
    

    writeUnsignedShort(42);
    

    writeLong(8L);
  }
  


  private void writeDirectory(int thisIFDOffset, SortedSet fields, int nextIFDOffset)
    throws IOException
  {
    int numEntries = fields.size();
    
    long offsetBeyondIFD = thisIFDOffset + 12 * numEntries + 4 + 2;
    ArrayList tooBig = new ArrayList();
    

    writeUnsignedShort(numEntries);
    
    Iterator iter = fields.iterator();
    while (iter.hasNext())
    {

      TIFFField field = (TIFFField)iter.next();
      

      int tag = field.getTag();
      writeUnsignedShort(tag);
      

      int type = field.getType();
      writeUnsignedShort(type);
      


      int count = field.getCount();
      int valueSize = getValueSize(field);
      writeLong(type == 2 ? valueSize : count);
      

      if (valueSize > 4)
      {

        writeLong(offsetBeyondIFD);
        offsetBeyondIFD += valueSize;
        tooBig.add(field);
      }
      else
      {
        writeValuesAsFourBytes(field);
      }
    }
    


    writeLong(nextIFDOffset);
    

    for (int i = 0; i < tooBig.size(); i++) {
      writeValues((TIFFField)tooBig.get(i));
    }
  }
  


  private static final int getValueSize(TIFFField field)
  {
    int type = field.getType();
    int count = field.getCount();
    int valueSize = 0;
    if (type == 2) {
      for (int i = 0; i < count; i++) {
        byte[] stringBytes = field.getAsString(i).getBytes();
        valueSize += stringBytes.length;
        if (stringBytes[(stringBytes.length - 1)] != 0) {
          valueSize++;
        }
      }
    } else {
      valueSize = count * sizeOfType[type];
    }
    return valueSize;
  }
  
  private static final int[] sizeOfType = { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };
  













  private void writeValuesAsFourBytes(TIFFField field)
    throws IOException
  {
    int dataType = field.getType();
    int count = field.getCount();
    
    switch (dataType)
    {

    case 1: 
    case 6: 
    case 7: 
      byte[] bytes = field.getAsBytes();
      
      for (int i = 0; i < count; i++) {
        output.write(bytes[i]);
      }
      
      for (int i = 0; i < 4 - count; i++) {
        output.write(0);
      }
      
      break;
    

    case 3: 
      char[] shorts = field.getAsChars();
      
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(shorts[i]);
      }
      
      for (int i = 0; i < 2 - count; i++) {
        writeUnsignedShort(0);
      }
      
      break;
    

    case 8: 
      short[] sshorts = field.getAsShorts();
      
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(sshorts[i]);
      }
      
      for (int i = 0; i < 2 - count; i++) {
        writeUnsignedShort(0);
      }
      
      break;
    

    case 4: 
      writeLong(field.getAsLong(0));
      break;
    

    case 9: 
      writeLong(field.getAsInt(0));
      break;
    
    case 11: 
      writeLong(Float.floatToIntBits(field.getAsFloat(0)));
      break;
    
    case 2: 
      int asciiByteCount = 0;
      for (int i = 0; i < count; i++) {
        byte[] stringBytes = field.getAsString(i).getBytes();
        output.write(stringBytes);
        asciiByteCount += stringBytes.length;
        if (stringBytes[(stringBytes.length - 1)] != 0) {
          output.write(0);
          asciiByteCount++;
        }
      }
      for (int i = 0; i < 4 - asciiByteCount; i++) {
        output.write(0);
      }
      break;
    case 5: case 10: 
    default: 
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder10"));
    }
    
  }
  
  private void writeValues(TIFFField field) throws IOException
  {
    int dataType = field.getType();
    int count = field.getCount();
    
    switch (dataType)
    {

    case 1: 
    case 6: 
    case 7: 
      byte[] bytes = field.getAsBytes();
      for (int i = 0; i < count; i++) {
        output.write(bytes[i]);
      }
      break;
    

    case 3: 
      char[] shorts = field.getAsChars();
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(shorts[i]);
      }
      break;
    

    case 8: 
      short[] sshorts = field.getAsShorts();
      for (int i = 0; i < count; i++) {
        writeUnsignedShort(sshorts[i]);
      }
      break;
    

    case 4: 
      long[] longs = field.getAsLongs();
      for (int i = 0; i < count; i++) {
        writeLong(longs[i]);
      }
      break;
    

    case 9: 
      int[] slongs = field.getAsInts();
      for (int i = 0; i < count; i++) {
        writeLong(slongs[i]);
      }
      break;
    
    case 11: 
      float[] floats = field.getAsFloats();
      for (int i = 0; i < count; i++) {
        int intBits = Float.floatToIntBits(floats[i]);
        writeLong(intBits);
      }
      break;
    
    case 12: 
      double[] doubles = field.getAsDoubles();
      for (int i = 0; i < count; i++) {
        long longBits = Double.doubleToLongBits(doubles[i]);
        writeLong((int)(longBits >> 32));
        writeLong((int)(longBits & 0xFFFFFFFFFFFFFFFF));
      }
      break;
    

    case 5: 
      long[][] rationals = field.getAsRationals();
      for (int i = 0; i < count; i++) {
        writeLong(rationals[i][0]);
        writeLong(rationals[i][1]);
      }
      break;
    

    case 10: 
      int[][] srationals = field.getAsSRationals();
      for (int i = 0; i < count; i++) {
        writeLong(srationals[i][0]);
        writeLong(srationals[i][1]);
      }
      break;
    
    case 2: 
      for (int i = 0; i < count; i++) {
        byte[] stringBytes = field.getAsString(i).getBytes();
        output.write(stringBytes);
        if (stringBytes[(stringBytes.length - 1)] != 0) {
          output.write(0);
        }
      }
      break;
    
    default: 
      throw new RuntimeException(JaiI18N.getString("TIFFImageEncoder10"));
    }
    
  }
  

  private void writeUnsignedShort(int s)
    throws IOException
  {
    if (isLittleEndian) {
      output.write(s & 0xFF);
      output.write((s & 0xFF00) >>> 8);
    } else {
      output.write((s & 0xFF00) >>> 8);
      output.write(s & 0xFF);
    }
  }
  
  private void writeLong(long l) throws IOException {
    if (isLittleEndian) {
      output.write((int)l & 0xFF);
      output.write((int)((l & 0xFF00) >>> 8));
      output.write((int)((l & 0xFF0000) >>> 16));
      output.write((int)((l & 0xFFFFFFFFFF000000) >>> 24));
    } else {
      output.write((int)((l & 0xFFFFFFFFFF000000) >>> 24));
      output.write((int)((l & 0xFF0000) >>> 16));
      output.write((int)((l & 0xFF00) >>> 8));
      output.write((int)l & 0xFF);
    }
  }
  


  private long getOffset(OutputStream out)
    throws IOException
  {
    if ((out instanceof ByteArrayOutputStream))
      return ((ByteArrayOutputStream)out).size();
    if ((out instanceof SeekableOutputStream)) {
      return ((SeekableOutputStream)out).getFilePointer();
    }
    
    throw new IllegalStateException();
  }
  




  private static int compressPackBits(byte[] data, int numRows, int bytesPerRow, byte[] compData)
  {
    int inOffset = 0;
    int outOffset = 0;
    
    for (int i = 0; i < numRows; i++) {
      outOffset = packBits(data, inOffset, bytesPerRow, compData, outOffset);
      
      inOffset += bytesPerRow;
    }
    
    return outOffset;
  }
  





  private static int packBits(byte[] input, int inOffset, int inCount, byte[] output, int outOffset)
  {
    int inMax = inOffset + inCount - 1;
    int inMaxMinus1 = inMax - 1;
    
    while (inOffset <= inMax) {
      int run = 1;
      byte replicate = input[inOffset];
      
      while ((run < 127) && (inOffset < inMax) && (input[inOffset] == input[(inOffset + 1)])) {
        run++;
        inOffset++;
      }
      if (run > 1) {
        inOffset++;
        output[(outOffset++)] = ((byte)-(run - 1));
        output[(outOffset++)] = replicate;
      }
      
      run = 0;
      int saveOffset = outOffset;
      while ((run < 128) && (((inOffset < inMax) && (input[inOffset] != input[(inOffset + 1)])) || ((inOffset < inMaxMinus1) && (input[inOffset] != input[(inOffset + 2)]))))
      {



        run++;
        output[(++outOffset)] = input[(inOffset++)];
      }
      if (run > 0) {
        output[saveOffset] = ((byte)(run - 1));
        outOffset++;
      }
      
      if (inOffset == inMax) {
        if ((run > 0) && (run < 128)) {
          int tmp198_196 = saveOffset; byte[] tmp198_195 = output;tmp198_195[tmp198_196] = ((byte)(tmp198_195[tmp198_196] + 1));
          output[(outOffset++)] = input[(inOffset++)];
        } else {
          output[(outOffset++)] = 0;
          output[(outOffset++)] = input[(inOffset++)];
        }
      }
    }
    
    return outOffset;
  }
  
  private static int deflate(Deflater deflater, byte[] inflated, byte[] deflated)
  {
    deflater.setInput(inflated);
    deflater.finish();
    int numCompressedBytes = deflater.deflate(deflated);
    deflater.reset();
    return numCompressedBytes;
  }
}
