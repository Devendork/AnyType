/**************************************************************                                                                       
AnyType                                    
Copyright (C) 2012-2013 by Laura Devendorf     
www.ischool.berkeley.edu/~ldevendorf/anytype                  
---------------------------------------------------------------             
                                                                           
This file is part of AnyType.

AnyType is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

AnyType is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with AnyTypePhoto. If not, see <http://www.gnu.org/licenses/>.

*****************************************************************/

package com.artfordorks.anytype;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.artfordorks.anytype.R.id;



/**
 * This class is in charge of handling the screen where the user captures a video
 * @author lauradevendorf
 *
 */
public class VideoCaptureActivity extends Activity{

	protected static final String TAG = null;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Camera mCamera;
	private CameraPreview mPreview;
	private static DrawShapeOnTop shapeView;
	private FrameLayout preview;
	private boolean capturing;
	private double beginTime = System.currentTimeMillis();
	private Button captureButton;
	private Timer timer;
	private TimerTask timer_task;
	private int interval = 1;
	private ProgressBar mProgress;
	private int ticks = 0;
	private boolean started_recording = false;
	private boolean finished_recording = false;
	private MediaRecorder mMediaRecorder;
	private boolean isRecording = false;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    capturing = false;
			
			
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.videocameracapture);
		
		

	}
			

	@Override
	public void onRestart() {
		Log.d("Memory", "onRestart");
		super.onRestart();
	}
	
	@Override
	public void onResume() {
		
		
		started_recording = false;
		finished_recording = false;
		
		Log.d("Memory", "onResume");
		
		
		mPreview = new CameraPreview(this);

		boolean success = safeCameraOpen(0);
		Log.d("Memory", "Camera Open Success "+success);
		
		mPreview.setCamera(mCamera);
		
		shapeView = new DrawShapeOnTop(this, Globals.getStageShape(), false);

		preview = (FrameLayout) findViewById(id.camera_preview);	
		
		preview.addView(mPreview, new LayoutParams(Globals.preview_size.x,Globals.preview_size.y));
		preview.addView(shapeView, new LayoutParams(Globals.preview_size.x,Globals.preview_size.y));
		

		SeekBar seek = (SeekBar) findViewById(id.seek);
		seek.setProgress(0);
		seek.setVisibility(View.INVISIBLE);
		
		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(started_recording && !finished_recording){
					if(timer != null) timer.cancel();
					endRecording();
					finished_recording = true;
				}else if(!started_recording){
					 mCamera.takePicture(null, null, mPicture);
					started_recording = true;
				}
			}
			
		});
		
		mProgress  = (ProgressBar) findViewById(id.progress_bar);
		mProgress.setMax(Globals.max_video_time);
		mProgress.setProgress(ticks);
		mProgress.setVisibility(View.INVISIBLE);
		
		// Spawn a thread to build the letters
		// Start lengthy operation in a background thread
		timer_task = new TimerTask() {
			Handler handler = new Handler();
			public void run() {
				handler.post(new Runnable(){
					public void run(){
						mProgress.setProgress(++ticks);
						if(ticks == Globals.max_video_time) endRecording();
					}
				});				
			}
		};


		


			
		
		super.onResume();
	}
	
	private void startTimer() {
		mProgress.setProgress(ticks);
		mProgress.setVisibility(View.VISIBLE);
		timer = new Timer();
		timer.scheduleAtFixedRate(timer_task, 0, interval);
	}
	
	
	private void initRecording() {

		// initialize video camera
		if (prepareVideoRecorder()) {

			// Camera is available and unlocked, MediaRecorder is prepared,
			// now you can start recording
			mMediaRecorder.start();

			// inform the user that recording has started
			setCaptureButtonText("Stop");
			isRecording = true;

			startTimer();
		} else {
			// prepare didn't work, release the camera
			releaseMediaRecorder();
			// inform user
		}
	}
	
	private void setCaptureButtonText(String text) {
		Button captureButton = (Button) findViewById(id.button_capture);
		captureButton.setText(text);

	}

	private boolean prepareVideoRecorder() {

		// mCamera = getCameraInstance(); //already called before this function
		mMediaRecorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mMediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));

		// Step 4: Set output file
		mMediaRecorder.setOutputFile(getOutputMediaFile(
				Globals.MEDIA_TYPE_VIDEO).toString());

		// Step 5: Set the preview output
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// Step 6: Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			Log.d("Video Capture",
					"IllegalStateException preparing MediaRecorder: "
							+ e.getMessage());
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			Log.d("Video Capture",
					"IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder();
			return false;
		}
		return true;
	}
	
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}


	private void endRecording() {
		// stop recording and release camera
		mMediaRecorder.stop(); // stop the recording
		releaseMediaRecorder(); // release the MediaRecorder object
		mCamera.lock(); // take camera access back from MediaRecorder

		// inform the user that recording has stopped
		setCaptureButtonText("Capture");
		isRecording = false;
		nextScreen();
	}

	@Override
	protected void onPause() {
		Log.d("Memory", "onPause");
		releaseCameraAndPreview(); 
		super.onPause();
	}

	@Override
	protected void onStop(){
		Log.d("Memory", "OnStop");
		super.onStop();
	}
	
	@Override 
	protected void onDestroy(){
		Log.d("Memory", "OnDestory");
		releaseCameraAndPreview(); 
		super.onDestroy();
	}
	
	
	
	private boolean safeCameraOpen(int id) {
	    boolean qOpened = false;
	  
	    try {
	        releaseCameraAndPreview();
	        mCamera = Camera.open();
	        qOpened = (mCamera != null);
	    } catch (Exception e) {
	        Log.e(getString(R.string.app_name), "failed to open Camera");
	        e.printStackTrace();
	    }

	    return qOpened;    
	}

	private void releaseCameraAndPreview() {
	    mPreview.setCamera(null);
	    if (mCamera != null) {
	        mCamera.release();
	        mCamera = null;
	    }
	}
	
	

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {

		// Create a media file name
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(Globals.getTestPath() + File.separator
					+ "IMG_" + Integer.toString(Globals.stage) + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(Globals.getTestPath() + File.separator
					+ "VID_" + Integer.toString(Globals.stage) + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	// go to the next screen - or - go to the edit screen and
	// return when finished
	private void nextScreen() {
		Log.d("Capture", "Next Screen ");
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "ViewCaptureActivity", time);
		
		Intent intent = new Intent(this, ViewCaptureActivity.class);
		startActivity(intent);
	}


	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
					        
            
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				Log.d("Capture Activity", "File Created");
				

			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			
			initRecording();
		}

	};
	
	

}
