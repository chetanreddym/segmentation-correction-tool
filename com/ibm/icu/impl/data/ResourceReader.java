package com.ibm.icu.impl.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
































public class ResourceReader
{
  private BufferedReader reader;
  private String resourceName;
  private String encoding;
  private boolean isReset;
  
  public ResourceReader(String resourceName, String encoding)
    throws UnsupportedEncodingException
  {
    this.resourceName = resourceName;
    this.encoding = encoding;
    isReset = false;
    _reset();
  }
  





  public ResourceReader(String resourceName)
  {
    this.resourceName = resourceName;
    encoding = null;
    isReset = false;
    try {
      _reset();
    }
    catch (UnsupportedEncodingException e) {}
  }
  

  public String readLine()
    throws IOException
  {
    if (isReset)
    {
      isReset = false;
      String line = reader.readLine();
      if ((line.charAt(0) == 65519) || (line.charAt(0) == 65279))
      {
        return line.substring(1);
      }
      return line;
    }
    return reader.readLine();
  }
  






  public void reset()
  {
    try
    {
      _reset();
    }
    catch (UnsupportedEncodingException e) {}
  }
  







  private void _reset()
    throws UnsupportedEncodingException
  {
    if (isReset) {
      return;
    }
    InputStream is = getClass().getResourceAsStream(resourceName);
    if (is == null) {
      throw new IllegalArgumentException("Can't open " + resourceName);
    }
    InputStreamReader isr = encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, encoding);
    

    reader = new BufferedReader(isr);
    isReset = true;
  }
}
