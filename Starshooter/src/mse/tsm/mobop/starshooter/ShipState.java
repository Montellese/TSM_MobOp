package mse.tsm.mobop.starshooter;

import android.os.SystemClock;

public abstract class ShipState
{
  /** ship's position relative to it's owners view **/
  private float position;
  /** ship's velocity relative to it's owners view **/
  private float velocity;
  /** ship's acceleration relative to it's owners view **/
  private float acceleration;
  /** ingame elapsed time **/
  private long time;
  /** defines whether this ship is stored as opponend (i.e. not owned by this device's user) **/
  private boolean opponent;

  
  /** maximum possible acceleration in positiv direction **/
  protected final float ACCELERATION_MAX = .5f;
  /** maximum possible acceleration in negativ direction **/
  protected final float ACCELERATION_MIN = -ACCELERATION_MAX;
  
  /** maximum possible velocity in positiv direction **/
  protected final float VELOCITY_MAX = 2;
  /** maximum possible velocity in negativ direction **/
  protected final float VELOCITY_MIN = -VELOCITY_MAX;
  
  /** maximum possible position in positiv direction **/
  protected final float POSITION_MAX = 1;
  /** maximum possible position in negativ direction **/
  protected final float POSITION_MIN = -POSITION_MAX;
  
  /** construct ShipState as owner **/
  public ShipState()
  {
    this(false);
  }

  /** construct ShipState decideable whether it's owned by this device or by opponent 
   * @param   opponent   defines whether this ship is stored as opponend (i.e. not owned by this device's user) **/
  public ShipState(boolean opponent)
  {
    position = 0;
    acceleration = 0;
    time = SystemClock.uptimeMillis();
    this.opponent = opponent;
  }
  
  /** get ships position relative to device's screen */
  public float getPosition()
  {
    return (opponent?-1*position:position);
  }

  public void resetPosition()
  {
    resetPosition(0);
  }
  public void resetPosition(float position)
  {
    this.position = position;
    this.velocity = 0;
    this.acceleration = 0;
  }


  protected void setPosition(float position)
  {
    this.position = Math.max(Math.min(position,POSITION_MAX),POSITION_MIN);
  }
  protected void setVelocity(float velocity)
  {
    this.velocity = Math.max(Math.min(velocity,VELOCITY_MAX),VELOCITY_MIN);
  }
  public void setAcceleration(float acceleration)
  {
    this.acceleration = Math.max(Math.min(acceleration,ACCELERATION_MAX),ACCELERATION_MIN);
  }
  
  public void updateFromAcceleration()
  {
    long time2 = SystemClock.uptimeMillis();
    long timediv = time2-time;
    time = time2;
    setVelocity(velocity + acceleration*timediv);
    setPosition(position + velocity*timediv);
  }

}
