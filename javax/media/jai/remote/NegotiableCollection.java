package javax.media.jai.remote;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


























public class NegotiableCollection
  implements Negotiable
{
  private Vector elements;
  private Class elementClass;
  
  public NegotiableCollection(Collection collection)
  {
    if (collection == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection0"));
    }
    

    elements = new Vector();
    

    Iterator i = collection.iterator();
    if (i.hasNext()) {
      Object obj = i.next();
      elements.add(obj);
      elementClass = obj.getClass();
    }
    




    while (i.hasNext()) {
      Object obj = i.next();
      if (obj.getClass() != elementClass) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection1"));
      }
      
      elements.add(obj);
    }
  }
  









  public NegotiableCollection(Object[] objects)
  {
    if (objects == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection0"));
    }
    

    int length = objects.length;
    if (length != 0) {
      elementClass = objects[0].getClass();
    }
    




    elements = new Vector(length);
    for (int i = 0; i < length; i++) {
      if (objects[i].getClass() != elementClass) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCollection1"));
      }
      
      elements.add(objects[i]);
    }
  }
  



  public Collection getCollection()
  {
    if (elements.isEmpty())
      return null;
    return elements;
  }
  











  public Negotiable negotiate(Negotiable other)
  {
    if (other == null) {
      return null;
    }
    

    if ((!(other instanceof NegotiableCollection)) || (other.getNegotiatedValueClass() != elementClass))
    {
      return null;
    }
    

    Vector result = new Vector();
    
    Collection otherCollection = ((NegotiableCollection)other).getCollection();
    



    if (otherCollection == null) {
      return null;
    }
    

    for (Iterator i = elements.iterator(); i.hasNext();) {
      Object obj = i.next();
      
      if ((otherCollection.contains(obj)) && 
      
        (!result.contains(obj))) {
        result.add(obj);
      }
    }
    


    if (result.isEmpty()) {
      return null;
    }
    
    return new NegotiableCollection(result);
  }
  










  public Object getNegotiatedValue()
  {
    if ((elements != null) && (elements.size() > 0)) {
      return elements.elementAt(0);
    }
    return null;
  }
  







  public Class getNegotiatedValueClass()
  {
    return elementClass;
  }
}
