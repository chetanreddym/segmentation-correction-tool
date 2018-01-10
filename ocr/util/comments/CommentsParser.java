package ocr.util.comments;

import com.sun.org.apache.xpath.internal.XPathAPI;
import gttool.misc.LoadUserProps;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import ocr.gui.LoadDataFile;
import ocr.gui.MultiPagePanel;
import ocr.gui.OCRInterface;
import ocr.gui.Zone;
import ocr.gui.leftPanel.AttributeWindow;
import ocr.gui.leftPanel.DatasetSpecificToolPanel;
import ocr.gui.leftPanel.ElectronicTextDisplayer;
import ocr.gui.leftPanel.FilePropPacket;
import ocr.gui.leftPanel.LeftPanel;
import ocr.gui.leftPanel.WorkmodeTable;
import ocr.manager.PropertiesInfoHolder;
import ocr.tif.ImageDisplay;
import ocr.tif.ImageDisplay.ZoneVector;
import ocr.tif.ImageReaderDrawer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;




























public class CommentsParser
{
  private Document dom;
  private static String ext = ".cmt";
  public static String newExt = ".new";
  public static String noXML = " (warning: no XML)";
  
  private String root = "GEDIComments";
  private String document = "document";
  private String page = "page";
  private String zone = "zone";
  private String comment = "comment";
  private String imageAtt = "image";
  private String xmlAtt = "xml";
  private String dateAtt = "date";
  private String userAtt = "user";
  private String archivedAtt = "archived";
  private String idAtt = "id";
  





  public CommentsParser()
  {
    if (OCRInterface.this_interface.getDisplayCommentsInSeparateWindow()) {
      showCommentsInSeparateDialog(getCommentsForCurrDoc());
    } else {
      showCommentsAtBottom(getCommentsForCurrDoc());
    }
  }
  




  public static boolean continueToParse()
  {
    if (OCRInterface.currDoc == null) {
      int answer = JOptionPane.showConfirmDialog(OCRInterface.this_interface, 
        "There is no XML file for \"" + ImageReaderDrawer.getFile_name() + 
        "\".\n" + 
        "It is highly recommended to create XML first.\n" + 
        "Continue anyway (without XML)?\n", "Add comment", 
        0, 
        3);
      if ((answer == 2) || (answer == -1) || (answer == 1)) {
        return false;
      }
    }
    return true;
  }
  





  private Document getDocument(File file)
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse(file);
    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
      return null;
    } catch (SAXException se) {
      se.printStackTrace();
      return null;
    } catch (IOException ioe) {
      ioe.printStackTrace(); }
    return null;
  }
  



  public String getCurrCommentsFilePath()
  {
    return 
    


      OCRInterface.getCurrentXmlDir() + OCRInterface.getXmlDirName() + File.separator + this_interfaceocrTable.selFileName + ext;
  }
  




  public static String getCommentsFilePathFor(String fileBasename)
  {
    String path = OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator + 
      fileBasename + ext;
    
    if (!new File(path).exists()) {
      path = null;
    }
    return path;
  }
  




  private Document createNewDocument()
  {
    Document dom = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      dom = db.newDocument();
      Element rootEle = dom.createElement(root);
      dom.appendChild(rootEle);
      return dom;
    } catch (ParserConfigurationException pce) {
      System.out.println("Error while trying to instantiate DocumentBuilder " + pce); }
    return null;
  }
  











  private Element getZoneElement(String pageNum, String zoneID)
  {
    String pagePath = page + "[@" + idAtt + "='" + pageNum + "']";
    String zonePath = zone + "[@" + idAtt + "='" + zoneID + "']";
    

    String xpath = "//" + pagePath + "/" + zonePath;
    
    NodeList nodelist = null;
    try
    {
      nodelist = XPathAPI.selectNodeList(dom, xpath);
      if (nodelist.getLength() == 0) {
        return null;
      }
      return (Element)nodelist.item(0);
    } catch (TransformerException e) {
      e.printStackTrace(); }
    return null;
  }
  










  private Element getPageElement(String pageNum)
  {
    String pagePath = page + "[@" + idAtt + "='" + pageNum + "']";
    

    String xpath = "//" + pagePath;
    
    NodeList nodelist = null;
    try
    {
      nodelist = XPathAPI.selectNodeList(dom, xpath);
      if (nodelist.getLength() == 0) {
        return null;
      }
      return (Element)nodelist.item(0);
    } catch (TransformerException e) {
      e.printStackTrace(); }
    return null;
  }
  









  private Element getDocElement(String xmlName)
  {
    String docPath = document + "[@" + xmlAtt + "='" + xmlName + "']";
    String xpath = "//" + docPath;
    NodeList nodelist = null;
    try
    {
      nodelist = XPathAPI.selectNodeList(dom, xpath);
      if (nodelist.getLength() == 0) {
        return null;
      }
      return (Element)nodelist.item(0);
    } catch (TransformerException e) {
      e.printStackTrace(); }
    return null;
  }
  













  private Element getCommentElement(String date, String user, String comment)
  {
    String xPath = "//*[contains(@date,'" + date + "') and " + 
      "contains(@user,'" + user + "')]";
    NodeList nodelist = null;
    try
    {
      nodelist = XPathAPI.selectNodeList(dom, xPath);
      if (nodelist.getLength() == 0) {
        return null;
      }
      return (Element)nodelist.item(0);
    } catch (TransformerException e) {
      e.printStackTrace(); }
    return null;
  }
  










  private void insertZoneLevelComment(GEDIComment commentObj)
  {
    Element parentElement = getZoneElement(commentObj.getPageNum(), commentObj.getZoneID());
    if (parentElement != null) {
      parentElement.appendChild(createCommentElement(commentObj));
    }
    else {
      parentElement = getPageElement(commentObj.getPageNum());
      if (parentElement != null) {
        Element zoneElem = createZoneElement(commentObj);
        Element commElem = createCommentElement(commentObj);
        zoneElem.appendChild(commElem);
        parentElement.appendChild(zoneElem);
      }
      else {
        parentElement = getDocElement(commentObj.getXml());
        if (parentElement != null) {
          Element pageElem = createPageElement(commentObj);
          Element zoneElem = createZoneElement(commentObj);
          Element commElem = createCommentElement(commentObj);
          zoneElem.appendChild(commElem);
          pageElem.appendChild(zoneElem);
          parentElement.appendChild(pageElem);
        }
        else {
          Element docElem = createDocumentElement(commentObj);
          Element pageElem = createPageElement(commentObj);
          Element zoneElem = createZoneElement(commentObj);
          Element commElem = createCommentElement(commentObj);
          zoneElem.appendChild(commElem);
          pageElem.appendChild(zoneElem);
          docElem.appendChild(pageElem);
          dom.getDocumentElement().appendChild(docElem);
        }
      }
    }
  }
  










  private void insertPageLevelComment(GEDIComment commentObj)
  {
    Element parentElement = getPageElement(commentObj.getPageNum());
    if (parentElement != null) {
      parentElement.appendChild(createCommentElement(commentObj));
    }
    else {
      parentElement = getDocElement(commentObj.getXml());
      if (parentElement != null) {
        Element pageElem = createPageElement(commentObj);
        Element commElem = createCommentElement(commentObj);
        pageElem.appendChild(commElem);
        parentElement.appendChild(pageElem);
      }
      else {
        Element docElem = createDocumentElement(commentObj);
        Element pageElem = createPageElement(commentObj);
        Element commElem = createCommentElement(commentObj);
        pageElem.appendChild(commElem);
        docElem.appendChild(pageElem);
        dom.getDocumentElement().appendChild(docElem);
      }
    }
  }
  




  public void insertComment(String commentTxt)
  {
    updateDom(getCurrCommentsFilePath());
    
    GEDIComment commentObj = buildCommentObj(commentTxt);
    
    insertComment(commentObj);
    
    writeXmlFile(dom);
  }
  
  private void insertComment(GEDIComment commentObj) {
    if (commentObj.getZoneID() == 0) {
      insertPageLevelComment(commentObj);
    } else {
      insertZoneLevelComment(commentObj);
    }
    setHasNewComments(true);
  }
  




  private void writeXmlFile(Document doc)
  {
    correctXMLattribute(doc);
    
    if (isDocEmpty(doc)) {
      return;
    }
    try {
      Source source = new DOMSource(doc);
      
      File file = new File(getCurrCommentsFilePath());
      Result result = new StreamResult(file);
      
      Transformer xformer = TransformerFactory.newInstance().newTransformer();
      xformer.setOutputProperty("indent", "yes");
      xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      xformer.transform(source, result);
    }
    catch (TransformerConfigurationException localTransformerConfigurationException) {}catch (TransformerException localTransformerException) {}
  }
  







  private boolean isDocEmpty(Document doc)
  {
    NodeList nl = doc.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ((nl.item(i).getNodeName().equals(root)) && 
        (nl.item(i).getChildNodes().getLength() == 0))
        return true;
    }
    return false;
  }
  












  private void correctXMLattribute(Document doc)
  {
    NodeList nodeList = null;
    
    String xpath = "//" + document;
    try {
      nodeList = XPathAPI.selectNodeList(dom, xpath);
      for (int i = 0; i < nodeList.getLength(); i++) {
        ((Element)nodeList.item(i)).setAttribute(xmlAtt, getCurrXml());
      }
    } catch (TransformerException e) {
      System.out.println(getClass());
      e.printStackTrace();
    }
  }
  





  public GEDIComment buildCommentObj(String commentTxt)
  {
    String user = getCurrUser();
    String image = getCurrImage();
    String xml = getCurrXml();
    String pageNum = getCurrPageNum();
    String zoneID = getCurrZoneID();
    
    GEDIComment comm = new GEDIComment();
    comm.setComment(commentTxt);
    comm.setImage(image);
    comm.setXml(xml);
    comm.setDate(new Timestamp(new Date().getTime()));
    comm.setUser(user);
    comm.setPageNum(pageNum);
    comm.setZoneID(zoneID);
    comm.setArchived("false");
    return comm;
  }
  




  private Element createDocumentElement(GEDIComment commentObj)
  {
    Element docEle = dom.createElement(document);
    docEle.setAttribute(imageAtt, commentObj.getImage());
    docEle.setAttribute(xmlAtt, commentObj.getXml());
    
    return docEle;
  }
  




  private Element createPageElement(GEDIComment commentObj)
  {
    Element pageEle = dom.createElement(page);
    pageEle.setAttribute(idAtt, commentObj.getPageNum());
    
    return pageEle;
  }
  




  private Element createCommentElement(GEDIComment commentObj)
  {
    Element commEle = dom.createElement(comment);
    commEle.setAttribute(dateAtt, commentObj.getFormattedDate());
    commEle.setAttribute(userAtt, commentObj.getUser());
    commEle.setAttribute(archivedAtt, commentObj.getArchived());
    Text commText = dom.createTextNode(commentObj.getComment());
    commEle.appendChild(commText);
    
    return commEle;
  }
  




  private Element createZoneElement(GEDIComment commentObj)
  {
    Element commEle = dom.createElement(zone);
    commEle.setAttribute(idAtt, commentObj.getZoneID());
    
    return commEle;
  }
  






  private void updateDom(String filePath)
  {
    if (new File(filePath).exists()) {
      dom = getDocument(new File(filePath));
    } else {
      dom = createNewDocument();
    }
    writeXmlFile(dom);
  }
  


  private File[] getAllCMTFiles()
  {
    String fileExtension_cmt = ".cmt";
    File xmlDir = new File(OCRInterface.getCurrentXmlDir() + 
      OCRInterface.getXmlDirName() + 
      File.separator);
    
    FileFilter fileFilter = new FileFilter() {
      public boolean accept(File file) {
        String fileName = file.getName().toLowerCase();
        return (file.isFile()) && (fileName.endsWith(".cmt"));
      }
      
    };
    File[] cmtList = (File[])null;
    cmtList = xmlDir.listFiles(fileFilter);
    
    return cmtList;
  }
  




  public Vector<GEDIComment> getCommentsForCurrDoc()
  {
    updateDom(getCurrCommentsFilePath());
    Element doc = null;
    
    doc = dom.getDocumentElement();
    
    if (doc == null) {
      return new Vector(1);
    }
    Vector<GEDIComment> allComments = new Vector();
    
    allComments = visit(doc, allComments);
    
    return allComments;
  }
  



  public Vector<GEDIComment> getAllComments()
  {
    File[] cmtList = getAllCMTFiles();
    if ((cmtList == null) || (cmtList.length == 0)) {
      return new Vector(1);
    }
    
    Document mainDom = createNewDocument();
    
    for (File f : cmtList) {
      NodeList nodeList = getDocument(f).getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node n = mainDom.importNode(nodeList.item(i), true);
        n = mainDom.adoptNode(n);
        mainDom.getDocumentElement().appendChild(n);
      }
    }
    

    if (mainDom == null) {
      return new Vector(1);
    }
    Vector<GEDIComment> allComments = new Vector();
    
    allComments = visit(mainDom, allComments);
    
    return allComments;
  }
  







  public Vector<GEDIComment> visit(Node node, Vector<GEDIComment> allComments)
  {
    if (node.getNodeName().equals(comment)) {
      allComments.add(getGEDIComment(node));
    }
    
    NodeList list = node.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      Node childNode = list.item(i);
      

      visit(childNode, allComments);
    }
    return allComments;
  }
  






  private GEDIComment getGEDIComment(Node node)
  {
    if (!node.getNodeName().equals(comment)) {
      return null;
    }
    String image = "";String xml = "";String pageNum = "";String zoneID = null;
    String date = "";String user = "";String archived = "";
    
    Node pageNode = null;
    if (node.getParentNode().getNodeName().equals(zone)) {
      zoneID = node.getParentNode().getAttributes().getNamedItem(idAtt).getTextContent();
      pageNode = node.getParentNode().getParentNode();
    }
    else if (node.getParentNode().getNodeName().equals(page)) {
      pageNode = node.getParentNode();
    }
    
    pageNum = pageNode.getAttributes().getNamedItem(idAtt).getTextContent();
    Node docNode = pageNode.getParentNode();
    xml = docNode.getAttributes().getNamedItem(xmlAtt).getTextContent();
    image = docNode.getAttributes().getNamedItem(imageAtt).getTextContent();
    
    date = node.getAttributes().getNamedItem(dateAtt).getTextContent();
    user = node.getAttributes().getNamedItem(userAtt).getTextContent();
    archived = node.getAttributes().getNamedItem(archivedAtt).getTextContent();
    
    GEDIComment c = new GEDIComment();
    c.setComment(node.getTextContent());
    c.setDate(date);
    c.setImage(image);
    c.setPageNum(pageNum);
    c.setUser(user);
    c.setXml(xml);
    c.setZoneID(zoneID);
    c.setArchived(archived);
    
    return c;
  }
  




  private void showCommentsAtBottom(Vector<GEDIComment> comments)
  {
    if (OCRInterface.this_interface.getCommentsWindow() != null) {
      OCRInterface.this_interface.setCommentsWindow(null);
    }
    JSplitPane rightSplitPane = OCRInterface.this_interface.getRightSplitPanel();
    rightSplitPane.setBottomComponent(null);
    
    JPanel pTop = new JPanel();
    

    pTop.setLayout(new BoxLayout(pTop, 1));
    CommentsDialog cd = new CommentsDialog(comments, this);
    cd.setFloatable(true);
    pTop.add(cd);
    

    JPanel pBottom = new JPanel();
    pBottom.setLayout(new BoxLayout(pBottom, 1));
    ElectronicTextDisplayer eTextWindow = this_interfacetbdPane.data_panel.a_window.eTextWindow;
    pBottom.add(eTextWindow);
    
    JSplitPane sp = new JSplitPane(0);
    sp.setOneTouchExpandable(true);
    sp.setTopComponent(pTop);
    sp.setBottomComponent(pBottom);
    sp.setResizeWeight(0.8D);
    sp.setDividerLocation(0.8D);
    
    rightSplitPane.setBottomComponent(sp);
    this_interfacerightSpliPanelDividerLocation = 0.5D;
    rightSplitPane.setResizeWeight(this_interfacerightSpliPanelDividerLocation);
    rightSplitPane.setDividerLocation(this_interfacerightSpliPanelDividerLocation);
    OCRInterface.this_interface.setCommentsWindow(cd);
  }
  



  private void showCommentsInSeparateDialog(Vector<GEDIComment> comments)
  {
    CommentsDialog openDialog = OCRInterface.this_interface.getCommentsWindow();
    
    final CommentsDialog cd = new CommentsDialog(comments, this);
    
    cd.setFloatable(false);
    JDialog dialog = new JDialog(OCRInterface.this_interface, "Commenting", false);
    
    dialog.setModal(false);
    dialog.toFront();
    dialog.getContentPane().add(cd);
    
    if (openDialog != null) {
      JDialog parent = (JDialog)openDialog.getTopLevelAncestor();
      dialog.setLocation(parent.getLocation());
      dialog.setSize(parent.getSize());
      parent.dispose();
      parent.setVisible(false);
    }
    else {
      setToCenter(dialog);
      dialog.pack();
    }
    

    dialog.setVisible(true);
    
    OCRInterface.this_interface.setCommentsWindow(cd);
    

    dialog.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        cd.setGlobalHotkeyManagerEnabled(true);
        OCRInterface.this_interface.setCommentsWindow(null);
      }
    });
  }
  


  private static String getCurrImage()
  {
    return ImageReaderDrawer.getFile_name();
  }
  



  public static String getCurrXml()
  {
    return OCRInterface.currDoc == null ? getCurrImage() + noXML : OCRInterface.currDoc.getFileName();
  }
  




  public static String getCurrPageNum()
  {
    return OCRInterface.this_interface.getMultiPagePanel().getSelectedPage();
  }
  


  private String getCurrUser()
  {
    return LoadUserProps.getInstance().getProperty("mostRecentUser");
  }
  


  public static String getCurrZoneID()
  {
    if ((ImageDisplay.activeZones == null) || (ImageDisplay.activeZones.isEmpty())) {
      return null;
    }
    if (ImageDisplay.activeZones.get(0) != null) {
      return ((Zone)ImageDisplay.activeZones.firstElement()).getAttributeValue("id");
    }
    return null;
  }
  



  private void setToCenter(JDialog dialog)
  {
    Dimension screenSize = OCRInterface.this_interface.getSize();
    int x = (width - getPreferredSizewidth) / 2;
    int y = (height - getPreferredSizeheight) / 2;
    
    dialog.setLocation(x, y);
  }
  




  public void setArchived(GEDIComment commObj)
  {
    Element el = getCommentElement(commObj.getFormattedDate(), 
      commObj.getUser(), 
      commObj.getComment());
    if (el == null) {
      System.out.println("Comment node not found.");
      return;
    }
    el.setAttribute(archivedAtt, commObj.getArchived());
    el.setTextContent(commObj.getComment().trim());
    writeXmlFile(dom);
  }
  
  public void setHasNewComments(boolean hasNewComments) {
    setHasNewComments(hasNewComments, getCurrCommentsFilePath());
  }
  








  public static void setHasNewComments(boolean hasNewComments, String xmlPath)
  {
    File commFileNew = new File(xmlPath + newExt);
    if (hasNewComments) {
      if (!commFileNew.exists()) {
        try {
          commFileNew.createNewFile();
        } catch (IOException e) { e.printStackTrace();
        }
      }
    }
    else if (commFileNew.exists()) {
      commFileNew.delete();
    }
    
    setMessageIconForFile(hasNewComments);
  }
  




  private static void setMessageIconForFile(boolean hasMessage)
  {
    if (WorkmodeTable.curRow == -1)
      return;
    OCRInterface ocrIF = OCRInterface.this_interface;
    
    FilePropPacket fpp = workmodeProps[0]
      .getElementFilePropVec(WorkmodeTable.curRow);
    
    int dataSet = 1;
    boolean updateTableRow = true;
    
    fpp.setNewComments(dataSet, hasMessage, updateTableRow);
  }
  




  public static boolean hasNewFileComments(String path)
  {
    String msgPath = path.substring(0, path.lastIndexOf('.')) + ext + newExt;
    return new File(msgPath).exists();
  }
}
