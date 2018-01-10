package javax.mail.internet;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.mail.Header;





































































































































































































































































































































































































class matchEnum
  implements Enumeration
{
  private Enumeration e;
  private String[] names;
  private boolean match;
  private boolean want_line;
  private hdr next_header;
  
  matchEnum(Vector paramVector, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    e = paramVector.elements();
    names = paramArrayOfString;
    match = paramBoolean1;
    want_line = paramBoolean2;
    next_header = null;
  }
  




  public boolean hasMoreElements()
  {
    if (next_header == null)
      next_header = nextMatch();
    return next_header != null;
  }
  


  public Object nextElement()
  {
    if (next_header == null) {
      next_header = nextMatch();
    }
    if (next_header == null) {
      throw new NoSuchElementException("No more headers");
    }
    hdr localHdr = next_header;
    next_header = null;
    if (want_line) {
      return line;
    }
    return new Header(localHdr.getName(), 
      localHdr.getValue());
  }
  




  private hdr nextMatch()
  {
    while (e.hasMoreElements()) {
      hdr localHdr = (hdr)e.nextElement();
      

      if (line != null)
      {


        if (names == null) {
          if (match) return null; return localHdr;
        }
        
        for (int i = 0; i < names.length; i++) {
          if (names[i].equalsIgnoreCase(name)) {
            if (!match) break;
            return localHdr;
          }
        }
        





        if (!match)
          return localHdr;
      } }
    return null;
  }
}
