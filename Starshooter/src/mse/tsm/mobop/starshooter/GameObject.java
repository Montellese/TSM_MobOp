package mse.tsm.mobop.starshooter;

public abstract class GameObject implements IDrawable, IMoveable {
	
	/** position relative to it's owners view **/
	protected float position;
	/** velocity relative to it's owners view **/
	protected float velocity;
	/** acceleration relative to it's owners view **/
	protected float acceleration;
	
	public GameObject()
	{
		this.setPosition(0.0f);
		this.setVelocity(0.0f);
		this.setAcceleration(0.0f);
	}
	
	public GameObject(float position)
	{
		this();
		this.setPosition(position);
	}
	
	public GameObject(float position, float velocity)
	{
		this(position);
		this.setVelocity(velocity);
	}
	
	public GameObject(float position, float velocity, float acceleration)
	{
		this(position, velocity);
		this.setAcceleration(acceleration);
	}

	public void Move(long deltaTime) {
		this.setVelocity(this.velocity + this.acceleration * deltaTime);
	    this.setPosition(this.position + this.velocity * deltaTime);
	}

	abstract public void Draw();

	protected void setPosition(float position)
	{
		this.position = position;
	}
	
	protected void setVelocity(float velocity)
	{
		this.velocity = velocity;
	}
	
	protected void setAcceleration(float acceleration)
	{
		this.acceleration = acceleration;
	}
}
