package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class MimeTypeFile
{
  private String fname = null;
  private Hashtable type_hash = new Hashtable();
  private static boolean DEBUG = false;
  



  public MimeTypeFile(String paramString)
    throws IOException
  {
    File localFile = null;
    FileReader localFileReader = null;
    
    fname = paramString;
    
    localFile = new File(fname);
    
    localFileReader = new FileReader(localFile);
    try
    {
      parse(new BufferedReader(localFileReader));
    } finally {
      try {
        localFileReader.close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  public MimeTypeFile(InputStream paramInputStream) throws IOException
  {
    parse(new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1")));
  }
  








  public MimeTypeEntry getMimeTypeEntry(String paramString)
  {
    return (MimeTypeEntry)type_hash.get(paramString);
  }
  


  public String getMIMETypeString(String paramString)
  {
    MimeTypeEntry localMimeTypeEntry = getMimeTypeEntry(paramString);
    
    if (localMimeTypeEntry != null) {
      return localMimeTypeEntry.getMIMEType();
    }
    return null;
  }
  













  public void appendToRegistry(String paramString)
  {
    try
    {
      parse(new BufferedReader(new StringReader(paramString)));
    }
    catch (IOException localIOException) {}
  }
  


  private void parse(BufferedReader paramBufferedReader)
    throws IOException
  {
    String str1 = null;String str2 = null;
    
    while ((str1 = paramBufferedReader.readLine()) != null) {
      if (str2 == null) {
        str2 = str1;
      } else
        str2 = str2 + str1;
      int i = str2.length();
      if ((str2.length() > 0) && (str2.charAt(i - 1) == '\\')) {
        str2 = str2.substring(0, i - 1);
      }
      else {
        parseEntry(str2);
        str2 = null;
      } }
    if (str2 != null) {
      parseEntry(str2);
    }
  }
  

  private void parseEntry(String paramString)
  {
    Object localObject1 = null;
    String str1 = null;
    paramString = paramString.trim();
    
    if (paramString.length() == 0) {
      return;
    }
    
    if (paramString.charAt(0) == '#')
      return;
    Object localObject2;
    Object localObject3;
    if (paramString.indexOf('=') > 0)
    {
      localObject2 = new LineTokenizer(paramString);
      while (((LineTokenizer)localObject2).hasMoreTokens()) {
        String str2 = ((LineTokenizer)localObject2).nextToken();
        localObject3 = null;
        if ((((LineTokenizer)localObject2).hasMoreTokens()) && (((LineTokenizer)localObject2).nextToken().equals("=")) && 
          (((LineTokenizer)localObject2).hasMoreTokens()))
          localObject3 = ((LineTokenizer)localObject2).nextToken();
        if (localObject3 == null) {
          System.err.println("Bad .mime.types entry: " + paramString);
          return;
        }
        if (str2.equals("type")) {
          localObject1 = localObject3;
        } else if (str2.equals("exts")) {
          StringTokenizer localStringTokenizer = new StringTokenizer((String)localObject3, ",");
          while (localStringTokenizer.hasMoreTokens()) {
            str1 = localStringTokenizer.nextToken();
            MimeTypeEntry localMimeTypeEntry = 
              new MimeTypeEntry((String)localObject1, str1);
            type_hash.put(str1, localMimeTypeEntry);
            if (DEBUG) {
              System.out.println("Added: " + localMimeTypeEntry.toString());
            }
          }
        }
      }
    }
    else {
      localObject2 = new StringTokenizer(paramString);
      int i = ((StringTokenizer)localObject2).countTokens();
      
      if (i == 0) {
        return;
      }
      localObject1 = ((StringTokenizer)localObject2).nextToken();
      
      while (((StringTokenizer)localObject2).hasMoreTokens()) {
        localObject3 = null;
        
        str1 = ((StringTokenizer)localObject2).nextToken();
        localObject3 = new MimeTypeEntry((String)localObject1, str1);
        type_hash.put(str1, localObject3);
        if (DEBUG) {
          System.out.println("Added: " + ((MimeTypeEntry)localObject3).toString());
        }
      }
    }
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    DEBUG = true;
    MimeTypeFile localMimeTypeFile = new MimeTypeFile(paramArrayOfString[0]);
    System.out.println("ext " + paramArrayOfString[1] + " type " + 
      localMimeTypeFile.getMIMETypeString(paramArrayOfString[1]));
    System.exit(0);
  }
  
  public MimeTypeFile() {}
}
