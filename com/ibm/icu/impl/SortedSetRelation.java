package com.ibm.icu.impl;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;






































public class SortedSetRelation
{
  public static final int A_NOT_B = 4;
  public static final int A_AND_B = 2;
  public static final int B_NOT_A = 1;
  public static final int ANY = 7;
  public static final int CONTAINS = 6;
  public static final int DISJOINT = 5;
  public static final int ISCONTAINED = 3;
  public static final int NO_B = 4;
  public static final int EQUALS = 2;
  public static final int NO_A = 1;
  public static final int NONE = 0;
  public static final int ADDALL = 7;
  public static final int A = 6;
  public static final int COMPLEMENTALL = 5;
  public static final int B = 3;
  public static final int REMOVEALL = 4;
  public static final int RETAINALL = 2;
  public static final int B_REMOVEALL = 1;
  
  public SortedSetRelation() {}
  
  public static boolean hasRelation(SortedSet a, int allow, SortedSet b)
  {
    if ((allow < 0) || (allow > 7)) {
      throw new IllegalArgumentException("Relation " + allow + " out of range");
    }
    



    boolean anb = (allow & 0x4) != 0;
    boolean ab = (allow & 0x2) != 0;
    boolean bna = (allow & 0x1) != 0;
    

    switch (allow) {
    case 6:  if (a.size() < b.size()) return false;
      break; case 3:  if (a.size() > b.size()) return false;
      break; case 2:  if (a.size() != b.size()) { return false;
      }
      break;
    }
    if (a.size() == 0) {
      if (b.size() == 0) return true;
      return bna; }
    if (b.size() == 0) {
      return anb;
    }
    

    Iterator ait = a.iterator();
    Iterator bit = b.iterator();
    
    Comparable aa = (Comparable)ait.next();
    Comparable bb = (Comparable)bit.next();
    for (;;)
    {
      int comp = aa.compareTo(bb);
      if (comp == 0) {
        if (!ab) return false;
        if (!ait.hasNext()) {
          if (!bit.hasNext()) return true;
          return bna; }
        if (!bit.hasNext()) {
          return anb;
        }
        aa = (Comparable)ait.next();
        bb = (Comparable)bit.next();
      } else if (comp < 0) {
        if (!anb) return false;
        if (!ait.hasNext()) {
          return bna;
        }
        aa = (Comparable)ait.next();
      } else {
        if (!bna) return false;
        if (!bit.hasNext()) {
          return anb;
        }
        bb = (Comparable)bit.next();
      }
    }
  }
  




  public static SortedSet doOperation(SortedSet a, int relation, SortedSet b)
  {
    TreeSet temp;
    



    switch (relation) {
    case 7: 
      a.addAll(b);
      return a;
    case 6: 
      return a;
    case 3: 
      a.clear();
      a.addAll(b);
      return a;
    case 4: 
      a.removeAll(b);
      return a;
    case 2: 
      a.retainAll(b);
      return a;
    

    case 5: 
      temp = new TreeSet(b);
      temp.removeAll(a);
      a.removeAll(b);
      a.addAll(temp);
      return a;
    case 1: 
      temp = new TreeSet(b);
      temp.removeAll(a);
      a.clear();
      a.addAll(temp);
      return a;
    case 0: 
      a.clear();
      return a;
    }
    throw new IllegalArgumentException("Relation " + relation + " out of range");
  }
}
