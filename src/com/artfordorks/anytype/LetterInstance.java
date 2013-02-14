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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.artfordorks.data.Shape;

/***
 * This class handles the letters that are drawn on the screen
 * @author lauradevendorf
 *
 */
public class LetterInstance {
	
	private int id;
	private boolean hasFocus;
	private float  scale;
	private float 	rotation;
	private File bitmapPath;
	private float[] pos;
	private RectF bounds;
	private Path p;
	private Matrix m;
	private float rotation_x;
	private float rotation_y;
	private float bmap_width;
	private float bmap_height;
	private float instanceWidth;
	private float instanceHeight;
	
	
	public LetterInstance(int id){
		
		this.id = id;
		this.hasFocus = false;
		this.scale = 0.5f;
		this.rotation = 0;
		this.p = new Path();
		
		bitmapPath = new File(Globals.getTestPath() + File.separator +Globals.intToChar(id)+".png");
		Bitmap bitmap = Globals.decodeSampledBitmapFromResource(bitmapPath,600, 600);
		bmap_width = bitmap.getWidth();	
		bmap_height = bitmap.getHeight();	
		bitmap.recycle();
		
		pos = new float[2];
		pos[0] = 0;
		pos[1] = 0;
		
		bounds = new RectF();
		updateVars();
	}
	
	

	
	private void updateVars(){
		int[] shape_ids = Globals.letters[id].getShapeIds();
		int[] letter_x = Globals.letters[id].getXPoints();
		int[] letter_y = Globals.letters[id].getYPoints();
		float[] rots =  Globals.letters[id].getRotations();
		float[] scales =  Globals.letters[id].getScales();

		p = new Path();
		boolean custom = Globals.letters[id].isCustom();

		
		for(int j = 0; j < shape_ids.length; j++){
			Shape s =  Globals.getShape(shape_ids[j]);
			int[] offset = s.getOffset();
			int[] x_points = s.getXPoints();
			int[] y_points = s.getYPoints();
						
			Path sp = new Path();
			sp.moveTo(x_points[0], y_points[0]);
			for (int i = 1; i < x_points.length; i++)
				sp.lineTo(x_points[i], y_points[i]);
			sp.lineTo(x_points[0], y_points[0]);
			
			
			
			
			
			Matrix tm = new Matrix();
			tm.preTranslate(letter_x[j], letter_y[j]);
			tm.preScale(scales[j], scales[j]);
			tm.preRotate((float) Math.toDegrees(rots[j]));
			tm.preScale(1f/scales[j], 1f/scales[j]);
			tm.preTranslate(offset[0], offset[1]);
			p.addPath(sp, tm);
			
		}
		
		m = new Matrix();
		m.preTranslate(pos[0], pos[1]);
		m.preScale(scale,scale);
		
		Path pcopy = new Path(p);
		pcopy.transform(m);
		pcopy.computeBounds(bounds, true);

		m.preTranslate(bounds.width()/2, bounds.height()/2);
		m.preRotate(rotation);
		m.preTranslate(-bounds.width()/2, -bounds.height()/2);

		
		instanceWidth = bmap_width*scale/2;
		instanceHeight = bmap_height*scale/2;
		
		p.transform(m);
		p.computeBounds(bounds, true);
		

	}
	
	public float getBoundArea(){
		return bounds.width()*bounds.height();
	}
	
	
	public float getInstanceHeight(){
		return instanceHeight;
	}
	
	public float getInstanceWidth(){
		return instanceWidth;
	}	
	
	public Matrix getM(){
		return m;
	}
	
	public Path getPath(){
		return p;
	}
	
	public boolean contains(int x, int y){
		Path temp = new Path(p);
		temp.addCircle(x, y, 10, Path.Direction.CCW);
		
		RectF r_temp = new RectF();
		temp.computeBounds(r_temp, true);
		
		if(r_temp.width()*r_temp.height() - bounds.width()*bounds.height() == 0) return true;
		return false;
		
		
	}
	
	
	public float[] getPos() {
		return pos;
	}



	public void setPosCentered(float x, float y) {
		
		float xoff = x - bounds.centerX();
		float yoff = y - bounds.centerY();
		
		this.pos[0] += xoff;
		this.pos[1] += yoff;
	
		//make sure to update the path
		updateVars();
		
	}
	
	
	public void scaleByPercent(double per){
		double curWidth = scale*600; 
		double newWidth = curWidth + (curWidth *per*0.01);
		double prescale = scale;
		

		scale = (float) (newWidth / 600);
		if(scale <= 0.1) scale = (float) 0.1;
		
		updateVars();
		Log.d("Scale", "cur / new "+curWidth+" : "+newWidth+" scale before : "+prescale +" scale after "+scale);
		
	}
	
	public void setScale(float scale2) {
		scale = scale2;
		updateVars();
	}
	
	public void incRotations(double r){
		rotation += r;
		updateVars();
	}
		
	
	public void setRotations(float rots, float x, float y){
//		rotation = rots;
//		//this should be relative to the letter
//		rotation_x = x - pos[0];
//		rotation_y = y - pos[1];
//		updateVars();
	}


	public boolean hasFocus() {
		return hasFocus;
	}

	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	public int getId() {
		return id;
	}

	public File getBitmapPath() {
		return bitmapPath;
	}
	
	public float getScale() {
		return scale;
	}

	public RectF getBounds() {
		return bounds;
	}

	public float getRotations() {
		return rotation;
	}


	
	

}
