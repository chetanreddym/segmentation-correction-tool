package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
















public class ListInfo
{
  public String name;
  public char separator = '/';
  public boolean hasInferiors = true;
  public boolean canOpen = true;
  public int changeState = 3;
  public static final int CHANGED = 1;
  public static final int UNCHANGED = 2;
  public static final int INDETERMINATE = 3;
  
  public ListInfo(IMAPResponse paramIMAPResponse) throws ParsingException
  {
    String[] arrayOfString = paramIMAPResponse.readSimpleList();
    
    if (arrayOfString != null)
    {
      for (int i = 0; i < arrayOfString.length; i++) {
        if (arrayOfString[i].equalsIgnoreCase("\\Marked")) {
          changeState = 1;
        } else if (arrayOfString[i].equalsIgnoreCase("\\Unmarked")) {
          changeState = 2;
        } else if (arrayOfString[i].equalsIgnoreCase("\\Noselect")) {
          canOpen = false;
        } else if (arrayOfString[i].equalsIgnoreCase("\\Noinferiors")) {
          hasInferiors = false;
        }
      }
    }
    paramIMAPResponse.skipSpaces();
    if (paramIMAPResponse.readByte() == 34) {
      if ((this.separator = (char)paramIMAPResponse.readByte()) == '\\')
      {
        separator = ((char)paramIMAPResponse.readByte()); }
      paramIMAPResponse.skip(1);
    } else {
      paramIMAPResponse.skip(2);
    }
    paramIMAPResponse.skipSpaces();
    name = paramIMAPResponse.readAtomString();
    

    name = BASE64MailboxDecoder.decode(name);
  }
}
