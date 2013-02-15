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



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.artfordorks.data.*;
import com.artfordorks.anytype.R.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.ViewGroup.LayoutParams;



/**
 * This class is in charge of handling the screen where the user captures and image
 * @author lauradevendorf
 *
 */
public class PhotoCaptureActivity extends Activity{

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// Get current size of heap in bytes
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will increase
		// after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		Log.d("Memory", "view capture "+heapSize +" "+heapMaxSize+" "+(float)heapSize/(float)heapMaxSize*100+" "+(float)heapFreeSize/(float)heapMaxSize*100);
		

		
	    capturing = false;
			
			
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cameracapture);
		
		

	}
			

	@Override
	public void onRestart() {
		Log.d("Memory", "onRestart");
		super.onRestart();
	}
	
	@Override
	public void onResume() {
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

		captureButton = (Button) findViewById(id.button_capture);
		captureButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Log.d("Tap", "Event Source "+event.getSource());				
				if(!capturing) return false;
				capturing = true;
				return true;
			}
			

		});
		
		captureButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Log.d("Tap", "Capture!! "+capturing);
				
			    mCamera.takePicture(null, null, mPicture);
				view.setBackgroundColor(Color.CYAN);
			}
		});

				
		
		captureButton = (Button) findViewById(id.button_capture);
		captureButton.setBackgroundColor(Color.rgb(255, 33, 177));
		
		super.onResume();
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
			

			nextScreen();
		}

	};
	
	

}
