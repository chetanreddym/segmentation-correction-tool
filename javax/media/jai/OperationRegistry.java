package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import com.sun.media.jai.util.Service;
import java.awt.RenderingHints;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.registry.CIFRegistry;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;





































































































































































































public class OperationRegistry
  implements Externalizable
{
  static String JAI_REGISTRY_FILE = "META-INF/javax.media.jai.registryFile.jai";
  

  static String USR_REGISTRY_FILE = "META-INF/registryFile.jai";
  




  private Hashtable descriptors;
  




  private Hashtable factories;
  





  private FactoryCache getFactoryCache(String modeName)
  {
    CaselessStringKey key = new CaselessStringKey(modeName);
    
    FactoryCache fc = (FactoryCache)factories.get(key);
    
    if (fc == null)
    {
      if (RegistryMode.getMode(modeName) != null) {
        factories.put(key, fc = new FactoryCache(modeName));
      }
      else {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry0", new Object[] { modeName }));
      }
    }
    

    return fc;
  }
  





  private DescriptorCache getDescriptorCache(String modeName)
  {
    CaselessStringKey key = new CaselessStringKey(modeName);
    
    DescriptorCache dc = (DescriptorCache)descriptors.get(key);
    
    if (dc == null)
    {
      if (RegistryMode.getMode(modeName) != null) {
        descriptors.put(key, dc = new DescriptorCache(modeName));
      }
      else {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry0", new Object[] { modeName }));
      }
    }
    

    return dc;
  }
  








  private void initialize()
  {
    descriptors = new Hashtable();
    


    factories = new Hashtable();
  }
  






  public OperationRegistry()
  {
    initialize();
  }
  








  public static OperationRegistry getThreadSafeOperationRegistry()
  {
    return new ThreadSafeOperationRegistry();
  }
  









  static OperationRegistry initializeRegistry()
  {
    try
    {
      InputStream url = PropertyUtil.getFileFromClasspath(JAI_REGISTRY_FILE);
      
      if (url == null) {
        throw new RuntimeException(JaiI18N.getString("OperationRegistry1"));
      }
      
      OperationRegistry registry = new ThreadSafeOperationRegistry();
      
      if (url != null) {
        RegistryFileParser.loadOperationRegistry(registry, null, url);
      }
      registry.registerServices(null);
      return registry;
    }
    catch (IOException ioe) {
      ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
      
      String message = JaiI18N.getString("OperationRegistry2");
      listener.errorOccurred(message, new ImagingException(message, ioe), OperationRegistry.class, false);
    }
    
    return null;
  }
  










  public String toString()
  {
    StringWriter sw = new StringWriter();
    try
    {
      RegistryFileParser.writeOperationRegistry(this, new BufferedWriter(sw));
      
      return sw.getBuffer().toString();
    }
    catch (Exception e) {
      return "\n[ERROR!] " + e.getMessage();
    }
  }
  











  public void writeToStream(OutputStream out)
    throws IOException
  {
    if (out == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    RegistryFileParser.writeOperationRegistry(this, out);
  }
  





















  public void initializeFromStream(InputStream in)
    throws IOException
  {
    if (in == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    initialize();
    updateFromStream(in);
  }
  
























  public void updateFromStream(InputStream in)
    throws IOException
  {
    if (in == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    RegistryFileParser.loadOperationRegistry(this, null, in);
  }
  























  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    if (in == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    byte[] barray = (byte[])in.readObject();
    InputStream s = new ByteArrayInputStream(barray);
    initializeFromStream(s);
  }
  















































































  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    if (out == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ByteArrayOutputStream bstream = new ByteArrayOutputStream();
    writeToStream(bstream);
    out.writeObject(bstream.toByteArray());
  }
  




















  public void removeRegistryMode(String modeName)
  {
    if (getDescriptorCache(modeName) != null) {
      descriptors.remove(new CaselessStringKey(modeName));
    }
    if (getFactoryCache(modeName) != null) {
      factories.remove(new CaselessStringKey(modeName));
    }
  }
  






  public String[] getRegistryModes()
  {
    Enumeration e = descriptors.keys();
    int size = descriptors.size();
    String[] names = new String[size];
    
    for (int i = 0; i < size; i++) {
      CaselessStringKey key = (CaselessStringKey)e.nextElement();
      names[i] = key.getName();
    }
    
    return names;
  }
  
























  public void registerDescriptor(RegistryElementDescriptor descriptor)
  {
    if (descriptor == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    String[] supportedModes = descriptor.getSupportedModes();
    
    String descriptorName = descriptor.getName();
    


    for (int i = 0; i < supportedModes.length; i++) {
      if (RegistryMode.getMode(supportedModes[i]) == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry3", new Object[] { descriptorName, supportedModes[i] }));
      }
    }
    


    for (int i = 0; i < supportedModes.length; i++)
    {
      DescriptorCache dc = getDescriptorCache(supportedModes[i]);
      
      dc.addDescriptor(descriptor);
    }
  }
  

















  public void unregisterDescriptor(RegistryElementDescriptor descriptor)
  {
    if (descriptor == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    String descriptorName = descriptor.getName();
    
    String[] supportedModes = descriptor.getSupportedModes();
    


    for (int i = 0; i < supportedModes.length; i++) {
      if (RegistryMode.getMode(supportedModes[i]) == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry3", new Object[] { descriptorName, supportedModes[i] }));
      }
    }
    


    for (int i = 0; i < supportedModes.length; i++)
    {
      DescriptorCache dc = getDescriptorCache(supportedModes[i]);
      
      dc.removeDescriptor(descriptor);
    }
  }
  























  public RegistryElementDescriptor getDescriptor(Class descriptorClass, String descriptorName)
  {
    if ((descriptorClass == null) || (descriptorName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    String[] supportedModes = RegistryMode.getModeNames(descriptorClass);
    
    if (supportedModes == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry4", new Object[] { descriptorClass.getName() }));
    }
    




    for (int i = 0; i < supportedModes.length; i++)
    {
      DescriptorCache dc = getDescriptorCache(supportedModes[i]);
      RegistryElementDescriptor red;
      if ((red = dc.getDescriptor(descriptorName)) != null) {
        return red;
      }
    }
    return null;
  }
  















  public List getDescriptors(Class descriptorClass)
  {
    if (descriptorClass == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    String[] supportedModes = RegistryMode.getModeNames(descriptorClass);
    
    if (supportedModes == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry4", new Object[] { descriptorClass.getName() }));
    }
    


    HashSet set = new HashSet();
    

    for (int i = 0; i < supportedModes.length; i++)
    {
      DescriptorCache dc = getDescriptorCache(supportedModes[i]);
      List list;
      if ((list = dc.getDescriptors()) != null) {
        set.addAll(list);
      }
    }
    return new ArrayList(set);
  }
  















  public String[] getDescriptorNames(Class descriptorClass)
  {
    List dlist = getDescriptors(descriptorClass);
    
    if (dlist != null)
    {
      Iterator diter = dlist.iterator();
      
      String[] names = new String[dlist.size()];
      int i = 0;
      
      while (diter.hasNext()) {
        RegistryElementDescriptor red = (RegistryElementDescriptor)diter.next();
        

        names[(i++)] = red.getName();
      }
      
      return names;
    }
    
    return null;
  }
  




















  public RegistryElementDescriptor getDescriptor(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getDescriptor(descriptorName);
    }
    return null;
  }
  











  public List getDescriptors(String modeName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getDescriptors();
    }
    return null;
  }
  











  public String[] getDescriptorNames(String modeName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getDescriptorNames();
    }
    return null;
  }
  
































  public void setProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.setProductPreference(descriptorName, preferredProductName, otherProductName);
    }
  }
  



























  public void unsetProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.unsetProductPreference(descriptorName, preferredProductName, otherProductName);
    }
  }
  

















  public void clearProductPreferences(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.clearProductPreferences(descriptorName);
    }
  }
  


















  public String[][] getProductPreferences(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getProductPreferences(descriptorName);
    }
    return (String[][])null;
  }
  
























  public Vector getOrderedProductList(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getOrderedProductList(descriptorName);
    }
    return null;
  }
  











  String getLocalName(String modeName, Object factoryInstance)
  {
    FactoryCache fc = getFactoryCache(modeName);
    
    if (fc != null) {
      return fc.getLocalName(factoryInstance);
    }
    return null;
  }
  
























  public void registerFactory(String modeName, String descriptorName, String productName, Object factory)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    if (factory == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (arePreferencesSupported)
    {
      OperationGraph og = dc.addProduct(descriptorName, productName);
      

      if (og == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
      }
      


      og.addOp(new PartialOrderNode(factory, factory.getClass().getName()));
    }
    

    fc.addFactory(descriptorName, productName, factory);
  }
  



























  public void unregisterFactory(String modeName, String descriptorName, String productName, Object factory)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    if (factory == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    fc.removeFactory(descriptorName, productName, factory);
    
    if (arePreferencesSupported)
    {
      OperationGraph og = dc.lookupProduct(descriptorName, productName);
      

      if (og == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
      }
      


      og.removeOp(factory);
    }
  }
  





























  public void setFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    




    fc.setPreference(descriptorName, productName, preferredOp, otherOp);
    

    if (arePreferencesSupported)
    {
      OperationGraph og = dc.lookupProduct(descriptorName, productName);
      

      if (og == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
      }
      


      og.setPreference(preferredOp, otherOp);
    }
  }
  





























  public void unsetFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    




    fc.unsetPreference(descriptorName, productName, preferredOp, otherOp);
    

    if (arePreferencesSupported)
    {
      OperationGraph og = dc.lookupProduct(descriptorName, productName);
      

      if (og == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
      }
      


      og.unsetPreference(preferredOp, otherOp);
    }
  }
  





















  public void clearFactoryPreferences(String modeName, String descriptorName, String productName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    Object[][] prefs = fc.getPreferences(descriptorName, productName);
    
    if (prefs != null)
    {
      OperationGraph og = dc.lookupProduct(descriptorName, productName);
      

      if (og == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
      }
      


      for (int i = 0; i < prefs.length; i++) {
        og.unsetPreference(prefs[i][0], prefs[i][1]);
      }
    }
    
    fc.clearPreferences(descriptorName, productName);
  }
  





















  public Object[][] getFactoryPreferences(String modeName, String descriptorName, String productName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    return fc.getPreferences(descriptorName, productName);
  }
  






























  public List getOrderedFactoryList(String modeName, String descriptorName, String productName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    if (arePreferencesSupported)
    {
      OperationGraph og = dc.lookupProduct(descriptorName, productName);
      

      if (og == null) {
        return null;
      }
      Vector v = og.getOrderedOperationList();
      
      if ((v == null) || (v.size() <= 0)) {
        return null;
      }
      ArrayList list = new ArrayList(v.size());
      
      for (int i = 0; i < v.size(); i++) {
        list.add(((PartialOrderNode)v.elementAt(i)).getData());
      }
      
      return list;
    }
    
    return fc.getFactoryList(descriptorName, productName);
  }
  




























  public Iterator getFactoryIterator(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    FactoryCache fc = getFactoryCache(modeName);
    
    if (dc.getDescriptor(descriptorName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[] { descriptorName, modeName }));
    }
    


    if (arePreferencesSupported) {
      Vector v = getOrderedProductList(modeName, descriptorName);
      
      if ((v == null) || (v.size() <= 0)) {
        return null;
      }
      ArrayList list = new ArrayList();
      


      for (int i = 0; i < v.size(); i++) {
        List plist = getOrderedFactoryList(modeName, descriptorName, (String)v.get(i));
        
        if (plist != null) {
          list.addAll(plist);
        }
      }
      return list.iterator();
    }
    
    List list = fc.getFactoryList(descriptorName, null);
    
    if (list != null) {
      return list.iterator();
    }
    
    return null;
  }
  





















  public Object getFactory(String modeName, String descriptorName)
  {
    Iterator it = getFactoryIterator(modeName, descriptorName);
    
    if ((it != null) && (it.hasNext())) {
      return it.next();
    }
    return null;
  }
  
























  public Object invokeFactory(String modeName, String descriptorName, Object[] args)
  {
    Iterator it = getFactoryIterator(modeName, descriptorName);
    
    if (it == null) {
      return null;
    }
    FactoryCache fc = getFactoryCache(modeName);
    ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
    
    Exception savedOne = null;
    
    while (it.hasNext())
    {
      Object factory = it.next();
      try
      {
        Object obj;
        if ((obj = fc.invoke(factory, args)) != null)
          return obj;
        savedOne = null;
      } catch (Exception e) {
        listener.errorOccurred(JaiI18N.getString("OperationRegistry6") + " \"" + descriptorName + "\"", e, this, false);
        

        savedOne = e;
      }
    }
    

    if (savedOne != null) {
      throw new ImagingException(JaiI18N.getString("OperationRegistry7") + " \"" + descriptorName + "\"", savedOne);
    }
    

    return null;
  }
  


































  public void addPropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.addPropertyGenerator(descriptorName, generator);
    }
  }
  























  public void removePropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.removePropertyGenerator(descriptorName, generator);
    }
  }
  

























  public void copyPropertyFromSource(String modeName, String descriptorName, String propertyName, int sourceIndex)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.copyPropertyFromSource(descriptorName, propertyName, sourceIndex);
    }
  }
  























  public void suppressProperty(String modeName, String descriptorName, String propertyName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.suppressProperty(descriptorName, propertyName);
    }
  }
  




















  public void suppressAllProperties(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.suppressAllProperties(descriptorName);
    }
  }
  












  public void clearPropertyState(String modeName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      dc.clearPropertyState();
    }
  }
  





















  public String[] getGeneratedPropertyNames(String modeName, String descriptorName)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getGeneratedPropertyNames(descriptorName);
    }
    return null;
  }
  


































  public PropertySource getPropertySource(String modeName, String descriptorName, Object op, Vector sources)
  {
    DescriptorCache dc = getDescriptorCache(modeName);
    
    if (dc != null) {
      return dc.getPropertySource(descriptorName, op, sources);
    }
    return null;
  }
  
















  public PropertySource getPropertySource(OperationNode op)
  {
    if (op == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    ParameterBlock pb = op.getParameterBlock();
    Vector pv = pb == null ? null : pb.getSources();
    





    if (pv == null) {
      pv = new Vector();
    }
    
    return getPropertySource(op.getRegistryModeName(), op.getOperationName(), op, pv);
  }
  








  public void registerServices(ClassLoader cl)
    throws IOException
  {
    Enumeration en;
    







    Enumeration en;
    






    if (cl == null) {
      en = ClassLoader.getSystemResources(USR_REGISTRY_FILE);
    } else {
      en = cl.getResources(USR_REGISTRY_FILE);
    }
    while (en.hasMoreElements()) {
      URL url = (URL)en.nextElement();
      
      RegistryFileParser.loadOperationRegistry(this, cl, url);
    }
    
    Iterator spitr;
    
    Iterator spitr;
    
    if (cl == null) {
      spitr = Service.providers(OperationRegistrySpi.class);
    } else {
      spitr = Service.providers(OperationRegistrySpi.class, cl);
    }
    while (spitr.hasNext())
    {
      OperationRegistrySpi ospi = (OperationRegistrySpi)spitr.next();
      ospi.updateRegistry(this);
    }
  }
  















  /**
   * @deprecated
   */
  public void registerOperationDescriptor(OperationDescriptor odesc, String operationName)
  {
    registerDescriptor(odesc);
  }
  









  /**
   * @deprecated
   */
  public void unregisterOperationDescriptor(String operationName)
  {
    String[] operationModes = RegistryMode.getModeNames(OperationDescriptor.class);
    



    for (int i = 0; i < operationModes.length; i++) { RegistryElementDescriptor red;
      if ((red = getDescriptor(operationModes[i], operationName)) != null) {
        unregisterDescriptor(red);
      }
    }
  }
  












  /**
   * @deprecated
   */
  public OperationDescriptor getOperationDescriptor(String operationName)
  {
    return (OperationDescriptor)getDescriptor(OperationDescriptor.class, operationName);
  }
  










  /**
   * @deprecated
   */
  public Vector getOperationDescriptors()
  {
    List list = getDescriptors(OperationDescriptor.class);
    
    return list == null ? null : new Vector(list);
  }
  







  /**
   * @deprecated
   */
  public String[] getOperationNames()
  {
    return getDescriptorNames(OperationDescriptor.class);
  }
  















  /**
   * @deprecated
   */
  public void registerRIF(String operationName, String productName, RenderedImageFactory RIF)
  {
    registerFactory("rendered", operationName, productName, RIF);
  }
  













  /**
   * @deprecated
   */
  public void unregisterRIF(String operationName, String productName, RenderedImageFactory RIF)
  {
    unregisterFactory("rendered", operationName, productName, RIF);
  }
  













  /**
   * @deprecated
   */
  public void registerCRIF(String operationName, ContextualRenderedImageFactory CRIF)
  {
    registerFactory("renderable", operationName, null, CRIF);
  }
  












  /**
   * @deprecated
   */
  public void unregisterCRIF(String operationName, ContextualRenderedImageFactory CRIF)
  {
    unregisterFactory("renderable", operationName, null, CRIF);
  }
  













  /**
   * @deprecated
   */
  public void registerCIF(String operationName, String productName, CollectionImageFactory CIF)
  {
    registerFactory("collection", operationName, productName, CIF);
  }
  













  /**
   * @deprecated
   */
  public void unregisterCIF(String operationName, String productName, CollectionImageFactory CIF)
  {
    unregisterFactory("collection", operationName, productName, CIF);
  }
  

















  /**
   * @deprecated
   */
  public void setProductPreference(String operationName, String preferredProductName, String otherProductName)
  {
    setProductPreference("rendered", operationName, preferredProductName, otherProductName);
  }
  















  /**
   * @deprecated
   */
  public void unsetProductPreference(String operationName, String preferredProductName, String otherProductName)
  {
    unsetProductPreference("rendered", operationName, preferredProductName, otherProductName);
  }
  











  /**
   * @deprecated
   */
  public void clearProductPreferences(String operationName)
  {
    clearProductPreferences("rendered", operationName);
  }
  












  /**
   * @deprecated
   */
  public String[][] getProductPreferences(String operationName)
  {
    return getProductPreferences("rendered", operationName);
  }
  















  /**
   * @deprecated
   */
  public Vector getOrderedProductList(String operationName)
  {
    return getOrderedProductList("rendered", operationName);
  }
  




















  /**
   * @deprecated
   */
  public void setRIFPreference(String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF)
  {
    setFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
  }
  



















  /**
   * @deprecated
   */
  public void setCIFPreference(String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF)
  {
    setFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
  }
  


















  /**
   * @deprecated
   */
  public void unsetRIFPreference(String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF)
  {
    unsetFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
  }
  


















  /**
   * @deprecated
   */
  public void unsetCIFPreference(String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF)
  {
    unsetFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
  }
  













  /**
   * @deprecated
   */
  public void clearRIFPreferences(String operationName, String productName)
  {
    clearFactoryPreferences("rendered", operationName, productName);
  }
  













  /**
   * @deprecated
   */
  public void clearCIFPreferences(String operationName, String productName)
  {
    clearFactoryPreferences("collection", operationName, productName);
  }
  














  /**
   * @deprecated
   */
  public void clearOperationPreferences(String operationName, String productName)
  {
    String[] operationModes = RegistryMode.getModeNames(OperationDescriptor.class);
    

    for (int i = 0; i < operationModes.length; i++)
    {
      DescriptorCache dc = getDescriptorCache(operationModes[i]);
      
      if (arePreferencesSupported)
      {

        if (getDescriptor(operationModes[i], operationName) != null)
        {

          clearFactoryPreferences(operationModes[i], operationName, productName);
        }
      }
    }
  }
  


















  /**
   * @deprecated
   */
  public Vector getOrderedRIFList(String operationName, String productName)
  {
    List list = getOrderedFactoryList("rendered", operationName, productName);
    

    return list == null ? null : new Vector(list);
  }
  



















  /**
   * @deprecated
   */
  public Vector getOrderedCIFList(String operationName, String productName)
  {
    List list = getOrderedFactoryList("collection", operationName, productName);
    

    return list == null ? null : new Vector(list);
  }
  

























  /**
   * @deprecated
   */
  public PlanarImage create(String operationName, ParameterBlock paramBlock, RenderingHints renderHints)
  {
    return PlanarImage.wrapRenderedImage(RIFRegistry.create(this, operationName, paramBlock, renderHints));
  }
  
















  /**
   * @deprecated
   */
  public ContextualRenderedImageFactory createRenderable(String operationName, ParameterBlock paramBlock)
  {
    return CRIFRegistry.get(this, operationName);
  }
  






















  /**
   * @deprecated
   */
  public CollectionImage createCollection(String operationName, ParameterBlock args, RenderingHints hints)
  {
    return CIFRegistry.create(this, operationName, args, hints);
  }
  









  /**
   * @deprecated
   */
  public void clearPropertyState()
  {
    clearPropertyState("rendered");
  }
  











  /**
   * @deprecated
   */
  public void addPropertyGenerator(String operationName, PropertyGenerator generator)
  {
    addPropertyGenerator("rendered", operationName, generator);
  }
  













  /**
   * @deprecated
   */
  public void removePropertyGenerator(String operationName, PropertyGenerator generator)
  {
    removePropertyGenerator("rendered", operationName, generator);
  }
  












  /**
   * @deprecated
   */
  public void suppressProperty(String operationName, String propertyName)
  {
    suppressProperty("rendered", operationName, propertyName);
  }
  










  /**
   * @deprecated
   */
  public void suppressAllProperties(String operationName)
  {
    suppressAllProperties("rendered", operationName);
  }
  
















  /**
   * @deprecated
   */
  public void copyPropertyFromSource(String operationName, String propertyName, int sourceIndex)
  {
    copyPropertyFromSource("rendered", operationName, propertyName, sourceIndex);
  }
  













  /**
   * @deprecated
   */
  public String[] getGeneratedPropertyNames(String operationName)
  {
    return getGeneratedPropertyNames("rendered", operationName);
  }
  














  /**
   * @deprecated
   */
  public PropertySource getPropertySource(RenderedOp op)
  {
    return RIFRegistry.getPropertySource(op);
  }
  














  /**
   * @deprecated
   */
  public PropertySource getPropertySource(RenderableOp op)
  {
    return CRIFRegistry.getPropertySource(op);
  }
}
