package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;























public class VectorState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { Vector.class };
  }
  







  public VectorState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    Vector vector = (Vector)theObject;
    Vector serializableVector = new Vector();
    Iterator iterator = vector.iterator();
    

    while (iterator.hasNext()) {
      Object object = iterator.next();
      Object serializableObject = getSerializableForm(object);
      serializableVector.add(serializableObject);
    }
    

    out.writeObject(serializableVector);
  }
  



  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    Vector serializableVector = (Vector)in.readObject();
    

    Vector vector = new Vector();
    theObject = vector;
    

    if (serializableVector.isEmpty()) {
      return;
    }
    

    Iterator iterator = serializableVector.iterator();
    

    while (iterator.hasNext())
    {
      Object serializableObject = iterator.next();
      Object object = getDeserializedFrom(serializableObject);
      

      vector.add(object);
    }
  }
}
