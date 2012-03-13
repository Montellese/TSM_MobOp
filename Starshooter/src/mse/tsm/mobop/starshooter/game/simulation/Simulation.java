package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;
import java.util.ArrayList;

public class Simulation implements Serializable
{		
	public final static float PLAYFIELD_MIN_X = -14;
	public final static float PLAYFIELD_MAX_X = 14;
	public final static float PLAYFIELD_MIN_Z = -15;
	public final static float PLAYFIELD_MAX_Z = 2;
	
	public ArrayList<Shot> shots = new ArrayList<Shot>( );
	public ArrayList<Explosion> explosions = new ArrayList<Explosion>( );
	public Ship ship;
	public Shot shipShot = null;
	public transient SimulationListener listener;
	public float multiplier = 1;
	public int score;
	
	private ArrayList<Shot> removedShots = new ArrayList<Shot>();
	private ArrayList<Explosion> removedExplosions = new ArrayList<Explosion>( );
	
	public Simulation()
	{
		populate();
	}
	
	private void populate()
	{
		ship = new Ship();
	}
	
	public void update(float delta)
	{			
		ship.update(delta);
		updateShots(delta);
		updateExplosions(delta);
		checkShipCollision();	
	}
	
	private void updateShots( float delta )
	{
		removedShots.clear();
		for (int i = 0; i < shots.size(); i++)
		{
			Shot shot = shots.get(i);
			shot.update(delta);
			if (shot.hasLeftField)
				removedShots.add(shot);
		}
		
		for (int i = 0; i < removedShots.size(); i++)		
			shots.remove(removedShots.get(i));
		
		if (shipShot != null && shipShot.hasLeftField)
			shipShot = null;
	}
	
	public void updateExplosions(float delta)
	{
		removedExplosions.clear();
		for (int i = 0; i < explosions.size(); i++)
		{
			Explosion explosion = explosions.get(i);
			explosion.update(delta);
			if (explosion.aliveTime > Explosion.EXPLOSION_LIVE_TIME)
				removedExplosions.add(explosion);
		}
		
		for (int i = 0; i < removedExplosions.size(); i++)
			explosions.remove(removedExplosions.get(i));
	}

	private void checkShipCollision() 
	{	
		removedShots.clear();
		
		if (!ship.isExploding)
		{
			for (int i = 0; i < shots.size(); i++)
			{
				Shot shot = shots.get(i);
				if (!shot.isInvaderShot)
					continue;											
				
				if (ship.position.distance(shot.position) < Ship.SHIP_RADIUS)
				{					
					removedShots.add(shot);
					shot.hasLeftField = true;
					ship.lives--;
					ship.isExploding = true;
					explosions.add(new Explosion(ship.position) );
					if (listener != null)
						listener.explosion();
					break;
				}			
			}
			
			for (int i = 0; i < removedShots.size(); i++)		
				shots.remove(removedShots.get(i));
		}
	}
	
	public void moveShipLeft(float delta, float scale) 
	{	
		if (ship.isExploding)
			return;
		
		ship.position.x -= delta * Ship.SHIP_VELOCITY * scale;
		if (ship.position.x < PLAYFIELD_MIN_X)
			ship.position.x = PLAYFIELD_MIN_X;
	}

	public void moveShipRight(float delta, float scale) 
	{	
		if (ship.isExploding)
			return;
		
		ship.position.x += delta * Ship.SHIP_VELOCITY * scale;
		if (ship.position.x > PLAYFIELD_MAX_X)
			ship.position.x = PLAYFIELD_MAX_X;
	}

	public void shot() 
	{	
		if (shipShot == null && !ship.isExploding)
		{
			shipShot = new Shot (ship.position, false);			
			shots.add(shipShot);
			if( listener != null )
				listener.shot();
		}
	}		
}
