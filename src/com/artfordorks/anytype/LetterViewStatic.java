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
 * This class is specifically for non-video mode
 * @author lauradevendorf
 *
 */
public class LetterViewStatic extends LetterView {

	
	public LetterViewStatic(Context context) {
		super(context);
		cur = -1;
		letters = new HashMap();
		letter_images = new HashMap();
		
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);	
	}
	

	@Override
	protected void customDraw(Canvas canvas, int uid, LetterInstance li, Matrix m) {
		
		if(letter_images.get(li.getId()) != null) canvas.drawBitmap(letter_images.get(li.getId()), m, null);	
	
	}
	
	
	@Override
	public void stopAllVideos(){

	}
	
	
	public void removeCurrentLetter(){
		
		if(letters.containsKey(cur)){
			LetterInstance li = letters.get(cur);
			letters.remove(cur);
			
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



	@Override
	public boolean isPlaying(int uid) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void stopLetterVideo(int uid) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void playLetterVideo(int uid) {
		// TODO Auto-generated method stub
		
	}
	
	
}