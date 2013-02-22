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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.artfordorks.data.Letter;
import com.artfordorks.data.Shape;

/**
 * This class handles the letter view when video playback takes place concurrently within a letter. It 
 * accomplishes this by storing a list of buffers for every letter which update every shape. The reason it
 * doesn't just refer to a common list of shapes is aesthetic. Different letters should initiate their play 
 * sequences at different times
 * 
 * @author lauradevendorf
 * 
 */
public class LetterViewConcurrent extends LetterView {

	private HashMap<Integer, HashMap<Integer, VideoBufferShape>> buffers;
	private HashMap<Integer, Boolean> buffers_ready;
	private Handler handler = new Handler();
	private int next_frame = 0;
	private final long frame_delay = 1000l / (long) Globals.frames_per_second;

	public LetterViewConcurrent(Context context) {
		super(context);
		cur = -1;
		letters = new HashMap();
		letter_images = new HashMap();
		buffers = new HashMap();
		buffers_ready = new HashMap();
		
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		// set a background timer that just keeps running
		handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				next_frame = buffers.size(); // give this as many values as
												// there are buffers
				invalidate();
				handler.postDelayed(this, frame_delay);
			}

		}, frame_delay);

	}

	@Override
	protected void customDraw(Canvas canvas, int uid, LetterInstance li, Matrix m) {


		if (buffers.containsKey(uid) && buffers_ready.containsKey(uid)) {
			drawVideoFrame(uid, canvas, m);
		} else {
			if (letter_images.get(li.getId()) != null)
				canvas.drawBitmap(letter_images.get(li.getId()), m,
						null);
		}

		if(buffers.containsKey(uid) && !buffers_ready.containsKey(uid)) checkBuffersReady(uid);



	}


	

	public void checkBuffersReady(int uid) {
			boolean ready = true;
			HashMap<Integer, VideoBufferShape> shape_list = (HashMap<Integer, VideoBufferShape>) buffers.get(uid);
			Iterator iit = shape_list.entrySet().iterator();
			while (iit.hasNext()) {
				Map.Entry mme = (Map.Entry) iit.next();
				VideoBufferShape vb = (VideoBufferShape) mme.getValue();
				if(!vb.isReady()) ready = false;
			}

		if(ready) buffers_ready.put(uid, true);
	}


	public void stopAllVideos() {
		Iterator it = buffers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			HashMap<Integer, VideoBufferShape> shape_list = (HashMap<Integer, VideoBufferShape>) me
					.getValue();
			Iterator iit = shape_list.entrySet().iterator();
			while (iit.hasNext()) {
				Map.Entry mme = (Map.Entry) iit.next();
				VideoBufferShape vb = (VideoBufferShape) mme.getValue();
				vb.clearBuffer();
			}
			shape_list.clear();
		}
		buffers.clear();
		buffers_ready.clear();
	}

	public void stopLetterVideo(int uid) {
		if (buffers.size() > 0) {
			HashMap<Integer, VideoBufferShape> shape_list = buffers.get(uid);
			Iterator iit = shape_list.entrySet().iterator();
			while (iit.hasNext()) {
				Map.Entry mme = (Map.Entry) iit.next();
				VideoBufferShape vb = (VideoBufferShape) mme.getValue();
				vb.clearBuffer();
			}
			shape_list.clear();
			buffers.remove(uid);
			buffers_ready.remove(uid);
		}
	}

	public void playLetterVideo(int uid) {
		// make sure to cancel any currently playing video
		Log.d("Async", "Loading Video Buffer " + uid);

		// check to see if something is playing and hasn't been stopped
		// if(buffer != null) stopLetterVideo();

		LetterInstance li = letters.get(uid);
		int[] shape_ids = Globals.letters[li.getId()].getShapeIds();
		HashMap shape_buffers = new HashMap();
		//comb through and make this 
		for(int i = 0; i < shape_ids.length; i++){
			shape_buffers.put(shape_ids[i],
				new VideoBufferShape(Globals.shapes[shape_ids[i]]));
		}
		
		buffers.put(uid, shape_buffers);
		checkBuffersReady(uid);

	}

	// tells you if the interface is playing and that it's the currently
	// selected letter that is playing
	public boolean isPlaying(int selected) {

		// is the the same letter_instance that's playing?
		if (buffers.size() > 0 && buffers.containsKey(selected))
			return true;

		return false;
	}

	// ONLY DRAW THE VIDEO FRAME FOR THE ONE THAT IS PLAYING
	public void drawVideoFrame(int uid, Canvas c, Matrix letter_matrix) {

		HashMap<Integer, VideoBufferShape> shape_buffers = (HashMap<Integer, VideoBufferShape>) buffers.get(uid);

		// next_frame will be flagged to true every x milliseconds to animate
		// the view
		if (next_frame > 0) {
			Iterator it = shape_buffers.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry me = (Map.Entry) it.next();
				VideoBufferShape vbs = (VideoBufferShape) me.getValue();
				vbs.recycleLastFrame();
			}
			next_frame--;
		}

		
		Letter l = Globals.letters[((LetterInstance)letters.get(uid)).getId()];

			int[] shape_ids = l.getShapeIds();
			int[] y_points = l.getY_points();
			int[] x_points = l.getX_points();
			float[] rots = l.getRotations();

			for(int i = 0; i < shape_ids.length; i++){
			Shape s = Globals.getShape(shape_ids[i]);
			int[] offset = s.getOffset();

			Bitmap bmap = shape_buffers.get(shape_ids[i]).getTopFrame();
			if (bmap != null) {

			c.save();
			c.setMatrix(letter_matrix);
			c.translate(x_points[i] * Globals.shapeStretch,
					y_points[i] * Globals.shapeStretch);
			c.rotate((int) Math.toDegrees(rots[i]));
			c.translate(offset[0] * Globals.shapeStretch, offset[1]
					* Globals.shapeStretch);

			Matrix m = new Matrix();
			c.drawBitmap(bmap, m, null);
			c.restore();
			}
		}

	}


	public void removeCurrentLetter() {

		if (letters.containsKey(cur)) {
			LetterInstance li = letters.get(cur);
			letters.remove(cur);

			// if we're removing something that's video is playing, make sure we
			// stop the video
			if (buffers.size() > 0 && buffers.containsKey(cur))
				stopLetterVideo(cur);

			// if this was the only instance of a letter, also delete it from
			// images
			int num_letter_id = 0;
			Iterator it = letters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				LetterInstance lli = (LetterInstance) me.getValue();
				if (lli.getId() == li.getId())
					num_letter_id++;
			}

			if (num_letter_id == 0) {
				letter_images.get(li.getId()).recycle();
				letter_images.remove(li.getId());
			}

		}
		cur = -1;
	}

	
}
