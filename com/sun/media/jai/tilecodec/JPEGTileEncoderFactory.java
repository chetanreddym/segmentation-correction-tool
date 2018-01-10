package com.sun.media.jai.tilecodec;

import java.awt.image.SampleModel;
import java.io.OutputStream;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCollection;
import javax.media.jai.remote.NegotiableNumericRange;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;
















































public class JPEGTileEncoderFactory
  implements TileEncoderFactory
{
  public JPEGTileEncoderFactory() {}
  
  public TileEncoder createEncoder(OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel)
  {
    if (output == null)
      throw new IllegalArgumentException(JaiI18N.getString("TileEncoder0"));
    int nbands = sampleModel.getNumBands();
    if ((nbands != 1) && (nbands != 3) && (nbands != 4)) {
      throw new IllegalArgumentException(JaiI18N.getString("JPEGTileEncoder0"));
    }
    
    if (sampleModel.getDataType() != 0) {
      throw new IllegalArgumentException(JaiI18N.getString("JPEGTileEncoder1"));
    }
    
    return new JPEGTileEncoder(output, paramList);
  }
  




  public NegotiableCapability getEncodeCapability()
  {
    Vector generators = new Vector();
    generators.add(JPEGTileEncoderFactory.class);
    
    ParameterListDescriptor jpegPld = JAI.getDefaultInstance().getOperationRegistry().getDescriptor("tileEncoder", "jpeg").getParameterListDescriptor("tileEncoder");
    

    Class[] paramClasses = { NegotiableNumericRange.class, NegotiableCollection.class, NegotiableNumericRange.class, NegotiableCollection.class, NegotiableCollection.class, NegotiableCollection.class };
    















    String[] paramNames = { "quality", "qualitySet", "restartInterval", "writeImageInfo", "writeTableInfo", "writeJFIFHeader" };
    









    Vector v = new Vector();
    v.add(new Boolean(true));
    v.add(new Boolean(false));
    NegotiableCollection negCollection = new NegotiableCollection(v);
    
    NegotiableNumericRange nnr1 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[0]));
    


    NegotiableNumericRange nnr2 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[2]));
    



    Object[] defaults = { nnr1, negCollection, nnr2, negCollection, negCollection, negCollection };
    







    NegotiableCapability encodeCap = new NegotiableCapability("tileCodec", "jpeg", generators, new ParameterListDescriptorImpl(null, paramNames, paramClasses, defaults, null), false);
    











    encodeCap.setParameter(paramNames[0], nnr1);
    encodeCap.setParameter(paramNames[1], negCollection);
    encodeCap.setParameter(paramNames[2], nnr2);
    encodeCap.setParameter(paramNames[3], negCollection);
    encodeCap.setParameter(paramNames[4], negCollection);
    encodeCap.setParameter(paramNames[5], negCollection);
    
    return encodeCap;
  }
}
