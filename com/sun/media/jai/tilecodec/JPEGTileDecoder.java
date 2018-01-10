package com.sun.media.jai.tilecodec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGQTable;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RasterFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderImpl;
import javax.media.jai.util.ImagingListener;















public class JPEGTileDecoder
  extends TileDecoderImpl
{
  private TileCodecDescriptor tcd = null;
  


















  public JPEGTileDecoder(InputStream input, TileCodecParameterList param)
  {
    super("jpeg", input, param);
    tcd = TileCodecUtils.getTileCodecDescriptor("tileDecoder", "jpeg");
  }
  











  public Raster decode()
    throws IOException
  {
    if (!tcd.includesLocationInfo()) {
      throw new IllegalArgumentException(JaiI18N.getString("JPEGTileDecoder0"));
    }
    return decode(null);
  }
  
  public Raster decode(Point location) throws IOException {
    SampleModel sm = null;
    byte[] data = null;
    
    ObjectInputStream ois = new ObjectInputStream(inputStream);
    
    try
    {
      paramList.setParameter("quality", ois.readFloat());
      paramList.setParameter("qualitySet", ois.readBoolean());
      sm = TileCodecUtils.deserializeSampleModel(ois.readObject());
      location = (Point)ois.readObject();
      data = (byte[])ois.readObject();
    }
    catch (ClassNotFoundException e) {
      ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
      
      listener.errorOccurred(JaiI18N.getString("ClassNotFound"), e, this, false);
      

      return null;
    }
    finally {
      ois.close();
    }
    
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(bais);
    
    Object ras = decoder.decodeAsRaster().createTranslatedChild(x, y);
    
    extractParameters(decoder.getJPEGDecodeParam(), ((Raster)ras).getSampleModel().getNumBands());
    


    if (sm != null) {
      int minX = ((Raster)ras).getMinX();
      int minY = ((Raster)ras).getMinY();
      int h = ((Raster)ras).getHeight();
      int w = ((Raster)ras).getWidth();
      double[] buf = ((Raster)ras).getPixels(minX, minY, w, h, (double[])null);
      ras = RasterFactory.createWritableRaster(sm, new Point(minX, minY));
      
      ((WritableRaster)ras).setPixels(minX, minY, w, h, buf);
    }
    return ras;
  }
  

  private void extractParameters(JPEGDecodeParam jdp, int bandNum)
  {
    int[] horizontalSubsampling = new int[bandNum];
    for (int i = 0; i < bandNum; i++)
      horizontalSubsampling[i] = jdp.getHorizontalSubsampling(i);
    paramList.setParameter("horizontalSubsampling", horizontalSubsampling);
    

    int[] verticalSubsampling = new int[bandNum];
    for (int i = 0; i < bandNum; i++)
      verticalSubsampling[i] = jdp.getVerticalSubsampling(i);
    paramList.setParameter("verticalSubsampling", verticalSubsampling);
    


    if (!paramList.getBooleanParameter("qualitySet")) {
      for (int i = 0; i < 4; i++) {
        JPEGQTable table = jdp.getQTable(i);
        paramList.setParameter("quantizationTable" + i, table == null ? null : table.getTable());
      }
    }
    else {
      ParameterListDescriptor pld = paramList.getParameterListDescriptor();
      
      for (int i = 0; i < 4; i++) {
        paramList.setParameter("quantizationTable" + i, pld.getParamDefaultValue("quantizationTable" + i));
      }
    }
    


    int[] quanTableMapping = new int[bandNum];
    for (int i = 0; i < bandNum; i++)
      quanTableMapping[i] = jdp.getQTableComponentMapping(i);
    paramList.setParameter("quantizationTableMapping", quanTableMapping);
    

    paramList.setParameter("writeTableInfo", jdp.isTableInfoValid());
    paramList.setParameter("writeImageInfo", jdp.isImageInfoValid());
    

    paramList.setParameter("restartInterval", jdp.getRestartInterval());
    

    paramList.setParameter("writeJFIFHeader", jdp.getMarker(224));
  }
}
