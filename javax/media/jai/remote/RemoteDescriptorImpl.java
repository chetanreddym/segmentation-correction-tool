package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PropertyGenerator;










































public abstract class RemoteDescriptorImpl
  implements RemoteDescriptor
{
  protected String protocolName;
  protected URL serverNameDocURL;
  
  public RemoteDescriptorImpl(String protocolName, URL serverNameDocURL)
  {
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    
    this.protocolName = protocolName;
    this.serverNameDocURL = serverNameDocURL;
  }
  




  public String getName()
  {
    return protocolName;
  }
  







  public String[] getSupportedModes()
  {
    return new String[] { "remoteRendered", "remoteRenderable" };
  }
  













  public boolean isModeSupported(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
    }
    

    if ((modeName.equalsIgnoreCase("remoteRendered")) || (modeName.equalsIgnoreCase("remoteRenderable")))
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
      throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
    }
    

    throw new UnsupportedOperationException(JaiI18N.getString("RemoteDescriptorImpl2"));
  }
  





  public URL getServerNameDocs()
  {
    return serverNameDocURL;
  }
  

























































  public Object getInvalidRegion(String registryModeName, String oldServerName, ParameterBlock oldParamBlock, RenderingHints oldHints, String newServerName, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node)
    throws RemoteImagingException
  {
    return null;
  }
  










  public ParameterListDescriptor getParameterListDescriptor(String modeName)
  {
    if (modeName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteDescriptorImpl1"));
    }
    

    return null;
  }
}
