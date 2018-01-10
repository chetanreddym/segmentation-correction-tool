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







































































public class RescaleDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Rescale" }, { "LocalName", "Rescale" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("RescaleDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RescaleDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("RescaleDescriptor1") }, { "arg1Desc", JaiI18N.getString("RescaleDescriptor2") } };
  










  private static final Class[] paramClasses = { new double[0].getClass(), new double[0].getClass() };
  



  private static final String[] paramNames = { "constants", "offsets" };
  



  private static final Object[] paramDefaults = { { 1.0D }, { 0.0D } };
  


  public RescaleDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  
  public boolean isRenderableSupported()
  {
    return true;
  }
  







  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    
    int constantsLength = ((double[])args.getObjectParameter(0)).length;
    int offsetsLength = ((double[])args.getObjectParameter(1)).length;
    
    if (constantsLength < 1) {
      msg.append(getName() + " " + JaiI18N.getString("RescaleDescriptor3"));
      
      return false;
    }
    
    if (offsetsLength < 1) {
      msg.append(getName() + ": " + JaiI18N.getString("RescaleDescriptor4"));
      
      return false;
    }
    
    return true;
  }
  
























  public static RenderedOp create(RenderedImage source0, double[] constants, double[] offsets, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Rescale", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    pb.setParameter("offsets", offsets);
    
    return JAI.create("Rescale", pb, hints);
  }
  























  public static RenderableOp createRenderable(RenderableImage source0, double[] constants, double[] offsets, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Rescale", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("constants", constants);
    pb.setParameter("offsets", offsets);
    
    return JAI.createRenderable("Rescale", pb, hints);
  }
}
