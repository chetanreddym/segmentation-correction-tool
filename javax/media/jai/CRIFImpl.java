package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.util.ImagingListener;




































public abstract class CRIFImpl
  implements ContextualRenderedImageFactory
{
  protected String operationName = null;
  


  public CRIFImpl()
  {
    operationName = null;
  }
  



  public CRIFImpl(String operationName)
  {
    this.operationName = operationName;
  }
  











  public abstract RenderedImage create(ParameterBlock paramParameterBlock, RenderingHints paramRenderingHints);
  











  public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock)
  {
    RenderingHints renderHints = renderContext.getRenderingHints();
    if (operationName != null) {
      OperationRegistry registry = renderHints == null ? null : (OperationRegistry)renderHints.get(JAI.KEY_OPERATION_REGISTRY);
      
      RenderedImage rendering;
      
      RenderedImage rendering;
      
      if (registry == null)
      {
        rendering = JAI.create(operationName, paramBlock, renderHints);


      }
      else
      {


        OperationDescriptor odesc = (OperationDescriptor)registry.getDescriptor(OperationDescriptor.class, operationName);
        

        if (odesc == null) {
          throw new IllegalArgumentException(operationName + ": " + JaiI18N.getString("JAI0"));
        }
        


        if (!odesc.isModeSupported("rendered")) {
          throw new IllegalArgumentException(operationName + ": " + JaiI18N.getString("JAI1"));
        }
        


        if (!RenderedImage.class.isAssignableFrom(odesc.getDestClass("rendered"))) {
          throw new IllegalArgumentException(operationName + ": " + JaiI18N.getString("JAI2"));
        }
        




        StringBuffer msg = new StringBuffer();
        paramBlock = (ParameterBlock)paramBlock.clone();
        if (!odesc.validateArguments("rendered", paramBlock, msg))
        {
          throw new IllegalArgumentException(msg.toString());
        }
        

        rendering = new RenderedOp(registry, operationName, paramBlock, renderHints);
      }
      


      if (rendering != null)
      {

        if ((rendering instanceof RenderedOp)) {
          try {
            rendering = ((RenderedOp)rendering).getRendering();
          } catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(renderHints);
            
            String message = JaiI18N.getString("CRIFImpl0") + operationName;
            
            listener.errorOccurred(message, e, this, false);
          }
        }
        
        return rendering;
      }
    }
    

    return create(paramBlock, renderHints);
  }
  


















  public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image)
  {
    return renderContext;
  }
  








  public Rectangle2D getBounds2D(ParameterBlock paramBlock)
  {
    int numSources = paramBlock.getNumSources();
    
    if (numSources == 0) {
      return null;
    }
    
    RenderableImage src = paramBlock.getRenderableSource(0);
    Rectangle2D.Float box1 = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
    



    for (int i = 1; i < numSources; i++) {
      src = paramBlock.getRenderableSource(i);
      Rectangle2D.Float box2 = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      

      box1 = (Rectangle2D.Float)box1.createIntersection(box2);
      if (box1.isEmpty()) {
        break;
      }
    }
    
    return box1;
  }
  















  public Object getProperty(ParameterBlock paramBlock, String name)
  {
    return Image.UndefinedProperty;
  }
  







  public String[] getPropertyNames()
  {
    return null;
  }
  








  public boolean isDynamic()
  {
    return false;
  }
}
