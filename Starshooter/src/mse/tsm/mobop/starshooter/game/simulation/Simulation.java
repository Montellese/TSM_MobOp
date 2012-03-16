package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import mse.tsm.mobop.starshooter.game.telephony.*;

public class Simulation extends Thread implements Serializable
{		
	public final static float PLAYFIELD_MIN_X = -0.5f;
	public final static float PLAYFIELD_MAX_X = 0.5f;
	public final static float PLAYFIELD_MIN_Y = -1f;
	public final static float PLAYFIELD_MAX_Y = 1f;
	
	public ArrayList<Shot> shots = new ArrayList<Shot>( );
	public ArrayList<Explosion> explosions = new ArrayList<Explosion>( );
	public Ship ship, shipOpponent;
	public Shot shipShot = null, shipOpponentShot = null;
	public transient SimulationListener listener;
	
	private ArrayList<Shot> removedShots = new ArrayList<Shot>();
	private ArrayList<Explosion> removedExplosions = new ArrayList<Explosion>( );
	
	private Context ctx;
	private Com com;
	
	public Simulation(Context ctx, Com comu )
	{
		populate();
		com = comu;
		com.setSimulation(this);
		com.registerSimulation(this);
		this.ctx = ctx;
	}
	
	
	private void populate()
	{
		ship = new Ship(false);
		ship.position.y = -1.0f;
		shipOpponent = new Ship(true);
		shipOpponent.position.y = 0.9f;
	}
	
	public void update(float delta)
	{			
		ship.update(delta);
		shipOpponent.update(delta);
		updateShots(delta);
		updateExplosions(delta);
		checkShipCollision();	
	}
	
	private void updateShots(float delta)
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
		if (shipOpponentShot != null && shipOpponentShot.hasLeftField)
			shipOpponentShot = null;
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
				if (!shot.isOpponentShot)
					continue;											
				
				if (ship.position.distance(shot.position) < Ship.SHIP_RADIUS)
				{					
					removedShots.add(shot);
					shot.hasLeftField = true;
					ship.lives--;
					ship.isExploding = true;
					explosions.add(new Explosion(ship.position));
					if (listener != null)
						listener.explosion();
					break;
				}			
			}
			
			for (int i = 0; i < removedShots.size(); i++)		
				shots.remove(removedShots.get(i));
		}
		
		/* Not handling oppponent on this device
		if (!shipOpponent.isExploding)
		{
			for (int i = 0; i < shots.size(); i++)
			{
				Shot shot = shots.get(i);
				if (shot.isOpponentShot)
					continue;											
				
				if (shipOpponent.position.distance(shot.position) < Ship.SHIP_RADIUS)
				{					
					removedShots.add(shot);
					shot.hasLeftField = true;
					shipOpponent.lives--;
					shipOpponent.isExploding = true;
					explosions.add(new Explosion(shipOpponent.position));
					if (listener != null)
						listener.explosion();
					break;
				}			
			}
		
			for (int i = 0; i < removedShots.size(); i++)		
				shots.remove(removedShots.get(i));
		}
    */
	}
	
	public void moveShipLeft(boolean me, float delta, float scale) 
	{	
		Ship curShip = me ? ship : shipOpponent;
		
		if (curShip.isExploding)
			return;
		
		curShip.position.x -= delta * Ship.SHIP_VELOCITY * scale;
		if (curShip.position.x < PLAYFIELD_MIN_X)
			curShip.position.x = PLAYFIELD_MIN_X;
	}

	public void moveShipRight(boolean me, float delta, float scale) 
	{	
		Ship curShip = me ? ship : shipOpponent;
		
		if (curShip.isExploding)
			return;
		
		curShip.position.x += delta * Ship.SHIP_VELOCITY * scale;
		if (curShip.position.x > PLAYFIELD_MAX_X)
			curShip.position.x = PLAYFIELD_MAX_X;
	}

	public void setShipPosition(boolean me, float position)
	{
    Ship curShip = me ? ship : shipOpponent;
        
    if (curShip.isExploding)
      return;
    
    if( PLAYFIELD_MIN_X<=position && position<=PLAYFIELD_MAX_X )
      curShip.position.x = position;
	}
	
	public void shot(boolean me) 
	{	
		Ship curShip = me ? ship : shipOpponent;
		Shot curShot = me ? shipShot : shipOpponentShot;
		
		if (curShot == null && !curShip.isExploding)
		{
			curShot = new Shot(curShip.position, !me);
			shots.add(curShot);
			if( listener != null )
				listener.shot();
		}
	}		
	
	public void looseLife(boolean me)
	{
    Ship curShip = me ? ship : shipOpponent;
    
    curShip.lives--;
    curShip.isExploding = true;
    explosions.add(new Explosion(curShip.position));
    if (listener != null)
      listener.explosion();
	}
}
