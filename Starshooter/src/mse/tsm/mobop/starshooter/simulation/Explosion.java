package mse.tsm.mobop.starshooter.simulation;

import java.io.Serializable;

public class Explosion implements Serializable
{
	public static final float EXPLOSION_LIVE_TIME = 1;
	public float aliveTime = 0;
	public final Vector position = new Vector( );
	
	public Explosion( Vector position )
	{
		this.position.set( position );
	}
	
	public void update( float delta )
	{
		aliveTime += delta;
	}	
}
