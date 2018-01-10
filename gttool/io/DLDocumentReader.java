package gttool.io;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.misc.Attribute;
import gttool.misc.AttributeValueList;
import gttool.misc.HexColor;
import gttool.misc.Login;
import gttool.misc.TypeAttributeEntry;
import gttool.misc.TypeAttributes;
import gttool.misc.TypeSettingEntry;
import gttool.misc.TypeSettings;
import java.awt.Point;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import ocr.gui.OCRInterface;
import ocr.gui.OrientedBox;
import ocr.tag.GetDateTime;
import ocr.util.AttributeConfigUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

class DLDocumentReader implements XMLReader
{
  private DLDocument document;
  private TypeSettings typeSettings;
  private Map<String, Attribute> attributeSettings;
  int indentingLevel = 0;
  
  ContentHandler handler;
  
  LexicalHandler lexicalHandler;
  
  HashMap<String, Object> properties = new HashMap();
  
  String namespaceUri = "http://lamp.cfar.umd.edu/media/projects/GEDI/";
  
  AttributesImpl atts = new AttributesImpl();
  












  public DLDocumentReader(DLDocument document, TypeSettings typeSettings, Map<String, Attribute> attributeSettings)
    throws SAXException
  {
    this.document = document;
    this.typeSettings = typeSettings;
    
    this.attributeSettings = attributeSettings;
  }
  





  public void parse(InputSource input)
    throws SAXException
  {
    if (handler == null) {
      throw new SAXException("No content handler is registered.");
    }
    
    lexicalHandler = 
      ((LexicalHandler)getProperty("http://xml.org/sax/properties/lexical-handler"));
    


    handler.startDocument();
    
    newLine();
    
    outputGeneralInformation();
    
    newLine();
    
    outputComment();
    
    newLine();
    
    outputRoot();
    
    handler.endDocument();
  }
  








  private void addAttribute(String name, String value)
  {
    if (value != null) {
      atts.addAttribute(namespaceUri, 
        name, name, "CDATA", value);
    }
  }
  

  private void outputGeneralInformation()
    throws SAXException
  {
    lexicalHandler.comment("GEDI was developed at Language and Media Processing Laboratory, University of Maryland."
      .toCharArray(), 0, "GEDI was developed at Language and Media Processing Laboratory, University of Maryland.".length());
  }
  







  private void outputComment()
    throws SAXException
  {
    String comments = OCRInterface.this_interface.getCommentText();
    if (comments.isEmpty()) {
      return;
    }
    String newLine = "\n";
    if (comments.charAt(0) != '\n')
      comments = newLine + newLine + comments;
    if (comments.charAt(comments.length() - 1) != '\n') {
      comments = comments + newLine;
    }
    lexicalHandler.comment(comments.toCharArray(), 0, comments.length());
  }
  





  private void outputRoot()
    throws SAXException
  {
    atts.clear();
    
    addAttribute(
      "GEDI_version", 
      XmlConstant.VALUE_GEDI_VERSION);
    
    addAttribute(
      "GEDI_date", 
      XmlConstant.VALUE_GEDI_DATE);
    
    handler.startElement(namespaceUri, 
      "GEDI", "GEDI", 
      atts);
    
    newLine();
    
    outputConfiguration(1);
    
    outputDocument(1);
    
    handler.endElement(namespaceUri, "GEDI", 
      "GEDI");
  }
  





  private void outputConfiguration(int indenting)
    throws SAXException
  {
    if ((typeSettings == null) || (typeSettings.size() == 0))
    {
      if ((attributeSettings == null) || (attributeSettings.size() == 0))
      {
        return; }
    }
    atts.clear();
    
    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "configuration", 
      "configuration", atts);
    
    newLine();
    
    if ((typeSettings != null) && (typeSettings.size() > 0)) {
      outputTypeSettings(indenting + 1);
    }
    


    if ((attributeSettings != null) && (attributeSettings.size() > 0)) {
      outputAttributeSettings(indenting + 1);
    }
    indenting(indenting);
    handler.endElement(namespaceUri, 
      "configuration", 
      "configuration");
    
    newLine();
  }
  



  private void outputTypeSettings(int indenting)
    throws SAXException
  {
    atts.clear();
    indenting(indenting);
    handler.startElement(namespaceUri, 
      "type_settings", 
      "type_settings", atts);
    
    for (Map.Entry<String, TypeSettingEntry> entry : typeSettings.entrySet()) {
      outputGediType(entry, indenting + 1);
    }
    
    newLine();
    indenting(indenting);
    handler.endElement(namespaceUri, 
      "type_settings", 
      "type_settings");
    
    newLine();
  }
  





  private void outputGediType(Map.Entry<String, TypeSettingEntry> typeSettingEntry, int indenting)
    throws SAXException
  {
    atts.clear();
    
    TypeSettingEntry entry = (TypeSettingEntry)typeSettingEntry.getValue();
    
    entry.remove("groupOf");
    
    for (String reservedAttribute : XmlConstant.Reserved_Attributes) {
      entry.remove(reservedAttribute);
    }
    addAttribute("gedi_type", (String)typeSettingEntry.getKey());
    if (!((String)typeSettingEntry.getKey()).equals("DL_PAGE")) {
      addAttribute("color", HexColor.toHexString(entry.getColor()));
      addAttribute("visible", Boolean.toString(entry.visible()));
      if (entry.onlySelectedVisible())
        addAttribute("visible", "onlySelected");
      if (haveToSave(entry.getShortCutKey())) {
        addAttribute("key", entry.getShortCutKey());
      }
      addAttribute("groupOf", entry.getGroupOf_AsString());
    }
    
    newLine();
    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "type_setting_entry", 
      "type_setting_entry", atts);
    

    for (TypeAttributeEntry typeAttribute : ((TypeSettingEntry)typeSettingEntry.getValue()).getTypeAttributes().values()) {
      outputTypeAttribute(typeAttribute, indenting + 1);
    }
    

    for (Map.Entry<String, Attribute> hotkeyEntry : ((TypeSettingEntry)typeSettingEntry.getValue()).getAttributeHotkeys().entrySet()) {
      outputAttributeHotkey((String)hotkeyEntry.getKey(), (Attribute)hotkeyEntry.getValue(), indenting + 1);
    }
    
    if ((((TypeSettingEntry)typeSettingEntry.getValue()).getTypeAttributes().size() > 0) || 
      (((TypeSettingEntry)typeSettingEntry.getValue()).getAttributeHotkeys().size() > 0)) {
      newLine();
      indenting(indenting);
    }
    else {
      space();
    }
    handler.endElement(namespaceUri, 
      "type_setting_entry", 
      "type_setting_entry");
  }
  




  private void outputTypeAttribute(TypeAttributeEntry typeAttribute, int indenting)
    throws SAXException
  {
    atts.clear();
    addAttribute("name", typeAttribute.getName());
    addAttribute("default", typeAttribute.getDefaultValue());
    if (haveToSave(typeAttribute.getPossibleValues()))
      addAttribute("possible_values", typeAttribute.getPossibleValues().toString());
    addAttribute("ArbitraryVal", typeAttribute.getArbVal());
    for (int i = 0; i < typeAttribute.getHotKey().size(); i++) {
      String hotKey = (String)typeAttribute.getHotKey().elementAt(i);
      if (hotKey != null)
      {
        if (hotKey.equals(ocr.gui.leftPanel.TypeWindow.NONE))
          hotKey = "";
        if (!hotKey.equals("none")) {
          addAttribute("key" + String.valueOf(i), hotKey);
        }
      }
    }
    





    newLine();
    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "type_attribute", 
      "type_attribute", atts);
    
    space();
    
    handler.endElement(namespaceUri, 
      "type_attribute", 
      "type_attribute");
  }
  





  private void outputAttributeHotkey(String hotkey, Attribute attribute, int indenting)
    throws SAXException
  {
    atts.clear();
    
    addAttribute("key", hotkey);
    addAttribute("attribute", attribute.getName());
    addAttribute("value", attribute.getValue());
    
    newLine();
    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "attribute_hotkey", 
      "attribute_hotkey", atts);
    
    space();
    
    handler.endElement(namespaceUri, 
      "attribute_hotkey", 
      "attribute_hotkey");
  }
  






























































  private void outputAttributeSettings(int indenting)
    throws SAXException
  {
    atts.clear();
    indenting(indenting);
    handler.startElement(namespaceUri, 
      "attribute_settings", 
      "attribute_settings", atts);
    
    newLine();
    

    Iterator localIterator = attributeSettings.entrySet().iterator();
    while (localIterator.hasNext()) {
      Map.Entry<String, Attribute> entry = (Map.Entry)localIterator.next();
      outputAttribute(entry, indenting + 1);
    }
    
    indenting(indenting);
    handler.endElement(namespaceUri, 
      "attribute_settings", 
      "attribute_settings");
    
    newLine();
  }
  







  private void outputAttribute(Map.Entry<String, Attribute> entry, int indenting)
    throws SAXException
  {
    atts.clear();
    
    addAttribute("name", ((Attribute)entry.getValue()).getName());
    addAttribute("value", ((Attribute)entry.getValue()).getValue());
    

    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "attribute", 
      "attribute", atts);
    
    space();
    
    handler.endElement(namespaceUri, 
      "attribute", 
      "attribute");
    
    newLine();
  }
  



  private void outputDocument(int indenting)
    throws SAXException
  {
    if (document == null) {
      return;
    }
    
    outputUserName(1);
    
    atts.clear();
    if ((document.documentID != null) && (document.imageSrc.length() > 0)) {
      addAttribute("docID", document.documentID);
    }
    
    if (document.imageSrc != null) {
      addAttribute("src", document.imageSrc);
    }
    for (Map.Entry<String, String> entry : document.documentTags.entrySet())
    {
      addAttribute((String)entry.getKey(), (String)entry.getValue());
    }
    
    indenting(indenting);
    
    handler.startElement(namespaceUri, 
      "DL_DOCUMENT", 
      "DL_DOCUMENT", atts);
    
    newLine();
    
    for (DLPage page : document.documentPages) {
      outputPage(page, indenting + 1);
    }
    
    indenting(indenting);
    
    handler.endElement(namespaceUri, 
      "DL_DOCUMENT", 
      "DL_DOCUMENT");
    
    newLine();
  }
  





  private void outputPage(DLPage page, int indenting)
    throws SAXException
  {
    atts.clear();
    
    String pageType = (String)pageTags.get("gedi_type");
    
    if (pageType == null) {
      pageType = "DL_ZONE";
    }
    addAttribute("gedi_type", pageType);
    
    if ((imageSrc != null) && (imageSrc.length() > 0)) {
      addAttribute("src", imageSrc);
    }
    addAttribute("pageID", pageID);
    addAttribute("width", page.dlGetWidth());
    addAttribute("height", page.dlGetHeight());
    
    for (Map.Entry<String, String> entry : pageTags.entrySet()) {
      if (!((String)entry.getKey()).equals("gedi_type"))
      {

        addAttribute((String)entry.getKey(), (String)entry.getValue());
      }
    }
    indenting(indenting);
    handler.startElement(namespaceUri, "DL_PAGE", 
      "DL_PAGE", atts);
    
    if (page.dlHasZones()) {
      newLine();
    }
    for (DLZone zone : pageZones) {
      outputZone(zone, 3);
    }
    
    indenting(2);
    handler.endElement(namespaceUri, "DL_PAGE", 
      "DL_PAGE");
    
    newLine();
  }
  






  private void outputZone(DLZone zone, int level)
    throws SAXException
  {
    String zoneType = null;
    
    atts.clear();
    
    zoneType = zone.getAttributeValue("gedi_type").trim();
    
    cleanUpZoneTags(zone);
    


    if ((zoneType == null) || (zoneType.equals(""))) {
      return;
    }
    
    String zoneId = zone.getAttributeValue("id");
    


    zone.removeAttribute("groupOf");
    
    addAttribute("gedi_type", zoneType);
    addAttribute("id", zoneId);
    if (nextZone != null) {
      addAttribute("nextZoneID", nextZone.zoneID);
    }
    if (zone.getGroupElements_asString() != null) {
      addAttribute("elements", zone.getGroupElements_asString());
    }
    

    if (!(zone instanceof ocr.gui.PolygonZone)) {
      addAttribute("col", Integer.toString(dlGetZoneOriginx));
      addAttribute("row", Integer.toString(dlGetZoneOriginy));
      addAttribute("width", Integer.toString(zone.dlGetZoneWidth()));
      addAttribute("height", Integer.toString(zone.dlGetZoneHeight()));
    }
    

    for (Map.Entry<String, String> entry : zone.getZoneTags().entrySet()) {
      if (!((String)entry.getKey()).equals("gedi_type"))
      {
        String key = (String)entry.getKey();
        key = key.replace(' ', '_');
        if (key == "orientation") {
          key = "orientationD";
          


          String tempVal = OrientedBox.getDegrees(Double.parseDouble(((String)entry.getValue()).trim()));
          addAttribute(key, tempVal);
        } else {
          String value = (String)entry.getValue();
          if (value == null)
            value = "";
          addAttribute(key, value.trim());
        }
      }
    }
    
    indenting(level);
    
    handler.startElement(namespaceUri, "DL_ZONE", "DL_ZONE", atts);
    
    if (zone.dlHasChildZones()) {
      newLine();
      for (DLZone childZone : childZones) {
        outputZone(childZone, level + 1);
      }
    }
    



    if (zone.dlHasChildZones())
    {
      indenting(level);
    }
    else {
      space();
    }
    handler.endElement(namespaceUri, "DL_ZONE", "DL_ZONE");
    newLine();
  }
  



  private void space()
    throws SAXException
  {
    handler.ignorableWhitespace(XmlConstant.SPACE, 0, 
      XmlConstant.SPACE.length);
  }
  



  private void newLine()
    throws SAXException
  {
    handler.ignorableWhitespace(XmlConstant.NEW_LINE, 0, 
      XmlConstant.NEW_LINE.length);
  }
  




  private void indenting(int level)
    throws SAXException
  {
    StringBuilder tab = new StringBuilder("\t".length() * 
      level);
    while (level-- > 0) {
      tab.append("\t");
    }
    handler.ignorableWhitespace(tab.toString().toCharArray(), 0, 
    
      tab.toString().toCharArray().length);
  }
  
  private static boolean haveToSave(String value)
  {
    return (value != null) && (value.length() > 0);
  }
  
  private static boolean haveToSave(Object value) {
    return value != null;
  }
  


  public boolean getFeature(String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return false;
  }
  



  public void setFeature(String name, boolean value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {}
  


  public Object getProperty(String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return properties.get(name);
  }
  


  public void setProperty(String name, Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    properties.put(name, value);
  }
  



  public void setEntityResolver(EntityResolver resolver) {}
  



  public EntityResolver getEntityResolver()
  {
    return null;
  }
  



  public void setDTDHandler(DTDHandler handler) {}
  



  public DTDHandler getDTDHandler()
  {
    return null;
  }
  


  public void setContentHandler(ContentHandler contentHandler)
  {
    handler = contentHandler;
  }
  


  public ContentHandler getContentHandler()
  {
    return handler;
  }
  



  public void setErrorHandler(ErrorHandler errorHandler) {}
  



  public ErrorHandler getErrorHandler()
  {
    return null;
  }
  

  public void parse(String systemId)
    throws IOException, SAXException
  {
    parse(new InputSource(systemId));
  }
  






  private void outputUserName(int indenting)
    throws SAXException
  {
    atts.clear();
    
    String date = GetDateTime.getCurrentDateTime();
    

    if (Login.userListTags.size() == 0) {
      Login.userListTags.put(Login.userName, date);
    }
    
    for (Map.Entry<String, String> entry : Login.userListTags.entrySet()) {
      addAttribute("name", (String)entry.getKey());
      addAttribute("date", (String)entry.getValue());
      addAttribute("dateFormat", "mm/dd/yyyy hh:mm");
      
      indenting(indenting);
      
      handler.startElement(namespaceUri, 
        "USER", 
        "USER", atts);
      
      indenting(indenting);
      
      handler.endElement(namespaceUri, 
        "USER", 
        "USER");
      
      newLine();
    }
  }
  






  private void cleanUpZoneTags(DLZone zone)
  {
    String zoneType = zone.getAttributeValue("gedi_type").trim();
    
    if (OCRInterface.getAttsConfigUtil().getTypeAttributes(zoneType) == null) {
      System.out.println("unrecognized zoneType: \"" + zoneType + "\" zoneId: " + zone.getZoneId());
      return;
    }
    

    Iterator localIterator = OCRInterface.getAttsConfigUtil().getTypeAttributes(zoneType).entrySet().iterator();
    while (localIterator.hasNext()) {
      Map.Entry<String, TypeAttributeEntry> e = (Map.Entry)localIterator.next();
      String att = ((String)e.getKey()).trim();
      String defaultValue = ((TypeAttributeEntry)e.getValue()).getDefaultValue();
      
      String attValue = (String)zone.getZoneTags().get(att);
      

      if ((attValue == null) || (attValue.trim().isEmpty()))
      {

        if ((attValue != null) && (attValue.trim().isEmpty())) {
          zone.removeAttribute(att);
        }
        if ((defaultValue != null) && (!defaultValue.trim().isEmpty())) {
          zone.setAttributeValue(att, defaultValue);
        }
      }
    }
  }
}
