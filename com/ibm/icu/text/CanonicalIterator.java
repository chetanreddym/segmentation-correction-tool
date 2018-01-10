package com.ibm.icu.text;

import com.ibm.icu.impl.NormalizerImpl;
import com.ibm.icu.impl.USerializedSet;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



































public final class CanonicalIterator
{
  public CanonicalIterator(String source)
  {
    setSource(source);
  }
  




  public String getSource()
  {
    return source;
  }
  



  public void reset()
  {
    done = false;
    for (int i = 0; i < current.length; i++) {
      current[i] = 0;
    }
  }
  






  public String next()
  {
    if (done) { return null;
    }
    

    buffer.setLength(0);
    for (int i = 0; i < pieces.length; i++) {
      buffer.append(pieces[i][current[i]]);
    }
    String result = buffer.toString();
    


    int i = current.length - 1; for (goto 74;; i--) {
      if (i < 0) {
        done = true;
        break;
      }
      current[i] += 1;
      if (current[i] < pieces[i].length) break;
      current[i] = 0;
    }
    return result;
  }
  





  public void setSource(String newSource)
  {
    source = Normalizer.normalize(newSource, Normalizer.NFD);
    done = false;
    

    if (newSource.length() == 0) {
      pieces = new String[1][];
      current = new int[1];
      pieces[0] = { "" };
      return;
    }
    

    List list = new ArrayList();
    
    int start = 0;
    


    int i = UTF16.findOffsetFromCodePoint(source, 1);
    for (; 
        i < source.length(); i += UTF16.getCharCount(i)) {
      int cp = UTF16.charAt(source, i);
      if (NormalizerImpl.isCanonSafeStart(cp)) {
        list.add(source.substring(start, i));
        start = i;
      }
    }
    list.add(source.substring(start, i));
    

    pieces = new String[list.size()][];
    current = new int[list.size()];
    for (i = 0; i < pieces.length; i++) {
      if (PROGRESS) System.out.println("SEGMENT");
      pieces[i] = getEquivalents((String)list.get(i));
    }
  }
  












  public static void permute(String source, boolean skipZeros, Set output)
  {
    if ((source.length() <= 2) && (UTF16.countCodePoint(source) <= 1)) {
      output.add(source);
      return;
    }
    

    Set subpermute = new HashSet();
    int cp;
    for (int i = 0; i < source.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(source, i);
      



      if ((!skipZeros) || (i == 0) || (UCharacter.getCombiningClass(cp) != 0))
      {




        subpermute.clear();
        permute(source.substring(0, i) + source.substring(i + UTF16.getCharCount(cp)), skipZeros, subpermute);
        


        String chStr = UTF16.valueOf(source, i);
        Iterator it = subpermute.iterator();
        while (it.hasNext()) {
          String piece = chStr + (String)it.next();
          
          output.add(piece);
        }
      }
    }
  }
  
























  private static boolean PROGRESS = false;
  
  private static boolean SKIP_ZEROS = true;
  

  private String source;
  

  private boolean done;
  
  private String[][] pieces;
  
  private int[] current;
  
  private transient StringBuffer buffer = new StringBuffer();
  

  private String[] getEquivalents(String segment)
  {
    Set result = new HashSet();
    Set basic = getEquivalents2(segment);
    Set permutations = new HashSet();
    



    Iterator it = basic.iterator();
    Iterator it2; for (; it.hasNext(); 
        



        it2.hasNext())
    {
      String item = (String)it.next();
      permutations.clear();
      permute(item, SKIP_ZEROS, permutations);
      it2 = permutations.iterator();
      continue;
      String possible = (String)it2.next();
      




      if (Normalizer.compare(possible, segment, 0) == 0)
      {
        if (PROGRESS) System.out.println("Adding Permutation: " + Utility.hex(possible));
        result.add(possible);

      }
      else if (PROGRESS) { System.out.println("-Skipping Permutation: " + Utility.hex(possible));
      }
    }
    


    String[] finalResult = new String[result.size()];
    result.toArray(finalResult);
    return finalResult;
  }
  

  private Set getEquivalents2(String segment)
  {
    Set result = new HashSet();
    
    if (PROGRESS) { System.out.println("Adding: " + Utility.hex(segment));
    }
    result.add(segment);
    StringBuffer workingBuffer = new StringBuffer();
    

    int cp = 0;int end = 0;
    int[] range = new int[2];
    for (int i = 0; i < segment.length(); i += UTF16.getCharCount(cp))
    {

      cp = UTF16.charAt(segment, i);
      USerializedSet starts = new USerializedSet();
      
      if (NormalizerImpl.getCanonStartSet(cp, starts))
      {

        int j = 0;
        
        j = 0; for (cp = end + 1; (cp <= end) || (starts.getRange(j++, range)); cp++) {
          if (cp > end) {
            cp = range[0];
            end = range[1];
          }
          
          Set remainder = extract(cp, segment, i, workingBuffer);
          if (remainder != null)
          {

            String prefix = segment.substring(0, i);
            prefix = prefix + UTF16.valueOf(cp);
            
            Iterator iter = remainder.iterator();
            while (iter.hasNext()) {
              String item = (String)iter.next();
              String toAdd = new String(prefix);
              toAdd = toAdd + item;
              result.add(toAdd);
            }
          }
        }
      }
    }
    return result;
  }
  








































  private Set extract(int comp, String segment, int segmentPos, StringBuffer buffer)
  {
    if (PROGRESS) { System.out.println(" extract: " + Utility.hex(UTF16.valueOf(comp)) + ", " + Utility.hex(segment.substring(segmentPos)));
    }
    

    String decomp = Normalizer.normalize(comp, Normalizer.NFD);
    

    boolean ok = false;
    
    int decompPos = 0;
    int decompCp = UTF16.charAt(decomp, 0);
    decompPos += UTF16.getCharCount(decompCp);
    
    buffer.setLength(0);
    int cp;
    for (int i = segmentPos; i < segment.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(segment, i);
      if (cp == decompCp) {
        if (PROGRESS) System.out.println("  matches: " + Utility.hex(UTF16.valueOf(cp)));
        if (decompPos == decomp.length()) {
          buffer.append(segment.substring(i + UTF16.getCharCount(cp)));
          ok = true;
          break;
        }
        decompCp = UTF16.charAt(decomp, decompPos);
        decompPos += UTF16.getCharCount(decompCp);
      }
      else {
        if (PROGRESS) { System.out.println("  buffer: " + Utility.hex(UTF16.valueOf(cp)));
        }
        UTF16.append(buffer, cp);
      }
    }
    










    if (!ok) return null;
    if (PROGRESS) System.out.println("Matches");
    if (buffer.length() == 0) return SET_WITH_NULL_STRING;
    String remainder = buffer.toString();
    







    if (0 != Normalizer.compare(UTF16.valueOf(comp) + remainder, segment.substring(segmentPos), 0)) { return null;
    }
    
    return getEquivalents2(remainder);
  }
  











  private static final UnicodeSet EMPTY = new UnicodeSet();
  private static final Set SET_WITH_NULL_STRING = new HashSet();
  
  static { SET_WITH_NULL_STRING.add(""); }
}
