package javax.media.jai.remote;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.media.jai.util.Range;




























public class NegotiableNumericRange
  implements Negotiable
{
  private Range range;
  
  public NegotiableNumericRange(Range range)
  {
    if (range == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumericRange0"));
    }
    



    if (!Number.class.isAssignableFrom(range.getElementClass())) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumericRange1"));
    }
    

    this.range = range;
  }
  



  public Range getRange()
  {
    if (range.isEmpty())
      return null;
    return range;
  }
  











  public Negotiable negotiate(Negotiable other)
  {
    if (other == null) {
      return null;
    }
    

    if (!(other instanceof NegotiableNumericRange)) {
      return null;
    }
    NegotiableNumericRange otherNNRange = (NegotiableNumericRange)other;
    Range otherRange = otherNNRange.getRange();
    


    if (otherRange == null) {
      return null;
    }
    
    if (otherRange.getElementClass() != range.getElementClass()) {
      return null;
    }
    Range result = range.intersect(otherRange);
    

    if (result.isEmpty()) {
      return null;
    }
    return new NegotiableNumericRange(result);
  }
  











  public Object getNegotiatedValue()
  {
    if (range.isEmpty()) {
      return null;
    }
    Number minValue = (Number)range.getMinValue();
    
    if (minValue == null) {
      Number maxValue = (Number)range.getMaxValue();
      
      if (maxValue == null)
      {
        Class elementClass = range.getElementClass();
        

        if (elementClass == Byte.class)
          return new Byte((byte)0);
        if (elementClass == Short.class)
          return new Short((short)0);
        if (elementClass == Integer.class)
          return new Integer(0);
        if (elementClass == Long.class)
          return new Long(0L);
        if (elementClass == Float.class)
          return new Float(0.0F);
        if (elementClass == Double.class)
          return new Double(0.0D);
        if (elementClass == BigInteger.class)
          return BigInteger.ZERO;
        if (elementClass == BigDecimal.class) {
          return new BigDecimal(BigInteger.ZERO);
        }
      } else {
        return maxValue;
      }
    }
    
    return minValue;
  }
  



  public Class getNegotiatedValueClass()
  {
    return range.getElementClass();
  }
}
