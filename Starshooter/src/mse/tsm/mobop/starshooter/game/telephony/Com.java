package mse.tsm.mobop.starshooter.game.telephony;

import java.net.Socket;

import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import android.content.Context;

public abstract class Com extends Thread
{
  protected static Context ctx;
  protected static Simulation sim;
  protected Boolean connectionSetup=false;
  protected Rprotocoll kkp;
  
  protected Com(Context ctx)
  {
    this(ctx,"unkwn");
  }
  
  protected Com(Context ctx,String threadname)
  {
    super(threadname);
    Com.ctx=ctx;
  }

  public void registerSimulation(Simulation sim)
  {
    this.sim=sim;
  }
  
  public boolean connectionSetup()
  {
    return connectionSetup;
  }
  
  public void setSimulation(Simulation sim)
  {
    Com.sim=sim;
    kkp.sim = sim;
  }
}
