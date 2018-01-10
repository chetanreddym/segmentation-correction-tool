package gttool.io;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.exceptions.DLExceptionCodes;
import gttool.exceptions.DLXmlException;
import gttool.misc.Attribute;
import gttool.misc.Login;
import gttool.misc.TypeAttributeEntry;
import gttool.misc.TypeAttributes;
import gttool.misc.TypeSettingEntry;
import gttool.misc.TypeSettings;
import java.awt.Point;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import ocr.gui.Group;
import ocr.gui.OrientedBox;
import ocr.gui.PolygonZone;
import ocr.gui.Zone;
import ocr.tag.GetDateTime;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;




class DLDocumentXmlHandler
  extends DefaultHandler
{
  private DLPage currentPage;
  private boolean erroneousZoneEncountered = false;
  
  private boolean processingDocument = false;
  
  private boolean processingPage = false;
  
  private boolean processingConfiguration = false;
  


  private boolean processingAttributeSettings = false;
  
  private boolean processingTypeSettings = false;
  
  private TypeSettingEntry currentTypeSettingEntry = null;
  
  private int numOfDocument = 0;
  

  private DLZone currentZone = null;
  

  private Locator locator;
  

  private Map<String, String> namespaceMappings = new HashMap();
  


  TypeSettings typeSettings = null;
  
  Map<String, Attribute> attributesSettings = null;
  

  DLDocument document;
  


  public DLDocumentXmlHandler() {}
  


  public void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
  }
  



  public void startDocument()
    throws SAXException
  {}
  



  public void endDocument()
    throws SAXException
  {}
  



  public void processingInstruction(String target, String data)
    throws SAXException
  {}
  



  public void startPrefixMapping(String prefix, String uri)
    throws SAXException
  {
    super.startPrefixMapping(prefix, uri);
    namespaceMappings.put(uri, prefix);
  }
  




  public void endPrefixMapping(String prefix)
  {
    for (Iterator i = namespaceMappings.keySet().iterator(); i.hasNext();) {
      String uri = (String)i.next();
      String thisPrefix = (String)namespaceMappings.get(uri);
      if (prefix.equals(thisPrefix)) {
        namespaceMappings.remove(uri);
        break;
      }
    }
  }
  





  public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    throws SAXException
  {
    if ((localName == null) || (localName.trim().length() == 0)) {
      localName = qName;
    }
    if (localName.equalsIgnoreCase("configuration")) {
      startConfiguration();
    } else if (localName.equalsIgnoreCase("type_settings")) {
      startTypeSettings();
    } else if (localName.equalsIgnoreCase("type_attribute")) {
      startTypeAttribute(namespaceURI, localName, qName, atts);
    } else if (localName.equalsIgnoreCase("attribute_hotkey")) {
      startAttributeHotkey(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("attribute_settings")) {
      startAttributeSettings();
    } else if (localName.equalsIgnoreCase("type_setting_entry")) {
      startGediType(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("attribute")) {
      startAttribute(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("DL_DOCUMENT")) {
      startDLDocument(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("DL_PAGE")) {
      startDLPage(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("DL_ZONE")) {
      startDLZone(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("USER")) {
      startUserName(namespaceURI, localName, qName, atts);
    }
    else if (localName.equalsIgnoreCase("GEDI")) {
      startGEDI(namespaceURI, localName, qName, atts);
    }
  }
  



  public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException
  {
    if ((localName == null) || (localName.trim().length() == 0)) {
      localName = qName;
    }
    if (localName.equalsIgnoreCase("configuration")) {
      endConfiguration();
    } else if (localName.equalsIgnoreCase("type_settings")) {
      endTypeSettings();
    } else if (localName.equalsIgnoreCase("type_attribute")) {
      endTypeAttribute();
    } else if (localName.equalsIgnoreCase("attribute_hotkey")) {
      endAttributeHotkey();
    }
    else if (localName.equalsIgnoreCase("attribute_settings")) {
      endAttributeSettings();
    } else if (localName.equalsIgnoreCase("type_setting_entry")) {
      endGediType();
    }
    else if (localName.equalsIgnoreCase("attribute")) {
      endAttribute();
    }
    else if (localName.equalsIgnoreCase("DL_DOCUMENT")) {
      endDLDocument();
    }
    else if (localName.equalsIgnoreCase("DL_PAGE")) {
      endDLPage();
    }
    else if (localName.equalsIgnoreCase("DL_ZONE")) {
      endDLZone();
    }
    else if (localName.equalsIgnoreCase("USER")) {
      endUserName();
    }
    else if (localName.equalsIgnoreCase("GEDI")) {
      endGEDI();
    }
  }
  














  public void characters(char[] ch, int start, int length)
    throws SAXException
  {}
  














  public void skippedEntity(String name)
    throws SAXException
  {
    throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
      "Error in the xml file. '" + name + "' cannot be processed." + 
      " Line: " + locator.getLineNumber());
  }
  





  public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException
  {}
  




  private void startConfiguration()
    throws DLXmlException
  {
    processingConfiguration = true;
  }
  


  private void endConfiguration()
    throws DLXmlException
  {
    if (processingConfiguration) {
      processingConfiguration = false;
    }
  }
  







  private void startTypeSettings()
    throws DLXmlException
  {
    typeSettings = new TypeSettings();
    processingTypeSettings = true;
  }
  


  private void endTypeSettings()
    throws DLXmlException
  {
    if ((processingConfiguration) && (processingTypeSettings)) {
      processingTypeSettings = false;
    }
  }
  









  private void startGediType(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if ((processingConfiguration) && (processingTypeSettings)) {
      String typeName = atts.getValue("gedi_type");
      


      if ((typeName != null) && (typeName.length() > 0)) {
        typeSettings.put(typeName, this.currentTypeSettingEntry = new TypeSettingEntry());
        currentTypeSettingEntry.setColor(atts.getValue("color"));
        currentTypeSettingEntry.setShortCutKey(atts.getValue("key"));
        currentTypeSettingEntry.visible(atts.getValue("visible"));
        if (!typeName.equals("DL_PAGE")) {
          currentTypeSettingEntry.setGroupOf(atts.getValue("groupOf"));
        }
      }
      else {
        throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
          "<type_setting_entry> must have gedi_type attribute. Line: " + 
          
          locator.getLineNumber());
      }
    } else {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "type_setting_entry must be in type settings section of configuration section. Line: " + 
        
        locator.getLineNumber());
    }
  }
  


  private void endGediType()
    throws DLXmlException
  {
    if ((!processingTypeSettings) || (!processingConfiguration)) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "type_setting_entry must be in type settings section of configuration section. Line: " + 
        
        locator.getLineNumber());
    }
    currentTypeSettingEntry = null;
  }
  








  private void startTypeAttribute(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if (currentTypeSettingEntry == null) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<type_attribute> must be in between <type_setting_entry> and </type_setting_entry>. Line: " + 
        
        locator.getLineNumber());
    }
    String name = atts.getValue("name");
    String defaultValue = atts.getValue("default");
    String possibleValues = atts.getValue("possible_values");
    String aval = atts.getValue("ArbitraryVal");
    String k0 = atts.getValue("key0");
    String k1 = atts.getValue("key1");
    String k2 = atts.getValue("key2");
    String k3 = atts.getValue("key3");
    String k4 = atts.getValue("key4");
    String k5 = atts.getValue("key5");
    String k6 = atts.getValue("key6");
    String k7 = atts.getValue("key7");
    String k8 = atts.getValue("key8");
    String k9 = atts.getValue("key9");
    

    TypeAttributeEntry typeAttribute = new TypeAttributeEntry(name, defaultValue, possibleValues, aval, k0, k1, 
      k2, k3, k4, k5, k6, k7, k8, k9);
    currentTypeSettingEntry.getTypeAttributes().put(name, typeAttribute);
  }
  


  private void endTypeAttribute()
    throws DLXmlException
  {
    if (currentTypeSettingEntry == null) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<type_attribute> must be in between <type_setting_entry> and </type_setting_entry>. Line: " + 
        
        locator.getLineNumber());
    }
  }
  







  private void startAttributeHotkey(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if (currentTypeSettingEntry == null) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<attribute_hotkey> must be in between <type_setting_entry> and </type_setting_entry>. Line: " + 
        
        locator.getLineNumber());
    }
    String hotkey = atts.getValue("key");
    String attributeName = atts.getValue("attribute");
    String value = atts.getValue("value");
    
    currentTypeSettingEntry.getAttributeHotkeys().put(hotkey, new Attribute(attributeName, value));
  }
  


  private void endAttributeHotkey()
    throws DLXmlException
  {
    if (currentTypeSettingEntry == null)
    {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<attribute_hotkey> must be in between <type_setting_entry> and </type_setting_entry>. Line: " + 
        
        locator.getLineNumber());
    }
  }
  



  private void startAttributeSettings()
    throws DLXmlException
  {
    if (!processingConfiguration) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<attribute_settings> must be in the <configuration> section. Line: " + 
        locator.getLineNumber());
    }
    attributesSettings = new HashMap();
    processingAttributeSettings = true;
  }
  


  private void endAttributeSettings()
    throws DLXmlException
  {
    if ((!processingConfiguration) || (!processingAttributeSettings)) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "<attribute_settings> must be in the <configuration> section. Line: " + 
        locator.getLineNumber());
    }
    processingAttributeSettings = false;
  }
  








  private void startAttribute(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if (processingAttributeSettings) {
      String name = atts.getValue("name");
      String value = atts.getValue("value");
      








      attributesSettings.put(name, new Attribute(name, 
        value));
    } else {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "Attribute property list must be in configuration section. Line: " + 
        locator.getLineNumber());
    }
  }
  


  private void endAttribute()
    throws DLXmlException
  {
    if (processingConfiguration) {}
  }
  










  private void startDLDocument(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if ((processingDocument) || (numOfDocument > 0))
    {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "A page file cannot have more than one document. Line: " + 
        locator.getLineNumber());
    }
    
    processingDocument = true;
    numOfDocument += 1;
    
    String documentID = atts.getValue("docID");
    String imageName = atts.getValue("src");
    document = new DLDocument();
    
    document.documentID = documentID;
    







    document.imageSrc = imageName;
    
    for (int i = 0; i < atts.getLength(); i++) {
      document.documentTags.put(atts.getLocalName(i), atts
        .getValue(i));
    }
    document.documentTags.remove("docID");
    document.documentTags.remove("src");
  }
  


  private void endDLDocument()
    throws DLXmlException
  {
    if (!processingDocument)
    {
      throw new DLXmlException(
        DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "Error in the xml file. Organization of the file is corrupt. Line: " + 
        locator.getLineNumber());
    }
    erroneousZoneEncountered = false;
    processingDocument = false;
  }
  







  private void startDLPage(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    if (!processingDocument) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "DL_PAGE must belong to DL_DOCUMENT. Line: " + 
        locator.getLineNumber());
    }
    
    if (processingPage)
    {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "DLPage cannot contain DLPage. Line: " + 
        locator.getLineNumber());
    }
    
    processingPage = true;
    
    String pageID = atts.getValue("pageID");
    
    if (pageID != null) {
      currentPage = new DLPage(pageID);
      try {
        document.dlAppendPage(currentPage);
      }
      catch (Exception localException) {}
    }
    else {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "DL_Page should have pageID attribute. Line: " + 
        
        locator.getLineNumber());
    }
    
    String imageSrc = atts.getValue("src");
    currentPage.imageSrc = imageSrc;
    
    String strWidth = atts.getValue("width");
    String strHeight = atts.getValue("height");
    try
    {
      if (strWidth != null) {
        currentPage.dlSetWidth(Integer.parseInt(strWidth));
      } else
        currentPage.dlSetWidth(0);
      if (strHeight != null) {
        currentPage.dlSetHeight(Integer.parseInt(strHeight));
      } else
        currentPage.dlSetHeight(0);
    } catch (NumberFormatException nfe) {
      throw new DLXmlException(DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "Width and height of a page should be integer value. Line: " + 
        locator.getLineNumber(), nfe);
    }
    
    for (int i = 0; i < atts.getLength(); i++) {
      currentPage.pageTags.put(atts.getLocalName(i), atts
        .getValue(i));
    }
    currentPage.pageTags.remove("width");
    currentPage.pageTags.remove("height");
    currentPage.pageTags.remove("src");
    currentPage.pageTags.remove("pageID");
  }
  


  private void endDLPage()
    throws DLXmlException
  {
    if ((!processingDocument) || (!processingPage))
    {
      throw new DLXmlException(
        DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "Error in the xml file. Organization of the file is corrupt. Line: " + 
        locator.getLineNumber());
    }
    processingPage = false;
  }
  









  private void startDLZone(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    int x = 0;int y = 0;int w = 0;int h = 0;
    Double orientation = Double.valueOf(0.0D);
    Vector<Point> polygon = null;
    String groupElements = null;
    String groupOf = null;
    String orientationInDegrees = null;
    
    if (!processingPage) {
      return;
    }
    String zoneID = atts.getValue("id");
    groupOf = atts.getValue("groupOf");
    groupElements = atts.getValue("elements");
    if (zoneID == null) {
      if (!erroneousZoneEncountered)
        JOptionPane.showMessageDialog(null, 
          "Errors Found Reading XML file.\nFirst line: " + locator.getLineNumber(), 
          "Error Reading Data File", 2);
      erroneousZoneEncountered = true;
      return;
    }
    



    try
    {
      try
      {
        polygon = parsePolygon(atts.getValue("polygon"));
      } catch (NullPointerException npe) {
        System.out.println("polygon is null");
      }
      
      if (polygon == null) {
        x = Integer.parseInt(atts.getValue("col"));
        y = Integer.parseInt(atts.getValue("row"));
        w = Integer.parseInt(atts.getValue("width"));
        h = Integer.parseInt(atts.getValue("height"));
      }
      try
      {
        orientation = Double.valueOf(Double.parseDouble(atts.getValue("orientation")));
        orientationInDegrees = OrientedBox.getDegrees(orientation.doubleValue());
      } catch (NullPointerException npe) {
        try {
          orientation = Double.valueOf(Math.toRadians(Double.parseDouble(atts.getValue("orientationD"))));
        } catch (NullPointerException npe_deg) {
          orientation = null;
        }
      }
      
















      if (currentZone != null) {
        break label547;
      }
    }
    catch (NumberFormatException nfe)
    {
      if (!erroneousZoneEncountered)
        JOptionPane.showMessageDialog(null, 
          "Errors Found Reading XML file.\nFirst line: " + locator.getLineNumber(), 
          "Error Reading Data File", 2);
      erroneousZoneEncountered = true;
      return;
    }
    catch (NullPointerException npe) {
      System.out.println("something is null");
    }
    




    if (orientation != null) {
      Point origin = new Point(x, y);
      currentZone = new OrientedBox(zoneID, origin, w, h, orientation.doubleValue());
    }
    else if (polygon != null)
    {
      Point origin = new Point(x, y);
      currentZone = new PolygonZone(zoneID, origin, w, h, polygon);
    }
    else if ((groupOf != null) && (!groupOf.isEmpty())) {
      currentZone = new Group(zoneID, x, y, w, h);
    }
    else if ((groupElements != null) && (!groupElements.isEmpty())) {
      currentZone = new Group(zoneID, x, y, w, h);
    }
    else {
      currentZone = new Zone(zoneID, x, y, w, h); }
    currentPage.dlAppendZoneWithoutCheck(currentZone);
    break label583;
    label547:
    DLZone parentZone = currentZone;
    currentZone = new Zone(zoneID, x, y, w, h);
    parentZone.dlAppendChildZoneWithoutCheck(currentZone);
    
    label583:
    
    for (int i = 0; i < atts.getLength(); i++) {
      String attName = atts.getLocalName(i);
      if ((attName == null) || (attName.trim().equals("")))
        attName = atts.getQName(i);
      currentZone.setAttributeValue(attName, atts.getValue(i));
    }
    






    if ((groupOf != null) && (!groupOf.isEmpty())) {
      currentZone.setAttributeValue("elements", groupOf);
    }
    currentZone.removeAttribute("groupOf");
    

    String nextZoneID = atts.getValue("nextZoneID");
    
    if (nextZoneID != null) {
      currentZone.setAttributeValue("nextZoneID", nextZoneID);
    }
    






    if (orientationInDegrees != null) {
      currentZone.setAttributeValue("orientationD", orientationInDegrees);
      currentZone.removeAttribute("orientation");
    }
    
    currentZone.removeAttribute("col");
    currentZone.removeAttribute("row");
    currentZone.removeAttribute("width");
    currentZone.removeAttribute("height");
  }
  
  private Vector<Point> parsePolygon(String str)
  {
    if ((str == "") || (str == null)) { return null;
    }
    str = str.replace("(", "");
    str = str.replace(")", "");
    
    String[] coords = str.split(";");
    Vector<Point> tempVec = new Vector();
    
    for (int i = 0; i < coords.length; i++)
    {
      String[] xy = coords[i].split(",");
      tempVec.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
    }
    
    if (tempVec.size() == 0) {
      return null;
    }
    return tempVec;
  }
  

  private void endDLZone()
    throws DLXmlException
  {
    if ((erroneousZoneEncountered) && (currentZone == null)) {
      return;
    }
    if ((!processingDocument) || (!processingPage) || (currentZone == null))
    {
      throw new DLXmlException(
        DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
        "Error in the xml file. Organization of the file is corrupt. Line: " + 
        locator.getLineNumber());
    }
    currentZone = currentZone.dlGetParentZone();
  }
  












  private void startUserName(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    String userName = atts.getValue("name");
    String userDate = atts.getValue("date");
    












    Login.userListTags.put(userName, userDate);
    
    TreeMap<String, String> userList = (TreeMap)Login.userListTags.clone();
    
    String date = GetDateTime.getCurrentDateTime();
    

    if (userList.containsValue(Login.userName)) {
      Login.userListTags.remove(Login.userName);
      Login.userListTags.put(Login.userName, date);
    } else {
      Login.userListTags.put(Login.userName, date);
    }
  }
  



















  private void endUserName()
    throws DLXmlException
  {}
  



















  private void startGEDI(String namespaceURI, String localName, String qName, Attributes atts)
    throws DLXmlException
  {
    Login.userListTags.clear();
  }
  
  private void endGEDI()
    throws DLXmlException
  {}
}
