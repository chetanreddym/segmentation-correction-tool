package com.sun.media.jai.tilecodec;

import java.io.InputStream;
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
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;
























































public class JPEGTileDecoderFactory
  implements TileDecoderFactory
{
  public JPEGTileDecoderFactory() {}
  
  public TileDecoder createDecoder(InputStream input, TileCodecParameterList param)
  {
    if (input == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileDecoder0"));
    }
    return new JPEGTileDecoder(input, param);
  }
  




  public NegotiableCapability getDecodeCapability()
  {
    Vector generators = new Vector();
    generators.add(JPEGTileDecoderFactory.class);
    
    ParameterListDescriptor jpegPld = JAI.getDefaultInstance().getOperationRegistry().getDescriptor("tileDecoder", "jpeg").getParameterListDescriptor("tileDecoder");
    
    Class[] paramClasses = { NegotiableNumericRange.class, NegotiableCollection.class, NegotiableNumericRange.class, NegotiableCollection.class, NegotiableCollection.class, NegotiableCollection.class };
    
















    String[] paramNames = { "quality", "qualitySet", "restartInterval", "writeImageInfo", "writeTableInfo", "writeJFIFHeader" };
    









    Vector v = new Vector();
    v.add(new Boolean(true));
    v.add(new Boolean(false));
    NegotiableCollection negCollection = new NegotiableCollection(v);
    
    NegotiableNumericRange nnr1 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[0]));
    


    NegotiableNumericRange nnr2 = new NegotiableNumericRange(jpegPld.getParamValueRange(paramNames[2]));
    



    Object[] defaults = { nnr1, negCollection, nnr2, negCollection, negCollection, negCollection };
    







    NegotiableCapability decodeCap = new NegotiableCapability("tileCodec", "jpeg", generators, new ParameterListDescriptorImpl(null, paramNames, paramClasses, defaults, null), false);
    











    decodeCap.setParameter(paramNames[0], nnr1);
    decodeCap.setParameter(paramNames[1], negCollection);
    decodeCap.setParameter(paramNames[2], nnr2);
    decodeCap.setParameter(paramNames[3], negCollection);
    decodeCap.setParameter(paramNames[4], negCollection);
    decodeCap.setParameter(paramNames[5], negCollection);
    
    return decodeCap;
  }
}
