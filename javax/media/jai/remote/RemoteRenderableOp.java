package javax.media.jai.remote;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationNodeSupport;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.registry.RemoteCRIFRegistry;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;


















































public class RemoteRenderableOp
  extends RenderableOp
{
  protected String protocolName;
  protected String serverName;
  private transient RemoteCRIF remoteCRIF = null;
  

  private NegotiableCapabilitySet negotiated = null;
  















  private transient RenderedImage linkToRemoteOp;
  
















  public RemoteRenderableOp(String protocolName, String serverName, String opName, ParameterBlock pb)
  {
    this(null, protocolName, serverName, opName, pb);
  }
  

































  public RemoteRenderableOp(OperationRegistry registry, String protocolName, String serverName, String opName, ParameterBlock pb)
  {
    super(registry, opName, pb);
    
    if ((protocolName == null) || (opName == null)) {
      throw new IllegalArgumentException();
    }
    
    this.protocolName = protocolName;
    this.serverName = serverName;
  }
  





  public String getRegistryModeName()
  {
    return RegistryMode.getMode("remoteRenderable").getName();
  }
  


  public String getServerName()
  {
    return serverName;
  }
  













  public void setServerName(String serverName)
  {
    if (serverName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
    }
    if (serverName.equalsIgnoreCase(this.serverName)) return;
    String oldServerName = this.serverName;
    this.serverName = serverName;
    fireEvent("ServerName", oldServerName, serverName);
    nodeSupport.resetPropertyEnvironment(false);
  }
  



  public String getProtocolName()
  {
    return protocolName;
  }
  













  public void setProtocolName(String protocolName)
  {
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    
    if (protocolName.equalsIgnoreCase(this.protocolName)) { return;
    }
    String oldProtocolName = this.protocolName;
    this.protocolName = protocolName;
    fireEvent("ProtocolName", oldProtocolName, protocolName);
    nodeSupport.resetPropertyEnvironment(false);
  }
  



























  public void setProtocolAndServerNames(String protocolName, String serverName)
  {
    if (serverName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
    }
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    boolean protocolNotChanged = protocolName.equalsIgnoreCase(this.protocolName);
    
    boolean serverNotChanged = serverName.equalsIgnoreCase(this.serverName);
    

    if (protocolNotChanged) {
      if (serverNotChanged)
      {
        return;
      }
      
      setServerName(serverName);
      return;
    }
    
    if (serverNotChanged)
    {
      setProtocolName(protocolName);
      return;
    }
    

    String oldProtocolName = this.protocolName;
    String oldServerName = this.serverName;
    this.protocolName = protocolName;
    this.serverName = serverName;
    

    fireEvent("ProtocolAndServerName", new String[] { oldProtocolName, oldServerName }, new String[] { protocolName, serverName });
    

    nodeSupport.resetPropertyEnvironment(false);
  }
  
  private void fireEvent(String propName, Object oldVal, Object newVal)
  {
    if (eventManager != null) {
      Object eventSource = eventManager.getPropertyChangeEventSource();
      PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
      

      eventManager.firePropertyChange(evt);
    }
  }
  







  public float getWidth()
  {
    findRemoteCRIF();
    Rectangle2D boundingBox = remoteCRIF.getBounds2D(serverName, nodeSupport.getOperationName(), nodeSupport.getParameterBlock());
    



    return (float)boundingBox.getWidth();
  }
  







  public float getHeight()
  {
    findRemoteCRIF();
    Rectangle2D boundingBox = remoteCRIF.getBounds2D(serverName, nodeSupport.getOperationName(), nodeSupport.getParameterBlock());
    



    return (float)boundingBox.getHeight();
  }
  





  public float getMinX()
  {
    findRemoteCRIF();
    Rectangle2D boundingBox = remoteCRIF.getBounds2D(serverName, nodeSupport.getOperationName(), nodeSupport.getParameterBlock());
    



    return (float)boundingBox.getX();
  }
  





  public float getMinY()
  {
    findRemoteCRIF();
    Rectangle2D boundingBox = remoteCRIF.getBounds2D(serverName, nodeSupport.getOperationName(), nodeSupport.getParameterBlock());
    



    return (float)boundingBox.getY();
  }
  

































  public RenderedImage createRendering(RenderContext renderContext)
  {
    findRemoteCRIF();
    



    ParameterBlock renderedPB = (ParameterBlock)nodeSupport.getParameterBlock().clone();
    




    RenderContext rcIn = renderContext;
    RenderingHints nodeHints = nodeSupport.getRenderingHints();
    if (nodeHints != null) {
      RenderingHints hints = renderContext.getRenderingHints();
      RenderingHints mergedHints;
      RenderingHints mergedHints; if (hints == null) {
        mergedHints = nodeHints; } else { RenderingHints mergedHints;
        if ((nodeHints == null) || (nodeHints.isEmpty())) {
          mergedHints = hints;
        } else {
          mergedHints = new RenderingHints(nodeHints);
          mergedHints.add(hints);
        }
      }
      if (mergedHints != hints) {
        rcIn = new RenderContext(renderContext.getTransform(), renderContext.getAreaOfInterest(), mergedHints);
      }
    }
    



    Vector sources = nodeSupport.getParameterBlock().getSources();
    try
    {
      if (sources != null) {
        Vector renderedSources = new Vector();
        for (int i = 0; i < sources.size(); i++)
        {
          RenderedImage rdrdImage = null;
          Object source = sources.elementAt(i);
          if ((source instanceof RenderableImage)) {
            RenderContext rcOut = remoteCRIF.mapRenderContext(serverName, nodeSupport.getOperationName(), i, renderContext, nodeSupport.getParameterBlock(), this);
            







            RenderableImage src = (RenderableImage)source;
            rdrdImage = src.createRendering(rcOut);
          } else if ((source instanceof RenderedOp)) {
            rdrdImage = ((RenderedOp)source).getRendering();
          } else if ((source instanceof RenderedImage)) {
            rdrdImage = (RenderedImage)source;
          }
          
          if (rdrdImage == null) {
            return null;
          }
          


          renderedSources.addElement(rdrdImage);
        }
        
        if (renderedSources.size() > 0) {
          renderedPB.setSources(renderedSources);
        }
      }
      
      RenderedImage rendering = remoteCRIF.create(serverName, nodeSupport.getOperationName(), renderContext, renderedPB);
      




      if ((rendering instanceof RenderedOp)) {
        rendering = ((RenderedOp)rendering).getRendering();
      }
      



      linkToRemoteOp = rendering;
      

      if ((rendering != null) && ((rendering instanceof WritablePropertySource)))
      {
        String[] propertyNames = getPropertyNames();
        if (propertyNames != null) {
          WritablePropertySource wps = (WritablePropertySource)rendering;
          
          for (int j = 0; j < propertyNames.length; j++) {
            String name = propertyNames[j];
            Object value = getProperty(name);
            if ((value != null) && (value != Image.UndefinedProperty))
            {
              wps.setProperty(name, value);
            }
          }
        }
      }
      
      return rendering;
    }
    catch (ArrayIndexOutOfBoundsException e) {}
    return null;
  }
  


  private RemoteCRIF findRemoteCRIF()
  {
    if (remoteCRIF == null)
    {
      remoteCRIF = RemoteCRIFRegistry.get(nodeSupport.getRegistry(), protocolName);
      

      if (remoteCRIF == null) {
        throw new ImagingException(JaiI18N.getString("RemoteRenderableOp0"));
      }
    }
    

    return remoteCRIF;
  }
  







  public int getRetryInterval()
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      return 1000;
    }
    Integer i = (Integer)rh.get(JAI.KEY_RETRY_INTERVAL);
    if (i == null) {
      return 1000;
    }
    return i.intValue();
  }
  


















  public void setRetryInterval(int retryInterval)
  {
    if (retryInterval < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
    }
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      RenderingHints hints = new RenderingHints(null);
      nodeSupport.setRenderingHints(hints);
    }
    
    nodeSupport.getRenderingHints().put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
  }
  







  public int getNumRetries()
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      return 5;
    }
    Integer i = (Integer)rh.get(JAI.KEY_NUM_RETRIES);
    if (i == null) {
      return 5;
    }
    return i.intValue();
  }
  


















  public void setNumRetries(int numRetries)
  {
    if (numRetries < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    RenderingHints rh = nodeSupport.getRenderingHints();
    if (rh == null) {
      RenderingHints hints = new RenderingHints(null);
      nodeSupport.setRenderingHints(hints);
    }
    
    nodeSupport.getRenderingHints().put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
  }
  





  public NegotiableCapabilitySet getNegotiationPreferences()
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    
    NegotiableCapabilitySet ncs = rh == null ? null : (NegotiableCapabilitySet)rh.get(JAI.KEY_NEGOTIATION_PREFERENCES);
    

    return ncs;
  }
  

































  public void setNegotiationPreferences(NegotiableCapabilitySet preferences)
  {
    RenderingHints rh = nodeSupport.getRenderingHints();
    

    if (preferences != null)
    {

      if (rh == null) {
        RenderingHints hints = new RenderingHints(null);
        nodeSupport.setRenderingHints(hints);
      }
      

      nodeSupport.getRenderingHints().put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);



    }
    else if (rh != null) {
      rh.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
    }
    

    negotiated = negotiate(preferences);
  }
  
  private NegotiableCapabilitySet negotiate(NegotiableCapabilitySet prefs)
  {
    OperationRegistry registry = nodeSupport.getRegistry();
    
    NegotiableCapabilitySet serverCap = null;
    

    RemoteDescriptor descriptor = (RemoteDescriptor)registry.getDescriptor(RemoteDescriptor.class, protocolName);
    

    if (descriptor == null) {
      Object[] msgArg0 = { new String(protocolName) };
      MessageFormat formatter = new MessageFormat("");
      formatter.setLocale(Locale.getDefault());
      formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
      throw new RuntimeException(formatter.format(msgArg0));
    }
    
    int count = 0;
    int numRetries = getNumRetries();
    int retryInterval = getRetryInterval();
    
    Exception rieSave = null;
    while (count++ < numRetries) {
      try {
        serverCap = descriptor.getServerCapabilities(serverName);
      }
      catch (RemoteImagingException rie)
      {
        System.err.println(JaiI18N.getString("RemoteJAI24"));
        rieSave = rie;
        try
        {
          Thread.sleep(retryInterval);
        }
        catch (InterruptedException ie) {
          sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
        }
      }
    }
    

    if ((serverCap == null) && (count > numRetries)) {
      sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
    }
    


    RemoteRIF rrif = (RemoteRIF)registry.getFactory("remoteRenderable", protocolName);
    

    return RemoteJAI.negotiate(prefs, serverCap, rrif.getClientCapabilities());
  }
  








  public NegotiableCapabilitySet getNegotiatedValues()
    throws RemoteImagingException
  {
    return negotiated;
  }
  








  public NegotiableCapability getNegotiatedValues(String category)
    throws RemoteImagingException
  {
    if (negotiated != null)
      return negotiated.getNegotiatedValue(category);
    return null;
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = (ImagingListener)getRenderingHints().get(JAI.KEY_IMAGING_LISTENER);
    

    listener.errorOccurred(message, e, this, false);
  }
}
