package mse.tsm.mobop.starshooter;

import android.hardware.Sensor;
import android.hardware.SensorManager;


public class GSensorPlayer extends Player
{
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private static String ClassName = "Sensor-Player";
	
	static
	{
		registerClass(ClassName);
	}
  
  	public void Draw()
  	{
  		// TODO: GSensorPlayer.Draw()
  	}
}
