package javax.mail.search;

import javax.mail.Message;





















public final class SizeTerm
  extends IntegerComparisonTerm
{
  public SizeTerm(int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
  }
  



  public boolean match(Message paramMessage)
  {
    int i;
    

    try
    {
      i = paramMessage.getSize();
    } catch (Exception localException) {
      return false;
    }
    
    if (i == -1) {
      return false;
    }
    return super.match(i);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SizeTerm))
      return false;
    return super.equals(paramObject);
  }
}
