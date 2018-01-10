package javax.media.jai;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;





























public class CanvasJAI
  extends Canvas
{
  public CanvasJAI(GraphicsConfiguration config)
  {
    super(config);
  }
  



  public Graphics getGraphics()
  {
    Graphics2D g = (Graphics2D)super.getGraphics();
    return GraphicsJAI.createGraphicsJAI(g, this);
  }
}
