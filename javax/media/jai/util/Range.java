package javax.media.jai.util;

import java.io.Serializable;
















































public class Range
  implements Serializable
{
  private boolean isMaxIncluded = true; private boolean isMinIncluded = true;
  





  private Comparable maxValue;
  





  private Comparable minValue;
  





  private Class elementClass;
  





  public Range(Class elementClass, Comparable minValue, Comparable maxValue)
  {
    if ((minValue == null) && (maxValue == null)) {
      Class c = null;
      try {
        c = Class.forName("java.lang.Comparable");
      }
      catch (ClassNotFoundException e) {}
      

      if (!c.isAssignableFrom(elementClass)) {
        throw new IllegalArgumentException(JaiI18N.getString("Range0"));
      }
    }
    this.elementClass = elementClass;
    
    if ((minValue != null) && (minValue.getClass() != this.elementClass)) {
      throw new IllegalArgumentException(JaiI18N.getString("Range1"));
    }
    
    this.minValue = minValue;
    
    if ((maxValue != null) && (maxValue.getClass() != this.elementClass)) {
      throw new IllegalArgumentException(JaiI18N.getString("Range2"));
    }
    
    this.maxValue = maxValue;
  }
  


































  public Range(Class elementClass, Comparable minValue, boolean isMinIncluded, Comparable maxValue, boolean isMaxIncluded)
  {
    this(elementClass, minValue, maxValue);
  }
  






  public boolean isMinIncluded()
  {
    if (minValue == null) {
      return true;
    }
    return isMinIncluded;
  }
  




  public boolean isMaxIncluded()
  {
    if (maxValue == null) {
      return true;
    }
    return isMaxIncluded;
  }
  


  public Class getElementClass()
  {
    return elementClass;
  }
  



  public Comparable getMinValue()
  {
    return minValue;
  }
  



  public Comparable getMaxValue()
  {
    return maxValue;
  }
  











  public boolean contains(Comparable value)
  {
    if ((value != null) && (value.getClass() != elementClass)) {
      throw new IllegalArgumentException(JaiI18N.getString("Range3"));
    }
    

    if (isEmpty() == true) {
      return false;
    }
    
    return (isUnderUpperBound(value)) && (isOverLowerBound(value));
  }
  






  private boolean isUnderUpperBound(Comparable value)
  {
    if (maxValue == null) {
      return true;
    }
    

    if (value == null) {
      return false;
    }
    if (isMaxIncluded)
      return maxValue.compareTo(value) >= 0;
    return maxValue.compareTo(value) > 0;
  }
  




  private boolean isOverLowerBound(Comparable value)
  {
    if (minValue == null) {
      return true;
    }
    

    if (value == null) {
      return false;
    }
    if (isMinIncluded)
      return minValue.compareTo(value) <= 0;
    return minValue.compareTo(value) < 0;
  }
  











  public boolean contains(Range range)
  {
    if (range == null)
      throw new IllegalArgumentException(JaiI18N.getString("Range5"));
    if (elementClass != range.getElementClass()) {
      throw new IllegalArgumentException(JaiI18N.getString("Range4"));
    }
    if (range.isEmpty()) {
      return true;
    }
    Comparable min = range.getMinValue();
    Comparable max = range.getMaxValue();
    boolean maxSide;
    boolean maxSide;
    if (max == null) {
      maxSide = maxValue == null;
    } else {
      maxSide = (isUnderUpperBound(max)) || ((isMaxIncluded == range.isMaxIncluded()) && (max.equals(maxValue)));
    }
    boolean minSide;
    boolean minSide;
    if (min == null) {
      minSide = minValue == null;
    } else {
      minSide = (isOverLowerBound(min)) || ((isMinIncluded == range.isMinIncluded()) && (min.equals(minValue)));
    }
    
    return (minSide) && (maxSide);
  }
  








  public boolean intersects(Range range)
  {
    if (range == null)
      throw new IllegalArgumentException(JaiI18N.getString("Range5"));
    if (elementClass != range.getElementClass()) {
      throw new IllegalArgumentException(JaiI18N.getString("Range4"));
    }
    return !intersect(range).isEmpty();
  }
  














  public Range union(Range range)
  {
    if (range == null)
      throw new IllegalArgumentException(JaiI18N.getString("Range5"));
    if (elementClass != range.getElementClass()) {
      throw new IllegalArgumentException(JaiI18N.getString("Range4"));
    }
    if (isEmpty()) {
      return new Range(elementClass, range.getMinValue(), range.isMinIncluded(), range.getMaxValue(), range.isMaxIncluded());
    }
    

    if (range.isEmpty()) {
      return new Range(elementClass, this.minValue, this.isMinIncluded, this.maxValue, this.isMaxIncluded);
    }
    
    boolean containMin = !isOverLowerBound(range.getMinValue());
    boolean containMax = !isUnderUpperBound(range.getMaxValue());
    




    Comparable minValue = containMin ? range.getMinValue() : this.minValue;
    Comparable maxValue = containMax ? range.getMaxValue() : this.maxValue;
    boolean isMinIncluded = containMin ? range.isMinIncluded() : this.isMinIncluded;
    
    boolean isMaxIncluded = containMax ? range.isMaxIncluded() : this.isMaxIncluded;
    
    return new Range(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
  }
  










  public Range intersect(Range range)
  {
    if (range == null)
      throw new IllegalArgumentException(JaiI18N.getString("Range5"));
    if (elementClass != range.getElementClass()) {
      throw new IllegalArgumentException(JaiI18N.getString("Range4"));
    }
    if (isEmpty()) {
      Comparable temp = this.minValue;
      if (temp == null) {
        temp = this.maxValue;
      }
      


      return new Range(elementClass, temp, false, temp, false);
    }
    
    if (range.isEmpty()) {
      Comparable temp = range.getMinValue();
      if (temp == null) {
        temp = range.getMaxValue();
      }
      


      return new Range(elementClass, temp, false, temp, false);
    }
    
    boolean containMin = !isOverLowerBound(range.getMinValue());
    boolean containMax = !isUnderUpperBound(range.getMaxValue());
    



    Comparable minValue = containMin ? this.minValue : range.getMinValue();
    Comparable maxValue = containMax ? this.maxValue : range.getMaxValue();
    boolean isMinIncluded = containMin ? this.isMinIncluded : range.isMinIncluded();
    
    boolean isMaxIncluded = containMax ? this.isMaxIncluded : range.isMaxIncluded();
    
    return new Range(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
  }
  





















  public Range[] subtract(Range range)
  {
    if (range == null)
      throw new IllegalArgumentException(JaiI18N.getString("Range5"));
    if (elementClass != range.getElementClass()) {
      throw new IllegalArgumentException(JaiI18N.getString("Range4"));
    }
    

    if ((isEmpty()) || (range.isEmpty())) {
      Range[] ra = { new Range(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded) };
      
      return ra;
    }
    
    Comparable min = range.getMinValue();
    Comparable max = range.getMaxValue();
    boolean minIn = range.isMinIncluded();
    boolean maxIn = range.isMaxIncluded();
    
    if ((minValue == null) && (maxValue == null) && (min == null) && (max == null))
    {
      Range[] ra = { null };
      return ra;
    }
    
    boolean containMin = contains(min);
    boolean containMax = contains(max);
    

    if ((containMin) && (containMax)) {
      Range r1 = new Range(elementClass, minValue, isMinIncluded, min, !minIn);
      
      Range r2 = new Range(elementClass, max, !maxIn, maxValue, isMaxIncluded);
      






      if ((r1.isEmpty()) || ((minValue == null) && (min == null))) {
        Range[] ra = { r2 };
        return ra;
      }
      
      if ((r2.isEmpty()) || ((maxValue == null) && (max == null))) {
        Range[] ra = { r1 };
        return ra;
      }
      Range[] ra = { r1, r2 };
      return ra;
    }
    

    if (containMax) {
      Range[] ra = { new Range(elementClass, max, !maxIn, maxValue, isMaxIncluded) };
      
      return ra;
    }
    

    if (containMin) {
      Range[] ra = { new Range(elementClass, minValue, isMinIncluded, min, !minIn) };
      
      return ra;
    }
    

    if (((min != null) && (!isUnderUpperBound(min))) || ((max != null) && (!isOverLowerBound(max))))
    {
      Range[] ra = { new Range(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded) };
      
      return ra;
    }
    

    min = minValue == null ? maxValue : minValue;
    Range[] ra = { new Range(elementClass, min, false, min, false) };
    return ra;
  }
  






  public int hashCode()
  {
    int code = elementClass.hashCode();
    
    if (isEmpty()) {
      return code;
    }
    code ^= 0x7FFFFFFF;
    
    if (minValue != null) {
      code ^= minValue.hashCode();
      if (isMinIncluded) {
        code ^= 0xFFFF0000;
      }
    }
    if (maxValue != null) {
      code ^= maxValue.hashCode() * 31;
      if (isMaxIncluded) {
        code ^= 0xFFFF;
      }
    }
    return code;
  }
  












  public boolean equals(Object other)
  {
    if (other == null) {
      return false;
    }
    
    if (!(other instanceof Range)) {
      return false;
    }
    Range r = (Range)other;
    

    if (elementClass != r.getElementClass()) {
      return false;
    }
    
    if ((isEmpty()) && (r.isEmpty())) {
      return true;
    }
    Comparable min = r.getMinValue();
    
    if (minValue != null) {
      if (!minValue.equals(min)) {
        return false;
      }
      if (isMinIncluded != r.isMinIncluded()) {
        return false;
      }
      
    }
    else if (min != null) {
      return false;
    }
    Comparable max = r.getMaxValue();
    
    if (maxValue != null) {
      if (!maxValue.equals(max)) {
        return false;
      }
      if (isMaxIncluded != r.isMaxIncluded()) {
        return false;
      }
      
    }
    else if (max != null) {
      return false;
    }
    return true;
  }
  







  public boolean isEmpty()
  {
    if ((minValue == null) || (maxValue == null)) {
      return false;
    }
    int cmp = minValue.compareTo(maxValue);
    

    if (cmp > 0) {
      return true;
    }
    

    if (cmp == 0) {
      return !(isMinIncluded & isMaxIncluded);
    }
    
    return false;
  }
  




  public String toString()
  {
    char c1 = isMinIncluded ? '[' : '(';
    char c2 = isMaxIncluded ? ']' : ')';
    

    if ((minValue != null) && (maxValue != null)) {
      return new String(c1 + minValue.toString() + ", " + maxValue.toString() + c2);
    }
    

    if (maxValue != null) {
      return new String(c1 + "---, " + maxValue.toString() + c2);
    }
    
    if (minValue != null) {
      return new String(c1 + minValue.toString() + ", " + "---" + c2);
    }
    
    return new String(c1 + "---, ---" + c2);
  }
}
