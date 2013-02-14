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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.artfordorks.data.Shape;


/**
 * This class handles drawing a shape within a letter with highlighting to indicate original shape
 * @author lauradevendorf
 *
 */
class LetterPartView extends View{

	private Shape shape;
	int[] x_points;
	int[] y_points;
	Bitmap bmap;
	private Path path;
	private int x_off, y_off;
	private float rots;
	private int[] colors;
	int[] offset;
	float scale = (float) 700f / Globals.letter_size;
	Matrix m;
	int color_id;
	float pathArea;
	boolean selected;
	boolean vhs;
	Path arrow;
	

	private RectF pathBounds;

	public LetterPartView(Context context, int shape_id, int x, int y, float rots, int color_id) {
		super(context);
		shape = Globals.getShape(shape_id);
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		this.color_id = color_id;
		
		colors = new int[5];
		colors[0] = Color.MAGENTA;
		colors[1] = Color.CYAN;
		colors[2] = Color.GREEN;
		colors[3] = Color.BLUE;
		colors[4] = Color.YELLOW;

		
		x_points = shape.getXPoints();
		y_points = shape.getYPoints();
		
		x_off = x;
		y_off = y;
		this.rots = rots;
		
		offset = shape.getOffset();
		
		Log.d("Bound", "Scale " + scale);
		

		
		int imageSize = Globals.letter_size;
		File f = new File(Globals.getTestPath() + File.separator +"IMG_"+ Integer.toString(shape.getId()) + "_CROP.png");
		bmap = Globals.decodeSampledBitmapFromResource(f,imageSize, imageSize);
	
		
		path = new Path();
		path.moveTo(x_points[0], y_points[0]);
		for (int i = 1; i < x_points.length; i++)
			path.lineTo(x_points[i], y_points[i]);
		path.lineTo(x_points[0], y_points[0]);
		
		m = new Matrix();
		m.preTranslate(x_off *scale, y_off* scale);
		m.preRotate((float) Math.toDegrees(rots));
		m.preTranslate(offset[0] *scale, offset[1]* scale);
		m.preScale(scale, scale);
		
		path.transform(m);
				
	
	
		//compute the bounds
		pathBounds = new RectF();
		boolean  exact = true;
		path.computeBounds(pathBounds, exact);
		
		pathArea = pathBounds.width()*pathBounds.height();
		

		
		 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
	Matrix mm = new Matrix(m);
	mm.preScale(0.5f, 0.5f);
	canvas.drawBitmap(bmap, mm, null);

	//draw the path on top of the image
	Paint paint = new Paint();


	
	if(!vhs || (vhs && selected)){
		paint.setStyle(Paint.Style.STROKE);
		paint.setAlpha(255);
		paint.setColor(colors[color_id]);
		paint.setStrokeWidth(3);
		canvas.drawPath(path, paint);
	}
	
	
	if(selected){
		//draw an arrow to this shape
		int right = this.getWidth();
		arrow = new Path();
		arrow.moveTo(pathBounds.right+20, pathBounds.top + 40);
		arrow.lineTo(right, pathBounds.top);
		arrow.lineTo(right, pathBounds.top +40);
		arrow.lineTo(pathBounds.right+20, pathBounds.top+40);

		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawPath(arrow, paint);
	}

		
	super.onDraw(canvas);
	}
	
	public boolean hasTouch(float mx, float my){
		Log.d("Bound", "Mouse at "+mx+", "+my);
		this.invalidate();
		selected = pathBounds.contains(mx, my);
		return selected;
	}
	
	public int getShapeId(){
		return shape.getId();
	}
	
	public void deSelect(){
		selected = false;
	}
	
	public void setViewHasSelection(boolean vhs){
		this.vhs = vhs;
		this.invalidate();
	}
	
	
	

}
