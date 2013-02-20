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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.artfordorks.anytype.Globals;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.util.Log;


/**
 * This class handles the base information for any shape (rectangle, arch)
 * @author lauradevendorf
 *
 */
public class Shape extends Activity{

	private int  	id;
	private int[]   x_points;
	private int[]   y_points;
	
	private Path 	path;
	private Path	custom_path;
	private Rect 	bounds;
	private int[]   offset; //the amount this shape was shifted from zero
	private boolean custom;
	
	private int num_frames = 0;



	public Shape(int id){
		this.id = id;
		custom = false;
	}
	
	public void clearPaths(){
		path = null;
		custom_path = null;
		custom = false;
	}
	
	public int getNumFrames(){
		return num_frames;
	}
	
	public void setNumFrames(int n){
		 num_frames = n;
	}
	
	


	
	public void setPoints(int[] x, int[]y){
		x_points = x;
		y_points = y;
		makeBounds();
		offset = new int[2];
		offset[0] = bounds.left;
		offset[1] = bounds.top;
		
		path = new Path();
		path.moveTo(x_points[0], y_points[0]);
		for(int i = 1; i < x_points.length; i++) path.lineTo(x_points[i], y_points[i]);
		path.lineTo(x_points[0], y_points[0]);	
		
	
		
	}
	
	public void updatePath(){
		//initialize the path for the clipping
	    path = new Path();
		path.moveTo(x_points[0], y_points[0]);
		for(int i = 1; i < x_points.length; i++) path.lineTo(x_points[i], y_points[i]);
		path.lineTo(x_points[0], y_points[0]);
			
		
	}
	
	
	public Path getPath(){
		return path;
	}
	
	
	
	public int[] getOffset(){
		return offset;
	}
	
	public void setCustomPath(Path p){
		
		custom_path = new Path(p);	
		

	}
	
	public void makeBounds(){
		bounds = new Rect(1000000, 1000000, -1000000, -1000000);
		
		for(int i = 0; i < x_points.length; i++){
			if(x_points[i] < bounds.left) bounds.left = x_points[i];
			else if(x_points[i] > bounds.right) bounds.right = x_points[i];
			if(y_points[i] < bounds.top) bounds.top = y_points[i];
			else if(y_points[i] > bounds.bottom) bounds.bottom = y_points[i];
		}

	}
	
	public void offset(int x, int y){
		for(int i = 0; i < x_points.length; i++){
			x_points[i] += x;
			y_points[i] += y;
		}
		makeBounds();
		updatePath();
	}
	
	public Rect getBounds(){
		return bounds;
	}
	
	
	public int[] getXPoints(){
		return x_points;
	}
	
	public int[] getYPoints(){
		return y_points;
	}
	
	public int getId(){
		return id;
	}
	
	public void setCustom(boolean b){
		custom = b;
	}
	
	
		
	
	

}
