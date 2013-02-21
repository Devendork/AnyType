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
	private int lv_unique_id; //the unique of the playing letter from letterview
	private int buffer_depth = Globals.buffer_depth;
	
	public VideoBuffer(int uid, LetterView lv, Letter l){

		int num_frames = 0;
		int ndx = 0;
		int[] shape_ids = l.getShapeIds();
		
		frames = new LinkedList<Bitmap>();
		letter_view = lv;
		letter = l;
		lv_unique_id = uid;
		
		for(int i = 0; i < shape_ids.length; i++) num_frames += Globals.getShape(shape_ids[i]).getNumFrames();

		image_files = new File[num_frames];
		shape_in_letters_ids = new int[num_frames];
		
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
				ndx++;
			}
		}
		
		initFrameSet();
	}		
	
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
		if(letter_view != null && frames.size() == buffer_depth) letter_view.signalBeginVideo();
		
	}
	
	//keeps check on how many are added because you can only add when you delete a frame
	//this recycles the last frame and then loads the next frame into the buffer
	public void recycleLastFrame(){
		Bitmap b = frames.pop();
		b.recycle();
		
		LoadAnimationFrameThread thread = new LoadAnimationFrameThread(this);
		thread.execute(image_files[next_frame_to_load%image_files.length]);
		next_frame_to_load++;
		
	}
	
	//this returns the top of the buffer. 
	public Bitmap getTopFrame(){
		if(frames.size() > 0) return frames.getFirst();
		else return null;
	}
	
	public int getTopFrameShapeRelativeToLetter(){
		int ndx = (next_frame_to_load - buffer_depth) % image_files.length;
		if(ndx < 0) ndx = shape_in_letters_ids.length + ndx;
		
		
		return shape_in_letters_ids[ndx];
	}

	public Letter getLetter(){
		return letter;
	}
	
	
	public void clearBuffer(){
		Log.d("Async", "Clearing Buffer on "+letter.getId());
		Log.d("Async", "Fames: "+frames.size()+" Buffer Depth "+buffer_depth);

		//cancel the threads that might be running
		
		//wait until all of the threads are done
//		while(frames.size() < buffer_depth){
//			Log.d("Async", "Clearing Buffer on "+letter.getId());
//			Log.d("Async", "Fames: "+frames.size()+" Buffer Depth "+buffer_depth);
//
//			try {
//				Thread.sleep(500, 0);
//			} catch (InterruptedException e) {
//				Log.d("Async", "Thread Interupted in Clear Buffer");
//				e.printStackTrace();
//			}
//		}
		
		//recycle all the bitmaps
		for(int i = 0; i < frames.size(); i++) frames.get(0).recycle();
		frames.clear();

	}
	
	public int getUniqueId(){
		return lv_unique_id;
	}
	

}
