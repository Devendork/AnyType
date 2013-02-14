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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

/***
class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera mCamera;

    CameraPreview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFixedSize(Globals.preview_size.x, Globals.preview_size.y);

    }
    
    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }
        

        stopPreviewAndFreeCamera();
        
        mCamera = camera;
        
        if (mCamera != null) {
          
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
          
          
    		Log.d("Memory", "Starting Preview");

            mCamera.startPreview();
        }
    }
  
    
 

  
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {

            mCamera.stopPreview();
        
     
     
            mCamera.release();
        
            mCamera = null;
        }
    }
    

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	    // Now that the size is known, set up the camera parameters and begin
	    // the preview.
		Parameters parameters = mCamera.getParameters();		
		parameters.setPreviewSize(Globals.preview_size.x, Globals.preview_size.y);
       	parameters.setPictureSize(Globals.picture_size.x, Globals.picture_size.y);
    	parameters.setJpegQuality(50);
    	parameters.setPictureFormat(ImageFormat.JPEG);
		mCamera.setParameters(parameters);

	  
	    mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("Memory", "Surface Created");
		mCamera.startPreview();
	}

	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
         
            mCamera.stopPreview();
        }
    }

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

}
*/

/** This camera preview class is in charge of managing the live camera preview*/
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "";
	private SurfaceHolder mHolder;
    private Camera mCamera;
    
    
    
     

    public CameraPreview(Context context) {
        super(context);
        
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        //mHolder.setFixedSize(Globals.preview_size.x, Globals.preview_size.y);
        
    }
    
    /**
     * When this function returns, mCamera will be null.
     */
   private void stopPreviewAndFreeCamera() {

       if (mCamera != null) {
           /*
             Call stopPreview() to stop updating the preview surface.
           */
           mCamera.stopPreview();
       
           /*
             Important: Call release() to release the camera for use by other applications. 
             Applications should release the camera immediately in onPause() (and re-open() it in
             onResume()).
           */
           mCamera.release();
       
           mCamera = null;
       }
   }
    
    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }
        
        stopPreviewAndFreeCamera();
        
        mCamera = camera;
        
        if (mCamera != null) {
       
            requestLayout();
          
            try {
                mCamera.setPreviewDisplay(mHolder);
        		Log.d("Memory", "Set Camera: Preview Display Set");

            } catch (IOException e) {
                e.printStackTrace();
            }
          
            /*
              Important: Call startPreview() to start updating the preview surface. Preview must 
              be started before you can take a picture.
              */
            // start preview with new settings
    		Log.d("Memory", "Set Camera: Starting Preview");

           mCamera.startPreview();
        }
    }
    
    

    public void surfaceCreated(SurfaceHolder holder) {



    }
    
 

    

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
     
    	if(mCamera != null){
		Parameters parameters = mCamera.getParameters();		
		parameters.setPreviewSize(Globals.preview_size.x, Globals.preview_size.y);
       	parameters.setPictureSize(Globals.picture_size.x, Globals.picture_size.y);
    	parameters.setJpegQuality(100);
    	parameters.setPictureFormat(ImageFormat.JPEG);
   
		mCamera.setParameters(parameters);
		 // start preview with new settings
        try {
        	Log.d("Memory", "SurfaceChanged: Preview Display Set");
            mCamera.setPreviewDisplay(mHolder);
        	Log.d("Memory", "SurfaceChanged: Start Preivew");

            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    	}
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}


