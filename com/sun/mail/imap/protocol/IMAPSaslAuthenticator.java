package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;





















public class IMAPSaslAuthenticator
  implements SaslAuthenticator
{
  private IMAPProtocol pr;
  private String name;
  private Properties props;
  private boolean debug;
  private PrintStream out;
  private String host;
  
  public IMAPSaslAuthenticator(IMAPProtocol paramIMAPProtocol, String paramString1, Properties paramProperties, boolean paramBoolean, PrintStream paramPrintStream, String paramString2)
  {
    pr = paramIMAPProtocol;
    name = paramString1;
    props = paramProperties;
    debug = paramBoolean;
    out = paramPrintStream;
    host = paramString2;
  }
  
  public boolean authenticate(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, String paramString4)
    throws ProtocolException
  {
    Vector localVector = new Vector();
    String str1 = null;
    Response localResponse = null;
    boolean bool = false;
    if (debug) {
      out.print("IMAP SASL DEBUG: Mechanisms:");
      for (int i = 0; i < paramArrayOfString.length; i++)
        out.print(" " + paramArrayOfString[i]);
      out.println();
    }
    

    final String str2 = paramString1;
    final String str3 = paramString3;
    final String str4 = paramString4;
    CallbackHandler local1 = new CallbackHandler() { private final String val$u0;
      
      public void handle(Callback[] paramAnonymousArrayOfCallback) { if (debug) {
          out.println("IMAP SASL DEBUG: callback length: " + paramAnonymousArrayOfCallback.length);
        }
        for (int i = 0; i < paramAnonymousArrayOfCallback.length; i++) {
          if (debug)
            out.println("IMAP SASL DEBUG: callback " + i + ": " + paramAnonymousArrayOfCallback[i]);
          Object localObject;
          if ((paramAnonymousArrayOfCallback[i] instanceof NameCallback)) {
            localObject = (NameCallback)paramAnonymousArrayOfCallback[i];
            ((NameCallback)localObject).setName(str3);
          } else if ((paramAnonymousArrayOfCallback[i] instanceof PasswordCallback)) {
            localObject = (PasswordCallback)paramAnonymousArrayOfCallback[i];
            ((PasswordCallback)localObject).setPassword(str4.toCharArray());
          } else if ((paramAnonymousArrayOfCallback[i] instanceof RealmCallback)) {
            localObject = (RealmCallback)paramAnonymousArrayOfCallback[i];
            ((RealmCallback)localObject).setText(str2 != null ? str2 : ((RealmCallback)localObject).getDefaultText());
          }
          else if ((paramAnonymousArrayOfCallback[i] instanceof RealmChoiceCallback)) {
            localObject = (RealmChoiceCallback)paramAnonymousArrayOfCallback[i];
            
            if (str2 == null) {
              ((RealmChoiceCallback)localObject).setSelectedIndex(((RealmChoiceCallback)localObject).getDefaultChoice());
            }
            else {
              String[] arrayOfString = ((RealmChoiceCallback)localObject).getChoices();
              for (int j = 0; j < arrayOfString.length; j++) {
                if (arrayOfString[j].equals(str2)) {
                  ((RealmChoiceCallback)localObject).setSelectedIndex(j);
                  break;
                }
              }
            }
          }
        }
      }
    };
    SaslClient localSaslClient;
    try {
      localSaslClient = Sasl.createSaslClient(paramArrayOfString, paramString2, name, host, props, local1);
    }
    catch (SaslException localSaslException) {
      if (debug) {
        out.println("IMAP SASL DEBUG: Failed to create SASL client: " + localSaslException);
      }
      return false;
    }
    if (localSaslClient == null) {
      if (debug)
        out.println("IMAP SASL DEBUG: No SASL support");
      return false;
    }
    if (debug) {
      out.println("IMAP SASL DEBUG: SASL client " + localSaslClient.getMechanismName());
    }
    try
    {
      str1 = pr.writeCommand("AUTHENTICATE " + localSaslClient.getMechanismName(), null);
    }
    catch (Exception localException1) {
      if (debug)
        out.println("IMAP SASL DEBUG: AUTHENTICATE Exception: " + localException1);
      return false;
    }
    
    OutputStream localOutputStream = pr.getIMAPOutputStream();
    













    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte1 = { 13, 10 };
    
    while (!bool) {
      try {
        localResponse = pr.readResponse();
        if (localResponse.isContinuation()) {
          byte[] arrayOfByte2 = localResponse.readByteArray().getNewBytes();
          if (arrayOfByte2.length > 0)
            arrayOfByte2 = BASE64DecoderStream.decode(arrayOfByte2);
          if (debug) {
            out.println("IMAP SASL DEBUG: challenge: " + ASCIIUtility.toString(arrayOfByte2, 0, arrayOfByte2.length) + " :");
          }
          arrayOfByte2 = localSaslClient.evaluateChallenge(arrayOfByte2);
          
          bool = localSaslClient.isComplete();
          if (arrayOfByte2 == null) {
            if (debug)
              out.println("IMAP SASL DEBUG: no response");
            bool = true;
            localOutputStream.write(arrayOfByte1);
            localOutputStream.flush();
            localByteArrayOutputStream.reset();
          } else {
            if (debug) {
              out.println("IMAP SASL DEBUG: response: " + ASCIIUtility.toString(arrayOfByte2, 0, arrayOfByte2.length) + " :");
            }
            arrayOfByte2 = BASE64EncoderStream.encode(arrayOfByte2);
            localByteArrayOutputStream.write(arrayOfByte2);
            
            localByteArrayOutputStream.write(arrayOfByte1);
            localOutputStream.write(localByteArrayOutputStream.toByteArray());
            localOutputStream.flush();
            localByteArrayOutputStream.reset();
          }
        } else if ((localResponse.isTagged()) && (localResponse.getTag().equals(str1)))
        {
          bool = true;
        } else if (localResponse.isBYE()) {
          bool = true;
        } else {
          localVector.addElement(localResponse);
        }
      } catch (Exception localException2) { if (debug) {
          localException2.printStackTrace();
        }
        localResponse = Response.byeResponse(localException2);
        bool = true;
      }
    }
    

    if (localSaslClient.isComplete()) {
      localObject = (String)localSaslClient.getNegotiatedProperty("javax.security.sasl.qop");
      if ((localObject != null) && ((((String)localObject).equalsIgnoreCase("auth-int")) || (((String)localObject).equalsIgnoreCase("auth-conf"))))
      {

        if (debug) {
          out.println("IMAP SASL DEBUG: Mechanism requires integrity or confidentiality");
        }
        return false;
      }
    }
    






    Object localObject = new Response[localVector.size()];
    localVector.copyInto((Object[])localObject);
    pr.notifyResponseHandlers((Response[])localObject);
    

    pr.handleResult(localResponse);
    return true;
  }
  
  private final String val$p0;
  private final String val$r0;
}
