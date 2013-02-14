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
 * This class draws a shape to the screen and allows the user 
 * to capture that shape
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.artfordorks.anytype.R.id;


/***
 * This activity is in charge of screen for editing individual letters
 * @author lauradevendorf
 *
 */
public class LetterEditActivity extends Activity{

	private GridView shape_grid;
	private LetterEditView le_view;
	private boolean two_finger = false;
	private int savedNum = 0;
	private GestureDetector gd;
	private ScaleGestureDetector sd;
	
	private double beginTime = System.currentTimeMillis();


	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	double scale = Globals.getScale(detector.getCurrentSpan());
	    	le_view.updateScale(scale);
			le_view.updatePosition(detector.getFocusX(), detector.getFocusY());
	        le_view.invalidate();
	        return true;
	    }
	}
	
	private class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_MAX_OFF_PATH = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 1000;
	    
	    
	    public void onShowPress(MotionEvent e){
	    	//set current id
	    	int selected = le_view.locate((int) e.getX(), (int) e.getY());
			if (selected != le_view.getCur() && le_view.getCur() != -1)
				le_view.deselect(le_view.getCur());
			if(selected != -1) le_view.select(selected);
			
	    }
		

	    
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	        try {
	            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	                return false;
	            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY || 
	            		(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
	            	le_view.removeCurrentLetter();
	        		le_view.invalidate();


	            }
	        } catch (Exception e) {
	            // nothing
	        }
	        return false;
	    }
	    
	}



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("LetterEdit", "On Create");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editletter);
		
		gd = new GestureDetector(LetterEditActivity.this, new GestureListener(), new Handler());
		sd = new ScaleGestureDetector(LetterEditActivity.this, new ScaleListener());
		
		shape_grid = (GridView) findViewById(R.id.shape_grid);
		shape_grid.setAdapter(new ShapeAdapter(this));

		le_view = new LetterEditView(this);
		
		FrameLayout canvas = (FrameLayout) findViewById(R.id.letter_edit_view);
		canvas.addView(le_view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		
		Log.d("LetterEdit", "Num Shapes Loaded1: "+le_view.getShapesSize());
		
		le_view.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(final View view, final MotionEvent event) {

				
				boolean action = gd.onTouchEvent(event);
				boolean scaled = sd.onTouchEvent(event);
				
				if(!action ){
            	LetterEditView lv = (LetterEditView) view;
    	    	
            	int selected = lv.getCur();
            	if(selected == -1) selected = lv.locate((int) event.getX(), (int) event.getY());
    	    	if(selected == -1) return true;
    	    	lv.select(selected);
    	    
    	    	          
	            if (event.getActionIndex() > 0) {
	    			two_finger = !two_finger;
	            }
	            
	            //check for move and stretches
	            if (event.getAction() == MotionEvent.ACTION_MOVE) {
	                    
	    			if(event.findPointerIndex(0) != -1 && event.findPointerIndex(1) != -1){
	    				float r = (float)Globals.getRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
	    				lv.incRotations(r);
	    				lv.updatePosition((event.getX(0)+event.getX(1))/2, (event.getY(0)+event.getY(1))/2);
	    				
	    			}else{
	    				lv.updatePosition(event.getX(), event.getY());
	    			}
	    
	            }else if(event.getAction() == MotionEvent.ACTION_UP){
	            	lv.deselect(selected);
	            	Globals.clearFingerData();
	            }
	            lv.invalidate();
				}
	            return true;
	        }
	     });
		
		
//		if(Globals.saved_lev != null){
//			le_view.loadState(Globals.saved_lev);
//			Globals.saved_lev = null;
//		}
		
		Log.d("LetterEdit", "Num Shapes Loaded2: "+le_view.getShapesSize());

		Button saveButton = (Button) findViewById(id.save_letter);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveLetter();
			}
		});
		
		
		Button back = (Button) findViewById(id.button_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				le_view.clearData();
				Globals.saved_lev = null;
				finish();
			}
		});
		
		
		Log.d("LetterEdit", "Num Shapes Loaded3: "+le_view.getShapesSize());
		Log.d("LetterEdit", "Num Shape Images Loaded: "+le_view.getShapeImagesSize());
		le_view.invalidate();	

	

	}
	

	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  // TODO Auto-generated method stub
	  super.onWindowFocusChanged(hasFocus);
	    Log.d("WH", "leview "+le_view.getWidth());
		le_view.setWidthandHeight(le_view.getWidth(), le_view.getHeight());
		le_view.initLetterShapes();
	 }
	
	public void writeToLog(String to){
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, getLocalClassName(), to, time);
	}
	


	@Override
	protected void onPause() {
		Log.d("Delete Mode", "On Pause Called");
		saveLetterState();
		super.onPause();
	}

	void saveLetterState(){
		Globals.saved_lev = le_view;
	}
	

	private File saveLetter(){
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "_SaveLetter "+Globals.intToChar(Globals.force_letter), time);

		//update the path information 
		Globals.letters[Globals.force_letter].updateLetterFromInstance(le_view.getShapes());

		File pictureFile = Globals.getOutputMediaFile(Globals.MEDIA_TYPE_IMAGE, Globals.intToChar(Globals.force_letter) + ".png");
		
		if (pictureFile == null) {
			return null;
		}


		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			Bitmap bmap = le_view.getImageOut();
			bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			

		} catch (FileNotFoundException e) {
			Log.d("", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("", "Error accessing file: " + e.getMessage());
		}
		
		
	    Toast.makeText(this, "Letter Saved", Toast.LENGTH_SHORT).show();

		return pictureFile;
		
	}
	
	public void reset(){
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "LoadActivity", time);
		
		
		Intent intent = new Intent(this, LoadActivity.class);
		startActivity(intent);
	}
	


	public void addToCanvas(ImageView v, MotionEvent e) {
		float x = (le_view.getWidth()/26*v.getId())+e.getX();
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "_AddLetter - "+Globals.intToChar(v.getId()), time);
		
		le_view.addLetter(v.getId());
		le_view.invalidate();
		le_view.updatePosition(x, 61f);	
		
    
	}

}


