package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Response;
import java.util.Vector;
import javax.mail.internet.ParameterList;
















public class BODYSTRUCTURE
  implements Item
{
  public static char[] name = { 'B', 'O', 'D', 'Y', 'S', 'T', 'R', 'U', 'C', 'T', 'U', 'R', 'E' };
  
  public int msgno;
  public String type;
  public String subtype;
  public String encoding;
  public int lines = -1;
  public int size = -1;
  
  public String disposition;
  
  public String id;
  public String description;
  public String md5;
  public String attachment;
  public ParameterList cParams;
  public ParameterList dParams;
  public String[] language;
  public BODYSTRUCTURE[] bodies;
  public ENVELOPE envelope;
  private static int SINGLE = 1;
  private static int MULTI = 2;
  private static int NESTED = 3;
  private int processedType;
  
  public BODYSTRUCTURE(FetchResponse paramFetchResponse) throws ParsingException {
    msgno = paramFetchResponse.getNumber();
    
    paramFetchResponse.skipSpaces();
    
    if (paramFetchResponse.readByte() != 40)
      throw new ParsingException(
        "BODYSTRUCTURE parse error: missing ``('' at start");
    Object localObject1;
    Object localObject2; if (paramFetchResponse.peekByte() == 40) {
      type = "multipart";
      processedType = MULTI;
      localObject1 = new Vector(1);
      do
      {
        ((Vector)localObject1).addElement(new BODYSTRUCTURE(paramFetchResponse));
      } while (paramFetchResponse.peekByte() == 40);
      

      bodies = new BODYSTRUCTURE[((Vector)localObject1).size()];
      ((Vector)localObject1).copyInto(bodies);
      
      subtype = paramFetchResponse.readString();
      
      if (paramFetchResponse.readByte() == 41) {
        return;
      }
      


      cParams = parseParameters(paramFetchResponse);
      if (paramFetchResponse.readByte() == 41) {
        return;
      }
      
      int j = paramFetchResponse.readByte();
      if (j == 40) {
        disposition = paramFetchResponse.readString();
        dParams = parseParameters(paramFetchResponse);
        if (paramFetchResponse.readByte() != 41) {
          throw new ParsingException(
            "BODYSTRUCTURE parse error: missing ``)'' at end of disposition in multipart");
        }
      } else if ((j == 78) || (j == 110)) {
        paramFetchResponse.skip(2);
      }
      
      if (paramFetchResponse.readByte() != 32) {
        throw new ParsingException(
          "BODYSTRUCTURE parse error: missing space after disposition");
      }
      

      if (paramFetchResponse.peekByte() == 40) {
        language = paramFetchResponse.readStringList();
      } else {
        localObject2 = paramFetchResponse.readString();
        if (localObject2 != null) {
          String[] arrayOfString = { localObject2 };
          language = arrayOfString;
        }
      }
      

      while (paramFetchResponse.readByte() == 32) {
        parseBodyExtension(paramFetchResponse);
      }
    } else {
      type = paramFetchResponse.readString();
      processedType = SINGLE;
      subtype = paramFetchResponse.readString();
      

      if (type == null) {
        type = "application";
        subtype = "octet-stream";
      }
      cParams = parseParameters(paramFetchResponse);
      id = paramFetchResponse.readString();
      description = paramFetchResponse.readString();
      encoding = paramFetchResponse.readString();
      size = paramFetchResponse.readNumber();
      if (size < 0) {
        throw new ParsingException(
          "BODYSTRUCTURE parse error: bad ``size'' element");
      }
      
      if (type.equalsIgnoreCase("text")) {
        lines = paramFetchResponse.readNumber();
        if (lines < 0)
          throw new ParsingException(
            "BODYSTRUCTURE parse error: bad ``lines'' element");
      } else if ((type.equalsIgnoreCase("message")) && 
        (subtype.equalsIgnoreCase("rfc822")))
      {
        processedType = NESTED;
        envelope = new ENVELOPE(paramFetchResponse);
        localObject1 = new BODYSTRUCTURE[] { new BODYSTRUCTURE(paramFetchResponse) };
        bodies = ((BODYSTRUCTURE[])localObject1);
        lines = paramFetchResponse.readNumber();
        if (lines < 0) {
          throw new ParsingException(
            "BODYSTRUCTURE parse error: bad ``lines'' element");
        }
      }
      if (paramFetchResponse.readByte() == 41) {
        return;
      }
      


      md5 = paramFetchResponse.readString();
      if (paramFetchResponse.readByte() == 41) {
        return;
      }
      
      int i = paramFetchResponse.readByte();
      if (i == 40) {
        disposition = paramFetchResponse.readString();
        dParams = parseParameters(paramFetchResponse);
        if (paramFetchResponse.readByte() != 41) {
          throw new ParsingException(
            "BODYSTRUCTURE parse error: missing ``)'' at end of disposition");
        }
      } else if ((i == 78) || (i == 110)) {
        paramFetchResponse.skip(2);
      }
      if (paramFetchResponse.readByte() == 41) {
        return;
      }
      
      if (paramFetchResponse.peekByte() == 40) {
        language = paramFetchResponse.readStringList();
      } else {
        String str = paramFetchResponse.readString();
        if (str != null) {
          localObject2 = new String[] { str };
          language = ((String[])localObject2);
        }
      }
      

      while (paramFetchResponse.readByte() == 32)
        parseBodyExtension(paramFetchResponse);
    }
  }
  
  public boolean isMulti() {
    return processedType == MULTI;
  }
  
  public boolean isSingle() {
    return processedType == SINGLE;
  }
  
  public boolean isNested() {
    return processedType == NESTED;
  }
  
  private ParameterList parseParameters(Response paramResponse) throws ParsingException
  {
    paramResponse.skipSpaces();
    
    ParameterList localParameterList = null;
    int i = paramResponse.readByte();
    if (i == 40) {
      localParameterList = new ParameterList();
      do {
        String str1 = paramResponse.readString();
        String str2 = paramResponse.readString();
        localParameterList.set(str1, str2);
      } while (paramResponse.readByte() != 41);
    } else if ((i == 78) || (i == 110)) {
      paramResponse.skip(2);
    } else {
      throw new ParsingException("Parameter list parse error");
    }
    return localParameterList;
  }
  
  private void parseBodyExtension(Response paramResponse) throws ParsingException {
    paramResponse.skipSpaces();
    
    int i = paramResponse.peekByte();
    if (i == 40) {
      paramResponse.skip(1);
      do {
        parseBodyExtension(paramResponse);
      } while (paramResponse.readByte() != 41);
      return;
    }
    


    if (Character.isDigit((char)i)) {
      paramResponse.readNumber();
    } else {
      paramResponse.readString();
    }
  }
}
