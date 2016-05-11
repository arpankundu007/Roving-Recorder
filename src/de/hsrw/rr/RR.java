package de.hsrw.rr;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RR extends Activity {

	private static final String TAG = RR.class.getSimpleName();
	 private MediaRecorder recorder = new MediaRecorder();

	// Enumeration for the automaton state
	private static enum MODE {
		WAITING, RECORDING, PLAYING
	};
	int status=0;
	private MODE mode;

	// All the HMI elements of the app
	private RRListAdapter listAdapter;
	private Button b;

	// File containing the current recording
	private File currentFile;
	File f;

	// Media objects
	private MediaPlayer player;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Initialize listView
		final ListView lv = (ListView) findViewById(R.id.listview);
		listAdapter = new RRListAdapter(this);
		lv.setAdapter(listAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			String sp;
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String s = listAdapter.getItem(position).getAbsolutePath().toString();
				if(player != null){
					releasePlayer();
					status=0;
					if(!sp.equals(s)){
						sp=s;
						playAudioFile(s);
					}
					
				}
				else
				{
					sp=s;
					playAudioFile(s);
				}
			}
		});
		b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			
			int state = 0;
			@Override
			public void onClick(View v) {
					state++;
					updateButtonText(state);
					if(state==1){
						
						
						recordToFile();
						Toast.makeText(getBaseContext(), "Recording Started!", Toast.LENGTH_SHORT).show();
					}
					if(state==2)
					{
						state=0;
						releaseRecorder();
						Toast.makeText(getBaseContext(), "Recording Stopped!", Toast.LENGTH_SHORT).show();
					}
			}
		});
		

		currentFile = null;
		mode = MODE.WAITING;
		player = null;
		recorder = null;
		
	}

	@Override
	protected void onPause() {
		// Release resources
		super.onPause();
		releasePlayer();
		releaseRecorder();
	}

	private void updateButtonText(int state) {
		/**
		 * TODO: Change the button text depending
		 * on the state of the automaton.
		 */
		if(state==1)
			b.setText("Stop Recording");
		if(state==2)
			b.setText("Record");
	}

	private void recordToFile() {
		
		if(recorder == null){
			recorder = new MediaRecorder();
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		String time = Long.toString(System
				.currentTimeMillis());
		f = new RRFile(getBaseDir(),time  + RRFile.EXT_3GP);
		if(f.exists())
			f.delete();
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
			recorder.setOutputFile(f.getAbsolutePath());
			try
			{
				recorder.prepare();
				recorder.start();
				//return f;
			}
			catch (IOException e)
			{
				Log.e(TAG, "could not start recording", e);
			}
		//return null;
		
	}
	

	private void releaseRecorder() {
		if (recorder != null) {
			// Release recorder resources 
			// should be called whenever recorder stops.
			recorder.stop();
			recorder.release();
			listAdapter.refresh(f);
			recorder = null;
		}
	}
	
	private void playAudioFile(String filename) {
		if(player == null) 
			player = new MediaPlayer();
		player = new MediaPlayer();
		player.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer player) {
				status = 1;
			}
		});
		try {
			/**
			 * TODO: 
			 * 1. play the file (see MediaPlayer API)
			 * 2. update the user interface and states
			 */
			player.setDataSource(filename);
			player.prepare();
			player.start();
			
			}
			catch (Throwable thr)
			{
				Log.e(TAG, "could not play audio", thr);
			}
	}

	private void releasePlayer() {
		if (player != null) {
			// Release player resources
			// should be called whenever player stops.
			player.stop();
			player.release();
			player = null;
		}
	}

	public static File getBaseDir() {
		File dir = new File(Environment.getExternalStorageDirectory(), "RR");
		
		// if required: create directories
		dir.mkdirs();
		return dir;
	}
}
