package mse.tsm.mobop.starshooter;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Playground extends Activity {
  private SensorManager mSensorManager;
  private PowerManager mPowerManager;
  private WindowManager mWindowManager;
  private Display mDisplay;
  private WakeLock mWakeLock;
  
  private Player myShip, opponentShip;
  
  private GLSurfaceView mGLView;
  
  public Playground()
  {
    //Bundle b = getIntent().getExtras();
    //short playerclassids[]= b.getShortArray("playerclassids");
    
    myShip = new GSensorPlayer();
    opponentShip = new TCPPlayer();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    // go fullsreen
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
    // Get an instance of the SensorManager
    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    // Get an instance of the PowerManager
    mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

    // Get an instance of the WindowManager
    mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    mDisplay = mWindowManager.getDefaultDisplay();

    // Create a bright wake lock
    mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
    
    // Create a GLSurfaceView instance and set it
    // as the ContentView for this Activity
    mGLView = new PlaygroundSurfaceView(this,myShip,opponentShip);
    setContentView(mGLView);
  }
    
    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();

        // and release our wake-lock
        mWakeLock.release();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        /*
         * when the activity is resumed, we acquire a wake-lock so that the
         * screen stays on, since the user will likely not be fiddling with the
         * screen or buttons.
         */
        mWakeLock.acquire();
        
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
    
    class PlaygroundSurfaceView extends GLSurfaceView implements SensorEventListener {
    	
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
          setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY /*RENDERMODE_WHEN_DIRTY*/);
          
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
             */

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
    }
}
