package mse.tsm.mobop.starshooter.game.telephony;

import java.io.*;
import java.net.*;

import mse.tsm.mobop.starshooter.game.screens.GameLoop;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Client extends Com
{
  private int port;
  private String serverIP;
  private Socket kkSocket = null;
  private BufferedReader stdIn = null;
  private static volatile int runningThreads=0;
  private static volatile boolean run = false;
  private Context ctx;
  
  
  public Client(String client,int port,Context context)
  {
    super(context,"SingleClientThread");
    this.port=port;
    this.serverIP=client;
    ctx=context;
  }
  
  public void run()
  {
    try
    {
      connect();
    }
    catch(Exception e)
    {
      Log.e("Com-Client", "Error in Thread: "+e.toString() );
    }
  }
  
  public static boolean ClientsRunning()
  {
    //System.out.println(Client.);
    return runningThreads>0; // || Client.activeCount()>0;
  }
  
  private void connect() throws IOException
  {
    run=true;
    runningThreads++;
    try
    {
        kkSocket = new Socket(serverIP, port);
        out = new PrintWriter(kkSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
    }
    catch (UnknownHostException e)
    {
      Log.e("Com-Client", "Host unknown: `"+serverIP+"`: "+e.toString() );
      return;
    }
    catch (IOException e)
    {
      Log.e("Com-Client", "Couldn't get I/O for the connection to: "+serverIP+":"+port+": "+e.toString() );
      return;
    }
  
    
    stdIn = new BufferedReader(new InputStreamReader(System.in));
    String outputLine, inputLine;
    kkp = new Rprotocoll(ctx);
  
    while((inputLine = in.readLine()) != null)
    {
      outputLine = (run?kkp.processClientInput(inputLine):"BYE");
      if(outputLine!=null)
      {
        out.println(outputLine);
        if (outputLine.equals("BYE"))
        {
          connectionIsSetup = false;
          break;
        }
      }
      connectionIsSetup = kkp.getConnectionSetUp();
    }
    if( connectionIsSetup )
      comLost();
    disconnect();
    out.close();
    in.close();
    stdIn.close();
    kkSocket.close();
    runningThreads--;
    if( runningThreads == 0 )
      run=false;
  }
  
  public void disconnect()
  {
    run=false;
  }
}
