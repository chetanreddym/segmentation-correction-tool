package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;


























































public class MagnitudeSquaredDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "MagnitudeSquared" }, { "LocalName", "MagnitudeSquared" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("MagnitudeSquaredDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MagnitudeSquaredDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  







  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public MagnitudeSquaredDescriptor()
  {
    super(resources, supportedModes, 1, null, null, null, null);
  }
  





  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new ComplexPropertyGenerator();
    return pg;
  }
  








  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateSources(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src = args.getRenderedSource(0);
    
    int bands = src.getSampleModel().getNumBands();
    
    if (bands % 2 != 0) {
      msg.append(getName() + " " + JaiI18N.getString("MagnitudeSquaredDescriptor1"));
      
      return false;
    }
    
    return true;
  }
  


















  public static RenderedOp create(RenderedImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("MagnitudeSquared", "rendered");
    


    pb.setSource("source0", source0);
    
    return JAI.create("MagnitudeSquared", pb, hints);
  }
  

















  public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("MagnitudeSquared", "renderable");
    


    pb.setSource("source0", source0);
    
    return JAI.createRenderable("MagnitudeSquared", pb, hints);
  }
}
