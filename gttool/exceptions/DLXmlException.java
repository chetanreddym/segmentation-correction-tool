package gttool.exceptions;

import org.xml.sax.SAXException;


public class DLXmlException
  extends SAXException
{
  protected DLExceptionCodes errorID;
  static final long serialVersionUID = 843920023L;
  
  public DLXmlException(DLExceptionCodes errorID, String msg)
  {
    super(msg);
    this.errorID = errorID;
  }
  
  public DLXmlException(DLExceptionCodes errorID, String msg, Exception e)
  {
    super(msg, e);
    this.errorID = errorID;
  }
  
  public DLExceptionCodes DLGetErrID()
  {
    return errorID;
  }
}
