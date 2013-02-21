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
 * This class contains the view that acts as the canvas on which individuals compose messages
 * @author lauradevendorf
 *
 */
class LetterView extends View {

	private HashMap<Integer, LetterInstance> letters;
	private HashMap<Integer, Bitmap> letter_images;
	private int cur;
	private int uid = 0;
	
	private VideoBuffer buffer;
	private boolean playing = false;
	private Handler handler = new Handler();
	private boolean next_frame = false;
	private final long frame_delay = 1000l / (long)Globals.frames_per_second;


	public LetterView(Context context) {
		super(context);
		cur = -1;
		letters = new HashMap();
		letter_images = new HashMap();
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);	
		
		//set a background timer that just keeps running
		handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				next_frame = true;
				invalidate();
				handler.postDelayed(this, frame_delay);				
			}
			
		}, frame_delay);

	}
	

	public boolean hasLetters(){
		if(letters.size() == 0) return false;
		return true;
	}
	

	
	public void loadState(LetterView lv){
		this.letters = lv.getLetters();
		this.cur = lv.getCur();
		this.uid = lv.getUid();
		
		Iterator it = letters.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			LetterInstance li = (LetterInstance) me.getValue();
			if(!letter_images.containsKey(li.getId()))
			letter_images.put(li.getId(), Globals.decodeSampledBitmapFromResource(li.getBitmapPath(), 600, 600));
		}
	}
	



	
	
	public HashMap<Integer, LetterInstance> getLetters(){
		return letters;
	}
	
	

	
	
	public int  getUid(){
		return uid;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		Iterator it = letters.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			Integer uid = (Integer) me.getKey();
			LetterInstance li = (LetterInstance) me.getValue();
			
					
	
			Matrix m = new Matrix(li.getM());
			Path path = li.getPath();
			
			if(li.hasFocus()){
				Paint p = new Paint();
				p.setColor(Color.YELLOW);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				p.setStrokeWidth(10);
				canvas.drawPath(path, p);
			}
			
			m.preScale(0.5f, 0.5f);			
			
			//draw the whole letter first
			if(letter_images.get(li.getId()) != null) canvas.drawBitmap(letter_images.get(li.getId()), m, null);	
			
			//if there's video and the video has started playing and the shape is the one we want - draw the shape image on top of the letter
			if(Globals.using_video && playing && uid == buffer.getUniqueId()){
				drawVideoFrame(canvas, m);
			}
			
			
			
		}
		
		super.onDraw(canvas);
	}
	
	

	
	public void stopLetterVideo(){
		playing = false;
		
		if(buffer != null){
			buffer.clearBuffer();
			buffer = null;
		}
	}
	
	public void playLetterVideo(){
		//make sure to cancel any currently playing video 
		Log.d("Async", "Loading Video Buffer "+cur);

		if(cur != -1){
			//check to see if something is playing and hasn't been stopped
			if(buffer != null) stopLetterVideo(); 
			
			LetterInstance li = letters.get(cur);
			buffer = new VideoBuffer(cur, this, Globals.letters[li.getId()]);
			
		}
	}
	
	//tells you if the interface is playing and that it's the currently selected letter that is playing
	public boolean isPlaying(int selected){
		if(!playing) return false;

		//is the the same letter_instance that's playing?
		if(buffer != null && selected == buffer.getUniqueId()) return true;
		
		return false;
	}
	

	//ONLY DRAW THE VIDEO FRAME FOR THE ONE THAT IS PLAYING
	public void drawVideoFrame(Canvas c, Matrix letter_matrix){
		

		//next_frame will be flagged to true every x milliseconds to animate the view
		if(next_frame){
			buffer.recycleLastFrame();
			next_frame = false;
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
	
	
	public int getCur(){
		return cur;
	}
	
	public int getCurLetterId(){
		LetterInstance li = letters.get(cur);
		return li.getId();
	}
	
	public int getSelectedLetterId(int selected){
		LetterInstance li = letters.get(selected);
		return li.getId();
	}
	
	/*
	 * Finds all the letters containing the point and returns the smallest letter contained.  
	 * returns the unqiue id of the letter that was found
	 */
	public int locate(int x, int y){
		ArrayList<Map.Entry> found = new ArrayList();
		Iterator it = letters.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			LetterInstance li = (LetterInstance) me.getValue();

			if(li.contains(x, y)) found.add(me);
			
		}
		
		if(found.size() > 0){
			it = found.iterator();
			float min_area = -1;
			int min_id = -1;
			while(it.hasNext()){
				Map.Entry me = (Map.Entry) it.next();
				LetterInstance li = (LetterInstance) me.getValue();
				if(li.getBoundArea() < min_area || min_area == -1){
					min_area = li.getBoundArea();
					min_id = (Integer) me.getKey();
				}
			}
			return min_id;
		}
		
		return -1;
	}
	
/**
 * Selected the item at key in letters
 * @param id
 */
	public void select(int id){
		if(letters.containsKey(id)){
		LetterInstance li = letters.get(id);
		li.setFocus(true);
		cur = id;
		}
		
	}

	
	public void deselect(int id){
		if(letters.containsKey(id)){
		LetterInstance li = letters.get(id);
		li.setFocus(false);
		
		}
		cur = -1;
	}
	
	public void removeCurrentLetter(){
		
		if(letters.containsKey(cur)){
			LetterInstance li = letters.get(cur);
			letters.remove(cur);
			
			//if we're removing something that's video is playing, make sure we stop the video
			if(buffer != null && buffer.getUniqueId() == cur) stopLetterVideo();
			
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
	
	public void addLetter(int id){
		LetterInstance li = new LetterInstance(id);
		letters.put((uid),li);
		if(!letter_images.containsKey(li.getId())){
			letter_images.put(li.getId(), Globals.decodeSampledBitmapFromResource(li.getBitmapPath(), 600, 600));
		}
		cur = uid++;
		updatePosition((1280/26)*id, 60);
		
		//frame_bitmap =li.getFrameBitmap(frame_id);
		
	}
	
	///this is only to be used in the Letter Video View Class
	public void addSingleLetter(int id){
		letters.put((uid), new LetterInstance(id));
	}
	
	public void updatePosition(float x, float y){
		if(cur == -1) return;
		LetterInstance li = letters.get(cur);
		li.setPosCentered(x, y);
	}
	
	

	public void updateScale(double percent){
		if(cur == -1) return;
		
		LetterInstance li = letters.get(cur);
		li.scaleByPercent(percent);
	}

//	public void updateScale(float scale) {
//		if(cur == -1) return;
//		
//		LetterInstance li = letters.get(cur);
//		li.setScale(scale*2);
//		
//	}
	
	public void setRotations(float rots, float x, float y){
		if(cur == -1) return;
		
		LetterInstance li = letters.get(cur);
		li.setRotations(rots, x, y);
		
	}
	
	public void incRotations(float rots){
		if(cur == -1) return;
		LetterInstance li = letters.get(cur);
		li.incRotations(rots);
		
	}
	
//	get the bitmap from the screen	
	public Bitmap getImageOut(){

		Log.d("BITMAP", "Get Image Out "+Globals.preview_size.x+" "+Globals.preview_size.y);
		Bitmap  bitmap = Bitmap.createBitmap(Globals.preview_size.x, Globals.preview_size.y, Bitmap.Config.ARGB_8888);
		Canvas  c = new Canvas(bitmap);
		
		Log.d("BITMAP", "onDraw");
		//changed to draw due to error - 2/14/13
		//onDraw(c);
		
		draw(c);
		Log.d("BITMAP", "return bitmap");

		//end copy
		return bitmap;
	}

	//it only sets to true after the buffer says it's okay
	public void signalBeginVideo() {
		if(!playing){
			playing = true;
		 }			
	}

	

	
	

}
