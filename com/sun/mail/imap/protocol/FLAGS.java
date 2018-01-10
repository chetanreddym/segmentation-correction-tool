package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import javax.mail.Flags;
import javax.mail.Flags.Flag;















public class FLAGS
  extends Flags
  implements Item
{
  public static char[] name = { 'F', 'L', 'A', 'G', 'S' };
  
  public int msgno;
  
  public FLAGS(IMAPResponse paramIMAPResponse)
    throws ParsingException
  {
    msgno = paramIMAPResponse.getNumber();
    
    paramIMAPResponse.skipSpaces();
    String[] arrayOfString = paramIMAPResponse.readSimpleList();
    if (arrayOfString != null) {
      for (int i = 0; i < arrayOfString.length; i++) {
        String str = arrayOfString[i];
        if ((str.length() >= 2) && (str.charAt(0) == '\\')) {}
        switch (Character.toUpperCase(str.charAt(1))) {
        case 'S': 
          add(Flags.Flag.SEEN);
          break;
        case 'R': 
          add(Flags.Flag.RECENT);
          break;
        case 'D': 
          if (str.length() >= 3) {
            int j = str.charAt(2);
            if ((j == 101) || (j == 69)) {
              add(Flags.Flag.DELETED);
            } else if ((j == 114) || (j == 82))
              add(Flags.Flag.DRAFT);
          } else {
            add(str); }
          break;
        case 'A': 
          add(Flags.Flag.ANSWERED);
          break;
        case 'F': 
          add(Flags.Flag.FLAGGED);
          break;
        case '*': 
          add(Flags.Flag.USER);
          break;
        default: 
          add(str);
          continue;
          

          add(str);
        }
      }
    }
  }
}
