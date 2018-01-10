package com.ibm.icu.text;

import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.data.ResourceReader;
import com.ibm.icu.lang.UCharacter;
import java.io.IOException;
import java.text.ParsePosition;
import java.util.Hashtable;
import java.util.Vector;





























































































class TransliteratorParser
{
  public RuleBasedTransliterator.Data data;
  public String idBlock;
  public int idSplitPoint;
  public UnicodeSet compoundFilter;
  private int ruleCount;
  private int direction;
  private ParseData parseData;
  private Vector variablesVector;
  private StringBuffer segmentStandins;
  private Vector segmentObjects;
  private char variableNext;
  private char variableLimit;
  private String undefinedVariableName;
  private int dotStandIn = -1;
  
  private static final String ID_TOKEN = "::";
  
  private static final int ID_TOKEN_LEN = 2;
  
  private static final char VARIABLE_DEF_OP = '=';
  
  private static final char FORWARD_RULE_OP = '>';
  
  private static final char REVERSE_RULE_OP = '<';
  
  private static final char FWDREV_RULE_OP = '~';
  
  private static final String OPERATORS = "=><←→↔";
  
  private static final String HALF_ENDERS = "=><←→↔;";
  
  private static final char QUOTE = '\'';
  
  private static final char ESCAPE = '\\';
  
  private static final char END_OF_RULE = ';';
  
  private static final char RULE_COMMENT_CHAR = '#';
  
  private static final char CONTEXT_ANTE = '{';
  
  private static final char CONTEXT_POST = '}';
  
  private static final char CURSOR_POS = '|';
  
  private static final char CURSOR_OFFSET = '@';
  
  private static final char ANCHOR_START = '^';
  
  private static final char KLEENE_STAR = '*';
  
  private static final char ONE_OR_MORE = '+';
  
  private static final char ZERO_OR_ONE = '?';
  
  private static final char DOT = '.';
  
  private static final String DOT_SET = "[^[:Zp:][:Zl:]\\r\\n$]";
  
  private static final char SEGMENT_OPEN = '(';
  
  private static final char SEGMENT_CLOSE = ')';
  
  private static final char FUNCTION = '&';
  
  private static final char ALT_REVERSE_RULE_OP = '←';
  
  private static final char ALT_FORWARD_RULE_OP = '→';
  
  private static final char ALT_FWDREV_RULE_OP = '↔';
  
  private static final char ALT_FUNCTION = '∆';
  private static UnicodeSet ILLEGAL_TOP = new UnicodeSet("[\\)]");
  

  private static UnicodeSet ILLEGAL_SEG = new UnicodeSet("[\\{\\}\\|\\@]");
  

  private static UnicodeSet ILLEGAL_FUNC = new UnicodeSet("[\\^\\(\\.\\*\\+\\?\\{\\}\\|\\@]");
  

  public TransliteratorParser() {}
  


  private class ParseData
    implements SymbolTable
  {
    ParseData(TransliteratorParser.1 x1)
    {
      this();
    }
    

    public char[] lookup(String name)
    {
      return (char[])data.variableNames.get(name);
    }
    




    public UnicodeMatcher lookupMatcher(int ch)
    {
      int i = ch - data.variablesBase;
      if ((i >= 0) && (i < variablesVector.size())) {
        return (UnicodeMatcher)variablesVector.elementAt(i);
      }
      return null;
    }
    



    public String parseReference(String text, ParsePosition pos, int limit)
    {
      int start = pos.getIndex();
      int i = start;
      while (i < limit) {
        char c = text.charAt(i);
        if (((i == start) && (!Character.isUnicodeIdentifierStart(c))) || (!Character.isUnicodeIdentifierPart(c))) {
          break;
        }
        
        i++;
      }
      if (i == start) {
        return null;
      }
      pos.setIndex(i);
      return text.substring(start, i);
    }
    





    public boolean isMatcher(int ch)
    {
      int i = ch - data.variablesBase;
      if ((i >= 0) && (i < variablesVector.size())) {
        return variablesVector.elementAt(i) instanceof UnicodeMatcher;
      }
      return true;
    }
    





    public boolean isReplacer(int ch)
    {
      int i = ch - data.variablesBase;
      if ((i >= 0) && (i < variablesVector.size())) {
        return variablesVector.elementAt(i) instanceof UnicodeReplacer;
      }
      return true;
    }
    



    private ParseData() {}
  }
  


  private static abstract class RuleBody
  {
    RuleBody(TransliteratorParser.1 x0)
    {
      this();
    }
    
    abstract String handleNextLine();
    
    abstract void reset();
    
    String nextLine()
    {
      String s = handleNextLine();
      if ((s != null) && (s.length() > 0) && (s.charAt(s.length() - 1) == '\\'))
      {


        StringBuffer b = new StringBuffer(s);
        do {
          b.deleteCharAt(b.length() - 1);
          s = handleNextLine();
          if (s == null) {
            break;
          }
          b.append(s);
        }
        while ((s.length() > 0) && (s.charAt(s.length() - 1) == '\\'));
        
        s = b.toString();
      }
      return s;
    }
    


    private RuleBody() {}
  }
  


  private static class RuleArray
    extends TransliteratorParser.RuleBody
  {
    String[] array;
    
    int i;
    

    public RuleArray(String[] array)
    {
      super();this.array = array;i = 0; }
    
    public String handleNextLine() { return i < array.length ? array[(i++)] : null; }
    
    public void reset() {
      i = 0;
    }
  }
  
  private static class RuleReader extends TransliteratorParser.RuleBody
  {
    ResourceReader reader;
    
    public RuleReader(ResourceReader reader) {
      super();this.reader = reader;
    }
    
    public String handleNextLine() { try { return reader.readLine();
      } catch (IOException e) {}
      return null;
    }
    
    public void reset() { reader.reset(); }
  }
  


  private static class RuleHalf
  {
    public String text;
    


    RuleHalf(TransliteratorParser.1 x0)
    {
      this();
    }
    

    public int cursor = -1;
    public int ante = -1;
    public int post = -1;
    









    public int cursorOffset = 0;
    


    private int cursorOffsetPos = 0;
    
    public boolean anchorStart = false;
    public boolean anchorEnd = false;
    




    private int nextSegmentNumber = 1;
    






    public int parse(String rule, int pos, int limit, TransliteratorParser parser)
    {
      int start = pos;
      StringBuffer buf = new StringBuffer();
      pos = parseSection(rule, pos, limit, parser, buf, TransliteratorParser.ILLEGAL_TOP, false);
      text = buf.toString();
      
      if ((cursorOffset > 0) && (cursor != cursorOffsetPos)) {
        TransliteratorParser.syntaxError("Misplaced |", rule, start);
      }
      
      return pos;
    }
    


























    private int parseSection(String rule, int pos, int limit, TransliteratorParser parser, StringBuffer buf, UnicodeSet illegal, boolean isSegment)
    {
      int start = pos;
      ParsePosition pp = null;
      int quoteStart = -1;
      int quoteLimit = -1;
      int varStart = -1;
      int varLimit = -1;
      int[] iref = new int[1];
      int bufStart = buf.length();
      

      while (pos < limit)
      {

        char c = rule.charAt(pos++);
        if (!UCharacterProperty.isRuleWhiteSpace(c))
        {


          if ("=><←→↔;".indexOf(c) >= 0) {
            if (!isSegment) break;
            TransliteratorParser.syntaxError("Unclosed segment", rule, start); break;
          }
          

          if (anchorEnd)
          {
            TransliteratorParser.syntaxError("Malformed variable reference", rule, start);
          }
          if (UnicodeSet.resemblesPattern(rule, pos - 1)) {
            if (pp == null) {
              pp = new ParsePosition(0);
            }
            pp.setIndex(pos - 1);
            buf.append(parser.parseSet(rule, pp));
            pos = pp.getIndex();


          }
          else if (c == '\\') {
            if (pos == limit) {
              TransliteratorParser.syntaxError("Trailing backslash", rule, start);
            }
            iref[0] = pos;
            int escaped = Utility.unescapeAt(rule, iref);
            pos = iref[0];
            if (escaped == -1) {
              TransliteratorParser.syntaxError("Malformed escape", rule, start);
            }
            parser.checkVariableRange(escaped, rule, start);
            UTF16.append(buf, escaped);


          }
          else if (c == '\'') {
            int iq = rule.indexOf('\'', pos);
            if (iq == pos) {
              buf.append(c);
              pos++;


            }
            else
            {


              quoteStart = buf.length();
              for (;;) {
                if (iq < 0) {
                  TransliteratorParser.syntaxError("Unterminated quote", rule, start);
                }
                buf.append(rule.substring(pos, iq));
                pos = iq + 1;
                if ((pos >= limit) || (rule.charAt(pos) != '\''))
                  break;
                iq = rule.indexOf('\'', pos + 1);
              }
              



              quoteLimit = buf.length();
              
              for (iq = quoteStart; iq < quoteLimit; iq++) {
                parser.checkVariableRange(buf.charAt(iq), rule, start);
              }
            }
          }
          else
          {
            parser.checkVariableRange(c, rule, start);
            
            if (illegal.contains(c)) {
              TransliteratorParser.syntaxError("Illegal character '" + c + '\'', rule, start);
            }
            
            switch (c)
            {



            case '^': 
              if ((buf.length() == 0) && (!anchorStart)) {
                anchorStart = true;
              } else {
                TransliteratorParser.syntaxError("Misplaced anchor start", rule, start);
              }
              
              break;
            


            case '(': 
              int bufSegStart = buf.length();
              



              int segmentNumber = nextSegmentNumber++;
              

              pos = parseSection(rule, pos, limit, parser, buf, TransliteratorParser.ILLEGAL_SEG, true);
              




              StringMatcher m = new StringMatcher(buf.substring(bufSegStart), segmentNumber, data);
              



              parser.setSegmentObject(segmentNumber, m);
              buf.setLength(bufSegStart);
              buf.append(parser.getSegmentStandin(segmentNumber));
              
              break;
            
            case '&': 
            case '∆': 
              iref[0] = pos;
              TransliteratorIDParser.SingleID single = TransliteratorIDParser.parseFilterID(rule, iref);
              
              if ((single == null) || (!Utility.parseChar(rule, iref, '(')))
              {
                TransliteratorParser.syntaxError("Invalid function", rule, start);
              }
              
              Transliterator t = single.getInstance();
              if (t == null) {
                TransliteratorParser.syntaxError("Invalid function ID", rule, start);
              }
              


              int bufSegStart = buf.length();
              

              pos = parseSection(rule, iref[0], limit, parser, buf, TransliteratorParser.ILLEGAL_FUNC, true);
              


              FunctionReplacer r = new FunctionReplacer(t, new StringReplacer(buf.substring(bufSegStart), data));
              



              buf.setLength(bufSegStart);
              buf.append(parser.generateStandInFor(r));
              
              break;
            





            case '$': 
              if (pos == limit)
              {

                anchorEnd = true;
              }
              else
              {
                c = rule.charAt(pos);
                int r = UCharacter.digit(c, 10);
                if ((r >= 1) && (r <= 9)) {
                  iref[0] = pos;
                  r = Utility.parseNumber(rule, iref, 10);
                  if (r < 0) {
                    TransliteratorParser.syntaxError("Undefined segment reference", rule, start);
                  }
                  
                  pos = iref[0];
                  buf.append(parser.getSegmentStandin(r));
                } else {
                  if (pp == null) {
                    pp = new ParsePosition(0);
                  }
                  pp.setIndex(pos);
                  String name = parseData.parseReference(rule, pp, limit);
                  
                  if (name == null)
                  {




                    anchorEnd = true;
                  }
                  else {
                    pos = pp.getIndex();
                    



                    varStart = buf.length();
                    parser.appendVariableDef(name, buf);
                    varLimit = buf.length();
                  }
                } }
              break;
            case '.': 
              buf.append(parser.getDotStandIn());
              break;
            






            case '*': 
            case '+': 
            case '?': 
              if ((isSegment) && (buf.length() == bufStart))
              {
                TransliteratorParser.syntaxError("Misplaced quantifier", rule, start);
              }
              else
              {
                int qstart;
                
                int qlimit;
                if (buf.length() == quoteLimit)
                {
                  qstart = quoteStart;
                  qlimit = quoteLimit;
                } else if (buf.length() == varLimit)
                {
                  qstart = varStart;
                  qlimit = varLimit;
                }
                else
                {
                  qstart = buf.length() - 1;
                  qlimit = qstart + 1;
                }
                
                UnicodeMatcher m = new StringMatcher(buf.toString(), qstart, qlimit, 0, data);
                

                int min = 0;
                int max = Integer.MAX_VALUE;
                switch (c) {
                case '+': 
                  min = 1;
                  break;
                case '?': 
                  min = 0;
                  max = 1;
                }
                
                

                m = new Quantifier(m, min, max);
                buf.setLength(qstart);
                buf.append(parser.generateStandInFor(m));
              }
              break;
            




            case ')': 
              break;
            




            case '{': 
              if (ante >= 0) {
                TransliteratorParser.syntaxError("Multiple ante contexts", rule, start);
              }
              ante = buf.length();
              break;
            case '}': 
              if (post >= 0) {
                TransliteratorParser.syntaxError("Multiple post contexts", rule, start);
              }
              post = buf.length();
              break;
            case '|': 
              if (cursor >= 0) {
                TransliteratorParser.syntaxError("Multiple cursors", rule, start);
              }
              cursor = buf.length();
              break;
            case '@': 
              if (cursorOffset < 0) {
                if (buf.length() > 0) {
                  TransliteratorParser.syntaxError("Misplaced " + c, rule, start);
                }
                cursorOffset -= 1;
              } else if (cursorOffset > 0) {
                if ((buf.length() != cursorOffsetPos) || (cursor >= 0)) {
                  TransliteratorParser.syntaxError("Misplaced " + c, rule, start);
                }
                cursorOffset += 1;
              }
              else if ((cursor == 0) && (buf.length() == 0)) {
                cursorOffset = -1;
              } else if (cursor < 0) {
                cursorOffsetPos = buf.length();
                cursorOffset = 1;
              } else {
                TransliteratorParser.syntaxError("Misplaced " + c, rule, start);
              }
              
              break;
            






            default: 
              if ((c >= '!') && (c <= '~') && ((c < '0') || (c > '9')) && ((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')))
              {


                TransliteratorParser.syntaxError("Unquoted " + c, rule, start);
              }
              buf.append(c); }
            
          }
        } }
      return pos;
    }
    


    void removeContext()
    {
      text = text.substring(ante < 0 ? 0 : ante, post < 0 ? text.length() : post);
      
      ante = (this.post = -1);
      anchorStart = (this.anchorEnd = 0);
    }
    



    public boolean isValidOutput(TransliteratorParser parser)
    {
      for (int i = 0; i < text.length();) {
        int c = UTF16.charAt(text, i);
        i += UTF16.getCharCount(c);
        if (!parseData.isReplacer(c)) {
          return false;
        }
      }
      return true;
    }
    



    public boolean isValidInput(TransliteratorParser parser)
    {
      for (int i = 0; i < text.length();) {
        int c = UTF16.charAt(text, i);
        i += UTF16.getCharCount(c);
        if (!parseData.isMatcher(c)) {
          return false;
        }
      }
      return true;
    }
    






    private RuleHalf() {}
  }
  





  public void parse(String rules, int direction)
  {
    parseRules(new RuleArray(new String[] { rules }), direction);
  }
  



  public void parse(ResourceReader rules, int direction)
  {
    parseRules(new RuleReader(rules), direction);
  }
  
















  void parseRules(RuleBody ruleArray, int dir)
  {
    data = new RuleBasedTransliterator.Data();
    direction = dir;
    ruleCount = 0;
    compoundFilter = null;
    




    setVariableRange(61440, 63743);
    
    variablesVector = new Vector();
    parseData = new ParseData(null);
    
    StringBuffer errors = null;
    int errorCount = 0;
    
    ruleArray.reset();
    
    StringBuffer idBlockResult = new StringBuffer();
    idSplitPoint = -1;
    




    int mode = 0;
    





    compoundFilter = null;
    int compoundFilterOffset = -1;
    

    int idBlockCount = 0;
    
    for (;;)
    {
      String rule = ruleArray.nextLine();
      if (rule == null) {
        break;
      }
      int pos = 0;
      int limit = rule.length();
      while (pos < limit) {
        char c = rule.charAt(pos++);
        if (!UCharacterProperty.isRuleWhiteSpace(c))
        {


          if (c == '#') {
            pos = rule.indexOf("\n", pos) + 1;
            if (pos == 0)
            {
              break;
            }
            

          }
          else
          {

            try
            {
              pos--;
              

              if ((pos + 2 + 1 <= limit) && (rule.regionMatches(pos, "::", 0, 2)))
              {
                pos += 2;
                c = rule.charAt(pos);
                while ((UCharacterProperty.isRuleWhiteSpace(c)) && (pos < limit)) {
                  pos++;
                  c = rule.charAt(pos);
                }
                if (mode == 1)
                {
                  mode = 2;
                  

                  idSplitPoint = idBlockCount;
                }
                int[] p = { pos };
                
                TransliteratorIDParser.SingleID id = TransliteratorIDParser.parseSingleID(rule, p, direction);
                

                if ((p[0] != pos) && (Utility.parseChar(rule, p, ';')))
                {

                  if (direction == 0) {
                    idBlockResult.append(canonID).append(';');
                  } else {
                    idBlockResult.insert(0, canonID + ';');
                  }
                  
                  idBlockCount++;
                }
                else
                {
                  int[] withParens = { -1 };
                  UnicodeSet f = TransliteratorIDParser.parseGlobalFilter(rule, p, direction, withParens, idBlockResult);
                  if ((f != null) && (Utility.parseChar(rule, p, ';'))) {
                    if ((direction == 0 ? 1 : 0) == (withParens[0] == 0 ? 1 : 0))
                    {
                      if (compoundFilter != null)
                      {
                        syntaxError("Multiple global filters", rule, pos);
                      }
                      compoundFilter = f;
                      compoundFilterOffset = idBlockCount;
                    }
                    
                  }
                  else {
                    syntaxError("Invalid ::ID", rule, pos);
                  }
                }
                
                pos = p[0];
              } else if (resemblesPragma(rule, pos, limit)) {
                int ppp = parsePragma(rule, pos, limit);
                if (ppp < 0) {
                  syntaxError("Unrecognized pragma", rule, pos);
                }
                pos = ppp;
              }
              else {
                pos = parseRule(rule, pos, limit);
                ruleCount += 1;
                if (mode == 2)
                {

                  syntaxError("::ID in illegal position", rule, pos);
                }
                mode = 1;
              }
            } catch (IllegalArgumentException e) {
              if (errorCount == 30) {
                errors.append("\nMore than 30 errors; further messages squelched");
                break label665;
              }
              if (errors == null) {
                errors = new StringBuffer(e.getMessage());
              } else {
                errors.append("\n" + e.getMessage());
              }
              errorCount++;
              pos = ruleEnd(rule, pos, limit) + 1;
            }
          } }
      } }
    label665:
    idBlock = idBlockResult.toString();
    
    if (idSplitPoint < 0) {
      idSplitPoint = idBlockCount;
    }
    
    if (direction == 1) {
      idSplitPoint = (idBlockCount - idSplitPoint);
    }
    

    data.variables = new Object[variablesVector.size()];
    variablesVector.copyInto(data.variables);
    variablesVector = null;
    
    try
    {
      if ((compoundFilter != null) && (
        ((direction == 0) && (compoundFilterOffset != 0)) || ((direction == 1) && (compoundFilterOffset != idBlockCount))))
      {


        throw new IllegalArgumentException("Compound filters misplaced");
      }
      

      data.ruleSet.freeze();
      
      if (ruleCount == 0) {
        data = null;
      }
    } catch (IllegalArgumentException e) {
      if (errors == null) {
        errors = new StringBuffer(e.getMessage());
      } else {
        errors.append("\n").append(e.getMessage());
      }
    }
    
    if (errors != null) {
      throw new IllegalArgumentException(errors.toString());
    }
  }
  















  private int parseRule(String rule, int pos, int limit)
  {
    int start = pos;
    char operator = '\000';
    

    segmentStandins = new StringBuffer();
    segmentObjects = new Vector();
    
    RuleHalf left = new RuleHalf(null);
    RuleHalf right = new RuleHalf(null);
    
    undefinedVariableName = null;
    pos = left.parse(rule, pos, limit, this);
    
    if ((pos == limit) || ("=><←→↔".indexOf(operator = rule.charAt(--pos)) < 0))
    {
      syntaxError("No operator pos=" + pos, rule, start);
    }
    pos++;
    

    if ((operator == '<') && (pos < limit) && (rule.charAt(pos) == '>'))
    {
      pos++;
      operator = '~';
    }
    

    switch (operator) {
    case '→': 
      operator = '>';
      break;
    case '←': 
      operator = '<';
      break;
    case '↔': 
      operator = '~';
    }
    
    
    pos = right.parse(rule, pos, limit, this);
    
    if (pos < limit) {
      if (rule.charAt(--pos) == ';') {
        pos++;
      }
      else {
        syntaxError("Unquoted operator", rule, start);
      }
    }
    
    if (operator == '=')
    {






      if (undefinedVariableName == null) {
        syntaxError("Missing '$' or duplicate definition", rule, start);
      }
      if ((text.length() != 1) || (text.charAt(0) != variableLimit)) {
        syntaxError("Malformed LHS", rule, start);
      }
      if ((anchorStart) || (anchorEnd) || (anchorStart) || (anchorEnd))
      {
        syntaxError("Malformed variable def", rule, start);
      }
      
      int n = text.length();
      char[] value = new char[n];
      text.getChars(0, n, value, 0);
      data.variableNames.put(undefinedVariableName, value);
      
      variableLimit = ((char)(variableLimit + '\001'));
      return pos;
    }
    


    if (undefinedVariableName != null) {
      syntaxError("Undefined variable $" + undefinedVariableName, rule, start);
    }
    


    if (segmentStandins.length() > segmentObjects.size()) {
      syntaxError("Undefined segment reference", rule, start);
    }
    for (int i = 0; i < segmentStandins.length(); i++) {
      if (segmentStandins.charAt(i) == 0) {
        syntaxError("Internal error", rule, start);
      }
    }
    for (int i = 0; i < segmentObjects.size(); i++) {
      if (segmentObjects.elementAt(i) == null) {
        syntaxError("Internal error", rule, start);
      }
    }
    


    if (operator != '~') { if ((direction == 0 ? 1 : 0) != (operator == '>' ? 1 : 0))
      {
        return pos;
      }
    }
    

    if (direction == 1) {
      RuleHalf temp = left;
      left = right;
      right = temp;
    }
    



    if (operator == '~') {
      right.removeContext();
      cursor = -1;
      cursorOffset = 0;
    }
    

    if (ante < 0) {
      ante = 0;
    }
    if (post < 0) {
      post = text.length();
    }
    






    if ((ante >= 0) || (post >= 0) || (cursor >= 0) || ((cursorOffset != 0) && (cursor < 0)) || (anchorStart) || (anchorEnd) || (!left.isValidInput(this)) || (!right.isValidOutput(this)) || (ante > post))
    {









      syntaxError("Malformed rule", rule, start);
    }
    

    UnicodeMatcher[] segmentsArray = null;
    if (segmentObjects.size() > 0) {
      segmentsArray = new UnicodeMatcher[segmentObjects.size()];
      segmentObjects.toArray(segmentsArray);
    }
    
    data.ruleSet.addRule(new TransliterationRule(text, ante, post, text, cursor, cursorOffset, segmentsArray, anchorStart, anchorEnd, data));
    





    return pos;
  }
  


  private void setVariableRange(int start, int end)
  {
    if ((start > end) || (start < 0) || (end > 65535)) {
      throw new IllegalArgumentException("Invalid variable range " + start + ", " + end);
    }
    
    data.variablesBase = (this.variableNext = (char)start);
    variableLimit = ((char)(end + 1));
  }
  




  private void checkVariableRange(int ch, String rule, int start)
  {
    if ((ch >= data.variablesBase) && (ch < variableLimit)) {
      syntaxError("Variable range character in rule", rule, start);
    }
  }
  








  private void pragmaMaximumBackup(int backup)
  {
    throw new IllegalArgumentException("use maximum backup pragma not implemented yet");
  }
  









  private void pragmaNormalizeRules(Normalizer.Mode mode)
  {
    throw new IllegalArgumentException("use normalize rules pragma not implemented yet");
  }
  







  static boolean resemblesPragma(String rule, int pos, int limit)
  {
    return Utility.parsePattern(rule, pos, limit, "use ", null) >= 0;
  }
  








  private int parsePragma(String rule, int pos, int limit)
  {
    int[] array = new int[2];
    



    pos += 4;
    




    int p = Utility.parsePattern(rule, pos, limit, "~variable range # #~;", array);
    if (p >= 0) {
      setVariableRange(array[0], array[1]);
      return p;
    }
    
    p = Utility.parsePattern(rule, pos, limit, "~maximum backup #~;", array);
    if (p >= 0) {
      pragmaMaximumBackup(array[0]);
      return p;
    }
    
    p = Utility.parsePattern(rule, pos, limit, "~nfd rules~;", null);
    if (p >= 0) {
      pragmaNormalizeRules(Normalizer.NFD);
      return p;
    }
    
    p = Utility.parsePattern(rule, pos, limit, "~nfc rules~;", null);
    if (p >= 0) {
      pragmaNormalizeRules(Normalizer.NFC);
      return p;
    }
    

    return -1;
  }
  








  static final void syntaxError(String msg, String rule, int start)
  {
    int end = ruleEnd(rule, start, rule.length());
    throw new IllegalArgumentException(msg + " in \"" + Utility.escape(rule.substring(start, end)) + '"');
  }
  
  static final int ruleEnd(String rule, int start, int limit)
  {
    int end = Utility.quotedIndexOf(rule, start, limit, ";");
    if (end < 0) {
      end = limit;
    }
    return end;
  }
  



  private final char parseSet(String rule, ParsePosition pos)
  {
    UnicodeSet set = new UnicodeSet(rule, pos, parseData);
    if (variableNext >= variableLimit) {
      throw new RuntimeException("Private use variables exhausted");
    }
    set.compact();
    return generateStandInFor(set);
  }
  







  char generateStandInFor(Object obj)
  {
    for (int i = 0; i < variablesVector.size(); i++) {
      if (variablesVector.elementAt(i) == obj) {
        return (char)(data.variablesBase + i);
      }
    }
    
    if (variableNext >= variableLimit) {
      throw new RuntimeException("Variable range exhausted");
    }
    variablesVector.addElement(obj);
    return variableNext++;
  }
  


  public char getSegmentStandin(int seg)
  {
    if (segmentStandins.length() < seg) {
      segmentStandins.setLength(seg);
    }
    char c = segmentStandins.charAt(seg - 1);
    if (c == 0) {
      if (variableNext >= variableLimit) {
        throw new RuntimeException("Variable range exhausted");
      }
      c = variableNext++;
      


      variablesVector.addElement(null);
      segmentStandins.setCharAt(seg - 1, c);
    }
    return c;
  }
  






  public void setSegmentObject(int seg, StringMatcher obj)
  {
    if (segmentObjects.size() < seg) {
      segmentObjects.setSize(seg);
    }
    int index = getSegmentStandin(seg) - data.variablesBase;
    if ((segmentObjects.elementAt(seg - 1) != null) || (variablesVector.elementAt(index) != null))
    {
      throw new RuntimeException();
    }
    segmentObjects.setElementAt(obj, seg - 1);
    variablesVector.setElementAt(obj, index);
  }
  



  char getDotStandIn()
  {
    if (dotStandIn == -1) {
      dotStandIn = generateStandInFor(new UnicodeSet("[^[:Zp:][:Zl:]\\r\\n$]"));
    }
    return (char)dotStandIn;
  }
  




  private void appendVariableDef(String name, StringBuffer buf)
  {
    char[] ch = (char[])data.variableNames.get(name);
    if (ch == null)
    {



      if (undefinedVariableName == null) {
        undefinedVariableName = name;
        if (variableNext >= variableLimit) {
          throw new RuntimeException("Private use variables exhausted");
        }
        buf.append(this.variableLimit = (char)(variableLimit - '\001'));
      } else {
        throw new IllegalArgumentException("Undefined variable $" + name);
      }
    }
    else {
      buf.append(ch);
    }
  }
}
