package com.example.headsetrecorder;

import java.io.File;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends Activity
{
   private MediaRecorder _recorder;
   private AudioRecord audioRecord;
   private AudioManager _audioManager;
   private TextView _text1, _text2;
   private BroadcastReceiver receiver;
   int frequency = 8000;
   int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
   int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
 
      _text1 = (TextView) findViewById(R.id.textView1);
      _text2 = (TextView) findViewById(R.id.textView2);
 
      Button btn1 = (Button) findViewById(R.id.button1);
      btn1.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            start();
         }
      });

      Button btn2 = (Button) findViewById(R.id.button2);
      btn2.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
        	 stop();
         }
      }); 
    	  
   }    

 
   @Override
   protected void onDestroy()
   {
      stop();
      super.onDestroy();
      unregisterReceiver(receiver);
   }
 
   private void start()
   {
	   _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);    
	   registerReceiver(new BroadcastReceiver() {

	          @Override
	          public void onReceive(Context context, Intent intent) {
	              int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
	              //Log.w('D', "Audio SCO state: " + state);

	              if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) { 
	            	  Log.d("D", "Audio SCO state: "+state);
	                  /* 
	                   * Now the connection has been established to the bluetooth device. 
	                   * Record audio or whatever (on another thread).With AudioRecord you can record with an object created like this:
	                   * new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                   * AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
	                   *
	                   * After finishing, don't forget to unregister this receiver and
	                   * to stop the bluetooth connection with am.stopBluetoothSco();
	                   */
                       startRecord();
                       unregisterReceiver(this);
	              }

	          }
	      }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));

	      Log.d("D", "starting bluetooth");
	      _audioManager.startBluetoothSco();

   }
   public void startRecord()
   {
 	  File path = new File(Environment.getExternalStorageDirectory() + "/VoiceRecord");
      if (!path.exists())
         path.mkdirs();
 
      Log.w("BluetoothReceiver.java | startRecord", "|" + path.toString() + "|");
 
      File file = null;
      try
      {
         file = File.createTempFile("voice_", ".m4a", path);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      Log.w("BluetoothReceiver.java | startRecord", "|" + file.toString() + "|");
      //_text1.setText(file.toString());
 
      try
      {
         //_audioManager.startBluetoothSco();
         _recorder = new MediaRecorder();
         _recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
         _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
         _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
         _recorder.setOutputFile(file.toString());
         _recorder.prepare();
         _recorder.start();
 
         _text2.setText("recording");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   private void stop()
   {
      try
      {
         _recorder.stop();
         _recorder.release();
         _audioManager.stopBluetoothSco();
         _text2.setText("stop");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
