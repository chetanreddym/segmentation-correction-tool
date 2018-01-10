package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import java.text.CharacterIterator;
import java.util.Locale;
















final class BreakTransliterator
  extends Transliterator
{
  private BreakIterator bi;
  private String insertion;
  private int[] boundaries = new int[50];
  private int boundaryCount = 0;
  static final int LETTER_OR_MARK_MASK = 510;
  
  public BreakTransliterator(String ID, UnicodeFilter filter, BreakIterator bi, String insertion) { super(ID, filter);
    if (bi == null) bi = BreakIterator.getWordInstance(new Locale("th", "TH"));
    this.bi = bi;
    this.insertion = insertion;
  }
  
  public BreakTransliterator(String ID, UnicodeFilter filter) {
    this(ID, filter, null, " ");
  }
  
  public String getInsertion() {
    return insertion;
  }
  
  public void setInsertion(String insertion) {
    this.insertion = insertion;
  }
  
  public BreakIterator getBreakIterator() {
    return bi;
  }
  
  public void setBreakIterator(BreakIterator bi) {
    this.bi = bi;
  }
  









  protected void handleTransliterate(Replaceable text, Transliterator.Position pos, boolean incremental)
  {
    boundaryCount = 0;
    int boundary = 0;
    bi.setText(new ReplaceableCharacterIterator(text, start, limit, start));
    










    for (boundary = bi.first(); (boundary != -1) && (boundary < limit); boundary = bi.next())
      if (boundary != 0)
      {

        int cp = UTF16.charAt(text, boundary - 1);
        int type = UCharacter.getType(cp);
        
        if ((1 << type & 0x1FE) != 0)
        {
          cp = UTF16.charAt(text, boundary);
          type = UCharacter.getType(cp);
          
          if ((1 << type & 0x1FE) != 0)
          {
            if (boundaryCount >= boundaries.length) {
              int[] temp = new int[boundaries.length * 2];
              System.arraycopy(boundaries, 0, temp, 0, boundaries.length);
              boundaries = temp;
            }
            
            boundaries[(boundaryCount++)] = boundary;
          }
        }
      }
    int delta = 0;
    int lastBoundary = 0;
    
    if (boundaryCount != 0) {
      delta = boundaryCount * insertion.length();
      lastBoundary = boundaries[(boundaryCount - 1)];
      


      while (boundaryCount > 0) {
        boundary = boundaries[(--boundaryCount)];
        text.replace(boundary, boundary, insertion);
      }
    }
    

    contextLimit += delta;
    limit += delta;
    start = (incremental ? lastBoundary + delta : limit);
  }
  





  static void register()
  {
    Transliterator trans = new BreakTransliterator("Any-BreakInternal", null);
    Transliterator.registerInstance(trans, false);
  }
  



  static final class ReplaceableCharacterIterator
    implements CharacterIterator
  {
    private Replaceable text;
    


    private int begin;
    

    private int end;
    

    private int pos;
    


    public ReplaceableCharacterIterator(Replaceable text)
    {
      this(text, 0);
    }
    






    public ReplaceableCharacterIterator(Replaceable text, int pos)
    {
      this(text, 0, text.length(), pos);
    }
    








    public ReplaceableCharacterIterator(Replaceable text, int begin, int end, int pos)
    {
      if (text == null) {
        throw new NullPointerException();
      }
      this.text = text;
      
      if ((begin < 0) || (begin > end) || (end > text.length())) {
        throw new IllegalArgumentException("Invalid substring range");
      }
      
      if ((pos < begin) || (pos > end)) {
        throw new IllegalArgumentException("Invalid position");
      }
      
      this.begin = begin;
      this.end = end;
      this.pos = pos;
    }
    







    public void setText(Replaceable text)
    {
      if (text == null) {
        throw new NullPointerException();
      }
      this.text = text;
      begin = 0;
      end = text.length();
      pos = 0;
    }
    




    public char first()
    {
      pos = begin;
      return current();
    }
    




    public char last()
    {
      if (end != begin) {
        pos = (end - 1);
      } else {
        pos = end;
      }
      return current();
    }
    




    public char setIndex(int p)
    {
      if ((p < begin) || (p > end)) {
        throw new IllegalArgumentException("Invalid index");
      }
      pos = p;
      return current();
    }
    




    public char current()
    {
      if ((pos >= begin) && (pos < end)) {
        return text.charAt(pos);
      }
      
      return 65535;
    }
    





    public char next()
    {
      if (pos < end - 1) {
        pos += 1;
        return text.charAt(pos);
      }
      
      pos = end;
      return 65535;
    }
    





    public char previous()
    {
      if (pos > begin) {
        pos -= 1;
        return text.charAt(pos);
      }
      
      return 65535;
    }
    





    public int getBeginIndex()
    {
      return begin;
    }
    




    public int getEndIndex()
    {
      return end;
    }
    




    public int getIndex()
    {
      return pos;
    }
    






    public boolean equals(Object obj)
    {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof ReplaceableCharacterIterator)) {
        return false;
      }
      
      ReplaceableCharacterIterator that = (ReplaceableCharacterIterator)obj;
      
      if (hashCode() != that.hashCode()) {
        return false;
      }
      if (!text.equals(text)) {
        return false;
      }
      if ((pos != pos) || (begin != begin) || (end != end)) {
        return false;
      }
      return true;
    }
    




    public int hashCode()
    {
      return text.hashCode() ^ pos ^ begin ^ end;
    }
    



    public Object clone()
    {
      try
      {
        return (ReplaceableCharacterIterator)super.clone();

      }
      catch (CloneNotSupportedException e)
      {
        throw new InternalError();
      }
    }
  }
}
