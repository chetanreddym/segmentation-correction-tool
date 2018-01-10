package javax.media.jai.remote;

import com.sun.media.jai.rmi.InterfaceState;
import com.sun.media.jai.rmi.SerializerImpl;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;































































































public final class SerializerFactory
{
  private static Hashtable repository = new Hashtable();
  




  private static Serializer serializableSerializer = new SerSerializer();
  
  static final SerializableState NULL_STATE = new SerializableState()
  {
    public Class getObjectClass() {
      return Object.class;
    }
    
    public Object getObject() {
      return null;
    }
  };
  
  static
  {
    SerializerImpl.registerSerializers();
  }
  








  public static synchronized void registerSerializer(Serializer s)
  {
    if (s == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Class c = s.getSupportedClass();
    
    if (repository.containsKey(c)) {
      Object value = repository.get(c);
      if ((value instanceof Vector)) {
        ((Vector)value).add(0, s);
      } else {
        Vector v = new Vector(2);
        v.add(0, s);
        v.add(1, value);
        repository.put(c, v);
      }
    } else {
      repository.put(c, s);
    }
  }
  






  public static synchronized void unregisterSerializer(Serializer s)
  {
    if (s == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Class c = s.getSupportedClass();
    Object value = repository.get(c);
    if (value != null) {
      if ((value instanceof Vector)) {
        Vector v = (Vector)value;
        v.remove(s);
        if (v.size() == 1) {
          repository.put(c, v.get(0));
        }
      } else {
        repository.remove(c);
      }
    }
  }
  











  public static synchronized Serializer[] getSerializers(Class c)
  {
    if (c == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    Object value = repository.get(c);
    Serializer[] result = null;
    if ((value == null) && (Serializable.class.isAssignableFrom(c))) {
      result = new Serializer[] { serializableSerializer };
    } else if ((value instanceof Vector)) {
      result = (Serializer[])((Vector)value).toArray(new Serializer[0]);
    } else if (value != null) {
      result = new Serializer[] { (Serializer)value };
    }
    return result;
  }
  




















  public static synchronized Serializer getSerializer(Class c)
  {
    if (c == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    

    Object value = repository.get(c);
    

    if (value == null) {
      Class theClass = c;
      while (theClass != Object.class) {
        Class theSuperclass = theClass.getSuperclass();
        if (isSupportedClass(theSuperclass)) {
          Serializer s = getSerializer(theSuperclass);
          if (s.permitsSubclasses()) {
            value = s;
            break;
          }
        }
        theClass = theSuperclass;
      }
    }
    
    if ((value == null) && (Serializable.class.isAssignableFrom(c))) {
      value = serializableSerializer;
    }
    

    return (value instanceof Vector) ? (Serializer)((Vector)value).get(0) : (Serializer)value;
  }
  











  public static boolean isSupportedClass(Class c)
  {
    if (c == null)
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (Serializable.class.isAssignableFrom(c)) {
      return true;
    }
    return repository.containsKey(c);
  }
  






  public static Class[] getSupportedClasses()
  {
    Class[] classes = new Class[repository.size() + 1];
    repository.keySet().toArray(classes);
    classes[(classes.length - 1)] = Serializable.class;
    return classes;
  }
  


















  public static Class getDeserializedClass(Class c)
  {
    if (c == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    Class deserializedClass = null;
    

    if (isSupportedClass(c)) {
      deserializedClass = c;
    } else {
      Class theClass = c;
      while (theClass != Object.class) {
        Class theSuperclass = theClass.getSuperclass();
        if (isSupportedClass(theSuperclass)) {
          Serializer s = getSerializer(theSuperclass);
          if (s.permitsSubclasses()) {
            deserializedClass = theSuperclass;
            break;
          }
        }
        theClass = theSuperclass;
      }
    }
    
    return deserializedClass;
  }
  






























  public static SerializableState getState(Object o, RenderingHints h)
  {
    if (o == null) {
      return NULL_STATE;
    }
    
    Class c = o.getClass();
    SerializableState state = null;
    if (isSupportedClass(c))
    {
      Serializer s = getSerializer(c);
      state = s.getState(o, h);
    }
    else {
      Class theClass = c;
      while (theClass != Object.class) {
        Class theSuperclass = theClass.getSuperclass();
        if (isSupportedClass(theSuperclass)) {
          Serializer s = getSerializer(theSuperclass);
          if (s.permitsSubclasses()) {
            state = s.getState(o, h);
            break;
          }
        }
        theClass = theSuperclass;
      }
      
      if (state == null)
      {

        Class[] interfaces = getInterfaces(c);
        Vector serializers = null;
        int numInterfaces = interfaces == null ? 0 : interfaces.length;
        for (int i = 0; i < numInterfaces; i++) {
          Class iface = interfaces[i];
          if (isSupportedClass(iface)) {
            if (serializers == null) {
              serializers = new Vector();
            }
            serializers.add(getSerializer(iface));
          }
        }
        
        int numSupportedInterfaces = serializers == null ? 0 : serializers.size();
        
        if (numSupportedInterfaces == 0) {
          throw new IllegalArgumentException(JaiI18N.getString("SerializerFactory1"));
        }
        if (numSupportedInterfaces == 1) {
          state = ((Serializer)serializers.get(0)).getState(o, h);
        } else {
          Serializer[] sArray = (Serializer[])serializers.toArray(new Serializer[0]);
          
          state = new InterfaceState(o, sArray, h);
        }
      }
    }
    
    return state;
  }
  



  private static Class[] getInterfaces(Class c)
  {
    if (c == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    ArrayList interfaces = new ArrayList();
    Class laClasse = c;
    while (laClasse != Object.class) {
      Class[] iFaces = laClasse.getInterfaces();
      if (iFaces != null) {
        for (int i = 0; i < iFaces.length; i++) {
          interfaces.add(iFaces[i]);
        }
      }
      laClasse = laClasse.getSuperclass();
    }
    
    return interfaces.size() == 0 ? null : (Class[])interfaces.toArray(new Class[interfaces.size()]);
  }
  






  public static final SerializableState getState(Object o)
  {
    return getState(o, null);
  }
  
  protected SerializerFactory() {}
}
