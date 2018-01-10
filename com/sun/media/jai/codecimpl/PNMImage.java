package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.util.ImagingException;
import com.sun.media.jai.codecimpl.util.RasterFactory;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;























































class PNMImage
  extends SimpleRenderedImage
{
  private static final int PBM_ASCII = 49;
  private static final int PGM_ASCII = 50;
  private static final int PPM_ASCII = 51;
  private static final int PBM_RAW = 52;
  private static final int PGM_RAW = 53;
  private static final int PPM_RAW = 54;
  private static final int LINE_FEED = 10;
  private SeekableStream input;
  private byte[] lineSeparator;
  private int variant;
  private int maxValue;
  private Raster theTile;
  private int numBands;
  private int dataType;
  
  public PNMImage(SeekableStream input)
  {
    theTile = null;
    
    this.input = input;
    
    String ls = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    
    lineSeparator = ls.getBytes();
    
    try
    {
      if (this.input.read() != 80) {
        throw new RuntimeException(JaiI18N.getString("PNMImageDecoder0"));
      }
      
      variant = this.input.read();
      if ((variant < 49) || (variant > 54)) {
        throw new RuntimeException(JaiI18N.getString("PNMImageDecoder1"));
      }
      
      width = readInteger(this.input);
      height = readInteger(this.input);
      
      if ((variant == 49) || (variant == 52)) {
        maxValue = 1;
      } else {
        maxValue = readInteger(this.input);
      }
    } catch (IOException e) {
      String message = JaiI18N.getString("PNMImageDecoder6");
      sendExceptionToListener(message, e);
    }
    





    if ((isRaw(variant)) && (maxValue >= 256)) {
      maxValue = 255;
    }
    

    tileWidth = width;
    tileHeight = height;
    


    if ((variant == 51) || (variant == 54)) {
      numBands = 3;
    } else {
      numBands = 1;
    }
    

    if (maxValue < 256) {
      dataType = 0;
    } else if (maxValue < 65536) {
      dataType = 1;
    } else {
      dataType = 3;
    }
    

    if ((variant == 49) || (variant == 52))
    {
      sampleModel = new MultiPixelPackedSampleModel(0, width, height, 1);
      
      colorModel = ImageCodec.createGrayIndexColorModel(sampleModel, false);
    }
    else {
      int[] bandOffsets = { 0, 1, numBands == 1 ? new int[] { 0 } : 2 };
      
      sampleModel = RasterFactory.createPixelInterleavedSampleModel(dataType, tileWidth, tileHeight, numBands, tileWidth * numBands, bandOffsets);
      



      colorModel = ImageCodec.createComponentColorModel(sampleModel);
    }
  }
  

  private boolean isRaw(int v)
  {
    return v >= 52;
  }
  
  private int readInteger(SeekableStream in) throws IOException
  {
    int ret = 0;
    boolean foundDigit = false;
    
    int b;
    while ((b = in.read()) != -1) {
      char c = (char)b;
      if (Character.isDigit(c)) {
        ret = ret * 10 + Character.digit(c, 10);
        foundDigit = true;
      }
      else if (c == '#') {
        int length = lineSeparator.length;
        
        while ((b = in.read()) != -1) {
          boolean eol = false;
          for (int i = 0; i < length; i++) {
            if (b == lineSeparator[i]) {
              eol = true;
              break;
            }
          }
          if (eol) {
            break;
          }
        }
        if (b == -1) {
          break;
        }
      } else {
        if (foundDigit) {
          break;
        }
      }
    }
    
    return ret;
  }
  
  private Raster computeTile(int tileX, int tileY)
  {
    Point org = new Point(tileXToX(tileX), tileYToY(tileY));
    WritableRaster tile = Raster.createWritableRaster(sampleModel, org);
    Rectangle tileRect = tile.getBounds();
    
    try
    {
      switch (variant)
      {

      case 49: 
      case 52: 
        DataBuffer dataBuffer = tile.getDataBuffer();
        if (isRaw(variant))
        {
          byte[] buf = ((DataBufferByte)dataBuffer).getData();
          input.readFully(buf, 0, buf.length);
        }
        else {
          byte[] pixels = new byte[8 * width];
          int offset = 0;
          for (int row = 0; row < tileHeight; row += 8) {
            int rows = Math.min(8, tileHeight - row);
            int len = (rows * width + 7) / 8;
            
            for (int i = 0; i < rows * width; i++) {
              pixels[i] = ((byte)readInteger(input));
            }
            sampleModel.setDataElements(x, row, width, rows, pixels, dataBuffer);
          }
        }
        




        break;
      

      case 50: 
      case 51: 
      case 53: 
      case 54: 
        int size = width * height * numBands;
        
        switch (dataType) {
        case 0: 
          DataBufferByte bbuf = (DataBufferByte)tile.getDataBuffer();
          
          byte[] byteArray = bbuf.getData();
          if (isRaw(variant)) {
            input.readFully(byteArray);
          } else {
            for (int i = 0; i < size; i++) {
              byteArray[i] = ((byte)readInteger(input));
            }
          }
          break;
        
        case 1: 
          DataBufferUShort sbuf = (DataBufferUShort)tile.getDataBuffer();
          
          short[] shortArray = sbuf.getData();
          for (int i = 0; i < size; i++) {
            shortArray[i] = ((short)readInteger(input));
          }
          break;
        
        case 3: 
          DataBufferInt ibuf = (DataBufferInt)tile.getDataBuffer();
          
          int[] intArray = ibuf.getData();
          for (int i = 0; i < size; i++) {
            intArray[i] = readInteger(input);
          }
        }
        
        
        break;
      }
      
      input.close();
    } catch (IOException e) {
      String message = JaiI18N.getString("PNMImageDecoder7");
      sendExceptionToListener(message, e);
    }
    


    return tile;
  }
  
  public synchronized Raster getTile(int tileX, int tileY) {
    if ((tileX != 0) || (tileY != 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("PNMImageDecoder4"));
    }
    
    if (theTile == null) {
      theTile = computeTile(tileX, tileY);
    }
    
    return theTile;
  }
  
  public void dispose() {
    theTile = null;
  }
  
  private void sendExceptionToListener(String message, Exception e) {
    ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
  }
}
