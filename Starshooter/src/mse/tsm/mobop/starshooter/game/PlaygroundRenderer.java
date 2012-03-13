package mse.tsm.mobop.starshooter.game;

import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import mse.tsm.mobop.starshooter.game.tools.GameListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class PlaygroundRenderer extends GameActivity implements GameListener 
{
  private FloatBuffer vertices;
  private FloatBuffer colors;

  private Player myShip, opponentShip;
  
  private float myShip_wing_coords[];
  
  /** Basis dimension of wing */
  public static final float ship_wing_coords[] = { -0.075f, 0.0f, 0.1f,    0.075f, 0.0f, 0.1f,    0.0f, 0.18f, 0.1f };
  public static final float ship_rudder_coords[] = { -0.005f, 0.0f, 0.1f,  0.0f, 0.0f, 0.15f,     0.0f, 0.15f, 0.1f };
  public static final float myShip_wing_offset[] = { 0f, -0.95f, 0f };
  public static final float myShip_rudder_offset[] = myShip_wing_offset;
  private static float myShip_wing_coords_base[];
  private static float myShip_rudder_coords_base[];
  
  
  public PlaygroundRenderer()
  {
    myShip = new GSensorPlayer();
    opponentShip = new TCPPlayer(); 
  }

  static 
  {
    try{
      myShip_wing_coords_base = addVec2Vectors(myShip_wing_offset, ship_wing_coords);
      myShip_rudder_coords_base = addVec2Vectors(myShip_rudder_offset, ship_rudder_coords);
      
    
    } catch( Exception e ) {};
  }
  
  public void onCreate(Bundle savedInstance)
  {
    super.onCreate(savedInstance);

    // go fullsreen
    //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
    setGameListener(this);

  }

  public void setup(GameActivity activity, GL10 gl)
  {
    ByteBuffer buffer = ByteBuffer.allocateDirect( 3 * 4 * 3 );
    buffer.order(ByteOrder.nativeOrder());
    vertices = buffer.asFloatBuffer();
    
    vertices.put( -0.5f );
    vertices.put( -0.5f );
    vertices.put( 0 );
    
    vertices.put( 0.5f );
    vertices.put( -0.5f );
    vertices.put( 0 );
    
    vertices.put( 0 );
    vertices.put( 0.5f );
    vertices.put( 0 );
    
    vertices.rewind();
    
    buffer = ByteBuffer.allocateDirect( 3 * 4 * 4 );
    buffer.order(ByteOrder.nativeOrder());
    colors = buffer.asFloatBuffer();
    
    colors.put( 1 );
    colors.put( 0 );
    colors.put( 0 );
    colors.put( 1 );
    
    colors.put( 0 );
    colors.put( 1 );
    colors.put( 0 );
    colors.put( 1 );
    
    colors.put( 0 );
    colors.put( 0 );
    colors.put( 1 );
    colors.put( 1 );
    
    colors.rewind();
  }

  public void mainLoopIteration(GameActivity activity, GL10 gl)
  {
    gl.glViewport( 0, 0, activity.getViewportWidth(), activity.getViewportHeight() );
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY );    
    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
    gl.glEnableClientState(GL10.GL_COLOR_ARRAY );
    gl.glColorPointer( 4, GL10.GL_FLOAT, 0, colors );
    gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
  }
  
  /** add some components to a list of vectors
   * 
   * @param x           x component to add
   * @param y           y component to add
   * @param z           z component to add
   * @param vectors     list of vectors
   * @return            list of vectors with added components
   * @throws Exception  If vectors lengths does not match
   */
  public static float [] addXYZ2Vectors(float x, float y, float z, float[] vectors) throws Exception
  {
    float vec []= { x, y, z };
    
    try{ return addVec2Vectors(vec, vectors); } catch(Exception e) { throw e; }
  }
  /** add some components to a list of vectors
   * 
   * @param vec         component vectors to add
   * @param vectors     list of vectors
   * @return            list of vectors with added components
   * @throws Exception  If vectors lengths does not match
   */
  public static float [] addVec2Vectors(float[] vec2add, float[] vectors) throws Exception
  {
    if( (vectors.length%3 != 0) || (vec2add.length!=3) )
      throw new Exception("Not every vector in given matrix has length 3.");
    
    float retVec[]=new float[vectors.length];
    
    for(int i=0;i<vectors.length;i++)
      retVec[i]=vectors[i]+vec2add[i%3];
    
    return retVec;
  }



}



/*
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.Display;
import android.view.WindowManager;

public class PlaygroundRenderer implements GLSurfaceView.Renderer
{
  private Player myShip, opponentShip;
  private Context ctx;
  
  private int screenHeight, screenWidth;
  
  private FloatBuffer fbuff_myShip_wing, fbuff_myShip_rudder;
  private ByteBuffer bbuff_myShip_wing, bbuff_myShip_rudder;
  private float myShip_wing_coords[];
  public static final float ship_wing_coords[] = { -0.075f, 0.0f, 0.1f,    0.075f, 0.0f, 0.1f,    0.0f, 0.18f, 0.1f };
  public static final float ship_rudder_coords[] = { -0.005f, 0.0f, 0.1f,  0.0f, 0.0f, 0.15f,     0.0f, 0.15f, 0.1f };
  public static final float myShip_wing_offset[] = { 0f, -0.95f, 0f };
  public static final float myShip_rudder_offset[] = myShip_wing_offset;
  private static float myShip_wing_coords_base[];
  private static float myShip_rudder_coords_base[];
  
  
  private final String vertexShaderCode = 
      // This matrix member variable provides a hook to manipulate
      // the coordinates of the objects that use this vertex shader
      "uniform mat4 uMVPMatrix;   \n" +
      
      "attribute vec4 vPosition;  \n" +
      "void main(){               \n" +
      
      // the matrix must be included as a modifier of gl_Position
      " gl_Position = uMVPMatrix * vPosition; \n" +
      
      "}  \n";
  
  private final String fragmentShaderCode = 
      "precision mediump float;  \n" +
      "void main(){              \n" +
      " gl_FragColor = vec4 (0.63671875, 0.76953125, 0.22265625, 1.0); \n" +
      "}                         \n";
  
  private int mProgram;
  private int maPositionHandle;
  
  private int muMVPMatrixHandle;
  private float[] mMVPMatrix = new float[16];
  private float[] mMMatrix = new float[16];
  private float[] mVMatrix = new float[16];
  private float[] mProjMatrix = new float[16];
  public float mAngle;
  
  static 
  {
    try{
      myShip_wing_coords_base = addVec2Vectors(myShip_wing_offset, ship_wing_coords);
      myShip_rudder_coords_base = addVec2Vectors(myShip_rudder_offset, ship_rudder_coords);
      
    
    } catch( Exception e ) {};
  }
  
  public PlaygroundRenderer(Context context, Player myShip, Player opponentShip)
  {
    this.ctx = context;
    this.myShip = myShip;
    this.opponentShip = opponentShip;
    
    fetchScreenDimensions();
  }
  
  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    
    // Set the background frame color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    
    // initialize the triangle vertex array
    initShapes();
    
    int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
    
    mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
    GLES20.glLinkProgram(mProgram);                  // creates OpenGL program executables
    
    // get handle to the vertex shader's vPosition member
    maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    
  }
  
  public void onDrawFrame(GL10 unused) {
      //updateShapes(unused);
      // Redraw background color
      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
      
      // Add program to OpenGL environment
      GLES20.glUseProgram(mProgram);
      
      // Prepare the triangle data
      GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 12, bbuff_myShip_wing);
      GLES20.glEnableVertexAttribArray(maPositionHandle);
      
      // Apply a ModelView Projection transformation
      //Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
      GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
      
   // Create a rotation for the triangle (Boring! Comment this out:)
      // long time = SystemClock.uptimeMillis() % 4000L;
      // float angle = 0.090f * ((int) time);

      // Use the mAngle member as the rotation value
      Matrix.setRotateM(mMMatrix, 0, mAngle, 0, 0, 1.0f);
      
      Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
      Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
      
      // Apply a ModelView Projection transformation
      GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
     
      // Draw the triangle
      GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
      
  }
  

  
  public void onSurfaceChanged(GL10 unused, int width, int height) {
    GLES20.glViewport(0, 0, width, height);
    
    float ratio = (float) width / height;
    
    // this projection matrix is applied to object coodinates
    // in the onDrawFrame() method
    Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
  }


  private void initShapes()
  {
    // initialize vertex Buffer for wing  
    bbuff_myShip_wing = ByteBuffer.allocateDirect(myShip_wing_coords_base.length * 4); 
    bbuff_myShip_wing.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
    fbuff_myShip_wing = bbuff_myShip_wing.asFloatBuffer();  // create a floating point buffer from the ByteBuffer

    // initialize vertex Buffer for rudder  
    bbuff_myShip_rudder = ByteBuffer.allocateDirect(myShip_rudder_coords_base.length * 4); 
    bbuff_myShip_rudder.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
    fbuff_myShip_rudder = bbuff_myShip_rudder.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
    
    updateShapes();

  }
  
  private void updateShapes()
  {
    try{ 
      float ttt[] = addXYZ2Vectors( (float) myShip.getPosition(), 0f, 0f, myShip_wing_coords_base);
      fbuff_myShip_wing.position(0);
      fbuff_myShip_wing.put( ttt ); // add the coordinates to the FloatBuffer
    } catch (Exception e) { };
    fbuff_myShip_wing.position(0);                  // set the buffer to read the first coordinate
    
    try{ 
      float ttt[] = addXYZ2Vectors( (float) myShip.getPosition(), 0f, 0f, myShip_rudder_coords_base);
      fbuff_myShip_rudder.position(0);
      fbuff_myShip_rudder.put( ttt ); // add the coordinates to the FloatBuffer
    } catch (Exception e) { };
    fbuff_myShip_rudder.position(0);                  // set the buffer to read the first coordinate
     
    //paintText(unused, "Hellou");
  }
  
  private void fetchScreenDimensions()
  {
    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    screenWidth = display.getWidth();
    screenHeight = display.getHeight();
  }
  
  private int loadShader(int type, String shaderCode){
    
    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    int shader = GLES20.glCreateShader(type); 
    
    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);
    
    return shader;
  }
 
}*/
