package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.media.jai.JAI;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;






















public class InterfaceState
  implements SerializableState
{
  private transient Object theObject;
  private transient Serializer[] theSerializers;
  private transient RenderingHints hints;
  
  public InterfaceState(Object o, Serializer[] serializers, RenderingHints h)
  {
    if ((o == null) || (serializers == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    theObject = o;
    theSerializers = serializers;
    hints = (h == null ? null : (RenderingHints)h.clone());
  }
  
  public Object getObject() {
    return theObject;
  }
  
  public Class getObjectClass() {
    return theObject.getClass();
  }
  




  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    int numSerializers = theSerializers.length;
    out.writeInt(numSerializers);
    for (int i = 0; i < numSerializers; i++) {
      Serializer s = theSerializers[i];
      out.writeObject(s.getSupportedClass());
      out.writeObject(s.getState(theObject, hints));
    }
  }
  





  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    int numInterfaces = in.readInt();
    Class[] interfaces = new Class[numInterfaces];
    SerializableState[] implementations = new SerializableState[numInterfaces];
    
    for (int i = 0; i < numInterfaces; i++) {
      interfaces[i] = ((Class)in.readObject());
      implementations[i] = ((SerializableState)in.readObject());
    }
    
    InvocationHandler handler = new InterfaceHandler(interfaces, implementations);
    

    theObject = Proxy.newProxyInstance(JAI.class.getClassLoader(), interfaces, handler);
  }
}
