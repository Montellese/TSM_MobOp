package mse.tsm.mobop.starshooter;

public class Shot extends GameObject {
	
	public Shot(float position)
	{
		super(position);
		this.setVelocity(0.5f);
	}

	@Override
	public void Draw() {
		// TODO: Shot.Draw()
	}

	@Override
	public void setAcceleration(float acceleration)
	{
		// A shot does not have any acceleration
		this.acceleration = 0.0f;
	}
}
