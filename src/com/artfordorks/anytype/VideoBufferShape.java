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
import com.artfordorks.data.Shape;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/**
 * This buffer is specifically for individuals shapes to play back
 * @author lauradevendorf
 *
 */
public class VideoBufferShape extends VideoBuffer {
	
	private int shape_id;
	
	public VideoBufferShape(Shape s){

		int ndx = 0;
		
		this.shape_id = s.getId();
		
		frames = new LinkedList<Bitmap>();
		image_files = new File[(s.getNumFrames()*2)-2];
		
		Log.d("Async", "Image Files size "+image_files.length);
		
		//handle the case when their might be fewer images than the buffer depth
		if(image_files.length < buffer_depth) buffer_depth = image_files.length;
		
		Log.d("File Adds", "Image Files Len "+image_files.length);

		//load the images in the order you want them played
			for(int j = 0; j <  s.getNumFrames(); j++){
				image_files[ndx] = new File(Globals.getTestPath() 
						+ File.separator 
						+ Integer.toString(shape_id) 
						+ "_video_"
						+ j
						+".png");
				Log.d("File Adds", "Added F "+image_files[ndx].getPath());
				ndx++;
			}
			
			//reverse the direction
			for(int j = (s.getNumFrames()-2); j >  0; j--){
				image_files[ndx] = new File(Globals.getTestPath() 
						+ File.separator 
						+ Integer.toString(shape_id) 
						+ "_video_"
						+ j
						+".png");
				Log.d("File Adds", "Added B"+image_files[ndx].getPath());
				ndx++;
			}
		
		initFrameSet();
	}		

}
