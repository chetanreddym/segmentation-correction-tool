package javax.media.jai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.util.CaselessStringKey;












class RegistryFileParser
{
  private URL url;
  private InputStream is;
  private ClassLoader classLoader;
  private OperationRegistry or;
  private StreamTokenizer st;
  private int token;
  private int lineno;
  private Hashtable localNamesTable;
  
  static void loadOperationRegistry(OperationRegistry or, ClassLoader cl, InputStream is)
    throws IOException
  {
    new RegistryFileParser(or, cl, is).parseFile();
  }
  




  static void loadOperationRegistry(OperationRegistry or, ClassLoader cl, URL url)
    throws IOException
  {
    new RegistryFileParser(or, cl, url).parseFile();
  }
  





















  private RegistryFileParser(OperationRegistry or, ClassLoader cl, URL url)
    throws IOException
  {
    this(or, cl, url.openStream());
    this.url = url;
  }
  



  private RegistryFileParser(OperationRegistry or, ClassLoader cl, InputStream is)
    throws IOException
  {
    if (or == null) {
      or = JAI.getDefaultInstance().getOperationRegistry();
    }
    this.is = is;
    url = null;
    this.or = or;
    classLoader = cl;
    

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    

    st = new StreamTokenizer(reader);
    
    st.commentChar(35);
    st.eolIsSignificant(true);
    st.slashSlashComments(true);
    st.slashStarComments(true);
    
    token = st.ttype;
    lineno = -1;
    


    localNamesTable = new Hashtable();
    
    String[] modeNames = RegistryMode.getModeNames();
    
    for (int i = 0; i < modeNames.length; i++) {
      localNamesTable.put(new CaselessStringKey(modeNames[i]), new Hashtable());
    }
  }
  



  private int skipEmptyTokens()
    throws IOException
  {
    while (st.sval == null) {
      if (token == -1) {
        return token;
      }
      token = st.nextToken();
    }
    
    return token;
  }
  



  private String[] getNextLine()
    throws IOException
  {
    if (skipEmptyTokens() == -1) {
      return null;
    }
    Vector v = new Vector();
    
    lineno = st.lineno();
    
    while ((token != 10) && (token != -1))
    {

      if (st.sval != null) {
        v.addElement(st.sval);
      }
      token = st.nextToken();
    }
    
    if (v.size() == 0) {
      return null;
    }
    return (String[])v.toArray(new String[0]);
  }
  

  private static String[][] aliases = { { "odesc", "descriptor" }, { "rif", "rendered" }, { "crif", "renderable" }, { "cif", "collection" } };
  







  private String mapName(String key)
  {
    for (int i = 0; i < aliases.length; i++) {
      if (key.equalsIgnoreCase(aliases[i][0]))
        return aliases[i][1];
    }
    return key;
  }
  


  private Object getInstance(String className)
  {
    try
    {
      Class descriptorClass = null;
      String errorMsg = null;
      





      if (classLoader != null) {
        try {
          descriptorClass = Class.forName(className, true, classLoader);
        }
        catch (Exception e) {
          errorMsg = e.getMessage();
        }
      }
      

      if (descriptorClass == null) {
        try {
          descriptorClass = Class.forName(className);
        } catch (Exception e) {
          errorMsg = e.getMessage();
        }
      }
      



      if (descriptorClass == null) {
        try {
          descriptorClass = Class.forName(className, true, ClassLoader.getSystemClassLoader());
        }
        catch (Exception e) {
          errorMsg = e.getMessage();
        }
      }
      
      if (descriptorClass == null) {
        registryFileError(errorMsg);
        return null;
      }
      
      return descriptorClass.newInstance();
    }
    catch (Exception e) {
      registryFileError(e.getMessage());
      e.printStackTrace();
    }
    
    return null;
  }
  




  boolean parseFile()
    throws IOException
  {
    if (token == -1) {
      return true;
    }
    

    token = st.nextToken();
    
    while (token != -1) {
      String[] keys;
      if ((keys = getNextLine()) == null) {
        break;
      }
      

      String key = mapName(keys[0]);
      

      if (key.equalsIgnoreCase("registryMode"))
      {
        RegistryMode mode = (RegistryMode)getInstance(keys[1]);
        
        if ((mode != null) && 
          (!RegistryMode.addMode(mode))) {
          registryFileError(JaiI18N.getString("RegistryFileParser10"));

        }
        

      }
      else if (key.equalsIgnoreCase("descriptor"))
      {
        registerDescriptor(keys);
      }
      else {
        RegistryMode mode;
        if ((mode = RegistryMode.getMode(key)) != null)
        {
          registerFactory(mode, keys);

        }
        else if (key.equalsIgnoreCase("pref"))
        {
          key = mapName(keys[1]);
          



          if (key.equalsIgnoreCase("product"))
          {
            setProductPreference(RegistryMode.getMode("rendered"), keys);



          }
          else if ((mode = RegistryMode.getMode(key)) != null)
          {
            setFactoryPreference(mode, keys);
          }
          else {
            registryFileError(JaiI18N.getString("RegistryFileParser4"));
          }
          
        }
        else if (key.equalsIgnoreCase("productPref"))
        {
          key = mapName(keys[1]);
          


          if ((mode = RegistryMode.getMode(key)) != null)
          {
            setProductPreference(mode, keys);
          }
          else {
            registryFileError(JaiI18N.getString("RegistryFileParser5"));
          }
        } else {
          registryFileError(JaiI18N.getString("RegistryFileParser6"));
        }
      }
    }
    

    if (url != null) {
      is.close();
    }
    return true;
  }
  



  private void registerDescriptor(String[] keys)
  {
    if (keys.length >= 2)
    {
      RegistryElementDescriptor red = (RegistryElementDescriptor)getInstance(keys[1]);
      

      if (red != null) {
        try {
          or.registerDescriptor(red);
        } catch (Exception e) {
          registryFileError(e.getMessage());
        }
      }
    }
    else {
      registryFileError(JaiI18N.getString("RegistryFileParser1"));
    }
  }
  






  private void registerFactory(RegistryMode mode, String[] keys)
  {
    if (mode.arePreferencesSupported())
    {
      if (keys.length >= 5) {
        Object factory;
        if ((factory = getInstance(keys[1])) != null) {
          try {
            or.registerFactory(mode.getName(), keys[3], keys[2], factory);
            

            mapLocalNameToObject(mode.getName(), keys[4], factory);
          }
          catch (Exception e) {
            registryFileError(e.getMessage());
          }
        }
      }
      else {
        registryFileError(JaiI18N.getString("RegistryFileParser2"));
      }
      

    }
    else if (keys.length >= 3) {
      Object factory;
      if ((factory = getInstance(keys[1])) != null) {
        try {
          or.registerFactory(mode.getName(), keys[2], null, factory);
        }
        catch (Exception e)
        {
          registryFileError(e.getMessage());
        }
      }
    }
    else {
      registryFileError(JaiI18N.getString("RegistryFileParser3"));
    }
  }
  






  private void setProductPreference(RegistryMode mode, String[] keys)
  {
    String modeName = mode.getName();
    
    if (mode.arePreferencesSupported())
    {
      if (keys.length >= 5) {
        try
        {
          or.setProductPreference(modeName, keys[2], keys[3], keys[4]);
        }
        catch (Exception e)
        {
          registryFileError(e.getMessage());
        }
        
      } else {
        registryFileError(JaiI18N.getString("RegistryFileParser5"));
      }
      
    }
    else {
      registryFileError(JaiI18N.getString("RegistryFileParser9"));
    }
  }
  




  private void setFactoryPreference(RegistryMode mode, String[] keys)
  {
    String modeName = mode.getName();
    

    if (mode.arePreferencesSupported())
    {
      if (keys.length >= 6)
      {
        Object preferred = getObjectFromLocalName(modeName, keys[4]);
        Object other = getObjectFromLocalName(modeName, keys[5]);
        
        if ((preferred != null) && (other != null)) {
          try
          {
            or.setFactoryPreference(modeName, keys[2], keys[3], preferred, other);
          }
          catch (Exception e)
          {
            registryFileError(e.getMessage());
          }
        }
      }
      else {
        registryFileError(JaiI18N.getString("RegistryFileParser4"));
      }
      
    }
    else {
      registryFileError(JaiI18N.getString("RegistryFileParser7"));
    }
  }
  




  private void mapLocalNameToObject(String modeName, String localName, Object factory)
  {
    Hashtable modeTable = (Hashtable)localNamesTable.get(new CaselessStringKey(modeName));
    

    modeTable.put(new CaselessStringKey(localName), factory);
  }
  



  private Object getObjectFromLocalName(String modeName, String localName)
  {
    Hashtable modeTable = (Hashtable)localNamesTable.get(new CaselessStringKey(modeName));
    

    Object obj = modeTable.get(new CaselessStringKey(localName));
    
    if (obj == null) {
      registryFileError(localName + ": " + JaiI18N.getString("RegistryFileParser8"));
    }
    
    return obj;
  }
  
  private boolean headerLinePrinted = false;
  



  private void registryFileError(String msg)
  {
    if (!headerLinePrinted)
    {
      if (url != null) {
        errorMsg(JaiI18N.getString("RegistryFileParser11"), new Object[] { url.getPath() });
      }
      

      headerLinePrinted = true;
    }
    
    errorMsg(JaiI18N.getString("RegistryFileParser0"), new Object[] { new Integer(lineno) });
    

    if (msg != null) {
      errorMsg(msg, null);
    }
  }
  


  private void errorMsg(String key, Object[] args)
  {
    MessageFormat mf = new MessageFormat(key);
    mf.setLocale(Locale.getDefault());
    
    if (System.err != null) {
      System.err.println(mf.format(args));
    }
  }
  


  static void writeOperationRegistry(OperationRegistry or, OutputStream os)
    throws IOException
  {
    writeOperationRegistry(or, new BufferedWriter(new OutputStreamWriter(os)));
  }
  





  static void writeOperationRegistry(OperationRegistry or, BufferedWriter bw)
    throws IOException
  {
    Iterator dcit = RegistryMode.getDescriptorClasses().iterator();
    
    String tab = "  ";
    
    while (dcit.hasNext())
    {
      Class descriptorClass = (Class)dcit.next();
      
      List descriptors = or.getDescriptors(descriptorClass);
      


      bw.write("#");bw.newLine();
      bw.write("# Descriptors corresponding to class : " + descriptorClass.getName());bw.newLine();
      bw.write("#");bw.newLine();
      
      if ((descriptors == null) || (descriptors.size() <= 0)) {
        bw.write("# <EMPTY>");bw.newLine();
      }
      else {
        Iterator it = descriptors.iterator();
        
        while (it.hasNext()) {
          bw.write("descriptor" + tab);
          bw.write(it.next().getClass().getName());
          bw.newLine();
        }
      }
      bw.newLine();
      




      String[] modeNames = RegistryMode.getModeNames(descriptorClass);
      



      for (int i = 0; i < modeNames.length; i++) {
        bw.write("#");bw.newLine();
        bw.write("# Factories registered under mode : " + modeNames[i]);bw.newLine();
        bw.write("#");bw.newLine();
        
        RegistryMode mode = RegistryMode.getMode(modeNames[i]);
        
        boolean prefs = mode.arePreferencesSupported();
        
        String[] descriptorNames = or.getDescriptorNames(modeNames[i]);
        


        int j = 0; for (boolean empty = true; j < descriptorNames.length; j++)
        {
          if (prefs) {
            Vector productVector = or.getOrderedProductList(modeNames[i], descriptorNames[j]);
            


            if (productVector != null)
            {

              String[] productNames = (String[])productVector.toArray(new String[0]);
              



              for (int k = 0; k < productNames.length; k++)
              {
                List factoryList = or.getOrderedFactoryList(modeNames[i], descriptorNames[j], productNames[k]);
                



                Iterator fit = factoryList.iterator();
                
                while (fit.hasNext()) {
                  Object instance = fit.next();
                  
                  if (instance != null)
                  {

                    bw.write(modeNames[i] + tab);
                    bw.write(instance.getClass().getName() + tab);
                    bw.write(productNames[k] + tab);
                    bw.write(descriptorNames[j] + tab);
                    bw.write(or.getLocalName(modeNames[i], instance));
                    bw.newLine();
                    
                    empty = false;
                  }
                }
              }
            } } else { Iterator fit = or.getFactoryIterator(modeNames[i], descriptorNames[j]);
            

            while (fit.hasNext()) {
              Object instance = fit.next();
              
              if (instance != null)
              {

                bw.write(modeNames[i] + tab);
                bw.write(instance.getClass().getName() + tab);
                bw.write(descriptorNames[j]);
                bw.newLine();
                
                empty = false;
              }
            }
          }
        }
        if (empty) {
          bw.write("# <EMPTY>");bw.newLine();
        }
        bw.newLine();
        


        if (!prefs) {
          bw.write("#");bw.newLine();
          bw.write("# Preferences not supported for mode : " + modeNames[i]);bw.newLine();
          bw.write("#");bw.newLine();
          bw.newLine();

        }
        else
        {
          bw.write("#");bw.newLine();
          bw.write("# Product preferences for mode : " + modeNames[i]);bw.newLine();
          bw.write("#");bw.newLine();
          
          j = 0; for (empty = true; j < descriptorNames.length; j++)
          {
            String[][] productPrefs = or.getProductPreferences(modeNames[i], descriptorNames[j]);
            

            if (productPrefs != null)
            {

              for (int k = 0; k < productPrefs.length; k++) {
                bw.write("productPref" + tab);
                bw.write(modeNames[i] + tab);
                bw.write(descriptorNames[j] + tab);
                bw.write(productPrefs[k][0] + tab);
                bw.write(productPrefs[k][1]);
                bw.newLine();
                
                empty = false;
              }
            }
          }
          if (empty) {
            bw.write("# <EMPTY>");bw.newLine();
          }
          bw.newLine();
          

          bw.write("#");bw.newLine();
          bw.write("# Factory preferences for mode : " + modeNames[i]);bw.newLine();
          bw.write("#");bw.newLine();
          

          j = 0; for (empty = true; j < descriptorNames.length; j++)
          {
            if (prefs) {
              Vector productVector = or.getOrderedProductList(modeNames[i], descriptorNames[j]);
              


              if (productVector != null)
              {

                String[] productNames = (String[])productVector.toArray(new String[0]);
                



                for (int k = 0; k < productNames.length; k++)
                {
                  Object[][] fprefs = or.getFactoryPreferences(modeNames[i], descriptorNames[j], productNames[k]);
                  

                  if (fprefs != null)
                  {

                    for (int l = 0; l < fprefs.length; l++) {
                      bw.write("pref" + tab);
                      bw.write(modeNames[i] + tab);
                      bw.write(descriptorNames[j] + tab);
                      bw.write(productNames[k] + tab);
                      bw.write(or.getLocalName(modeNames[i], fprefs[l][0]) + tab);
                      bw.write(or.getLocalName(modeNames[i], fprefs[l][1]));
                      bw.newLine();
                      
                      empty = false;
                    } }
                }
              }
            }
          }
          if (empty) {
            bw.write("# <EMPTY>");bw.newLine();
          }
          bw.newLine();
        }
      }
    }
    bw.flush();
  }
}
