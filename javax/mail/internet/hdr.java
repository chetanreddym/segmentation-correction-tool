package javax.mail.internet;















































































































class hdr
{
  String name;
  













































































































  String line;
  














































































































  hdr(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0)
    {
      name = paramString.trim();
    } else {
      name = paramString.substring(0, i).trim();
    }
    line = paramString;
  }
  


  hdr(String paramString1, String paramString2)
  {
    name = paramString1;
    if (paramString2 != null) {
      line = (paramString1 + ": " + paramString2);
    } else {
      line = null;
    }
  }
  

  String getName()
  {
    return name;
  }
  


  String getValue()
  {
    int i = line.indexOf(':');
    if (i < 0) {
      return line;
    }
    
    for (int j = i + 1; j < line.length(); j++) {
      int k = line.charAt(j);
      if ((k != 32) && (k != 9) && (k != 13) && (k != 10))
        break;
    }
    return line.substring(j);
  }
}
