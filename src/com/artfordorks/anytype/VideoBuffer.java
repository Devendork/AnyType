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
 * This class manages the loading of frames for playing a letter back on the canvas
 * @author lauradevendorf
 *
 */
public class VideoBuffer {
	
	private LinkedList<Bitmap> frames;
	private File[] image_files;
	private int[] shape_in_letters_ids;
	private int	   next_frame_to_load = 0;
	private LetterView letter_view;
	private Letter letter;
	
	public VideoBuffer(LetterView lv, Letter l){

		int num_frames = 0;
		int ndx = 0;
		int[] shape_ids = l.getShapeIds();
		
		frames = new LinkedList<Bitmap>();
		letter_view = lv;
		letter = l;
		
		for(int i = 0; i < shape_ids.length; i++) num_frames += Globals.getShape(shape_ids[i]).getNumFrames();

		image_files = new File[num_frames];
		shape_in_letters_ids = new int[num_frames];
		
		Log.d("Async", "Image Files size "+image_files.length);
		
		for(int i = 0; i < shape_ids.length; i++){
			for(int j = 0; j <  Globals.getShape(shape_ids[i]).getNumFrames(); j++){
				image_files[ndx] = new File(Globals.getTestPath() 
						+ File.separator 
						+ Integer.toString(shape_ids[i]) 
						+ "_video_"
						+ j
						+".png");
				
				shape_in_letters_ids[ndx] = i;
				ndx++;
			}
		}
		
		initFrameSet();
	}		
	
	//loads the first x frames into a buffer
	public void initFrameSet(){
		//push the initial frames
		for(int i = 0; i < Globals.frames_per_second; i++){
			LoadAnimationFrameThread thread = new LoadAnimationFrameThread(this, i);
			thread.execute(image_files[i]);
		}

		next_frame_to_load = (int) Globals.frames_per_second;
		
	}
	
	public void setFrameBitmap(Bitmap b, int id){
		Log.d("Async", "Setting Bitmap at "+id);
		frames.addLast(b);
		if(letter_view != null && frames.size() == Globals.frames_per_second) letter_view.signalBeginVideo();
		
	}
	
	//keeps check on how many are added because you can only add when you delete a frame
	//this recycles the last frame and then loads the next frame into the buffer
	public void recycleLastFrame(){
		Bitmap b = frames.pop();
		b.recycle();
		
		LoadAnimationFrameThread thread = new LoadAnimationFrameThread(this, 100);
		thread.execute(image_files[next_frame_to_load%image_files.length]);
		next_frame_to_load++;
		
	}
	
	//this returns the top of the buffer. 
	public Bitmap getTopFrame(){
		return frames.getFirst();
	}
	
	public int getTopFrameShapeRelativeToLetter(){
		int ndx = (int) ((next_frame_to_load - Globals.frames_per_second)) % image_files.length;
		if(ndx < 0) ndx = shape_in_letters_ids.length + ndx;
		
		
		return shape_in_letters_ids[ndx];
	}

	public Letter getLetter(){
		return letter;
	}
	

}
