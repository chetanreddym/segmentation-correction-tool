package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
























public class HashtableState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { Hashtable.class };
  }
  







  public HashtableState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  


  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    Hashtable table = (Hashtable)theObject;
    
    Hashtable serializableTable = new Hashtable();
    

    if ((table != null) && (!table.isEmpty()))
    {
      Set keySet = table.keySet();
      

      if (!keySet.isEmpty())
      {
        Iterator keyIterator = keySet.iterator();
        

        while (keyIterator.hasNext())
        {
          Object key = keyIterator.next();
          Object serializableKey = getSerializableForm(key);
          
          if (serializableKey != null)
          {


            Object value = table.get(key);
            
            Object serializableValue = getSerializableForm(value);
            
            if (serializableValue != null)
            {

              serializableTable.put(serializableKey, serializableValue);
            }
          }
        }
      }
    }
    out.writeObject(serializableTable);
  }
  



  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    Hashtable serializableTable = (Hashtable)in.readObject();
    

    Hashtable table = new Hashtable();
    theObject = table;
    

    if (serializableTable.isEmpty()) {
      return;
    }
    

    Enumeration keys = serializableTable.keys();
    

    while (keys.hasMoreElements())
    {
      Object serializableKey = keys.nextElement();
      Object key = getDeserializedFrom(serializableKey);
      

      Object serializableValue = serializableTable.get(serializableKey);
      Object value = getDeserializedFrom(serializableValue);
      

      table.put(key, value);
    }
  }
}
