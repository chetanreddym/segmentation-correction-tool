package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import javax.mail.Flags;
















public class MailboxInfo
{
  public Flags availableFlags;
  public Flags permanentFlags;
  public int total = -1;
  public int recent = -1;
  public int first = -1;
  public int uidvalidity = -1;
  public int mode;
  
  public MailboxInfo(Response[] paramArrayOfResponse) throws ParsingException {
    for (int i = 0; i < paramArrayOfResponse.length; i++) {
      if ((paramArrayOfResponse[i] != null) && ((paramArrayOfResponse[i] instanceof IMAPResponse)))
      {

        IMAPResponse localIMAPResponse = (IMAPResponse)paramArrayOfResponse[i];
        
        if (localIMAPResponse.keyEquals("EXISTS")) {
          total = localIMAPResponse.getNumber();
          paramArrayOfResponse[i] = null;
        }
        else if (localIMAPResponse.keyEquals("RECENT")) {
          recent = localIMAPResponse.getNumber();
          paramArrayOfResponse[i] = null;
        }
        else if (localIMAPResponse.keyEquals("FLAGS")) {
          availableFlags = new FLAGS(localIMAPResponse);
          paramArrayOfResponse[i] = null;
        }
        else if ((localIMAPResponse.isUnTagged()) && (localIMAPResponse.isOK()))
        {




          localIMAPResponse.skipSpaces();
          
          if (localIMAPResponse.readByte() == 91)
          {

            String str = localIMAPResponse.readAtom();
            if (str.equalsIgnoreCase("UNSEEN")) {
              first = localIMAPResponse.readNumber();
            } else if (str.equalsIgnoreCase("UIDVALIDITY")) {
              uidvalidity = localIMAPResponse.readNumber();
            } else if (str.equalsIgnoreCase("PERMANENTFLAGS")) {
              permanentFlags = new FLAGS(localIMAPResponse);
            }
            paramArrayOfResponse[i] = null;
          }
        }
      }
    }
    



    if (permanentFlags == null) {
      permanentFlags = new Flags(availableFlags);
    }
  }
}
