package mse.tsm.mobop.starshooter.game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.simulation.Explosion;
import mse.tsm.mobop.starshooter.game.simulation.Ship;
import mse.tsm.mobop.starshooter.game.simulation.Shot;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.simulation.Vector;
import mse.tsm.mobop.starshooter.game.tools.Font;
import mse.tsm.mobop.starshooter.game.tools.Font.FontStyle;
import mse.tsm.mobop.starshooter.game.tools.Font.Text;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import mse.tsm.mobop.starshooter.game.tools.Mesh;
import mse.tsm.mobop.starshooter.game.tools.Mesh.PrimitiveType;
import mse.tsm.mobop.starshooter.game.tools.Texture;
import android.opengl.GLU;

public class Renderer 
{
	Mesh shipMesh_base, shipMesh_tail;
	Mesh shipOpponentMesh_base, shipOpponentMesh_tail;
	Texture shipTexture;
	
	Mesh shotMesh;
	
	Mesh backgroundMesh;
	Texture backgroundTexture;
	
	Mesh explosionMesh;
	Texture explosionTexture;
	float shippos=0.0f, opponenshippos=0.0f;
	
	Font font;
	Text text;
	
	public Renderer(GL10 gl, GameActivity activity)
	{
		try
		{
		  // draw own ship
		    // basement
      shipMesh_base = new Mesh(gl, 3, true, false, false);
      shipMesh_base.color(0, 1, 0, 1);
      shipMesh_base.vertex(-0.075f, 0.0f, 0.0001f);
      shipMesh_base.color(0, 1, 0, 1);
      shipMesh_base.vertex(0.075f, 0.0f, 0.0001f);
      shipMesh_base.color(0, 1, 0, 1);
      shipMesh_base.vertex(0.0f, 0.18f, 0.0001f);
        // tail
      shipMesh_tail = new Mesh(gl, 4, true, false, false);
      shipMesh_tail.color(0.1f, .66f, 0, 1);
      shipMesh_tail.vertex(-0.01f, 0.0f, 0.0001f);
      shipMesh_tail.color(0.1f, .66f, 0, 1);
      shipMesh_tail.vertex( 0.01f, 0.0f, 0.0001f);
      shipMesh_tail.color(0.1f, .66f, 0, 1);
      shipMesh_tail.vertex(0.0f, 0.14f, 0.0001f);
      shipMesh_tail.color(0.1f, .66f, 0, 1);
      shipMesh_tail.vertex(0.0f, 0.0f, 0.08f);
			
      // opponen ship
        //basement
			shipOpponentMesh_base = new Mesh(gl, 3, true, false, false);
			shipOpponentMesh_base.color(1, 1, 0, 1);
			shipOpponentMesh_base.vertex(-0.075f, 0.0f, 0.0001f);
			shipOpponentMesh_base.color(1, 1, 0, 1);
			shipOpponentMesh_base.vertex(0.075f, 0.0f, 0.0001f);
			shipOpponentMesh_base.color(1, 1, 0, 1);
			shipOpponentMesh_base.vertex(0.0f, 0.18f, 0.0001f);
        // tail
			shipOpponentMesh_tail = new Mesh(gl, 4, true, false, false);
      shipOpponentMesh_tail.color(0.66f, .66f, 0, 1);
      shipOpponentMesh_tail.vertex(-0.01f, 0.0f, 0.0001f);
      shipOpponentMesh_tail.color(0.66f, .66f, 0, 1);
      shipOpponentMesh_tail.vertex( 0.01f, 0.0f, 0.0001f);
      shipOpponentMesh_tail.color(0.66f, .66f, 0, 1);
      shipOpponentMesh_tail.vertex(0.0f, 0.14f, 0.0001f);
      shipOpponentMesh_tail.color(0.66f, .66f, 0, 1);
      shipOpponentMesh_tail.vertex(0.0f, 0.0f, 0.08f);
			
			shotMesh = new Mesh(gl, 100, true, false, false);
			for (int index = 0; index < 100; index++)
			{
				double angle = index * 2 * Math.PI / 100;
				shotMesh.color(1, 0, 0, 1);
				shotMesh.vertex((float)(Math.cos(angle) * 0.02), (float)(Math.sin(angle) * 0.02), 0.0001f);
			}
			
			/* TODO
			shipMesh = MeshLoader.loadObj(gl, activity.getAssets().open( "ship.obj" ));
			shotMesh = MeshLoader.loadObj(gl, activity.getAssets().open( "shot.obj" ));
			
			backgroundMesh = new Mesh(gl, 4, false, true, false);
			backgroundMesh.texCoord(0, 0);
			backgroundMesh.vertex(-1, 1, 0);
			backgroundMesh.texCoord(1, 0);
			backgroundMesh.vertex(1, 1, 0);
			backgroundMesh.texCoord(1, 1);
			backgroundMesh.vertex(1, -1, 0);
			backgroundMesh.texCoord(0, 1);
			backgroundMesh.vertex(-1, -1, 0);
			
			explosionMesh = new Mesh(gl, 4 * 16, false, true, false);
			for (int row = 0; row < 4; row++)
			{
				for (int column = 0; column < 4; column++)
				{
					explosionMesh.texCoord(0.25f + column * 0.25f, 0 + row * 0.25f);
					explosionMesh.vertex(1, 1, 0 );
					explosionMesh.texCoord(0 + column * 0.25f, 0 + row * 0.25f);
					explosionMesh.vertex(-1, 1, 0 );
					explosionMesh.texCoord(0f + column * 0.25f, 0.25f + row * 0.25f);
					explosionMesh.vertex(-1, -1, 0 );
					explosionMesh.texCoord(0.25f + column * 0.25f, 0.25f + row * 0.25f);
					explosionMesh.vertex(1, -1, 0);		
				}
			}*/				
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		
		try
		{					
			/*Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open( "ship.png" ));
			shipTexture = new Texture(gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();
			
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "planet.jpg" ) );
			backgroundTexture = new Texture(gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();
						
			bitmap = BitmapFactory.decodeStream(activity.getAssets().open( "explode.png" ));
			explosionTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();*/
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
		
		font = new Font(gl, activity.getAssets(), "Battlev2.ttf", 24, FontStyle.Plain);
		text = font.newText(gl);
	}
	
	public void render(GL10 gl, GameActivity activity, Simulation simulation)
	{		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, activity.getViewportWidth(), activity.getViewportHeight());
		
		//gl.glEnable(GL10.GL_TEXTURE_2D);				
		// TODO: renderBackground(gl);		
		
		setProjectionAndCamera(gl, simulation.ship, activity);
		
		renderShots(gl, simulation.shots);
					
		renderShip(gl, simulation.ship, activity);
		renderShip(gl, simulation.shipOpponent, activity);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// TODO: renderExplosions(gl, simulation.explosions);
		
		set2DProjection(gl, activity);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTranslatef(8, activity.getViewportHeight() - 8, 0);
		text.setText(simulation.ship.lives + " vs " + simulation.shipOpponent.lives);
		text.render();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}	

	/*private void renderBackground(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		backgroundTexture.bind();
		backgroundMesh.render(PrimitiveType.TriangleFan);
	}*/
	
	private void setProjectionAndCamera(GL10 gl, Ship ship, GameActivity activity)
	{
		float ratio = (float) activity.getViewportWidth() / activity.getViewportHeight();
		gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
		gl.glLoadIdentity();                        // reset the matrix to its default state
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix
		
		// Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();   // reset the matrix to its default state
        
        // When using GL_MODELVIEW, you must set the view point
        GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
	}
	
	private void set2DProjection( GL10 gl, GameActivity activity )
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, activity.getViewportWidth(), 0, activity.getViewportHeight());
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	private void renderShip(GL10 gl, Ship ship, GameActivity activity)
	{
		if (ship.isExploding)
			return;
		
		//shipTexture.bind();
		gl.glPushMatrix();
		gl.glTranslatef(ship.position.x, ship.position.y, ship.position.z);
		if (ship.isOpponent)
		{
			gl.glRotatef(180, 0, 0, 1);
      shipOpponentMesh_base.render(PrimitiveType.Triangles);
      shipOpponentMesh_tail.render(PrimitiveType.Triangles);
		}
		else
		{
		  float vel=ship.position.x-shippos;
      gl.glRotatef(Math.max(-35.0f,Math.min(35.0f,vel*(-20.0f) )), 0, 1, 0);
      shipMesh_base.render(PrimitiveType.Triangles);
			shipMesh_tail.render(PrimitiveType.Triangles);

      shippos = ship.position.x;
		}
		gl.glPopMatrix();
	}
	
	private void renderShots(GL10 gl, ArrayList<Shot> shots)
	{
		for (int i = 0; i < shots.size(); i++)
		{
			Shot shot = shots.get(i);
			gl.glPushMatrix();
			gl.glTranslatef(shot.position.x, shot.position.y, shot.position.z);
			shotMesh.render(PrimitiveType.TriangleFan);
			gl.glPopMatrix();
		}
	}
	
	/*private void renderExplosions(GL10 gl, ArrayList<Explosion> explosions) 
	{	
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		explosionTexture.bind();
		for (int i = 0; i < explosions.size(); i++)
		{
			Explosion explosion = explosions.get(i);
			gl.glPushMatrix();
			gl.glTranslatef(explosion.position.x, explosion.position.y, explosion.position.z);
			explosionMesh.render(PrimitiveType.TriangleFan, (int)((explosion.aliveTime / Explosion.EXPLOSION_LIVE_TIME) * 15) * 4, 4);
			gl.glPopMatrix();
		}			
		gl.glDisable(GL10.GL_BLEND);
	}*/
	
	public void dispose()
	{
		//shipTexture.dispose();
		//backgroundTexture.dispose();
		//explosionTexture.dispose();
		font.dispose();
		text.dispose();
		//explosionMesh.dispose();
		shipMesh_base.dispose();
		shipMesh_tail.dispose();
    shipOpponentMesh_base.dispose();
    shipOpponentMesh_tail.dispose();
		//shotMesh.dispose();
		//backgroundMesh.dispose();
	}
}
