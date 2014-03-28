package com.artfordorks.anytype;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

public class BuildLettersThread extends AsyncTask<Object, Void, Void> {

	    private int data;
	    private Bitmap bmap;
	    private MediaMetadataRetriever mmr;
	    
	    public BuildLettersThread() {
	    	Log.d("Async", "Added to globals builder threads");
			Globals.builder_threads++;
			
			
			
			
	    }

	    
	    // Decode image in background.
	    @Override
	    protected Void doInBackground(Object... params) {

	    	    		
		        data = (Integer) params[0];
		        bmap = (Bitmap) params[1];
		        
		        
		        
		        
		        
		        //if bmap is null then we're just rebuilding the letters from existing files
		        if(bmap != null){
			        Log.d("Bitmap", "Starting Process "+data);
	    				
					File pictureFile = Globals.getOutputMediaFile(Globals.MEDIA_TYPE_IMAGE, "IMG_" + Integer.toString(data) + "_CROP.png");
		
					try {
						OutputStream out = null;
						   try {
						    out = new BufferedOutputStream(new FileOutputStream(pictureFile));
							bmap.compress(Bitmap.CompressFormat.PNG, 60, out);

						   }finally {
						     if (out != null) {
						       out.close();
						     }
					}
						
						//FileOutputStream fos = new FileOutputStream(pictureFile);
//						bmap.compress(Bitmap.CompressFormat.PNG, 60, out);
//						fos.close();
//					
					} catch (FileNotFoundException e) {
						Log.d("Bitmap", "File not found: " + e.getMessage());
					} catch (IOException e) {
						Log.d("Bitmap", "Error accessing file: " + e.getMessage());
					}
					
					
					bmap.recycle();
		        }
		   
		        Globals.buildLetters(data);
		        
		        if(Globals.using_video){
		        	String f = Globals.getStageVideoPath(data);
					mmr = new MediaMetadataRetriever();
					mmr.setDataSource(f);
					
					String value = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					long video_length = Long.parseLong(value);  //this is the length of the video in milliseconds
					video_length *= 1000; //convert from milliseconds to microseconds
					
		        	Globals.makeVideoFrames(data, mmr, video_length);
		        	mmr.release();
		        }
	    	
	    	
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
