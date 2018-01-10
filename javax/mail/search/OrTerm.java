package javax.mail.search;

import javax.mail.Message;



























public final class OrTerm
  extends SearchTerm
{
  protected SearchTerm[] terms;
  
  public OrTerm(SearchTerm paramSearchTerm1, SearchTerm paramSearchTerm2)
  {
    terms = new SearchTerm[2];
    terms[0] = paramSearchTerm1;
    terms[1] = paramSearchTerm2;
  }
  





  public OrTerm(SearchTerm[] paramArrayOfSearchTerm)
  {
    terms = new SearchTerm[paramArrayOfSearchTerm.length];
    for (int i = 0; i < paramArrayOfSearchTerm.length; i++) {
      terms[i] = paramArrayOfSearchTerm[i];
    }
  }
  

  public SearchTerm[] getTerms()
  {
    return (SearchTerm[])terms.clone();
  }
  










  public boolean match(Message paramMessage)
  {
    for (int i = 0; i < terms.length; i++)
      if (terms[i].match(paramMessage))
        return true;
    return false;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof OrTerm))
      return false;
    OrTerm localOrTerm = (OrTerm)paramObject;
    if (terms.length != terms.length)
      return false;
    for (int i = 0; i < terms.length; i++)
      if (!terms[i].equals(terms[i]))
        return false;
    return true;
  }
  


  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < terms.length; j++)
      i += terms[j].hashCode();
    return i;
  }
}
