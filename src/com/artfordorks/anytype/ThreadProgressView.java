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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.artfordorks.data.Letter;
import com.artfordorks.data.Shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


/**
 * This class contains the view that acts as the canvas on which individuals compose messages
 * @author lauradevendorf
 *
 */
class ThreadProgressView extends ProgressBar {

	Context c;

	public ThreadProgressView(Context context) {
		super(context);
		c = context;
	}
	
	public void updateProgress(){
		Log.d("Async", "TPV update prgress called");

		Globals.progress.setProgress(5 - Globals.builder_threads);
		
		if(Globals.builder_threads == 0){
			((ProgressActivity) c).nextScreen();
		}


	}
	

	

}
