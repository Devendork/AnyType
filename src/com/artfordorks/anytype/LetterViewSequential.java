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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.artfordorks.data.Letter;
import com.artfordorks.data.Shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.View;


/**
 * This class is specifically for the letter view in the mode where letters play back in sequential order
 * @author lauradevendorf
 *
 */
public class LetterViewSequential extends LetterView {

	
	private HashMap<Integer, VideoBuffer> buffers; //map the unique id to the Video Buffer
	private Handler handler = new Handler();
	private int next_frame = 0; 
	private final long frame_delay = 1000l / (long)Globals.frames_per_second;


	public LetterViewSequential(Context context) {
		super(context);
		cur = -1;
		letters = new HashMap();
		letter_images = new HashMap();
		buffers = new HashMap();
		
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);	
		
		//set a background timer that just keeps running
		handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				next_frame = buffers.size(); //give this as many values as there are buffers 
				invalidate();
				handler.postDelayed(this, frame_delay);				
			}
			
		}, frame_delay);

	}
	

	
	@Override
	protected void customDraw(Canvas canvas, int uid, LetterInstance li, Matrix m) {

		// draw the whole letter first
		if (letter_images.get(li.getId()) != null)
			canvas.drawBitmap(letter_images.get(li.getId()), m, null);

		// if there's video and the video has started playing and the shape is
		// the one we want - draw the shape image on top of the letter
		if (Globals.using_video && buffers.containsKey(uid)
				&& buffers.get(uid).isReady()) {
			drawVideoFrame(uid, canvas, m);
		}

	}
	
	
	public void stopAllVideos(){
		Iterator it = buffers.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			VideoBuffer vb = (VideoBuffer) me.getValue();
			vb.clearBuffer();
			
		}
		buffers.clear();
	}
	
	
	public void stopLetterVideo(int uid){		
		if(buffers.size() > 0){
			buffers.get(uid).clearBuffer();
			buffers.remove(uid);
		}
	}
	
	public void playLetterVideo(int uid){
		//make sure to cancel any currently playing video 
		Log.d("Async", "Loading Video Buffer "+uid);
			
			LetterInstance li = letters.get(uid);
			buffers.put(uid, new VideoBufferLetter(uid, Globals.letters[li.getId()]));
			
	}
	
	//tells you if the interface is playing and that it's the currently selected letter that is playing
	public boolean isPlaying(int selected){
		
		//is the the same letter_instance that's playing?
		if(buffers.size() > 0 && buffers.containsKey(selected)) return true;
		
		return false;
	}
	

	//ONLY DRAW THE VIDEO FRAME FOR THE ONE THAT IS PLAYING
	public void drawVideoFrame(int uid, Canvas c, Matrix letter_matrix){
		
		VideoBufferLetter buffer = (VideoBufferLetter) buffers.get(uid);

		
		//next_frame will be flagged to true every x milliseconds to animate the view
		if(next_frame > 0){
			buffer.recycleLastFrame();
			next_frame--;
		}
	
		int shape_in_letter_id = buffer.getTopFrameShapeRelativeToLetter();
		Bitmap bmap = buffer.getTopFrame();

		if(bmap != null){
		int[] shape_ids = buffer.getLetter().getShapeIds();
		int[] y_points = buffer.getLetter().getY_points();
		int[] x_points = buffer.getLetter().getX_points();
		float[] rots = buffer.getLetter().getRotations();

		Shape s = Globals.getShape(shape_ids[shape_in_letter_id]);
		int[] offset = s.getOffset();

		c.save();
		c.setMatrix(letter_matrix);
		c.translate(x_points[shape_in_letter_id] * Globals.shapeStretch, y_points[shape_in_letter_id]
				* Globals.shapeStretch);
		c.rotate((int) Math.toDegrees(rots[shape_in_letter_id]));
		c.translate(offset[0] * Globals.shapeStretch, offset[1]
				* Globals.shapeStretch);
		
		Matrix m = new Matrix();
		c.drawBitmap(bmap, m, null);
		c.restore();
		}
				
				
	}
	
	
	
	public void removeCurrentLetter(){
		
		if(letters.containsKey(cur)){
			LetterInstance li = letters.get(cur);
			letters.remove(cur);
			
			//if we're removing something that's video is playing, make sure we stop the video
			if(buffers.size() > 0 && buffers.containsKey(cur)) stopLetterVideo(cur);
			
			//if this was the only instance of a letter, also delete it from images
			int num_letter_id = 0;
			Iterator it = letters.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry  me = (Map.Entry) it.next();
				LetterInstance lli = (LetterInstance) me.getValue();
				if(lli.getId() == li.getId()) num_letter_id++;
			}
			
			if(num_letter_id == 0){
				letter_images.get(li.getId()).recycle();
				letter_images.remove(li.getId());
			}
			
			
		}
		cur = -1;
	}
}
