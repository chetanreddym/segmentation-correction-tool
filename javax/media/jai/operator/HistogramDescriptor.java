package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;




































































































public class HistogramDescriptor
  extends OperationDescriptorImpl
{
  private static final String[][] resources = { { "GlobalName", "Histogram" }, { "LocalName", "Histogram" }, { "Vendor", "com.sun.media.jai" }, { "Description", JaiI18N.getString("HistogramDescriptor0") }, { "DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/HistogramDescriptor.html" }, { "Version", JaiI18N.getString("DescriptorVersion2") }, { "arg0Desc", JaiI18N.getString("HistogramDescriptor1") }, { "arg1Desc", JaiI18N.getString("HistogramDescriptor2") }, { "arg2Desc", JaiI18N.getString("HistogramDescriptor3") }, { "arg3Desc", JaiI18N.getString("HistogramDescriptor4") }, { "arg4Desc", JaiI18N.getString("HistogramDescriptor5") }, { "arg5Desc", JaiI18N.getString("HistogramDescriptor6") } };
  














  private static final String[] paramNames = { "roi", "xPeriod", "yPeriod", "numBins", "lowValue", "highValue" };
  








  private static final Class[] paramClasses = { ROI.class, Integer.class, Integer.class, new int[0].getClass(), new double[0].getClass(), new double[0].getClass() };
  








  private static final Object[] paramDefaults = { null, new Integer(1), new Integer(1), { 256 }, { 0.0D }, { 256.0D } };
  







  public HistogramDescriptor()
  {
    super(resources, 1, paramClasses, paramNames, paramDefaults);
  }
  



  public Number getParamMinValue(int index)
  {
    switch (index) {
    case 1: 
    case 2: 
      return new Integer(1);
    case 0: 
    case 3: 
    case 4: 
    case 5: 
      return null;
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  















  protected boolean validateParameters(ParameterBlock args, StringBuffer msg)
  {
    if ((args == null) || (msg == null)) {
      throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
    }
    
    if (!super.validateParameters(args, msg)) {
      return false;
    }
    
    int[] numBins = (int[])args.getObjectParameter(3);
    double[] lowValue = (double[])args.getObjectParameter(4);
    double[] highValue = (double[])args.getObjectParameter(5);
    
    int l1 = numBins.length;
    int l2 = lowValue.length;
    int l3 = highValue.length;
    
    int length = Math.max(l1, Math.max(l2, l3));
    
    for (int i = 0; i < length; i++) {
      if ((i < l1) && (numBins[i] <= 0)) {
        msg.append(getName() + " " + JaiI18N.getString("HistogramDescriptor7"));
        
        return false;
      }
      
      double l = i < l2 ? lowValue[i] : lowValue[0];
      double h = i < l3 ? highValue[i] : highValue[0];
      
      if (l >= h) {
        msg.append(getName() + " " + JaiI18N.getString("HistogramDescriptor8"));
        
        return false;
      }
    }
    
    return true;
  }
  




































  public static RenderedOp create(RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, int[] numBins, double[] lowValue, double[] highValue, RenderingHints hints)
  {
    ParameterBlockJAI pb = new ParameterBlockJAI("Histogram", "rendered");
    


    pb.setSource("source0", source0);
    
    pb.setParameter("roi", roi);
    pb.setParameter("xPeriod", xPeriod);
    pb.setParameter("yPeriod", yPeriod);
    pb.setParameter("numBins", numBins);
    pb.setParameter("lowValue", lowValue);
    pb.setParameter("highValue", highValue);
    
    return JAI.create("Histogram", pb, hints);
  }
}
