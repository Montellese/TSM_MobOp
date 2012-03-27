package mse.tsm.mobop.starshooter.game.screens;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.Renderer;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.simulation.SimulationListener;
import mse.tsm.mobop.starshooter.game.telephony.Com;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;

public class GameLoop implements GameScreen, SimulationListener
{
	public Simulation simulation;
	Renderer renderer;	
	
	protected String errorMsg = "";
	
	boolean wasTouched = false;

	public GameLoop(GL10 gl, GameActivity activity, Com com)
	{
		simulation = new Simulation(com);
		simulation.listener = this;
		renderer = new Renderer(gl, activity);
		com.gl = this;
	}
	
	public GameLoop(GL10 gl, GameActivity activity, Simulation simulation) 
	{
		this.simulation = simulation;
		this.simulation.listener = this;
		renderer = new Renderer(gl, activity);
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
		return simulation.ship.lives == 0 || simulation.shipOpponent.lives == 0 || errorMsg.length()>0;
	}
	
	public void dispose()
	{
		renderer.dispose();
	}

	public void explosion() 
	{
		// Nothing to be done
	}

	public void shot() 
	{	
		// Nothing to be done
	}


	public String getErrorMsg()
  {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg)
  {
    this.errorMsg = errorMsg;
  }
}
