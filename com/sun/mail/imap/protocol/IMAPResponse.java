package com.sun.mail.imap.protocol;

import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.util.ASCIIUtility;
import java.io.IOException;
import java.util.Vector;













public class IMAPResponse
  extends Response
{
  private String key;
  private int number;
  
  public IMAPResponse(Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    super(paramProtocol);
    

    if ((isUnTagged()) && (!isOK()) && (!isNO()) && (!isBAD()) && (!isBYE())) {
      key = readAtom();
      
      try
      {
        number = Integer.parseInt(key);
        key = readAtom();
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
  }
  

  public IMAPResponse(IMAPResponse paramIMAPResponse)
  {
    super(paramIMAPResponse);
    key = key;
    number = number;
  }
  





  public String[] readSimpleList()
  {
    skipSpaces();
    
    if (buffer[index] != 40)
      return null;
    index += 1;
    
    Vector localVector = new Vector();
    
    for (int i = index; buffer[index] != 41; index += 1) {
      if (buffer[index] == 32) {
        localVector.addElement(ASCIIUtility.toString(buffer, i, index));
        i = index + 1;
      }
    }
    if (index > i)
      localVector.addElement(ASCIIUtility.toString(buffer, i, index));
    index += 1;
    
    int j = localVector.size();
    if (j > 0) {
      String[] arrayOfString = new String[j];
      localVector.copyInto(arrayOfString);
      return arrayOfString;
    }
    return null;
  }
  
  public String getKey() {
    return key;
  }
  
  public boolean keyEquals(String paramString) {
    if ((key != null) && (key.equalsIgnoreCase(paramString))) {
      return true;
    }
    return false;
  }
  
  public int getNumber() {
    return number;
  }
  
  public static IMAPResponse readResponse(Protocol paramProtocol) throws IOException, ProtocolException
  {
    Object localObject = new IMAPResponse(paramProtocol);
    if (((IMAPResponse)localObject).keyEquals("FETCH"))
      localObject = new FetchResponse((IMAPResponse)localObject);
    return localObject;
  }
}
