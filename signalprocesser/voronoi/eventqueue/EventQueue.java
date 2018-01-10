package signalprocesser.voronoi.eventqueue;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;




public class EventQueue
{
  private static final Comparator PRIORITY_COMPARATOR = new Comparator() {
    public int compare(VEvent p1, VEvent p2) {
      if (p1.getY() < p2.getY()) return -1;
      if (p1.getY() > p2.getY()) return 1;
      if (p1.getX() < p2.getX()) return -1;
      if (p1.getX() > p2.getX()) return 1;
      if (p1 == p2) { return 0;
      }
      



      if ((p1.isSiteEvent()) && (p2.isSiteEvent()))
        return 0;
      if ((p1.isCircleEvent()) && (p2.isCircleEvent())) {
        if (id < id) return -1;
        if (id > id) return 1;
        return 0; }
      if (p1.isSiteEvent()) {
        return -1;
      }
      return 1;
    }
  };
  



  private TreeMap<VEvent, VEvent> queue;
  




  public EventQueue()
  {
    queue = new TreeMap(PRIORITY_COMPARATOR);
  }
  
  public EventQueue(Collection<VEvent> events) {
    this();
    
    for (VEvent event : events) {
      queue.put(event, event);
    }
  }
  


  public void addEvent(VEvent event)
  {
    queue.put(event, event);
  }
  
  public boolean removeEvent(VEvent event) {
    return queue.remove(event) != null;
  }
  
  public VEvent getFirstEvent() {
    if (queue.size() > 0) {
      return (VEvent)queue.firstKey();
    }
    return null;
  }
  
  public VEvent getAndRemoveFirstEvent()
  {
    VEvent event = (VEvent)queue.firstKey();
    queue.remove(event);
    return event;
  }
  
  public boolean isEventQueueEmpty() {
    return queue.isEmpty();
  }
}
