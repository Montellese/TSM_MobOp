package mse.tsm.mobop.starshooter.game.simulation;

import java.io.Serializable;
import java.util.ArrayList;

import mse.tsm.mobop.starshooter.game.telephony.*;

public class Simulation extends Thread implements Serializable
{		
	/**
   * 
   */
  private static final long serialVersionUID = 7801873743028773473L;
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
	
	private Com com;
	private float lastTransmittedPosition=10;
	
	public Simulation(Com comu)
	{
		populate();
		com = comu;
		com.registerSimulation(this);
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
		// Don't draw any shots
		if (ship.isExploding || shipOpponent.isExploding)
		{
			removedShots.addAll(shots);
			shots.clear();
			shipShot = null;
			shipOpponentShot = null;
			return;
		}
		
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
          com.sendMinusOneLife();
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
	}
	
	public void moveShipLeft(boolean me, float delta, float scale) 
	{	
		Ship curShip = me ? ship : shipOpponent;
		
		if (curShip.isExploding)
			return;
		
		curShip.position.x -= delta * Ship.SHIP_VELOCITY * scale;
		if (curShip.position.x < PLAYFIELD_MIN_X)
			curShip.position.x = PLAYFIELD_MIN_X;
		
    if(me)
      transmitMyPosition();
	}

	public void moveShipRight(boolean me, float delta, float scale) 
	{	
		Ship curShip = me ? ship : shipOpponent;
		
		if (curShip.isExploding)
			return;
		
		curShip.position.x += delta * Ship.SHIP_VELOCITY * scale;
		if (curShip.position.x > PLAYFIELD_MAX_X)
			curShip.position.x = PLAYFIELD_MAX_X;
		
    if(me)
      transmitMyPosition();
	}

	public void setShipPosition(boolean me, float position)
	{
    Ship curShip = me ? ship : shipOpponent;
        
    if (curShip.isExploding)
      return;
    
    if( PLAYFIELD_MIN_X<=position && position<=PLAYFIELD_MAX_X )
      curShip.position.x = position;
    
    if(me)
      transmitMyPosition();
	}
	
	public void shot(boolean me) 
	{	
		// We don't allow shooting while a ship is exploding
		if (ship.isExploding || shipOpponent.isExploding)
			return;
		
		Ship curShip = me ? ship : shipOpponent;
		Shot curShot = me ? shipShot : shipOpponentShot;
		
		if (curShot == null && !curShip.isExploding)
		{
			curShot = new Shot(curShip.position, !me);
			shots.add(curShot);
			if (listener != null)
				listener.shot();
			if (me)
				com.sendShot(curShip.position.x);
		}
	}		
	
	private void transmitMyPosition()
	{
	  if( Math.abs(lastTransmittedPosition-ship.position.x)>0.01 )
	  {
	    lastTransmittedPosition = ship.position.x;
      com.setPos(ship.position.x);
	  }
	}
	
	/** Decimates a ships lifes and reports if a ship has entirely been destroyed
	 * 
	 * @param me   true if its our ship otherwhise false
	 * @return     true if ship is fully destroyed now
	 */
	public boolean looseLife(boolean me)
	{
	    Ship curShip = me ? ship : shipOpponent;
	    
	    curShip.lives--;
	    curShip.isExploding = true;
	    explosions.add(new Explosion(curShip.position));
	    if (listener != null)
	    	listener.explosion();
	    
	    return curShip.lives<=0;
	}
	
	public boolean isDead(boolean me)
	{
    Ship curShip = me ? ship : shipOpponent;

    return curShip.lives<=0;
	}
	
}
