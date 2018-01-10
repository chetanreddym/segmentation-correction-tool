package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
























public class HashSetState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { HashSet.class };
  }
  







  public HashSetState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    HashSet set = (HashSet)theObject;
    
    HashSet serializableSet = new HashSet();
    

    if ((set != null) && (!set.isEmpty()))
    {
      Iterator iterator = set.iterator();
      

      while (iterator.hasNext()) {
        Object object = iterator.next();
        Object serializableObject = getSerializableForm(object);
        serializableSet.add(serializableObject);
      }
    }
    

    out.writeObject(serializableSet);
  }
  



  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    HashSet serializableSet = (HashSet)in.readObject();
    

    HashSet set = new HashSet();
    

    if (serializableSet.isEmpty()) {
      theObject = set;
      return;
    }
    
    Iterator iterator = serializableSet.iterator();
    

    while (iterator.hasNext())
    {
      Object serializableObject = iterator.next();
      Object object = getDeserializedFrom(serializableObject);
      

      set.add(object);
    }
    
    theObject = set;
  }
}
