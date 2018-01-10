package com.lotontech.talk;

import java.io.StreamTokenizer;
import java.util.StringTokenizer;

public class LOTONtext
{
  public static char OPEN_PHONETIC = '{';
  public static char CLOSE_PHONETIC = '}';
  public static char PHONETIC_SEPARATOR = '|';
  
  public static String getPhoneString(String paramString)
  {
    String str1 = "";
    
    java.io.StringReader localStringReader = new java.io.StringReader(paramString);
    StreamTokenizer localStreamTokenizer = new StreamTokenizer(localStringReader);
    
    localStreamTokenizer.resetSyntax();
    localStreamTokenizer.wordChars(65, 122);
    
    try
    {
      String str2 = "";
      int i = 0;
      
      int j;
      while ((j = localStreamTokenizer.nextToken()) != -1)
      {
        Object localObject = "";
        
        if (ttype == -3)
        {

          String str3 = sval;
          if (str3.endsWith(".")) { str3 = str3.substring(0, str3.length() - 1);
          }
          if (i != 0) localObject = str3; else {
            localObject = getPhoneWord(str3);
          }
        } else if (ttype == OPEN_PHONETIC)
        {
          i = 1;
        }
        else if (ttype == CLOSE_PHONETIC)
        {
          i = 0;
          localObject = str2;
        }
        else if (ttype == PHONETIC_SEPARATOR)
        {
          str2 = str2 + PHONETIC_SEPARATOR;
        }
        else if (ttype == 48)
        {
          localObject = "z|ee|r|oo";
        }
        else if (ttype == 49)
        {
          localObject = "w|o|n";
        }
        else if (ttype == 50)
        {
          localObject = "t|ouu";
        }
        else if (ttype == 51)
        {
          localObject = "th|r|ee";
        }
        else if (ttype == 52)
        {
          localObject = "f|or";
        }
        else if (ttype == 53)
        {
          localObject = "f|ii|v";
        }
        else if (ttype == 54)
        {
          localObject = "s|i|k|s";
        }
        else if (ttype == 55)
        {
          localObject = "s|e|v|e|n";
        }
        else if (ttype == 56)
        {
          localObject = "ay|t";
        }
        else if (ttype == 57)
        {
          localObject = "n|ii|n";
        }
        else if (ttype == 44)
        {
          localObject = "50ms";
        }
        else if (ttype == 46)
        {

          j = localStreamTokenizer.nextToken();
          if (j != -1)
          {
            if ((ttype >= 48) && (ttype <= 57))
            {
              localObject = "p|oy|n|t";
            }
            else if (ttype == -3)
            {
              localObject = "d|o|t";
            } else {
              localObject = "50ms";
            }
            localStreamTokenizer.pushBack();
          }
        }
        else if (ttype == 45)
        {

          j = localStreamTokenizer.nextToken();
          if (j != -1)
          {
            if ((ttype >= 48) && (ttype <= 57))
            {
              localObject = "m|ii|n|u|s";
            }
            else if (ttype == -3)
            {
              localObject = "d|a|sh";
            } else {
              localObject = "50ms";
            }
            localStreamTokenizer.pushBack();
          }
        }
        else if (ttype == 64)
        {
          localObject = "a|t";
        }
        else if (ttype == 47)
        {
          localObject = "s|l|a|sh";
        }
        else if (ttype == 92)
        {
          localObject = "b|a|k|s|l|a|sh";
        }
        else if (ttype == 58)
        {
          localObject = "k|oo|l|o|n";
        }
        else if (ttype == 43)
        {
          localObject = "p|l|u|s";
        }
        
        if (i != 0) { str2 = str2 + (String)localObject;
        } else if (((String)localObject).length() > 0) str1 = str1 + "|10ms|" + (String)localObject;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    
    localStringReader.close();
    
    return str1;
  }
  
  public static String getSpelledPhoneWord(String paramString)
  {
    int i = 1;
    String str1 = "";
    
    for (int j = 0; j < paramString.length(); j++)
    {
      char c = paramString.charAt(j);
      if (Character.isLowerCase(c))
      {
        i = 0;
        break;
      }
      
      String str2 = "";
      if (c == 'A') { str2 = "ay";
      } else if (c == 'B') { str2 = "b|ee";
      } else if (c == 'C') { str2 = "s|ee";
      } else if (c == 'D') { str2 = "d|ee";
      } else if (c == 'E') { str2 = "ee";
      } else if (c == 'F') { str2 = "e|f";
      } else if (c == 'G') { str2 = "j|ee";
      } else if (c == 'H') { str2 = "h|ay|ch";
      } else if (c == 'I') { str2 = "ii";
      } else if (c == 'J') { str2 = "j|ay";
      } else if (c == 'K') { str2 = "k|ay";
      } else if (c == 'L') { str2 = "e|l";
      } else if (c == 'M') { str2 = "e|m";
      } else if (c == 'N') { str2 = "e|n";
      } else if (c == 'O') { str2 = "oo";
      } else if (c == 'P') { str2 = "p|ee";
      } else if (c == 'Q') { str2 = "k|y|ou";
      } else if (c == 'R') { str2 = "ar";
      } else if (c == 'S') { str2 = "e|s";
      } else if (c == 'T') { str2 = "t|ee";
      } else if (c == 'U') { str2 = "y|ou";
      } else if (c == 'V') { str2 = "v|ee";
      } else if (c == 'W') { str2 = "d|u|b|l|y|ou";
      } else if (c == 'X') { str2 = "e|k|s";
      } else if (c == 'Y') { str2 = "w|ii";
      } else if (c == 'Z') { str2 = "z|ee";
      }
      str1 = str1 + "|10ms|" + str2;
    }
    
    if (i != 0) return str1;
    return null;
  }
  
  public static String getPhoneWord(String paramString)
  {
    String str1 = getSpelledPhoneWord(paramString);
    if (str1 != null) { return str1;
    }
    paramString = paramString.toLowerCase();
    

    paramString = replace(paramString, "ties", "t|ii|z");
    paramString = replace(paramString, "centre*", "s|e|n|t|er");
    paramString = replace(paramString, "center*", "s|e|n|t|er");
    paramString = replace(paramString, "speaking", "s|p|ee|k");
    paramString = replace(paramString, "me", "m|ee");
    paramString = replace(paramString, "read", "r|ee|d");
    paramString = replace(paramString, "once", "w|u|n|s");
    paramString = replace(paramString, "query", "k|w|ear|ee");
    paramString = replace(paramString, "he", "h|ee");
    paramString = replace(paramString, "she", "sh|ee");
    paramString = replace(paramString, "my", "m|ii");
    paramString = replace(paramString, "by", "b|ii");
    paramString = replace(paramString, "bye", "b|ii");
    paramString = replace(paramString, "buy", "b|ii");
    paramString = replace(paramString, "lose", "l|ouu|z");
    paramString = replace(paramString, "losing", "l|ouu|z|i|ng");
    paramString = replace(paramString, "engineer*", "e|n|j|i|n|ear");
    paramString = replace(paramString, "because", "b|ee|k|or|z");
    paramString = replace(paramString, "have", "h|a|v");
    paramString = replace(paramString, "over", "oo|v|er");
    paramString = replace(paramString, "one", "w|o|n");
    paramString = replace(paramString, "to", "t|ouu");
    paramString = replace(paramString, "or", "or");
    paramString = replace(paramString, "i", "ii");
    paramString = replace(paramString, "their", "dth|aer");
    paramString = replace(paramString, "you", "y|ou");
    paramString = replace(paramString, "measure", "m|e|zh|or");
    paramString = replace(paramString, "operation*", "o|p|er|ay|sh|o|n");
    

    paramString = replace(paramString, "*sive", "s|i|v");
    paramString = replace(paramString, "*tive", "t|i|v");
    paramString = replace(paramString, "*erties", "er|t|ee|z");
    paramString = replace(paramString, "*ties", "t|ee|z");
    paramString = replace(paramString, "*ic", "i|k");
    paramString = replace(paramString, "*lly", "l|y");
    paramString = replace(paramString, "*ly", "l|y");
    paramString = replace(paramString, "*azing", "ay|z|i|ng");
    paramString = replace(paramString, "*aking", "ay|k|i|ng");
    paramString = replace(paramString, "*ating", "ay|t|i|ng");
    paramString = replace(paramString, "*using", "y|ou|z|i|ng");
    paramString = replace(paramString, "*ising", "ii|z|i|ng");
    paramString = replace(paramString, "*osing", "oo|z|i|ng");
    paramString = replace(paramString, "*izing", "ii|z|i|ng");
    paramString = replace(paramString, "*ozing", "oo|z|i|ng");
    paramString = replace(paramString, "*sation*", "z|ay|sh|o|n");
    paramString = replace(paramString, "*zation*", "z|ay|sh|o|n");
    paramString = replace(paramString, "*sation*", "z|ay|sh|o|n");
    paramString = replace(paramString, "*ction*", "k|sh|o|n");
    paramString = replace(paramString, "*ging*", "j|i|ng");
    paramString = replace(paramString, "*guish*", "g|w|i|sh");
    paramString = replace(paramString, "*ing*", "i|ng");
    paramString = replace(paramString, "*ent*", "e|n|t");
    paramString = replace(paramString, "*ess*", "e|s");
    paramString = replace(paramString, "*quence*", "k|w|e|n|s");
    paramString = replace(paramString, "*nce", "n|s");
    paramString = replace(paramString, "*cy", "s|ee");
    paramString = replace(paramString, "*ised", "ii|z|d");
    paramString = replace(paramString, "*osed", "oo|z|d");
    paramString = replace(paramString, "*used", "y|ou|z|d");
    paramString = replace(paramString, "*ized", "ii|z|d");
    paramString = replace(paramString, "*ozed", "oo|z|d");
    paramString = replace(paramString, "*igned", "ii|n|d");
    paramString = replace(paramString, "*ined", "ii|n|d");
    paramString = replace(paramString, "*aned", "ay|n|d");
    paramString = replace(paramString, "*uned", "ouu|n|d");
    paramString = replace(paramString, "*iting", "ii|t|i|ng");
    paramString = replace(paramString, "*ited", "ii|t|e|d");
    paramString = replace(paramString, "*ated", "ay|t|e|d");
    paramString = replace(paramString, "*ative", "ay|t|ee|v");
    paramString = replace(paramString, "*moved", "m|oo|v|d");
    paramString = replace(paramString, "*move", "m|oo|v");
    paramString = replace(paramString, "*ed", "e|d");
    

    paramString = replace(paramString, "*icit*", "i|s|i|t");
    paramString = replace(paramString, "straight*", "s|t|r|ay|t");
    paramString = replace(paramString, "there*", "dth|aer");
    paramString = replace(paramString, "here*", "h|ear");
    paramString = replace(paramString, "*where*", "wh|aer");
    paramString = replace(paramString, "*experienc*", "e|k|s|p|ear|ee|e|n|s");
    paramString = replace(paramString, "*oice", "oy|s");
    paramString = replace(paramString, "*oic*", "oy|s");
    paramString = replace(paramString, "*even*", "ee|v|e|n");
    paramString = replace(paramString, "*alk*", "or|k");
    paramString = replace(paramString, "pleas*", "p|l|ee|z");
    paramString = replace(paramString, "*leas*", "l|ee|s");
    paramString = replace(paramString, "*eas*", "ee|z");
    paramString = replace(paramString, "signa*", "s|i|g|n|a");
    paramString = replace(paramString, "*signa*", "z|i|g|n|ay");
    paramString = replace(paramString, "sign*", "s|ii|n");
    paramString = replace(paramString, "*esign*", "ee|z|ii|n");
    paramString = replace(paramString, "measur*", "m|e|zh|or");
    paramString = replace(paramString, "*human*", "h|ou|m|a|n");
    paramString = replace(paramString, "*world*", "w|err|l|d");
    paramString = replace(paramString, "*year*", "y|err");
    paramString = replace(paramString, "*cent*", "s|e|n|tt");
    paramString = replace(paramString, "*being*", "b|ee|n|g");
    paramString = replace(paramString, "*ever*", "e|v|e|r");
    paramString = replace(paramString, "*application*", "a|p|l|i|k|ay|sh|o|n");
    paramString = replace(paramString, "*evel*", "e|v|e|l");
    paramString = replace(paramString, "*ted*", "t|e|d");
    paramString = replace(paramString, "*want*", "w|o|n|t");
    paramString = replace(paramString, "*charac*", "k|a|r|a|k");
    paramString = replace(paramString, "*chine*", "sh|ee|n");
    paramString = replace(paramString, "*chin*", "ch|i|n");
    paramString = replace(paramString, "*flue*", "f|l|ou");
    paramString = replace(paramString, "*flu*", "f|l|ou");
    paramString = replace(paramString, "*climax*", "k|l|ii|m|a|k|s");
    paramString = replace(paramString, "*quen*", "k|w|e|n");
    paramString = replace(paramString, "*cator*", "k|ay|t|or");
    paramString = replace(paramString, "*eak*", "ee|k");
    paramString = replace(paramString, "*learn*", "l|er|n");
    paramString = replace(paramString, "*erv*", "er|v");
    paramString = replace(paramString, "*erve*", "er|v");
    paramString = replace(paramString, "*oad*", "oo|d");
    

    paramString = replace(paramString, "*thought*", "th|or|t");
    paramString = replace(paramString, "*though*", "dth|eau");
    paramString = replace(paramString, "*through*", "th|r|ouu");
    paramString = replace(paramString, "drought*", "d|r|ow|tt");
    paramString = replace(paramString, "brought*", "b|r|or|tt");
    paramString = replace(paramString, "*rough*", "|r|u|f");
    paramString = replace(paramString, "*ough*", "ow");
    
    paramString = replace(paramString, "*mould*", "m|oo|l|d");
    paramString = replace(paramString, "*ould*", "u|d");
    paramString = replace(paramString, "*uild", "i|l|d");
    paramString = replace(paramString, "*uild*", "i|l|d");
    
    paramString = replace(paramString, "*eicest*", "e|s|t");
    paramString = replace(paramString, "*icest*", "ii|s|e|s|t");
    
    paramString = replace(paramString, "*igh*", "ii");
    
    paramString = replace(paramString, "*cause*", "k|or|z");
    paramString = replace(paramString, "*aus*", "or|z");
    

    paramString = replace(paramString, "*cious*", "sh|i|s");
    paramString = replace(paramString, "*cial*", "sh|a|l");
    paramString = replace(paramString, "*tia*", "sh|a");
    paramString = replace(paramString, "*tio", "sh|eau");
    paramString = replace(paramString, "*tio*", "sh|o");
    

    paramString = replace(paramString, "uni*", "y|ou|n|ee");
    paramString = replace(paramString, "whil*", "wh|ii|l");
    paramString = replace(paramString, "pn*", "n");
    paramString = replace(paramString, "kn*", "n");
    paramString = replace(paramString, "*dis*", "d|i|s");
    paramString = replace(paramString, "pre*", "p|r|ee");
    

    paramString = replace(paramString, "*que*", "k");
    paramString = replace(paramString, "*qu*", "k|w");
    paramString = replace(paramString, "*q*", "k");
    

    paramString = replace(paramString, "*sky*", "s|k|ii");
    paramString = replace(paramString, "fly*", "f|l|ii");
    

    paramString = replace(paramString, "than", "dth|a|n");
    paramString = replace(paramString, "that", "dth|a|t");
    paramString = replace(paramString, "the", "dth|i");
    paramString = replace(paramString, "thee", "dth|ee");
    paramString = replace(paramString, "them", "dth|e|m");
    paramString = replace(paramString, "then", "dth|e|n");
    paramString = replace(paramString, "thence", "dth|e|n|s");
    paramString = replace(paramString, "there*", "dth|aer");
    paramString = replace(paramString, "they", "dth|ay");
    paramString = replace(paramString, "this", "dth|i|s");
    paramString = replace(paramString, "thus", "dth|u|s");
    

    paramString = replace(paramString, "*th*", "th");
    paramString = replace(paramString, "*ch*", "ch");
    paramString = replace(paramString, "*cl*", "k|l");
    paramString = replace(paramString, "*sh*", "sh");
    paramString = replace(paramString, "*ck*", "k");
    paramString = replace(paramString, "*ct*", "k|t");
    

    paramString = replace(paramString, "*abe*", "ay|b");
    paramString = replace(paramString, "*ace*", "ay|s");
    paramString = replace(paramString, "*ade*", "ay|d");
    paramString = replace(paramString, "*afe*", "ay|f");
    paramString = replace(paramString, "*age*", "ay|g");
    paramString = replace(paramString, "*ake*", "ay|k");
    paramString = replace(paramString, "*ale*", "ay|l");
    paramString = replace(paramString, "*ame*", "ay|m");
    paramString = replace(paramString, "*ane*", "ay|n");
    paramString = replace(paramString, "*ape*", "ay|p");
    paramString = replace(paramString, "*ase*", "ay|s");
    paramString = replace(paramString, "*aze*", "ay|z");
    paramString = replace(paramString, "*ate*", "ay|t");
    paramString = replace(paramString, "*ave*", "ay|v");
    paramString = replace(paramString, "*aze*", "ay|z");
    paramString = replace(paramString, "*azy*", "a|z|y");
    

    paramString = replace(paramString, "*ede*", "ee|d");
    paramString = replace(paramString, "*eke", "ee|k");
    paramString = replace(paramString, "*eme", "ee|m");
    paramString = replace(paramString, "*ere", "err");
    paramString = replace(paramString, "*ere*", "ee|r");
    paramString = replace(paramString, "*ese*", "ee|z");
    paramString = replace(paramString, "*ete", "ay|t");
    paramString = replace(paramString, "*iev*", "ee|v");
    paramString = replace(paramString, "*eve*", "ee|v");
    paramString = replace(paramString, "*eze*", "e|z");
    

    paramString = replace(paramString, "*ibe*", "ii|b");
    paramString = replace(paramString, "*ice*", "ii|s");
    paramString = replace(paramString, "*ide*", "ii|d");
    paramString = replace(paramString, "*ife*", "ii|f");
    paramString = replace(paramString, "*ige*", "ii|g");
    paramString = replace(paramString, "*ike*", "ii|k");
    paramString = replace(paramString, "*ile*", "ii|l");
    paramString = replace(paramString, "*ime*", "ii|m");
    paramString = replace(paramString, "*ine*", "ii|n");
    paramString = replace(paramString, "*ipe*", "ii|p");
    paramString = replace(paramString, "*ire*", "ii|r");
    paramString = replace(paramString, "*ise*", "ii|z");
    paramString = replace(paramString, "*ite*", "ii|t");
    paramString = replace(paramString, "*usive*", "ou|s|i|v");
    paramString = replace(paramString, "*asive*", "ay|s|i|v");
    paramString = replace(paramString, "*ive*", "ii|v");
    paramString = replace(paramString, "*ize*", "ii|z");
    

    paramString = replace(paramString, "*obe*", "oo|b");
    paramString = replace(paramString, "*ode*", "oo|d");
    paramString = replace(paramString, "*oke*", "oo|k");
    paramString = replace(paramString, "*ole*", "oo|l");
    paramString = replace(paramString, "*ome*", "oo|m");
    paramString = replace(paramString, "*one*", "oo|n");
    paramString = replace(paramString, "*ope*", "oo|p");
    paramString = replace(paramString, "*ore*", "or");
    paramString = replace(paramString, "*ose*", "oo|z");
    paramString = replace(paramString, "*ote*", "oo|t");
    paramString = replace(paramString, "*ove*", "oo|v");
    paramString = replace(paramString, "*owe*", "oo");
    paramString = replace(paramString, "*oze*", "oo|z");
    paramString = replace(paramString, "*or*", "or");
    

    paramString = replace(paramString, "*ube*", "ouu|b");
    paramString = replace(paramString, "*uce*", "ouu|c");
    paramString = replace(paramString, "*ude*", "ouu|d");
    paramString = replace(paramString, "*uge*", "ouu|g");
    paramString = replace(paramString, "*uke*", "ouu|k");
    paramString = replace(paramString, "*ule*", "ouu|l");
    paramString = replace(paramString, "*ume*", "ouu|m");
    paramString = replace(paramString, "*uma*", "ouu|m|a");
    paramString = replace(paramString, "*une*", "ouu|n");
    paramString = replace(paramString, "*upe*", "ouu|p");
    paramString = replace(paramString, "*ure*", "ouu|r");
    paramString = replace(paramString, "usa*", "y|ou|z|a");
    paramString = replace(paramString, "use*", "y|ou|z");
    paramString = replace(paramString, "*use*", "ou|z");
    paramString = replace(paramString, "usu*", "y|ou|z|y|u:");
    paramString = replace(paramString, "*ute*", "ou|tt");
    paramString = replace(paramString, "*uze*", "ou|z");
    paramString = replace(paramString, "*ur*", "er");
    

    paramString = replace(paramString, "*oci*", "oo|s|ee");
    paramString = replace(paramString, "*ace*", "ay|s");
    paramString = replace(paramString, "*aci*", "ay|s|ee");
    paramString = replace(paramString, "*ece*", "ee|s");
    paramString = replace(paramString, "*eci*", "e|c|ee");
    

    paramString = replace(paramString, "*air*", "aer");
    paramString = replace(paramString, "all*", "a|l");
    paramString = replace(paramString, "*all*", "or|l");
    paramString = replace(paramString, "*aria*", "aer|y|a");
    paramString = replace(paramString, "*ar*", "ar");
    paramString = replace(paramString, "*ear*", "ear");
    paramString = replace(paramString, "*eak*", "ee|k");
    paramString = replace(paramString, "*eat*", "ee|t");
    paramString = replace(paramString, "*ew", "ou");
    paramString = replace(paramString, "ind*", "i|n|d");
    paramString = replace(paramString, "*ind*", "ii|n|d");
    paramString = replace(paramString, "*ow*", "ow");
    paramString = replace(paramString, "*err*", "e|r");
    paramString = replace(paramString, "*er*", "er");
    

    paramString = replace(paramString, "*aa*", "ar");
    paramString = replace(paramString, "*ae*", "ee");
    paramString = replace(paramString, "*ai*", "ay");
    paramString = replace(paramString, "*au*", "or");
    paramString = replace(paramString, "*ea*", "ee|a");
    paramString = replace(paramString, "*ee*", "ee");
    paramString = replace(paramString, "*ei*", "ee");
    paramString = replace(paramString, "*eo*", "ee|oo");
    paramString = replace(paramString, "*eu*", "ou");
    paramString = replace(paramString, "*ia*", "ee|a");
    paramString = replace(paramString, "*ie*", "ii");
    paramString = replace(paramString, "*io*", "ee|oo");
    paramString = replace(paramString, "*oa*", "oo");
    paramString = replace(paramString, "*oe*", "oo");
    paramString = replace(paramString, "*oi*", "u|y");
    paramString = replace(paramString, "*oo*", "ouu");
    paramString = replace(paramString, "*ou*", "ow");
    paramString = replace(paramString, "*ua*", "y|ou|a");
    paramString = replace(paramString, "*ue*", "y|ou");
    paramString = replace(paramString, "*ui*", "ou|ee");
    

    paramString = replace(paramString, "*bb*", "b");
    paramString = replace(paramString, "*cc*", "k|s");
    paramString = replace(paramString, "*dd*", "d");
    paramString = replace(paramString, "*ff*", "f");
    paramString = replace(paramString, "*gg*", "g");
    paramString = replace(paramString, "*ll*", "l");
    paramString = replace(paramString, "*mm*", "m");
    paramString = replace(paramString, "*nn*", "n");
    paramString = replace(paramString, "*pp*", "p");
    paramString = replace(paramString, "*ss*", "s");
    paramString = replace(paramString, "*tt*", "t");
    paramString = replace(paramString, "*zz*", "z");
    

    paramString = replace(paramString, "*ay*", "ay");
    paramString = replace(paramString, "y*", "y");
    paramString = replace(paramString, "*y*", "y");
    

    paramString = replace(paramString, "*a*", "a");
    paramString = replace(paramString, "*e*", "e");
    paramString = replace(paramString, "*i*", "i");
    paramString = replace(paramString, "*o", "oo");
    paramString = replace(paramString, "*q*", "k|w");
    paramString = replace(paramString, "*u*", "u");
    

    paramString = replace(paramString, "b*", "b");
    paramString = replace(paramString, "*b*", "b");
    paramString = replace(paramString, "*c*", "k");
    paramString = replace(paramString, "d*", "d");
    paramString = replace(paramString, "*d", "d");
    paramString = replace(paramString, "*ds", "d|z");
    paramString = replace(paramString, "*d*", "d");
    paramString = replace(paramString, "f*", "f");
    paramString = replace(paramString, "*f*", "f");
    paramString = replace(paramString, "g*", "g");
    paramString = replace(paramString, "*g*", "g");
    paramString = replace(paramString, "h*", "h");
    paramString = replace(paramString, "*h*", "h");
    paramString = replace(paramString, "*j*", "j");
    paramString = replace(paramString, "k*", "k");
    paramString = replace(paramString, "*k*", "k");
    paramString = replace(paramString, "l*", "l");
    paramString = replace(paramString, "*l*", "l");
    paramString = replace(paramString, "m*", "m");
    paramString = replace(paramString, "*m*", "m");
    paramString = replace(paramString, "n*", "n");
    paramString = replace(paramString, "*n", "n");
    paramString = replace(paramString, "*n*", "n");
    paramString = replace(paramString, "p*", "p");
    paramString = replace(paramString, "*p*", "p");
    paramString = replace(paramString, "r*", "r");
    paramString = replace(paramString, "*r*", "r");
    paramString = replace(paramString, "*s*", "s");
    paramString = replace(paramString, "t*", "t");
    paramString = replace(paramString, "*t*", "t");
    paramString = replace(paramString, "v*", "v");
    paramString = replace(paramString, "*v*", "v");
    paramString = replace(paramString, "w*", "w");
    paramString = replace(paramString, "*w*", "w");
    paramString = replace(paramString, "*x*", "k|s");
    paramString = replace(paramString, "*z*", "z");
    
    String str2 = "";
    

    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "[]");
    while (localStringTokenizer.hasMoreTokens())
    {
      str2 = str2 + localStringTokenizer.nextToken();
      if (localStringTokenizer.hasMoreTokens()) { str2 = str2 + "|";
      }
    }
    return str2;
  }
  

  private static String replace(String paramString1, String paramString2, String paramString3)
  {
    String str1 = paramString1;
    



    if ((paramString2.startsWith("*")) && (paramString2.endsWith("*")))
    {
      int i = 0;
      

      paramString2 = paramString2.substring(1, paramString2.length() - 1);
      
      int j = 0;
      int k = 1;
      

      while (k != 0)
      {
        int m = paramString1.indexOf(paramString2, j);
        if (m >= 0)
        {
          int n = 1;
          

          int i1 = m;
          while (i1 >= 0)
          {
            String str2 = paramString1.substring(i1, i1 + 1);
            if (str2.equals("["))
            {
              n = 0;
              break;
            }
            if (str2.equals("]")) {
              break;
            }
            
            i1--;
          }
          
          if (n != 0)
          {
            str1 = "";
            if (m > 0) str1 = str1 + paramString1.substring(0, m);
            str1 = str1 + "[" + paramString3 + "]";
            if (m + paramString2.length() < paramString1.length()) { str1 = str1 + paramString1.substring(m + paramString2.length(), paramString1.length());
            }
            k = 0;
            i = 1;
          }
          else
          {
            j = m + 1;
          }
        }
        else
        {
          k = 0;
        }
      }
      

      if (i != 0) str1 = replace(str1, "*" + paramString2 + "*", paramString3);
    }
    else if (paramString2.endsWith("*"))
    {

      paramString2 = paramString2.substring(0, paramString2.length() - 1);
      if (paramString1.startsWith(paramString2))
      {
        str1 = "[" + paramString3 + "]" + paramString1.substring(paramString2.length(), paramString1.length());
      }
    }
    else if (paramString2.startsWith("*"))
    {

      paramString2 = paramString2.substring(1, paramString2.length());
      if (paramString1.endsWith(paramString2))
      {
        str1 = paramString1.substring(0, paramString1.length() - paramString2.length()) + "[" + paramString3 + "]";
      }
    }
    else if (paramString1.equals(paramString2))
    {

      str1 = "[" + paramString3 + "]";
    }
    return str1;
  }
  
  public LOTONtext() {}
}
