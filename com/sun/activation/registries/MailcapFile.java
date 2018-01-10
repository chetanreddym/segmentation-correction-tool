package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class MailcapFile
{
  private Hashtable type_hash = null;
  private static boolean debug = false;
  
  static {
    try {
      debug = Boolean.getBoolean("javax.activation.debug");
    }
    catch (Throwable localThrowable) {}
  }
  




  public MailcapFile(String paramString)
    throws IOException
  {
    if (debug)
      System.out.println("new MailcapFile: file " + paramString);
    FileReader localFileReader = null;
    try {
      localFileReader = new FileReader(paramString);
      type_hash = createMailcapHash(new BufferedReader(localFileReader));
    } finally {
      if (localFileReader != null) {
        try {
          localFileReader.close();
        }
        catch (IOException localIOException) {}
      }
    }
  }
  


  public MailcapFile(InputStream paramInputStream)
    throws IOException
  {
    if (debug)
      System.out.println("new MailcapFile: InputStream");
    type_hash = createMailcapHash(
      new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1")));
  }
  


  public MailcapFile()
  {
    if (debug) {
      System.out.println("new MailcapFile: default");
    }
    type_hash = new Hashtable();
  }
  







  public Hashtable getMailcapList(String paramString)
  {
    Object localObject = null;
    Hashtable localHashtable = null;
    

    localObject = (Hashtable)type_hash.get(paramString);
    

    int i = paramString.indexOf('/');
    String str = paramString.substring(0, i + 1) + "*";
    localHashtable = (Hashtable)type_hash.get(str);
    
    if (localHashtable != null) {
      if (localObject != null) {
        localObject = mergeResults((Hashtable)localObject, localHashtable);
      } else
        localObject = localHashtable;
    }
    return localObject;
  }
  





  private Hashtable mergeResults(Hashtable paramHashtable1, Hashtable paramHashtable2)
  {
    Enumeration localEnumeration1 = paramHashtable2.keys();
    Hashtable localHashtable = (Hashtable)paramHashtable1.clone();
    

    while (localEnumeration1.hasMoreElements()) {
      String str = (String)localEnumeration1.nextElement();
      Vector localVector1 = (Vector)localHashtable.get(str);
      if (localVector1 == null) {
        localHashtable.put(str, paramHashtable2.get(str));
      }
      else {
        Vector localVector2 = (Vector)paramHashtable2.get(str);
        Enumeration localEnumeration2 = localVector2.elements();
        localVector1 = (Vector)localVector1.clone();
        localHashtable.put(str, localVector1);
        while (localEnumeration2.hasMoreElements()) {
          localVector1.addElement(localEnumeration2.nextElement());
        }
      }
    }
    return localHashtable;
  }
  









  public void appendToMailcap(String paramString)
  {
    if (debug)
      System.out.println("appendToMailcap: " + paramString);
    try {
      parse(new StringReader(paramString), type_hash);
    }
    catch (IOException localIOException) {}
  }
  


  private Hashtable createMailcapHash(Reader paramReader)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable();
    
    parse(paramReader, localHashtable);
    
    return localHashtable;
  }
  

  private void parse(Reader paramReader, Hashtable paramHashtable)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(paramReader);
    String str = null;
    Object localObject = null;
    
    while ((str = localBufferedReader.readLine()) != null)
    {

      str = str.trim();
      try
      {
        if (str.charAt(0) != '#')
        {
          if (str.charAt(str.length() - 1) == '\\') {
            if (localObject != null) {
              localObject = localObject + str.substring(0, str.length() - 1);
            } else
              localObject = str.substring(0, str.length() - 1);
          } else if (localObject != null)
          {
            localObject = localObject + str;
            try
            {
              parseLine((String)localObject, paramHashtable);
            }
            catch (MailcapParseException localMailcapParseException1) {}
            
            localObject = null;
          }
          else
          {
            try {
              parseLine(str, paramHashtable);
            }
            catch (MailcapParseException localMailcapParseException2) {}
          }
        }
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    }
  }
  





  protected static void parseLine(String paramString, Hashtable paramHashtable)
    throws MailcapParseException, IOException
  {
    MailcapTokenizer localMailcapTokenizer = new MailcapTokenizer(paramString);
    localMailcapTokenizer.setIsAutoquoting(false);
    String str1 = "";
    String str2 = "*";
    String str3 = "";
    
    if (debug) {
      System.out.println("parse: " + paramString);
    }
    int i = localMailcapTokenizer.nextToken();
    str3 = str3.concat(localMailcapTokenizer.getCurrentTokenValue());
    if (i != 2) {
      reportParseError(2, i, 
        localMailcapTokenizer.getCurrentTokenValue());
    }
    str1 = localMailcapTokenizer.getCurrentTokenValue().toLowerCase();
    


    i = localMailcapTokenizer.nextToken();
    str3 = str3.concat(localMailcapTokenizer.getCurrentTokenValue());
    if ((i != 47) && 
      (i != 59)) {
      reportParseError(47, 
        59, i, 
        localMailcapTokenizer.getCurrentTokenValue());
    }
    

    if (i == 47)
    {
      i = localMailcapTokenizer.nextToken();
      str3 = str3.concat(localMailcapTokenizer.getCurrentTokenValue());
      if (i != 2) {
        reportParseError(2, 
          i, localMailcapTokenizer.getCurrentTokenValue());
      }
      str2 = localMailcapTokenizer.getCurrentTokenValue().toLowerCase();
      

      i = localMailcapTokenizer.nextToken();
      str3 = str3.concat(localMailcapTokenizer.getCurrentTokenValue());
    }
    
    if (debug) {
      System.out.println("  Type: " + str1 + "/" + str2);
    }
    Hashtable localHashtable = 
      (Hashtable)paramHashtable.get(str1 + "/" + str2);
    if (localHashtable == null) {
      localHashtable = new Hashtable();
      paramHashtable.put(str1 + "/" + str2, localHashtable);
    }
    

    if (i != 59) {
      reportParseError(59, 
        i, localMailcapTokenizer.getCurrentTokenValue());
    }
    


    localMailcapTokenizer.setIsAutoquoting(true);
    i = localMailcapTokenizer.nextToken();
    localMailcapTokenizer.setIsAutoquoting(false);
    str3 = str3.concat(localMailcapTokenizer.getCurrentTokenValue());
    if ((i != 2) && 
      (i != 59))
    {
      reportParseError(2, 
        59, i, 
        localMailcapTokenizer.getCurrentTokenValue());
    }
    


    if (i != 59) {
      i = localMailcapTokenizer.nextToken();
    }
    


    if (i == 59)
    {
      do
      {

        i = localMailcapTokenizer.nextToken();
        if (i != 2) {
          reportParseError(2, 
            i, localMailcapTokenizer.getCurrentTokenValue());
        }
        String str4 = 
          localMailcapTokenizer.getCurrentTokenValue().toLowerCase();
        

        i = localMailcapTokenizer.nextToken();
        if ((i != 61) && 
          (i != 59) && 
          (i != 5)) {
          reportParseError(61, 
            59, 
            5, 
            i, localMailcapTokenizer.getCurrentTokenValue());
        }
        

        if (i == 61)
        {


          localMailcapTokenizer.setIsAutoquoting(true);
          i = localMailcapTokenizer.nextToken();
          localMailcapTokenizer.setIsAutoquoting(false);
          if (i != 2) {
            reportParseError(2, 
              i, localMailcapTokenizer.getCurrentTokenValue());
          }
          String str5 = 
            localMailcapTokenizer.getCurrentTokenValue();
          

          if (str4.startsWith("x-java-")) {
            String str6 = str4.substring(7);
            


            if (debug)
              System.out.println("    Command: " + str6 + 
                ", Class: " + str5);
            Vector localVector = 
              (Vector)localHashtable.get(str6);
            if (localVector == null) {
              localVector = new Vector();
              localHashtable.put(str6, localVector);
            }
            
            localVector.insertElementAt(str5, 0);
          }
          

          i = localMailcapTokenizer.nextToken();
        }
      } while (i == 59);
    } else if (i != 5) {
      reportParseError(5, 
        59, 
        i, localMailcapTokenizer.getCurrentTokenValue());
    }
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, String paramString) throws MailcapParseException
  {
    throw new MailcapParseException("Encountered a " + 
      MailcapTokenizer.nameForToken(paramInt2) + " token (" + 
      paramString + ") while expecting a " + 
      MailcapTokenizer.nameForToken(paramInt1) + " token.");
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, String paramString)
    throws MailcapParseException
  {
    throw new MailcapParseException("Encountered a " + 
      MailcapTokenizer.nameForToken(paramInt3) + " token (" + 
      paramString + ") while expecting a " + 
      MailcapTokenizer.nameForToken(paramInt1) + " or a " + 
      MailcapTokenizer.nameForToken(paramInt2) + " token.");
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
    throws MailcapParseException
  {
    if (debug)
      System.out.println("PARSE ERROR: Encountered a " + 
        MailcapTokenizer.nameForToken(paramInt4) + " token (" + 
        paramString + ") while expecting a " + 
        MailcapTokenizer.nameForToken(paramInt1) + ", a " + 
        MailcapTokenizer.nameForToken(paramInt2) + ", or a " + 
        MailcapTokenizer.nameForToken(paramInt3) + " token.");
    throw new MailcapParseException("Encountered a " + 
      MailcapTokenizer.nameForToken(paramInt4) + " token (" + 
      paramString + ") while expecting a " + 
      MailcapTokenizer.nameForToken(paramInt1) + ", a " + 
      MailcapTokenizer.nameForToken(paramInt2) + ", or a " + 
      MailcapTokenizer.nameForToken(paramInt3) + " token.");
  }
}
