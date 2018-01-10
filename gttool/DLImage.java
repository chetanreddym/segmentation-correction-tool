package gttool;

import java.awt.image.RenderedImage;
import ocr.tif.JAIImageReader;








public class DLImage
{
  RenderedImage im = null;
  
  public DLImage(String filename)
  {
    im = JAIImageReader.readImage(filename, false);
  }
  

  public int getWidth()
  {
    return im.getWidth();
  }
  


  public int getHeight()
  {
    return im.getHeight();
  }
  

  public RenderedImage dlGetImageData()
  {
    return im;
  }
}
