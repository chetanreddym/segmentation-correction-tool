package com.sun.mail.iap;

















public class LiteralException
  extends ProtocolException
{
  private static final long serialVersionUID = -6919179828339609913L;
  
















  public LiteralException(Response paramResponse)
  {
    super(paramResponse.toString());
    response = paramResponse;
  }
}
