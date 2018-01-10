package javax.media.jai.remote;

import java.io.Serializable;

public abstract interface Negotiable
  extends Serializable
{
  public abstract Negotiable negotiate(Negotiable paramNegotiable);
  
  public abstract Object getNegotiatedValue();
  
  public abstract Class getNegotiatedValueClass();
}
