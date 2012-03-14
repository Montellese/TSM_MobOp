package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;

public class Ship implements Serializable
{
	public static final float SHIP_RADIUS = 0.09f;
	public static final float SHIP_VELOCITY = 10;
	public final Vector position = new Vector();
	public int lives = 3;
	public boolean isExploding = false;
	public float explodeTime = 0;	
	public boolean isOpponent = false;
	
	public Ship()
	{
	}
	
	public Ship(boolean isOpponent)
	{
		this.isOpponent = isOpponent;
	}
	
	public void update( float delta )
	{
		if( isExploding )
		{
			explodeTime += delta;
			if( explodeTime > Explosion.EXPLOSION_LIVE_TIME )
			{
				isExploding = false;
				explodeTime = 0;
			}
		}
	}
}
