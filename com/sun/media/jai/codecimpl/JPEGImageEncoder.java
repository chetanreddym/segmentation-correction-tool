package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGQTable;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoderImpl;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;




























public class JPEGImageEncoder
  extends ImageEncoderImpl
{
  private com.sun.media.jai.codec.JPEGEncodeParam jaiEP = null;
  
  public JPEGImageEncoder(OutputStream output, ImageEncodeParam param)
  {
    super(output, param);
    if (param != null) {
      jaiEP = ((com.sun.media.jai.codec.JPEGEncodeParam)param);
    }
  }
  









  static void modifyEncodeParam(com.sun.media.jai.codec.JPEGEncodeParam jaiEP, com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP, int nbands)
  {
    for (int i = 0; i < nbands; i++)
    {


      int val = jaiEP.getHorizontalSubsampling(i);
      j2dEP.setHorizontalSubsampling(i, val);
      
      val = jaiEP.getVerticalSubsampling(i);
      j2dEP.setVerticalSubsampling(i, val);
      



      if (jaiEP.isQTableSet(i)) {
        int[] qTab = jaiEP.getQTable(i);
        val = jaiEP.getQTableSlot(i);
        j2dEP.setQTableComponentMapping(i, val);
        j2dEP.setQTable(val, new JPEGQTable(qTab));
      }
    }
    

    if (jaiEP.isQualitySet()) {
      float fval = jaiEP.getQuality();
      j2dEP.setQuality(fval, true);
    }
    

    int val = jaiEP.getRestartInterval();
    j2dEP.setRestartInterval(val);
    

    if (jaiEP.getWriteTablesOnly() == true) {
      j2dEP.setImageInfoValid(false);
      j2dEP.setTableInfoValid(true);
    }
    

    if (jaiEP.getWriteImageOnly() == true) {
      j2dEP.setTableInfoValid(false);
      j2dEP.setImageInfoValid(true);
    }
    

    if (!jaiEP.getWriteJFIFHeader()) {
      j2dEP.setMarkerData(224, (byte[][])null);
    }
  }
  








  public void encode(RenderedImage im)
    throws IOException
  {
    SampleModel sampleModel = im.getSampleModel();
    ColorModel colorModel = im.getColorModel();
    

    int numBands = colorModel.getNumColorComponents();
    int transType = sampleModel.getTransferType();
    if (((transType != 0) && (!CodecUtils.isPackedByteImage(im))) || ((numBands != 1) && (numBands != 3)))
    {

      throw new RuntimeException(JaiI18N.getString("JPEGImageEncoder0"));
    }
    

    int cspaceType = colorModel.getColorSpace().getType();
    if ((cspaceType != 6) && (cspaceType != 5))
    {
      throw new RuntimeException(JaiI18N.getString("JPEGImageEncoder1"));
    }
    

    BufferedImage bi;
    
    BufferedImage bi;
    
    if ((im instanceof BufferedImage)) {
      bi = (BufferedImage)im;
    }
    else
    {
      Raster ras;
      



      Raster ras;
      


      if ((im.getNumXTiles() == 1) && (im.getNumYTiles() == 1))
      {
        ras = im.getTile(im.getMinTileX(), im.getMinTileY());



      }
      else
      {


        WritableRaster target = sampleModel.getSampleSize(0) == 8 ? Raster.createInterleavedRaster(0, im.getWidth(), im.getHeight(), sampleModel.getNumBands(), new Point(im.getMinX(), im.getMinY())) : null;
        








        ras = im.copyData(target);
      }
      
      WritableRaster wRas;
      WritableRaster wRas;
      if ((ras instanceof WritableRaster)) {
        wRas = (WritableRaster)ras;
      } else {
        wRas = Raster.createWritableRaster(ras.getSampleModel(), ras.getDataBuffer(), new Point(ras.getSampleModelTranslateX(), ras.getSampleModelTranslateY()));
      }
      







      if ((wRas.getMinX() != 0) || (wRas.getMinY() != 0) || (wRas.getWidth() != im.getWidth()) || (wRas.getHeight() != im.getHeight()))
      {

        wRas = wRas.createWritableChild(wRas.getMinX(), wRas.getMinY(), im.getWidth(), im.getHeight(), 0, 0, null);
      }
      




      bi = new BufferedImage(colorModel, wRas, false, null);
    }
    
    if ((colorModel instanceof IndexColorModel))
    {



      IndexColorModel icm = (IndexColorModel)colorModel;
      bi = icm.convertToIntDiscrete(bi.getRaster(), false);
      
      if (bi.getSampleModel().getNumBands() == 4)
      {



        WritableRaster rgbaRas = bi.getRaster();
        WritableRaster rgbRas = rgbaRas.createWritableChild(0, 0, bi.getWidth(), bi.getHeight(), 0, 0, new int[] { 0, 1, 2 });
        








        PackedColorModel pcm = (PackedColorModel)bi.getColorModel();
        int bits = pcm.getComponentSize(0) + pcm.getComponentSize(1) + pcm.getComponentSize(2);
        


        DirectColorModel dcm = new DirectColorModel(bits, pcm.getMask(0), pcm.getMask(1), pcm.getMask(2));
        


        bi = new BufferedImage(dcm, rgbRas, false, null);
      }
    }
    

    com.sun.image.codec.jpeg.JPEGEncodeParam j2dEP = JPEGCodec.getDefaultJPEGEncodeParam(bi);
    



    if (jaiEP != null) {
      modifyEncodeParam(jaiEP, j2dEP, numBands);
    }
    


    com.sun.image.codec.jpeg.JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output, j2dEP);
    

    try
    {
      encoder.encode(bi);
    } catch (IOException e) {
      String message = JaiI18N.getString("JPEGImageEncoder2");
      ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
    }
  }
}
