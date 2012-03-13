package mse.tsm.mobop.starshooter.game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLU;

import mse.tsm.mobop.starshooter.game.simulation.Explosion;
import mse.tsm.mobop.starshooter.game.simulation.Ship;
import mse.tsm.mobop.starshooter.game.simulation.Shot;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import mse.tsm.mobop.starshooter.game.tools.Mesh;
import mse.tsm.mobop.starshooter.game.tools.MeshLoader;
import mse.tsm.mobop.starshooter.game.tools.Texture;
import mse.tsm.mobop.starshooter.game.tools.Mesh.PrimitiveType;
import mse.tsm.mobop.starshooter.game.tools.Texture.TextureFilter;
import mse.tsm.mobop.starshooter.game.tools.Texture.TextureWrap;
import mse.tsm.mobop.starshooter.game.tools.Font;
import mse.tsm.mobop.starshooter.game.tools.Font.FontStyle;
import mse.tsm.mobop.starshooter.game.tools.Font.Text;

public class Renderer 
{
	Mesh shipMesh;
	Texture shipTexture;
	
	Mesh shotMesh;
	
	Mesh backgroundMesh;
	Texture backgroundTexture;
	
	Mesh explosionMesh;
	Texture explosionTexture;
	
	Font font;
	Text text;
	
	int lastScore = 0;
	int lastLives = 0;
	int lastWave = 0;
	
	public Renderer(GL10 gl, GameActivity activity)
	{
		try
		{
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
			}					
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		
		try
		{					
			Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open( "ship.png" ));
			shipTexture = new Texture(gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();
			
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "planet.jpg" ) );
			backgroundTexture = new Texture(gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();
						
			bitmap = BitmapFactory.decodeStream(activity.getAssets().open( "explode.png" ));
			explosionTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
			bitmap.recycle();
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
		
		font = new Font(gl, activity.getAssets(), "font.ttf", 16, FontStyle.Plain);
		text = font.newText(gl);
		
		float[] lightColor = { 1, 1, 1, 1 };
		float[] ambientLightColor = { 0.0f, 0.0f, 0.0f, 1};		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLightColor, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightColor, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightColor, 0);
	}
	
	public void render(GL10 gl, GameActivity activity, Simulation simulation)
	{		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, activity.getViewportWidth(), activity.getViewportHeight());					
		
		gl.glEnable(GL10.GL_TEXTURE_2D);				
		renderBackground(gl);		
		
		gl.glDisable(GL10.GL_DITHER);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);		
		
		setProjectionAndCamera(gl, simulation.ship, activity);
		setLighting(gl);
					
		renderShip(gl, simulation.ship, activity);
		
		gl.glDisable( GL10.GL_LIGHTING );
		renderShots(gl, simulation.shots);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		renderExplosions(gl, simulation.explosions);
		
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_DEPTH_TEST);
	
		set2DProjection(gl, activity);
		
		/* TODO
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTranslatef(0, activity.getViewportHeight(), 0);
		if (simulation.ship.lives != lastLives || simulation.score != lastScore || simulation.wave != lastWave)
		{
			text.setText("lives: " + simulation.ship.lives + " wave: " + simulation.wave + " score: " + simulation.score);
			lastLives = simulation.ship.lives;
			lastScore = simulation.score;
			lastWave = simulation.wave;
		}
		text.render();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);*/
	}	

	private void renderBackground(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		backgroundTexture.bind();
		backgroundMesh.render(PrimitiveType.TriangleFan);
	}
	
	private void setProjectionAndCamera(GL10 gl, Ship ship, GameActivity activity)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float aspectRatio = (float)activity.getViewportWidth() / activity.getViewportHeight();
		GLU.gluPerspective(gl, 67, aspectRatio, 1, 1000);					
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, ship.position.x, 6, 2, ship.position.x, 0, -4, 0, 1, 0);
	}
	
	private void set2DProjection(GL10 gl, GameActivity activity)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0, activity.getViewportWidth(), 0, activity.getViewportHeight());
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	float[] direction = { 1, 0.5f, 0, 0 };	
	private void setLighting(GL10 gl)
	{
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);				
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, direction, 0);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);		
	}
	
	private void renderShip(GL10 gl, Ship ship, GameActivity activity)
	{
		if (ship.isExploding)
			return;
		
		shipTexture.bind();
		gl.glPushMatrix();
		gl.glTranslatef( ship.position.x, ship.position.y, ship.position.z);
		gl.glRotatef(45 * (-activity.getAccelerationOnYAxis() / 5), 0, 0, 1);
		gl.glRotatef(180, 0, 1, 0);
		shipMesh.render(PrimitiveType.Triangles);
		gl.glPopMatrix();
	}
	
	private void renderShots(GL10 gl, ArrayList<Shot> shots)
	{
		gl.glColor4f(1, 1, 0, 1);
		for (int i = 0; i < shots.size(); i++)
		{
			Shot shot = shots.get(i);
			gl.glPushMatrix();
			gl.glTranslatef(shot.position.x, shot.position.y, shot.position.z);
			shotMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}		
		gl.glColor4f(1, 1, 1, 1);
	}
	
	private void renderExplosions(GL10 gl, ArrayList<Explosion> explosions) 
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
	}
	
	public void dispose()
	{
		shipTexture.dispose();
		backgroundTexture.dispose();
		explosionTexture.dispose();
		font.dispose();
		text.dispose();
		explosionMesh.dispose();
		shipMesh.dispose();
		shotMesh.dispose();
		backgroundMesh.dispose();
	}
}
