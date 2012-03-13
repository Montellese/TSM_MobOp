package mse.tsm.mobop.starshooter.game;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL10;

import mse.tsm.mobop.starshooter.game.screens.GameLoop;
import mse.tsm.mobop.starshooter.game.screens.GameOverScreen;
import mse.tsm.mobop.starshooter.game.screens.GameScreen;
import mse.tsm.mobop.starshooter.game.screens.StartScreen;
import mse.tsm.mobop.starshooter.game.simulation.Simulation;
import mse.tsm.mobop.starshooter.game.tools.GameActivity;
import mse.tsm.mobop.starshooter.game.tools.GameListener;

public class Playground extends GameActivity implements GameListener 
{
	private GameScreen screen = null;
	private Simulation simulation = null;
	
	private long startTime = System.nanoTime();
	private int frameCount = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
  	{
		this.setRequestedOrientation(0);
		// go fullscreen
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                              WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  
		super.onCreate(savedInstanceState);
	  
		this.setGameListener(this);
    
		if (savedInstanceState != null && savedInstanceState.containsKey("simulation"))
			simulation = (Simulation)savedInstanceState.getSerializable("simulation");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (screen instanceof GameLoop)
			outState.putSerializable("simulation", ((GameLoop)screen).simulation);
	}
    
    @Override
    protected void onPause()
    {
        super.onPause();
        if (screen != null)
        	screen.dispose();
        if (screen instanceof GameLoop)
        	simulation = ((GameLoop)screen).simulation;
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
    }

	public void setup(GameActivity activity, GL10 gl) 
	{
		if (simulation != null)
		{
			screen = new GameLoop(gl, activity, simulation);
			simulation = null;
		}
		else
		{
			screen = new StartScreen(gl, activity);
		}
	}

	public void mainLoopIteration(GameActivity activity, GL10 gl) 
	{
		screen.update(activity);
		screen.render(gl, activity);
		
		if (screen.isDone())
		{
			screen.dispose();
			
			if (screen instanceof StartScreen)
				screen = new GameLoop(gl, activity);
			else if (screen instanceof GameLoop)
				screen = new GameOverScreen(gl, activity, ((GameLoop)screen).simulation.score);
			else if (screen instanceof GameOverScreen)
				screen = new StartScreen(gl, activity);
		}
		
		frameCount++;
		if (System.nanoTime() - startTime > 1000000000)
		{
			frameCount = 0;
			startTime = System.nanoTime();
		}
	}
    
    /*class PlaygroundSurfaceView extends GLSurfaceView implements SensorEventListener {
    	
    	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    	private PlaygroundRenderer mRenderer;
    	private float mPreviousX;
    	private float mPreviousY;
      
    	private Sensor mAccelerometer;

        private float mXDpi;
        private float mYDpi;
        private float mMetersToPixelsX;
        private float mMetersToPixelsY;

        private float mSensorX;
        private float mSensorY;
        private long mSensorTimeStamp;
        private long mCpuTimeStamp;
        
        private int counterToClampTextRefresh = 0;
      
        public PlaygroundSurfaceView(Context context, Player myShip, Player opponentShip){
          super(context);
          
          // Create an OpenGL ES 2.0 context.
          setEGLContextClientVersion(2);
          
          mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

          DisplayMetrics metrics = new DisplayMetrics();
          getWindowManager().getDefaultDisplay().getMetrics(metrics);
          mXDpi = metrics.xdpi;
          mYDpi = metrics.ydpi;
          mMetersToPixelsX = mXDpi / 0.0254f;
          mMetersToPixelsY = mYDpi / 0.0254f;
              
          // set the mRenderer member
          //mRenderer = new PlaygroundRenderer(context, myShip, opponentShip);
          setRenderer(mRenderer);
          
          // Render the view only when there is a change
          setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY /*RENDERMODE_WHEN_DIRTY*//*);
          
          // Register this implementation as a SensorEventListener
          mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        
        protected void finalize() throws Throwable
        {
        	mSensorManager.unregisterListener(this);
        	
        	super.finalize(); //not necessary if extending Object.
        } 
        
        @Override 
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = e.getX();
            float y = e.getY();
            
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
        
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
        
                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                      dx = dx * -1 ;
                    }
        
                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                      dy = dy * -1 ;
                    }
                  
                    //mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }

    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// Nothing to do
    	}

    	public void onSensorChanged(SensorEvent event) {
    		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            /*
             * record the accelerometer data, the event's timestamp as well as
             * the current time. The latter is needed so we can calculate the
             * "present" time during rendering. In this application, we need to
             * take into account how the screen is rotated with respect to the
             * sensors (which always return data in a coordinate space aligned
             * to with the screen in its native orientation).
             *//*

            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }

            mSensorTimeStamp = event.timestamp;
            mCpuTimeStamp = System.nanoTime();
            if( counterToClampTextRefresh++ >= 10 )
            {
              counterToClampTextRefresh=0;
              StringBuffer sb = new StringBuffer("SensorX: ");
              sb.append(mSensorX).append("\nSensorY: ").append(mSensorY);
              Toast.makeText(getApplicationContext(),sb, Toast.LENGTH_SHORT).show();
            }
    	}
    }*/
}
