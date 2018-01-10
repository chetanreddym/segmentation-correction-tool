package javax.media.jai.iterator;

import com.sun.media.jai.iterator.RookIterFallback;
import com.sun.media.jai.iterator.WrapperRI;
import com.sun.media.jai.iterator.WrapperWRI;
import com.sun.media.jai.iterator.WritableRookIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;











































public class RookIterFactory
{
  private RookIterFactory() {}
  
  public static RookIter create(RenderedImage im, Rectangle bounds)
  {
    if (bounds == null) {
      bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
    }
    

    SampleModel sm = im.getSampleModel();
    if ((sm instanceof ComponentSampleModel)) {
      switch (sm.getDataType())
      {
      }
      
    }
    










    return new RookIterFallback(im, bounds);
  }
  










  public static RookIter create(Raster ras, Rectangle bounds)
  {
    RenderedImage im = new WrapperRI(ras);
    return create(im, bounds);
  }
  










  public static WritableRookIter createWritable(WritableRenderedImage im, Rectangle bounds)
  {
    if (bounds == null) {
      bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
    }
    

    SampleModel sm = im.getSampleModel();
    if ((sm instanceof ComponentSampleModel)) {
      switch (sm.getDataType())
      {
      }
      
    }
    










    return new WritableRookIterFallback(im, bounds);
  }
  










  public static WritableRookIter createWritable(WritableRaster ras, Rectangle bounds)
  {
    WritableRenderedImage im = new WrapperWRI(ras);
    return createWritable(im, bounds);
  }
}
