package signalprocesser.voronoi.eventqueue;

import signalprocesser.voronoi.statusstructure.VLinkedNode;










public class VCircleEvent
  extends VEvent
{
  private int x;
  private int y;
  private int center_y;
  public VLinkedNode leafnode;
  
  public VCircleEvent() {}
  
  public VCircleEvent(int _x, int _y)
  {
    x = _x;
    y = _y;
  }
  



  public int getX() { return x; }
  
  public void setX(int _x) { x = _x; }
  

  public int getY() { return y; }
  
  public void setY(int _y) { y = _y; }
  

  public int getCenterY() { return center_y; }
  
  public void setCenterY(int _center_y) { center_y = _center_y; }
  

  public boolean isSiteEvent() { return false; }
  
  public boolean isCircleEvent() { return true; }
  



  public String toString()
  {
    return "VCircleEvent (" + x + "," + y + ")";
  }
}
