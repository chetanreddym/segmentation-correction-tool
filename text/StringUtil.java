package text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class StringUtil
{
  public StringUtil() {}
  
  public static String getListAsString(List list, String delimiter)
  {
    if (list == null) return "";
    StringBuffer sb = new StringBuffer("");
    
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      Object element = iter.next();
      
      sb.append(element);
      
      if (iter.hasNext())
        sb.append(delimiter);
    }
    return sb.toString();
  }
  


  public static boolean isEmpty(String text)
  {
    return (text == null) || (text.trim().equals(""));
  }
  


  public static String removeSpaces(String text)
  {
    StringBuffer str = new StringBuffer("");
    
    for (int i = 0; i < text.length(); i++) {
      if ((text.charAt(i) != ' ') && (text.charAt(i) != '\n') && (text.charAt(i) != '\r'))
        str.append(text.charAt(i));
    }
    return str.toString();
  }
  


  public static String removeExtension(String str)
  {
    int indexOfDot = str.lastIndexOf(".");
    if (indexOfDot == -1) {
      return str;
    }
    return str.substring(0, indexOfDot);
  }
  


  public static boolean containsSpaces(String str)
  {
    String[] strings = str.split(" ");
    
    if (strings.length > 1) return true;
    return false;
  }
  





  public static String readFileIntoString(File file)
  {
    StringBuffer tmp = new StringBuffer();
    try
    {
      BufferedReader br = new BufferedReader(new java.io.FileReader(file));
      
      String line = br.readLine();
      
      while (line != null) {
        tmp.append(line);
        tmp.append("\n");
        line = br.readLine();
      }
      
      br.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      BufferedReader br;
      e.printStackTrace();
    }
    
    return tmp.toString();
  }
  





  public static String getLastWord(String str)
  {
    String[] strings = str.split("\\s+");
    
    return strings[(strings.length - 1)];
  }
}
