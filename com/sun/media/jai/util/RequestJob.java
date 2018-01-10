package com.sun.media.jai.util;

import java.awt.Point;
import java.awt.image.Raster;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;


















































































































































































final class RequestJob
  implements Job
{
  final SunTileScheduler scheduler;
  final PlanarImage owner;
  final int tileX;
  final int tileY;
  final Raster[] tiles;
  final int offset;
  boolean done = false;
  Exception exception = null;
  



  RequestJob(SunTileScheduler scheduler, PlanarImage owner, int tileX, int tileY, Raster[] tiles, int offset)
  {
    this.scheduler = scheduler;
    this.owner = owner;
    this.tileX = tileX;
    this.tileY = tileY;
    this.tiles = tiles;
    this.offset = offset;
  }
  




  public void compute()
  {
    synchronized (scheduler.tileRequests)
    {
      Object tileID = SunTileScheduler.tileKey(owner, tileX, tileY);
      

      List reqList = (List)scheduler.tileRequests.remove(tileID);
      

      scheduler.tileJobs.remove(tileID);
    }
    

    List reqList;
    
    if ((reqList != null) && (!reqList.isEmpty()))
    {
      Point p = new Point(tileX, tileY);
      Integer tileStatus = new Integer(1);
      Iterator reqIter = reqList.iterator();
      while (reqIter.hasNext()) {
        Request r = (Request)reqIter.next();
        tileStatus.put(p, tileStatus);
      }
      try
      {
        tiles[offset] = owner.getTile(tileX, tileY);
      } catch (Exception e) {
        exception = e;
      }
      finally {
        int numReq = reqList.size();
        Set listeners = SunTileScheduler.getListeners(reqList);
        

        if ((listeners != null) && (!listeners.isEmpty()))
        {
          TileRequest[] requests = (TileRequest[])reqList.toArray(new TileRequest[0]);
          


          tileStatus = new Integer(exception == null ? 2 : 4);
          

          for (int i = 0; i < numReq; i++) {
            tileStatus.put(p, tileStatus);
          }
          

          Iterator iter = listeners.iterator();
          

          if (exception == null)
          {
            while (iter.hasNext()) {
              TileComputationListener listener = (TileComputationListener)iter.next();
              
              listener.tileComputed(scheduler, requests, owner, tileX, tileY, tiles[offset]);
            }
          }
          


          while (iter.hasNext()) {
            TileComputationListener listener = (TileComputationListener)iter.next();
            
            listener.tileComputationFailure(scheduler, requests, owner, tileX, tileY, exception);
          }
        }
      }
    }
    




    done = true;
  }
  



  public boolean notDone()
  {
    return !done;
  }
  
  public PlanarImage getOwner()
  {
    return owner;
  }
  
  public boolean isBlocking()
  {
    return false;
  }
  
  public Exception getException()
  {
    return exception;
  }
  
  public String toString()
  {
    String tString = "null";
    if (tiles[offset] != null) {
      tString = tiles[offset].toString();
    }
    return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": owner = " + owner.toString() + " tileX = " + Integer.toString(tileX) + " tileY = " + Integer.toString(tileY) + " tile = " + tString;
  }
}
