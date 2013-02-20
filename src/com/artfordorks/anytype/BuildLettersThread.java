package com.artfordorks.anytype;

import java.lang.ref.WeakReference;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

public class BuildLettersThread extends AsyncTask<Integer, Void, Void> {

	    private int data = 0;
	    private final WeakReference<GridView> letterGrid;


	    public BuildLettersThread() {
	    	letterGrid = new WeakReference<GridView>(Globals.canvas_letter_grid);    
	    }

	    // Decode image in background.
	    @Override
	    protected Void doInBackground(Integer... params) {
	        data = params[0];
	    	Log.d("Async", "Starting Process "+data);

	        Globals.buildLetters(data);
	        if(Globals.using_video) Globals.makeVideoFrames(data);
	        return null;      
	    }

	    // Once complete, see if (Change this to the GridView) is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Void v) {
	    	 Log.d("Async", "Finished Process "+data);
	    	
	    	  if (letterGrid != null ) {
	 	    	 Log.d("Async", "LetterGrid not null ");

	              //final GridView gv = letterGrid.get();
	              
	              if (Globals.canvas_letter_grid != null) {
	 	 	    	 Log.d("Async", "Grid View not null ");
	 	 	    	Globals.canvas_letter_grid.invalidate();
	              }
	          }	    
	    }
	
}
