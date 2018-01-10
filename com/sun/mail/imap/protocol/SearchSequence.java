package com.sun.mail.imap.protocol;

import com.sun.mail.iap.Argument;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.search.AddressTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.IntegerComparisonTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;
import javax.mail.search.SizeTerm;
import javax.mail.search.StringTerm;
import javax.mail.search.SubjectTerm;

class SearchSequence
{
  static Argument generateSequence(SearchTerm paramSearchTerm, String paramString) throws SearchException, IOException
  {
    if ((paramSearchTerm instanceof AndTerm))
      return and((AndTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof OrTerm))
      return or((OrTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof NotTerm))
      return not((NotTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof HeaderTerm))
      return header((HeaderTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof FlagTerm))
      return flag((FlagTerm)paramSearchTerm);
    Object localObject; if ((paramSearchTerm instanceof FromTerm)) {
      localObject = (FromTerm)paramSearchTerm;
      return from(((AddressTerm)localObject).getAddress().toString(), paramString);
    }
    if ((paramSearchTerm instanceof FromStringTerm)) {
      localObject = (FromStringTerm)paramSearchTerm;
      return from(((StringTerm)localObject).getPattern(), paramString);
    }
    if ((paramSearchTerm instanceof RecipientTerm)) {
      localObject = (RecipientTerm)paramSearchTerm;
      return recipient(((RecipientTerm)localObject).getRecipientType(), 
        ((AddressTerm)localObject).getAddress().toString(), 
        paramString);
    }
    if ((paramSearchTerm instanceof RecipientStringTerm)) {
      localObject = (RecipientStringTerm)paramSearchTerm;
      return recipient(((RecipientStringTerm)localObject).getRecipientType(), 
        ((StringTerm)localObject).getPattern(), 
        paramString);
    }
    if ((paramSearchTerm instanceof SubjectTerm))
      return subject((SubjectTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof BodyTerm))
      return body((BodyTerm)paramSearchTerm, paramString);
    if ((paramSearchTerm instanceof SizeTerm))
      return size((SizeTerm)paramSearchTerm);
    if ((paramSearchTerm instanceof javax.mail.search.SentDateTerm))
      return sentdate((javax.mail.search.SentDateTerm)paramSearchTerm);
    if ((paramSearchTerm instanceof ReceivedDateTerm))
      return receiveddate((ReceivedDateTerm)paramSearchTerm);
    if ((paramSearchTerm instanceof MessageIDTerm)) {
      return messageid((MessageIDTerm)paramSearchTerm, paramString);
    }
    throw new SearchException("Search too complex");
  }
  



  static boolean isAscii(SearchTerm paramSearchTerm)
  {
    if (((paramSearchTerm instanceof AndTerm)) || ((paramSearchTerm instanceof OrTerm))) {
      SearchTerm[] arrayOfSearchTerm;
      if ((paramSearchTerm instanceof AndTerm)) {
        arrayOfSearchTerm = ((AndTerm)paramSearchTerm).getTerms();
      } else {
        arrayOfSearchTerm = ((OrTerm)paramSearchTerm).getTerms();
      }
      for (int i = 0; i < arrayOfSearchTerm.length; i++)
        if (!isAscii(arrayOfSearchTerm[i]))
          return false;
    } else { if ((paramSearchTerm instanceof NotTerm))
        return isAscii(((NotTerm)paramSearchTerm).getTerm());
      if ((paramSearchTerm instanceof StringTerm))
        return isAscii(((StringTerm)paramSearchTerm).getPattern());
      if ((paramSearchTerm instanceof AddressTerm)) {
        return isAscii(((AddressTerm)paramSearchTerm).getAddress().toString());
      }
    }
    return true;
  }
  
  private static boolean isAscii(String paramString) {
    int i = paramString.length();
    
    for (int j = 0; j < i; j++) {
      if (paramString.charAt(j) > '')
        return false;
    }
    return true;
  }
  
  private static Argument and(AndTerm paramAndTerm, String paramString)
    throws SearchException, IOException
  {
    SearchTerm[] arrayOfSearchTerm = paramAndTerm.getTerms();
    
    Argument localArgument = generateSequence(arrayOfSearchTerm[0], paramString);
    
    for (int i = 1; i < arrayOfSearchTerm.length; i++)
      localArgument.append(generateSequence(arrayOfSearchTerm[i], paramString));
    return localArgument;
  }
  
  private static Argument or(OrTerm paramOrTerm, String paramString) throws SearchException, IOException
  {
    SearchTerm[] arrayOfSearchTerm = paramOrTerm.getTerms();
    




    if (arrayOfSearchTerm.length > 2) {
      localObject = arrayOfSearchTerm[0];
      

      for (int i = 1; i < arrayOfSearchTerm.length; i++) {
        localObject = new OrTerm((SearchTerm)localObject, arrayOfSearchTerm[i]);
      }
      paramOrTerm = (OrTerm)localObject;
      
      arrayOfSearchTerm = paramOrTerm.getTerms();
    }
    

    Object localObject = new Argument();
    

    ((Argument)localObject).writeAtom("OR");
    





    if (((arrayOfSearchTerm[0] instanceof AndTerm)) || ((arrayOfSearchTerm[0] instanceof FlagTerm))) {
      ((Argument)localObject).writeArgument(generateSequence(arrayOfSearchTerm[0], paramString));
    } else {
      ((Argument)localObject).append(generateSequence(arrayOfSearchTerm[0], paramString));
    }
    
    if (((arrayOfSearchTerm[1] instanceof AndTerm)) || ((arrayOfSearchTerm[1] instanceof FlagTerm))) {
      ((Argument)localObject).writeArgument(generateSequence(arrayOfSearchTerm[1], paramString));
    } else {
      ((Argument)localObject).append(generateSequence(arrayOfSearchTerm[1], paramString));
    }
    return localObject;
  }
  
  private static Argument not(NotTerm paramNotTerm, String paramString) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    

    localArgument.writeAtom("NOT");
    





    SearchTerm localSearchTerm = paramNotTerm.getTerm();
    if (((localSearchTerm instanceof AndTerm)) || ((localSearchTerm instanceof FlagTerm))) {
      localArgument.writeArgument(generateSequence(localSearchTerm, paramString));
    } else {
      localArgument.append(generateSequence(localSearchTerm, paramString));
    }
    return localArgument;
  }
  
  private static Argument header(HeaderTerm paramHeaderTerm, String paramString) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    localArgument.writeAtom("HEADER");
    localArgument.writeString(paramHeaderTerm.getHeaderName());
    localArgument.writeString(paramHeaderTerm.getPattern(), paramString);
    return localArgument;
  }
  
  private static Argument messageid(MessageIDTerm paramMessageIDTerm, String paramString) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    localArgument.writeAtom("HEADER");
    localArgument.writeString("Message-ID");
    
    localArgument.writeString(paramMessageIDTerm.getPattern(), paramString);
    return localArgument;
  }
  
  private static Argument flag(FlagTerm paramFlagTerm) throws SearchException {
    boolean bool = paramFlagTerm.getTestSet();
    
    Argument localArgument = new Argument();
    
    Flags localFlags = paramFlagTerm.getFlags();
    Flags.Flag[] arrayOfFlag = localFlags.getSystemFlags();
    String[] arrayOfString = localFlags.getUserFlags();
    if ((arrayOfFlag.length == 0) && (arrayOfString.length == 0)) {
      throw new SearchException("Invalid FlagTerm");
    }
    for (int i = 0; i < arrayOfFlag.length; i++) {
      if (arrayOfFlag[i] == Flags.Flag.DELETED) {
        localArgument.writeAtom(bool ? "DELETED" : "UNDELETED");
      } else if (arrayOfFlag[i] == Flags.Flag.ANSWERED) {
        localArgument.writeAtom(bool ? "ANSWERED" : "UNANSWERED");
      } else if (arrayOfFlag[i] == Flags.Flag.DRAFT) {
        localArgument.writeAtom(bool ? "DRAFT" : "UNDRAFT");
      } else if (arrayOfFlag[i] == Flags.Flag.FLAGGED) {
        localArgument.writeAtom(bool ? "FLAGGED" : "UNFLAGGED");
      } else if (arrayOfFlag[i] == Flags.Flag.RECENT) {
        localArgument.writeAtom(bool ? "RECENT" : "OLD");
      } else if (arrayOfFlag[i] == Flags.Flag.SEEN) {
        localArgument.writeAtom(bool ? "SEEN" : "UNSEEN");
      }
    }
    for (int j = 0; j < arrayOfString.length; j++) {
      localArgument.writeAtom(bool ? "KEYWORD" : "UNKEYWORD");
      localArgument.writeAtom(arrayOfString[j]);
    }
    
    return localArgument;
  }
  
  private static Argument from(String paramString1, String paramString2) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    localArgument.writeAtom("FROM");
    localArgument.writeString(paramString1, paramString2);
    return localArgument;
  }
  
  private static Argument recipient(Message.RecipientType paramRecipientType, String paramString1, String paramString2)
    throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    
    if (paramRecipientType == Message.RecipientType.TO) {
      localArgument.writeAtom("TO");
    } else if (paramRecipientType == Message.RecipientType.CC) {
      localArgument.writeAtom("CC");
    } else if (paramRecipientType == Message.RecipientType.BCC) {
      localArgument.writeAtom("BCC");
    } else {
      throw new SearchException("Illegal Recipient type");
    }
    localArgument.writeString(paramString1, paramString2);
    return localArgument;
  }
  
  private static Argument subject(SubjectTerm paramSubjectTerm, String paramString) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    
    localArgument.writeAtom("SUBJECT");
    localArgument.writeString(paramSubjectTerm.getPattern(), paramString);
    return localArgument;
  }
  
  private static Argument body(BodyTerm paramBodyTerm, String paramString) throws SearchException, IOException
  {
    Argument localArgument = new Argument();
    
    localArgument.writeAtom("BODY");
    localArgument.writeString(paramBodyTerm.getPattern(), paramString);
    return localArgument;
  }
  
  private static Argument size(SizeTerm paramSizeTerm) throws SearchException
  {
    Argument localArgument = new Argument();
    
    switch (paramSizeTerm.getComparison()) {
    case 5: 
      localArgument.writeAtom("LARGER");
      break;
    case 2: 
      localArgument.writeAtom("SMALLER");
      break;
    
    default: 
      throw new SearchException("Cannot handle Comparison");
    }
    
    localArgument.writeNumber(paramSizeTerm.getNumber());
    return localArgument;
  }
  











  private static String[] monthTable = {
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", 
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
  


  private static Calendar cal = new java.util.GregorianCalendar();
  
  private static String toIMAPDate(Date paramDate) {
    StringBuffer localStringBuffer = new StringBuffer();
    cal.setTime(paramDate);
    
    localStringBuffer.append(cal.get(5)).append("-");
    localStringBuffer.append(monthTable[cal.get(2)]).append('-');
    localStringBuffer.append(cal.get(1));
    
    return localStringBuffer.toString();
  }
  
  private static Argument sentdate(DateTerm paramDateTerm) throws SearchException
  {
    Argument localArgument = new Argument();
    String str = toIMAPDate(paramDateTerm.getDate());
    
    switch (paramDateTerm.getComparison()) {
    case 5: 
      localArgument.writeAtom("SENTSINCE " + str);
      break;
    case 3: 
      localArgument.writeAtom("SENTON " + str);
      break;
    case 2: 
      localArgument.writeAtom("SENTBEFORE " + str);
      break;
    case 6: 
      localArgument.writeAtom("OR SENTSINCE " + str + " SENTON " + str);
      break;
    case 1: 
      localArgument.writeAtom("OR SENTBEFORE " + str + " SENTON " + str);
      break;
    case 4: 
      localArgument.writeAtom("NOT SENTON " + str);
      break;
    default: 
      throw new SearchException("Cannot handle Date Comparison");
    }
    
    return localArgument;
  }
  
  private static Argument receiveddate(DateTerm paramDateTerm) throws SearchException
  {
    Argument localArgument = new Argument();
    String str = toIMAPDate(paramDateTerm.getDate());
    
    switch (paramDateTerm.getComparison()) {
    case 5: 
      localArgument.writeAtom("SINCE " + str);
      break;
    case 3: 
      localArgument.writeAtom("ON " + str);
      break;
    case 2: 
      localArgument.writeAtom("BEFORE " + str);
      break;
    case 6: 
      localArgument.writeAtom("OR SINCE " + str + " ON " + str);
      break;
    case 1: 
      localArgument.writeAtom("OR BEFORE " + str + " ON " + str);
      break;
    case 4: 
      localArgument.writeAtom("NOT ON " + str);
      break;
    default: 
      throw new SearchException("Cannot handle Date Comparison");
    }
    
    return localArgument;
  }
  
  SearchSequence() {}
}
