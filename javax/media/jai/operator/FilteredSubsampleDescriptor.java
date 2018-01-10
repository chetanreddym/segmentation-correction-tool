package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;






































































































































































































































































































































































public class FilteredSubsampleDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "FilteredSubsample" }, { "LocalName", "FilteredSubsample" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("FilteredSubsampleDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FilteredSubsampleDescriptor.html" }, { "Version", "1.0" }, { "arg0Desc", "The X subsample factor." }, { "arg1Desc", "The Y subsample factor." }, { "arg2Desc", "Symmetric filter coefficients." }, { "arg3Desc", "Interpolation object." } };
  












  private static final Class[] paramClasses = { Integer.class, Integer.class, new float[0].getClass(), Interpolation.class };
  




  private static final String[] paramNames = { "scaleX", "scaleY", "qsFilterArray", "interpolation" };
  



  private static final Object[] paramDefaults = { new Integer(2), new Integer(2), null, Interpolation.getInstance(0) };
  





  private static final String[] supportedModes = { "rendered" };
  


  public FilteredSubsampleDescriptor()
  {
    super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
  }
  
















  protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg)
  {
    if (!super.validateParameters(modeName, args, msg)) {
      return false;
    }
    int scaleX = args.getIntParameter(0);
    int scaleY = args.getIntParameter(1);
    if ((scaleX < 1) || (scaleY < 1)) {
      msg.append(getName() + " " + JaiI18N.getString("FilteredSubsampleDescriptor1"));
      
      return false;
    }
    
    float[] filter = (float[])args.getObjectParameter(2);
    


    if (filter == null) {
      int m = scaleX > scaleY ? scaleX : scaleY;
      if ((m & 0x1) == 0) {
        m++;
      }
      double sigma = (m - 1) / 6.0D;
      


      if (m == 1) {
        sigma = 1.0D;
      }
      filter = new float[m / 2 + 1];
      float sum = 0.0F;
      
      for (int i = 0; i < filter.length; i++) {
        filter[i] = ((float)gaussian(i, sigma));
        if (i == 0) {
          sum += filter[i];
        } else {
          sum += filter[i] * 2.0F;
        }
      }
      for (int i = 0; i < filter.length; i++) {
        filter[i] /= sum;
      }
      
      args.set(filter, 2);
    }
    
    Interpolation interp = (Interpolation)args.getObjectParameter(3);
    

    if ((!(interp instanceof InterpolationNearest)) && (!(interp instanceof InterpolationBilinear)) && (!(interp instanceof InterpolationBicubic)) && (!(interp instanceof InterpolationBicubic2)))
    {


      msg.append(getName() + " " + JaiI18N.getString("FilteredSubsampleDescriptor2"));
      
      return false;
    }
    return true;
  }
  




  private double gaussian(double x, double sigma)
  {
    return Math.exp(-x * x / (2.0D * sigma * sigma)) / sigma / Math.sqrt(6.283185307179586D);
  }
  































  public static RenderedOp create(RenderedImage source0, Integer scaleX, Integer scaleY, float[] qsFilterArray, Interpolation interpolation, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("FilteredSubsample", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("scaleX", scaleX);
    pb.setParameter("scaleY", scaleY);
    pb.setParameter("qsFilterArray", qsFilterArray);
    pb.setParameter("interpolation", interpolation);
    
    return JAI.create("FilteredSubsample", pb, hints);
  }
}
