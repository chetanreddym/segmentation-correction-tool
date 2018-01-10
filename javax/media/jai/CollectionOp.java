package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.registry.CIFRegistry;
import javax.media.jai.registry.RCIFRegistry;


















































































































































































































public class CollectionOp
  extends CollectionImage
  implements OperationNode, PropertyChangeListener
{
  protected OperationNodeSupport nodeSupport;
  protected PropertySource thePropertySource;
  protected boolean isRenderable = false;
  



  private transient RenderingHints oldHints;
  


  private static Set nodeEventNames = null;
  
  static {
    nodeEventNames = new HashSet();
    nodeEventNames.add("operationname");
    nodeEventNames.add("operationregistry");
    nodeEventNames.add("parameterblock");
    nodeEventNames.add("sources");
    nodeEventNames.add("parameters");
    nodeEventNames.add("renderinghints");
  }
  










































  public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints, boolean isRenderable)
  {
    if (opName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (pb == null)
    {
      pb = new ParameterBlock();
    }
    else {
      pb = (ParameterBlock)pb.clone();
    }
    
    if (hints != null)
    {
      hints = (RenderingHints)hints.clone();
    }
    

    eventManager = new PropertyChangeSupportJAI(this);
    
    properties = new WritablePropertySourceImpl(null, null, eventManager);
    
    nodeSupport = new OperationNodeSupport(getRegistryModeName(), opName, registry, pb, hints, eventManager);
    





    this.isRenderable = isRenderable;
    



    addPropertyChangeListener("OperationName", this);
    addPropertyChangeListener("OperationRegistry", this);
    addPropertyChangeListener("ParameterBlock", this);
    addPropertyChangeListener("Sources", this);
    addPropertyChangeListener("Parameters", this);
    addPropertyChangeListener("RenderingHints", this);
    

    Vector nodeSources = pb.getSources();
    if (nodeSources != null) {
      Iterator it = nodeSources.iterator();
      while (it.hasNext()) {
        Object src = it.next();
        if ((src instanceof CollectionImage)) {
          ((CollectionImage)src).addSink(this);
        } else if ((src instanceof PlanarImage)) {
          ((PlanarImage)src).addSink(this);
        }
      }
    }
  }
  

































  public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints)
  {
    this(registry, opName, pb, hints, false);
  }
  





























  public CollectionOp(String opName, ParameterBlock pb, RenderingHints hints)
  {
    this(null, opName, pb, hints);
  }
  































  /**
   * @deprecated
   */
  public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb)
  {
    this(registry, opName, pb, null);
  }
  




  public boolean isRenderable()
  {
    return isRenderable;
  }
  





  public String getRegistryModeName()
  {
    return isRenderable ? "renderableCollection" : "collection";
  }
  








  public synchronized OperationRegistry getRegistry()
  {
    return nodeSupport.getRegistry();
  }
  












  public synchronized void setRegistry(OperationRegistry registry)
  {
    nodeSupport.setRegistry(registry);
  }
  



  public String getOperationName()
  {
    return nodeSupport.getOperationName();
  }
  













  public synchronized void setOperationName(String opName)
  {
    nodeSupport.setOperationName(opName);
  }
  
  public ParameterBlock getParameterBlock()
  {
    return (ParameterBlock)nodeSupport.getParameterBlock().clone();
  }
  































  public synchronized void setParameterBlock(ParameterBlock pb)
  {
    Vector nodeSources = nodeSupport.getParameterBlock().getSources();
    if ((nodeSources != null) && (nodeSources.size() > 0)) {
      Iterator it = nodeSources.iterator();
      while (it.hasNext()) {
        Object src = it.next();
        if ((src instanceof PlanarImage)) {
          ((PlanarImage)src).removeSink(this);
        } else if ((src instanceof CollectionImage)) {
          ((CollectionImage)src).removeSink(this);
        }
      }
    }
    
    if (pb != null) {
      Vector newSources = pb.getSources();
      if ((newSources != null) && (newSources.size() > 0)) {
        Iterator it = newSources.iterator();
        while (it.hasNext()) {
          Object src = it.next();
          if ((src instanceof PlanarImage)) {
            ((PlanarImage)src).addSink(this);
          } else if ((src instanceof CollectionImage)) {
            ((CollectionImage)src).addSink(this);
          }
        }
      }
    }
    
    nodeSupport.setParameterBlock(pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone());
  }
  





  public RenderingHints getRenderingHints()
  {
    RenderingHints hints = nodeSupport.getRenderingHints();
    return hints == null ? null : (RenderingHints)hints.clone();
  }
  












  public synchronized void setRenderingHints(RenderingHints hints)
  {
    if (hints != null) {
      hints = (RenderingHints)hints.clone();
    }
    nodeSupport.setRenderingHints(hints);
  }
  























  public Collection getCollection()
  {
    createCollection();
    return imageCollection;
  }
  
  private synchronized void createCollection()
  {
    if (imageCollection == null) {
      imageCollection = createInstance(true);
    }
  }
  



















  public synchronized Collection createInstance()
  {
    return createInstance(false);
  }
  








  private synchronized Collection createInstance(boolean isChainFrozen)
  {
    ParameterBlock args = ImageUtil.evaluateParameters(nodeSupport.getParameterBlock());
    

    ParameterBlock pb = new ParameterBlock();
    pb.setParameters(args.getParameters());
    
    int numSources = args.getNumSources();
    for (int i = 0; i < numSources; i++) {
      Object source = args.getSource(i);
      Object src = null;
      
      if ((source instanceof RenderedOp)) {
        src = isChainFrozen ? ((RenderedOp)source).getRendering() : ((RenderedOp)source).createInstance();

      }
      else if ((source instanceof CollectionOp)) {
        CollectionOp co = (CollectionOp)source;
        src = isChainFrozen ? co.getCollection() : co.createInstance();

      }
      else if (((source instanceof RenderedImage)) || ((source instanceof RenderableImage)) || ((source instanceof Collection)))
      {

        src = source;
      }
      else {
        src = source;
      }
      pb.addSource(src);
    }
    
    Collection instance = null;
    if (isRenderable) {
      instance = RCIFRegistry.create(nodeSupport.getRegistry(), nodeSupport.getOperationName(), pb);
    }
    else
    {
      CollectionImageFactory cif = CIFRegistry.get(nodeSupport.getRegistry(), nodeSupport.getOperationName());
      

      instance = cif.create(pb, nodeSupport.getRenderingHints());
      

      if (instance != null) {
        ((CollectionImage)instance).setImageFactory(cif);
      }
    }
    

    if (instance == null) {
      throw new RuntimeException(JaiI18N.getString("CollectionOp0"));
    }
    

    oldHints = (nodeSupport.getRenderingHints() == null ? null : (RenderingHints)nodeSupport.getRenderingHints().clone());
    

    return instance;
  }
  



















  public Collection createRendering(RenderContext renderContext)
  {
    if (!isRenderable) {
      return this;
    }
    

    RenderingHints mergedHints = JAI.mergeRenderingHints(nodeSupport.getRenderingHints(), renderContext.getRenderingHints());
    

    if (mergedHints != renderContext.getRenderingHints()) {
      renderContext = (RenderContext)renderContext.clone();
      renderContext.setRenderingHints(mergedHints);
    }
    
    return renderCollection(imageCollection, renderContext);
  }
  





  private Collection renderCollection(Collection cIn, RenderContext rc)
  {
    if ((cIn == null) || (rc == null)) {
      throw new IllegalArgumentException();
    }
    Collection cOut;
    Collection cOut;
    if ((cIn instanceof Set)) {
      cOut = Collections.synchronizedSet(new HashSet(cIn.size())); } else { Collection cOut;
      if ((cIn instanceof SortedSet)) {
        Comparator comparator = ((SortedSet)cIn).comparator();
        cOut = Collections.synchronizedSortedSet(new TreeSet(comparator));
      } else {
        cOut = new Vector(cIn.size());
      }
    }
    Iterator it = cIn.iterator();
    while (it.hasNext()) {
      Object element = it.next();
      if ((element instanceof RenderableImage)) {
        cOut.add(((RenderableImage)cIn).createRendering(rc));
      } else if ((element instanceof Collection)) {
        cOut.add(renderCollection((Collection)element, rc));
      } else {
        cOut.add(element);
      }
    }
    
    return cOut;
  }
  















  public synchronized void propertyChange(PropertyChangeEvent evt)
  {
    if (isRenderable()) { return;
    }
    








    Object evtSrc = evt.getSource();
    Vector nodeSources = nodeSupport.getParameterBlock().getSources();
    


    String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
    
    if ((imageCollection != null) && ((((evt instanceof PropertyChangeEventJAI)) && (evtSrc == this) && (!(evt instanceof PropertySourceChangeEvent)) && (nodeEventNames.contains(propName))) || ((((evt instanceof CollectionChangeEvent)) || ((evt instanceof RenderingChangeEvent))) && (nodeSources.contains(evtSrc)))))
    {








      Collection theOldCollection = imageCollection;
      

      boolean fireEvent = false;
      
      if (!(imageCollection instanceof CollectionImage))
      {


        fireEvent = true;
        imageCollection = null;
      }
      else if ((evtSrc == this) && ((propName.equals("operationname")) || (propName.equals("operationregistry"))))
      {




        fireEvent = true;
        imageCollection = null;
      }
      else if ((evt instanceof CollectionChangeEvent))
      {

        fireEvent = true;
        



        CollectionImageFactory oldCIF = ((CollectionImage)theOldCollection).getImageFactory();
        

        if (oldCIF == null)
        {

          imageCollection = null;

        }
        else
        {
          CollectionChangeEvent ccEvent = (CollectionChangeEvent)evt;
          

          Vector parameters = nodeSupport.getParameterBlock().getParameters();
          
          parameters = ImageUtil.evaluateParameters(parameters);
          ParameterBlock oldPB = new ParameterBlock((Vector)nodeSources.clone(), parameters);
          

          ParameterBlock newPB = new ParameterBlock((Vector)nodeSources.clone(), parameters);
          

          int sourceIndex = nodeSources.indexOf(ccEvent.getSource());
          oldPB.setSource(ccEvent.getOldValue(), sourceIndex);
          newPB.setSource(ccEvent.getNewValue(), sourceIndex);
          

          imageCollection = oldCIF.update(oldPB, oldHints, newPB, oldHints, (CollectionImage)theOldCollection, this);

        }
        


      }
      else
      {


        CollectionImageFactory oldCIF = ((CollectionImage)theOldCollection).getImageFactory();
        

        if ((oldCIF == null) || (oldCIF != CIFRegistry.get(nodeSupport.getRegistry(), nodeSupport.getOperationName())))
        {




          imageCollection = null;
          

          fireEvent = true;

        }
        else
        {

          ParameterBlock oldPB = null;
          ParameterBlock newPB = null;
          
          boolean updateCollection = false;
          
          if (propName.equals("parameterblock")) {
            oldPB = (ParameterBlock)evt.getOldValue();
            newPB = (ParameterBlock)evt.getNewValue();
            updateCollection = true;
          } else if (propName.equals("sources"))
          {
            Vector params = nodeSupport.getParameterBlock().getParameters();
            
            oldPB = new ParameterBlock((Vector)evt.getOldValue(), params);
            
            newPB = new ParameterBlock((Vector)evt.getNewValue(), params);
            
            updateCollection = true;
          } else if (propName.equals("parameters"))
          {
            oldPB = new ParameterBlock(nodeSources, (Vector)evt.getOldValue());
            
            newPB = new ParameterBlock(nodeSources, (Vector)evt.getNewValue());
            
            updateCollection = true;
          } else if (propName.equals("renderinghints")) {
            oldPB = newPB = nodeSupport.getParameterBlock();
            updateCollection = true;
          } else if ((evt instanceof RenderingChangeEvent))
          {


            int renderingIndex = nodeSources.indexOf(evt.getSource());
            
            Vector oldSources = (Vector)nodeSources.clone();
            Vector newSources = (Vector)nodeSources.clone();
            oldSources.set(renderingIndex, evt.getOldValue());
            newSources.set(renderingIndex, evt.getNewValue());
            
            Vector params = nodeSupport.getParameterBlock().getParameters();
            

            oldPB = new ParameterBlock(oldSources, params);
            newPB = new ParameterBlock(newSources, params);
            
            updateCollection = true;
          }
          
          if (updateCollection)
          {
            fireEvent = true;
            

            oldPB = ImageUtil.evaluateParameters(oldPB);
            newPB = ImageUtil.evaluateParameters(newPB);
            

            RenderingHints newHints = nodeSupport.getRenderingHints();
            
            if ((this.imageCollection = oldCIF.update(oldPB, oldHints, newPB, newHints, (CollectionImage)theOldCollection, this)) != null)
            {



              oldHints = newHints;
            }
          }
        }
      }
      


      getCollection();
      

      if (fireEvent)
      {

        resetProperties(true);
        

        CollectionChangeEvent ccEvent = new CollectionChangeEvent(this, theOldCollection, imageCollection);
        




        eventManager.firePropertyChange(ccEvent);
        

        Set sinks = getSinks();
        if (sinks != null) {
          Iterator it = sinks.iterator();
          while (it.hasNext()) {
            Object sink = it.next();
            if ((sink instanceof PropertyChangeListener)) {
              ((PropertyChangeListener)sink).propertyChange(ccEvent);
            }
          }
        }
      }
    }
  }
  


  private synchronized void createPropertySource()
  {
    if (thePropertySource == null) {
      getCollection();
      
      PropertySource defaultPS = null;
      if ((imageCollection instanceof PropertySource))
      {
        defaultPS = new PropertySource()
        {

          public String[] getPropertyNames()
          {
            return ((PropertySource)imageCollection).getPropertyNames();
          }
          
          public String[] getPropertyNames(String prefix) {
            return PropertyUtil.getPropertyNames(getPropertyNames(), prefix);
          }
          
          public Class getPropertyClass(String name)
          {
            return null;
          }
          



          public Object getProperty(String name)
          {
            return ((PropertySource)imageCollection).getProperty(name);
          }
        };
      }
      


      thePropertySource = nodeSupport.getPropertySource(this, defaultPS);
      

      properties.addProperties(thePropertySource);
    }
  }
  








  protected synchronized void resetProperties(boolean resetPropertySource)
  {
    properties.clearCachedProperties();
    if ((resetPropertySource) && (thePropertySource != null)) {
      properties.removePropertySource(thePropertySource);
      thePropertySource = null;
    }
  }
  









  public synchronized String[] getPropertyNames()
  {
    createPropertySource();
    return properties.getPropertyNames();
  }
  











  public Class getPropertyClass(String name)
  {
    createPropertySource();
    return properties.getPropertyClass(name);
  }
  












  public Object getProperty(String name)
  {
    createPropertySource();
    return properties.getProperty(name);
  }
  











  public void setProperty(String name, Object value)
  {
    createPropertySource();
    properties.setProperty(name, value);
  }
  









  public void removeProperty(String name)
  {
    createPropertySource();
    properties.removeProperty(name);
  }
  

















  public synchronized Object getDynamicProperty(String name)
  {
    createPropertySource();
    return thePropertySource.getProperty(name);
  }
  









  public void addPropertyGenerator(PropertyGenerator pg)
  {
    nodeSupport.addPropertyGenerator(pg);
  }
  













  public synchronized void copyPropertyFromSource(String propertyName, int sourceIndex)
  {
    nodeSupport.copyPropertyFromSource(propertyName, sourceIndex);
  }
  


















  public void suppressProperty(String name)
  {
    nodeSupport.suppressProperty(name);
  }
  








  public int size()
  {
    createCollection();
    return imageCollection.size();
  }
  




  public boolean isEmpty()
  {
    createCollection();
    return imageCollection.isEmpty();
  }
  




  public boolean contains(Object o)
  {
    createCollection();
    return imageCollection.contains(o);
  }
  




  public Iterator iterator()
  {
    createCollection();
    return imageCollection.iterator();
  }
  




  public Object[] toArray()
  {
    createCollection();
    return imageCollection.toArray();
  }
  









  public Object[] toArray(Object[] a)
  {
    createCollection();
    return imageCollection.toArray(a);
  }
  



  public boolean add(Object o)
  {
    createCollection();
    return imageCollection.add(o);
  }
  



  public boolean remove(Object o)
  {
    createCollection();
    return imageCollection.remove(o);
  }
  




  public boolean containsAll(Collection c)
  {
    createCollection();
    return imageCollection.containsAll(c);
  }
  




  public boolean addAll(Collection c)
  {
    createCollection();
    return imageCollection.addAll(c);
  }
  




  public boolean removeAll(Collection c)
  {
    createCollection();
    return imageCollection.removeAll(c);
  }
  




  public boolean retainAll(Collection c)
  {
    createCollection();
    return imageCollection.retainAll(c);
  }
  



  public void clear()
  {
    createCollection();
    imageCollection.clear();
  }
}
