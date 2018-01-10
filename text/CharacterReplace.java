package text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;






























public class CharacterReplace
{
  static Hashtable characterTable = new Hashtable();
  static boolean configurationRead = false;
  static String charReplaceFileName = "Character.txt";
  
  public CharacterReplace() {}
  
  private static void loadMap()
  {
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("text/" + charReplaceFileName)));
      
      String all = "";
      String line; while ((line = in.readLine()) != null) { String line;
        StringTokenizer st = new StringTokenizer(line);
        String oldChar = "";
        if (st.hasMoreTokens())
          oldChar = st.nextToken();
        String newChar;
        String newChar; if (st.hasMoreTokens()) {
          newChar = st.nextToken();
        }
        else {
          newChar = oldChar;
        }
        if (!oldChar.trim().equals(""))
          characterTable.put(oldChar, newChar);
      }
      in.close();
    }
    catch (IOException ioe)
    {
      System.err.println(ioe.getMessage());
    }
    configurationRead = true;
  }
  
  private static String replaceCharacters(String oldString) {
    StringBuffer newString = new StringBuffer(oldString);
    Iterator keys = characterTable.keySet().iterator();
    int pos; for (; keys.hasNext(); 
        


        pos != -1)
    {
      String oldChar = (String)keys.next();
      String replacingChar = (String)characterTable.get(oldChar);
      pos = newString.toString().indexOf(oldChar);
      continue;
      newString.replace(pos, pos + oldChar.length(), replacingChar);
      pos = newString.toString().indexOf(oldChar, pos + 1);
    }
    
    return newString.toString();
  }
  



  public static String removeAccent(String accented)
  {
    if (!configurationRead) {
      loadMap();
    }
    return replaceCharacters(accented);
  }
  


  public static String[][] getMapAs2DVector()
  {
    if (!configurationRead) {
      loadMap();
    }
    if (characterTable.size() == 0) { return null;
    }
    Enumeration enumeration = characterTable.keys();
    String[][] map = new String[characterTable.size()][];
    
    for (int i = 0; enumeration.hasMoreElements(); i++) {
      String key = (String)enumeration.nextElement();
      
      String value = (String)characterTable.get(key);
      map[i] = new String[2];
      
      map[i][0] = key;
      map[i][1] = value;
    }
    
    return map;
  }
  


































  public static void main(String[] args)
  {
    System.out.println(removeAccent("Maestà"));
  }
}
