package javax.mail.search;










public abstract class StringTerm
  extends SearchTerm
{
  protected String pattern;
  








  protected boolean ignoreCase;
  








  protected StringTerm(String paramString)
  {
    pattern = paramString;
    ignoreCase = true;
  }
  
  protected StringTerm(String paramString, boolean paramBoolean) {
    pattern = paramString;
    ignoreCase = paramBoolean;
  }
  


  public String getPattern()
  {
    return pattern;
  }
  


  public boolean getIgnoreCase()
  {
    return ignoreCase;
  }
  
  protected boolean match(String paramString) {
    int i = paramString.length() - pattern.length();
    for (int j = 0; j <= i; j++) {
      if (paramString.regionMatches(ignoreCase, j, 
        pattern, 0, pattern.length()))
        return true;
    }
    return false;
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof StringTerm))
      return false;
    StringTerm localStringTerm = (StringTerm)paramObject;
    if (ignoreCase) {
      return (pattern.equalsIgnoreCase(pattern)) && 
        (ignoreCase == ignoreCase);
    }
    return (pattern.equals(pattern)) && 
      (ignoreCase == ignoreCase);
  }
  


  public int hashCode()
  {
    if (ignoreCase) return pattern.hashCode(); return pattern.hashCode() ^ 0xFFFFFFFF;
  }
}
