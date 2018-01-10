package javax.media.jai.tilecodec;

import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListImpl;



























































public class TileCodecParameterList
  extends ParameterListImpl
{
  private String formatName;
  private String[] validModes;
  
  public TileCodecParameterList(String formatName, String[] validModes, ParameterListDescriptor descriptor)
  {
    super(descriptor);
    


    if (formatName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl0"));
    }
    

    if (validModes == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecParameterList0"));
    }
    

    if (descriptor == null) {
      throw new IllegalArgumentException(JaiI18N.getString("TileCodecParameterList1"));
    }
    

    this.formatName = formatName;
    this.validModes = validModes;
  }
  


  public String getFormatName()
  {
    return formatName;
  }
  






  public boolean isValidForMode(String registryModeName)
  {
    for (int i = 0; i < validModes.length; i++) {
      if (validModes[i].equalsIgnoreCase(registryModeName)) {
        return true;
      }
    }
    
    return false;
  }
  



  public String[] getValidModes()
  {
    return (String[])validModes.clone();
  }
}
