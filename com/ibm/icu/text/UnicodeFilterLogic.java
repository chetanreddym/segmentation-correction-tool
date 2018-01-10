package com.ibm.icu.text;





















/**
 * @deprecated
 */
public final class UnicodeFilterLogic
{
  private static abstract class _UF
    extends UnicodeFilter
  {
    _UF(UnicodeFilterLogic.1 x0)
    {
      this();
    }
    
    public String toPattern(boolean escapeUnprintable) { return ""; }
    

    public boolean matchesIndexValue(int v) { return false; }
    
    private _UF() {}
    
    public abstract boolean contains(int paramInt);
    
    public void addMatchSetTo(UnicodeSet toUnionTo) {} }
  
  /**
   * @deprecated
   */
  public static UnicodeFilter not(UnicodeFilter f) { new _UF(f) { private final UnicodeFilter val$f;
      
      public boolean contains(int c) { return !val$f.contains(c); }
    }; }
  








  /**
   * @deprecated
   */
  public static UnicodeFilter and(UnicodeFilter f, UnicodeFilter g)
  {
    if (f == null) {
      return g;
    }
    if (g == null) {
      return f;
    }
    new _UF(f) { private final UnicodeFilter val$f;
      
      public boolean contains(int c) { return (val$f.contains(c)) && (val$g.contains(c)); }
    };
  }
  





  /**
   * @deprecated
   */
  public static UnicodeFilter and(UnicodeFilter[] f)
  {
    new _UF(f) { private final UnicodeFilter[] val$f;
      
      public boolean contains(int c) { for (int i = 0; i < val$f.length; i++) {
          if (!val$f[i].contains(c)) {
            return false;
          }
        }
        return true;
      }
    };
  }
  



  private final UnicodeFilter val$g;
  

  /**
   * @deprecated
   */
  public static UnicodeFilter or(UnicodeFilter f, UnicodeFilter g)
  {
    if (f == null) {
      return g;
    }
    if (g == null) {
      return f;
    }
    new _UF(f) { private final UnicodeFilter val$f;
      
      public boolean contains(int c) { return (val$f.contains(c)) || (val$g.contains(c)); }
    };
  }
  


  private final UnicodeFilter val$g;
  

  /**
   * @deprecated
   */
  public static UnicodeFilter or(UnicodeFilter[] f)
  {
    new _UF(f) { private final UnicodeFilter[] val$f;
      
      public boolean contains(int c) { for (int i = 0; i < val$f.length; i++) {
          if (val$f[i].contains(c)) {
            return true;
          }
        }
        return false;
      }
    };
  }
  
  private UnicodeFilterLogic() {}
}
