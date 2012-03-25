package mse.tsm.mobop.starshooter.game.telephony;
import java.net.*;
import java.io.*;
import java.net.Socket;

import mse.tsm.mobop.starshooter.game.Playground;
import mse.tsm.mobop.starshooter.game.screens.GameLoop;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Server extends Com
{
  private Socket socket = null;
  private static boolean serverRunning=false;
  private static ServerSocket serverSocket = null;
  
  private Server(Socket socket, Context ctx)
  {
    super(ctx, "SingleServerThread");
    this.socket = socket;
  }
  
  public void finalize()
  {
    if( serverSocket != null )
      try{
        serverSocket.close();
      } catch (IOException e) { }
  }
  
  
  public static Server startServer(int port, Context context)
  {
    Server myInstance=null;
    
    try
    {
      serverSocket = new ServerSocket(port);
    } 
    catch (IOException e)
    {
      Log.e("Com-Server", "Error while listening on port "+port+": "+e.toString() );
    }
    
    try
    {
      myInstance = new Server(serverSocket.accept(),context);
      myInstance.start();
    }
    catch( SocketException e )
    {}
    catch( IOException e )
    {
      Log.e("Com-Server", "IO Error: "+e.toString() );
    }
    
    return myInstance;
  }
  
  public void run()
  {
    connect();
  }
  
  private void connect()
  {
    serverOn();
    try
    {
      connectionIsSetup = false;
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String inputLine, outputLine;
      kkp = new Rprotocoll(ctx);
  
      outputLine = kkp.processServerInput(null);
      
      out.println(outputLine);
  
      while ((inputLine = in.readLine()) != null)
      {
       outputLine = kkp.processServerInput(inputLine);
       if( outputLine != null )
         out.println(outputLine);
       
       connectionIsSetup = kkp.getConnectionSetUp();
       
       if( outputLine != null && outputLine.equals("BYE"))
        break;
      }
      connectionIsSetup=false;
      // connection lost?
      if( kkp.gameIsRunning() )
        this.comLost();
      out.close();
      in.close();
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  /** registers new client connection **/
  private static synchronized void serverOn()
  {
    serverRunning=true;
  }  
  /** removes new client connection **/
  public static synchronized void serverOff()
  {
    serverRunning=false;
    try { serverSocket.close();  } catch (Exception e) {};
  }
  /** reset this class for reuse **/
  public static void reset()
  {
    serverOff();
  }
  /** checks if this server is in listening mode **/
  public static boolean serverRunning()
  {
    if( serverSocket == null )
      return false;
    if( serverSocket.isClosed())
      serverOff();
    else
      serverOn();
    
    return serverRunning;
  }
  
  public static boolean getserverRunning()
  {
    return serverRunning;
  }


}

