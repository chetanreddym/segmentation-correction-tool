package com.sun.media.jai.rmi;

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;




















public class RenderingKeyState
  extends SerializableStateImpl
{
  private transient RenderingHintsState.HintElement predefinedKey;
  
  public static Class[] getSupportedClasses()
  {
    return new Class[] { RenderingHints.Key.class };
  }
  
  public static boolean permitsSubclasses()
  {
    return true;
  }
  









  public RenderingKeyState(Class c, Object o, RenderingHints h)
  {
    super(c, o, h);
    
    Hashtable predefinedObjects = RenderingHintsState.getHintTable();
    
    predefinedKey = ((RenderingHintsState.HintElement)predefinedObjects.get(o));
    

    if (predefinedKey == null)
      throw new RuntimeException(JaiI18N.getString("RenderingKeyState0"));
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(predefinedKey);
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    predefinedKey = ((RenderingHintsState.HintElement)in.readObject());
    theObject = predefinedKey.getObject();
  }
}
