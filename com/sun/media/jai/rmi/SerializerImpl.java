package com.sun.media.jai.rmi;

import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

























public final class SerializerImpl
  implements Serializer
{
  private Class theClass;
  private boolean areSubclassesPermitted;
  private Constructor ctor;
  
  public static final void registerSerializers()
  {
    registerSerializers(ColorModelState.class);
    registerSerializers(DataBufferState.class);
    registerSerializers(HashSetState.class);
    registerSerializers(HashtableState.class);
    registerSerializers(RasterState.class);
    registerSerializers(RenderedImageState.class);
    registerSerializers(RenderContextState.class);
    registerSerializers(RenderingHintsState.class);
    registerSerializers(RenderingKeyState.class);
    registerSerializers(SampleModelState.class);
    registerSerializers(VectorState.class);
    registerSerializers(ShapeState.class);
  }
  
  private static void registerSerializers(Class ssi) {
    if (ssi == null) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!SerializableStateImpl.class.isAssignableFrom(ssi)) {
      throw new IllegalArgumentException(JaiI18N.getString("SerializerImpl0"));
    }
    
    ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
    
    Class[] classes = null;
    try {
      Method m1 = ssi.getMethod("getSupportedClasses", null);
      classes = (Class[])m1.invoke(null, null);
    } catch (NoSuchMethodException e) {
      String message = JaiI18N.getString("SerializerImpl1");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    catch (IllegalAccessException e)
    {
      String message = JaiI18N.getString("SerializerImpl1");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    catch (InvocationTargetException e)
    {
      String message = JaiI18N.getString("SerializerImpl1");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    


    boolean supportsSubclasses = false;
    try {
      Method m2 = ssi.getMethod("permitsSubclasses", null);
      Boolean b = (Boolean)m2.invoke(null, null);
      supportsSubclasses = b.booleanValue();
    } catch (NoSuchMethodException e) {
      String message = JaiI18N.getString("SerializerImpl4");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    catch (IllegalAccessException e)
    {
      String message = JaiI18N.getString("SerializerImpl4");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    catch (InvocationTargetException e)
    {
      String message = JaiI18N.getString("SerializerImpl4");
      listener.errorOccurred(message, new RemoteImagingException(message, e), SerializerImpl.class, false);
    }
    


    int numClasses = classes.length;
    for (int i = 0; i < numClasses; i++) {
      Serializer s = new SerializerImpl(ssi, classes[i], supportsSubclasses);
      
      SerializerFactory.registerSerializer(s);
    }
  }
  









  protected SerializerImpl(Class ssi, Class c, boolean areSubclassesPermitted)
  {
    theClass = c;
    this.areSubclassesPermitted = areSubclassesPermitted;
    try
    {
      Class[] paramTypes = { Class.class, Object.class, RenderingHints.class };
      

      ctor = ssi.getConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl2");
      
      sendExceptionToListener(message, new RemoteImagingException(message, e));
    }
  }
  





  public SerializableState getState(Object o, RenderingHints h)
  {
    Object state = null;
    try {
      state = ctor.newInstance(new Object[] { theClass, o, h });
    } catch (InstantiationException e) {
      String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
      
      sendExceptionToListener(message, new RemoteImagingException(message, e));
    }
    catch (IllegalAccessException e) {
      String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
      
      sendExceptionToListener(message, new RemoteImagingException(message, e));
    }
    catch (InvocationTargetException e) {
      String message = theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
      
      sendExceptionToListener(message, new RemoteImagingException(message, e));
    }
    

    return (SerializableState)state;
  }
  
  public Class getSupportedClass() {
    return theClass;
  }
  
  public boolean permitsSubclasses() {
    return areSubclassesPermitted;
  }
  
  private void sendExceptionToListener(String message, Exception e) {
    ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
    
    listener.errorOccurred(message, new ImagingException(message, e), this, false);
  }
}
