package javax.mail.search;

import javax.mail.Message;



















public final class NotTerm
  extends SearchTerm
{
  protected SearchTerm term;
  
  public NotTerm(SearchTerm paramSearchTerm)
  {
    term = paramSearchTerm;
  }
  


  public SearchTerm getTerm()
  {
    return term;
  }
  
  public boolean match(Message paramMessage)
  {
    return !term.match(paramMessage);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof NotTerm))
      return false;
    NotTerm localNotTerm = (NotTerm)paramObject;
    return term.equals(term);
  }
  


  public int hashCode()
  {
    return term.hashCode() << 1;
  }
}
