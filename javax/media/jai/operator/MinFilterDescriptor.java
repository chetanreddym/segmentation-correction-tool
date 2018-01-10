package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
















































































































public class MinFilterDescriptor
  extends OperationDescriptorImpl
{
  public static final MinFilterShape MIN_MASK_SQUARE = new MinFilterShape("MIN_MASK_SQUARE", 1);
  


  public static final MinFilterShape MIN_MASK_PLUS = new MinFilterShape("MIN_MASK_PLUS", 2);
  


  public static final MinFilterShape MIN_MASK_X = new MinFilterShape("MIN_MASK_X", 3);
  


  public static final MinFilterShape MIN_MASK_SQUARE_SEPARABLE = new MinFilterShape("MIN_MASK_SQUARE_SEPARABLE", 4);
  





  private static final String[][] resources = { { "GlobalName", "MinFilter" }, { "LocalName", "MinFilter" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("MinFilterDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/javax.media.jai.operator.MinFilterDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("MinFilterDescriptor1") }, { "arg1Desc", JaiI18N.getString("MinFilterDescriptor2") } };
  










  private static final Class[] paramClasses = { MinFilterShape.class, Integer.class };
  



  private static final String[] paramNames = { "maskShape", "maskSize" };
  



  private static final Object[] paramDefaults = { MIN_MASK_SQUARE, new Integer(3) };
  


  public MinFilterDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  



  public Number getParamMinValue(int index)
  {
    if (index == 0)
      return null;
    if (index == 1) {
      return new Integer(1);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  




  public Number getParamMaxValue(int index)
  {
    if (index == 0)
      return null;
    if (index == 1) {
      return new Integer(Integer.MAX_VALUE);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  






  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new AreaOpPropertyGenerator();
    return pg;
  }
  
























  public static RenderedOp create(RenderedImage source0, MinFilterShape maskShape, Integer maskSize, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("MinFilter", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("maskShape", maskShape);
    pb.setParameter("maskSize", maskSize);
    
    return JAI.create("MinFilter", pb, hints);
  }
}
