package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import java.util.Vector;


























public class Namespaces
{
  public Namespaces(Response paramResponse)
    throws ProtocolException
  {}
  
  public static class Namespace
  {
    public String prefix;
    public char delimiter;
    
    public Namespace(Response paramResponse)
      throws ProtocolException
    {
      if (paramResponse.readByte() != 40) {
        throw new ProtocolException(
          "Missing '(' at start of Namespace");
      }
      prefix = BASE64MailboxDecoder.decode(paramResponse.readString());
      paramResponse.skipSpaces();
      
      if (paramResponse.peekByte() == 34) {
        paramResponse.readByte();
        delimiter = ((char)paramResponse.readByte());
        if (paramResponse.readByte() != 34)
          throw new ProtocolException(
            "Missing '\"' at end of QUOTED_CHAR");
      } else {
        String str = paramResponse.readAtom();
        if (str == null)
          throw new ProtocolException("Expected NIL, got null");
        if (!str.equalsIgnoreCase("NIL"))
          throw new ProtocolException("Expected NIL, got " + str);
        delimiter = '\000';
      }
      
      if (paramResponse.peekByte() != 41)
      {


        paramResponse.skipSpaces();
        paramResponse.readString();
        paramResponse.skipSpaces();
        paramResponse.readStringList();
      }
      if (paramResponse.readByte() != 41) {
        throw new ProtocolException("Missing ')' at end of Namespace");
      }
    }
  }
  





















  public Namespace[] personal = getNamespaces(paramResponse);
  public Namespace[] otherUsers = getNamespaces(paramResponse);
  public Namespace[] shared = getNamespaces(paramResponse);
  


  private Namespace[] getNamespaces(Response paramResponse)
    throws ProtocolException
  {
    paramResponse.skipSpaces();
    
    if (paramResponse.peekByte() == 40) {
      localObject1 = new Vector();
      paramResponse.readByte();
      do {
        localObject2 = new Namespace(paramResponse);
        ((Vector)localObject1).addElement(localObject2);
      } while (paramResponse.peekByte() != 41);
      paramResponse.readByte();
      Object localObject2 = new Namespace[((Vector)localObject1).size()];
      ((Vector)localObject1).copyInto((Object[])localObject2);
      return localObject2;
    }
    Object localObject1 = paramResponse.readAtom();
    if (localObject1 == null)
      throw new ProtocolException("Expected NIL, got null");
    if (!((String)localObject1).equalsIgnoreCase("NIL"))
      throw new ProtocolException("Expected NIL, got " + (String)localObject1);
    return null;
  }
}
