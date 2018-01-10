package com.sun.mail.iap;

import com.sun.mail.util.ASCIIUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

























public class Response
{
  protected int index;
  protected int size;
  protected byte[] buffer;
  protected int type;
  protected String tag;
  private static final int increment = 100;
  public static final int TAG_MASK = 3;
  public static final int CONTINUATION = 1;
  public static final int TAGGED = 2;
  public static final int UNTAGGED = 3;
  public static final int TYPE_MASK = 28;
  public static final int OK = 4;
  public static final int NO = 8;
  public static final int BAD = 10;
  public static final int BYE = 16;
  public static Response ByeResponse = new Response("* BYE Connection down");
  
  public Response(String paramString) {
    buffer = ASCIIUtility.getBytes(paramString);
    size = buffer.length;
    parse();
  }
  




  public Response(Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    ByteArray localByteArray = paramProtocol.getInputStream().readResponse();
    buffer = localByteArray.getBytes();
    size = (localByteArray.getCount() - 2);
    
    parse();
  }
  


  public Response(Response paramResponse)
  {
    index = index;
    size = size;
    buffer = buffer;
    type = type;
    tag = tag;
  }
  
  private void parse() {
    index = 0;
    
    if (buffer[index] == 43) {
      type |= 0x1;
      index += 1;
      return; }
    if (buffer[index] == 42) {
      type |= 0x3;
      index += 1;
    } else {
      type |= 0x2;
      tag = readAtom();
    }
    
    int i = index;
    String str = readAtom();
    if (str.equalsIgnoreCase("OK")) {
      type |= 0x4;
    } else if (str.equalsIgnoreCase("NO")) {
      type |= 0x8;
    } else if (str.equalsIgnoreCase("BAD")) {
      type |= 0xA;
    } else if (str.equalsIgnoreCase("BYE")) {
      type |= 0x10;
    } else {
      index = i;
    }
  }
  
  public void skipSpaces()
  {
    while ((index < size) && (buffer[index] == 32)) {
      index += 1;
    }
  }
  

  public void skipToken()
  {
    while ((index < size) && (buffer[index] != 32))
      index += 1;
  }
  
  public void skip(int paramInt) {
    index += paramInt;
  }
  
  public byte peekByte() {
    if (index < size) {
      return buffer[index];
    }
    return 0;
  }
  



  public byte readByte()
  {
    if (index < size) {
      return buffer[(index++)];
    }
    return 0;
  }
  




  public String readAtom()
  {
    skipSpaces();
    
    if (index >= size) {
      return null;
    }
    



    int j = index;
    int i; while ((index < size) && ((i = buffer[index]) > 32) && 
      (i != 40) && (i != 41) && (i != 37) && (i != 42) && 
      (i != 34) && (i != 92) && (i != 127)) {
      index += 1;
    }
    return ASCIIUtility.toString(buffer, j, index);
  }
  
  public String[] readStringList() {
    skipSpaces();
    
    if (buffer[index] != 40)
      return null;
    index += 1;
    
    Vector localVector = new Vector();
    do {
      localVector.addElement(readString());
    } while (buffer[(index++)] != 41);
    
    int i = localVector.size();
    if (i > 0) {
      String[] arrayOfString = new String[i];
      localVector.copyInto(arrayOfString);
      return arrayOfString;
    }
    return null;
  }
  







  public int readNumber()
  {
    skipSpaces();
    
    int i = index;
    while ((index < size) && (Character.isDigit((char)buffer[index]))) {
      index += 1;
    }
    if (index > i) {
      try {
        return ASCIIUtility.parseInt(buffer, i, index);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return -1;
  }
  







  public long readLong()
  {
    skipSpaces();
    
    int i = index;
    while ((index < size) && (Character.isDigit((char)buffer[index]))) {
      index += 1;
    }
    if (index > i) {
      try {
        return ASCIIUtility.parseLong(buffer, i, index);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return -1L;
  }
  







  public String readString()
  {
    return (String)parseString(false, true);
  }
  







  public ByteArrayInputStream readBytes()
  {
    ByteArray localByteArray = readByteArray();
    if (localByteArray != null) {
      return localByteArray.toByteArrayInputStream();
    }
    return null;
  }
  











  public ByteArray readByteArray()
  {
    if (isContinuation()) {
      skipSpaces();
      return new ByteArray(buffer, index, size - index);
    }
    return (ByteArray)parseString(false, false);
  }
  










  public String readAtomString()
  {
    return (String)parseString(true, true);
  }
  







  private Object parseString(boolean paramBoolean1, boolean paramBoolean2)
  {
    skipSpaces();
    
    int i = buffer[index];
    int j; int k; if (i == 34) {
      index += 1;
      j = index;
      k = index;
      
      while ((i = buffer[index]) != 34) {
        if (i == 92)
          index += 1;
        if (index != k)
        {

          buffer[k] = buffer[index];
        }
        k++;
        index += 1;
      }
      index += 1;
      
      if (paramBoolean2) {
        return ASCIIUtility.toString(buffer, j, k);
      }
      return new ByteArray(buffer, j, k - j); }
    if (i == 123) {
      j = ++index;
      
      while (buffer[index] != 125) {
        index += 1;
      }
      k = 0;
      try {
        k = ASCIIUtility.parseInt(buffer, j, index);
      }
      catch (NumberFormatException localNumberFormatException) {
        return null;
      }
      
      j = index + 3;
      index = (j + k);
      
      if (paramBoolean2) {
        return ASCIIUtility.toString(buffer, j, j + k);
      }
      return new ByteArray(buffer, j, k); }
    if (paramBoolean1) {
      j = index;
      
      String str = readAtom();
      if (paramBoolean2) {
        return str;
      }
      return new ByteArray(buffer, j, index); }
    if ((i == 78) || (i == 110)) {
      index += 3;
      return null;
    }
    return null;
  }
  
  public int getType() {
    return type;
  }
  
  public boolean isContinuation() {
    return (type & 0x3) == 1;
  }
  
  public boolean isTagged() {
    return (type & 0x3) == 2;
  }
  
  public boolean isUnTagged() {
    return (type & 0x3) == 3;
  }
  
  public boolean isOK() {
    return (type & 0x1C) == 4;
  }
  
  public boolean isNO() {
    return (type & 0x1C) == 8;
  }
  
  public boolean isBAD() {
    return (type & 0x1C) == 10;
  }
  
  public boolean isBYE() {
    return (type & 0x1C) == 16;
  }
  



  public String getTag()
  {
    return tag;
  }
  



  public String getRest()
  {
    skipSpaces();
    return ASCIIUtility.toString(buffer, index, size);
  }
  
  public String toString() {
    return ASCIIUtility.toString(buffer, 0, size);
  }
}
