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
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.artfordorks.data.Letter;


/**
 * I think this class manages the view for a letter that is currently being edited
 * @author lauradevendorf
 *
 */
class LetterEditView extends View {

	private HashMap<Integer, ShapeInstance> shapes;
	private HashMap<Integer, Bitmap> shape_images;
	private int cur;
	private int uid = 0;
	private int box = 0;
	private Point ctr;
	private boolean showRect = true;


	public LetterEditView(Context context) {
		super(context);
		cur = -1;
		shapes = new HashMap();
		shape_images = new HashMap();
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		
		
		
	}
	

	
	public void setWidthandHeight(int w, int h){
		//start by drawing the rectangle of acceptable size in the middle of the screen
		float limiting = (w < h) ? w : h;
		//box = (int) ((limiting-50)*Globals.screen_density);
		
		box = 600;
		
		ctr = new Point();
		ctr.set((int)((w*Globals.screen_density - box) / 2), (int)((h*Globals.screen_density - box) / 2));
	}
	
	public void clearData(){
		cur = -1;
		shapes.clear();
		shape_images.clear();
		uid = 0;
	}
	
	public void initLetterShapes(){
		Path letterPath = new Path();
		
		Log.d("LetterEdit", "Initializing Letters");
		
		Letter l = Globals.getLetter(Globals.force_letter);
		int [] offset;
		int[] x_points = l.getXPoints();
		int[] y_points = l.getYPoints();
		float[] rots = l.getRotations();
		int[] shape_ids = l.getShapeIds();
		float[] scales = l.getScales();


		for (int j = 0; j < x_points.length; j++) {
			
			offset = Globals.shapes[shape_ids[j]].getOffset();
			ShapeInstance si = new ShapeInstance(shape_ids[j], Globals.force_letter);
			
			si.initShape((x_points[j])*Globals.shapeStretch, (y_points[j])*Globals.shapeStretch, rots[j], scales[j]);
			
			
			shapes.put((uid),si);
			if(!shape_images.containsKey(si.getId())){
				shape_images.put(si.getId(), Globals.decodeSampledBitmapFromResource(si.getBitmapPath(), 600, 600));
			}
			cur = uid++;
			
			letterPath.addPath(si.getPath());	
		}
		
		RectF bound = new RectF();
		letterPath.computeBounds(bound, true);
		
		float limiting = (bound.width() < bound.height()) ? bound.height() : bound.width();
		float scale = box / limiting;
		
		Log.d("WH", "Bounds "+bound.left+" "+bound.top+" "+bound.width()+" "+bound.height());
		Log.d("WH", "Box "+box+" Scale"+scale);

		Path pnew = new Path();
		Iterator it = shapes.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			ShapeInstance si = (ShapeInstance) me.getValue();
			float[] pos = si.getPos();
			//offset entire letter to 0, 0
			si.setPos(pos[0]-bound.left, pos[1]-bound.left);
			//make it all fit in the box
			//si.forceScale(scale-0.1f);
			
			pnew.addPath(si.getPath());
			
		}
		
		pnew.computeBounds(bound, true);
		Log.d("WH", "Bounds2 "+bound.left+" "+bound.top+" "+bound.width()+" "+bound.height());

		invalidate();
		
	}
	
	public int getShapesSize(){
		return shapes.size();
	}
	
	public int getShapeImagesSize(){
		return shape_images.size();
	}


	
	public void loadState(LetterEditView lev){
		this.shapes = lev.getShapes();
		this.cur = lev.getCur();
		this.uid = lev.getUid();
	}
	

	public HashMap<Integer, ShapeInstance> getShapes(){
		return shapes;
	}
	
	
	public int  getUid(){
		return uid;
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		
		
		
		Paint p = new Paint();
		
		if(showRect){
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.BLACK);
		
		canvas.translate(ctr.x, ctr.y);
		canvas.drawRect(0,0, box, box, p);
		}
		
		Iterator it = shapes.entrySet().iterator();
		
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			ShapeInstance li = (ShapeInstance) me.getValue();
			
			Matrix m = new Matrix(li.getM());
			Path path = li.getPath();
			
			if(li.hasFocus()){
				p = new Paint();
				p.setColor(Color.YELLOW);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(6);
				canvas.drawPath(path, p);
			}
			
			if(shape_images.get(li.getId()) != null) canvas.drawBitmap(shape_images.get(li.getId()), m, null);	
			
		}
		
		showRect = true;
		super.onDraw(canvas);
	}
	
	

	
	
	public int getCur(){
		return cur;
	}
	
	public int getCurLetterId(){
		ShapeInstance si = shapes.get(cur);
		return si.getId();
	}
	
	
	/*
	 * Finds all the letters containing the point and returns the smallest letter contained.  
	 */
	public int locate(int x, int y){
		x -= ctr.x;
		y -= ctr.y;
		
		ArrayList<Map.Entry> found = new ArrayList();
		Iterator it = shapes.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			ShapeInstance si = (ShapeInstance) me.getValue();

			if(si.contains(x, y)) found.add(me);
			
		}
		
		if(found.size() > 0){
			it = found.iterator();
			float min_area = -1;
			int min_id = -1;
			while(it.hasNext()){
				Map.Entry me = (Map.Entry) it.next();
				ShapeInstance si = (ShapeInstance) me.getValue();
				if(si.getBoundArea() < min_area || min_area == -1){
					min_area = si.getBoundArea();
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
		if(shapes.containsKey(id)){
		ShapeInstance si = shapes.get(id);
		si.setFocus(true);
		cur = id;
		}
		
	}
	

	
	public void deselect(int id){
		if(shapes.containsKey(id)){
		ShapeInstance si = shapes.get(id);
		si.setFocus(false);
		
		}
		cur = -1;
	}
	
	public void removeCurrentLetter(){
		
		if(shapes.containsKey(cur)){
			ShapeInstance si = shapes.get(cur);
			shapes.remove(cur);
			
			int num_shape_id = 0;
			Iterator it = shapes.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry  me = (Map.Entry) it.next();
				ShapeInstance ssi = (ShapeInstance) me.getValue();
				if(ssi.getId() == si.getId()) num_shape_id++;
			}
			
			if(num_shape_id == 0){
				shape_images.get(si.getId()).recycle();
				shape_images.remove(si.getId());
			}
			
			
		}
		cur = -1;
	}
	
	public void addLetter(int id){
		ShapeInstance si = new ShapeInstance(id, Globals.force_letter);
		shapes.put((uid),si);
		if(!shape_images.containsKey(si.getId())){
			shape_images.put(si.getId(), Globals.decodeSampledBitmapFromResource(si.getBitmapPath(), 600, 600));
		}
		cur = uid++;
		
		updatePosition((1280/26)*id, 60);
		
		//frame_bitmap =li.getFrameBitmap(frame_id);
		
	}
	
	
	
	public void updatePosition(float x, float y){
		if(cur == -1) return;
		
		ShapeInstance li = shapes.get(cur);
		li.setPositionCentered(x-ctr.x, y-ctr.y);
	}

	
	public void updateScale(double percent) {
		if(cur == -1) return;
		
		ShapeInstance si = shapes.get(cur);
		si.scaleByPercent(percent);
		
	}

//	public void updateScale(float scale) {
//		if(cur == -1) return;
//		
//		ShapeInstance li = shapes.get(cur);
//		li.setScale(scale*2);
//		
//	}
	

	
	public void incRotations(float rots){
		if(cur == -1) return;
		ShapeInstance li = shapes.get(cur);
		li.incRotations(rots);
	}
	
//	get the bitmap from the screen	
	public Bitmap getImageOut(){
		
		Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
		showRect = false;
		Log.d("BITMAP", "Get Image Out "+this.getWidth()+" "+this.getHeight());
		Canvas  c = new Canvas(bitmap);
		
		//changed this to draw(c) to fix the error - 2/14/13
		//onDraw(c);
		draw(c);
		
    	Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, box, box);
		bitmap.recycle();
		Bitmap scaled = Bitmap.createScaledBitmap(out, 600, 600, false);
		out.recycle();
		
		return scaled;
	}

	

	
	

}
