package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.io.OutputStream;
import javax.media.jai.util.ImagingListener;























public class EncodeRIF
  implements RenderedImageFactory
{
  public EncodeRIF() {}
  
  public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints)
  {
    ImagingListener listener = ImageUtil.getImagingListener(renderHints);
    

    OutputStream stream = (OutputStream)paramBlock.getObjectParameter(0);
    

    String format = (String)paramBlock.getObjectParameter(1);
    

    ImageEncodeParam param = null;
    if (paramBlock.getNumParameters() > 2) {
      param = (ImageEncodeParam)paramBlock.getObjectParameter(2);
    }
    

    ImageEncoder encoder = ImageCodec.createImageEncoder(format, stream, param);
    


    if (encoder == null) {
      throw new RuntimeException(JaiI18N.getString("EncodeRIF0"));
    }
    

    RenderedImage im = (RenderedImage)paramBlock.getSource(0);
    try {
      encoder.encode(im);
      stream.flush();

    }
    catch (IOException e)
    {
      String message = JaiI18N.getString("EncodeRIF1") + " " + format;
      listener.errorOccurred(message, e, this, false);
      
      return null;
    }
    
    return im;
  }
}
