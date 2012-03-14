package mse.tsm.mobop.starshooter.game.screens;

import android.opengl.GLU;
import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.tools.Font;
import mse.tsm.mobop.starshooter.game.tools.Font.FontStyle;
import mse.tsm.mobop.starshooter.game.tools.Font.Text;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;

public class StartScreen implements GameScreen
{	
	boolean isDone = false;

	Font font;
	Text textTop;
	Text textBottom;
	String textWaitingTop = "Waiting for";
	String textWaitingBottom = "your opponent";
	
	public StartScreen(GL10 gl, GameActivity activity)
	{			
		font = new Font(gl, activity.getAssets(), "Battlev2.ttf", 32, FontStyle.Plain);
		textTop = font.newText(gl);
		textTop.setText(textWaitingTop);
		textBottom = font.newText(gl);
		textBottom.setText(textWaitingBottom);
	}	

	public boolean isDone() 
	{	
		return isDone;
	}

	public void update(GameActivity activity) 
	{
		// TODO: Update when both players are ready
		if (activity.isTouched())
			isDone = true;
	}
	
	public void render(GL10 gl, GameActivity activity) 
	{	
		gl.glViewport(0, 0, activity.getViewportWidth(), activity.getViewportHeight());
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		
		gl.glMatrixMode( GL10.GL_PROJECTION );
		GLU.gluOrtho2D( gl, 0, activity.getViewportWidth(), 0, activity.getViewportHeight() );
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		
		gl.glLoadIdentity();
		float x = activity.getViewportWidth() / 2 - font.getStringWidth(textWaitingTop) / 2;
		float y = activity.getViewportHeight() / 2 + font.getLineHeight();
		gl.glTranslatef(x, y, 0);
		textTop.render();
		
		gl.glLoadIdentity();
		x = activity.getViewportWidth() / 2 - font.getStringWidth(textWaitingBottom) / 2;
		y = activity.getViewportHeight() / 2 - font.getLineHeight();
		gl.glTranslatef(x, y, 0);
		textBottom.render();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
	}
	
	public void dispose() 
	{	
		font.dispose();
		textTop.dispose();
		textBottom.dispose();
	}
}
