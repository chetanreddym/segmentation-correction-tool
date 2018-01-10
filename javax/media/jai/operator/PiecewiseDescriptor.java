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







































































public class PiecewiseDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Piecewise" }, { "LocalName", "Piecewise" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("PiecewiseDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PiecewiseDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion") }, { "arg0Desc", "The breakpoint array." } };
  









  private static final Class[] paramClasses = { new float[0].getClass() };
  



  private static final String[] paramNames = { "breakPoints" };
  



  private static final Object[] paramDefaults = { { { { 0.0F, 255.0F }, { 0.0F, 255.0F } } } };
  


  private static final String[] supportedModes = { "rendered", "renderable" };
  



  public PiecewiseDescriptor()
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
    
    float[][][] breakPoints = (float[][][])args.getObjectParameter(0);
    






    if ((breakPoints.length != 1) && (breakPoints.length != src.getSampleModel().getNumBands()))
    {

      msg.append(getName() + " " + JaiI18N.getString("PiecewiseDescriptor1"));
      
      return false;
    }
    int numBands = breakPoints.length;
    for (int b = 0; b < numBands; b++) {
      if (breakPoints[b].length != 2)
      {
        msg.append(getName() + " " + JaiI18N.getString("PiecewiseDescriptor2"));
        
        return false; }
      if (breakPoints[b][0].length != breakPoints[b][1].length)
      {

        msg.append(getName() + " " + JaiI18N.getString("PiecewiseDescriptor3"));
        
        return false;
      }
    }
    for (int b = 0; b < numBands; b++) {
      int count = breakPoints[b][0].length - 1;
      float[] x = breakPoints[b][0];
      for (int i = 0; i < count; i++) {
        if (x[i] >= x[(i + 1)])
        {
          msg.append(getName() + " " + JaiI18N.getString("PiecewiseDescriptor4"));
          
          return false;
        }
      }
    }
    

    return true;
  }
  





















  public static RenderedOp create(RenderedImage source0, float[][][] breakPoints, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Piecewise", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("breakPoints", breakPoints);
    
    return JAI.create("Piecewise", pb, hints);
  }
  




















  public static RenderableOp createRenderable(RenderableImage source0, float[][][] breakPoints, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Piecewise", "renderable");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("breakPoints", breakPoints);
    
    return JAI.createRenderable("Piecewise", pb, hints);
  }
}
