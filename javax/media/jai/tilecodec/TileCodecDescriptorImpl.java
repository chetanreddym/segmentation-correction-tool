package javax.media.jai.tilecodec;

import javax.media.jai.PropertyGenerator;









































public abstract class TileCodecDescriptorImpl
  implements TileCodecDescriptor
{
  private String formatName;
  private boolean includesSMInfo;
  private boolean includesLocationInfo;
  
  public TileCodecDescriptorImpl(String formatName, boolean includesSampleModelInfo, boolean includesLocationInfo)
  {
    if (formatName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
    }
    

    this.formatName = formatName;
    includesSMInfo = includesSampleModelInfo;
    this.includesLocationInfo = includesLocationInfo;
  }
  


  public String getName()
  {
    return formatName;
  }
  









  public String[] getSupportedModes()
  {
    return new String[] { "tileDecoder", "tileEncoder" };
  }
  










  public boolean isModeSupported(String registryModeName)
  {
    if (registryModeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
    }
    

    if ((registryModeName.equalsIgnoreCase("tileDecoder") == true) || (registryModeName.equalsIgnoreCase("tileEncoder") == true))
    {
      return true;
    }
    
    return false;
  }
  








  public boolean arePropertiesSupported()
  {
    return false;
  }
  












  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
    }
    

    throw new UnsupportedOperationException(JaiI18N.getString("TileCodecDescriptorImpl2"));
  }
  




  public boolean includesSampleModelInfo()
  {
    return includesSMInfo;
  }
  



  public boolean includesLocationInfo()
  {
    return includesLocationInfo;
  }
}
