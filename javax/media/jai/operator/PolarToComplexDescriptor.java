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































































public class PolarToComplexDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "PolarToComplex" }, { "LocalName", "PolarToComplex" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("PolarToComplexDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PolarToComplexDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") } };
  







  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public PolarToComplexDescriptor()
  {
    super(resources, supportedModes, 2, null, null, null, null);
  }
  








  protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateSources(modeName, args, msg)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src1 = args.getRenderedSource(0);
    RenderedImage src2 = args.getRenderedSource(1);
    
    if (src1.getSampleModel().getNumBands() != src2.getSampleModel().getNumBands())
    {
      msg.append(getName() + " " + JaiI18N.getString("PolarToComplexDescriptor1"));
      
      return false;
    }
    
    return true;
  }
  





  public PropertyGenerator[] getPropertyGenerators(String modeName)
  {
    PropertyGenerator[] pg = new PropertyGenerator[1];
    pg[0] = new ComplexPropertyGenerator();
    return pg;
  }
  





















  public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("PolarToComplex", "rendered");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    return JAI.create("PolarToComplex", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("PolarToComplex", "renderable");
    


    pb.setSource("source0", source0);
    pb.setSource("source1", source1);
    
    return JAI.createRenderable("PolarToComplex", pb, hints);
  }
}
