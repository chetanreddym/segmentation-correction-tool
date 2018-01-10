package com.sun.media.jai.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Set;
import javax.media.jai.remote.SerializableState;



























































































class InterfaceHandler
  implements InvocationHandler
{
  private Hashtable interfaceMap;
  
  public InterfaceHandler(Class[] interfaces, SerializableState[] implementations)
  {
    if ((interfaces == null) || (implementations == null))
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    if (interfaces.length != implementations.length) {
      throw new IllegalArgumentException(JaiI18N.getString("InterfaceHandler0"));
    }
    
    int numInterfaces = interfaces.length;
    interfaceMap = new Hashtable(numInterfaces);
    for (int i = 0; i < numInterfaces; i++) {
      Class iface = interfaces[i];
      SerializableState state = implementations[i];
      
      if (!iface.isAssignableFrom(state.getObjectClass())) {
        throw new RuntimeException(JaiI18N.getString("InterfaceHandler1"));
      }
      
      Object impl = state.getObject();
      interfaceMap.put(iface, impl);
    }
  }
  
  public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException
  {
    Class key = method.getDeclaringClass();
    if (!interfaceMap.containsKey(key)) {
      Class[] classes = (Class[])interfaceMap.keySet().toArray(new Class[0]);
      
      for (int i = 0; i < classes.length; i++) {
        Class aClass = classes[i];
        if (key.isAssignableFrom(aClass)) {
          interfaceMap.put(key, interfaceMap.get(aClass));
          break;
        }
      }
      if (!interfaceMap.containsKey(key)) {
        throw new RuntimeException(key.getName() + JaiI18N.getString("InterfaceHandler2"));
      }
    }
    

    Object result = null;
    try {
      Object impl = interfaceMap.get(key);
      result = method.invoke(impl, args);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(method.getName() + JaiI18N.getString("InterfaceHandler3"));
    }
    

    return result;
  }
}
