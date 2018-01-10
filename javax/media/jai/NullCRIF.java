package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;



























































public class NullCRIF
  extends CRIFImpl
{
  private static RenderedImage sourcelessImage = null;
  






  public NullCRIF() {}
  






  public static final synchronized void setSourcelessImage(RenderedImage im)
  {
    sourcelessImage = im;
  }
  





  public static final synchronized RenderedImage getSourcelessImage()
  {
    return sourcelessImage;
  }
  








  public RenderedImage create(ParameterBlock args, RenderingHints renderHints)
  {
    return args.getNumSources() == 0 ? getSourcelessImage() : args.getRenderedSource(0);
  }
}
