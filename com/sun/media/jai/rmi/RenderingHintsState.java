package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;



















public class RenderingHintsState
  extends SerializableStateImpl
{
  public static Class[] getSupportedClasses()
  {
    return new Class[] { RenderingHints.class };
  }
  





  private static final Class[] KEY_CLASSES = { RenderingHints.class, JAI.class };
  







  private static final Object[] SUPPRESSED_KEYS = { JAI.KEY_OPERATION_REGISTRY, JAI.KEY_TILE_CACHE, JAI.KEY_RETRY_INTERVAL, JAI.KEY_NUM_RETRIES, JAI.KEY_NEGOTIATION_PREFERENCES };
  





  private static SoftReference suppressedKeyReference = null;
  




  private static SoftReference hintTableReference = null;
  





  public RenderingHintsState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
  }
  


  static class HintElement
    implements Serializable
  {
    private static final int TYPE_OBJECT = 1;
    

    private static final int TYPE_FIELD = 2;
    

    private int type;
    

    private Object obj;
    

    private String className;
    

    private String fieldName;
    


    public HintElement(Object obj)
      throws NotSerializableException
    {
      if (!(obj instanceof Serializable)) {
        throw new NotSerializableException();
      }
      type = 1;
      this.obj = obj;
    }
    
    public HintElement(Class cls, Field fld)
    {
      type = 2;
      className = cls.getName();
      fieldName = fld.getName();
    }
    
    public Object getObject()
    {
      Object elt = null;
      
      if (type == 1) {
        elt = obj;
      } else if (type == 2) {
        try {
          Class cls = Class.forName(className);
          Field fld = cls.getField(fieldName);
          elt = fld.get(null);
        }
        catch (Exception e) {}
      }
      

      return elt;
    }
  }
  
  private static synchronized Vector getSuppressedKeys()
  {
    Vector suppressedKeys = null;
    
    if (SUPPRESSED_KEYS != null)
    {
      suppressedKeys = suppressedKeyReference != null ? (Vector)suppressedKeyReference.get() : null;
      

      if (suppressedKeys == null)
      {
        int numSuppressedKeys = SUPPRESSED_KEYS.length;
        

        suppressedKeys = new Vector(numSuppressedKeys);
        for (int i = 0; i < numSuppressedKeys; i++) {
          suppressedKeys.add(SUPPRESSED_KEYS[i]);
        }
        

        suppressedKeyReference = new SoftReference(suppressedKeys);
      }
    }
    
    return suppressedKeys;
  }
  




  static synchronized Hashtable getHintTable()
  {
    Hashtable table = hintTableReference != null ? (Hashtable)hintTableReference.get() : null;
    

    if (table == null)
    {
      table = new Hashtable();
      
      for (int i = 0; i < KEY_CLASSES.length; i++)
      {
        Class cls = KEY_CLASSES[i];
        

        Field[] fields = cls.getFields();
        


        for (int j = 0; j < fields.length; j++) {
          Field fld = fields[j];
          int modifiers = fld.getModifiers();
          if ((Modifier.isPublic(modifiers)) && (Modifier.isStatic(modifiers))) {
            try
            {
              Object fieldValue = fld.get(null);
              table.put(fieldValue, new HintElement(cls, fld));
            }
            catch (Exception e) {}
          }
        }
      }
      



      hintTableReference = new SoftReference(table);
    }
    
    return table;
  }
  









  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    RenderingHints hints = (RenderingHints)theObject;
    

    Hashtable table = new Hashtable();
    

    if ((hints != null) && (!hints.isEmpty()))
    {
      Set keySet = hints.keySet();
      

      if (!keySet.isEmpty())
      {
        Iterator keyIterator = keySet.iterator();
        

        Hashtable hintTable = getHintTable();
        

        Vector suppressedKeys = getSuppressedKeys();
        

        while (keyIterator.hasNext())
        {
          Object key = keyIterator.next();
          

          if ((suppressedKeys == null) || (suppressedKeys.indexOf(key) == -1))
          {




            Object keyElement = SerializerFactory.getState(key, null);
            


            if (keyElement != null)
            {



              Object value = hints.get(key);
              

              HintElement valueElement = null;
              try
              {
                valueElement = new HintElement(value);

              }
              catch (NotSerializableException nse)
              {
                valueElement = (HintElement)hintTable.get(value);
              }
              

              if (valueElement != null) {
                table.put(keyElement, valueElement);
              }
            }
          }
        }
      }
    }
    out.writeObject(table);
  }
  



  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    Hashtable table = (Hashtable)in.readObject();
    

    RenderingHints hints = new RenderingHints(null);
    
    theObject = hints;
    

    if (table.isEmpty()) {
      return;
    }
    



    Enumeration keys = table.keys();
    

    while (keys.hasMoreElements())
    {
      SerializableState keyElement = (SerializableState)keys.nextElement();
      

      Object key = keyElement.getObject();
      

      HintElement valueElement = (HintElement)table.get(keyElement);
      

      Object value = valueElement.getObject();
      

      hints.put(key, value);
    }
  }
}
