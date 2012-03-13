package mse.tsm.mobop.starshooter.game.screens;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.Renderer;
import mse.tsm.mobop.starshooter.game.SoundManager;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.simulation.SimulationListener;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;

public class GameLoop implements GameScreen, SimulationListener
{
	public Simulation simulation;
	Renderer renderer;	
	SoundManager soundManager;

	public GameLoop(GL10 gl, GameActivity activity)
	{
		simulation = new Simulation();
		simulation.listener = this;
		renderer = new Renderer(gl, activity);
		soundManager = new SoundManager(activity);
	}
	
	public GameLoop(GL10 gl, GameActivity activity, Simulation simulation) 
	{
		this.simulation = simulation;
		this.simulation.listener = this;
		renderer = new Renderer(gl, activity);
		soundManager = new SoundManager(activity);
	}

	public void render(GL10 gl, GameActivity activity) 
	{	
		renderer.render(gl, activity, simulation);
	}

	public void update(GameActivity activity) 
	{	
		processInput(activity);
		simulation.update(activity.getDeltaTime());
	}
	
	private void processInput(GameActivity activity)
	{		
		if (activity.getAccelerationOnYAxis() < 0)
			simulation.moveShipLeft(activity.getDeltaTime(), Math.abs(activity.getAccelerationOnYAxis()) / 10);
		else
			simulation.moveShipRight(activity.getDeltaTime(), Math.abs(activity.getAccelerationOnYAxis()) / 10);
	
		
		if (activity.isTouched())
			simulation.shot();
	}

	public boolean isDone()
	{
		return simulation.ship.lives == 0;
	}
	
	public void dispose()
	{
		renderer.dispose();
		soundManager.dispose();
	}

	public void explosion() 
	{
		soundManager.playExplosionSound();
	}

	public void shot() 
	{	
		soundManager.playShotSound();
	}
}
