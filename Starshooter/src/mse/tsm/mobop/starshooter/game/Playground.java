package mse.tsm.mobop.starshooter.game;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.R;
import mse.tsm.mobop.starshooter.StarshooterMain;
import mse.tsm.mobop.starshooter.game.screens.GameLoop;
import mse.tsm.mobop.starshooter.game.screens.GameOverScreen;
import mse.tsm.mobop.starshooter.game.screens.GameScreen;
import mse.tsm.mobop.starshooter.game.screens.StartScreen;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.telephony.Com;
import mse.tsm.mobop.starshooter.game.telephony.Server;
import mse.tsm.mobop.starshooter.game.telephony.Client;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import mse.tsm.mobop.starshooter.game.tools.GameListener;

public class Playground extends GameActivity implements GameListener, Runnable
{
	private GameScreen screen = null;
	private Simulation simulation = null;
	
	private long startTime = System.nanoTime();
	private int frameCount = 0;
	private Boolean isMaster;
	private String masterSip="";
	private String additionalText="";
	public Com com;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
  	{
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// go fullscreen
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                              WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  
		super.onCreate(savedInstanceState);
	  
		this.setGameListener(this);

	    Bundle bundle = this.getIntent().getExtras();
	    isMaster = bundle.getBoolean("isMaster");
    
		if (isMaster!=null && savedInstanceState != null && savedInstanceState.containsKey("simulation"))
			simulation = (Simulation)savedInstanceState.getSerializable("simulation");
		
		masterSip = bundle.getString("serverip");
		if (isMaster)
			additionalText = String.format(getResources().getString(R.string.con_server_ip_is), masterSip);
		else
			additionalText = String.format(getResources().getString(R.string.con_trycon_to), masterSip);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (screen instanceof GameLoop)
			outState.putSerializable("simulation", ((GameLoop)screen).simulation);
	}
    
    @Override
    protected void onPause()
    {
        super.onPause();
        if (screen != null)
        	screen.dispose();
        if (screen instanceof GameLoop)
        	simulation = ((GameLoop)screen).simulation;
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
    }

	public void setup(GameActivity activity, GL10 gl) 
	{
		if (simulation != null)
		{
			screen = new GameLoop(gl, activity, simulation);
			simulation = null;
		}
		else
		{
			screen = new StartScreen(gl, activity, additionalText);
			Thread t=new Thread(this);
			t.start();
		}
	}

	public void run()
	{
	    // setup communication
	    if( isMaster )
	    {
	    	Server.startServer(StarshooterMain.comPort,this.getApplicationContext(),this);
	    }
	    else
	    {
	    	com = new Client(masterSip, StarshooterMain.comPort, this);
	    	com.start();
	    }
	}
	
	public void mainLoopIteration(GameActivity activity, GL10 gl) 
	{
		screen.update(activity);
		screen.render(gl, activity);
		
		if (screen instanceof StartScreen && com != null && com.connectionSetup())
	    {
	      ((StartScreen)screen).isDone = true;
	    }
		
		if (screen.isDone())
		{
			screen.dispose();
			
			if (screen instanceof StartScreen)
				screen = new GameLoop(gl, activity, com);
			else if (screen instanceof GameLoop)
			{
				screen = new GameOverScreen(gl, activity, ((GameLoop)screen).simulation.ship.lives > 0);
				com = null;
			}
			else if (screen instanceof GameOverScreen)
				screen = new StartScreen(gl, activity);
		}
		  
		
		frameCount++;
		if (System.nanoTime() - startTime > 1000000000)
		{
			frameCount = 0;
			startTime = System.nanoTime();
		}
	}
}
