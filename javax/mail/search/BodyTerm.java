package javax.mail.search;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
























public final class BodyTerm
  extends StringTerm
{
  public BodyTerm(String paramString)
  {
    super(paramString);
  }
  





  public boolean match(Message paramMessage)
  {
    Object localObject1 = null;
    String str = null;
    Object localObject2 = paramMessage;
    try
    {
      str = ((Part)localObject2).getContentType();
    } catch (Exception localException1) {
      return false;
    }
    
    if (str.regionMatches(true, 0, "text/", 0, 5)) {
      try
      {
        localObject1 = ((Part)localObject2).getContent();
      } catch (Exception localException2) {}
    } else if (str.regionMatches(true, 0, "multipart/mixed", 0, 15)) {
      try
      {
        localObject2 = ((Multipart)((Part)localObject2).getContent()).getBodyPart(0);
        str = ((Part)localObject2).getContentType();
        if (str.regionMatches(true, 0, "text/", 0, 5)) {
          localObject1 = ((Part)localObject2).getContent();
        }
      } catch (Exception localException3) {}
    }
    if ((localObject1 == null) || (!(localObject1 instanceof String))) {
      return false;
    }
    






    return super.match((String)localObject1);
  }
  


  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof BodyTerm))
      return false;
    return super.equals(paramObject);
  }
}
