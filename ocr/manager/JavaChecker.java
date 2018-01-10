package ocr.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
































































































































































































































































































public class JavaChecker
{
  public static void TestDocumentCheck(String dfile)
  {
    int range = 1;
    


    try
    {
      fr = new BufferedReader(new FileReader(dfile));
    }
    catch (FileNotFoundException e) {
      BufferedReader fr;
      System.out.println("File " + dfile + " not found."); return;
    }
    BufferedReader fr;
    try {
      fw = new PrintStream(new FileOutputStream(dfile + ".checked"));
    }
    catch (IOException e) {
      PrintStream fw;
      e.printStackTrace();
      System.out.println("Error: " + e); return;
    }
    PrintStream fw;
    try
    {
      int TotalWordNo = 0;
      String rstr = fr.readLine();
      while (rstr != null) {
        StringTokenizer st = new StringTokenizer(rstr);
        TotalWordNo += st.countTokens();
        rstr = fr.readLine();
      }
      fr.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("Error: " + e); return; }
    int TotalWordNo;
    String rstr;
    String[] totalWord = new String[TotalWordNo];
    try {
      fr = new BufferedReader(new FileReader(dfile));
      int id = 0;
      rstr = fr.readLine();
      while (rstr != null) {
        StringTokenizer st = new StringTokenizer(rstr);
        int index = st.countTokens();
        for (int i = 0; i < index; i++) {
          totalWord[id] = st.nextToken();
          id++;
        }
        rstr = fr.readLine();
      }
      fr.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("Error: " + e); return;
    }
    
    int id;
    String[] tstr = new String[3 * range];
    int[] gpNumber = new int[range];
    int index = 0;
    int qwno = 0;
    for (int i = 0; i < TotalWordNo; i++)
    {
      WordList swdResult = CheckSingleWord(totalWord[i]);
      if (nWord == -1) {
        if (qwno >= range) {
          for (int j = 0; j < gpNumber[0]; j++)
            fw.print(tstr[j] + " ");
          for (j = 0; j < index - gpNumber[0]; j++)
            tstr[j] = tstr[(j + gpNumber[0])];
          index -= gpNumber[0];
          for (j = 0; j < qwno - 1; j++)
            gpNumber[j] = gpNumber[(j + 1)];
          gpNumber[(qwno - 1)] = 1;
          if (withPreStr) {
            gpNumber[(qwno - 1)] += 1;
            tstr[index] = preStr;
            index++;
          }
          tstr[index] = pWord[0];
          index++;
          if (withPosStr) {
            gpNumber[(qwno - 1)] += 1;
            tstr[index] = posStr;
            index++;
          }
        }
        else {
          gpNumber[qwno] = 1;
          if (withPreStr) {
            gpNumber[qwno] += 1;
            tstr[index] = preStr;
            index++;
          }
          tstr[index] = pWord[0];
          index++;
          if (withPosStr) {
            gpNumber[qwno] += 1;
            tstr[index] = posStr;
            index++;
          }
          qwno++;
        }
      }
      else
      {
        String[] groupWord = new String[range * 2 + 1];
        id = 0;
        int mwid = range;
        for (int j = i - range; j <= i + range; j++) {
          if ((j >= 0) && (j < TotalWordNo)) {
            groupWord[id] = totalWord[j];
            if (j == i) mwid = id;
            id++;
          }
        }
        MultiWordList mtw = CheckMultipleWord(groupWord, id, mwid);
        System.out.println("wtwResult: " + ckResult);
        switch (ckResult)
        {

        case 4: 
          gpNumber[0] = 1;
          qwno = 1;
          for (j = 0; j < pList[0].length - 1; j++)
            fw.print(pList[0][j] + " ");
          tstr[0] = pList[0][(pList[0].length - 1)];
          index = 1;
          i += range;
          break;
        case 5: 
        case 9: 
          if (qwno >= range) {
            for (j = 0; j < gpNumber[0]; j++)
              fw.print(tstr[j] + " ");
            for (j = 0; j < index - gpNumber[0]; j++)
              tstr[j] = tstr[(j + gpNumber[0])];
            index -= gpNumber[0];
            tstr[index] = pList[0][mwid];
            index++;
            for (j = 0; j < qwno - 1; j++)
              gpNumber[j] = gpNumber[(j + 1)];
            gpNumber[(qwno - 1)] = 1;
          }
          else {
            tstr[index] = pList[0][mwid];
            index++;
            gpNumber[qwno] = 1;
            qwno++;
          }
          break;
        case 6: 
        case 7: 
        case 10: 
        case 11: 
          if (qwno >= range) {
            for (j = 0; j < gpNumber[0]; j++)
              fw.print(tstr[j] + " ");
            for (j = 0; j < index - gpNumber[0]; j++)
              tstr[j] = tstr[(j + gpNumber[0])];
            index -= gpNumber[0];
            tstr[index] = pList[0][mwid];
            index++;
            tstr[index] = pList[0][(mwid + 1)];
            index++;
            for (j = 0; j < qwno - 1; j++)
              gpNumber[j] = gpNumber[(j + 1)];
            gpNumber[(qwno - 1)] = 2;
          }
          else {
            tstr[index] = pList[0][mwid];
            index++;
            tstr[index] = pList[0][(mwid + 1)];
            index++;
            gpNumber[qwno] = 2;
            qwno++;
          }
          break;
        case 8: 
        case 12: 
          if (qwno >= range) {
            for (j = 0; j < gpNumber[0]; j++)
              fw.print(tstr[j] + " ");
            for (j = 0; j < index - gpNumber[0]; j++)
              tstr[j] = tstr[(j + gpNumber[0])];
            index -= gpNumber[0];
            tstr[index] = pList[0][mwid];
            index++;
            tstr[index] = pList[0][(mwid + 1)];
            index++;
            tstr[index] = pList[0][(mwid + 2)];
            index++;
            for (j = 0; j < qwno - 1; j++)
              gpNumber[j] = gpNumber[(j + 1)];
            gpNumber[(qwno - 1)] = 3;
          }
          else {
            tstr[index] = pList[0][mwid];
            index++;
            tstr[index] = pList[0][(mwid + 1)];
            index++;
            tstr[index] = pList[0][(mwid + 2)];
            index++;
            gpNumber[qwno] = 3;
            qwno++;
          }
          break;
        default: 
          System.out.println("Do not know the multiple checking result.");
        }
        
      }
    }
    if (index > 0) {
      for (i = 0; i < index; i++) {
        fw.print(tstr[i] + " ");
      }
      qwno = 0;
      index = 0;
    }
    fw.close();
  }
  
  static
  {
    System.load(System.getProperty("user.dir") + File.separatorChar + "bin" + File.separatorChar + "execute" + File.separatorChar + "Segmentation" + File.separatorChar + "BridgeChecker.dll");
  }
  
  public static void main(String[] args)
  {
    System.out.println("In the main");
    

    JavaChecker mychecker = new JavaChecker();
    String dataDirStr = "C:" + File.separatorChar + "sumod" + File.separatorChar + "Tides" + File.separatorChar + "bin" + File.separatorChar + "Dictionary" + File.separatorChar + "data";
    String dictDirStr = "C:" + File.separatorChar + "sumod" + File.separatorChar + "Tides" + File.separatorChar + "bin" + File.separatorChar + "Dictionary" + File.separatorChar + "dict";
    String dictStr = "en_CB";
    SetDataDirectory(dataDirStr);
    SetDictDirectory(dictDirStr);
    DictionarySetup(dictStr);
    CheckDocument("C:" + File.separatorChar + "sumod" + File.separatorChar + "Tides" + File.separatorChar + "testfiles" + File.separatorChar + "testfile_2.txt", 1);
    TestDocumentCheck("C:" + File.separatorChar + "sumod" + File.separatorChar + "Tides" + File.separatorChar + "testfiles" + File.separatorChar + "testfile_2.txt");
    











    System.out.println("Check multiple word.");
    
    String[] tstr = { "ad", "jective", "Baggage", "paper" };
    MultiWordList clist = CheckMultipleWord(tstr, 4, 1);
    System.out.println(ckResult);
    System.out.println(nList);
    



    for (int i = 0; i < nList; i++) {
      for (int j = 0; j < pList[i].length; j++) {
        System.out.print(pList[i][j] + " ");
      }
      System.out.println(" ");
    }
  }
  
  public JavaChecker() {}
  
  public static native void AddToPersonalList(String paramString);
  
  public static native void CheckDocument(String paramString, int paramInt);
  
  public static native WordList CheckSingleWord(String paramString);
  
  public static native MultiWordList CheckMultipleWord(String[] paramArrayOfString, int paramInt1, int paramInt2);
  
  public static native int CreateNewDictionary(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt, String paramString3);
  
  public static native void IgnoreWord(String paramString);
  
  public static native void DictionarySetup(String paramString);
  
  public static native void SaveIgnoreWordList(String paramString);
  
  public static native void SavePersonalWordList(String paramString);
  
  public static native void SetDataDirectory(String paramString);
  
  public static native void SetDictDirectory(String paramString);
  
  public static native void SetIgnoreWordList(String paramString);
  
  public static native void SetPersonalWordList(String paramString);
}
