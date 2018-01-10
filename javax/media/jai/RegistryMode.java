package javax.media.jai;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.media.jai.registry.CollectionRegistryMode;
import javax.media.jai.registry.RemoteRenderableRegistryMode;
import javax.media.jai.registry.RemoteRenderedRegistryMode;
import javax.media.jai.registry.RenderableCollectionRegistryMode;
import javax.media.jai.registry.RenderableRegistryMode;
import javax.media.jai.registry.RenderedRegistryMode;
import javax.media.jai.registry.TileDecoderRegistryMode;
import javax.media.jai.registry.TileEncoderRegistryMode;
import javax.media.jai.util.CaselessStringKey;
















































public class RegistryMode
{
  private static Hashtable registryModes = new Hashtable(4);
  private static HashSet immutableNames = new HashSet();
  private CaselessStringKey name;
  
  static { addMode(new RenderedRegistryMode(), true);
    addMode(new RenderableRegistryMode(), true);
    addMode(new CollectionRegistryMode(), true);
    addMode(new RenderableCollectionRegistryMode(), true);
    

    addMode(new RemoteRenderedRegistryMode(), true);
    addMode(new RemoteRenderableRegistryMode(), true);
    

    addMode(new TileEncoderRegistryMode(), true);
    addMode(new TileDecoderRegistryMode(), true);
  }
  





  private static boolean addMode(RegistryMode mode, boolean immutable)
  {
    if (registryModes.containsKey(name)) {
      return false;
    }
    registryModes.put(name, mode);
    
    if (immutable) {
      immutableNames.add(name);
    }
    return true;
  }
  










  public static synchronized boolean addMode(RegistryMode mode)
  {
    return addMode(mode, false);
  }
  



  private Class descriptorClass;
  


  private Class productClass;
  


  public static synchronized boolean removeMode(String name)
  {
    CaselessStringKey key = new CaselessStringKey(name);
    
    if (immutableNames.contains(key)) {
      return false;
    }
    return registryModes.remove(key) != null;
  }
  







  public static synchronized String[] getModeNames()
  {
    String[] names = new String[registryModes.size()];
    
    int i = 0;
    
    for (Enumeration e = registryModes.keys(); e.hasMoreElements();) {
      CaselessStringKey key = (CaselessStringKey)e.nextElement();
      
      names[(i++)] = key.getName();
    }
    
    if (i <= 0) {
      return null;
    }
    return names;
  }
  











  public static synchronized String[] getModeNames(Class descriptorClass)
  {
    String[] names = new String[registryModes.size()];
    
    int i = 0;
    
    for (Enumeration e = registryModes.elements(); e.hasMoreElements();) {
      RegistryMode mode = (RegistryMode)e.nextElement();
      
      if (mode.getDescriptorClass() == descriptorClass) {
        names[(i++)] = mode.getName();
      }
    }
    if (i <= 0) {
      return null;
    }
    String[] matchedNames = new String[i];
    
    for (int j = 0; j < i; j++) {
      matchedNames[j] = names[j];
    }
    return matchedNames;
  }
  



  public static RegistryMode getMode(String name)
  {
    CaselessStringKey key = new CaselessStringKey(name);
    
    return (RegistryMode)registryModes.get(key);
  }
  



  public static synchronized Set getDescriptorClasses()
  {
    HashSet set = new HashSet();
    
    for (Enumeration e = registryModes.elements(); e.hasMoreElements();) {
      RegistryMode mode = (RegistryMode)e.nextElement();
      
      set.add(descriptorClass);
    }
    
    return set;
  }
  





  private Method factoryMethod;
  




  private boolean arePreferencesSupported;
  




  private boolean arePropertiesSupported;
  



  protected RegistryMode(String name, Class descriptorClass, Class productClass, Method factoryMethod, boolean arePreferencesSupported, boolean arePropertiesSupported)
  {
    this.name = new CaselessStringKey(name);
    this.descriptorClass = descriptorClass;
    this.productClass = productClass;
    this.factoryMethod = factoryMethod;
    this.arePreferencesSupported = arePreferencesSupported;
    this.arePropertiesSupported = arePropertiesSupported;
  }
  
  public final String getName()
  {
    return name.getName();
  }
  
  public final Method getFactoryMethod()
  {
    return factoryMethod;
  }
  
  public final boolean arePreferencesSupported()
  {
    return arePreferencesSupported;
  }
  


  public final boolean arePropertiesSupported()
  {
    return arePropertiesSupported;
  }
  





  public final Class getDescriptorClass()
  {
    return descriptorClass;
  }
  



  public final Class getProductClass()
  {
    return productClass;
  }
  



  public final Class getFactoryClass()
  {
    return factoryMethod.getDeclaringClass();
  }
}
