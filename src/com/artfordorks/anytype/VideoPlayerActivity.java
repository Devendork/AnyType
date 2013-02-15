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

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * This is the screen that plays back a video full screen after a user captures it
 * @author lauradevendorf
 *
 */
public class VideoPlayerActivity extends Activity{

	protected static final String TAG = null;
	private VideoView	mVideo;	
	private VideoClipperView mask;

	  int videoWidth = 0,videoHeight = 0;
	private double beginTime = System.currentTimeMillis();
	public final static String LOGTAG = "CUSTOM_VIDEO_PLAYER";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.videoplayer);
		RelativeLayout frame = (RelativeLayout) findViewById(R.id.frame_holder);
	


		mVideo = (VideoView) findViewById(R.id.video_viewer);
		mask = new VideoClipperView(this);
		frame.addView(mask, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));


	    MediaController controller = new MediaController(this);
	    mVideo.setMediaController(controller);
	    mVideo.setVideoPath(Globals.getStageVideoPath());
	    mVideo.start();
	    
	    //force this window to close when the video is through playing
	    mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            	writeToLog();
            	finish();
            }
        });
	}
	
	public void writeToLog(){
		double endTime = System.currentTimeMillis();
		double time = endTime - beginTime;
		Globals.writeToLog(this, getLocalClassName(), "ViewCaptureActivity", time);
	}
	


	@Override
	public void onRestart() {

	}

	@Override
	protected void onPause() {
		super.onPause();
	}




}