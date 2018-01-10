package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.PNMEncodeParam;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
































public class PNMImageEncoder
  extends ImageEncoderImpl
{
  private static final int PBM_ASCII = 49;
  private static final int PGM_ASCII = 50;
  private static final int PPM_ASCII = 51;
  private static final int PBM_RAW = 52;
  private static final int PGM_RAW = 53;
  private static final int PPM_RAW = 54;
  private static final int SPACE = 32;
  private static final String COMMENT = "# written by com.sun.media.jai.codecimpl.PNMImageEncoder";
  private byte[] lineSeparator;
  private int variant;
  private int maxValue;
  
  public PNMImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    if (this.param == null) {
      this.param = new PNMEncodeParam();
    }
  }
  


  public void encode(RenderedImage im)
    throws IOException
  {
    int minX = im.getMinX();
    int minY = im.getMinY();
    int width = im.getWidth();
    int height = im.getHeight();
    int tileHeight = im.getTileHeight();
    SampleModel sampleModel = im.getSampleModel();
    ColorModel colorModel = im.getColorModel();
    
    String ls = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    
    lineSeparator = ls.getBytes();
    
    int dataType = sampleModel.getTransferType();
    if ((dataType == 4) || (dataType == 5))
    {
      throw new RuntimeException(JaiI18N.getString("PNMImageEncoder0"));
    }
    

    int[] sampleSize = sampleModel.getSampleSize();
    int numBands = sampleModel.getNumBands();
    

    byte[] reds = null;
    byte[] greens = null;
    byte[] blues = null;
    

    boolean isPBMInverted = false;
    
    if (numBands == 1) {
      if ((colorModel instanceof IndexColorModel)) {
        IndexColorModel icm = (IndexColorModel)colorModel;
        
        int mapSize = icm.getMapSize();
        if (mapSize < 1 << sampleSize[0]) {
          throw new RuntimeException(JaiI18N.getString("PNMImageEncoder1"));
        }
        

        if (sampleSize[0] == 1) {
          variant = 52;
          



          isPBMInverted = icm.getRed(1) + icm.getGreen(1) + icm.getBlue(1) > icm.getRed(0) + icm.getGreen(0) + icm.getBlue(0);
        }
        else
        {
          variant = 54;
          
          reds = new byte[mapSize];
          greens = new byte[mapSize];
          blues = new byte[mapSize];
          
          icm.getReds(reds);
          icm.getGreens(greens);
          icm.getBlues(blues);
        }
      } else if (sampleSize[0] == 1) {
        variant = 52;
      } else if (sampleSize[0] <= 8) {
        variant = 53;
      } else {
        variant = 50;
      }
    } else if (numBands == 3) {
      if ((sampleSize[0] <= 8) && (sampleSize[1] <= 8) && (sampleSize[2] <= 8))
      {
        variant = 54;
      } else {
        variant = 51;
      }
    } else {
      throw new RuntimeException(JaiI18N.getString("PNMImageEncoder2"));
    }
    

    if (((PNMEncodeParam)param).getRaw()) {
      if (!isRaw(variant)) {
        boolean canUseRaw = true;
        

        for (int i = 0; i < sampleSize.length; i++) {
          if (sampleSize[i] > 8) {
            canUseRaw = false;
            break;
          }
        }
        
        if (canUseRaw) {
          variant += 3;
        }
      }
    }
    else if (isRaw(variant)) {
      variant -= 3;
    }
    

    maxValue = ((1 << sampleSize[0]) - 1);
    

    output.write(80);
    output.write(variant);
    
    output.write(lineSeparator);
    output.write("# written by com.sun.media.jai.codecimpl.PNMImageEncoder".getBytes());
    
    output.write(lineSeparator);
    writeInteger(output, width);
    output.write(32);
    writeInteger(output, height);
    

    if ((variant != 52) && (variant != 49)) {
      output.write(lineSeparator);
      writeInteger(output, maxValue);
    }
    


    if ((variant == 52) || (variant == 53) || (variant == 54))
    {

      output.write(10);
    }
    


    boolean writeOptimal = false;
    if ((variant == 52) && (sampleModel.getTransferType() == 0) && ((sampleModel instanceof MultiPixelPackedSampleModel)))
    {


      MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sampleModel;
      


      if ((mppsm.getDataBitOffset() == 0) && (mppsm.getPixelBitStride() == 1))
      {

        writeOptimal = true;
      }
    } else if (((variant == 53) || (variant == 54)) && ((sampleModel instanceof ComponentSampleModel)) && (!(colorModel instanceof IndexColorModel)))
    {


      ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
      


      if (csm.getPixelStride() == numBands) {
        writeOptimal = true;
        

        if (variant == 54) {
          int[] bandOffsets = csm.getBandOffsets();
          for (int b = 0; b < numBands; b++) {
            if (bandOffsets[b] != b) {
              writeOptimal = false;
              break;
            }
          }
        }
      }
    }
    

    if (writeOptimal) {
      int bytesPerRow = variant == 52 ? (width + 7) / 8 : width * sampleModel.getNumBands();
      
      int numYTiles = im.getNumYTiles();
      Rectangle imageBounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
      

      Rectangle stripRect = new Rectangle(im.getMinX(), im.getMinTileY() * im.getTileHeight() + im.getTileGridYOffset(), im.getWidth(), im.getTileHeight());
      



      byte[] invertedData = null;
      if (isPBMInverted) {
        invertedData = new byte[bytesPerRow];
      }
      

      for (int j = 0; j < numYTiles; j++)
      {
        if (j == numYTiles - 1) {
          height = (im.getHeight() - y);
        }
        
        Rectangle encodedRect = stripRect.intersection(imageBounds);
        
        Raster strip = im.getData(encodedRect);
        

        byte[] bdata = ((DataBufferByte)strip.getDataBuffer()).getData();
        


        int rowStride = variant == 52 ? ((MultiPixelPackedSampleModel)strip.getSampleModel()).getScanlineStride() : ((ComponentSampleModel)strip.getSampleModel()).getScanlineStride();
        


        if ((rowStride == bytesPerRow) && (!isPBMInverted))
        {
          output.write(bdata, 0, bdata.length);
        }
        else {
          int offset = 0;
          for (int i = 0; i < height; i++) {
            if (isPBMInverted) {
              for (int k = 0; k < bytesPerRow; k++) {
                invertedData[k] = ((byte)(bdata[(offset + k)] & 0xFF ^ 0xFFFFFFFF));
              }
              
              output.write(invertedData, 0, bytesPerRow);
            } else {
              output.write(bdata, offset, bytesPerRow);
            }
            offset += rowStride;
          }
        }
        

        y += tileHeight;
      }
      

      output.flush();
      
      return;
    }
    

    int[] pixels = new int[8 * width * numBands];
    


    byte[] bpixels = reds == null ? new byte[8 * width * numBands] : new byte[8 * width * 3];
    




    int count = 0;
    


    int lastRow = minY + height;
    for (int row = minY; row < lastRow; row += 8) {
      int rows = Math.min(8, lastRow - row);
      int size = rows * width * numBands;
      

      Raster src = im.getData(new Rectangle(minX, row, width, rows));
      src.getPixels(minX, row, width, rows, pixels);
      

      if (isPBMInverted) {
        for (int k = 0; k < size; k++) {
          pixels[k] ^= 0x1;
        }
      }
      
      switch (variant) {
      case 49: 
      case 50: 
        for (int i = 0; i < size; i++) {
          if (count++ % 16 == 0) {
            output.write(lineSeparator);
          } else {
            output.write(32);
          }
          writeInteger(output, pixels[i]);
        }
        output.write(lineSeparator);
        break;
      
      case 51: 
        if (reds == null) {
          for (int i = 0; i < size; i++) {
            if (count++ % 16 == 0) {
              output.write(lineSeparator);
            } else {
              output.write(32);
            }
            writeInteger(output, pixels[i]);
          }
          
        } else {
          for (int i = 0; i < size; i++) {
            if (count++ % 16 == 0) {
              output.write(lineSeparator);
            } else {
              output.write(32);
            }
            writeInteger(output, reds[pixels[i]] & 0xFF);
            output.write(32);
            writeInteger(output, greens[pixels[i]] & 0xFF);
            output.write(32);
            writeInteger(output, blues[pixels[i]] & 0xFF);
          }
        }
        output.write(lineSeparator);
        break;
      

      case 52: 
        int kdst = 0;
        int ksrc = 0;
        for (int i = 0; i < size / 8; i++) {
          int b = pixels[(ksrc++)] << 7 | pixels[(ksrc++)] << 6 | pixels[(ksrc++)] << 5 | pixels[(ksrc++)] << 4 | pixels[(ksrc++)] << 3 | pixels[(ksrc++)] << 2 | pixels[(ksrc++)] << 1 | pixels[(ksrc++)];
          






          bpixels[(kdst++)] = ((byte)b);
        }
        

        if (size % 8 > 0) {
          int b = 0;
          for (int i = 0; i < size % 8; i++) {
            b |= pixels[(size + i)] << 7 - i;
          }
          bpixels[(kdst++)] = ((byte)b);
        }
        output.write(bpixels, 0, (size + 7) / 8);
        
        break;
      
      case 53: 
        for (int i = 0; i < size; i++) {
          bpixels[i] = ((byte)pixels[i]);
        }
        output.write(bpixels, 0, size);
        break;
      
      case 54: 
        if (reds == null) {
          for (int i = 0; i < size; i++) {
            bpixels[i] = ((byte)(pixels[i] & 0xFF));
          }
        } else {
          int i = 0; for (int j = 0; i < size; i++) {
            bpixels[(j++)] = reds[pixels[i]];
            bpixels[(j++)] = greens[pixels[i]];
            bpixels[(j++)] = blues[pixels[i]];
          }
        }
        output.write(bpixels, 0, bpixels.length);
      }
      
    }
    

    output.flush();
  }
  
  private void writeInteger(OutputStream output, int i) throws IOException
  {
    output.write(Integer.toString(i).getBytes());
  }
  
  private void writeByte(OutputStream output, byte b) throws IOException
  {
    output.write(Byte.toString(b).getBytes());
  }
  
  private boolean isRaw(int v)
  {
    return v >= 52;
  }
}
