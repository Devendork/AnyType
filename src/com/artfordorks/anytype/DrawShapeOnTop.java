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

import com.artfordorks.data.*;
import com.artfordorks.anytype.R.id;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Button;


/**
 * This class extends view in order to draw an overlays on top of the camera's preview
 * @author lauradevendorf
 *
 */
class DrawShapeOnTop extends View {

	private Shape shape;
	private boolean confirm;
	private Matrix image_scale;

	private boolean custom;
	private Path basePath;
	private Path custom_path;
	private float[] first;
	private boolean valid_path = false;
	private RectF pathBounds;
	private float image_offset_x = 0;
	private float image_offset_y = 0;
	private Bitmap bmap;
	private boolean modified;
	
	private Point ctrAt = new Point();
	

	public DrawShapeOnTop(Context context, Shape s, boolean c) {
		super(context);
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		shape = s;
		confirm = c;

		Path path = new Path(shape.getPath());
		
		
		if(confirm){
			File f = new File(Globals.getTestPath() + File.separator + "IMG_"+ Integer.toString(shape.getId()) + ".jpg");
		    bmap = Globals.decodeSampledBitmapFromResource(f, Globals.preview_size.x, Globals.preview_size.y);
		}
		
		try {
			Matrix m = new Matrix();
			//m.setScale(Globals.shapeStretch, Globals.shapeStretch);
			m.setScale(Globals.shapeStretch, Globals.shapeStretch);
			path.transform(m);
		} catch (Exception e) {
			Log.d("Offset", "Matrix " + e.getMessage());
		}
	
		//compute the bounds
		pathBounds = new RectF();
		boolean  exact = true;
		path.computeBounds(pathBounds, exact);

		ctrAt.set((int)(Globals.preview_size.x-pathBounds.width())/2, (int)(Globals.preview_size.y-pathBounds.height())/2 + 15);

		
		basePath = new Path(path);
		 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		Rect image_bound = new Rect(0, 0, Globals.preview_size.x, Globals.preview_size.y);
		Path path = new Path(basePath);
		
		
		
		if(confirm){
			canvas.save();
			canvas.translate(image_offset_x, image_offset_y);
		    canvas.drawBitmap(bmap, null,image_bound, null);
			canvas.restore();
		}
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 20, 520, 60, paint);

		paint.setTextSize((float) 24);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.YELLOW);
		
		try {
		path.offset(ctrAt.x, ctrAt.y);
		} catch (Exception e) {
		Log.d("Offset", e.getMessage());
		}


		path.setFillType(FillType.INVERSE_WINDING);
		paint.setStyle(Paint.Style.FILL);
		paint.setARGB(Globals.background_alpha, 0, 0, 0);
		canvas.drawPath(path, paint);
		
		
		path.setFillType(FillType.WINDING);
		paint.setStyle(Paint.Style.STROKE);
		
		if(!custom){
			paint.setColor(Color.YELLOW);
			canvas.drawPath(path, paint);
		}else{
			paint.setColor(Color.CYAN);
			paint.setStrokeWidth(8);
			canvas.drawPath(custom_path, paint);
			
			RectF tempb = new RectF();
			custom_path.computeBounds(tempb, true);
			Log.d("Bounds", "L T Width / Height "+tempb.left+" "+tempb.top+" "+tempb.width()+" "+tempb.height());
		}
			
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 20, 520, 60, paint);

		paint.setTextSize((float) 24);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		
		if (!confirm)
			canvas.drawText("Find something that fits into this shape...", 20,
					50, paint);
		else
			canvas.drawText("Is this what you wanted?", 20, 50, paint);

		
		super.onDraw(canvas);
	}
	
	public void recycleImages(){
		bmap.recycle();
	}
	

//	get the bitmap from the screen	
	public Bitmap getShapeImageOut(){


					
		//get the pixels from the current screen
		Bitmap  bitmap = Bitmap.createBitmap(Globals.preview_size.x, Globals.preview_size.y, Bitmap.Config.ARGB_8888);
		Canvas  c = new Canvas(bitmap);
		c.drawColor(Color.TRANSPARENT);
		
		Path path = new Path(basePath);		
		if(custom){
			path = new Path(custom_path);
		}
		
		//compute the bounds for the base path
		pathBounds = new RectF();
		boolean  exact = true;
		basePath.computeBounds(pathBounds, exact);
		

		
		//offset by bounds
		if(!custom){
			try {
				path.offset(ctrAt.x, ctrAt.y);
			} catch (Exception e) {
				Log.d("Offset", e.getMessage());
			}
		}
		
		
		RectF pbounds = new RectF();
		path.computeBounds(pbounds, true);

		//set the background to transparent
	    Rect image_bound = new Rect(0, 0, Globals.preview_size.x, Globals.preview_size.y);
		c.clipPath(path);
		c.drawBitmap(bmap, null,image_bound, null);			
		
		//create a cropped version of the bitmap
		try{
			Bitmap out = Bitmap.createBitmap(bitmap,(int)pbounds.left, (int)pbounds.top, (int) pbounds.width()+2, (int) pbounds.height()+2, new Matrix(), false);
			bitmap.recycle();
			return out;
		}catch(IllegalArgumentException e){
			Log.d("Get Screen Bitmap", e.getMessage());
		}
		
		return bitmap;
	}
	
	public void updateImageOffset(float x, float y){
		image_offset_x -= x;
		image_offset_y -= y;
		modified = true;
	}
	
	public void updateScale(float x){
		modified = true;
	}
	

	public void startPath(float x, float y) {
		
		Path path = new Path(basePath);
		pathBounds = new RectF();
		boolean  exact = true;
		path.computeBounds(pathBounds, exact);
	
		
		Log.d("Tap", "XY: "+x+", "+y);
		Log.d("Tap", "Path at "+pathBounds.top+" "+pathBounds.left);
		
		if(pathBounds.contains(x-ctrAt.x, y-ctrAt.y)){
			valid_path = true;
		
			try {
				path.offset(ctrAt.x, ctrAt.y);
				} catch (Exception e) {
				Log.d("Offset", e.getMessage());
			}
			
			custom = true;
			first = new float[2];
			
			first[0] = x;
			first[1] = y;
			
			custom_path = new Path();
			custom_path.moveTo(x, y);
		}else{
			valid_path = false;
		}
		
		
	}

	public void endPath(float x, float y) {	
		if(valid_path){
			custom_path.lineTo(first[0], first[1]);
		
			Path path = new Path(custom_path);
			pathBounds = new RectF();
			boolean  exact = true;
			path.computeBounds(pathBounds, exact);
			
			shape.setCustom(true);
			shape.setCustomPath(custom_path);
			
		}
	}

	public void addPathPoint(float x, float y) {
		if(valid_path){
		custom_path.lineTo(x, y);
		}
	}
	
	public boolean isValidPath(){
		return valid_path;
	}
	
	
	

}
