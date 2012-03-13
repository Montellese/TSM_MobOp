package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;

public class Shot implements Serializable 
{
	public static float SHOT_VELOCITY = 10;
	public final Vector position = new Vector();
	public boolean isInvaderShot;
	public boolean hasLeftField = false;

	public Shot( Vector position, boolean isInvaderShot )
	{
		this.position.set( position );
		this.isInvaderShot = isInvaderShot;
	}
	
	public void update(float delta) 
	{	
		if( isInvaderShot )
			position.z += SHOT_VELOCITY * delta;
		else
			position.z -= SHOT_VELOCITY * delta;
		
		if( position.z > Simulation.PLAYFIELD_MAX_Z )
			hasLeftField = true;
		if( position.z < Simulation.PLAYFIELD_MIN_Z )
			hasLeftField = true;
	}
}
