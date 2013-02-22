package com.artfordorks.anytype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

public class BuildLettersThread extends AsyncTask<Object, Void, Void> {

	    private int data;
	    private Bitmap bmap;

	    
	    public BuildLettersThread() {
	    	Log.d("Async", "Added to globals builder threads");
			Globals.builder_threads++;
	    }
	    
	    // Decode image in background.
	    @Override
	    protected Void doInBackground(Object... params) {

	    	    		
		        data = (Integer) params[0];
		        bmap = (Bitmap) params[1];
		        
		        Log.d("Async", "Starting Process "+data);
    				
				File pictureFile = Globals.getOutputMediaFile(Globals.MEDIA_TYPE_IMAGE, "IMG_" + Integer.toString(data) + "_CROP.png");
	
	
				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					bmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
					fos.close();
				
				} catch (FileNotFoundException e) {
					Log.d("Thead", "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.d("Thead", "Error accessing file: " + e.getMessage());
				}
				
				
				bmap.recycle();
		    	
		   
		        Globals.buildLetters(data);
		        if(Globals.using_video) Globals.makeVideoFrames(data);
	    	
	    	
	        return null;      
	    }

	    
	    @Override
	    protected void onPostExecute(Void v) {
	    	Globals.builder_threads--;
	    	
	    	 Log.d("Async", "Finished Process "+data);
              
	        	 if (Globals.progress != null) {
 	 	    	 Log.d("Async", "TPV not null ");
 	 	    	 	Globals.progress.updateProgress();
	        	 }else{
	        		 Log.d("Async", "TPV NULL");
	        	 }
	         
	             
	    }
	
}
