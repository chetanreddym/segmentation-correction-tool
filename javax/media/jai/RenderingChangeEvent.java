package javax.media.jai;

import java.awt.Shape;


































public class RenderingChangeEvent
  extends PropertyChangeEventJAI
{
  private Shape invalidRegion;
  
  public RenderingChangeEvent(RenderedOp source, PlanarImage oldRendering, PlanarImage newRendering, Shape invalidRegion)
  {
    super(source, "Rendering", oldRendering, newRendering);
    this.invalidRegion = invalidRegion;
  }
  








  public Shape getInvalidRegion()
  {
    return invalidRegion;
  }
}
