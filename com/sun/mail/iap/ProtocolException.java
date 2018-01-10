package com.sun.mail.iap;








public class ProtocolException
  extends Exception
{
  private Response response;
  







  public ProtocolException() {}
  






  public ProtocolException(String paramString)
  {
    super(paramString);
  }
  


  public ProtocolException(Response paramResponse)
  {
    super(paramResponse.toString());
    response = paramResponse;
  }
  


  public Response getResponse()
  {
    return response;
  }
}
