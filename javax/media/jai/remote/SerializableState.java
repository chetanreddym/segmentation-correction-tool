package javax.media.jai.remote;

import java.io.Serializable;

public abstract interface SerializableState
  extends Serializable
{
  public abstract Class getObjectClass();
  
  public abstract Object getObject();
}
