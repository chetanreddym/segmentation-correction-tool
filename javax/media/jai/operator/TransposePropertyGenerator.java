package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
























class TransposePropertyGenerator
  extends PropertyGeneratorImpl
{
  public TransposePropertyGenerator()
  {
    super(new String[] { "ROI" }, new Class[] { ROI.class }, new Class[] { RenderedOp.class });
  }
  








  public Object getProperty(String name, Object opNode)
  {
    validate(name, opNode);
    
    if (((opNode instanceof RenderedOp)) && (name.equalsIgnoreCase("roi")))
    {
      RenderedOp op = (RenderedOp)opNode;
      
      ParameterBlock pb = op.getParameterBlock();
      

      PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
      Object property = src.getProperty("ROI");
      if ((property == null) || (property.equals(Image.UndefinedProperty)) || (!(property instanceof ROI)))
      {

        return Image.UndefinedProperty;
      }
      

      ROI srcROI = (ROI)property;
      if (srcROI.getBounds().isEmpty()) {
        return Image.UndefinedProperty;
      }
      






      TransposeType transposeType = (TransposeType)pb.getObjectParameter(0);
      
      Interpolation interp = Interpolation.getInstance(0);
      


      return new ROI(JAI.create("transpose", srcROI.getAsImage(), transposeType));
    }
    

    return Image.UndefinedProperty;
  }
}
