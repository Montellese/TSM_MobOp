package mse.tsm.mobop.starshooter;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.text.style.SuperscriptSpan;


public class GSensor_Player extends Player
{
  private SensorManager mSensorManager;
  private Sensor mSensor;
  private static String ClassName = "Sensor-Player";
  
  static
  {
    registerClass(ClassName);
  }
}
