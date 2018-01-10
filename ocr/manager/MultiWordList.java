package ocr.manager;














class MultiWordList
{
  int ckResult;
  











  int nList;
  











  String[][] pList;
  












  public MultiWordList() {}
  












  private void SetList(String[][] ilist, int ilistno, int ickresult)
  {
    nList = ilistno;
    ckResult = ickresult;
    pList = new String[ilist.length][];
    System.arraycopy(ilist, 0, pList, 0, ilistno);
  }
}
