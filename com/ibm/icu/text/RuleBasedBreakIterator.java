package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.CompactByteArray;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;



















































































































































































































































public class RuleBasedBreakIterator
  extends BreakIterator
{
  protected static final byte IGNORE = -1;
  private static final String IGNORE_VAR = "_ignore_";
  private static final short START_STATE = 1;
  private static final short STOP_STATE = 0;
  private String description;
  private CompactByteArray charCategoryTable = null;
  



  private short[] stateTable = null;
  




  private short[] backwardsStateTable = null;
  




  private boolean[] endStates = null;
  




  private boolean[] lookaheadStates = null;
  




  private int numCategories;
  



  private CharacterIterator text = null;
  














  public RuleBasedBreakIterator(String description)
  {
    this.description = description;
    

    Builder builder = makeBuilder();
    builder.buildBreakIterator();
  }
  




  protected Builder makeBuilder()
  {
    return new Builder();
  }
  









  public Object clone()
  {
    RuleBasedBreakIterator result = (RuleBasedBreakIterator)super.clone();
    if (text != null) {
      text = ((CharacterIterator)text.clone());
    }
    return result;
  }
  



  public boolean equals(Object that)
  {
    try
    {
      RuleBasedBreakIterator other = (RuleBasedBreakIterator)that;
      if (!description.equals(description)) {
        return false;
      }
      if (text == null) {
        return text == null;
      }
      
      return text.equals(text);
    }
    catch (ClassCastException e) {}
    
    return false;
  }
  




  public String toString()
  {
    return description;
  }
  





  public int hashCode()
  {
    return description.hashCode();
  }
  





  public void debugDumpTables()
  {
    System.out.println("Character Classes:");
    int currentCharClass = 257;
    int startCurrentRange = 0;
    int initialStringLength = 0;
    
    StringBuffer[] charClassRanges = new StringBuffer[numCategories];
    for (int i = 0; i < numCategories; i++) {
      charClassRanges[i] = new StringBuffer();
    }
    
    for (int i = 0; i < 65535; i++) {
      if (charCategoryTable.elementAt((char)i) != currentCharClass) {
        if (currentCharClass != 257)
        {
          if (i != startCurrentRange + 1) {
            charClassRanges[currentCharClass].append("-" + Integer.toHexString(i - 1));
          }
          if (charClassRanges[currentCharClass].length() % 72 < initialStringLength % 72) {
            charClassRanges[currentCharClass].append("\n     ");
          }
        }
        

        currentCharClass = charCategoryTable.elementAt((char)i);
        startCurrentRange = i;
        initialStringLength = charClassRanges[currentCharClass].length();
        if (charClassRanges[currentCharClass].length() > 0)
          charClassRanges[currentCharClass].append(", ");
        charClassRanges[currentCharClass].append(Integer.toHexString(i));
      }
    }
    
    for (int i = 0; i < numCategories; i++) {
      System.out.println(i + ":     " + charClassRanges[i]);
    }
    

    System.out.println("\n\nState Table.   *: end state     %: look ahead state");
    System.out.print("C:\t");
    for (int i = 0; i < numCategories; i++)
      System.out.print(Integer.toString(i) + "\t");
    System.out.println();System.out.print("=================================================");
    for (int i = 0; i < stateTable.length; i++) {
      if (i % numCategories == 0) {
        System.out.println();
        if (endStates[(i / numCategories)] != 0) {
          System.out.print("*");
        } else
          System.out.print(" ");
        if (lookaheadStates[(i / numCategories)] != 0) {
          System.out.print("%");
        }
        else
          System.out.print(" ");
        System.out.print(Integer.toString(i / numCategories) + ":\t");
      }
      if (stateTable[i] == 0) {
        System.out.print(".\t");
      } else {
        System.out.print(Integer.toString(stateTable[i]) + "\t");
      }
    }
    System.out.println();
  }
  









  public void writeTablesToFile(FileOutputStream file, boolean littleEndian)
    throws IOException
  {
    DataOutputStream out = new DataOutputStream(file);
    

    byte[] comment = "Copyright (C) 1999, International Business Machines Corp. and others. All Rights Reserved.".getBytes("US-ASCII");
    
    short headerSize = (short)(comment.length + 1 + 24);
    
    short realHeaderSize = (short)(headerSize + (headerSize % 16 == 0 ? 0 : 16 - headerSize % 16));
    writeSwappedShort(realHeaderSize, out, littleEndian);
    
    out.write(218);
    out.write(39);
    
    writeSwappedShort((short)20, out, littleEndian);
    
    writeSwappedShort((short)0, out, littleEndian);
    

    if (littleEndian) {
      out.write(0);
    } else {
      out.write(1);
    }
    

    out.write(0);
    
    out.write(2);
    
    out.write(0);
    
    out.writeInt(1112689491);
    
    out.writeInt(0);
    
    out.writeInt(0);
    
    out.write(comment);
    out.write(0);
    
    while (headerSize < realHeaderSize) {
      out.write(0);
      headerSize = (short)(headerSize + 1);
    }
    


    writeSwappedInt(numCategories, out, littleEndian);
    int fileEnd = 36;
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += (description.length() + 1) * 2;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += charCategoryTable.getIndexArray().length * 2;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += charCategoryTable.getValueArray().length;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += stateTable.length * 2;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += backwardsStateTable.length * 2;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += endStates.length;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    fileEnd += lookaheadStates.length;
    fileEnd += (fileEnd % 4 == 0 ? 0 : 4 - fileEnd % 4);
    
    writeSwappedInt(fileEnd, out, littleEndian);
    


    for (int i = 0; i < description.length(); i++)
      writeSwappedShort((short)description.charAt(i), out, littleEndian);
    out.writeShort(0);
    if ((description.length() + 1) % 2 == 1) {
      out.writeShort(0);
    }
    char[] temp1 = charCategoryTable.getIndexArray();
    for (int i = 0; i < temp1.length; i++)
      writeSwappedShort((short)temp1[i], out, littleEndian);
    if (temp1.length % 2 == 1)
      out.writeShort(0);
    byte[] temp2 = charCategoryTable.getValueArray();
    out.write(temp2);
    switch (temp2.length % 4) {
    case 1:  out.write(0);
    case 2:  out.write(0);
    case 3:  out.write(0);
    }
    
    
    for (int i = 0; i < stateTable.length; i++)
      writeSwappedShort(stateTable[i], out, littleEndian);
    if (stateTable.length % 2 == 1)
      out.writeShort(0);
    for (int i = 0; i < backwardsStateTable.length; i++)
      writeSwappedShort(backwardsStateTable[i], out, littleEndian);
    if (backwardsStateTable.length % 2 == 1) {
      out.writeShort(0);
    }
    for (int i = 0; i < endStates.length; i++)
      out.writeBoolean(endStates[i]);
    switch (endStates.length % 4) {
    case 1:  out.write(0);
    case 2:  out.write(0);
    case 3:  out.write(0);
    }
    
    for (int i = 0; i < lookaheadStates.length; i++)
      out.writeBoolean(lookaheadStates[i]);
    switch (lookaheadStates.length % 4) {
    case 1:  out.write(0);
    case 2:  out.write(0);
    case 3:  out.write(0);
    }
    
  }
  


  protected void writeSwappedShort(short x, DataOutputStream out, boolean littleEndian)
    throws IOException
  {
    if (littleEndian) {
      out.write((byte)(x & 0xFF));
      out.write((byte)(x >> 8 & 0xFF));
    }
    else {
      out.write((byte)(x >> 8 & 0xFF));
      out.write((byte)(x & 0xFF));
    }
  }
  


  protected void writeSwappedInt(int x, DataOutputStream out, boolean littleEndian)
    throws IOException
  {
    if (littleEndian) {
      out.write((byte)(x & 0xFF));
      out.write((byte)(x >> 8 & 0xFF));
      out.write((byte)(x >> 16 & 0xFF));
      out.write((byte)(x >> 24 & 0xFF));
    }
    else {
      out.write((byte)(x >> 24 & 0xFF));
      out.write((byte)(x >> 16 & 0xFF));
      out.write((byte)(x >> 8 & 0xFF));
      out.write((byte)(x & 0xFF));
    }
  }
  










  public int first()
  {
    CharacterIterator t = getText();
    
    t.first();
    return t.getIndex();
  }
  





  public int last()
  {
    CharacterIterator t = getText();
    


    t.setIndex(t.getEndIndex());
    return t.getIndex();
  }
  









  public int next(int n)
  {
    int result = current();
    while (n > 0) {
      result = handleNext();
      n--;
    }
    while (n < 0) {
      result = previous();
      n++;
    }
    return result;
  }
  




  public int next()
  {
    return handleNext();
  }
  





  public int previous()
  {
    CharacterIterator text = getText();
    if (current() == text.getBeginIndex()) {
      return -1;
    }
    





    int start = current();
    text.previous();
    int lastResult = handlePrevious();
    int result = lastResult;
    



    while ((result != -1) && (result < start)) {
      lastResult = result;
      result = handleNext();
    }
    


    text.setIndex(lastResult);
    return lastResult;
  }
  



  protected static final void checkOffset(int offset, CharacterIterator text)
  {
    if ((offset < text.getBeginIndex()) || (offset > text.getEndIndex())) {
      throw new IllegalArgumentException("offset out of bounds");
    }
  }
  








  public int following(int offset)
  {
    CharacterIterator text = getText();
    if (offset == text.getEndIndex()) {
      return -1;
    }
    checkOffset(offset, text);
    



    text.setIndex(offset);
    if (offset == text.getBeginIndex()) {
      return handleNext();
    }
    







    int result = handlePrevious();
    while ((result != -1) && (result <= offset)) {
      result = handleNext();
    }
    return result;
  }
  









  public int preceding(int offset)
  {
    CharacterIterator text = getText();
    checkOffset(offset, text);
    text.setIndex(offset);
    return previous();
  }
  







  public boolean isBoundary(int offset)
  {
    CharacterIterator text = getText();
    checkOffset(offset, text);
    if (offset == text.getBeginIndex()) {
      return true;
    }
    




    return following(offset - 1) == offset;
  }
  





  public int current()
  {
    return getText().getIndex();
  }
  










  public CharacterIterator getText()
  {
    if (text == null) {
      text = new StringCharacterIterator("");
    }
    return text;
  }
  






  public void setText(CharacterIterator newText)
  {
    int end = newText.getEndIndex();
    newText.setIndex(end);
    if (newText.getIndex() != end)
    {
      text = new SafeCharIterator(newText);
    }
    else {
      text = newText;
    }
    text.first();
  }
  













  protected int handleNext()
  {
    CharacterIterator text = getText();
    if (text.getIndex() == text.getEndIndex()) {
      return -1;
    }
    

    int result = text.getIndex() + 1;
    int lookaheadResult = 0;
    

    int state = 1;
    
    char c = text.current();
    char lastC = c;
    int lastCPos = 0;
    




    if (lookupCategory(c) == -1) {
      while (lookupCategory(c) == -1) {
        c = text.next();
      }
      if ((Character.getType(c) == 6) || (Character.getType(c) == 7))
      {
        return text.getIndex();
      }
    }
    

    while ((c != 65535) && (state != 0))
    {


      int category = lookupCategory(c);
      


      if (category != -1) {
        state = lookupState(state, category);
      }
      




      if (lookaheadStates[state] != 0) {
        if (endStates[state] != 0) {
          if (lookaheadResult > 0) {
            result = lookaheadResult;
          }
          else {
            result = text.getIndex() + 1;
          }
        }
        else {
          lookaheadResult = text.getIndex() + 1;

        }
        


      }
      else if (endStates[state] != 0) {
        result = text.getIndex() + 1;
      }
      



      if ((category != -1) && (state != 0)) {
        lastC = c;
        lastCPos = text.getIndex();
      }
      c = text.next();
    }
    




    if ((c == 65535) && (lookaheadResult == text.getEndIndex())) {
      result = lookaheadResult;





    }
    else if ("\n\r\f  ".indexOf(lastC) != -1) {
      result = lastCPos + 1;
    }
    
    text.setIndex(result);
    return result;
  }
  







  protected int handlePrevious()
  {
    CharacterIterator text = getText();
    int state = 1;
    int category = 0;
    int lastCategory = 0;
    char c = text.current();
    

    while ((c != 65535) && (state != 0))
    {



      lastCategory = category;
      category = lookupCategory(c);
      


      if (category != -1) {
        state = lookupBackwardState(state, category);
      }
      

      c = text.previous();
    }
    





    if (c != 65535) {
      if (lastCategory != -1) {
        text.setIndex(text.getIndex() + 2);
      }
      else {
        text.next();
      }
    }
    
    return text.getIndex();
  }
  






  protected int lookupCategory(char c)
  {
    return charCategoryTable.elementAt(c);
  }
  











  protected int lookupState(int state, int category)
  {
    return stateTable[(state * numCategories + category)];
  }
  




  protected int lookupBackwardState(int state, int category)
  {
    return backwardsStateTable[(state * numCategories + category)];
  }
  






  private static UnicodeSet intersection(UnicodeSet a, UnicodeSet b)
  {
    UnicodeSet result = new UnicodeSet(a);
    
    result.retainAll(b);
    
    return result;
  }
  






























  protected class Builder
  {
    protected Vector categories = null;
    





    protected Hashtable expressions = null;
    




    protected UnicodeSet ignoreChars = null;
    




    protected Vector tempStateTable = null;
    






    protected Vector decisionPointList = null;
    





    protected Stack decisionPointStack = null;
    




    protected Vector loopingStates = null;
    






    protected Vector statesToBackfill = null;
    







    protected Vector mergeList = null;
    





    protected boolean clearLoopingStates = false;
    





    protected static final int END_STATE_FLAG = 32768;
    





    protected static final int DONT_LOOP_FLAG = 16384;
    





    protected static final int LOOKAHEAD_STATE_FLAG = 8192;
    





    protected static final int ALL_FLAGS = 57344;
    





    public Builder() {}
    





    public void buildBreakIterator()
    {
      Vector tempRuleList = buildRuleList(description);
      buildCharCategories(tempRuleList);
      buildStateTable(tempRuleList);
      buildBackwardsStateTable(tempRuleList);
    }
    





























    private Vector buildRuleList(String description)
    {
      Vector tempRuleList = new Vector();
      Stack parenStack = new Stack();
      
      int p = 0;
      int ruleStart = 0;
      char c = '\000';
      char lastC = '\000';
      char lastOpen = '\000';
      boolean haveEquals = false;
      boolean havePipe = false;
      boolean sawVarName = false;
      boolean sawIllegalChar = false;
      int illegalCharPos = 0;
      String charsThatCantPrecedeAsterisk = "=/<(|>*+?;\000";
      

      if ((description.length() != 0) && (description.charAt(description.length() - 1) != ';')) {
        description = description + ";";
      }
      

      while (p < description.length()) {
        c = description.charAt(p);
        switch (c)
        {

        case '(': 
        case '[': 
        case '{': 
          if (lastOpen == '{') {
            error("Can't nest brackets inside {}", p, description);
          }
          if ((lastOpen == '[') && (c != '[')) {
            error("Can't nest anything in [] but []", p, description);
          }
          


          if ((c == '{') && ((haveEquals) || (havePipe))) {
            error("Unknown variable name", p, description);
          }
          
          lastOpen = c;
          parenStack.push(new Character(c));
          if (c == '{') {
            sawVarName = true;
          }
          



          break;
        case ')': 
        case ']': 
        case '}': 
          char expectedClose = '\000';
          switch (lastOpen) {
          case '{': 
            expectedClose = '}';
            break;
          case '[': 
            expectedClose = ']';
            break;
          case '(': 
            expectedClose = ')';
          }
          
          if (c != expectedClose) {
            error("Unbalanced parentheses", p, description);
          }
          if (lastC == lastOpen) {
            error("Parens don't contain anything", p, description);
          }
          parenStack.pop();
          if (!parenStack.empty()) {
            lastOpen = ((Character)parenStack.peek()).charValue();
          }
          else {
            lastOpen = '\000';
          }
          
          break;
        
        case '*': 
        case '+': 
        case '?': 
          if (("=/<(|>*+?;\000".indexOf(lastC) != -1) && ((c != '?') || (lastC != '*')))
          {
            error("Misplaced *, +, or ?", p, description);
          }
          


          break;
        case '=': 
          if ((haveEquals) || (havePipe)) {
            error("More than one = or / in rule", p, description);
          }
          haveEquals = true;
          sawIllegalChar = false;
          break;
        


        case '/': 
          if ((haveEquals) || (havePipe)) {
            error("More than one = or / in rule", p, description);
          }
          if (sawVarName) {
            error("Unknown variable name", p, description);
          }
          havePipe = true;
          break;
        


        case '!': 
          if ((lastC != ';') && (lastC != 0)) {
            error("! can only occur at the beginning of a rule", p, description);
          }
          


          break;
        case '\\': 
          p++;
          break;
        


        case '.': 
          break;
        


        case '&': 
        case '-': 
        case ':': 
        case '^': 
          if ((lastOpen != '[') && (lastOpen != '{') && (!sawIllegalChar)) {
            sawIllegalChar = true;
            illegalCharPos = p;
          }
          



          break;
        case ';': 
          if (sawIllegalChar) {
            error("Illegal character", illegalCharPos, description);
          }
          


          if ((lastC == ';') || (lastC == 0)) {
            error("Empty rule", p, description);
          }
          if (!parenStack.empty()) {
            error("Unbalanced parenheses", p, description);
          }
          
          if (parenStack.empty())
          {


            if (haveEquals) {
              description = processSubstitution(description.substring(ruleStart, p), description, p + 1);

            }
            else
            {

              if (sawVarName) {
                error("Unknown variable name", p, description);
              }
              

              tempRuleList.addElement(description.substring(ruleStart, p));
            }
            

            ruleStart = p + 1;
            haveEquals = havePipe = sawVarName = sawIllegalChar = 0;
          }
          



          break;
        case '|': 
          if (lastC == '|') {
            error("Empty alternative", p, description);
          }
          if ((parenStack.empty()) || (lastOpen != '(')) {
            error("Misplaced |", p, description);
          }
          


          break;
        default: 
          if ((c >= ' ') && (c < '') && (!Character.isLetter(c)) && (!Character.isDigit(c)) && (!sawIllegalChar))
          {
            sawIllegalChar = true;
            illegalCharPos = p;
          }
          break; }
        
        lastC = c;
        p++;
      }
      if (tempRuleList.size() == 0) {
        error("No valid rules in description", p, description);
      }
      return tempRuleList;
    }
    











    protected String processSubstitution(String substitutionRule, String description, int startPos)
    {
      int equalPos = substitutionRule.indexOf('=');
      if (substitutionRule.charAt(0) != '$') {
        error("Missing '$' on left-hand side of =", startPos, description);
      }
      String replace = substitutionRule.substring(1, equalPos);
      String replaceWith = substitutionRule.substring(equalPos + 1);
      




      handleSpecialSubstitution(replace, replaceWith, startPos, description);
      

      if (replaceWith.length() == 0) {
        error("Nothing on right-hand side of =", startPos, description);
      }
      if (replace.length() == 0) {
        error("Nothing on left-hand side of =", startPos, description);
      }
      if (((replaceWith.charAt(0) != '[') || (replaceWith.charAt(replaceWith.length() - 1) != ']')) && ((replaceWith.charAt(0) != '(') || (replaceWith.charAt(replaceWith.length() - 1) != ')')))
      {

        error("Illegal right-hand side for =", startPos, description);
      }
      



      replace = "$" + replace;
      StringBuffer result = new StringBuffer();
      result.append(description.substring(0, startPos));
      int lastPos = startPos;
      int pos = description.indexOf(replace, startPos);
      while (pos != -1)
      {

        if ((description.charAt(pos - 1) == ';') && (description.charAt(pos + replace.length()) == '='))
        {
          error("Attempt to redefine " + replace, pos, description);
        }
        result.append(description.substring(lastPos, pos));
        result.append(replaceWith);
        lastPos = pos + replace.length();
        pos = description.indexOf(replace, lastPos);
      }
      result.append(description.substring(lastPos));
      return result.toString();
    }
    














    protected void handleSpecialSubstitution(String replace, String replaceWith, int startPos, String description)
    {
      if (replace.equals("_ignore_")) {
        if (replaceWith.charAt(0) == '(') {
          error("Ignore group can't be enclosed in (", startPos, description);
        }
        ignoreChars = new UnicodeSet(replaceWith, false);
      }
    }
    








    protected void buildCharCategories(Vector tempRuleList)
    {
      int bracketLevel = 0;
      int p = 0;
      int lineNum = 0;
      


      expressions = new Hashtable();
      while (lineNum < tempRuleList.size()) {
        String line = (String)tempRuleList.elementAt(lineNum);
        p = 0;
        while (p < line.length()) {
          char c = line.charAt(p);
          switch (c) {
          case '!': case '(': 
          case ')': case '*': 
          case '+': case '.': 
          case '/': case ';': 
          case '?': 
          case '|': 
            break;
          case '[': 
            int q = p + 1;
            bracketLevel++;
            while ((q < line.length()) && (bracketLevel != 0)) {
              c = line.charAt(q);
              if (c == '[') {
                bracketLevel++;
              }
              else if (c == ']') {
                bracketLevel--;
              }
              q++;
            }
            if (expressions.get(line.substring(p, q)) == null) {
              expressions.put(line.substring(p, q), new UnicodeSet(line.substring(p, q), false));
            }
            

            p = q - 1;
            break;
          


          case '\\': 
            p++;
            c = line.charAt(p);
          


          default: 
            UnicodeSet s = new UnicodeSet();
            s.add(line.charAt(p));
            expressions.put(line.substring(p, p + 1), s);
          }
          
          
          p++;
        }
        lineNum++;
      }
      

      categories = new Vector();
      if (this.ignoreChars != null) {
        categories.addElement(this.ignoreChars);
      }
      else {
        categories.addElement(new UnicodeSet());
      }
      this.ignoreChars = null;
      

      mungeExpressionList(expressions);
      













      Enumeration iter = expressions.elements();
      while (iter.hasMoreElements())
      {
        UnicodeSet work = new UnicodeSet((UnicodeSet)iter.nextElement());
        

        for (int j = categories.size() - 1; (!work.isEmpty()) && (j > 0); j--)
        {


          UnicodeSet cat = (UnicodeSet)categories.elementAt(j);
          UnicodeSet overlap = RuleBasedBreakIterator.intersection(work, cat);
          
          if (!overlap.isEmpty())
          {



            if (!overlap.equals(cat)) {
              cat.removeAll(overlap);
              categories.addElement(overlap);
            }
            


            work.removeAll(overlap);
          }
        }
        


        if (!work.isEmpty()) {
          categories.addElement(work);
        }
      }
      



      UnicodeSet allChars = new UnicodeSet();
      for (int i = 1; i < categories.size(); i++)
        allChars.addAll((UnicodeSet)categories.elementAt(i));
      UnicodeSet ignoreChars = (UnicodeSet)categories.elementAt(0);
      ignoreChars.removeAll(allChars);
      





      iter = expressions.keys();
      while (iter.hasMoreElements()) {
        String key = (String)iter.nextElement();
        UnicodeSet cs = (UnicodeSet)expressions.get(key);
        StringBuffer cats = new StringBuffer();
        

        for (int j = 1; j < categories.size(); j++) {
          UnicodeSet cat = new UnicodeSet((UnicodeSet)categories.elementAt(j));
          

          if (cs.containsAll(cat))
          {


            cats.append((char)(256 + j));
            if (cs.equals(cat)) {
              break;
            }
          }
        }
        


        expressions.put(key, cats.toString());
      }
      



      charCategoryTable = new CompactByteArray((byte)0);
      

      for (int i = 0; i < categories.size(); i++) {
        UnicodeSet chars = (UnicodeSet)categories.elementAt(i);
        int n = chars.getRangeCount();
        

        for (int j = 0; j < n; j++) {
          int rangeStart = chars.getRangeStart(j);
          

          if (rangeStart >= 65536) {
            break;
          }
          

          if (i != 0) {
            charCategoryTable.setElementAt((char)rangeStart, (char)chars.getRangeEnd(j), (byte)i);



          }
          else
          {


            charCategoryTable.setElementAt((char)rangeStart, (char)chars.getRangeEnd(j), (byte)-1);
          }
        }
      }
      


      charCategoryTable.compact();
      

      numCategories = categories.size();
    }
    






    protected void mungeExpressionList(Hashtable expressions) {}
    






    private void buildStateTable(Vector tempRuleList)
    {
      tempStateTable = new Vector();
      tempStateTable.addElement(new short[numCategories + 1]);
      tempStateTable.addElement(new short[numCategories + 1]);
      



      for (int i = 0; i < tempRuleList.size(); i++) {
        String rule = (String)tempRuleList.elementAt(i);
        if (rule.charAt(0) != '!') {
          parseRule(rule, true);
        }
      }
      


      finishBuildingStateTable(true);
    }
    




























































































    private void parseRule(String rule, boolean forward)
    {
      int p = 0;
      int currentState = 1;
      int lastState = currentState;
      String pendingChars = "";
      
      decisionPointStack = new Stack();
      decisionPointList = new Vector();
      loopingStates = new Vector();
      statesToBackfill = new Vector();
      

      boolean sawEarlyBreak = false;
      


      if (!forward) {
        loopingStates.addElement(new Integer(1));
      }
      

      decisionPointList.addElement(new Integer(currentState));
      
      currentState = tempStateTable.size() - 1;
      short[] state;
      while (p < rule.length()) {
        char c = rule.charAt(p);
        clearLoopingStates = false;
        


        if ((c == '[') || (c == '\\') || (Character.isLetter(c)) || (Character.isDigit(c)) || (c < ' ') || (c == '.') || (c >= ''))
        {








          if (c != '.') {
            int q = p;
            


            if (c == '\\') {
              q = p + 2;
              p++;



            }
            else if (c == '[') {
              int bracketLevel = 1;
              while (bracketLevel > 0) {
                q++;
                c = rule.charAt(q);
                if (c == '[') {
                  bracketLevel++;
                }
                else if (c == ']') {
                  bracketLevel--;
                }
                else if (c == '\\') {
                  q++;
                }
              }
              q++;

            }
            else
            {
              q = p + 1;
            }
            


            pendingChars = (String)expressions.get(rule.substring(p, q));
            

            p = q - 1;

          }
          else
          {
            int rowNum = ((Integer)decisionPointList.lastElement()).intValue();
            state = (short[])tempStateTable.elementAt(rowNum);
            


            if ((p + 1 < rule.length()) && (rule.charAt(p + 1) == '*') && (state[0] != 0)) {
              decisionPointList.addElement(new Integer(state[0]));
              pendingChars = "";
              p++;
              if ((p + 1 < rule.length()) && (rule.charAt(p + 1) == '?'))
              {
                setLoopingStates(decisionPointList, decisionPointList);
                p++;
              }
              

            }
            else
            {

              StringBuffer temp = new StringBuffer();
              for (int i = 0; i < numCategories; i++)
                temp.append((char)(i + 256));
              pendingChars = temp.toString();
            }
          }
          


          if (pendingChars.length() != 0)
          {


            if ((p + 1 < rule.length()) && ((rule.charAt(p + 1) == '*') || (rule.charAt(p + 1) == '?')))
            {


              decisionPointStack.push(decisionPointList.clone());
            }
            




            int newState = tempStateTable.size();
            if (loopingStates.size() != 0) {
              statesToBackfill.addElement(new Integer(newState));
            }
            state = new short[numCategories + 1];
            if (sawEarlyBreak) {
              state[numCategories] = 16384;
            }
            tempStateTable.addElement(state);
            




            updateStateTable(decisionPointList, pendingChars, (short)newState);
            decisionPointList.removeAllElements();
            


            lastState = currentState;
            do {
              currentState++;
              decisionPointList.addElement(new Integer(currentState));
            } while (currentState + 1 < tempStateTable.size());
          }
        }
        


        if ((c == '+') || (c == '*') || (c == '?'))
        {

          if ((c == '*') || (c == '+'))
          {

            for (int i = lastState + 1; i < tempStateTable.size(); i++) {
              Vector temp = new Vector();
              temp.addElement(new Integer(i));
              updateStateTable(temp, pendingChars, (short)(lastState + 1));
            }
            



            while (currentState + 1 < tempStateTable.size()) {
              decisionPointList.addElement(new Integer(++currentState));
            }
          }
          




          if ((c == '*') || (c == '?')) {
            Vector temp = (Vector)decisionPointStack.pop();
            for (int i = 0; i < decisionPointList.size(); i++)
              temp.addElement(decisionPointList.elementAt(i));
            decisionPointList = temp;
            















            if ((c == '*') && (p + 1 < rule.length()) && (rule.charAt(p + 1) == '?'))
            {
              setLoopingStates(decisionPointList, decisionPointList);
              p++;
            }
          }
        }
        

















        if (c == '(')
        {


          tempStateTable.addElement(new short[numCategories + 1]);
          


          lastState = currentState;
          currentState++;
          


          decisionPointList.insertElementAt(new Integer(currentState), 0);
          




          decisionPointStack.push(decisionPointList.clone());
          decisionPointStack.push(new Vector());
        }
        



        if (c == '|')
        {

          Vector oneDown = (Vector)decisionPointStack.pop();
          Vector twoDown = (Vector)decisionPointStack.peek();
          decisionPointStack.push(oneDown);
          



          for (int i = 0; i < decisionPointList.size(); i++)
            oneDown.addElement(decisionPointList.elementAt(i));
          decisionPointList = ((Vector)twoDown.clone());
        }
        










        if (c == ')')
        {



          Vector exitPoints = (Vector)decisionPointStack.pop();
          for (int i = 0; i < decisionPointList.size(); i++)
            exitPoints.addElement(decisionPointList.elementAt(i));
          decisionPointList = exitPoints;
          


          if ((p + 1 >= rule.length()) || ((rule.charAt(p + 1) != '*') && (rule.charAt(p + 1) != '+') && (rule.charAt(p + 1) != '?')))
          {



            decisionPointStack.pop();



          }
          else
          {


            exitPoints = (Vector)decisionPointList.clone();
            

            Vector temp = (Vector)decisionPointStack.pop();
            



            int tempStateNum = ((Integer)temp.firstElement()).intValue();
            short[] tempState = (short[])tempStateTable.elementAt(tempStateNum);
            


            if ((rule.charAt(p + 1) == '?') || (rule.charAt(p + 1) == '*')) {
              for (int i = 0; i < decisionPointList.size(); i++)
                temp.addElement(decisionPointList.elementAt(i));
              decisionPointList = temp;
            }
            


            if ((rule.charAt(p + 1) == '+') || (rule.charAt(p + 1) == '*')) {
              for (int i = 0; i < tempState.length; i++) {
                if (tempState[i] > tempStateNum) {
                  updateStateTable(exitPoints, new Character((char)(i + 256)).toString(), tempState[i]);
                }
              }
            }
            



            lastState = currentState;
            currentState = tempStateTable.size() - 1;
            p++;
          }
        }
        




        if (c == '/') {
          sawEarlyBreak = true;
          for (int i = 0; i < decisionPointList.size(); i++) {
            state = (short[])tempStateTable.elementAt(((Integer)decisionPointList.elementAt(i)).intValue()); int 
            
              tmp1456_1453 = numCategories; short[] tmp1456_1447 = state;tmp1456_1447[tmp1456_1453] = ((short)(tmp1456_1447[tmp1456_1453] | 0x2000));
          }
        }
        



















        if (clearLoopingStates) {
          setLoopingStates(null, decisionPointList);
        }
        


        p++;
      }
      

      setLoopingStates(null, decisionPointList);
      
















      for (int i = 0; i < decisionPointList.size(); i++) {
        int rowNum = ((Integer)decisionPointList.elementAt(i)).intValue();
        state = (short[])tempStateTable.elementAt(rowNum); int 
          tmp1561_1558 = numCategories; short[] tmp1561_1552 = state;tmp1561_1552[tmp1561_1558] = ((short)(tmp1561_1552[tmp1561_1558] | 0x8000));
        if (sawEarlyBreak) {
          int tmp1582_1579 = numCategories; short[] tmp1582_1573 = state;tmp1582_1573[tmp1582_1579] = ((short)(tmp1582_1573[tmp1582_1579] | 0x2000));
        }
      }
    }
    
























    private void updateStateTable(Vector rows, String pendingChars, short newValue)
    {
      short[] newValues = new short[numCategories + 1];
      for (int i = 0; i < pendingChars.length(); i++) {
        newValues[(pendingChars.charAt(i) - 'Ā')] = newValue;
      }
      

      for (int i = 0; i < rows.size(); i++) {
        mergeStates(((Integer)rows.elementAt(i)).intValue(), newValues, rows);
      }
    }
    





















    private void mergeStates(int rowNum, short[] newValues, Vector rowsBeingUpdated)
    {
      short[] oldValues = (short[])tempStateTable.elementAt(rowNum);
      








      boolean isLoopingState = loopingStates.contains(new Integer(rowNum));
      

      for (int i = 0; i < oldValues.length; i++)
      {

        if (oldValues[i] != newValues[i])
        {





          if ((isLoopingState) && (loopingStates.contains(new Integer(oldValues[i])))) {
            if (newValues[i] != 0) {
              if (oldValues[i] == 0) {
                clearLoopingStates = true;
              }
              oldValues[i] = newValues[i];

            }
            

          }
          else if (oldValues[i] == 0) {
            oldValues[i] = newValues[i];



          }
          else if (i == numCategories) {
            oldValues[i] = ((short)(newValues[i] & 0xE000 | oldValues[i]));



          }
          else if ((oldValues[i] != 0) && (newValues[i] != 0))
          {


            int combinedRowNum = searchMergeList(oldValues[i], newValues[i]);
            if (combinedRowNum != 0) {
              oldValues[i] = ((short)combinedRowNum);

            }
            else
            {

              int oldRowNum = oldValues[i];
              int newRowNum = newValues[i];
              combinedRowNum = tempStateTable.size();
              


              if (mergeList == null) {
                mergeList = new Vector();
              }
              mergeList.addElement(new int[] { oldRowNum, newRowNum, combinedRowNum });
              






              short[] newRow = new short[numCategories + 1];
              short[] oldRow = (short[])tempStateTable.elementAt(oldRowNum);
              System.arraycopy(oldRow, 0, newRow, 0, numCategories + 1);
              tempStateTable.addElement(newRow);
              oldValues[i] = ((short)combinedRowNum);
              








              if (((decisionPointList.contains(new Integer(oldRowNum))) || (decisionPointList.contains(new Integer(newRowNum)))) && (!decisionPointList.contains(new Integer(combinedRowNum))))
              {


                decisionPointList.addElement(new Integer(combinedRowNum));
              }
              

              if (((rowsBeingUpdated.contains(new Integer(oldRowNum))) || (rowsBeingUpdated.contains(new Integer(newRowNum)))) && (!rowsBeingUpdated.contains(new Integer(combinedRowNum))))
              {


                decisionPointList.addElement(new Integer(combinedRowNum));
              }
              

              for (int k = 0; k < decisionPointStack.size(); k++) {
                Vector dpl = (Vector)decisionPointStack.elementAt(k);
                if (((dpl.contains(new Integer(oldRowNum))) || (dpl.contains(new Integer(newRowNum)))) && (!dpl.contains(new Integer(combinedRowNum))))
                {


                  dpl.addElement(new Integer(combinedRowNum));
                }
              }
              



              mergeStates(combinedRowNum, (short[])tempStateTable.elementAt(newValues[i]), rowsBeingUpdated);
            }
          }
        }
      }
    }
    








    private int searchMergeList(int a, int b)
    {
      if (mergeList == null) {
        return 0;
      }
      



      for (int i = 0; i < mergeList.size(); i++) {
        int[] entry = (int[])mergeList.elementAt(i);
        



        if (((entry[0] == a) && (entry[1] == b)) || ((entry[0] == b) && (entry[1] == a))) {
          return entry[2];
        }
        


        if ((entry[2] == a) && ((entry[0] == b) || (entry[1] == b))) {
          return entry[2];
        }
        if ((entry[2] == b) && ((entry[0] == a) || (entry[1] == a))) {
          return entry[2];
        }
      }
      return 0;
    }
    














    private void setLoopingStates(Vector newLoopingStates, Vector endStates)
    {
      if (!loopingStates.isEmpty()) {
        int loopingState = ((Integer)loopingStates.lastElement()).intValue();
        




        for (int i = 0; i < endStates.size(); i++) {
          eliminateBackfillStates(((Integer)endStates.elementAt(i)).intValue());
        }
        







        for (int i = 0; i < statesToBackfill.size(); i++) {
          int rowNum = ((Integer)statesToBackfill.elementAt(i)).intValue();
          short[] state = (short[])tempStateTable.elementAt(rowNum);
          state[numCategories] = ((short)(state[numCategories] & 0xE000 | loopingState));
        }
        
        statesToBackfill.removeAllElements();
        loopingStates.removeAllElements();
      }
      
      if (newLoopingStates != null) {
        loopingStates = ((Vector)newLoopingStates.clone());
      }
    }
    






    private void eliminateBackfillStates(int baseState)
    {
      if (statesToBackfill.contains(new Integer(baseState)))
      {

        statesToBackfill.removeElement(new Integer(baseState));
        


        short[] state = (short[])tempStateTable.elementAt(baseState);
        for (int i = 0; i < numCategories; i++) {
          if (state[i] != 0) {
            eliminateBackfillStates(state[i]);
          }
        }
      }
    }
    




    private void backfillLoopingStates()
    {
      short[] loopingState = null;
      int loopingStateRowNum = 0;
      


      for (int i = 0; i < tempStateTable.size(); i++) {
        short[] state = (short[])tempStateTable.elementAt(i);
        




        int fromState = state[numCategories] & 0xFFFF1FFF;
        if (fromState > 0)
        {

          if (fromState != loopingStateRowNum) {
            loopingStateRowNum = fromState;
            loopingState = (short[])tempStateTable.elementAt(loopingStateRowNum);
          }
          

          int tmp71_68 = numCategories; short[] tmp71_63 = state;tmp71_63[tmp71_68] = ((short)(tmp71_63[tmp71_68] & 0xE000));
          


          for (int j = 0; j < state.length; j++) {
            if (state[j] == 0) {
              state[j] = loopingState[j];
            }
            else if (state[j] == 16384) {
              state[j] = 0;
            }
          }
        }
      }
    }
    







    private void finishBuildingStateTable(boolean forward)
    {
      backfillLoopingStates();
      

      int[] rowNumMap = new int[tempStateTable.size()];
      Stack rowsToFollow = new Stack();
      rowsToFollow.push(new Integer(1));
      rowNumMap[1] = 1;
      int i;
      for (; 
          

          rowsToFollow.size() != 0; 
          


          i < numCategories)
      {
        int rowNum = ((Integer)rowsToFollow.pop()).intValue();
        short[] row = (short[])tempStateTable.elementAt(rowNum);
        
        i = 0; continue;
        if ((row[i] != 0) && 
          (rowNumMap[row[i]] == 0)) {
          rowNumMap[row[i]] = row[i];
          rowsToFollow.push(new Integer(row[i]));
        }
        i++;
      }
      























      int[] stateClasses = new int[tempStateTable.size()];
      int nextClass = numCategories + 1;
      short[] state1;
      for (int i = 1; i < stateClasses.length; i++) {
        if (rowNumMap[i] != 0)
        {

          state1 = (short[])tempStateTable.elementAt(i);
          for (int j = 0; j < numCategories; j++) {
            if (state1[j] != 0) {
              stateClasses[i] += 1;
            }
          }
          if (stateClasses[i] == 0)
            stateClasses[i] = nextClass;
        }
      }
      nextClass++;
      




      int lastClass;
      




      do
      {
        int currentClass = 1;
        lastClass = nextClass;
        while (currentClass < nextClass)
        {
          boolean split = false;
          short[] state2; state1 = state2 = null;
          for (int i = 0; i < stateClasses.length; i++) {
            if (stateClasses[i] == currentClass)
            {
              if (state1 == null) {
                state1 = (short[])tempStateTable.elementAt(i);
              }
              else {
                state2 = (short[])tempStateTable.elementAt(i);
                for (int j = 0; j < state2.length; j++)
                  if (((j == numCategories) && (state1[j] != state2[j]) && (forward)) || ((j != numCategories) && (stateClasses[state1[j]] != stateClasses[state2[j]])))
                  {

                    stateClasses[i] = nextClass;
                    split = true;
                    break;
                  }
              }
            }
          }
          if (split) {
            nextClass++;
          }
          currentClass++;
        }
        
      } while (lastClass != nextClass);
      


      int[] representatives = new int[nextClass];
      for (int i = 1; i < stateClasses.length; i++) {
        if (representatives[stateClasses[i]] == 0) {
          representatives[stateClasses[i]] = i;
        }
        else {
          rowNumMap[i] = representatives[stateClasses[i]];
        }
      }
      


      for (int i = 1; i < rowNumMap.length; i++) {
        if (rowNumMap[i] != i) {
          tempStateTable.setElementAt(null, i);
        }
      }
      





      int newRowNum = 1;
      for (int i = 1; i < rowNumMap.length; i++) {
        if (tempStateTable.elementAt(i) != null) {
          rowNumMap[i] = (newRowNum++);
        }
      }
      for (int i = 1; i < rowNumMap.length; i++) {
        if (tempStateTable.elementAt(i) == null) {
          rowNumMap[i] = rowNumMap[rowNumMap[i]];
        }
      }
      





      if (forward) {
        endStates = new boolean[newRowNum];
        lookaheadStates = new boolean[newRowNum];
        stateTable = new short[newRowNum * numCategories];
        int p = 0;
        int p2 = 0;
        for (int i = 0; i < tempStateTable.size(); i++) {
          short[] row = (short[])tempStateTable.elementAt(i);
          if (row != null)
          {

            for (int j = 0; j < numCategories; j++) {
              stateTable[p] = ((short)rowNumMap[row[j]]);
              p++;
            }
            endStates[p2] = ((row[numCategories] & 0x8000) != 0 ? 1 : 0);
            lookaheadStates[p2] = ((row[numCategories] & 0x2000) != 0 ? 1 : 0);
            p2++;
          }
        }
      }
      else
      {
        backwardsStateTable = new short[newRowNum * numCategories];
        int p = 0;
        for (int i = 0; i < tempStateTable.size(); i++) {
          short[] row = (short[])tempStateTable.elementAt(i);
          if (row != null)
          {

            for (int j = 0; j < numCategories; j++) {
              backwardsStateTable[p] = ((short)rowNumMap[row[j]]);
              p++;
            }
          }
        }
      }
    }
    







    private void buildBackwardsStateTable(Vector tempRuleList)
    {
      tempStateTable = new Vector();
      tempStateTable.addElement(new short[numCategories + 1]);
      tempStateTable.addElement(new short[numCategories + 1]);
      







      for (int i = 0; i < tempRuleList.size(); i++) {
        String rule = (String)tempRuleList.elementAt(i);
        if (rule.charAt(0) == '!') {
          parseRule(rule.substring(1), false);
        }
      }
      backfillLoopingStates();
      

























      int backTableOffset = tempStateTable.size();
      if (backTableOffset > 2) {
        backTableOffset++;
      }
      





      for (int i = 0; i < numCategories + 1; i++) {
        tempStateTable.addElement(new short[numCategories + 1]);
      }
      short[] state = (short[])tempStateTable.elementAt(backTableOffset - 1);
      for (int i = 0; i < numCategories; i++) {
        state[i] = ((short)(i + backTableOffset));
      }
      











      int numRows = stateTable.length / numCategories;
      for (int column = 0; column < numCategories; column++) {
        for (int row = 0; row < numRows; row++) {
          int nextRow = lookupState(row, column);
          if (nextRow != 0) {
            for (int nextColumn = 0; nextColumn < numCategories; nextColumn++) {
              int cellValue = lookupState(nextRow, nextColumn);
              if (cellValue != 0) {
                state = (short[])tempStateTable.elementAt(nextColumn + backTableOffset);
                
                state[column] = ((short)(column + backTableOffset));
              }
            }
          }
        }
      }
      







      if (backTableOffset > 1)
      {




        state = (short[])tempStateTable.elementAt(1);
        for (int i = backTableOffset - 1; i < tempStateTable.size(); i++) {
          short[] state2 = (short[])tempStateTable.elementAt(i);
          for (int j = 0; j < numCategories; j++) {
            if ((state[j] != 0) && (state2[j] != 0)) {
              state2[j] = state[j];
            }
          }
        }
        




        state = (short[])tempStateTable.elementAt(backTableOffset - 1);
        for (int i = 1; i < backTableOffset - 1; i++) {
          short[] state2 = (short[])tempStateTable.elementAt(i);
          if ((state2[numCategories] & 0x8000) == 0) {
            for (int j = 0; j < numCategories; j++) {
              if (state2[j] == 0) {
                state2[j] = state[j];
              }
            }
          }
        }
      }
      




      finishBuildingStateTable(false);
    }
    






















    protected void error(String message, int position, String context)
    {
      throw new IllegalArgumentException("Parse error: " + message + "\n" + Utility.escape(context.substring(0, position)) + "\n\n" + Utility.escape(context.substring(position)));
    }
    





    protected void debugPrintVector(String label, Vector v)
    {
      System.out.print(label);
      for (int i = 0; i < v.size(); i++)
        System.out.print(v.elementAt(i).toString() + "\t");
      System.out.println();
    }
    


    protected void debugPrintVectorOfVectors(String label1, String label2, Vector v)
    {
      System.out.println(label1);
      for (int i = 0; i < v.size(); i++) {
        debugPrintVector(label2, (Vector)v.elementAt(i));
      }
    }
    

    protected void debugPrintTempStateTable()
    {
      System.out.println("      tempStateTable:");
      System.out.print("        C:\t");
      for (int i = 0; i <= numCategories; i++)
        System.out.print(Integer.toString(i) + "\t");
      System.out.println();
      for (int i = 1; i < tempStateTable.size(); i++) {
        short[] row = (short[])tempStateTable.elementAt(i);
        System.out.print("        " + i + ":\t");
        for (int j = 0; j < row.length; j++) {
          if (row[j] == 0) {
            System.out.print(".\t");
          }
          else {
            System.out.print(Integer.toString(row[j]) + "\t");
          }
        }
        System.out.println();
      }
    }
  }
  



  private static final class SafeCharIterator
    implements CharacterIterator, Cloneable
  {
    private CharacterIterator base;
    


    private int rangeStart;
    

    private int rangeLimit;
    

    private int currentIndex;
    


    SafeCharIterator(CharacterIterator base)
    {
      this.base = base;
      rangeStart = base.getBeginIndex();
      rangeLimit = base.getEndIndex();
      currentIndex = base.getIndex();
    }
    
    public char first() {
      return setIndex(rangeStart);
    }
    
    public char last() {
      return setIndex(rangeLimit - 1);
    }
    
    public char current() {
      if ((currentIndex < rangeStart) || (currentIndex >= rangeLimit)) {
        return 65535;
      }
      
      return base.setIndex(currentIndex);
    }
    

    public char next()
    {
      currentIndex += 1;
      if (currentIndex >= rangeLimit) {
        currentIndex = rangeLimit;
        return 65535;
      }
      
      return base.setIndex(currentIndex);
    }
    

    public char previous()
    {
      currentIndex -= 1;
      if (currentIndex < rangeStart) {
        currentIndex = rangeStart;
        return 65535;
      }
      
      return base.setIndex(currentIndex);
    }
    

    public char setIndex(int i)
    {
      if ((i < rangeStart) || (i > rangeLimit)) {
        throw new IllegalArgumentException("Invalid position");
      }
      currentIndex = i;
      return current();
    }
    
    public int getBeginIndex() {
      return rangeStart;
    }
    
    public int getEndIndex() {
      return rangeLimit;
    }
    
    public int getIndex() {
      return currentIndex;
    }
    
    public Object clone()
    {
      SafeCharIterator copy = null;
      try {
        copy = (SafeCharIterator)super.clone();
      }
      catch (CloneNotSupportedException e) {
        throw new Error("Clone not supported: " + e);
      }
      
      CharacterIterator copyOfBase = (CharacterIterator)base.clone();
      base = copyOfBase;
      return copy;
    }
  }
  




  public static void debugPrintln(String s)
  {
    String zeros = "0000";
    
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c >= ' ') && (c < '')) {
        out.append(c);
      }
      else {
        out.append("\\u");
        String temp = Integer.toHexString(c);
        out.append("0000".substring(0, 4 - temp.length()));
        out.append(temp);
      }
    }
    System.out.println(out);
  }
}
