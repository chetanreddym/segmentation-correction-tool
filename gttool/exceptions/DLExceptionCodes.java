package gttool.exceptions;

public enum DLExceptionCodes {
  DL_IO_EXCEPTION(1), 
  DL_FILE_NOT_FOUND_EXCEPTION(2), 
  DL_NOT_IMPLEMENTED_EXCEPTION(3), 
  DL_UNKNOWN_FILE_FORMAT_EXCEPTION(4), 
  DL_NULL_POINTER_EXCEPTION(5), 
  DL_UNKNOWN_OBJECT_EXCEPTION(6), 
  DL_WRONG_FORMAT_EXCEPTION(7), 
  DL_NOT_SUPPORTED_IMAGE_EXCEPTION(8), 
  DL_OUT_IMAGE_BOUNDARY_EXCEPTION(9), 
  DL_OUT_CHANNEL_NUMBER_EXCEPTION(10), 
  DL_NOT_SUPPORTED_EXCEPTION(11), 
  DL_UNKNOWN_TAG_EXCEPTION(12), 
  DL_MEMORY_EXCEPTION(13), 
  DL_PARAMETERS_EXCEPTION(14), 
  DL_INVALID_PAGE_NUMBER(15), 
  DL_DEPRECATED_EXCEPTION(16), 
  DL_INDEX_OUT_OF_BOUND(17), 
  DL_WRONG_XML_PAGE_FORMAT(18), 
  DL_NONE_TYPED_ZONE(19);
  
  private int code;
  
  private DLExceptionCodes(int code)
  {
    this.code = code;
  }
  
  public int dlGetCodeNumber()
  {
    return code;
  }
}
