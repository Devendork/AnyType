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

/***
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.artfordorks.anytype.R.id;

/**
 * This class manages the screen just after an image is captured and lets the user make edits to it
 * @author lauradevendorf
 *
 */
public class ViewCaptureActivity extends Activity implements OnTouchListener{
	

	protected static final String TAG = "ViewCaptureActivity";
	private DrawShapeOnTop shapeView;
	private boolean started_saving = false;
	private double beginTime = System.currentTimeMillis();
	private boolean dragging = false;
	private Point last;
	private ScaleGestureDetector sd;

	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	double scale = Globals.getScale(detector.getCurrentSpan());
	    	shapeView.updateScale((float)scale);
	    	shapeView.updateImageOffset(detector.getFocusX(), detector.getFocusY());
	    	shapeView.invalidate();
	        return true;
	    }
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sd = new ScaleGestureDetector(ViewCaptureActivity.this, new ScaleListener());

		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewcapture);
		
		// add accept button listener
		Button acceptButton = (Button) findViewById(id.button_accept);
		acceptButton.setBackgroundColor(Color.rgb(255, 33, 177));
		acceptButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("Tap", "Event Source "+event.getSource());
				
				v.setBackgroundColor(Color.CYAN);
				if(!started_saving) return false;
				else return true;
				
			}
		});
		
		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			   int stage = Globals.stage;
			   saveImages(stage, shapeView.getShapeImageOut());      //this needs to execute before 
			   BuildLettersThread bl_thread = new BuildLettersThread();
			   bl_thread.execute(stage);
			   
			   //Globals.buildLetters(stage); 
			   Globals.nextStage();
				    
			    if(Globals.edit) nextEditScreen(true);
			    else nextScreen();
					
				started_saving = true;
			}
		});

		// add accept button listener
		Button rejectButton = (Button) findViewById(id.button_reject);
		rejectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				writeToLog("_reject");	
				   if(Globals.edit) nextEditScreen(false);
				   else nextScreen();
			}
		});
		
		Button playback = (Button) findViewById(id.button_playback);
		if(Globals.using_video) playback.setVisibility(View.VISIBLE);
		else playback.setVisibility(View.INVISIBLE);
		playback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				writeToLog("_view");
				openVideoPlayer(); 
			}
		});
		

		// Create our Preview view and set it as the content of our activity.
		shapeView = new DrawShapeOnTop(this, Globals.getStageShape(), true);
		shapeView.setOnTouchListener(this);
		FrameLayout preview = (FrameLayout) findViewById(id.camera_preview);
		preview.addView(shapeView, new LayoutParams(Globals.preview_size.x,Globals.preview_size.y));
		//preview.addView(shapeView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		

	}
	
	public void openVideoPlayer(){
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		startActivity(intent);
	}
	
	public void nextEditScreen(boolean accepted){
		Intent intent;
		shapeView.recycleImages();
		
		if(Globals.stage <= 4){
			
			if(accepted){
				File pfile;
				pfile = new File(Globals.getStagePhotoPath(Globals.stage));
				if(!pfile.exists()){
					if(Globals.using_video) intent = new Intent(this, VideoCaptureActivity.class);
					else intent = new Intent(this, PhotoCaptureActivity.class);
				}
				else intent = new Intent(this, ViewCaptureActivity.class);
				
			}else{
				if(Globals.using_video) intent = new Intent(this, VideoCaptureActivity.class);
				else intent = new Intent(this, PhotoCaptureActivity.class);
			}
			startActivity(intent);
			
		}else{
		    intent = new Intent(this, CanvasActivity.class);
			startActivity(intent);
		}
	}

	public void nextScreen(){
		shapeView.recycleImages();

		if(Globals.stage == 5){
			Intent intent;
		    intent = new Intent(this, CanvasActivity.class);
			startActivity(intent);
		}else finish();

	}
	
	public void writeToLog(String to){
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, getLocalClassName(), to, time);
	}

	
	
	// Implement the OnTouchListener callback
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("Touch", "Action: " + event.getAction());
		Log.d("Touch", "Action Index: " + event.getActionIndex());

		DrawShapeOnTop dv = (DrawShapeOnTop) v;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			
			dv.startPath(event.getX(), event.getY());
			writeToLog("_DrawPath");

			if(!dv.isValidPath()){
				dragging = true;
				last = new Point();
				last.set((int)event.getX(), (int)event.getY());
			}
			
			// finger up - nothing selected
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if(dragging){
				dragging = false;
			}else{
			 dv.endPath(event.getX(), event.getY());
			}

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if(dragging){
				float x_off = last.x - event.getX(0);
				float y_off = last.y - event.getY(0);
				dv.updateImageOffset(x_off, y_off);
				last.set((int)event.getX(), (int)event.getY());
				
			}else{
			   dv.addPathPoint(event.getX(), event.getY());
			}
		}
		
		
		dv.invalidate();
		return true;

	}
	
	public void saveImages(int stage, Bitmap bmap){
		Log.d("Thead", "Enter Save Images "+stage+" && bitmap "+bmap);

		
		File pictureFile = Globals.getOutputMediaFile(Globals.MEDIA_TYPE_IMAGE, "IMG_" + Integer.toString(stage) + "_CROP.png");
		if (pictureFile == null) return;


		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			bmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
			fos.close();
			Log.d("Capture Activity", "File Created");
		
		} catch (FileNotFoundException e) {
			Log.d("Thead", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("Thead", "Error accessing file: " + e.getMessage());
		}
		
		Log.d("Thead", "File Exists "+pictureFile.exists()+ "Path:"+pictureFile.getPath());
		Log.d("Thead", "Exit Save Images ");
		
		bmap.recycle();
				
	}
	


	

}





