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


/**
 * This class starts all of the global variables.  It launches the application and moves on
 */

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.artfordorks.anytype.R.id;

/**
 * This is the screen where it shows each letter and all the shapes that composed the letter. Clicking 
 * a shape reveals the photo the shape came from or the video if using video mode
 * @author lauradevendorf
 *
 */
public class LetterPartViewerActivity extends Activity {

	protected static final String TAG = null;
	private LetterPartView letter_part_view;
	private int[] colors;
	private ImageView mPhoto;
	private VideoView mVideo;
	private LetterPartView cur;
	private LetterPartView[] parts;
	private double beginTime = System.currentTimeMillis();



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(Globals.using_video) setContentView(R.layout.lettervideo);
		else setContentView(R.layout.letterphoto);
		
		FrameLayout letter_frame = (FrameLayout) findViewById(R.id.letter_frame);
		
		if(Globals.using_video) mVideo = (VideoView) findViewById(R.id.video_viewer);
		else  mPhoto = (ImageView) findViewById(R.id.image_viewer);
		
		int i = Globals.force_letter;
//		//For each shape in the letter get this stuff
		int[] shape_ids = Globals.letters[i].getShapeIds();
		int[] x_points = Globals.letters[i].getXPoints();
		int[] y_points = Globals.letters[i].getYPoints();
		float[] rots =  Globals.letters[i].getRotations();
		
		parts = new LetterPartView[shape_ids.length];

		
		for(int j = 0; j < shape_ids.length; j++){
			letter_part_view = new LetterPartView(this, shape_ids[j], x_points[j], y_points[j], rots[j], j);
			letter_part_view.setId(j);
			parts[j] = letter_part_view;
			letter_frame.addView(letter_part_view, new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			letter_part_view.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent e) {
					LetterPartView lpv = (LetterPartView) v;
					
					if(lpv.hasTouch(e.getX(), e.getY())){
						cur = lpv;
						
						for(int i = 0; i < parts.length; i++){
							if(parts[i] == lpv) parts[i].setViewHasSelection(true);
							else parts[i].setViewHasSelection(false);
						}
						
						if(Globals.using_video) launchVideo(lpv.getShapeId());
						else launchPhoto(lpv.getShapeId());
						return true;
					}
					return false;
				}
				
			});
		}
		
		Button back_button = (Button) findViewById(id.button_back);
		back_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Globals.playback_mode = false;
				writeLog();
				finish();
			}
		});
		
		if(Globals.using_video){
		    MediaController controller = new MediaController(this);
		    mVideo.setMediaController(controller);
		    
		    //force this window to close when the video is through playing
		    mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            @Override
	            public void onCompletion(MediaPlayer mp) {
	            	
	            	cur.deSelect();
	            	
					for(int i = 0; i < parts.length; i++){
						parts[i].setViewHasSelection(false);
					}
	            	
	            }
	        });
			}
		
		
	    
	}
	
	public void writeLog(){
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, super.getLocalClassName(), "CanvasActivity - "+Globals.intToChar(Globals.force_letter), time);
	}
	
	//launch the video player when clicked
	public void launchPhoto(int stage){
		
		Bitmap bmap = Globals.decodeSampledBitmapFromResource(new File(Globals.getStagePhotoPath(stage)), mPhoto.getWidth(), mPhoto.getHeight());
	    mPhoto.setImageBitmap(bmap);
	    double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "_PlayLetterPhoto"+Globals.force_letter, time);

	}
	
	//launch the video player when clicked
	public void launchVideo(int stage){
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "_PlayLetterVideo"+Globals.force_letter, time);


		Globals.playback_mode = true;
		Globals.force_stage = stage;
		mVideo.setVideoPath(Globals.getStageVideoPath());
		mVideo.start();

	}

	
	@Override
	protected void onPause() {
		super.onPause();
	}



}
