package javax.media.jai.remote;










public class NegotiableNumeric
  implements Negotiable
{
  Number number;
  








  Class elementClass;
  








  public NegotiableNumeric(byte b)
  {
    number = new Byte(b);
    elementClass = number.getClass();
  }
  





  public NegotiableNumeric(short s)
  {
    number = new Short(s);
    elementClass = number.getClass();
  }
  





  public NegotiableNumeric(int i)
  {
    number = new Integer(i);
    elementClass = number.getClass();
  }
  





  public NegotiableNumeric(long l)
  {
    number = new Long(l);
    elementClass = number.getClass();
  }
  





  public NegotiableNumeric(float f)
  {
    number = new Float(f);
    elementClass = number.getClass();
  }
  





  public NegotiableNumeric(double d)
  {
    number = new Double(d);
    elementClass = number.getClass();
  }
  








  public NegotiableNumeric(Number n)
  {
    if (n == null) {
      throw new IllegalArgumentException(JaiI18N.getString("NegotiableNumeric0"));
    }
    

    number = n;
    elementClass = number.getClass();
  }
  





  public Number getNumber()
  {
    return number;
  }
  











  public Negotiable negotiate(Negotiable other)
  {
    if (other == null) {
      return null;
    }
    if ((!(other instanceof NegotiableNumeric)) || (other.getNegotiatedValueClass() != elementClass))
    {
      return null;
    }
    
    NegotiableNumeric otherNN = (NegotiableNumeric)other;
    
    if (number.equals(otherNN.getNumber())) {
      return new NegotiableNumeric(number);
    }
    return null;
  }
  






  public Object getNegotiatedValue()
  {
    return number;
  }
  






  public Class getNegotiatedValueClass()
  {
    return elementClass;
  }
  





  public byte getNegotiatedValueAsByte()
  {
    if (elementClass != Byte.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.byteValue();
  }
  





  public short getNegotiatedValueAsShort()
  {
    if (elementClass != Short.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.shortValue();
  }
  





  public int getNegotiatedValueAsInt()
  {
    if (elementClass != Integer.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.intValue();
  }
  





  public long getNegotiatedValueAsLong()
  {
    if (elementClass != Long.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.longValue();
  }
  





  public float getNegotiatedValueAsFloat()
  {
    if (elementClass != Float.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.floatValue();
  }
  





  public double getNegotiatedValueAsDouble()
  {
    if (elementClass != Double.class) {
      throw new ClassCastException(JaiI18N.getString("NegotiableNumeric1"));
    }
    return number.doubleValue();
  }
}
