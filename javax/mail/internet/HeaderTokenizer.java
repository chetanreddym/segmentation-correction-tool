package javax.mail.internet;




public class HeaderTokenizer
{
  private String string;
  


  private boolean skipComments;
  


  private String delimiters;
  


  private int currentPos;
  


  private int maxPos;
  


  private int nextPos;
  


  private int peekPos;
  


  public static final String RFC822 = "()<>@,;:\\\"\t .[]";
  


  public static final String MIME = "()<>@,;:\\\"\t []/?=";
  


  public static class Token
  {
    private int type;
    

    private String value;
    

    public static final int ATOM = -1;
    

    public static final int QUOTEDSTRING = -2;
    

    public static final int COMMENT = -3;
    

    public static final int EOF = -4;
    


    public Token(int paramInt, String paramString)
    {
      type = paramInt;
      value = paramString;
    }
    















    public int getType()
    {
      return type;
    }
    







    public String getValue()
    {
      return value;
    }
  }
  



















  private static final Token EOFToken = new Token(-4, null);
  











  public HeaderTokenizer(String paramString1, String paramString2, boolean paramBoolean)
  {
    string = (paramString1 == null ? "" : paramString1);
    skipComments = paramBoolean;
    delimiters = paramString2;
    currentPos = (this.nextPos = this.peekPos = 0);
    maxPos = string.length();
  }
  





  public HeaderTokenizer(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, true);
  }
  




  public HeaderTokenizer(String paramString)
  {
    this(paramString, "()<>@,;:\\\"\t .[]");
  }
  









  public Token next()
    throws ParseException
  {
    currentPos = nextPos;
    Token localToken = getNext();
    nextPos = (this.peekPos = currentPos);
    return localToken;
  }
  









  public Token peek()
    throws ParseException
  {
    currentPos = peekPos;
    Token localToken = getNext();
    peekPos = currentPos;
    return localToken;
  }
  





  public String getRemainder()
  {
    return string.substring(nextPos);
  }
  




  private Token getNext()
    throws ParseException
  {
    if (currentPos >= maxPos) {
      return EOFToken;
    }
    
    if (skipWhiteSpace() == -4) {
      return EOFToken;
    }
    

    int k = 0;
    
    int i = string.charAt(currentPos);
    


    while (i == 40)
    {

      j = ++currentPos;int m = 1;
      for (; (m > 0) && (currentPos < maxPos); 
          currentPos += 1) {
        i = string.charAt(currentPos);
        if (i == 92) {
          currentPos += 1;
          k = 1;
        } else if (i == 13) {
          k = 1;
        } else if (i == 40) {
          m++;
        } else if (i == 41) {
          m--;
        } }
      if (m != 0) {
        throw new ParseException("Unbalanced comments");
      }
      if (!skipComments)
      {
        String str;
        
        if (k != 0) {
          str = filterToken(string, j, currentPos - 1);
        } else {
          str = string.substring(j, currentPos - 1);
        }
        return new Token(-3, str);
      }
      

      if (skipWhiteSpace() == -4)
        return EOFToken;
      i = string.charAt(currentPos);
    }
    
    Object localObject;
    
    if (i == 34) {
      for (j = ++currentPos; currentPos < maxPos; currentPos += 1) {
        i = string.charAt(currentPos);
        if (i == 92) {
          currentPos += 1;
          k = 1;
        } else if (i == 13) {
          k = 1;
        } else if (i == 34) {
          currentPos += 1;
          

          if (k != 0) {
            localObject = filterToken(string, j, currentPos - 1);
          } else {
            localObject = string.substring(j, currentPos - 1);
          }
          return new Token(-2, (String)localObject);
        }
      }
      throw new ParseException("Unbalanced quoted string");
    }
    

    if ((i < 32) || (i >= 127) || (delimiters.indexOf(i) >= 0)) {
      currentPos += 1;
      localObject = new char[1];
      localObject[0] = i;
      return new Token(i, new String((char[])localObject));
    }
    

    for (int j = currentPos; currentPos < maxPos; currentPos += 1) {
      i = string.charAt(currentPos);
      

      if ((i < 32) || (i >= 127) || (i == 40) || (i == 32) || 
        (i == 34) || (delimiters.indexOf(i) >= 0))
        break;
    }
    return new Token(-1, string.substring(j, currentPos));
  }
  
  private int skipWhiteSpace()
  {
    for (; 
        currentPos < maxPos; currentPos += 1) { int i;
      if (((i = string.charAt(currentPos)) != ' ') && 
        (i != 9) && (i != 13) && (i != 10))
        return currentPos; }
    return -4;
  }
  


  private static String filterToken(String paramString, int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    
    int i = 0;
    int j = 0;
    
    for (int k = paramInt1; k < paramInt2; k++) {
      char c = paramString.charAt(k);
      if ((c == '\n') && (j != 0))
      {

        j = 0;
      }
      else
      {
        j = 0;
        if (i == 0)
        {
          if (c == '\\') {
            i = 1;
          } else if (c == '\r') {
            j = 1;
          } else {
            localStringBuffer.append(c);
          }
        }
        else
        {
          localStringBuffer.append(c);
          i = 0;
        }
      } }
    return localStringBuffer.toString();
  }
}
