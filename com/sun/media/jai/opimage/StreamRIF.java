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
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.TileCache;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;



























public class StreamRIF
  implements RenderedImageFactory
{
  public StreamRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImagingListener listener = ImageUtil.getImagingListener(renderHints);
    SeekableStream src = (SeekableStream)paramBlock.getObjectParameter(0);
    try {
      src.seek(0L);
    } catch (IOException e) {
      listener.errorOccurred(JaiI18N.getString("StreamRIF0"), e, this, false);
      

      return null;
    }
    
    ImageDecodeParam param = null;
    if (paramBlock.getNumParameters() > 1) {
      param = (ImageDecodeParam)paramBlock.getObjectParameter(1);
    }
    
    String[] names = ImageCodec.getDecoderNames(src);
    
    OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
    
    int bound = 2;
    ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
    
    if (renderHints != null)
    {

      RenderingHints.Key key = JAI.KEY_OPERATION_REGISTRY;
      if (renderHints.containsKey(key)) {
        registry = (OperationRegistry)renderHints.get(key);
      }
      
      key = JAI.KEY_OPERATION_BOUND;
      if (renderHints.containsKey(key)) {
        bound = ((Integer)renderHints.get(key)).intValue();
      }
    }
    

    for (int i = 0; i < names.length; i++) {
      RenderedImageFactory rif = null;
      try {
        rif = RIFRegistry.get(registry, names[i]);
      }
      catch (IllegalArgumentException iae) {}
      
      if (rif != null) {
        RenderedImage im = RIFRegistry.create(registry, names[i], paramBlock, renderHints);
        
        if (im != null) {
          return im;
        }
      }
    }
    



    boolean canAttemptRecovery = src.canSeekBackwards();
    

    long streamPosition = Long.MIN_VALUE;
    if (canAttemptRecovery) {
      try {
        streamPosition = src.getFilePointer();
      } catch (IOException ioe) {
        listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, this, false);
        


        canAttemptRecovery = false;
      }
    }
    

    for (int i = 0; i < names.length; i++) {
      ImageDecoder dec = ImageCodec.createImageDecoder(names[i], src, param);
      
      RenderedImage im = null;
      try {
        im = dec.decodeAsRenderedImage();

      }
      catch (OutOfMemoryError memoryError)
      {
        if (canAttemptRecovery)
        {
          TileCache cache = RIFUtil.getTileCacheHint(renderHints);
          if (cache != null) {
            cache.flush();
          }
          

          System.gc();
          
          try
          {
            src.seek(streamPosition);
            

            im = dec.decodeAsRenderedImage();
          } catch (IOException ioe) {
            listener.errorOccurred(JaiI18N.getString("StreamRIF2"), ioe, this, false);
            
            im = null;
          }
        } else {
          String message = JaiI18N.getString("CodecRIFUtil0");
          listener.errorOccurred(message, new ImagingException(message, memoryError), this, false);
        }
        

      }
      catch (IOException e)
      {

        listener.errorOccurred(JaiI18N.getString("StreamRIF2"), e, this, false);
        
        im = null;
      }
      

      if (im != null) {
        return new DisposableNullOpImage(im, layout, renderHints, bound);
      }
    }
    

    return null;
  }
}
