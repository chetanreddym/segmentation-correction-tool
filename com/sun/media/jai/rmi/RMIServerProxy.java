package com.sun.media.jai.rmi;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.util.ImagingListener;

















public class RMIServerProxy
  extends PlanarImageServerProxy
{
  private ImageServer remoteImage = null;
  



  private Long id;
  


  private Long renderingID = null;
  


  private boolean preferencesSet;
  


  private NegotiableCapabilitySet negPref;
  

  private static final Class NULL_PROPERTY_CLASS = JAIRMIImageServer.NULL_PROPERTY.getClass();
  




  private ImagingListener listener;
  




  public RMIServerProxy(String serverName, String opName, RenderingHints hints)
  {
    super(serverName, "jairmi", opName, null, hints);
    



    int index = serverName.indexOf("::");
    boolean remoteChaining = index != -1;
    
    if (!remoteChaining)
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
    }
    

    if (remoteChaining)
    {

      id = Long.valueOf(serverName.substring(index + 2));
      serverName = serverName.substring(0, index);
      this.serverName = serverName;
    }
    
    listener = ImageUtil.getImagingListener(hints);
    
    remoteImage = getImageServer(serverName);
    
    if (preferencesSet) {
      super.setNegotiationPreferences(negPref);
    }
    
    try
    {
      remoteImage.incrementRefCount(id);
    } catch (RemoteException re) {
      System.err.println(JaiI18N.getString("RMIServerProxy2"));
    }
  }
  








  public RMIServerProxy(String serverName, ParameterBlock pb, String opName, RenderingHints hints)
  {
    super(serverName, "jairmi", opName, pb, hints);
    



    int index = serverName.indexOf("::");
    boolean remoteChaining = index != -1;
    
    if (!remoteChaining)
    {
      throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
    }
    

    if (remoteChaining)
    {

      id = Long.valueOf(serverName.substring(index + 2));
      serverName = serverName.substring(0, index);
      this.serverName = serverName;
    }
    
    listener = ImageUtil.getImagingListener(hints);
    
    remoteImage = getImageServer(serverName);
    
    if (preferencesSet) {
      super.setNegotiationPreferences(negPref);
    }
    
    try
    {
      remoteImage.incrementRefCount(id);
    } catch (RemoteException re) {
      System.err.println(JaiI18N.getString("RMIServerProxy2"));
    }
  }
  





  public RMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, RenderingHints hints)
  {
    super(serverName, "jairmi", operationName, paramBlock, hints);
    
    listener = ImageUtil.getImagingListener(hints);
    

    remoteImage = getImageServer(serverName);
    

    getRMIID();
    


    if (preferencesSet) {
      super.setNegotiationPreferences(negPref);
    }
    
    ParameterBlock newPB = (ParameterBlock)paramBlock.clone();
    newPB.removeSources();
    

    JAIRMIUtil.checkClientParameters(newPB, serverName);
    try
    {
      SerializableState rhs = SerializerFactory.getState(hints, null);
      remoteImage.createRenderedOp(id, operationName, newPB, rhs);
    } catch (RemoteException e) {
      String message = JaiI18N.getString("RMIServerProxy5");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
    






    int size = getNumSources();
    for (int i = 0; i < size; i++)
    {
      RenderedImage source = getSource(i);
      
      if ((source instanceof RMIServerProxy)) {
        try {
          RMIServerProxy rop = (RMIServerProxy)source;
          if (serverName.equalsIgnoreCase(this.serverName))
          {
            remoteImage.setRenderedSource(id, rop.getRMIID(), i);
          } else {
            remoteImage.setRenderedSource(id, rop.getRMIID(), serverName, operationName, i);
          }
          

        }
        catch (RemoteException e)
        {
          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(e), this, false);

        }
        

      }
      else if ((source instanceof RenderedOp))
      {




        RenderedOp rop = (RenderedOp)source;
        RenderedImage rendering = rop.getRendering();
        if (!(rendering instanceof Serializable)) {
          rendering = new SerializableRenderedImage(rendering);
        }
        try {
          remoteImage.setRenderedSource(id, rendering, i);
        } catch (RemoteException e) {
          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);


        }
        



      }
      else if ((source instanceof RenderedImage)) {
        try {
          if ((source instanceof Serializable)) {
            remoteImage.setRenderedSource(id, source, i);
          } else {
            remoteImage.setRenderedSource(id, new SerializableRenderedImage(source), i);
          }
          

        }
        catch (RemoteException e)
        {
          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
      }
    }
    






    try
    {
      remoteImage.incrementRefCount(id);
    } catch (RemoteException re) {
      System.err.println(JaiI18N.getString("RMIServerProxy2"));
    }
  }
  








  public RMIServerProxy(PlanarImageServerProxy oldRendering, OperationNode node, String newServerName)
  {
    this(newServerName, node.getOperationName(), node.getParameterBlock(), node.getRenderingHints());
  }
  










  public RMIServerProxy(PlanarImageServerProxy oldRendering, OperationNode node, PropertyChangeEventJAI event)
  {
    super(((RemoteRenderedOp)node).getServerName(), "jairmi", node.getOperationName(), node.getParameterBlock(), node.getRenderingHints());
    




    listener = ImageUtil.getImagingListener(node.getRenderingHints());
    

    remoteImage = getImageServer(serverName);
    
    RMIServerProxy oldRMISP = null;
    if ((oldRendering instanceof RMIServerProxy)) {
      oldRMISP = (RMIServerProxy)oldRendering;
    } else {
      System.err.println(JaiI18N.getString("RMIServerProxy3"));
    }
    
    Long opID = oldRMISP.getRMIID();
    
    String propName = event.getPropertyName();
    if ((event instanceof RenderingChangeEvent))
    {
      RenderingChangeEvent rce = (RenderingChangeEvent)event;
      

      int idx = ((RenderedOp)node).getSources().indexOf(rce.getSource());
      
      PlanarImage oldSrcRendering = (PlanarImage)event.getOldValue();
      
      Object oldSrc = null;
      String serverNodeDesc = null;
      if ((oldSrcRendering instanceof RMIServerProxy))
      {
        RMIServerProxy oldSrcRMISP = (RMIServerProxy)oldSrcRendering;
        
        if (!oldSrcRMISP.getServerName().equalsIgnoreCase(serverName))
        {
          serverNodeDesc = oldSrcRMISP.getServerName() + "::" + oldSrcRMISP.getRMIID();
        }
        else {
          serverNodeDesc = oldSrcRMISP.getRMIID().toString();
        }
        oldSrc = serverNodeDesc;
      } else if ((oldSrcRendering instanceof Serializable)) {
        oldSrc = oldSrcRendering;
      } else {
        oldSrc = new SerializableRenderedImage(oldSrcRendering);
      }
      
      Object srcInvalidRegion = rce.getInvalidRegion();
      SerializableState shapeState = SerializerFactory.getState((Shape)srcInvalidRegion, null);
      

      Long oldRenderingID = null;
      try {
        oldRenderingID = remoteImage.handleEvent(opID, idx, shapeState, oldSrc);

      }
      catch (RemoteException re)
      {

        String message = JaiI18N.getString("RMIServerProxy7");
        listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
      }
      




      id = oldRenderingID;
      id = opID;




    }
    else
    {



      Object oldValue = null;Object newValue = null;
      
      if (propName.equals("operationname"))
      {
        oldValue = event.getOldValue();
        newValue = event.getNewValue();
      }
      else if (propName.equals("parameterblock"))
      {
        ParameterBlock oldPB = (ParameterBlock)event.getOldValue();
        Vector oldSrcs = oldPB.getSources();
        oldPB.removeSources();
        
        ParameterBlock newPB = (ParameterBlock)event.getNewValue();
        Vector newSrcs = newPB.getSources();
        newPB.removeSources();
        

        JAIRMIUtil.checkClientParameters(oldPB, serverName);
        JAIRMIUtil.checkClientParameters(newPB, serverName);
        
        oldPB.setSources(JAIRMIUtil.replaceSourcesWithId(oldSrcs, serverName));
        
        newPB.setSources(JAIRMIUtil.replaceSourcesWithId(newSrcs, serverName));
        

        oldValue = oldPB;
        newValue = newPB;
      }
      else if (propName.equals("sources"))
      {
        Vector oldSrcs = (Vector)event.getOldValue();
        Vector newSrcs = (Vector)event.getNewValue();
        
        oldValue = JAIRMIUtil.replaceSourcesWithId(oldSrcs, serverName);
        
        newValue = JAIRMIUtil.replaceSourcesWithId(newSrcs, serverName);

      }
      else if (propName.equals("parameters"))
      {
        Vector oldParameters = (Vector)event.getOldValue();
        Vector newParameters = (Vector)event.getNewValue();
        

        JAIRMIUtil.checkClientParameters(oldParameters, serverName);
        JAIRMIUtil.checkClientParameters(newParameters, serverName);
        
        oldValue = oldParameters;
        newValue = newParameters;
      }
      else if (propName.equals("renderinghints"))
      {
        RenderingHints oldRH = (RenderingHints)event.getOldValue();
        RenderingHints newRH = (RenderingHints)event.getNewValue();
        
        oldValue = SerializerFactory.getState(oldRH, null);
        newValue = SerializerFactory.getState(newRH, null);
      } else {
        throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy4"));
      }
      

      Long oldRenderingID = null;
      try
      {
        oldRenderingID = remoteImage.handleEvent(opID, propName, oldValue, newValue);
        



        remoteImage.incrementRefCount(oldRenderingID);
      } catch (RemoteException re) {
        String message = JaiI18N.getString("RMIServerProxy7");
        listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
      }
      



      id = oldRenderingID;
      id = opID;
    }
    


    if (preferencesSet) {
      super.setNegotiationPreferences(negPref);
    }
  }
  







  public RMIServerProxy(String serverName, String operationName, ParameterBlock pb, RenderingHints hints, Long id)
  {
    super(serverName, "jairmi", operationName, pb, hints);
    
    listener = ImageUtil.getImagingListener(hints);
    

    remoteImage = getImageServer(serverName);
    
    this.id = id;
  }
  








  public RMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, RenderContext rc, boolean isRender)
  {
    super(serverName, "jairmi", operationName, paramBlock, null);
    
    listener = ImageUtil.getImagingListener(rc.getRenderingHints());
    

    remoteImage = getImageServer(serverName);
    

    getRMIID();
    
    if (preferencesSet) {
      super.setNegotiationPreferences(negPref);
    }
    

    ParameterBlock newPB = (ParameterBlock)paramBlock.clone();
    newPB.removeSources();
    







    try
    {
      remoteImage.createRenderableOp(id, operationName, newPB);
    } catch (RemoteException e) {
      String message = JaiI18N.getString("RMIServerProxy8");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
    




    int size = getNumSources();
    
    for (int i = 0; i < size; i++)
    {
      Vector sources = paramBlock.getSources();
      Object source = sources.elementAt(i);
      
      if ((source instanceof RMIServerProxy)) {
        try {
          RMIServerProxy rop = (RMIServerProxy)source;
          
          if (serverName.equals(this.serverName)) {
            remoteImage.setRenderableSource(id, rop.getRMIID(), i);
          } else {
            remoteImage.setRenderableSource(id, rop.getRMIID(), serverName, operationName, i);
          }
        }
        catch (RemoteException e)
        {
          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);



        }
        


      }
      else if ((source instanceof RenderableOp)) {
        try {
          remoteImage.setRenderableSource(id, (RenderableOp)source, i);
        }
        catch (RemoteException e)
        {
          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);



        }
        


      }
      else if ((source instanceof RenderedImage)) {
        try {
          remoteImage.setRenderableSource(id, new SerializableRenderedImage((RenderedImage)source), i);

        }
        catch (RemoteException e)
        {

          String message = JaiI18N.getString("RMIServerProxy6");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
      }
    }
    






    try
    {
      remoteImage.incrementRefCount(id);
    } catch (RemoteException e) {
      String message = JaiI18N.getString("RMIServerProxy9");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
    










    if (isRender) {
      try {
        renderingID = remoteImage.getRendering(id, SerializerFactory.getState(rc, null));
        



        remoteImage.incrementRefCount(renderingID);
      } catch (RemoteException e) {
        String message = JaiI18N.getString("RMIServerProxy10");
        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
      }
    }
  }
  



















  protected synchronized ImageServer getImageServer(String serverName)
  {
    if (remoteImage == null)
    {
      if (serverName == null) {
        try {
          serverName = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
          String message = JaiI18N.getString("RMIServerProxy11");
          listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
      }
      




      String serviceName = new String("rmi://" + serverName + "/" + "JAIRMIRemoteServer1.1");
      



      remoteImage = null;
      try {
        remoteImage = ((ImageServer)Naming.lookup(serviceName));
      } catch (Exception e) {
        String message = JaiI18N.getString("RMIServerProxy12");
        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
      }
    }
    



    return remoteImage;
  }
  




  public synchronized Long getRMIID()
  {
    if (id != null) {
      return id;
    }
    try
    {
      id = remoteImage.getRemoteID();
      return id;
    } catch (Exception e) {
      String message = JaiI18N.getString("RMIServerProxy13");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
    



    return id;
  }
  
  public Long getRenderingID() {
    return renderingID;
  }
  
  public boolean canBeRendered()
  {
    boolean cbr = true;
    getImageServer(serverName);
    try {
      cbr = remoteImage.getRendering(getRMIID());
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy10");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    



    return cbr;
  }
  


  protected void finalize()
  {
    try
    {
      remoteImage.dispose(id);
    }
    catch (Exception e) {}
    

    super.dispose();
  }
  






  public ImageLayout getImageLayout()
    throws RemoteImagingException
  {
    ImageLayout layout = new ImageLayout();
    try {
      layout.setMinX(remoteImage.getMinX(id));
      layout.setMinY(remoteImage.getMinY(id));
      layout.setWidth(remoteImage.getWidth(id));
      layout.setHeight(remoteImage.getHeight(id));
      layout.setTileWidth(remoteImage.getTileWidth(id));
      layout.setTileHeight(remoteImage.getTileHeight(id));
      layout.setTileGridXOffset(remoteImage.getTileGridXOffset(id));
      layout.setTileGridYOffset(remoteImage.getTileGridYOffset(id));
      
      SerializableState smState = remoteImage.getSampleModel(id);
      layout.setSampleModel((SampleModel)smState.getObject());
      SerializableState cmState = remoteImage.getColorModel(id);
      layout.setColorModel((ColorModel)cmState.getObject());
      return layout;
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy14");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    
    return null;
  }
  










  public Raster computeTile(int tileX, int tileY)
    throws RemoteImagingException
  {
    if ((tileX < getMinTileX()) || (tileX > getMaxTileX()) || (tileY < getMinTileY()) || (tileY > getMaxTileY()))
    {
      return null;
    }
    


    NegotiableCapability codecCap = getNegotiatedValue("tileCodec");
    
    TileDecoderFactory tdf = null;
    TileCodecParameterList tcpl = null;
    
    if (codecCap != null)
    {
      String category = codecCap.getCategory();
      String capabilityName = codecCap.getCapabilityName();
      List generators = codecCap.getGenerators();
      

      for (Iterator i = generators.iterator(); i.hasNext();) {
        Class factory = (Class)i.next();
        if ((tdf == null) && (TileDecoderFactory.class.isAssignableFrom(factory)))
        {
          try
          {
            tdf = (TileDecoderFactory)factory.newInstance();
          } catch (InstantiationException ie) {
            throw new RemoteImagingException(ImageUtil.getStackTraceString(ie));
          } catch (IllegalAccessException iae) {
            throw new RemoteImagingException(ImageUtil.getStackTraceString(iae));
          }
        }
      }
      
      if (tdf == null) {
        throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy0"));
      }
      

      TileCodecDescriptor tcd = (TileCodecDescriptor)registry.getDescriptor("tileDecoder", capabilityName);
      


      if ((!tcd.includesSampleModelInfo()) || (!tcd.includesLocationInfo()))
      {
        throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy1"));
      }
      

      ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileDecoder");
      

      tcpl = new TileCodecParameterList(capabilityName, new String[] { "tileDecoder" }, pld);
      




      if (pld != null)
      {
        String[] paramNames = pld.getParamNames();
        

        if (paramNames != null) {
          for (int i = 0; i < paramNames.length; i++) {
            String currParam = paramNames[i];
            try {
              currValue = codecCap.getNegotiatedValue(currParam);
            }
            catch (IllegalArgumentException iae) {
              Object currValue;
              continue;
            }
            Object currValue;
            tcpl.setParameter(currParam, currValue);
          }
        }
      }
    }
    
    try
    {
      if (codecCap != null) {
        byte[] ctile = remoteImage.getCompressedTile(id, tileX, tileY);
        

        ByteArrayInputStream stream = new ByteArrayInputStream(ctile);
        TileDecoder decoder = tdf.createDecoder(stream, tcpl);
        try {
          return decoder.decode();
        } catch (IOException ioe) {
          throw new RemoteImagingException(ImageUtil.getStackTraceString(ioe));
        }
      }
      
      SerializableState rp = remoteImage.getTile(id, tileX, tileY);
      return (Raster)rp.getObject();
    }
    catch (RemoteException e) {
      String message = JaiI18N.getString("RMIServerProxy15");
      listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
    



    return null;
  }
  
  public Object getRemoteProperty(String name) throws RemoteImagingException
  {
    try {
      Object property = remoteImage.getProperty(id, name);
      if (NULL_PROPERTY_CLASS.isInstance(property)) {}
      return Image.UndefinedProperty;
    }
    catch (RemoteException re)
    {
      String message = JaiI18N.getString("RMIServerProxy16");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    



    return Image.UndefinedProperty;
  }
  





  public String[] getRemotePropertyNames()
    throws RemoteImagingException
  {
    try
    {
      return remoteImage.getPropertyNames(id);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy17");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    



    return null;
  }
  





















  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
    throws RemoteImagingException
  {
    Rectangle dstRect = null;
    try
    {
      dstRect = remoteImage.mapSourceRect(id, sourceRect, sourceIndex);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy18");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    



    return dstRect;
  }
  



















  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
    throws RemoteImagingException
  {
    Rectangle srcRect = null;
    try
    {
      srcRect = remoteImage.mapDestRect(id, destRect, sourceIndex);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy18");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
    



    return srcRect;
  }
  
  public void setNegotiationPreferences(NegotiableCapabilitySet preferences)
  {
    if (remoteImage == null) {
      negPref = preferences;
      preferencesSet = true;
    } else {
      super.setNegotiationPreferences(preferences);
    }
  }
  





  public synchronized void setServerNegotiatedValues(NegotiableCapabilitySet negotiatedValues)
    throws RemoteImagingException
  {
    try
    {
      remoteImage.setServerNegotiatedValues(id, negotiatedValues);
    } catch (RemoteException re) {
      String message = JaiI18N.getString("RMIServerProxy19");
      listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
    }
  }
}
