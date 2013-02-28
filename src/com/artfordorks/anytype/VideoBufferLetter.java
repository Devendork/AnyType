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
import java.util.LinkedList;
import com.artfordorks.data.Letter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/**
 * This buffer object is specifically for playing each shape's frames in turn within the letter
 * @author lauradevendorf
 *
 */
public class VideoBufferLetter extends VideoBuffer{
	
	
	private int[] shape_in_letters_ids;
	private Letter letter;
	private int lv_unique_id; //the unique of the playing letter from letterview
	
	public VideoBufferLetter(int uid, Letter l, boolean rev){

		int num_frames = 0;
		int ndx = 0;
		int[] shape_ids = l.getShapeIds();
		
		frames = new LinkedList<Bitmap>();
		letter = l;
		lv_unique_id = uid;
		reverse_order = rev;
		
		for(int i = 0; i < shape_ids.length; i++) num_frames += Globals.getShape(shape_ids[i]).getNumFrames();

		if(reverse_order) image_files = new File[num_frames*2-2];
		else image_files = new File[num_frames];

		
		shape_in_letters_ids = new int[image_files.length];
		
		Log.d("Async", "Image Files size "+image_files.length);
		
		//handle the case when their might be fewer images than the buffer depth
		if(image_files.length < buffer_depth) buffer_depth = image_files.length;
		
		for(int i = 0; i < shape_ids.length; i++){
			for(int j = 0; j <  Globals.getShape(shape_ids[i]).getNumFrames(); j++){
				image_files[ndx] = new File(Globals.getTestPath() 
						+ File.separator 
						+ Integer.toString(shape_ids[i]) 
						+ "_video_"
						+ j
						+".png");
				
				shape_in_letters_ids[ndx] = i;
				Log.d("File Adds", "Added B"+image_files[ndx].getPath());

				ndx++;
			}
		}
		
		if(reverse_order){
			//reverse the direction
			for(int i = shape_ids.length-1; i <= 0; i--){
				for(int j = (Globals.getShape(shape_ids[i]).getNumFrames()-2); j >  0; j--){
					image_files[ndx] = new File(Globals.getTestPath() 
							+ File.separator 
							+ Integer.toString(shape_ids[i]) 
							+ "_video_"
							+ j
							+".png");
					shape_in_letters_ids[ndx] = i;

					Log.d("File Adds", "Added B"+image_files[ndx].getPath());
					ndx++;
				}
			}
		}
		
		initFrameSet();
	}		
	
	
	public int getTopFrameShapeRelativeToLetter(){
		int ndx = (next_frame_to_load - buffer_depth) % image_files.length;
		if(ndx < 0) ndx = shape_in_letters_ids.length + ndx;
		
		
		return shape_in_letters_ids[ndx];
	}

	public Letter getLetter(){
		return letter;
	}
	

	public int getUniqueId(){
		return lv_unique_id;
	}
	

}
