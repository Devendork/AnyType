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
 * essentially a video buffer is just a list of image files and some threads that load 
 * those image files in order
 * @author lauradevendorf
 *
 */
public class VideoBuffer {
	
	protected LinkedList<Bitmap> frames;
	protected File[] image_files;
	protected int	   next_frame_to_load = 0;
	protected int buffer_depth = Globals.buffer_depth;
	protected boolean ready = false;
	protected boolean reverse_order;
	protected int last_image_file = 0;

			
	
	//loads the first x frames into a buffer
	public void initFrameSet(){

		//push the initial frames
		for(int i = 0; i < buffer_depth; i++){
			LoadAnimationFrameThread thread = new LoadAnimationFrameThread(this);
			thread.execute(image_files[i]);
		}
		next_frame_to_load = buffer_depth;
	}
	
	
	
	public void setFrameBitmap(Bitmap b){
		frames.addLast(b);
		if(frames.size() == buffer_depth) ready = true;
		
	}
	
	public boolean isReady(){
		return ready;
	}
	
	//keeps check on how many are added because you can only add when you delete a frame
	//this recycles the last frame and then loads the next frame into the buffer
	public void recycleLastFrame(){
		if(frames.size() > 0){
		Bitmap b = frames.pop();
		b.recycle();
		
		LoadAnimationFrameThread thread = new LoadAnimationFrameThread(this);
		thread.execute(image_files[next_frame_to_load%image_files.length]);
		next_frame_to_load++;
		}
		
	}
	
	public int getTopFrameId(){
		int ndx = (next_frame_to_load - buffer_depth) % image_files.length;
		if(ndx < 0) ndx = image_files.length + ndx;
		return ndx;
	}
	
	//this returns the top of the buffer. 
	public Bitmap getTopFrame(){
		if(frames.size() > 0) return frames.getFirst();
		else return null;
	}
	
	public boolean isTopFrameLast(){
		int ndx = (next_frame_to_load - buffer_depth) % image_files.length;
		if(ndx < 0) ndx = image_files.length + ndx;
		
		return (ndx == last_image_file);
		
	}
	
	public boolean isTopFrameReverseLast(){
		if(!reverse_order) return false;
		
		int ndx = (next_frame_to_load - buffer_depth) % image_files.length;
		if(ndx < 0) ndx = image_files.length + ndx;
		
		return (ndx == (image_files.length -1));
		
	}
	
	public void clearBuffer(){
		Log.d("Async", "Clearing Buffer ");
		Log.d("Async", "Fames: "+frames.size()+" Buffer Depth "+buffer_depth);

		//recycle all the bitmaps
		for(int i = 0; i < frames.size(); i++) frames.get(0).recycle();
		frames.clear();

	}
	
	

}
