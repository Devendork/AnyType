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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.artfordorks.data.Shape;

class VideoClipperView extends View {


	private Shape shape;
	private boolean confirm;
	int[] x_points;
	int[] y_points;
	Bitmap bmap;
	private Path path;
	private RectF pathBounds;

	
	/**
	 * This is drawn over the video playback to make the regions opaque that are going to be cropped out
	 * @param context
	 */
	public VideoClipperView(Context context) {
		super(context);
		shape = Globals.getStageShape();
		confirm = true;
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		if(confirm){
			File f = new File(Globals.getTestPath() + File.separator + "IMG_"+ Integer.toString(shape.getId()) + ".jpg");
			bmap = Globals.decodeSampledBitmapFromResource(f, Globals.preview_size.x, Globals.preview_size.y);
		}
		
		path = new Path(shape.getPath());
		
		try {
			Matrix m = new Matrix();
			m.setScale(Globals.shapeStretch, Globals.shapeStretch);
			path.transform(m);
		} catch (Exception e) {
			Log.d("Offset", "Matrix " + e.getMessage());
		}
	
		//compute the bounds
		pathBounds = new RectF();
		boolean  exact = true;
		path.computeBounds(pathBounds, exact);
		
		 
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("Tap", "VCV - on Draw");
		path.setFillType(FillType.INVERSE_WINDING);

		canvas.drawARGB(0, 255, 255, 255);
		
		
		try {
		path.offset((this.getWidth()-pathBounds.width())/2, (this.getHeight()-pathBounds.height())/2);
		} catch (Exception e) {
		Log.d("Offset", e.getMessage());
		}
	
		
		Paint paint = new Paint();
		paint.setARGB(Globals.background_alpha, 0, 0, 0);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawPath(path, paint);
		
	

		super.onDraw(canvas);
	}
	

	

	

}
