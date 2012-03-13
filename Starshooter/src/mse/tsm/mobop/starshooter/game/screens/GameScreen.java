package mse.tsm.mobop.starshooter.game.screens;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.tools.GameActivity;

public interface GameScreen 
{
	public void update(GameActivity activity);
	public void render(GL10 gl, GameActivity activity);
	public boolean isDone();
	public void dispose();
}
