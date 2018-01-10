package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codec.SeekableOutputStream;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.OutputStream;



























public class BMPImageEncoder
  extends ImageEncoderImpl
{
  private OutputStream output;
  private int version;
  private boolean isCompressed;
  private boolean isTopDown;
  private int w;
  private int h;
  private int compImageSize = 0;
  






  public BMPImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    
    this.output = output;
    BMPEncodeParam bmpParam;
    BMPEncodeParam bmpParam;
    if (param == null)
    {
      bmpParam = new BMPEncodeParam();
    } else {
      bmpParam = (BMPEncodeParam)param;
    }
    
    version = bmpParam.getVersion();
    isCompressed = bmpParam.isCompressed();
    if ((isCompressed) && (!(output instanceof SeekableOutputStream))) {
      throw new IllegalArgumentException(JaiI18N.getString("BMPImageEncoder6"));
    }
    

    isTopDown = bmpParam.isTopDown();
  }
  




  public void encode(RenderedImage im)
    throws IOException
  {
    int minX = im.getMinX();
    int minY = im.getMinY();
    w = im.getWidth();
    h = im.getHeight();
    

    int bitsPerPixel = 24;
    boolean isPalette = false;
    int paletteEntries = 0;
    IndexColorModel icm = null;
    
    SampleModel sm = im.getSampleModel();
    int numBands = sm.getNumBands();
    
    ColorModel cm = im.getColorModel();
    
    if ((numBands != 1) && (numBands != 3)) {
      throw new IllegalArgumentException(JaiI18N.getString("BMPImageEncoder1"));
    }
    

    int[] sampleSize = sm.getSampleSize();
    if (sampleSize[0] > 8) {
      throw new RuntimeException(JaiI18N.getString("BMPImageEncoder2"));
    }
    
    for (int i = 1; i < sampleSize.length; i++) {
      if (sampleSize[i] != sampleSize[0]) {
        throw new RuntimeException(JaiI18N.getString("BMPImageEncoder3"));
      }
    }
    


    int dataType = sm.getTransferType();
    if ((dataType != 0) && (!CodecUtils.isPackedByteImage(im)))
    {
      throw new RuntimeException(JaiI18N.getString("BMPImageEncoder0"));
    }
    

    int destScanlineBytes = w * numBands;
    int compression = 0;
    

    byte[] r = null;byte[] g = null;byte[] b = null;byte[] a = null;
    
    if ((cm instanceof IndexColorModel))
    {
      isPalette = true;
      icm = (IndexColorModel)cm;
      paletteEntries = icm.getMapSize();
      
      if (paletteEntries <= 2)
      {
        bitsPerPixel = 1;
        destScanlineBytes = (int)Math.ceil(w / 8.0D);
      }
      else if (paletteEntries <= 16)
      {
        bitsPerPixel = 4;
        destScanlineBytes = (int)Math.ceil(w / 2.0D);
      }
      else if (paletteEntries <= 256)
      {
        bitsPerPixel = 8;

      }
      else
      {

        bitsPerPixel = 24;
        isPalette = false;
        paletteEntries = 0;
        destScanlineBytes = w * 3;
      }
      
      if (isPalette == true)
      {
        r = new byte[paletteEntries];
        g = new byte[paletteEntries];
        b = new byte[paletteEntries];
        a = new byte[paletteEntries];
        
        icm.getAlphas(a);
        icm.getReds(r);
        icm.getGreens(g);
        icm.getBlues(b);

      }
      

    }
    else if (numBands == 1)
    {
      isPalette = true;
      paletteEntries = 256;
      
      bitsPerPixel = sampleSize[0];
      
      destScanlineBytes = (int)Math.ceil(w * bitsPerPixel / 8.0D);
      

      r = new byte['Ā'];
      g = new byte['Ā'];
      b = new byte['Ā'];
      a = new byte['Ā'];
      
      for (int i = 0; i < 256; i++) {
        r[i] = ((byte)i);
        g[i] = ((byte)i);
        b[i] = ((byte)i);
        

        a[i] = -1;
      }
    } else if ((sm instanceof SinglePixelPackedSampleModel)) {
      bitsPerPixel = DataBuffer.getDataTypeSize(sm.getDataType());
      destScanlineBytes = w * bitsPerPixel + 7 >> 3;
    }
    


    int fileSize = 0;
    int offset = 0;
    int headerSize = 0;
    int imageSize = 0;
    int xPelsPerMeter = 0;
    int yPelsPerMeter = 0;
    int colorsUsed = 0;
    int colorsImportant = paletteEntries;
    int padding = 0;
    

    int remainder = destScanlineBytes % 4;
    if (remainder != 0) {
      padding = 4 - remainder;
    }
    
    switch (version) {
    case 0: 
      offset = 26 + paletteEntries * 3;
      headerSize = 12;
      imageSize = (destScanlineBytes + padding) * h;
      fileSize = imageSize + offset;
      throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
    




    case 1: 
      if ((isCompressed) && (bitsPerPixel == 8)) {
        compression = 1;
      } else if ((isCompressed) && (bitsPerPixel == 4)) {
        compression = 2;
      }
      offset = 54 + paletteEntries * 4;
      
      imageSize = (destScanlineBytes + padding) * h;
      fileSize = imageSize + offset;
      headerSize = 40;
      break;
    
    case 2: 
      headerSize = 108;
      throw new RuntimeException(JaiI18N.getString("BMPImageEncoder5"));
    }
    
    

    int redMask = 0;int blueMask = 0;int greenMask = 0;
    if ((cm instanceof DirectColorModel)) {
      redMask = ((DirectColorModel)cm).getRedMask();
      greenMask = ((DirectColorModel)cm).getGreenMask();
      blueMask = ((DirectColorModel)cm).getBlueMask();
      destScanlineBytes = w;
      compression = 3;
      fileSize += 12;
      offset += 12;
    }
    
    writeFileHeader(fileSize, offset);
    
    writeInfoHeader(headerSize, bitsPerPixel);
    

    writeDWord(compression);
    

    writeDWord(imageSize);
    

    writeDWord(xPelsPerMeter);
    

    writeDWord(yPelsPerMeter);
    

    writeDWord(colorsUsed);
    

    writeDWord(colorsImportant);
    
    if (compression == 3) {
      writeDWord(redMask);
      writeDWord(greenMask);
      writeDWord(blueMask);
    }
    
    if (compression == 3) {
      for (int i = 0; i < h; i++) {
        int row = minY + i;
        
        if (!isTopDown) {
          row = minY + h - i - 1;
        }
        
        Rectangle srcRect = new Rectangle(minX, row, w, 1);
        
        Raster src = im.getData(srcRect);
        
        SampleModel sm1 = src.getSampleModel();
        int pos = 0;
        int startX = x - src.getSampleModelTranslateX();
        int startY = y - src.getSampleModelTranslateY();
        if ((sm1 instanceof SinglePixelPackedSampleModel)) {
          SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm1;
          
          pos = sppsm.getOffset(startX, startY);
        }
        
        switch (dataType) {
        case 2: 
          short[] sdata = ((DataBufferShort)src.getDataBuffer()).getData();
          
          for (int m = 0; m < sdata.length; m++)
            writeWord(sdata[m]);
          break;
        
        case 1: 
          short[] usdata = ((DataBufferUShort)src.getDataBuffer()).getData();
          
          for (int m = 0; m < usdata.length; m++)
            writeWord(usdata[m]);
          break;
        
        case 3: 
          int[] idata = ((DataBufferInt)src.getDataBuffer()).getData();
          
          for (int m = 0; m < idata.length; m++) {
            writeDWord(idata[m]);
          }
        }
      }
      return;
    }
    

    if (isPalette == true)
    {

      switch (version)
      {


      case 0: 
        for (int i = 0; i < paletteEntries; i++) {
          output.write(b[i]);
          output.write(g[i]);
          output.write(r[i]);
        }
        break;
      


      default: 
        for (int i = 0; i < paletteEntries; i++) {
          output.write(b[i]);
          output.write(g[i]);
          output.write(r[i]);
          output.write(a[i]);
        }
      }
      
    }
    



    int scanlineBytes = w * numBands;
    

    int[] pixels = new int[8 * scanlineBytes];
    


    byte[] bpixels = new byte[destScanlineBytes];
    


    if (!isTopDown)
    {

      int lastRow = minY + h;
      
      for (int row = lastRow - 1; row >= minY; row -= 8)
      {
        int rows = Math.min(8, row - minY + 1);
        

        Raster src = im.getData(new Rectangle(minX, row - rows + 1, w, rows));
        

        src.getPixels(minX, row - rows + 1, w, rows, pixels);
        
        int l = 0;
        

        int max = scanlineBytes * rows - 1;
        
        for (int i = 0; i < rows; i++)
        {

          l = max - (i + 1) * scanlineBytes + 1;
          
          writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
        }
        
      }
      

    }
    else
    {

      int lastRow = minY + h;
      
      for (int row = minY; row < lastRow; row += 8) {
        int rows = Math.min(8, lastRow - row);
        

        Raster src = im.getData(new Rectangle(minX, row, w, rows));
        
        src.getPixels(minX, row, w, rows, pixels);
        
        int l = 0;
        for (int i = 0; i < rows; i++)
        {
          writePixels(l, scanlineBytes, bitsPerPixel, pixels, bpixels, padding, numBands, icm);
        }
      }
    }
    



    if ((isCompressed) && ((bitsPerPixel == 4) || (bitsPerPixel == 8)))
    {
      output.write(0);
      output.write(1);
      incCompImageSize(2);
      
      imageSize = compImageSize;
      fileSize = compImageSize + offset;
      writeSize(fileSize, 2);
      writeSize(imageSize, 34);
    }
  }
  




  private void writePixels(int l, int scanlineBytes, int bitsPerPixel, int[] pixels, byte[] bpixels, int padding, int numBands, IndexColorModel icm)
    throws IOException
  {
    int pixel = 0;
    int k = 0;
    switch (bitsPerPixel)
    {

    case 1: 
      for (int j = 0; j < scanlineBytes / 8; j++) {
        bpixels[(k++)] = ((byte)(pixels[(l++)] << 7 | pixels[(l++)] << 6 | pixels[(l++)] << 5 | pixels[(l++)] << 4 | pixels[(l++)] << 3 | pixels[(l++)] << 2 | pixels[(l++)] << 1 | pixels[(l++)]));
      }
      








      if (scanlineBytes % 8 > 0) {
        pixel = 0;
        for (int j = 0; j < scanlineBytes % 8; j++) {
          pixel |= pixels[(l++)] << 7 - j;
        }
        bpixels[(k++)] = ((byte)pixel);
      }
      output.write(bpixels, 0, (scanlineBytes + 7) / 8);
      
      break;
    
    case 4: 
      if (isCompressed) {
        byte[] bipixels = new byte[scanlineBytes];
        for (int h = 0; h < scanlineBytes; h++) {
          bipixels[h] = ((byte)pixels[(l++)]);
        }
        encodeRLE4(bipixels, scanlineBytes);
      } else {
        for (int j = 0; j < scanlineBytes / 2; j++) {
          pixel = pixels[(l++)] << 4 | pixels[(l++)];
          bpixels[(k++)] = ((byte)pixel);
        }
        
        if (scanlineBytes % 2 == 1) {
          pixel = pixels[l] << 4;
          bpixels[(k++)] = ((byte)pixel);
        }
        output.write(bpixels, 0, (scanlineBytes + 1) / 2);
      }
      break;
    
    case 8: 
      if (isCompressed) {
        for (int h = 0; h < scanlineBytes; h++) {
          bpixels[h] = ((byte)pixels[(l++)]);
        }
        encodeRLE8(bpixels, scanlineBytes);
      } else {
        for (int j = 0; j < scanlineBytes; j++) {
          bpixels[j] = ((byte)pixels[(l++)]);
        }
        output.write(bpixels, 0, scanlineBytes);
      }
      break;
    
    case 24: 
      if (numBands == 3) {
        for (int j = 0; j < scanlineBytes; j += 3)
        {
          bpixels[(k++)] = ((byte)pixels[(l + 2)]);
          bpixels[(k++)] = ((byte)pixels[(l + 1)]);
          bpixels[(k++)] = ((byte)pixels[l]);
          l += 3;
        }
        output.write(bpixels, 0, scanlineBytes);
      }
      else {
        int entries = icm.getMapSize();
        
        byte[] r = new byte[entries];
        byte[] g = new byte[entries];
        byte[] b = new byte[entries];
        
        icm.getReds(r);
        icm.getGreens(g);
        icm.getBlues(b);
        

        for (int j = 0; j < scanlineBytes; j++) {
          int index = pixels[l];
          bpixels[(k++)] = b[index];
          bpixels[(k++)] = g[index];
          bpixels[(k++)] = b[index];
          l++;
        }
        output.write(bpixels, 0, scanlineBytes * 3);
      }
      
      break;
    }
    
    
    if ((!isCompressed) || ((bitsPerPixel != 8) && (bitsPerPixel != 4))) {
      for (k = 0; k < padding; k++) {
        output.write(0);
      }
    }
  }
  
  private void encodeRLE8(byte[] bpixels, int scanlineBytes)
    throws IOException
  {
    int runCount = 1;int absVal = -1;int j = -1;
    byte runVal = 0;byte nextVal = 0;
    
    runVal = bpixels[(++j)];
    byte[] absBuf = new byte['Ā'];
    
    while (j < scanlineBytes - 1) {
      nextVal = bpixels[(++j)];
      if (nextVal == runVal) {
        if (absVal >= 3)
        {
          output.write(0);
          output.write(absVal);
          incCompImageSize(2);
          for (int a = 0; a < absVal; a++) {
            output.write(absBuf[a]);
            incCompImageSize(1);
          }
          if (!isEven(absVal))
          {
            output.write(0);
            incCompImageSize(1);
          }
        }
        else if (absVal > -1)
        {



          for (int b = 0; b < absVal; b++) {
            output.write(1);
            output.write(absBuf[b]);
            incCompImageSize(2);
          }
        }
        absVal = -1;
        runCount++;
        if (runCount == 256)
        {
          output.write(runCount - 1);
          output.write(runVal);
          incCompImageSize(2);
          runCount = 1;
        }
      }
      else {
        if (runCount > 1)
        {
          output.write(runCount);
          output.write(runVal);
          incCompImageSize(2);
        } else if (absVal < 0)
        {
          absBuf[(++absVal)] = runVal;
          absBuf[(++absVal)] = nextVal;
        } else if (absVal < 254)
        {
          absBuf[(++absVal)] = nextVal;
        } else {
          output.write(0);
          output.write(absVal + 1);
          incCompImageSize(2);
          for (int a = 0; a <= absVal; a++) {
            output.write(absBuf[a]);
            incCompImageSize(1);
          }
          
          output.write(0);
          incCompImageSize(1);
          absVal = -1;
        }
        runVal = nextVal;
        runCount = 1;
      }
      
      if (j == scanlineBytes - 1)
      {
        if (absVal == -1) {
          output.write(runCount);
          output.write(runVal);
          incCompImageSize(2);
          runCount = 1;


        }
        else if (absVal >= 2) {
          output.write(0);
          output.write(absVal + 1);
          incCompImageSize(2);
          for (int a = 0; a <= absVal; a++) {
            output.write(absBuf[a]);
            incCompImageSize(1);
          }
          if (!isEven(absVal + 1))
          {
            output.write(0);
            incCompImageSize(1);
          }
          
        }
        else if (absVal > -1) {
          for (int b = 0; b <= absVal; b++) {
            output.write(1);
            output.write(absBuf[b]);
            incCompImageSize(2);
          }
        }
        


        output.write(0);
        output.write(0);
        incCompImageSize(2);
      }
    }
  }
  
  private void encodeRLE4(byte[] bipixels, int scanlineBytes)
    throws IOException
  {
    int runCount = 2;int absVal = -1;int j = -1;int pixel = 0;int q = 0;
    byte runVal1 = 0;byte runVal2 = 0;byte nextVal1 = 0;byte nextVal2 = 0;
    byte[] absBuf = new byte['Ā'];
    

    runVal1 = bipixels[(++j)];
    runVal2 = bipixels[(++j)];
    
    while (j < scanlineBytes - 2) {
      nextVal1 = bipixels[(++j)];
      nextVal2 = bipixels[(++j)];
      
      if (nextVal1 == runVal1)
      {

        if (absVal >= 4) {
          output.write(0);
          output.write(absVal - 1);
          incCompImageSize(2);
          

          for (int a = 0; a < absVal - 2; a += 2) {
            pixel = absBuf[a] << 4 | absBuf[(a + 1)];
            output.write((byte)pixel);
            incCompImageSize(1);
          }
          
          if (!isEven(absVal - 1)) {
            q = absBuf[(absVal - 2)] << 4 | 0x0;
            output.write(q);
            incCompImageSize(1);
          }
          
          if (!isEven((int)Math.ceil((absVal - 1) / 2))) {
            output.write(0);
            incCompImageSize(1);
          }
        } else if (absVal > -1) {
          output.write(2);
          pixel = absBuf[0] << 4 | absBuf[1];
          output.write(pixel);
          incCompImageSize(2);
        }
        absVal = -1;
        
        if (nextVal2 == runVal2)
        {
          runCount += 2;
          if (runCount == 256) {
            output.write(runCount - 1);
            pixel = runVal1 << 4 | runVal2;
            output.write(pixel);
            incCompImageSize(2);
            runCount = 2;
            if (j < scanlineBytes - 1) {
              runVal1 = runVal2;
              runVal2 = bipixels[(++j)];
            } else {
              output.write(1);
              int r = runVal2 << 4 | 0x0;
              output.write(r);
              incCompImageSize(2);
              runCount = -1;
            }
            
          }
        }
        else
        {
          runCount++;
          pixel = runVal1 << 4 | runVal2;
          output.write(runCount);
          output.write(pixel);
          incCompImageSize(2);
          runCount = 2;
          runVal1 = nextVal2;
          
          if (j < scanlineBytes - 1) {
            runVal2 = bipixels[(++j)];
          } else {
            output.write(1);
            int r = nextVal2 << 4 | 0x0;
            output.write(r);
            incCompImageSize(2);
            runCount = -1;
          }
        }
      }
      else
      {
        if (runCount > 2) {
          pixel = runVal1 << 4 | runVal2;
          output.write(runCount);
          output.write(pixel);
          incCompImageSize(2);
        } else if (absVal < 0) {
          absBuf[(++absVal)] = runVal1;
          absBuf[(++absVal)] = runVal2;
          absBuf[(++absVal)] = nextVal1;
          absBuf[(++absVal)] = nextVal2;
        } else if (absVal < 253) {
          absBuf[(++absVal)] = nextVal1;
          absBuf[(++absVal)] = nextVal2;
        } else {
          output.write(0);
          output.write(absVal + 1);
          incCompImageSize(2);
          for (int a = 0; a < absVal; a += 2) {
            pixel = absBuf[a] << 4 | absBuf[(a + 1)];
            output.write((byte)pixel);
            incCompImageSize(1);
          }
          

          output.write(0);
          incCompImageSize(1);
          absVal = -1;
        }
        
        runVal1 = nextVal1;
        runVal2 = nextVal2;
        runCount = 2;
      }
      
      if (j >= scanlineBytes - 2) {
        if ((absVal == -1) && (runCount >= 2)) {
          if (j == scanlineBytes - 2) {
            if (bipixels[(++j)] == runVal1) {
              runCount++;
              pixel = runVal1 << 4 | runVal2;
              output.write(runCount);
              output.write(pixel);
              incCompImageSize(2);
            } else {
              pixel = runVal1 << 4 | runVal2;
              output.write(runCount);
              output.write(pixel);
              output.write(1);
              pixel = bipixels[j] << 4 | 0x0;
              output.write(pixel);
              int n = bipixels[j] << 4 | 0x0;
              incCompImageSize(4);
            }
          } else {
            output.write(runCount);
            pixel = runVal1 << 4 | runVal2;
            output.write(pixel);
            incCompImageSize(2);
          }
        } else if (absVal > -1) {
          if (j == scanlineBytes - 2) {
            absBuf[(++absVal)] = bipixels[(++j)];
          }
          if (absVal >= 2) {
            output.write(0);
            output.write(absVal + 1);
            incCompImageSize(2);
            for (int a = 0; a < absVal; a += 2) {
              pixel = absBuf[a] << 4 | absBuf[(a + 1)];
              output.write((byte)pixel);
              incCompImageSize(1);
            }
            if (!isEven(absVal + 1)) {
              q = absBuf[absVal] << 4 | 0x0;
              output.write(q);
              incCompImageSize(1);
            }
            

            if (!isEven((int)Math.ceil((absVal + 1) / 2))) {
              output.write(0);
              incCompImageSize(1);
            }
          }
          else {
            switch (absVal) {
            case 0: 
              output.write(1);
              int n = absBuf[0] << 4 | 0x0;
              output.write(n);
              incCompImageSize(2);
              break;
            case 1: 
              output.write(2);
              pixel = absBuf[0] << 4 | absBuf[1];
              output.write(pixel);
              incCompImageSize(2);
            }
            
          }
        }
        
        output.write(0);
        output.write(0);
        incCompImageSize(2);
      }
    }
  }
  
  private synchronized void incCompImageSize(int value)
  {
    compImageSize += value;
  }
  
  private boolean isEven(int number) { return number % 2 == 0; }
  
  private void writeFileHeader(int fileSize, int offset) throws IOException
  {
    output.write(66);
    output.write(77);
    

    writeDWord(fileSize);
    

    output.write(0);
    output.write(0);
    output.write(0);
    output.write(0);
    

    writeDWord(offset);
  }
  


  private void writeInfoHeader(int headerSize, int bitsPerPixel)
    throws IOException
  {
    writeDWord(headerSize);
    

    writeDWord(w);
    

    writeDWord(h);
    

    writeWord(1);
    

    writeWord(bitsPerPixel);
  }
  
  public void writeWord(int word) throws IOException
  {
    output.write(word & 0xFF);
    output.write((word & 0xFF00) >> 8);
  }
  
  public void writeDWord(int dword) throws IOException {
    output.write(dword & 0xFF);
    output.write((dword & 0xFF00) >> 8);
    output.write((dword & 0xFF0000) >> 16);
    output.write((dword & 0xFF000000) >> 24);
  }
  
  private void writeSize(int dword, int offset) throws IOException { ((SeekableOutputStream)output).seek(offset);
    writeDWord(dword);
  }
}
