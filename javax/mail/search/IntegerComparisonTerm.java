package javax.mail.search;










public abstract class IntegerComparisonTerm
  extends ComparisonTerm
{
  protected int number;
  









  protected IntegerComparisonTerm(int paramInt1, int paramInt2)
  {
    comparison = paramInt1;
    number = paramInt2;
  }
  


  public int getNumber()
  {
    return number;
  }
  


  public int getComparison()
  {
    return comparison;
  }
  
  protected boolean match(int paramInt) {
    switch (comparison) {
    case 1: 
      return paramInt <= number;
    case 2: 
      return paramInt < number;
    case 3: 
      return paramInt == number;
    case 4: 
      return paramInt != number;
    case 5: 
      return paramInt > number;
    case 6: 
      return paramInt >= number;
    }
    return false;
  }
  



  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof IntegerComparisonTerm))
      return false;
    IntegerComparisonTerm localIntegerComparisonTerm = (IntegerComparisonTerm)paramObject;
    return (number == number) && (super.equals(paramObject));
  }
  


  public int hashCode()
  {
    return number + super.hashCode();
  }
}
