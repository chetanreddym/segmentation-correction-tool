package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;




















































public class PatternDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Pattern" }, { "LocalName", "Pattern" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("PatternDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PatternDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("PatternDescriptor1") }, { "arg1Desc", JaiI18N.getString("PatternDescriptor2") } };
  










  private static final Class[] paramClasses = { Integer.class, Integer.class };
  




  private static final String[] paramNames = { "width", "height" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT };
  


  public PatternDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public Number getParamMinValue(int index) {
    if ((index == 0) || (index == 1)) {
      return new Integer(1);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  

























  public static RenderedOp create(RenderedImage source0, Integer width, Integer height, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Pattern", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("width", width);
    pb.setParameter("height", height);
    
    return JAI.create("Pattern", pb, hints);
  }
}
