package mse.tsm.mobop.starshooter;

public class Test_Player extends Player
{
  private boolean forward = true;
  
  public Test_Player()
  {
    super();
    this.start();
  }

  public void run()
  {
    if( (forward  && (this.getPosition() == this.POSITION_MAX) ) ||
        (!forward && (this.getPosition() == this.POSITION_MIN) ) )
        forward = !forward;
    
    if( forward )
      this.setAcceleration(this.ACCELERATION_MAX/100);
    else
      this.setAcceleration(this.ACCELERATION_MIN/100);
    
    this.updateFromAcceleration();
    
    try {
      sleep(200);
    }
    catch(InterruptedException e) {
    }
    run();
  }
}
