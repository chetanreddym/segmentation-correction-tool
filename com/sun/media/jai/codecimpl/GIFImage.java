package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.Point;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

















































































































































































class GIFImage
  extends SimpleRenderedImage
{
  private static final int[] INTERLACE_INCREMENT = { 8, 8, 4, 2, -1 };
  private static final int[] INTERLACE_OFFSET = { 0, 4, 2, 1, -1 };
  

  private SeekableStream input;
  

  private boolean interlaceFlag = false;
  

  private byte[] block = new byte['ÿ'];
  private int blockLength = 0;
  private int bitPos = 0;
  private int nextByte = 0;
  
  private int initCodeSize;
  
  private int clearCode;
  private int eofCode;
  private int bitsLeft;
  private int next32Bits = 0;
  


  private boolean lastBlockFound = false;
  

  private int interlacePass = 0;
  

  private WritableRaster theTile = null;
  
  private void skipBlocks() throws IOException
  {
    for (;;) {
      int length = input.readUnsignedByte();
      if (length == 0) {
        break;
      }
      input.skipBytes(length);
    }
  }
  









  GIFImage(SeekableStream input, byte[] globalColorTable)
    throws IOException
  {
    this.input = input;
    
    byte[] localColorTable = null;
    boolean transparentColorFlag = false;
    int transparentColorIndex = 0;
    


    try
    {
      long startPosition = input.getFilePointer();
      for (;;) {
        int blockType = input.readUnsignedByte();
        if (blockType == 44)
        {
          input.skipBytes(4);
          
          width = input.readUnsignedShortLE();
          height = input.readUnsignedShortLE();
          
          int idPackedFields = input.readUnsignedByte();
          boolean localColorTableFlag = (idPackedFields & 0x80) != 0;
          
          interlaceFlag = ((idPackedFields & 0x40) != 0);
          int numLCTEntries = 1 << (idPackedFields & 0x7) + 1;
          
          if (localColorTableFlag)
          {
            localColorTable = new byte[3 * numLCTEntries];
            
            input.readFully(localColorTable); break;
          }
          localColorTable = null;
          


          break; }
        if (blockType == 33) {
          int label = input.readUnsignedByte();
          
          if (label == 249) {
            input.read();
            int gcePackedFields = input.readUnsignedByte();
            transparentColorFlag = (gcePackedFields & 0x1) != 0;
            

            input.skipBytes(2);
            
            transparentColorIndex = input.readUnsignedByte();
            

            input.read();
          } else if (label == 1)
          {
            input.skipBytes(13);
            
            skipBlocks();
          } else if (label == 254)
          {
            skipBlocks();
          } else if (label == 255)
          {
            input.skipBytes(12);
            
            skipBlocks();
          }
          else {
            int length = 0;
            do {
              length = input.readUnsignedByte();
              input.skipBytes(length);
            } while (length > 0);
          }
        } else {
          throw new IOException(JaiI18N.getString("GIFImage0") + " " + blockType + "!");
        }
      }
    }
    catch (IOException ioe) {
      throw new IOException(JaiI18N.getString("GIFImage1"));
    }
    



    minX = (this.minY = this.tileGridXOffset = this.tileGridYOffset = 0);
    

    tileWidth = width;
    tileHeight = height;
    byte[] colorTable;
    byte[] colorTable;
    if (localColorTable != null) {
      colorTable = localColorTable;
    } else {
      colorTable = globalColorTable;
    }
    

    int length = colorTable.length / 3;
    int bits;
    int bits; if (length == 2) {
      bits = 1; } else { int bits;
      if (length == 4) {
        bits = 2; } else { int bits;
        if ((length == 8) || (length == 16))
        {
          bits = 4;
        }
        else
          bits = 8;
      } }
    int lutLength = 1 << bits;
    byte[] r = new byte[lutLength];
    byte[] g = new byte[lutLength];
    byte[] b = new byte[lutLength];
    

    int rgbIndex = 0;
    for (int i = 0; i < length; i++) {
      r[i] = colorTable[(rgbIndex++)];
      g[i] = colorTable[(rgbIndex++)];
      b[i] = colorTable[(rgbIndex++)];
    }
    
    int[] bitsPerSample = new int[1];
    bitsPerSample[0] = bits;
    
    sampleModel = new PixelInterleavedSampleModel(0, width, height, 1, width, new int[] { 0 });
    




    if (!transparentColorFlag) {
      if (ImageCodec.isIndicesForGrayscale(r, g, b)) {
        colorModel = ImageCodec.createComponentColorModel(sampleModel);
      } else
        colorModel = new IndexColorModel(bits, r.length, r, g, b);
    } else {
      colorModel = new IndexColorModel(bits, r.length, r, g, b, transparentColorIndex);
    }
  }
  


  private void initNext32Bits()
  {
    next32Bits = (block[0] & 0xFF);
    next32Bits |= (block[1] & 0xFF) << 8;
    next32Bits |= (block[2] & 0xFF) << 16;
    next32Bits |= block[3] << 24;
    nextByte = 4;
  }
  


  private int getCode(int codeSize, int codeMask)
    throws IOException
  {
    if (bitsLeft <= 0) {
      return eofCode;
    }
    
    int code = next32Bits >> bitPos & codeMask;
    bitPos += codeSize;
    bitsLeft -= codeSize;
    

    while ((bitPos >= 8) && (!lastBlockFound)) {
      next32Bits >>>= 8;
      bitPos -= 8;
      

      if (nextByte >= blockLength)
      {
        blockLength = input.readUnsignedByte();
        if (blockLength == 0) {
          lastBlockFound = true;
          if (bitsLeft < 0) {
            return eofCode;
          }
          return code;
        }
        int left = blockLength;
        int off = 0;
        while (left > 0) {
          int nbytes = input.read(block, off, left);
          off += nbytes;
          left -= nbytes;
        }
        
        bitsLeft += (blockLength << 3);
        nextByte = 0;
      }
      

      next32Bits |= block[(nextByte++)] << 24;
    }
    
    return code;
  }
  


  private void initializeStringTable(int[] prefix, byte[] suffix, byte[] initial, int[] length)
  {
    int numEntries = 1 << initCodeSize;
    for (int i = 0; i < numEntries; i++) {
      prefix[i] = -1;
      suffix[i] = ((byte)i);
      initial[i] = ((byte)i);
      length[i] = 1;
    }
    


    for (int i = numEntries; i < 4096; i++) {
      prefix[i] = -1;
      length[i] = 1;
    }
  }
  


  private Point outputPixels(byte[] string, int len, Point streamPos, byte[] rowBuf)
  {
    if ((interlacePass < 0) || (interlacePass > 3)) {
      return streamPos;
    }
    
    for (int i = 0; i < len; i++) {
      if (x >= minX) {
        rowBuf[(x - minX)] = string[i];
      }
      

      x += 1;
      if (x == width) {
        theTile.setDataElements(minX, y, width, 1, rowBuf);
        
        x = 0;
        if (interlaceFlag) {
          y += INTERLACE_INCREMENT[interlacePass];
          if (y >= height) {
            interlacePass += 1;
            if (interlacePass > 3) {
              return streamPos;
            }
            y = INTERLACE_OFFSET[interlacePass];
          }
        } else {
          y += 1;
        }
      }
    }
    
    return streamPos;
  }
  



  public synchronized Raster getTile(int tileX, int tileY)
  {
    if ((tileX != 0) || (tileY != 0)) {
      throw new IllegalArgumentException(JaiI18N.getString("GIFImage2"));
    }
    

    if (theTile != null) {
      return theTile;
    }
    

    theTile = WritableRaster.createWritableRaster(sampleModel, sampleModel.createDataBuffer(), null);
    




    Point streamPos = new Point(0, 0);
    

    byte[] rowBuf = new byte[width];
    
    try
    {
      initCodeSize = input.readUnsignedByte();
      

      blockLength = input.readUnsignedByte();
      int left = blockLength;
      int off = 0;
      while (left > 0) {
        int nbytes = input.read(block, off, left);
        left -= nbytes;
        off += nbytes;
      }
      
      bitPos = 0;
      nextByte = 0;
      lastBlockFound = false;
      bitsLeft = (blockLength << 3);
      

      initNext32Bits();
      
      clearCode = (1 << initCodeSize);
      eofCode = (clearCode + 1);
      
      int oldCode = 0;
      
      int[] prefix = new int['က'];
      byte[] suffix = new byte['က'];
      byte[] initial = new byte['က'];
      int[] length = new int['က'];
      byte[] string = new byte['က'];
      
      initializeStringTable(prefix, suffix, initial, length);
      int tableIndex = (1 << initCodeSize) + 2;
      int codeSize = initCodeSize + 1;
      int codeMask = (1 << codeSize) - 1;
      for (;;)
      {
        int code = getCode(codeSize, codeMask);
        WritableRaster localWritableRaster;
        if (code == clearCode) {
          initializeStringTable(prefix, suffix, initial, length);
          tableIndex = (1 << initCodeSize) + 2;
          codeSize = initCodeSize + 1;
          codeMask = (1 << codeSize) - 1;
          code = getCode(codeSize, codeMask);
          if (code == eofCode) {
            localWritableRaster = theTile;
            












































            return theTile;
          }
        }
        else
        {
          if (code == eofCode) {
            localWritableRaster = theTile;
            









































            return theTile;
          }
          int newSuffixIndex;
          int newSuffixIndex;
          if (code < tableIndex) {
            newSuffixIndex = code;
          } else {
            newSuffixIndex = oldCode;
          }
          
          int ti = tableIndex;
          int oc = oldCode;
          
          prefix[ti] = oc;
          suffix[ti] = initial[newSuffixIndex];
          initial[ti] = initial[oc];
          length[oc] += 1;
          
          tableIndex++;
          if ((tableIndex == 1 << codeSize) && (tableIndex < 4096))
          {
            codeSize++;
            codeMask = (1 << codeSize) - 1;
          }
        }
        

        int c = code;
        int len = length[c];
        for (int i = len - 1; i >= 0; i--) {
          string[i] = suffix[c];
          c = prefix[c];
        }
        
        outputPixels(string, len, streamPos, rowBuf);
        oldCode = code;
      }
      


      String message;
      


      return theTile;
    }
    catch (IOException e)
    {
      e = e;
      message = JaiI18N.getString("GIFImage3");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
      



      return theTile;
    } finally {}
  }
  
  public void dispose() {
    theTile = null;
  }
}
