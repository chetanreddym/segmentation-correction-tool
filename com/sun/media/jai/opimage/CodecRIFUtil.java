package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.util.DisposableNullOpImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.TileCache;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;












public class CodecRIFUtil
{
  private CodecRIFUtil() {}
  
  public static RenderedImage create(String type, ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImagingListener listener = ImageUtil.getImagingListener(renderHints);
    
    SeekableStream source = (SeekableStream)paramBlock.getObjectParameter(0);
    

    ImageDecodeParam param = null;
    if (paramBlock.getNumParameters() > 1) {
      param = (ImageDecodeParam)paramBlock.getObjectParameter(1);
    }
    int page = 0;
    if (paramBlock.getNumParameters() > 2) {
      page = paramBlock.getIntParameter(2);
    }
    
    ImageDecoder dec = ImageCodec.createImageDecoder(type, source, param);
    try {
      int bound = 2;
      ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
      
      if (renderHints != null)
      {

        RenderingHints.Key key = JAI.KEY_OPERATION_BOUND;
        if (renderHints.containsKey(key)) {
          bound = ((Integer)renderHints.get(key)).intValue();
        }
      }
      



      boolean canAttemptRecovery = source.canSeekBackwards();
      

      long streamPosition = Long.MIN_VALUE;
      if (canAttemptRecovery) {
        try {
          streamPosition = source.getFilePointer();
        } catch (IOException ioe) {
          listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, CodecRIFUtil.class, false);
          


          canAttemptRecovery = false;
        }
      }
      
      OpImage image = null;
      try
      {
        image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, renderHints, bound);


      }
      catch (OutOfMemoryError memoryError)
      {


        if (canAttemptRecovery)
        {
          TileCache cache = image != null ? image.getTileCache() : RIFUtil.getTileCacheHint(renderHints);
          

          if (cache != null) {
            cache.flush();
          }
          

          System.gc();
          

          source.seek(streamPosition);
          

          image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, renderHints, bound);

        }
        else
        {

          String message = JaiI18N.getString("CodecRIFUtil0");
          listener.errorOccurred(message, new ImagingException(message, memoryError), CodecRIFUtil.class, false);
        }
      }
      




      return image;
    } catch (Exception e) {
      listener.errorOccurred(JaiI18N.getString("CodecRIFUtil1"), e, CodecRIFUtil.class, false);
    }
    
    return null;
  }
}
