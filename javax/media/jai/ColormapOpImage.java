package javax.media.jai;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Map;






















































public abstract class ColormapOpImage
  extends PointOpImage
{
  private boolean isInitialized = false;
  










  private boolean isColormapAccelerated;
  











  public ColormapOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources)
  {
    super(source, layout, configuration, cobbleSources);
    
















    isColormapAccelerated = true;
    Boolean value = configuration == null ? Boolean.TRUE : (Boolean)configuration.get(JAI.KEY_TRANSFORM_ON_COLORMAP);
    

    if (value != null) {
      isColormapAccelerated = value.booleanValue();
    }
  }
  

  protected final boolean isColormapOperation()
  {
    return isColormapAccelerated;
  }
  









  protected final void initializeColormapOperation()
  {
    ColorModel srcCM = getSource(0).getColorModel();
    ColorModel dstCM = super.getColorModel();
    

    isColormapAccelerated &= ((srcCM != null) && (dstCM != null) && ((srcCM instanceof IndexColorModel)) && ((dstCM instanceof IndexColorModel)));
    




    isInitialized = true;
    

    if (isColormapAccelerated)
    {
      IndexColorModel icm = (IndexColorModel)dstCM;
      

      int mapSize = icm.getMapSize();
      byte[][] colormap = new byte[3][mapSize];
      

      icm.getReds(colormap[0]);
      icm.getGreens(colormap[1]);
      icm.getBlues(colormap[2]);
      

      transformColormap(colormap);
      



      for (int b = 0; b < 3; b++) {
        int maxComponent = 255 >> 8 - icm.getComponentSize(b);
        if (maxComponent < 255) {
          byte[] map = colormap[b];
          for (int i = 0; i < mapSize; i++) {
            if ((map[i] & 0xFF) > maxComponent) {
              map[i] = ((byte)maxComponent);
            }
          }
        }
      }
      

      byte[] reds = colormap[0];
      byte[] greens = colormap[1];
      byte[] blues = colormap[2];
      

      int[] rgb = new int[mapSize];
      

      if (icm.hasAlpha()) {
        byte[] alphas = new byte[mapSize];
        icm.getAlphas(alphas);
        for (int i = 0; i < mapSize; i++) {
          rgb[i] = ((alphas[i] & 0xFF) << 24 | (reds[i] & 0xFF) << 16 | (greens[i] & 0xFF) << 8 | blues[i] & 0xFF);
        }
        

      }
      else
      {
        for (int i = 0; i < mapSize; i++) {
          rgb[i] = ((reds[i] & 0xFF) << 16 | (greens[i] & 0xFF) << 8 | blues[i] & 0xFF);
        }
      }
      




      colorModel = new IndexColorModel(icm.getPixelSize(), mapSize, rgb, 0, icm.hasAlpha(), icm.getTransparentPixel(), sampleModel.getTransferType());
    }
  }
  
  protected abstract void transformColormap(byte[][] paramArrayOfByte);
}
