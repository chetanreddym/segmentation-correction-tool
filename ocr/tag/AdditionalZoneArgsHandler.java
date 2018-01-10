package ocr.tag;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import ocr.gui.OCRInterface;










public class AdditionalZoneArgsHandler
{
  public final String ALL_STR = "*";
  public final String NONE_STR = "-";
  
  public static final int ALL = 0;
  
  public static final int NONE = 1;
  public static final int SOME = 2;
  private int filetype = -1;
  private int readfor = 1;
  
  private Vector<String> args_vec = null;
  

  private Hashtable<Integer, String> readfor_idhash = null;
  

  private Hashtable argtype_hash = null;
  

  public AdditionalZoneArgsHandler(Properties prop, int file_type)
  {
    filetype = file_type;
    argtype_hash = new Hashtable();
    



    String prop_line = prop.getProperty("type" + filetype + "_readfor");
    if (prop_line.equals("*"))
    {

      readfor = 0;
      args_vec = new Vector();
    }
    else if (prop_line.equals("-"))
    {

      readfor = 1;

    }
    else
    {
      readfor = 2;
      readfor_idhash = new Hashtable();
      args_vec = new Vector();
    }
    
    StringTokenizer t = null;
    String token = "";
    
    if (readfor == 2)
    {
      t = new StringTokenizer(prop_line, ", ");
      

      while (t.hasMoreTokens())
      {
        token = t.nextToken();
        
        try
        {
          Integer.parseInt(token);
        }
        catch (NumberFormatException nfe)
        {
          nfe.printStackTrace();
          System.out.println("Error Reading Property File " + filetype + " at property type" + filetype + "_readfor");
        }
        readfor_idhash.put(new Integer(token), "");
      }
    }
    

    if ((readfor == 2) || (readfor == 0))
    {

      prop_line = prop.getProperty("type" + filetype + "_args");
      t = new StringTokenizer(prop_line, ";");
      

      while (t.hasMoreTokens())
      {
        token = t.nextToken();
        

        args_vec.add(token);
      }
      


      prop_line = prop.getProperty("type" + filetype + "_argstype");
      t = new StringTokenizer(prop_line, ";");
      int vartype_cnt = 0;
      


      while (t.hasMoreTokens())
      {
        token = t.nextToken();
        
        if (token.startsWith("List"))
        {
          StringTokenizer lst_t = new StringTokenizer(token, "\n(),");
          lst_t.nextToken();
          

          Vector<String> lst_vec = new Vector();
          


          while (lst_t.hasMoreTokens())
          {
            String lst_token = lst_t.nextToken();
            lst_vec.add(lst_token);
          }
          
          argtype_hash.put(args_vec.get(vartype_cnt), lst_vec);
          vartype_cnt++;
        }
        else
        {
          argtype_hash.put(args_vec.get(vartype_cnt), token);
          vartype_cnt++;
        }
      }
      

      if (vartype_cnt != args_vec.size())
      {
        System.out.println("Error: Error encountered with config file.");
        
        try
        {
          OCRInterface.log.write("Error: Error encountered with config file.");
          OCRInterface.log.newLine();
          OCRInterface.log.write("Closing Application.");
          OCRInterface.log.newLine();
          OCRInterface.log.write(GetDateTime.getInstance());
          OCRInterface.log.newLine();
          OCRInterface.log.close();
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
          System.out.println("IoException Encountered with processing of " + OCRInterface.log_path);
        }
        
        System.exit(-1);
      }
    }
  }
  


  public boolean readfor(int id)
  {
    if (readfor == 1) return false;
    if (readfor == 0) { return true;
    }
    
    return readfor_idhash.containsKey(new Integer(id));
  }
  
  public String getTypeOfArgByArg(String arg)
  {
    return (String)argtype_hash.get(arg);
  }
  
  public Object getTypeOfArgByIndex(int index)
  {
    return argtype_hash.get(args_vec.get(index));
  }
  

  public Vector getArgVector()
  {
    return args_vec;
  }
  
  public String getTypeLabelAt(int index)
  {
    return (String)args_vec.get(index);
  }
  
  public int getFileType()
  {
    return filetype;
  }
  
  public int getReadFor()
  {
    return readfor;
  }
  
  public int getTotalNumOfArgs()
  {
    if (readfor == 1)
      return 0;
    return args_vec.size();
  }
}
