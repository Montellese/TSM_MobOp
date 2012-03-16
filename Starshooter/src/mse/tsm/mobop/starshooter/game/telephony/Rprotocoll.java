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
  private Boolean connectionSetUp=false;
  



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
        return "HLO SRV "+ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
      } catch (NameNotFoundException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    if( theInput.length() < 6 )
      return theOutput;
    
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
      /// TODO: FINISH GAME
    }
    else
      switch(state)
      {
        case 0 :
          if( prefix.equals("HLO") && command.equals("CLT") )
          {
            try
            {
              if( param.equals(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName) )
              {
                theOutput="HLO SRV";
                state++;
                /// TODO: START GAME
                connectionSetUp = true;
              }
            } catch (NameNotFoundException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          break;
        case 1 :
          if( prefix.equals("SET") )
          {
            // receive ship position
            if( command.equals("POS") )
            {
              float newpos=Float.parseFloat(param);
              sim.setShipPosition(false, newpos);
              theOutput="SET OK!";
            }
            // shot position
            else if( command.equals("SHT") )
            {
              float newpos=Float.parseFloat(param);
              sim.setShipPosition(false, newpos);
              sim.shot(false);
              theOutput="SET OK!";
            }
            // ship destroyed
            else if( command.equals("DST") )
            {
              sim.looseLife(false);
              theOutput="SET OK!";
            }
            // answer ok
            else if( command.equals("OK!") )
            {
              theOutput=null;
            }
          }
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
    
    String prefix = theInput.length()>=3?theInput.substring(0,3):"";
    if(prefix.equals("BYE"))
      return "BYE";
    String command= theInput.length()>=7?theInput.substring(4,7):"";
    String param  = theInput.length()>9?theInput.substring(9):"";
    
    if( prefix.equals("FOO") && command.equals("BAR") )
    {
      theOutput="FOO BAR CLIENT";
    }
    else
      switch(state)
      {
        case 0 :
          if( prefix.equals("HLO") && command.equals("SRV") )
          {
            try
            {
              if( param.equals(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName) )
              {
                state++;
                theOutput=null;
                /// TODO: START GAME
              }
            } catch (NameNotFoundException e)
            {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          break;

        case 1 :
          if( prefix.equals("SET") )
          {
            // receive ship position
            if( command.equals("POS") )
            {
              float newpos=Float.parseFloat(param);
              sim.setShipPosition(false, newpos);
              theOutput="SET OK!";
            }
            // shot position
            else if( command.equals("SHT") )
            {
              float newpos=Float.parseFloat(param);
              sim.setShipPosition(false, newpos);
              sim.shot(false);
              theOutput="SET OK!";
            }
            // ship destroyed
            else if( command.equals("DST") )
            {
              sim.looseLife(false);
              theOutput="SET OK!";
            }
            // answer ok
            else if( command.equals("OK!") )
            {
              theOutput=null;
            }
          }
          break;
        default:
          theOutput="BYE";
          break;
      }
    
    return theOutput;
  }
  
  
  public String sendPos(float pos)
  {
    return "SET POS "+pos;
  }
  
  public String sendShoot(float pos)
  {
    return "SET SHT "+pos;
  }
}
