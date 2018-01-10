package javax.mail.search;

import javax.mail.Message;






















public final class SubjectTerm
  extends StringTerm
{
  public SubjectTerm(String paramString)
  {
    super(paramString);
  }
  



  public boolean match(Message paramMessage)
  {
    String str;
    


    try
    {
      str = paramMessage.getSubject();
    } catch (Exception localException) {
      return false;
    }
    
    if (str == null) {
      return false;
    }
    return super.match(str);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SubjectTerm))
      return false;
    return super.equals(paramObject);
  }
}
