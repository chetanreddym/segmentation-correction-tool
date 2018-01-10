package com.sun.mail.imap.protocol;

import com.sun.mail.iap.Argument;
import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.Literal;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.Protocol;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.ACL;
import com.sun.mail.imap.Quota;
import com.sun.mail.imap.Quota.Resource;
import com.sun.mail.imap.Rights;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeUtility;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;









public class IMAPProtocol
  extends Protocol
{
  private boolean rev1 = false;
  private boolean authenticated = false;
  



  private Hashtable capabilities;
  


  private String[] searchCharsets;
  



  public IMAPProtocol(String paramString1, String paramString2, int paramInt, boolean paramBoolean, Properties paramProperties)
    throws IOException, ProtocolException
  {
    super(paramString2, paramInt, paramBoolean, paramProperties, "mail." + paramString1);
    

    Response[] arrayOfResponse = command("CAPABILITY", null);
    
    if (!arrayOfResponse[(arrayOfResponse.length - 1)].isOK()) {
      throw new ProtocolException(arrayOfResponse[(arrayOfResponse.length - 1)].toString());
    }
    capabilities = new Hashtable(5);
    int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
      if ((arrayOfResponse[i] instanceof IMAPResponse))
      {

        IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
        




        if (localIMAPResponse.keyEquals("CAPABILITY")) {
          Object localObject;
          while ((localObject = localIMAPResponse.readAtom()) != null) {
            if (((String)localObject).length() == 0)
            {










              localIMAPResponse.skipToken();
            } else
              capabilities.put(((String)localObject).toLowerCase(), localObject);
          }
        }
      }
    }
    if (hasCapability("IMAP4rev1")) {
      rev1 = true;
    }
    searchCharsets = new String[2];
    searchCharsets[0] = "UTF-8";
    searchCharsets[1] = MimeUtility.mimeCharset(
      MimeUtility.getDefaultJavaCharset());
  }
  


  protected void processGreeting(Response paramResponse)
    throws ProtocolException
  {
    super.processGreeting(paramResponse);
    if (paramResponse.isOK()) {
      return;
    }
    IMAPResponse localIMAPResponse = (IMAPResponse)paramResponse;
    if (localIMAPResponse.keyEquals("PREAUTH")) {
      authenticated = true;
    } else {
      throw new ConnectionException(paramResponse);
    }
  }
  


  public boolean isAuthenticated()
  {
    return authenticated;
  }
  


  public boolean isREV1()
  {
    return rev1;
  }
  


  protected boolean supportsNonSyncLiterals()
  {
    return hasCapability("LITERAL+");
  }
  

  public Response readResponse()
    throws IOException, ProtocolException
  {
    return IMAPResponse.readResponse(this);
  }
  




  public boolean hasCapability(String paramString)
  {
    return capabilities.containsKey(paramString.toLowerCase());
  }
  





  public void disconnect()
  {
    super.disconnect();
  }
  



  public void noop()
    throws ProtocolException
  {
    simpleCommand("NOOP", null);
  }
  



  public void logout()
    throws ProtocolException
  {
    Response[] arrayOfResponse = command("LOGOUT", null);
    


    notifyResponseHandlers(arrayOfResponse);
    disconnect();
  }
  



  public void login(String paramString1, String paramString2)
    throws ProtocolException
  {
    Argument localArgument = new Argument();
    localArgument.writeString(paramString1);
    localArgument.writeString(paramString2);
    
    simpleCommand("LOGIN", localArgument);
  }
  



  public void authlogin(String paramString1, String paramString2)
    throws ProtocolException
  {
    Vector localVector = new Vector();
    String str = null;
    Response localResponse = null;
    int i = 0;
    try
    {
      str = writeCommand("AUTHENTICATE LOGIN", null);
    }
    catch (Exception localException1) {
      localResponse = Response.ByeResponse;
      i = 1;
    }
    
    OutputStream localOutputStream = getOutputStream();
    
















    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    BASE64EncoderStream localBASE64EncoderStream = new BASE64EncoderStream(localByteArrayOutputStream, Integer.MAX_VALUE);
    byte[] arrayOfByte = { 13, 10 };
    int j = 1;
    
    while (i == 0) {
      try {
        localResponse = readResponse();
        if (localResponse.isContinuation())
        {

          if (j != 0) {
            localObject = paramString1;
            j = 0;
          } else {
            localObject = paramString2;
          }
          
          localBASE64EncoderStream.write(ASCIIUtility.getBytes((String)localObject));
          localBASE64EncoderStream.flush();
          
          localByteArrayOutputStream.write(arrayOfByte);
          localOutputStream.write(localByteArrayOutputStream.toByteArray());
          localOutputStream.flush();
          localByteArrayOutputStream.reset();
        } else if ((localResponse.isTagged()) && (localResponse.getTag().equals(str)))
        {
          i = 1;
        } else if (localResponse.isBYE()) {
          i = 1;
        } else {
          localVector.addElement(localResponse);
        }
      } catch (Exception localException2) {
        localResponse = Response.ByeResponse;
        i = 1;
      }
    }
    






    Object localObject = new Response[localVector.size()];
    localVector.copyInto((Object[])localObject);
    notifyResponseHandlers((Response[])localObject);
    

    handleResult(localResponse);
  }
  




  public MailboxInfo select(String paramString)
    throws ProtocolException
  {
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("SELECT", localArgument);
    


    MailboxInfo localMailboxInfo = new MailboxInfo(arrayOfResponse);
    

    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    
    if (localResponse.isOK()) {
      if (localResponse.toString().indexOf("READ-ONLY") != -1) {
        mode = 1;
      } else {
        mode = 2;
      }
    }
    handleResult(localResponse);
    return localMailboxInfo;
  }
  




  public MailboxInfo examine(String paramString)
    throws ProtocolException
  {
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("EXAMINE", localArgument);
    


    MailboxInfo localMailboxInfo = new MailboxInfo(arrayOfResponse);
    mode = 1;
    

    notifyResponseHandlers(arrayOfResponse);
    
    handleResult(arrayOfResponse[(arrayOfResponse.length - 1)]);
    return localMailboxInfo;
  }
  




  public Status status(String paramString, String[] paramArrayOfString)
    throws ProtocolException
  {
    if ((!isREV1()) && (!hasCapability("IMAP4SUNVERSION")))
    {

      throw new BadCommandException("STATUS not supported");
    }
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument1 = new Argument();
    localArgument1.writeString(paramString);
    
    Argument localArgument2 = new Argument();
    if (paramArrayOfString == null) {
      paramArrayOfString = Status.standardItems;
    }
    int i = 0; for (int j = paramArrayOfString.length; i < j; i++)
      localArgument2.writeAtom(paramArrayOfString[i]);
    localArgument1.writeArgument(localArgument2);
    
    Response[] arrayOfResponse = command("STATUS", localArgument1);
    
    Status localStatus = null;
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    if (localResponse.isOK()) {
      int k = 0; for (int m = arrayOfResponse.length; k < m; k++) {
        if ((arrayOfResponse[k] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[k];
          if (localIMAPResponse.keyEquals("STATUS")) {
            if (localStatus == null) {
              localStatus = new Status(localIMAPResponse);
            } else
              Status.add(localStatus, new Status(localIMAPResponse));
            arrayOfResponse[k] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    return localStatus;
  }
  




  public void create(String paramString)
    throws ProtocolException
  {
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    simpleCommand("CREATE", localArgument);
  }
  




  public void delete(String paramString)
    throws ProtocolException
  {
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    simpleCommand("DELETE", localArgument);
  }
  




  public void rename(String paramString1, String paramString2)
    throws ProtocolException
  {
    paramString1 = BASE64MailboxEncoder.encode(paramString1);
    paramString2 = BASE64MailboxEncoder.encode(paramString2);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString1);
    localArgument.writeString(paramString2);
    
    simpleCommand("RENAME", localArgument);
  }
  



  public void subscribe(String paramString)
    throws ProtocolException
  {
    Argument localArgument = new Argument();
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    localArgument.writeString(paramString);
    
    simpleCommand("SUBSCRIBE", localArgument);
  }
  



  public void unsubscribe(String paramString)
    throws ProtocolException
  {
    Argument localArgument = new Argument();
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    localArgument.writeString(paramString);
    
    simpleCommand("UNSUBSCRIBE", localArgument);
  }
  




  public ListInfo[] list(String paramString1, String paramString2)
    throws ProtocolException
  {
    return doList("LIST", paramString1, paramString2);
  }
  




  public ListInfo[] lsub(String paramString1, String paramString2)
    throws ProtocolException
  {
    return doList("LSUB", paramString1, paramString2);
  }
  
  private ListInfo[] doList(String paramString1, String paramString2, String paramString3)
    throws ProtocolException
  {
    paramString2 = BASE64MailboxEncoder.encode(paramString2);
    paramString3 = BASE64MailboxEncoder.encode(paramString3);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString2);
    localArgument.writeString(paramString3);
    
    Response[] arrayOfResponse = command(paramString1, localArgument);
    
    ListInfo[] arrayOfListInfo = null;
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    
    if (localResponse.isOK()) {
      Vector localVector = new Vector(1);
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++)
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals(paramString1)) {
            localVector.addElement(new ListInfo(localIMAPResponse));
            arrayOfResponse[i] = null;
          }
        }
      if (localVector.size() > 0) {
        arrayOfListInfo = new ListInfo[localVector.size()];
        localVector.copyInto(arrayOfListInfo);
      }
    }
    

    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    return arrayOfListInfo;
  }
  





  public void append(String paramString, Flags paramFlags, Date paramDate, Literal paramLiteral)
    throws ProtocolException
  {
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    if (paramFlags != null)
    {
      paramFlags.remove(Flags.Flag.RECENT);
    }
    








    localArgument.writeAtom(createFlagList(paramFlags));
    if (paramDate != null) {
      localArgument.writeString(INTERNALDATE.format(paramDate));
    }
    localArgument.writeBytes(paramLiteral);
    
    simpleCommand("APPEND", localArgument);
  }
  



  public void check()
    throws ProtocolException
  {
    simpleCommand("CHECK", null);
  }
  



  public void close()
    throws ProtocolException
  {
    simpleCommand("CLOSE", null);
  }
  



  public void expunge()
    throws ProtocolException
  {
    simpleCommand("EXPUNGE", null);
  }
  


  public BODYSTRUCTURE fetchBodyStructure(int paramInt)
    throws ProtocolException
  {
    Response[] arrayOfResponse = fetch(paramInt, "BODYSTRUCTURE");
    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    if (localResponse.isOK())
      return (BODYSTRUCTURE)FetchResponse.getItem(arrayOfResponse, paramInt, 
        BODYSTRUCTURE.class);
    if (localResponse.isNO()) {
      return null;
    }
    handleResult(localResponse);
    return null;
  }
  




  public BODY peekBody(int paramInt, String paramString)
    throws ProtocolException
  {
    return fetchBody(paramInt, paramString, true);
  }
  


  public BODY fetchBody(int paramInt, String paramString)
    throws ProtocolException
  {
    return fetchBody(paramInt, paramString, false);
  }
  
  private BODY fetchBody(int paramInt, String paramString, boolean paramBoolean)
    throws ProtocolException
  {
    Response[] arrayOfResponse;
    if (paramBoolean) {
      arrayOfResponse = fetch(paramInt, 
        "BODY.PEEK[" + (paramString == null ? "]" : new StringBuffer(String.valueOf(paramString)).append("]").toString()));
    } else {
      arrayOfResponse = fetch(paramInt, 
        "BODY[" + (paramString == null ? "]" : new StringBuffer(String.valueOf(paramString)).append("]").toString()));
    }
    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    if (localResponse.isOK())
      return (BODY)FetchResponse.getItem(arrayOfResponse, paramInt, BODY.class);
    if (localResponse.isNO()) {
      return null;
    }
    handleResult(localResponse);
    return null;
  }
  



  public BODY fetchBody(int paramInt1, String paramString, int paramInt2, int paramInt3)
    throws ProtocolException
  {
    Response[] arrayOfResponse = fetch(
      paramInt1, 
      "BODY[" + (paramString == null ? "]<" : new StringBuffer(String.valueOf(paramString)).append("]<").toString()) + 
      String.valueOf(paramInt2) + "." + 
      String.valueOf(paramInt3) + ">");
    

    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    if (localResponse.isOK())
      return (BODY)FetchResponse.getItem(arrayOfResponse, paramInt1, BODY.class);
    if (localResponse.isNO()) {
      return null;
    }
    handleResult(localResponse);
    return null;
  }
  





  public RFC822DATA fetchRFC822(int paramInt, String paramString)
    throws ProtocolException
  {
    Response[] arrayOfResponse = fetch(paramInt, 
      "RFC822." + paramString);
    


    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    if (localResponse.isOK())
      return (RFC822DATA)FetchResponse.getItem(arrayOfResponse, paramInt, 
        RFC822DATA.class);
    if (localResponse.isNO()) {
      return null;
    }
    handleResult(localResponse);
    return null;
  }
  


  public Flags fetchFlags(int paramInt)
    throws ProtocolException
  {
    Flags localFlags = null;
    Response[] arrayOfResponse = fetch(paramInt, "FLAGS");
    

    int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
      if ((arrayOfResponse[i] != null) && 
        ((arrayOfResponse[i] instanceof FetchResponse)) && 
        (((FetchResponse)arrayOfResponse[i]).getNumber() == paramInt))
      {

        FetchResponse localFetchResponse = (FetchResponse)arrayOfResponse[i];
        if ((localFlags = (Flags)localFetchResponse.getItem(Flags.class)) != null) {
          arrayOfResponse[i] = null;
          break;
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    return localFlags;
  }
  

  public UID fetchUID(int paramInt)
    throws ProtocolException
  {
    Response[] arrayOfResponse = fetch(paramInt, "UID");
    

    notifyResponseHandlers(arrayOfResponse);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    if (localResponse.isOK())
      return (UID)FetchResponse.getItem(arrayOfResponse, paramInt, UID.class);
    if (localResponse.isNO()) {
      return null;
    }
    handleResult(localResponse);
    return null;
  }
  




  public UID fetchSequenceNumber(long paramLong)
    throws ProtocolException
  {
    UID localUID = null;
    Response[] arrayOfResponse = fetch(String.valueOf(paramLong), "UID", true);
    
    int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
      if ((arrayOfResponse[i] != null) && ((arrayOfResponse[i] instanceof FetchResponse)))
      {

        FetchResponse localFetchResponse = (FetchResponse)arrayOfResponse[i];
        if ((localUID = (UID)localFetchResponse.getItem(UID.class)) != null) {
          if (uid == paramLong) {
            break;
          }
          localUID = null;
        }
      }
    }
    notifyResponseHandlers(arrayOfResponse);
    handleResult(arrayOfResponse[(arrayOfResponse.length - 1)]);
    return localUID;
  }
  




  public UID[] fetchSequenceNumbers(long paramLong1, long paramLong2)
    throws ProtocolException
  {
    Response[] arrayOfResponse = fetch(String.valueOf(paramLong1) + ":" + (
      paramLong2 == -1L ? "*" : 
      String.valueOf(paramLong2)), 
      "UID", true);
    

    Vector localVector = new Vector();
    int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
      if ((arrayOfResponse[i] != null) && ((arrayOfResponse[i] instanceof FetchResponse)))
      {

        localObject = (FetchResponse)arrayOfResponse[i];
        UID localUID; if ((localUID = (UID)((FetchResponse)localObject).getItem(UID.class)) != null)
          localVector.addElement(localUID);
      }
    }
    notifyResponseHandlers(arrayOfResponse);
    handleResult(arrayOfResponse[(arrayOfResponse.length - 1)]);
    
    Object localObject = new UID[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  



  public UID[] fetchSequenceNumbers(long[] paramArrayOfLong)
    throws ProtocolException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      if (i > 0)
        localStringBuffer.append(",");
      localStringBuffer.append(String.valueOf(paramArrayOfLong[i]));
    }
    
    Response[] arrayOfResponse = fetch(localStringBuffer.toString(), "UID", true);
    

    Vector localVector = new Vector();
    int j = 0; for (int k = arrayOfResponse.length; j < k; j++) {
      if ((arrayOfResponse[j] != null) && ((arrayOfResponse[j] instanceof FetchResponse)))
      {

        localObject = (FetchResponse)arrayOfResponse[j];
        UID localUID; if ((localUID = (UID)((FetchResponse)localObject).getItem(UID.class)) != null)
          localVector.addElement(localUID);
      }
    }
    notifyResponseHandlers(arrayOfResponse);
    handleResult(arrayOfResponse[(arrayOfResponse.length - 1)]);
    
    Object localObject = new UID[localVector.size()];
    localVector.copyInto((Object[])localObject);
    return localObject;
  }
  
  public Response[] fetch(MessageSet[] paramArrayOfMessageSet, String paramString) throws ProtocolException
  {
    return fetch(MessageSet.toString(paramArrayOfMessageSet), paramString, false);
  }
  
  public Response[] fetch(int paramInt1, int paramInt2, String paramString) throws ProtocolException
  {
    return fetch(String.valueOf(paramInt1) + ":" + String.valueOf(paramInt2), 
      paramString, false);
  }
  
  public Response[] fetch(int paramInt, String paramString) throws ProtocolException
  {
    return fetch(String.valueOf(paramInt), paramString, false);
  }
  
  private Response[] fetch(String paramString1, String paramString2, boolean paramBoolean) throws ProtocolException
  {
    if (paramBoolean) {
      return command("UID FETCH " + paramString1 + " (" + paramString2 + ")", null);
    }
    return command("FETCH " + paramString1 + " (" + paramString2 + ")", null);
  }
  


  public void copy(MessageSet[] paramArrayOfMessageSet, String paramString)
    throws ProtocolException
  {
    copy(MessageSet.toString(paramArrayOfMessageSet), paramString);
  }
  
  public void copy(int paramInt1, int paramInt2, String paramString) throws ProtocolException
  {
    copy(String.valueOf(paramInt1) + ":" + String.valueOf(paramInt2), 
      paramString);
  }
  
  private void copy(String paramString1, String paramString2)
    throws ProtocolException
  {
    paramString2 = BASE64MailboxEncoder.encode(paramString2);
    
    Argument localArgument = new Argument();
    localArgument.writeAtom(paramString1);
    localArgument.writeString(paramString2);
    
    simpleCommand("COPY", localArgument);
  }
  
  public void storeFlags(MessageSet[] paramArrayOfMessageSet, Flags paramFlags, boolean paramBoolean) throws ProtocolException
  {
    storeFlags(MessageSet.toString(paramArrayOfMessageSet), paramFlags, paramBoolean);
  }
  
  public void storeFlags(int paramInt1, int paramInt2, Flags paramFlags, boolean paramBoolean) throws ProtocolException
  {
    storeFlags(String.valueOf(paramInt1) + ":" + String.valueOf(paramInt2), 
      paramFlags, paramBoolean);
  }
  


  public void storeFlags(int paramInt, Flags paramFlags, boolean paramBoolean)
    throws ProtocolException
  {
    storeFlags(String.valueOf(paramInt), paramFlags, paramBoolean);
  }
  
  private void storeFlags(String paramString, Flags paramFlags, boolean paramBoolean) throws ProtocolException
  {
    Response[] arrayOfResponse;
    if (paramBoolean) {
      arrayOfResponse = command("STORE " + paramString + " +FLAGS " + 
        createFlagList(paramFlags), null);
    } else {
      arrayOfResponse = command("STORE " + paramString + " -FLAGS " + 
        createFlagList(paramFlags), null);
    }
    
    notifyResponseHandlers(arrayOfResponse);
  }
  


  private String createFlagList(Flags paramFlags)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("(");
    
    Flags.Flag[] arrayOfFlag = paramFlags.getSystemFlags();
    int i = 1;
    for (int j = 0; j < arrayOfFlag.length; j++)
    {
      Flags.Flag localFlag = arrayOfFlag[j];
      if (localFlag == Flags.Flag.ANSWERED) {
        localObject = "\\Answered";
      } else if (localFlag == Flags.Flag.DELETED) {
        localObject = "\\Deleted";
      } else if (localFlag == Flags.Flag.DRAFT) {
        localObject = "\\Draft";
      } else if (localFlag == Flags.Flag.FLAGGED) {
        localObject = "\\Flagged";
      } else if (localFlag == Flags.Flag.RECENT) {
        localObject = "\\Recent";
      } else { if (localFlag != Flags.Flag.SEEN) continue;
        localObject = "\\Seen";
      }
      
      if (i != 0) {
        i = 0;
      } else
        localStringBuffer.append(' ');
      localStringBuffer.append((String)localObject);
    }
    
    Object localObject = paramFlags.getUserFlags();
    for (int k = 0; k < localObject.length; k++) {
      if (i != 0) {
        i = 0;
      } else
        localStringBuffer.append(' ');
      localStringBuffer.append(localObject[k]);
    }
    
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  








  public int[] search(MessageSet[] paramArrayOfMessageSet, SearchTerm paramSearchTerm)
    throws ProtocolException, SearchException
  {
    return search(MessageSet.toString(paramArrayOfMessageSet), paramSearchTerm);
  }
  







  public int[] search(SearchTerm paramSearchTerm)
    throws ProtocolException, SearchException
  {
    return search("ALL", paramSearchTerm);
  }
  




  private int[] search(String paramString, SearchTerm paramSearchTerm)
    throws ProtocolException, SearchException
  {
    if (SearchSequence.isAscii(paramSearchTerm)) {
      try {
        return issueSearch(paramString, paramSearchTerm, null);
      }
      catch (IOException localIOException1) {}
    }
    







    for (int i = 0; i < searchCharsets.length; i++) {
      if (searchCharsets[i] != null)
      {
        try
        {
          return issueSearch(paramString, paramSearchTerm, searchCharsets[i]);


        }
        catch (CommandFailedException localCommandFailedException)
        {

          searchCharsets[i] = null;

        }
        catch (IOException localIOException2) {}catch (ProtocolException localProtocolException)
        {

          throw localProtocolException;
        } catch (SearchException localSearchException) {
          throw localSearchException;
        }
      }
    }
    
    throw new SearchException("Search failed");
  }
  







  private int[] issueSearch(String paramString1, SearchTerm paramSearchTerm, String paramString2)
    throws ProtocolException, SearchException, IOException
  {
    Argument localArgument = SearchSequence.generateSequence(paramSearchTerm, 
      paramString2 == null ? null : 
      MimeUtility.javaCharset(paramString2));
    
    localArgument.writeAtom(paramString1);
    
    Response[] arrayOfResponse;
    
    if (paramString2 == null) {
      arrayOfResponse = command("SEARCH", localArgument);
    } else {
      arrayOfResponse = command("SEARCH CHARSET " + paramString2, localArgument);
    }
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    int[] arrayOfInt = null;
    

    if (localResponse.isOK()) {
      Vector localVector = new Vector();
      
      int j = 0; for (int k = arrayOfResponse.length; j < k; j++) {
        if ((arrayOfResponse[j] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[j];
          
          if (localIMAPResponse.keyEquals("SEARCH")) { int i;
            while ((i = localIMAPResponse.readNumber()) != -1)
              localVector.addElement(new Integer(i));
            arrayOfResponse[j] = null;
          }
        }
      }
      
      int m = localVector.size();
      arrayOfInt = new int[m];
      for (int n = 0; n < m; n++) {
        arrayOfInt[n] = ((Integer)localVector.elementAt(n)).intValue();
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    return arrayOfInt;
  }
  



  public Namespaces namespace()
    throws ProtocolException
  {
    if (!hasCapability("NAMESPACE")) {
      throw new BadCommandException("NAMESPACE not supported");
    }
    Response[] arrayOfResponse = command("NAMESPACE", null);
    
    Namespaces localNamespaces = null;
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals("NAMESPACE")) {
            if (localNamespaces == null)
              localNamespaces = new Namespaces(localIMAPResponse);
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    return localNamespaces;
  }
  







  public Quota[] getQuotaRoot(String paramString)
    throws ProtocolException
  {
    if (!hasCapability("QUOTA")) {
      throw new BadCommandException("GETQUOTAROOT not supported");
    }
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("GETQUOTAROOT", localArgument);
    
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    
    Hashtable localHashtable = new Hashtable();
    

    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          Object localObject; if (localIMAPResponse.keyEquals("QUOTAROOT"))
          {



            localIMAPResponse.readAtomString();
            
            localObject = null;
            while ((localObject = localIMAPResponse.readAtom()) != null)
              localHashtable.put(localObject, new Quota((String)localObject));
            arrayOfResponse[i] = null;
          } else if (localIMAPResponse.keyEquals("QUOTA")) {
            localObject = parseQuota(localIMAPResponse);
            Quota localQuota = (Quota)localHashtable.get(quotaRoot);
            ((localQuota == null) || (resources == null) ? 0 : 1);
            

            localHashtable.put(quotaRoot, localObject);
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    
    Quota[] arrayOfQuota = new Quota[localHashtable.size()];
    Enumeration localEnumeration = localHashtable.elements();
    for (int k = 0; localEnumeration.hasMoreElements(); k++)
      arrayOfQuota[k] = ((Quota)localEnumeration.nextElement());
    return arrayOfQuota;
  }
  






  public Quota[] getQuota(String paramString)
    throws ProtocolException
  {
    if (!hasCapability("QUOTA")) {
      throw new BadCommandException("QUOTA not supported");
    }
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("GETQUOTA", localArgument);
    
    Quota localQuota = null;
    Vector localVector = new Vector();
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals("QUOTA")) {
            localQuota = parseQuota(localIMAPResponse);
            localVector.addElement(localQuota);
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    Quota[] arrayOfQuota = new Quota[localVector.size()];
    localVector.copyInto(arrayOfQuota);
    return arrayOfQuota;
  }
  





  public void setQuota(Quota paramQuota)
    throws ProtocolException
  {
    if (!hasCapability("QUOTA")) {
      throw new BadCommandException("QUOTA not supported");
    }
    Argument localArgument1 = new Argument();
    localArgument1.writeString(quotaRoot);
    Argument localArgument2 = new Argument();
    for (int i = 0; i < resources.length; i++) {
      localArgument2.writeAtom(resources[i].name);
      localArgument2.writeNumber(resources[i].limit);
    }
    localArgument1.writeArgument(localArgument2);
    
    Response[] arrayOfResponse = command("SETQUOTA", localArgument1);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

























    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
  }
  







  private Quota parseQuota(Response paramResponse)
    throws ParsingException
  {
    String str1 = paramResponse.readAtomString();
    Quota localQuota = new Quota(str1);
    paramResponse.skipSpaces();
    
    if (paramResponse.readByte() != 40) {
      throw new ParsingException("parse error in QUOTA");
    }
    Vector localVector = new Vector();
    while (paramResponse.peekByte() != 41)
    {
      String str2 = paramResponse.readAtom();
      if (str2 != null) {
        long l1 = paramResponse.readLong();
        long l2 = paramResponse.readLong();
        Quota.Resource localResource = new Quota.Resource(str2, l1, l2);
        localVector.addElement(localResource);
      }
    }
    paramResponse.readByte();
    resources = new Quota.Resource[localVector.size()];
    localVector.copyInto(resources);
    return localQuota;
  }
  





  public void setACL(String paramString, char paramChar, ACL paramACL)
    throws ProtocolException
  {
    if (!hasCapability("ACL")) {
      throw new BadCommandException("ACL not supported");
    }
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    localArgument.writeString(paramACL.getName());
    String str = paramACL.getRights().toString();
    if ((paramChar == '+') || (paramChar == '-'))
      str = paramChar + str;
    localArgument.writeString(str);
    
    Response[] arrayOfResponse = command("SETACL", localArgument);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
  }
  



  public void deleteACL(String paramString1, String paramString2)
    throws ProtocolException
  {
    if (!hasCapability("ACL")) {
      throw new BadCommandException("ACL not supported");
    }
    
    paramString1 = BASE64MailboxEncoder.encode(paramString1);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString1);
    localArgument.writeString(paramString2);
    
    Response[] arrayOfResponse = command("DELETEACL", localArgument);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
  }
  



  public ACL[] getACL(String paramString)
    throws ProtocolException
  {
    if (!hasCapability("ACL")) {
      throw new BadCommandException("ACL not supported");
    }
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("GETACL", localArgument);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    Vector localVector = new Vector();
    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals("ACL"))
          {


            localIMAPResponse.readAtomString();
            String str1 = null;
            while ((str1 = localIMAPResponse.readAtom()) != null) {
              String str2 = localIMAPResponse.readAtomString();
              if (str2 == null)
                break;
              ACL localACL = new ACL(str1, new Rights(str2));
              localVector.addElement(localACL);
            }
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    ACL[] arrayOfACL = new ACL[localVector.size()];
    localVector.copyInto(arrayOfACL);
    return arrayOfACL;
  }
  




  public Rights[] listRights(String paramString1, String paramString2)
    throws ProtocolException
  {
    if (!hasCapability("ACL")) {
      throw new BadCommandException("ACL not supported");
    }
    
    paramString1 = BASE64MailboxEncoder.encode(paramString1);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString1);
    localArgument.writeString(paramString2);
    
    Response[] arrayOfResponse = command("LISTRIGHTS", localArgument);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    Vector localVector = new Vector();
    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals("LISTRIGHTS"))
          {


            localIMAPResponse.readAtomString();
            
            localIMAPResponse.readAtomString();
            String str;
            while ((str = localIMAPResponse.readAtomString()) != null)
              localVector.addElement(new Rights(str));
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    Rights[] arrayOfRights = new Rights[localVector.size()];
    localVector.copyInto(arrayOfRights);
    return arrayOfRights;
  }
  



  public Rights myRights(String paramString)
    throws ProtocolException
  {
    if (!hasCapability("ACL")) {
      throw new BadCommandException("ACL not supported");
    }
    
    paramString = BASE64MailboxEncoder.encode(paramString);
    
    Argument localArgument = new Argument();
    localArgument.writeString(paramString);
    
    Response[] arrayOfResponse = command("MYRIGHTS", localArgument);
    Response localResponse = arrayOfResponse[(arrayOfResponse.length - 1)];
    

    Rights localRights = null;
    if (localResponse.isOK()) {
      int i = 0; for (int j = arrayOfResponse.length; i < j; i++) {
        if ((arrayOfResponse[i] instanceof IMAPResponse))
        {

          IMAPResponse localIMAPResponse = (IMAPResponse)arrayOfResponse[i];
          if (localIMAPResponse.keyEquals("MYRIGHTS"))
          {

            localIMAPResponse.readAtomString();
            String str = localIMAPResponse.readAtomString();
            if (localRights == null)
              localRights = new Rights(str);
            arrayOfResponse[i] = null;
          }
        }
      }
    }
    
    notifyResponseHandlers(arrayOfResponse);
    handleResult(localResponse);
    return localRights;
  }
}
