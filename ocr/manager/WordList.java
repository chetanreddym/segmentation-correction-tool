package ocr.manager;



public class WordList
{
  public int nWord;
  

  String preStr;
  

  String posStr;
  

  public String[] pWord;
  

  boolean withPreStr;
  

  boolean withPosStr;
  


  public WordList()
  {
    nWord = 0;
    preStr = "";
    posStr = "";
    withPreStr = false;
    withPosStr = false;
  }
  
  private void SetPreString(String pstr) {
    preStr = pstr;
    withPreStr = true;
  }
  
  private void SetPosString(String pstr) {
    posStr = pstr;
    withPosStr = true;
  }
  
  private void SetWord(String[] iword, int iwno)
  {
    nWord = iwno;
    if (iwno > 0) {
      pWord = new String[iwno];
      System.arraycopy(iword, 0, pWord, 0, iwno);
    }
    else {
      pWord = new String[1];
      System.arraycopy(iword, 0, pWord, 0, 1);
    }
  }
}
