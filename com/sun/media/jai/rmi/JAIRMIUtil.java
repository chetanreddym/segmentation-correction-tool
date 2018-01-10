package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;





















public final class JAIRMIUtil
{
  public JAIRMIUtil() {}
  
  public static Vector replaceIdWithSources(Vector srcs, Hashtable nodes, String opName, RenderingHints hints)
  {
    Vector replacedSrcs = new Vector();
    
    for (int i = 0; i < srcs.size(); i++) {
      Object obj = srcs.elementAt(i);
      if ((obj instanceof String)) {
        String serverNodeDesc = (String)obj;
        int index = serverNodeDesc.indexOf("::");
        boolean diffServer = index != -1;
        if (diffServer)
        {

          replacedSrcs.add(new RMIServerProxy(serverNodeDesc, opName, hints));


        }
        else
        {

          replacedSrcs.add(nodes.get(Long.valueOf(serverNodeDesc)));
        }
      } else {
        PlanarImage pi = PlanarImage.wrapRenderedImage((RenderedImage)obj);
        
        replacedSrcs.add(pi);
      }
    }
    
    return replacedSrcs;
  }
  






  public static Vector replaceSourcesWithId(Vector srcs, String serverName)
  {
    Vector replacedSrcs = new Vector();
    
    for (int i = 0; i < srcs.size(); i++) {
      Object obj = srcs.elementAt(i);
      if ((obj instanceof RMIServerProxy)) {
        RMIServerProxy rmisp = (RMIServerProxy)obj;
        if (rmisp.getServerName().equalsIgnoreCase(serverName)) {
          replacedSrcs.add(rmisp.getRMIID().toString());
        } else {
          String str = new String(rmisp.getServerName() + "::" + rmisp.getRMIID());
          

          replacedSrcs.add(str);
        }
      } else if ((obj instanceof RemoteRenderedOp)) {
        RemoteRenderedOp rrop = (RemoteRenderedOp)obj;
        Object ai = rrop.getRendering();
        if ((ai instanceof RMIServerProxy)) {
          RMIServerProxy rmisp = (RMIServerProxy)ai;
          if (rmisp.getServerName().equalsIgnoreCase(serverName)) {
            replacedSrcs.add(rmisp.getRMIID().toString());
          } else {
            String str = new String(rmisp.getServerName() + "::" + rmisp.getRMIID());
            

            replacedSrcs.add(str);
          }
        } else {
          RenderedImage ri = (RenderedImage)ai;
          replacedSrcs.add(new SerializableRenderedImage(ri));
        }
      } else if ((obj instanceof RenderedOp)) {
        RenderedOp rop = (RenderedOp)obj;
        replacedSrcs.add(new SerializableRenderedImage(rop.getRendering()));
      }
      else if ((obj instanceof Serializable)) {
        replacedSrcs.add(obj);
      } else if ((obj instanceof RenderedImage)) {
        RenderedImage ri = (RenderedImage)obj;
        replacedSrcs.add(new SerializableRenderedImage(ri));
      }
    }
    
    return replacedSrcs;
  }
  





  public static Object replaceImage(RenderedImage obj, String thisServerName)
  {
    if ((obj instanceof RMIServerProxy))
    {
      RMIServerProxy rmisp = (RMIServerProxy)obj;
      if (rmisp.getServerName().equalsIgnoreCase(thisServerName)) {
        return "::" + rmisp.getRMIID();
      }
      return rmisp.getServerName() + "::" + rmisp.getRMIID() + ";;" + rmisp.getOperationName();
    }
    
    if ((obj instanceof RenderedOp))
    {
      RenderedImage rendering = ((RenderedOp)obj).getRendering();
      return replaceImage(rendering, thisServerName);
    }
    if ((obj instanceof RenderedImage))
    {
      if ((obj instanceof Serializable)) {
        return obj;
      }
      return new SerializableRenderedImage(obj);
    }
    
    return obj;
  }
  









  public static void checkClientParameters(ParameterBlock pb, String thisServerName)
  {
    if (pb == null) {
      return;
    }
    int numParams = pb.getNumParameters();
    Vector params = pb.getParameters();
    

    for (int i = 0; i < numParams; i++) {
      Object obj = params.elementAt(i);
      
      if (obj != null)
      {


        if ((obj instanceof RenderedImage))
        {
          pb.set(replaceImage((RenderedImage)obj, thisServerName), i);
        }
      }
    }
  }
  









  public static void checkClientParameters(Vector parameters, String thisServerName)
  {
    if (parameters == null) {
      return;
    }
    
    for (int i = 0; i < parameters.size(); i++) {
      Object obj = parameters.elementAt(i);
      
      if (obj != null)
      {


        if ((obj instanceof RenderedImage))
        {
          parameters.set(i, replaceImage((RenderedImage)obj, thisServerName));
        }
      }
    }
  }
  






  public static Object replaceStringWithImage(String s, Hashtable nodes)
  {
    int index1 = s.indexOf("::");
    int index2 = s.indexOf(";;");
    

    if (index1 == -1)
      return s;
    if (index2 == -1) {
      Long id = Long.valueOf(s.substring(index1 + 2));
      return nodes.get(id);
    }
    



    Long id = Long.valueOf(s.substring(index1 + 2, index2));
    String paramServerName = s.substring(0, index1);
    String opName = s.substring(index2 + 2);
    


    return new RMIServerProxy(paramServerName + "::" + id, opName, null);
  }
  








  public static void checkServerParameters(ParameterBlock pb, Hashtable nodes)
  {
    if (pb == null) {
      return;
    }
    int numParams = pb.getNumParameters();
    Vector params = pb.getParameters();
    

    for (int i = 0; i < numParams; i++) {
      Object obj = params.elementAt(i);
      
      if (obj != null)
      {
        if ((obj instanceof String)) {
          pb.set(replaceStringWithImage((String)obj, nodes), i);
        }
      }
    }
  }
  




  public static void checkServerParameters(Vector parameters, Hashtable nodes)
  {
    if (parameters == null) {
      return;
    }
    
    for (int i = 0; i < parameters.size(); i++) {
      Object obj = parameters.elementAt(i);
      
      if (obj != null)
      {
        if ((obj instanceof String)) {
          parameters.set(i, replaceStringWithImage((String)obj, nodes));
        }
      }
    }
  }
}
