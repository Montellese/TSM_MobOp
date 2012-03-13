package mse.tsm.mobop.starshooter.game;

import mse.tsm.mobop.starshooter.game.simulation.Ship;

/** Player template **/
public abstract class Player
{
  /** defines whether this player is stored as opponent (i.e. not owned by this device's user) **/
  private boolean opponent;
  
  private Ship ship;
  
  /** construct Player as owner **/
  public Player()
  {
    this(false);
  }

  /** construct Player decideable whether it's owned by this device or by opponent 
   * @param   opponent   defines whether this ship is stored as opponend (i.e. not owned by this device's user) **/
  public Player(boolean opponent)
  {
	super();
    this.opponent = opponent;
    this.ship = new Ship();
  }
}
