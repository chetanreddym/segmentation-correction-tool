package ocr.util;

import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.Bidi;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class BidiString
{
  public static final int LtoR = 0;
  public static final int RtoL = 1;
  protected int direction = 0;
  
  protected String text = "";
  
  protected AttributedCharacterIterator para;
  
  protected int type;
  
  protected String[] wordArray = new String[0];
  
  protected int index = 0;
  
  protected CharStringArray charArray;
  
  class CharStringArray
  {
    protected ArrayList<String> charLevelArray = new ArrayList();
    
    protected int arrayIndex = 0;
    
    protected int stringIndex = 0;
    
    protected int index = 0;
    
    protected int direction = 0;
    
    public CharStringArray(char[] chars, int d) {
      direction = d;
      for (char c : chars) {
        System.out.println("c: " + c);
        charLevelArray.add(Character.toString(c));
      }
    }
    
    public CharStringArray(String inText, int d) { Pattern p = Pattern.compile("(\\d+)");
      Matcher m = p.matcher(text);
      direction = d;
      


      int le = 0;
      
      while (m.find()) {
        int s = m.start();
        int en = m.end();
        
        if (s != 0) {
          String tempS = text.substring(le, s);
          if ((direction == 1) && (tempS.matches("\\d+"))) {
            tempS = reverseString(tempS);
          }
          charLevelArray.add(tempS);
        }
        
        String tempS = text.substring(s, en);
        if ((direction == 1) && (tempS.matches("\\d+"))) {
          tempS = reverseString(tempS);
        }
        charLevelArray.add(tempS);
        le = en;
      }
      
      if (le != text.length()) {
        String tempS = text.substring(le, text.length());
        if ((direction == 1) && (tempS.matches("\\d+"))) {
          tempS = reverseString(tempS);
        }
        charLevelArray.add(tempS);
      }
    }
    
    public String reverseString(String in)
    {
      String ret = new String();
      StringCharacterIterator csi = new StringCharacterIterator(in, in.length() - 1);
      for (char i = csi.current(); i != 65535; i = csi.previous()) {
        ret = ret + String.valueOf(i);
      }
      return ret;
    }
    
    public String getNextChar() {
      String ret = null;
      if (arrayIndex < charLevelArray.size()) {
        String line = (String)charLevelArray.get(arrayIndex);
        if (stringIndex < line.length()) {
          ret = String.valueOf(line.charAt(stringIndex));
          stringIndex += 1;
          index += 1;
        } else {
          stringIndex = 0;
          arrayIndex += 1;
          ret = getNextChar();
        }
      }
      
      return ret;
    }
    
    public int getIndex() {
      return index;
    }
  }
  






  public BidiString(String in, int inType)
  {
    text = in.replaceAll("\\s+", " ");
    
    type = inType;
    if (text.matches("^\\s+$"))
      text = "";
    if (text.length() > 0) {
      Map<TextAttribute, NumericShaper> map = new HashMap();
      map.put(TextAttribute.NUMERIC_SHAPING, 
        NumericShaper.getContextualShaper(4));
      
      AttributedString string = new AttributedString(text, map);
      
      para = string.getIterator();
      para.first();
      
      Bidi bidi = new Bidi(para);
      if (bidi.isLeftToRight()) {
        direction = 0;
      } else {
        direction = 1;
      }
      
      if (type == 2)
      {
        charArray = new CharStringArray(text, direction);
      }
      else if (type == 3)
      {

        text = text.trim();
        

        wordArray = text.split(" ");
      }
      else
      {
        wordArray = text.split("\n");
      }
    }
  }
  
  public int getDirection()
  {
    return direction;
  }
  
  private String getNextWord(int remaining)
  {
    if (remaining == 0)
    {
      String ret = getNextWordHelp();
      
      for (String s = getNextWordHelp(); s != null; s = getNextWordHelp())
      {
        ret = ret + " " + s;
      }
      if (ret == null) {
        ret = "";
      }
      return ret;
    }
    return getNextWordHelp();
  }
  

  private String getNextWordHelp()
  {
    index += 1;
    if (index > wordArray.length) {
      index -= 1;
      return null;
    }
    

    return wordArray[(index - 1)];
  }
  


  private String getNextChar(int remaining)
  {
    if (remaining == 0)
    {
      int first = charArray.getIndex();
      
      return text.substring(first);
    }
    

    return charArray.getNextChar();
  }
  



  public String getNext(int remaining)
  {
    if (type == 2)
    {
      return getNextChar(remaining);
    }
    return getNextWord(remaining);
  }
  
  public int size() {
    if (type == 4) {
      return wordArray.length;
    }
    if (type == 2) {
      return text.length();
    }
    return wordArray.length;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String txt) {
    text = txt;
  }
  
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  public void setWord(int index, String word) {
    if (index == -1)
      return;
    wordArray[(index - 1)] = word;
    text = "";
    for (String s : wordArray)
      text = (text + " " + s);
  }
  
  public int getIndex() {
    return index;
  }
}
