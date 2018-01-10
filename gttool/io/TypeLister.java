package gttool.io;

import gttool.exceptions.DLExceptionCodes;
import gttool.exceptions.DLXmlException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;















public class TypeLister
{
  public TypeLister() {}
  
  class TypeListingHandler
    extends DefaultHandler
  {
    boolean processingDocument = false;
    
    boolean processingPage = false;
    

    TypeListingHandler() {}
    

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      throws SAXException
    {
      if ((localName == null) || (localName.trim().length() == 0)) {
        localName = qName;
      }
      if (localName.equalsIgnoreCase("DL_DOCUMENT")) {
        if (processingDocument)
        {
          throw new DLXmlException(
            DLExceptionCodes.DL_WRONG_XML_PAGE_FORMAT, 
            "A page file cannot have more than one document.");
        }
        processingDocument = true;
      } else if ((localName.equalsIgnoreCase("DL_PAGE")) && 
        (processingDocument)) {
        processingPage = true;
      } else if ((localName.equalsIgnoreCase("DL_ZONE")) && 
        (processingPage)) {
        startDLZone(namespaceURI, localName, qName, atts);
      }
    }
    


    public void endElement(String namespaceURI, String localName, String qName)
      throws SAXException
    {
      if ((localName == null) || (localName.trim().length() == 0)) {
        localName = qName;
      }
      else if (localName.equalsIgnoreCase("DL_PAGE")) {
        processingPage = false;
      }
    }
    








    private void startDLZone(String namespaceURI, String localName, String qName, Attributes atts)
      throws DLXmlException
    {
      String typeName = atts.getValue("gedi_type");
      
      if ((typeName == null) || (typeName.trim().length() == 0)) {
        return;
      }
      typeList.add(typeName);
    }
  }
  
  Set<String> typeList = new HashSet();
  
  DefaultHandler listingHandler = new TypeListingHandler();
  









  public Set<String> listUp(String path)
    throws IOException, SAXException
  {
    return listUp(new File(path));
  }
  









  public Set<String> listUp(String[] paths)
    throws IOException, SAXException
  {
    for (String path : paths) {
      listUp(path);
    }
    return typeList;
  }
  











  public Set<String> listUp(File[] dirsOrFiles)
    throws IOException, SAXException
  {
    for (File file : dirsOrFiles) {
      listUp(file);
    }
    return typeList;
  }
  










  public Set<String> listUp(File dirOrFile)
    throws IOException, SAXException
  {
    if (dirOrFile.isDirectory())
    {
      listUpDirectory(dirOrFile);
    } else if (dirOrFile.isFile())
    {
      listUpFile(dirOrFile);
    }
    return typeList;
  }
  
  private void listUpFile(File file) throws IOException, SAXException
  {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setValidating(false);
      SAXParser parser = factory.newSAXParser();
      

      InputSource inputSource = new InputSource(new BufferedInputStream(
        new FileInputStream(file)));
      inputSource.setSystemId(file.getAbsolutePath());
      
      parser.parse(inputSource, new TypeListingHandler());
    }
    catch (ParserConfigurationException e) {
      System.out.println("The underlying parser doesn't support the requested features.");
      e.printStackTrace(System.out);
    }
    catch (FactoryConfigurationError e) {
      System.out.println("Error occured while obtaining SAXParserFactory instance.");
      e.printStackTrace(System.out);
    }
    catch (DLXmlException localDLXmlException) {}catch (SAXException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    }
  }
  
  private void listUpDirectory(File dir) throws IOException, SAXException {
    File[] xmlFiles = dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        if (filename.endsWith(".xml"))
          return true;
        return false;
      }
    });
    
    for (File xmlFile : xmlFiles) {
      try
      {
        listUpFile(xmlFile);
      }
      catch (DLXmlException localDLXmlException) {}
    }
  }
  
  public static void main(String[] args) throws IOException, SAXException {
    File file = new File("C:\\Documents and Settings\\ezotkina\\My Documents\\Work\\Eclipse Workspace\\GEDI\\sample_documents\\0001\\0001\\002-0001-00188.overlay.xml");
    


    Set<String> typeInfo = new TypeLister().listUp(file);
    System.out.println("Types in " + file.getAbsolutePath() + "\ninfo size = " + 
      typeInfo.size() + "\n " + file.exists() + "\n " + 
      file.isDirectory() + "\n " + file.isFile());
    for (String typeName : typeInfo) {
      System.out.println(typeName);
    }
    
    file = new File("C:\\Documents and Settings\\ezotkina\\My Documents\\Work\\Eclipse Workspace\\GEDI\\sample_documents\\0001\\0001\\002-0001-00188.merge.xml");
    


    Set<String> typeInfo1 = new TypeLister().listUp(file);
    System.out.println("\n");
    for (String typeName : typeInfo1) {
      System.out.println(typeName);
    }
    
    System.out.println(typeInfo.equals(typeInfo1));
  }
}
