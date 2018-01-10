package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
















public class Status
{
  public String mbox;
  public int total = -1;
  public int recent = -1;
  public long uidnext = -1L;
  public long uidvalidity = -1L;
  public int unseen = -1;
  

  public static String[] standardItems = { "MESSAGES", "RECENT", "UNSEEN", "UIDNEXT", "UIDVALIDITY" };
  
  public Status(Response paramResponse) throws ParsingException {
    mbox = paramResponse.readAtomString();
    paramResponse.skipSpaces();
    if (paramResponse.readByte() != 40) {
      throw new ParsingException("parse error in STATUS");
    }
    do {
      String str = paramResponse.readAtom();
      if (str.equalsIgnoreCase("MESSAGES")) {
        total = paramResponse.readNumber();
      } else if (str.equalsIgnoreCase("RECENT")) {
        recent = paramResponse.readNumber();
      } else if (str.equalsIgnoreCase("UIDNEXT")) {
        uidnext = paramResponse.readLong();
      } else if (str.equalsIgnoreCase("UIDVALIDITY")) {
        uidvalidity = paramResponse.readLong();
      } else if (str.equalsIgnoreCase("UNSEEN"))
        unseen = paramResponse.readNumber();
    } while (paramResponse.readByte() != 41);
  }
  
  public static void add(Status paramStatus1, Status paramStatus2) {
    if (total != -1)
      total = total;
    if (recent != -1)
      recent = recent;
    if (uidnext != -1L)
      uidnext = uidnext;
    if (uidvalidity != -1L)
      uidvalidity = uidvalidity;
    if (unseen != -1) {
      unseen = unseen;
    }
  }
}
