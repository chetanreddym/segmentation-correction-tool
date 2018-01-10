package com.sun.mail.iap;

import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Vector;


























public class Protocol
{
  protected String host;
  private Socket socket;
  private TraceInputStream traceInput;
  private ResponseInputStream input;
  private TraceOutputStream traceOutput;
  private DataOutputStream output;
  private int tagCounter;
  private Vector handlers;
  private long timestamp;
  
  public Protocol(String paramString1, int paramInt, boolean paramBoolean, Properties paramProperties, String paramString2)
    throws IOException, ProtocolException
  {
    host = paramString1;
    
    socket = SocketFetcher.getSocket(paramString1, paramInt, paramProperties, paramString2);
    
    traceInput = new TraceInputStream(socket.getInputStream(), 
      System.out);
    traceInput.setTrace(paramBoolean);
    input = new ResponseInputStream(traceInput);
    
    traceOutput = new TraceOutputStream(socket.getOutputStream(), 
      System.out);
    traceOutput.setTrace(paramBoolean);
    output = new DataOutputStream(
      new BufferedOutputStream(traceOutput));
    

    processGreeting(readResponse());
    
    timestamp = System.currentTimeMillis();
  }
  



  public Protocol(InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean)
  {
    host = "localhost";
    traceInput = new TraceInputStream(paramInputStream, System.out);
    traceInput.setTrace(paramBoolean);
    input = new ResponseInputStream(traceInput);
    
    traceOutput = new TraceOutputStream(paramOutputStream, System.out);
    traceOutput.setTrace(paramBoolean);
    output = new DataOutputStream(new BufferedOutputStream(traceOutput));
    timestamp = System.currentTimeMillis();
  }
  



  public long getTimestamp()
  {
    return timestamp;
  }
  


  public synchronized void addResponseHandler(ResponseHandler paramResponseHandler)
  {
    if (handlers == null)
      handlers = new Vector();
    handlers.addElement(paramResponseHandler);
  }
  


  public synchronized void removeResponseHandler(ResponseHandler paramResponseHandler)
  {
    if (handlers != null) {
      handlers.removeElement(paramResponseHandler);
    }
  }
  

  public void notifyResponseHandlers(Response[] paramArrayOfResponse)
  {
    if (handlers == null) {
      return;
    }
    int i = handlers.size();
    for (int j = 0; j < paramArrayOfResponse.length; j++) {
      Response localResponse = paramArrayOfResponse[j];
      

      if ((localResponse != null) && (localResponse.isUnTagged()))
      {


        for (int k = 0; k < i; k++)
          ((ResponseHandler)handlers.elementAt(k)).handleResponse(localResponse); }
    }
  }
  
  protected void processGreeting(Response paramResponse) throws ProtocolException {
    if (paramResponse.isBYE()) {
      throw new ConnectionException(paramResponse);
    }
  }
  

  protected ResponseInputStream getInputStream()
  {
    return input;
  }
  


  protected OutputStream getOutputStream()
  {
    return output;
  }
  



  protected boolean supportsNonSyncLiterals()
  {
    return false;
  }
  
  public Response readResponse() throws IOException, ProtocolException
  {
    return new Response(this);
  }
  
  private static final byte[] CRLF = { 13, 10 };
  
  public String writeCommand(String paramString, Argument paramArgument) throws IOException, ProtocolException
  {
    String str = "A" + Integer.toString(tagCounter++, 10);
    
    output.writeBytes(str + " " + paramString);
    
    if (paramArgument != null) {
      output.write(32);
      paramArgument.write(this);
    }
    
    output.write(CRLF);
    output.flush();
    return str;
  }
  








  public synchronized Response[] command(String paramString, Argument paramArgument)
  {
    Vector localVector = new Vector();
    int i = 0;
    String str = null;
    Response localResponse = null;
    
    try
    {
      str = writeCommand(paramString, paramArgument);
    }
    catch (Exception localException) {
      localVector.addElement(Response.ByeResponse);
      i = 1;
    }
    
    while (i == 0) {
      try {
        localResponse = readResponse();
      }
      catch (IOException localIOException) {
        localResponse = Response.ByeResponse;
      }
      catch (ProtocolException localProtocolException) {
        continue;
      }
      localVector.addElement(localResponse);
      
      if (localResponse.isBYE()) {
        i = 1;
      }
      
      if ((localResponse.isTagged()) && (localResponse.getTag().equals(str))) {
        i = 1;
      }
    }
    Response[] arrayOfResponse = new Response[localVector.size()];
    localVector.copyInto(arrayOfResponse);
    timestamp = System.currentTimeMillis();
    return arrayOfResponse;
  }
  

  public void handleResult(Response paramResponse)
    throws ProtocolException
  {
    if (paramResponse.isOK())
      return;
    if (paramResponse.isNO())
      throw new CommandFailedException(paramResponse);
    if (paramResponse.isBAD())
      throw new BadCommandException(paramResponse);
    if (paramResponse.isBYE()) {
      disconnect();
      throw new ConnectionException(paramResponse);
    }
  }
  




  public void simpleCommand(String paramString, Argument paramArgument)
    throws ProtocolException
  {
    Response[] arrayOfResponse = command(paramString, paramArgument);
    

    notifyResponseHandlers(arrayOfResponse);
    

    handleResult(arrayOfResponse[(arrayOfResponse.length - 1)]);
  }
  


  protected synchronized void disconnect()
  {
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException localIOException) {}
      socket = null;
    }
  }
  

  protected void finalize()
    throws Throwable
  {
    super.finalize();
    disconnect();
  }
}
