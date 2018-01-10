package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import java.io.IOException;
import java.util.Vector;














public class FetchResponse
  extends IMAPResponse
{
  private Item[] items;
  
  public FetchResponse(Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    super(paramProtocol);
    parse();
  }
  
  public FetchResponse(IMAPResponse paramIMAPResponse) throws IOException, ProtocolException
  {
    super(paramIMAPResponse);
    parse();
  }
  
  public int getItemCount() {
    return items.length;
  }
  
  public Item getItem(int paramInt) {
    return items[paramInt];
  }
  
  public Item getItem(Class paramClass) {
    for (int i = 0; i < items.length; i++) {
      if (paramClass.isInstance(items[i])) {
        return items[i];
      }
    }
    return null;
  }
  
  public static Item getItem(Response[] paramArrayOfResponse, int paramInt, Class paramClass) {
    if (paramArrayOfResponse == null) {
      return null;
    }
    for (int i = 0; i < paramArrayOfResponse.length; i++)
    {
      if ((paramArrayOfResponse[i] != null) && 
        ((paramArrayOfResponse[i] instanceof FetchResponse)) && 
        (((FetchResponse)paramArrayOfResponse[i]).getNumber() == paramInt))
      {

        FetchResponse localFetchResponse = (FetchResponse)paramArrayOfResponse[i];
        for (int j = 0; j < items.length; j++) {
          if (paramClass.isInstance(items[j]))
            return items[j];
        }
      }
    }
    return null;
  }
  
  private static final char[] HEADER = { '.', 'H', 'E', 'A', 'D', 'E', 'R' };
  private static final char[] TEXT = { '.', 'T', 'E', 'X', 'T' };
  
  private void parse() throws ParsingException
  {
    skipSpaces();
    if (buffer[index] != 40) {
      throw new ParsingException(
        "error in FETCH parsing, missing '(' at index " + index);
    }
    Vector localVector = new Vector();
    Object localObject = null;
    do {
      index += 1;
      
      if (index >= size) {
        throw new ParsingException(
          "error in FETCH parsing, ran off end of buffer, size " + size);
      }
      switch (buffer[index]) {
      case 69: 
        if (match(ENVELOPE.name)) {
          index += ENVELOPE.name.length;
          localObject = new ENVELOPE(this);
        }
        break;
      case 70: 
        if (match(FLAGS.name)) {
          index += FLAGS.name.length;
          localObject = new FLAGS(this);
        }
        break;
      case 73: 
        if (match(INTERNALDATE.name)) {
          index += INTERNALDATE.name.length;
          localObject = new INTERNALDATE(this);
        }
        break;
      case 66: 
        if (match(BODY.name)) {
          if (buffer[(index + 4)] == 91) {
            index += BODY.name.length;
            localObject = new BODY(this);
          }
          else {
            if (match(BODYSTRUCTURE.name)) {
              index += BODYSTRUCTURE.name.length;
            }
            else
              index += BODY.name.length;
            localObject = new BODYSTRUCTURE(this);
          }
        }
        break;
      case 82: 
        if (match(RFC822SIZE.name)) {
          index += RFC822SIZE.name.length;
          localObject = new RFC822SIZE(this);

        }
        else if (match(RFC822DATA.name)) {
          index += RFC822DATA.name.length;
          if (match(HEADER)) {
            index += HEADER.length;
          } else if (match(TEXT))
            index += TEXT.length;
          localObject = new RFC822DATA(this);
        }
        
        break;
      case 85: 
        if (match(UID.name)) {
          index += UID.name.length;
          localObject = new UID(this);
        }
        break;
      }
      
      if (localObject != null)
        localVector.addElement(localObject);
    } while (buffer[index] != 41);
    
    index += 1;
    items = new Item[localVector.size()];
    localVector.copyInto(items);
  }
  



  private boolean match(char[] paramArrayOfChar)
  {
    int i = paramArrayOfChar.length;
    int j = 0; for (int k = index; j < i;)
    {

      if (Character.toUpperCase((char)buffer[(k++)]) != paramArrayOfChar[(j++)])
        return false; }
    return true;
  }
}
