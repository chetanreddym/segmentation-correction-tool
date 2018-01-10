package com.sun.mail.iap;

import java.io.IOException;
import java.io.OutputStream;

public interface Literal
{
  public abstract int size();
  
  public abstract void writeTo(OutputStream paramOutputStream)
    throws IOException;
}
