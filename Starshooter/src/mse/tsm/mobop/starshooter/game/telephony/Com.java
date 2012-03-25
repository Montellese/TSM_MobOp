package mse.tsm.mobop.starshooter.game.telephony;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import mse.tsm.mobop.starshooter.game.screens.GameLoop;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Com extends Thread
{
  protected static Context ctx;
  protected static Simulation sim;
  protected boolean connectionIsSetup=false;
  protected Rprotocoll kkp;
  protected PrintWriter out = null;
  protected BufferedReader in;
  public GameLoop gl;
  
  
  protected Com(Context ctx,String threadname)
  {
    super(threadname);
    Com.ctx=ctx;
  }

  public void registerSimulation(Simulation sim)
  {
    Com.sim=sim;
    kkp.sim = sim;
    Log.d("Com","Connected");
  }
  
  public boolean connectionSetup()
  {
    return connectionIsSetup;
  }
  
  public void comLost()
  {
    kkp.gameFinished();

    gl.setErrorMsg("Connection lost");
    Log.w("Com", "Connection lost");
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

  public void showError(String msg)
  {
    gl.setErrorMsg(msg);
  }
  
  public void launchNewGame()
  {
    if(out!=null /*&& connectionSetup()*/ )
      out.println(kkp.launchNewGame());
  }

  public boolean gameIsRunning()
  {
    return kkp.gameIsRunning();
  }
}
