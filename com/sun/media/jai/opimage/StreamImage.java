package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedImageAdapter;





























class StreamImage
  extends RenderedImageAdapter
{
  private InputStream stream;
  
  public StreamImage(RenderedImage image, InputStream stream)
  {
    super(image);
    this.stream = stream;
    if ((image instanceof OpImage))
    {

      setProperty("tile_cache_key", image);
      Object tileCache = ((OpImage)image).getTileCache();
      setProperty("tile_cache", tileCache == null ? Image.UndefinedProperty : tileCache);
    }
  }
  


  public void dispose()
  {
    RenderedImage trueSrc = getWrappedImage();
    Method disposeMethod = null;
    try {
      Class cls = trueSrc.getClass();
      disposeMethod = cls.getMethod("dispose", null);
      if (!disposeMethod.isAccessible()) {
        AccessibleObject.setAccessible(new AccessibleObject[] { disposeMethod }, true);
      }
      

      disposeMethod.invoke(trueSrc, null);
    }
    catch (Exception e) {}
  }
  


  protected void finalize()
    throws Throwable
  {
    stream.close();
    super.finalize();
  }
}
