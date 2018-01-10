package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ImageFunction;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;





















class ImageFunctionPropertyGenerator
  extends PropertyGeneratorImpl
{
  public ImageFunctionPropertyGenerator()
  {
    super(new String[] { "COMPLEX" }, new Class[] { Boolean.class }, new Class[] { RenderedOp.class, RenderableOp.class });
  }
  








  public Object getProperty(String name, Object opNode)
  {
    validate(name, opNode);
    
    if (name.equalsIgnoreCase("complex")) {
      if ((opNode instanceof RenderedOp)) {
        RenderedOp op = (RenderedOp)opNode;
        ParameterBlock pb = op.getParameterBlock();
        ImageFunction imFunc = (ImageFunction)pb.getObjectParameter(0);
        return imFunc.isComplex() ? Boolean.TRUE : Boolean.FALSE; }
      if ((opNode instanceof RenderableOp)) {
        RenderableOp op = (RenderableOp)opNode;
        ParameterBlock pb = op.getParameterBlock();
        ImageFunction imFunc = (ImageFunction)pb.getObjectParameter(0);
        return imFunc.isComplex() ? Boolean.TRUE : Boolean.FALSE;
      }
    }
    
    return Image.UndefinedProperty;
  }
}
