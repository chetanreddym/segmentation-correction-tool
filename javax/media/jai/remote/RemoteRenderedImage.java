package javax.media.jai.remote;

import java.awt.image.RenderedImage;

public abstract interface RemoteRenderedImage
  extends RenderedImage
{
  public abstract String getServerName();
  
  public abstract String getProtocolName();
  
  public abstract int getRetryInterval();
  
  public abstract void setRetryInterval(int paramInt);
  
  public abstract int getNumRetries();
  
  public abstract void setNumRetries(int paramInt);
  
  public abstract NegotiableCapabilitySet getNegotiationPreferences();
  
  public abstract void setNegotiationPreferences(NegotiableCapabilitySet paramNegotiableCapabilitySet);
  
  public abstract NegotiableCapabilitySet getNegotiatedValues()
    throws RemoteImagingException;
  
  public abstract NegotiableCapability getNegotiatedValue(String paramString)
    throws RemoteImagingException;
  
  public abstract void setServerNegotiatedValues(NegotiableCapabilitySet paramNegotiableCapabilitySet)
    throws RemoteImagingException;
}
