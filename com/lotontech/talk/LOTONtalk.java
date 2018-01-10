package com.lotontech.talk;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.audio.AudioPlayer;

public class LOTONtalk
{
  private Applet theApplet = null;
  
  private static final int overlap = 0;
  private Hashtable cache = new Hashtable();
  

  private AudioPlayer player;
  

  public static void main(String[] paramArrayOfString)
  {
    LOTONtalk localLOTONtalk = new LOTONtalk();
    if (paramArrayOfString.length > 0) { localLOTONtalk.speak(paramArrayOfString[0], true);
    }
    System.exit(0);
  }
  
  public LOTONtalk()
  {
    player = AudioPlayer.player;
  }
  
  public LOTONtalk(Applet paramApplet)
  {
    theApplet = paramApplet;
  }
  



  public void speak(String paramString, boolean paramBoolean)
  {
    String str = LOTONtext.getPhoneString(paramString);
    

    sayPhoneWord(str, paramBoolean);
  }
  



  public void sayPhoneWord(String paramString, boolean paramBoolean)
  {
    try
    {
      if (Singleton.instances == 0)
      {
        String str = paramString;
        
        paramString = "k";
        paramString = "e|" + paramString;
        paramString = "t|" + paramString;
        paramString = "n|" + paramString;
        paramString = "o|" + paramString;
        paramString = "t|" + paramString;
        paramString = "oo|" + paramString;
        paramString = "l|" + paramString;
        paramString = "10ms|" + paramString;
        paramString = "t|" + paramString;
        paramString = "ii|" + paramString;
        paramString = "r|" + paramString;
        paramString = "ee|" + paramString;
        paramString = "p|" + paramString;
        paramString = "o|" + paramString;
        paramString = "k|" + paramString;
        
        paramString = str + paramString;
      }
    }
    catch (Exception localException1) {
      localException1.printStackTrace();
    }
    
    paramString = paramString + "|50ms";
    
    Vector localVector = new Vector();
    int i = 0;
    

    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "|", false);
    while (localStringTokenizer.hasMoreTokens())
    {
      try
      {

        localObject1 = localStringTokenizer.nextToken();
        
        localObject2 = "l_";
        if (Character.isUpperCase(((String)localObject1).charAt(0))) localObject2 = "u_";
        localObject1 = "/" + (String)localObject2 + ((String)localObject1).toLowerCase() + ".tlk";
        

        Allophone localAllophone = (Allophone)cache.get(localObject1);
        
        if (localAllophone == null)
        {
          URL localURL = getClass().getResource((String)localObject1);
          
          if ((localURL == null) && (theApplet != null))
          {

            localURL = new URL(theApplet.getCodeBase(), "./" + (String)localObject1);
          }
          


          localObject3 = new ObjectInputStream(localURL.openStream());
          localAllophone = (Allophone)((ObjectInputStream)localObject3).readObject();
          cache.put(localObject1, localAllophone);
          ((ObjectInputStream)localObject3).close();
        }
        
        if (localAllophone != null) { localVector.addElement(localAllophone);
        }
        
        i += (int)dataSize;
      }
      catch (Exception localException2) {}
    }
    









    Object localObject1 = new byte[i + 24];
    
    Object localObject2 = (Allophone)localVector.elementAt(0);
    

    localObject1[0] = ((byte)(int)(magicNumber / 16777216L));
    localObject1[1] = ((byte)(int)(magicNumber / 65536L));
    localObject1[2] = ((byte)(int)(magicNumber / 256L));
    localObject1[3] = ((byte)(int)magicNumber);
    localObject1[4] = ((byte)(int)(dataOffset / 16777216L));
    localObject1[5] = ((byte)(int)(dataOffset / 65536L));
    localObject1[6] = ((byte)(int)(dataOffset / 256L));
    localObject1[7] = ((byte)(int)dataOffset);
    localObject1[8] = ((byte)(i / 16777216));
    localObject1[9] = ((byte)(i / 65536));
    localObject1[10] = ((byte)(i / 256));
    localObject1[11] = ((byte)i);
    localObject1[12] = ((byte)(int)(encoding / 16777216L));
    localObject1[13] = ((byte)(int)(encoding / 65536L));
    localObject1[14] = ((byte)(int)(encoding / 256L));
    localObject1[15] = ((byte)(int)encoding);
    localObject1[16] = ((byte)(int)(sampleRate / 16777216L));
    localObject1[17] = ((byte)(int)(sampleRate / 65536L));
    localObject1[18] = ((byte)(int)(sampleRate / 256L));
    localObject1[19] = ((byte)(int)sampleRate);
    localObject1[20] = ((byte)(int)(channels / 16777216L));
    localObject1[21] = ((byte)(int)(channels / 65536L));
    localObject1[22] = ((byte)(int)(channels / 256L));
    localObject1[23] = ((byte)(int)channels);
    

    int j = 24;
    for (int k = 0; k < localVector.size(); k++)
    {
      localObject3 = (Allophone)localVector.elementAt(k);
      
      for (int m = 0; m < data.length; m++)
      {
        if (localObject1[j] != 0) localObject1[j] = ((byte)((localObject1[j] + data[m]) / 2)); else
          localObject1[j] = data[m];
        j++;
      }
      
      j = j;
    }
    
    Object localObject3 = new ByteArrayInputStream((byte[])localObject1);
    
    try
    {
      player.start((java.io.InputStream)localObject3);
      if (paramBoolean) Thread.sleep(i / 5);
    }
    catch (Exception localException3) {}
  }
}
