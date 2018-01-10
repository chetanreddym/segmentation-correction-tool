package javax.mail.search;

import java.util.Date;
























public abstract class DateTerm
  extends ComparisonTerm
{
  protected Date date;
  
  protected DateTerm(int paramInt, Date paramDate)
  {
    comparison = paramInt;
    date = paramDate;
  }
  


  public Date getDate()
  {
    return new Date(date.getTime());
  }
  


  public int getComparison()
  {
    return comparison;
  }
  





  protected boolean match(Date paramDate)
  {
    switch (comparison) {
    case 1: 
      return (paramDate.before(date)) || (paramDate.equals(date));
    case 2: 
      return paramDate.before(date);
    case 3: 
      return paramDate.equals(date);
    case 4: 
      return !paramDate.equals(date);
    case 5: 
      return paramDate.after(date);
    case 6: 
      return (paramDate.after(date)) || (paramDate.equals(date));
    }
    return false;
  }
  



  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof DateTerm))
      return false;
    DateTerm localDateTerm = (DateTerm)paramObject;
    return (date.equals(date)) && (super.equals(paramObject));
  }
  


  public int hashCode()
  {
    return date.hashCode() + super.hashCode();
  }
}
