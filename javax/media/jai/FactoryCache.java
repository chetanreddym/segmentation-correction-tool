package javax.media.jai;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.util.CaselessStringKey;


















































class FactoryCache
{
  final String modeName;
  final RegistryMode mode;
  final Class factoryClass;
  final Method factoryMethod;
  final boolean arePreferencesSupported;
  private Hashtable instances;
  private Hashtable instancesByName;
  private int count = 0;
  






  private Hashtable prefs;
  







  FactoryCache(String modeName)
  {
    this.modeName = modeName;
    
    mode = RegistryMode.getMode(modeName);
    factoryClass = mode.getFactoryClass();
    factoryMethod = mode.getFactoryMethod();
    arePreferencesSupported = mode.arePreferencesSupported();
    
    instances = new Hashtable();
    
    if (arePreferencesSupported) {
      instancesByName = new Hashtable();
      prefs = new Hashtable();
    }
  }
  














  Object invoke(Object factoryInstance, Object[] parameterValues)
    throws InvocationTargetException, IllegalAccessException
  {
    return factoryMethod.invoke(factoryInstance, parameterValues);
  }
  












  void addFactory(String descriptorName, String productName, Object factoryInstance)
  {
    checkInstance(factoryInstance);
    
    if (arePreferencesSupported)
    {
      if (productName == null) {
        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
      }
      

      Vector v = new Vector();
      
      v.add(factoryInstance.getClass().getName());
      v.add(productName);
      v.add(descriptorName);
      
      CaselessStringKey fileName = new CaselessStringKey(modeName + count);
      

      instancesByName.put(factoryInstance, fileName);
      instances.put(fileName, v);
      count += 1;
    }
    else {
      instances.put(new CaselessStringKey(descriptorName), factoryInstance);
    }
  }
  









  void removeFactory(String descriptorName, String productName, Object factoryInstance)
  {
    checkInstance(factoryInstance);
    checkRegistered(descriptorName, productName, factoryInstance);
    
    if (arePreferencesSupported)
    {


      CaselessStringKey fileName = (CaselessStringKey)instancesByName.get(factoryInstance);
      

      instancesByName.remove(factoryInstance);
      instances.remove(fileName);
      count -= 1;
    } else {
      instances.remove(new CaselessStringKey(descriptorName));
    }
  }
  












  void setPreference(String descriptorName, String productName, Object preferredOp, Object otherOp)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[] { modeName }));
    }
    


    if ((preferredOp == null) || (otherOp == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    checkRegistered(descriptorName, productName, preferredOp);
    checkRegistered(descriptorName, productName, otherOp);
    
    if (preferredOp == otherOp) {
      return;
    }
    checkInstance(preferredOp);
    checkInstance(otherOp);
    
    CaselessStringKey dn = new CaselessStringKey(descriptorName);
    CaselessStringKey pn = new CaselessStringKey(productName);
    
    Hashtable dht = (Hashtable)prefs.get(dn);
    
    if (dht == null) {
      prefs.put(dn, dht = new Hashtable());
    }
    
    Vector pv = (Vector)dht.get(pn);
    
    if (pv == null) {
      dht.put(pn, pv = new Vector());
    }
    
    pv.addElement(new Object[] { preferredOp, otherOp });
  }
  












  void unsetPreference(String descriptorName, String productName, Object preferredOp, Object otherOp)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[] { modeName }));
    }
    


    if ((preferredOp == null) || (otherOp == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    checkRegistered(descriptorName, productName, preferredOp);
    checkRegistered(descriptorName, productName, otherOp);
    
    if (preferredOp == otherOp) {
      return;
    }
    checkInstance(preferredOp);
    checkInstance(otherOp);
    

    Hashtable dht = (Hashtable)prefs.get(new CaselessStringKey(descriptorName));
    

    boolean found = false;
    
    if (dht != null)
    {
      Vector pv = (Vector)dht.get(new CaselessStringKey(productName));
      

      if (pv != null) {
        Iterator it = pv.iterator();
        
        while (it.hasNext()) {
          Object[] objs = (Object[])it.next();
          
          if ((objs[0] == preferredOp) && (objs[1] == otherOp))
          {

            it.remove();
            found = true;
          }
        }
      }
    }
    

    if (!found) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache2", new Object[] { preferredOp.getClass().getName(), otherOp.getClass().getName(), modeName, descriptorName, productName }));
    }
  }
  












  Object[][] getPreferences(String descriptorName, String productName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[] { modeName }));
    }
    


    if ((descriptorName == null) || (productName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Hashtable dht = (Hashtable)prefs.get(new CaselessStringKey(descriptorName));
    

    if (dht != null)
    {
      Vector pv = (Vector)dht.get(new CaselessStringKey(productName));
      

      if (pv != null) {
        return (Object[][])pv.toArray(new Object[0][]);
      }
    }
    
    return (Object[][])null;
  }
  








  void clearPreferences(String descriptorName, String productName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[] { modeName }));
    }
    



    Hashtable dht = (Hashtable)prefs.get(new CaselessStringKey(descriptorName));
    

    if (dht != null) {
      dht.remove(new CaselessStringKey(productName));
    }
  }
  



  List getFactoryList(String descriptorName, String productName)
  {
    if (arePreferencesSupported)
    {
      ArrayList list = new ArrayList();
      
      Enumeration keys = instancesByName.keys();
      
      while (keys.hasMoreElements()) {
        Object instance = keys.nextElement();
        CaselessStringKey fileName = (CaselessStringKey)instancesByName.get(instance);
        

        Vector v = (Vector)instances.get(fileName);
        
        String dn = (String)v.get(2);
        String pn = (String)v.get(1);
        
        if ((descriptorName.equalsIgnoreCase(dn)) && (productName.equalsIgnoreCase(pn)))
        {
          list.add(instance);
        }
      }
      return list;
    }
    
    Object obj = instances.get(new CaselessStringKey(descriptorName));
    

    ArrayList list = new ArrayList(1);
    
    list.add(obj);
    return list;
  }
  



  String getLocalName(Object factoryInstance)
  {
    CaselessStringKey fileName = (CaselessStringKey)instancesByName.get(factoryInstance);
    

    if (fileName != null) {
      return fileName.getName();
    }
    return null;
  }
  




  private boolean checkInstance(Object factoryInstance)
  {
    if (!factoryClass.isInstance(factoryInstance)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache0", new Object[] { factoryInstance.getClass().getName(), modeName, factoryClass.getName() }));
    }
    





    return true;
  }
  





  private void checkRegistered(String descriptorName, String productName, Object factoryInstance)
  {
    if (arePreferencesSupported)
    {
      if (productName == null) {
        throw new IllegalArgumentException("productName : " + JaiI18N.getString("Generic0"));
      }
      
      CaselessStringKey fileName = (CaselessStringKey)instancesByName.get(factoryInstance);
      

      if (fileName != null)
      {
        Vector v = (Vector)instances.get(fileName);
        
        String pn = (String)v.get(1);
        String dn = (String)v.get(2);
        
        if ((dn != null) && (dn.equalsIgnoreCase(descriptorName)) && (pn != null) && (pn.equalsIgnoreCase(productName)))
        {
          return;
        }
      }
      
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache3", new Object[] { factoryInstance.getClass().getName(), descriptorName, productName }));
    }
    



    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (factoryInstance != instances.get(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache4", new Object[] { factoryInstance.getClass().getName(), descriptorName }));
    }
  }
}
