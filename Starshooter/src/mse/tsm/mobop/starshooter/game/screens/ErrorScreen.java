package mse.tsm.mobop.starshooter.game.screens;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.tools.Font;
import mse.tsm.mobop.starshooter.game.tools.Font.FontStyle;
import mse.tsm.mobop.starshooter.game.tools.Font.Text;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import android.opengl.GLU;

public class ErrorScreen implements GameScreen 
{ 
  boolean isDone = false;
  
  Font font;
  Font fontSmall;
  Text text;
  Text textSmall;
  String textEnd = "";
  String textTouch = "Touch to continue";
  
  public ErrorScreen(GL10 gl, GameActivity activity, String msg, int width)
  {
    textEnd = msg;
    font = new Font(gl, activity.getAssets(), "Battlev2.ttf", width/20 /*24*/, FontStyle.Plain);
    text = font.newText(gl);
    text.setText(textEnd);
    
    fontSmall = new Font(gl, activity.getAssets(), "Battlev2.ttf", width/22 /*22*/, FontStyle.Plain);
    textSmall = fontSmall.newText(gl);
    textSmall.setText(textTouch);
  } 

  public boolean isDone() 
  { 
    return isDone;
  }

  public void update(GameActivity activity) 
  { 
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
    
    gl.glEnable(GL10.GL_BLEND);
    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    
    gl.glMatrixMode(GL10.GL_PROJECTION);
    GLU.gluOrtho2D(gl, 0, activity.getViewportWidth(), 0, activity.getViewportHeight());
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();
    
    gl.glLoadIdentity();
    float x = activity.getViewportWidth() / 2 - font.getStringWidth(textEnd) / 2;
    float y = activity.getViewportHeight() / 2 + font.getLineHeight() / 2;
    gl.glTranslatef(x, y, 0);
    text.render();
    
    gl.glLoadIdentity();
    x = activity.getViewportWidth() / 2 - fontSmall.getStringWidth(textTouch) / 2;
    y = y - font.getLineHeight();
    gl.glTranslatef(x, y, 0);
    textSmall.render();
    
    gl.glDisable( GL10.GL_TEXTURE_2D );
    gl.glDisable( GL10.GL_BLEND );
  }
  
  public void dispose() 
  { 
    font.dispose();
    fontSmall.dispose();
    text.dispose();
    textSmall.dispose();
  }
}
