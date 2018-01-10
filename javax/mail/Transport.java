package javax.mail;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
































public abstract class Transport
  extends Service
{
  private Vector transportListeners;
  
  public Transport(Session paramSession, URLName paramURLName)
  {
    super(paramSession, paramURLName);
  }
  



























  public static void send(Message paramMessage)
    throws MessagingException
  {
    paramMessage.saveChanges();
    send0(paramMessage, paramMessage.getAllRecipients());
  }
  















  public static void send(Message paramMessage, Address[] paramArrayOfAddress)
    throws MessagingException
  {
    paramMessage.saveChanges();
    send0(paramMessage, paramArrayOfAddress);
  }
  

  private static void send0(Message paramMessage, Address[] paramArrayOfAddress)
    throws MessagingException
  {
    if ((paramArrayOfAddress == null) || (paramArrayOfAddress.length == 0)) {
      throw new SendFailedException("No recipient addresses");
    }
    



    Hashtable localHashtable = new Hashtable();
    

    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    Vector localVector3 = new Vector();
    
    for (int i = 0; i < paramArrayOfAddress.length; i++) {
      Vector localVector4;
      if (localHashtable.containsKey(paramArrayOfAddress[i].getType())) {
        localVector4 = (Vector)localHashtable.get(paramArrayOfAddress[i].getType());
        localVector4.addElement(paramArrayOfAddress[i]);
      }
      else {
        localVector4 = new Vector();
        localVector4.addElement(paramArrayOfAddress[i]);
        localHashtable.put(paramArrayOfAddress[i].getType(), localVector4);
      }
    }
    
    int j = localHashtable.size();
    if (j == 0) {
      throw new SendFailedException("No recipient addresses");
    }
    Session localSession = session != null ? session : 
      Session.getDefaultInstance(System.getProperties(), null);
    

    Object localObject1 = null;
    int k = 0;
    
    Enumeration localEnumeration = localHashtable.elements();
    Object localObject2; Address[] arrayOfAddress1; while (localEnumeration.hasMoreElements()) {
      localObject2 = (Vector)localEnumeration.nextElement();
      arrayOfAddress1 = new Address[((Vector)localObject2).size()];
      ((Vector)localObject2).copyInto(arrayOfAddress1);
      
      Transport localTransport;
      if ((localTransport = localSession.getTransport(arrayOfAddress1[0])) == null)
      {

        for (int m = 0; m < arrayOfAddress1.length; m++) {
          localVector1.addElement(arrayOfAddress1[m]);
        }
      } else {
        try {
          localTransport.connect();
          localTransport.sendMessage(paramMessage, arrayOfAddress1);
        } catch (SendFailedException localSendFailedException) {
          k = 1;
          
          if (localObject1 == null) {
            localObject1 = localSendFailedException;
          } else {
            localObject1.setNextException(localSendFailedException);
          }
          
          Address[] arrayOfAddress3 = localSendFailedException.getInvalidAddresses();
          int n; if (arrayOfAddress3 != null) {
            for (n = 0; n < arrayOfAddress3.length; n++) {
              localVector1.addElement(arrayOfAddress3[n]);
            }
          }
          arrayOfAddress3 = localSendFailedException.getValidSentAddresses();
          if (arrayOfAddress3 != null) {
            for (n = 0; n < arrayOfAddress3.length; n++) {
              localVector2.addElement(arrayOfAddress3[n]);
            }
          }
          Address[] arrayOfAddress4 = localSendFailedException.getValidUnsentAddresses();
          if (arrayOfAddress4 != null)
            for (int i1 = 0; i1 < arrayOfAddress4.length; i1++)
              localVector3.addElement(arrayOfAddress4[i1]);
        } catch (MessagingException localMessagingException) {
          k = 1;
          
          if (localObject1 == null) {
            localObject1 = localMessagingException;
          } else
            localObject1.setNextException(localMessagingException);
        } finally {
          localTransport.close();
        }
      }
    }
    
    if ((k != 0) || (localVector1.size() != 0) || (localVector3.size() != 0)) {
      localObject2 = null;arrayOfAddress1 = null;Address[] arrayOfAddress2 = null;
      

      if (localVector2.size() > 0) {
        localObject2 = new Address[localVector2.size()];
        localVector2.copyInto((Object[])localObject2);
      }
      if (localVector3.size() > 0) {
        arrayOfAddress1 = new Address[localVector3.size()];
        localVector3.copyInto(arrayOfAddress1);
      }
      if (localVector1.size() > 0) {
        arrayOfAddress2 = new Address[localVector1.size()];
        localVector1.copyInto(arrayOfAddress2);
      }
      throw new SendFailedException("Sending failed", localObject1, 
        (Address[])localObject2, arrayOfAddress1, arrayOfAddress2);
    }
  }
  















  public abstract void sendMessage(Message paramMessage, Address[] paramArrayOfAddress)
    throws MessagingException;
  















  public synchronized void addTransportListener(TransportListener paramTransportListener)
  {
    if (transportListeners == null)
      transportListeners = new Vector();
    transportListeners.addElement(paramTransportListener);
  }
  








  public synchronized void removeTransportListener(TransportListener paramTransportListener)
  {
    if (transportListeners != null) {
      transportListeners.removeElement(paramTransportListener);
    }
  }
  










  protected void notifyTransportListeners(int paramInt, Address[] paramArrayOfAddress1, Address[] paramArrayOfAddress2, Address[] paramArrayOfAddress3, Message paramMessage)
  {
    if (transportListeners == null) {
      return;
    }
    TransportEvent localTransportEvent = new TransportEvent(this, paramInt, paramArrayOfAddress1, 
      paramArrayOfAddress2, paramArrayOfAddress3, paramMessage);
    queueEvent(localTransportEvent, transportListeners);
  }
}
