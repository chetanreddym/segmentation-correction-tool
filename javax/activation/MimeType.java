package javax.activation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


















public class MimeType
  implements Externalizable
{
  private String primaryType;
  private String subType;
  private MimeTypeParameterList parameters;
  private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
  
  public MimeType()
  {
    primaryType = "application";
    subType = "*";
    parameters = new MimeTypeParameterList();
  }
  



  public MimeType(String paramString)
    throws MimeTypeParseException
  {
    parse(paramString);
  }
  






  public MimeType(String paramString1, String paramString2)
    throws MimeTypeParseException
  {
    if (isValidToken(paramString1)) {
      primaryType = paramString1.toLowerCase();
    } else {
      throw new MimeTypeParseException("Primary type is invalid.");
    }
    

    if (isValidToken(paramString2)) {
      subType = paramString2.toLowerCase();
    } else {
      throw new MimeTypeParseException("Sub type is invalid.");
    }
    
    parameters = new MimeTypeParameterList();
  }
  

  private void parse(String paramString)
    throws MimeTypeParseException
  {
    int i = paramString.indexOf('/');
    int j = paramString.indexOf(';');
    if ((i < 0) && (j < 0))
    {

      throw new MimeTypeParseException("Unable to find a sub type."); }
    if ((i < 0) && (j >= 0))
    {

      throw new MimeTypeParseException("Unable to find a sub type."); }
    if ((i >= 0) && (j < 0))
    {
      primaryType = paramString.substring(0, i).trim().toLowerCase();
      subType = paramString.substring(i + 1).trim().toLowerCase();
      parameters = new MimeTypeParameterList();
    } else if (i < j)
    {
      primaryType = paramString.substring(0, i).trim().toLowerCase();
      subType = paramString.substring(i + 1, 
        j).trim().toLowerCase();
      parameters = new MimeTypeParameterList(paramString.substring(j));
    }
    else
    {
      throw new MimeTypeParseException("Unable to find a sub type.");
    }
    



    if (!isValidToken(primaryType)) {
      throw new MimeTypeParseException("Primary type is invalid.");
    }
    
    if (!isValidToken(subType)) {
      throw new MimeTypeParseException("Sub type is invalid.");
    }
  }
  



  public String getPrimaryType()
  {
    return primaryType;
  }
  




  public void setPrimaryType(String paramString)
    throws MimeTypeParseException
  {
    if (!isValidToken(primaryType))
      throw new MimeTypeParseException("Primary type is invalid.");
    primaryType = paramString.toLowerCase();
  }
  




  public String getSubType()
  {
    return subType;
  }
  




  public void setSubType(String paramString)
    throws MimeTypeParseException
  {
    if (!isValidToken(subType))
      throw new MimeTypeParseException("Sub type is invalid.");
    subType = paramString.toLowerCase();
  }
  




  public MimeTypeParameterList getParameters()
  {
    return parameters;
  }
  






  public String getParameter(String paramString)
  {
    return parameters.get(paramString);
  }
  






  public void setParameter(String paramString1, String paramString2)
  {
    parameters.set(paramString1, paramString2);
  }
  




  public void removeParameter(String paramString)
  {
    parameters.remove(paramString);
  }
  


  public String toString()
  {
    return getBaseType() + parameters.toString();
  }
  





  public String getBaseType()
  {
    return primaryType + "/" + subType;
  }
  






  public boolean match(MimeType paramMimeType)
  {
    return (primaryType.equals(paramMimeType.getPrimaryType())) && (
      (subType.equals("*")) || 
      (paramMimeType.getSubType().equals("*")) || 
      (subType.equals(paramMimeType.getSubType())));
  }
  





  public boolean match(String paramString)
    throws MimeTypeParseException
  {
    return match(new MimeType(paramString));
  }
  







  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeUTF(toString());
    paramObjectOutput.flush();
  }
  









  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    try
    {
      parse(paramObjectInput.readUTF());
    } catch (MimeTypeParseException localMimeTypeParseException) {
      throw new IOException(localMimeTypeParseException.toString());
    }
  }
  




  private static boolean isTokenChar(char paramChar)
  {
    return (paramChar > ' ') && (paramChar < '') && ("()<>@,;:/[]?=\\\"".indexOf(paramChar) < 0);
  }
  


  private boolean isValidToken(String paramString)
  {
    int i = paramString.length();
    if (i > 0) {
      for (int j = 0; j < i; j++) {
        char c = paramString.charAt(j);
        if (!isTokenChar(c)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
