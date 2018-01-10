package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;


















































































public class BoxFilterDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "BoxFilter" }, { "LocalName", "BoxFilter" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BoxFilterDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BoxFilterDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("BoxFilterDescriptor1") }, { "arg1Desc", JaiI18N.getString("BoxFilterDescriptor2") }, { "arg2Desc", JaiI18N.getString("BoxFilterDescriptor3") }, { "arg3Desc", JaiI18N.getString("BoxFilterDescriptor4") } };
  












  private static final Class[] paramClasses = { Integer.class, Integer.class, Integer.class, Integer.class };
  




  private static final String[] paramNames = { "width", "height", "xKey", "yKey" };
  



  private static final Object[] paramDefaults = { new Integer(3), null, null, null };
  


  public BoxFilterDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  



  public Number getParamMinValue(int index)
  {
    if ((index == 0) || (index == 1))
      return new Integer(1);
    if ((index == 2) || (index == 3)) {
      return new Integer(Integer.MIN_VALUE);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  


  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    int argNumParams = args.getNumParameters();
    
    if ((argNumParams > 0) && ((args.getObjectParameter(0) instanceof Integer)))
    {

      if (argNumParams < 2) {
        Object obj = args.getObjectParameter(0);
        if ((obj instanceof Integer))
        {
          args.add(obj);
        }
      }
      
      if (argNumParams < 3) {
        Object obj = args.getObjectParameter(0);
        if ((obj instanceof Integer))
        {
          args.add(((Integer)obj).intValue() / 2);
        }
      }
      
      if (argNumParams < 4) {
        Object obj = args.getObjectParameter(1);
        if ((obj instanceof Integer))
        {
          args.add(((Integer)obj).intValue() / 2);
        }
      }
    }
    
    return super.validateParameters(args, msg);
  }
  





  public PropertyGenerator[] getPropertyGenerators()
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new AreaOpPropertyGenerator();
    return pg;
  }
  






























  public static RenderedOp create(RenderedImage source0, Integer width, Integer height, Integer xKey, Integer yKey, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BoxFilter", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("width", width);
    pb.setParameter("height", height);
    pb.setParameter("xKey", xKey);
    pb.setParameter("yKey", yKey);
    
    return JAI.create("BoxFilter", pb, hints);
  }
}
