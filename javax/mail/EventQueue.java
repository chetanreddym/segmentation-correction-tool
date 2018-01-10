package javax.mail;

import java.util.Vector;
import javax.mail.event.MailEvent;














class EventQueue
  implements Runnable
{
  private QueueElement head;
  private QueueElement tail;
  private Thread qThread;
  
  class QueueElement
  {
    QueueElement next;
    QueueElement prev;
    MailEvent event;
    Vector vector;
    
    QueueElement(MailEvent paramMailEvent, Vector paramVector)
    {
      event = paramMailEvent;
      vector = paramVector;
    }
  }
  



  public EventQueue()
  {
    qThread = new Thread(this);
    qThread.setDaemon(true);
    qThread.start();
  }
  


  public synchronized void enqueue(MailEvent paramMailEvent, Vector paramVector)
  {
    QueueElement localQueueElement = new QueueElement(paramMailEvent, paramVector);
    
    if (head == null) {
      head = localQueueElement;
      tail = localQueueElement;
    } else {
      next = head;
      head.prev = localQueueElement;
      head = localQueueElement;
    }
    notify();
  }
  







  private synchronized QueueElement dequeue()
    throws InterruptedException
  {
    while (tail == null)
      wait();
    QueueElement localQueueElement = tail;
    tail = prev;
    if (tail == null) {
      head = null;
    } else {
      tail.next = null;
    }
    prev = (localQueueElement.next = null);
    return localQueueElement;
  }
  

  public void run()
  {
    try
    {
      QueueElement localQueueElement;
      
      while ((localQueueElement = dequeue()) != null) {
        MailEvent localMailEvent = event;
        Vector localVector = vector;
        
        for (int i = 0; i < localVector.size(); i++) {
          localMailEvent.dispatch(localVector.elementAt(i));
        }
        localQueueElement = null;localMailEvent = null;localVector = null;
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
  



  void stop()
  {
    if (qThread != null) {
      qThread.interrupt();
      qThread = null;
    }
  }
}
