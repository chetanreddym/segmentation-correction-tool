package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;






























public class GIFImageDecoder
  extends ImageDecoderImpl
{
  private byte[] globalColorTable = null;
  

  private boolean maxPageFound = false;
  

  private int maxPage;
  

  private int prevPage = -1;
  

  private int prevSyncedPage = -1;
  

  private HashMap images = new HashMap();
  


  private static byte[] readHeader(SeekableStream input)
    throws IOException
  {
    byte[] globalColorTable = null;
    try
    {
      input.skipBytes(10);
      
      int packedFields = input.readUnsignedByte();
      boolean globalColorTableFlag = (packedFields & 0x80) != 0;
      int numGCTEntries = 1 << (packedFields & 0x7) + 1;
      
      int backgroundColorIndex = input.readUnsignedByte();
      

      input.read();
      
      if (globalColorTableFlag) {
        globalColorTable = new byte[3 * numGCTEntries];
        input.readFully(globalColorTable);
      } else {
        globalColorTable = null;
      }
    } catch (IOException e) {
      String message = JaiI18N.getString("GIFImageDecoder0");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), GIFImageDecoder.class, false);
    }
    



    return globalColorTable;
  }
  
  public GIFImageDecoder(SeekableStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public GIFImageDecoder(InputStream input, ImageDecodeParam param)
  {
    super(input, param);
  }
  
  public int getNumPages() throws IOException {
    int page = prevPage + 1;
    
    while (!maxPageFound) {
      try {
        decodeAsRenderedImage(page++);
      }
      catch (IOException e) {}
    }
    

    return maxPage + 1;
  }
  

  public synchronized RenderedImage decodeAsRenderedImage(int page)
    throws IOException
  {
    if ((page < 0) || ((maxPageFound) && (page > maxPage))) {
      throw new IOException(JaiI18N.getString("GIFImageDecoder1"));
    }
    

    Integer pageKey = new Integer(page);
    if (images.containsKey(pageKey)) {
      return (RenderedImage)images.get(pageKey);
    }
    

    if (prevPage == -1) {
      try {
        globalColorTable = readHeader(input);
      } catch (IOException e) {
        maxPageFound = true;
        maxPage = -1;
        throw e;
      }
    }
    

    if (page > 0) {
      for (int idx = prevSyncedPage + 1; idx < page; idx++) {
        RenderedImage im = (RenderedImage)images.get(new Integer(idx));
        
        im.getTile(0, 0);
        prevSyncedPage = idx;
      }
    }
    

    RenderedImage image = null;
    while (prevPage < page) {
      int index = prevPage + 1;
      RenderedImage ri = null;
      try {
        ri = new GIFImage(input, globalColorTable);
        images.put(new Integer(index), ri);
        if (index < page) {
          ri.getTile(0, 0);
          prevSyncedPage = index;
        }
        prevPage = index;
        if (index == page) {
          image = ri;
          break;
        }
      } catch (IOException e) {
        maxPageFound = true;
        maxPage = prevPage;
        String message = JaiI18N.getString("GIFImage3");
        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
      }
    }
    



    return image;
  }
}
