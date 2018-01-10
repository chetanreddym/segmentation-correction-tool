package com.sun.mail.smtp;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.StringTokenizer;
























public class DigestMD5
{
  private PrintStream debugout;
  private MessageDigest md5;
  private String uri;
  private String clientResponse;
  
  public DigestMD5(PrintStream paramPrintStream)
  {
    debugout = paramPrintStream;
    if (paramPrintStream != null) {
      paramPrintStream.println("DEBUG DIGEST-MD5: Loaded");
    }
  }
  




  public byte[] authClient(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    BASE64EncoderStream localBASE64EncoderStream = new BASE64EncoderStream(localByteArrayOutputStream, Integer.MAX_VALUE);
    SecureRandom localSecureRandom;
    try
    {
      localSecureRandom = new SecureRandom();
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
      if (debugout != null)
        debugout.println("DEBUG DIGEST-MD5: " + localNoSuchAlgorithmException);
      throw new IOException(localNoSuchAlgorithmException.toString());
    }
    StringBuffer localStringBuffer = new StringBuffer();
    
    uri = ("smtp/" + paramString1);
    String str1 = "00000001";
    String str2 = "auth";
    byte[] arrayOfByte = new byte[32];
    

    if (debugout != null) {
      debugout.println("DEBUG DIGEST-MD5: Begin authentication ...");
    }
    
    Hashtable localHashtable = tokenize(paramString5);
    
    if (paramString4 == null) {
      str3 = (String)localHashtable.get("realm");
      paramString4 = str3 != null ? new StringTokenizer(str3, ",").nextToken() : paramString1;
    }
    


    String str3 = (String)localHashtable.get("nonce");
    
    localSecureRandom.nextBytes(arrayOfByte);
    localBASE64EncoderStream.write(arrayOfByte);
    localBASE64EncoderStream.flush();
    

    String str4 = localByteArrayOutputStream.toString();
    localByteArrayOutputStream.reset();
    

    md5.update(md5.digest(ASCIIUtility.getBytes(paramString2 + ":" + paramString4 + ":" + paramString3)));
    
    md5.update(ASCIIUtility.getBytes(":" + str3 + ":" + str4));
    clientResponse = (toHex(md5.digest()) + ":" + str3 + ":" + str1 + ":" + str4 + ":" + str2 + ":");
    


    md5.update(ASCIIUtility.getBytes("AUTHENTICATE:" + uri));
    md5.update(ASCIIUtility.getBytes(clientResponse + toHex(md5.digest())));
    

    localStringBuffer.append("username=\"" + paramString2 + "\"");
    localStringBuffer.append(",realm=\"" + paramString4 + "\"");
    localStringBuffer.append(",qop=" + str2);
    localStringBuffer.append(",nc=" + str1);
    localStringBuffer.append(",nonce=\"" + str3 + "\"");
    localStringBuffer.append(",cnonce=\"" + str4 + "\"");
    localStringBuffer.append(",digest-uri=\"" + uri + "\"");
    localStringBuffer.append(",response=" + toHex(md5.digest()));
    
    if (debugout != null) {
      debugout.println("DEBUG DIGEST-MD5: Response => " + localStringBuffer.toString());
    }
    localBASE64EncoderStream.write(ASCIIUtility.getBytes(localStringBuffer.toString()));
    localBASE64EncoderStream.flush();
    return localByteArrayOutputStream.toByteArray();
  }
  




  public boolean authServer(String paramString)
    throws IOException
  {
    Hashtable localHashtable = tokenize(paramString);
    
    md5.update(ASCIIUtility.getBytes(":" + uri));
    md5.update(ASCIIUtility.getBytes(clientResponse + toHex(md5.digest())));
    String str = toHex(md5.digest());
    if (!str.equals((String)localHashtable.get("rspauth"))) {
      if (debugout != null) {
        debugout.println("DEBUG DIGEST-MD5: Expected => rspauth=" + str);
      }
      return false;
    }
    return true;
  }
  



  private Hashtable tokenize(String paramString)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable();
    byte[] arrayOfByte = paramString.getBytes();
    String str = null;
    
    StreamTokenizer localStreamTokenizer = new StreamTokenizer(new InputStreamReader(new BASE64DecoderStream(new ByteArrayInputStream(arrayOfByte, 4, arrayOfByte.length - 4))));
    





    localStreamTokenizer.ordinaryChars(48, 57);
    localStreamTokenizer.wordChars(48, 57);
    int i; while ((i = localStreamTokenizer.nextToken()) != -1) {
      switch (i) {
      case -3: 
        if (str == null)
          str = sval;
        break;
      

      case 34: 
        if (debugout != null) {
          debugout.println("DEBUG DIGEST-MD5: Received => " + str + "='" + sval + "'");
        }
        if (localHashtable.containsKey(str)) {
          localHashtable.put(str, localHashtable.get(str) + "," + sval);
        } else {
          localHashtable.put(str, sval);
        }
        str = null;
      }
      
    }
    return localHashtable;
  }
  
  private static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  





  private static String toHex(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar = new char[paramArrayOfByte.length * 2];
    
    int i = 0; for (int j = 0; i < paramArrayOfByte.length; i++) {
      int k = paramArrayOfByte[i] & 0xFF;
      arrayOfChar[(j++)] = digits[(k >> 4)];
      arrayOfChar[(j++)] = digits[(k & 0xF)];
    }
    return new String(arrayOfChar);
  }
}
