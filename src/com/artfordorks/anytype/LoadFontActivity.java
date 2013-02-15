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
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * This manages the screen where all the different font names are displayed and you can select one
 * @author lauradevendorf
 *
 */
public class LoadFontActivity extends Activity {

	
	protected static final String TAG = null;
	private HashMap<Integer, String> files = new HashMap();
	private LinearLayout list;
	private double beginTime = System.currentTimeMillis();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.load);

	    list = (LinearLayout) findViewById(R.id.list);
		File dir = new File(Globals.getBasePath());
        String[] children = dir.list();

        
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i=0; i<children.length; i++) {
            	Button b = new Button(this);
                String filename = children[i];
                
                if((!Globals.using_video && Globals.dirHasAFont(filename)) || (Globals.using_video && Globals.fontHasAnyVideos(filename))){
                files.put(i, filename);
                
                
                b.setId(i);
                b.setText(filename);
                b.setWidth(400);
                b.setGravity(Gravity.CENTER);
                b.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View view) {
						Log.d("Tap", "Clicked "+files.get(view.getId()));
					    if(Globals.edit) launchEdit(files.get(view.getId()));
						else openFont(files.get(view.getId()));
					}
                	
                });
                
                b.setOnTouchListener(new OnTouchListener(){
                
                	
					@Override
					public boolean onTouch(View view, MotionEvent arg1) {
						Button but = (Button) view;
						//filepath = but.getText().toString();
						
						but.setText("Loading ...");
						Log.d("Tap", "Touched");
						return false;
					}
                	
                });
                
                
                list.addView(b, new LayoutParams(LayoutParams.WRAP_CONTENT,
        				LayoutParams.WRAP_CONTENT));
                }
            }
        }
		

		
	
	}
	
	public void launchEdit(String s){
		Globals.base_dir_name = s;
		Globals.edit = true;

		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, super.getLocalClassName(), "ViewCapture - "+s, time);
		
		Intent intent = new Intent(this, ViewCaptureActivity.class);
		startActivity(intent);
	}
	
	
	public void openFont(String s){
		Globals.base_dir_name = s;
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, super.getLocalClassName(), "CanvasActivity - "+s, time);
			
		
		Intent intent = new Intent(this, CanvasActivity.class);
		startActivity(intent);
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

	}
	
	
//	public void remakeFont(String s){
//		Globals.base_dir_name = s;
//		Globals.useVideo = Globals.hasAnyVideos();
//		
//		
//		for(int i = 0; i < 5; i++){
//			Globals.stage = i;
//			DrawShapeOnTop dsot = new DrawShapeOnTop(this, Globals.getShape(i), true);
//			saveImages(i, dsot.getShapeImageOut());
//			Globals.buildLetters(i);
//		}
//		
//		
//		double endTime = System.currentTimeMillis();
//		double time = endTime - beginTime;
//		Globals.writeToLog(this, super.getLocalClassName(), "LoadActivity", time);
//		
//		Intent intent = new Intent(this, CanvasActivity.class);
//		startActivity(intent);
//	}
	
	
	@Override
	public void onRestart() {
		
		//change all the names back to what they are
		for(int i = 0; i < list.getChildCount(); i++){
			Button b = (Button) list.getChildAt(i);
			b.setText(files.get(b.getId()));
		}
		
		super.onRestart();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}



}
