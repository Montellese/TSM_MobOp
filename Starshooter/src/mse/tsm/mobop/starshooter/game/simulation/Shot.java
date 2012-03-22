package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;

public class Shot implements Serializable 
{
	public static float SHOT_VELOCITY = 1;
	public final Vector position = new Vector();
	public boolean isOpponentShot;
	public boolean hasLeftField = false;

	public Shot(Vector position, boolean isOpponentShot)
	{
		this.position.set(position);
		this.isOpponentShot = isOpponentShot;

		// Make sure the shot is not drawn below the ship
		if (isOpponentShot)
			this.position.y -= Ship.SHIP_RADIUS / 2;
		else
			this.position.y += Ship.SHIP_RADIUS / 2;
	}
	
	public void update(float delta) 
	{	
		if (isOpponentShot)
			position.y -= SHOT_VELOCITY * delta;
		else
			position.y += SHOT_VELOCITY * delta;
		
		if (position.y > Simulation.PLAYFIELD_MAX_Y)
			hasLeftField = true;
		if (position.y < Simulation.PLAYFIELD_MIN_Y)
			hasLeftField = true;
	}
}
