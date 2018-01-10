package com.ibm.icu.text;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;












































































public class DictionaryBasedBreakIterator
  extends RuleBasedBreakIterator
{
  private BreakDictionary dictionary;
  private boolean[] categoryFlags;
  private int dictionaryCharCount;
  private int[] cachedBreakPositions;
  private int positionInCache;
  private static final String DICTIONARY_VAR = "_dictionary_";
  
  public DictionaryBasedBreakIterator(String description, InputStream dictionaryStream)
    throws IOException
  {
    super(description);
    dictionary = new BreakDictionary(dictionaryStream);
  }
  





  protected RuleBasedBreakIterator.Builder makeBuilder()
  {
    return new Builder();
  }
  
  public void writeTablesToFile(FileOutputStream file, boolean littleEndian) throws IOException
  {
    super.writeTablesToFile(file, littleEndian);
    
    DataOutputStream out = new DataOutputStream(file);
    

    writeSwappedInt(8, out, littleEndian);
    writeSwappedInt((short)(categoryFlags.length + 3 & 0xF), out, littleEndian);
    
    for (int i = 0; i < categoryFlags.length; i++)
      out.writeBoolean(categoryFlags[i]);
    switch (categoryFlags.length % 4) {
    case 1:  out.write(0);
    case 2:  out.write(0);
    case 3:  out.write(0);
    }
    
  }
  
  public void setText(CharacterIterator newText)
  {
    super.setText(newText);
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
  }
  





  public int first()
  {
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
    return super.first();
  }
  





  public int last()
  {
    cachedBreakPositions = null;
    dictionaryCharCount = 0;
    positionInCache = 0;
    return super.last();
  }
  





  public int previous()
  {
    CharacterIterator text = getText();
    


    if ((cachedBreakPositions != null) && (positionInCache > 0)) {
      positionInCache -= 1;
      text.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
    




    cachedBreakPositions = null;
    int result = super.previous();
    if (cachedBreakPositions != null)
      positionInCache = (cachedBreakPositions.length - 2);
    return result;
  }
  







  public int preceding(int offset)
  {
    CharacterIterator text = getText();
    RuleBasedBreakIterator.checkOffset(offset, text);
    




    if ((cachedBreakPositions == null) || (offset <= cachedBreakPositions[0]) || (offset > cachedBreakPositions[(cachedBreakPositions.length - 1)]))
    {
      cachedBreakPositions = null;
      return super.preceding(offset);
    }
    




    positionInCache = 0;
    
    while ((positionInCache < cachedBreakPositions.length) && (offset > cachedBreakPositions[positionInCache]))
      positionInCache += 1;
    positionInCache -= 1;
    text.setIndex(cachedBreakPositions[positionInCache]);
    return text.getIndex();
  }
  







  public int following(int offset)
  {
    CharacterIterator text = getText();
    RuleBasedBreakIterator.checkOffset(offset, text);
    




    if ((cachedBreakPositions == null) || (offset < cachedBreakPositions[0]) || (offset >= cachedBreakPositions[(cachedBreakPositions.length - 1)]))
    {
      cachedBreakPositions = null;
      return super.following(offset);
    }
    




    positionInCache = 0;
    
    while ((positionInCache < cachedBreakPositions.length) && (offset >= cachedBreakPositions[positionInCache]))
      positionInCache += 1;
    text.setIndex(cachedBreakPositions[positionInCache]);
    return text.getIndex();
  }
  




  protected int handleNext()
  {
    CharacterIterator text = getText();
    



    if ((cachedBreakPositions == null) || (positionInCache == cachedBreakPositions.length - 1))
    {



      int startPos = text.getIndex();
      dictionaryCharCount = 0;
      int result = super.handleNext();
      



      if ((dictionaryCharCount > 1) && (result - startPos > 1)) {
        divideUpDictionaryRange(startPos, result);

      }
      else
      {

        cachedBreakPositions = null;
        return result;
      }
    }
    



    if (cachedBreakPositions != null) {
      positionInCache += 1;
      text.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
    return 55537;
  }
  








  protected int lookupCategory(char c)
  {
    int result = super.lookupCategory(c);
    if ((result != -1) && (categoryFlags[result] != 0)) {
      dictionaryCharCount += 1;
    }
    return result;
  }
  







  private void divideUpDictionaryRange(int startPos, int endPos)
  {
    CharacterIterator text = getText();
    




    text.setIndex(startPos);
    char c = text.current();
    int category = lookupCategory(c);
    while ((category == -1) || (categoryFlags[category] == 0)) {
      c = text.next();
      category = lookupCategory(c);
    }
    












    Stack currentBreakPositions = new Stack();
    Stack possibleBreakPositions = new Stack();
    Vector wrongBreakPositions = new Vector();
    




    int state = 0;
    







    int farthestEndPoint = text.getIndex();
    Stack bestBreakPositions = null;
    

    c = text.current();
    



    for (;;)
    {
      if (dictionary.at(state, 0) == -1) {
        possibleBreakPositions.push(new Integer(text.getIndex()));
      }
      

      state = dictionary.at(state, c) & 0xFFFF;
      





      if (state == 65535) {
        currentBreakPositions.push(new Integer(text.getIndex()));
        break;
      }
      




      if ((state == 0) || (text.getIndex() >= endPos))
      {


        if (text.getIndex() > farthestEndPoint) {
          farthestEndPoint = text.getIndex();
          bestBreakPositions = (Stack)currentBreakPositions.clone();
        }
        











        while ((!possibleBreakPositions.isEmpty()) && (wrongBreakPositions.contains(possibleBreakPositions.peek())))
        {
          possibleBreakPositions.pop();
        }
        





        if (possibleBreakPositions.isEmpty()) {
          if (bestBreakPositions != null) {
            currentBreakPositions = bestBreakPositions;
            if (farthestEndPoint >= endPos) break;
            text.setIndex(farthestEndPoint + 1);


          }
          else
          {

            if (((currentBreakPositions.size() == 0) || (((Integer)currentBreakPositions.peek()).intValue() != text.getIndex())) && (text.getIndex() != startPos))
            {

              currentBreakPositions.push(new Integer(text.getIndex()));
            }
            text.next();
            currentBreakPositions.push(new Integer(text.getIndex()));

          }
          


        }
        else
        {

          Integer temp = (Integer)possibleBreakPositions.pop();
          Object temp2 = null;
          while ((!currentBreakPositions.isEmpty()) && (temp.intValue() < ((Integer)currentBreakPositions.peek()).intValue()))
          {
            temp2 = currentBreakPositions.pop();
            wrongBreakPositions.addElement(temp2);
          }
          currentBreakPositions.push(temp);
          text.setIndex(((Integer)currentBreakPositions.peek()).intValue());
        }
        


        c = text.current();
        state = 0;
        if (text.getIndex() >= endPos) {
          break;
        }
        

      }
      else
      {
        c = text.next();
      }
    }
    






    if (!currentBreakPositions.isEmpty()) {
      currentBreakPositions.pop();
    }
    currentBreakPositions.push(new Integer(endPos));
    





    cachedBreakPositions = new int[currentBreakPositions.size() + 1];
    cachedBreakPositions[0] = startPos;
    
    for (int i = 0; i < currentBreakPositions.size(); i++) {
      cachedBreakPositions[(i + 1)] = ((Integer)currentBreakPositions.elementAt(i)).intValue();
    }
    positionInCache = 0;
  }
  








  protected class Builder
    extends RuleBasedBreakIterator.Builder
  {
    private UnicodeSet dictionaryChars = new UnicodeSet();
    private String dictionaryExpression = "";
    


    public Builder()
    {
      super();
    }
    







    protected void handleSpecialSubstitution(String replace, String replaceWith, int startPos, String description)
    {
      super.handleSpecialSubstitution(replace, replaceWith, startPos, description);
      
      if (replace.equals("_dictionary_")) {
        if (replaceWith.charAt(0) == '(') {
          error("Dictionary group can't be enclosed in (", startPos, description);
        }
        dictionaryExpression = replaceWith;
        dictionaryChars = new UnicodeSet(replaceWith, false);
      }
    }
    






    protected void buildCharCategories(Vector tempRuleList)
    {
      super.buildCharCategories(tempRuleList);
      
      categoryFlags = new boolean[categories.size()];
      for (int i = 0; i < categories.size(); i++) {
        UnicodeSet cs = (UnicodeSet)categories.elementAt(i);
        
        cs.retainAll(dictionaryChars);
        if (!cs.isEmpty()) {
          categoryFlags[i] = 1;
        }
      }
    }
    






    protected void mungeExpressionList(Hashtable expressions)
    {
      expressions.put(dictionaryExpression, dictionaryChars);
    }
  }
}
