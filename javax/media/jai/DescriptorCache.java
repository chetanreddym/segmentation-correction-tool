package javax.media.jai;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.util.CaselessStringKey;



























































































class DescriptorCache
{
  final String modeName;
  final RegistryMode mode;
  final boolean arePreferencesSupported;
  final boolean arePropertiesSupported;
  private Hashtable descriptorNames;
  private Hashtable products;
  private Hashtable productPrefs;
  private Hashtable properties;
  private Hashtable suppressed;
  private Hashtable sourceForProp;
  private Hashtable propNames;
  
  DescriptorCache(String modeName)
  {
    this.modeName = modeName;
    mode = RegistryMode.getMode(modeName);
    
    arePreferencesSupported = mode.arePreferencesSupported();
    arePropertiesSupported = mode.arePropertiesSupported();
    
    descriptorNames = new Hashtable();
    products = new Hashtable();
    
    if (arePreferencesSupported) {
      productPrefs = new Hashtable();
    }
    
    properties = new Hashtable();
    suppressed = new Hashtable();
    sourceForProp = new Hashtable();
    propNames = new Hashtable();
  }
  
















  boolean addDescriptor(RegistryElementDescriptor rdesc)
  {
    if (rdesc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    String descriptorName = rdesc.getName();
    

    CaselessStringKey key = new CaselessStringKey(descriptorName);
    

    if (descriptorNames.containsKey(key) == true) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache0", new Object[] { descriptorName, modeName }));
    }
    



    descriptorNames.put(key, rdesc);
    


    if (arePreferencesSupported) {
      products.put(key, new ProductOperationGraph());
    }
    
    if (!rdesc.arePropertiesSupported()) {
      return true;
    }
    

    PropertyGenerator[] props = rdesc.getPropertyGenerators(modeName);
    
    if (props != null) {
      for (int i = 0; i < props.length; i++)
      {
        Vector v = (Vector)properties.get(key);
        if (v == null) {
          v = new Vector();
          v.addElement(props[i]);
          properties.put(key, v);
        } else {
          v.addElement(props[i]);
        }
        
        v = (Vector)suppressed.get(key);
        Hashtable h = (Hashtable)sourceForProp.get(key);
        String[] names = props[i].getPropertyNames();
        
        for (int j = 0; j < names.length; j++) {
          CaselessStringKey name = new CaselessStringKey(names[j]);
          
          if (v != null) v.remove(name);
          if (h != null) { h.remove(name);
          }
        }
      }
    }
    return true;
  }
  














  boolean removeDescriptor(String descriptorName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    

    if (!descriptorNames.containsKey(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[] { descriptorName, modeName }));
    }
    


    RegistryElementDescriptor rdesc = (RegistryElementDescriptor)descriptorNames.get(key);
    

    PropertyGenerator[] props = null;
    

    if (rdesc.arePropertiesSupported() == true) {
      props = rdesc.getPropertyGenerators(modeName);
    }
    
    if (props != null) {
      for (int i = 0; i < props.length; i++)
      {
        if (props[i] == null) {
          throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache2", new Object[] { new Integer(i), descriptorName, modeName }));
        }
        




        Vector v = (Vector)properties.get(key);
        if (v != null) {
          v.removeElement(props[i]);
        }
      }
    }
    

    descriptorNames.remove(key);
    
    if (arePreferencesSupported) {
      products.remove(key);
    }
    return true;
  }
  













  boolean removeDescriptor(RegistryElementDescriptor rdesc)
  {
    if (rdesc == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    return removeDescriptor(rdesc.getName());
  }
  











  RegistryElementDescriptor getDescriptor(String descriptorName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    return (RegistryElementDescriptor)descriptorNames.get(key);
  }
  






  List getDescriptors()
  {
    ArrayList list = new ArrayList();
    
    Enumeration en = descriptorNames.elements();
    while (en.hasMoreElements()) {
      list.add(en.nextElement());
    }
    
    return list;
  }
  






  String[] getDescriptorNames()
  {
    Enumeration e = descriptorNames.keys();
    int size = descriptorNames.size();
    String[] names = new String[size];
    
    for (int i = 0; i < size; i++) {
      CaselessStringKey key = (CaselessStringKey)e.nextElement();
      names[i] = key.getName();
    }
    
    return names;
  }
  














  OperationGraph addProduct(String descriptorName, String productName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (productName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ProductOperationGraph pog = (ProductOperationGraph)products.get(key);
    

    if (pog == null) {
      return null;
    }
    PartialOrderNode pon = pog.lookupOp(productName);
    
    if (pon == null) {
      pog.addProduct(productName);
      
      pon = pog.lookupOp(productName);
    }
    
    return (OperationGraph)pon.getData();
  }
  













  boolean removeProduct(String descriptorName, String productName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (productName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ProductOperationGraph pog = (ProductOperationGraph)products.get(key);
    

    if (pog == null) {
      return false;
    }
    PartialOrderNode pon = pog.lookupOp(productName);
    
    if (pon == null) {
      return false;
    }
    pog.removeOp(productName);
    
    return true;
  }
  













  OperationGraph lookupProduct(String descriptorName, String productName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (productName == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    ProductOperationGraph pog = (ProductOperationGraph)products.get(key);
    

    if (pog == null) {
      return null;
    }
    PartialOrderNode pon = pog.lookupOp(productName);
    
    if (pon == null) {
      return null;
    }
    return (OperationGraph)pon.getData();
  }
  























  boolean setProductPreference(String descriptorName, String preferredProductName, String otherProductName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[] { modeName }));
    }
    


    if ((descriptorName == null) || (preferredProductName == null) || (otherProductName == null))
    {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (preferredProductName.equalsIgnoreCase(otherProductName)) {
      return false;
    }
    

    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (!descriptorNames.containsKey(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[] { descriptorName, modeName }));
    }
    


    ProductOperationGraph og = (ProductOperationGraph)products.get(key);
    
    if (og == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[] { descriptorName, modeName }));
    }
    


    if (og.lookupOp(preferredProductName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, preferredProductName }));
    }
    



    if (og.lookupOp(otherProductName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, otherProductName }));
    }
    



    og.setPreference(preferredProductName, otherProductName);
    
    String[] prefs = { preferredProductName, otherProductName };
    

    if (!productPrefs.containsKey(key)) {
      Vector v = new Vector();
      v.addElement(prefs);
      
      productPrefs.put(key, v);
    }
    else {
      Vector v = (Vector)productPrefs.get(key);
      v.addElement(prefs);
    }
    
    return true;
  }
  





















  boolean unsetProductPreference(String descriptorName, String preferredProductName, String otherProductName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[] { modeName }));
    }
    


    if ((descriptorName == null) || (preferredProductName == null) || (otherProductName == null))
    {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    if (preferredProductName.equalsIgnoreCase(otherProductName)) {
      return false;
    }
    

    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (!descriptorNames.containsKey(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[] { descriptorName, modeName }));
    }
    


    ProductOperationGraph og = (ProductOperationGraph)products.get(key);
    
    if (og == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[] { descriptorName, modeName }));
    }
    


    if (og.lookupOp(preferredProductName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, preferredProductName }));
    }
    



    if (og.lookupOp(otherProductName) == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, otherProductName }));
    }
    



    og.unsetPreference(preferredProductName, otherProductName);
    


    if (!productPrefs.containsKey(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache5", new Object[] { descriptorName, modeName }));
    }
    


    Vector v = (Vector)productPrefs.get(key);
    Iterator it = v.iterator();
    while (it.hasNext()) {
      String[] prefs = (String[])it.next();
      
      if ((prefs[0].equalsIgnoreCase(preferredProductName)) && (prefs[1].equalsIgnoreCase(otherProductName)))
      {
        it.remove();
        break;
      }
    }
    
    return true;
  }
  











  boolean clearProductPreferences(String descriptorName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[] { modeName }));
    }
    



    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (!descriptorNames.containsKey(key)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[] { descriptorName, modeName }));
    }
    


    ProductOperationGraph og = (ProductOperationGraph)products.get(key);
    
    if (og == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[] { descriptorName, modeName }));
    }
    



    if (!productPrefs.containsKey(key)) {
      return true;
    }
    Vector v = (Vector)productPrefs.get(key);
    Enumeration e = v.elements();
    
    while (e.hasMoreElements()) {
      String[] prefs = (String[])e.nextElement();
      
      String pref = prefs[0];
      String other = prefs[1];
      
      if (og.lookupOp(pref) == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, pref }));
      }
      



      if (og.lookupOp(other) == null) {
        throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[] { descriptorName, modeName, other }));
      }
      



      og.unsetPreference(pref, other);
    }
    productPrefs.remove(key);
    return true;
  }
  












  String[][] getProductPreferences(String descriptorName)
  {
    if (!arePreferencesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[] { modeName }));
    }
    



    CaselessStringKey key = new CaselessStringKey(descriptorName);
    


    if (!productPrefs.containsKey(key))
    {
      return (String[][])null;
    }
    Vector v = (Vector)productPrefs.get(key);
    int s = v.size();
    if (s == 0) {
      return (String[][])null;
    }
    String[][] productPreferences = new String[s][2];
    int count = 0;
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      String[] o = (String[])e.nextElement();
      productPreferences[count][0] = o[0];
      productPreferences[(count++)][1] = o[1];
    }
    
    return productPreferences;
  }
  

















  Vector getOrderedProductList(String descriptorName)
  {
    if (!arePreferencesSupported) {
      return null;
    }
    
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    if (!descriptorNames.containsKey(key)) {
      return null;
    }
    
    ProductOperationGraph productGraph = (ProductOperationGraph)products.get(key);
    


    if (productGraph == null) {
      return null;
    }
    

    Vector v1 = productGraph.getOrderedOperationList();
    
    if (v1 == null) {
      return null;
    }
    int size = v1.size();
    
    if (size == 0) {
      return null;
    }
    Vector v2 = new Vector();
    for (int i = 0; i < size; i++) {
      v2.addElement(((PartialOrderNode)v1.elementAt(i)).getName());
    }
    
    return v2;
  }
  



  private boolean arePropertiesSupported(String descriptorName)
  {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    RegistryElementDescriptor rdesc = (RegistryElementDescriptor)descriptorNames.get(key);
    

    if (rdesc == null) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[] { descriptorName, modeName }));
    }
    


    return arePropertiesSupported;
  }
  




  void clearPropertyState()
  {
    if (!arePropertiesSupported) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    properties = new Hashtable();
    suppressed = new Hashtable();
  }
  








  void addPropertyGenerator(String descriptorName, PropertyGenerator generator)
  {
    if ((descriptorName == null) || (generator == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    Vector v = (Vector)properties.get(key);
    
    if (v == null) {
      v = new Vector();
      properties.put(key, v);
    }
    
    v.addElement(generator);
    
    v = (Vector)suppressed.get(key);
    Hashtable h = (Hashtable)sourceForProp.get(key);
    
    String[] names = generator.getPropertyNames();
    
    for (int j = 0; j < names.length; j++) {
      CaselessStringKey name = new CaselessStringKey(names[j]);
      
      if (v != null) v.remove(name);
      if (h != null) h.remove(name);
    }
  }
  
  private void hashNames(String descriptorName) {
    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    Vector c = (Vector)properties.get(key);
    Vector s = (Vector)suppressed.get(key);
    
    Hashtable h = new Hashtable();
    propNames.put(key, h);
    Iterator it;
    if (c != null)
    {


      for (it = c.iterator(); it.hasNext();) {
        PropertyGenerator pg = (PropertyGenerator)it.next();
        String[] names = pg.getPropertyNames();
        
        for (int i = 0; i < names.length; i++) {
          CaselessStringKey name = new CaselessStringKey(names[i]);
          


          if ((s == null) || (!s.contains(name))) {
            h.put(name, pg);
          }
        }
      }
    }
    
    Hashtable htable = (Hashtable)sourceForProp.get(key);
    Enumeration e;
    if (htable != null) {
      for (e = htable.keys(); e.hasMoreElements();) {
        CaselessStringKey name = (CaselessStringKey)e.nextElement();
        
        int i = ((Integer)htable.get(name)).intValue();
        
        PropertyGenerator generator = new PropertyGeneratorFromSource(i, name.getName());
        

        h.put(name, generator);
      }
    }
  }
  












  void removePropertyGenerator(String descriptorName, PropertyGenerator generator)
  {
    if ((descriptorName == null) || (generator == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    Vector v = (Vector)properties.get(key);
    
    if (v != null) {
      v.removeElement(generator);
    }
  }
  












  void suppressProperty(String descriptorName, String propertyName)
  {
    if ((descriptorName == null) || (propertyName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    CaselessStringKey propertyKey = new CaselessStringKey(propertyName);
    

    Vector v = (Vector)suppressed.get(key);
    
    if (v == null) {
      v = new Vector();
      suppressed.put(key, v);
    }
    
    v.addElement(propertyKey);
    
    Hashtable h = (Hashtable)sourceForProp.get(key);
    
    if (h != null) {
      h.remove(propertyKey);
    }
  }
  









  void suppressAllProperties(String descriptorName)
  {
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    






    CaselessStringKey key = new CaselessStringKey(descriptorName);
    


    Vector v = (Vector)properties.get(key);
    Iterator it;
    if (v != null)
    {

      for (it = v.iterator(); it.hasNext();) {
        PropertyGenerator pg = (PropertyGenerator)it.next();
        String[] propertyNames = pg.getPropertyNames();
        for (int i = 0; i < propertyNames.length; i++) {
          suppressProperty(descriptorName, propertyNames[i]);
        }
      }
    }
  }
  















  void copyPropertyFromSource(String descriptorName, String propertyName, int sourceIndex)
  {
    if ((descriptorName == null) || (propertyName == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    CaselessStringKey propertyKey = new CaselessStringKey(propertyName);
    
    Hashtable h = (Hashtable)sourceForProp.get(key);
    
    if (h == null) {
      h = new Hashtable();
      sourceForProp.put(key, h);
    }
    
    h.put(propertyKey, new Integer(sourceIndex));
    
    Vector v = (Vector)suppressed.get(key);
    
    if (v != null) {
      v.remove(propertyKey);
    }
  }
  










  String[] getGeneratedPropertyNames(String descriptorName)
  {
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    hashNames(descriptorName);
    
    Hashtable h = (Hashtable)propNames.get(key);
    
    if ((h != null) && (h.size() > 0)) {
      String[] names = new String[h.size()];
      int count = 0;
      for (Enumeration e = h.keys(); e.hasMoreElements();) {
        CaselessStringKey str = (CaselessStringKey)e.nextElement();
        names[(count++)] = str.getName();
      }
      
      return count > 0 ? names : null;
    }
    
    return null;
  }
  





























  PropertySource getPropertySource(String descriptorName, Object op, Vector sources)
  {
    if ((descriptorName == null) || (op == null) || (sources == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!arePropertiesSupported(descriptorName)) {
      throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[] { modeName }));
    }
    


    CaselessStringKey key = new CaselessStringKey(descriptorName);
    
    Vector pg = (Vector)properties.get(key);
    Vector sp = (Vector)suppressed.get(key);
    
    Hashtable sfp = (Hashtable)sourceForProp.get(key);
    
    return new PropertyEnvironment(sources, pg, sp, sfp, op);
  }
}
