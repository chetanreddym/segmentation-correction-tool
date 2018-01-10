package javax.media.jai;

import java.util.Collection;

















public class CollectionChangeEvent
  extends PropertyChangeEventJAI
{
  public CollectionChangeEvent(CollectionOp source, Collection oldValue, Collection newValue)
  {
    super(source, "Collection", oldValue, newValue);
  }
}
