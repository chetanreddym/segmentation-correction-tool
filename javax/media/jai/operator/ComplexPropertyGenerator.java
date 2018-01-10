package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;














class ComplexPropertyGenerator
  extends PropertyGeneratorImpl
{
  public ComplexPropertyGenerator()
  {
    super(new String[] { "COMPLEX" }, new Class[] { Boolean.class }, new Class[] { RenderedOp.class, RenderableOp.class });
  }
  








  public Object getProperty(String name, Object op)
  {
    validate(name, op);
    
    return name.equalsIgnoreCase("complex") ? Boolean.TRUE : Image.UndefinedProperty;
  }
}
