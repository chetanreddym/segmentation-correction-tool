package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;































































































































































public class TransposeDescriptor
  extends OperationDescriptorImpl
{
  public static final TransposeType FLIP_VERTICAL = new TransposeType("FLIP_VERTICAL", 0);
  
  public static final TransposeType FLIP_HORIZONTAL = new TransposeType("FLIP_HORIZONTAL", 1);
  
  public static final TransposeType FLIP_DIAGONAL = new TransposeType("FLIP_DIAGONAL", 2);
  
  public static final TransposeType FLIP_ANTIDIAGONAL = new TransposeType("FLIP_ANTIDIAGONAL", 3);
  
  public static final TransposeType ROTATE_90 = new TransposeType("ROTATE_90", 4);
  
  public static final TransposeType ROTATE_180 = new TransposeType("ROTATE_180", 5);
  
  public static final TransposeType ROTATE_270 = new TransposeType("ROTATE_270", 6);
  





  private static final String[][] resources = { { "GlobalName", "Transpose" }, { "LocalName", "Transpose" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("TransposeDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TransposeDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("TransposeDescriptor1") } };
  









  private static final Class[] paramClasses = { TransposeType.class };
  



  private static final String[] paramNames = { "type" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  public TransposeDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new TransposePropertyGenerator();
    return pg;
  }
  





















  public static RenderedOp create(RenderedImage source0, TransposeType type, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Transpose", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("type", type);
    
    return JAI.create("Transpose", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, TransposeType type, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Transpose", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("type", type);
    
    return JAI.createRenderable("Transpose", pb, hints);
  }
}
