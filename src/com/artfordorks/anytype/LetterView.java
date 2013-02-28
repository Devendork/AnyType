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
public abstract class LetterView extends View {



	protected HashMap<Integer, LetterInstance> letters;
	protected HashMap<Integer, Bitmap> letter_images;
	protected int cur;
	protected int uid = 0;
	protected boolean reverse_order;
	

	public LetterView(Context context) {
		super(context);
		
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
		
		
			customDraw(canvas, uid, li, m);
		}
		
		super.onDraw(canvas);
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
		
	
	public void addLetter(int id){
		LetterInstance li = new LetterInstance(id);
		letters.put((uid),li);
		if(!letter_images.containsKey(li.getId())){
			letter_images.put(li.getId(), Globals.decodeSampledBitmapFromResource(li.getBitmapPath(), 600, 600));
		}
		cur = uid++;
		updatePosition((1280/26)*id, 60);
				
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
	
	public void setReverse(boolean r){
		stopAllVideos();
		reverse_order = r;
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
	
	public abstract void stopAllVideos();
	public abstract boolean isPlaying(int uid);
	public abstract void stopLetterVideo(int uid);
	public abstract void playLetterVideo(int uid);
	public abstract void removeCurrentLetter();
	protected abstract void customDraw(Canvas canvas, int uid, LetterInstance li, Matrix m);
	
	
}
