package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;























































public class BinarizeDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Binarize" }, { "LocalName", "Binarize" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BinarizeDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BinarizeDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("BinarizeDescriptor1") } };
  









  private static final String[] paramNames = { "threshold" };
  






  private static final Class[] paramClasses = { Double.class };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public BinarizeDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  









  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateSources(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage source = (RenderedImage)args.getSource(0);
    int numBands = source.getSampleModel().getNumBands();
    if (numBands != 1) {
      msg.append(getName() + " " + JaiI18N.getString("BinarizeDescriptor2"));
      
      return false;
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, Double threshold, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("threshold", threshold);
    
    return JAI.create("Binarize", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, Double threshold, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("threshold", threshold);
    
    return JAI.createRenderable("Binarize", pb, hints);
  }
}
