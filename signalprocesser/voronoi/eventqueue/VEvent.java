package signalprocesser.voronoi.eventqueue;




public abstract class VEvent
{
  public static int uniqueid = 1;
  



  public final int id = uniqueid++;
  

  public VEvent() {}
  

  public abstract int getX();
  

  public abstract int getY();
  

  public abstract boolean isSiteEvent();
  
  public abstract boolean isCircleEvent();
  
  public String getID()
  {
    return String.format("EVT-%X", new Object[] { Integer.valueOf(id) });
  }
  
  public String toString() {
    return "VEvent (" + getID() + ")";
  }
}
