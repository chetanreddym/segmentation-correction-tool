package gttool.document;

import gttool.DLImage;
import gttool.exceptions.DLException;
import gttool.exceptions.DLExceptionCodes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

















public class DLDocument
  implements Comparable
{
  public String documentID;
  public String imageSrc;
  public HashMap<String, String> documentTags = new HashMap();
  

  public LinkedList<DLPage> documentPages = new LinkedList();
  




  public DLDocument()
  {
    documentID = "Void";
  }
  






  public DLDocument(DLImage documentImage, String pageID, String docID)
    throws DLException
  {
    documentID = docID;
    
    DLPage docPage = new DLPage(documentImage, pageID);
    documentPages.add(docPage);
  }
  






  public DLDocument(String imageFileName, String pageID, String docID)
    throws DLException
  {
    documentID = docID;
    
    DLPage docPage = new DLPage(new DLImage(imageFileName), pageID);
    documentPages.add(docPage);
  }
  
  public LinkedList<DLPage> getdocPages() {
    return documentPages;
  }
  


  public int dlGetNrOfPages()
  {
    return documentPages.size();
  }
  

  public boolean dlHasPages()
  {
    return documentPages.isEmpty();
  }
  




  public void dlAppendPage(DLPage docPage)
    throws DLException
  {
    if ((pageDocument != null) && (pageDocument != this)) {
      throw new DLException(DLExceptionCodes.DL_NOT_SUPPORTED_EXCEPTION, "<DLDocument::dlAppendPage()> The page to be appended has a conflicting DLDocument pointer with the current document!");
    }
    
    pageDocument = this;
    documentPages.add(docPage);
  }
  






  public void dlAppendPageList(LinkedList<DLPage> pageList)
    throws DLException
  {
    if (!pageList.isEmpty())
    {
      for (Iterator<DLPage> pages_Iter = pageList.iterator(); pages_Iter.hasNext();)
      {
        dlAppendPage((DLPage)pages_Iter.next());
      }
    }
  }
  





  public void dlInsertPage(DLPage docPage, int cursorPosition)
    throws DLException
  {
    if ((cursorPosition < 0) || (cursorPosition > dlGetNrOfPages()))
      throw new DLException(DLExceptionCodes.DL_WRONG_FORMAT_EXCEPTION, "<DLDocument::dlInsertPage()> The specified position for page insertion is invalid!");
    if ((pageDocument != null) && (pageDocument != this)) {
      throw new DLException(DLExceptionCodes.DL_NOT_SUPPORTED_EXCEPTION, "<DLDocument::dlInsertPage()> The page to be inserted has a conflicting DLDocument pointer with the current document!");
    }
    pageDocument = this;
    documentPages.add(cursorPosition, docPage);
  }
  








































  public void dlDeletePage(int cursorPosition)
    throws DLException
  {
    if ((cursorPosition < 0) || (cursorPosition > dlGetNrOfPages()))
      throw new DLException(DLExceptionCodes.DL_WRONG_FORMAT_EXCEPTION, "<DLDocument::dlDeletePage()> The specified position for page deletion is invalid!");
    documentPages.remove(cursorPosition);
  }
  




  void dlDeletePage(DLPage documentPage)
  {
    documentPages.remove(documentPage);
  }
  
  void dlClearPages()
  {
    documentPages.clear();
  }
  
  public boolean equals(Object right) { return ((right instanceof DLDocument)) && (documentID.equals(documentID)); }
  

  public int compareTo(Object arg0)
  {
    return (arg0 instanceof DLDocument) ? documentID.compareTo(documentID) : 1;
  }
  
  public String getImageSrc()
  {
    return imageSrc;
  }
  
  public void setImageSrc(String imageSrc)
  {
    this.imageSrc = imageSrc;
  }
}
