package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;






















































































































































public class RemoteJAI
{
  protected String serverName;
  protected String protocolName;
  private OperationRegistry operationRegistry = JAI.getDefaultInstance().getOperationRegistry();
  


  public static final int DEFAULT_RETRY_INTERVAL = 1000;
  


  public static final int DEFAULT_NUM_RETRIES = 5;
  


  private int retryInterval = 1000;
  

  private int numRetries = 5;
  

  private transient TileCache cache = JAI.getDefaultInstance().getTileCache();
  





  private RenderingHints renderingHints;
  




  private NegotiableCapabilitySet preferences = null;
  



  private static NegotiableCapabilitySet negotiated;
  


  private NegotiableCapabilitySet serverCapabilities = null;
  private NegotiableCapabilitySet clientCapabilities = null;
  




  private Hashtable odHash = null;
  

  private OperationDescriptor[] descriptors = null;
  









  private static MessageFormat formatter;
  










  public RemoteJAI(String protocolName, String serverName)
  {
    this(protocolName, serverName, null, null);
  }
  



























  public RemoteJAI(String protocolName, String serverName, OperationRegistry registry, TileCache tileCache)
  {
    if (protocolName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
    }
    

    formatter = new MessageFormat("");
    formatter.setLocale(Locale.getDefault());
    
    this.protocolName = protocolName;
    this.serverName = serverName;
    



    if (registry != null) {
      operationRegistry = registry;
    }
    
    if (tileCache != null) {
      cache = tileCache;
    }
    
    renderingHints = new RenderingHints(null);
    renderingHints.put(JAI.KEY_OPERATION_REGISTRY, operationRegistry);
    renderingHints.put(JAI.KEY_TILE_CACHE, cache);
    renderingHints.put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
    
    renderingHints.put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
  }
  


  public String getServerName()
  {
    return serverName;
  }
  


  public String getProtocolName()
  {
    return protocolName;
  }
  










  public void setRetryInterval(int retryInterval)
  {
    if (retryInterval < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
    }
    
    this.retryInterval = retryInterval;
    renderingHints.put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
  }
  


  public int getRetryInterval()
  {
    return retryInterval;
  }
  








  public void setNumRetries(int numRetries)
  {
    if (numRetries < 0) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
    }
    
    this.numRetries = numRetries;
    renderingHints.put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
  }
  


  public int getNumRetries()
  {
    return numRetries;
  }
  



  public OperationRegistry getOperationRegistry()
  {
    return operationRegistry;
  }
  







  public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    if (operationRegistry == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI4"));
    }
    
    this.operationRegistry = operationRegistry;
    renderingHints.put(JAI.KEY_OPERATION_REGISTRY, operationRegistry);
  }
  







  public void setTileCache(TileCache tileCache)
  {
    if (tileCache == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI5"));
    }
    
    cache = tileCache;
    renderingHints.put(JAI.KEY_TILE_CACHE, cache);
  }
  



  public TileCache getTileCache()
  {
    return cache;
  }
  





  public RenderingHints getRenderingHints()
  {
    return renderingHints;
  }
  







  public void setRenderingHints(RenderingHints hints)
  {
    if (hints == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI6"));
    }
    
    renderingHints = hints;
  }
  



  public void clearRenderingHints()
  {
    renderingHints = new RenderingHints(null);
  }
  






  public Object getRenderingHint(RenderingHints.Key key)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI7"));
    }
    
    return renderingHints.get(key);
  }
  










  public void setRenderingHint(RenderingHints.Key key, Object value)
  {
    if (key == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI7"));
    }
    
    if (value == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI8"));
    }
    
    try
    {
      renderingHints.put(key, value);
    } catch (Exception e) {
      throw new IllegalArgumentException(e.toString());
    }
  }
  



  public void removeRenderingHint(RenderingHints.Key key)
  {
    renderingHints.remove(key);
  }
  




































































  public RemoteRenderedOp create(String opName, ParameterBlock args, RenderingHints hints)
  {
    if (opName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI9"));
    }
    

    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI10"));
    }
    


    getServerSupportedOperationList();
    

    OperationDescriptor odesc = (OperationDescriptor)odHash.get(new CaselessStringKey(opName));
    

    if (odesc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI11"));
    }
    


    if (!odesc.isModeSupported("rendered")) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI12"));
    }
    


    if (!RenderedImage.class.isAssignableFrom(odesc.getDestClass("rendered")))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI13"));
    }
    




    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    if (!odesc.validateArguments("rendered", args, msg)) {
      throw new IllegalArgumentException(msg.toString());
    }
    
    RenderingHints mergedHints;
    RenderingHints mergedHints;
    if (hints == null) {
      mergedHints = renderingHints; } else { RenderingHints mergedHints;
      if (renderingHints.isEmpty()) {
        mergedHints = hints;
      } else {
        mergedHints = new RenderingHints(renderingHints);
        mergedHints.add(hints);
      }
    }
    RemoteRenderedOp op = new RemoteRenderedOp(operationRegistry, protocolName, serverName, opName, args, mergedHints);
    






    if (odesc.isImmediate()) {
      PlanarImage im = null;
      im = op.getRendering();
      
      if (im == null)
      {
        return null;
      }
    }
    

    return op;
  }
  
























































  public RemoteRenderableOp createRenderable(String opName, ParameterBlock args)
  {
    if (opName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI9"));
    }
    

    if (args == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI10"));
    }
    


    getServerSupportedOperationList();
    

    OperationDescriptor odesc = (OperationDescriptor)odHash.get(new CaselessStringKey(opName));
    

    if (odesc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI11"));
    }
    


    if (!odesc.isModeSupported("renderable")) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI14"));
    }
    


    if (!RenderableImage.class.isAssignableFrom(odesc.getDestClass("renderable")))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI15"));
    }
    




    StringBuffer msg = new StringBuffer();
    args = (ParameterBlock)args.clone();
    if (!odesc.validateArguments("renderable", args, msg)) {
      throw new IllegalArgumentException(msg.toString());
    }
    
    RemoteRenderableOp op = new RemoteRenderableOp(operationRegistry, protocolName, serverName, opName, args);
    




    op.setRenderingHints(renderingHints);
    

    return op;
  }
  




























  public void setNegotiationPreferences(NegotiableCapabilitySet preferences)
  {
    this.preferences = preferences;
    
    if (preferences == null) {
      renderingHints.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
    } else {
      renderingHints.put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);
    }
    

    negotiated = null;
    getNegotiatedValues();
  }
  
















  public NegotiableCapabilitySet getNegotiatedValues()
    throws RemoteImagingException
  {
    if (negotiated == null)
    {
      if (serverCapabilities == null) {
        serverCapabilities = getServerCapabilities();
      }
      
      if (clientCapabilities == null) {
        clientCapabilities = getClientCapabilities();
      }
      

      negotiated = negotiate(preferences, serverCapabilities, clientCapabilities);
    }
    


    return negotiated;
  }
  
























  public NegotiableCapability getNegotiatedValues(String category)
    throws RemoteImagingException
  {
    if (negotiated == null)
    {
      if (serverCapabilities == null) {
        serverCapabilities = getServerCapabilities();
      }
      
      if (clientCapabilities == null) {
        clientCapabilities = getClientCapabilities();
      }
      

      return negotiate(preferences, serverCapabilities, clientCapabilities, category);
    }
    





    return negotiated.getNegotiatedValue(category);
  }
  










































  public static NegotiableCapabilitySet negotiate(NegotiableCapabilitySet preferences, NegotiableCapabilitySet serverCapabilities, NegotiableCapabilitySet clientCapabilities)
  {
    if ((serverCapabilities == null) || (clientCapabilities == null)) {
      return null;
    }
    if ((serverCapabilities != null) && (serverCapabilities.isPreference() == true))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI20"));
    }
    
    if ((clientCapabilities != null) && (clientCapabilities.isPreference() == true))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI21"));
    }
    
    if (preferences == null) {
      return serverCapabilities.negotiate(clientCapabilities);
    }
    if (!preferences.isPreference()) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI19"));
    }
    
    NegotiableCapabilitySet clientServerCap = serverCapabilities.negotiate(clientCapabilities);
    
    if (clientServerCap == null)
      return null;
    return clientServerCap.negotiate(preferences);
  }
  













































  public static NegotiableCapability negotiate(NegotiableCapabilitySet preferences, NegotiableCapabilitySet serverCapabilities, NegotiableCapabilitySet clientCapabilities, String category)
  {
    if ((serverCapabilities == null) || (clientCapabilities == null)) {
      return null;
    }
    if ((serverCapabilities != null) && (serverCapabilities.isPreference() == true))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI20"));
    }
    
    if ((clientCapabilities != null) && (clientCapabilities.isPreference() == true))
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI21"));
    }
    
    if ((preferences != null) && (!preferences.isPreference())) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI19"));
    }
    
    if (category == null) {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI26"));
    }
    
    if ((preferences == null) || (preferences.isEmpty())) {
      return serverCapabilities.getNegotiatedValue(clientCapabilities, category);
    }
    

    List prefList = preferences.get(category);
    List serverList = serverCapabilities.get(category);
    List clientList = clientCapabilities.get(category);
    Iterator p = prefList.iterator();
    


    NegotiableCapability pref = null;
    
    if (!p.hasNext()) {
      pref = null;
    } else {
      pref = (NegotiableCapability)p.next();
    }
    Vector results = new Vector();
    

    for (Iterator s = serverList.iterator(); s.hasNext();) {
      server = (NegotiableCapability)s.next();
      for (c = clientList.iterator(); c.hasNext();) {
        NegotiableCapability client = (NegotiableCapability)c.next();
        
        NegotiableCapability result = server.negotiate(client);
        if (result != null)
        {




          results.add(result);
          
          if (pref != null)
          {

            result = result.negotiate(pref);
          }
          
          if (result != null)
            return result;
        }
      }
    }
    NegotiableCapability server;
    Iterator c;
    while (p.hasNext()) {
      pref = (NegotiableCapability)p.next();
      for (int r = 0; r < results.size(); r++) { NegotiableCapability result;
        if ((result = pref.negotiate((NegotiableCapability)results.elementAt(r))) != null)
        {
          return result;
        }
      }
    }
    

    return null;
  }
  






  public NegotiableCapabilitySet getServerCapabilities()
    throws RemoteImagingException
  {
    if (serverCapabilities == null)
    {

      RemoteDescriptor descriptor = (RemoteDescriptor)operationRegistry.getDescriptor(RemoteDescriptor.class, protocolName);
      


      if (descriptor == null) {
        Object[] msgArg0 = { new String(protocolName) };
        formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
        throw new RuntimeException(formatter.format(msgArg0));
      }
      Exception rieSave = null;
      int count = 0;
      while (count++ < numRetries) {
        try {
          serverCapabilities = descriptor.getServerCapabilities(serverName);

        }
        catch (RemoteImagingException rie)
        {
          System.err.println(JaiI18N.getString("RemoteJAI24"));
          rieSave = rie;
          try
          {
            Thread.sleep(retryInterval);
          } catch (InterruptedException ie) {
            sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
          }
        }
      }
      


      if ((serverCapabilities == null) && (count > numRetries)) {
        sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
      }
    }
    


    return serverCapabilities;
  }
  



  public NegotiableCapabilitySet getClientCapabilities()
  {
    if (clientCapabilities == null)
    {
      RemoteRIF rrif = (RemoteRIF)operationRegistry.getFactory("remoteRendered", protocolName);
      

      if (rrif == null) {
        rrif = (RemoteRIF)operationRegistry.getFactory("remoteRenderable", protocolName);
      }
      


      if (rrif == null) {
        Object[] msgArg0 = { new String(protocolName) };
        formatter.applyPattern(JaiI18N.getString("RemoteJAI17"));
        throw new RuntimeException(formatter.format(msgArg0));
      }
      
      clientCapabilities = rrif.getClientCapabilities();
    }
    
    return clientCapabilities;
  }
  







  public OperationDescriptor[] getServerSupportedOperationList()
    throws RemoteImagingException
  {
    if (descriptors == null)
    {

      RemoteDescriptor descriptor = (RemoteDescriptor)operationRegistry.getDescriptor(RemoteDescriptor.class, protocolName);
      


      if (descriptor == null) {
        Object[] msgArg0 = { new String(protocolName) };
        formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
        throw new RuntimeException(formatter.format(msgArg0));
      }
      Exception rieSave = null;
      int count = 0;
      while (count++ < numRetries) {
        try {
          descriptors = descriptor.getServerSupportedOperationList(serverName);

        }
        catch (RemoteImagingException rie)
        {
          System.err.println(JaiI18N.getString("RemoteJAI25"));
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
      

      if ((descriptors == null) && (count > numRetries)) {
        sendExceptionToListener(JaiI18N.getString("RemoteJAI23"), rieSave);
      }
      




      odHash = new Hashtable();
      for (int i = 0; i < descriptors.length; i++) {
        odHash.put(new CaselessStringKey(descriptors[i].getName()), descriptors[i]);
      }
    }
    

    return descriptors;
  }
  
  void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
    listener.errorOccurred(message, e, this, false);
  }
}
