package javax.mail.search;

import java.io.Serializable;
import javax.mail.Message;

public abstract class SearchTerm
  implements Serializable
{
  public abstract boolean match(Message paramMessage);
  
  public SearchTerm() {}
}
