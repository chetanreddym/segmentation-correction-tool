package javax.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterFallback;
import com.sun.media.jai.iterator.WrapperRI;
import com.sun.media.jai.iterator.WrapperWRI;
import com.sun.media.jai.iterator.WritableRandomIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;











































public class RandomIterFactory
{
  private RandomIterFactory() {}
  
  public static RandomIter create(RenderedImage im, Rectangle bounds)
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
    










    return new RandomIterFallback(im, bounds);
  }
  










  public static RandomIter create(Raster ras, Rectangle bounds)
  {
    RenderedImage im = new WrapperRI(ras);
    return create(im, bounds);
  }
  










  public static WritableRandomIter createWritable(WritableRenderedImage im, Rectangle bounds)
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
    










    return new WritableRandomIterFallback(im, bounds);
  }
  










  public static WritableRandomIter createWritable(WritableRaster ras, Rectangle bounds)
  {
    WritableRenderedImage im = new WrapperWRI(ras);
    return createWritable(im, bounds);
  }
}
