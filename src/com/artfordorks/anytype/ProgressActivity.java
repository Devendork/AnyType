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

/***
 * This class draws a shape to the screen and allows the user 
 * to capture that shape
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;



public class ProgressActivity extends Activity{


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.buildletters);
		
		Globals.progress = new ThreadProgressView(this);
		Log.d("Async", "Progress Bar Assigned : Build Threads = "+Globals.builder_threads);
		Globals.progress.setMinimumHeight(20);
		Globals.progress.setMax(5 - Globals.builder_threads);
		
		
		
		LinearLayout canvas = (LinearLayout) findViewById(R.id.layout);
		canvas.addView(Globals.progress, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
	}
	
	public void nextScreen(){
		
			Intent intent = new Intent(this, CanvasActivity.class);
			startActivity(intent);
		
	}
	

}


