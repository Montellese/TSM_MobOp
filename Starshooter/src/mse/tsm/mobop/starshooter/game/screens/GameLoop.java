package mse.tsm.mobop.starshooter.game.screens;

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
	
	boolean wasTouched = false;

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
		if (activity.getAccelerationOnXAxis() < 0)
			simulation.moveShipLeft(true, activity.getDeltaTime(), Math.abs(activity.getAccelerationOnXAxis()) / 10);
		else
			simulation.moveShipRight(true, activity.getDeltaTime(), Math.abs(activity.getAccelerationOnXAxis()) / 10);
	
		boolean isTouched = activity.isTouched();
		if (isTouched && !wasTouched)
		{
			wasTouched = true;
			simulation.shot(true);
		}
		else if (!isTouched)
			wasTouched = false;
	}

	public boolean isDone()
	{
		return simulation.ship.lives == 0 || simulation.shipOpponent.lives == 0;
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
