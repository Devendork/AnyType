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
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

class LoadAnimationFrameThread extends AsyncTask<File, Void, Bitmap> {
    private final WeakReference<VideoBuffer> videoBufferReference;
    private File data_file = null;

    public LoadAnimationFrameThread(VideoBuffer vb) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	videoBufferReference = new WeakReference<VideoBuffer>(vb);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(File... params) {
        data_file = params[0];
        return Globals.decodeSampledBitmapFromResource(data_file, 600, 600);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (videoBufferReference != null && bitmap != null) {
            final VideoBuffer buffer = videoBufferReference.get();
            if (buffer != null) {
            	buffer.setFrameBitmap(bitmap);
            }
        }
    }
}