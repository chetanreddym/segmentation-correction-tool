package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import javax.media.jai.RegistryElementDescriptor;

public abstract interface TileCodecDescriptor
  extends RegistryElementDescriptor
{
  public abstract boolean includesSampleModelInfo();
  
  public abstract boolean includesLocationInfo();
  
  public abstract TileCodecParameterList getDefaultParameters(String paramString);
  
  public abstract TileCodecParameterList getDefaultParameters(String paramString, SampleModel paramSampleModel);
  
  public abstract TileCodecParameterList getCompatibleParameters(String paramString, TileCodecParameterList paramTileCodecParameterList);
}
