package javax.media.jai.tilecodec;

import com.sun.media.jai.tilecodec.TileCodecUtils;
import java.awt.image.SampleModel;
import java.io.InputStream;
import javax.media.jai.ParameterListDescriptor;








































































public abstract class TileDecoderImpl
  implements TileDecoder
{
  protected String formatName;
  protected InputStream inputStream;
  protected TileCodecParameterList paramList;
  
  public TileDecoderImpl(String formatName, InputStream input, TileCodecParameterList param)
  {
    if (formatName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
    }
    

    if (input == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl0"));
    }
    

    TileCodecDescriptor tcd = TileCodecUtils.getTileCodecDescriptor("tileDecoder", formatName);
    


    if (param == null) {
      param = tcd.getDefaultParameters("tileDecoder");
    }
    if (param != null)
    {


      if (!param.getFormatName().equalsIgnoreCase(formatName)) {
        throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl1"));
      }
      



      if (!param.isValidForMode("tileDecoder")) {
        throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl2"));
      }
      


      if (!param.getParameterListDescriptor().equals(tcd.getParameterListDescriptor("tileDecoder")))
      {
        throw new IllegalArgumentException(JaiI18N.getString("TileCodec0"));
      }
      SampleModel sm = null;
      

      if (!tcd.includesSampleModelInfo()) {
        try {
          sm = (SampleModel)param.getObjectParameter("sampleModel");
        }
        catch (IllegalArgumentException iae)
        {
          throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl3"));
        }
        

        if ((sm == null) || (sm == ParameterListDescriptor.NO_PARAMETER_DEFAULT))
        {

          if (tcd.getParameterListDescriptor("tileDecoder").getParamDefaultValue("sampleModel") == null)
          {


            throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl4"));
          }
          
        }
        
      }
      
    }
    else
    {
      ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileDecoder");
      


      if (!tcd.includesSampleModelInfo())
      {
        throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl5"));
      }
      




      if ((pld != null) && (pld.getNumParameters() != 0)) {
        throw new IllegalArgumentException(JaiI18N.getString("TileDecoderImpl6"));
      }
    }
    

    this.formatName = formatName;
    inputStream = input;
    paramList = param;
  }
  


  public String getFormatName()
  {
    return formatName;
  }
  



  public TileCodecParameterList getDecodeParameterList()
  {
    return paramList;
  }
  



  public InputStream getInputStream()
  {
    return inputStream;
  }
}