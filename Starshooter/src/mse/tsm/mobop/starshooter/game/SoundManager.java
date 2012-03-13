package mse.tsm.mobop.starshooter.game;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import mse.tsm.mobop.starshooter.game.tools.GameActivity;

public class SoundManager
{
  SoundPool soundPool;
  AudioManager audioManager;
  MediaPlayer mediaPlayer;
  int shotId;
  int explosionId;

  public SoundManager( GameActivity activity )
  {
  
  soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0);
  
  audioManager = (AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
  
  activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

  
  try
  
  {
  
  
  AssetFileDescriptor descriptor = activity.getAssets().openFd( "shot.ogg" );
  
  
  shotId = soundPool.load( descriptor, 1 );
  
  
  descriptor = activity.getAssets().openFd( "explosion.ogg" );
  
  
  explosionId = soundPool.load( descriptor, 1 );
  
  }
  
  catch( Exception ex )
  
  {
  
  
  Log.d( "Sound Sample", "couldn't load sound 'shot.ogg'" );
  
  
  throw new RuntimeException( ex );
  
  }

  
  mediaPlayer = new MediaPlayer();
  
  try
  
  {
  
  
  AssetFileDescriptor descriptor = activity.getAssets().openFd( "8.12.mp3" );
  
  
  mediaPlayer.setDataSource( descriptor.getFileDescriptor() );
  
  
  mediaPlayer.prepare();
  
  
  mediaPlayer.setLooping(true);
  
  
  mediaPlayer.start();
  
  }
  
  catch( Exception ex )
  
  {
  
  
  ex.printStackTrace();
  
  
  Log.d( "Sound Sample", "couldn't load music 'music.mp3'" );
  
  
  throw new RuntimeException( ex );
  
  }
  }

  public void playShotSound( )
  {
  
  int volume = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
  
  soundPool.play(shotId, volume, volume, 1, 0, 1);
  }

  public void playExplosionSound( )
  {
  
  int volume = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
  
  soundPool.play(explosionId, volume, volume, 1, 0, 1);
  }

  public void dispose( )
  {
  
  soundPool.release();
  
  mediaPlayer.stop();
  
  mediaPlayer.release();
  }
}