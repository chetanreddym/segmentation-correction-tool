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















































































public class BandCombineDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "BandCombine" }, { "LocalName", "BandCombine" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("BandCombineDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BandCombineDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", JaiI18N.getString("BandCombineDescriptor1") } };
  









  private static final Class[] paramClasses = { new double[0].getClass() };
  



  private static final String[] paramNames = { "matrix" };
  



  private static final Object[] paramDefaults = { NO_PARAMETER_DEFAULT };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public BandCombineDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  












  public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message)
  {
    if (!super.validateArguments(modeName, args, message)) {
      return false;
    }
    
    if (!modeName.equalsIgnoreCase("rendered")) {
      return true;
    }
    RenderedImage src = args.getRenderedSource(0);
    
    double[][] matrix = (double[][])args.getObjectParameter(0);
    SampleModel sm = src.getSampleModel();
    int rowLength = sm.getNumBands() + 1;
    
    if (matrix.length < 1) {
      message.append(getName() + ": " + JaiI18N.getString("BandCombineDescriptor2"));
      
      return false;
    }
    
    for (int i = 0; i < matrix.length; i++) {
      if (matrix[i].length != rowLength) {
        message.append(getName() + ": " + JaiI18N.getString("BandCombineDescriptor2"));
        
        return false;
      }
    }
    
    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, double[][] matrix, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BandCombine", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("matrix", matrix);
    
    return JAI.create("BandCombine", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, double[][] matrix, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("BandCombine", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("matrix", matrix);
    
    return JAI.createRenderable("BandCombine", pb, hints);
  }
}
