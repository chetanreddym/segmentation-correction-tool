package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

























class DFTPropertyGenerator
  extends PropertyGeneratorImpl
{
  public DFTPropertyGenerator()
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
        DFTDataNature dataNature = (DFTDataNature)pb.getObjectParameter(1);
        
        return dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
      }
      if ((opNode instanceof RenderableOp)) {
        RenderableOp op = (RenderableOp)opNode;
        ParameterBlock pb = op.getParameterBlock();
        DFTDataNature dataNature = (DFTDataNature)pb.getObjectParameter(1);
        
        return dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
      }
    }
    

    return Image.UndefinedProperty;
  }
}
