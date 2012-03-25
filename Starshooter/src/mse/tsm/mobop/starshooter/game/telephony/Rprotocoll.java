package mse.tsm.mobop.starshooter.game.telephony;

import java.net.InetAddress;

import mse.tsm.mobop.starshooter.game.simulation.Simulation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


public class Rprotocoll
{
  /** tell us where we are in the protocol **/
  private int state = 0;

  private Context ctx;
  public Simulation sim;
  private boolean isMaster = false;
  private Boolean connectionSetUp=false;
  public static final int gameOverPaused = 2;
  public static final int gameOverPausedOtherIsReady = 3;
  public static final int gameOverWeAreReady = 4;
  public static final int gameRunning = 1;
  public static final int gameNotRunning = 0;
  



  public Rprotocoll(Context ctxe)
  {
    state=0;
    connectionSetUp = false;
    ctx=ctxe;
  }
  
  public Boolean getConnectionSetUp()
  {
    return connectionSetUp;
  }
  
  public String processServerInput(String theInput)
  {
    String theOutput = "BYE";
 
    if( theInput == null )
      try
      {
        isMaster = true;
        return "HLO SRV "+ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
      } catch (NameNotFoundException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    if( theInput.length() < 6 )
    {
      connectionSetUp = false;
      return theOutput;
    }
    
    String prefix = theInput.length()>=3?theInput.substring(0,3):"";
    String command= theInput.length()>=7?theInput.substring(4,7):"";
    String param  = theInput.length()>8?theInput.substring(8):"";

    if( !Server.getserverRunning() )
      state=Integer.MIN_VALUE;

    if( prefix.equals("FOO") && command.equals("BAR") )
    {
      theOutput="FOO BAR SERVER";
    }
    else if( prefix.equals("BYE") )
    {
      state=0;
      /// FINISH GAME
      connectionSetUp = false;
    }
    else
      switch(state)
      {
        case gameNotRunning :
          if( prefix.equals("HLO") && command.equals("CLT") )
          {
            try
            {
              if( param.equals(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName) )
              {
                theOutput="HLO CLT";
                state++;
                // START GAME
                connectionSetUp = true;
              }
            } catch (NameNotFoundException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          break;
        case gameRunning :
          if( prefix.equals("SET") )
          {
            // receive ship position
            if( command.equals("POS") )
            {
              float newpos=Float.parseFloat(param);
              if(sim!=null)
                sim.setShipPosition(false, newpos);
              theOutput="SET OK!";
            }
            // shot position
            else if( command.equals("SHT") )
            {
              float newpos=Float.parseFloat(param);
              if(sim!=null)
              {
                sim.setShipPosition(false, newpos);
                sim.shot(false);
              }
              theOutput="SET OK!";
            }
            // ship destroyed
            else if( command.equals("DST") )
            {
              if(sim!=null && sim.looseLife(false) )
              {
                // ship fully destroyed
                gameOverPause();
              }
              theOutput="SET OK!";
            }
            // answer ok
            else if( command.equals("OK!") )
            {
              theOutput=null;
            }
          }
          break;          
        case gameOverPaused :
          if( prefix.equals("PSE") )
          {
            // receive ship position
            if( command.equals("RDY") )
            {
              state = gameOverPausedOtherIsReady;
              theOutput="SET OK!";
            }
          }
          else if( prefix.equals("SET") )
          {
            //if( command.equals("OK!") )
            //{
              theOutput=null;
            //}
          }
          break;
        case gameOverPausedOtherIsReady :
          theOutput = null;
          break;
        case gameOverWeAreReady:
          if( prefix.equals("PSE") )
          {
            // start immediately
            if( command.equals("SRT") )
            {
              state = gameRunning;
              theOutput=null;
            }
          }
          else
            theOutput = null;
          break;
        default:
          theOutput="BYE";
          break;
      }
    
    return theOutput;
  }
  
  public String processClientInput(String theInput)
  {
    String theOutput = "BYE";

    if( theInput.length() < 6 )
    {
      connectionSetUp = false;
      return theOutput;
    }
    
    String prefix = theInput.length()>=3?theInput.substring(0,3):"";
    String command= theInput.length()>=7?theInput.substring(4,7):"";
    String param  = theInput.length()>8?theInput.substring(8):"";
    
    if( prefix.equals("FOO") && command.equals("BAR") )
    {
      theOutput="FOO BAR CLIENT";
    }
    else
      switch(state)
      {
        case gameNotRunning :
          if( prefix.equals("HLO") && command.equals("SRV") )
          {
            try
            {
              if( param.equals(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName) )
              {
                state++;
                theOutput="HLO CLT "+ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
                // START GAME
                connectionSetUp = true;
              }
            } catch (NameNotFoundException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          else if( prefix.equals("SET"))
          {
            /*if( command.equals("OK!") )
            {*/
              theOutput = null;
            //}
          }
          break;

        case gameRunning :
          if( prefix.equals("SET") )
          {
            // receive ship position
            if( command.equals("POS") )
            {
              float newpos=Float.parseFloat(param);
              if(sim!=null)
                sim.setShipPosition(false, newpos);
              theOutput="SET OK!";
            }
            // shot position
            else if( command.equals("SHT") )
            {
              float newpos=Float.parseFloat(param);
              if(sim!=null)
              {
                sim.setShipPosition(false, newpos);
                sim.shot(false);
              }
              theOutput="SET OK!";
            }
            // ship destroyed
            else if( command.equals("DST") )
            {
              if(sim!=null && sim.looseLife(false) )
              {
                // ship fully destroyed
                gameOverPause();
              }
              theOutput="SET OK!";
            }
            // answer ok
            else if( command.equals("OK!") )
            {
              theOutput=null;
            }
          }else if(prefix.equals("HLO") && command.equals("CLT"))
          {
            theOutput=null;
          }
          break;
          case gameOverPaused :
            if( prefix.equals("PSE") )
            {
              // receive ship position
              if( command.equals("RDY") )
              {
                state = gameOverPausedOtherIsReady;
                theOutput="SET OK!";
              }
            }
            else if( prefix.equals("SET") )
            {
              //if( command.equals("OK!") )
              //{
                theOutput=null;
              //}
            }
            break;
          case gameOverPausedOtherIsReady :
            theOutput = null;
            break;
          case gameOverWeAreReady:
            if( prefix.equals("PSE") )
            {
              // start immediately
              if( command.equals("SRT") )
              {
                state = gameRunning;
                theOutput=null;
              }
            }
            else
              theOutput = null;
            break;
        default:
          theOutput="BYE";
          break;
      }
    
    return theOutput;
  }
  
  public void gameFinished()
  {
    connectionSetUp = false;
    state = gameNotRunning;
  }
  
  public String sendPos(float pos)
  {
    return "SET POS "+pos;
  }
  
  public String sendShot(float pos)
  {
    return "SET SHT "+pos;
  }

  public String sendMinusOneLife()
  {
    if( sim.isDead(true) )
    {
      state=gameOverPaused;
    }
    return "SET DST";
  }

  public String launchNewGame()
  {
    if( state == gameOverPausedOtherIsReady )
    {
      state = gameRunning;
      return "PSE SRT";
    }
    else
    {
      state = gameOverWeAreReady;
      return "PSE RDY";
    }
  }

  
  public boolean gameIsRunning()
  {
    return (state == gameRunning);
  }

  public void gameOverPause()
  {
    state = gameOverPaused;
  }
}
