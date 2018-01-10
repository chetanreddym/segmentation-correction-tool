package ocr.tag;

import java.io.IOException;

public class UnicodeConverter { public UnicodeConverter() {}
  
  public static String dumpfile = "bin/trash/unicodeDump.txt";
  
  static void writeOutput(String str) throws IOException {
    java.io.FileOutputStream fos = new java.io.FileOutputStream(dumpfile);
    java.io.Writer out = new java.io.OutputStreamWriter(fos, "UTF8");
    out.write(str);
    out.close();
  }
  


  static String readInput()
  {
    StringBuffer buffer = new StringBuffer();
    try
    {
      java.io.FileInputStream fis = new java.io.FileInputStream(dumpfile);
      java.io.InputStreamReader isr = new java.io.InputStreamReader(fis, "UTF8");
      java.io.Reader in = new java.io.BufferedReader(isr);
      int ch;
      while ((ch = in.read()) > -1) { int ch;
        buffer.append((char)ch); }
      in.close();
      
      return buffer.toString();
    }
    catch (IOException e)
    {
      e.printStackTrace(); }
    return null;
  }
  

  public static void main(String[] args)
  {
    String jaString = new String("日本語文字列");
    try
    {
      writeOutput(jaString);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      System.out.println("Warning: could not write " + dumpfile + " [UnicodeConverter:48]");
    }
    
    String inputString = readInput();
    String displayString = jaString + " " + inputString;
    new ShowString(displayString, "Conversion Demo");
  }
}
