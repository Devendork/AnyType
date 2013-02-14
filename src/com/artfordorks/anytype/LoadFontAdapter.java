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

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This populates the list of fonts that can be loaded
 * @author lauradevendorf
 *
 */
public class LoadFontAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList fonts;

    public LoadFontAdapter(Context c) {
        mContext = c;
        fonts = new ArrayList();
        
        File dir = new File(Globals.getBasePath());
        String[] children = dir.list();
        
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i=0; i<children.length; i++) {
                String filename = children[i];
                fonts.add(filename);
            }
        }

        
		Log.d("Canvas Call", "Letter Adapter Created");

    }
    

    public int getCount() {
        return fonts.size();
    }

    public Object getItem(int position) {
        return fonts.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	textView = new TextView(mContext);
        	textView.setWidth(1280);
        	textView.setHeight(40);
        	textView.setLeft(10);
        	textView.setTextSize(24);
        	
           
        } else {
            textView = (TextView) convertView;
        }

		File f = new File(Globals.getTestPath() + File.separator +Globals.intToChar(position) + ".png");
		textView.setText((String)fonts.get(position));
		     

        return textView;
    }

  
}