package gttool.io;

import gttool.document.DLDocument;
import gttool.misc.Attribute;
import gttool.misc.TypeSettings;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
























public class DLDocumentXmlTransformer
{
  public DLDocumentXmlTransformer() {}
  
  public void writeDocument(DLDocument documentSource, TypeSettings typeSettings, Map<String, Attribute> attributeSettings, File xmlFileDest)
    throws IOException, SAXException
  {
    StreamResult result = null;
    
    try
    {
      TransformerFactory factory = TransformerFactory.newInstance();
      


      DLDocumentReader reader = new DLDocumentReader(documentSource, typeSettings, attributeSettings);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", 
        new DefaultHandler2());
      

      Transformer transformer = factory.newTransformer();
      

      SAXSource source = new SAXSource(reader, new InputSource());
      

      result = new StreamResult(new BufferedOutputStream(
        new FileOutputStream(xmlFileDest)));
      transformer.transform(source, result);
    }
    catch (TransformerConfigurationException e) {
      throw new SAXException("Cannot configure a xml transformer.", e);
    }
    catch (TransformerException e) {
      e.printStackTrace();
      throw new SAXException("Error occured while transforming a page to xml file.", e);
    } finally {
      if (result != null) {
        result.getOutputStream().close();
      }
    }
  }
  





  public void writeDocument(DLDocument documentSource, File xmlFileDest)
    throws IOException, SAXException
  {
    writeDocument(documentSource, null, null, xmlFileDest);
  }
  














  public void writeDocument(DLDocument documentSource, TypeSettings typeSettings, Map<String, Attribute> attributeSettings, String xmlFileDestPath)
    throws IOException, SAXException
  {
    writeDocument(documentSource, typeSettings, attributeSettings, new File(xmlFileDestPath));
  }
  






  public void writeDocument(DLDocument documentSource, String xmlFileDestPath)
    throws IOException, SAXException
  {
    writeDocument(documentSource, null, null, xmlFileDestPath);
  }
  




  public static void main(String[] args)
  {
    TypeSettings typeSettings = null;
    
    Map<String, Attribute> attributeSettings = null;
    try
    {
      DLDocumentXmlParser parser = new DLDocumentXmlParser();
      
      parser.parse(args[0]);
      
      DLDocument document = parser.getDocument();
      typeSettings = parser.getTypeSettings();
      
      attributeSettings = parser.getAttributeSettings();
      
      DLDocumentXmlTransformer transformer = new DLDocumentXmlTransformer();
      transformer.writeDocument(document, typeSettings, attributeSettings, args[1]);
      
      System.out.printf("Reading '%s'...", new Object[] { args[0] });
      System.out.println();
      outputFile(args[0]);
      System.out.println();
      System.out.println("------------------------------------------------------------------------------");
      System.out.println();
      System.out.printf("Writing '%s'...", new Object[] { args[1] });
      System.out.println();
      outputFile(args[1]);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }
  
  private static void outputFile(String fileName)
    throws IOException
  {
    char[] buffer = new char['ᎈ'];
    FileReader fout = null;
    int charRead;
    try { fout = new FileReader(fileName);
      int charRead;
      int i; int charRead; for (; (charRead = fout.read(buffer)) >= 0; 
          i < charRead) { i = 0; continue;
        System.out.print(buffer[i]);i++;
      }
    }
    finally {
      if (fout != null) {
        fout.close();
      }
    }
  }
}
