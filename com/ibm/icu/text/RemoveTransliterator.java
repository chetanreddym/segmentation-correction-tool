package com.ibm.icu.text;




















class RemoveTransliterator
  extends Transliterator
{
  private static String _ID = "Any-Remove";
  


  static void register()
  {
    Transliterator.registerFactory(_ID, new Transliterator.Factory() {
      public Transliterator getInstance(String ID) {
        return new RemoveTransliterator();
      }
    });
    Transliterator.registerSpecialInverse("Remove", "Null", false);
  }
  


  public RemoveTransliterator()
  {
    super(_ID, null);
  }
  





  protected void handleTransliterate(Replaceable text, Transliterator.Position index, boolean incremental)
  {
    text.replace(start, limit, "");
    int len = limit - start;
    contextLimit -= len;
    limit -= len;
  }
}
