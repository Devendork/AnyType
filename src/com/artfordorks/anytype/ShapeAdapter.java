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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * This adapter populates a grid of five shapes to use when editing a given letter
 * @author lauradevendorf
 *
 */
public class ShapeAdapter extends BaseAdapter {
    private Context mContext;

    public ShapeAdapter(Context c) {
        mContext = c;
		Log.d("Canvas Call", "Letter Adapter Created");

    }

    public int getCount() {
        return 5;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setId(position);
            
            imageView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Log.d("Delete", "touched at "+event.getX()+", "+event.getY());
					addToCanvas((ImageView)v, event);
					return false;
				}});
            
            
        } else {
            imageView = (ImageView) convertView;
        }

		File f = new File(Globals.getTestPath() + File.separator + "IMG_"+position + "_CROP.png");
		imageView.setImageBitmap(Globals.decodeSampledBitmapFromResource(f,60, 60));
		    
		
      

        return imageView;
    }
    
    public void addToCanvas(ImageView v, MotionEvent e){
		((LetterEditActivity)mContext).addToCanvas(v, e);

    }

  
}