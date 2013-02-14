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



package com.artfordorks.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.artfordorks.anytype.*;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

/**
 * This class manages the base data for any letter as read from the data files
 * @author lauradevendorf
 *
 */
public class Letter{

	private int  	id;
	private int[]   x_points;
	private int[]   y_points;
	private float[] rotations;
	private int[]	shape_ids;
	private float[]   scales;
//	private Bitmap  image;  //don't keep these in memory if I don't have to 
//	private Bitmap 	edited;
	private Rect bounds;
	private boolean isCustom;
	


	public Letter(int id){
		this.id = id;
        Log.d("Letter", "Initialized "+id);
        isCustom = false;
	}
	
	public boolean isCustom(){
		return isCustom;
	}
	
	//this can only happen 
	public void  updateLetterFromInstance(HashMap shapes){
		/*isCustom = true;
		
		for(int i = 0; i < x_points.length; i++){
		Log.d("update", "was: "+x_points[i]+" "+y_points[i]+" "+rotations[i]+" "+scales[i]);
		}
		
		x_points = new int[shapes.size()];
		y_points = new int[shapes.size()];
		shape_ids = new int[shapes.size()];
		rotations = new float[shapes.size()];
		scales = new float[shapes.size()];
		
		int i = 0;
		Iterator it = shapes.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry) it.next();
			ShapeInstance si = (ShapeInstance) me.getValue();
			float[] pos = si.getPos();
			x_points[i] = (int) pos[0];
			y_points[i] = (int) pos[1];
			shape_ids[i] = si.getId();
			rotations[i] = (float) Math.toRadians(si.getRotations());
			scales[i] = si.getScale();
			Log.d("update", "is: "+x_points[i]+" "+y_points[i]+" "+rotations[i]+" "+scales[i]);
			
			
			i++;
			
			
		}
		
	*/
		
	}

	public void setInfo(int[] x, int[]y, float[] r, int[] s){
		x_points = x;
		y_points = y;
		rotations = r;
		shape_ids = s;
		scales = new float[x.length];
		for(int i = 0; i< x.length; i++) scales[i] = 1f;
		makeBounds();
	}
	
	public int[] getXPoints(){
		return x_points;
	}
	
	public int[] getYPoints(){
		return y_points;
	}
	
	public float[] getRotations(){
		return rotations;
	}
	
	public int[] getShapeIds(){
		return shape_ids;
	}
	
	public int getId(){
		return id;
	}
	
	//sets the left point of the letter to 0, 0
	public void cleanUp(){
		//offset(-1*bounds.left, -1*bounds.top);
	}
	
	public void makeBounds(){
		bounds = new Rect(1000000, 1000000, -1000000, -1000000);
		
		for(int i = 0; i < x_points.length; i++){
			Rect s_bound = Globals.getShape(shape_ids[i]).getBounds();
			if(x_points[i] < bounds.left) bounds.left = x_points[i];
			if((x_points[i] + s_bound.width())  > bounds.right) bounds.right = (x_points[i] + s_bound.width());
			if(y_points[i] < bounds.top) bounds.top = y_points[i];
			if((y_points[i]+ s_bound.height()) > bounds.bottom) bounds.bottom = (y_points[i]+ s_bound.height());
		}
	}
	
	public void offset(int x, int y){
		for(int i = 0; i < x_points.length; i++){
			x_points[i] += x;
			y_points[i] += y;
		}
		makeBounds();		
	}
	
	public Rect getBounds(){
		return bounds;
	}
	

	public int[] getX_points() {
		return x_points;
	}

	public void setX_points(int[] x_points) {
		this.x_points = x_points;
	}

	public int[] getY_points() {
		return y_points;
	}

	public void setY_points(int[] y_points) {
		this.y_points = y_points;
	}

	public int[] getShape_ids() {
		return shape_ids;
	}
	
	public float[] getScales() {
		return scales;
	}

	public void setShape_ids(int[] shape_ids) {
		this.shape_ids = shape_ids;
	}

	public void setRotations(float[] rotations) {
		this.rotations = rotations;
	}
	
	
	
}
