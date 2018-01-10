package javax.mail.search;




public abstract class ComparisonTerm
  extends SearchTerm
{
  public static final int LE = 1;
  


  public static final int LT = 2;
  


  public static final int EQ = 3;
  


  public static final int NE = 4;
  


  public static final int GT = 5;
  


  public static final int GE = 6;
  


  protected int comparison;
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ComparisonTerm))
      return false;
    ComparisonTerm localComparisonTerm = (ComparisonTerm)paramObject;
    return comparison == comparison;
  }
  


  public int hashCode()
  {
    return comparison;
  }
  
  public ComparisonTerm() {}
}
