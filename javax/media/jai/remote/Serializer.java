package javax.media.jai.remote;

import java.awt.RenderingHints;

public abstract interface Serializer
{
  public abstract Class getSupportedClass();
  
  public abstract boolean permitsSubclasses();
  
  public abstract SerializableState getState(Object paramObject, RenderingHints paramRenderingHints);
}
