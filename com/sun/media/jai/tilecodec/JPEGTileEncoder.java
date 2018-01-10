package com.sun.media.jai.tilecodec;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGQTable;
import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoderImpl;
import sun.awt.image.codec.JPEGParam;


















public class JPEGTileEncoder
  extends TileEncoderImpl
{
  private TileCodecDescriptor tcd = null;
  



















  public JPEGTileEncoder(OutputStream output, TileCodecParameterList param)
  {
    super("jpeg", output, param);
    tcd = TileCodecUtils.getTileCodecDescriptor("tileEncoder", "jpeg");
  }
  








  public void encode(Raster ras)
    throws IOException
  {
    if (ras == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileEncoder1"));
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    SampleModel sm = ras.getSampleModel();
    
    JPEGEncodeParam j2dEP = convertToJ2DJPEGEncodeParam(paramList, sm);
    ((JPEGParam)j2dEP).setWidth(ras.getWidth());
    ((JPEGParam)j2dEP).setHeight(ras.getHeight());
    
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos, j2dEP);
    encoder.encode(ras);
    
    byte[] data = baos.toByteArray();
    
    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    oos.writeFloat(paramList.getFloatParameter("quality"));
    oos.writeBoolean(paramList.getBooleanParameter("qualitySet"));
    oos.writeObject(TileCodecUtils.serializeSampleModel(sm));
    
    Point location = new Point(ras.getMinX(), ras.getMinY());
    oos.writeObject(location);
    
    oos.writeObject(data);
    oos.close();
  }
  

  private JPEGEncodeParam convertToJ2DJPEGEncodeParam(TileCodecParameterList paramList, SampleModel sm)
  {
    if (sm == null) {
      return null;
    }
    int nbands = sm.getNumBands();
    
    JPEGParam j2dJP = createDefaultJ2DJPEGEncodeParam(nbands);
    
    int[] hSubSamp = (int[])paramList.getObjectParameter("horizontalSubsampling");
    
    int[] vSubSamp = (int[])paramList.getObjectParameter("verticalSubsampling");
    
    int[] qTabSlot = (int[])paramList.getObjectParameter("quantizationTableMapping");
    

    for (int i = 0; i < nbands; i++) {
      j2dJP.setHorizontalSubsampling(i, hSubSamp[i]);
      j2dJP.setVerticalSubsampling(i, vSubSamp[i]);
      
      int[] qTab = (int[])paramList.getObjectParameter("quantizationTable" + i);
      
      if ((qTab != null) && (qTab.equals(ParameterListDescriptor.NO_PARAMETER_DEFAULT)))
      {
        j2dJP.setQTableComponentMapping(i, qTabSlot[i]);
        j2dJP.setQTable(qTabSlot[i], new JPEGQTable(qTab));
      }
    }
    
    if (paramList.getBooleanParameter("qualitySet")) {
      float quality = paramList.getFloatParameter("quality");
      j2dJP.setQuality(quality, true);
    }
    
    int rInt = paramList.getIntParameter("restartInterval");
    j2dJP.setRestartInterval(rInt);
    
    j2dJP.setImageInfoValid(paramList.getBooleanParameter("writeImageInfo"));
    j2dJP.setTableInfoValid(paramList.getBooleanParameter("writeTableInfo"));
    
    if (paramList.getBooleanParameter("writeJFIFHeader")) {
      j2dJP.setMarkerData(224, (byte[][])null);
    }
    
    return j2dJP;
  }
  
  private JPEGParam createDefaultJ2DJPEGEncodeParam(int nbands) {
    if (nbands == 1)
      return new JPEGParam(1, 1);
    if (nbands == 3)
      return new JPEGParam(3, 3);
    if (nbands == 4)
      return new JPEGParam(4, 4);
    return null;
  }
}
