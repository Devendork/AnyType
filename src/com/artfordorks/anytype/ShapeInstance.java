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

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.artfordorks.data.Shape;

/***
 * A shape instance manages one particular instance of shape with in a letter while editing
 * @author lauradevendorf
 *
 */
public class ShapeInstance {
	
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
	private boolean moved = false;
	private int letter_id;
	
	public ShapeInstance(int id, int letter_id){
		
		this.letter_id = letter_id;
		this.id = id;
		this.hasFocus = false;
		this.scale = 1f;
		this.rotation = 0;
		this.p = new Path();
		
		bitmapPath = new File(Globals.getTestPath() + File.separator + "IMG_"+id + "_CROP.png");
		bmap_width = Globals.letter_size;	
		bmap_height = Globals.letter_size;	
		
		pos = new float[2];
		pos[0] = 0;
		pos[1] = 0;
		

		bounds = new RectF();
		updateVars();
	}
	

	
	private void updateVars(){

		p = new Path();
		
		Shape s =  Globals.getShape(id);
		int[] offset = s.getOffset();
		int[] x_points = s.getXPoints();
		int[] y_points = s.getYPoints();
		
		//create a path from these points
		Path sp = new Path();
		sp.moveTo(x_points[0], y_points[0]);
		for (int i = 1; i < x_points.length; i++)
			sp.lineTo(x_points[i], y_points[i]);
		sp.lineTo(x_points[0], y_points[0]);
		
		Matrix tm = new Matrix();
	    tm.preScale(Globals.shapeStretch, Globals.shapeStretch);
	    p.addPath(sp, tm);
		
		m = new Matrix();
		m.preTranslate(pos[0], pos[1]);
		m.preScale(scale,scale);
		m.preRotate(rotation);
		if(!moved && !Globals.getLetter(letter_id).isCustom()) m.preTranslate(offset[0]*Globals.shapeStretch, offset[1]*Globals.shapeStretch);
		

		p.transform(m);
		p.computeBounds(bounds, true);
		
		Log.d("Bound", "P: "+bounds.top+" "+bounds.left);


	}
	
	
	
	public void scaleByPercent(double per){
		double curWidth = scale*600; 
		double newWidth = curWidth + (curWidth *per*0.01);
		double prescale = scale;
		

		scale = (float) (newWidth / 600);
		if(scale <= 0.1) scale = (float) 0.1;
		
		Log.d("Scale", "cur / new "+curWidth+" : "+newWidth+" scale before : "+prescale +" scale after "+scale);
		
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
	
	public void initShape(float x, float y, float rots, float scale){

		rotation = (float) Math.toDegrees(rots);	
		this.scale = scale;
		this.pos[0] = (float) (x);
		this.pos[1] = (float) (y);	
		
		updateVars();
		
	}

	public void setPositionCentered(float x, float y) {
		moved = true;
		
		Log.d("Bounds", "Bounds Center "+bounds.centerX()+" "+bounds.centerY());
		float xoff = x - bounds.centerX();
		float yoff = y - bounds.centerY();
		
		this.pos[0] += xoff;
		this.pos[1] += yoff;

	
		//make sure to update the path
		updateVars();
		
	}
	
	public float getBoundArea(){
		return bounds.width()*bounds.height();
	}

	public void setPos(float x, float y) {
		
		this.pos[0] = x;
		this.pos[1] = y;
	
		//make sure to update the path
		updateVars();
		
	}
	
	public void setScale(float scale2) {
		scale = scale2;
		updateVars();
	}
		
	
	public void incRotations(double r){
		rotation += r;
		updateVars();
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
	
	public void forceScale(float s){
		scale = s;
		pos[0] *= scale;
		pos[1] *= scale;
		
		updateVars();
	}
	


	
	

}
