package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;


































public class OperationNodeSupport
  implements Serializable
{
  private static final int PB_EQUAL = 0;
  private static final int PB_SOURCES_DIFFER = 1;
  private static final int PB_PARAMETERS_DIFFER = 2;
  private static final int PB_DIFFER = 3;
  private String registryModeName;
  private String opName;
  private transient OperationRegistry registry;
  private transient ParameterBlock pb;
  private transient RenderingHints hints;
  private PropertyChangeSupportJAI eventManager;
  private transient PropertyEnvironment propertySource = null;
  





  private Vector localPropEnv = new Vector();
  




  private Hashtable paramObservers = new Hashtable();
  


  private static int compare(ParameterBlock pb1, ParameterBlock pb2)
  {
    if ((pb1 == null) && (pb2 == null)) {
      return 0;
    }
    
    if (((pb1 == null) && (pb2 != null)) || ((pb1 != null) && (pb2 == null)))
    {
      return 3;
    }
    
    int result = 0;
    if (!equals(pb1.getSources(), pb2.getSources())) {
      result |= 0x1;
    }
    if (!equals(pb1.getParameters(), pb2.getParameters())) {
      result |= 0x2;
    }
    
    return result;
  }
  
  private static boolean equals(ParameterBlock pb1, ParameterBlock pb2) {
    return pb2 == null;
  }
  

  private static boolean equals(Object o1, Object o2)
  {
    return o1 == null ? false : o2 == null ? true : o1.equals(o2);
  }
  











































  public OperationNodeSupport(String registryModeName, String opName, OperationRegistry registry, ParameterBlock pb, RenderingHints hints, PropertyChangeSupportJAI eventManager)
  {
    if ((registryModeName == null) || (opName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    this.registryModeName = registryModeName;
    this.opName = opName;
    if (registry == null) {
      this.registry = JAI.getDefaultInstance().getOperationRegistry();
    } else
      this.registry = registry;
    this.pb = pb;
    this.hints = hints;
    this.eventManager = eventManager;
    

    if (pb != null) {
      updateObserverMap(pb.getParameters());
    }
  }
  




  private class CopyDirective
    implements Serializable
  {
    private String name;
    



    private int index;
    



    CopyDirective(String name, int index)
    {
      if (name == null) {
        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
      }
      this.name = name;
      this.index = index;
    }
    
    String getName() {
      return name;
    }
    
    int getIndex() {
      return index;
    }
  }
  




  private class ParamObserver
    implements Observer
  {
    final int paramIndex;
    


    final DeferredData dd;
    



    ParamObserver(int paramIndex, DeferredData dd)
    {
      if (dd == null)
        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
      if ((paramIndex < 0) || ((pb != null) && (paramIndex >= pb.getNumParameters())))
      {


        throw new ArrayIndexOutOfBoundsException();
      }
      
      this.paramIndex = paramIndex;
      this.dd = dd;
      

      dd.addObserver(this);
    }
    





    public synchronized void update(Observable o, Object arg)
    {
      if (o != dd) {
        return;
      }
      

      if ((arg != null) && (eventManager != null)) {
        Vector params = pb.getParameters();
        Vector oldParams = (Vector)params.clone();
        Vector newParams = (Vector)params.clone();
        
        oldParams.set(paramIndex, arg);
        newParams.set(paramIndex, dd.getData());
        
        OperationNodeSupport.this.fireEvent("Parameters", oldParams, newParams);
      }
    }
  }
  




  private void updateObserverMap(Vector parameters)
  {
    if (parameters == null) {
      return;
    }
    
    int numParameters = parameters.size();
    for (int i = 0; i < numParameters; i++) {
      Object parameter = parameters.get(i);
      Integer index = new Integer(i);
      
      Object oldObs;
      Object oldObs;
      if ((parameter instanceof DeferredData)) {
        Observer obs = new ParamObserver(i, (DeferredData)parameter);
        
        oldObs = paramObservers.put(index, obs);
      } else {
        oldObs = paramObservers.remove(index);
      }
      

      if (oldObs != null) {
        ParamObserver obs = (ParamObserver)oldObs;
        dd.deleteObserver(obs);
      }
    }
  }
  




  public String getRegistryModeName()
  {
    return registryModeName;
  }
  



  public String getOperationName()
  {
    return opName;
  }
  















  public void setOperationName(String opName)
  {
    if (opName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (opName.equalsIgnoreCase(this.opName)) { return;
    }
    String oldOpName = this.opName;
    this.opName = opName;
    fireEvent("OperationName", oldOpName, opName);
    resetPropertyEnvironment(false);
  }
  



  public OperationRegistry getRegistry()
  {
    return registry;
  }
  














  public void setRegistry(OperationRegistry registry)
  {
    if (registry == null) {
      registry = JAI.getDefaultInstance().getOperationRegistry();
    }
    if (registry != this.registry) {
      OperationRegistry oldRegistry = this.registry;
      this.registry = registry;
      fireEvent("OperationRegistry", oldRegistry, registry);
      resetPropertyEnvironment(false);
    }
  }
  





  public ParameterBlock getParameterBlock()
  {
    return pb;
  }
  










































  public void setParameterBlock(ParameterBlock pb)
  {
    int comparison = compare(this.pb, pb);
    if (comparison == 0) {
      return;
    }
    
    ParameterBlock oldPB = this.pb;
    this.pb = pb;
    

    if (pb != null) {
      updateObserverMap(pb.getParameters());
    }
    
    if (comparison == 1)
    {
      fireEvent("Sources", oldPB.getSources(), pb.getSources());
    } else if (comparison == 2)
    {
      fireEvent("Parameters", oldPB.getParameters(), pb.getParameters());
    }
    else
    {
      fireEvent("ParameterBlock", oldPB, pb);
    }
    
    resetPropertyEnvironment(false);
  }
  





  public RenderingHints getRenderingHints()
  {
    return hints;
  }
  

















  public void setRenderingHints(RenderingHints hints)
  {
    if (equals(this.hints, hints)) {
      return;
    }
    RenderingHints oldHints = this.hints;
    this.hints = hints;
    fireEvent("RenderingHints", oldHints, hints);
    resetPropertyEnvironment(false);
  }
  









  public void addPropertyGenerator(PropertyGenerator pg)
  {
    if (pg == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    localPropEnv.add(pg);
    if (propertySource != null) {
      propertySource.addPropertyGenerator(pg);
    }
  }
  










  public void copyPropertyFromSource(String propertyName, int sourceIndex)
  {
    if (propertyName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    localPropEnv.add(new CopyDirective(propertyName, sourceIndex));
    if (propertySource != null) {
      propertySource.copyPropertyFromSource(propertyName, sourceIndex);
    }
  }
  














  public void suppressProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    localPropEnv.add(name);
    if (propertySource != null) {
      propertySource.suppressProperty(name);
    }
  }
  
































  public PropertySource getPropertySource(OperationNode opNode, PropertySource defaultPS)
  {
    if (opNode == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (propertySource == null) {
      synchronized (this) {
        RegistryMode regMode = RegistryMode.getMode(registryModeName);
        if ((regMode != null) && (regMode.arePropertiesSupported()))
        {
          propertySource = ((PropertyEnvironment)registry.getPropertySource(opNode));


        }
        else
        {


          propertySource = new PropertyEnvironment(pb != null ? pb.getSources() : null, null, null, null, opNode);
        }
        





        updatePropertyEnvironment(propertySource);
      }
    }
    

    propertySource.setDefaultPropertySource(defaultPS);
    
    return propertySource;
  }
  







  public void resetPropertyEnvironment(boolean resetLocalEnvironment)
  {
    propertySource = null;
    if (resetLocalEnvironment) {
      localPropEnv.clear();
    }
  }
  
  private void updatePropertyEnvironment(PropertyEnvironment pe)
  {
    if (pe != null) {
      synchronized (this)
      {
        int size = localPropEnv.size();
        for (int i = 0; i < size; i++) {
          Object element = localPropEnv.get(i);
          if ((element instanceof String)) {
            pe.suppressProperty((String)element);
          } else if ((element instanceof CopyDirective)) {
            CopyDirective cd = (CopyDirective)element;
            pe.copyPropertyFromSource(cd.getName(), cd.getIndex());
          } else if ((element instanceof PropertyGenerator)) {
            pe.addPropertyGenerator((PropertyGenerator)element);
          }
        }
      }
    }
  }
  
  private void fireEvent(String propName, Object oldVal, Object newVal) {
    if (eventManager != null) {
      Object eventSource = eventManager.getPropertyChangeEventSource();
      PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
      

      eventManager.firePropertyChange(evt);
    }
  }
  












  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    ParameterBlock pbClone = pb;
    boolean pbCloned = false;
    

    for (int index = 0; index < pbClone.getNumSources(); index++) {
      Object source = pbClone.getSource(index);
      if ((source != null) && (!(source instanceof Serializable)))
      {
        if (!pbCloned) {
          pbClone = (ParameterBlock)pb.clone();
          pbCloned = true;
        }
        if ((source instanceof RenderedImage)) {
          SerializableState serializableImage = SerializerFactory.getState(source, null);
          
          pbClone.setSource(serializableImage, index);
        } else {
          throw new RuntimeException(source.getClass().getName() + JaiI18N.getString("OperationNodeSupport0"));
        }
      }
    }
    




    for (int index = 0; index < pbClone.getNumParameters(); index++) {
      Object parameter = pbClone.getObjectParameter(index);
      if ((parameter != null) && (!(parameter instanceof Serializable)))
      {
        if (!pbCloned) {
          pbClone = (ParameterBlock)pb.clone();
          pbCloned = true;
        }
        if ((parameter instanceof Raster)) {
          pbClone.set(SerializerFactory.getState(parameter, null), index);
        } else if ((parameter instanceof RenderedImage)) {
          RenderedImage ri = (RenderedImage)parameter;
          RenderingHints hints = new RenderingHints(null);
          hints.put(JAI.KEY_SERIALIZE_DEEP_COPY, new Boolean(true));
          pbClone.set(SerializerFactory.getState(ri, hints), index);
        }
        else {
          throw new RuntimeException(parameter.getClass().getName() + JaiI18N.getString("OperationNodeSupport1"));
        }
      }
    }
    



    out.defaultWriteObject();
    
    out.writeObject(pbClone);
    
    out.writeObject(SerializerFactory.getState(this.hints, null));
  }
  




  private synchronized void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    
    pb = ((ParameterBlock)in.readObject());
    
    SerializableState ss = (SerializableState)in.readObject();
    hints = ((RenderingHints)ss.getObject());
    

    for (int index = 0; index < pb.getNumSources(); index++) {
      Object source = pb.getSource(index);
      if ((source instanceof SerializableState)) {
        ss = (SerializableState)source;
        PlanarImage pi = PlanarImage.wrapRenderedImage((RenderedImage)ss.getObject());
        
        pb.setSource(pi, index);
      }
    }
    


    for (int index = 0; index < pb.getNumParameters(); index++) {
      Object parameter = pb.getObjectParameter(index);
      if ((parameter instanceof SerializableState)) {
        Object object = ((SerializableState)parameter).getObject();
        if ((object instanceof Raster)) {
          pb.set(object, index);
        } else if ((object instanceof RenderedImage)) {
          pb.set(PlanarImage.wrapRenderedImage((RenderedImage)object), index);
        } else {
          pb.set(object, index);
        }
      }
    }
    registry = JAI.getDefaultInstance().getOperationRegistry();
  }
}
