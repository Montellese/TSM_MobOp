package mse.tsm.mobop.starshooter;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLU;
import android.util.Log;

import mse.tsm.mobop.starshooter.simulation.Block;
import mse.tsm.mobop.starshooter.simulation.Explosion;
import mse.tsm.mobop.starshooter.simulation.Ship;
import mse.tsm.mobop.starshooter.simulation.Shot;
import mse.tsm.mobop.starshooter.simulation.Simulation;
import mse.tsm.mobop.starshooter.tools.Font;
import mse.tsm.mobop.starshooter.tools.GameActivity;
import mse.tsm.mobop.starshooter.tools.Mesh;
import mse.tsm.mobop.starshooter.tools.MeshLoader;
import mse.tsm.mobop.starshooter.tools.Texture;
import mse.tsm.mobop.starshooter.tools.Font.FontStyle;
import mse.tsm.mobop.starshooter.tools.Font.Text;
import mse.tsm.mobop.starshooter.tools.Mesh.PrimitiveType;
import mse.tsm.mobop.starshooter.tools.Texture.TextureFilter;
import mse.tsm.mobop.starshooter.tools.Texture.TextureWrap;

public class Renderer 
{
	Mesh shipMesh;
	Texture shipTexture;
	Mesh invaderMesh;
	Texture invaderTexture;
	Mesh blockMesh;
	Mesh shotMesh;
	Mesh backgroundMesh;
	Texture backgroundTexture;
	Mesh explosionMesh;
	Texture explosionTexture;	
	Font font;
	Text text;
	float invaderAngle = 0;
	int lastScore = 0;
	int lastLives = 0;
	int lastWave = 0;
	
	public Renderer( GL10 gl, GameActivity activity )
	{
		try
		{
			shipMesh = MeshLoader.loadObj(gl, activity.getAssets().open( "ship.obj" ) );
			invaderMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "invader.obj" ) );
			blockMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "block.obj" ) );
			shotMesh = MeshLoader.loadObj( gl, activity.getAssets().open( "shot.obj" ) );
			
			backgroundMesh = new Mesh( gl, 4, false, true, false );
			backgroundMesh.texCoord(0, 0);
			backgroundMesh.vertex(-1, 1, 0 );
			backgroundMesh.texCoord(1, 0);
			backgroundMesh.vertex(1, 1, 0 );
			backgroundMesh.texCoord(1, 1);
			backgroundMesh.vertex(1, -1, 0 );
			backgroundMesh.texCoord(0, 1);
			backgroundMesh.vertex(-1, -1, 0 );
			
			explosionMesh = new Mesh( gl, 4 * 16, false, true, false );
			for( int row = 0; row < 4; row++ )
			{
				for( int column = 0; column < 4; column++ )
				{
					explosionMesh.texCoord( 0.25f + column * 0.25f, 0 + row * 0.25f );
					explosionMesh.vertex( 1, 1, 0 );
					explosionMesh.texCoord( 0 + column * 0.25f, 0 + row * 0.25f );
					explosionMesh.vertex( -1, 1, 0 );
					explosionMesh.texCoord( 0f + column * 0.25f, 0.25f + row * 0.25f );
					explosionMesh.vertex( -1, -1, 0 );
					explosionMesh.texCoord( 0.25f + column * 0.25f, 0.25f + row * 0.25f );
					explosionMesh.vertex( 1, -1, 0 );		
				}
			}					
		}
		catch( Exception ex )
		{
			Log.d( "Space Invaders", "couldn't load meshes" );
			throw new RuntimeException( ex );
		}
		
		try
		{					
			Bitmap bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "ship.png" ) );
			shipTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
			
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "invader.png" ));
			invaderTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
			
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "planet.jpg" ) );
			backgroundTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
						
			bitmap = BitmapFactory.decodeStream( activity.getAssets().open( "explode.png" ) );
			explosionTexture = new Texture( gl, bitmap, TextureFilter.MipMap, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
			bitmap.recycle();
		}
		catch( Exception ex )
		{
			Log.d( "Space Invaders", "couldn't load textures" );
			throw new RuntimeException( ex );
		}
		
		font = new Font( gl, activity.getAssets(), "font.ttf", 16, FontStyle.Plain );
		text = font.newText( gl );
		
		float[] lightColor = { 1, 1, 1, 1 };
		float[] ambientLightColor = {0.0f, 0.0f, 0.0f, 1 };		
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLightColor, 0 );
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightColor, 0 );
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightColor, 0 );
	}
	
	public void render( GL10 gl, GameActivity activity, Simulation simulation )
	{		
		gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		gl.glViewport( 0, 0, activity.getViewportWidth(), activity.getViewportHeight() );					
		
		gl.glEnable( GL10.GL_TEXTURE_2D );				
		renderBackground( gl );		
		
		gl.glDisable( GL10.GL_DITHER );
		gl.glEnable( GL10.GL_DEPTH_TEST );
		gl.glEnable( GL10.GL_CULL_FACE );		
		
		setProjectionAndCamera( gl, simulation.ship, activity );
		setLighting( gl );
					
		renderShip( gl, simulation.ship, activity );
		//renderInvaders( gl, simulation.invaders );
		
		gl.glDisable( GL10.GL_TEXTURE_2D );
		renderBlocks( gl, simulation.blocks );
		
		gl.glDisable( GL10.GL_LIGHTING );
		renderShots( gl, simulation.shots );
		
		gl.glEnable( GL10.GL_TEXTURE_2D );
		renderExplosions( gl, simulation.explosions );
		
		gl.glDisable( GL10.GL_CULL_FACE );
		gl.glDisable( GL10.GL_DEPTH_TEST );
	
		set2DProjection(gl, activity);
		
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		gl.glTranslatef( 0, activity.getViewportHeight(), 0 );
		if( simulation.ship.lives != lastLives || simulation.score != lastScore || simulation.wave != lastWave )
		{
			text.setText( "lives: " + simulation.ship.lives + " wave: " + simulation.wave + " score: " + simulation.score );
			lastLives = simulation.ship.lives;
			lastScore = simulation.score;
			lastWave = simulation.wave;
		}
		text.render();
		gl.glDisable( GL10.GL_BLEND);
		gl.glDisable( GL10.GL_TEXTURE_2D );
		
		invaderAngle+=activity.getDeltaTime() * 90;
		if( invaderAngle > 360 )
			invaderAngle -= 360;
	}	

	private void renderBackground( GL10 gl )
	{
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		backgroundTexture.bind();
		backgroundMesh.render(PrimitiveType.TriangleFan);
	}
	
	private void setProjectionAndCamera( GL10 gl, Ship ship, GameActivity activity )
	{
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		float aspectRatio = (float)activity.getViewportWidth() / activity.getViewportHeight();
		GLU.gluPerspective( gl, 67, aspectRatio, 1, 1000 );					
		
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		GLU.gluLookAt( gl, ship.position.x, 6, 2, ship.position.x, 0, -4, 0, 1, 0 );
	}
	
	private void set2DProjection( GL10 gl, GameActivity activity )
	{
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		GLU.gluOrtho2D( gl, 0, activity.getViewportWidth(), 0, activity.getViewportHeight() );
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
	}
	
	float[] direction = { 1, 0.5f, 0, 0 };	
	private void setLighting( GL10 gl )
	{
		gl.glEnable( GL10.GL_LIGHTING );
		gl.glEnable( GL10.GL_LIGHT0 );				
		gl.glLightfv( GL10.GL_LIGHT0, GL10.GL_POSITION, direction, 0 );
		gl.glEnable( GL10.GL_COLOR_MATERIAL );		
	}
	
	private void renderShip( GL10 gl, Ship ship, GameActivity activity )
	{
		if( ship.isExploding )
			return;
		
		shipTexture.bind();
		gl.glPushMatrix();
		gl.glTranslatef( ship.position.x, ship.position.y, ship.position.z );
		gl.glRotatef( 45 * (-activity.getAccelerationOnYAxis() / 5), 0, 0, 1 );
		gl.glRotatef( 180, 0, 1, 0 );
		shipMesh.render(PrimitiveType.Triangles);
		gl.glPopMatrix();
	}
	
	/* private void renderInvaders( GL10 gl, ArrayList<Invader> invaders )
	{
		invaderTexture.bind();
		for( int i = 0; i < invaders.size(); i++ )
		{
			Invader invader = invaders.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( invader.position.x, invader.position.y, invader.position.z );
			gl.glRotatef( invaderAngle, 0, 1, 0 );
			invaderMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}
	}*/
	
	private void renderBlocks( GL10 gl, ArrayList<Block> blocks )
	{		
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		gl.glColor4f( 0.2f, 0.2f, 1, 0.7f );
		for( int i = 0; i < blocks.size(); i++ )
		{
			Block block = blocks.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( block.position.x, block.position.y, block.position.z );
			blockMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}
		gl.glColor4f( 1, 1, 1, 1 );
		gl.glDisable( GL10.GL_BLEND );
	}
	
	private void renderShots( GL10 gl, ArrayList<Shot> shots )
	{
		gl.glColor4f( 1, 1, 0, 1 );
		for( int i = 0; i < shots.size(); i++ )
		{
			Shot shot = shots.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( shot.position.x, shot.position.y, shot.position.z );
			shotMesh.render(PrimitiveType.Triangles);
			gl.glPopMatrix();
		}		
		gl.glColor4f( 1, 1, 1, 1 );
	}
	
	private void renderExplosions(GL10 gl, ArrayList<Explosion> explosions) 
	{	
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		explosionTexture.bind();
		for( int i = 0; i < explosions.size(); i++ )
		{
			Explosion explosion = explosions.get(i);
			gl.glPushMatrix();
			gl.glTranslatef( explosion.position.x, explosion.position.y, explosion.position.z );
			explosionMesh.render(PrimitiveType.TriangleFan, (int)((explosion.aliveTime / Explosion.EXPLOSION_LIVE_TIME) * 15) * 4, 4);
			gl.glPopMatrix();
		}			
		gl.glDisable( GL10.GL_BLEND );
	}
	
	public void dispose( )
	{
		shipTexture.dispose();
		invaderTexture.dispose();
		backgroundTexture.dispose();
		explosionTexture.dispose();
		font.dispose();
		text.dispose();
		explosionMesh.dispose();
		shipMesh.dispose();
		invaderMesh.dispose();
		shotMesh.dispose();
		blockMesh.dispose();
		backgroundMesh.dispose();
	}
}
