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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.artfordorks.anytype.R.id;
import com.artfordorks.data.Letter;
import com.artfordorks.data.Shape;

/**
 * This is the first activity that runs and it gets everything moving. It launches the first screen
 * where you can select what action to take next 
 * @author lauradevendorf
 *
 */
public class LoadActivity extends Activity {

	protected static final String TAG = null;
	private static Globals environment;
	private static int stage = 0; // keeps a record of which shape we're
									// capturing
	
	private double beginTime = System.currentTimeMillis();
	private EditText fps_input;
	private TextView fps_label;
	
	public void loadShapes() {
		int ndx = 0;
		int count = 0;
		InputStream is;
		ArrayList x = new ArrayList();
		ArrayList y = new ArrayList();
		Iterator it;
		String[] data = new String[2];

		for (int id = 0; id < 5; id++) {
			x.clear();
			y.clear();
			
			try {

				String str = "";

				if(id == 0) is = this.getResources().openRawResource(R.raw.sa);
				else if(id == 1) is = this.getResources().openRawResource(R.raw.sb);
				else if(id == 2) is = this.getResources().openRawResource(R.raw.sc);
				else if(id == 3)is = this.getResources().openRawResource(R.raw.sd);
				else is = this.getResources().openRawResource(R.raw.se);
				
				
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				if (is != null) {
					while ((str = reader.readLine()) != null) {
						Log.d("Load Shape", "Reading: " + str);
						data = str.split(",");
						x.add(Integer.valueOf(data[0].trim()));
						y.add(Integer.valueOf(data[1].trim()));
					}
				}
				is.close();

			} catch (IOException e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			// add the points to the arrays
			it = x.iterator();
			int[] x_points = new int[x.size()];
			ndx = 0;
			while (it.hasNext()) {
				x_points[ndx++] = (Integer) it.next();
			}
			
			x.clear();

			it = y.iterator();
			int[] y_points = new int[y.size()];
			ndx = 0;
			while (it.hasNext()) {
				y_points[ndx++] = (Integer) it.next();
			}
			
			y.clear();

			environment.getShape(id).setPoints(x_points, y_points);

		}

	}
	
	
	public void loadLetters() {
		
		int ndx = 0;
		int count = 0;
		InputStream is;
		ArrayList x = new ArrayList();
		ArrayList y = new ArrayList();
		ArrayList z = new ArrayList();
		ArrayList shape_ids = new ArrayList();
		Iterator it;
		String[] data = new String[4];

		for (int id = 0; id < 26; id++) {
			x.clear();
			y.clear();
			z.clear();
			shape_ids.clear();
			
			try {

				String str = "";


				if(id == 0) is = this.getResources().openRawResource(R.raw.a);
				else if(id == 1) is = this.getResources().openRawResource(R.raw.b);
				else if(id == 2) is = this.getResources().openRawResource(R.raw.c);
				else if(id == 3)is = this.getResources().openRawResource(R.raw.d);
				else if(id == 4) is = this.getResources().openRawResource(R.raw.e);
				else if(id == 5) is = this.getResources().openRawResource(R.raw.f);
				else if(id == 6)is = this.getResources().openRawResource(R.raw.g);
				else if(id == 7) is = this.getResources().openRawResource(R.raw.h);
				else if(id == 8) is = this.getResources().openRawResource(R.raw.i);
				else if(id == 9)is = this.getResources().openRawResource(R.raw.j);
				else if(id == 10) is = this.getResources().openRawResource(R.raw.k);
				else if(id == 11) is = this.getResources().openRawResource(R.raw.l);
				else if(id == 12)is = this.getResources().openRawResource(R.raw.m);
				else if(id == 13) is = this.getResources().openRawResource(R.raw.n);
				else if(id == 14) is = this.getResources().openRawResource(R.raw.o);
				else if(id == 15)is = this.getResources().openRawResource(R.raw.p);
				else if(id == 16) is = this.getResources().openRawResource(R.raw.q);
				else if(id == 17) is = this.getResources().openRawResource(R.raw.r);
				else if(id == 18)is = this.getResources().openRawResource(R.raw.s);
				else if(id == 19) is = this.getResources().openRawResource(R.raw.t);
				else if(id == 20) is = this.getResources().openRawResource(R.raw.u);
				else if(id == 21)is = this.getResources().openRawResource(R.raw.v);
				else if(id == 22) is = this.getResources().openRawResource(R.raw.w);
				else if(id == 23) is = this.getResources().openRawResource(R.raw.x);
				else if(id == 24)is = this.getResources().openRawResource(R.raw.y);
				else is = this.getResources().openRawResource(R.raw.z);
				

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				if (is != null) {
					while ((str = reader.readLine()) != null) {
						data = str.split(",");
						x.add(Integer.valueOf(data[0].trim()));
						y.add(Integer.valueOf(data[1].trim()));
						z.add(Float.valueOf(data[2].trim()));
						shape_ids.add(Integer.valueOf(data[3].trim()));

					}
				}
				is.close();

			} catch (IOException e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			// add the points to the arrays
			it = x.iterator();
			int[] x_points = new int[x.size()];
			ndx = 0;
			while (it.hasNext()) {
				x_points[ndx++] = (Integer) it.next();
			}

			
			it = y.iterator();
			int[] y_points = new int[y.size()];
			ndx = 0;
			while (it.hasNext()) {
				y_points[ndx++] = (Integer) it.next();
			}
			
			
			it = z.iterator();
			float[] r = new float[z.size()];
			ndx = 0;
			while (it.hasNext()) {
				r[ndx++] = (Float) it.next();
			}
			
			it = shape_ids.iterator();
			int[] shape_info = new int[shape_ids.size()];
			ndx = 0;
			while (it.hasNext()) {
				shape_info[ndx++] = (Integer) it.next();
			}

			environment.getLetter(id).setInfo(x_points, y_points, r, shape_info);

		}
		
		

	}
	
	public void cleanUpShapes(){
		Rect[] skews = new Rect[Globals.shapes.length];
		Letter l;
		Shape s;
		int[] shapes, x, y;
		int correct_x,correct_y;
		
		//make a list of all of the offsets of the points
		for(int i = 0; i < Globals.shapes.length; i++){
			Rect r = Globals.shapes[i].getBounds();
			skews[i] = r;
		    correct_x = -1*r.left;
			correct_y = -1*r.top;
			Globals.shapes[i].offset(correct_x, correct_y);
		}
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Point size = new Point(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);

		environment = new Globals(size, getResources().getDisplayMetrics().density, this);
	
		Globals.edit = false;
			

		loadShapes();
		loadLetters();
		cleanUpShapes();

		setContentView(R.layout.main);
		Log.d("Load Activity", "Loaded");
		
		Globals.saved_lv = null;
		Globals.playback_mode = false;

		Button new_font = (Button) findViewById(id.button_new);
		new_font.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startNewFont();
			}
		});
		
		
		Button button_load = (Button) findViewById(id.button_load);
		button_load.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadExistingFont();
			}
		});
		
		
		
		
		Button button_edit = (Button) findViewById(id.button_edit);
		button_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Globals.edit = true;
				loadExistingFont();
			}
		});
		
		Button button_rebuild = (Button) findViewById(id.button_rebuild);
		button_rebuild.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 Globals.rebuild = true;
				loadExistingFont();

			}
		});
		
		fps_input = (EditText) findViewById(id.edit_text_fps);
		fps_input.setText(String.valueOf(Globals.frames_per_second));
		fps_input.setTextColor(Color.BLACK);
		fps_label = (TextView) findViewById(id.text_fps);
		
		
		if(!Globals.using_video){
			fps_input.setVisibility(View.INVISIBLE);
			fps_label.setVisibility(View.INVISIBLE);
		}
		
		Switch video_switch = (Switch) findViewById(id.video_toggle);
		video_switch.setChecked(Globals.using_video);
		video_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Globals.using_video = isChecked;
				if(isChecked){
					fps_input.setVisibility(View.VISIBLE);
					fps_label.setVisibility(View.VISIBLE);
				}else{
					fps_input.setVisibility(View.INVISIBLE);
					fps_label.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		
		fps_input.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	String temp = s.toString();
	        	temp = temp.trim();
	        	if(temp.length() == 0) Globals.frames_per_second = 0;
	        	else Globals.frames_per_second = Integer.parseInt(s.toString());
	           
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 

		

		

	}

	 
	public void startNewFont(){
		Globals.createNewDirectory(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
		Intent intent;
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "CaptureActivity", time);
		
		if(Globals.using_video)  intent = new Intent(this, VideoCaptureActivity.class);
		else  intent = new Intent(this, PhotoCaptureActivity.class);
		startActivity(intent);
	}
	
	
	public void loadExistingFont(){
		
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, this.getLocalClassName(), "LoadFontActivity", time);
		
		Intent intent = new Intent(this, LoadFontActivity.class);
		startActivity(intent);
	}
	
	



}
