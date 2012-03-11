package mse.tsm.mobop.starshooter;

/** Player template **/
public abstract class Player extends GameObject
{
  /** defines whether this player is stored as opponent (i.e. not owned by this device's user) **/
  private boolean opponent;
  
  private static short playerclass_id;
  private static short playerclassesCount = 0;
  
  /** maximum possible acceleration in positiv direction **/
  protected final float ACCELERATION_MAX = .5f;
  /** maximum possible acceleration in negativ direction **/
  protected final float ACCELERATION_MIN = -ACCELERATION_MAX;
  
  /** maximum possible velocity in positiv direction **/
  protected final float VELOCITY_MAX = 1;
  /** maximum possible velocity in negativ direction **/
  protected final float VELOCITY_MIN = -VELOCITY_MAX;
  
  /** maximum possible position in positiv direction **/
  protected final float POSITION_MAX = 1;
  /** maximum possible position in negativ direction **/
  protected final float POSITION_MIN = -POSITION_MAX;
  
  static
  {
    playerclass_id = playerclassesCount++;
  }
  
  protected final static void registerClass(String PlayerName)
  {
    
  }
  
  public final static short getClassId()
  {
    return playerclass_id;
  }
  
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
  }
  
  /** get ships position relative to device's screen */
  public float getPosition()
  {
    return (opponent ? -position : position);
  }

  public void resetPosition()
  {
    resetPosition(0.0f);
  }
  
  public void resetPosition(float position)
  {
    this.setPosition(position);
    this.setVelocity(0);
    this.setAcceleration(0);
  }

  @Override
  protected void setPosition(float position)
  {
	super.setPosition(Math.max(Math.min(position,POSITION_MAX),POSITION_MIN));
  }

  @Override
  protected void setVelocity(float velocity)
  {
	super.setVelocity(Math.max(Math.min(velocity,VELOCITY_MAX),VELOCITY_MIN));
  }

  @Override
  public void setAcceleration(float acceleration)
  {
	super.setAcceleration(Math.max(Math.min(acceleration,ACCELERATION_MAX),ACCELERATION_MIN));
  }

  abstract public void Draw();
}
