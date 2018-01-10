package javax.mail.event;

import java.util.EventListener;

public interface StoreListener
  extends EventListener
{
  public abstract void notification(StoreEvent paramStoreEvent);
}
