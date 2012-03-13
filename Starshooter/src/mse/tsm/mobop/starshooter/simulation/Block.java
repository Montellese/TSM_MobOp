package mse.tsm.mobop.starshooter.simulation;

import java.io.Serializable;

public class Block implements Serializable
{	
	public final static float BLOCK_RADIUS = 0.5f;
	
	public Vector position = new Vector( );
	
	public Block(Vector position) 
	{
		this.position.set( position );
	}
}
