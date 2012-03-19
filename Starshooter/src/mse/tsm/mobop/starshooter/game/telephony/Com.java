package mse.tsm.mobop.starshooter.game.telephony;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import android.content.Context;

public abstract class Com extends Thread
{
  protected static Context ctx;
  protected static Simulation sim;
  protected boolean connectionIsSetup=false;
  protected Rprotocoll kkp;
  protected PrintWriter out = null;
  protected BufferedReader in;
  
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
    Com.sim=sim;
    kkp.sim = sim;
  }
  
  public boolean connectionSetup()
  {
    return connectionIsSetup;
  }

  public void setPos(float pos)
  {
    if(out!=null && connectionSetup() )
      out.println(kkp.sendPos(pos));
  }
  
  public void sendShot(float pos)
  {
    if(out!=null && connectionSetup() )
      out.println(kkp.sendShot(pos));
  }

  public void sendMinusOneLife()
  {
    if(out!=null && connectionSetup() )
      out.println(kkp.sendMinusOneLife());
  }
}
