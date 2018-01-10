package gttool.io;

import gttool.document.DLDocument;
import gttool.document.DLPage;
import gttool.document.DLZone;
import gttool.misc.Attribute;
import gttool.misc.TypeSettings;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;









public class DLDocumentXmlParser
{
  private DLDocument document = null;
  


  private Map<String, Attribute> attributeSettings = null;
  
  private TypeSettings typeSettings = null;
  



  public DLDocumentXmlParser() {}
  



  public void parse(File xmlFile)
    throws IOException, SAXException
  {
    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      SAXParser parser = factory.newSAXParser();
      


      DLDocumentXmlHandler documentHandler = new DLDocumentXmlHandler();
      

      InputSource inputSource = new InputSource(new BufferedInputStream(
        new FileInputStream(xmlFile)));
      inputSource.setSystemId(xmlFile.getAbsolutePath());
      
      parser.parse(inputSource, documentHandler);
      






      document = document;
      
      attributeSettings = attributesSettings;
      typeSettings = typeSettings;
    }
    catch (ParserConfigurationException e) {
      System.out.println("The underlying parser doesn't support the requested features.");
      e.printStackTrace(System.out);
    }
    catch (FactoryConfigurationError e) {
      System.out.println("Error occured while obtaining SAXParserFactory instance.");
      e.printStackTrace(System.out);
    } catch (SAXException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    }
  }
  








  public void parse(String xmlURI)
    throws IOException, SAXException
  {
    parse(new File(xmlURI));
  }
  



  public DLDocument getDocument()
  {
    return document;
  }
  



  public Map<String, Attribute> getAttributeSettings()
  {
    return attributeSettings;
  }
  



  public TypeSettings getTypeSettings()
  {
    return typeSettings;
  }
  




  public static void main(String[] args)
    throws Exception
  {
    try
    {
      DLDocumentXmlParser parser = new DLDocumentXmlParser();
      parser.parse(args[0]);
      document = parser.getDocument();
    } catch (Exception e) { DLDocument document;
      System.out.println(e);
      System.out.println(e.getCause());
      throw e;
    }
    DLDocument document;
    for (DLPage page : documentPages) {
      System.out.printf("<DL_PAGE pageID=\"%s\">\n", new Object[] { pageID });
      
      printPage(page);
    }
  }
  
  public static void printPage(DLPage page)
  {
    for (DLZone zone : pageZones) {
      traverseZone(zone, 1);
    }
  }
  




  private static void traverseZone(DLZone zone, int level)
  {
    for (int i = 0; i < level; i++) {
      System.out.print('\t');
    }
    System.out.printf(
      "<%s id=\"%s\" width=\"%d\" height=\"%d\" y=\"%d\" x=\"%d\"", new Object[] {
      zone.getAttributeValue("type"), zoneID, Integer.valueOf(zone.dlGetZoneWidth()), 
      Integer.valueOf(zone.dlGetZoneHeight()), Integer.valueOf(dlGetZoneOriginy), 
      Integer.valueOf(dlGetZoneOriginx) });
    for (Map.Entry<String, String> entry : zone.getZoneTags().entrySet()) {
      System.out.printf(" %s=\"%s\"", new Object[] { ((String)entry.getKey()).replace(' ', '_'), entry
        .getValue() });
    }
    System.out.println("/>");
    
    if (zone.dlHasChildZones()) {
      for (DLZone childZone : childZones) {
        traverseZone(childZone, level + 1);
      }
    }
  }
}
