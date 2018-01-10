package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;








































































public class PeriodicShiftDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "PeriodicShift" }, { "LocalName", "PeriodicShift" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("PeriodicShiftDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PeriodicShiftDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("PeriodicShiftDescriptor1") }, { "arg1Desc", JaiI18N.getString("PeriodicShiftDescriptor2") } };
  










  private static final Class[] paramClasses = { Integer.class, Integer.class };
  




  private static final String[] paramNames = { "shiftX", "shiftY" };
  



  private static final Object[] paramDefaults = { null, null };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public PeriodicShiftDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  










  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateArguments(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src = args.getRenderedSource(0);
    

    if (args.getObjectParameter(0) == null) {
      args.set(new Integer(src.getWidth() / 2), 0);
    }
    if (args.getObjectParameter(1) == null) {
      args.set(new Integer(src.getHeight() / 2), 1);
    }
    
    int shiftX = args.getIntParameter(0);
    int shiftY = args.getIntParameter(1);
    if ((shiftX < 0) || (shiftX >= src.getWidth()) || (shiftY < 0) || (shiftY >= src.getHeight()))
    {
      msg.append(getName() + " " + JaiI18N.getString("PeriodicShiftDescriptor3"));
      
      return false;
    }
    
    return true;
  }
  
























  public static RenderedOp create(RenderedImage source0, Integer shiftX, Integer shiftY, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("PeriodicShift", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("shiftX", shiftX);
    pb.setParameter("shiftY", shiftY);
    
    return JAI.create("PeriodicShift", pb, hints);
  }
  























  public static RenderableOp createRenderable(RenderableImage source0, Integer shiftX, Integer shiftY, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("PeriodicShift", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("shiftX", shiftX);
    pb.setParameter("shiftY", shiftY);
    
    return JAI.createRenderable("PeriodicShift", pb, hints);
  }
}
