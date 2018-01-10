package javax.media.jai;

import java.io.Serializable;
import java.util.Observable;







































public abstract class DeferredData
  extends Observable
  implements Serializable
{
  protected Class dataClass;
  protected transient Object data;
  
  protected DeferredData(Class dataClass)
  {
    if (dataClass == null) {
      throw new IllegalArgumentException(JaiI18N.getString("DeferredData0"));
    }
    this.dataClass = dataClass;
  }
  


  public Class getDataClass()
  {
    return dataClass;
  }
  





  public boolean isValid()
  {
    return data != null;
  }
  






  protected abstract Object computeData();
  





  public final synchronized Object getData()
  {
    if (data == null) {
      setData(computeData());
    }
    return data;
  }
  




























  protected final void setData(Object data)
  {
    if ((data != null) && (!dataClass.isInstance(data))) {
      throw new IllegalArgumentException(JaiI18N.getString("DeferredData1"));
    }
    if ((this.data == null) || (!this.data.equals(data))) {
      Object oldData = this.data;
      this.data = data;
      setChanged();
      notifyObservers(oldData);
    }
  }
}
