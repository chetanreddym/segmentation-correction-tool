package signalprocesser.voronoi.representation.triangulation;

import signalprocesser.voronoi.VPoint;

public class VCluster extends java.util.ArrayList<VPoint> {
  public VCluster() {}
  
  public VPoint calculateAveragePoint() {
    VPoint average = new VPoint(0, 0);
    for (VPoint point : this) {
      x += x;
      y += y;
    }
    x /= super.size();
    y /= super.size();
    return average;
  }
}
