package com.sun.mail.iap;

import com.sun.mail.util.ASCIIUtility;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

















public class Argument
{
  protected Vector items = new Vector(1);
  


  public Argument() {}
  

  public void append(Argument paramArgument)
  {
    items.ensureCapacity(items.size() + items.size());
    for (int i = 0; i < items.size(); i++) {
      items.addElement(items.elementAt(i));
    }
  }
  







  public void writeString(String paramString)
  {
    items.addElement(new AString(ASCIIUtility.getBytes(paramString)));
  }
  



  public void writeString(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    if (paramString2 == null) {
      writeString(paramString1);
    } else {
      items.addElement(new AString(paramString1.getBytes(paramString2)));
    }
  }
  


  public void writeBytes(byte[] paramArrayOfByte)
  {
    items.addElement(paramArrayOfByte);
  }
  



  public void writeBytes(ByteArrayOutputStream paramByteArrayOutputStream)
  {
    items.addElement(paramByteArrayOutputStream);
  }
  



  public void writeBytes(Literal paramLiteral)
  {
    items.addElement(paramLiteral);
  }
  





  public void writeAtom(String paramString)
  {
    items.addElement(new Atom(paramString));
  }
  



  public void writeNumber(int paramInt)
  {
    items.addElement(new Integer(paramInt));
  }
  



  public void writeNumber(long paramLong)
  {
    items.addElement(new Long(paramLong));
  }
  



  public void writeArgument(Argument paramArgument)
  {
    items.addElement(paramArgument);
  }
  


  public void write(Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    int i = items != null ? items.size() : 0;
    DataOutputStream localDataOutputStream = (DataOutputStream)paramProtocol.getOutputStream();
    
    for (int j = 0; j < i; j++) {
      if (j > 0) {
        localDataOutputStream.write(32);
      }
      Object localObject = items.elementAt(j);
      if ((localObject instanceof Atom)) {
        localDataOutputStream.writeBytes(string);
      } else if ((localObject instanceof Number)) {
        localDataOutputStream.writeBytes(((Number)localObject).toString());
      } else if ((localObject instanceof AString)) {
        astring(bytes, paramProtocol);
      } else if ((localObject instanceof byte[])) {
        literal((byte[])localObject, paramProtocol);
      } else if ((localObject instanceof ByteArrayOutputStream)) {
        literal((ByteArrayOutputStream)localObject, paramProtocol);
      } else if ((localObject instanceof Literal)) {
        literal((Literal)localObject, paramProtocol);
      } else if ((localObject instanceof Argument)) {
        localDataOutputStream.write(40);
        ((Argument)localObject).write(paramProtocol);
        localDataOutputStream.write(41);
      }
    }
  }
  


  private void astring(byte[] paramArrayOfByte, Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    DataOutputStream localDataOutputStream = (DataOutputStream)paramProtocol.getOutputStream();
    int i = paramArrayOfByte.length;
    

    if (i > 1024) {
      literal(paramArrayOfByte, paramProtocol);
      return;
    }
    

    int j = i == 0 ? 1 : 0;
    int k = 0;
    
    int m;
    for (int n = 0; n < i; n++) {
      m = paramArrayOfByte[n];
      if ((m == 0) || (m == 13) || (m == 10) || ((m & 0xFF) > 127))
      {
        literal(paramArrayOfByte, paramProtocol);
        return;
      }
      if ((m == 42) || (m == 37) || (m == 40) || (m == 41) || (m == 123) || 
        (m == 34) || (m == 92) || ((m & 0xFF) <= 32)) {
        j = 1;
        if ((m == 34) || (m == 92)) {
          k = 1;
        }
      }
    }
    if (j != 0) {
      localDataOutputStream.write(34);
    }
    if (k != 0)
    {
      for (int i1 = 0; i1 < i; i1++) {
        m = paramArrayOfByte[i1];
        if ((m == 34) || (m == 92))
          localDataOutputStream.write(92);
        localDataOutputStream.write(m);
      }
    } else {
      localDataOutputStream.write(paramArrayOfByte);
    }
    
    if (j != 0) {
      localDataOutputStream.write(34);
    }
  }
  

  private void literal(byte[] paramArrayOfByte, Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    DataOutputStream localDataOutputStream = (DataOutputStream)paramProtocol.getOutputStream();
    boolean bool = paramProtocol.supportsNonSyncLiterals();
    
    localDataOutputStream.write(123);
    localDataOutputStream.writeBytes(Integer.toString(paramArrayOfByte.length));
    if (bool) {
      localDataOutputStream.writeBytes("+}\r\n");
    } else
      localDataOutputStream.writeBytes("}\r\n");
    localDataOutputStream.flush();
    


    if (!bool) {
      for (;;) {
        Response localResponse = paramProtocol.readResponse();
        if (localResponse.isContinuation())
          break;
      }
    }
    localDataOutputStream.write(paramArrayOfByte);
  }
  


  private void literal(ByteArrayOutputStream paramByteArrayOutputStream, Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    DataOutputStream localDataOutputStream = (DataOutputStream)paramProtocol.getOutputStream();
    boolean bool = paramProtocol.supportsNonSyncLiterals();
    
    localDataOutputStream.write(123);
    localDataOutputStream.writeBytes(Integer.toString(paramByteArrayOutputStream.size()));
    if (bool) {
      localDataOutputStream.writeBytes("+}\r\n");
    } else
      localDataOutputStream.writeBytes("}\r\n");
    localDataOutputStream.flush();
    


    if (!bool) {
      for (;;) {
        Response localResponse = paramProtocol.readResponse();
        if (localResponse.isContinuation())
          break;
      }
    }
    paramByteArrayOutputStream.writeTo(localDataOutputStream);
  }
  


  private void literal(Literal paramLiteral, Protocol paramProtocol)
    throws IOException, ProtocolException
  {
    DataOutputStream localDataOutputStream = (DataOutputStream)paramProtocol.getOutputStream();
    boolean bool = paramProtocol.supportsNonSyncLiterals();
    
    localDataOutputStream.write(123);
    localDataOutputStream.writeBytes(Integer.toString(paramLiteral.size()));
    if (bool) {
      localDataOutputStream.writeBytes("+}\r\n");
    } else
      localDataOutputStream.writeBytes("}\r\n");
    localDataOutputStream.flush();
    


    if (!bool) {
      for (;;) {
        Response localResponse = paramProtocol.readResponse();
        if (localResponse.isContinuation())
          break;
      }
    }
    paramLiteral.writeTo(localDataOutputStream);
  }
}
