package ocr.tif;

import java.io.InputStream;

public class StreamGobbler extends Thread
{
  InputStream is;
  String type;
  
  public StreamGobbler(InputStream is, String type)
  {
    this.is = is;
    this.type = type;
  }
  
  public void run()
  {
    try
    {
      java.io.InputStreamReader isr = new java.io.InputStreamReader(is);
      java.io.BufferedReader br = new java.io.BufferedReader(isr);
      String line = null;
      while ((line = br.readLine()) != null) {
        System.out.println(type + ">" + line);
      }
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
