package com.sun.mail.pop3;

import com.sun.mail.util.SharedByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;















































































































































































































































































































class SharedByteArrayOutputStream
  extends ByteArrayOutputStream
{
  public SharedByteArrayOutputStream(int paramInt)
  {
    super(paramInt);
  }
  
  public InputStream toStream() {
    return new SharedByteArrayInputStream(buf, 0, count);
  }
}
