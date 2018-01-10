package signalprocesser.voronoi.representation;

import java.awt.Graphics2D;
import signalprocesser.voronoi.VPoint;

public abstract class AbstractRepresentation
  implements RepresentationInterface
{
  public AbstractRepresentation() {}
  
  public abstract VPoint createPoint(int paramInt1, int paramInt2);
  
  public abstract void paint(Graphics2D paramGraphics2D);
}
