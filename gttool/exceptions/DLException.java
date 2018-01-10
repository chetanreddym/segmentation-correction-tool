package gttool.exceptions;


public class DLException
  extends Exception
{
  private static final long serialVersionUID = -3656143121751907454L;
  protected DLExceptionCodes errorID;
  
  public DLException(DLExceptionCodes errorID, String msg)
  {
    super(msg);
    this.errorID = errorID;
  }
  
  public DLException(DLExceptionCodes errorID, String msg, Throwable t)
  {
    super(msg, t);
    this.errorID = errorID;
  }
  
  public DLExceptionCodes DLGetErrID()
  {
    return errorID;
  }
}
