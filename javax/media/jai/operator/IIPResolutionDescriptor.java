package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

































































































































public class IIPResolutionDescriptor
  extends OperationDescriptorImpl
{
  public static final Integer MAX_RESOLUTION = new Integer(Integer.MAX_VALUE);
  




  private static final String[][] resources = { { "GlobalName", "IIPResolution" }, { "LocalName", "IIPResolution" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("IIPResolutionDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IIPResolutionDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("IIPResolutionDescriptor1") }, { "arg1Desc", JaiI18N.getString("IIPResolutionDescriptor2") }, { "arg2Desc", JaiI18N.getString("IIPResolutionDescriptor3") } };
  











  private static final Class[] paramClasses = { String.class, Integer.class, Integer.class };
  





  private static final String[] paramNames = { "URL", "resolution", "subImage" };
  





  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT, MAX_RESOLUTION, new Integer(0) };
  




  public IIPResolutionDescriptor()
  {
    super(resources, 0, paramClasses, paramNames, paramDefaults);
  }
  











  public Number getParamMinValue(int index)
  {
    if (index == 0)
      return null;
    if ((index == 1) || (index == 2)) {
      return new Integer(0);
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  








  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    try
    {
      new URL((String)args.getObjectParameter(0));
    }
    catch (Exception e) {
      msg.append(getName() + " " + JaiI18N.getString("IIPDescriptor15"));
      
      return false;
    }
    
    return true;
  }
  
























  public static RenderedOp create(String URL, Integer resolution, Integer subImage, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("IIPResolution", "rendered");
    


    pb.setParameter("URL", URL);
    pb.setParameter("resolution", resolution);
    pb.setParameter("subImage", subImage);
    
    return JAI.create("IIPResolution", pb, hints);
  }
}
